@ECHO OFF
ECHO ============================
call jjtree Project.jjt
ECHO ============================
call javacc Project.jj
ECHO ============================
call javac *.java
