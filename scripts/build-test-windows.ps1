$ErrorActionPreference = "Stop"
Set-Location (Resolve-Path (Join-Path $PSScriptRoot ".."))
& "$PSScriptRoot\prepare-windows.ps1"
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
& .\gradlew.bat :app:testDebugUnitTest :app:assembleDebug
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
Write-Host "APK: $(Join-Path (Get-Location) 'app\build\outputs\apk\debug\app-debug.apk')" -ForegroundColor Green
