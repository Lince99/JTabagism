#!/bin/sh
clear && reset
rm AutoTest/result.txt
echo "Compiling Core..."
javac *.java -Xlint:unchecked
echo "Executing JTabagism..."
java Main

#stupid thig, ik that bash exists, but this will be deleted in the next future
#echo "\n\n\n\n\n\n\n\nTEST LOW\n" >> AutoTest/result.txt
#java Main < AutoTest/low.txt >> AutoTest/result.txt

#echo "\n\n\n\n\n\n\n\nTEST HIGH\n" >> AutoTest/result.txt
#java Main < AutoTest/high.txt >> AutoTest/result.txt

#echo "\n\n\n\n\n\n\n\nTEST NO_TIME\n" >> AutoTest/result.txt
#java Main < AutoTest/no_time.txt >> AutoTest/result.txt

#echo "\n\n\n\n\n\n\n\nTEST HIGH_SMOKER\n" >> AutoTest/result.txt
#java Main < AutoTest/high_smoker.txt >> AutoTest/result.txt

#echo "\n\n\n\n\n\n\n\nTEST HIGH_TIME\n" #>> AutoTest/result.txt
#java Main < AutoTest/high_time.txt #>> AutoTest/result.txt

#java Main 20000 20000 20000 20000 0
#java Main 20000 20000 20000 20000 0 1000 2 5 0
#time -p java Main 2> /dev/null

echo "Removing Compilated classes..."
rm -rf *.class
