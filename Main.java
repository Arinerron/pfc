import java.util.*;
import java.util.regex.*;
import org.apache.http.util.*;
import org.apache.http.client.*;
import org.apache.http.cookie.*;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.*;
import org.apache.http.impl.client.*;
import org.apache.http.message.*;
import org.apache.http.*;
import org.apache.http.client.entity.*;

public class Main {
    public static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36";

    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        List<String> list = lookup("chaoswebs.net");
        for(String subdomain : list) {
            System.out.println("[+] FOUND: " + subdomain);
        }
    }

    // returns a list of subdomains on a given domain
    public List<String> lookup(String domain) {
        List<String> subdomains = new ArrayList<>();
        HttpClient client = HttpClients.createDefault();

        /* Enumerate Google for subdomains */
        String googleurl = "https://google.com/search?q={query}&btnG=Search&hl=en-US&biw=&bih=&gbv=1&start={page}&filter=0";
        String r_query = Pattern.quote("{query}");
        String r_page = Pattern.quote("{page}");
        String r_cite1 = Pattern.quote("<cite");
        String r_cite2 = Pattern.quote(">");
        String r_cite3 = Pattern.quote("</cite>");
        String r_protocol = Pattern.quote("://");
        String r_slash = Pattern.quote("/");
        int rpp = 10; // results per page; default is 10
        int times = 20; // how many results to go through
        int iterator = 0;
        while(iterator < times) {
            String pageurl = googleurl.replaceAll(r_query, "site:" + domain).replaceAll(r_page, "" + (iterator * rpp));
            String pagecontent = "";

            try {
                pagecontent = httpGET(client, pageurl);
            } catch(Exception e) {
                report(Status.ERROR, e);
                iterator = times;
                break;
            }

            if(pagecontent.contains("Our systems have detected unusual traffic")) {
                report(Status.INFO, "Google is blocking the traffic. Moving on...");
                iterator = times;
                break;
            } else if(pagecontent.contains("did not match any documents")) {
                iterator = times;
                break;
            } else {
                String[] cases = pagecontent.split(r_cite1);
                for(int i = 0; i < cases.length; i++)
                    if(i != 0) {
                        String result = cases[i].split(r_cite2)[1].split(r_cite3)[0];
                        if(result.startsWith("http")) {
                            String subdomain = result.split(r_protocol)[1].split(r_slash)[0];
                            if(!subdomains.contains(subdomain) && subdomain.contains(domain))
                                subdomains.add(subdomain);
                        }
                    }
            }

            iterator++;
        }

        /* Enumerate Ask for subdomains */
        String askurl = "http://www.ask.com/web?q=site%3A{query}+-www.{query}&page={page}";
        String r_res1 = Pattern.quote("<p class=\"web-result-url\">");
        String r_res2 = Pattern.quote("</p>");
        iterator = 0;
        while(iterator < times) {
            String pageurl = askurl.replaceAll(r_query, domain).replaceAll(r_page, "" + (iterator + 1));
            String pagecontent = "";

            try {
                pagecontent = httpGET(client, pageurl);
            } catch(Exception e) {
                report(Status.ERROR, e);
                iterator = times;
                break;
            }

            if(pagecontent.contains("Make sure all words are spelled correctly")) {
                iterator = times;
                break;
            } else {
                String[] cases = pagecontent.split(r_res1);
                for(int i = 0; i < cases.length; i++)
                    if(i != 0) {
                        String subdomain = cases[i].split(r_res2)[0].split(r_slash)[0];
                        if(subdomain.contains(domain) && !subdomains.contains(subdomain))
                            subdomains.add(subdomain);
                    }
            }

            iterator++;
        }

        /* Scrape DNSDumpster for subdomains */
        try {
            String r_col1 = Pattern.quote("<tr><td class=\"col-md-4\">");
            String r_col2 = Pattern.quote("<br>");
            String csrftoken = "";
            List<Cookie> cookies = getCookies(client, "https://dnsdumpster.com/");
            for(Cookie cookie : cookies)
                if(cookie.getName().contains("csrf"))
                    csrftoken = cookie.getValue();

            List<NameValuePair> form = new ArrayList<NameValuePair>();
            form.add(new BasicNameValuePair("csrfmiddlewaretoken", csrftoken));
            form.add(new BasicNameValuePair("targetip", domain));
            String cookie = "csrftoken";
            String pagecontent = httpPOST(client, "https://dnsdumpster.com/", cookie, form);

            String[] cases = pagecontent.split(r_col1);
            for(int i = 0; i < cases.length; i++)
                if(i != 0) {
                    String subdomain = cases[i].split(r_col2)[0];
                    if(subdomain.contains(domain) && !subdomains.contains(subdomain))
                        subdomains.add(subdomain);
                }
        } catch(Exception e) {
            report(Status.ERROR, e);
            iterator = times;
        }

        return subdomains;
    }

    // returns the content of a page fetched via an HTTP GET request
    public String httpGET(HttpClient client, String http) throws Exception {
        HttpGet request = new HttpGet(http);
        request.setHeader("User-Agent", Main.USER_AGENT);
        HttpResponse response = client.execute(request);
        return EntityUtils.toString(response.getEntity());
   }

    // returns the content of a page fetched via an HTTP GET request
    public String httpGET(HttpClient client, String http, String cookie) throws Exception {
       HttpGet request = new HttpGet(http);
       request.setHeader("User-Agent", Main.USER_AGENT);
       request.setHeader("Cookie", cookie);
       HttpResponse response = client.execute(request);
       return EntityUtils.toString(response.getEntity());
    }

    // returns the content of a page fetched via an HTTP POST request
    public String httpPOST(HttpClient client, String http, String cookie, List<NameValuePair> form) throws Exception {
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, Consts.UTF_8);
        HttpPost request = new HttpPost(http);
        request.setHeader("User-Agent", Main.USER_AGENT);
        request.setHeader("Cookie", cookie);
        request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        request.setHeader("Referer", http);
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        return EntityUtils.toString(response.getEntity());
    }

   // returns the cookies of a site fetched via an HTTP GET request
   public List<Cookie> getCookies(HttpClient client, String http) throws Exception {
       HttpGet request = new HttpGet(http);
       request.setHeader("User-Agent", Main.USER_AGENT);
       HttpClientContext context = HttpClientContext.create();
       HttpResponse response = client.execute(request, context);
       return context.getCookieStore().getCookies();
  }

   // reports a user-friendly error message
   public void report(int status, Object e) {
       String statustag = "INFO";
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
       }

       System.out.println("[" + statustag + "] " + (e != null ? e.toString() : "Unknown error. Please report this."));
   }
}
