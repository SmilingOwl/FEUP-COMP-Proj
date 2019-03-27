@ECHO OFF
ECHO ============================
cd ..
call jjtree Project.jjt
ECHO ============================
cd ASTFiles
call javacc Project.jj
ECHO ============================
call javac *.java
