package com.sscl.blelibrary.interfaces;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.sscl.blelibrary.BaseBleConnectCallback;
import com.sscl.blelibrary.enums.PhyMask;
import com.sscl.blelibrary.enums.Transport;

import java.util.List;
import java.util.UUID;

/**
 * 连接器接口
 *
 * @author jackie
 */
public interface MultiConnector {

    /**
     * request change mtu value.Result of request will be trigger callback{@link OnBleConnectStateChangedListener#mtuChanged(int)}
     *
     * @param mtu mtu value
     * @return true means request send successful.
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    boolean requestMtu(int mtu);

    /**
     * Disconnect remote device
     *
     * @return true means disconnect success
     */
    boolean disconnect();

    /**
     * Reconnect device
     *
     * @return true means request successful
     */
    boolean reConnect();

    /**
     * Initiate a request to connect to a remote device
     *
     * @param address                device address
     * @param baseBleConnectCallback 连接相关的回调
     * @return true means request successful
     */
    boolean connect(@NonNull String address, @NonNull BaseBleConnectCallback baseBleConnectCallback);

    /**
     * Initiate a request to connect to a remote device
     *
     * @param address                device address
     * @param autoReconnect          Whether to automatically reconnect
     * @param baseBleConnectCallback 连接相关的回调
     * @return true means request successful
     */
    boolean connect(@NonNull String address, boolean autoReconnect, @NonNull BaseBleConnectCallback baseBleConnectCallback);

    /**
     * Initiate a request to connect to a remote device
     *
     * @param address                device address
     * @param transport              preferred transport for GATT connections to remote dual-mode devices {@link
     *                               BluetoothDevice#TRANSPORT_AUTO} or {@link BluetoothDevice#TRANSPORT_BREDR} or {@link
     *                               BluetoothDevice#TRANSPORT_LE}
     * @param baseBleConnectCallback 连接相关的回调
     * @return true means request successful
     */
    boolean connect(@NonNull String address, @Nullable Transport transport, @NonNull BaseBleConnectCallback baseBleConnectCallback);

    /**
     * Initiate a request to connect to a remote device
     *
     * @param address                device address
     * @param phyMask                preferred PHY for connections to remote LE device. Bitwise OR of any of {@link
     *                               BluetoothDevice#PHY_LE_1M_MASK}, {@link BluetoothDevice#PHY_LE_2M_MASK}, and {@link
     *                               BluetoothDevice#PHY_LE_CODED_MASK}. This option does not take effect if {@code autoConnect}
     * @param baseBleConnectCallback 连接相关的回调
     * @return true means request successful
     */
    boolean connect(@NonNull String address, @Nullable PhyMask phyMask, @NonNull BaseBleConnectCallback baseBleConnectCallback);

    /**
     * Initiate a request to connect to a remote device
     *
     * @param address                device address
     * @param autoReconnect          Whether to automatically reconnect
     * @param phyMask                preferred PHY for connections to remote LE device. Bitwise OR of any of {@link
     *                               BluetoothDevice#PHY_LE_1M_MASK}, {@link BluetoothDevice#PHY_LE_2M_MASK}, and {@link
     *                               BluetoothDevice#PHY_LE_CODED_MASK}. This option does not take effect if {@code autoConnect}
     * @param baseBleConnectCallback 连接相关的回调
     * @return true means request successful
     */
    boolean connect(@NonNull String address, boolean autoReconnect, @Nullable PhyMask phyMask, @NonNull BaseBleConnectCallback baseBleConnectCallback);

    /**
     * Initiate a request to connect to a remote device
     *
     * @param address                device address
     * @param transport              preferred transport for GATT connections to remote dual-mode devices
     *                               {@link BluetoothDevice#TRANSPORT_AUTO} or {@link BluetoothDevice#TRANSPORT_BREDR}
     *                               or {@link BluetoothDevice#TRANSPORT_LE}
     * @param phyMask                preferred PHY for connections to remote LE device. Bitwise OR of any of {@link
     *                               BluetoothDevice#PHY_LE_1M_MASK}, {@link BluetoothDevice#PHY_LE_2M_MASK}, and {@link
     *                               BluetoothDevice#PHY_LE_CODED_MASK}. This option does not take effect if {@code autoConnect}
     * @param baseBleConnectCallback 连接相关的回调
     * @return true means request successful
     */
    boolean connect(@NonNull String address, @Nullable Transport transport, @Nullable PhyMask phyMask, @NonNull BaseBleConnectCallback baseBleConnectCallback);

    /**
     * Initiate a request to connect to a remote device
     *
     * @param address                device address
     * @param autoReconnect          Whether to automatically reconnect
     * @param transport              preferred transport for GATT connections to remote dual-mode devices
     *                               {@link BluetoothDevice#TRANSPORT_AUTO} or {@link BluetoothDevice#TRANSPORT_BREDR}
     *                               or {@link BluetoothDevice#TRANSPORT_LE}
     * @param baseBleConnectCallback 连接相关的回调
     * @return true means request successful
     */
    boolean connect(@NonNull String address, boolean autoReconnect, @Nullable Transport transport, @NonNull BaseBleConnectCallback baseBleConnectCallback);

    /**
     * Initiate a request to connect to a remote device
     *
     * @param address                device address
     * @param autoReconnect          Whether to automatically reconnect
     * @param transport              preferred transport for GATT connections to remote dual-mode devices {@link
     *                               BluetoothDevice#TRANSPORT_AUTO} or {@link BluetoothDevice#TRANSPORT_BREDR} or {@link
     *                               BluetoothDevice#TRANSPORT_LE}
     * @param phyMask                preferred PHY for connections to remote LE device. Bitwise OR of any of {@link
     *                               BluetoothDevice#PHY_LE_1M_MASK}, {@link BluetoothDevice#PHY_LE_2M_MASK}, and {@link
     *                               BluetoothDevice#PHY_LE_CODED_MASK}. This option does not take effect if {@code autoConnect}
     * @param baseBleConnectCallback 连接相关的回调
     * @return true means request successful
     */
    boolean connect(@NonNull final String address, final boolean autoReconnect, @Nullable Transport transport, @Nullable PhyMask phyMask, @NonNull BaseBleConnectCallback baseBleConnectCallback);

    /**
     * Initiate a request to connect to a remote device
     *
     * @param bluetoothDevice        remote device
     * @param baseBleConnectCallback 连接相关的回调
     * @return true means request successful.
     */
    boolean connect(@NonNull BluetoothDevice bluetoothDevice, @NonNull BaseBleConnectCallback baseBleConnectCallback);

    /**
     * Initiate a request to connect to a remote device
     *
     * @param bluetoothDevice        BluetoothDevice
     * @param autoReconnect          Whether to automatically reconnect
     * @param baseBleConnectCallback 连接相关的回调
     * @return true means request successful
     */
    boolean connect(@NonNull BluetoothDevice bluetoothDevice, boolean autoReconnect, @NonNull BaseBleConnectCallback baseBleConnectCallback);

    /**
     * Initiate a request to connect to a remote device
     *
     * @param bluetoothDevice        BluetoothDevice
     * @param transport              preferred transport for GATT connections to remote dual-mode devices {@link
     *                               BluetoothDevice#TRANSPORT_AUTO} or {@link BluetoothDevice#TRANSPORT_BREDR} or {@link
     *                               BluetoothDevice#TRANSPORT_LE}
     * @param baseBleConnectCallback 连接相关的回调
     * @return true means request successful
     */
    boolean connect(@NonNull BluetoothDevice bluetoothDevice, @Nullable Transport transport, @NonNull BaseBleConnectCallback baseBleConnectCallback);

    /**
     * Initiate a request to connect to a remote device
     *
     * @param bluetoothDevice        BluetoothDevice
     * @param phyMask                preferred PHY for connections to remote LE device. Bitwise OR of any of {@link
     *                               BluetoothDevice#PHY_LE_1M_MASK}, {@link BluetoothDevice#PHY_LE_2M_MASK}, and {@link
     *                               BluetoothDevice#PHY_LE_CODED_MASK}. This option does not take effect if {@code autoConnect}
     * @param baseBleConnectCallback 连接相关的回调
     * @return true means request successful
     */
    boolean connect(@NonNull BluetoothDevice bluetoothDevice, @Nullable PhyMask phyMask, @NonNull BaseBleConnectCallback baseBleConnectCallback);

    /**
     * Initiate a request to connect to a remote device
     *
     * @param bluetoothDevice        BluetoothDevice
     * @param autoReconnect          Whether to automatically reconnect
     * @param transport              preferred transport for GATT connections to remote dual-mode devices {@link
     *                               BluetoothDevice#TRANSPORT_AUTO} or {@link BluetoothDevice#TRANSPORT_BREDR} or {@link
     *                               BluetoothDevice#TRANSPORT_LE}
     * @param baseBleConnectCallback 连接相关的回调
     * @return true means request successful
     */
    boolean connect(@NonNull BluetoothDevice bluetoothDevice, boolean autoReconnect, @Nullable Transport transport, @NonNull BaseBleConnectCallback baseBleConnectCallback);

    /**
     * Initiate a request to connect to a remote device
     *
     * @param bluetoothDevice        BluetoothDevice
     * @param autoReconnect          Whether to automatically reconnect
     * @param phyMask                preferred PHY for connections to remote LE device. Bitwise OR of any of {@link
     *                               BluetoothDevice#PHY_LE_1M_MASK}, {@link BluetoothDevice#PHY_LE_2M_MASK}, and {@link
     *                               BluetoothDevice#PHY_LE_CODED_MASK}. This option does not take effect if {@code autoConnect}
     * @param baseBleConnectCallback 连接相关的回调
     * @return true means request successful
     */
    boolean connect(@NonNull BluetoothDevice bluetoothDevice, boolean autoReconnect, @Nullable PhyMask phyMask, @NonNull BaseBleConnectCallback baseBleConnectCallback);

    /**
     * Initiate a request to connect to a remote device
     *
     * @param bluetoothDevice        BluetoothDevice
     * @param transport              preferred transport for GATT connections to remote dual-mode devices {@link
     *                               BluetoothDevice#TRANSPORT_AUTO} or {@link BluetoothDevice#TRANSPORT_BREDR} or {@link
     *                               BluetoothDevice#TRANSPORT_LE}
     * @param phyMask                preferred PHY for connections to remote LE device. Bitwise OR of any of {@link
     *                               BluetoothDevice#PHY_LE_1M_MASK}, {@link BluetoothDevice#PHY_LE_2M_MASK}, and {@link
     *                               BluetoothDevice#PHY_LE_CODED_MASK}. This option does not take effect if {@code autoConnect}
     * @param baseBleConnectCallback 连接相关的回调
     * @return true means request successful
     */
    boolean connect(@NonNull BluetoothDevice bluetoothDevice, @Nullable Transport transport, @Nullable PhyMask phyMask, @NonNull BaseBleConnectCallback baseBleConnectCallback);

    /**
     * Initiate a request to connect to a remote device
     *
     * @param bluetoothDevice        remote device
     * @param autoReconnect          Whether to automatically reconnect
     * @param transport              preferred transport for GATT connections to remote dual-mode devices {@link
     *                               BluetoothDevice#TRANSPORT_AUTO} or {@link BluetoothDevice#TRANSPORT_BREDR} or {@link
     *                               BluetoothDevice#TRANSPORT_LE}
     * @param phyMask                preferred PHY for connections to remote LE device. Bitwise OR of any of {@link
     *                               BluetoothDevice#PHY_LE_1M_MASK}, {@link BluetoothDevice#PHY_LE_2M_MASK}, and {@link
     *                               BluetoothDevice#PHY_LE_CODED_MASK}. This option does not take effect if {@code autoConnect}
     * @param baseBleConnectCallback 连接相关的回调
     * @return true means request successful.
     */
    boolean connect(@NonNull final BluetoothDevice bluetoothDevice, final boolean autoReconnect, @Nullable Transport transport, @Nullable PhyMask phyMask, @NonNull BaseBleConnectCallback baseBleConnectCallback);

    /**
     * Get BluetoothGatt instance
     *
     * @return BluetoothGatt instance
     */
    @Nullable
    BluetoothGatt getBluetoothGatt();

    /**
     * get connection status
     *
     * @return true means remote device is connected
     */
    boolean isConnected();

    /**
     * get service discovered status
     *
     * @return true means remote device is discovered
     */
    boolean isServiceDiscovered();

    /**
     * closeGatt this connection util
     *
     * @return true means closeGatt successful
     */
    boolean close();

    /**
     * closeGatt GATT connection
     *
     * @return true means close Gatt successful
     */
    boolean closeGatt();

    /**
     * write data to remote device.Result for request will be trigger callback {@link OnBleConnectStateChangedListener#writeCharacteristicData(BluetoothGattCharacteristic, byte[])}
     *
     * @param serviceUUID        service UUID
     * @param characteristicUUID characteristic UUID
     * @param data               data
     * @return true means request successful
     */
    boolean writeData(@NonNull String serviceUUID, @NonNull String characteristicUUID, @NonNull byte[] data);

    /**
     * read data from remote device.Result for request will be trigger callback {@link OnBleConnectStateChangedListener#readCharacteristicData(BluetoothGattCharacteristic, byte[])}
     *
     * @param serviceUUID        service UUID
     * @param characteristicUUID characteristic UUID
     * @return true means request successful
     */
    boolean readData(@NonNull String serviceUUID, @NonNull String characteristicUUID);

    /**
     * enable or disable notification
     *
     * @param serviceUUID        service UUID
     * @param characteristicUUID characteristic UUID
     * @param enable             true means enable notification,false means disable notification
     * @return true means successful
     */
    boolean enableNotification(@NonNull String serviceUUID, @NonNull String characteristicUUID, boolean enable);

    /**
     * get remote device RSSI.Result for request will be trigger callback {@link OnBleConnectStateChangedListener#readRemoteRssi(int)}
     *
     * @return true means request successful
     */
    boolean getRssi();

    /**
     * refresh GATT cache.
     * Notice:Some Custom system return true but not take effect.There is no solution so far.
     *
     * @return true means successful.
     */
    boolean refreshGattCache();

    /**
     * get remote service list
     *
     * @return service Bluetooth Gatt list
     */
    @Nullable
    List<BluetoothGattService> getServices();

    /**
     * get remote device service by UUID
     *
     * @param uuid UUID
     * @return Bluetooth Gatt Service
     */

    @Nullable
    BluetoothGattService getService(UUID uuid);

    /**
     * Check for support notifications
     *
     * @param serviceUUID        Service UUID
     * @param characteristicUUID characteristic UUID
     * @return true means support
     */
    boolean canNotify(@NonNull String serviceUUID, @NonNull String characteristicUUID);

    /**
     * Check for support notifications
     *
     * @param characteristic BluetoothGattCharacteristic
     * @return true means support
     */

    boolean canNotify(@NonNull BluetoothGattCharacteristic characteristic);

    /**
     * Check for support read
     *
     * @param serviceUUID        Service UUID
     * @param characteristicUUID characteristic UUID
     * @return true means support
     */
    boolean canRead(@NonNull String serviceUUID, @NonNull String characteristicUUID);

    /**
     * Check for support read
     *
     * @param characteristic BluetoothGattCharacteristic
     * @return true means support
     */

    boolean canRead(@NonNull BluetoothGattCharacteristic characteristic);

    /**
     * Check for support write(Signed)
     *
     * @param serviceUUID        Service UUID
     * @param characteristicUUID characteristic UUID
     * @return true means support
     */
    boolean canSignedWrite(@NonNull String serviceUUID, @NonNull String characteristicUUID);

    /**
     * Check for support write(Signed)
     *
     * @param characteristic BluetoothGattCharacteristic
     * @return true means support
     */

    boolean canSignedWrite(@NonNull BluetoothGattCharacteristic characteristic);

    /**
     * Check for support write
     *
     * @param serviceUUID        Service UUID
     * @param characteristicUUID characteristic UUID
     * @return true means support
     */
    boolean canWrite(@NonNull String serviceUUID, @NonNull String characteristicUUID);

    /**
     * Check for support write
     *
     * @param characteristic BluetoothGattCharacteristic
     * @return true means support
     */

    boolean canWrite(@NonNull BluetoothGattCharacteristic characteristic);

    /**
     * Check for support write(no response)
     *
     * @param serviceUUID        Service UUID
     * @param characteristicUUID characteristic UUID
     * @return true means support
     */
    boolean canWriteNoResponse(@NonNull String serviceUUID, @NonNull String characteristicUUID);

    /**
     * Check for support write(no response)
     *
     * @param characteristic BluetoothGattCharacteristic
     * @return true means support
     */

    boolean canWriteNoResponse(@NonNull BluetoothGattCharacteristic characteristic);

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
     * <p>Requires {@link Manifest.permission#BLUETOOTH} permission.
     *
     * @return true, if the reliable write transaction has been initiated
     */
    boolean beginReliableWrite();

    /**
     * Cancels a reliable write transaction for a given device.
     *
     * <p>Calling this function will discard all queued characteristic write
     * operations for a given remote device.
     *
     * <p>Requires {@link Manifest.permission#BLUETOOTH} permission.
     */

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    boolean abortReliableWrite();

    /**
     * Discovers services offered by a remote device as well as their
     * characteristics and descriptors.
     *
     * <p>This is an asynchronous operation. Once service discovery is completed,
     * the {@link BluetoothGattCallback#onServicesDiscovered} callback is
     * triggered. If the discovery was successful, the remote services can be
     * retrieved using the {@link #getServices} function.
     *
     * <p>Requires {@link Manifest.permission#BLUETOOTH} permission.
     *
     * @return true, if the remote service discovery has been started
     */
    boolean discoverServices();

    /**
     * Executes a reliable write transaction for a given remote device.
     *
     * <p>This function will commit all queued up characteristic write
     * operations for a given remote device.
     *
     * <p>A {@link BluetoothGattCallback#onReliableWriteCompleted} callback is
     * invoked to indicate whether the transaction has been executed correctly.
     *
     * <p>Requires {@link Manifest.permission#BLUETOOTH} permission.
     *
     * @return true, if the request to execute the transaction has been sent
     */

    boolean executeReliableWrite();
}
