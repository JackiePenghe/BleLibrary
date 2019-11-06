package com.sscl.blelibrary.enums;

import android.bluetooth.le.ScanSettings;
import android.os.Build;

import androidx.annotation.RequiresApi;

/**
 * enum of BLE match Mode
 *
 * @author jackie
 */
@RequiresApi(Build.VERSION_CODES.M)
public enum BleMatchMode {

    /**
     * For sticky mode, higher threshold of signal strength and sightings is required
     * before reporting by hw
     */
    STICKY(ScanSettings.MATCH_MODE_STICKY),
    /**
     * In Aggressive mode, hw will determine a match sooner even with feeble signal strength
     * and few number of sightings/match in a duration.
     */
    AGGRESSIVE(ScanSettings.MATCH_MODE_AGGRESSIVE);

    private int matchMode;

    BleMatchMode(int matchMode) {
        this.matchMode = matchMode;
    }

    public int getMatchMode() {
        return matchMode;
    }
}
