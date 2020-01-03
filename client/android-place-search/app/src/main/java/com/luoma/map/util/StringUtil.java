package com.luoma.map.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringUtil {
    public static String MD5(String text) {

        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(text.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            System.exit(-1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(
                        Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return md5StrBuff.toString();

    }

    public static String getRandString() {
        String rtn = "";
        for (int i = 0; i < 10; i++) {
            rtn = MD5(rtn + Math.random());
        }
        return rtn;
    }

    public static String salt = "LTDLasdhkasdkljakjdhiuwqq123123b1,ndasbklascabdiq12@@12LNKJNSAKJDASDKA";
}