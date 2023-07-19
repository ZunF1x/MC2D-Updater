package fr.mc2d.updater;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Downloadable {

    private final String link;
    private final String destination;
    private final long size;
    private final FileType fileType;

    public Downloadable(String link, String destination, long size, FileType fileType) {
        this.link = link;
        this.destination = destination;
        this.size = size;
        this.fileType = fileType;
    }

    public void download() throws IOException {
        this.mkDirs(this.destination);

        URL url = new URL(this.link);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (InputStream inputStream = connection.getInputStream(); FileOutputStream outputStream = new FileOutputStream(this.destination)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                System.out.println("Downloaded : " + this.link);
            }
        } else {
            System.out.println("HTTP Error : " + responseCode);
        }

        connection.disconnect();
    }

    public long getSize() {
        return this.size;
    }

    public FileType getFileType() {
        return fileType;
    }

    public String getLink() {
        return link;
    }

    public String getDestination() {
        return destination;
    }

    private void mkDirs(String destination) {
        String[] paths = destination.split("/");
        StringBuilder path = new StringBuilder();

        for (int i = 0; i < paths.length - 1; i++) {
            path.append(paths[i]).append("/");
        }

        File dirs = new File(path.toString());

        if (!dirs.exists()) {
            if (!dirs.mkdirs()) {
                System.out.println("Unable to make folders");
            }
        }
    }
}
