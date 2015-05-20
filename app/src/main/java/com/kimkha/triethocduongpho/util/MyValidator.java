package com.kimkha.triethocduongpho.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Log;

import java.security.MessageDigest;

/**
 * @author kimkha
 * @version 1.3
 * @since 5/20/15
 */
public class MyValidator {
    private static String fingerprint = "";
    private static String packageName = "";

    public static void init(Context context) {
        try {
            context = context.getApplicationContext();
            PackageManager pm = context.getPackageManager();
            Signature sig = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES).signatures[0];
            fingerprint = doFingerprint(sig.toByteArray(), "SHA1").toLowerCase();
            packageName = context.getPackageName().toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getCertificate(Long timestamp) {
        try {
            return doSHA1(String.format("%s;%s;%d", fingerprint, packageName, timestamp));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String doFingerprint(byte[] certificateBytes, String algorithm) throws Exception {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        md.update(certificateBytes);
        byte[] digest = md.digest();

        String toRet = "";
        for (int i = 0; i < digest.length; i++) {
            if (i != 0)
                toRet += ":";
            int b = digest[i] & 0xff;
            String hex = Integer.toHexString(b);
            if (hex.length() == 1)
                toRet += "0";
            toRet += hex;
        }
        return toRet;
    }

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    private static String doSHA1(String text) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }
}
