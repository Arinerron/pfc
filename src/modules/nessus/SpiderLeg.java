package modules.nessus;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

public class SpiderLeg {
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    private List<String> links = new LinkedList<String>();
    private Document htmlDocument;

    /**
     * This performs all the work. It makes an HTTP request, checks the
     * response, and then gathers up all the links on the page. Perform a
     * searchForWord after the successful crawl
     *
     * @param url
     *            - The URL to visit
     * @return whether or not the crawl was successful
     */
    public boolean crawl(String url) {
        try {
            final String domain = getDomainName(url);
            Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
            Document htmlDocument = connection.get();
            this.htmlDocument = htmlDocument;
            if (!connection.response().contentType().contains("text/html"))
                return false;
            Elements linksOnPage = htmlDocument.select("a[href]");
            for(Element link : linksOnPage) {
                String lnk = link.absUrl("href");
                if(domain.equals(getDomainName(lnk)) && this.links.contains(lnk))
                    this.links.add(lnk);
            }
            return true;
        } catch (Exception ioe) {
            // We were not successful in our HTTP request
            return false;
        }
    }

    public static String getDomainName(String url) throws Exception {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    public List<String> getLinks() {
        return this.links;
    }

}
