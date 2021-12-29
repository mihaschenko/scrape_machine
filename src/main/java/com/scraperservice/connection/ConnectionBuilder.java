package com.scraperservice.connection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionBuilder {
    public static Connection build(Class<? extends Connection> connectionClass, Object[] parameters)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        List<Connection> connections = build(connectionClass, parameters, 1);
        return connections == null || connections.size() == 0 ? null : connections.get(0);
    }

    public static List<Connection> build(Class<? extends Connection> connectionClass, Object[] parameters, int amount)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if(amount < 1)
            throw new IllegalArgumentException("int amount < 1");
        Constructor<?>[] constructors = connectionClass.getConstructors();
        int argumentsAmount = parameters.length;

        for(Constructor<?> constructor : constructors) {
            if(constructor.getParameterCount() == argumentsAmount) {
                if(argumentsAmount == 0)
                    return buildConnectionList(constructor, parameters, amount);

                Class<?>[] constructorParameterTypes = constructor.getParameterTypes();
                boolean argumentsMatch = true;
                for(int i = 0; i < constructorParameterTypes.length; i++) {
                    if(parameters[i] == null)
                        continue;
                    if(!constructorParameterTypes[i].isAssignableFrom(parameters[i].getClass())) {
                        argumentsMatch = false;
                        break;
                    }
                }
                if(argumentsMatch)
                    return buildConnectionList(constructor, parameters, amount);
            }
        }
        return null;
    }

    private static List<Connection> buildConnectionList(Constructor<?> constructor, Object[] parameters, int amount)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        List<Connection> result = new ArrayList<>();
        for(int i = 0; i < amount; i++)
            result.add((Connection) constructor.newInstance(parameters));
        return result;
    }

    private ConnectionBuilder() {}
}
