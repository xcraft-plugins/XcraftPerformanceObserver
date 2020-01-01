/*
 * Decompiled with CFR 0.139.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.file.FileConfiguration
 */
package de.xcraft.voronwe.XcraftPerformanceObserver.manager;

import de.xcraft.voronwe.XcraftCore.XcraftPlugin;
import de.xcraft.voronwe.XcraftCore.command.XcraftCommand;
import de.xcraft.voronwe.XcraftCore.event.XcraftEvent;
import de.xcraft.voronwe.XcraftCore.event.XcraftEventHandler;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private XcraftPlugin plugin;

    public ConfigManager(XcraftPlugin plugin) {
        this.plugin = plugin;
    }

    public String getString(Config entry) {
        if (!this.getConfig().isString(entry.getPath())) {
            this.getConfig().set(entry.getPath(), (Object)entry.getDefaultString());
            this.getPlugin().saveConfig();
        }
        return this.getConfig().getString(entry.getPath());
    }

    public int getInt(Config entry) {
        return this.getInt(entry.getPath(), entry.getDefaultInt());
    }

    public boolean getBoolean(Config entry) {
        return this.getBoolean(entry.getPath(), entry.getDefaultBoolean());
    }

    public int getInt(String entry, int defaultEntry) {
        if (!this.getConfig().isInt(entry)) {
            this.getConfig().set(entry, (Object)defaultEntry);
            this.getPlugin().saveConfig();
        }
        return this.getConfig().getInt(entry);
    }

    public boolean getBoolean(String entry, boolean defaultEntry) {
        if (!this.getConfig().isBoolean(entry)) {
            this.getConfig().set(entry, (Object)defaultEntry);
            this.getPlugin().saveConfig();
        }
        return this.getConfig().getBoolean(entry);
    }

    public boolean isActive(XcraftCommand command, boolean enabledByDefault) {
        return this.getBoolean("modules.command." + command.getName(), enabledByDefault);
    }

    public boolean isActive(XcraftEventHandler event, boolean enabledByDefault) {
        return this.getBoolean("modules.event." + event.getName(), enabledByDefault);
    }

    public boolean isActive(XcraftEvent event, boolean enabledByDefault) {
        return this.getBoolean("modules.event." + event.getName(), enabledByDefault);
    }

    public XcraftPlugin getPlugin() {
        return this.plugin;
    }

    private FileConfiguration getConfig() {
        return this.getPlugin().getConfig();
    }
}

