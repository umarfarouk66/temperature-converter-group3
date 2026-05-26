@echo off
echo ========================================
echo    Temperature Converter - Group 3
echo ========================================

java --module-path "lib" ^
     --add-modules javafx.controls,javafx.fxml ^
     -Dprism.order=sw ^
     -cp bin App

pause