@echo off
echo ==========================================
echo  Smart Tourism System - Run Script
echo ==========================================

set JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.18.8-hotspot
set M2_HOME=%USERPROFILE%\tools\mvn_extracted\apache-maven-3.9.12
set PATH=%M2_HOME%\bin;%JAVA_HOME%\bin;%PATH%

echo [1/2] Building project...
call mvn clean package -q
if %ERRORLEVEL% NEQ 0 (
    echo BUILD FAILED! Check output above.
    pause
    exit /b 1
)
echo Build successful!

echo [2/2] Running application...
java -jar target\smart-tourism-1.0.0-jar-with-dependencies.jar

echo.
echo Done! Check the 'reports' folder for generated charts.
pause
