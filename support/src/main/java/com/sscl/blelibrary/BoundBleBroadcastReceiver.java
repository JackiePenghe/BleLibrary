package com.sscl.blelibrary;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.sscl.blelibrary.interfaces.OnDeviceBondStateChangedListener;


/**
 * Broadcast receiver listening for binding status
 *
 * @author alm
 */
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public final class BoundBleBroadcastReceiver extends BroadcastReceiver {

    /*-----------------------------------static constant-----------------------------------*/

    private static final String TAG = BoundBleBroadcastReceiver.class.getSimpleName();
    /**
     * The userBean will be prompted to enter a passkey
     */
    public static final int PAIRING_VARIANT_PASSKEY = 1;

    /*-----------------------------------field variables-----------------------------------*/

    /**
     * BLE Bluetooth device binding state changed listener
     */
    @Nullable
    private OnDeviceBondStateChangedListener mOnDeviceBondStateChangedListener;

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
            case BluetoothDevice.ACTION_PAIRING_REQUEST:
                DebugUtil.warnOut(TAG, "ACTION_PAIRING_REQUEST");
                int mType = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, BluetoothDevice.ERROR);
                DebugUtil.warnOut(TAG, "mType = " + mType);
                switch (mType) {
                    case BluetoothDevice.PAIRING_VARIANT_PASSKEY_CONFIRMATION:
                        DebugUtil.warnOut(TAG, "Let the userBean confirm that the PIN is correct");
                        break;
                    case PAIRING_VARIANT_PASSKEY:
                        DebugUtil.warnOut(TAG, "Prompt the userBean to enter a PIN or automatically enter a PIN");
                        break;
                    default:
                        break;
                }
                break;
            case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                DebugUtil.warnOut("BoundBleBroadcastReceiver", "ACTION_BOND_STATE_CHANGED");
                int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                DebugUtil.warnOut("BoundBleBroadcastReceiver", "bondState = " + bondState);
                switch (bondState) {
                    case BluetoothDevice.BOND_BONDING:
                        DebugUtil.warnOut("BoundBleBroadcastReceiver", "BOND_BONDING");
                        if (mOnDeviceBondStateChangedListener != null) {
                            mOnDeviceBondStateChangedListener.onDeviceBinding();
                        }
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        DebugUtil.warnOut("BoundBleBroadcastReceiver", "BOND_BONDED");
                        if (mOnDeviceBondStateChangedListener != null) {
                            mOnDeviceBondStateChangedListener.onDeviceBonded();
                        }
                        break;
                    case BluetoothDevice.BOND_NONE:
                        DebugUtil.warnOut("BoundBleBroadcastReceiver", "BOND_NONE");
                        if (mOnDeviceBondStateChangedListener != null) {
                            mOnDeviceBondStateChangedListener.onDeviceBindNone();
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

    /*-----------------------------------Package private method-----------------------------------*/

    /**
     * set BLE Bluetooth device binding state changed listener
     *
     * @param onDeviceBondStateChangedListener BLE Bluetooth device binding state changed listener
     */
    public void setOnDeviceBondStateChangedListener(@Nullable OnDeviceBondStateChangedListener onDeviceBondStateChangedListener) {
        mOnDeviceBondStateChangedListener = onDeviceBondStateChangedListener;
    }
}
