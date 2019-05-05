#!/bin/sh
#TODO FIX PACKAGE TREE
set -e
mkdir -p Release

echo "\e[01;32mCompiling Core...\e[0m"
javac -d Release Tabagism_core/*.java -Xlint:unchecked -verbose
echo "\e[01;32mCompiling GUI...\e[0m"
javac -classpath /usr/local/share/java/gtk-4.1.jar -d Release Tabagism_GUI/*.java -Xlint:unchecked -verbose
echo "\e[01;32mCreating jar...\e[0m"
jar -cvmf MANIFEST.MF Release/JTabagism.jar Release/Tabagism/Tabagism_core/*.class
echo "\e[01;32mRemoving Compilated classes...\e[0m"
rm -rf Release/Tabagism/
echo "\e[01;32mExecuting JTabagism...\e[0m"
java -jar Release/JTabagism.jar
