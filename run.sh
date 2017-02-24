set -e

javac -cp .:$(echo lib/*.jar | tr ' ' ':'):Status.java Main.java;
java -cp .:$(echo lib/*.jar | tr ' ' ':'):Status.java Main
