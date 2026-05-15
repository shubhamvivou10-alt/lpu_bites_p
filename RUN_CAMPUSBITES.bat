@echo off
echo Cleaning old builds...
if exist bin rd /s /q bin
mkdir bin

echo Compiling Java Backend...
javac -d bin src/*.java src/controllers/*.java src/dao/*.java src/models/*.java src/services/*.java src/utils/*.java

if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b %errorlevel%
)

echo Starting CampusBites...
echo Open http://localhost:8080 in your browser.
java -cp bin src.Main
pause
