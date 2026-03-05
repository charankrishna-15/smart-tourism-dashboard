@echo off
title Smart Tourism Web Server
echo Starting local web server for Tour Planner Dashboard on port 8080...
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0server.ps1"
pause
