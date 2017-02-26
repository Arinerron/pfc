package modules;

import core.*;

import java.util.*;

public class ModuleExit extends Module {
    private boolean me = false;

    public ModuleExit() {
        super();
        register("exit", "bye", "kys", "leave", "goodbye", "cya", "x", "clr", "clear");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                if(!me)
                    report(Status.EMPTY, null);
                report(Status.INFO, Color.GREEN + "bye!");
            }
        });
    }

    @Override
    public void help() {
        report(Status.HELP, "This module is simply for exiting the console.");
        report(Status.HELP, "syntax: exit");
    }

    @Override
    public void run(RunConfiguration config) {
        if(config.getP().equalsIgnoreCase("clear") || config.getP().equalsIgnoreCase("clr")) {
            report(Status.RESET, null);
        } else {
            me = true;
            System.exit(0);
        }
    }
}
