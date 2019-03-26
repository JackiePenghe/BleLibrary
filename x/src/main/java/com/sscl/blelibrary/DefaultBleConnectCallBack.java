package com.sscl.blelibrary;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;


/**
 * Default connect callback for multi-connection
 *
 * @author jackie
 */

final class DefaultBleConnectCallBack extends BaseBleConnectCallback {

    /*-----------------------------------static constant-----------------------------------*/

    /**
     * TAG
     */
    private static final String TAG = DefaultBleConnectCallBack.class.getSimpleName();

    /*-----------------------------------implementation method-----------------------------------*/

    @Override
    public void onServicesAutoDiscoverFailed(BluetoothGatt gatt) {
        DebugUtil.warnOut(TAG, "onServicesAutoDiscoverFailed");
    }

    @Override
    public void onGattClosed(BluetoothDevice address) {
        DebugUtil.warnOut(TAG, "onGattClosed");
    }

    @Override
    public void onBluetoothGattOptionsNotSuccess(BluetoothGatt gatt, String methodName, int errorStatus) {
        DebugUtil.warnOut(TAG, "onBluetoothGattOptionsNotSuccess");
    }

    @Override
    public void onUnknownState(BluetoothGatt gatt, int state) {
        DebugUtil.warnOut(TAG, "onUnknownState");
    }

    @Override
    public void onConnectTimeOut(BluetoothGatt gatt) {
        DebugUtil.warnOut(TAG, "onConnectTimeOut");
    }
}
