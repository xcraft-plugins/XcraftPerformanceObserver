/*
 * Decompiled with CFR 0.139.
 */
package de.xcraft.voronwe.XcraftCore.event;

import java.lang.reflect.Method;

public class EventEntry {
    private final XcraftEvent event;
    private final Method method;
    private final Byte settings;

    public EventEntry(XcraftEvent event, Method method, Byte settings) {
        this.event = event;
        this.method = method;
        this.settings = settings;
    }

    public Byte getSettings() {
        return this.settings;
    }

    public XcraftEvent getEvent() {
        return this.event;
    }

    public Method getMethod() {
        return this.method;
    }
}

