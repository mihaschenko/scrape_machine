package com.scraperservice.utils;

public class MatrixUtils {
    /**
     * Итерирует матрицу. Матрица должна быть корректной и заполнена только единицами и нулями.
     * В каждом ряду должна быть только одна единица
     * @return false - все варианты матрицы были пройдены
     */
    public static boolean iterateMatrix(int[][] matrix) {
        for(int r = 0; r < matrix.length; r++) {
            for(int i = 0; i < matrix[r].length; i++) {
                if(matrix[r][i] == 1) {
                    if(i+1 < matrix[r].length) {
                        matrix[r][i] = 0;
                        matrix[r][i+1] = 1;
                        return true;
                    }
                    else {
                        if(r+1 < matrix.length) {
                            matrix[r][i] = 0;
                            matrix[r][0] = 1;
                            break;
                        }
                        else
                            return false;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Создаёт матрицу на основе входных данных. Матрица будет заполнена единицами и нулями.
     * Единицы будут в первых ячейках каждого ряда.
     * @param variants количество чисел - количество рядов. Само число обозначает количество ячеек в ряде.
     * @exception IllegalArgumentException если аргумент равен null или пуст, или одно из чисел равно или меньше нуля
     */
    public static int[][] createVariantMatrix(int... variants) {
        if(variants == null || variants.length == 0)
            throw new IllegalArgumentException();
        for(int variant : variants) {
            if(variant <= 0)
                throw new IllegalArgumentException();
        }

        int[][] result = new int[variants.length][];
        for(int i = 0; i < result.length; i++) {
            result[i] = new int[variants[i]];
            result[i][0] = 1;
        }
        return result;
    }
}
