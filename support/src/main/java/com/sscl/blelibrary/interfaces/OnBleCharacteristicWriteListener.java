package com.sscl.blelibrary.interfaces;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 * callback triggered when gatt characteristic write data successful
 *
 * @author jackie
 */
public interface OnBleCharacteristicWriteListener {

    /**
     * gatt characteristic write data successful
     *
     * @param gattCharacteristic BluetoothGattCharacteristic
     * @param data               data
     */
    void onBleCharacteristicWrite(BluetoothGattCharacteristic gattCharacteristic, byte[] data);
}
