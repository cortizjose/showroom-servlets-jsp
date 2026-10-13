@echo off
REM ============================================================================
REM Compila y empaqueta el Modulo Comercial del Showroom Evidencia GA7-220501096-AA2-EV02 | Jose Cortes Uso: compilar.bat.
REM ============================================================================

setlocal enabledelayedexpansion

REM --- Ajuste estas dos rutas si su instalacion es distinta ----------------------
set "JAVA_INICIO=C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot"
set "TOMCAT=C:\xampp\tomcat"
REM ---------------------------------------------------------------------------

set "PROYECTO=%~dp0"
set "SALIDA=%PROYECTO%build"
set "APP=showroom"

echo.
echo === Modulo Comercial del Showroom =========================================

if not exist "%JAVA_INICIO%\bin\javac.exe" (
    echo  ERROR: no se encontro el JDK en:
    echo         %JAVA_INICIO%
    echo  Edite la variable JAVA_INICIO al inicio de este archivo.
    exit /b 1
)

if not exist "%TOMCAT%\lib\servlet-api.jar" (
    echo  ERROR: no se encontro servlet-api.jar en %TOMCAT%\lib
    echo  Edite la variable TOMCAT al inicio de este archivo.
    exit /b 1
)

echo  [1/5] Limpiando la compilacion anterior
if exist "%SALIDA%" rmdir /s /q "%SALIDA%"
mkdir "%SALIDA%\WEB-INF\classes"

echo  [2/5] Listando los archivos fuente
(for /f "delims=" %%f in ('dir /s /b "%PROYECTO%src\main\java\*.java"') do @(
    set "ARCHIVO=%%f"
    echo "!ARCHIVO:\=/!"
)) > "%TEMP%\fuentes_showroom.txt"

echo  [3/5] Compilando
REM release 8 produce bytecode compatible con Java 8 en adelante, de modo que la aplicacion corre en cualquier Tomcat.
"%JAVA_INICIO%\bin\javac.exe" -encoding UTF-8 --release 8 -nowarn ^
    -d "%SALIDA%\WEB-INF\classes" ^
    -cp "%TOMCAT%\lib\servlet-api.jar;%TOMCAT%\lib\jsp-api.jar;%PROYECTO%src\main\webapp\WEB-INF\lib\jstl-1.2.jar;%PROYECTO%src\main\webapp\WEB-INF\lib\mysql-connector-j-8.4.0.jar" ^
    @"%TEMP%\fuentes_showroom.txt"

if errorlevel 1 (
    echo.
    echo  La compilacion fallo. Revise los errores anteriores.
    exit /b 1
)

echo  [4/5] Copiando vistas, recursos y configuracion
xcopy "%PROYECTO%src\main\webapp" "%SALIDA%" /e /i /y /q > nul
xcopy "%PROYECTO%src\main\resources\*" "%SALIDA%\WEB-INF\classes" /y /q > nul

echo  [5/5] Compilacion terminada en:
echo        %SALIDA%

if /i "%~1"=="desplegar" (
    echo.
    echo  Desplegando en Tomcat como /%APP%
    if exist "%TOMCAT%\webapps\%APP%" rmdir /s /q "%TOMCAT%\webapps\%APP%"
    xcopy "%SALIDA%" "%TOMCAT%\webapps\%APP%" /e /i /y /q > nul
    echo  Listo. Inicie Tomcat y MySQL en el panel de XAMPP y abra:
    echo        http://localhost:8080/%APP%/
)

echo ===========================================================================
echo.
endlocal
