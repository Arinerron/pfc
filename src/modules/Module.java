package modules;

import core.*;

import java.util.*;
import java.util.regex.*;

public class Module {
    private boolean disabled = false;
    private boolean regular = false;

    public Module() {}

    // this function runs when the command is typed
    public void run(RunConfiguration config) {}

    // registers a class under different aliases
    public void register(String ix, String... is) {
        for(String s : is)
            Console.map.put(s, this);
        Console.map.put(ix.toLowerCase(), this);
        Console.modules.put(ix.toLowerCase(), this);
    }

    // for reporting help messages
    public void help() {
        report(Status.HELP, "The default help message");
    }

    // reports a user-friendly error message
    public void report(int status, Object e) {
        Logger.report(status, e);
    }

    // hides module from user
    public void disable() {
        this.disabled = true;
    }

    // register module as default
    public void regular() {
        this.regular = true;
    }

    // is the module disabled
    public boolean isDisabled() {
        return this.disabled;
    }

    // is the module regular
    public boolean isRegular() {
        return this.regular;
    }
}
