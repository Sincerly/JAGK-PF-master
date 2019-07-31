package com.ysxsoft.gkpf.bean;

import android.graphics.Paint;
import android.view.Gravity;

import org.apache.poi.hssf.usermodel.HSSFCell;

public class MyCell {

    private int row;    //所在行
    private int col;    //所在列
    private boolean IsCellRegion;//合并单元格需要绘制
    private int cellWidth;    //单元格宽度
    private int cellHeight;   //单元格高度
    private int ReginCol;   //合并的列数     0未合并
    private int ReginRow;   //合并的行数     0未合并
    private int indexX = 0;     //单元格X轴所在位置
    private int indexY = 0;     //单元格Y轴所在位置

    private Paint.Align align;      //对齐方式
    private int bgColor;//背景颜色
    private int fontColor;//字体颜色
    private short fontSize;//字体大小
    private boolean fontBold;//是否加粗
    private Object value;   //单元格内容

    private int viewWidth;//界面上控件的宽度（解决合并单元格问题）
    private int viewHeight;//界面上控件的高度（解决合并单元格问题）

    private HSSFCell hssfCell;  //单元格

    public int getCellWidth() {
        return cellWidth;
    }

    public void setCellWidth(int cellWidth) {
        this.cellWidth = cellWidth;
    }

    public int getCellHeight() {
        return cellHeight;
    }

    public void setCellHeight(int cellHeight) {
        this.cellHeight = cellHeight;
    }

    public int getReginCol() {
        return ReginCol;
    }

    public void setReginCol(int reginCol) {
        ReginCol = reginCol;
    }

    public int getReginRow() {
        return ReginRow;
    }

    public void setReginRow(int reginRow) {
        ReginRow = reginRow;
    }

    public int getIndexX() {
        return indexX;
    }

    public void setIndexX(int indexX) {
        this.indexX = indexX;
    }

    public int getIndexY() {
        return indexY;
    }

    public void setIndexY(int indexY) {
        this.indexY = indexY;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public int getFontColor() {
        return fontColor;
    }

    public void setFontColor(int fontColor) {
        this.fontColor = fontColor;
    }

    public short getFontSize() {
        return fontSize;
    }

    public void setFontSize(short fontSize) {
        this.fontSize = fontSize;
    }

    public boolean isFontBold() {
        return fontBold;
    }

    public void setFontBold(boolean fontBold) {
        this.fontBold = fontBold;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public HSSFCell getHssfCell() {
        return hssfCell;
    }

    public void setHssfCell(HSSFCell hssfCell) {
        this.hssfCell = hssfCell;
    }

    public boolean isCellRegion() {
        return IsCellRegion;
    }

    public void setCellRegion(boolean cellRegion) {
        IsCellRegion = cellRegion;
    }

    public int getAlign() {
        return align == Paint.Align.LEFT ? Gravity.LEFT :
                align == Paint.Align.LEFT ? Gravity.RIGHT
                        : Gravity.CENTER;
    }

    public void setAlign(Paint.Align align) {
        this.align = align;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getViewHeight() {
        return viewHeight;
    }

    public void setViewHeight(int viewHeight) {
        this.viewHeight = viewHeight;
    }

    public int getViewWidth() {
        return viewWidth;
    }

    public void setViewWidth(int viewWidth) {
        this.viewWidth = viewWidth;
    }

    @Override
    public String toString() {
        return "MyCell{" +
                "row=" + row +
                ", col=" + col +
                ", IsCellRegion=" + IsCellRegion +
                ", cellWidth=" + cellWidth +
                ", cellHeight=" + cellHeight +
                ", ReginCol=" + ReginCol +
                ", ReginRow=" + ReginRow +
                ", indexX=" + indexX +
                ", indexY=" + indexY +
                ", align=" + align +
                ", bgColor='" + bgColor + '\'' +
                ", fontColor='" + fontColor + '\'' +
                ", fontSize=" + fontSize +
                ", fontBold=" + fontBold +
                ", value=" + value +
                ", hssfCell=" + hssfCell +
                '}';
    }
}
