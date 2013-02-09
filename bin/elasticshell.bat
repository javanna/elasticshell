@echo off
SETLOCAL

if DEFINED JAVA_HOME set JAVA=%JAVA_HOME%/bin/java
if NOT DEFINED JAVA_HOME set JAVA=java

set SCRIPT_DIR=%~dp0
for %%I in ("%SCRIPT_DIR%..") do set DIRNAME=%%~dpfI
cd %DIRNAME%

%JAVA% %JAVA_OPTS% -jar lib/${project.build.finalName}.jar

ENDLOCAL
