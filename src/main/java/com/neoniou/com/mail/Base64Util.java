package com.neoniou.com.mail;

import java.util.Arrays;
import java.util.Base64;

/**
 * @author Neo.Zzj
 */
public class Base64Util {

    /**
     * Encode content
     * @param code content
     * @return encoded content
     */
    public static String encode(String code) {
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(code.getBytes());
    }

    /**
     * Decode content
     * @param code content
     * @return decoded content
     */
    public static String decode(String code) {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] bytes = decoder.decode(code);
        return Arrays.toString(bytes);
    }
}
