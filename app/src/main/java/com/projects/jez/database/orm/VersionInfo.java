package com.projects.jez.database.orm;

public class VersionInfo {
    String[] commands;
    Class<?>[] classes;

    public VersionInfo(String[] commands) {
        this.commands = commands;
        this.classes = new Class<?>[]{};
    }

    public VersionInfo(Class<?>[] classes) {
        this.classes = classes;
        this.commands = new String[]{};
    }

    public VersionInfo(String[] commands, Class<?>[] classes) {
        this.commands = commands;
        this.classes = classes;
    }
}
