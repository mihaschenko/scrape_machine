package com.scraperservice.storage;

import net.jcip.annotations.ThreadSafe;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@ThreadSafe
public class DataArray extends CopyOnWriteArrayList<DataCell> {
    /**
     * Ссылка на продукт. Рекомендуется полная ссылка
     */
    private final String url;
    private final boolean includeUrl;

    /**
     * @param url ссылка на продукт. Рекомендуется полная ссылка
     * @throws NullPointerException если переменная url = null
     */
    public DataArray(String url) {
        this(url, true);
    }

    public DataArray(String url, boolean includeUrl) {
        if(url == null)
            throw new NullPointerException("String url = null");
        this.url = url;
        this.includeUrl = includeUrl;
    }

    /**
     * @return ссылка на продукт, указанная в конструкторе
     */
    public String getUrl() { return url; }

    /**
     * Проверяет все обязательные ячейки на наличие данных.
     * @return true - если все обязательные ячейки не равны null и не пусты
     */
    public boolean checkAllNecessaryCells() {
        for(DataCell dc : this) {
            if(dc.isNecessary() && !dc.check())
                return false;
        }
        return true;
    }

    /**
     * Возвращает список имён всех обязательных, но пустых ячеек
     * @return список имён ячеек равных null или имеющих пустую строку
     */
    public List<String> getNamesOfNecessaryEmptyCells() {
        return this.stream().filter(dataCell -> dataCell.isNecessary() && !dataCell.check())
                .map(DataCell::getName).collect(Collectors.toList());
    }

    /**
     * Возвращает список имён всех пустых ячеек
     * @return список имён ячеек равных null или имеющих пустую строку
     */
    public List<String> getNamesOfEmptyCells() {
        return this.stream().filter(dataCell -> !dataCell.check())
                .map(DataCell::getName).collect(Collectors.toList());
    }

    /**
     * Возвращает список имён всех НЕобязательных, но пустых ячеек
     * @return список имён ячеек равных null или имеющих пустую строку
     */
    public List<String> getNamesOfNotNecessaryEmptyCells() {
        return this.stream().filter(dataCell -> !dataCell.isNecessary() && !dataCell.check())
                .map(DataCell::getName).collect(Collectors.toList());
    }

    /**
     * Данные содержатся согласно сортировке.
     * @return массив с именами ячеек в верхнем регистре
     */
    public String[] getTitleArray() {
        return getArray(true);
    }

    /**
     * Данные содержатся согласно сортировке.
     * @return массив со значениями ячеек
     */
    public String[] toValueArray() {
        return getArray(false);
    }

    /**
     * @param isTitle true - вернуть массив заголовков
     * @return возвращается отсортированный массив заголовков или их данных
     */
    private synchronized String[] getArray(boolean isTitle) {
        String[] result = new String[includeUrl ? size()+1 : size()];

        if(includeUrl) {
            if(isTitle)
                result[0] = "URL";
            else
                result[0] = url;
        }

        int i = includeUrl ? 1 : 0;
        for(DataCell dc : this) {
            if(isTitle)
                result[i] = dc.getName();
            else
                result[i] = escapeSlash(dc.getValue());
            i++;
        }
        return result;
    }

    /**
     * Экранирует символ '/'
     * @param str целевая строка
     * @return результат после экранирования
     */
    private String escapeSlash(String str) {
        if(str != null && str.contains("\\"))
            str = str.replaceAll("\\\\", "\\\\\\\\");
        return str;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("URL: ").append(url);
        for(DataCell dc : this)
            result.append(",\n").append(dc.toString());
        return result.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataArray dataArray = (DataArray) o;

        if (!url.equals(dataArray.url)) return false;
        return iterator().equals(dataArray.iterator());
    }

    @Override
    public int hashCode() {
        int result = url.hashCode();
        result = 31 * result + super.hashCode();
        return result;
    }

    @Override
    public DataArray clone() {
        DataArray result = new DataArray(url);
        result.addAll(this);
        return result;
    }
}
