package fr.mc2d.updater.utils;

import sun.misc.IOUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpUtil {

    public static String sendPostRequest(String link, String... args) {
        String response;

        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);
            StringBuilder postData = new StringBuilder();
            for (String arg : args) {
                postData.append(arg).append("&");
            }
            byte[] postDataBytes = postData.toString().getBytes(StandardCharsets.UTF_8);
            OutputStream outputStream = connection.getOutputStream();
            Throwable throwable = null;

            try {
                outputStream.write(postDataBytes);
                outputStream.flush();
            } catch (Throwable t) {
                throwable = t;
                throw t;
            } finally {
                if (outputStream != null) {
                    if (throwable != null) {
                        try {
                            outputStream.close();
                        } catch (Throwable t) {
                            throwable.addSuppressed(t);
                        }
                    } else {
                        outputStream.close();
                    }
                }
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder resp = new StringBuilder();

                String line;

                while ((line = reader.readLine()) != null) {
                    resp.append(line);
                }

                reader.close();

                response = resp.toString();
            } else {
                response = "{" + '"' + "error" + '"' + ":" + '"' + "HTTP Error : " + responseCode + '"' + "}";
            }
        } catch (Exception e) {
            response = "{" + '"' + "error" + '"' + ":" + '"' + e.getMessage() + '"' + "}";
        }

        return response;
    }

    public static String sendGetRequest(String link, String... args) {
        String response;

        try {
            StringBuilder getData = new StringBuilder();
            getData.append("?");
            for (String arg : args) {
                getData.append(arg).append("&");
            }

            URL url = new URL(link + getData);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder resp = new StringBuilder();

                String line;

                while ((line = reader.readLine()) != null) {
                    resp.append(line);
                }

                reader.close();

                response = resp.toString();
            } else {
                response = "{" + '"' + "error" + '"' + ":" + '"' + "HTTP Error : " + responseCode + '"' + "}";
            }
        } catch (Exception e) {
            response = "{" + '"' + "error" + '"' + ":" + '"' + e.getMessage() + '"' + "}";
        }

        return response;
    }

    public Set<String> listFilesUsingJavaIO(String dir) {
        return Stream.of(Objects.requireNonNull(new File(dir).listFiles()))
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toSet());
    }
}
