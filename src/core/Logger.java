package core;

import java.util.*;
import java.util.regex.*;

public class Logger {
    public static char tchar = '/';
    public static boolean liveupdate = false;
    public static String livestring = "";
    public static String oldstring = livestring;

    public static void init() {
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

                if(liveupdate) {
                    if(!oldstring.equals(livestring)) {
                        oldstring = livestring;
                        report(Status.CLEARLINE, null);
                    }

                    report(Status.TCHAR, livestring);
                }
            }
        }, 150, 150);
    }

    // reports a user-friendly error message
    public static void report(int status, Object e) {
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
            case Status.RESET:
                System.out.print("\u001b[2J\u001b[H");
                return;
            case Status.RAW:
                System.out.println(e);
                return;
            case Status.RAWNL:
                System.out.print(e);
                return;
            case Status.TCHAR:
                statustag = "" + tchar;
                break;
            case Status.HELP:
                statustag = "+";
                break;
        }

        boolean color = (status == Status.HELP) || (status == Status.ERROR);

        System.out.print(Color.GREEN + Color.BOLD + "[" + Color.BLUE + statustag + Color.GREEN + "] " + Color.RESET + (color ? Color.RED : "") + (e != null ? e.toString().replaceAll(Pattern.quote("{load}"), "[" + tchar + "]") : "Unknown error. Please report this.") + (status != Status.NOLINE && status != Status.TCHAR ? "\n" : "\r") + (color ? Color.RESET : ""));
    }
}
