package com.gmail.holubvojtech.snakes;

public class Utils {

    public static String exception(Throwable t) {
        StackTraceElement[] trace = t.getStackTrace();
        return t.getClass().getSimpleName() + " : " + t.getMessage() +
                (trace.length > 0 ?
                        (" @ " + t.getStackTrace()[0].getClassName() + ":" + t.getStackTrace()[0].getLineNumber()) : "");
    }
}
