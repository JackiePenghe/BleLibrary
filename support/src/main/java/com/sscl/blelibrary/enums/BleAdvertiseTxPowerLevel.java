package com.sscl.blelibrary.enums;

import android.bluetooth.le.AdvertiseSettings;
import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * enum of BLE eAdvertise Tx Power Level
 *
 * @author jackie
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
public enum BleAdvertiseTxPowerLevel {

    /**
     * Advertise using the lowest transmission (TX) power level. Low transmission power can be used
     * to restrict the visibility range of advertising packets.
     */
    ULTRA_LOW(AdvertiseSettings.ADVERTISE_TX_POWER_ULTRA_LOW),
    /**
     * Advertise using low TX power level.
     */
    LOW(AdvertiseSettings.ADVERTISE_TX_POWER_LOW),
    /**
     * Advertise using medium TX power level.
     */
    MEDIUM(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM),
    /**
     * Advertise using high TX power level. This corresponds to largest visibility range of the
     * advertising packet.
     */
    HIGH(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)

    ;
    private int value;

    BleAdvertiseTxPowerLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
