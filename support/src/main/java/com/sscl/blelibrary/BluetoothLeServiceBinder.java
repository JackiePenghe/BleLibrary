package com.sscl.blelibrary;

import android.os.Binder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Binder for BLE connection service
 *
 * @author jackie
 */
final class BluetoothLeServiceBinder extends Binder {

    /*-----------------------------------field variables-----------------------------------*/

    /**
     * BLE connection service
     */
    @Nullable
    private BluetoothLeService bluetoothLeService;

    /*-----------------------------------Constructor-----------------------------------*/

    /**
     * Constructor
     *
     * @param bluetoothLeService BLE connection service
     */
    BluetoothLeServiceBinder(@NonNull BluetoothLeService bluetoothLeService) {
        this.bluetoothLeService = bluetoothLeService;
    }

    /*-----------------------------------Package private method-----------------------------------*/

    /**
     * get BLE connection service
     *
     * @return BLE connection service
     */
    @Nullable
    BluetoothLeService getBluetoothLeService() {
        return bluetoothLeService;
    }

    /**
     * release data
     */
    void releaseData(){
        bluetoothLeService = null;
    }
}
