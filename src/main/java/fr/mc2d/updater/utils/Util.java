package fr.mc2d.updater.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Objects;

public class Util {

    public static String makeArgGroup(String arg, String val) {
        return arg + "=" + val;
    }

    public static boolean isEmptyString(String string) {
        return string == null || string.length() == 0;
    }

    public static JSONObject parseObject(String jsonString) {
        JSONParser parser = new JSONParser();

        try {
            return (JSONObject) parser.parse(jsonString);
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    public static JSONArray parseArray(String jsonString) {
        JSONParser parser = new JSONParser();

        try {
            return (JSONArray) parser.parse(jsonString);
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    public static void sleep(long time) {
        try {
            Thread.sleep(1000L);
        } catch (Exception ignored) {}
    }

    public static String getMD5Checksum(String filePath) {
        try (InputStream inputStream = Files.newInputStream(Paths.get(filePath))) {
            MessageDigest md5Digest = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                md5Digest.update(buffer, 0, bytesRead);
            }

            byte[] md5Bytes = md5Digest.digest();
            StringBuilder md5Checksum = new StringBuilder();
            for (byte md5Byte : md5Bytes) {
                md5Checksum.append(Integer.toString((md5Byte & 0xff) + 0x100, 16).substring(1));
            }

            return md5Checksum.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getWebFileMD5Checksum(String webFileUrl) {
        try {
            URL url = new URL(webFileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            try (InputStream inputStream = connection.getInputStream()) {
                MessageDigest md5Digest = MessageDigest.getInstance("MD5");
                byte[] buffer = new byte[1024];
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    md5Digest.update(buffer, 0, bytesRead);
                }

                byte[] md5Bytes = md5Digest.digest();
                StringBuilder md5Checksum = new StringBuilder();
                for (byte md5Byte : md5Bytes) {
                    md5Checksum.append(Integer.toString((md5Byte & 0xff) + 0x100, 16).substring(1));
                }

                return md5Checksum.toString();
            } catch (Exception e) {
                return null;
            } finally {
                connection.disconnect();
            }
        } catch (IOException ignored) {
            return null;
        }
    }

    public static boolean areFilesEquals(String localFilePath, String webURL) {
        if (new File(localFilePath).exists()) {
            return Objects.equals(Util.getMD5Checksum(localFilePath), Util.getWebFileMD5Checksum(webURL));
        } else {
            return false;
        }
    }

    public static boolean getJSONBoolean(File jsonFile, String key, boolean defaultValue) {
        StringBuilder fileContent = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(jsonFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line).append(System.lineSeparator());
            }

            JSONObject object = (JSONObject) new JSONParser().parse(fileContent.toString());

            return (Boolean) object.get(key);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

        return defaultValue;
    }
}
