@echo off
setlocal

set "JAVA_HOME=C:\skill_assessment_task\pleiades\java\8"
set "PATH=%JAVA_HOME%\bin;%PATH%"

java -jar "yamato.jar" "C:\submit\1nf" "1nf"

endlocal
pause
