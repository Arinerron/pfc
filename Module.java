import java.util.*;
import java.util.regex.*;

public class Module {
    public char tchar = '/';
    public boolean liveupdate = false;
    public String livestring = "";

    public Module() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                switch(tchar) {
                    case '/':
                        tchar = '–';
                        break;
                    case '–':
                        tchar = '\\';
                        break;
                    case '\\':
                        tchar = '|';
                        break;
                    case '|':
                        tchar = '/';
                        break;
                }

                if(liveupdate)
                    report(Status.TCHAR, livestring);
            }
        }, 150, 150);
    }

    // this function runs when the command is typed
    public void run(RunConfiguration config) {}

    // registers a class under different commands
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
        if(status == Status.NOLINE)
            System.out.print("\033[2K");

        String statustag = "*";
        switch(status) {
            case Status.INFO:
                statustag = "+";
                break;
            case Status.WARNING:
                statustag = "-";
                break;
            case Status.ERROR:
                statustag = "!";
                break;
            case Status.STATUS:
                statustag = "*";
                break;
            case Status.EMPTY:
                System.out.println();
                return;
            case Status.NOLINE:
                statustag = "*";
                break;
            case Status.CLEARLINE:
                System.out.print("\033[2K");
                return;
            case Status.RAW:
                System.out.println(e);
                return;
            case Status.TCHAR:
                statustag = "" + tchar;
                break;
            case Status.HELP:
                statustag = "+";
                break;
        }

        boolean color = (status == Status.HELP) || (status == Status.ERROR);

        System.out.print(Color.GREEN + "[" + Color.BLUE + statustag + Color.GREEN + "] " + (color ? Color.RED : Color.RESET) + (e != null ? e.toString().replaceAll(Pattern.quote("{load}"), "[" + tchar + "]") : "Unknown error. Please report this.") + (status != Status.NOLINE && status != Status.TCHAR ? "\n" : "\r") + (color ? Color.RESET : ""));
    }
}
