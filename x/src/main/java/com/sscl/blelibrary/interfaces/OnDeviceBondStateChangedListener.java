package com.sscl.blelibrary.interfaces;

/**
 * BLE Bluetooth device binding state changed listener
 *
 * @author jackie
 */
public interface OnDeviceBondStateChangedListener {
    /**
     * device is binding
     */
    void onDeviceBinding();

    /**
     * device has been bound
     */
    void onDeviceBonded();

    /**
     * bind canceled or bind failed
     */
    void onDeviceBindNone();
}
