package hm.orz.chaos114.android.carnavimodoki.util;

import android.util.Log;

import java.util.regex.Pattern;

import hm.orz.chaos114.android.carnavimodoki.BuildConfig;

public final class LogUtil {
    private LogUtil() {
    }

    public static void d(String message) {
        if (BuildConfig.DEBUG) {
            Log.d(getTag(), message);
        }
    }

    public static void e(String message, Exception e) {
        if (BuildConfig.DEBUG) {
            Log.e(getTag(), message, e);
        }
    }

    public static void debugMethod() {
        if (BuildConfig.DEBUG) {
            Log.d(getTag(), "");
        }
    }

    private static String getTag() {
        final StackTraceElement trace = Thread.currentThread().getStackTrace()[4];
        final String cla = trace.getClassName();
        Pattern pattern = Pattern.compile("[\\.]+");
        final String[] splitStr = pattern.split(cla);
        final String simpleClass = splitStr[splitStr.length - 1];
        final String method = trace.getMethodName();
        final int line = trace.getLineNumber();
        return simpleClass + "#" + method + ":" + line;
    }
}
