@echo off
echo Creando archivos y carpetas...

echo. > index.js
echo. > .env

mkdir routes
mkdir controllers
mkdir middleware

echo. > routes\authRoutes.js
echo. > controllers\authController.js
echo. > middleware\authMiddleware.js

echo Estructura creada correctamente.
pause