package com.sscl.blelibrary;

import android.util.Log;

/**
 * Debug tool class
 *
 * @author jackie
 */
public class DebugUtil {

    /*-----------------------------------private static variables-----------------------------------*/

    /**
     * default TAG
     */
    private static String defaultTag = "AlmLibrary->";
    /**
     * Whether to print debug information
     */
    private static boolean debugFlag = false;

    /*-----------------------------------private static method-----------------------------------*/

    /**
     * Get debug flag
     *
     * @return debug flag
     */
    @SuppressWarnings("unused")
    public static boolean isDebug() {
        return debugFlag;
    }

    /**
     * set debug flag
     *
     * @param debug debug flag
     */
    public static void setDebugFlag(boolean debug) {
        debugFlag = debug;
    }

    /**
     * set default TAG
     *
     * @param tag default TAG
     */
    @SuppressWarnings("unused")
    public static void setDefaultTAG(String tag) {
        DebugUtil.defaultTag = tag;
    }

    /**
     * Log.i
     *
     * @param tag     tag
     * @param message message
     */
    @SuppressWarnings("WeakerAccess")
    public static void infoOut(String tag, String message) {
        if (!debugFlag) {
            return;
        }
        Log.i(defaultTag + tag, message);
    }

    /**
     * Log.i
     *
     * @param message message
     */
    @SuppressWarnings("unused")
    public static void infoOut(String message) {
        infoOut(defaultTag, message);
    }

    /**
     * Log.e
     *
     * @param tag     tag
     * @param message message
     */
    @SuppressWarnings("WeakerAccess")
    public static void errorOut(String tag, String message) {
        if (!debugFlag) {
            return;
        }
        Log.e(defaultTag + tag, message);
    }

    /**
     * Log.e
     *
     * @param message message
     */
    @SuppressWarnings("unused")
    public static void errorOut(String message) {
        errorOut(defaultTag, message);
    }

    /**
     * Log.d
     *
     * @param tag     tag
     * @param message message
     */
    @SuppressWarnings("WeakerAccess")
    public static void debugOut(String tag, String message) {
        if (!debugFlag) {
            return;
        }
        Log.d(defaultTag + tag, message);
    }

    /**
     * Log.d
     *
     * @param message message
     */
    @SuppressWarnings("unused")
    public static void debugOut(String message) {
        debugOut(defaultTag, message);
    }

    /**
     * Log.w
     *
     * @param tag     tag
     * @param message message
     */
    public static void warnOut(String tag, String message) {
        if (!debugFlag) {
            return;
        }
        Log.w(defaultTag + tag, message);
    }

    /**
     * Log.w
     *
     * @param message message
     */
    public static void warnOut(String message) {
        warnOut(defaultTag, message);
    }


    /**
     * Log.v
     *
     * @param tag     tag
     * @param message message
     */
    @SuppressWarnings("WeakerAccess")
    public static void verOut(String tag, String message) {
        if (!debugFlag) {
            return;
        }
        Log.v(defaultTag + tag, message);
    }

    /**
     * Log.v
     *
     * @param message message
     */
    @SuppressWarnings("unused")
    public static void verOut(String message) {
        verOut(defaultTag, message);
    }
}
