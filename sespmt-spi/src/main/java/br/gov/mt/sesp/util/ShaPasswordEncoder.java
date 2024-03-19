package br.gov.mt.sesp.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ShaPasswordEncoder {

    private static String createDigestString(byte fileDigest[]) {

        StringBuffer checksumSb = new StringBuffer();

        for (int i = 0; i < fileDigest.length; i++) {
            String hexStr = Integer.toHexString(0xff & fileDigest[i]);

            if (hexStr.length() < 2) {
                checksumSb.append("0");
            }

            checksumSb.append(hexStr);
        }

        return checksumSb.toString();
    }

    public String encode(CharSequence rawPassword) {
        String encodedPasswordHash;
        MessageDigest md = null;
        try {
            md = java.security.MessageDigest.getInstance("SHA");
            byte[] passwordDigest = md.digest(rawPassword.toString().getBytes());
            encodedPasswordHash = createDigestString(passwordDigest);
        } catch (NoSuchAlgorithmException e) {
            encodedPasswordHash = null;
        }

        return encodedPasswordHash;
    }

    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (encodedPassword == null || encodedPassword.length() == 0) {
            return false;
        }

        return encodedPassword.equals(this.encode(rawPassword));
    }
}
