package com.shopsphere.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * Password hashing using PBKDF2 (salted, slow hash). Plain passwords are
 * never stored. Stored format:  pbkdf2$iterations$salt$hash  (Base64).
 */
public final class PasswordUtil {
    private static final int ITERATIONS = 65_536;
    private static final int KEY_BITS = 256;

    private PasswordUtil() {}

    public static String hash(String password) {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        byte[] h = pbkdf2(password.toCharArray(), salt, ITERATIONS);
        return "pbkdf2$" + ITERATIONS + "$" + Base64.getEncoder().encodeToString(salt)
                + "$" + Base64.getEncoder().encodeToString(h);
    }

    public static boolean verify(String password, String stored) {
        if (password == null || stored == null) return false;
        String[] p = stored.split("\\$");
        if (p.length != 4 || !"pbkdf2".equals(p[0])) return false;
        int iterations = Integer.parseInt(p[1]);
        byte[] salt = Base64.getDecoder().decode(p[2]);
        byte[] expected = Base64.getDecoder().decode(p[3]);
        byte[] actual = pbkdf2(password.toCharArray(), salt, iterations);
        // constant-time comparison
        int diff = expected.length ^ actual.length;
        for (int i = 0; i < expected.length && i < actual.length; i++) diff |= expected[i] ^ actual[i];
        return diff == 0;
    }

    private static byte[] pbkdf2(char[] pw, byte[] salt, int iterations) {
        try {
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return f.generateSecret(new PBEKeySpec(pw, salt, iterations, KEY_BITS)).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Password hashing failed", e);
        }
    }

    /** Small helper: prints a hash, used to generate seed data. */
    public static void main(String[] args) {
        for (String a : args) System.out.println(a + " -> " + hash(a));
    }
}
