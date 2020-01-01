/*
 * Decompiled with CFR 0.139.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 */
package de.xcraft.voronwe.XcraftCore;

import org.bukkit.command.CommandSender;

public class XcraftPermissionException
extends XcraftException {
    private final CommandSender player;
    private final String permission;
    private String message;

    public XcraftPermissionException(CommandSender player, String permission, String message) {
        super("Player " + player.getName() + " doesn't have permission to for " + permission);
        this.message = message;
        this.player = player;
        this.permission = permission;
    }

    public String getPermissionMessage() {
        return this.message;
    }

    public void setPermissionMessage(String message) {
        this.message = message;
    }

    public void notifyPlayer() {
        if (this.message != null) {
            this.player.sendMessage(this.message);
        }
    }

    public String getPermission() {
        return this.permission;
    }
}

