package core;

import modules.*;

import java.util.*;
import java.util.regex.*;
import java.io.*;
import java.math.*;

public class Console {
    public Scanner scanner = null;
    public static HashMap<String, Module> map = new HashMap<>();
    public static HashMap<String, Module> modules = new HashMap<>();
    public static final String banner = "    ##                                                                    ##\r\n  ############################################################################\r\n    ##                    ______    _______   ______                      ##\r\n    ##                    |   _  \\  |   ____| /      |                    ##\r\n    ##                    |  |_)  | |  |__   |  ,----'                    ##\r\n    ##                    |   ___/  |   __|  |  |                         ##\r\n    ##                    |  |      |  |     |  `----.                    ##\r\n    ##                    | _|      |__|      \\______|                    ##\r\n    ##                                                                    ##\r\n    ##                         Author: Arinerron                          ##\r\n    ##                           Version: 1.0                             ##\r\n  ############################################################################\r\n    ##                                                                    ##\r\n";

    // new Console instance
    public Console(String[] args) {
        long before = System.currentTimeMillis();
        //print banner
        Logger.init();
        System.out.println(banner);

        // init scanner
        try {
            this.scanner = new Scanner(System.in);
        } catch(Exception e) {
            e.printStackTrace();
        }

        // generate list of module files
        String r_dollar = Pattern.quote("$");
        String r_class = Pattern.quote(".class");
        List<String> modules = new ArrayList<>();
        File modspack = new File("modules");
        for(File f : modspack.listFiles())
            if(f.isFile() && f.getName().contains(".class"))
                modules.add(f.getName().split(r_class)[0].split(r_dollar)[0]);

        // remove duplicates
        Set<String> hs = new HashSet<>();
        hs.addAll(modules);
        modules.clear();
        modules.addAll(hs);

        // import modules
        for(String classname : modules)
            if(!classname.equals("Module"))
                try {
                    Class.forName("modules." + classname).newInstance();
                } catch(Exception e) {
                    Logger.report(Status.WARNING, "Failed to load module \"" + classname.toLowerCase() + "\"");
                    Logger.report(Status.ERROR, e.toString());
                }

        long after = System.currentTimeMillis();

        Logger.report(Status.STATUS, "Loaded [" + modules.size() + "] modules in [" + round((double)(after - before) / 1000, 2) + "] seconds...");
        Logger.report(Status.STATUS, "Welcome to pfc!");

        new Thread(new Runnable() {
            public void run() {
                Console.this.run();
            }
        }).start();
    }

    public void run() {
        String r_return = Pattern.quote("&&");

        while(true) {
            Logger.report(Status.RAWNL, Color.GREEN + Color.BOLD + "pfc" + Color.WHITE + "-> " + Color.RESET + Color.CYAN);

            final String line1 = readLine();
            String[] cases = line1.split(r_return);

            for(String line : cases) {
                line = line.trim();
                final String returnline = line;

                final String[] a = line.split(Pattern.quote(" "));
                final String p = a[0].toLowerCase();

                final RunConfiguration config = new RunConfiguration() {
                    public String getCommand() {
                        return returnline;
                    }

                    public String[] getArray() {
                        return a;
                    }

                    public String getP() {
                        return p;
                    }
                };

                if(this.map.containsKey(p)) {
                    Module module = ((Module)this.map.get(p));
                    if(module.isDisabled())
                        Logger.report(Status.WARNING, Color.BOLD + "Module is unstable!");
                    module.run(config);
                } else if("clear".equalsIgnoreCase(p)) {
                    System.out.print("\033[H\033[2J");
                    System.out.flush();
                } else {
                    Logger.report(Status.WARNING, "Module does not exist or is not loaded in");
                    ((Module)this.map.get("help")).run(config);
                }
            }
        }
    }

    public boolean is(String i, String... is) {
        for(String s : is)
            if(s.equals(i))
                return true;
        return false;
    }

    // reads one line from the console
    public String readLine() {
        return scanner.nextLine();
    }

    // rounds to the given decimal place
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
