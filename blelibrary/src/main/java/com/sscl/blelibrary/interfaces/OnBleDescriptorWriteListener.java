package com.sscl.blelibrary.interfaces;

import android.bluetooth.BluetoothGattDescriptor;

/**
 * callback triggered when gatt descriptor write successful
 *
 * @author jackie
 */
public interface OnBleDescriptorWriteListener {

    /**
     * descriptor data write successful
     *
     * @param bluetoothGattDescriptor BluetoothGattDescriptor
     * @param data                    descriptor
     */
    void onBleDescriptorWrite(BluetoothGattDescriptor bluetoothGattDescriptor, byte[] data);
}
