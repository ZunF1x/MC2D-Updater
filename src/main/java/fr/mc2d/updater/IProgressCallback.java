package fr.mc2d.updater;

public interface IProgressCallback {

    default void init() {}

    default void step(Step step) {}

    default void update(DownloadList.DownloadInfo info) {}

    default void onFileDownloaded(String path) {}

    static IProgressCallback defaultCallback() {
        return new IProgressCallback() {

        };
    }
}
