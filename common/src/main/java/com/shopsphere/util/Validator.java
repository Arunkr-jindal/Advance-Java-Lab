package com.shopsphere.util;

import java.util.regex.Pattern;

/** Server-side input validation helpers (never trust browser-side checks). */
public final class Validator {
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern MOBILE = Pattern.compile("^[6-9][0-9]{9}$");
    private static final Pattern PINCODE = Pattern.compile("^[1-9][0-9]{5}$");

    private Validator() {}

    public static boolean isBlank(String s) { return s == null || s.isBlank(); }
    public static boolean isEmail(String s) { return s != null && s.length() <= 150 && EMAIL.matcher(s).matches(); }
    public static boolean isMobile(String s) { return s != null && MOBILE.matcher(s).matches(); }
    public static boolean isPincode(String s) { return s != null && PINCODE.matcher(s).matches(); }

    /** At least 8 characters with a letter and a digit. */
    public static boolean isStrongPassword(String s) {
        return s != null && s.length() >= 8 && s.length() <= 100
                && s.chars().anyMatch(Character::isLetter) && s.chars().anyMatch(Character::isDigit);
    }

    /** Parses an int, returning the default when the text is missing or invalid. */
    public static int toInt(String s, int def) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return def; }
    }

    public static Integer toInteger(String s) {
        try { return Integer.valueOf(s.trim()); } catch (Exception e) { return null; }
    }

    public static Double toDouble(String s) {
        try { return Double.valueOf(s.trim()); } catch (Exception e) { return null; }
    }
}
