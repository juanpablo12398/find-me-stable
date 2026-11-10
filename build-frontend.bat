@echo off
setlocal

echo Building frontend...

cd frontend
call npm install

REM Production build
call npm run build

REM Copy build to backend static resources
echo Copying build to backend static resources...
cd ..
if not exist src\main\resources\static mkdir src\main\resources\static
xcopy /E /Y frontend\dist\* src\main\resources\static\ >nul

echo Frontend build complete!
endlocal