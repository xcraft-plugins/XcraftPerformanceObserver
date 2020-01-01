/*
 * Decompiled with CFR 0.139.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.Listener
 */
package de.xcraft.voronwe.XcraftCore.event;

import de.xcraft.voronwe.XcraftCore.XcraftPlugin;
import org.bukkit.event.Listener;

public abstract class OldXcraftEventListener
implements Listener {
    private XcraftPlugin plugin;

    public OldXcraftEventListener(XcraftEventManager manager) {
        this.plugin = manager.getPlugin();
    }

    protected XcraftPlugin getPlugin() {
        return this.plugin;
    }
}

