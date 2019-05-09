#!/bin/sh
clear && reset
echo "Compiling Core..."
javac *.java -Xlint:unchecked
echo "Executing JTabagism..."
java Main
echo "Removing Compilated classes..."
rm -rf *.class
