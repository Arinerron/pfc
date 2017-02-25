import java.util.*;

public class ModuleHelp extends Module {
    public ModuleHelp() {
        super();
        register("help", "h", "halp", "hlp", "helpme", "?", "??", "???");
    }

    @Override
    public void run(RunConfiguration config) {
        if(config != null && config.getArray().length != 1) {
            for(int i = 1; i < config.getArray().length; i++) {
                String param = config.getArray()[i].toLowerCase();
                if(Console.map.containsKey(param)) {
                    ((Module)Console.map.get(param)).help();
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
                builder.append(pair.getKey() + (it.hasNext() ? ", " : ""));
            }

            report(Status.HELP, builder.toString());
        }
    }

    @Override
    public void help() {
        run(null);
    }
}
