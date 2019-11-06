package com.sscl.blelibrary;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;


/**
 * Connection callback for BLE connection service
 *
 * @author jackie
 */
public final class BleServiceConnection implements ServiceConnection {

    /*-----------------------------------static constant-----------------------------------*/

    /**
     * TAG
     */
    private static final String TAG = BleServiceConnection.class.getSimpleName();


    /*-----------------------------------Constructor-----------------------------------*/

    BleServiceConnection() {

    }

    /*------------------------implementation method----------------------------*/

    /**
     * Called when a connection to the Service has been established, with
     * the {@link IBinder} of the communication channel to the
     * Service.
     *
     * @param name    The concrete component name of the service that has
     *                been connected.
     * @param iBinder The IBinder of the Service's communication channel,
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder iBinder) {
        BluetoothLeServiceBinder bluetoothLeServiceBinder = (BluetoothLeServiceBinder) iBinder;
        BluetoothLeService bluetoothLeService = bluetoothLeServiceBinder.getBluetoothLeService();
        if (bluetoothLeService == null) {
            DebugUtil.warnOut(TAG, "bluetoothLeService is null.");
            return;
        }
        if (!bluetoothLeService.initialize()) {
            DebugUtil.warnOut(TAG, "bluetoothLeService initialize failed!");
            return;
        }
        BleManager.setBluetoothLeService(bluetoothLeService);
    }

    /**
     * Called when a connection to the Service has been lost.  This typically
     * happens when the process hosting the service has crashed or been killed.
     * This does <em>not</em> remove the ServiceConnection itself -- this
     * binding to the service will remain active, and you will receive a call
     * to {@link #onServiceConnected} when the Service is next running.
     *
     * @param name The concrete component name of the service whose
     *             connection has been lost.
     */
    @Override
    public void onServiceDisconnected(ComponentName name) {
        BleManager.setBluetoothLeService(null);
    }

}
