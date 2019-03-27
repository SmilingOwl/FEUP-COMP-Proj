@ECHO OFF
ECHO ============================
call jjtree Project.jjt
ECHO ============================
cd ASTFiles
call javacc Project.jj
ECHO ============================
cd ..
call javac *.java
