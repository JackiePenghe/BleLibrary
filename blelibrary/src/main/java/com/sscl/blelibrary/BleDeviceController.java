package com.sscl.blelibrary;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.sscl.blelibrary.enums.PhyMask;
import com.sscl.blelibrary.enums.Transport;
import com.sscl.blelibrary.interfaces.MultiConnector;
import com.sscl.blelibrary.interfaces.OnBleConnectStateChangedListener;

import java.util.List;
import java.util.UUID;

/**
 * A tool class that operates on a device when multiple devices are connected
 *
 * @author jackie
 */
public final class BleDeviceController implements MultiConnector {
    /**
     * default max try count
     */
    private static final int DEFAULT_MAX_TRY_COUNT = 10;
    /**
     * delay time
     */
    private static final int SEND_LARGE_DATA_PACKAGE_DELAY_TIME = 0;

    /*-----------------------------------field variables-----------------------------------*/

    /**
     * BleMultiConnector
     */
    private BleMultiConnector bleMultiConnector;
    /**
     * device address
     */
    private String address;

    /*-----------------------------------Constructor-----------------------------------*/

    /**
     * Constructor
     *
     * @param bleMultiConnector BleMultiConnector
     * @param address           device address
     */
    BleDeviceController(@NonNull BleMultiConnector bleMultiConnector, @NonNull String address) {
        this.bleMultiConnector = bleMultiConnector;
        this.address = address;
    }

    /*-----------------------------------implementation parent methods-----------------------------------*/


    /**
     * Refresh gatt cache
     *
     * @return true means request successful
     */
    @Override
    public boolean refreshGattCache() {
        return address != null && bleMultiConnector.refreshGattCache(address);
    }

    /**
     * write data
     *
     * @param serviceUUID        service UUID
     * @param characteristicUUID characteristic UUID
     * @param values             data
     * @return true means request successful
     */
    @Override
    public boolean writeData(@NonNull String serviceUUID, @NonNull String characteristicUUID, @NonNull byte[] values) {
        return bleMultiConnector != null && address != null && bleMultiConnector.writeData(address, serviceUUID, characteristicUUID, values);
    }

    /**
     * read data
     *
     * @param serviceUUID        service UUID
     * @param characteristicUUID characteristic UUID
     * @return true means request successful
     */
    @Override
    public boolean readData(@NonNull String serviceUUID, @NonNull String characteristicUUID) {
        return bleMultiConnector != null && address != null && bleMultiConnector.readData(address, serviceUUID, characteristicUUID);
    }

    /**
     * enable notification
     *
     * @param serviceUUID        service UUID
     * @param characteristicUUID characteristic UUID
     * @param enable             true means enable,false means disable
     * @return true means request successful
     */
    @Override
    public boolean enableNotification(@NonNull String serviceUUID, @NonNull String characteristicUUID, boolean enable) {
        return bleMultiConnector != null && address != null && bleMultiConnector.enableNotification(address, serviceUUID, characteristicUUID, enable);
    }

    /**
     * get remote device RSSI.Result for request will be trigger callback {@link OnBleConnectStateChangedListener#readRemoteRssi(int)}
     *
     * @return true means request successful
     */
    @Override
    public boolean getRssi() {
        return bleMultiConnector != null && bleMultiConnector.getRssi(address);
    }

    /**
     * closeGatt current gatt connection
     *
     * @return true means request successful
     */
    @Override
    public boolean close() {
        BleMultiConnector bleMultiConnector = this.bleMultiConnector;
        this.bleMultiConnector = null;
        boolean result = bleMultiConnector != null && bleMultiConnector.close(address);
        if (result) {
            address = null;
        }
        return result;
    }

    /**
     * closeGatt GATT connection
     *
     * @return true means close Gatt successful
     */
    @Override
    public boolean closeGatt() {
        return bleMultiConnector != null && address != null && bleMultiConnector.closeGatt(address);
    }

    /**
     * get remote device service list
     *
     * @return service list
     */
    @Override
    @Nullable
    public List<BluetoothGattService> getServices() {
        if (bleMultiConnector == null) {
            return null;
        }
        if (address == null) {
            return null;
        }
        return bleMultiConnector.getServices(address);
    }

    /**
     * request change mtu value.Result of request will be trigger callback{@link OnBleConnectStateChangedListener#mtuChanged(int)}
     *
     * @param mtu mtu value
     * @return true means request send successful.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean requestMtu(int mtu) {
        if (bleMultiConnector == null) {
            return false;
        }
        if (address == null) {
            return false;
        }
        return bleMultiConnector.requestMtu(address, mtu);
    }

    /**
     * disconnect remote device
     *
     * @return true means request successful
     */
    @Override
    public boolean disconnect() {
        BleMultiConnector bleMultiConnector = this.bleMultiConnector;
        return bleMultiConnector != null && bleMultiConnector.disconnect(address);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param address                device address
     * @param baseBleConnectCallback 连接相关的回调
     * @return true means request successful
     */
    @Override
    public boolean connect(@NonNull String address, @NonNull BaseBleConnectCallback baseBleConnectCallback) {
        return connect(address, false, baseBleConnectCallback);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param address                device address
     * @param autoReconnect          Whether to automatically reconnect
     * @param baseBleConnectCallback 连接相关的回调
     * @return true means request successful
     */
    @Override
    public boolean connect(@NonNull String address, boolean autoReconnect, @NonNull BaseBleConnectCallback baseBleConnectCallback) {
        return connect(address, autoReconnect, (Transport) null, baseBleConnectCallback);
    }

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
    @Override
    public boolean connect(@NonNull String address, @Nullable Transport transport, @NonNull BaseBleConnectCallback baseBleConnectCallback) {
        return connect(address, transport, null, baseBleConnectCallback);
    }

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
    @Override
    public boolean connect(@NonNull String address, @Nullable PhyMask phyMask, @NonNull BaseBleConnectCallback baseBleConnectCallback) {
        return connect(address, false, phyMask, baseBleConnectCallback);
    }

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
    @Override
    public boolean connect(@NonNull String address, boolean autoReconnect, @Nullable PhyMask phyMask, @NonNull BaseBleConnectCallback baseBleConnectCallback) {
        return connect(address, autoReconnect, null, phyMask, baseBleConnectCallback);
    }

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
    @Override
    public boolean connect(@NonNull String address, @Nullable Transport transport, @Nullable PhyMask phyMask, @NonNull BaseBleConnectCallback baseBleConnectCallback) {
        return connect(address, false, transport, phyMask, baseBleConnectCallback);
    }

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
    @Override
    public boolean connect(@NonNull String address, boolean autoReconnect, @Nullable Transport transport, @NonNull BaseBleConnectCallback baseBleConnectCallback) {
        return connect(address, autoReconnect, transport, null, baseBleConnectCallback);
    }

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
    @Override
    public boolean connect(@NonNull String address, boolean autoReconnect, @Nullable Transport transport, @Nullable PhyMask phyMask, @NonNull BaseBleConnectCallback baseBleConnectCallback) {
        return bleMultiConnector != null && bleMultiConnector.connect(address, autoReconnect, transport, phyMask, baseBleConnectCallback);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param bluetoothDevice        remote device
     * @param baseBleConnectCallback 连接相关的回调
     * @return true means request successful.
     */
    @Override
    public boolean connect(@NonNull BluetoothDevice bluetoothDevice, @NonNull BaseBleConnectCallback baseBleConnectCallback) {
        return connect(bluetoothDevice, false, baseBleConnectCallback);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param bluetoothDevice        BluetoothDevice
     * @param autoReconnect          Whether to automatically reconnect
     * @param baseBleConnectCallback 连接相关的回调
     * @return true means request successful
     */
    @Override
    public boolean connect(@NonNull BluetoothDevice bluetoothDevice, boolean autoReconnect, @NonNull BaseBleConnectCallback baseBleConnectCallback) {
        return connect(bluetoothDevice, autoReconnect, (Transport) null, baseBleConnectCallback);
    }

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
    @Override
    public boolean connect(@NonNull BluetoothDevice bluetoothDevice, @Nullable Transport transport, @NonNull BaseBleConnectCallback baseBleConnectCallback) {
        return connect(bluetoothDevice, transport, null, baseBleConnectCallback);
    }

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
    @Override
    public boolean connect(@NonNull BluetoothDevice bluetoothDevice, @Nullable PhyMask phyMask, @NonNull BaseBleConnectCallback baseBleConnectCallback) {
        return connect(bluetoothDevice, false, phyMask, baseBleConnectCallback);
    }

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
    @Override
    public boolean connect(@NonNull BluetoothDevice bluetoothDevice, boolean autoReconnect, @Nullable Transport transport, @NonNull BaseBleConnectCallback baseBleConnectCallback) {
        return connect(bluetoothDevice, autoReconnect, transport, null, baseBleConnectCallback);
    }

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
    @Override
    public boolean connect(@NonNull BluetoothDevice bluetoothDevice, boolean autoReconnect, @Nullable PhyMask phyMask, @NonNull BaseBleConnectCallback baseBleConnectCallback) {
        return connect(bluetoothDevice, autoReconnect, null, phyMask, baseBleConnectCallback);
    }

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
    @Override
    public boolean connect(@NonNull BluetoothDevice bluetoothDevice, @Nullable Transport transport, @Nullable PhyMask phyMask, @NonNull BaseBleConnectCallback baseBleConnectCallback) {
        return connect(bluetoothDevice, false, transport, phyMask, baseBleConnectCallback);
    }

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
    @Override
    public boolean connect(@NonNull BluetoothDevice bluetoothDevice, boolean autoReconnect, @Nullable Transport transport, @Nullable PhyMask phyMask, @NonNull BaseBleConnectCallback baseBleConnectCallback) {
        return bleMultiConnector != null && bleMultiConnector.connect(bluetoothDevice, autoReconnect, transport, phyMask, baseBleConnectCallback);
    }

    /**
     * Reconnect device
     *
     * @return true means request successful
     */
    @Override
    public boolean reConnect() {
        return bleMultiConnector != null && address != null && bleMultiConnector.reConnect(address);
    }

    /**
     * Get BluetoothGatt instance
     *
     * @return BluetoothGatt instance
     */
    @Nullable
    @Override
    public BluetoothGatt getBluetoothGatt() {
        if (bleMultiConnector == null) {
            return null;
        }
        return bleMultiConnector.getBluetoothGatt(address);
    }

    /**
     * get gatt service by uuid
     *
     * @param uuid UUID
     * @return gatt service
     */
    @Override
    @Nullable
    public BluetoothGattService getService(@NonNull UUID uuid) {
        if (bleMultiConnector == null) {
            return null;
        }
        if (address == null) {
            return null;
        }
        return bleMultiConnector.getService(address, uuid);
    }

    /**
     * Check for support notifications
     *
     * @param serviceUUID        Service UUID
     * @param characteristicUUID characteristic UUID
     * @return true means support
     */
    @Override
    public boolean canNotify(@NonNull String serviceUUID, @NonNull String characteristicUUID) {
        BluetoothGattService service = getService(UUID.fromString(serviceUUID));
        if (service == null) {
            return false;
        }
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(characteristicUUID));
        if (characteristic == null) {
            return false;
        }
        return canNotify(characteristic);
    }

    /**
     * Check for support notifications
     *
     * @param characteristic BluetoothGattCharacteristic
     * @return true means support
     */
    @Override
    public boolean canNotify(@NonNull BluetoothGattCharacteristic characteristic) {
        int properties = characteristic.getProperties();
        return (properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0;
    }

    /**
     * Check for support write
     *
     * @param serviceUUID        Service UUID
     * @param characteristicUUID characteristic UUID
     * @return true means support
     */
    @Override
    public boolean canWrite(@NonNull String serviceUUID, @NonNull String characteristicUUID) {
        BluetoothGattService service = getService(UUID.fromString(serviceUUID));
        if (service == null) {
            return false;
        }
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(characteristicUUID));
        if (characteristic == null) {
            return false;
        }
        return canWrite(characteristic);
    }

    /**
     * Check for support write
     *
     * @param characteristic BluetoothGattCharacteristic
     * @return true means support
     */
    @Override
    public boolean canWrite(@NonNull BluetoothGattCharacteristic characteristic) {
        int properties = characteristic.getProperties();
        return (properties & BluetoothGattCharacteristic.PROPERTY_WRITE) != 0;
    }

    /**
     * Check for support read
     *
     * @param serviceUUID        Service UUID
     * @param characteristicUUID characteristic UUID
     * @return true means support
     */
    @Override
    public boolean canRead(@NonNull String serviceUUID, @NonNull String characteristicUUID) {
        BluetoothGattService service = getService(UUID.fromString(serviceUUID));
        if (service == null) {
            return false;
        }
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(characteristicUUID));
        if (characteristic == null) {
            return false;
        }
        return canRead(characteristic);
    }

    /**
     * Check for support read
     *
     * @param characteristic BluetoothGattCharacteristic
     * @return true means support
     */
    @Override
    public boolean canRead(@NonNull BluetoothGattCharacteristic characteristic) {
        int properties = characteristic.getProperties();
        return (properties & BluetoothGattCharacteristic.PROPERTY_READ) != 0;
    }

    /**
     * Check for support write(Signed)
     *
     * @param serviceUUID        Service UUID
     * @param characteristicUUID characteristic UUID
     * @return true means support
     */
    @Override
    public boolean canSignedWrite(@NonNull String serviceUUID, @NonNull String characteristicUUID) {
        BluetoothGattService service = getService(UUID.fromString(serviceUUID));
        if (service == null) {
            return false;
        }
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(characteristicUUID));
        if (characteristic == null) {
            return false;
        }
        return canSignedWrite(characteristic);
    }

    /**
     * Check for support write(Signed)
     *
     * @param characteristic BluetoothGattCharacteristic
     * @return true means support
     */
    @Override
    public boolean canSignedWrite(@NonNull BluetoothGattCharacteristic characteristic) {
        int properties = characteristic.getProperties();
        return (properties & BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE) != 0;
    }

    /**
     * Check for support write(no response)
     *
     * @param serviceUUID        Service UUID
     * @param characteristicUUID characteristic UUID
     * @return true means support
     */
    @Override
    public boolean canWriteNoResponse(@NonNull String serviceUUID, @NonNull String characteristicUUID) {
        BluetoothGattService service = getService(UUID.fromString(serviceUUID));
        if (service == null) {
            return false;
        }
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(characteristicUUID));
        if (characteristic == null) {
            return false;
        }
        return canWriteNoResponse(characteristic);
    }

    /**
     * Check for support write(no response)
     *
     * @param characteristic BluetoothGattCharacteristic
     * @return true means support
     */
    @Override
    public boolean canWriteNoResponse(@NonNull BluetoothGattCharacteristic characteristic) {
        int properties = characteristic.getProperties();
        return (properties & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0;
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
     * <p>Requires {@link Manifest.permission#BLUETOOTH} permission.
     *
     * @return true, if the reliable write transaction has been initiated
     */
    @Override
    public boolean beginReliableWrite() {
        if (bleMultiConnector == null) {
            return false;
        }
        if (address == null) {
            return false;
        }
        return bleMultiConnector.beginReliableWrite(address);
    }

    /**
     * Cancels a reliable write transaction for a given device.
     *
     * <p>Calling this function will discard all queued characteristic write
     * operations for a given remote device.
     *
     * <p>Requires {@link Manifest.permission#BLUETOOTH} permission.
     */
    @Override
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean abortReliableWrite() {
        if (bleMultiConnector == null) {
            return false;
        }
        if (address == null) {
            return false;
        }
        return bleMultiConnector.abortReliableWrite(address);
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
     * <p>Requires {@link Manifest.permission#BLUETOOTH} permission.
     *
     * @return true, if the remote service discovery has been started
     */
    @Override
    public boolean discoverServices() {
        if (bleMultiConnector == null) {
            return false;
        }
        if (address == null) {
            return false;
        }
        return bleMultiConnector.discoverServices(address);
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
     * <p>Requires {@link Manifest.permission#BLUETOOTH} permission.
     *
     * @return true, if the request to execute the transaction has been sent
     */
    @Override
    public boolean executeReliableWrite() {
        if (bleMultiConnector == null) {
            return false;
        }
        if (address == null) {
            return false;
        }
        return bleMultiConnector.executeReliableWrite(address);
    }

    /**
     * get connection status
     *
     * @return true means remote device is connected
     */
    @Override
    public boolean isConnected() {
        return bleMultiConnector != null && bleMultiConnector.isConnected(address);
    }

    /**
     * get service discovered status
     *
     * @return true means remote device is discovered
     */
    @Override
    public boolean isServiceDiscovered() {
        return bleMultiConnector != null && bleMultiConnector.isServiceDiscovered(address);
    }

    /*-----------------------------------public method-----------------------------------*/

    public long getSendLargeDataTimeOut() {
        if (bleMultiConnector == null) {
            return 0;
        }
        return bleMultiConnector.getSendLargeDataTimeOut();
    }

    public void setSendLargeDataTimeOut(long sendLargeDataTimeOut) {
        if (bleMultiConnector == null) {
            return;
        }
        bleMultiConnector.setSendLargeDataTimeOut(sendLargeDataTimeOut);
    }

    /*-----------------------------------getter and setter-----------------------------------*/

    /**
     * get BluetoothLeService
     *
     * @return BluetoothLeService
     */
    @SuppressWarnings("unused")
    @Nullable
    public BluetoothMultiService getBluetoothMultiService() {
        if (bleMultiConnector == null) {
            return null;
        }
        return bleMultiConnector.getBluetoothMultiService();
    }

    /**
     * get Bluetooth Adapter
     *
     * @return Bluetooth Adapter
     */
    @Nullable
    public BluetoothAdapter getBluetoothAdapter() {
        if (bleMultiConnector == null) {
            return null;
        }
        return bleMultiConnector.getBluetoothAdapter();
    }
}
