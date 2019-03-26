package com.sscl.blelibrary;

import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.sscl.blelibrary.interfaces.OnBluetoothStateChangedListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;

/**
 * Broadcast receiver listening to Bluetooth enabled status
 *
 * @author jackie
 */
public final class BluetoothStateReceiver extends BroadcastReceiver {

    /*-----------------------------------static constant-----------------------------------*/

    private ArrayList<OnBluetoothStateChangedListener> onBluetoothStateChangedListeners = new ArrayList<>();

    /*-----------------------------------Implementation method-----------------------------------*/

    /**
     * This method is called when the BroadcastReceiver is receiving an Intent
     * broadcast.  During this time you can use the other methods on
     * BroadcastReceiver to view/modify the current result values.  This method
     * is always called within the main thread of its process, unless you
     * explicitly asked for it to be scheduled on a different thread using
     * {@link Context#registerReceiver(BroadcastReceiver, * IntentFilter, String, Handler)}. When it runs on the main
     * thread you should
     * never perform long-running operations in it (there is a timeout of
     * 10 seconds that the system allows before considering the receiver to
     * be blocked and a candidate to be killed). You cannot launch a popup dialog
     * in your implementation of onReceive().
     * <p>
     * <p><b>If this BroadcastReceiver was launched through a &lt;receiver&gt; tag,
     * then the object is no longer alive after returning from this
     * function.</b> This means you should not perform any operations that
     * return a result to you asynchronously. If you need to perform any follow up
     * background work, schedule a {@link JobService} with
     * {@link JobScheduler}.
     * <p>
     * If you wish to interact with a service that is already running and previously
     * bound using {@link Context#bindService(Intent, ServiceConnection, int) bindService()},
     * you can use {@link #peekService}.
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
                        performBluetoothStateOffListener();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        performBluetoothStateOnListener();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        performBluetoothStateTurningOffListener();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        performBluetoothStateTurningOnListener();
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    private void performBluetoothStateTurningOnListener() {
        BleManager.getHANDLER().post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < onBluetoothStateChangedListeners.size(); i++) {
                    OnBluetoothStateChangedListener onBluetoothStateChangedListener = onBluetoothStateChangedListeners.get(i);
                    if (onBluetoothStateChangedListener != null) {
                        onBluetoothStateChangedListener.onBluetoothEnabling();
                    }
                }

            }
        });
    }

    private void performBluetoothStateTurningOffListener() {
        BleManager.getHANDLER().post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < onBluetoothStateChangedListeners.size(); i++) {
                    OnBluetoothStateChangedListener onBluetoothStateChangedListener = onBluetoothStateChangedListeners.get(i);
                    if (onBluetoothStateChangedListener != null) {
                        onBluetoothStateChangedListener.onBluetoothDisabling();
                    }
                }

            }
        });
    }

    private void performBluetoothStateOnListener() {

        BleManager.getHANDLER().post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < onBluetoothStateChangedListeners.size(); i++) {
                    OnBluetoothStateChangedListener onBluetoothStateChangedListener = onBluetoothStateChangedListeners.get(i);
                    if (onBluetoothStateChangedListener != null) {
                        onBluetoothStateChangedListener.onBluetoothEnable();
                    }
                }

            }
        });
    }

    private void performBluetoothStateOffListener() {
        BleManager.getHANDLER().post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < onBluetoothStateChangedListeners.size(); i++) {
                    OnBluetoothStateChangedListener onBluetoothStateChangedListener = onBluetoothStateChangedListeners.get(i);
                    if (onBluetoothStateChangedListener != null) {
                        onBluetoothStateChangedListener.onBluetoothDisable();
                    }
                }

            }
        });
    }

    /*--------------------------getter & setter--------------------------*/

    /**
     * add  Bluetooth status changed listener
     *
     * @param onBluetoothStateChangedListener Bluetooth status changed listener
     */
    public void addOnBluetoothStateChangedListener(@NonNull OnBluetoothStateChangedListener onBluetoothStateChangedListener) {
        onBluetoothStateChangedListeners.add(onBluetoothStateChangedListener);
    }

    /**
     * remove  Bluetooth status changed listener
     *
     * @param onBluetoothStateChangedListener Bluetooth status changed listener
     */
    public void removeOnBluetoothStateChangedListener(@NonNull OnBluetoothStateChangedListener onBluetoothStateChangedListener) {
        onBluetoothStateChangedListeners.remove(onBluetoothStateChangedListener);
    }

    public void removeAllOnBluetoothStateChangedListener() {
        onBluetoothStateChangedListeners.clear();
    }
}
