@ECHO OFF

setlocal enableDelayedExpansion

@ECHO %~dp0
@ECHO %~dp0/target

mkdir "%~dp0/jars"
cd %~dp0/jars
del *.* /F /Q
cd %~dp0

@REM windows
CALL mvn -Djavacpp.platform.custom -Djavacpp.platform.windows-x86_64 clean compile assembly:single -e
cd %~dp0/target
FOR %%F in (multicrew.arty*) do (
    set "name=%%~nF"
    set "ext=%%~xF"
    set "newname=!name!.windows-x86_64.!ext!"
    ren "%%F" "!newname!"
    move "!newname!" "%~dp0/jars/"
)

@REM linux
cd %~dp0
CALL mvn -Djavacpp.platform.custom -Djavacpp.platform.linux-x86_64 -Djavacpp.platform.linux-arm64 -Djavacpp.platform.linux-ppc64le clean compile assembly:single -e
cd %~dp0/target
FOR %%F in (multicrew.arty*) do (
    set "name=%%~nF"
    set "ext=%%~xF"
    set "newname=!name!.linux.!ext!"
    ren "%%F" "!newname!"
    move "!newname!" "%~dp0/jars/"
)

@REM mac
cd %~dp0
CALL mvn -Djavacpp.platform.custom -Djavacpp.platform.macosx-x86_64 -Djavacpp.platform.macosx-arm64 clean compile assembly:single -e
cd %~dp0/target
FOR %%F in (multicrew.arty*) do (
    set "name=%%~nF"
    set "ext=%%~xF"
    set "newname=!name!.macosx.!ext!"
    ren "%%F" "!newname!"
    move "!newname!" "%~dp0/jars/"
)
@REM all 3 no mobile
cd %~dp0
CALL mvn -Djavacpp.platform.custom -Djavacpp.platform.windows-x86_64 -Djavacpp.platform.linux-x86_64 -Djavacpp.platform.linux-arm64 -Djavacpp.platform.linux-ppc64le -Djavacpp.platform.macosx-x86_64 -Djavacpp.platform.macosx-arm64 clean compile assembly:single -e
cd %~dp0/target
FOR %%F in (multicrew.arty*) do (
    move "%%F" "%~dp0/jars/"
)