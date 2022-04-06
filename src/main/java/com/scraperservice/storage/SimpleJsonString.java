package com.scraperservice.storage;

import net.jcip.annotations.ThreadSafe;

import java.util.List;

/**
 * Записывает в json строку получаемые данные. Не исправляет ошибки, при неправильном
 * вводе данных.
 * @author Mihaschenko V.
 */
@ThreadSafe
public class SimpleJsonString {
    private final StringBuilder string;
    private int itemCounter;

    public SimpleJsonString() {
        string = new StringBuilder();
        itemCounter = 0;
    }

    /**
     * Добавляет пару ключ-значение
     * @param name ключ. Не может быть null или пустым
     * @param value значение. Не может быть null
     */
    public synchronized void writeNewItem(String name, String value) {
        if(name == null || name.isEmpty() || value == null)
            return;
        writeComma();
        string.append("\"").append(closeSlash(name)).append("\": \"").append(closeSlash(value)).append("\"");
        itemCounter++;
    }

    public synchronized void writeNewItems(List<String> names, List<String> values) {
        if(names == null || values == null || names.size() != values.size())
            return;
        for(int i = 0; i < names.size(); i++)
            writeNewItem(names.get(i), values.get(i));
    }

    /**
     * Добавляет значение в строку
     * @param name значение. Не может быть null
     */
    public synchronized void writeNewItem(String name) {
        if(name != null) {
            writeComma();
            string.append("\"").append(closeSlash(name)).append("\"");
            itemCounter++;
        }
    }

    public synchronized void writeNewItems(List<String> names) {
        if(names == null || names.size() == 0)
            return;
        for(String name : names)
            writeNewItem(name);
    }

    /**
     * Открывает массив ('[...]')
     * @param arrayName имя массива
     */
    public synchronized void openArray(String arrayName) {
        writeComma();
        string.append("\"").append(arrayName).append("\": [");
        itemCounter = 0;
    }

    /**
     * Закрывает массив ('[...]')
     */
    public synchronized void closeArray() {
        string.append("]");
        itemCounter = 1;
    }

    /**
     * Открывает массив ('[...]')
     * @param listName имя массива
     */
    @Deprecated
    public synchronized void openList(String listName) {
        openArray(listName);
    }

    /**
     * Закрывает массив ('[...]')
     */
    @Deprecated
    public synchronized void closeList() {
        closeArray();
    }

    /**
     * Открывает уровень ('{...}')
     * @param levelName имя уровня
     */
    public synchronized void openLevel(String levelName) {
        writeComma();
        string.append("\"").append(levelName).append("\": {");
        itemCounter = 0;
    }

    /**
     * Закрывает массив ('{...}')
     */
    public synchronized void closeLevel() {
        string.append("}");
        itemCounter = 1;
    }

    /**
     * Метод добавляет запятую в строку при необходимости
     */
    private void writeComma() {
        if(itemCounter > 0)
            string.append(", ");
    }

    private String closeSlash(String str) {
        return str.replaceAll("\"", "\\\\\"");
    }

    @Override
    public String toString() {
        if(!string.toString().isEmpty())
            return "{" + string + "}";
        return "";
    }
}
