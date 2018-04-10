package org.gitlab.tools;

import org.apache.commons.lang3.ArrayUtils;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.models.GitlabGroup;
import org.gitlab.api.models.GitlabProject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SyncGitlab {

    public static void main(String[] args) {
        /**
         * Program clone git repository if they were not there or it them
         * synchronizes with the server version of the repository (fetch-and-merge)
         */
        SyncGitlab gtLab = new SyncGitlab();
        Properties prop = gtLab.readProperties();
        gtLab.gitlabConnect(prop);

    }

    private void gitlabConnect(Properties prop) {
        GitlabAPI api;
        api = GitlabAPI.connect(prop.getProperty("URL"), prop.getProperty("TOKEN"));
//Вытаскиваем группы из проперти файла
        String[] groups = getListOfGroup(prop);
//        System.out.println("groups = " + Arrays.toString(groups));
//Получаем id групп, чтобы потом по ним получить список проектов
        ArrayList<GitlabGroup> filteredGitlabGroup = getFilteredGroup(api, groups);
//Выведем те группы, которые у нас есть
        for (GitlabGroup gitlabGroup : filteredGitlabGroup) {
//            System.out.println("getId = " + gitlabGroup.getId() + ", Name = " + gitlabGroup.getName());
            try {
                List<GitlabProject> gitlabProjects = api.getGroupProjects(gitlabGroup);
                for (GitlabProject gitlabProject : gitlabProjects) {
                    generateSyncOrClone(gitlabProject, gitlabGroup, prop);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private ArrayList<GitlabGroup> getFilteredGroup(GitlabAPI api, String[] groups) {
        ArrayList<GitlabGroup> filteredGitlabGroup = new ArrayList<>();
        try {
            for (GitlabGroup gitlabGroup : api.getGroups()) {

                if (ArrayUtils.contains(groups, gitlabGroup.getName())) {
                    // Do some stuff.
                    filteredGitlabGroup.add(gitlabGroup);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filteredGitlabGroup;
    }

    private String[] getListOfGroup(Properties prop) {
        String[] groups = null;
        if (prop.getProperty("GROUPS") != null) {
            groups = prop.getProperty("GROUPS").split(",");
        }
        return groups;
    }

/*    private void iterateByGroup(Properties prop, List<GitlabProject> glabProg, Stream<GitlabGroup> filterAllGroup) {
        filterAllGroup.forEach(groupBody -> glabProg.forEach(projectBody -> {
            generateSyncOrClone(projectBody, groupBody, prop);
        }));
    }*/

    private void generateSyncOrClone(GitlabProject projectBody, GitlabGroup groupBody, Properties prop) {
        String groupWithName = projectBody.getPathWithNamespace();
        Path repositoryFolder;
        if (prop.getProperty("FOLDER_GROUP") != null && prop.getProperty("FOLDER_GROUP").equals("N")) {
            repositoryFolder = Paths.get(prop.getProperty("PATH"));
        } else {
            repositoryFolder = Paths.get(prop.getProperty("PATH") + "/" + groupWithName.toString());//.split("/")[0]);
        }
        String group = groupWithName.split("/")[0];
        // Если группа проекта входит в текущую группу
        boolean load_flag = true;
        if (prop.getProperty("ARCHIVED") != null && ((prop.getProperty("ARCHIVED").equals("Y") && !projectBody.isArchived())
                || (prop.getProperty("ARCHIVED").equals("N") && projectBody.isArchived()))) {
            load_flag = false;
        }
        if (group.equals(groupBody.getName()) && load_flag) {
            makeSyncLine(repositoryFolder, projectBody);
        }
    }

    private void makeSyncLine(Path repositoryFolder, GitlabProject projectBody) {
        System.out.println("echo " + repositoryFolder);
        if (!Files.exists(repositoryFolder)) {
            System.out.println("git clone " + projectBody.getSshUrl() + " " + setdoubleQuote(repositoryFolder.toString()));
        } else {
            String baseLine = "git --git-dir=" + setdoubleQuote(repositoryFolder.toString() + "\\.git")+" --work-tree=" + setdoubleQuote(repositoryFolder.toString());
            System.out.println(baseLine + " fetch origin");
            System.out.println(baseLine + " merge origin/master");
        }
    }

    private String setdoubleQuote(String myText) {
        String quoteText = "";
        if (!myText.isEmpty()) {
            quoteText = "\"" + myText + "\"";
        }
        return quoteText;
    }

    private Properties readProperties() {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream("config.properties");
            // load a properties file
            prop.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return prop;
    }
}
