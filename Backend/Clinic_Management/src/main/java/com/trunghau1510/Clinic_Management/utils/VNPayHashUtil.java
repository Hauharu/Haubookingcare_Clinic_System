package com.trunghau1510.Clinic_Management.utils;

import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class VNPayHashUtil {

    private static Map<String, String> extractCleanParams(Map<String, String> params) {
        Map<String, String> cleanParams = new TreeMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            // Bỏ qua vnp_SecureHash và vnp_SecureHashType
            if ("vnp_SecureHash".equals(key) || "vnp_SecureHashType".equals(key)) {
                continue;
            }

            // Chỉ thêm parameters có giá trị không null và không rỗng
            if (value != null && !value.isEmpty()) {
                cleanParams.put(key, value);
            }
        }
        return cleanParams;
    }

    public static String createVNPayHash(Map<String, String> params, String secretKey) {
        Map<String, String> cleanParams = extractCleanParams(params);
        StringBuilder hashData = new StringBuilder();
        Iterator<Map.Entry<String, String>> iter = cleanParams.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = iter.next();
            hashData.append(entry.getKey());
            hashData.append("=");
            try {
                String encodedValue = URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8);
                hashData.append(encodedValue);
            } catch (Exception e) {
                hashData.append(entry.getValue());
            }
            if (iter.hasNext()) {
                hashData.append("&");
            }
        }
        String queryString = hashData.toString();
        return hmacSHA512(secretKey, queryString);
    }

    private static String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKey);

            byte[] hashBytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // Convert byte array to hex string (lowercase)
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public static boolean verifyVNPaySignature(Map<String, String> params, String receivedHash, String secretKey) {
        if (receivedHash == null || receivedHash.trim().isEmpty()) {
            return false;
        }

        // Tạo copy của params và loại bỏ hash fields
        Map<String, String> verifyParams = new HashMap<>(params);
        verifyParams.remove("vnp_SecureHash");
        verifyParams.remove("vnp_SecureHashType");

        String calculatedHash = createVNPayHash(verifyParams, secretKey);
        return calculatedHash.equals(receivedHash.trim());
    }
}
