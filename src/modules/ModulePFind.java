package modules;

import core.*;

import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.regex.Pattern;
import org.json.JSONObject;


public class ModulePFind extends Module {
    public String[] urllist = { "https://twitter.com/$user$", "https://scratch.mit.edu/users/$user$/",
            "https://github.com/$user$", "https://gitter.im/$user$", "https://www.youtube.com/user/$user$",
            "https://dev.bukkit.org/profiles/$user$/", "https://instagram.com/$user$/",
            "https://plus.google.com/+$user$/", "https://www.reddit.com/user/$user$",
            "http://www.planetminecraft.com/member/$user$/", "http://www.minecraftforum.net/members/$user$",
            "https://m.facebook.com/$user$", "https://www.pinterest.com/$user$/", "https://vine.co/$user$",
            "https://myspace.com/$user$", "http://del.icio.us/$user$", "https://namemc.com/s/$user$",
            "https://news.ycombinator.com/user?id=$user$", "http://www.folkd.com/user/$user$",
            "https://www.stumbleupon.com/api/v2_0/user/$user$?version=2", // "http://www.stumbleupon.com/stumbler/$user$",
            "https://www.flickr.com/photos/$user$", "http://pastebin.com/u/$user$",
            "https://www.etsy.com/people/$user$", "http://us.battle.net/wow/en/character/silver-hand/$user$/simple",
            "http://imgur.com/user/$user$","https://keybase.io/$user$","https://www.codecademy.com/$user$",
            "http://www.crunchyroll.com/user/$user$", "https://plus.google.com/s/$user$/people" };

    public ModulePFind() {
        super();
        register("pfind", "personfind", "username", "find", "psearch", "pf", "userfind", "uf", "profilesearch", "profilefind");
    }

    @Override
    public void run(RunConfiguration config) {
        if(config != null && config.getArray().length != 1) {
            try {
                List<String> list = new ArrayList<>();
                String urlencoded = URLEncoder.encode(config.getArray()[1], "UTF-8");

                Logger.liveupdate = true;
                Logger.livestring = "Searching for online profiles...";

                for(String url : urllist) {
                    url = url.replaceAll(Pattern.quote("$user$"), urlencoded);
                    Object yu = exists(url);
                    if (yu instanceof Boolean && (Boolean) yu)
                        list.add(url);
                    else if (yu instanceof String)
                        list.add(yu.toString());
                }

                Logger.liveupdate = false;

                report(Status.CLEARLINE, null);
                report(Status.INFO, "Profiles found [" + list.size() + "]:");
                for(String profile : list)
                    report(Status.RAW, profile);
            } catch(Exception e) {
                report(Status.ERROR, e);
            }
        } else
            help();
    }

    @Override
    public void help() {
        report(Status.HELP, "A tool for finding someone's online profiles");
        report(Status.HELP, "syntax: pfind <username>");
    }

    public Object exists(String url) throws Exception {
        int responseCode = 404;

        try {
            HttpURLConnection.setFollowRedirects(false);
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            responseCode = con.getResponseCode();

            String linx = "";
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"))) {
                for (String line; (line = reader.readLine()) != null;) {
                    linx += line;
                }
            }

            if (linx.toLowerCase().contains("0 results") || linx.toLowerCase().contains("no results for")
                    || linx.toLowerCase().contains("\"totalresults\": 0")
                    || linx.toLowerCase().contains("no such user.")
                    || linx.toLowerCase().contains("we cannot find this user")
                    || linx.toLowerCase().contains("we seem to have misplaced this page"))
                return false;
            else if (!linx.toLowerCase().contains("\"totalresults\": 0"))
                return "https://www.youtube.com/channel/"
                        + new JSONObject(linx).getJSONArray("items").getJSONObject(0).getString("id");

            linx = "";
        } catch (Exception e) {}

        if (responseCode != 200)
            return false;
        return true;
    }
}
