package modules;

import core.*;

import java.util.*;

public class ModuleAlias extends Module {
    public ModuleAlias() {
        super();
        regular();
        register("alias", "aliases", "al", "nick", "aliass", "alia");
    }

    @Override
    public void run(RunConfiguration config) {
        if(config != null && config.getArray().length != 1) {
            for(int i = 1; i < config.getArray().length; i++) {
                String param = config.getArray()[i].toLowerCase();
                if(Console.modules.containsKey(param)) {
                    alias(param);
                } else {
                    report(Status.ERROR, "Unknown module \"" + param + "\".");
                }
            }
        } else {
            Iterator it = Console.modules.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                alias(pair.getKey().toString());
            }
        }
    }

    public void alias(String module) {
        Module m = Console.modules.get(module);

        StringBuilder builder = new StringBuilder();
        builder.append((m.isDisabled() ? Color.RED : Color.WHITE)).append(module + ": " + Color.GREEN);

        Iterator it = Console.map.entrySet().iterator();
        List<String> aliases = new ArrayList<>();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if((Module)pair.getValue() == m)
                aliases.add(pair.getKey().toString());
        }

        for(int i = 0; i < aliases.size(); i++)
            builder.append(aliases.get(i) + (i != aliases.size() - 1 ? ", " : ""));

        report(Status.INFO, builder.toString());
    }

    @Override
    public void help() {
        report(Status.HELP, "Shows all of the aliases for either the specified modules, or all");
        report(Status.HELP, "syntax: alias [module]");
    }
}
