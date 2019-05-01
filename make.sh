#!/bin/sh
mkdir -p Release
echo "Compiling Core..."
javac -d Release Tabagism_core/*.java
echo "Compiling GUI..."
javac -classpath /usr/local/share/java/gtk-4.1.jar -d Release Tabagism_GUI/GUI_Main.java
echo "Creating jar..."
jar -cvmf MANIFEST.MF Release/JTabagism.jar Release/Tabagism/Tabagism_core/Main.class
echo "Removing Compilated classes..."
rm -rf Release/JTabagism
echo "Executing JTabagism..."
#TODO FIX THIS
java -jar Release/JTabagism.jar
