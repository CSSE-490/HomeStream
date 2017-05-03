package networking;

import networking.protocol.FoundFile;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static main.Settings.SETTINGS;

public class SearchDirectoryHelper {

    public static List<FoundFile> getFiles(String regex) {
        List<FoundFile> files = new ArrayList<>();

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher("");

        for (File f : SETTINGS.searchableDirectories) {
            File[] array = f.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    matcher.reset(name);
                    return matcher.find();
                }
            });

            for (File add : array) {
                files.add(new FoundFile(add.getName(), checksum(add), add.length()));
            }
        }

        return files;
    }

    private static byte[] checksum(File f) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new byte[0];
        }
        try (InputStream is = Files.newInputStream(f.toPath());
             DigestInputStream dis = new DigestInputStream(is, md)) {
            return md.digest();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }

    }
}