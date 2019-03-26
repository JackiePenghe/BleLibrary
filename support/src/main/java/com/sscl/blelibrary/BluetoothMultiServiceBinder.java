package com.sscl.blelibrary;

import android.os.Binder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;



/**
 * Binder for BLE Multi-Connection Service
 *
 * @author alm
 */

 final class BluetoothMultiServiceBinder extends Binder {

    /*-----------------------------------static constant-----------------------------------*/

    /**
     * TAG
     */
    private static final String TAG = BluetoothMultiServiceBinder.class.getSimpleName();

    /*-----------------------------------field variables-----------------------------------*/

    /**
     * BLE multi-connection service
     */
    @Nullable
    private BluetoothMultiService bluetoothMultiService;

    /*-----------------------------------Constructor-----------------------------------*/

    /**
     * Constructor
     *
     * @param bluetoothMultiService BLE multi-connection service
     */
    BluetoothMultiServiceBinder(@NonNull BluetoothMultiService bluetoothMultiService) {
        this.bluetoothMultiService = bluetoothMultiService;
    }

    /*-----------------------------------public method-----------------------------------*/

    /**
     * get Bluetooth Multi Service
     *
     * @return BluetoothMultiService
     */
    BluetoothMultiService getBluetoothMultiService() {
        DebugUtil.warnOut(TAG, "BLE Multi-Connection Service bind success");
        return bluetoothMultiService;
    }

    void releaseData() {
        bluetoothMultiService = null;
    }

}
