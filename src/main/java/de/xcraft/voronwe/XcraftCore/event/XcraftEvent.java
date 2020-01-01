/*
 * Decompiled with CFR 0.139.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.event.Event
 */
package de.xcraft.voronwe.XcraftCore.event;

import de.xcraft.voronwe.XcraftCore.XcraftPlugin;
import org.bukkit.Material;

public abstract class XcraftEvent {
    private boolean enabled = false;
    private boolean ignoreCancelled = false;
    private String name = null;
    private XcraftPlugin plugin;
    private Material[] blocks;
    private String[] permissions;
    private Class<? extends org.bukkit.event.Event> eventType = null;

    public XcraftEvent(XcraftEventManager manager) {
        this.plugin = manager.getPlugin();
        this.applyAnnotations();
        if (!this.isEnabled() || this.getAnnotations() == null) {
            return;
        }
        this.blocks = this.getAnnotations().block();
        this.permissions = this.getAnnotations().permission();
        this.ignoreCancelled = this.getAnnotations().ignoreCancelled();
    }

    public Material[] getBlocks() {
        return this.blocks;
    }

    public String[] getPermissions() {
        return this.permissions;
    }

    private void applyAnnotations() {
        Event annotations = this.getAnnotations();
        if (annotations == null) {
            this.plugin.log("No annotations found for: " + this.getClass().getName());
        } else {
            this.name = annotations.event();
            this.setEnabled(this.plugin.getConfigManager().isActive(this, annotations.enabled()));
        }
    }

    protected XcraftPlugin getPlugin() {
        return this.plugin;
    }

    private Event getAnnotations() {
        Class<?> element = this.getClass();
        if (element.isAnnotationPresent(Event.class)) {
            Event singleAnnotation = element.getAnnotation(Event.class);
            return singleAnnotation;
        }
        return null;
    }

    public Class<? extends org.bukkit.event.Event> getType() {
        return this.eventType;
    }

    public boolean isIgnoreCancelled() {
        return this.ignoreCancelled;
    }

    public String getName() {
        return this.name;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

