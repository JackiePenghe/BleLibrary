package com.sscl.blelibrary;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


/**
 * Broadcast receiver detecting Bluetooth switch status
 *
 * @author jackie
 */
final class BleScannerBluetoothStateReceiver extends BroadcastReceiver {

    /*-----------------------------------field variables-----------------------------------*/

    /**
     * BLE scanner
     */
    @Nullable
    private BleScanner bleScanner;

    /*-----------------------------------Constructor-----------------------------------*/

    /**
     * Constructor
     *
     * @param bleScanner BleScanner
     */
    BleScannerBluetoothStateReceiver(@NonNull BleScanner bleScanner) {
        this.bleScanner = bleScanner;
    }

    /*-----------------------------------Override method-----------------------------------*/

    /**
     * This method is called when the BroadcastReceiver is receiving an Intent
     * broadcast.  During this time you can use the other methods on
     * BroadcastReceiver to view/modify the current result values.  This method
     * is always called within the main thread of its process, unless you
     * explicitly asked for it to be scheduled on a different thread using
     * {@link Context#registerReceiver(BroadcastReceiver, * IntentFilter , String, Handler)}. When it runs on the main
     * thread you should
     * never perform long-running operations in it (there is a timeout of
     * 10 seconds that the system allows before considering the receiver to
     * be blocked and a candidate to be killed). You cannot launch a popup dialog
     * in your implementation of onReceive().
     * <p>
     * <p><b>If this BroadcastReceiver was launched through a &lt;receiver&gt; tag,
     * then the object is no longer alive after returning from this
     * function.</b>  This means you should not perform any operations that
     * return a result to you asynchronously -- in particular, for interacting
     * with services, you should use
     * {@link Context#startService(Intent)} instead of
     * {@link Context#bindService(Intent, ServiceConnection, int)}.  If you wish
     * to interact with a service that is already running, you can use
     * {@link #peekService}.
     * <p>
     * <p>The Intent filters used in {@link Context#registerReceiver}
     * and in application manifests are <em>not</em> guaranteed to be exclusive. They
     * are hints to the operating system about how to find suitable recipients. It is
     * possible for senders to force delivery to specific recipients, bypassing filter
     * resolution.  For this reason, {@link #onReceive(Context, Intent) onReceive()}
     * implementations should respond only to known actions, ignoring any unexpected
     * Intents that they may receive.
     *
     * @param context The Context in which the receiver is running.
     * @param intent  The Intent being received.
     */
    @SuppressWarnings({"JavadocReference", "JavaDoc"})
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        switch (action) {
            case BluetoothAdapter.ACTION_STATE_CHANGED:
                int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                switch (bluetoothState) {
                    case BluetoothAdapter.STATE_OFF:
                        if (bleScanner != null) {
                            bleScanner.stopScan();
                        }
                        break;
                    case BluetoothAdapter.STATE_ON:
                        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
                        if (bluetoothManager == null) {
                            return;
                        }
                        if (bleScanner != null) {
                            bleScanner.setBluetoothAdapter(bluetoothManager.getAdapter());
                        }
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    /*-----------------------------------package private method-----------------------------------*/

    /**
     * Free memory
     */
    void releaseData() {
        bleScanner = null;
    }
}
