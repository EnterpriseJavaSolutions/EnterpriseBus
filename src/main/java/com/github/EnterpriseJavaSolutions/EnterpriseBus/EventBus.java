package com.github.EnterpriseJavaSolutions.EnterpriseBus;

import com.github.EnterpriseJavaSolutions.EnterpriseBus.annotation.Subscribe;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EventBus {

    // event type -> listeners
    private final Map<Class<?>, List<Listener>> listeners = new ConcurrentHashMap<>();

    public void register(Object obj) {
        for (Method method : obj.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Subscribe.class)) continue;

            Class<?>[] params = method.getParameterTypes();
            if (params.length != 1)
                throw new IllegalStateException("subscriber must have exactly 1 parameter");

            method.setAccessible(true);
            Class<?> eventType = params[0];

            listeners
                    .computeIfAbsent(eventType, k -> new ArrayList<>())
                    .add(new Listener(obj, method));
        }
    }

    public void unregister(Object obj) {
        for (List<Listener> list : listeners.values()) {
            list.removeIf(l -> l.owner == obj);
        }
    }

    public void post(Object event) {
        Class<?> eventClass = event.getClass();

        for (Map.Entry<Class<?>, List<Listener>> entry : listeners.entrySet()) {
            if (!entry.getKey().isAssignableFrom(eventClass)) continue;

            for (Listener listener : entry.getValue()) {
                try {
                    listener.method.invoke(listener.owner, event);
                } catch (Exception e) {
                    throw new RuntimeException("Error while handling event " + eventClass.toString() + " in class " + listener.getClass(), e);
                }
            }
        }
    }

    private static class Listener {
        final Object owner;
        final Method method;

        Listener(Object owner, Method method) {
            this.owner = owner;
            this.method = method;
        }
    }
}
