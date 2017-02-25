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
        }, 200, 200);
    }

    public void run() {

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
        }

        System.out.print("[" + statustag + "] " + (e != null ? e.toString().replaceAll(Pattern.quote("{load}"), "[" + tchar + "]") : "Unknown error. Please report this.") + (status != Status.NOLINE && status != Status.TCHAR ? "\n" : "\r"));
    }
}
