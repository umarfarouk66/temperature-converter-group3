@echo off
title Temperature Converter - Group 3 Build System
cls

echo ========================================================
echo         Thermodynamic Converter - Group 3 Build
echo ========================================================
echo Course: Software Construction II (2025/2026)
echo.

:: 1. Ensure output folder exists
if not exist bin mkdir bin

:: 2. Compile all source files
echo [*] Compiling source code...
javac --module-path "lib" ^
      --add-modules javafx.controls,javafx.fxml ^
      -d bin src\*.java
if %errorlevel% neq 0 (
    echo.
    echo [x] Compilation FAILED! Please inspect errors above.
    pause
    exit /b %errorlevel%
)
echo [✓] Compilation successful.

:: 3. Copy CSS resources to target output directory
echo [*] Synching stylesheets...
copy /Y src\style.css bin\ >nul 2>&1
echo [✓] Resources copied.

:: 4. Ask if user wants to compile Javadocs
echo.
set /p JADOC="[?] Re-generate Javadoc documentation? (y/n, default=n): "
if /I "%JADOC%"=="y" (
    echo.
    echo [*] Generating updated Javadocs into 'docs/'...
    if not exist docs mkdir docs
    javadoc --module-path "lib" ^
            --add-modules javafx.controls,javafx.fxml ^
            -d docs src\*.java ^
            -quiet
    echo [✓] Javadoc generation complete.
)

echo.
echo ========================================================
echo [*] Launching Application with JVM Assertions Enabled (-ea)...
echo ========================================================
echo.

:: 5. Execute JVM with assertions enabled (-ea) and CPU-based rendering fallback (-Dprism.order=sw)
java -ea ^
     --module-path "lib" ^
     --add-modules javafx.controls,javafx.fxml ^
     -Dprism.order=sw ^
     -cp bin App

echo.
echo ========================================================
echo [*] Application closed.
echo ========================================================
pause