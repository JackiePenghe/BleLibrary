package com.sscl.blelibrary;

import android.bluetooth.BluetoothAdapter;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;

import com.sscl.blelibrary.exception.WrongByteArrayLengthException;
import com.sscl.blelibrary.exception.WrongNumberException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Data conversion tool
 *
 * @author jackie
 */
public class ConversionUtil {

    /*-----------------------------------static constant-----------------------------------*/

    /**
     * Length of device address
     */
    private static final int ADDRESS_BYTE_LENGTH = 6;

    /*-----------------------------------public static method-----------------------------------*/

    /**
     * Convert a byte array to an int
     *
     * @param bytes byte array.(array length must be less than or equal to 4)
     * @return int value
     */
    public static int bytesToInt(@NonNull byte[] bytes) {
        byte cache0;
        byte cache1;
        byte cache2;
        byte cache3;

        int value0;
        int value1;
        int value2;
        int value3;
        int length = bytes.length;
        switch (length) {
            case 1:
                cache0 = bytes[0];
                return 0x00FF & cache0;
            case 2:
                cache0 = bytes[0];
                cache1 = bytes[1];
                value0 = cache0 << 8;
                value1 = cache1;
                return value0 | value1;
            case 3:
                cache0 = bytes[0];
                cache1 = bytes[1];
                cache2 = bytes[2];
                value0 = cache0 << 16;
                value1 = cache1 << 8;
                value2 = cache2;
                return value0 | value1 | value2;
            case 4:
                cache0 = bytes[0];
                cache1 = bytes[1];
                cache2 = bytes[2];
                cache3 = bytes[3];
                value0 = cache0 << 24;
                value1 = cache1 << 16;
                value2 = cache2 << 8;
                value3 = cache3;
                return value0 | value1 | value2 | value3;
            default:
                throw new WrongByteArrayLengthException("byte array length must be less than 4");
        }
    }

    /**
     * Convert byte array to hex string
     *
     * @param bytes byte array
     * @return String  hex string(Separate each hexadecimal number with a space)
     */
    @SuppressWarnings("WeakerAccess")
    public static String bytesToHexStr(byte[] bytes) {
        String stmp;
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            stmp = Integer.toHexString(aByte & 0xFF);
            sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
            sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }

    /**
     * Convert an int number to a 2-bytes byte array
     *
     * @param i int number
     */
    public static byte[] shortToBytes2(short i) {
        String hexString = Integer.toHexString(getUnsignedShort(i));
        byte highByte;
        byte lowByte;
        int length = hexString.length();
        switch (length) {
            case 1:
            case 2:
                highByte = 0;
                lowByte = (byte) Integer.parseInt(hexString, 16);
                break;
            case 3:
            case 4:
                String substring = hexString.substring(0, length - 2);
                highByte = (byte) Integer.parseInt(substring, 16);
                substring = hexString.substring(length - 2, length);
                lowByte = (byte) Integer.parseInt(substring, 16);
                break;
            default:
                return null;
        }
        return new byte[]{highByte, lowByte};


    }

    /**
     * 将字节型数据转换为0~65535 (0xFFFF 即 WORD)
     *
     * @param data 字节型数据
     * @return 无符号的整型
     */
    private static int getUnsignedShort(short data) {
        return data & 0x0FFFF;
    }

    /**
     * Convert Bluetooth device address to byte array
     *
     * @param address device address
     * @return byte array
     */
    public static byte[] bluetoothAddressStringToByteArray(String address) {
        if (address == null) {
            return null;
        }

        if (!BluetoothAdapter.checkBluetoothAddress(address)) {
            return null;
        }

        String[] cacheArray = address.split(":", 6);
        byte[] bluetoothByteArray = new byte[6];

        for (int i = 0; i < cacheArray.length; i++) {
            String cache = cacheArray[i];
            Integer integer;
            try {
                integer = Integer.valueOf(cache, 16);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return null;
            }
            bluetoothByteArray[i] = integer.byteValue();
        }
        return bluetoothByteArray;
    }

    /**
     * Convert device address array to device address string
     *
     * @param addressByteArray Device address array
     * @return Device address string（AA:AA:AA:AA:AA:AA）
     */
    public static String bluetoothAddressByteArrayToString(byte[] addressByteArray) {
        if (addressByteArray == null) {
            return null;
        }
        if (addressByteArray.length != ADDRESS_BYTE_LENGTH) {
            return null;
        }

        String addressCacheString = bytesToHexStr(addressByteArray);
        String addressCache = addressCacheString.replace(" ", ":");
        return addressCache.toUpperCase();
    }

    /**
     * Get unsigned byte number
     *
     * @param data byte number
     * @return int value
     */
    public static int getUnsignedByte(byte data) {
        return data & 0x0FF;
    }

    /**
     * Get unsigned int number
     *
     * @param data int number
     * @return long value
     */
    public static long getUnsignedInt(int data) {
        //Get the lowest bit
        int lowBit = (byte) (0b1 & data);
        //Unsigned right shift one bit (unsigned number)
        int i = data >>> 1;
        //Turn the number after the right shift to long and then move it back to the left.
        long l = (long) i << 1;
        //Re-add the lower value
        return l + lowBit;
    }

    /**
     * Convert an int number to a 4-byte byte array
     *
     * @param n int
     * @return 4-byte byte array
     */
    public static byte[] intToBytesLength4(int n) {
        byte[] b = new byte[4];
        for (int i = 0; i < b.length; ++i) {
            b[i] = (byte) (n >>> 24 - i * 8);
        }
        return b;
    }

    /**
     * byte数组转为long类型
     *
     * @param bytes byte数组
     * @return long类型数据
     */
    public static long bytesToLong(@Size(max = 6) @NonNull byte[] bytes) {
        int length = bytes.length;

        long result = 0;

        for (int i = 0; i < length; i++) {
            long cache = bytes[i];
            cache = cache << (length - i + 1);
            result = cache + result;
        }
        return result;
    }

    /**
     * 将长整形转为byte数组
     *
     * @param value 长整形
     * @return byte数组
     */
    @SuppressWarnings("WeakerAccess")
    public static byte[] longToBytes(long value) {

        String hexString = Long.toHexString(value);
        int length = hexString.length();
        if (length % 2 == 0) {
            byte[] bytes = new byte[length / 2];
            for (int i = 0; i < bytes.length; i++) {
                String cacheString = hexString.substring(i * 2, i * 2 + 2);
                short cache = Short.parseShort(cacheString, 16);
                bytes[i] = (byte) cache;
            }
            return bytes;
        } else {
            byte[] bytes = new byte[length / 2 + 1];
            String substring = hexString.substring(0, 1);
            bytes[0] = (byte) Short.parseShort(substring, 16);
            hexString = hexString.substring(1);
            for (int i = 0; i < bytes.length - 1; i++) {
                String cacheString = hexString.substring(i * 2, i * 2 + 2);
                short cache = Short.parseShort(cacheString, 16);
                bytes[i + 1] = (byte) cache;
            }
            return bytes;
        }
    }

    /**
     * 将boolean转为int(true = 1,false = 0)
     *
     * @param b boolean值
     *          *
     * @return 对应的int值
     */
    @SuppressWarnings("unused")
    public static int booleanToInt(boolean b) {
        if (b) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * 将任意对象转为byte数组
     *
     * @param o 任意对象
     * @return byte数组
     */
    public static byte[] objectToByteArray(Object o) {
        if (!(o instanceof Serializable) && !(o instanceof Parcelable)) {
            return null;
        }
        byte[] bytes = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(o);
            bytes = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * 将int转为boolean(0 = false ,1 = true)
     *
     * @param value int值
     *              *
     * @return 对应的结果
     */
    public static boolean intToBoolean(int value) {
        switch (value) {
            case 0:
                return false;
            case 1:
                return true;
            default:
                throw new RuntimeException("The error value " + value);
        }
    }

    /**
     * 将数组类型转为指定的对象
     *
     * @param bytes 数组类
     * @return T 指定对象
     */
    @Nullable
    public static <T> T byteArrayToObject(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        Object o = null;
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            o = objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (objectInputStream != null) {
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            byteArrayInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //noinspection unchecked
        return (T) o;
    }

    /**
     * 将一个int型转为对应的位为1的数
     *
     * @param value 原始数
     * @return 转换后的位的数
     */
    public static int intToBitNumBytes(int value) {
        if (value < 1 || value > 32) {
            throw new WrongNumberException("value must be between 1-32");
        }
        long cache = 1;
        return (int) (cache << (32 - value));
    }

    /**
     * 将一个int型的Ip地址转为点分式地址字符串
     *
     * @param ip int型的Ip地址
     * @return 点分式字符串
     */
    public static String intIp4ToStringIp4(int ip) {
        return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + ((ip >> 24) & 0xFF);
    }

    @SuppressWarnings("WeakerAccess")
    public static boolean ipV4IsValid(String ipv4) {
        String ipV4Pattern = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";
        Pattern pattern = Pattern.compile(ipV4Pattern);
        Matcher matcher = pattern.matcher(ipv4);
        return matcher.matches();
    }

    @Nullable
    public static byte[] stringIp4ToByteArray(String serverIp) {
        if (!ipV4IsValid(serverIp)) {
            return null;
        }
        String[] split = serverIp.split("\\.");
        byte[] bytes = new byte[4];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (int) Integer.valueOf(split[i]);
        }
        return bytes;
    }

    /**
     * 字符串转换成byte数组（数组长度最长为bytesLength）
     *
     * @param s           要转换成byte[]的字符串
     * @param bytesLength 数组长度的最大值（数组长度超过该值会被截取，长度不足该值为数组原长度）
     * @return 转换后获得的byte[]
     */
    @SuppressWarnings("WeakerAccess")
    public static byte[] getBytes(String s, int bytesLength) {
        return getBytes(s, Charset.defaultCharset(), bytesLength);
    }

    /**
     * 字符串转换成byte数组（数组长度最长为bytesLength）
     *
     * @param s           要转换成byte[]的字符串
     * @param charsetName 编码方式的名字
     * @param bytesLength 数组长度的最大值（数组长度超过该值会被截取，长度不足该值为数组原长度）
     * @return 转换后获得的byte[]
     * @throws UnsupportedCharsetException 不支持的编码类型
     */
    @SuppressWarnings("WeakerAccess")
    public static byte[] getBytes(String s, String charsetName, int bytesLength) throws UnsupportedCharsetException {
        Charset charset = Charset.forName(charsetName);
        return getBytes(s, charset, bytesLength);
    }

    /**
     * 字符串转换成byte数组（数组长度最长为bytesLength）
     *
     * @param s           要转换成byte[]的字符串
     * @param charset     编码方式
     * @param bytesLength 数组长度的最大值（数组长度超过该值会被截取，长度不足该值为数组原长度）
     * @return 转换后获得的byte[]
     */
    @SuppressWarnings("WeakerAccess")
    public static byte[] getBytes(String s, Charset charset, int bytesLength) {
        if (s == null) {
            return null;
        }
        if (bytesLength < 0) {
            throw new UnsupportedOperationException("bytesLength cannot be negative");
        }
        byte[] data;
        if (bytesLength > 0) {
            if (s.length() > bytesLength) {
                data = new byte[bytesLength];
                System.arraycopy(s.getBytes(charset), 0, data, 0, bytesLength);
            } else {
                data = s.getBytes(charset);
            }
        } else {
            data = s.getBytes(charset);
        }
        return data;
    }

    /**
     * 字符串转换成byte数组，自动判断中文简体语言环境，在中文简体下，自动以GBK方式转换（数组长度最长为bytesLength）
     *
     * @param s           要转换成byte[]的字符串
     * @param bytesLength 数组长度的最大值,0为不截取（数组长度超过该值会被截取，长度不足该值为数组原长度）
     * @return 转换后获得的byte[]
     */
    @SuppressWarnings("unused")
    public static byte[] getBytesAutoGBK(String s, int bytesLength) {
        if (isZhCN()) {
            return getBytes(s, "GBK", bytesLength);
        } else {
            return getBytes(s, bytesLength);
        }
    }

    /**
     * 检测系统环境是否是中文简体
     *
     * @return true表示为中文简体
     */
    @SuppressWarnings("WeakerAccess")
    public static boolean isZhCN() {
        Locale aDefault = Locale.getDefault();
        String aDefaultStr = aDefault.toString();
        String zhCn = "zh_CN";
        return zhCn.equals(aDefaultStr);
    }

    public static byte[] getGroupAreaBytes(long groupAreaNum) {
        byte[] cache = ConversionUtil.longToBytes(groupAreaNum);
        byte[] result = new byte[6];
        if (cache.length < result.length) {
            int index = 6 - cache.length;
            System.arraycopy(cache, 0, result, index, cache.length);
        } else {
            System.arraycopy(cache, cache.length - result.length, result, 0, result.length);
        }
        return result;
    }
}
