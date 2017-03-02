package com.zhy.smail.lcp.util;
/**
 * 字节处理类
 *
 * @since 2008-11-08
 * @version 1.0
 */
public class Octet {
	/**
	 * 获取第1个字节(从低字节算起)
	 * @param i 整数
	 * @return 第1个字节
	 */
	public static byte getFirstByte(int i) {
		return (byte)((i & 0x000000FF) >> 0);
	}

	/**
	 * 获取第1个字节(从低字节算起)
	 * @param s 短整数
	 * @return 第1个字节
	 */
	public static byte getFirstByte(short s) {
		return (byte)((s & 0x00FF) >> 0);
	}

	/**
	 * 获取第2个字节(从低字节算起)
	 * @param i 整数
	 * @return 第2个字节
	 */
	public static byte getSecondByte(int i) {
		return (byte)((i & 0x0000FF00) >> 8);
	}

	/**
	 * 获取第2个字节(从低字节算起)
	 * @param s 短整数
	 * @return 第2个字节
	 */
	public static byte getSecondByte(short s) {
		return (byte)((s & 0xFF00) >> 8);
	}

	/**
	 * 获取第3个字节(从低字节算起)
	 * @param i 整数
	 * @return 第3个字节
	 */
	public static byte getThirdByte(int i) {
		return (byte)((i & 0x00FF0000) >> 16);
	}

	/**
	 * 获取第4个字节(从低字节算起)
	 * @param i 整数
	 * @return 第4个字节
	 */
	public static byte getFourthByte(int i) {
		return (byte)((i & 0xFF000000) >> 24);
	}

	/**
	 * 获取第1个短整数(从低字节算起)
	 * @param i 整数
	 * @return 第1个短整数的值
	 */
	public static short getFirstShort(int i) {
		return (short)(i >> 0);
	}

	/**
	 * 获取第1个短整数(从低字节算起)
	 * @param i 整数
	 * @return 第1个短整数的值
	 */
	public static short getSecondShort(int i) {
		return (short)(i >> 16) ;
	}

	/**
	 * 转化为字节数组
	 * @param i 整数
	 * @return 字节数组(低字节先行)
	 */
	public static byte[] toData(int i) {
		byte[] data = new byte[4];
		data[0] = getFirstByte(i);
		data[1] = getSecondByte(i);
		data[2] = getThirdByte(i);
		data[3] = getFourthByte(i);
		return data;
	}

	/**
	 * 转化为字节数组
	 * @param s 短整数
	 * @return 字节数组(低字节先行)
	 */
	public static byte[] toData(short s) {
		byte[] data = new byte[2];
		data[0] = getFirstByte(s);
		data[1] = getSecondByte(s);
		return data;
	}

	/**
	 * 转化为字节数组
	 * @param s 短整数
	 * @return 字节数组(DAS高低位不互换)
	 */
	public static byte[] toData_Das(short s) {
		byte[] data = new byte[2];
		data[0] = getSecondByte(s);
		data[1] = getFirstByte(s);
		return data;
	}

	/**
	 * 转化为字节数组
	 * @param b 字节
	 * @return 字节数组(低字节先行)
	 */
	public static byte[] toData(byte b) {
		byte[] data = new byte[1];
		data[0] = b;
		return data;
	}

	/**
	 * 合并4个字节为整数
	 * @param first 第1个字节(从低字节算起)
	 * @param second 第2个字节
	 * @param third 第3个字节
	 * @param fourth 第4个字节
	 * @return 整数
	 */
	public static int toInt(byte first, byte second, byte third, byte fourth) {
		int byte1 = (first & 0xFF) << 0;
		int byte2 = (second  & 0xFF) << 8;
		int byte3 = (third & 0xFF) << 16;
		int byte4 = (fourth & 0xFF) << 24;
		return (byte1 | byte2 | byte3 | byte4);
	}

	/**
	 * 合并2个字节为短整数
	 * @param first 第1个字节(从低字节算起)
	 * @param second 第2个字节
	 * @return 短整数
	 */
	public static short toShort(byte first, byte second) {
		int byte1 = (first & 0xFF) << 0;
		int byte2 = (second & 0xFF) << 8;
		return (short)(byte1 | byte2);
	}

	/**
	 * 合并字节数组为整数
	 * @param data 字节数组(低字节先行)
	 * @return 整数
	 */
	public static int toInt(byte[] data) {
		return toInt(data[0], data[1], data[2], data[3]);
	}

	/**
	 * 合并字节数组为短整数
	 * @param data 字节数组(低字节先行)
	 * @return 短整数
	 */
	public static short toShort(byte[] data) {
		return toShort(data[0], data[1]);
	}

	/**
	 * 合并字节数组为短整数
	 * @param data 字节数组(DAS，高低位不互换)
	 * @return 短整数
	 */
	public static short toShort_Das(byte[] data) {
		return toShort(data[1], data[0]);
	}

	/**
	 * 合并字节数组为字节
	 * @param data 字节数组(低字节先行)
	 * @return 字节
	 */
	public static byte toByte(byte[] data) {
		return data[0];
	}

}