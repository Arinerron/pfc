package core;

import modules.*;

import java.util.*;
import java.util.regex.*;
import java.io.*;

public class Console {
    public Scanner scanner = null;
    public static HashMap<String, Module> map = new HashMap<>();
    public static HashMap<String, Module> modules = new HashMap<>();
    public static final String banner = "    ##                                                                    ##\r\n  ############################################################################\r\n    ##                    ______    _______   ______                      ##\r\n    ##                    |   _  \\  |   ____| /      |                    ##\r\n    ##                    |  |_)  | |  |__   |  ,----'                    ##\r\n    ##                    |   ___/  |   __|  |  |                         ##\r\n    ##                    |  |      |  |     |  `----.                    ##\r\n    ##                    | _|      |__|      \\______|                    ##\r\n    ##                                                                    ##\r\n    ##                         Author: Arinerron                          ##\r\n    ##                           Version: 1.0                             ##\r\n  ############################################################################\r\n    ##                                                                    ##\r\n";

    // new Console instance
    public Console(String[] args) {
        //print banner
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
                    System.out.println("[-] Failed to load module " + classname);
                    e.printStackTrace();
                }

        new ModuleHelp();

        while(true) {
            System.out.print(Color.GREEN + "pfc" + Color.WHITE + "-> " + Color.CYAN);

            final String line = readLine();
            final String[] a = line.split(Pattern.quote(" "));
            final String p = a[0].toLowerCase();

            final RunConfiguration config = new RunConfiguration() {
                public String getCommand() {
                    return line;
                }

                public String[] getArray() {
                    return a;
                }

                public String getP() {
                    return p;
                }
            };

            if(this.map.containsKey(p))
                ((Module)this.map.get(p)).run(config);
        }
    }

    public boolean is(String i, String... is) {
        for(String s : is)
            if(s.equals(i))
                return true;
        return false;
    }

    public String readLine() {
        return scanner.nextLine();
    }
}
