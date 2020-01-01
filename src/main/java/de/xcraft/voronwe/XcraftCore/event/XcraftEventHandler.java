/*
 * Decompiled with CFR 0.139.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockState
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventException
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.BlockEvent
 *  org.bukkit.event.block.BlockGrowEvent
 *  org.bukkit.event.player.PlayerEvent
 *  org.bukkit.plugin.EventExecutor
 */
package de.xcraft.voronwe.XcraftCore.event;

import de.xcraft.voronwe.XcraftCore.XcraftPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.plugin.EventExecutor;

public abstract class XcraftEventHandler
implements EventExecutor,
Listener {
    private boolean enabled = false;
    private boolean ignoreCancelled = false;
    private String name = null;
    private XcraftPlugin plugin;
    private Material[] blocks;
    private String[] permissions;
    private Method executor;
    private Class<? extends org.bukkit.event.Event> eventType = null;

    public XcraftEventHandler(XcraftEventManager manager) {
        this.plugin = manager.getPlugin();
        this.applyAnnotations();
        if (!this.isEnabled() || this.getAnnotations() == null) {
            return;
        }
        this.findExecutor();
        if (this.executor == null) {
            this.setEnabled(false);
        }
        this.blocks = this.getAnnotations().block();
        this.permissions = this.getAnnotations().permission();
        this.ignoreCancelled = this.getAnnotations().ignoreCancelled();
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

    private void findExecutor() {
        for (Method m : this.getClass().getMethods()) {
            this.plugin.log(m.toString());
            if (!this.isValidExecutor(m)) continue;
            this.executor = m;
            this.eventType = (Class<? extends org.bukkit.event.Event>) m.getParameterTypes()[0];
            break;
        }
        this.plugin.log("No valid executor found!");
    }

    protected XcraftPlugin getPlugin() {
        return this.plugin;
    }

    public void execute(Listener listener, org.bukkit.event.Event event) throws EventException {
        if (this.eventType.isInstance((Object)event)) {
            this.handleEvent(event);
        } else {
            this.plugin.log("Invalid event. Expected " + this.eventType + ". Received: " + event.toString());
        }
    }

    private void handleEvent(org.bukkit.event.Event event) {
        if (this.checkBlockEvent(event)) {
            return;
        }
        if (this.checkPlayerEvent(event)) {
            return;
        }
        this.invokeEvent(event);
    }

    private boolean checkBlockEvent(org.bukkit.event.Event event) {
        if (this.blocks.length > 0 && event instanceof BlockEvent) {
            Material type = event instanceof BlockGrowEvent ? ((BlockGrowEvent)event).getNewState().getType() : ((BlockEvent)event).getBlock().getType();
            if (!Arrays.asList(this.blocks).contains((Object)type)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkPlayerEvent(org.bukkit.event.Event event) {
        if (this.permissions.length > 0 && event instanceof PlayerEvent) {
            Player player = ((PlayerEvent)event).getPlayer();
            for (String permission : this.permissions) {
                if (player.hasPermission(permission)) continue;
                return true;
            }
        }
        return false;
    }

    private void invokeEvent(org.bukkit.event.Event event) {
        try {
            Object eventResult = this.executor.invoke(this, new Object[]{this.eventType.cast((Object)event)});
            this.handleEventResult(event, eventResult);
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void handleEventResult(org.bukkit.event.Event event, Object eventResult) {
        if (eventResult instanceof EventAction) {
            EventAction action = (EventAction)((Object)eventResult);
            switch (action) {
                case CANCEL_EVENT: {
                    this.cancelEvent(event);
                    break;
                }
            }
        }
    }

    private void cancelEvent(org.bukkit.event.Event event) {
        if (event instanceof Cancellable) {
            ((Cancellable)event).setCancelled(true);
        }
    }

    private boolean isValidExecutor(Method m) {
        if (!m.getName().equals("execute")) {
            return false;
        }
        if (!m.getReturnType().isAssignableFrom(EventAction.class)) {
            return false;
        }
        if (m.getParameterTypes().length != 1) {
            return false;
        }
        return this.isAssignable(m.getParameterTypes()[0]);
    }

    private boolean isAssignable(Class<?> type) {
        Class<org.bukkit.event.Event> event = org.bukkit.event.Event.class;
        return event.isAssignableFrom(type);
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

