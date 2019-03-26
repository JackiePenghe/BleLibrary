package com.sscl.blelibrary;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import com.sscl.blelibrary.enums.PhyMask;
import com.sscl.blelibrary.enums.Transport;
import com.sscl.blelibrary.interfaces.OnBleCharacteristicWriteListener;
import com.sscl.blelibrary.interfaces.OnBleConnectStateChangedListener;
import com.sscl.blelibrary.interfaces.OnBleDescriptorWriteListener;
import com.sscl.blelibrary.interfaces.OnBleReceiveNotificationListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * BLE Connection service
 *
 * @author jackie
 */

public final class BluetoothLeService extends Service {

    /*-----------------------------------static constant-----------------------------------*/

    /**
     * TAG
     */
    private static final String TAG = BluetoothLeService.class.getSimpleName();

    /*-----------------------------------field variables-----------------------------------*/

    /**
     * Binder instance
     */
    private BluetoothLeServiceBinder bluetoothLeServiceBinder;

    /**
     * Required callback for Bluetooth GATT connection
     */
    private BleBluetoothGattCallback bleBluetoothGattCallback = new BleBluetoothGattCallback();


    /**
     * Bluetooth Manager
     */
    @Nullable
    private BluetoothManager bluetoothManager;

    /**
     * Bluetooth Adapter
     */
    @Nullable
    private BluetoothAdapter bluetoothAdapter;

    /**
     * Bluetooth Gatt
     */
    @Nullable
    private BluetoothGatt bluetoothGatt;

    /*-----------------------------------Override Method-----------------------------------*/

    /**
     * Called by the system when the service is first created.  Do not call this method directly.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        bluetoothLeServiceBinder = new BluetoothLeServiceBinder(BluetoothLeService.this);
    }

    /**
     * Called by the system to notify a Service that it is no longer used and is being removed.  The
     * service should clean up any resources it holds (threads, registered
     * receivers, etc) at this point.  Upon return, there will be no more calls
     * in to this Service object and it is effectively dead.  Do not call this method directly.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        bluetoothLeServiceBinder.releaseData();
        bluetoothLeServiceBinder = null;
        bleBluetoothGattCallback = null;
        bluetoothManager = null;
        bluetoothAdapter = null;
        bluetoothGatt = null;
    }

    /*-----------------------------------Implementation Method-----------------------------------*/

    /**
     * Return the communication channel to the service.  May return null if
     * clients can not bind to the service.  The returned
     * {@link IBinder} is usually for a complex interface
     * that has been <a href="{@docRoot}guide/components/aidl.html">described using
     * aidl</a>.
     * <p>
     * <p><em>Note that unlike other application components, calls on to the
     * IBinder interface returned here may not happen on the main thread
     * of the process</em>.  More information about the main thread can be found in
     * <a href="{@docRoot}guide/topics/ public booleandamentals/processes-and-threads.html">Processes and
     * Threads</a>.</p>
     *
     * @param intent The Intent that was used to bind to this service,
     *               as given to {@link Context#bindService
     *               Context.bindService}.  Note that any extras that were included with
     *               the Intent at that point will <em>not</em> be seen here.
     * @return Return an IBinder through which clients can call on to the
     * service.
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        DebugUtil.warnOut(TAG, "BLE connect service get successful");
        return bluetoothLeServiceBinder;
    }

    /*-----------------------------------getter-----------------------------------*/

    /**
     * get Bluetooth Adapter
     *
     * @return Bluetooth Adapter
     */
    @Nullable
    BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }


    /*-----------------------------------package private Method-----------------------------------*/

    /**
     * initialization
     *
     * @return true means initialization successful
     */
    boolean initialize() {

        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        if (bluetoothManager == null) {
            DebugUtil.warnOut(TAG, "get bluetoothManager failed!");
            return false;
        }

        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            DebugUtil.warnOut(TAG, "get bluetoothAdapter failed!");
            return false;
        }
        return true;
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param address       device address
     * @param autoReconnect Whether to automatically reconnect
     * @param transport     preferred transport for GATT connections to remote dual-mode devices {@link
     *                      BluetoothDevice#TRANSPORT_AUTO} or {@link BluetoothDevice#TRANSPORT_BREDR} or {@link
     *                      BluetoothDevice#TRANSPORT_LE}
     * @param phyMask       preferred PHY for connections to remote LE device. Bitwise OR of any of {@link
     *                      BluetoothDevice#PHY_LE_1M_MASK}, {@link BluetoothDevice#PHY_LE_2M_MASK}, and {@link
     *                      BluetoothDevice#PHY_LE_CODED_MASK}. This option does not take effect if {@code autoConnect}
     * @return true means request successful
     */
    boolean connect(@NonNull String address, boolean autoReconnect, @Nullable Transport transport, @Nullable PhyMask phyMask) {
        if (bluetoothAdapter == null) {
            return false;
        }
        if (!BluetoothAdapter.checkBluetoothAddress(address)) {
            return false;
        }
        BluetoothDevice remoteDevice = bluetoothAdapter.getRemoteDevice(address);
        if (remoteDevice == null) {
            return false;
        }

        return connect(remoteDevice, autoReconnect, transport, phyMask);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param bluetoothDevice remote device
     * @param autoReconnect   Whether to automatically reconnect
     * @param transport       preferred transport for GATT connections to remote dual-mode devices {@link
     *                        BluetoothDevice#TRANSPORT_AUTO} or {@link BluetoothDevice#TRANSPORT_BREDR} or {@link
     *                        BluetoothDevice#TRANSPORT_LE}
     * @param phyMask         preferred PHY for connections to remote LE device. Bitwise OR of any of {@link
     *                        BluetoothDevice#PHY_LE_1M_MASK}, {@link BluetoothDevice#PHY_LE_2M_MASK}, and {@link
     *                        BluetoothDevice#PHY_LE_CODED_MASK}. This option does not take effect if {@code autoConnect}
     * @return true means request successful.
     */
    boolean connect(@NonNull BluetoothDevice bluetoothDevice, boolean autoReconnect, @Nullable Transport transport, @Nullable PhyMask phyMask) {
        if (bluetoothAdapter == null) {
            return false;
        }
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            bluetoothGatt = null;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            bluetoothGatt = bluetoothDevice.connectGatt(this, autoReconnect, bleBluetoothGattCallback);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            if (transport == null) {
                transport = Transport.TRANSPORT_AUTO;
            }
            bluetoothGatt = bluetoothDevice.connectGatt(this, autoReconnect, bleBluetoothGattCallback, transport.getValue());
        } else {
            if (transport == null) {
                transport = Transport.TRANSPORT_AUTO;
            }
            if (phyMask == null) {
                phyMask = PhyMask.PHY_LE_1M_MASK;
            }
            bluetoothGatt = bluetoothDevice.connectGatt(this, autoReconnect, bleBluetoothGattCallback, transport.getValue(), phyMask.getValue());
        }
        return bluetoothGatt != null;
    }

    /**
     * Initiate a disconnect request
     *
     * @return true means request successful.
     */
    boolean disconnect() {
        if (bluetoothGatt == null) {
            return false;
        }
        bluetoothGatt.disconnect();
        return true;
    }

    /**
     * closeGatt GATT connection
     *
     * @return true means close Gatt successful
     */
    boolean closeGatt() {
        if (bluetoothGatt == null) {
            return false;
        }
        boolean result;
        try {
            bluetoothGatt.close();
            result = true;
        } catch (Exception e) {
            result = false;
        } finally {
            bluetoothGatt = null;
        }
        return result;
    }

    /**
     * write data to remote device.Result for request will be trigger callback {@link OnBleConnectStateChangedListener#writeCharacteristicData(BluetoothGattCharacteristic, byte[])}
     *
     * @param serviceUUID        service UUID
     * @param characteristicUUID characteristic UUID
     * @param data               data
     * @return true means request successful
     */
    boolean writeData(@NonNull String serviceUUID, @NonNull String characteristicUUID, @NonNull byte[] data) {
        DebugUtil.warnOut(TAG, "bluetoothGatt == " + bluetoothGatt);
        if (bluetoothGatt == null) {
            return false;
        }
        BluetoothGattService service = bluetoothGatt.getService(UUID.fromString(serviceUUID));
        DebugUtil.warnOut(TAG, "service from uuid = " + service);
        if (service == null) {
            return false;
        }
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(characteristicUUID));

        DebugUtil.warnOut(TAG, "characteristic from uuid = " + characteristic);
        if (characteristic == null) {
            return false;
        }

        if (!canWrite(characteristic)) {
            return false;
        }

        if (!characteristic.setValue(data)) {
            DebugUtil.warnOut(TAG, "characteristic setValue failed");
            return false;
        }
        DebugUtil.warnOut(TAG, "characteristic setValue success");

        DebugUtil.warnOut(TAG, "values = " + ConversionUtil.bytesToHexStr(data));
        return bluetoothGatt.writeCharacteristic(characteristic);
    }

    /**
     * read data from remote device.Result for request will be trigger callback {@link OnBleConnectStateChangedListener#readCharacteristicData(BluetoothGattCharacteristic, byte[])}
     *
     * @param serviceUUID        service UUID
     * @param characteristicUUID characteristic UUID
     * @return true means request successful
     */
    boolean readData(@NonNull String serviceUUID, @NonNull String characteristicUUID) {
        if (bluetoothGatt == null) {
            return false;
        }
        BluetoothGattService service = bluetoothGatt.getService(UUID.fromString(serviceUUID));
        if (service == null) {
            return false;
        }

        BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(characteristicUUID));

        //noinspection SimplifiableIfStatement
        if (characteristic == null) {
            return false;
        }
        if (!canRead(characteristic)) {
            return false;
        }
        return bluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * enable or disable notification
     *
     * @param serviceUUID        service UUID
     * @param characteristicUUID characteristic UUID
     * @param enable             true means enable notification,false means disable notification
     * @return true means successful
     */
    boolean enableNotification(@NonNull String serviceUUID, @NonNull String characteristicUUID, boolean enable) {
        if (bluetoothGatt == null) {
            return false;
        }
        BluetoothGattService bluetoothGattService = bluetoothGatt.getService(UUID.fromString(serviceUUID));
        if (bluetoothGattService == null) {
            return false;
        }
        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(characteristicUUID));
        if (!bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic, enable)) {
            return false;
        }
        BluetoothGattDescriptor bluetoothGattDescriptor = bluetoothGattCharacteristic.getDescriptor(UUID.fromString(BleConstants.CLIENT_CHARACTERISTIC_CONFIG));
        if (bluetoothGattDescriptor == null) {
            return false;
        } else {
            bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        }
        return bluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
    }

    /**
     * get remote device RSSI.Result for request will be trigger callback {@link OnBleConnectStateChangedListener#readRemoteRssi(int)}
     *
     * @return true means request successful
     */
    boolean getRssi() {
        if (bluetoothGatt == null) {
            return false;
        }
        return bluetoothGatt.readRemoteRssi();
    }

    /**
     * refresh GATT cache.
     * Notice:Some Custom system return true but not take effect.There is no solution so far.
     *
     * @return true means successful.
     */
    boolean refreshGattCache() {
        if (bluetoothGatt == null) {
            return false;
        }

        try {
            //noinspection JavaReflectionMemberAccess
            Method refresh = bluetoothGatt.getClass().getMethod("refresh");
            return (boolean) refresh.invoke(bluetoothGatt);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * get remote service list
     *
     * @return service Bluetooth Gatt list
     */
    @Nullable
    List<BluetoothGattService> getServices() {
        if (bluetoothGatt == null) {
            return null;
        }
        return bluetoothGatt.getServices();
    }

    /**
     * get remote device service by UUID
     *
     * @param uuid UUID
     * @return Bluetooth Gatt Service
     */
    @Nullable
    BluetoothGattService getService(UUID uuid) {
        if (bluetoothGatt == null) {
            return null;
        }
        return bluetoothGatt.getService(uuid);
    }

    /**
     * request change mtu value.Result of request will be trigger callback{@link OnBleConnectStateChangedListener#mtuChanged(int)}
     *
     * @param mtu mtu value
     * @return true means request send successful.
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    boolean requestMtu(int mtu) {
        return bluetoothGatt != null && bluetoothGatt.requestMtu(mtu);
    }

    /**
     * Get BluetoothGatt instance
     *
     * @return BluetoothGatt instance
     */
    @Nullable
    BluetoothGatt getBluetoothGatt() {
        return bluetoothGatt;
    }

    /**
     * Check for support notifications
     *
     * @param characteristic BluetoothGattCharacteristic
     * @return true means support
     */
    boolean canNotify(@NonNull BluetoothGattCharacteristic characteristic) {
        int properties = characteristic.getProperties();
        return (properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0;
    }

    /**
     * Check for support read
     *
     * @param characteristic BluetoothGattCharacteristic
     * @return true means support
     */
    boolean canRead(@NonNull BluetoothGattCharacteristic characteristic) {
        int properties = characteristic.getProperties();
        return (properties & BluetoothGattCharacteristic.PROPERTY_READ) != 0;
    }

    /**
     * Check for support write(Signed)
     *
     * @param characteristic BluetoothGattCharacteristic
     * @return true means support
     */
    boolean canSignedWrite(@NonNull BluetoothGattCharacteristic characteristic) {
        int properties = characteristic.getProperties();
        return (properties & BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE) != 0;
    }

    /**
     * Check for support write
     *
     * @param characteristic BluetoothGattCharacteristic
     * @return true means support
     */
    boolean canWrite(@NonNull BluetoothGattCharacteristic characteristic) {
        int properties = characteristic.getProperties();
        return (properties & BluetoothGattCharacteristic.PROPERTY_WRITE) != 0;
    }

    /**
     * Check for support write(no response)
     *
     * @param characteristic BluetoothGattCharacteristic
     * @return true means support
     */
    boolean canWriteNoResponse(@NonNull BluetoothGattCharacteristic characteristic) {

        int properties = characteristic.getProperties();
        return (properties & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0;
    }

    /**
     * get connection status
     *
     * @return true means remote device is connected
     */
    boolean isConnected() {
        return bluetoothGatt != null && bleBluetoothGattCallback.isConnected();
    }

    /**
     * get service discovered status
     *
     * @return true means remote device is discovered
     */
    boolean isServiceDiscovered() {
        return bluetoothGatt != null && bleBluetoothGattCallback.isServiceDiscovered();
    }

    /**
     * add a callback triggered when descriptor write successful
     *
     * @param onBleDescriptorWriteListener callback triggered when descriptor write successful
     * @return true means successful
     */
    boolean addOnBleDescriptorWriteListener(@NonNull OnBleDescriptorWriteListener onBleDescriptorWriteListener) {
        return bleBluetoothGattCallback.addOnBleDescriptorWriteListener(onBleDescriptorWriteListener);
    }

    /**
     * remove a callback triggered when descriptor write successful
     *
     * @param onBleDescriptorWriteListener callback triggered when descriptor write successful
     * @return true means successful
     */
    boolean removeOnBleDescriptorWriteListener(@NonNull OnBleDescriptorWriteListener onBleDescriptorWriteListener) {
        return bleBluetoothGattCallback.removeOnBleDescriptorWriteListener(onBleDescriptorWriteListener);
    }

    /**
     * add a callback triggered when received notification data
     *
     * @param onBleReceiveNotificationListener callback triggered when descriptor write successful
     * @return true means successful
     */
    boolean addOnBleReceiveNotificationListener(@NonNull OnBleReceiveNotificationListener onBleReceiveNotificationListener) {
        return bleBluetoothGattCallback.addOnBleReceiveNotificationListener(onBleReceiveNotificationListener);
    }

    /**
     * remove a callback triggered when received notification data
     *
     * @param onBleReceiveNotificationListener callback triggered when received notification data
     * @return true means successful
     */
    boolean removeOnBleReceiveNotificationListener(@NonNull OnBleReceiveNotificationListener onBleReceiveNotificationListener) {
        return bleBluetoothGattCallback.removeOnBleReceiveNotificationListener(onBleReceiveNotificationListener);
    }

    /**
     * add a callback triggered when gatt characteristic write data successful
     *
     * @param onBleCharacteristicWriteListener callback triggered when gatt characteristic write data successful
     * @return true means successful
     */
    boolean addOnBleCharacteristicWriteListener(@NonNull OnBleCharacteristicWriteListener onBleCharacteristicWriteListener) {
        return bleBluetoothGattCallback.addOnBleCharacteristicWriteListener(onBleCharacteristicWriteListener);
    }

    /**
     * remove a callback triggered when gatt characteristic write data successful
     *
     * @param onBleCharacteristicWriteListener callback triggered when gatt characteristic write data successful
     * @return true means successful
     */
    boolean removeOnBleCharacteristicWriteListener(@NonNull OnBleCharacteristicWriteListener onBleCharacteristicWriteListener) {
        return bleBluetoothGattCallback.removeOnBleCharacteristicWriteListener(onBleCharacteristicWriteListener);
    }

    /**
     * set BLE device connect status changed listener
     *
     * @param onBleConnectStateChangedListener BLE device connect status changed listener
     */
    void setOnBleConnectStateChangedListener(@Nullable OnBleConnectStateChangedListener onBleConnectStateChangedListener) {
        bleBluetoothGattCallback.setOnBleConnectStateChangedListener(onBleConnectStateChangedListener);

    }

    /**
     * Initiates a reliable write transaction for a given remote device.
     *
     * <p>Once a reliable write transaction has been initiated, all calls
     * to {@link BluetoothGatt#writeCharacteristic} are sent to the remote device for
     * verification and queued up for atomic execution. The application will
     * receive an {@link BluetoothGattCallback#onCharacteristicWrite} callback
     * in response to every {@link BluetoothGatt#writeCharacteristic} call and is responsible
     * for verifying if the value has been transmitted accurately.
     *
     * <p>After all characteristics have been queued up and verified,
     * {@link #executeReliableWrite} will execute all writes. If a characteristic
     * was not written correctly, calling {@link #abortReliableWrite} will
     * cancel the current transaction without commiting any values on the
     * remote device.
     *
     * <p>Requires {@link android.Manifest.permission#BLUETOOTH} permission.
     *
     * @return true, if the reliable write transaction has been initiated
     */
    boolean beginReliableWrite() {
        if (bluetoothGatt == null) {
            return false;
        }
        return bluetoothGatt.beginReliableWrite();
    }

    /**
     * Cancels a reliable write transaction for a given device.
     *
     * <p>Calling this function will discard all queued characteristic write
     * operations for a given remote device.
     *
     * <p>Requires {@link android.Manifest.permission#BLUETOOTH} permission.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    boolean abortReliableWrite() {
        if (bluetoothGatt == null) {
            return false;
        }
        bluetoothGatt.abortReliableWrite();
        return true;
    }

    /**
     * Discovers services offered by a remote device as well as their
     * characteristics and descriptors.
     *
     * <p>This is an asynchronous operation. Once service discovery is completed,
     * the {@link BluetoothGattCallback#onServicesDiscovered} callback is
     * triggered. If the discovery was successful, the remote services can be
     * retrieved using the {@link #getServices} function.
     *
     * <p>Requires {@link android.Manifest.permission#BLUETOOTH} permission.
     *
     * @return true, if the remote service discovery has been started
     */
    boolean discoverServices() {
        if (bluetoothGatt == null) {
            return false;
        }
        return bluetoothGatt.discoverServices();
    }

    /**
     * Executes a reliable write transaction for a given remote device.
     *
     * <p>This function will commit all queued up characteristic write
     * operations for a given remote device.
     *
     * <p>A {@link BluetoothGattCallback#onReliableWriteCompleted} callback is
     * invoked to indicate whether the transaction has been executed correctly.
     *
     * <p>Requires {@link android.Manifest.permission#BLUETOOTH} permission.
     *
     * @return true, if the request to execute the transaction has been sent
     */
    boolean executeReliableWrite() {
        if (bluetoothGatt == null) {
            return false;
        }
        return bluetoothGatt.executeReliableWrite();
    }
}
