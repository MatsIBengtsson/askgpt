@echo off
setlocal enabledelayedexpansion
call "%~dp0\venv\Scripts\activate.bat"
:: Load environment variables from .env file
for /f "delims=" %%A in ('%VIRTUAL_ENV%\scripts\python.exe LoadDotEnv.py') do (
    %%A
)
:: Check if PLUGIN_PRIVATE_KEY_PASSPHRASE is set
if "%PLUGIN_PRIVATE_KEY_PASSPHRASE%"=="" (
    echo PLUGIN_PRIVATE_KEY_PASSPHRASE is not set in environment variables
    exit /b 1
)
:: Create a subfolder for PEM files
set "KEYS_DIR=KeyFiles"
if not exist "%KEYS_DIR%" (
    mkdir "%KEYS_DIR%"
)
:: Generate the encrypted private key
openssl genpkey -aes-256-cbc -algorithm RSA -out "%KEYS_DIR%\private_encrypted.pem" -pkeyopt rsa_keygen_bits:4096 -pass pass:"%PLUGIN_PRIVATE_KEY_PASSPHRASE%"
:: Convert the encrypted private key to RSA format
openssl rsa -in "%KEYS_DIR%\private_encrypted.pem" -out "%KEYS_DIR%\private.pem" -passin pass:"%PLUGIN_PRIVATE_KEY_PASSPHRASE%"
:: Generate the certificate chain
openssl req -key "%KEYS_DIR%\private.pem" -new -x509 -days 365 -out "%KEYS_DIR%\chain.crt" -passin pass:"%PLUGIN_PRIVATE_KEY_PASSPHRASE%" -subj "/C=SE/ST=Skane/L=Malmoe/O=MatsMIB/OU=Consulting/CN=Mats Bengtsson/emailAddress=mats.bengtsson@mibnet.se"
echo Keys have been generated successfully.
endlocal
