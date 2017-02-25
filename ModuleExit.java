import java.util.*;

public class ModuleExit extends Module {
    private boolean me = false;

    public ModuleExit() {
        super();
        register("exit", "bye", "kys", "leave", "goodbye", "cya");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                if(!me)
                    report(Status.EMPTY, null);
                report(Status.INFO, "bye!");
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
        me = true;
        System.exit(0);
    }
}
