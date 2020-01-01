/*
 * Decompiled with CFR 0.139.
 * 
 * Could not load the following classes:
 *  org.bukkit.Server
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.EventExecutor
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 */
package de.xcraft.voronwe.XcraftCore.event;

import de.xcraft.voronwe.XcraftCore.XcraftPlugin;
import de.xcraft.voronwe.XcraftPerformanceObserver.XcraftPerformanceObserver;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

public abstract class XcraftEventManager {
    private XcraftPlugin plugin;
    private Map<Class<? extends Event>, XcraftEventListener> listeners;

    public XcraftEventManager(XcraftPerformanceObserver plugin) {
        this.plugin = plugin;
        this.listeners = new HashMap<Class<? extends Event>, XcraftEventListener>();
    }

    public void log(Object message) {
        this.plugin.log(message);
    }

    public XcraftPlugin getPlugin() {
        return this.plugin;
    }

    protected void register(OldXcraftEventListener eventListener) {
        this.getPlugin().getServer().getPluginManager().registerEvents((Listener)eventListener, (Plugin)this.getPlugin());
        this.getPlugin().getServer().getPluginManager();
    }

    protected void register(XcraftEventHandler eventListener) {
        if (eventListener.isEnabled()) {
            this.getPlugin().getServer().getPluginManager().registerEvent(eventListener.getType(), (Listener)eventListener, EventPriority.NORMAL, (EventExecutor)eventListener, (Plugin)this.getPlugin(), eventListener.isIgnoreCancelled());
        }
    }

    protected void register(XcraftEvent event) {
        this.plugin.log("Registering " + event);
        Method[] methods = event.getClass().getMethods();
        this.plugin.log(Arrays.toString(methods));
        Arrays.stream(methods).filter(this.getMethodFilter()).forEach(this.addMethodListener(event));
        Class<? extends Event> eventType = event.getType();
    }

    private void registerEvent(XcraftEvent event, Method method, Class<? extends Event> eventType) {
        XcraftEventListener listener;
        this.plugin.log("Registering " + event.toString() + " / " + method.toString() + " / " + eventType.toString());
        if (this.listeners.containsKey(eventType)) {
            listener = this.listeners.get(eventType);
        } else {
            listener = new XcraftEventListener(this, eventType);
            this.log("Registering new event listener for " + listener.getEvent());
            this.getPlugin().getEventManger().register(listener);
        }
        listener.registerEvent(event, method);
    }

    private Consumer<Method> addMethodListener(XcraftEvent event) {
        return method -> {
            Parameter parameter = method.getParameters()[0];
            Class<? extends org.bukkit.event.Event> type = (Class<? extends org.bukkit.event.Event>) parameter.getType();
            this.registerEvent(event, method, type);
        };
    }

    private Predicate<Method> getMethodFilter() {
        return method -> {
            boolean success;
            boolean parameterCount;
            boolean name = method.getName().equals("execute");
            boolean bl = parameterCount = method.getParameterCount() == 1;
            if (name && parameterCount) {
                this.plugin.log("Found " + method);
                Class<?> firstParameterClass = method.getParameters()[0].getType();
                success = Event.class.isAssignableFrom(firstParameterClass);
            } else {
                success = false;
            }
            return success;
        };
    }

    public abstract void registerEvents();

    public void register(XcraftEventListener listener) {
        this.getPlugin().getServer().getPluginManager().registerEvent(listener.getEvent(), (Listener)listener, EventPriority.NORMAL, (EventExecutor)listener, (Plugin)this.getPlugin(), false);
    }
}

