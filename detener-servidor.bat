@echo off
REM ============================================================================
REM  Detiene Tomcat.  Evidencia GA7-220501096-AA2-EV02  |  Jose Cortes
REM  MySQL se deja encendido: lo usan otros proyectos de XAMPP.
REM ============================================================================

title Detener el servidor del modulo

set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot"
set "CATALINA_HOME=C:\xampp\tomcat"

echo.
echo  Deteniendo Tomcat...
call "%CATALINA_HOME%\bin\shutdown.bat" 2> nul
timeout /t 6 /nobreak > nul

tasklist | findstr /i "java.exe" > nul
if not errorlevel 1 (
    echo  Tomcat no respondio al apagado ordenado. Cerrando el proceso...
    taskkill /F /IM java.exe > nul 2>&1
)

echo  Listo. MySQL sigue encendido; se apaga desde el panel de XAMPP.
echo.
pause
