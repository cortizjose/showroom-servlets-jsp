@echo off
REM ============================================================================
REM  Inicia MySQL y Tomcat, y abre el modulo en el navegador.
REM  Evidencia GA7-220501096-AA2-EV02  |  Jose Cortes
REM
REM  Uso: doble clic en este archivo.
REM
REM  Existe porque el panel de XAMPP necesita que la variable JAVA_HOME apunte
REM  a un JDK, y en un equipo recien instalado esa variable esta vacia. Aqui se
REM  define solo para esta ventana, sin modificar la configuracion del sistema.
REM ============================================================================

title Modulo Comercial del Showroom - servidor

REM --- Ajuste estas rutas si su instalacion es distinta ----------------------
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot"
set "CATALINA_HOME=C:\xampp\tomcat"
set "XAMPP=C:\xampp"
REM ---------------------------------------------------------------------------

set "APP=showroom"
set "PUERTO=8080"

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

REM --- 1. MySQL --------------------------------------------------------------
echo  [1/4] Verificando MySQL...
netstat -an | findstr ":3306" | findstr "LISTENING" > nul
if errorlevel 1 (
    echo        MySQL apagado. Iniciandolo...
    start "" /min "%XAMPP%\mysql_start.bat"
    timeout /t 8 /nobreak > nul
) else (
    echo        MySQL ya estaba encendido.
)

REM --- 2. Puerto libre -------------------------------------------------------
echo  [2/4] Verificando que el puerto %PUERTO% este libre...
netstat -an | findstr ":%PUERTO%" | findstr "LISTENING" > nul
if not errorlevel 1 (
    echo.
    echo        AVISO: algo ya esta escuchando en el puerto %PUERTO%.
    echo        Si es Docker Desktop, cierrelo; si es otro Tomcat, ejecute
    echo        primero detener-servidor.bat
    echo.
)

REM --- 3. Tomcat -------------------------------------------------------------
echo  [3/4] Iniciando Tomcat...
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

REM --- 4. Navegador ----------------------------------------------------------
echo  [4/4] Abriendo el navegador...
start "" "http://localhost:%PUERTO%/%APP%/"

echo.
echo ===========================================================================
echo   Modulo disponible en:  http://localhost:%PUERTO%/%APP%/
echo.
echo   Usuario  admin   Contrasena  Admin2025    (administrador)
echo   Usuario  asesor  Contrasena  Asesor2025   (asesor)
echo.
echo   Para apagarlo:  detener-servidor.bat
echo ===========================================================================
echo.
pause
