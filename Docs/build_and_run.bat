@echo off
setlocal enabledelayedexpansion

REM === Configuration ===
set JAVA_HOME=C:\path\to\your\jdk    REM <-- Change this to your JDK path if needed, or remove if JAVA is in PATH
set JAVAFX_SDK=C:\javafx-sdk-21

set SRC_DIR=C:\Downloads\Inventory Management System1\src
set BIN_DIR=C:\Downloads\Inventory Management System1\bin

set MAIN_CLASS=com.yourname.stockwise.app.InventoryApp

REM === Prepare bin directory ===
echo Cleaning bin directory...
if exist "%BIN_DIR%" (
    rmdir /s /q "%BIN_DIR%"
)
mkdir "%BIN_DIR%"

REM === Find all .java files ===
echo Searching for Java source files in "%SRC_DIR%"...
set JAVA_FILES=

for /r "%SRC_DIR%" %%f in (*.java) do (
    set JAVA_FILES=!JAVA_FILES! "%%f"
)

if "%JAVA_FILES%"=="" (
    echo ERROR: No Java source files found in "%SRC_DIR%".
    pause
    exit /b 1
)

REM === Compile ===
echo Compiling Java source files...
"%JAVA_HOME%\bin\javac" --module-path "%JAVAFX_SDK%\lib" --add-modules javafx.controls,javafx.fxml -d "%BIN_DIR%" %JAVA_FILES%
if errorlevel 1 (
    echo Compilation failed.
    pause
    exit /b 1
)

REM === Run ===
echo Running %MAIN_CLASS%...
"%JAVA_HOME%\bin\java" --module-path "%JAVAFX_SDK%\lib" --add-modules javafx.controls,javafx.fxml -cp "%BIN_DIR%" %MAIN_CLASS%

pause
