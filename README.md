# About
pfc is a cli-based modular toolkit for pentesting and reconnaissance.

# Installation
```
git clone http://git.arinerron.com/pfc
cd pfc
sh run.sh
```

# Modules
## help
If a module is specified, the help menu will be displayed for it. If not, a list of all of the modules with be displayed.
```
syntax: help [module]
```

## alias
Shows all of the aliases for either the specified modules, or, if no modules are specified, it shows the aliases of all of the modules.
```
syntax: alias [module]
```

## exit
This module is simply for exiting the console.
```
syntax: exit
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
