package com.example.project.config;

import jakarta.servlet.http.HttpServletRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PaymentConfig {
    public static String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static String vnp_ReturnUrl = "https://doanshop.onrender.com/vnpay_return";
    public static String vnp_TmnCode = "UCS4MJPH";
    public static String secretKey = "47OUNEHAYP642KVH5Z9A3NRRXDPIFENZ";
    public static String vnp_Version = "2.1.0";
    public static String vnp_Command = "pay";
    public static String vnp_ApiUrl = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";

    public static String hashAllFields(final Map fields) {
        final List fieldNames = new ArrayList(fields.keySet());
        Collections.sort(fieldNames);
        final StringBuilder sb = new StringBuilder();
        final Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            final String fieldName = (String) itr.next();
            final String fieldValue = (String) fields.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                sb.append(fieldName);
                sb.append("=");
                sb.append(fieldValue);
            }
            if (itr.hasNext()) {
                sb.append("&");
            }
        }
        return hmacSHA512(secretKey, sb.toString());
    }

    public static String hmacSHA512(final String key, final String data) {
        try {

            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            final byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            final byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            final byte[] result = hmac512.doFinal(dataBytes);
            final StringBuilder sb = new StringBuilder(2 * result.length);
            for (final byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (final Exception ex) {
            return "";
        }
    }

    public static String getIpAddress(final HttpServletRequest request) {
        String ipAdress;
        try {
            ipAdress = request.getHeader("X-FORWARDED-FOR");
            if (ipAdress == null) {
                ipAdress = request.getRemoteAddr();
            }
        } catch (final Exception e) {
            ipAdress = "Invalid IP:" + e.getMessage();
        }
        return ipAdress;
    }


    public static String getRandomNumber(final int len) {
        final Random rnd = new Random();
        final String chars = "0123456789";
        final StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }


}