@REM  Compile all java file
dir src /b /s .\src\*.java > file.txt
findstr /i .java file.txt > sources.txt
javac -source 11 -target 11 -cp ./lib/formulaire.jar;./lib/guava-32.1.2-jre.jar;./lib/jackson-annotations-2.16.1.jar;./lib/jackson-core-2.16.1.jar;./lib/jackson-databind-2.16.1.jar -d ./out/ @sources.txt
del "file.txt"
del  "sources.txt"

cd ./out/
@REM Convert to jar file
jar -cf "connection.jar" .\connection