package com.scraperservice.connection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionBuilder {
    public static Connection build(Class<? extends Connection> connectionClass)
            throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<Connection> connections = build(connectionClass, 1);
        return connections.size() == 0 ? null : connections.get(0);
    }

    public static List<Connection> build(Class<? extends Connection> connectionClass, int amount)
            throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        if(amount < 1)
            throw new IllegalArgumentException("connection amount < 1");

        return buildConnectionList(connectionClass.getConstructor(), amount);
    }

    private static List<Connection> buildConnectionList(Constructor<?> constructor, int amount)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        List<Connection> result = new ArrayList<>();
        for(int i = 0; i < amount; i++)
            result.add((Connection) constructor.newInstance());
        return result;
    }

    private ConnectionBuilder() {}
}
