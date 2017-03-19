package modules;

import core.*;

import java.util.*;

public class ModuleHelp extends Module {
    public ModuleHelp() {
        super();
        register("help", "h", "halp", "hlp", "helpme", "?", "??", "???", "modules", "module", "mods", "mod");
    }

    @Override
    public void run(RunConfiguration config) {
        if(config != null && config.getArray().length != 1) {
            for(int i = 1; i < config.getArray().length; i++) {
                String param = config.getArray()[i].toLowerCase();
                if(Console.map.containsKey(param)) {
                    Module module = ((Module)Console.map.get(param));
                    if(module.isDisabled())
                        Logger.report(Status.WARNING, Color.BOLD + "Module is unstable!");
                    module.help();
                } else {
                    report(Status.ERROR, "Unknown module \"" + param + "\".");
                }
            }
        } else {
            StringBuilder builder = new StringBuilder();

            builder.append(Color.WHITE).append("Modules: " + Color.GREEN);
            Iterator it = Console.modules.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                if(!Console.map.get(pair.getKey()).isDisabled())
                    builder.append(pair.getKey() + (it.hasNext() ? ", " : ""));
                else
                    builder.append(Color.RED + pair.getKey() + Color.GREEN + (it.hasNext() ? ", " : ""));
            }

            report(Status.HELP, builder.toString());
        }
    }

    @Override
    public void help() {
        run(null);
    }
}
