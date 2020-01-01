/*
 * Decompiled with CFR 0.139.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Chunk
 *  org.bukkit.Server
 *  org.bukkit.World
 *  org.bukkit.block.BlockState
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.ConsoleCommandSender
 */
package de.xcraft.voronwe.XcraftPerformanceObserver.command;

import de.xcraft.voronwe.XcraftCore.XcraftPlugin;
import de.xcraft.voronwe.XcraftCore.command.Command;
import de.xcraft.voronwe.XcraftCore.command.XcraftCommand;
import de.xcraft.voronwe.XcraftPerformanceObserver.XcraftPerformanceObserver;
import de.xcraft.voronwe.XcraftPerformanceObserver.task.PerformanceLogger;
import java.util.Iterator;
import java.util.LinkedList;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

@Command(command="performance", permission={"XcraftPerformanceObserver.performance"}, usage="[display worlds]", description="Display performance statistics.")
public class PerformanceCommand
extends XcraftCommand {
    private static final String fullLine = (Object)ChatColor.DARK_GRAY + "" + (Object)ChatColor.STRIKETHROUGH + "-----------------------------------------------------";
    private final PerformanceLogger serverLogger;
    private String lh = (Object)ChatColor.GRAY + " | " + (Object)ChatColor.DARK_AQUA;
    private String lf = (Object)ChatColor.GRAY + " | " + (Object)ChatColor.GOLD;
    private String worldStatsHeader = String.format("%s| %sWorld   %sPlayer%sChunks%s Mobs %sEntities%sTiles %s", new Object[]{ChatColor.DARK_GRAY, ChatColor.DARK_AQUA, this.lh, this.lh, this.lh, this.lh, this.lh, this.lh});
    private String worldStatsFormat = (Object)ChatColor.DARK_GRAY + "| " + (Object)ChatColor.AQUA + "%-8s" + this.lf + "%-6d" + this.lf + "%-6d" + this.lf + "%-6d" + this.lf + "%-8d" + this.lf + "%-6d" + this.lf;

    public PerformanceCommand(XcraftPlugin plugin) {
        super(plugin);
        if (plugin instanceof XcraftPerformanceObserver) {
            this.serverLogger = ((XcraftPerformanceObserver)plugin).getServerLogger();
        } else {
            this.serverLogger = null;
            plugin.log("Serverlogger not found!");
        }
    }

    private static String shortNumber(int input) {
        if (input > 1000000) {
            return Math.round(input / 1000000) + "m";
        }
        if (input > 1000) {
            return Math.round(input / 1000) + "k";
        }
        return String.valueOf(input);
    }

    private ChatColor statusColor(float input) {
        return this.statusColor(input, 100.0f, false);
    }

    private ChatColor statusColor(float input, float scale, boolean invert) {
        input = Math.abs(input);
        input = input * 100.0f / scale;
        if (invert) {
            input = 100.0f - input;
        }
        if (input > 95.0f) {
            return ChatColor.DARK_GREEN;
        }
        if (input > 80.0f) {
            return ChatColor.GREEN;
        }
        if (input > 70.0f) {
            return ChatColor.GOLD;
        }
        if (input > 60.0f) {
            return ChatColor.YELLOW;
        }
        if (input > 50.0f) {
            return ChatColor.RED;
        }
        return ChatColor.DARK_RED;
    }

    @Override
    public boolean onCommand(CommandSender sender, String label, String[] args) {
        LinkedList<String> output = new LinkedList<String>();
        ChatColor gray = ChatColor.GRAY;
        this.serverLogger.updateStats(false);
        output.add(fullLine);
        output.add(String.format("%s| %sServer status: %s%d%s online player, %s%d%s active worlds", new Object[]{ChatColor.DARK_GRAY, ChatColor.DARK_AQUA, ChatColor.GOLD, this.serverLogger.players, gray, ChatColor.GOLD, this.serverLogger.worlds, gray}));
        output.add(String.format("%s|     %sChunks: %s%d%s [%s+%d%s | %s*%d%s | %s-%d%s] %sTicks: [%sR %s%s | %sH %s%s%s]", new Object[]{ChatColor.DARK_GRAY, gray, ChatColor.GOLD, this.serverLogger.chunks, gray, ChatColor.GREEN, this.serverLogger.chunksLoaded, gray, ChatColor.GOLD, this.serverLogger.chunksGenerated, gray, ChatColor.RED, this.serverLogger.chunksUnloaded, gray, gray, ChatColor.GOLD, PerformanceCommand.shortNumber(Math.max(this.serverLogger.redstoneTicks, this.serverLogger.redstoneTicksLive)), gray, ChatColor.GOLD, ChatColor.GOLD, PerformanceCommand.shortNumber(Math.max(this.serverLogger.hopperTicks, this.serverLogger.hopperTicksLive)), gray}));
        output.add(String.format("%s|     %sMobs: %s%d%s | %sEntites: %s%d", new Object[]{ChatColor.DARK_GRAY, gray, ChatColor.GOLD, this.serverLogger.livingEntites, gray, ChatColor.GRAY, ChatColor.GOLD, this.serverLogger.entities}));
        String upTime = this.getUptime();
        output.add(String.format("%s| %sCurrent uptime: %s%s", new Object[]{ChatColor.DARK_GRAY, ChatColor.DARK_AQUA, gray, upTime}));
        output.add(fullLine);
        float tpspercentage = 0.0f;
        Iterator iterator = this.serverLogger.tpsHistory.iterator();
        while (iterator.hasNext()) {
            float tps = ((Float)iterator.next()).floatValue();
            tpspercentage += Math.abs(tps - 20.0f);
        }
        tpspercentage /= (float)this.serverLogger.tpsHistory.size();
        tpspercentage = Math.round((20.0f - tpspercentage) / 20.0f * 100.0f);
        ChatColor color = this.statusColor(tpspercentage);
        output.add((Object)ChatColor.DARK_GRAY + "| " + (Object)ChatColor.DARK_AQUA + "Processor status: " + (Object)color + tpspercentage + "%");
        String tpsHistory = (Object)ChatColor.WHITE + "[";
        for (int i = this.serverLogger.tpsHistory.size() - 1; i >= 0; --i) {
            float tps = this.serverLogger.tpsHistory.get(i).floatValue();
            color = this.statusColor(tps - 20.0f, 20.0f, true);
            tpsHistory = tpsHistory + (Object)color + String.valueOf(tps);
            if (i == 0) continue;
            tpsHistory = tpsHistory + (Object)ChatColor.WHITE + "|";
        }
        output.add((Object)ChatColor.DARK_GRAY + "| " + (Object)ChatColor.GRAY + "    Recent TPS: " + tpsHistory + (Object)ChatColor.WHITE + "]");
        output.add(fullLine);
        color = this.statusColor(this.serverLogger.memUsage, 100.0f, true);
        output.add((Object)ChatColor.DARK_GRAY + "| " + (Object)ChatColor.DARK_AQUA + "Memory status: " + (Object)color + (100.0f - this.serverLogger.memUsage) + "% free");
        int numBars = sender instanceof ConsoleCommandSender ? 45 : 100;
        float maxmem = this.serverLogger.maxMem;
        int minused = (int)((float)this.serverLogger.minUsedMem / maxmem * (float)numBars);
        int used = (int)((float)this.serverLogger.usedMem / maxmem * (float)numBars);
        int maxused = (int)((float)this.serverLogger.maxUsedMem / maxmem * (float)numBars);
        int alloc = (int)((float)this.serverLogger.allocMem / maxmem * (float)numBars);
        StringBuilder memBar = new StringBuilder((Object)ChatColor.WHITE + "[");
        for (int i = 0; i < numBars; ++i) {
            memBar.append((Object)this.getMemoryColor(minused, used, maxused, alloc, i)).append("|");
        }
        memBar.append((Object)ChatColor.WHITE).append("]");
        output.add(String.format("%s|     %sMemory profile: %s", new Object[]{ChatColor.DARK_GRAY, ChatColor.GRAY, memBar.toString()}));
        output.add(String.format("%s| %s    Used memory [min/max]: %s%d MB %s[%s%dMB%s/%s%dMB%s]", new Object[]{ChatColor.DARK_GRAY, ChatColor.GRAY, ChatColor.GOLD, this.serverLogger.usedMem, gray, ChatColor.RED, this.serverLogger.minUsedMem, gray, ChatColor.GREEN, this.serverLogger.maxUsedMem, gray}));
        output.add(String.format("%s| %s    Allocated/Maximum memory: %s%dMB%s/%s%dMB%s (%d%%)", new Object[]{ChatColor.DARK_GRAY, ChatColor.GRAY, ChatColor.DARK_GREEN, this.serverLogger.allocMem, gray, ChatColor.DARK_GRAY, this.serverLogger.maxMem, ChatColor.GRAY, alloc}));
        output.add(fullLine);
        if (args.length != 0) {
            output.add(this.worldStatsHeader);
            for (World world : this.getPlugin().getServer().getWorlds()) {
                String name = world.getName();
                if (name.startsWith("world_")) {
                    name = name.replaceFirst("world_", "").replace("_", " ");
                }
                int livingentities = world.getLivingEntities().size();
                int entities = world.getEntities().size() - livingentities;
                int tiles = 0;
                for (Chunk chunk : world.getLoadedChunks()) {
                    tiles += chunk.getTileEntities().length;
                }
                output.add(String.format(this.worldStatsFormat, name, world.getPlayers().size(), world.getLoadedChunks().length, livingentities, entities, tiles));
            }
            output.add(fullLine);
        }
        sender.sendMessage(output.toArray(new String[output.size()]));
        return true;
    }

    private ChatColor getMemoryColor(int minused, int used, int maxused, int alloc, int i) {
        ChatColor color = i < minused ? ChatColor.DARK_RED : (i < used ? ChatColor.RED : (i == used ? ChatColor.GOLD : (i < maxused ? ChatColor.GREEN : (i < alloc ? ChatColor.DARK_GREEN : ChatColor.DARK_GRAY))));
        return color;
    }

    private String getUptime() {
        long diff = this.getPlugin().getOnlineTime() / 1000L;
        long secs = diff % 60L;
        long mins = diff / 60L % 60L;
        long hours = diff / 3600L % 24L;
        long days = diff / 86400L;
        return (days == 0L ? "" : new StringBuilder().append(days).append(days == 1L ? " day " : " days ").toString()) + (hours == 0L ? "" : new StringBuilder().append(hours).append(hours == 1L ? " hour " : " hours ").toString()) + (mins == 0L ? "" : new StringBuilder().append(mins).append(mins == 1L ? " minute " : " minutes ").toString()) + (secs == 0L ? "" : new StringBuilder().append(secs).append(secs == 1L ? " second " : " seconds ").toString());
    }
}

