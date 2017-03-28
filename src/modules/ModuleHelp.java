package modules;

import core.*;

import java.util.*;

public class ModuleHelp extends Module {
    public ModuleHelp() {
        super();
        regular();
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

            List<String> names = new ArrayList<>();
            List<String> regulars = new ArrayList<>();
            List<String> disabled = new ArrayList<>();
            List<String> normals = new ArrayList<>();

            Iterator it = Console.modules.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                if(Console.map.get(pair.getKey()).isRegular())
                    regulars.add(Color.BLUE + pair.getKey());
                else if(!Console.map.get(pair.getKey()).isDisabled())
                    normals.add("" + pair.getKey());
                else
                    disabled.add(Color.RED + pair.getKey());
            }

            // don't sort regulars
            Collections.sort(normals);
            Collections.sort(disabled);

            names.addAll(regulars);
            names.addAll(normals);
            names.addAll(disabled);

            for(int i = 0; i < names.size(); i ++) {
                builder.append(names.get(i) + Color.GREEN + (i != names.size() - 1 ? ", " : ""));
            }

            report(Status.HELP, builder.toString());
        }
    }

    @Override
    public void help() {
        run(null);
    }
}
