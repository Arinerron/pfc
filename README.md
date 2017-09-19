# About
pfc is a cli-based modular toolkit for pentesting and reconnaissance.

# Installation
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
3. If you have _any_ other problems, feel free to open a new issue using GitHub's issue tracker.

## Manual installation
```
git clone http://git.arinerron.com/pfc
cd pfc
sh run.sh
```

To set up a function so that you can just call `pfc` as a command, execute:
```
echo "pfc() {cd `pwd` && command sh run.sh \"$@\"}" >> ~/.profile
```
Then, `source ~/.profile` to "reset" the session

You can change `.profile` to `.bashrc` or `.zshrc` or whatever as needed.

# Modules
## helphelp, exit, alias, file, pfind, subdomains, ghtiming
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
