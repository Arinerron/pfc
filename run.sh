rm *.class > /dev/null 2>&1

set -e

javac -cp .:$(echo lib/*.jar | tr ' ' ':'):$(echo *.java | tr ' ' ':') Console.java;
java -cp .:$(echo lib/*.jar | tr ' ' ':'):$(echo *.java | tr ' ' ':') Console
