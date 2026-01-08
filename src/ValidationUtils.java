package util;

public class ValidationUtils {
    public static boolean isEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    public static boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }
}
