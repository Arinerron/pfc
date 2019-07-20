# About
pfc is a cli-based modular toolkit for pentesting and reconnaissance.

**This project is still under development, and many features have not been implemented yet.**

# Installation
## Arch User Repository
We're in the AUR at https://aur.archlinux.org/packages/pfc-git/
```
trizen -S pfc-git
```

## Automatic pfc installation
```
# simply execute the below string
curl https://gist.githubusercontent.com/Arinerron/fdab66594c87ee105a0a4ed5ba734343/raw/73a8311e60ca08d66ca2deda3ca3777907364474/install.sh | sh

# don't like blindly downloading and running scripts? neither do I! feel free to download and view the source first
curl https://gist.githubusercontent.com/Arinerron/fdab66594c87ee105a0a4ed5ba734343/raw/73a8311e60ca08d66ca2deda3ca3777907364474/install.sh > install.sh
less install.sh
# ...and once you feel comfortable
sh install.sh
```

pfc will be installed to `$HOME/.pfc`.

**Troubleshooting**
1. `pfc: command not found`? The reason is probably because for whatever reason, `$HOME/.profile` is not executing. Try adding the line `source .profile` to your `.bashrc`/`.zshrc` to fix this, and reset the session.
2. Still not working? Try doing `cd ~/.pfc`, then executing `sh run.sh`. Does that work? If so, then the problem is most likely that your `PATH` variable is still not being set. Make sure to add `/home/<user>/.pfc` to your `PATH`.
3. If for any reason you need to reinstall it, just re-run the `curl` script above. To uninstall, just `rm /home/<user>/.pfc/`.
4. If you have _any_ other problems, feel free to open a new issue using GitHub's issue tracker.

## Manual installation
```
# first make sure you have the following packages installed:
# git, java, javac

# clone the repository
git clone http://github.com/Arinerron/pfc.git

# enter the repository folder
cd pfc

# give files execute permissions
chmod +x run.sh compile.sh pfc

# compile the files (or recompile the files)
sh compile.sh

# set up PATH
echo "export PATH=\$PATH:`pwd`/" >> $HOME/.profile;
export PATH=$PATH:`pwd`/

# run the program
pfc

# and later, if you feel like getting the latest update, run the following
git pull
sh compile.sh
```

You may need to add `source ~/.profile` to `.bashrc` or `.zshrc` or whatever if it says `pfc: command not found`.

# Modules
## help
If a module is specified, the help menu will be displayed for it. If not, a list of all of the modules with be displayed.
```
syntax: help [module]
```

## exit
This module is simply for exiting the console.
```
syntax: exit
```

## alias
Shows all of the aliases for either the specified modules, or, if no modules are specified, it shows the aliases of all of the modules.
```
syntax: alias [module]
```

## file
Reads file signature to discover filetype, or lists files in directory
```
syntax: file <paths>
```

## pfind
A tool for finding someone's online profiles
```
syntax: pfind <username>
```

## subdomains
Scans for subdomains of a given domain
```
syntax: subdom <domain>
```

## ghtiming
Performs a timing attack on GitHub to discover if a hidden repository exists
```
syntax: ghtiming <url>
```
