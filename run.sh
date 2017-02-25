set -e

rm *.class
javac -cp .:$(echo lib/*.jar | tr ' ' ':'):$(echo *.java | tr ' ' ':') Console.java;
java -cp .:$(echo lib/*.jar | tr ' ' ':'):$(echo *.java | tr ' ' ':') Console
rm *.class
