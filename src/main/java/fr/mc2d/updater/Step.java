package fr.mc2d.updater;

public enum Step {

    START("start"),
    LIBRARIES("libraries"),
    NATIVES("natives"),
    JSON("json"),
    CLIENT("client"),
    END("end"),
    EXTERNAL("external");

    private final String name;

    Step(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
