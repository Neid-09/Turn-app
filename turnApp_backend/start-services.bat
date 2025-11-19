@echo off
REM Script para iniciar los servicios de TurnApp en el orden correcto
REM Orden: config-server -> discovery-server -> api-gateway

echo ========================================
echo Iniciando servicios de TurnApp
echo ========================================
echo.

REM Colores para mensajes
set "GREEN=[92m"
set "YELLOW=[93m"
set "RESET=[0m"

REM Obtener la ruta del directorio del script
set "BASE_DIR=%~dp0"

REM 1. Iniciar Config Server
echo %YELLOW%[1/3] Iniciando Config Server...%RESET%
cd /d "%BASE_DIR%config-server"
start "Config Server" cmd /k "mvnw.cmd spring-boot:run"
echo %GREEN%Config Server iniciado en una nueva ventana%RESET%
echo Esperando 15 segundos para que Config Server esté listo...
timeout /t 15 /nobreak >nul
echo.

REM 2. Iniciar Discovery Server
echo %YELLOW%[2/3] Iniciando Discovery Server...%RESET%
cd /d "%BASE_DIR%discovery-server"
start "Discovery Server" cmd /k "mvnw.cmd spring-boot:run"
echo %GREEN%Discovery Server iniciado en una nueva ventana%RESET%
echo Esperando 20 segundos para que Discovery Server esté listo...
timeout /t 20 /nobreak >nul
echo.

REM 3. Iniciar API Gateway
echo %YELLOW%[3/3] Iniciando API Gateway...%RESET%
cd /d "%BASE_DIR%api-gateway"
start "API Gateway" cmd /k "mvnw.cmd spring-boot:run"
echo %GREEN%API Gateway iniciado en una nueva ventana%RESET%
echo.

echo ========================================
echo %GREEN%Todos los servicios han sido iniciados%RESET%
echo ========================================
echo.
echo Config Server:    http://localhost:8888
echo Discovery Server: http://localhost:8761
echo API Gateway:      http://localhost:8080
echo.
echo Presiona cualquier tecla para cerrar esta ventana...
pause >nul
