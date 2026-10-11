@echo off
setlocal

set "APP_HOME=%~dp0"
if "%APP_HOME:~-1%"=="\" set "APP_HOME=%APP_HOME:~0,-1%"

if not defined JAVA_HOME (
    for /d %%i in ("C:\Program Files\Eclipse Adoptium\jdk*") do (
        set "JAVA_HOME=%%i"
    )
    if not defined JAVA_HOME (
        for /d %%i in ("C:\Program Files\Java\jdk*") do (
            set "JAVA_HOME=%%i"
        )
    )
)

set "PATH=%JAVA_HOME%\bin;%PATH%"

set "MAVEN_CMD=%APP_HOME%\.mvn\apache-maven-3.9.6\bin\mvn.cmd"
"%MAVEN_CMD%" %*
