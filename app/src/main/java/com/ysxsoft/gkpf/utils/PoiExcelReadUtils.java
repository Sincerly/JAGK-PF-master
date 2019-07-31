package com.ysxsoft.gkpf.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;

import com.ysxsoft.gkpf.bean.MyCell;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import jxl.Cell;
import jxl.format.CellFormat;
import jxl.format.Colour;
import jxl.format.Font;
import jxl.format.RGB;

/**
 * Poi方式获取Excel文件内容
 * TODO
 * 获取单元格文字和背景颜色不能用
 */
public class PoiExcelReadUtils {
    public static HSSFSheet sheet;

    public static HashMap<Integer, ArrayList<MyCell>> readExcelCell(Context context, String fileName, int position) throws Exception {
        HashMap<Integer, ArrayList<MyCell>> excelBean = new HashMap<>();    //创建元数据
        try {
            HSSFWorkbook workbook = new HSSFWorkbook(getInputStream(context, fileName)); // 获整个Excel
            if (workbook.getSheetAt(position) != null) {
                sheet = workbook.getSheetAt(position);// 获得不为空的这个sheet
                int tableWidth = getTableWidth(sheet);

                if (sheet != null) {
                    int firstRowNum = sheet.getFirstRowNum(); // 第一行
                    int lastRowNum = sheet.getLastRowNum(); // 最后一行
                    // 构造Table
                    for (int rowNum = firstRowNum; rowNum <= lastRowNum; rowNum++) {
                        if (sheet.getRow(rowNum) != null) {// 如果行不为空，
                            HSSFRow row = sheet.getRow(rowNum);
                            short firstCellNum = row.getFirstCellNum(); // 该行的第一个单元格
                            short lastCellNum = row.getLastCellNum(); // 该行的最后一个单元格
                            ArrayList<MyCell> tempRow = new ArrayList<>();  //存放行数据
                            for (short cellNum = firstCellNum; cellNum <= lastCellNum; cellNum++) { // 循环该行的每一个单元格
                                HSSFCell cell = row.getCell(cellNum);
                                if (cell != null) {
                                    HSSFCellStyle cellStyle = cell.getCellStyle();

                                    /**
                                     * TODO 这种获取颜色方法报错，缺少Java的java.awt.Color文件，暂未找到好的解决办法
                                     * HSSFPalette palette = workbook.getCustomPalette(); //类HSSFPalette用于求颜色的国际标准形式
                                     * HSSFColor hColor2 = palette.getColor(cellStyle.getFont(workbook).getColor());// 字体颜色
                                     * String bgColor = convertToStardColor(palette.getColor(cellStyle.getFillForegroundColor()));//背景颜色
                                     */

                                    boolean boldWeight = cellStyle.getFont(workbook).getBold(); // 字体粗细
                                    short fontHeight = (short) (cellStyle.getFont(workbook).getFontHeight() / 2); // 字体大小
                                    //封装实体类
                                    MyCell myCell = new MyCell();
                                    myCell.setCellRegion(IsCellRegion(sheet, rowNum, cellNum));   //是否在合并单元格内
                                    myCell.setCellHeight((int) (row.getHeightInPoints() * 2.5));// 行的高度
                                    myCell.setCellWidth((int) (sheet.getColumnWidthInPixels(cellNum) * tableWidth / 1772)); //单元格宽度
                                    myCell.setReginCol(getMergerCellRegionCol(sheet, rowNum, cellNum));// 合并的列（solspan）
                                    myCell.setReginRow(getMergerCellRegionRow(sheet, rowNum, cellNum));// 合并的行（rowspan）
//                                  myCell.setBgColor(bgColor);     //背景颜色
//                                  myCell.setFontColor(); //字体颜色
                                    myCell.setAlign(getAlign(cell));
                                    myCell.setFontBold(boldWeight); //字体加粗
                                    myCell.setFontSize(fontHeight); //字体大小
                                    myCell.setValue(getCellValue(cell));    //单元格内容
                                    myCell.setHssfCell(cell);       //原始数据备用
                                    myCell.setRow(rowNum);      //所在行
                                    myCell.setCol(cellNum);     //所在列
                                    tempRow.add(myCell);
//                                    }
                                }
                            }
                            Logutils.e(tempRow.toString());
                            excelBean.put(rowNum, tempRow);
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new Exception(e);
        } catch (IOException e) {
            throw new Exception(e);
        }
        return excelBean;
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
     * 取得单元格的值
     *
     * @param cell
     * @return
     * @throws IOException
     */
    public static Object getCellValue(HSSFCell cell) throws IOException {
        Object value = "";
        if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
            value = cell.getRichStringCellValue().toString();
        } else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                Date date = cell.getDateCellValue();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                value = sdf.format(date);
            } else {
                double value_temp = cell.getNumericCellValue();
                if (value_temp == (int) value_temp) {
                    value = (int) value_temp;
                } else {
                    value = value_temp;
                }
            }
        }
        if (cell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
            value = "";
        }
        return value;
    }

    /**
     * 是否在合并单元格内，此单元格是否绘制
     *
     * @param sheet
     * @param cellRow
     * @param cellCol
     * @return
     */
    public static boolean IsCellRegion(HSSFSheet sheet, int cellRow, int cellCol) {
        try {
            if (getMergerCellRegionCol(sheet, cellRow, cellCol) > 0 && getMergerCellRegionRow(sheet, cellRow, cellCol) > 0) {
                HSSFCell cell = sheet.getRow(cellRow).getCell(cellCol);
                if (cell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 判断单元格在不在合并单元格范围内，如果是，获取其合并的列数。
     *
     * @param sheet   工作表
     * @param cellRow 被判断的单元格的行号
     * @param cellCol 被判断的单元格的列号
     * @return
     * @throws IOException
     */
    public static int getMergerCellRegionCol(HSSFSheet sheet, int cellRow, int cellCol) throws IOException {
        int retVal = 0;
        int sheetMergerCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergerCount; i++) {
            CellRangeAddress cra = sheet.getMergedRegion(i);
            int firstRow = cra.getFirstRow();  // 合并单元格CELL起始行
            int firstCol = cra.getFirstColumn(); // 合并单元格CELL起始列
            int lastRow = cra.getLastRow(); // 合并单元格CELL结束行
            int lastCol = cra.getLastColumn(); // 合并单元格CELL结束列
            if (cellRow >= firstRow && cellRow <= lastRow) { // 判断该单元格是否是在合并单元格中
                if (cellCol >= firstCol && cellCol <= lastCol) {
                    retVal = lastCol - firstCol + 1; // 得到合并的列数
                    break;
                }
            }
        }
        return retVal;
    }

    /**
     * 判断单元格是否是合并的单格，如果是，获取其合并的行数。
     *
     * @param sheet   表单
     * @param cellRow 被判断的单元格的行号
     * @param cellCol 被判断的单元格的列号
     * @return
     * @throws IOException
     */
    public static int getMergerCellRegionRow(HSSFSheet sheet, int cellRow, int cellCol) throws IOException {
        int retVal = 0;
        int sheetMergerCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergerCount; i++) {
            CellRangeAddress cra = sheet.getMergedRegion(i);
            int firstRow = cra.getFirstRow();  // 合并单元格CELL起始行
            int firstCol = cra.getFirstColumn(); // 合并单元格CELL起始列
            int lastRow = cra.getLastRow(); // 合并单元格CELL结束行
            int lastCol = cra.getLastColumn(); // 合并单元格CELL结束列
            if (cellRow >= firstRow && cellRow <= lastRow) { // 判断该单元格是否是在合并单元格中
                if (cellCol >= firstCol && cellCol <= lastCol) {
                    retVal = lastRow - firstRow + 1; // 得到合并的行数
                    break;
                }
            }
        }
        return retVal;
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

    /**
     * 单元格背景色转换
     *
     * @param hc
     * @return
     */
    public static String convertToStardColor(HSSFColor hc) {
        StringBuffer sb = new StringBuffer("");
        if (hc != null) {
            int a = HSSFColor.AUTOMATIC.index;
            int b = hc.getIndex();
            if (a == b) {
                return null;
            }
            sb.append("#");
            for (int i = 0; i < hc.getTriplet().length; i++) {
                String str;
                String str_tmp = Integer.toHexString(hc.getTriplet()[i]);
                if (str_tmp != null && str_tmp.length() < 2) {
                    str = "0" + str_tmp;
                } else {
                    str = str_tmp;
                }
                sb.append(str);
            }
        }
        return sb.toString();
    }

    /**
     * 获取表格总宽度
     *
     * @param sheet
     * @return
     */
    public static int getTableWidth(HSSFSheet sheet) {
        if (sheet != null) {
            int firstRowNum = sheet.getFirstRowNum(); // 第一行
            int totalWidth = 0; // 总长度
            // 构造Table
            for (int rowNum = firstRowNum; rowNum <= firstRowNum + 1; rowNum++) {
                if (sheet.getRow(rowNum) != null) {// 如果行不为空，
                    HSSFRow row = sheet.getRow(rowNum);
                    short firstCellNum = row.getFirstCellNum(); // 该行的第一个单元格
                    short lastCellNum = row.getLastCellNum(); // 该行的最后一个单元格
                    for (short cellNum = firstCellNum; cellNum <= lastCellNum; cellNum++) { // 循环该行的每一个单元格
                        HSSFCell cell = row.getCell(cellNum);
                        if (cell != null) {
                            totalWidth += sheet.getColumnWidthInPixels(cellNum); //单元格宽度
                        }
                    }
                }
            }
            return totalWidth;
        }
        return 0;
    }

    /**
     * 获取表格总宽度
     *
     * @param sheet
     * @return
     */
    public static int getTableHeight(HSSFSheet sheet) {
        if (sheet != null) {
            int firstRowNum = sheet.getFirstRowNum(); // 第一行
            int lastRowNum = sheet.getLastRowNum(); // 最后一行
            int totalHeight = 0; // 总长度
            // 构造Table
            for (int rowNum = firstRowNum; rowNum <= lastRowNum; rowNum++) {
                if (sheet.getRow(rowNum) != null) {// 如果行不为空，
                    HSSFRow row = sheet.getRow(rowNum);
                    short firstCellNum = row.getFirstCellNum(); // 该行的第一个单元格
                    for (short cellNum = firstCellNum; cellNum < firstCellNum + 1; cellNum++) { // 循环该行的每一个单元格
                        HSSFCell cell = row.getCell(cellNum);
                        if (cell != null) {
                            totalHeight += (float) (row.getHeightInPoints() * 2.5); //单元格高度
                        }
                    }
                }
            }
            return totalHeight;
        }
        return 0;
    }

    /**
     * 单元格对齐格式
     *
     * @param cell
     * @return
     */
    public static Paint.Align getAlign(HSSFCell cell) {
        HorizontalAlignment alignment = cell.getCellStyle().getAlignmentEnum();
        return alignment == HorizontalAlignment.LEFT ? Paint.Align.LEFT :
                alignment == HorizontalAlignment.RIGHT ? Paint.Align.RIGHT
                        : Paint.Align.CENTER;
    }

}
