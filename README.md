# About
pfc is a cli-based modular toolkit for pentesting and reconnaissance.

# Installation
```
git clone http://git.arinerron.com/pfc
cd pfc
sh run.sh
```

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
