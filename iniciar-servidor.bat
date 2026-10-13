@echo off
REM Inicia MySQL, Apache y Tomcat, y abre el modulo en el navegador.
REM Define JAVA_HOME solo para esta ventana, sin tocar las variables del sistema.

title Modulo Comercial del Showroom - servidor

REM Ajuste estas rutas si su instalacion es distinta.
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot"
set "CATALINA_HOME=C:\xampp\tomcat"
set "XAMPP=C:\xampp"

set "APP=showroom"
set "PUERTO=8080"

REM Apache sirve phpMyAdmin. Escucha en el 8081 porque el 80 lo ocupa IIS.
set "PUERTO_APACHE=8081"

echo.
echo ===========================================================================
echo   MODULO COMERCIAL DEL SHOWROOM
echo ===========================================================================
echo.

if not exist "%JAVA_HOME%\bin\java.exe" (
    echo  ERROR: no se encontro el JDK en:
    echo         %JAVA_HOME%
    echo.
    echo  Edite la variable JAVA_HOME en las primeras lineas de este archivo.
    pause
    exit /b 1
)

echo  [1/5] Verificando MySQL...
netstat -an | findstr ":3306" | findstr "LISTENING" > nul
if errorlevel 1 (
    echo        MySQL apagado. Iniciandolo...
    start "" /min "%XAMPP%\mysql_start.bat"
    timeout /t 8 /nobreak > nul
) else (
    echo        MySQL ya estaba encendido.
)

echo  [2/5] Verificando Apache (phpMyAdmin)...
netstat -an | findstr ":%PUERTO_APACHE%" | findstr "LISTENING" > nul
if errorlevel 1 (
    echo        Apache apagado. Iniciandolo...
    start "" /b "%XAMPP%\apache\bin\httpd.exe"
    timeout /t 5 /nobreak > nul
) else (
    echo        Apache ya estaba encendido.
)

echo  [3/5] Verificando que el puerto %PUERTO% este libre...
netstat -an | findstr ":%PUERTO%" | findstr "LISTENING" > nul
if not errorlevel 1 (
    echo.
    echo        AVISO: algo ya esta escuchando en el puerto %PUERTO%.
    echo        Si es Docker Desktop, cierrelo; si es otro Tomcat, ejecute
    echo        primero detener-servidor.bat
    echo.
)

echo  [4/5] Iniciando Tomcat...
if not exist "%CATALINA_HOME%\webapps\%APP%\WEB-INF\web.xml" (
    echo.
    echo        La aplicacion no esta desplegada. Ejecute primero:
    echo           compilar.bat desplegar
    echo.
    pause
    exit /b 1
)
call "%CATALINA_HOME%\bin\startup.bat"
timeout /t 12 /nobreak > nul

echo  [5/5] Abriendo el navegador...
start "" "http://localhost:%PUERTO%/%APP%/"

echo.
echo ===========================================================================
echo   Modulo disponible en:  http://localhost:%PUERTO%/%APP%/
echo   phpMyAdmin en:         http://localhost:%PUERTO_APACHE%/phpmyadmin/
echo.
echo   Usuario  admin   Contrasena  Admin2025    (administrador)
echo   Usuario  asesor  Contrasena  Asesor2025   (asesor)
echo.
echo   Para apagarlo:  detener-servidor.bat
echo ===========================================================================
echo.
pause