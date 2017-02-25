import java.util.*;
import java.util.regex.*;

public class Console {
    public Scanner scanner = null;
    public static HashMap<String, Module> map = new HashMap<>();
    public static HashMap<String, Module> modules = new HashMap<>();

    public static void main(String[] args) {
        new Console();
    }

    // new Console instance
    public Console() {
        try {
            this.scanner = new Scanner(System.in);
        } catch(Exception e) {
            e.printStackTrace();
        }

        new ModuleHelp();
        new ModuleExit();
        new ModuleSubdomain();

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
