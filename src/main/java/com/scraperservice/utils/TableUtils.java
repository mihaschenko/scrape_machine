package com.scraperservice.utils;

import com.scraperservice.storage.SimpleJsonString;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.stream.Collectors;

public class TableUtils {
    /**
     * Таблица преобразуется в json строку. Заголовок находится слева.
     * Теги 'thead' и 'tbody' игнорируются
     * @param table таблица. Если элемент не является таблицей - возвращается пустая строка
     * @return json строка или пустая строка, если элемент не является таблицей или
     * таблица не корректна
     */
    public static String leftHeadTable(Element table) {
        String[][] matrix = getMatrix(table);
        if(matrix != null) {
            SimpleJsonString json = new SimpleJsonString();
            for (String[] m : matrix) {
                List<String> values = Arrays.stream(m)
                        .skip(1).distinct().toList();

                if (values.size() > 1)
                    json.openArray(m[0]);
                for (String value : values) {
                    if(values.size() > 1)
                        json.writeNewItem(value);
                    else
                        json.writeNewItem(m[0], value);
                }
                if (values.size() > 1)
                    json.closeArray();
            }
            return json.toString();
        }
        return "";
    }

    /**
     * Таблицы преобразуются в json строку. Заголовок находится слева.
     * Теги 'thead' и 'tbody' игнорируются. Несколько таблиц объеденяются в одну json строку
     * как одна таблица
     * @param tables таблицы. Если элемент не является таблицей - элемент игнорируется
     * @return json строка или пустая строка, если все элементы не являются таблицами или
     * таблицы не корректны
     */
    public static String leftHeadTableJoin(Elements tables) {
        if(tables.size() > 0) {
            SimpleJsonString json = new SimpleJsonString();
            String[][][] matrices = new String[tables.size()][][];
            for(int i = 0; i < tables.size(); i++)
                matrices[i] = getMatrix(tables.get(i));

            for (String[][] matrix : matrices) {
                if (matrix != null) {
                    for (String[] m : matrix) {
                        List<String> values = Arrays.stream(m)
                                .skip(1).distinct().collect(Collectors.toList());

                        if(values.size() == 1 && values.get(0).equals(m[0]))
                            continue;
                        if (values.size() > 1)
                            json.openArray(m[0]);
                        for (String value : values) {
                            if (values.size() > 1)
                                json.writeNewItem(value);
                            else
                                json.writeNewItem(m[0], value);
                        }
                        if (values.size() > 1)
                            json.closeArray();
                    }
                }
            }
            return json.toString();
        }
        return "";
    }

    /**
     * Таблица преобразуется в json строку. Заголовок находится сверху.
     * Теги 'thead' и 'tbody' игнорируются
     * @param table таблица. Если элемент не является таблицей - возвращается пустая строка
     * @return json строка или пустая строка, если элемент не является таблицей или
     * таблица не корректна
     */
    public static String topHeadTable(Element table) {
        String[][] matrix = getMatrix(table);
        if(matrix != null) {
            SimpleJsonString json = new SimpleJsonString();
            for(int i = 1; i < matrix.length; i++) {
                json.openLevel(Integer.toString(i-1));
                for(int j = 0; j < matrix[i].length; j++)
                    json.writeNewItem(matrix[0][j], matrix[i][j]);
                json.closeLevel();
            }
            return json.toString();
        }
        return "";
    }

    /**
     * Таблица преобразуется в json строку. Заголовок находится сверху.
     * Теги 'thead' и 'tbody' игнорируются. Несколько таблиц объеденяются в одну json строку
     * как одна таблица
     * @param tables таблицы. Если элемент не является таблицей - элемент игнорируется
     * @return json строка или пустая строка, если элементы не являются таблицами или
     * таблицы не корректны
     */
    public static String topHeadTableJoin(Elements tables) {
        if(tables.size() > 0) {
            SimpleJsonString json = new SimpleJsonString();
            String[][][] matrices = new String[tables.size()][][];
            for(int i = 0; i < tables.size(); i++)
                matrices[i] = getMatrix(tables.get(i));

            int jsonLineIndex = 0;
            for (String[][] matrix : matrices) {
                if (matrix != null) {
                    for(int i = 1; i < matrix.length; i++) {
                        json.openLevel(Integer.toString(jsonLineIndex));
                        for(int j = 0; j < matrix[i].length; j++)
                            json.writeNewItem(matrix[0][j], matrix[i][j]);
                        json.closeLevel();
                        jsonLineIndex++;
                    }
                }
            }
            return json.toString();
        }
        return "";
    }

    /**
     * Таблица преобразуется в json строку. Заголовки находится сверху и слева.
     * Теги 'thead' и 'tbody' игнорируются
     * @param table таблица. Если элемент не является таблицей - возвращается пустая строка
     * @param topLeadHead true, если основной заголовок сверху. Влияет на построение json строки
     * @return json строка или пустая строка, если элемент не является таблицей или
     * таблица не корректна
     */
    public static String topLeftHeadTable(Element table, boolean topLeadHead) {
        String[][] matrix = getMatrix(table);
        if(matrix != null) {
            SimpleJsonString json = new SimpleJsonString();
            if(topLeadHead) {
                for(int i = 1; i < matrix[0].length; i++) {
                    json.openLevel(matrix[0][i]);
                    for(int j = 1; j < matrix.length; j++)
                        json.writeNewItem(matrix[j][0], matrix[j][i]);
                    json.closeLevel();
                }
            }
            else {
                for(int i = 1; i < matrix.length; i++) {
                    json.openLevel(matrix[i][0]);
                    for(int j = 1; j < matrix[i].length; j++)
                        json.writeNewItem(matrix[0][j], matrix[i][j]);
                    json.closeLevel();
                }
            }
            return json.toString();
        }
        return "";
    }

    /**
     * Таблица преобразуется в json строки. Заголовки находится сверху и слева.
     * Теги 'thead' и 'tbody' игнорируются
     * @param table таблица. Если элемент не является таблицей - возвращается пустая строка
     * @param topLeadHead true, если основной заголовок сверху. Влияет на построение json строки.
     *                    Ведущий заголовок будет ключом для json с соответствующеми ему данными.
     * @return карта json строк. Json строки будут разделены на колонки/ряды с ведущим заголовком.
     *                    Ключом для json строки будет заголовок колонки/ряда
     */
    public static Map<String, String> topLeftHeadTableLeadColumnSeparately(Element table, boolean topLeadHead) {
        String[][] matrix = getMatrix(table);
        Map<String, String> result = new HashMap<>();
        if(matrix != null) {
            if(topLeadHead) {
                for(int i = 1; i < matrix[0].length; i++) {
                    SimpleJsonString json = new SimpleJsonString();
                    String name;

                    name = matrix[0][i];
                    for(int j = 1; j < matrix.length; j++)
                        json.writeNewItem(matrix[j][0], matrix[j][i]);
                    if(name != null && !name.isEmpty())
                        result.put(name, json.toString());
                }
            }
            else {
                for(int i = 1; i < matrix.length; i++) {
                    SimpleJsonString json = new SimpleJsonString();
                    String name;

                    name = matrix[i][0];
                    for(int j = 1; j < matrix[i].length; j++)
                        json.writeNewItem(matrix[0][j], matrix[i][j]);
                    if(name != null && !name.isEmpty())
                        result.put(name, json.toString());
                }
            }
        }
        return result;
    }

    /**
     * Таблица преобразуется в json строки. Заголовок находится сверху.
     * Теги 'thead' и 'tbody' игнорируются
     * @param table таблица. Если элемент не является таблицей - возвращается пустой Map
     * @param keyName имя столбца, значения которого будут ключами
     * @return Map, в котором ключами будут значения в столбце с заголовком равном значению
     * переменной keyName, а значение - строка с данными, которые соответствуют ключу
     */
    public static Map<String, String> topHeadTableRowsByKey(Element table, String keyName) {
        String[][] matrix = getMatrix(table);
        Map<String, String> result = new TreeMap<>();
        if(matrix != null) {
            int keyIndex = -1;
            for(int i = 0; i < matrix[0].length; i++) {
                if(matrix[0][i].equals(keyName)) {
                    keyIndex = i;
                    break;
                }
            }

            if(keyIndex >= 0) {
                for(int i = 1; i < matrix.length; i++) {
                    SimpleJsonString json = new SimpleJsonString();
                    if(matrix[i][keyIndex].trim().isEmpty())
                        continue;
                    for(int j = 0; j < matrix[i].length; j++)
                        json.writeNewItem(matrix[0][j], matrix[i][j]);
                    result.put(matrix[i][keyIndex], json.toString());
                }
            }
        }
        return result;
    }

    /**
     * Таблицы преобразуется в json строку. Заголовок находится слева.
     * Теги 'thead' и 'tbody' игнорируются. Каждая таблица будет преобразована в отдельную json строку
     * @param tables таблицы. Если элемент не является таблицей - возвращается пустая строка
     * @return массив json строк. Если один из элементов не является таблицей или таблица
     * не корректна - json строка будет пустой
     */
    public static String[] leftHeadTables(Elements tables) {
        String[] result = new String[tables.size()];
        for(int i = 0; i < tables.size(); i++)
            result[i] = leftHeadTable(tables.get(i));
        return result;
    }

    /**
     * Таблицы преобразуется в json строку. Заголовок находится сверху.
     * Теги 'thead' и 'tbody' игнорируются. Каждая таблица будет преобразована в отдельную json строку
     * @param tables таблицы. Если элемент не является таблицей - возвращается пустая строка
     * @return массив json строк. Если один из элементов не является таблицей или таблица
     * не корректна - json строка будет пустой
     */
    public static String[] topHeadTables(Elements tables) {
        String[] result = new String[tables.size()];
        for(int i = 0; i < tables.size(); i++)
            result[i] = topHeadTable(tables.get(i));
        return result;
    }

    /**
     * Таблицы преобразуется в json строку. Заголовки находится сверху и слева.
     * Теги 'thead' и 'tbody' игнорируются. Каждая таблица будет преобразована в отдельную json строку
     * @param tables таблицы. Если элемент не является таблицей - возвращается пустая строка
     * @param topLeadHead true, если основной заголовок сверху. Влияет на построение json строки
     * @return массив json строк. Если один из элементов не является таблицей или таблица
     * не корректна - json строка будет пустой
     */
    public static String[] topLeftHeadTables(Elements tables, boolean topLeadHead) {
        String[] result = new String[tables.size()];
        for(int i = 0; i < tables.size(); i++)
            result[i] = topLeftHeadTable(tables.get(i), topLeadHead);
        return result;
    }

    public static String[][] getMatrix(Element table) {
        return getMatrix(table, false);
    }

    public static String[][] getMatrixHTML(Element table) {
        return getMatrix(table, true);
    }

    /**
     * Возвращает матрицу таблицы. Размеры матрицы задаются от максимального количество
     * рядов и колонок в таблице. Значения ячеек, которые занимают сразу несколько ячеек в таблице,
     * дублируются в соответствующие места в матрице
     * @param table Jsoup Element. Если элемент не является таблицей - возвращается null
     * @return матрица таблицы или null, если элемент не является таблицей
     */
    private static String[][] getMatrix(Element table, boolean isHtml) {
        String[][] matrix = null;
        if(table == null || !table.is("table"))
            return null;

        Elements tableElements = table.children();
        List<Element> rows = new ArrayList<>();
        for(Element tableElem : tableElements) {
            if(tableElem.is("tr"))
                rows.add(tableElem);
            else if(tableElem.is("tbody, thead"))
                rows.addAll(tableElem.children());
        }

        if(rows.size() > 0) {
            int maxCellInRow = 0;
            List<Elements> rowAndCells = new ArrayList<>();
            for(Element row : rows) {
                Elements rowCells = row.children();
                if(rowCells.size() > maxCellInRow)
                    maxCellInRow = rowCells.size();
                rowAndCells.add(rowCells);
            }

            if(maxCellInRow > 0) {
                matrix = new String[rows.size()][maxCellInRow];
                for(int i = 0; i < rowAndCells.size() && i < matrix.length; i++) {
                    Elements cellsInRow = rowAndCells.get(i);
                    for(int j = 0, c = 0; j < matrix[i].length && c < cellsInRow.size(); j++) {
                        Element cell = cellsInRow.get(c);
                        if(matrix[i][j] != null)
                            continue;
                        else
                            c++;

                        if(cell.is("[colspan], [rowspan]")) {
                            int colspan = 1;
                            int rowspan = 1;
                            if(cell.is("[colspan]") && cell.attr("colspan").matches("\s*[0-9]+\s*")) {
                                colspan = Integer.parseInt(cell.attr("colspan").trim());
                            }
                            if(cell.is("[rowspan]") && cell.attr("rowspan").matches("\s*[0-9]+\s*")) {
                                rowspan = Integer.parseInt(cell.attr("rowspan").trim());
                            }
                            if(!cell.hasText() && cell.selectFirst("img[src]") != null)
                                fillMatrixHardCell(matrix, i, j, rowspan, colspan,
                                        ScrapeUtils.getAttribute(cell, "img", "abs:src"));
                            else
                                fillMatrixHardCell(matrix, i, j, rowspan, colspan,
                                    isHtml ? cell.outerHtml() : cell.text().trim());
                        }
                        else {
                            if(!cell.hasText() && cell.selectFirst("img[src]") != null)
                                matrix[i][j] = ScrapeUtils.getAttribute(cell, "img", "abs:src");
                            else
                                matrix[i][j] = isHtml ? cell.outerHtml() : cell.text().trim();
                        }
                    }
                }
            }
        }
        fillMatrix(matrix);
        return matrix;
    }

    private static void fillMatrix(String[][] matrix) {
        if(matrix != null) {
            for(int i = 0; i < matrix.length; i++) {
                for(int j = 0; j < matrix[i].length; j++) {
                    if(matrix[i][j] == null)
                        matrix[i][j] = "";
                }
            }
        }
    }

    /**
     * Заполняет ячейки матрицы заданным значением
     * @param matrix матрица
     * @param iStart номер ряда, в котором находится ячейка
     * @param jStart номер колонки, в котором находится ячейка
     * @param rowspan значение 'rowspan' ячейки. Значение таких ячеек дублируется вниз по матрице от начальной ячейки
     * @param colspan значение 'colspan' ячейки. Значение таких ячеек дублируется вправо по матрице от начальной ячейки
     * @param value значение, которое будет записано в матрицу
     */
    private static void fillMatrixHardCell(String[][] matrix, int iStart, int jStart, int rowspan, int colspan, String value) {
        for(int i = iStart; i < matrix.length && i - iStart < rowspan; i++) {
            for(int j = jStart; j < matrix[i].length && j - jStart < colspan; j++) {
                matrix[i][j] = value;
            }
        }
    }
}
