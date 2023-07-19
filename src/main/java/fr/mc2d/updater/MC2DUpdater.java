package fr.mc2d.updater;

import fr.mc2d.updater.utils.HttpUtil;
import fr.mc2d.updater.utils.Util;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MC2DUpdater {

    private static final String MC2D_UPDATER_URL = "https://kyosu.fr/mc2d/updater/updater.php";

    private final Path gameDir;
    private final DownloadList downloadList;
    private final IProgressCallback progressCallback;

    public MC2DUpdater(Path gameDir, Version version, IProgressCallback progressCallback) {
        this.gameDir = gameDir;
        this.downloadList = new DownloadList(this.listFilesToDownloadFromVersion(version));
        this.progressCallback = progressCallback;

        this.downloadList.init();
    }

    public void update() {
        this.progressCallback.step(Step.START);
        this.processLibraries();
        this.progressCallback.step(Step.LIBRARIES);
        this.processNatives();
        this.progressCallback.step(Step.NATIVES);
        this.processJSON();
        this.progressCallback.step(Step.JSON);
        this.processClient();
        this.progressCallback.step(Step.CLIENT);
        this.processExternalFiles();
        this.progressCallback.step(Step.EXTERNAL);
        this.progressCallback.step(Step.END);
    }

    private void processLibraries() {
        List<Downloadable> downloadables = this.downloadList.get(FileType.LIBRARY);

        for (Downloadable downloadable : downloadables) {
            try {
                downloadable.download();

                this.downloadList.incrementDownloaded(downloadable.getSize());
                this.progressCallback.update(this.downloadList.getDownloadInfo());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processNatives() {
        List<Downloadable> downloadables = this.downloadList.get(FileType.NATIVE);

        for (Downloadable downloadable : downloadables) {
            try {
                downloadable.download();

                this.downloadList.incrementDownloaded(downloadable.getSize());
                this.progressCallback.update(this.downloadList.getDownloadInfo());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processJSON() {
        List<Downloadable> downloadables = this.downloadList.get(FileType.JSON);

        for (Downloadable downloadable : downloadables) {
            try {
                downloadable.download();

                this.downloadList.incrementDownloaded(downloadable.getSize());
                this.progressCallback.update(this.downloadList.getDownloadInfo());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processClient() {
        List<Downloadable> downloadables = this.downloadList.get(FileType.CLIENT);

        for (Downloadable downloadable : downloadables) {
            try {
                downloadable.download();

                this.downloadList.incrementDownloaded(downloadable.getSize());
                this.progressCallback.update(this.downloadList.getDownloadInfo());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void processExternalFiles() {
        List<Downloadable> downloadables = this.downloadList.get(FileType.OTHER);

        for (Downloadable downloadable : downloadables) {
            try {
                downloadable.download();

                this.downloadList.incrementDownloaded(downloadable.getSize());
                this.progressCallback.update(this.downloadList.getDownloadInfo());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Downloadable> listFilesToDownloadFromVersion(Version version) {
        List<Downloadable> paths = new ArrayList<>();

        String gamePath = this.gameDir.toString() + "/";
        String clientPath = this.gameDir.toString() + "/versions/" + version.getVersionName() + "/";

        String response = HttpUtil.sendPostRequest(MC2DUpdater.MC2D_UPDATER_URL, Util.makeArgGroup("version", version.getVersionName()));
        JSONObject jsonResponse = Util.parseObject(response);

        File versionJsonFile = new File(clientPath + version.getVersionName() + ".json");

        if (!versionJsonFile.exists() || (versionJsonFile.exists() && Util.getJSONBoolean(versionJsonFile, "download", true))) {
            JSONArray libraries = (JSONArray) jsonResponse.get("libraries");
            for (Object o : libraries) {
                JSONObject file = (JSONObject) o;

                String fileName = (String) file.get("name");
                long size = (Long) file.get("size");
                String libraryUrl = "https://kyosu.fr/mc2d/updater/versions/" + version.getVersionName() + "/" + jsonResponse.get("libraries_link") + fileName;
                String destination = gamePath + jsonResponse.get("libraries_link") + fileName;

                if (!Util.areFilesEquals(destination, libraryUrl))
                    paths.add(new Downloadable(libraryUrl, destination, size, FileType.LIBRARY));
            }

            JSONArray natives = (JSONArray) jsonResponse.get("natives");
            for (Object o : natives) {
                JSONObject file = (JSONObject) o;

                String fileName = (String) file.get("name");
                long size = (Long) file.get("size");
                String nativeUrl = "https://kyosu.fr/mc2d/updater/versions/" + version.getVersionName() + "/" + jsonResponse.get("natives_link") + fileName;
                String destination = gamePath + jsonResponse.get("natives_link") + fileName;

                if (!Util.areFilesEquals(destination, nativeUrl))
                    paths.add(new Downloadable(nativeUrl, destination, size, FileType.NATIVE));
            }

            JSONObject jsonFile = (JSONObject) jsonResponse.get("json");
            String jsonFileName = (String) jsonFile.get("name");
            long jsonFileSize = (Long) jsonFile.get("size");
            String jsonFileUrl = "https://kyosu.fr/mc2d/updater/versions/" + version.getVersionName() + "/" + jsonFileName;
            String jsonFileDestination = clientPath + jsonFileName;

            if (!Util.areFilesEquals(jsonFileDestination, jsonFileUrl))
                paths.add(new Downloadable(jsonFileUrl, jsonFileDestination, jsonFileSize, FileType.JSON));

            JSONObject clientFile = (JSONObject) jsonResponse.get("client");
            String clientFileName = (String) clientFile.get("name");
            long clientFileSize = (Long) clientFile.get("size");
            String clientFileUrl = "https://kyosu.fr/mc2d/updater/versions/" + version.getVersionName() + "/" + clientFileName;
            String clientFileDestination = clientPath + clientFileName;

            if (!Util.areFilesEquals(clientFileDestination, clientFileUrl))
                paths.add(new Downloadable(clientFileUrl, clientFileDestination, clientFileSize, FileType.CLIENT));
        }

        return paths;
    }

    public static class MC2DUpdaterBuilder {

        private final Path gameDir;
        private Version version = Version.getLatest();
        private IProgressCallback progressCallback = IProgressCallback.defaultCallback();

        public MC2DUpdaterBuilder(Path gameDir) {
            this.gameDir = gameDir;
        }

        public MC2DUpdaterBuilder withVersion(Version version) {
            this.version = version;

            return this;
        }

        public MC2DUpdaterBuilder withProgressCallback(IProgressCallback progressCallback) {
            this.progressCallback = progressCallback;

            return this;
        }

        public MC2DUpdater build() {
            return new MC2DUpdater(this.gameDir, this.version, this.progressCallback);
        }
    }
}
