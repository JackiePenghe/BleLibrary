package com.sscl.blelibrary;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.sscl.blelibrary.enums.PhyMask;
import com.sscl.blelibrary.enums.Transport;
import com.sscl.blelibrary.interfaces.OnLargeDataSendStateChangedListener;
import com.sscl.blelibrary.interfaces.OnLargeDataWriteWithNotificationSendStateChangedListener;

import java.util.List;
import java.util.UUID;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


/**
 * Ble connection tool when connecting multiple devices
 *
 * @author jackie
 */

public final class BleMultiConnector {

    /*-----------------------------------field variables-----------------------------------*/

    /**
     * connect timeout
     */
    private long connectTimeOut = 10000;
    /**
     * Connect multiple BLE device service connection
     */
    private BleServiceMultiConnection bleServiceMultiConnection;
    /**
     * BluetoothMultiService
     */
    private BluetoothMultiService bluetoothMultiService;

    /*-----------------------------------Constructor-----------------------------------*/

    /**
     * Constructor
     */
    BleMultiConnector() {
        bleServiceMultiConnection = new BleServiceMultiConnection(this);
        Intent intent = new Intent(BleManager.getContext().getApplicationContext(), BluetoothMultiService.class);
        BleManager.getContext().getApplicationContext().bindService(intent, bleServiceMultiConnection, Context.BIND_AUTO_CREATE);
    }

    /*-----------------------------------Package private getter & setter-----------------------------------*/

    /**
     * get BluetoothMultiService
     *
     * @return BluetoothMultiService
     */
    BluetoothMultiService getBluetoothMultiService() {
        return bluetoothMultiService;
    }

    /**
     * set BluetoothMultiService
     *
     * @param bluetoothMultiService BluetoothMultiService
     */
    void setBluetoothMultiService(BluetoothMultiService bluetoothMultiService) {
        this.bluetoothMultiService = bluetoothMultiService;
    }

    /*-----------------------------------public getter & setter-----------------------------------*/

    /**
     * get connect time out
     *
     * @return connect time out
     */
    @SuppressWarnings("unused")
    public long getConnectTimeOut() {
        return connectTimeOut;
    }

    /**
     * set connect timeout
     *
     * @param connectTimeOut connect timeout(unit:ms)
     */
    @SuppressWarnings("WeakerAccess")
    public void setConnectTimeOut(@IntRange(from = 0) long connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
        if (bluetoothMultiService != null) {
            bluetoothMultiService.setConnectTimeOut(connectTimeOut);
        }
    }

    /*-----------------------------------Package private method-----------------------------------*/

    /**
     * Disconnect the GATT connection based on the device address.Result of request will be trigger callback {@link BaseBleConnectCallback#onDisConnected(BluetoothGatt)}
     *
     * @param address device address
     * @return true means request successful.
     */
    boolean disconnect(@NonNull String address) {
        return bluetoothMultiService != null && bluetoothMultiService.isInitializeFinished() && bluetoothMultiService.disconnect(address);
    }

    /**
     * Turn off GATT based on device address
     *
     * @param address device address
     * @return true means request successful
     */
    boolean close(@NonNull String address) {
        return bluetoothMultiService != null && bluetoothMultiService.isInitializeFinished() && bluetoothMultiService.close(address);
    }

    /**
     * reconnect remote device by address.(GATT must be not closed)
     *
     * @param address device address
     * @return true means request successful
     */
    boolean reConnect(@NonNull String address) {
        return bluetoothMultiService != null && bluetoothMultiService.isInitializeFinished() && bluetoothMultiService.reConnect(address);
    }

    /**
     * refresh GATT cache
     *
     * @return true means successful
     */
    boolean refreshGattCache(@NonNull String address) {
        return bluetoothMultiService != null && bluetoothMultiService.refreshGattCache(address);
    }

    /**
     * write data to remote device
     *
     * @param address            device address
     * @param serviceUUID        service UUID
     * @param characteristicUUID characteristic UUID
     * @param data               data
     * @return true means request success
     */
    boolean writeData(@NonNull String address, @NonNull String serviceUUID, @NonNull String characteristicUUID, @NonNull byte[] data) {
        return bluetoothMultiService != null && bluetoothMultiService.writeData(address, serviceUUID, characteristicUUID, data);
    }

    /**
     * read data from remote device
     *
     * @param serviceUUID        service UUID
     * @param characteristicUUID characteristic UUID
     * @return true means request success
     */
    boolean readData(@NonNull String address, @NonNull String serviceUUID, @NonNull String characteristicUUID) {
        return bluetoothMultiService != null && bluetoothMultiService.readData(address, serviceUUID, characteristicUUID);
    }

    /**
     * enable or disable notification
     *
     * @param address            device address
     * @param serviceUUID        service UUID
     * @param characteristicUUID characteristic UUID
     * @param enable             true means enable,false means disable
     * @return true means request success
     */
    boolean enableNotification(@NonNull String address, @NonNull String serviceUUID, @NonNull String characteristicUUID, boolean enable) {
        return bluetoothMultiService != null && bluetoothMultiService.enableNotification(address, serviceUUID, characteristicUUID, enable);
    }

    /**
     * get GATT services by Specified address
     *
     * @param address device address
     * @return GATT services
     */
    List<BluetoothGattService> getServices(@NonNull String address) {
        if (bluetoothMultiService == null) {
            return null;
        }

        if (!bluetoothMultiService.isInitializeFinished()) {
            return null;
        }
        return bluetoothMultiService.getServices(address);
    }

    /**
     * get GATT service by Specified address and UUID
     *
     * @param address address
     * @param uuid    UUID
     * @return GATT service
     */
    BluetoothGattService getService(@NonNull String address, @NonNull UUID uuid) {
        if (bluetoothMultiService == null) {
            return null;
        }
        if (!bluetoothMultiService.isInitializeFinished()) {
            return null;
        }
        return bluetoothMultiService.getService(address, uuid);
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
     * @param address device address
     * @return true, if the reliable write transaction has been initiated
     */
    boolean beginReliableWrite(@NonNull String address) {
        if (bluetoothMultiService == null) {
            return false;
        }
        if (!bluetoothMultiService.isInitializeFinished()) {
            return false;
        }
        return bluetoothMultiService.beginReliableWrite(address);
    }

    /**
     * Cancels a reliable write transaction for a given device.
     *
     * <p>Calling this function will discard all queued characteristic write
     * operations for a given remote device.
     *
     * @param address device address
     *                <p>Requires {@link android.Manifest.permission#BLUETOOTH} permission.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    boolean abortReliableWrite(@NonNull String address) {
        if (bluetoothMultiService == null) {
            return false;
        }
        if (!bluetoothMultiService.isInitializeFinished()) {
            return false;
        }
        return bluetoothMultiService.abortReliableWrite(address);
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
     * @param address device address
     * @return true, if the remote service discovery has been started
     */
    boolean discoverServices(@NonNull String address) {
        if (bluetoothMultiService == null) {
            return false;
        }
        if (!bluetoothMultiService.isInitializeFinished()) {
            return false;
        }
        return bluetoothMultiService.discoverServices(address);
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
     * @param address device address
     * @return true, if the request to execute the transaction has been sent
     */
    boolean executeReliableWrite(@NonNull String address) {
        if (bluetoothMultiService == null) {
            return false;
        }
        if (!bluetoothMultiService.isInitializeFinished()) {
            return false;
        }
        return bluetoothMultiService.executeReliableWrite(address);
    }

    /**
     * get BluetoothAdapter
     *
     * @return BluetoothAdapter
     */
    BluetoothAdapter getBluetoothAdapter() {
        if (bluetoothMultiService == null) {
            return null;
        }
        return bluetoothMultiService.getBluetoothAdapter();
    }

    /*-----------------------------------public method-----------------------------------*/

    /**
     * disconnect all device
     */
    @SuppressWarnings("unused")
    public boolean disconnectAll() {
        if (bluetoothMultiService == null) {
            return false;
        }
        if (!bluetoothMultiService.isInitializeFinished()) {
            return false;
        }
        bluetoothMultiService.disconnectAll();
        return true;
    }

    /**
     * close all GATT
     *
     * @return true means request success
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean closeAll() {
        if (bluetoothMultiService == null) {
            return false;
        }

        if (!bluetoothMultiService.isInitializeFinished()) {
            return false;
        }

        bluetoothMultiService.closeAll();
        if (BleManager.getContext() == null) {
            return false;
        }
        try {
            BleManager.getContext().getApplicationContext().unbindService(bleServiceMultiConnection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        bleServiceMultiConnection = null;
        bluetoothMultiService = null;
        BleManager.resetBleMultiConnector();
        return true;
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param address device address
     * @return true means request successful
     */
    public boolean connect(@NonNull String address) {
        return connect(address, false);
    }


    /**
     * Initiate a request to connect to a remote device
     *
     * @param address device address
     * @return true means request successful
     */
    public boolean connect(@NonNull String address, BaseBleConnectCallback baseBleConnectCallback) {
        return connect(address, false, baseBleConnectCallback);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param address       device address
     * @param autoReconnect Whether to automatically reconnect
     * @return true means request successful
     */
    public boolean connect(@NonNull String address, boolean autoReconnect) {
        return connect(address, autoReconnect, (Transport) null);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param address       device address
     * @param autoReconnect Whether to automatically reconnect
     * @return true means request successful
     */
    public boolean connect(@NonNull String address, boolean autoReconnect, BaseBleConnectCallback baseBleConnectCallback) {
        return connect(address, autoReconnect, (Transport) null, baseBleConnectCallback);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param address   device address
     * @param transport preferred transport for GATT connections to remote dual-mode devices {@link
     *                  BluetoothDevice#TRANSPORT_AUTO} or {@link BluetoothDevice#TRANSPORT_BREDR} or {@link
     *                  BluetoothDevice#TRANSPORT_LE}
     * @return true means request successful
     */
    public boolean connect(@NonNull String address, @Nullable Transport transport) {
        return connect(address, false, transport);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param address   device address
     * @param transport preferred transport for GATT connections to remote dual-mode devices {@link
     *                  BluetoothDevice#TRANSPORT_AUTO} or {@link BluetoothDevice#TRANSPORT_BREDR} or {@link
     *                  BluetoothDevice#TRANSPORT_LE}
     * @return true means request successful
     */
    public boolean connect(@NonNull String address, @Nullable Transport transport, BaseBleConnectCallback baseBleConnectCallback) {
        return connect(address, false, transport, baseBleConnectCallback);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param address device address
     * @param phyMask preferred PHY for connections to remote LE device. Bitwise OR of any of {@link
     *                BluetoothDevice#PHY_LE_1M_MASK}, {@link BluetoothDevice#PHY_LE_2M_MASK}, and {@link
     *                BluetoothDevice#PHY_LE_CODED_MASK}. This option does not take effect if {@code autoConnect}
     * @return true means request successful
     */
    public boolean connect(@NonNull String address, @Nullable PhyMask phyMask) {
        return connect(address, false, phyMask);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param address device address
     * @param phyMask preferred PHY for connections to remote LE device. Bitwise OR of any of {@link
     *                BluetoothDevice#PHY_LE_1M_MASK}, {@link BluetoothDevice#PHY_LE_2M_MASK}, and {@link
     *                BluetoothDevice#PHY_LE_CODED_MASK}. This option does not take effect if {@code autoConnect}
     * @return true means request successful
     */
    public boolean connect(@NonNull String address, @Nullable PhyMask phyMask, BaseBleConnectCallback baseBleConnectCallback) {
        return connect(address, false, phyMask, baseBleConnectCallback);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param address       device address
     * @param autoReconnect Whether to automatically reconnect
     * @param phyMask       preferred PHY for connections to remote LE device. Bitwise OR of any of {@link
     *                      BluetoothDevice#PHY_LE_1M_MASK}, {@link BluetoothDevice#PHY_LE_2M_MASK}, and {@link
     *                      BluetoothDevice#PHY_LE_CODED_MASK}. This option does not take effect if {@code autoConnect}
     * @return true means request successful
     */
    public boolean connect(@NonNull String address, boolean autoReconnect, @Nullable PhyMask phyMask, BaseBleConnectCallback baseBleConnectCallback) {
        return connect(address, autoReconnect, null, phyMask, baseBleConnectCallback);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param address       device address
     * @param autoReconnect Whether to automatically reconnect
     * @param phyMask       preferred PHY for connections to remote LE device. Bitwise OR of any of {@link
     *                      BluetoothDevice#PHY_LE_1M_MASK}, {@link BluetoothDevice#PHY_LE_2M_MASK}, and {@link
     *                      BluetoothDevice#PHY_LE_CODED_MASK}. This option does not take effect if {@code autoConnect}
     * @return true means request successful
     */
    public boolean connect(@NonNull String address, boolean autoReconnect, @Nullable PhyMask phyMask) {
        return connect(address, autoReconnect, null, phyMask);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param address   device address
     * @param transport preferred transport for GATT connections to remote dual-mode devices
     *                  {@link BluetoothDevice#TRANSPORT_AUTO} or {@link BluetoothDevice#TRANSPORT_BREDR}
     *                  or {@link BluetoothDevice#TRANSPORT_LE}
     * @param phyMask   preferred PHY for connections to remote LE device. Bitwise OR of any of {@link
     *                  BluetoothDevice#PHY_LE_1M_MASK}, {@link BluetoothDevice#PHY_LE_2M_MASK}, and {@link
     *                  BluetoothDevice#PHY_LE_CODED_MASK}. This option does not take effect if {@code autoConnect}
     * @return true means request successful
     */
    public boolean connect(@NonNull String address, @Nullable Transport transport, @Nullable PhyMask phyMask) {
        return connect(address, false, transport, phyMask);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param address   device address
     * @param transport preferred transport for GATT connections to remote dual-mode devices
     *                  {@link BluetoothDevice#TRANSPORT_AUTO} or {@link BluetoothDevice#TRANSPORT_BREDR}
     *                  or {@link BluetoothDevice#TRANSPORT_LE}
     * @param phyMask   preferred PHY for connections to remote LE device. Bitwise OR of any of {@link
     *                  BluetoothDevice#PHY_LE_1M_MASK}, {@link BluetoothDevice#PHY_LE_2M_MASK}, and {@link
     *                  BluetoothDevice#PHY_LE_CODED_MASK}. This option does not take effect if {@code autoConnect}
     * @return true means request successful
     */
    public boolean connect(@NonNull String address, @Nullable Transport transport, @Nullable PhyMask phyMask, BaseBleConnectCallback baseBleConnectCallback) {
        return connect(address, false, transport, phyMask, baseBleConnectCallback);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param address       device address
     * @param autoReconnect Whether to automatically reconnect
     * @param transport     preferred transport for GATT connections to remote dual-mode devices
     *                      {@link BluetoothDevice#TRANSPORT_AUTO} or {@link BluetoothDevice#TRANSPORT_BREDR}
     *                      or {@link BluetoothDevice#TRANSPORT_LE}
     * @return true means request successful
     */
    public boolean connect(@NonNull String address, boolean autoReconnect, @Nullable Transport transport) {
        return connect(address, autoReconnect, transport, (PhyMask) null);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param address       device address
     * @param autoReconnect Whether to automatically reconnect
     * @param transport     preferred transport for GATT connections to remote dual-mode devices
     *                      {@link BluetoothDevice#TRANSPORT_AUTO} or {@link BluetoothDevice#TRANSPORT_BREDR}
     *                      or {@link BluetoothDevice#TRANSPORT_LE}
     * @return true means request successful
     */
    public boolean connect(@NonNull String address, boolean autoReconnect, @Nullable Transport transport, BaseBleConnectCallback baseBleConnectCallback) {
        return connect(address, autoReconnect, transport, null, baseBleConnectCallback);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param address       device address
     * @param autoReconnect Whether to automatically reconnect
     * @param transport     preferred transport for GATT connections to remote dual-mode devices
     *                      {@link BluetoothDevice#TRANSPORT_AUTO} or {@link BluetoothDevice#TRANSPORT_BREDR}
     *                      or {@link BluetoothDevice#TRANSPORT_LE}
     * @param phyMask       preferred PHY for connections to remote LE device. Bitwise OR of any of {@link PhyMask}
     * @return true means request successful
     */
    public boolean connect(@NonNull String address, boolean autoReconnect, @Nullable Transport transport, @Nullable PhyMask phyMask) {
        return connect(address, autoReconnect, transport, phyMask, new DefaultBleConnectCallBack());
    }

    /**
     * connect a device
     *
     * @param address       device address
     * @param autoReconnect Automatically reconnect when the device is accidentally disconnected
     * @param transport     preferred transport for GATT connections to remote dual-mode devices{@link Transport}
     * @param phyMask       preferred PHY for connections to remote LE device. Bitwise OR of any of {@link PhyMask}
     * @return true means request success
     */
    public boolean connect(@NonNull String address, boolean autoReconnect, @Nullable Transport transport, @Nullable PhyMask phyMask, @NonNull BaseBleConnectCallback baseBleConnectCallback) {
        if (bluetoothMultiService == null) {
            return false;
        }
        return bluetoothMultiService.connect(address, autoReconnect, transport, phyMask, baseBleConnectCallback);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param bluetoothDevice remote device
     * @return true means request successful
     */
    public boolean connect(@NonNull BluetoothDevice bluetoothDevice) {
        return connect(bluetoothDevice, false);
    }


    /**
     * Initiate a request to connect to a remote device
     *
     * @param bluetoothDevice remote device
     * @return true means request successful
     */
    public boolean connect(@NonNull BluetoothDevice bluetoothDevice, BaseBleConnectCallback baseBleConnectCallback) {
        return connect(bluetoothDevice, false, baseBleConnectCallback);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param bluetoothDevice remote device
     * @param autoReconnect   Whether to automatically reconnect
     * @return true means request successful
     */
    public boolean connect(@NonNull BluetoothDevice bluetoothDevice, boolean autoReconnect) {
        return connect(bluetoothDevice, autoReconnect, (Transport) null);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param bluetoothDevice remote device
     * @param autoReconnect   Whether to automatically reconnect
     * @return true means request successful
     */
    public boolean connect(@NonNull BluetoothDevice bluetoothDevice, boolean autoReconnect, BaseBleConnectCallback baseBleConnectCallback) {
        return connect(bluetoothDevice, autoReconnect, (Transport) null, baseBleConnectCallback);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param bluetoothDevice remote device
     * @param transport       preferred transport for GATT connections to remote dual-mode devices {@link
     *                        BluetoothDevice#TRANSPORT_AUTO} or {@link BluetoothDevice#TRANSPORT_BREDR} or {@link
     *                        BluetoothDevice#TRANSPORT_LE}
     * @return true means request successful
     */
    public boolean connect(@NonNull BluetoothDevice bluetoothDevice, @Nullable Transport transport) {
        return connect(bluetoothDevice, false, transport);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param bluetoothDevice remote device
     * @param transport       preferred transport for GATT connections to remote dual-mode devices {@link
     *                        BluetoothDevice#TRANSPORT_AUTO} or {@link BluetoothDevice#TRANSPORT_BREDR} or {@link
     *                        BluetoothDevice#TRANSPORT_LE}
     * @return true means request successful
     */
    public boolean connect(@NonNull BluetoothDevice bluetoothDevice, @Nullable Transport transport, BaseBleConnectCallback baseBleConnectCallback) {
        return connect(bluetoothDevice, false, transport, baseBleConnectCallback);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param bluetoothDevice remote device
     * @param phyMask         preferred PHY for connections to remote LE device. Bitwise OR of any of {@link
     *                        BluetoothDevice#PHY_LE_1M_MASK}, {@link BluetoothDevice#PHY_LE_2M_MASK}, and {@link
     *                        BluetoothDevice#PHY_LE_CODED_MASK}. This option does not take effect if {@code autoConnect}
     * @return true means request successful
     */
    public boolean connect(@NonNull BluetoothDevice bluetoothDevice, @Nullable PhyMask phyMask) {
        return connect(bluetoothDevice, false, phyMask);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param bluetoothDevice remote device
     * @param phyMask         preferred PHY for connections to remote LE device. Bitwise OR of any of {@link
     *                        BluetoothDevice#PHY_LE_1M_MASK}, {@link BluetoothDevice#PHY_LE_2M_MASK}, and {@link
     *                        BluetoothDevice#PHY_LE_CODED_MASK}. This option does not take effect if {@code autoConnect}
     * @return true means request successful
     */
    public boolean connect(@NonNull BluetoothDevice bluetoothDevice, @Nullable PhyMask phyMask, BaseBleConnectCallback baseBleConnectCallback) {
        return connect(bluetoothDevice, false, phyMask, baseBleConnectCallback);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param bluetoothDevice remote device
     * @param autoReconnect   Whether to automatically reconnect
     * @param phyMask         preferred PHY for connections to remote LE device. Bitwise OR of any of {@link
     *                        BluetoothDevice#PHY_LE_1M_MASK}, {@link BluetoothDevice#PHY_LE_2M_MASK}, and {@link
     *                        BluetoothDevice#PHY_LE_CODED_MASK}. This option does not take effect if {@code autoConnect}
     * @return true means request successful
     */
    public boolean connect(@NonNull BluetoothDevice bluetoothDevice, boolean autoReconnect, @Nullable PhyMask phyMask, BaseBleConnectCallback baseBleConnectCallback) {
        return connect(bluetoothDevice, autoReconnect, null, phyMask, baseBleConnectCallback);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param bluetoothDevice remote device
     * @param autoReconnect   Whether to automatically reconnect
     * @param phyMask         preferred PHY for connections to remote LE device. Bitwise OR of any of {@link
     *                        BluetoothDevice#PHY_LE_1M_MASK}, {@link BluetoothDevice#PHY_LE_2M_MASK}, and {@link
     *                        BluetoothDevice#PHY_LE_CODED_MASK}. This option does not take effect if {@code autoConnect}
     * @return true means request successful
     */
    public boolean connect(@NonNull BluetoothDevice bluetoothDevice, boolean autoReconnect, @Nullable PhyMask phyMask) {
        return connect(bluetoothDevice, autoReconnect, null, phyMask);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param bluetoothDevice remote device
     * @param transport       preferred transport for GATT connections to remote dual-mode devices
     *                        {@link BluetoothDevice#TRANSPORT_AUTO} or {@link BluetoothDevice#TRANSPORT_BREDR}
     *                        or {@link BluetoothDevice#TRANSPORT_LE}
     * @param phyMask         preferred PHY for connections to remote LE device. Bitwise OR of any of {@link
     *                        BluetoothDevice#PHY_LE_1M_MASK}, {@link BluetoothDevice#PHY_LE_2M_MASK}, and {@link
     *                        BluetoothDevice#PHY_LE_CODED_MASK}. This option does not take effect if {@code autoConnect}
     * @return true means request successful
     */
    public boolean connect(@NonNull BluetoothDevice bluetoothDevice, @Nullable Transport transport, @Nullable PhyMask phyMask) {
        return connect(bluetoothDevice, false, transport, phyMask);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param bluetoothDevice remote device
     * @param transport       preferred transport for GATT connections to remote dual-mode devices
     *                        {@link BluetoothDevice#TRANSPORT_AUTO} or {@link BluetoothDevice#TRANSPORT_BREDR}
     *                        or {@link BluetoothDevice#TRANSPORT_LE}
     * @param phyMask         preferred PHY for connections to remote LE device. Bitwise OR of any of {@link
     *                        BluetoothDevice#PHY_LE_1M_MASK}, {@link BluetoothDevice#PHY_LE_2M_MASK}, and {@link
     *                        BluetoothDevice#PHY_LE_CODED_MASK}. This option does not take effect if {@code autoConnect}
     * @return true means request successful
     */
    public boolean connect(@NonNull BluetoothDevice bluetoothDevice, @Nullable Transport transport, @Nullable PhyMask phyMask, BaseBleConnectCallback baseBleConnectCallback) {
        return connect(bluetoothDevice, false, transport, phyMask, baseBleConnectCallback);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param bluetoothDevice remote device
     * @param autoReconnect   Whether to automatically reconnect
     * @param transport       preferred transport for GATT connections to remote dual-mode devices
     *                        {@link BluetoothDevice#TRANSPORT_AUTO} or {@link BluetoothDevice#TRANSPORT_BREDR}
     *                        or {@link BluetoothDevice#TRANSPORT_LE}
     * @return true means request successful
     */
    public boolean connect(@NonNull BluetoothDevice bluetoothDevice, boolean autoReconnect, @Nullable Transport transport) {
        return connect(bluetoothDevice, autoReconnect, transport, (PhyMask) null);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param bluetoothDevice remote device
     * @param autoReconnect   Whether to automatically reconnect
     * @param transport       preferred transport for GATT connections to remote dual-mode devices
     *                        {@link BluetoothDevice#TRANSPORT_AUTO} or {@link BluetoothDevice#TRANSPORT_BREDR}
     *                        or {@link BluetoothDevice#TRANSPORT_LE}
     * @return true means request successful
     */
    public boolean connect(@NonNull BluetoothDevice bluetoothDevice, boolean autoReconnect, @Nullable Transport transport, BaseBleConnectCallback baseBleConnectCallback) {
        return connect(bluetoothDevice, autoReconnect, transport, null, baseBleConnectCallback);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param bluetoothDevice remote device
     * @param autoReconnect   Whether to automatically reconnect
     * @param transport       preferred transport for GATT connections to remote dual-mode devices
     *                        {@link BluetoothDevice#TRANSPORT_AUTO} or {@link BluetoothDevice#TRANSPORT_BREDR}
     *                        or {@link BluetoothDevice#TRANSPORT_LE}
     * @param phyMask         preferred PHY for connections to remote LE device. Bitwise OR of any of {@link PhyMask}
     * @return true means request successful
     */
    public boolean connect(@NonNull BluetoothDevice bluetoothDevice, boolean autoReconnect, @Nullable Transport transport, @Nullable PhyMask phyMask) {
        return connect(bluetoothDevice, autoReconnect, transport, phyMask, new DefaultBleConnectCallBack());
    }

    /**
     * connect a device
     *
     * @param bluetoothDevice remote device
     * @param autoReconnect   Automatically reconnect when the device is accidentally disconnected
     * @param transport       preferred transport for GATT connections to remote dual-mode devices{@link Transport}
     * @param phyMask         preferred PHY for connections to remote LE device. Bitwise OR of any of {@link PhyMask}
     * @return true means request success
     */
    public boolean connect(@NonNull BluetoothDevice bluetoothDevice, boolean autoReconnect, Transport transport, PhyMask phyMask, @NonNull BaseBleConnectCallback baseBleConnectCallback) {
        if (bluetoothMultiService == null) {
            return false;
        }
        return bluetoothMultiService.connect(bluetoothDevice, autoReconnect, transport, phyMask, baseBleConnectCallback);
    }

    /**
     * refresh all GATT cache
     */
    public void refreshAllGattCache() {
        if (bluetoothMultiService == null) {
            return;
        }
        bluetoothMultiService.refreshAllGattCache();
    }

    /**
     * get device controller by specified address
     *
     * @param address device address
     * @return BleDeviceController
     */
    @SuppressWarnings("unused")
    public BleDeviceController getBleDeviceController(String address) {
        if (bluetoothMultiService == null) {
            return null;
        }
        if (!bluetoothMultiService.isConnected(address)) {
            return null;
        }
        return new BleDeviceController(this, address);
    }

    boolean isConnected(String address) {
        if (bluetoothMultiService == null) {
            return false;
        }
        return bluetoothMultiService.isConnected(address);
    }

    /**
     * Get BluetoothGatt instance
     *
     * @return BluetoothGatt instance
     */
    @Nullable
    BluetoothGatt getBluetoothGatt(String address) {
        if (bluetoothMultiService == null) {
            return null;
        }
        return bluetoothMultiService.getBluetoothGatt(address);
    }

    /**
     * read remote device rssi
     *
     * @param address remote device address
     * @return true, if the request started
     */
    boolean getRssi(@NonNull String address) {
        if (bluetoothMultiService == null) {
            return false;
        }
        return bluetoothMultiService.getRssi(address);
    }

    long getSendLargeDataTimeOut() {
        if (bluetoothMultiService == null) {
            return 0;
        }
        return bluetoothMultiService.getSendLargeDataTimeOut();
    }

    void setSendLargeDataTimeOut(long sendLargeDataTimeOut) {
        if (bluetoothMultiService == null) {
            return;
        }
        bluetoothMultiService.setSendLargeDataTimeOut(sendLargeDataTimeOut);
    }

    boolean closeGatt(@NonNull String address) {
        if (bluetoothMultiService == null) {
            return false;
        }
        return bluetoothMultiService.closeGatt(address);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    boolean requestMtu(@NonNull String address, int mtu) {
        return bluetoothMultiService != null && bluetoothMultiService.requestMtu(address, mtu);
    }

    /**
     * get service discovered status
     *
     * @param address device address
     * @return true means remote device is discovered
     */
    boolean isServiceDiscovered(@NonNull String address) {
        return bluetoothMultiService != null && bluetoothMultiService.isServiceDiscovered(address);
    }

    /*-----------------------------------private methods-----------------------------------*/

    /**
     * 触发大量数据发送失败的回调
     *
     * @param onLargeDataSendStateChangedListener 大量数据发送失败的回调
     */
    private void performOnLargeDataSendStateChangedListenerStartFailedListener(@Nullable final OnLargeDataSendStateChangedListener onLargeDataSendStateChangedListener) {
        BleManager.getHANDLER().post(new Runnable() {
            @Override
            public void run() {
                if (onLargeDataSendStateChangedListener != null) {
                    onLargeDataSendStateChangedListener.onStartFailed();
                }
            }
        });
    }

    private void performOnLargeDataWriteWithNotificationSendStateChangedListenerStartFailedListener(final OnLargeDataWriteWithNotificationSendStateChangedListener onLargeDataWriteWithNotificationSendStateChangedListener) {
        BleManager.getHANDLER().post(new Runnable() {
            @Override
            public void run() {
                if (onLargeDataWriteWithNotificationSendStateChangedListener != null) {
                    onLargeDataWriteWithNotificationSendStateChangedListener.onStartFailed();
                }
            }
        });
    }
}
