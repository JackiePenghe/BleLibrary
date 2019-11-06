package com.sscl.blelibrary.interfaces;

import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.AdvertisingSet;
import android.bluetooth.le.BluetoothLeAdvertiser;

/**
 * callback triggered when BLE Advertise State changed
 *
 * @author jackie
 */
public interface OnBleAdvertiseStateChangedListener {

    /*-----------------------------------methods-----------------------------------*/

    /**
     * Callback triggered in response to {@link BluetoothLeAdvertiser#startAdvertising} indicating
     * that the advertising has been started successfully.
     *
     * @param settingsInEffect The actual settings used for advertising, which may be different from
     *                         what has been requested.
     */
    void onBroadCastStartSuccess(AdvertiseSettings settingsInEffect);

    /**
     * Callback when advertising could not be started.
     *
     * @param errorCode Error code (see ADVERTISE_FAILED_* constants) for advertising start
     *                  failures.
     */
    void onBroadCastStartFailure(int errorCode);

    /**
     * 如果设置了超时时间，在超时结束后，会执行此回调
     */
    void onBroadCastStopped();

    /**
     * Callback triggered in response to {@link BluetoothLeAdvertiser#startAdvertisingSet}
     * indicating result of the operation. If status is ADVERTISE_SUCCESS, then advertisingSet
     * contains the started set and it is advertising. If error occured, advertisingSet is
     * null, and status will be set to proper error code.
     *
     * @param advertisingSet The advertising set that was started or null if error.
     * @param txPower        tx power that will be used for this set.
     * @param status         Status of the operation.
     */
    void onAdvertisingSetStarted(AdvertisingSet advertisingSet, int txPower, int status);


    /**
     * Callback triggered in response to {@link BluetoothLeAdvertiser#stopAdvertisingSet}
     * indicating advertising set is stopped.
     *
     * @param advertisingSet The advertising set.
     */
    void onAdvertisingSetStopped(AdvertisingSet advertisingSet);


    /**
     * Callback triggered in response to {@link BluetoothLeAdvertiser#startAdvertisingSet} indicating
     * result of the operation. If status is ADVERTISE_SUCCESS, then advertising set is advertising.
     *
     * @param advertisingSet The advertising set.
     * @param enable         enable
     * @param status         Status of the operation.
     */
    void onAdvertisingEnabled(AdvertisingSet advertisingSet, boolean enable, int status);


    /**
     * Callback triggered in response to {@link AdvertisingSet#setAdvertisingData} indicating
     * result of the operation.
     *
     * @param advertisingSet The advertising set.
     * @param status         Status of the operation.
     */
    void onScanResponseDataSet(AdvertisingSet advertisingSet, int status);

    /**
     * Callback triggered in response to {@link AdvertisingSet#setAdvertisingParameters}
     * indicating result of the operation.
     *
     * @param advertisingSet The advertising set.
     * @param txPower        tx power that will be used for this set.
     * @param status         Status of the operation.
     */
    void onAdvertisingParametersUpdated(AdvertisingSet advertisingSet, int txPower, int status);

    /**
     * Callback triggered in response to {@link AdvertisingSet#setPeriodicAdvertisingParameters}
     * indicating result of the operation.
     *
     * @param advertisingSet The advertising set.
     * @param status         Status of the operation.
     */
    void onPeriodicAdvertisingParametersUpdated(AdvertisingSet advertisingSet, int status);


    /**
     * Callback triggered in response to {@link AdvertisingSet#setPeriodicAdvertisingData}
     * indicating result of the operation.
     *
     * @param advertisingSet The advertising set.
     * @param status         Status of the operation.
     */
    void onPeriodicAdvertisingDataSet(AdvertisingSet advertisingSet, int status);

    /**
     * Callback triggered in response to {@link AdvertisingSet#setPeriodicAdvertisingEnabled}
     * indicating result of the operation.
     *
     * @param advertisingSet The advertising set.
     * @param enable         enable
     * @param status         Status of the operation.
     */
    void onPeriodicAdvertisingEnabled(AdvertisingSet advertisingSet, boolean enable, int status);

    /**
     * Callback triggered in response to {@link AdvertisingSet#setAdvertisingData} indicating
     * result of the operation. If status is ADVERTISE_SUCCESS, then data was changed.
     *
     * @param advertisingSet The advertising set.
     * @param status         Status of the operation.
     */
    void onAdvertisingDataSet(AdvertisingSet advertisingSet, int status);
}
