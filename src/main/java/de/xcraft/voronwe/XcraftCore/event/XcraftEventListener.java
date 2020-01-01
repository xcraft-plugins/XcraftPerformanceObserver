/*
 * Decompiled with CFR 0.139.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventException
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.EventExecutor
 */
package de.xcraft.voronwe.XcraftCore.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

public class XcraftEventListener
implements Listener,
EventExecutor {
    private final XcraftEventManager manager;
    private final Class<? extends Event> eventClass;
    private final Set<EventEntry> executors;

    public XcraftEventListener(XcraftEventManager manager, Class<? extends Event> eventClass) {
        this.manager = manager;
        this.eventClass = eventClass;
        this.executors = new HashSet<EventEntry>();
    }

    public void execute(Listener listener, Event event) throws EventException {
        if (this.executors.isEmpty()) {
            this.manager.log("No executor in " + this.eventClass.getName() + " found for " + event.getClass());
        } else if (event.getClass().isAssignableFrom(this.eventClass)) {
            this.executors.stream().filter(this.getEventFilter(event)).forEach(this.executeEntry(event));
        } else {
            this.manager.log("Listener " + this.eventClass.getName() + " received invalid event: " + event.getClass());
        }
    }

    private Consumer<EventEntry> executeEntry(Event event) {
        return methodEntry -> {
            try {
                Object invoke = methodEntry.getMethod().invoke(methodEntry.getEvent(), new Object[]{event});
                EventAction result = (EventAction)((Object)invoke);
                this.handleEventResult(result, event);
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        };
    }

    private void handleEventResult(EventAction result, Event event) {
        if (result.equals((Object)EventAction.CANCEL_EVENT)) {
            ((Cancellable)event).setCancelled(true);
        }
    }

    private Predicate<EventEntry> getEventFilter(Event event) {
        return methodEntry -> true;
    }

    public boolean registerEvent(XcraftEvent handler, Method method) {
        boolean success;
        if (this.executors.stream().noneMatch(method::equals)) {
            this.executors.add(new EventEntry(handler, method, (byte)0));
            success = true;
        } else {
            this.manager.log("Event was registered twice, using original handler.");
            success = false;
        }
        return success;
    }

    public Class<? extends Event> getEvent() {
        return this.eventClass;
    }
}

