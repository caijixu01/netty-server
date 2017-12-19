package com.cjx.server.util;

import java.util.Arrays;

public class HexUtil {
    public static String bytes2hex(byte[] bytes, int length) {
        byte[] copyOf = Arrays.copyOf(bytes, length);
        String bytes2hex = bytes2hex(copyOf);
        return bytes2hex;
    }
    
    public static String bytes2hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        String tmp = null;
        for (byte b : bytes) {
            tmp = Integer.toHexString(0xFF & b);
            if (tmp.length() == 1)
            {
                tmp = "0" + tmp;
            }
            sb.append(tmp + " ");
        }

        return sb.toString();
    }
    
}
