@echo off

echo ============= 1 ==============
call javac *.java
echo done!

echo ============= 2 ==============
call java Project ../ex/"%~1".java

echo ============= 3 ==============
call java -jar ../jasmin.jar JVM.j

start cmd.exe @cmd /k "@echo off&echo ============= 4 ==============&date /t&time /t&call javac ../ex/"%~1".java&call javap -c ../ex/"%~1".class&date /t&time /t"

echo ============= 5 ==============
call java "%~1"