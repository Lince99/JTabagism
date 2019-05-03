#!/bin/sh
#TODO FIX PACKAGE TREE
mkdir -p Release
echo "Compiling Core..."
javac -d Release Tabagism_core/*.java -Xlint:unchecked
echo "Creating jar..."
jar -cvmf MANIFEST_NOGUI.MF Release/JTabagism.jar Release/Tabagism/Tabagism_core/Main.class
echo "Removing Compilated classes..."
rm -rf Release/Tabagism/
echo "Executing JTabagism..."
java -jar Release/JTabagism.jar
