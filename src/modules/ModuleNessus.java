package modules;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import core.*;
import modules.nessus.*;

public class ModuleNessus extends Module {

    public ModuleNessus() {
        super();
        register("nessus", "n", "pentest", "test", "penetrate", "penetrationtest");
    }

    @Override
    public void run(RunConfiguration config) {
        if(config != null && config.getArray().length != 1) {
            try {
                String URL = config.getArray()[1];
                if(!URL.startsWith("http"))
                    URL = "http://" + URL;
                if(isURL(URL)) {
                    Logger.liveupdate = true;
                    Logger.livestring = "Crawling website...";
                    List<String> list =  new ArrayList<>(new LinkedHashSet<>(new Spider().search(URL)));
                    Logger.liveupdate = false;
                    for(String s : list)
                        test(s);
                } else
                    report(Status.ERROR, "Invalid URL supplied!");
            } catch(Exception e) {
                report(Status.ERROR, e);
            }
        } else
            help();
    }

    @Override
    public void help() {
        report(Status.HELP, "Automatically pentests the given site");
        report(Status.HELP, "syntax: nessus <website>");
    }

    public static boolean isURL(String url) {
        try {
            new URL(url);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String test(String URL) throws Exception {
        if(!isURL(URL))
            return "";

        String[] headers = {"X-XSS-Protection:1; mode=block", "Strict-Transport-Security", "Public-Key-Pins", "X-Frame-Options:SAMEORIGIN", "X-Content-Type-Options:nosniff"};
        List<String> headers_missing = new ArrayList<>();
        List<String> headers_misconfig = new ArrayList<>();
        List<String> parameters_vulnerable = new ArrayList<>();
        List<String> csrf_vulnerable = new ArrayList<>();
        StringBuilder s = new StringBuilder();

        final String filename = URL.split(Pattern.quote("?"))[0].split(Pattern.quote("/"))[URL.split(Pattern.quote("?"))[0].split(Pattern.quote("/")).length - 1];

        URLConnection connection = new URL(URL).openConnection();
        connection.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        for(String header : headers) {
            String[] split = header.split(Pattern.quote(":"));
            if(connection.getHeaderField(split[0]) == null)
                headers_missing.add(header);
            else {
                if(split.length > 1)
                    if(!split[1].trim().equalsIgnoreCase(connection.getHeaderField(split[0].trim())))
                        headers_misconfig.add(header);
            }
        }
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);
        in.close();
        final String html = response.toString();

        if(URL.contains("?")) {
            String[] parameters = URL.split(Pattern.quote("?"))[1].split(Pattern.quote("&"));
            for(String x : parameters) {
                if(x.contains("=")) {
                    String[] g = x.split(Pattern.quote("="));
                    String key = g[0];
                    String val = g[1];

                    if(getText(URL.replaceAll(Pattern.quote(key + "=" + val), key + "='")).contains(" in your SQL syntax"))
                        parameters_vulnerable.add(key);
                }
            }
        }
        boolean valid = false;
        for(String ss : html.split(Pattern.quote("<form"))) {
            if(valid != true) {valid = true;} else {
                String h = ss.split(Pattern.quote("</form"))[0];
                if(!h.contains("authent") && !h.contains("csrf") && !h.contains("token") && !h.contains("/search"))
                    csrf_vulnerable.add("Potential CSRF at " + (h.contains("action=\"") ? " at action=\"" + h.split(Pattern.quote("action=\""))[1].split(Pattern.quote("\""))[0] + "\"" : ""));
            }
        }

        if(headers_missing.size() != 0 || headers_misconfig.size() > 0 || parameters_vulnerable.size() > 0 || csrf_vulnerable.size() > 0)
            report(Status.INFO, "Results for " + URL + " ...");
        if(headers_missing.size() != 0) {
            report(Status.INFO, "Headers Missing [" + headers_missing.size() + "]:");
            for(String header : headers_missing)
                report(Status.INFO, " - " + header.split(Pattern.quote(":"))[0]);
        }
        if(headers_misconfig.size() != 0) {
            report(Status.INFO, "Headers Misconfigured [" + headers_misconfig.size() + "]:");
            for(String header : headers_misconfig) {
                String[] g = header.split(Pattern.quote(":"));
                String key = g[0];
                String val = g[1];

                report(Status.INFO, " - " + key + " (should be `" + val + "`)");
            }
        }
        if(parameters_vulnerable.size() != 0) {
            report(Status.INFO, "SQLi-Vulnerable Parameters [" + parameters_vulnerable.size() + "]:");
            for(String param : parameters_vulnerable)
                report(Status.INFO, " - " + param);
        }
        if(csrf_vulnerable.size() != 0) {
            report(Status.INFO, "CSRF-Vulnerable Parameters [" + csrf_vulnerable.size() + "]:");
            for(String param : csrf_vulnerable)
                report(Status.INFO, " - " + param);
        }

        return s.toString();
    }

    public static String getText(String url) throws Exception {
        System.setProperty("http.agent", "");
        URLConnection connection = new URL(url).openConnection();
        connection.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);
        in.close();
        return response.toString();
    }
}
