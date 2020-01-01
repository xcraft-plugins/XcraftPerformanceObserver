/*
 * Decompiled with CFR 0.139.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.PluginDescriptionFile
 *  org.bukkit.plugin.java.JavaPlugin
 */
package de.xcraft.voronwe.XcraftCore;

import de.xcraft.voronwe.XcraftCore.event.XcraftEventManager;
import de.xcraft.voronwe.XcraftPerformanceObserver.manager.ConfigManager;
import de.xcraft.voronwe.XcraftPerformanceObserver.manager.EventManager;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class XcraftPlugin
extends JavaPlugin {
    private static final Logger log = Logger.getLogger("Minecraft");
    private final ConfigManager configManager = new ConfigManager(this);
    private long startTime;
    private XcraftEventManager eventManager;

    public void onEnable() {
        this.startTime = System.currentTimeMillis();
        this.startup();
        this.eventManager.registerEvents();
        this.log("enabled.");
    }

    protected abstract void startup();

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public XcraftEventManager getEventManger() {
        return this.eventManager;
    }

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public long getOnlineTime() {
        return System.currentTimeMillis() - this.startTime;
    }

    public String getNameBrackets() {
        return "[" + this.getDescription().getName() + "] ";
    }

    public void log(Object text) {
        log.info(this.getNameBrackets() + text.toString());
    }
}

