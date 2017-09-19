rm -rf bin > /dev/null 2>&1
mkdir bin

set -e

cd src

javac -d ../bin -cp .:modules/*.java:modules/:modules/*:$(echo ../lib/*.jar | tr ' ' ':'):$(echo */*.java | tr ' ' ':') Main.java $(echo modules/*);

cd ../bin

if [ $# -eq 0 ]; then
    clear;
fi

java -cp .:$(echo ../lib/*.jar | tr ' ' ':'):$(echo */*.class | tr ' ' ':') Main $@

cd ..
