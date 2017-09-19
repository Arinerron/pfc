#!/bin/bash

set -e;

# check dependencies
command -v git >/dev/null 2>&1 || { echo "\033[0;31mError: pfc is dependant on the package \`git\`.\033[0m" >&2; exit 1; }
command -v java >/dev/null 2>&1 || { echo "\033[0;31mError: pfc is dependant on the package \`java\`.\033[0m" >&2; exit 1; }
command -v javac >/dev/null 2>&1 || { echo "\033[0;31mError: pfc is dependant on the package \`javac\`.\033[0m" >&2; exit 1; }
command -v gcc >/dev/null 2>&1 || { echo "\033[0;31mError: pfc is dependant on the package \`gcc\`.\033[0m" >&2; exit 1; }

location="$HOME/.pfc";

echo -n "\033[0;32m"; # color :)

# check if pfc is already installed
if [ -d "$location" ]; then
    echo -n "\033[0;31mThe pfc working folder already exists. Reinstall pfc (y/n)? \033[0;32m";
    read answer;
    if echo "$answer" | grep -iq "^y" ; then
        echo "Deleting old pfc installation...";
        rm -rf $location;
        # TODO: Remove the PATH explort string from $HOME/.profile
    else
        echo "Goodbye...";
        exit;
    fi
fi

# enter the directory
mkdir -p $location;
cd $location;

# download git repository
echo "Downloading files...";
git init . > /dev/null;
git remote add origin https://github.com/Arinerron/pfc > /dev/null;
git pull -q origin master > /dev/null;

# compile code to run pfc
echo "Compiling pfc...";
chmod +x ./pfc;
chmod +x ./compile.sh
sh compile.sh;

# add working directory to path
echo "export PATH=\$PATH:$location/" >> $HOME/.profile;
export PATH=$PATH:$location/

sleep 1;
echo "Installation complete.\033[0m";
exit;
