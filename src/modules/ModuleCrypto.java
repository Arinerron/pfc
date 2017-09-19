package modules;

import core.*;

import java.util.*;

public class ModuleCrypto extends Module {
    private boolean me = false;

    public ModuleCrypto() {
        super();
        register("crypto", "crypt", "cryptography", "cryptoops");
        CryptoOps.init();
    }

    @Override
    public void help() {
        report(Status.HELP, "This module provides support for performing cryptographic operations.");
        subhelp("help", "Shows this help menu");
        subhelp("base64-encode <string>", "Encodes the given string with base64");
        subhelp("base64-decode <string>", "Decodes the given string from base64");
        subhelp("hex-encode <string>", "Encodes the given string with hex");
        subhelp("hex-decode <string>", "Decodes the given string from hex");
        subhelp("morse-encode <string>", "Encodes the given string with morse");
        subhelp("morse-decode <string>", "Decodes the given string from morse");
        subhelp("atbash <string>", "Encodes the given string using the atbash cipher");
        subhelp("rot <int> <string>", "Shifts the given string a given number of characters");
        subhelp("hash <algorithm/all> <string>", "Hashes the given string with the given algorithm");
        subhelp("reverse <string>", "Reverses the characters in the given string");
        subhelp("is-prime <int>", "Checks if the given integer is prime");
        subhelp("phi <int>", "Performs ϕ(int) on the given number");
    }

    private void subhelp(String command, String description) {
        report(Status.HELP, "`crypto " + command + "` - " + description);
    }

    @Override
    public void run(RunConfiguration config) {
        String[] array = config.getArray();
        if(array.length >= 2) {
            String cmd = array[1];
            if(Console.is(cmd, "help", "h")) {
                help();
            } else if(Console.is(cmd, "phi")) {
                report(Status.INFO, "you entered " + cmd);
            }
        } else {
            help();
        }
    }
}
