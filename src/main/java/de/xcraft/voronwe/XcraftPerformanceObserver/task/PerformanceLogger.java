/*
 * Decompiled with CFR 0.139.
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.Server
 *  org.bukkit.World
 *  org.bukkit.command.CommandSender
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitScheduler
 *  org.bukkit.scheduler.BukkitTask
 */
package de.xcraft.voronwe.XcraftPerformanceObserver.task;

import de.xcraft.voronwe.XcraftCore.XcraftPlugin;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Formatter;
import java.util.LinkedList;

public class PerformanceLogger
implements Runnable {
    public static final String ICON = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5c/OpenMoji-color_1F6E1.svg/240px-OpenMoji-color_1F6E1.svg.png";
    private static String HEADER = " | %-8s | %-5s | %-30s | %-6s | %-21s | %-7s | %-7s | %-7s | %-7s |\n";
    private static String PATTERN = " | %1$tH:%1$tM:%1$tS | %2$-5s | %3$19sMB (%4$6s) | %5$-6d | %6$5s[%7$-4s|%8$-4s|%9$-4s] | %10$-7d | %11$-7d | %12$-7d | %13$-7d | %14$-7d |\n";
    private final Runtime runtime = Runtime.getRuntime();
    public int tpsPerLog;
    public long timePerLog;
    public long prevTime;
    public float elapsedTime = 0.0f;
    public float tps = 0.0f;
    public LinkedList<Float> tpsHistory = new LinkedList();
    public long prevusedmem;
    public long maxMem;
    public long allocMem;
    public long usedMem;
    public float memUsage;
    public long minUsedMem = Long.MAX_VALUE;
    public long maxUsedMem;
    public int worlds;
    public int chunks;
    public int chunksLoaded;
    public int chunksUnloaded;
    public int chunksGenerated;
    public int entities;
    public int livingEntites;
    public int players;
    public int redstoneTicks;
    public int hopperTicks;
    public int redstoneTicksLive;
    public int hopperTicksLive;
    private XcraftPlugin plugin;
    private BukkitTask task;
    private BufferedWriter fileLogger;
    private File logfile;
    private Formatter formatter;

    public PerformanceLogger(XcraftPlugin plugin, int tpsPerLog) {
        this.plugin = plugin;
        this.tpsPerLog = tpsPerLog;
        this.timePerLog = tpsPerLog * 50;
        this.logfile = plugin.getDataFolder();
        this.logfile.mkdirs();
        this.logfile = new File(this.logfile + File.separator + "log.txt");
        try {
            this.fileLogger = new BufferedWriter(new FileWriter(this.logfile, true));
        }
        catch (IOException ex) {
            plugin.log("Failed to initialize performance logger file stream:");
            ex.printStackTrace();
        }
        this.formatter = new Formatter(this.fileLogger);
    }

    public void start() {
        this.prevTime = System.currentTimeMillis();
        this.task = this.plugin.getServer().getScheduler().runTaskTimer((Plugin)this.plugin, (Runnable)this, (long)this.tpsPerLog, (long)this.tpsPerLog);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd H:mm:ss");
        try {
            this.fileLogger.newLine();
            this.fileLogger.write(sdf.format(Calendar.getInstance().getTime()) + " - PerformanceMonitor started!");
            this.fileLogger.newLine();
            this.formatter.format(HEADER, "Time", "TPS", "Memory: used/[alloc/max]", "Worlds", "Chunks[+|*|-]", "RST", "HPT", "Mobs", "Enities", "Players");
            this.formatter.flush();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public void stop() {
        this.task.cancel();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd H:mm:ss");
        try {
            this.fileLogger.write(sdf.format(Calendar.getInstance().getTime()) + " - PerformanceMonitor stopped!");
            this.fileLogger.flush();
            this.fileLogger.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private void logStats() throws IOException {
        String memStats = this.usedMem + "/[" + this.allocMem + "/" + this.maxMem + "]";
        this.formatter.format(PATTERN, Calendar.getInstance(), Float.valueOf(this.tps), memStats, this.memUsage + "%", this.worlds, this.chunks, "+" + this.chunksLoaded, "*" + this.chunksGenerated, "-" + this.chunksUnloaded, this.redstoneTicksLive, this.hopperTicksLive, this.livingEntites, this.entities, this.players);
        this.formatter.flush();
        this.redstoneTicks = this.redstoneTicksLive;
        this.hopperTicks = this.hopperTicksLive;
        this.chunksLoaded = 0;
        this.chunksUnloaded = 0;
        this.chunksGenerated = 0;
        this.redstoneTicksLive = 0;
        this.hopperTicksLive = 0;
    }

    public void updateStats(boolean logging) {
        if (logging) {
            long time = System.currentTimeMillis();
            this.elapsedTime = time - this.prevTime;
            this.prevTime = time;
            this.tps = (float)Math.round((float)this.timePerLog / this.elapsedTime * 200.0f) / 10.0f;
            this.tpsHistory.add(Float.valueOf(this.tps));
        }
        if (this.tpsHistory.size() > 10) {
            this.tpsHistory.remove();
        }
        this.maxMem = this.runtime.maxMemory() / 0x100000L;
        this.allocMem = this.runtime.totalMemory() / 0x100000L;
        long lastUsedMem = this.usedMem;
        this.usedMem = this.allocMem - this.runtime.freeMemory() / 0x100000L;
        this.memUsage = (float)Math.round((float)this.usedMem / ((float)this.allocMem / 1000.0f)) / 10.0f;
        if (this.usedMem - lastUsedMem < 0L) {
            this.minUsedMem = this.usedMem;
        } else if (this.usedMem > this.maxUsedMem) {
            this.maxUsedMem = this.usedMem;
        }
        this.worlds = this.plugin.getServer().getWorlds().size();
        this.entities = 0;
        this.chunks = 0;
        this.livingEntites = 0;
        for (World world : this.plugin.getServer().getWorlds()) {
            this.entities += world.getEntities().size();
            this.livingEntites += world.getLivingEntities().size();
            this.chunks += world.getLoadedChunks().length;
        }
        this.entities -= this.livingEntites;
        this.players = this.plugin.getServer().getOnlinePlayers().size();
    }

    @Override
    public void run() {
        try {
            this.updateStats(true);
            this.logStats();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

