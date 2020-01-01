/*
 * Decompiled with CFR 0.139.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandMap
 *  org.bukkit.plugin.SimplePluginManager
 */
package de.xcraft.voronwe.XcraftCore.command;

import de.xcraft.voronwe.XcraftCore.XcraftPlugin;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.SimplePluginManager;

public abstract class XcraftCommandManager {
    private final XcraftPlugin plugin;
    private final List<XcraftCommand> registeredCommands;

    public XcraftCommandManager(XcraftPlugin plugin) {
        this.plugin = plugin;
        this.registeredCommands = new LinkedList<XcraftCommand>();
    }

    private static CommandMap getCommandMap() {
        CommandMap commandMap = null;
        try {
            if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
                Field f = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
                f.setAccessible(true);
                commandMap = (CommandMap)f.get((Object)Bukkit.getPluginManager());
            }
        }
        catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        return commandMap;
    }

    public XcraftPlugin getPlugin() {
        return this.plugin;
    }

    protected void registerCommand(XcraftCommand command) {
        if (this.plugin.getConfigManager().isActive(command, command.isEnabled())) {
            if (!XcraftCommandManager.getCommandMap().register(this.plugin.getName(), (Command)command)) {
                this.getPlugin().log(String.format("Command %s already registered!", command.getName()));
            } else {
                this.registeredCommands.add(command);
            }
        }
    }

    public void unregisterCommands() {
        for (XcraftCommand registeredCommand : this.registeredCommands) {
            registeredCommand.unregister(XcraftCommandManager.getCommandMap());
        }
    }

    public abstract void registerCommands();
}

