package com.auca.interfaces;

public interface Identifiable {
    static final String ID_PREFIX = "AUCA";
    static final int MIN_ID_LENGTH = 5;
    static final int MAX_ID_LENGTH = 30;
    static final String ID_SEPARATOR = "-";
    static final String DEFAULT_STATUS = "ACTIVE";
    static final int ENTITY_VERSION = 1;

    String getId();
    String getDisplayName();
    boolean isActive();
}
