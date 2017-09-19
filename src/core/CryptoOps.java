package core;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.*;
import java.util.regex.*;
import java.math.*;
import org.json.*;
import org.apache.commons.io.*;
import java.nio.file.*;
import java.nio.charset.*;
import java.security.*;
import java.text.*;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.binary.Base64;
import java.util.stream.Collectors;
/*import org.apache.http.client.*;
import org.apache.http.client.entity.*;
import org.apache.http.util.*;
import org.apache.http.impl.client.*;
import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.message.*;
import org.apache.http.entity.*;*/
import org.apache.commons.lang3.*;

public final class CryptoOps {
    public static String[] HASH_ALGORITHMS = {"MD2", "MD5", "SHA-1", "SHA-224", "SHA-256", "SHA-384", "SHA-512"};

    private static final String PLAIN = "abcdefghijklmnopqrstuvwxyz";
    private static final String CIPHER = "zyxwvutsrqponmlkjihgfedcba";

    public static boolean isPrime(BigInteger number) {
        if (!number.isProbablePrime(5))
            return false;

        BigInteger two = new BigInteger("2");
        if (!two.equals(number) && BigInteger.ZERO.equals(number.mod(two)))
            return false;

        for (BigInteger i = new BigInteger("3"); i.multiply(i).compareTo(number) < 1; i = i.add(two)) {
            if (BigInteger.ZERO.equals(number.mod(i)))
                return false;
        }
        return true;
    }

    public static String atbash(String input) {
        input = input.toLowerCase();
        String cyphered = "";

        for (char c : input.toCharArray()) {
            if(PLAIN.contains(c + "")) {
                int idx = PLAIN.indexOf(c);
                cyphered += CIPHER.toCharArray()[idx];
            } else
                cyphered += c;
        }

        return cyphered;
    }

    public static int phi(int i) {
        int n = i;
        int result = n;

        for (int p=2; p*p<=n; ++p) {
            if (n % p == 0) {
                while (n % p == 0)
                    n /= p;
                result -= result / p;
            }
        }

        if (n > 1)
            result -= result / n;

        return result;
    }

    public static boolean isPrime(long n) {
        if (n % 2L == 0L)
            return false;
        for(long i = 3L; (long)(i * i) <= n; i += 2L)
            if(n % i == 0L)
                return false;
        return true;
    }

    public static String hex_decode(String s) {
        try {
            return new String(Hex.decodeHex(s.replace("-", "").replace(" ", "").toCharArray()));
        } catch(Exception e) {
            return "Error: Failed to decode hex";
        }
    }

    public static String hex_encode(String s) {
        try {
            return new String(Hex.encodeHex(s.getBytes()));
        } catch(Exception e) {
            return "Error: Failed to encode hex";
        }
    }

    /* rot encodes s to e*/
    public static String rot(String s, int e) {
        String[] oldletters = s.split("");
        String[] letters = "abcdefghijklmnopqrstuvwxyz".split("");
        List<String> letters2 = Arrays.asList(letters);
        StringBuilder b = new StringBuilder();
        e = e % 26;

        for(String old : oldletters) {
            if(letters2.contains(old.toLowerCase())) {
                int index = letters2.indexOf(old.toLowerCase());
                String newer = letters[(index + e) % letters.length];
                b.append((Character.isUpperCase(old.charAt(0)) ? newer.toUpperCase() : newer.toLowerCase()));
            } else
                b.append(old);
        }

        return b.toString();
    }

    /* returns the type of file from the filetype */
    public static String getType(String s) {
        System.out.println(s);
        String ending = s;//String ending = getType(s.substring(s.lastIndexOf('.') + 1).trim());

        switch(ending.toLowerCase()) {
            case "wav":
                return "audio";
            case "mp3":
                return "audio";
            case "jpg":
                return "image";
            case "png":
                return "image";
            case "mov":
                return "movie";
            case "mp4":
                return "movie";
            default:
                return "";
        }
    }

    /* reverses a string (duh) */
    public static String reverse(String s) {
        return new StringBuilder(s).reverse().toString();
    }

    public static String base64_decode(String s) {
        return StringUtils.newStringUtf8(Base64.decodeBase64(s));
    }

    public static String base64_encode(String s) {
        return Base64.encodeBase64String(StringUtils.getBytesUtf8(s));
    }

    public static String hash(String s, String algorithm) {
        try {
            MessageDigest m = MessageDigest.getInstance(algorithm.toUpperCase());
            m.reset();
            m.update(s.getBytes());
            return new BigInteger(1, m.digest()).toString(16).toLowerCase();
        } catch(Exception e) {
            return "Error: Algorithm not found";
        }
    }

    public static String morse_encode(String str) {
        str = str.toLowerCase();

        StringBuilder builder = new StringBuilder();
        for(String s : str.split("")) {
            if(map.containsKey(s))
                builder.append(map.get(s)).append(" ");
            else
                builder.append("  ");
        }

        return builder.toString();
    }

    private static Map <String, String> map = new HashMap<>();
    private static boolean inited = false;

    public static void init() {
        if(!inited) {
            String a = Pattern.quote(" = ");
            try {
                List<String> lines = Files.readAllLines(Paths.get("morse.txt"), Charset.defaultCharset());
                for(String s : lines) {
                    String[] split = s.split(a);
                    map.put(split[0], split[1]);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }

            inited = true;
        }
    }

    public static String morse_decode(String str) {
        str = str.toLowerCase().replace("    ", "  ");;

        StringBuilder builder = new StringBuilder();
        for(String s : str.split(" ")) {
            if(map.containsValue(s))
                builder.append(getKeysByValue(map, s).iterator().next());
            else
                builder.append(" ");
        }

        return builder.toString();
    }

    private static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
        return map.entrySet()
                  .stream()
                  .filter(entry -> Objects.equals(entry.getValue(), value))
                  .map(Map.Entry::getKey)
                  .collect(Collectors.toSet());
    }
}
