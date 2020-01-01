/*
 * Decompiled with CFR 0.139.
 * 
 * Could not load the following classes:
 *  org.bukkit.Server
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitScheduler
 */
package de.xcraft.voronwe.XcraftPerformanceObserver;

import de.xcraft.voronwe.XcraftCore.XcraftPlugin;
import de.xcraft.voronwe.XcraftPerformanceObserver.manager.CommandManager;
import de.xcraft.voronwe.XcraftPerformanceObserver.manager.EventManager;
import de.xcraft.voronwe.XcraftPerformanceObserver.task.PerformanceLogger;
import de.xcraft.voronwe.XcraftPerformanceObserver.task.StartupMessage;
import org.bukkit.plugin.Plugin;

public class XcraftPerformanceObserver
extends XcraftPlugin {
    private final CommandManager commandManager = new CommandManager(this);
    private PerformanceLogger serverLogger;

    @Override
    public void startup() {
        this.serverLogger = new PerformanceLogger(this, 200);
        this.serverLogger.start();
        this.setEventManager(new EventManager(this));
        this.commandManager.registerCommands();
        new StartupMessage(this);
    }

    public void onDisable() {
        this.serverLogger.stop();
        try {
            this.commandManager.unregisterCommands();
            this.getServer().getScheduler().cancelTasks((Plugin)this);
        }
        catch (IllegalStateException e) {
            e.printStackTrace();
        }
        this.log("disabled.");
    }

    public PerformanceLogger getServerLogger() {
        return this.serverLogger;
    }
}

