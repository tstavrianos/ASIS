@ECHO OFF
SET BINDIR=%~dp0
CD /D "%BINDIR%
REM If this line below fails, replace it with the location of your java 7 install.
"c:\Program Files\Java\jdk1.8.0_121\jre\bin\javaw.exe" -Xmx1024M -Xms1024M -jar asis-skyproc.jar
PAUSE