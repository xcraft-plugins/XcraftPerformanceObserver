/*
 * Decompiled with CFR 0.139.
 * 
 * Could not load the following classes:
 *  org.bukkit.Server
 *  org.bukkit.command.CommandSender
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginDescriptionFile
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitTask
 */
package de.xcraft.voronwe.XcraftPerformanceObserver.task;

import de.xcraft.voronwe.XcraftCore.XcraftPlugin;
import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class StartupMessage
extends BukkitRunnable {
    public static final String STARTUP_ICON = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/62/OpenMoji-color_1F4C8.svg/240px-OpenMoji-color_1F4C8.svg.png";
    public static final String STARTUP_GREEN = "#52C18E";
    private final XcraftPlugin plugin;

    public StartupMessage(XcraftPlugin plugin) {
        this.plugin = plugin;
        this.runTaskLater((Plugin)plugin, 1L);
    }

    public void run() {
        Server server = this.plugin.getServer();
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime() / 1000L;
        //String serverName = String.format("%s:%s", server.getServerName(), server.getPort());
        String startupTime = uptime > 1000L ? "Server was probably running." : String.format("Server start took *%s seconds*", uptime);
        String bukkitName = String.format("%s %s%s", server.getName(), server.getBukkitVersion(), this.testOutdated());
    }

    private String testOutdated() {
        Date buildDate;
        if (this.plugin.getServer().getClass().getPackage().getImplementationVendor() == null) {
            return "";
        }
        try {
            buildDate = new SimpleDateFormat("yyyyMMdd-HHmm").parse(this.plugin.getServer().getClass().getPackage().getImplementationVendor());
        }
        catch (ParseException e) {
            this.plugin.log("Unable to check version, you should check manually!");
            return " (Unable to check!)";
        }
        Calendar deadline = Calendar.getInstance();
        deadline.add(6, -21);
        if (buildDate.before(deadline.getTime())) {
            Calendar current = Calendar.getInstance();
            long difference = TimeUnit.MILLISECONDS.toDays(current.getTimeInMillis() - buildDate.getTime()) / 7L;
            return String.format(" (Outdated - %s weeks old!)", difference);
        }
        return "";
    }
}

