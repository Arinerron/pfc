rm */*.class > /dev/null 2>&1

set -e

cd src

javac -d ../bin -cp .:$(echo ../lib/*.jar | tr ' ' ':'):$(echo *.java | tr ' ' ':') Console.java;

cd ../bin

java -cp .:$(echo ../lib/*.jar | tr ' ' ':'):$(echo *.class | tr ' ' ':') Console

cd ..
