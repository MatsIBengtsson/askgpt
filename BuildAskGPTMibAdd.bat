@echo off
setlocal enabledelayedexpansion
call "%~dp0\venv\Scripts\activate.bat"
:: Load environment variables from .env file
for /f "delims=" %%A in ('%VIRTUAL_ENV%\scripts\python.exe LoadDotEnv.py') do (
    %%A
)

:: Check if DOTENV_VAULT_ID is set
if "%DOTENV_VAULT_ID%"=="" (
    echo DOTENV_VAULT_ID is not set in .env file
    exit /b 1
)
:: Load environment variables from .env.vault using dotenv-vault
npx dotenv-vault@latest push
if %ERRORLEVEL% NEQ 0 (
    echo Error loading environment variables from .env.vault
    exit /b 1
)
