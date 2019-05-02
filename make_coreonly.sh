#!/bin/sh
#TODO FIX PACKAGE TREE
mkdir -p Release
echo "Compiling Core..."
javac -d Release Tabagism_core/*.java
echo "Creating jar..."
jar -cvf Release/JTabagism.jar Release/Tabagism/Tabagism_core/Main.class
echo "Removing Compilated classes..."
rm -rf Release/Tabagism/
echo "Executing JTabagism..."
java -jar Release/JTabagism.jar
#echo "Project Directory tree:"
#tree
