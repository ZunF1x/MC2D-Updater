package fr.mc2d.updater;

public enum Version {

    B1_0("b1.0");

    private final String versionName;

    Version(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionName() {
        return versionName;
    }

    public static Version getLatest() {
        return Version.B1_0;
    }
}
