package com.ysxsoft.gkpf.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.CellFormat;
import jxl.format.Colour;
import jxl.format.Font;
import jxl.format.RGB;

public class ExcelUtils {


    public static Cell[][] readExcelCell(Context context, String fileName, int position) throws Exception {
        //这里可以优化
        int maxRow, maxColumn;
        Workbook workbook = Workbook.getWorkbook(getInputStream(context, fileName));
        Sheet sheet = workbook.getSheet(position);
        maxRow = sheet.getRows();
        maxColumn = sheet.getColumns();
        Cell[][] data = new Cell[maxColumn][];
        for (int i = 0; i < maxColumn; i++) {
            Cell[] rows = new Cell[maxRow];
            for (int j = 0; j < maxRow; j++) {
                Log.e("---->AAAAA", "" + sheet.getColumnWidth(j));
                Cell cell = sheet.getCell(i, j);
                if (cell != null) {
                    rows[j] = cell;
                } else {
                    rows[j] = null;
                }
            }
            data[i] = rows;
        }
        workbook.close();
        //将行二维数组转换成列的二维数组
        return transformColumnArray(data);
    }

    /**
     * 获取输出流
     *
     * @param context
     * @return
     * @throws IOException
     */
    private static InputStream getInputStream(Context context, String fileName) throws IOException {
        InputStream is;
        is = context.getAssets().open(fileName);    //从assets获取
//            is = new FileInputStream(fileName);
        return is;
    }


    /**
     * 提供将数组[col][row]转换成数组[row][col]
     * 因为平时我们提供的二维数组可能是以行作为一组。
     *
     * @param rowArray 数组[row][col]
     * @return 数组[col][row]
     */
    public static <T> T[][] transformColumnArray(T[][] rowArray) {
        T[][] newData = null;
        T[] row = null;
        if (rowArray != null) {
            int maxLength = 0;
            for (T[] t : rowArray) {
                if (t != null && t.length > maxLength) {
                    maxLength = t.length;
                    row = t;
                }
            }
            if (row != null) {
                newData = (T[][]) Array.newInstance(rowArray.getClass().getComponentType(), maxLength);
                for (int i = 0; i < rowArray.length; i++) { //转换一下
                    for (int j = 0; j < rowArray[i].length; j++) {
                        if (newData[j] == null) {
                            newData[j] = (T[]) Array.newInstance(row.getClass().getComponentType(), rowArray.length);
                        }
                        newData[j][i] = rowArray[i][j];
                    }
                }
            }

        }
        return newData;
    }

    public static int getTextColor(Context context, Cell cell) {
        CellFormat cellFormat = cell.getCellFormat();
        if (cellFormat != null) {
            Font font = cellFormat.getFont();
            Colour colour = font.getColour();
            RGB rgb = colour.getDefaultRGB();
            return Color.rgb(rgb.getRed(), rgb.getGreen(), rgb.getBlue());
        }
        return Color.BLACK;
    }

    public static int getFontSize(Context context, Cell cell) {
        CellFormat cellFormat = cell.getCellFormat();
        if (cellFormat != null) {
            Font font = cellFormat.getFont();
            return font.getPointSize();
        }
        return 13;
    }

    public static int getBackgroundColor(Context context, Cell cell) {
        if (cell != null) {
            CellFormat cellFormat = cell.getCellFormat();
            if (cellFormat != null) {
                Colour colour = cellFormat.getBackgroundColour();
                RGB rgb = colour.getDefaultRGB();
                return Color.argb(100, rgb.getRed(), rgb.getGreen(), rgb.getBlue());
//                return Color.rgb(rgb.getRed(), rgb.getGreen(), rgb.getBlue());
            }
        }
        return Color.WHITE;
    }

    public static Paint.Align getAlign(Cell cell) {
        CellFormat cellFormat = cell.getCellFormat();
        if (cellFormat != null) {
            Alignment alignment = cellFormat.getAlignment();
            return alignment == Alignment.LEFT ? Paint.Align.LEFT :
                    alignment == Alignment.RIGHT ? Paint.Align.RIGHT
                            : Paint.Align.CENTER;
        }
        return Paint.Align.CENTER;
    }

}
