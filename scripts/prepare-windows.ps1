$ErrorActionPreference = "Stop"
$projectRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
$target = Join-Path $projectRoot "gradle\wrapper\gradle-wrapper.jar"

if (Test-Path $target) {
    Write-Host "Gradle Wrapper is ready."
    exit 0
}

$parent = Split-Path $projectRoot -Parent
foreach ($directory in Get-ChildItem -Path $parent -Directory) {
    if ($directory.FullName -eq $projectRoot) { continue }
    $candidate = Join-Path $directory.FullName "gradle\wrapper\gradle-wrapper.jar"
    if (Test-Path $candidate) {
        Copy-Item -Path $candidate -Destination $target -Force
        Write-Host "Gradle Wrapper JAR copied from $($directory.Name)."
        exit 0
    }
}

Write-Host "Missing gradle/wrapper/gradle-wrapper.jar. Copy it from a trusted Gradle project."
exit 1
