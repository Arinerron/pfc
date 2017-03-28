package modules;

import core.*;

import java.util.*;
import java.util.regex.*;
import java.io.*;

public class ModuleFile extends Module {
    public ModuleFile() {
        super();
        register("file", "filetype", "ft", "f", "detect", "type", "binaryid", "fileid");
    }

    @Override
    public void run(RunConfiguration config) {
        if(config != null && config.getArray().length != 1) {
            for(int i = 1; i < config.getArray().length; i++) {
                File file = new File(config.getArray()[i]);
                if(file.exists() && file.isFile()) {
                    System.out.println(generateReport(file));
                } else {
                    report(Status.ERROR, "File \"" + file.getAbsolutePath() + "\" does not exist.");
                }
            }
        } else {
            help();
        }
    }

    /* Generates a report for a file */
    public String generateReport(File file) {
        StringBuilder builder = new StringBuilder();

        builder.append("Report for [" + file.getName() + "]:\n");
        String[] data = getDataByBytes(readBytes(file));
        builder.append("Bytes: " + data[0] + " & ext: " + data[1]);

        return builder.toString();
    }

    /* Converts an array of bytes to hex*/
    private String bytesToHex(byte[] bytes) {
        final char[] hexArray = "0123456789ABCDEF".toCharArray();

        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /* Reads first 2048 bytes of a file */
    private byte[] readBytes(File file) {
        byte[] buffer = new byte[256];

        try {
            InputStream is = new FileInputStream(file.getAbsolutePath());
            if (is.read(buffer) != buffer.length);
            is.close();
        } catch(Exception e) {
            Logger.report(Status.ERROR, "Failed to read bytes from \"" + file.getAbsolutePath() + "\".");
            Logger.report(Status.ERROR, e);
        }

        return buffer;
    }

    /* Returns data by the given bytes */
    public String[] getDataByBytes(byte[] bytez) {
        String hex = bytesToHex(bytez).replaceAll("(.{2})(?!$)", "$1 ").toUpperCase();

        String past = "";
        String extension = "";
        String description = "";
        for(String split1 : formats) {
            String[] split = split1.split(Pattern.quote("|"));
            String ext = split[0];
            String desc = split[1];
            String bytes = split[2];

            if(hex.contains(bytes.toUpperCase()) && bytes.length() > past.length()) {
                past = bytes.toUpperCase();
                description = desc;
                extension = ext;
            }
        }

        String[] data = new String[2];
        if(past.length() != 0) {
            data[0] = description;
            data[1] = extension;
        } else {
            data[0] = "Text file";
            data[1] = "txt";
        }

        return data;
    }

    @Override
    public void help() {
        report(Status.HELP, "Generates a report about a file");
        report(Status.HELP, "syntax: file [filepath]");
    }

    public static final String[] formats = "class|Java class file|CA FE BA BE\r\nclass|Java class file|EF BB BF\r\nclass|Java class file|FE ED FA CE\r\nclass|Java class file|FE ED FA CF\r\nclass|Java class file|CE FA ED FE\r\nclass|Java class file|CF FA ED FE\r\nclass|Java class file|FF FE\r\nclass|Java class file|FF FE 00 00\r\npdf|PDF document|25 50 44 46\r\nwma|Advanced Systems Format|30 26 B2 75 8E 66 CF 11A6 D9 00 AA 00 62 CE 6C\r\npsd|Photoshop Document file|38 42 50 53\r\nwav|Waveform Audio file|52 49 46 46 nn nn nn nn 57 41 56 45\r\navi|Audio Video Interleave file|52 49 46 46 nn nn nn nn 41 56 49 20\r\nmp3|MPEG-1 Layer 3|FF FB\r\nbmp|Bitmap file|49 44 33\r\niso|ISO Image file|43 44 30 30 31\r\npng|Portable Network Graphics file|89 50 4E 47 0D 0A 1A 0A\r\nmidi|MIDI sound file|4D 54 68 64\r\nvmdk|VMDK file|4B 44 4D\r\ndat|Windows data file|50 4D 4F 43 43 4D 4F 43\r\ntar|TAR archive file|75 73 74 61 72 00 30 30\r\ntar|TAR archive file|75 73 74 61 72 20 20 00\r\n7z|7-Zip file|37 7A BC AF 27 1C\r\ntar.gz|GZIP file|1F 8B\r\nswf|SWF flash file|43 57 5346 57 53\r\ndeb|Linux DEB file|21 3C 61 72 63 68 3E\r\nogg|OGG file|4F 67 67 53\r\nrar|RAR archive file|52 61 72 21 1A 07 01 00\r\nzip|Compressed format files|50 4B 03 04\r\nzip|Compressed format files|50 4B 05 06\r\nzip|Compressed format files|50 4B 07 08\r\nexe|Windows Executable file|4D 5A\r\ntxt|Text file|46 4F 52 4D nn nn nn nn 46 54 58 54\r\njpg|JPEG file|FF D8 FF DB\r\njpg|JPEG file|FF D8 FF E0 nn nn 4A 46 49 46 00 01\r\njpg|JPEG file|FF D8 FF E1 nn nn 45 78 69 66 00 00\r\ngif|Animated image file|47 49 46 38 37 61\r\ngif|Animated image file|47 49 46 38 39 61\r\nico|Computer icon file|00 00 01 00\r\nrpm|RedHat package file|ed ab ee db".split(Pattern.quote("\r\n"));
}
