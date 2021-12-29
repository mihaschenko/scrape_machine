package com.scraperservice.storage;

import net.jcip.annotations.ThreadSafe;

import java.util.Objects;

@ThreadSafe
public class DataCell implements Comparable<DataCell> {
    private final int priority;
    private final String name;
    private final String value;
    private final boolean isNecessary;

    public DataCell(int priority, String name, String value, boolean isNecessary) {
        if(name == null)
            throw new NullPointerException("String name = null");
        this.priority = priority;
        this.name = name;
        this.value = value;
        this.isNecessary = isNecessary;
    }

    public DataCell(String name, String value, boolean isNecessary) {
        this(-1, name, value, isNecessary);
    }

    public DataCell(int priority, String name, String value) { this(priority, name, value, false); }

    public DataCell(String name, String value) {
        this(-1, name, value, false);
    }

    public int getPriority() { return priority; }
    public String getName() { return name; }
    public String getValue() { return value; }
    public boolean isNecessary() { return isNecessary; }

    /**
     * @return true - поле value не равняется null и не пустое
     */
    public boolean check() {
        return value != null && !value.isEmpty();
    }

    @Override
    public int compareTo(DataCell o2) {
        return this.priority != o2.priority ? this.priority - o2.priority : this.value.compareTo(o2.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataCell dataCell = (DataCell) o;

        if (priority != dataCell.priority) return false;
        if (isNecessary != dataCell.isNecessary) return false;
        if (!Objects.equals(name, dataCell.name)) return false;
        return Objects.equals(value, dataCell.value);
    }

    @Override
    public int hashCode() {
        int result = priority;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (isNecessary ? 1 : 0);
        return result;
    }

    @Override
    public String toString() { return name.toUpperCase() + ": " + value; }
}
