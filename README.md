# SyncGitlab  
Program clone git repository if they were not there or it them synchronizes with the server version of the repository (fetch-and-merge)  
  
# Utility SyncGitlab  
All of us are lazy people. Our GIT as you can see in GitLab, implemented through groups and projects. Accordingly, in order to get the project 1 of 1 GitLab's group - you can enter one command. Since projects and groups we have a lot - it is accordingly not very convenient.  
To simplify the process Mishin Nikolay wrote a utility that simplifies the process several times:  
Setting:  
1) Download source https://github.com/mishin/SyncGitlab/archive/master.zip  
2) Unpack the local machine and compile it `mvn package`  
The archive has unpacked config.properties file. Before the first start it, open with any text editor and edit.  
URL=http://local.gitlab.org  
TOKEN=eqw13213eqwr443  
PATH=c:/temp/all_repo  
GROUPS=projects,prod  
There are settings in the file:  
PATH - The path which will "load" the data from the Gitlab groups  
GROUPS - the name of the GitLab groups of a to be pumped into the directory specified previously. Groups should indicate compliance with the register from the list of groups, separated by commas and no spaces. After applying the settings - save  
Launch  
Start SyncGitlab.bat.  
After running the utility SyncGitlab will open a console window without the text and start unloading procedure. Once the procedure is executed - the window closes. If you encounter problems, as well as just for fun, you can always view the log of the last launch in sync.log file.  
