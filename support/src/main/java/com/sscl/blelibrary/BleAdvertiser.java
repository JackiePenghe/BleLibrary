package com.sscl.blelibrary;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.ParcelUuid;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.sscl.blelibrary.enums.BleAdvertiseMode;
import com.sscl.blelibrary.enums.BleAdvertiseTxPowerLevel;
import com.sscl.blelibrary.interfaces.OnBleAdvertiseStateChangedListener;
import com.sscl.blelibrary.interfaces.OnConnectedByOtherDevicesListener;

import java.util.ArrayList;

import static android.bluetooth.le.AdvertiseData.Builder;

/**
 * BLE broadcast util class
 *
 * @author jacke
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
public final class BleAdvertiser {
    private static final String TAG = BleAdvertiser.class.getSimpleName();

    /*-----------------------------------field variables-----------------------------------*/
    /**
     * BLE Advertise Callback of system api implement
     */
    private DefaultBleAdvertiseCallback defaultBleAdvertiseCallback = new DefaultBleAdvertiseCallback();

    /**
     * BLE Advertise Mode
     */
    private BleAdvertiseMode bleAdvertiseMode = BleAdvertiseMode.LOW_LATENCY;

    /**
     * Use to record the advertisement type should be connectable or non-connectable.
     */
    private boolean connectable = false;

    /**
     * BLE Advertise Tx Power Level
     */
    private BleAdvertiseTxPowerLevel txPowerLevel = BleAdvertiseTxPowerLevel.HIGH;

    /**
     * BLE Advertise time out
     */
    private int timeOut = 0;

    /**
     * Use to record the transmission power level should be included in the advertise packet. Tx power
     * level field takes 3 bytes in advertise packet.
     */
    private boolean scanResponseIncludeTxPowerLevel = false;

    /**
     * Use to record the device name should be included in advertise packet.
     */
    private boolean scanResponseIncludeDeviceName = false;

    /**
     * BLE advertisement scan response pack content data list.
     */
    private ArrayList<AdvertiseData> scanResponseAdvertiseRecords = new ArrayList<>();

    /**
     * BLE advertisement pack service uuid list
     */
    private ArrayList<AdvertiseServiceUuid> scanResponseAdvertiseServiceUuids = new ArrayList<>();

    /**
     * Use to record the transmission power level should be included in the advertise packet. Tx power
     * level field takes 3 bytes in advertise packet.
     */
    private boolean advertiseDataIncludeTxPowerLevel = false;

    /**
     * Use to record the device name should be included in advertise packet.
     */
    private boolean advertiseDataIncludeDeviceName = false;

    /**
     * BLE advertisement scan response pack content data list.
     */
    private ArrayList<AdvertiseData> advertiseDataAdvertiseRecords = new ArrayList<>();

    /**
     * BLE advertisement pack service uuid list
     */
    private ArrayList<AdvertiseServiceUuid> advertiseDataAdvertiseServiceUuids = new ArrayList<>();

    /**
     * Advertise Settings
     */
    private AdvertiseSettings advertiseSettings;
    /**
     * Scan Response
     */
    private android.bluetooth.le.AdvertiseData scanResponse;
    /**
     * Advertise Data
     */
    private android.bluetooth.le.AdvertiseData advertiseData;

    /**
     * Used to record whether it is currently broadcasting
     */
    private boolean advertising;

    /**
     * Context
     */
    private Context context;

    /**
     * Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter;

    /**
     * BluetoothLe advertiser instance
     */
    private BluetoothLeAdvertiser mBluetoothAdvertiser;

    /**
     * Initialization state
     */
    private boolean initSuccess;

    /**
     * Bluetooth Manager
     */
    private BluetoothManager bluetoothManager;

    /**
     * Callback connected by other devices
     */
    private DefaultBluetoothGattServerCallback defaultBluetoothGattServerCallback;

    /**
     * BluetoothGattServer
     */
    private BluetoothGattServer bluetoothGattServer;

    /**
     * callback triggered when BLE Advertise State changed
     */
    private OnBleAdvertiseStateChangedListener baseAdvertiseCallback;
    /**
     * Use the timer to stop advertising
     */
    private AdvertiserTimer advertiserTimer;

    /*-----------------------------------Constructor-----------------------------------*/

    /**
     * Constructor
     *
     * @param context Context
     */
    BleAdvertiser(@NonNull Context context) {
        this.context = context;

        // Use this check to determine whether BLE is supported on the device.
        if (!(this.context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))) {
            return;
        }

        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null) {
            return;
        }
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            return;
        }
        mBluetoothAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        if (!mBluetoothAdapter.isEnabled()) {
            //If it is created by the activity, enable bluetooth by request code.
            if (this.context instanceof Activity) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                this.context.startActivity(enableBtIntent);
            }
            //If not created by the activity,enable bluetooth by bluetooth adapter directly
            else {
                mBluetoothAdapter.enable();
            }
        }
    }

    /*-----------------------------------public methods-----------------------------------*/

    /**
     * Get Bluetooth GATT Server instance
     *
     * @return BluetoothGattServer
     */
    @Nullable
    public BluetoothGattServer getBluetoothGattServer() {
        return bluetoothGattServer;
    }
    /*-----------------------------------public methods-----------------------------------*/

    /**
     * set ble advertise mode
     *
     * @param bleAdvertiseMode ble advertise mode
     */
    public void setBleAdvertiseMode(@NonNull BleAdvertiseMode bleAdvertiseMode) {
        this.bleAdvertiseMode = bleAdvertiseMode;
        initAdvertiseSettings();
    }

    /**
     * set the advertisement type should be connectable or non-connectable
     *
     * @param connectable connectable
     */
    public void setConnectable(boolean connectable) {
        this.connectable = connectable;
        initAdvertiseSettings();
    }

    /**
     * set BLE Advertise Tx Power Level
     *
     * @param txPowerLevel BLE Advertise Tx Power Level
     */
    public void setTxPowerLevel(@NonNull BleAdvertiseTxPowerLevel txPowerLevel) {
        this.txPowerLevel = txPowerLevel;
        initAdvertiseSettings();
    }

    /**
     * Return true if the multi advertisement is supported by the chipset
     *
     * @return true if Multiple Advertisement feature is supported
     */
    public boolean isMultipleAdvertisementSupported() {
        return mBluetoothAdapter != null && mBluetoothAdapter.isMultipleAdvertisementSupported();
    }

    /**
     * set advertise time out
     *
     * @param timeOut time out
     */
    public void setTimeOut(@IntRange(from = 0, to = 180000) int timeOut) {
        this.timeOut = timeOut;
        initAdvertiseSettings();
    }

    /**
     * set whether tx power level need included in the scan response data
     *
     * @param scanResponseIncludeTxPowerLevel true means tx power level include in the scan response data
     */
    public void setScanResponseIncludeTxPowerLevel(boolean scanResponseIncludeTxPowerLevel) {
        this.scanResponseIncludeTxPowerLevel = scanResponseIncludeTxPowerLevel;
        initScanResponse();
    }

    /**
     * set whether device name need included in the scan response data
     *
     * @param scanResponseIncludeDeviceName true means device name include in the scan response data
     */
    public void setScanResponseIncludeDeviceName(boolean scanResponseIncludeDeviceName) {
        this.scanResponseIncludeDeviceName = scanResponseIncludeDeviceName;
        initScanResponse();
    }

    /**
     * set whether tx power level need included in the advertise data
     *
     * @param advertiseDataIncludeTxPowerLevel true means tx power level include in the advertise data
     */
    public void setAdvertiseDataIncludeTxPowerLevel(boolean advertiseDataIncludeTxPowerLevel) {
        this.advertiseDataIncludeTxPowerLevel = advertiseDataIncludeTxPowerLevel;
        initAdvertiseData();
    }

    /**
     * set whether device name need included in the advertise data
     *
     * @param advertiseDataIncludeDeviceName true means device name include in the advertise data
     */
    public void setAdvertiseDataIncludeDeviceName(boolean advertiseDataIncludeDeviceName) {
        this.advertiseDataIncludeDeviceName = advertiseDataIncludeDeviceName;
        initAdvertiseData();
    }

    /**
     * initialization Advertiser
     *
     * @return true means initialization successful
     */
    public boolean init() {
        advertiserTimer = new AdvertiserTimer(this);
        return initAdvertiser();
    }

    /**
     * Set callbacks of BLE Advertise
     *
     * @param onBleAdvertiseStateChangedListener callbacks of BLE Advertise
     */
    public void setOnBleAdvertiseStateChangedListener(@Nullable OnBleAdvertiseStateChangedListener onBleAdvertiseStateChangedListener) {
        this.baseAdvertiseCallback = onBleAdvertiseStateChangedListener;
        if (defaultBleAdvertiseCallback != null) {
            defaultBleAdvertiseCallback.setOnBleAdvertiseStateChangedListener(onBleAdvertiseStateChangedListener);
        }
    }

    /**
     * startAdvertising
     */
    public boolean startAdvertising() {
        if (mBluetoothAdapter == null) {
            return false;
        }

        if (mBluetoothAdvertiser == null) {
            return false;
        }
        if (!initSuccess) {
            return false;
        }
        if (advertising) {
            return false;
        }
        mBluetoothAdvertiser.startAdvertising(advertiseSettings, advertiseData, scanResponse, defaultBleAdvertiseCallback);
        int timeout = advertiseSettings.getTimeout();
        if (timeout > 0) {
            advertiserTimer.startTimer(timeout);
        }
        advertising = true;
        return true;
    }

    /**
     * Stop Advertising
     */
    public void stopAdvertising() {
        if (mBluetoothAdapter == null) {
            return;
        }

        if (mBluetoothAdvertiser == null) {
            return;
        }
        if (!advertising) {
            return;
        }
        try {
            advertiserTimer.stopTimer();
        } catch (Exception e) {
            DebugUtil.warnOut(TAG,"stop advertiser timer failed");
        }
        try {
            mBluetoothAdvertiser.stopAdvertising(defaultBleAdvertiseCallback);
        } catch (Exception e) {
            DebugUtil.warnOut(TAG,"stop advertising failed");
        }
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (baseAdvertiseCallback != null) {
                    baseAdvertiseCallback.onBroadCastStopped();
                }
            }
        });

        advertising = false;
    }


    /**
     * Close advertise
     */
    public void close() {
        stopAdvertising();
        initSuccess = false;
        context = null;
        mBluetoothAdvertiser = null;
        mBluetoothAdapter = null;
        advertiseSettings = null;
        advertiseData = null;
        scanResponse = null;
        defaultBleAdvertiseCallback = null;
    }

    /**
     * Set callback of connected by other devices
     *
     * @param onConnectedByOtherDevicesListener Callback of connected by other devices
     */
    public void setOnBluetoothGattServerCallbackListener(@Nullable OnConnectedByOtherDevicesListener onConnectedByOtherDevicesListener) {
        if (defaultBluetoothGattServerCallback != null) {
            defaultBluetoothGattServerCallback.setOnConnectedByOtherDevicesListener(onConnectedByOtherDevicesListener);
        }
    }

    /**
     * add a advertise record to scan response pack
     *
     * @param advertiseData AdvertiseRecord
     */
    public void addScanResponseAdvertiseRecord(@NonNull AdvertiseData advertiseData) {
        this.scanResponseAdvertiseRecords.add(advertiseData);
        initScanResponse();
    }

    /**
     * remove a advertise record from scan response pack
     *
     * @param advertiseData AdvertiseRecord
     */
    public void removeScanResponseAdvertiseRecord(@NonNull AdvertiseData advertiseData) {
        this.scanResponseAdvertiseRecords.remove(advertiseData);
        initScanResponse();
    }

    /**
     * add a service uuid data to scan response pack
     *
     * @param advertiseServiceUuid AdvertiseServiceUuid
     */
    public void addScanResponseAdvertiseServiceUuid(@NonNull AdvertiseServiceUuid advertiseServiceUuid) {
        this.scanResponseAdvertiseServiceUuids.add(advertiseServiceUuid);
        initScanResponse();
    }

    /**
     * remove a service uuid data from scan response pack
     *
     * @param advertiseServiceUuid AdvertiseServiceUuid
     */
    public void removeScanResponseAdvertiseServiceUuid(@NonNull AdvertiseServiceUuid advertiseServiceUuid) {
        this.scanResponseAdvertiseServiceUuids.remove(advertiseServiceUuid);
        initScanResponse();
    }

    /**
     * add a advertise record to advertise pack
     *
     * @param advertiseData AdvertiseRecord
     */
    public void addAdvertiseDataAdvertiseRecord(@NonNull AdvertiseData advertiseData) {
        this.advertiseDataAdvertiseRecords.add(advertiseData);
        initAdvertiseData();
    }

    /**
     * remove a advertise record from advertise pack
     *
     * @param advertiseData AdvertiseRecord
     */
    public void removeAdvertiseDataAdvertiseRecord(@NonNull AdvertiseData advertiseData) {
        this.advertiseDataAdvertiseRecords.remove(advertiseData);
        initAdvertiseData();
    }

    /**
     * add a service uuid data to advertise pack
     *
     * @param advertiseServiceUuid AdvertiseServiceUuid
     */
    public void addAdvertiseDataAdvertiseServiceUuids(@NonNull AdvertiseServiceUuid advertiseServiceUuid) {
        this.advertiseDataAdvertiseServiceUuids.add(advertiseServiceUuid);
        initAdvertiseData();
    }

    /**
     * remove a service uuid data from advertise pack
     *
     * @param advertiseServiceUuid AdvertiseServiceUuid
     */
    public void removeAdvertiseDataAdvertiseServiceUuids(@NonNull AdvertiseServiceUuid advertiseServiceUuid) {
        this.advertiseDataAdvertiseServiceUuids.remove(advertiseServiceUuid);
        initAdvertiseData();
    }

    /*-----------------------------------private methods-----------------------------------*/

    /**
     * initialization Advertiser
     *
     * @return true means initialization successful
     */
    private boolean initAdvertiser() {
        if (mBluetoothAdapter == null) {
            initSuccess = false;
            return false;
        }

        // Bluetooth LE advertise class from system api
        mBluetoothAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        if (mBluetoothAdvertiser == null) {
            initSuccess = false;
            return false;
        }

        Context context = this.context;
        if (context == null) {
            initSuccess = false;
            return false;
        }

        initAdvertiseSettings();
        initScanResponse();
        initAdvertiseData();

        boolean connectable = this.advertiseSettings.isConnectable();
        if (connectable) {
            if (defaultBluetoothGattServerCallback == null) {
                defaultBluetoothGattServerCallback = new DefaultBluetoothGattServerCallback();
            }
            bluetoothGattServer = bluetoothManager.openGattServer(context, defaultBluetoothGattServerCallback);
        }
        initSuccess = true;
        return true;
    }

    /**
     * initialization Advertise Data
     */
    private void initAdvertiseData() {
        Builder builder = new Builder();
        builder.setIncludeTxPowerLevel(advertiseDataIncludeTxPowerLevel)
                .setIncludeDeviceName(advertiseDataIncludeDeviceName);

        for (int i = 0; i < advertiseDataAdvertiseRecords.size(); i++) {
            AdvertiseData advertiseData = advertiseDataAdvertiseRecords.get(i);
            addManufacturerData(builder, advertiseData);
        }

        for (int i = 0; i < advertiseDataAdvertiseServiceUuids.size(); i++) {
            AdvertiseServiceUuid advertiseServiceUuid = scanResponseAdvertiseServiceUuids.get(i);
            addServiceData(builder, advertiseServiceUuid);
        }

        advertiseData = builder.build();
    }

    /**
     * initialization Advertise Settings
     */
    private void initAdvertiseSettings() {
        advertiseSettings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(bleAdvertiseMode.getValue())
                .setConnectable(connectable)
                .setTxPowerLevel(txPowerLevel.getValue())
                .setTimeout(timeOut)
                .build();
    }

    /**
     * initialization Scan Response Data
     */
    private void initScanResponse() {
        Builder builder = new Builder();
        builder.setIncludeTxPowerLevel(scanResponseIncludeTxPowerLevel)
                .setIncludeDeviceName(scanResponseIncludeDeviceName);

        for (int i = 0; i < scanResponseAdvertiseRecords.size(); i++) {
            AdvertiseData advertiseData = scanResponseAdvertiseRecords.get(i);
            addManufacturerData(builder, advertiseData);
        }

        for (int i = 0; i < scanResponseAdvertiseServiceUuids.size(); i++) {
            AdvertiseServiceUuid advertiseServiceUuid = scanResponseAdvertiseServiceUuids.get(i);
            addServiceData(builder, advertiseServiceUuid);
        }

        scanResponse = builder.build();
    }

    /**
     * add Manufacturer Data to advertise data or scan response data
     *
     * @param builder         AdvertiseData.Builder
     * @param advertiseRecord AdvertiseRecord
     */
    private void addManufacturerData(Builder builder, AdvertiseData advertiseRecord) {
        int manufacturerId = advertiseRecord.getManufacturerId();
        byte[] data = advertiseRecord.getData();
        if (data == null) {
            return;
        }
        if (manufacturerId == 0) {
            return;
        }
        builder.addManufacturerData(manufacturerId, data);
    }

    /**
     * add ServiceData Data to advertise data or scan response data
     *
     * @param builder              AdvertiseData.Builder
     * @param advertiseServiceUuid AdvertiseServiceUuid
     */
    private void addServiceData(Builder builder, AdvertiseServiceUuid advertiseServiceUuid) {
        ParcelUuid parcelUuid = advertiseServiceUuid.getParcelUuid();
        byte[] data = advertiseServiceUuid.getData();

        if (data == null) {
            builder.addServiceUuid(parcelUuid);
            return;
        }
        builder.addServiceUuid(parcelUuid);
        builder.addServiceData(parcelUuid, data);
    }

    public boolean isAdvertising() {
        return advertising;
    }
}
