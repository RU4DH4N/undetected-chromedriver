package utils;

public final class OSInfo {
    private final OS os;
    private final String path;
    private final String legacyFile;
    private final String[] command; // mutable but doesn't matter at all

    public OSInfo(OS os, String path, String legacyFile, String[] command) {
        this.os = os;
        this.path = path;
        this.legacyFile = legacyFile;
        this.command = command;
    }

    public OS os() {
        return os;
    }

    public String path() {
        return path;
    }

    public String legacyFile() {
        return legacyFile;
    }

    public String[] command() {
        return command;
    }

    ; // use .clone() maybe?
}