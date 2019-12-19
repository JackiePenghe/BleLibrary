package com.sscl.blelibrary.enums;

import android.bluetooth.le.AdvertiseSettings;
import android.os.Build;

import androidx.annotation.RequiresApi;

/**
 * enum of BLE eAdvertise Mode
 *
 * @author jackie
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
public enum BleAdvertiseMode {

    /**
     * Perform Bluetooth LE advertising in low power mode.This is the default and preferred
     * advertising mode as it consumes the least power.
     */
    LOW_POWER(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER),
    /**
     * Perform Bluetooth LE advertising in balanced power mode. This is balanced between advertising
     * frequency and power consumption.
     */
    BALANCED(AdvertiseSettings.ADVERTISE_MODE_BALANCED),
    /**
     * Perform Bluetooth LE advertising in low latency, high power mode. This has the highest power
     * to restrict the visibility range of advertising packets.
     */
    LOW_LATENCY(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);

    private int value;

    BleAdvertiseMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
