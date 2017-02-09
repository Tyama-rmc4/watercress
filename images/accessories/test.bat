@echo off

for /f "usebackq" %%i in (`dir /B /S *.jpg`) do (
    SET /P<NUL=%%i:
    type %%i | find /C ""
)

pause
