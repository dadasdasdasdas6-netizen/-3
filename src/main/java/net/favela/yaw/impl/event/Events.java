package net.favela.yaw.impl.event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Events {
    private Events() {
    }

    private static final Logger LOGGER = Logger.getLogger(Events.class.getName());
    private static final Map<Class<?>, CopyOnWriteArrayList<Handler<?>>> HANDLERS = new ConcurrentHashMap<>();

    public record Handler<T extends Event>(
            Class<T> type,
            Priority priority,
            boolean receiveCancelled,
            Consumer<T> action
    ) {
    }

    public static <T extends Event> Handler<T> on(Class<T> type, Consumer<T> action) {
        return on(type, Priority.NORMAL, false, action);
    }

    public static <T extends Event> Handler<T> on(Class<T> type, Priority priority, Consumer<T> action) {
        return on(type, priority, false, action);
    }

    public static <T extends Event> Handler<T> on(Class<T> type, Priority priority,
                                                  boolean receiveCancelled, Consumer<T> action) {
        Handler<T> handler = new Handler<>(type, priority, receiveCancelled, action);
        CopyOnWriteArrayList<Handler<?>> list =
                HANDLERS.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>());

        synchronized (list) {
            int i = 0;
            for (Handler<?> existing : list) {
                if (handler.priority().ordinal() > existing.priority().ordinal()) break;
                i++;
            }
            list.add(i, handler);
        }
        return handler;
    }

    public static void off(Handler<?> handler) {
        CopyOnWriteArrayList<Handler<?>> list = HANDLERS.get(handler.type());
        if (list == null) return;
        list.remove(handler);
        if (list.isEmpty()) HANDLERS.remove(handler.type());
    }

    @SuppressWarnings("unchecked")
    public static <T extends Event> T post(T event) {
        CopyOnWriteArrayList<Handler<?>> list = HANDLERS.get(event.getClass());
        if (list == null || list.isEmpty()) return event;

        boolean cancellable = event instanceof Cancellable;
        for (Handler<?> handler : list) {
            if (cancellable && ((Cancellable) event).isCancelled() && !handler.receiveCancelled()) {
                continue;
            }
            try {
                ((Handler<T>) handler).action().accept(event);
            } catch (Throwable t) {
                LOGGER.log(Level.SEVERE,
                        "[Events] Handler threw for " + event.getClass().getSimpleName(), t);
            }
        }
        return event;
    }
}