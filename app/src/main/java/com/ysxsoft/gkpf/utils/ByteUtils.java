package com.ysxsoft.gkpf.utils;

import com.google.gson.Gson;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;

public class ByteUtils {
    public static String byteArrToHex(byte[] inBytArr) {
        StringBuilder strBuilder = new StringBuilder();
        int j = inBytArr.length;
        for (int i = 0; i < j; i++) {
            strBuilder.append(byte2Hex(inBytArr[i]));
            strBuilder.append(" ");
        }
        return strBuilder.toString();
    }

    /**
     * 1字节转2个Hex字符
     *
     * @param inByte
     * @return
     */
    public static String byte2Hex(Byte inByte) {
        return String.format("%02x", inByte).toUpperCase();
    }

    // 以下 是整型数 和 网络字节序的  byte[] 数组之间的转换
    public static byte[] longToBytes(long n) {
        byte[] b = new byte[8];
        b[7] = (byte) (n & 0xff);
        b[6] = (byte) (n >> 8  & 0xff);
        b[5] = (byte) (n >> 16 & 0xff);
        b[4] = (byte) (n >> 24 & 0xff);
        b[3] = (byte) (n >> 32 & 0xff);
        b[2] = (byte) (n >> 40 & 0xff);
        b[1] = (byte) (n >> 48 & 0xff);
        b[0] = (byte) (n >> 56 & 0xff);
        return b;
    }

    public static void longToBytes( long n, byte[] array, int offset ){
        array[7+offset] = (byte) (n & 0xff);
        array[6+offset] = (byte) (n >> 8 & 0xff);
        array[5+offset] = (byte) (n >> 16 & 0xff);
        array[4+offset] = (byte) (n >> 24 & 0xff);
        array[3+offset] = (byte) (n >> 32 & 0xff);
        array[2+offset] = (byte) (n >> 40 & 0xff);
        array[1+offset] = (byte) (n >> 48 & 0xff);
        array[0+offset] = (byte) (n >> 56 & 0xff);
    }

    public static long bytesToLong( byte[] array )
    {
        return ((((long) array[ 0] & 0xff) << 56)
                | (((long) array[ 1] & 0xff) << 48)
                | (((long) array[ 2] & 0xff) << 40)
                | (((long) array[ 3] & 0xff) << 32)
                | (((long) array[ 4] & 0xff) << 24)
                | (((long) array[ 5] & 0xff) << 16)
                | (((long) array[ 6] & 0xff) << 8)
                | (((long) array[ 7] & 0xff) << 0));
    }

    public static long bytesToLong( byte[] array, int offset )
    {
        return ((((long) array[offset + 0] & 0xff) << 56)
                | (((long) array[offset + 1] & 0xff) << 48)
                | (((long) array[offset + 2] & 0xff) << 40)
                | (((long) array[offset + 3] & 0xff) << 32)
                | (((long) array[offset + 4] & 0xff) << 24)
                | (((long) array[offset + 5] & 0xff) << 16)
                | (((long) array[offset + 6] & 0xff) << 8)
                | (((long) array[offset + 7] & 0xff) << 0));
    }

    public static byte[] intToBytes(int n) {
        byte[] b = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);
        return b;
    }

    public static void intToBytes( int n, byte[] array, int offset ){
        array[3+offset] = (byte) (n & 0xff);
        array[2+offset] = (byte) (n >> 8 & 0xff);
        array[1+offset] = (byte) (n >> 16 & 0xff);
        array[offset] = (byte) (n >> 24 & 0xff);
    }

    public static int bytesToInt(byte b[]) {
        return    b[3] & 0xff
                | (b[2] & 0xff) << 8
                | (b[1] & 0xff) << 16
                | (b[0] & 0xff) << 24;
    }

    public static int bytesToInt(byte b[], int offset) {
        return    b[offset+3] & 0xff
                | (b[offset+2] & 0xff) << 8
                | (b[offset+1] & 0xff) << 16
                | (b[offset] & 0xff) << 24;
    }

    public static byte[] uintToBytes( long n )
    {
        byte[] b = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);

        return b;
    }

    public static void uintToBytes( long n, byte[] array, int offset ){
        array[3+offset] = (byte) (n );
        array[2+offset] = (byte) (n >> 8 & 0xff);
        array[1+offset] = (byte) (n >> 16 & 0xff);
        array[offset]   = (byte) (n >> 24 & 0xff);
    }

    public static long bytesToUint(byte[] array) {
        return ((long) (array[3] & 0xff))
                | ((long) (array[2] & 0xff)) << 8
                | ((long) (array[1] & 0xff)) << 16
                | ((long) (array[0] & 0xff)) << 24;
    }

    public static long bytesToUint(byte[] array, int offset) {
        return ((long) (array[offset+3] & 0xff))
                | ((long) (array[offset+2] & 0xff)) << 8
                | ((long) (array[offset+1] & 0xff)) << 16
                | ((long) (array[offset]   & 0xff)) << 24;
    }

    public static byte[] shortToBytes(short n) {
        byte[] b = new byte[2];
        b[1] = (byte) ( n       & 0xff);
        b[0] = (byte) ((n >> 8) & 0xff);
        return b;
    }

    public static void shortToBytes(short n, byte[] array, int offset ) {
        array[offset+1] = (byte) ( n       & 0xff);
        array[offset] = (byte) ((n >> 8) & 0xff);
    }

    public static short bytesToShort(byte[] b){
        return (short)( b[1] & 0xff
                |(b[0] & 0xff) << 8 );
    }

    public static short bytesToShort(byte[] b, int offset){
        return (short)( b[offset+1] & 0xff
                |(b[offset]    & 0xff) << 8 );
    }

    public static byte[] ushortToBytes(int n) {
        byte[] b = new byte[2];
        b[1] = (byte) ( n       & 0xff);
        b[0] = (byte) ((n >> 8) & 0xff);
        return b;
    }

    public static void ushortToBytes(int n, byte[] array, int offset ) {
        array[offset+1] = (byte) ( n       & 0xff);
        array[offset] = (byte)   ((n >> 8) & 0xff);
    }

    public static int bytesToUshort(byte b[]) {
        return    b[1] & 0xff
                | (b[0] & 0xff) << 8;
    }

    public static int bytesToUshort(byte b[], int offset) {
        return    b[offset+1] & 0xff
                | (b[offset]   & 0xff) << 8;
    }

    public static byte[] ubyteToBytes( int n ){
        byte[] b = new byte[1];
        b[0] = (byte) (n & 0xff);
        return b;
    }

    public static void ubyteToBytes( int n, byte[] array, int offset ){
        array[0] = (byte) (n & 0xff);
    }

    public static int bytesToUbyte( byte[] array ){
        return array[0] & 0xff;
    }

    public static int bytesToUbyte( byte[] array, int offset ){
        return array[offset] & 0xff;
    }
    // char 类型、 float、double 类型和 byte[] 数组之间的转换关系还需继续研究实现。

    ///////////////////////////////////////////////////////////////////////////
    // 转换方法
    ///////////////////////////////////////////////////////////////////////////
    /*整型转化成字符序列,类似的函数baidu可以找到很多
     *执行位操作，将int i = 0x12345678; 对应二进制：
     *内存中存放：低地址
     *00010010 0x12
     *00110100 0x34
     *01010110 0x56
     *01111000 0x78
     *高地址
     */
// 存放到byte[]={'0x12','0x34','0x56','0x78'}位置处
    public static byte[] intToBytes(int i, int length) {
//bytes[0] = 00010010
//bytes[1] = 00110100
//bytes[2] = 01010110
//bytes[3] = 01111000
        int start = length - 1;
        byte bytes[] = new byte[length];
        for (int j = start; j >= 0; j--)
            bytes[start - j] = (byte) (i >> 8 * (start - j) & 0xff); //bytes[0] ：直接将i的低8bits和0xff与操作即可，此时j=3
        return bytes;
    }

    public byte[] shortToBytes2(short s) {
        byte bytes[] = new byte[2];
        bytes[0] = (byte) (0xFF & (s >> 8)); //低地址存放高位数据，低位数据被右移8bits后删除了。(返回高8位值)
        bytes[1] = (byte) (0xFF & s); //高地址存放低位数据，位操作当然是低位执行的与操作。当前只和8bits与，(即返回低8bits值)
        return bytes;
    }

    /**
     * 4字节数组转换为int
     * 13转换为 [13,0,0,0]
     *
     * @param byteNum
     * @return
     */
    public static int fourBytes2Int(byte[] byteNum) {
        int num = 0;
        for (int ix = 0; ix < 4; ++ix) {
            num <<= 8;
            num |= (byteNum[ix] & 0xff);
        }
        return num;
    }

    /**
     * 2字节数组转换为int
     * 2转换为 [2,0]
     *
     * @param buffer
     * @return
     */
    public static int twoBytes2Int(byte[] buffer) {
        return buffer[0] | buffer[1] << 8;
    }

    public static void main(String[] args) {
        byte[] twoBytes = shortToBytes((short) 11);//2字节数组
        byte[] fourBytes = intToBytes(13, 4);//4字节数组
        System.out.println("报文类型:" + new Gson().toJson(twoBytes));
        System.out.println("报文数据长度:" + new Gson().toJson(fourBytes));

        System.out.println("解析报文类型:" + twoBytes2Int(twoBytes));
        System.out.println("解析报文数据长度:" + fourBytes2Int(fourBytes));


        int x = 2;
        ByteBuffer bb = ByteBuffer.wrap(new byte[4]);
        bb.asIntBuffer().put(x);
        String ss_before = Arrays.toString(bb.array());
        System.out.println("默认字节序 " + bb.order().toString() + "," + " 内存数据 " + ss_before);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.asIntBuffer().put(x);
        String ss_after = Arrays.toString(bb.array());
        System.out.println("修改字节序 " + bb.order().toString() + "," + " 内存数据 " + ss_after);
        byte[] body = "呵呵呵".getBytes(Charset.defaultCharset());

        String s=new String(body);
        System.out.println("s" +s);
        System.out.println("s" + ByteUtils.byte2Hex((byte) 11));
    }
}
