package com.neoniou.com.mail;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author Neo.Zzj
 */
public class Base64Util {

    public static String encode(String code) {
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(code.getBytes());
    }

    public static String decode(String code) {
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] bytes = new byte[0];
        try {
            bytes = decoder.decodeBuffer(code);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Arrays.toString(bytes);
    }
}
