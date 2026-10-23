@echo off
setlocal
set DIRNAME=%~dp0
if defined JAVA_HOME (
  set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
) else (
  set "JAVA_EXE=java.exe"
)
if not exist "%DIRNAME%gradle\wrapper\gradle-wrapper.jar" (
  echo Gradle Wrapper JAR is missing. Run powershell -ExecutionPolicy Bypass -File scripts\prepare-windows.ps1
  exit /b 1
)
"%JAVA_EXE%" -Xmx64m -classpath "%DIRNAME%gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*
exit /b %ERRORLEVEL%
