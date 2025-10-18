package com.prz.edu.pl.ogloszone.category;

import java.util.Arrays;

public enum Category {
    JOB("JOB"),
    HOUSING("HOUSING"),
    SALE("SALE"),
    SERVICES("SERVICES"),
    TRANSPORT("TRANSPORT"),
    EDUCATION("EDUCATION"),
    EVENTS("EVENTS"),
    PETS("PETS"),
    HEALTH("HEALTH"),
    OTHER("OTHER");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static String[] getNames() {
        return Arrays.stream(values())
                .map(Category::getDisplayName)
                .toArray(String[]::new);
    }
}