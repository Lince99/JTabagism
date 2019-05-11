#!/bin/sh
clear && reset
echo "Compiling Core..."
javac *.java -Xlint:unchecked
echo "Executing JTabagism..."
time -p java Main 2> /dev/null
echo "Removing Compilated classes..."
rm -rf *.class
