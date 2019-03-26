package com.sscl.blelibrary.interfaces;

/**
 * Bluetooth status changed listener
 *
 * @author jackie
 */
public interface OnBluetoothStateChangedListener {

    /**
     * Bluetooth is enabling
     */
    void onBluetoothEnabling();

    /**
     * Bluetooth is enable
     */
    void onBluetoothEnable();

    /**
     * Bluetooth is disabling
     */
    void onBluetoothDisabling();

    /**
     * Bluetooth is disable
     */
    void onBluetoothDisable();
}