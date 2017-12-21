package com.gmail.holubvojtech.snakes;

import java.util.regex.Pattern;

public class Utils {

    public static final Pattern USERNAME_PATTERN = Pattern.compile("([a-zA-Z0-9_\\-])+");

    public static String exception(Throwable t) {
        StackTraceElement[] trace = t.getStackTrace();
        return t.getClass().getSimpleName() + " : " + t.getMessage() +
                (trace.length > 0 ?
                        (" @ " + t.getStackTrace()[0].getClassName() + ":" + t.getStackTrace()[0].getLineNumber()) : "");
    }

    public static boolean validateUsername(String username) {
        return !username.isEmpty() && username.length() <= 10 && USERNAME_PATTERN.matcher(username).matches();
    }
}
