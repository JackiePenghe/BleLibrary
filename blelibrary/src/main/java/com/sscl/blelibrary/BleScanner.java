package com.sscl.blelibrary;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.ParcelUuid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.sscl.blelibrary.enums.BleCallbackType;
import com.sscl.blelibrary.enums.BleMatchMode;
import com.sscl.blelibrary.enums.BleNumOfMatches;
import com.sscl.blelibrary.enums.BleScanMode;
import com.sscl.blelibrary.enums.ScanPhy;
import com.sscl.blelibrary.interfaces.OnBleScanStateChangedListener;
import com.sscl.blelibrary.systems.BleArrayList;
import com.sscl.blelibrary.systems.BleParcelUuid;
import com.sscl.blelibrary.systems.BleScanRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * BLE Scanner
 *
 * @author jackie
 */
public final class BleScanner {

    /*-----------------------------------static constant-----------------------------------*/

    /**
     * TAGs
     */
    private static final String TAG = BleScanner.class.getSimpleName();

    /*-----------------------------------field variables-----------------------------------*/

    /**
     * Duration of the scan
     */
    private long scanPeriod = 20000;

    /**
     * Scan result list
     */
    private ArrayList<BleDevice> scanResults = new ArrayList<>();

    /**
     * BLE scan node
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private BleScanMode bleScanMode = BleScanMode.LOW_LATENCY;

    /**
     * whether only legacy advertisments should be returned in scan results.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean legacy = false;

    /**
     * scan phy
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private ScanPhy scanPhy = ScanPhy.PHY_LE_ALL_SUPPORTED;

    /**
     * BLE match mode
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private BleMatchMode bleMatchMode = BleMatchMode.AGGRESSIVE;

    /**
     * BLE callback type
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private BleCallbackType bleCallbackType = BleCallbackType.CALLBACK_TYPE_ALL_MATCHES;

    @RequiresApi(api = Build.VERSION_CODES.M)
    private BleNumOfMatches bleNumOfMatches = BleNumOfMatches.MATCH_NUM_MAX_ADVERTISEMENT;

    private int reportDelay = 0;

    /**
     * Used to store the device name to be filtered.
     * The beginning of the device name exists in the list.
     * If the list is empty, it will not take effect.
     */
    private ArrayList<String> filterNames = new ArrayList<>();

    /**
     * Used to store the device service uuid to be filtered.
     * The service uuid of the device exists in the list.
     * If the list is empty, it will not take effect.
     */
    private ArrayList<String> filterUuids = new ArrayList<>();

    /**
     * Used to store the device name to be filtered.
     * The full name of the device exists in the list.
     * If the list is empty, it will not take effect.
     */
    private ArrayList<String> filterFullNames = new ArrayList<>();

    /**
     * Used to store the device address to be filtered.
     * The beginning of the device address exists in the list.
     * Address need include colon.For example : "A" or "AA" or "AA:" or "AA:B"
     * If the list is empty, it will not take effect
     */
    private ArrayList<String> filterAddresses = new ArrayList<>();

    /**
     * Used to store the device address to be filtered.
     * The full address of the device exists in the list.
     * Address need include colon.For example : "AA:BB:CC:DD:EE:FF"
     * If the list is empty, it will not take effect
     */
    private ArrayList<String> filterFullAddresses = new ArrayList<>();

    /**
     * Used to store user-defined filter conditions.
     * If the list is empty, the list will not take effect
     */
    private ArrayList<ScanFilter> customScanFilters = new ArrayList<>();

    /**
     * Used to record whether the next scan is automatically performed
     */
    private boolean autoStartNextScan = false;

    /**
     * BLE scan result changed listener
     */
    @Nullable
    private OnBleScanStateChangedListener onBleScanStateChangedListener;
    /**
     * Broadcast receiver detecting Bluetooth switch status
     */
    private BleScannerBluetoothStateReceiver bleScannerBluetoothStateReceiver;

    /**
     * Used to record whether the scanner has been initialized
     */
    private boolean initialized;

    /**
     * Context
     */
    private Context context;

    /**
     * Bluetooth adapter
     */
    private BluetoothAdapter bluetoothAdapter;

    /**
     * Used to record whether it is scanning
     */
    private boolean scanning;

    /**
     * System scan callback (API 20 and below)
     */
    private BluetoothAdapter.LeScanCallback scanCallback18;

    /**
     * System scan callback (API 20 and above)
     */
    private ScanCallback scanCallback21;

    /**
     * a timer for scanner to stop scan
     */
    private ScanTimer scanTimer;
    /**
     * System BLE scanner
     */
    private BluetoothLeScanner bluetoothLeScanner;

    /*-----------------------------------Constructor-----------------------------------*/

    /**
     * Constructor
     *
     * @param context Context
     */
    BleScanner(@NonNull Context context) {
        this.context = context;
        scanTimer = new ScanTimer(BleScanner.this);
        if (!(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //Initialize scan callback (API21 and above)
            initBleScanCallBack21();
        } else {
            //Initialize BLE scan callback (API21 below and does not include API21)
            initBleScanCallBack18();
        }

        bleScannerBluetoothStateReceiver = new BleScannerBluetoothStateReceiver(BleScanner.this);

        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null) {
            return;
        }
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            //If it is created by activity,directly request to enable Bluetooth
            if (context instanceof Activity) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                context.startActivity(enableBtIntent);
            } else {
                if (bluetoothAdapter != null) {
                    bluetoothAdapter.enable();
                }
            }
        }
    }

    /*-----------------------------------package private getter-----------------------------------*/

    /**
     * get whether the next scan is automatically performed
     *
     * @return whether the next scan is automatically performed
     */
    boolean isAutoStartNextScan() {
        return autoStartNextScan;
    }

    /**
     * set bluetooth adapter
     *
     * @param bluetoothAdapter bluetooth adapter
     */
    void setBluetoothAdapter(BluetoothAdapter bluetoothAdapter) {
        this.bluetoothAdapter = bluetoothAdapter;
    }

    /**
     * set scanning flag to false
     */
    void setScanningFalse() {
        this.scanning = false;
    }

    /*-----------------------------------public setter and getter-----------------------------------*/

    /**
     * set ble scan mode
     *
     * @param bleScanMode ble scan mode
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public void setBleScanMode(@NonNull BleScanMode bleScanMode) {
        this.bleScanMode = bleScanMode;
    }

    /**
     * set ble match mode
     *
     * @param bleMatchMode ble match mode
     */
    @RequiresApi(Build.VERSION_CODES.M)
    public void setBleMatchMode(@NonNull BleMatchMode bleMatchMode) {
        this.bleMatchMode = bleMatchMode;
    }

    /**
     * set ble callback type
     *
     * @param bleCallbackType ble callback type
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setBleCallbackType(BleCallbackType bleCallbackType) {
        this.bleCallbackType = bleCallbackType;
    }

    /**
     * set ble num of mactches
     *
     * @param bleNumOfMatches ble num of mactches
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setBleNumOfMatches(BleNumOfMatches bleNumOfMatches) {
        this.bleNumOfMatches = bleNumOfMatches;
    }

    public void setReportDelay(int reportDelay) {
        this.reportDelay = reportDelay;
    }

    /**
     * Get the current scan status
     *
     * @return True means scanner is scanning
     */
    public boolean isScanning() {
        return scanning;
    }

    /**
     * set scan period
     *
     * @param scanPeriod scan period(unit:ms)
     */
    @SuppressWarnings("unused")
    public void setScanPeriod(long scanPeriod) {
        this.scanPeriod = scanPeriod;
    }

    /**
     * set whether the next scan is automatically performed.
     * If you want keep scanning without auto stop,set true in parameter.
     *
     * @param autoStartNextScan true means auto start next scan.
     */
    public void setAutoStartNextScan(boolean autoStartNextScan) {
        this.autoStartNextScan = autoStartNextScan;
    }

    /**
     * get Context
     *
     * @return Context
     */
    public Context getContext() {
        return context;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setLegacy(boolean legacy) {
        this.legacy = legacy;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setScanPhy(ScanPhy scanPhy) {
        this.scanPhy = scanPhy;
    }

    /**
     * get scan result
     *
     * @return scan result
     */
    @NonNull
    public ArrayList<BleDevice> getScanResults() {
        return scanResults;
    }

    /*-----------------------------------public method-----------------------------------*/

    /**
     * add a user-defined filter.
     *
     * @param scanFilter user-defined filter
     */
    public void addCustomFilter(@NonNull ScanFilter scanFilter) {
        customScanFilters.add(scanFilter);
    }

    /**
     * remove a user-defined filter.
     *
     * @param scanFilter user-defined filter
     */
    public void removeCustomFilter(@NonNull ScanFilter scanFilter) {
        customScanFilters.add(scanFilter);
    }

    /**
     * clear all user-defined filters
     */
    public void clearCustomFilters() {
        customScanFilters.clear();
    }

    /**
     * Add a filter name.
     * if device name starts with the same filter name,
     * that device will be trigger callback and add in scanResult
     *
     * @param startsName a string used to filter the device name
     */
    public void addFilterStartsName(@NonNull String startsName) {
        filterNames.add(startsName);
    }

    /**
     * remove a filter name.
     * if device name starts with the same filter name,
     * that device will be trigger callback and add in scanResult
     *
     * @param startsName a string used to filter the device name
     */
    public void removeFilterStartsName(@NonNull String startsName) {
        filterNames.remove(startsName);
    }

    /**
     * remove all starts name filters
     */
    public void clearFilterStartsNames() {
        filterNames.clear();
    }

    /**
     * Add a filter name.
     * if device has service uuid in advertise data with the same uuid,
     * that device will be trigger callback and add in scanResult
     *
     * @param uuid a string used to filter the device uuid
     */
    public void addFilterUuid(String uuid) {
        filterUuids.add(uuid);
    }

    /**
     * remove a filter name.
     * if device has service uuid in advertise data with the same uuid,
     * that device will be trigger callback and add in scanResult
     *
     * @param uuid a string used to filter the device uuid
     */
    public void removeFilterUuid(String uuid) {
        filterUuids.remove(uuid);
    }

    /**
     * remove All uuids filters
     */
    public void clearFilterUuid() {
        filterUuids.clear();
    }

    /**
     * Add a filter name.
     * If the device name is the same as the filter name,
     * that device will be trigger callback and add in scanResult
     *
     * @param fullName a string used to filter the device name
     */
    public void addFilterFullName(@NonNull String fullName) {
        filterFullNames.add(fullName);
    }

    /**
     * remove a filter name.
     * if device name starts with the same filter name,
     * that device will be trigger callback and add in scanResult
     *
     * @param fullName a string used to filter the device name
     */
    public void removeFilterFullName(@NonNull String fullName) {
        filterFullNames.remove(fullName);
    }

    /**
     * clear all full name filters
     */
    public void clearFilterFullName() {
        filterFullNames.clear();
    }

    /**
     * Add a filter address.
     * if device name starts with the same filter address,
     * that device will be trigger callback and add in scanResult
     *
     * @param startsAddress a string used to filter the device address.Address need include colon.For example : "A" or "AA" or "AA:" or "AA:B"
     */
    public void addFilterStartsAddress(@NonNull String startsAddress) {
        filterAddresses.add(startsAddress.toUpperCase());
    }

    /**
     * remove a filter address.
     * if device name starts with the same filter address,
     * that device will be trigger callback and add in scanResult
     *
     * @param startsAddress a string used to filter the device address.Address need include colon.For example : "A" or "AA" or "AA:" or "AA:B"
     */
    public void removeFilterStartsAddress(@NonNull String startsAddress) {
        filterAddresses.remove(startsAddress.toUpperCase());
    }

    /**
     * clear all starts address filters
     */
    public void clearFilterStartsAddress() {
        filterAddresses.clear();
    }

    /**
     * Add a filter address.
     * If the device name is the same as the filter address,
     * that device will be trigger callback and add in scanResult
     *
     * @param fullAddress a string used to filter the device address.Address need include colon.For example : "AA:BB:CC:DD:EE:FF"
     */
    public void addFilterFullAddress(@NonNull String fullAddress) {
        filterFullAddresses.add(fullAddress.toUpperCase());
    }

    /**
     * remove a filter address.
     * if device name starts with the same filter address,
     * that device will be trigger callback and add in scanResult
     *
     * @param fullAddress a string used to filter the device address.Address need include colon.For example : "AA:BB:CC:DD:EE:FF"
     */
    public void removeFilterFullAddress(@NonNull String fullAddress) {
        filterFullAddresses.remove(fullAddress.toUpperCase());
    }

    /**
     * clear all full address filters
     */
    public void clearFilterFullAddress() {
        filterFullAddresses.clear();
    }

    /**
     * clear all filters
     */
    public void clearAllFilters() {
        clearCustomFilters();
        clearFilterFullAddress();
        clearFilterStartsAddress();
        clearFilterFullName();
        clearFilterStartsNames();
        clearFilterUuid();
    }

    /**
     * set ble scan state changed listener
     *
     * @param onBleScanStateChangedListener ble scan state changed listener
     */
    public void setOnBleScanStateChangedListener(@Nullable OnBleScanStateChangedListener onBleScanStateChangedListener) {
        this.onBleScanStateChangedListener = onBleScanStateChangedListener;
        if (scanTimer != null) {
            scanTimer.setOnBleScanStateChangedListener(onBleScanStateChangedListener);
        }
    }

    /**
     * initialization
     */
    public boolean init() {
        if (context == null) {
            return false;
        }
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.registerReceiver(bleScannerBluetoothStateReceiver, filter);
        scanResults.clear();
        initialized = true;
        return true;
    }

    /**
     * Flush pending batch scan results stored in Bluetooth controller. This will return Bluetooth
     * LE scan results batched on bluetooth controller. Returns immediately, batch scan results data
     * will be delivered through the {@code callback}.
     * <p>
     * used to start scan.
     */
    @SuppressWarnings("WeakerAccess")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public void flushPendingScanResults() {
        if (bluetoothLeScanner != null) {
            bluetoothLeScanner.flushPendingScanResults(scanCallback21);
        }
    }

    /**
     * start scan
     *
     * @return true means the scanner successfully started scanning
     */
    public boolean startScan() {
        return startScan(false);
    }

    /**
     * start scan
     *
     * @param clearScanResult whether to clear previous scan records
     * @return true means the scanner successfully started scanning
     */
    public boolean startScan(boolean clearScanResult) {
        if (context == null) {
            return false;
        }
        if (bluetoothAdapter == null) {
            return false;
        }

        if (!initialized) {
            return false;
        }

        if (!bluetoothAdapter.isEnabled()) {
            return false;
        }

        if (scanning) {
            return false;
        }

        if (clearScanResult) {
            clearScanResults();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                flushPendingScanResults();
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            try {
                bluetoothLeScanner.startScan(refreshScanFilter(), refreshScanSettings(), scanCallback21);
                scanTimer.startTimer(scanPeriod);
                scanning = true;
                return true;
            } catch (Exception e) {
                return false;
            }

        } else {
            try {
                bluetoothAdapter.startLeScan(scanCallback18);
                scanTimer.startTimer(scanPeriod);
                scanning = true;
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    /**
     * stop scan
     *
     * @return true means the scanner successfully stop scanning
     */
    public boolean stopScan() {
        return stopScan(0);
    }

    /**
     * closeGatt gatt connection
     *
     * @return true mean gatt connection closeGatt successful
     */
    public boolean close() {
        Context context = this.context;
        if (context == null) {
            return false;
        }
        if (!initialized) {
            return false;
        }

        if (scanning) {
            stopScan();
        }

        if (bleScannerBluetoothStateReceiver != null) {
            bleScannerBluetoothStateReceiver.releaseData();
            context.unregisterReceiver(bleScannerBluetoothStateReceiver);
        }

        scanPeriod = 0;
        initialized = false;
        scanning = false;
        autoStartNextScan = false;
        scanResults = null;
        bleScannerBluetoothStateReceiver = null;
        this.context = null;
        bluetoothAdapter = null;
        scanCallback18 = null;
        scanCallback21 = null;
        scanTimer = null;
        return true;
    }

    /**
     * clear scan results
     */
    public void clearScanResults() {
        scanResults.clear();
    }

    /*-----------------------------------private method-----------------------------------*/

    /**
     * initialization system scan callback for api 18
     */
    private void initBleScanCallBack18() {
        scanCallback18 = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
                final Context context = BleScanner.this.context;
                if (context == null) {
                    return;
                }
                if (scanRecord == null) {
                    return;
                }
                String name = device.getName();
                BleScanRecord bleScanRecord = BleScanRecord.parseFromBytes(scanRecord);
                if (null == name || "".equals(name)) {
                    name = bleScanRecord.getDeviceName();
                }

                if (!filterNames(name)) {
                    return;
                }

                if (!filterFullName(name)) {
                    return;
                }

                if (!filterAddress(device.getAddress())) {
                    return;
                }

                if (!filterFullAddress(device.getAddress())) {
                    return;
                }
                if (!filterServiceUuid(bleScanRecord.getServiceUuids())) {
                    return;
                }
                int primaryPhy = 1;
                int secondaryPhy = 1;
                int advertisingSid = 255;
                int periodicAdvertisingInterval = 0;
                int dataStatus = 0;
                int txPower = 127;
                long timestampNanos = System.currentTimeMillis();

                final BleDevice bleDevice = new BleDevice(device, rssi, bleScanRecord);
                bleDevice.setPrimaryPhy(primaryPhy);
                bleDevice.setSecondaryPhy(secondaryPhy);
                bleDevice.setAdvertisingSid(advertisingSid);
                bleDevice.setPeriodicAdvertisingInterval(periodicAdvertisingInterval);
                bleDevice.setDataStatus(dataStatus);
                bleDevice.setTxPower(txPower);
                bleDevice.setTimestampNanos(timestampNanos);

                callOnScanFindOneDeviceListener(bleDevice);
                if (!scanResults.contains(bleDevice)) {
                    scanResults.add(bleDevice);
                    callOnScanFindOneNewDeviceListener(scanResults.size() - 1, bleDevice, scanResults);
                }

            }
        };
    }

    /**
     * Filter device address
     *
     * @param address device address
     * @return true means pass
     */
    private boolean filterFullAddress(String address) {
        if (filterFullAddresses.size() != 0) {
            boolean pass = false;

            for (int i = 0; i < filterFullAddresses.size(); i++) {
                String filterName = filterFullAddresses.get(i);
                if (address.equals(filterName)) {
                    pass = true;
                    break;
                }
            }
            return pass;
        }
        return true;
    }

    /**
     * Filter the start character of the device address
     *
     * @param address device address
     * @return true means pass
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean filterAddress(String address) {
        if (filterAddresses.size() != 0) {
            boolean pass = false;
            for (int i = 0; i < filterAddresses.size(); i++) {
                String filterName = filterAddresses.get(i);
                if (address.startsWith(filterName)) {
                    pass = true;
                    break;
                }
            }
            return pass;
        }
        return true;
    }

    /**
     * Filter device name
     *
     * @param name device name
     * @return true means pass
     */
    private boolean filterFullName(String name) {
        if (filterFullNames.size() != 0) {
            boolean pass = false;
            if (name == null) {
                return false;
            }
            for (int i = 0; i < filterFullNames.size(); i++) {
                String filterName = filterNames.get(i);
                if (name.equals(filterName)) {
                    pass = true;
                    break;
                }
            }
            return pass;
        }
        return true;
    }

    private boolean filterServiceUuid(BleArrayList<BleParcelUuid> uuids) {

        if (filterUuids.size() != 0) {
            if (uuids == null) {
                return false;
            }
            boolean pass = false;
            for (BleParcelUuid parcelUuid : uuids) {
                String uuidInDevice = parcelUuid.getUuid().toString();
                if (filterUuids.contains(uuidInDevice)) {
                    pass = true;
                    break;
                }
            }
            return pass;
        }
        return true;
    }

    /**
     * Filter the start character of the device name
     *
     * @param name device name
     * @return true means pass
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean filterNames(String name) {
        if (filterNames.size() != 0) {
            boolean pass = false;
            if (name == null) {
                return false;
            }
            for (int i = 0; i < filterNames.size(); i++) {
                String filterName = filterNames.get(i);
                if (name.startsWith(filterName)) {
                    pass = true;
                    break;
                }
            }
            return pass;
        }
        return true;
    }

    /**
     * initialization system scan callback for api 21
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private void initBleScanCallBack21() {
        scanCallback21 = new ScanCallback() {
            /**
             * BaseBleConnectCallback when a BLE advertisement has been found.
             *
             * @param callbackType Determines how this callback was triggered. Could be one of
             *                     {@link ScanSettings#CALLBACK_TYPE_ALL_MATCHES},
             *                     {@link ScanSettings#CALLBACK_TYPE_FIRST_MATCH} or
             *                     {@link ScanSettings#CALLBACK_TYPE_MATCH_LOST}
             * @param result       A Bluetooth LE scan result.
             */
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                onApi21ScanResultProcessor(result);
            }

            /**
             * BaseBleConnectCallback when batch results are delivered.
             *
             * @param results List of scan results that are previously scanned.
             */
            @Override
            public void onBatchScanResults(final List<ScanResult> results) {
                BleManager.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (onBleScanStateChangedListener != null) {
                            onBleScanStateChangedListener.onBatchScanResults(results);
                        }
                    }
                });
            }

            /**
             * BaseBleConnectCallback when scan could not be started.
             *
             * @param errorCode Error code (one of SCAN_FAILED_*) for scan failure.
             */
            @Override
            public void onScanFailed(final int errorCode) {
                BleManager.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (onBleScanStateChangedListener != null) {
                            onBleScanStateChangedListener.onScanFailed(errorCode);
                        }
                    }
                });
            }
        };
    }

    /**
     * Processing the scan results of API21
     *
     * @param result ScanResult of API21
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private void onApi21ScanResultProcessor(ScanResult result) {
        if (context == null) {
            return;
        }

        ScanRecord scanRecord = result.getScanRecord();
        if (scanRecord == null) {
            return;
        }

        BluetoothDevice device = result.getDevice();

        String deviceName;
        deviceName = result.getDevice().getName();
        DebugUtil.warnOut(TAG, "deviceName = " + deviceName);
        if (null == deviceName || "".equals(deviceName)) {
            deviceName = scanRecord.getDeviceName();
            DebugUtil.warnOut(TAG, "deviceName = " + deviceName);
        }
        if (null == deviceName || "".equals(deviceName)) {
            deviceName = scanRecord.getDeviceName();
            DebugUtil.warnOut(TAG, "deviceName = " + deviceName);
        }
        String address = device.getAddress();
        DebugUtil.warnOut(TAG, "address = " + address);
        BleScanRecord bleScanRecord = BleScanRecord.parseFromBytes(scanRecord.getBytes());

        if (null == deviceName || "".equals(deviceName)) {
            deviceName = bleScanRecord.getDeviceName();
            DebugUtil.warnOut(TAG, "deviceName = " + deviceName);
        }

        if (!filterNames(deviceName)) {
            return;
        }

        if (!filterAddress(address)) {
            return;
        }

        int rssi = result.getRssi();

        long timestampNanos = result.getTimestampNanos();
        int primaryPhy;
        int secondaryPhy;
        int advertisingSid;
        int periodicAdvertisingInterval;
        int dataStatus;
        int txPower;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            primaryPhy = result.getPrimaryPhy();
            secondaryPhy = result.getSecondaryPhy();
            advertisingSid = result.getAdvertisingSid();
            periodicAdvertisingInterval = result.getPeriodicAdvertisingInterval();
            dataStatus = result.getDataStatus();
            txPower = result.getTxPower();
        } else {
            primaryPhy = 1;
            secondaryPhy = 1;
            advertisingSid = 255;
            periodicAdvertisingInterval = 0;
            dataStatus = 0;
            txPower = 127;
        }

        DebugUtil.warnOut(TAG, "primaryPhy = " + primaryPhy);
        DebugUtil.warnOut(TAG, "secondaryPhy = " + secondaryPhy);

        final BleDevice bleDevice = new BleDevice(device, rssi, bleScanRecord);

        bleDevice.setPrimaryPhy(primaryPhy);
        bleDevice.setSecondaryPhy(secondaryPhy);
        bleDevice.setAdvertisingSid(advertisingSid);
        bleDevice.setPeriodicAdvertisingInterval(periodicAdvertisingInterval);
        bleDevice.setDataStatus(dataStatus);
        bleDevice.setTxPower(txPower);
        bleDevice.setTimestampNanos(timestampNanos);

        callOnScanFindOneDeviceListener(bleDevice);

        if (scanResults == null) {
            return;
        }
        if (!scanResults.contains(bleDevice)) {
            scanResults.add(bleDevice);
            callOnScanFindOneNewDeviceListener(scanResults.size() - 1, bleDevice, scanResults);
        } else {
            int index = scanResults.indexOf(bleDevice);
            BleDevice bleDevice1 = scanResults.get(index);
            if (bleDevice1.getDeviceName() == null && bleDevice.getDeviceName() != null) {
                scanResults.set(index, bleDevice);
                callOnScanFindOneNewDeviceListener(index, null, scanResults);
            }
        }
    }

    /**
     * refresh filter of api 21
     *
     * @return ScanFilter list
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private ArrayList<ScanFilter> refreshScanFilter() {
        ArrayList<ScanFilter> scanFilters = new ArrayList<>();
        for (int i = 0; i < filterFullNames.size(); i++) {
            String filterName = filterFullNames.get(i);
            ScanFilter scanFilter = new ScanFilter.Builder()
                    .setDeviceName(filterName)
                    .build();
            scanFilters.add(scanFilter);
        }

        for (int i = 0; i < filterFullAddresses.size(); i++) {
            String filterFullAddress = filterFullAddresses.get(i);
            ScanFilter scanFilter = new ScanFilter.Builder()
                    .setDeviceAddress(filterFullAddress)
                    .build();
            scanFilters.add(scanFilter);
        }

        for (int i = 0; i < filterUuids.size(); i++) {
            String uuid = filterUuids.get(i);
            ScanFilter scanFilter = new ScanFilter.Builder()
                    .setServiceUuid(ParcelUuid.fromString(uuid))
                    .build();
            scanFilters.add(scanFilter);
        }

        scanFilters.addAll(customScanFilters);
        return scanFilters;
    }

    /**
     * refresh scan settings if apu 21
     *
     * @return ScanSettings
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private ScanSettings refreshScanSettings() {
        ScanSettings.Builder builder = new ScanSettings.Builder();
        builder.setScanMode(bleScanMode.getScanMode())
                .setReportDelay(reportDelay);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            builder.setMatchMode(bleMatchMode.getMatchMode())
                    .setCallbackType(bleCallbackType.getValue())
                    .setNumOfMatches(bleNumOfMatches.getValue());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (bluetoothAdapter.isLeCodedPhySupported()) {
                DebugUtil.warnOut(TAG, "isLeCodedPhySupported = true");
                builder.setLegacy(legacy)
                        .setPhy(scanPhy.getValue());
            } else {
                DebugUtil.warnOut(TAG, "isLeCodedPhySupported = false");
                builder.setLegacy(true)
                        .setPhy(ScanPhy.PHY_LE_ALL_SUPPORTED.getValue());
            }
        }
        return builder.build();
    }

    /**
     * stop scan
     *
     * @param tryCount try count
     * @return true means successful
     */
    private boolean stopScan(int tryCount) {
        tryCount++;
        Context context = this.context;
        if (context == null) {
            return false;
        }
        if (!initialized) {
            return false;
        }

        if (!scanning) {
            return false;
        }

        if (bluetoothAdapter == null) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (bluetoothLeScanner == null) {
                return false;
            }
            try {
                bluetoothLeScanner.stopScan(scanCallback21);
                scanTimer.stopTimer();
                scanning = false;
                return true;
            } catch (Exception e) {
                if (tryCount >= 3) {
                    return false;
                }
                return stopScan(tryCount);
            }
        } else {
            try {
                this.bluetoothAdapter.stopLeScan(scanCallback18);
                scanTimer.stopTimer();
                scanning = false;
                return true;
            } catch (Exception e) {
                if (tryCount >= 3) {
                    return false;
                }
                return stopScan(tryCount);
            }
        }
    }

    private void callOnScanFindOneNewDeviceListener(final int inedx, final BleDevice bleDevice, final ArrayList<BleDevice> mScanResults) {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (onBleScanStateChangedListener != null) {
                    onBleScanStateChangedListener.onScanFindOneNewDevice(inedx, bleDevice, mScanResults);
                }
            }
        });
    }

    private void callOnScanFindOneDeviceListener(final BleDevice bleDevice) {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (onBleScanStateChangedListener != null) {
                    onBleScanStateChangedListener.onScanFindOneDevice(bleDevice);
                }
            }
        });
    }
}
