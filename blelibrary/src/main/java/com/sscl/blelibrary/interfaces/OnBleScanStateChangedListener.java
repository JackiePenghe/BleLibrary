package com.sscl.blelibrary.interfaces;

import android.bluetooth.le.ScanResult;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.sscl.blelibrary.BleDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * BLE scan result changed listener
 *
 * @author jackie
 */
public interface OnBleScanStateChangedListener {
    /**
     * callback triggered when a Bluetooth device is found
     *
     * @param bleDevice BLE device
     */
    void onScanFindOneDevice(BleDevice bleDevice);

    /**
     * * callback triggered when a new bluetooth device is found.The device first appeared in this scan.
     *
     * @param index      The index of the current device in the list of scanned devices
     * @param bleDevice  BLE device.If null,that means a device info updated at index value in parameter bleDevices.
     * @param bleDevices the list of scanned devices
     */
    void onScanFindOneNewDevice(int index, @Nullable BleDevice bleDevice, @NonNull ArrayList<BleDevice> bleDevices);

    /**
     * callback triggered when the scan is finished
     */
    void onScanComplete();

    /**
     * BaseBleConnectCallback when batch results are delivered.
     *
     * @param results List of scan results that are previously scanned.
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    void onBatchScanResults(List<ScanResult> results);

    /**
     * BaseBleConnectCallback when scan could not be started.
     *
     * @param errorCode Error code (one of SCAN_FAILED_*) for scan failure.
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    void onScanFailed(int errorCode);
}
