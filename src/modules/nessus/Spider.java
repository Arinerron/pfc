package modules.nessus;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

public class Spider {
    private static final int MAX_PAGES_TO_SEARCH = 25;
    private Set<String> pagesVisited = new HashSet<String>();
    private List<String> pagesToVisit = new LinkedList<String>();
    int i = 0;

    public List<String> search(String url) {
        List<String> data = new ArrayList<String>();
        boolean go = true;
        while (this.pagesVisited.size() < MAX_PAGES_TO_SEARCH && go) {
            String currentUrl;
            SpiderLeg leg = new SpiderLeg();
            if (this.pagesToVisit.isEmpty() && i<MAX_PAGES_TO_SEARCH) {
                currentUrl = url;
                this.pagesVisited.add(url);
            } else {
                currentUrl = this.nextUrl();
                if(currentUrl.equals(""))
                    go = false;
            }
            data.add(currentUrl);
            leg.crawl(currentUrl);
            this.pagesToVisit.addAll(leg.getLinks());i++;
        }

        return data;
    }

    /**
     * Returns the next URL to visit (in the order that they were found). We
     * also do a check to make sure this method doesn't return a URL that has
     * already been visited.
     *
     * @return
     */
    private String nextUrl() {
        try {
            String nextUrl;
            do {
                nextUrl = this.pagesToVisit.remove(0);
            } while (this.pagesVisited.contains(nextUrl));
            this.pagesVisited.add(nextUrl);
            return nextUrl;
        } catch (Exception e) {return "";}
    }

}
