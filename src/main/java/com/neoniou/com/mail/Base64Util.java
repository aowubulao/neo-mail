package com.neoniou.com.mail;

import java.util.Arrays;
import java.util.Base64;

/**
 * @author Neo.Zzj
 */
public class Base64Util {

    /**
     * Encode content
     *
     * @param code content
     * @return encoded content
     */
    public static String encode(String code) {
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(code.getBytes());
    }
}
