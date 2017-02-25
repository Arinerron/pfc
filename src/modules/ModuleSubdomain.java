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

public class ModuleSubdomain extends Module {
    public static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36";

    public boolean run = true;

    public ModuleSubdomain() {
        super();
        register("subdomains", "subdomain", "subdomainsearch", "subd", "sd", "sds", "subdoms", "sdomain", "subdom");
    }

    // when module is run
    @Override
    public void run(RunConfiguration config) {
        if(config.getArray().length != 1) {
            List<String> list = lookup(config.getArray()[1]);
            java.util.Collections.sort(list, Collator.getInstance());

            report(Status.CLEARLINE, null);
            report(Status.INFO, "Subdomains found [" + list.size() + "]:");
            for(String subdomain : list)
                report(Status.RAW, subdomain);
        } else
            help();
    }

    // when help is displayed
    @Override
    public void help() {
        report(Status.HELP, "Scans for subdomains of a given domain");
        report(Status.HELP, "syntax: subdom <domain>");
    }

    // returns a list of subdomains on a given domain
    public List<String> lookup(String domain) {
        List<String> subdomains = new ArrayList<>();
        HttpClient client = HttpClients.createDefault();
        boolean bruteforce = false;

        String r_query = Pattern.quote("{query}");
        String r_page = Pattern.quote("{page}");

        Logger.liveupdate = true;

        /* Enumerate Google for subdomains */
        Logger.livestring = "Scraping Google for subdomains...";
        String googleurl = "https://google.com/search?q={query}&btnG=Search&hl=en-US&biw=&bih=&gbv=1&start={page}&filter=0";
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
                report(Status.WARNING, "Google is blocking the traffic. Moving on...");
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
        Logger.livestring = "Scraping Ask for subdomains...";
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
        Logger.livestring = "Scraping DNSDumpster for subdomains...";
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

        /* Scrape crt.sh for subdomains */
        Logger.livestring = "Scraping crt.sh for subdomains...";
        String crturl = "https://crt.sh/?q=%25{query}";
        try {
            String r_td1 = Pattern.quote("<TD>");
            String r_td2 = Pattern.quote("</TD>");

            String pagecontent = httpGET(client, crturl.replaceAll(r_query, domain));
            String[] cases = pagecontent.split(r_td1);
            for(int i = 0; i < cases.length; i++)
                if(i != 0) {
                    String subdomain = cases[i].split(r_td2)[0];
                    if(subdomain.contains(domain) && !subdomains.contains(subdomain))
                        subdomains.add(subdomain);
                }
        } catch(Exception e) {
            report(Status.ERROR, e);
        }

        /* Parse Threatcrowd for subdomains */
        Logger.livestring = "Scraping Threatcrowd for subdomains...";
        String tcurl = "https://www.threatcrowd.org/searchApi/v2/domain/report/?domain={query}";
        try {
            String r_quote = Pattern.quote("\"");

            String pagecontent = httpGET(client, tcurl.replaceAll(r_query, domain));
            String[] cases = pagecontent.split(r_quote);
            for(String s : cases)
                if(s.contains(domain) && !s.contains("@") && !s.contains("/") && !s.contains(":") && !subdomains.contains(s))
                    subdomains.add(s);
        } catch(Exception e) {
            report(Status.ERROR, e);
        }

        /* Scrape PassiveDNS for subdomains */
        Logger.livestring = "Scraping PassiveDNS for subdomains...";
        String pdnsurl = "http://ptrarchive.com/tools/search.htm?label={query}";
        try {
            String r_td1 = Pattern.quote("</td><td>");
            String r_td2 = Pattern.quote(" ");

            String pagecontent = httpGET(client, pdnsurl.replaceAll(r_query, domain));
            String[] cases = pagecontent.split(r_td1);
            for(String s : cases) {
                s = s.split(r_td2)[0];
                if(s.contains(domain) && !s.contains("@") && !s.contains("/") && !s.contains(":") && !subdomains.contains(s))
                    subdomains.add(s);
            }
        } catch(Exception e) {
            report(Status.ERROR, e);
        }

        /* Bruteforce top 1000 subdomains */
        /**
         * DISABLED BY DEFAULT:
         * This feature won't work with lots of
         * different ISPs. TODO use NSlookup
         * instead of InetAddress.
         */
        if(bruteforce) {
            Logger.livestring = "Bruteforcing subdomains...";
            run = true;
            final String subs = "www\r\nmail\r\nftp\r\nlocalhost\r\nwebmail\r\nsmtp\r\npop\r\nns1\r\nwebdisk\r\nns2\r\ncpanel\r\nwhm\r\nautodiscover\r\nautoconfig\r\nm\r\nimap\r\ntest\r\nns\r\nblog\r\npop3\r\ndev\r\nwww2\r\nadmin\r\nforum\r\nnews\r\nvpn\r\nns3\r\nmail2\r\nnew\r\nmysql\r\nold\r\nlists\r\nsupport\r\nmobile\r\nmx\r\nstatic\r\ndocs\r\nbeta\r\nshop\r\nsql\r\nsecure\r\ndemo\r\ncp\r\ncalendar\r\nwiki\r\nweb\r\nmedia\r\nemail\r\nimages\r\nimg\r\nwww1\r\nintranet\r\nportal\r\nvideo\r\nsip\r\ndns2\r\napi\r\ncdn\r\nstats\r\ndns1\r\nns4\r\nwww3\r\ndns\r\nsearch\r\nstaging\r\nserver\r\nmx1\r\nchat\r\nwap\r\nmy\r\nsvn\r\nmail1\r\nsites\r\nproxy\r\nads\r\nhost\r\ncrm\r\ncms\r\nbackup\r\nmx2\r\nlyncdiscover\r\ninfo\r\napps\r\ndownload\r\nremote\r\ndb\r\nforums\r\nstore\r\nrelay\r\nfiles\r\nnewsletter\r\napp\r\nlive\r\nowa\r\nen\r\nstart\r\nsms\r\noffice\r\nexchange\r\nipv4\r\nmail3\r\nhelp\r\nblogs\r\nhelpdesk\r\nweb1\r\nhome\r\nlibrary\r\nftp2\r\nntp\r\nmonitor\r\nlogin\r\nservice\r\ncorreo\r\nwww4\r\nmoodle\r\nit\r\ngateway\r\ngw\r\ni\r\nstat\r\nstage\r\nldap\r\ntv\r\nssl\r\nweb2\r\nns5\r\nupload\r\nnagios\r\nsmtp2\r\nonline\r\nad\r\nsurvey\r\ndata\r\nradio\r\nextranet\r\ntest2\r\nmssql\r\ndns3\r\njobs\r\nservices\r\npanel\r\nirc\r\nhosting\r\ncloud\r\nde\r\ngmail\r\ns\r\nbbs\r\ncs\r\nww\r\nmrtg\r\ngit\r\nimage\r\nmembers\r\npoczta\r\ns1\r\nmeet\r\npreview\r\nfr\r\ncloudflare-resolve-to\r\ndev2\r\nphoto\r\njabber\r\nlegacy\r\ngo\r\nes\r\nssh\r\nredmine\r\npartner\r\nvps\r\nserver1\r\nsv\r\nns6\r\nwebmail2\r\nav\r\ncommunity\r\ncacti\r\ntime\r\nsftp\r\nlib\r\nfacebook\r\nwww5\r\nsmtp1\r\nfeeds\r\nw\r\ngames\r\nts\r\nalumni\r\ndl\r\ns2\r\nphpmyadmin\r\narchive\r\ncn\r\ntools\r\nstream\r\nprojects\r\nelearning\r\nim\r\niphone\r\ncontrol\r\nvoip\r\ntest1\r\nws\r\nrss\r\nsp\r\nwwww\r\nvpn2\r\njira\r\nlist\r\nconnect\r\ngallery\r\nbilling\r\nmailer\r\nupdate\r\npda\r\ngame\r\nns0\r\ntesting\r\nsandbox\r\njob\r\nevents\r\ndialin\r\nml\r\nfb\r\nvideos\r\nmusic\r\na\r\npartners\r\nmailhost\r\ndownloads\r\nreports\r\nca\r\nrouter\r\nspeedtest\r\nlocal\r\ntraining\r\nedu\r\nbugs\r\nmanage\r\ns3\r\nstatus\r\nhost2\r\nww2\r\nmarketing\r\nconference\r\ncontent\r\nnetwork-ip\r\nbroadcast-ip\r\nenglish\r\ncatalog\r\nmsoid\r\nmailadmin\r\npay\r\naccess\r\nstreaming\r\nproject\r\nt\r\nsso\r\nalpha\r\nphotos\r\nstaff\r\ne\r\nauth\r\nv2\r\nweb5\r\nweb3\r\nmail4\r\ndevel\r\npost\r\nus\r\nimages2\r\nmaster\r\nrt\r\nftp1\r\nqa\r\nwp\r\ndns4\r\nwww6\r\nru\r\nstudent\r\nw3\r\ncitrix\r\ntrac\r\ndoc\r\nimg2\r\ncss\r\nmx3\r\nadm\r\nweb4\r\nhr\r\nmailserver\r\ntravel\r\nsharepoint\r\nsport\r\nmember\r\nbb\r\nagenda\r\nlink\r\nserver2\r\nvod\r\nuk\r\nfw\r\npromo\r\nvip\r\nnoc\r\ndesign\r\ntemp\r\ngate\r\nns7\r\nfile\r\nms\r\nmap\r\ncache\r\npainel\r\njs\r\nevent\r\nmailing\r\ndb1\r\nc\r\nauto\r\nimg1\r\nvpn1\r\nbusiness\r\nmirror\r\nshare\r\ncdn2\r\nsite\r\nmaps\r\ntickets\r\ntracker\r\ndomains\r\nclub\r\nimages1\r\nzimbra\r\ncvs\r\nb2b\r\noa\r\nintra\r\nzabbix\r\nns8\r\nassets\r\nmain\r\nspam\r\nlms\r\nsocial\r\nfaq\r\nfeedback\r\nloopback\r\ngroups\r\nm2\r\ncas\r\nloghost\r\nxml\r\nnl\r\nresearch\r\nart\r\nmunin\r\ndev1\r\ngis\r\nsales\r\nimages3\r\nreport\r\ngoogle\r\nidp\r\ncisco\r\ncareers\r\nseo\r\ndc\r\nlab\r\nd\r\nfirewall\r\nfs\r\neng\r\nann\r\nmail01\r\nmantis\r\nv\r\naffiliates\r\nwebconf\r\ntrack\r\nticket\r\npm\r\ndb2\r\nb\r\nclients\r\ntech\r\nerp\r\nmonitoring\r\ncdn1\r\nimages4\r\npayment\r\norigin\r\nclient\r\nfoto\r\ndomain\r\npt\r\npma\r\ndirectory\r\ncc\r\npublic\r\nfinance\r\nns11\r\ntest3\r\nwordpress\r\ncorp\r\nsslvpn\r\ncal\r\nmailman\r\nbook\r\nip\r\nzeus\r\nns10\r\nhermes\r\nstorage\r\nfree\r\nstatic1\r\npbx\r\nbanner\r\nmobil\r\nkb\r\nmail5\r\ndirect\r\nipfixe\r\nwifi\r\ndevelopment\r\nboard\r\nns01\r\nst\r\nreviews\r\nradius\r\npro\r\natlas\r\nlinks\r\nin\r\noldmail\r\nregister\r\ns4\r\nimages6\r\nstatic2\r\nid\r\nshopping\r\ndrupal\r\nanalytics\r\nm1\r\nimages5\r\nimages7\r\nimg3\r\nmx01\r\nwww7\r\nredirect\r\nsitebuilder\r\nsmtp3\r\nadserver\r\nnet\r\nuser\r\nforms\r\noutlook\r\npress\r\nvc\r\nhealth\r\nwork\r\nmb\r\nmm\r\nf\r\npgsql\r\njp\r\nsports\r\npreprod\r\ng\r\np\r\nmdm\r\nar\r\nlync\r\nmarket\r\ndbadmin\r\nbarracuda\r\naffiliate\r\nmars\r\nusers\r\nimages8\r\nbiblioteca\r\nmc\r\nns12\r\nmath\r\nntp1\r\nweb01\r\nsoftware\r\npr\r\njupiter\r\nlabs\r\nlinux\r\nsc\r\nlove\r\nfax\r\nphp\r\nlp\r\ntracking\r\nthumbs\r\nup\r\ntw\r\ncampus\r\nreg\r\ndigital\r\ndemo2\r\nda\r\ntr\r\notrs\r\nweb6\r\nns02\r\nmailgw\r\neducation\r\norder\r\npiwik\r\nbanners\r\nrs\r\nse\r\nvenus\r\ninternal\r\nwebservices\r\ncm\r\nwhois\r\nsync\r\nlb\r\nis\r\ncode\r\nclick\r\nw2\r\nbugzilla\r\nvirtual\r\norigin-www\r\ntop\r\ncustomer\r\npub\r\nhotel\r\nopenx\r\nlog\r\nuat\r\ncdn3\r\nimages0\r\ncgi\r\nposta\r\nreseller\r\nsoft\r\nmovie\r\nmba\r\nn\r\nr\r\ndeveloper\r\nnms\r\nns9\r\nwebcam\r\nconstrutor\r\nebook\r\nftp3\r\njoin\r\ndashboard\r\nbi\r\nwpad\r\nadmin2\r\nagent\r\nwm\r\nbooks\r\njoomla\r\nhotels\r\nezproxy\r\nds\r\nsa\r\nkatalog\r\nteam\r\nemkt\r\nantispam\r\nadv\r\nmercury\r\nflash\r\nmyadmin\r\nsklep\r\nnewsite\r\nlaw\r\npl\r\nntp2\r\nx\r\nsrv1\r\nmp3\r\narchives\r\nproxy2\r\nps\r\npic\r\nir\r\norion\r\nsrv\r\nmt\r\nocs\r\nserver3\r\nmeeting\r\nv1\r\ndelta\r\ntitan\r\nmanager\r\nsubscribe\r\ndevelop\r\nwsus\r\noascentral\r\nmobi\r\npeople\r\ngalleries\r\nwwwtest\r\nbackoffice\r\nsg\r\nrepo\r\nsoporte\r\nwww8\r\neu\r\nead\r\nstudents\r\nhq\r\nawstats\r\nec\r\nsecurity\r\nschool\r\ncorporate\r\npodcast\r\nvote\r\nconf\r\nmagento\r\nmx4\r\nwebservice\r\ntour\r\ns5\r\npower\r\ncorreio\r\nmon\r\nmobilemail\r\nweather\r\ninternational\r\nprod\r\naccount\r\nxx\r\npages\r\npgadmin\r\nbfn2\r\nwebserver\r\nwww-test\r\nmaintenance\r\nme\r\nmagazine\r\nsyslog\r\nint\r\nview\r\nenews\r\nci\r\nau\r\nmis\r\ndev3\r\npdf\r\nmailgate\r\nv3\r\nss\r\ninternet\r\nhost1\r\nsmtp01\r\njournal\r\nwireless\r\nopac\r\nw1\r\nsignup\r\ndatabase\r\ndemo1\r\nbr\r\nandroid\r\ncareer\r\nlistserv\r\nbt\r\nspb\r\ncam\r\ncontacts\r\nwebtest\r\nresources\r\n1\r\nlife\r\nmail6\r\ntransfer\r\napp1\r\nconfluence\r\ncontrolpanel\r\nsecure2\r\npuppet\r\nclassifieds\r\ntunet\r\nedge\r\nbiz\r\nhost3\r\nred\r\nnewmail\r\nmx02\r\nsb\r\nphysics\r\nap\r\nepaper\r\nsts\r\nproxy1\r\nww1\r\nstg\r\nsd\r\nscience\r\nstar\r\nwww9\r\nphoenix\r\npluto\r\nwebdav\r\nbooking\r\neshop\r\nedit\r\npanelstats\r\nxmpp\r\nfood\r\ncert\r\nadfs\r\nmail02\r\ncat\r\nedm\r\nvcenter\r\nmysql2\r\nsun\r\nphone\r\nsurveys\r\nsmart\r\nsystem\r\ntwitter\r\nupdates\r\nwebmail1\r\nlogs\r\nsitedefender\r\nas\r\ncbf1\r\nsugar\r\ncontact\r\nvm\r\nipad\r\ntraffic\r\ndm\r\nsaturn\r\nbo\r\nnetwork\r\nac\r\nns13\r\nwebdev\r\nlibguides\r\nasp\r\ntm\r\ncore\r\nmms\r\nabc\r\nscripts\r\nfm\r\nsm\r\ntest4\r\nnas\r\nnewsletters\r\nrsc\r\ncluster\r\nlearn\r\npanelstatsmail\r\nlb1\r\nusa\r\napollo\r\npre\r\nterminal\r\nl\r\ntc\r\nmovies\r\nsh\r\nfms\r\ndms\r\nz\r\nbase\r\njwc\r\ngs\r\nkvm\r\nbfn1\r\ncard\r\nweb02\r\nlg\r\neditor\r\nmetrics\r\nfeed\r\nrepository\r\nasterisk\r\nsns\r\nglobal\r\ncounter\r\nch\r\nsistemas\r\npc\r\nchina\r\nu\r\npayments\r\nma\r\npics\r\nwww10\r\ne-learning\r\nauction\r\nhub\r\nsf\r\ncbf8\r\nforum2\r\nns14\r\napp2\r\npassport\r\nhd\r\ntalk\r\nex\r\ndebian\r\nct\r\nrc\r\n2012\r\nimap4\r\nblog2\r\nce\r\nsk\r\nrelay2\r\ngreen\r\nprint\r\ngeo\r\nmultimedia\r\niptv\r\nbackup2\r\nwebapps\r\naudio\r\nro\r\nsmtp4\r\npg\r\nldap2\r\nbackend\r\nprofile\r\noldwww\r\ndrive\r\nbill\r\nlistas\r\norders\r\nwin\r\nmag\r\napply\r\nbounce\r\nmta\r\nhp\r\nsuporte\r\ndir\r\npa\r\nsys\r\nmx0\r\nems\r\nantivirus\r\nweb8\r\ninside\r\nplay\r\nnic\r\nwelcome\r\npremium\r\nexam\r\nsub\r\ncz\r\nomega\r\nboutique\r\npp\r\nmanagement\r\nplanet\r\nww3\r\norange\r\nc1\r\nzzb\r\nform\r\necommerce\r\ntmp\r\nplus\r\nopenvpn\r\nfw1\r\nhk\r\nowncloud\r\nhistory\r\nclientes\r\nsrv2\r\nimg4\r\nopen\r\nregistration\r\nmp\r\nblackboard\r\nfc\r\nstatic3\r\nserver4\r\ns6\r\necard\r\ndspace\r\ndns01\r\nmd\r\nmcp\r\nares\r\nspf\r\nkms\r\nintranet2\r\naccounts\r\nwebapp\r\nask\r\nrd\r\nwww-dev\r\ngw2\r\nmall\r\nbg\r\nteste\r\nldap1\r\nreal\r\nm3\r\nwave\r\nmovil\r\nportal2\r\nkids\r\ngw1\r\nra\r\ntienda\r\nprivate\r\npo\r\n2013\r\ncdn4\r\ngps\r\nkm\r\nent\r\ntt\r\nns21\r\nat\r\nathena\r\ncbf2\r\nwebmail3\r\nmob\r\nmatrix\r\nns15\r\nsend\r\nlb2\r\npos\r\n2\r\ncl\r\nrenew\r\nadmissions\r\nam\r\nbeta2\r\ngamma\r\nmx5\r\nportfolio\r\ncontest\r\nbox\r\nmg\r\nwwwold\r\nneptune\r\nmac\r\npms\r\ntraveler\r\nmedia2\r\nstudio\r\nsw\r\nimp\r\nbs\r\nalfa\r\ncbf4\r\nservicedesk\r\nwmail\r\nvideo2\r\nswitch\r\nsam\r\nsky\r\nee\r\nwidget\r\nreklama\r\nmsn\r\nparis\r\ntms\r\nth\r\nvega\r\ntrade\r\nintern\r\next\r\noldsite\r\nlearning\r\ngroup\r\nf1\r\nns22\r\nns20\r\ndemo3\r\nbm\r\ndom\r\npe\r\nannuaire\r\nportail\r\ngraphics\r\niris\r\none\r\nrobot\r\nams\r\ns7\r\nforo\r\ngaia\r\nvpn3";

            String r_line = Pattern.quote("\r\n");

            String[] cases2 = subs.split(r_line);
            List<String> cases = java.util.Collections.synchronizedList(new ArrayList<>());
            for(String s : cases2)
                cases.add(s);


            Runnable r = new Runnable() {
                @Override
                public void run() {
                    while(run) {
                        if(cases.size() == 0) {
                            run = false;
                        } else {
                            String item = cases.get(0);
                            cases.remove(0);
                            String subdomain = item + "." + domain;
                            try {
                                InetAddress iadr = InetAddress.getByName(subdomain);
                                if(!subdomains.contains(subdomain))
                                    subdomains.add(subdomain);
                            } catch(Exception e) {}
                        }
                    }
                }
            };

            for(int i = 0; i < 10; i++)
                new Thread(r).start();
            while(run);
        }

        Logger.liveupdate = false;

        // Clean up subdomains and remove invalid ones
        List<String> subdoms = new ArrayList<>();
        for(String s : subdomains)
            if(s.contains("." + domain) && !s.contains("*") && !s.contains(" ")) // the space part is remove some domains it shouldn't TODO fix
                subdoms.add(s);
        return subdoms;
    }

    // returns the content of a page fetched via an HTTP GET request
    public String httpGET(HttpClient client, String http) throws Exception {
        HttpGet request = new HttpGet(http);
        request.setHeader("User-Agent", USER_AGENT);
        HttpResponse response = client.execute(request);
        return EntityUtils.toString(response.getEntity());
   }

    // returns the content of a page fetched via an HTTP GET request
    public String httpGET(HttpClient client, String http, String cookie) throws Exception {
       HttpGet request = new HttpGet(http);
       request.setHeader("User-Agent", USER_AGENT);
       request.setHeader("Cookie", cookie);
       HttpResponse response = client.execute(request);
       return EntityUtils.toString(response.getEntity());
    }

    // returns the content of a page fetched via an HTTP POST request
    public String httpPOST(HttpClient client, String http, String cookie, List<NameValuePair> form) throws Exception {
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, Consts.UTF_8);
        HttpPost request = new HttpPost(http);
        request.setHeader("User-Agent", USER_AGENT);
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
       request.setHeader("User-Agent", USER_AGENT);
       HttpClientContext context = HttpClientContext.create();
       HttpResponse response = client.execute(request, context);
       return context.getCookieStore().getCookies();
  }
}
