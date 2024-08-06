@echo off
setlocal
:: Load environment variables from .env file
for /f "tokens=1,2 delims==" %%A in ('type .env') do (
    set "%%A=%%B"
)
:: Check if DOTENV_VAULT_ID is set
if "%DOTENV_VAULT_ID%"=="" (
    echo DOTENV_VAULT_ID is not set in .env file
    exit /b 1
)
:: Run the npx command
npx dotenv-vault@latest new %DOTENV_VAULT_ID%
if %ERRORLEVEL% NEQ 0 (
    echo Error generating .env.vault file
    exit /b 1
)
:: Load environment variables from .env.vault using dotenv-vault
npx dotenv-vault@latest pull
if %ERRORLEVEL% NEQ 0 (
    echo Error loading environment variables from .env.vault
    exit /b 1
)
