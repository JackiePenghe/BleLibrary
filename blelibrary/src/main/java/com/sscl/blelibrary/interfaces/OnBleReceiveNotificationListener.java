package com.sscl.blelibrary.interfaces;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 * callback triggered when gatt received notification data
 *
 * @author jackie
 */
public interface OnBleReceiveNotificationListener {

    /**
     * received remote device data
     *
     * @param gattCharacteristic BluetoothGattCharacteristic
     * @param data               received data
     */
    void onBleReceiveNotification(BluetoothGattCharacteristic gattCharacteristic, byte[] data);
}
