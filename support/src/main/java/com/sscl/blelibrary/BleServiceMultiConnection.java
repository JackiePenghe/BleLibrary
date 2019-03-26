package com.sscl.blelibrary;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


/**
 * Connect multiple BLE device service connection
 *
 * @author jackie
 */

public final class BleServiceMultiConnection implements ServiceConnection {

    /*-----------------------------------static constant-----------------------------------*/

    /**
     * TAG
     */
    private static final String TAG = BleServiceMultiConnection.class.getSimpleName();

    /*-----------------------------------field variables-----------------------------------*/

    /**
     * BleMultiConnector
     */
    @Nullable
    private BleMultiConnector bleMultiConnector;

    /*-----------------------------------Constructor-----------------------------------*/

    /**
     * Constructor
     *
     * @param bleMultiConnector BleMultiConnector
     */
     BleServiceMultiConnection(@NonNull BleMultiConnector bleMultiConnector) {
        this.bleMultiConnector = bleMultiConnector;
    }

    /*-----------------------------------Override method-----------------------------------*/

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

        if (iBinder == null) {
            return;
        }
        if (bleMultiConnector == null) {
            return;
        }
        if (iBinder instanceof BluetoothMultiServiceBinder) {
            bleMultiConnector.setBluetoothMultiService(((BluetoothMultiServiceBinder) iBinder).getBluetoothMultiService());
            if (bleMultiConnector.getBluetoothMultiService().initialize()) {
                DebugUtil.warnOut(TAG, "Bluetooth multi-connection service initialization completed");
                bleMultiConnector.getBluetoothMultiService().setInitializeFinished();
            } else {
                DebugUtil.warnOut(TAG, "Bluetooth multi-connection service initialization failed");
            }
        }
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
        if (bleMultiConnector == null) {
            return;
        }
        bleMultiConnector.setBluetoothMultiService(null);
        bleMultiConnector = null;

    }


}
