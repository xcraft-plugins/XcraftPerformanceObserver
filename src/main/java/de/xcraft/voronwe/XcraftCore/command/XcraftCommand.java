/*
 * Decompiled with CFR 0.139.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Server
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.ConsoleCommandSender
 *  org.bukkit.entity.Player
 */
package de.xcraft.voronwe.XcraftCore.command;

import de.xcraft.voronwe.XcraftCore.XcraftException;
import de.xcraft.voronwe.XcraftCore.XcraftPermissionException;
import de.xcraft.voronwe.XcraftCore.XcraftPlugin;
import de.xcraft.voronwe.XcraftCore.util.Default;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public abstract class XcraftCommand
extends org.bukkit.command.Command {
    public static final String ERROR_MESSAGE = (Object)ChatColor.RED + "An internal error occurred while attempting to perform this command";
    private final XcraftPlugin plugin;

    public XcraftCommand(XcraftPlugin plugin) {
        super("");
        this.plugin = plugin;
        this.setCommandInfo();
    }

    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        boolean success = false;
        try {
            this.testPermission(sender);
            success = this.onCommand(sender, commandLabel, args);
            if (!success) {
                sender.sendMessage((Object)ChatColor.RED + String.format("/%s %s", commandLabel, this.getUsage()).trim());
            }
        }
        catch (XcraftPermissionException e) {
            e.notifyPlayer();
        }
        catch (XcraftCommandUsageException e) {
            sender.sendMessage((Object)ChatColor.RED + e.getMessage());
            sender.sendMessage((Object)ChatColor.RED + String.format("/%s %s", commandLabel, this.getUsage()).trim());
        }
        catch (XcraftException e) {
            sender.sendMessage(ERROR_MESSAGE);
            e.printStackTrace();
        }
        catch (Exception e) {
            sender.sendMessage(ERROR_MESSAGE);
        }
        return success;
    }

    private void setCommandInfo() {
        this.setName(this.getAnnotatedCommand());
        this.setUsage(this.getAnnotatedUsage());
        this.setAliases(this.getAnnotatedAliases());
        this.setDescription(this.getAnnotatedDescription());
        this.setPermission(this.getAnnotatedPermissions());
        this.setPermissionMessage(this.getAnnotatedPermissionMessage());
    }

    private String getAnnotatedPermissionMessage() {
        return new Default<String>(this.getAnnotations().permissionMessage(), "").get();
    }

    private String getAnnotatedPermissions() {
        return new Default<String>(String.join((CharSequence)";", this.getAnnotations().permission()), "").get();
    }

    private String getAnnotatedDescription() {
        return new Default<String>(this.getAnnotations().description(), "").get();
    }

    private List<String> getAnnotatedAliases() {
        return new Default<List<String>>(Arrays.asList(this.getAnnotations().aliases()), new ArrayList()).get();
    }

    private String getAnnotatedUsage() {
        return new Default<String>(this.getAnnotations().usage(), "").get();
    }

    private String getAnnotatedCommand() {
        return new Default<String>(this.getAnnotations().command(), ((Object)((Object)this)).getClass().getSimpleName()).get();
    }

    private Command getAnnotations() {
        Class<?> element = ((Object)((Object)this)).getClass();
        if (element.isAnnotationPresent(Command.class)) {
            return element.getAnnotation(Command.class);
        }
        throw new IllegalStateException("Annotations not present!");
    }

    protected boolean onCommand(CommandSender sender, String commandLabel, String[] args) throws XcraftException {
        boolean success = false;
        if (sender instanceof ConsoleCommandSender) {
            success = this.executeAsConsole((ConsoleCommandSender)sender, commandLabel, args);
        } else if (sender instanceof Player) {
            success = this.executeAsPlayer((Player)sender, commandLabel, args);
        } else {
            sender.sendMessage(String.format("%sThis command cannot be executed as %s.", new Object[]{ChatColor.RED, sender.getClass().getSimpleName()}));
        }
        return success;
    }

    protected boolean executeAsConsole(ConsoleCommandSender sender, String commandLabel, String[] args) throws XcraftException {
        sender.sendMessage("This command cannot be executed from Console.");
        return true;
    }

    protected boolean executeAsPlayer(Player sender, String commandLabel, String[] args) throws XcraftException {
        sender.sendMessage((Object)ChatColor.RED + "This command cannot be executed as a Player.");
        return true;
    }

    public boolean testPermission(CommandSender target) {
        return this.testPermissionSilent(target) || this.throwPermissionException(target);
    }

    private boolean throwPermissionException(CommandSender target) {
        String message = this.getPermissionMessage() == null ? (Object)ChatColor.RED + "I'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error." : (this.getPermissionMessage().length() != 0 ? this.getPermissionMessage().replace("<permission>", this.getPermission()) : null);
        throw new XcraftPermissionException(target, this.getPermission(), message);
    }

    public boolean isEnabled() {
        return this.getAnnotations().enabled();
    }

    protected XcraftPlugin getPlugin() {
        return this.plugin;
    }

    protected Player getPlayer(String name) {
        return this.getPlugin().getServer().getPlayer(name);
    }
}

