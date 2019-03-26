package com.sscl.blelibrary;

import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.sscl.blelibrary.interfaces.OnBleAdvertiseStateChangedListener;

/**
 * BLE Advertise Callback of system api implement
 *
 * @author jackie
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
public class DefaultBleAdvertiseCallback extends AdvertiseCallback {

    /*-----------------------------------static constant-----------------------------------*/

    private final String TAG = DefaultBleAdvertiseCallback.class.getSimpleName();

    /*-----------------------------------field variables-----------------------------------*/

    /**
     * callbacks of BLE Advertise
     */
    @Nullable
    private OnBleAdvertiseStateChangedListener onBleAdvertiseStateChangedListener;

    /*-----------------------------------override methods-----------------------------------*/

    /**
     * Callback triggered in response to {@link BluetoothLeAdvertiser#startAdvertising} indicating
     * that the advertising has been started successfully.
     *
     * @param settingsInEffect The actual settings used for advertising, which may be different from
     *                         what has been requested.
     */
    @Override
    public void onStartSuccess(final AdvertiseSettings settingsInEffect) {
        DebugUtil.warnOut(TAG, "onStartSuccess");
        if (settingsInEffect != null) {
            DebugUtil.warnOut(TAG, "onStartSuccess TxPowerLv=" + settingsInEffect.getTxPowerLevel() + " mode=" + settingsInEffect.getMode()
                    + " timeout=" + settingsInEffect.getTimeout());
        } else {
            DebugUtil.warnOut(TAG, "onStartSuccess, settingInEffect is null");
        }
        DebugUtil.warnOut(TAG, "onStartSuccess settingsInEffect" + settingsInEffect);
        triggerAdvertiseStartSuccessCallback(settingsInEffect);
    }

    /**
     * Callback when advertising could not be started.
     *
     * @param errorCode Error code (see ADVERTISE_FAILED_* constants) for advertising start
     *                  failures.
     */
    @Override
    public void onStartFailure(final int errorCode) {
        DebugUtil.warnOut(TAG, "onStartFailure");
        if (errorCode == ADVERTISE_FAILED_DATA_TOO_LARGE) {
            DebugUtil.errorOut(TAG, "Failed to start advertising as the advertise data to be broadcasted is larger than 31 bytes.");
        } else if (errorCode == ADVERTISE_FAILED_TOO_MANY_ADVERTISERS) {
            DebugUtil.errorOut(TAG, "Failed to start advertising because no advertising instance is available.");
        } else if (errorCode == ADVERTISE_FAILED_ALREADY_STARTED) {
            DebugUtil.errorOut(TAG, "Failed to start advertising as the advertising is already started");
        } else if (errorCode == ADVERTISE_FAILED_INTERNAL_ERROR) {
            DebugUtil.errorOut(TAG, "Operation failed due to an internal error");
        } else if (errorCode == ADVERTISE_FAILED_FEATURE_UNSUPPORTED) {
            DebugUtil.errorOut(TAG, "This feature is not supported on this platform");
        }
        triggerAdvertiseStartFailedCallback(errorCode);
    }

    /*-----------------------------------package private methods-----------------------------------*/

    /**
     * Set callbacks of BLE Advertise
     *
     * @param onBleAdvertiseStateChangedListener callbacks of BLE Advertise
     */
    void setOnBleAdvertiseStateChangedListener(@Nullable OnBleAdvertiseStateChangedListener onBleAdvertiseStateChangedListener) {
        this.onBleAdvertiseStateChangedListener = onBleAdvertiseStateChangedListener;
    }

    /*-----------------------------------private methods-----------------------------------*/

    private void triggerAdvertiseStartSuccessCallback(final AdvertiseSettings settingsInEffect) {
        BleManager.getHANDLER().post(new Runnable() {
            @Override
            public void run() {
                if (onBleAdvertiseStateChangedListener != null) {
                    onBleAdvertiseStateChangedListener.onBroadCastStartSuccess(settingsInEffect);
                }
            }
        });
    }

    private void triggerAdvertiseStartFailedCallback(final int errorCode) {
        BleManager.getHANDLER().post(new Runnable() {
            @Override
            public void run() {
                if (onBleAdvertiseStateChangedListener != null) {
                    onBleAdvertiseStateChangedListener.onBroadCastStartFailure(errorCode);
                }
            }
        });
    }
}
