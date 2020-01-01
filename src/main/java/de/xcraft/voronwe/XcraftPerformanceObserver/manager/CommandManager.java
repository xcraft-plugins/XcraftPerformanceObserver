/*
 * Decompiled with CFR 0.139.
 */
package de.xcraft.voronwe.XcraftPerformanceObserver.manager;

import de.xcraft.voronwe.XcraftCore.XcraftPlugin;
import de.xcraft.voronwe.XcraftCore.command.XcraftCommandManager;
import de.xcraft.voronwe.XcraftPerformanceObserver.command.PerformanceCommand;

public class CommandManager
extends XcraftCommandManager {
    public CommandManager(XcraftPlugin plugin) {
        super(plugin);
    }

    @Override
    public void registerCommands() {
        this.registerCommand(new PerformanceCommand(this.getPlugin()));
    }
}

