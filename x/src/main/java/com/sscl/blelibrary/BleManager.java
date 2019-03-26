package com.sscl.blelibrary;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;

import com.sscl.blelibrary.interfaces.OnBluetoothStateChangedListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * BlE Manager
 *
 * @author jackie
 */

@SuppressWarnings("unused")
public final class BleManager {

    /*-----------------------------------static constant-----------------------------------*/

    private static final String TAG = BleManager.class.getSimpleName();

    /**
     * Broadcast receiver listening to Bluetooth status
     */
    private static final BluetoothStateReceiver BLUETOOTH_STATE_RECEIVER = new BluetoothStateReceiver();
    /**
     * handler
     */
    private static final Handler HANDLER = new Handler();
    /**
     * 线程池工厂
     */
    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {
        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r);
        }
    };

    /*-----------------------------------field variables-----------------------------------*/

    /**
     * BleConnector singleton
     */
    @Nullable
    @SuppressLint("StaticFieldLeak")
    private static BleConnector bleConnector;
    /**
     * BleScanner singleton
     */
    @Nullable
    @SuppressLint("StaticFieldLeak")
    private static BleScanner bleScanner;
    /**
     * BleMultiConnector singleton
     */
    @Nullable
    @SuppressLint("StaticFieldLeak")
    private static BleMultiConnector bleMultiConnector;
    /**
     * BleAdvertiser singleton
     */
    @Nullable
    @SuppressLint("StaticFieldLeak")
    private static BleAdvertiser bleAdvertiser;
    /**
     * Ble broadcast instance has been reset (Avoid infinite loop calls)
     */
    private static boolean resetBleAdvertiserFlag;
    /**
     * Context
     */
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    /**
     * Service Connection for {@link BluetoothLeService}
     */
    private static BleServiceConnection bleServiceConnection = new BleServiceConnection();

    private static BluetoothLeService bluetoothLeService;

    /*-----------------------------------Package private method-----------------------------------*/

    /**
     * Reset bleMultiConnector
     */
    static void resetBleMultiConnector() {
        if (bleMultiConnector != null) {
            bleMultiConnector.closeAll();
        }
        bleMultiConnector = null;
    }

    /**
     * Reset the Ble broadcast instance
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    static void resetBleAdvertiser() {
        if (resetBleAdvertiserFlag) {
            return;
        }
        resetBleAdvertiserFlag = true;

        if (bleAdvertiser != null) {
            bleAdvertiser.close();
        }
        bleAdvertiser = null;
        resetBleAdvertiserFlag = false;
    }

    /*-----------------------------------public static method-----------------------------------*/

    /**
     * Unbind a device directly from the device address
     *
     * @param context Context
     * @param address Device address
     * @return true means request send success
     */
    @SuppressWarnings("WeakerAccess")
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    public static boolean unBound(@Nullable Context context, @NonNull String address) {

        if (context == null) {
            return false;
        }

        if (!BluetoothAdapter.checkBluetoothAddress(address)) {
            return false;
        }

        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);

        if (bluetoothManager == null) {
            return false;
        }

        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null) {
            return false;
        }

        BluetoothDevice remoteDevice = bluetoothAdapter.getRemoteDevice(address);

        if (remoteDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
            return false;
        }

        Method removeBondMethod;
        boolean result = false;
        try {
            //noinspection JavaReflectionMemberAccess
            removeBondMethod = BluetoothDevice.class.getMethod("removeBond");
            result = (boolean) removeBondMethod.invoke(remoteDevice);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        if (result) {
            DebugUtil.warnOut(TAG, "remove bound request success");
        } else {
            DebugUtil.warnOut(TAG, "remove bound request failed");
        }

        return result;
    }

    /**
     * Determine if the phone supports BLE
     *
     * @return true means support
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isSupportBle() {
        checkInitStatus();
        if (context == null) {
            return false;
        }
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    /**
     * initialization
     *
     * @param context Context
     */
    public static void init(@NonNull Context context) {
        BleManager.context = context.getApplicationContext();
        BleManager.context.registerReceiver(BLUETOOTH_STATE_RECEIVER, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        Intent intent = new Intent(BleManager.getContext(), BluetoothLeService.class);
        BleManager.context.bindService(intent, bleServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Create a new BleConnector
     *
     * @return BleConnector
     */
    public static BleConnector newBleConnector() {
        checkInitStatus();
        if (!isSupportBle()) {
            return null;
        }
        return new BleConnector(bluetoothLeService);
    }

    public static Context getContext() {
        return context;
    }

    /**
     * Get a singleton of BleConnector
     *
     * @return singleton of BleConnector
     */
    public static BleConnector getBleConnectorInstance() {
        checkInitStatus();
        if (!isSupportBle()) {
            return null;
        }

        if (bleConnector == null) {
            synchronized (BleManager.class) {
                if (bleConnector == null) {
                    bleConnector = new BleConnector(bluetoothLeService);
                }
            }
        }
        return bleConnector;
    }

    /**
     * Create a new BleScanner
     *
     * @return BleScanner
     */
    public static BleScanner newBleScanner() {
        checkInitStatus();
        if (!isSupportBle()) {
            return null;
        }
        return new BleScanner(context);
    }

    /**
     * Get a singleton of BleScanner
     *
     * @return singleton of BleScanner
     */
    public static BleScanner getBleScannerInstance() {
        checkInitStatus();
        if (!isSupportBle()) {
            return null;
        }
        if (bleScanner == null) {
            synchronized (BleManager.class) {
                if (bleScanner == null) {
                    bleScanner = new BleScanner(context);
                }
            }
        } else {
            if (bleScanner.getContext() == null) {
                bleScanner = null;
            }

            if (bleScanner == null) {
                synchronized (BleManager.class) {
                    if (bleScanner == null) {
                        bleScanner = new BleScanner(context);
                    }
                }
            }
        }
        return bleScanner;
    }

    /**
     * Get a singleton of BleAdvertiser
     *
     * @return BleAdvertiser
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public static BleAdvertiser getBleAdvertiserInstance() {
        checkInitStatus();
        if (!isSupportBle()) {
            return null;
        }
        if (bleAdvertiser == null) {
            synchronized (BleManager.class) {
                if (bleAdvertiser == null) {
                    bleAdvertiser = new BleAdvertiser(context);
                }
            }
        }
        return bleAdvertiser;
    }

    /**
     * Create a new BleAdvertiser
     *
     * @return BleAdvertiser
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public static BleAdvertiser newBleAdvertiser() {
        checkInitStatus();
        if (!isSupportBle()) {
            return null;
        }

        return new BleAdvertiser(context);
    }


    /**
     * Get a singleton of BleMultiConnector
     *
     * @return BleMultiConnector
     */
    public static BleMultiConnector getBleMultiConnectorInstance() {
        checkInitStatus();
        if (!isSupportBle()) {
            return null;
        }
        if (bleMultiConnector == null) {
            synchronized (BleManager.class) {
                if (bleMultiConnector == null) {
                    bleMultiConnector = new BleMultiConnector();
                }
            }
        }
        return bleMultiConnector;
    }


    /**
     * Create a new BleMultiConnector
     *
     * @return BleMultiConnector
     */
    public static BleMultiConnector newBleMultiConnector() {
        checkInitStatus();
        if (!isSupportBle()) {
            return null;
        }
        return new BleMultiConnector();
    }

    /**
     * Determine if Bluetooth is enable
     *
     * @return true means enable
     */
    public static boolean isBluetoothOpened() {
        checkInitStatus();
        if (!isSupportBle()) {
            return false;
        }
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null) {
            return false;
        }
        BluetoothAdapter adapter = bluetoothManager.getAdapter();
        return adapter != null && adapter.isEnabled();
    }

    /**
     * request to enable or disable bluetooth
     *
     * @return true means request success
     */
    public static boolean enableBluetooth(boolean enable) {
        checkInitStatus();
        if (!isSupportBle()) {
            return false;
        }
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null) {
            return false;
        }
        BluetoothAdapter adapter = bluetoothManager.getAdapter();
        if (adapter == null) {
            return false;
        }
        if (enable) {
            return adapter.enable();
        } else {
            return adapter.disable();
        }
    }

    /**
     * Release the resources of the BleConnector
     */
    public static void releaseBleConnector() {
        checkInitStatus();
        if (bleConnector != null) {
            bleConnector.close();
            bleConnector = null;
        }
    }

    /**
     * Release the resources of the BleScanner
     */
    @SuppressWarnings("WeakerAccess")
    public static void releaseBleScanner() {
        checkInitStatus();
        if (bleScanner != null) {
            bleScanner.close();
            bleScanner = null;
        }
    }

    /**
     * Release the resources of the BleScanner
     */
    public static void addOnBluetoothStateChangedListener(@NonNull OnBluetoothStateChangedListener onBluetoothStateChangedListener) {
        BLUETOOTH_STATE_RECEIVER.addOnBluetoothStateChangedListener(onBluetoothStateChangedListener);
    }

    /**
     * remove  Bluetooth status changed listener
     *
     * @param onBluetoothStateChangedListener Bluetooth status changed listener
     */
    public static void removeOnBluetoothStateChangedListener(@NonNull OnBluetoothStateChangedListener onBluetoothStateChangedListener) {
        BLUETOOTH_STATE_RECEIVER.removeOnBluetoothStateChangedListener(onBluetoothStateChangedListener);
    }

    /**
     * Release the resources of the BleMultiConnector
     */
    @SuppressWarnings("WeakerAccess")
    public static void releaseBleMultiConnector() {
        checkInitStatus();
        if (bleMultiConnector != null) {
            bleMultiConnector.closeAll();
            bleConnector = null;
        }
    }

    /**
     * Release the resources of the BleAdvertiser
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressWarnings("WeakerAccess")
    public static void releaseBleAdvertiser() {
        checkInitStatus();
        if (bleAdvertiser != null) {
            bleAdvertiser.close();
            bleAdvertiser = null;
        }
    }

    /**
     * Release all resources
     */
    public static void releaseAll() {
        checkInitStatus();
        releaseBleConnector();
        releaseBleScanner();
        releaseBleMultiConnector();
        BLUETOOTH_STATE_RECEIVER.removeAllOnBluetoothStateChangedListener();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            releaseBleAdvertiser();
        }
    }

    /*-----------------------------------private static method-----------------------------------*/

    /**
     * Check if the initialization is successful
     */
    private static void checkInitStatus() {
        if (context == null) {
            throw new IllegalStateException("Please invoke method \"init(Context context)\" in your Applications class");
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static ScheduledExecutorService newScheduledExecutorService() {
        return new ScheduledThreadPoolExecutor(20, THREAD_FACTORY);
    }

    public static Handler getHANDLER() {
        return HANDLER;
    }

    static ThreadFactory getThreadFactory() {
        return THREAD_FACTORY;
    }

    static void setBluetoothLeService(BluetoothLeService bluetoothLeService) {
        BleManager.bluetoothLeService = bluetoothLeService;
    }
}
