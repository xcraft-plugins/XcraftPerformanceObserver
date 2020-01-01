/*
 * Decompiled with CFR 0.139.
 */
package de.xcraft.voronwe.XcraftCore.command;

import de.xcraft.voronwe.XcraftCore.XcraftException;

public class XcraftCommandUsageException
extends XcraftException {
    public XcraftCommandUsageException(String message) {
        super(message);
    }

    public XcraftCommandUsageException(Exception e) {
        super(e);
    }
}

