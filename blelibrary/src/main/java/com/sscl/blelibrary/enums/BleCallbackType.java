package com.sscl.blelibrary.enums;

import android.bluetooth.le.ScanSettings;
import android.os.Build;

import androidx.annotation.RequiresApi;


/**
 * @author jackie
 */

public enum BleCallbackType {
    /**
     * Trigger a callback for every Bluetooth advertisement found that matches the filter criteria.
     * If no filter is active, all advertisement packets are reported.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    CALLBACK_TYPE_ALL_MATCHES(ScanSettings.CALLBACK_TYPE_ALL_MATCHES),

    /**
     * A result callback is only triggered for the first advertisement packet received that matches
     * the filter criteria.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    CALLBACK_TYPE_FIRST_MATCH(ScanSettings.CALLBACK_TYPE_FIRST_MATCH),

    /**
     * Receive a callback when advertisements are no longer received from a device that has been
     * previously reported by a first match callback.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    CALLBACK_TYPE_MATCH_LOST(ScanSettings.CALLBACK_TYPE_MATCH_LOST);

    private int value;

    BleCallbackType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
