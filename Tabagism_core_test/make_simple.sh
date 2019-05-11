#!/bin/sh
clear && reset
echo "Compiling Core..."
javac *.java -Xlint:unchecked
echo "Executing JTabagism..."
java Main 20000 20000 20000 100 0 100 2 5
#java Main
#time -p java Main 2> /dev/null
echo "Removing Compilated classes..."
rm -rf *.class
