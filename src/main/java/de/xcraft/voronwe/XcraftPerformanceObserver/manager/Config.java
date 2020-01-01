/*
 * Decompiled with CFR 0.139.
 */
package de.xcraft.voronwe.XcraftPerformanceObserver.manager;

public enum Config {
    ;
    private int defaultInt;
    private boolean defaultBoolean;
    private String path;
    private String defaultString;

    private Config(String path, String defaultValue) {
        this.path = path;
        this.defaultString = defaultValue;
    }

    private Config(String path, int defaultValue) {
        this.path = path;
        this.defaultInt = defaultValue;
    }

    private Config(String path, boolean defaultValue) {
        this.path = path;
        this.defaultBoolean = defaultValue;
    }

    public String getPath() {
        return this.path;
    }

    public String getDefaultString() {
        return this.defaultString;
    }

    public int getDefaultInt() {
        return this.defaultInt;
    }

    public boolean getDefaultBoolean() {
        return this.defaultBoolean;
    }
}

