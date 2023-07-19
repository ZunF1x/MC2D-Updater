package fr.mc2d.updater;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DownloadList {

    private final List<Downloadable> downloadableFiles;
    private final Lock updateInfoLock;
    private DownloadInfo downloadInfo;
    private boolean init = false;

    public DownloadList(List<Downloadable> downloadableFiles) {
        this.downloadableFiles = downloadableFiles;
        this.updateInfoLock = new ReentrantLock();
    }

    public void init() {
        if (this.init) return;

        this.downloadInfo = new DownloadInfo();
        this.downloadableFiles.forEach(downloadable -> this.downloadInfo.totalBytesToDownload.set(this.downloadInfo.totalBytesToDownload.get() + downloadable.getSize()));

        this.downloadInfo.totalFilesToDownload.set(this.downloadInfo.totalFilesToDownload.get() + this.downloadableFiles.size());

        this.init = true;
    }

    public void incrementDownloaded(long bytes) {
        this.updateInfoLock.lock();
        this.downloadInfo.totalDownloadedFiles.incrementAndGet();
        this.downloadInfo.totalDownloadedBytes.set(this.downloadInfo.totalDownloadedBytes.get() + bytes);
        this.updateInfoLock.unlock();
    }

    public DownloadInfo getDownloadInfo() {
        return this.downloadInfo;
    }

    public List<Downloadable> getDownloadableFiles() {
        return this.downloadableFiles;
    }

    public List<Downloadable> get(FileType fileType) {
        List<Downloadable> downloadables = new ArrayList<>();

        for (Downloadable downloadable : this.downloadableFiles) {
            if (downloadable.getFileType() == fileType) {
                downloadables.add(downloadable);
            }
        }

        return downloadables;
    }

    public void clear() {
        this.downloadableFiles.clear();
        this.downloadInfo.reset();
        this.init = false;
    }

    public static class DownloadInfo {
        private final AtomicLong totalBytesToDownload = new AtomicLong(0);
        private final AtomicLong totalDownloadedBytes = new AtomicLong(0);
        private final AtomicInteger totalFilesToDownload = new AtomicInteger(0);
        private final AtomicInteger totalDownloadedFiles = new AtomicInteger(0);

        public void reset() {
            this.totalBytesToDownload.set(0);
            this.totalDownloadedBytes.set(0);
            this.totalFilesToDownload.set(0);
            this.totalDownloadedFiles.set(0);
        }

        public long getTotalBytesToDownload() {
            return this.totalBytesToDownload.get();
        }

        public long getDownloadedBytes() {
            return this.totalDownloadedBytes.get();
        }

        public int getTotalFilesToDownload() {
            return this.totalFilesToDownload.get();
        }

        public int getDownloadedFiles() {
            return this.totalDownloadedFiles.get();
        }
    }
}