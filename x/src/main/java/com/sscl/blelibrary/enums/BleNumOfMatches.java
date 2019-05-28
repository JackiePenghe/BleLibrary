package com.sscl.blelibrary.enums;

import android.bluetooth.le.ScanSettings;
import android.os.Build;

import androidx.annotation.RequiresApi;

/**
 * BLE num if matches
 * <p>
 * Determines how many advertisements to match per filter, as this is scarce hw resource
 *
 * @author jackie
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public enum BleNumOfMatches {

    /**
     * Match one advertisement per filter
     */
    MATCH_NUM_ONE_ADVERTISEMENT(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT),

    /**
     * Match few advertisement per filter, depends on current capability and availibility of
     * the resources in hw
     */
    MATCH_NUM_FEW_ADVERTISEMENT(ScanSettings.MATCH_NUM_FEW_ADVERTISEMENT),

    /**
     * Match as many advertisement per filter as hw could allow, depends on current
     * capability and availibility of the resources in hw
     */
    MATCH_NUM_MAX_ADVERTISEMENT(ScanSettings.MATCH_NUM_MAX_ADVERTISEMENT);

    private int value;

    BleNumOfMatches(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
