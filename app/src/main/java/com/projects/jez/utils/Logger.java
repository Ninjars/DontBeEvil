package com.projects.jez.utils;

import android.util.Log;

import com.projects.jez.dontbeevil.DebugConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jez on 05/10/2016.
 */

public class Logger {
    
    private static boolean DLOG = DebugConfig.ENABLE_DEBUG_LOGGING;
    private static List<Object> registeredLoggables = new ArrayList<>();

    public static void register(Class obj) {
        registeredLoggables.add(obj);
    }

    public static void registerAll(Class[] activeLoggers) {
        registeredLoggables.addAll(Arrays.asList(activeLoggers));
    }
    
    public static void v(Object obj, String message) {
        if (DLOG) {
            String optionalTag = getTagIfValid(obj);
            if (optionalTag != null) {
                Log.v(optionalTag, message);
            }
        }
    }

    public static void d(Object obj, String message) {
        if (DLOG) {
            String optionalTag = getTagIfValid(obj);
            if (optionalTag != null) {
                Log.d(optionalTag, message);
            }
        }
    }

    public static void i(Object obj, String message) {
        if (DLOG) {
            String optionalTag = getTagIfValid(obj);
            if (optionalTag != null) {
                Log.i(optionalTag, message);
            }
        }
    }

    public static void w(Object obj, String message) {
        if (DLOG) {
            String optionalTag = getTagIfValid(obj);
            if (optionalTag != null) {
                Log.w(optionalTag, message);
            }
        }
    }

    public static void w(Object obj, String message, Exception e) {
        if (DLOG) {
            String optionalTag = getTagIfValid(obj);
            if (optionalTag != null) {
                Log.w(optionalTag, message, e);
            }
        }
    }

    public static void e(Object obj, String message) {
        if (DLOG) {
            String optionalTag = getTagIfValid(obj);
            if (optionalTag != null) {
                Log.e(optionalTag, message);
            }
        }
    }

    public static void e(Object obj, String message, Exception e) {
        if (DLOG) {
            String optionalTag = getTagIfValid(obj);
            if (optionalTag != null) {
                Log.e(optionalTag, message, e);
            }
        }
    }
    
    private static String getTagIfValid(Object obj) {
        Class c = obj.getClass();
        if (registeredLoggables.contains(c)) {
            return c.getSimpleName();
        }
        return null;
    }
}
