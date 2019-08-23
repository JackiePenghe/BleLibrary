package com.sscl.blelibrary;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.sscl.blelibrary.enums.PhyMask;
import com.sscl.blelibrary.enums.Transport;
import com.sscl.blelibrary.interfaces.Connector;
import com.sscl.blelibrary.interfaces.OnBleCharacteristicWriteListener;
import com.sscl.blelibrary.interfaces.OnBleConnectStateChangedListener;
import com.sscl.blelibrary.interfaces.OnBleDescriptorWriteListener;
import com.sscl.blelibrary.interfaces.OnBleReceiveNotificationListener;
import com.sscl.blelibrary.interfaces.OnDeviceBondStateChangedListener;
import com.sscl.blelibrary.interfaces.OnLargeDataSendStateChangedListener;
import com.sscl.blelibrary.interfaces.OnLargeDataWriteWithNotificationSendStateChangedListener;

import java.util.List;
import java.util.UUID;

/**
 * BLE connect utils
 *
 * @author jackie
 */
public final class BleConnector implements Connector {

    /*-----------------------------------static constant-----------------------------------*/

    /**
     * TAG
     */
    private static final String TAG = BleConnector.class.getSimpleName();
    /**
     * default value of maximum length of data packets sent while Bluetooth connection
     */
    private static final int PACKAGE_MAX_LENGTH = 20;
    /**
     * default value of maximum length of valid data transmitted per packet when large packet transmission
     */
    private static final int LARGE_DATA_AUTO_FORMAT_TRANSFORM_PACKAGE_MAX_LENGTH = 17;
    /**
     * default value of try count
     */
    private static final int DEFAULT_MAX_TRY_COUNT = 10;
    /**
     * default value of resend delay when data transmission fails
     */
    private static final int RETRY_DELAY_TIME = 100;

    /*-----------------------------------field variables-----------------------------------*/

    /**
     * connect time out
     */
    private long connectTimeOut = 10000;
    /**
     * timeout for sending large data pack
     */
    private long sendLargeDataTimeOut = 3000;
    /**
     * delay time
     */
    private int sendLargeDataPackageDelayTime = 0;

    /**
     * BLE device Broadcast receiver of the binding result
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private BoundBleBroadcastReceiver boundBleBroadcastReceiver = new BoundBleBroadcastReceiver();
    /**
     * If the binding is initiated, the address of the bound device is recorded.
     */
    @Nullable
    private String bondAddress;
    /**
     * Whether to continue writing large amounts of data.
     * requires receive remote device notification data and return true in callback {@link OnLargeDataWriteWithNotificationSendStateChangedListener}
     */
    private boolean writeLargeDataWithNotificationContinueFlag;
    /**
     * Whether to keep writing large amounts of data
     */
    private boolean writeLargeDataContinueFlag;

    /**
     * Callback triggered when connect state changed
     */
    @Nullable
    private OnBleConnectStateChangedListener onBleConnectStateChangedListener;
    /**
     * LE Connection service
     */
    @Nullable
    private BluetoothLeService bluetoothLeService;

    private boolean closed;

    /*-----------------------------------Constructor-----------------------------------*/

    /**
     * Constructor
     */
    BleConnector(@Nullable BluetoothLeService bluetoothLeService) {
        this.bluetoothLeService = bluetoothLeService;
    }

    /*-----------------------------------package private setter-----------------------------------*/

    /**
     * set BluetoothLeService
     *
     * @param bluetoothLeService BluetoothLeService
     */
    void setBluetoothLeService(@Nullable BluetoothLeService bluetoothLeService) {
        this.bluetoothLeService = bluetoothLeService;
        if (this.bluetoothLeService != null) {
            this.bluetoothLeService.setOnBleConnectStateChangedListener(onBleConnectStateChangedListener);
        }
    }

    /*-----------------------------------setter and getter-----------------------------------*/

    /**
     * set connect timeout
     *
     * @param connectTimeOut timeout(unit:ms)
     */
    public void setConnectTimeOut(@IntRange(from = 0) long connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
    }

    /**
     * set large data send timeout
     *
     * @param sendLargeDataTimeOut timeout(unit:ms)
     */
    public void setSendLargeDataTimeOut(@IntRange(from = 0) long sendLargeDataTimeOut) {
        this.sendLargeDataTimeOut = sendLargeDataTimeOut;
    }

    public int getSendLargeDataPackageDelayTime() {
        return sendLargeDataPackageDelayTime;
    }

    /**
     * get connect time out
     *
     * @return timeout value
     */
    public long getConnectTimeOut() {
        return connectTimeOut;
    }

    /**
     * get timeout for sending large data pack
     *
     * @return timeout for sending large data pack
     */
    public long getSendLargeDataTimeOut() {
        return sendLargeDataTimeOut;
    }

    /**
     * get BluetoothLeService
     *
     * @return BluetoothLeService
     */
    @Nullable
    public BluetoothLeService getBluetoothLeService() {
        return bluetoothLeService;
    }

    /**
     * get Bluetooth Adapter
     *
     * @return Bluetooth Adapter
     */
    @Nullable
    public BluetoothAdapter getBluetoothAdapter() {
        if (bluetoothLeService == null) {
            return null;
        }
        return bluetoothLeService.getBluetoothAdapter();
    }

    /*-----------------------------------implementation parent method-----------------------------------*/


    /**
     * request change mtu value.Result of request will be trigger callback{@link OnBleConnectStateChangedListener#mtuChanged(int)}
     *
     * @param mtu mtu value
     * @return true means request send successful.
     */
    @Override
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean requestMtu(int mtu) {
        return bluetoothLeService != null && bluetoothLeService.requestMtu(mtu);
    }

    /**
     * start bind device
     *
     * @param address device address
     * @return request result is constant in  {@link BleConstants}
     * {@link BleConstants#BLUETOOTH_ADDRESS_INCORRECT} wrong address
     * {@link BleConstants#BLUETOOTH_MANAGER_NULL} No Bluetooth Manager
     * {@link BleConstants#BLUETOOTH_ADAPTER_NULL} No Bluetooth adapter
     * {@link BleConstants#DEVICE_BOND_BONDED} The device has been bound
     * {@link BleConstants#DEVICE_BOND_BONDING} A binding is being initiated to the device (or the device is being bound to another device)
     * {@link BleConstants#DEVICE_BOND_REQUEST_SUCCESS} Successfully initiated a bind request
     * {@link BleConstants#DEVICE_BOND_REQUEST_FAILED} Failed to initiate a bind request
     */

    @Override
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public int startBound(@NonNull String address) {
        //Register a broadcast receiver for bound to BLE bleDevice
        BleManager.getContext().registerReceiver(boundBleBroadcastReceiver, makeBoundBLEIntentFilter());
        if (BleManager.getContext() == null) {
            return BleConstants.CONTEXT_NULL;
        }
        if (!BluetoothAdapter.checkBluetoothAddress(address)) {
            return BleConstants.BLUETOOTH_ADDRESS_INCORRECT;
        }
        BluetoothManager bluetoothManager = (BluetoothManager) BleManager.getContext().getSystemService(Context.BLUETOOTH_SERVICE);

        if (bluetoothManager == null) {
            return BleConstants.BLUETOOTH_MANAGER_NULL;
        }

        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null) {
            return BleConstants.BLUETOOTH_ADAPTER_NULL;
        }

        bondAddress = address;

        BluetoothDevice remoteDevice = bluetoothAdapter.getRemoteDevice(address);
        switch (remoteDevice.getBondState()) {
            case BluetoothDevice.BOND_BONDED:
                return BleConstants.DEVICE_BOND_BONDED;
            case BluetoothDevice.BOND_BONDING:
                return BleConstants.DEVICE_BOND_BONDING;
            default:
                break;
        }
        //create bound by system api
        if (remoteDevice.createBond()) {
            return BleConstants.DEVICE_BOND_REQUEST_SUCCESS;
        } else {
            return BleConstants.DEVICE_BOND_REQUEST_FAILED;
        }
    }

    /**
     * Disconnect remote device
     *
     * @return true means disconnect success
     */
    @Override
    public boolean disconnect() {
        return bluetoothLeService != null && bluetoothLeService.disconnect();
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param address device address
     * @return true means request successful
     */
    @Override
    public boolean connect(@NonNull String address) {
        return connect(address, false);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param address       device address
     * @param autoReconnect Whether to automatically reconnect
     * @return true means request successful
     */
    @Override
    public boolean connect(@NonNull String address, boolean autoReconnect) {
        return connect(address, autoReconnect, (Transport) null);
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
    @Override
    public boolean connect(@NonNull String address, @Nullable Transport transport) {
        return connect(address, transport, null);
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
    @Override
    public boolean connect(@NonNull String address, @Nullable PhyMask phyMask) {
        return connect(address, false, phyMask);
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
    @Override
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
    @Override
    public boolean connect(@NonNull String address, @Nullable Transport transport, @Nullable PhyMask phyMask) {
        return connect(address, false, transport, phyMask);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param address       device address
     * @param autoReconnect Whether to automatically reconnect
     * @param transport     preferred transport for GATT connections to remote dual-mode devices {@link
     *                      BluetoothDevice#TRANSPORT_AUTO} or {@link BluetoothDevice#TRANSPORT_BREDR} or {@link
     *                      BluetoothDevice#TRANSPORT_LE}
     * @return true means request successful
     */
    @Override
    public boolean connect(@NonNull String address, boolean autoReconnect, @Nullable Transport transport) {
        return connect(address, autoReconnect, transport, null);
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
    @Override
    public boolean connect(@NonNull final String address, final boolean autoReconnect, @Nullable Transport transport, @Nullable PhyMask phyMask) {
        if (!isInitialized()) {
            return false;
        }
        //noinspection ConstantConditions
        boolean result = bluetoothLeService != null && bluetoothLeService.connect(address, autoReconnect, transport, phyMask);
        if (result) {
            closed = false;
            checkConnectTimeOut();
        }
        return result;
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param bluetoothDevice remote device
     * @return true means request successful.
     */
    @Override
    public boolean connect(@NonNull BluetoothDevice bluetoothDevice) {
        return connect(bluetoothDevice, false);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param bluetoothDevice BluetoothDevice
     * @param autoReconnect   Whether to automatically reconnect
     * @return true means request successful
     */
    @Override
    public boolean connect(@NonNull BluetoothDevice bluetoothDevice, boolean autoReconnect) {
        return connect(bluetoothDevice, autoReconnect, (Transport) null);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param bluetoothDevice BluetoothDevice
     * @param transport       preferred transport for GATT connections to remote dual-mode devices {@link
     *                        BluetoothDevice#TRANSPORT_AUTO} or {@link BluetoothDevice#TRANSPORT_BREDR} or {@link
     *                        BluetoothDevice#TRANSPORT_LE}
     * @return true means request successful
     */
    @Override
    public boolean connect(@NonNull BluetoothDevice bluetoothDevice, @Nullable Transport transport) {
        return connect(bluetoothDevice, transport, null);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param bluetoothDevice BluetoothDevice
     * @param phyMask         preferred PHY for connections to remote LE device. Bitwise OR of any of {@link
     *                        BluetoothDevice#PHY_LE_1M_MASK}, {@link BluetoothDevice#PHY_LE_2M_MASK}, and {@link
     *                        BluetoothDevice#PHY_LE_CODED_MASK}. This option does not take effect if {@code autoConnect}
     * @return true means request successful
     */
    @Override
    public boolean connect(@NonNull BluetoothDevice bluetoothDevice, @Nullable PhyMask phyMask) {
        return connect(bluetoothDevice, false, phyMask);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param bluetoothDevice BluetoothDevice
     * @param autoReconnect   Whether to automatically reconnect
     * @param transport       preferred transport for GATT connections to remote dual-mode devices {@link
     *                        BluetoothDevice#TRANSPORT_AUTO} or {@link BluetoothDevice#TRANSPORT_BREDR} or {@link
     *                        BluetoothDevice#TRANSPORT_LE}
     * @return true means request successful
     */
    @Override
    public boolean connect(@NonNull BluetoothDevice bluetoothDevice, boolean autoReconnect, @Nullable Transport transport) {
        return connect(bluetoothDevice, autoReconnect, transport, null);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param bluetoothDevice BluetoothDevice
     * @param autoReconnect   Whether to automatically reconnect
     * @param phyMask         preferred PHY for connections to remote LE device. Bitwise OR of any of {@link
     *                        BluetoothDevice#PHY_LE_1M_MASK}, {@link BluetoothDevice#PHY_LE_2M_MASK}, and {@link
     *                        BluetoothDevice#PHY_LE_CODED_MASK}. This option does not take effect if {@code autoConnect}
     * @return true means request successful
     */
    @Override
    public boolean connect(@NonNull BluetoothDevice bluetoothDevice, boolean autoReconnect, @Nullable PhyMask phyMask) {
        return connect(bluetoothDevice, autoReconnect, null, phyMask);
    }

    /**
     * Initiate a request to connect to a remote device
     *
     * @param bluetoothDevice BluetoothDevice
     * @param transport       preferred transport for GATT connections to remote dual-mode devices {@link
     *                        BluetoothDevice#TRANSPORT_AUTO} or {@link BluetoothDevice#TRANSPORT_BREDR} or {@link
     *                        BluetoothDevice#TRANSPORT_LE}
     * @param phyMask         preferred PHY for connections to remote LE device. Bitwise OR of any of {@link
     *                        BluetoothDevice#PHY_LE_1M_MASK}, {@link BluetoothDevice#PHY_LE_2M_MASK}, and {@link
     *                        BluetoothDevice#PHY_LE_CODED_MASK}. This option does not take effect if {@code autoConnect}
     * @return true means request successful
     */
    @Override
    public boolean connect(@NonNull BluetoothDevice bluetoothDevice, @Nullable Transport transport, @Nullable PhyMask phyMask) {
        return connect(bluetoothDevice, false, transport, phyMask);
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
    @Override
    public boolean connect(@NonNull final BluetoothDevice bluetoothDevice, final boolean autoReconnect, @Nullable Transport transport, @Nullable PhyMask phyMask) {
        if (!isInitialized()) {
            return false;
        }
        //noinspection ConstantConditions
        boolean result = bluetoothLeService != null && bluetoothLeService.connect(bluetoothDevice, autoReconnect, transport, phyMask);
        if (result) {
            closed = false;
            checkConnectTimeOut();
        }
        return result;
    }

    /**
     * Unbind device
     *
     * @return true means request successful
     */

    @Override
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    public boolean unBound() {
        if (bondAddress == null) {
            return false;
        }
        return BleManager.unBound(BleManager.getContext(), bondAddress);
    }

    /**
     * Get BluetoothGatt instance
     *
     * @return BluetoothGatt instance
     */
    @Override
    @Nullable
    public BluetoothGatt getBluetoothGatt() {
        if (bluetoothLeService == null) {
            return null;
        }
        return bluetoothLeService.getBluetoothGatt();
    }

    /**
     * get connection status
     *
     * @return true means remote device is connected
     */
    @Override
    public boolean isConnected() {
        return bluetoothLeService != null && bluetoothLeService.isConnected();
    }

    /**
     * get service discovered status
     *
     * @return true means remote device is discovered
     */
    @Override
    public boolean isServiceDiscovered() {
        return bluetoothLeService != null && bluetoothLeService.isServiceDiscovered();
    }

    /**
     * closeGatt this connection util
     *
     * @return true means closeGatt successful
     */
    @Override
    public boolean close() {
        if (bluetoothLeService == null) {
            return false;
        }
        if (BleManager.getContext() == null) {
            return false;
        }
        writeLargeDataWithNotificationContinueFlag = false;
        writeLargeDataContinueFlag = false;
        bondAddress = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            boundBleBroadcastReceiver.setOnDeviceBondStateChangedListener(null);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                BleManager.getContext().unregisterReceiver(boundBleBroadcastReceiver);
            } catch (Exception e) {
                DebugUtil.verOut(TAG, "unregisterReceiver(boundBleBroadcastReceiver) failed");
            }
        }
        disconnect();
        closeGatt();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            boundBleBroadcastReceiver.setOnDeviceBondStateChangedListener(null);
        }
        checkCloseStatus();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            boundBleBroadcastReceiver = null;
        }
        bluetoothLeService = null;
        closed = true;
        return true;
    }

    /**
     * Send large amounts of data to remote devices and automate packet formatting
     *
     * @param serviceUuid        Service UUID
     * @param characteristicUuid characteristic UUID
     * @param largeData          large data
     */
    @Override
    public void writeLargeData(@NonNull String serviceUuid, @NonNull String characteristicUuid, @NonNull byte[] largeData) {
        writeLargeData(serviceUuid, characteristicUuid, largeData, true);
    }

    /**
     * Send large amounts of data to remote devices
     *
     * @param serviceUuid        Service UUID
     * @param characteristicUuid characteristic UUID
     * @param largeData          large data
     * @param autoFormat         whether to format the packet
     */
    @Override
    public void writeLargeData(@NonNull String serviceUuid, @NonNull String characteristicUuid, @NonNull byte[] largeData, boolean autoFormat) {
        writeLargeData(serviceUuid, characteristicUuid, largeData, new DefaultLargeDataSendStateChangedListener(), autoFormat);
    }

    /**
     * Send large amounts of data to remote devices and automate packet formatting
     *
     * @param serviceUuid                         Service UUID
     * @param characteristicUuid                  characteristic UUID
     * @param largeData                           large data
     * @param onLargeDataSendStateChangedListener Callback during large data transmission
     */
    @Override
    public void writeLargeData(@NonNull String serviceUuid, @NonNull String characteristicUuid, @NonNull byte[] largeData, @Nullable OnLargeDataSendStateChangedListener onLargeDataSendStateChangedListener) {
        writeLargeData(serviceUuid, characteristicUuid, largeData, sendLargeDataPackageDelayTime, onLargeDataSendStateChangedListener);
    }

    /**
     * Send large amounts of data to remote devices
     *
     * @param serviceUuid                         Service UUID
     * @param characteristicUuid                  characteristic UUID
     * @param largeData                           large data
     * @param onLargeDataSendStateChangedListener Callback during large data transmission
     * @param autoFormat                          whether to format the packet
     */
    @Override
    public void writeLargeData(@NonNull String serviceUuid, @NonNull String characteristicUuid, @NonNull byte[] largeData, @Nullable OnLargeDataSendStateChangedListener onLargeDataSendStateChangedListener, boolean autoFormat) {
        writeLargeData(serviceUuid, characteristicUuid, largeData, sendLargeDataPackageDelayTime, onLargeDataSendStateChangedListener, autoFormat);
    }


    /**
     * Send large amounts of data to remote devices and automate packet formatting
     *
     * @param serviceUuid                         Service UUID
     * @param characteristicUuid                  characteristic UUID
     * @param largeData                           large data
     * @param packageDelayTime                    Time interval between each packet of data
     * @param onLargeDataSendStateChangedListener Callback during large data transmission
     */
    @Override
    public void writeLargeData(@NonNull String serviceUuid, @NonNull String characteristicUuid, @NonNull byte[] largeData, @IntRange(from = 0) int packageDelayTime, @Nullable OnLargeDataSendStateChangedListener onLargeDataSendStateChangedListener) {
        writeLargeData(serviceUuid, characteristicUuid, largeData, packageDelayTime, DEFAULT_MAX_TRY_COUNT, onLargeDataSendStateChangedListener, true);
    }


    /**
     * Send large amounts of data to remote devices
     *
     * @param serviceUuid                         Service UUID
     * @param characteristicUuid                  characteristic UUID
     * @param largeData                           large data
     * @param packageDelayTime                    Time interval between each packet of data
     * @param onLargeDataSendStateChangedListener Callback during large data transmission
     * @param autoFormat                          whether to format the packet
     */
    @Override
    public void writeLargeData(@NonNull String serviceUuid, @NonNull String characteristicUuid, @NonNull byte[] largeData, @IntRange(from = 0) int packageDelayTime, @Nullable OnLargeDataSendStateChangedListener onLargeDataSendStateChangedListener, boolean autoFormat) {
        writeLargeData(serviceUuid, characteristicUuid, largeData, packageDelayTime, DEFAULT_MAX_TRY_COUNT, onLargeDataSendStateChangedListener, autoFormat);
    }

    /**
     * Send large amounts of data to remote devices
     *
     * @param serviceUuid                         Service UUID
     * @param characteristicUuid                  characteristic UUID
     * @param largeData                           large data
     * @param packageDelayTime                    ime interval between each packet of data
     * @param maxTryCount                         Maximum number of retransmissions per packet of data
     * @param onLargeDataSendStateChangedListener Callback during large data transmission
     * @param autoFormat                          whether to format the packet
     */
    @Override
    public void writeLargeData(@NonNull String serviceUuid, @NonNull String characteristicUuid, @NonNull byte[] largeData, @IntRange(from = 0) int packageDelayTime, @IntRange(from = 0) int maxTryCount, @Nullable OnLargeDataSendStateChangedListener onLargeDataSendStateChangedListener, boolean autoFormat) {
        startThreadToWriteLargeData(serviceUuid, characteristicUuid, largeData, largeData.length, packageDelayTime, maxTryCount, onLargeDataSendStateChangedListener, autoFormat);
    }

    /**
     * closeGatt GATT connection
     *
     * @return true means close Gatt successful
     */
    @Override
    public boolean closeGatt() {
        return bluetoothLeService != null && bluetoothLeService.closeGatt();
    }

    /**
     * write data to remote device.Result for request will be trigger callback {@link OnBleConnectStateChangedListener#writeCharacteristicData(BluetoothGattCharacteristic, byte[])}
     *
     * @param serviceUUID        service UUID
     * @param characteristicUUID characteristic UUID
     * @param data               data
     * @return true means request successful
     */
    @Override
    public boolean writeData(@NonNull String serviceUUID, @NonNull String characteristicUUID, @NonNull byte[] data) {
        DebugUtil.warnOut(TAG, "bluetoothLeService == " + bluetoothLeService);
        return bluetoothLeService != null && bluetoothLeService.writeData(serviceUUID, characteristicUUID, data);
    }

    /**
     * read data from remote device.Result for request will be trigger callback {@link OnBleConnectStateChangedListener#readCharacteristicData(BluetoothGattCharacteristic, byte[])}
     *
     * @param serviceUUID        service UUID
     * @param characteristicUUID characteristic UUID
     * @return true means request successful
     */
    @Override
    public boolean readData(@NonNull String serviceUUID, @NonNull String characteristicUUID) {
        return bluetoothLeService != null && bluetoothLeService.readData(serviceUUID, characteristicUUID);
    }

    /**
     * enable or disable notification
     *
     * @param serviceUUID        service UUID
     * @param characteristicUUID characteristic UUID
     * @param enable             true means enable notification,false means disable notification
     * @return true means successful
     */
    @Override
    public boolean enableNotification(@NonNull String serviceUUID, @NonNull String characteristicUUID, boolean enable) {
        return bluetoothLeService != null && bluetoothLeService.enableNotification(serviceUUID, characteristicUUID, enable);
    }

    /**
     * get remote device RSSI.Result for request will be trigger callback {@link OnBleConnectStateChangedListener#readRemoteRssi(int)}
     *
     * @return true means request successful
     */
    @Override
    public boolean getRssi() {
        return bluetoothLeService != null && bluetoothLeService.getRssi();
    }

    /**
     * refresh GATT cache.
     * Notice:Some Custom system return true but not take effect.There is no solution so far.
     *
     * @return true means successful.
     */
    @Override
    public boolean refreshGattCache() {
        return bluetoothLeService != null && bluetoothLeService.refreshGattCache();
    }

    /**
     * get remote service list
     *
     * @return service Bluetooth Gatt list
     */
    @Override
    @Nullable
    public List<BluetoothGattService> getServices() {
        if (bluetoothLeService == null) {
            return null;
        }
        return bluetoothLeService.getServices();
    }

    /**
     * get remote device service by UUID
     *
     * @param uuid UUID
     * @return Bluetooth Gatt Service
     */
    @Override
    @Nullable
    public BluetoothGattService getService(UUID uuid) {
        if (bluetoothLeService == null) {
            return null;
        }
        return bluetoothLeService.getService(uuid);
    }

    /**
     * write large data and require remote devices to notify collaboration.And automatically format the packet.
     *
     * @param serviceUUID        service UUID for writing data and receiving notification data
     * @param characteristicUUID characteristic UUID for writing data and receiving notification data
     * @param largeData          large data
     * @return true means request successful
     */
    @Override
    public boolean writeLargeDataWithNotification(@NonNull String serviceUUID, @NonNull String characteristicUUID, @NonNull byte[] largeData) {
        return writeLargeDataWithNotification(serviceUUID, characteristicUUID, largeData, new DefaultLargeDataWriteWithNotificationSendStateChangedListener());
    }

    /**
     * write large data and require remote devices to notify collaboration
     *
     * @param serviceUUID        service UUID for writing data and receiving notification data
     * @param characteristicUUID characteristic UUID for writing data and receiving notification data
     * @param largeData          large data
     * @param autoFormat         whether to format the packet
     * @return true means request successful
     */
    @Override
    public boolean writeLargeDataWithNotification(@NonNull String serviceUUID, @NonNull String characteristicUUID, @NonNull byte[] largeData, boolean autoFormat) {
        return writeLargeDataWithNotification(serviceUUID, characteristicUUID, largeData, new DefaultLargeDataWriteWithNotificationSendStateChangedListener(), autoFormat);
    }

    /**
     * write large data and require remote devices to notify collaboration.And automatically format the packet.
     *
     * @param serviceUUID                                              service UUID for writing data and receiving notification data
     * @param characteristicUUID                                       characteristic UUID for writing data and receiving notification data
     * @param largeData                                                large data
     * @param onLargeDataWriteWithNotificationSendStateChangedListener Callback that write large data and require remote devices to notify collaboration
     * @return true means request successful
     */
    @Override
    public boolean writeLargeDataWithNotification(@NonNull String serviceUUID, @NonNull String characteristicUUID, @NonNull byte[] largeData, @Nullable OnLargeDataWriteWithNotificationSendStateChangedListener onLargeDataWriteWithNotificationSendStateChangedListener) {
        return writeLargeDataWithNotification(serviceUUID, characteristicUUID, largeData, sendLargeDataPackageDelayTime, onLargeDataWriteWithNotificationSendStateChangedListener);
    }

    /**
     * write large data and require remote devices to notify collaboration
     *
     * @param serviceUUID                                              service UUID for writing data and receiving notification data
     * @param characteristicUUID                                       characteristic UUID for writing data and receiving notification data
     * @param largeData                                                large data
     * @param onLargeDataWriteWithNotificationSendStateChangedListener Callback that write large data and require remote devices to notify collaboration
     * @param autoFormat                                               whether to format the packet
     * @return true means request successful
     */
    @Override
    public boolean writeLargeDataWithNotification(@NonNull String serviceUUID, @NonNull String characteristicUUID, @NonNull byte[] largeData, @Nullable OnLargeDataWriteWithNotificationSendStateChangedListener onLargeDataWriteWithNotificationSendStateChangedListener, boolean autoFormat) {
        return writeLargeDataWithNotification(serviceUUID, characteristicUUID, largeData, sendLargeDataPackageDelayTime, onLargeDataWriteWithNotificationSendStateChangedListener, autoFormat);
    }

    /**
     * write large data and require remote devices to notify collaboration.And automatically format the packet.
     *
     * @param serviceUUID                                              service UUID for writing data and receiving notification data
     * @param characteristicUUID                                       characteristic UUID for writing data and receiving notification data
     * @param largeData                                                large data
     * @param packageDelayTime                                         Time interval between each packet of data
     * @param onLargeDataWriteWithNotificationSendStateChangedListener Callback that write large data and require remote devices to notify collaboration
     * @return true means request successful
     */
    @Override
    public boolean writeLargeDataWithNotification(@NonNull String serviceUUID, @NonNull String characteristicUUID,
                                                  @NonNull byte[] largeData, @IntRange(from = 0) int packageDelayTime,
                                                  @Nullable OnLargeDataWriteWithNotificationSendStateChangedListener onLargeDataWriteWithNotificationSendStateChangedListener) {
        return writeLargeDataWithNotification(serviceUUID, characteristicUUID, largeData, packageDelayTime, DEFAULT_MAX_TRY_COUNT, onLargeDataWriteWithNotificationSendStateChangedListener);
    }

    /**
     * write large data and require remote devices to notify collaboration
     *
     * @param serviceUUID                                              service UUID for writing data and receiving notification data
     * @param characteristicUUID                                       characteristic UUID for writing data and receiving notification data
     * @param largeData                                                large data
     * @param packageDelayTime                                         Time interval between each packet of data
     * @param onLargeDataWriteWithNotificationSendStateChangedListener Callback that write large data and require remote devices to notify collaboration
     * @param autoFormat                                               whether to format the packet
     * @return true means request successful
     */
    @Override
    public boolean writeLargeDataWithNotification(@NonNull String serviceUUID, @NonNull String characteristicUUID,
                                                  @NonNull byte[] largeData, int packageDelayTime,
                                                  @Nullable OnLargeDataWriteWithNotificationSendStateChangedListener onLargeDataWriteWithNotificationSendStateChangedListener,
                                                  boolean autoFormat) {
        return writeLargeDataWithNotification(serviceUUID, characteristicUUID, largeData, packageDelayTime, DEFAULT_MAX_TRY_COUNT, onLargeDataWriteWithNotificationSendStateChangedListener, autoFormat);
    }

    /**
     * write large data and require remote devices to notify collaboration.And automatically format the packet.
     *
     * @param serviceUUID                                              service UUID for writing data and receiving notification data
     * @param characteristicUUID                                       characteristic UUID for writing data and receiving notification data
     * @param largeData                                                large data
     * @param packageDelayTime                                         Time interval between each packet of data
     * @param maxTryCount                                              Maximum number of retries
     * @param onLargeDataWriteWithNotificationSendStateChangedListener Callback that write large data and require remote devices to notify collaboration
     * @return true means request successful
     */
    @Override
    public boolean writeLargeDataWithNotification(@NonNull String serviceUUID, @NonNull String characteristicUUID,
                                                  @NonNull byte[] largeData, @IntRange(from = 0) int packageDelayTime, @IntRange(from = 0) int maxTryCount,
                                                  @Nullable OnLargeDataWriteWithNotificationSendStateChangedListener onLargeDataWriteWithNotificationSendStateChangedListener) {
        return writeLargeDataWithNotification(serviceUUID, characteristicUUID, serviceUUID, characteristicUUID, largeData, packageDelayTime, maxTryCount, onLargeDataWriteWithNotificationSendStateChangedListener, true);
    }

    /**
     * write large data and require remote devices to notify collaboration.
     *
     * @param serviceUUID                                              service UUID for writing data and receiving notification data
     * @param characteristicUUID                                       characteristic UUID for writing data and receiving notification data
     * @param largeData                                                large data
     * @param packageDelayTime                                         Time interval between each packet of data
     * @param maxTryCount                                              Maximum number of retries
     * @param onLargeDataWriteWithNotificationSendStateChangedListener Callback that write large data and require remote devices to notify collaboration
     * @param autoFormat                                               whether to format the packet
     * @return true means request successful
     */
    @Override
    public boolean writeLargeDataWithNotification(@NonNull String serviceUUID, @NonNull String characteristicUUID,
                                                  @NonNull byte[] largeData, @IntRange(from = 0) int packageDelayTime, @IntRange(from = 0) int maxTryCount,
                                                  @Nullable OnLargeDataWriteWithNotificationSendStateChangedListener onLargeDataWriteWithNotificationSendStateChangedListener,
                                                  boolean autoFormat) {
        return writeLargeDataWithNotification(serviceUUID, characteristicUUID, serviceUUID, characteristicUUID, largeData, packageDelayTime, maxTryCount, onLargeDataWriteWithNotificationSendStateChangedListener, autoFormat);
    }

    /**
     * write large data and require remote devices to notify collaboration.
     *
     * @param writeDataServiceUUID                                     service UUID for writing data
     * @param writeDataCharacteristicUUID                              characteristic UUID for writing data
     * @param notificationServiceUUID                                  service UUID for receiving notification data
     * @param notificationCharacteristicUUID                           characteristic UUID for receiving notification data
     * @param largeData                                                large data
     * @param packageDelayTime                                         Time interval between each packet of data
     * @param maxTryCount                                              Maximum number of retries
     * @param onLargeDataWriteWithNotificationSendStateChangedListener Callback that write large data and require remote devices to notify collaboration
     * @param autoFormat                                               whether to format the packet
     * @return true means request successful
     */
    @Override
    public boolean writeLargeDataWithNotification(@NonNull final String writeDataServiceUUID, @NonNull final String writeDataCharacteristicUUID,
                                                  @NonNull final String notificationServiceUUID, @NonNull final String notificationCharacteristicUUID,
                                                  @NonNull final byte[] largeData, @IntRange(from = 0) final int packageDelayTime,
                                                  @IntRange(from = 0) final int maxTryCount,
                                                  @Nullable final OnLargeDataWriteWithNotificationSendStateChangedListener onLargeDataWriteWithNotificationSendStateChangedListener,
                                                  final boolean autoFormat) {
        final int length = largeData.length;
        if (!canWrite(writeDataServiceUUID, writeDataCharacteristicUUID)) {
            return false;
        }
        if (!canNotify(notificationServiceUUID, notificationCharacteristicUUID)) {
            return false;
        }
        OnBleDescriptorWriteListener onBleDescriptorWriteListener = new OnBleDescriptorWriteListener() {
            /**
             * descriptor write successful
             *
             * @param bluetoothGattDescriptor BluetoothGattDescriptor
             * @param data                    descriptor
             */
            @Override
            public void onBleDescriptorWrite(BluetoothGattDescriptor bluetoothGattDescriptor, byte[] data) {
                DebugUtil.warnOut(TAG, "open notification success");
                if (!removeOnBleDescriptorWriteListener(this)) {
                    performLargeDataSendWithNotificationStartFailedListener(onLargeDataWriteWithNotificationSendStateChangedListener);
                    return;
                }
                startThreadToWriteLargeDataWithNotification(largeData, length, maxTryCount, writeDataServiceUUID, writeDataCharacteristicUUID, notificationCharacteristicUUID, packageDelayTime, onLargeDataWriteWithNotificationSendStateChangedListener, autoFormat);
            }
        };
        if (!addOnBleDescriptorWriteListener(onBleDescriptorWriteListener)) {
            performLargeDataSendWithNotificationStartFailedListener(onLargeDataWriteWithNotificationSendStateChangedListener);
            return false;
        }

        return enableNotification(notificationServiceUUID, notificationCharacteristicUUID, true);
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
        if (bluetoothLeService == null) {
            return false;
        }

        return bluetoothLeService.canNotify(characteristic);
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
        if (bluetoothLeService == null) {
            return false;
        }

        return bluetoothLeService.canRead(characteristic);
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
        if (bluetoothLeService == null) {
            return false;
        }

        return bluetoothLeService.canSignedWrite(characteristic);
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

        if (bluetoothLeService == null) {
            return false;
        }

        return bluetoothLeService.canWrite(characteristic);
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
        if (bluetoothLeService == null) {
            return false;
        }
        return bluetoothLeService.canWriteNoResponse(characteristic);
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
        if (bluetoothLeService == null) {
            return false;
        }
        return bluetoothLeService.beginReliableWrite();
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
        if (bluetoothLeService == null) {
            return false;
        }
        return bluetoothLeService.abortReliableWrite();
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
        if (bluetoothLeService == null) {
            return false;
        }
        return bluetoothLeService.discoverServices();
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
        if (bluetoothLeService == null) {
            return false;
        }
        return bluetoothLeService.executeReliableWrite();
    }

    /*-----------------------------------public method-----------------------------------*/

    /**
     * set BLE device connect status changed listener
     *
     * @param onBleConnectStateChangedListener BLE device connect status changed listener
     */
    public void setOnBleConnectStateChangedListener(@Nullable OnBleConnectStateChangedListener onBleConnectStateChangedListener) {
        this.onBleConnectStateChangedListener = onBleConnectStateChangedListener;
        if (bluetoothLeService != null) {
            bluetoothLeService.setOnBleConnectStateChangedListener(onBleConnectStateChangedListener);
        }
    }

    /**
     * Set the callback when the binding state changes
     *
     * @param onDeviceBondStateChangedListener binding state changes callback
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setOnDeviceBondStateChangedListener(@Nullable OnDeviceBondStateChangedListener onDeviceBondStateChangedListener) {
        if (boundBleBroadcastReceiver == null) {
            return;
        }
        boundBleBroadcastReceiver.setOnDeviceBondStateChangedListener(onDeviceBondStateChangedListener);
    }

    /**
     * stop BLE connection service
     */
    public void stopService() {
        if (bluetoothLeService == null) {
            return;
        }
        bluetoothLeService.stopSelf();
    }

    /**
     * add a callback triggered when descriptor write successful
     *
     * @param onBleDescriptorWriteListener callback triggered when descriptor write successful
     * @return true means successful
     */
    public boolean addOnBleDescriptorWriteListener(@NonNull OnBleDescriptorWriteListener onBleDescriptorWriteListener) {
        if (bluetoothLeService == null) {
            return false;
        }
        return bluetoothLeService.addOnBleDescriptorWriteListener(onBleDescriptorWriteListener);
    }

    /**
     * remove a callback triggered when descriptor write successful
     *
     * @param onBleDescriptorWriteListener callback triggered when descriptor write successful
     * @return true means successful
     */
    public boolean removeOnBleDescriptorWriteListener(@NonNull OnBleDescriptorWriteListener onBleDescriptorWriteListener) {
        if (bluetoothLeService == null) {
            return false;
        }
        return bluetoothLeService.removeOnBleDescriptorWriteListener(onBleDescriptorWriteListener);
    }

    /**
     * add a callback triggered when received notification data
     *
     * @param onBleReceiveNotificationListener callback triggered when received notification data
     * @return true means successful
     */
    @SuppressWarnings("WeakerAccess")
    public boolean addOnBleReceiveNotificationListener(@NonNull OnBleReceiveNotificationListener onBleReceiveNotificationListener) {
        if (bluetoothLeService == null) {
            return false;
        }
        return bluetoothLeService.addOnBleReceiveNotificationListener(onBleReceiveNotificationListener);
    }

    /**
     * remove a callback triggered when received notification data
     *
     * @param onBleReceiveNotificationListener callback triggered when received notification data
     * @return true means successful
     */
    @SuppressWarnings("WeakerAccess")
    public boolean removeOnBleReceiveNotificationListener(@NonNull OnBleReceiveNotificationListener onBleReceiveNotificationListener) {
        if (bluetoothLeService == null) {
            return false;
        }
        return bluetoothLeService.removeOnBleReceiveNotificationListener(onBleReceiveNotificationListener);
    }

    /**
     * add a callback triggered when gatt characteristic write data successful
     *
     * @param onBleCharacteristicWriteListener callback triggered when gatt characteristic write data successful
     * @return true means successful
     */
    @SuppressWarnings({"WeakerAccess", "BooleanMethodIsAlwaysInverted"})
    public boolean addOnBleCharacteristicWriteListener(@NonNull OnBleCharacteristicWriteListener onBleCharacteristicWriteListener) {
        if (bluetoothLeService == null) {
            return false;
        }
        return bluetoothLeService.addOnBleCharacteristicWriteListener(onBleCharacteristicWriteListener);
    }

    /**
     * remove a callback triggered when gatt characteristic write data successful
     *
     * @param onBleCharacteristicWriteListener callback triggered when gatt characteristic write data successful
     * @return true means successful
     */
    @SuppressWarnings("WeakerAccess")
    public boolean removeOnBleCharacteristicWriteListener(@NonNull OnBleCharacteristicWriteListener onBleCharacteristicWriteListener) {
        if (bluetoothLeService == null) {
            return false;
        }
        return bluetoothLeService.removeOnBleCharacteristicWriteListener(onBleCharacteristicWriteListener);
    }

    /*-----------------------------------private method-----------------------------------*/

    /**
     * check connect time out
     */
    private void checkConnectTimeOut() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                DebugUtil.warnOut(TAG, "time out thread start");
                long startTime = System.currentTimeMillis();
                while (!closed) {
                    if (isConnected() && isServiceDiscovered()) {
                        DebugUtil.warnOut(TAG, "connected and serviceDiscovered,cancel time out thread");
                        break;
                    }
                    if (System.currentTimeMillis() - startTime >= connectTimeOut) {
                        BleManager.getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                DebugUtil.warnOut(TAG, "connect time out");
                                if (onBleConnectStateChangedListener != null) {
                                    onBleConnectStateChangedListener.onConnectTimeOut();
                                }
                            }
                        });
                        break;
                    }
                }
                DebugUtil.warnOut(TAG, "time out thread end");
            }
        };
        BleManager.getThreadFactory().newThread(runnable).start();
    }

    /**
     * create a thread to write large data
     *
     * @param largeData                                                large data
     * @param dataLength                                               large data length
     * @param maxTryCount                                              if a packet write failed,will retry to write,It is max try count to set.
     * @param writeDataServiceUUID                                     service uuid to write data
     * @param writeDataCharacteristicUUID                              characteristic uuid to write data
     * @param notificationCharacteristicUUID                           characteristic uuid to receive notification data
     * @param packageDelayTime                                         Time interval between each packet of data
     * @param onLargeDataWriteWithNotificationSendStateChangedListener Callback that write large data and require remote devices to notify collaboration
     * @param autoFormat                                               whether to format the packet
     */
    private void startThreadToWriteLargeDataWithNotification(@NonNull final byte[] largeData,
                                                             final int dataLength,
                                                             final int maxTryCount,
                                                             @NonNull final String writeDataServiceUUID,
                                                             @NonNull final String writeDataCharacteristicUUID,
                                                             @NonNull final String notificationCharacteristicUUID,
                                                             final int packageDelayTime,
                                                             @Nullable final OnLargeDataWriteWithNotificationSendStateChangedListener onLargeDataWriteWithNotificationSendStateChangedListener,
                                                             final boolean autoFormat) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final byte[][] packetData = {new byte[0]};
                final int writeLargeDataWithNotificationPackageCount = getPageCount(dataLength, autoFormat);
                final int[] writeLargeDataWithNotificationCurrentPackageCount = {0};
                //Record the number of data retransmissions
                final int[] writeLargeDataWithNotificationTryCount = {0};
                writeLargeDataWithNotificationContinueFlag = true;
                final int[] wrongNotificationResultCount = {0};
                final boolean[] receivedNotification = {true};
                final boolean[] characteristicWriteSuccess = {true};

                OnBleReceiveNotificationListener onBleReceiveNotificationListener = new OnBleReceiveNotificationListener() {
                    /**
                     * received remote device data
                     *
                     * @param gattCharacteristic BluetoothGattCharacteristic
                     * @param data                        received data
                     */
                    @Override
                    public void onBleReceiveNotification(BluetoothGattCharacteristic gattCharacteristic, byte[] data) {
                        String uuidString = gattCharacteristic.getUuid().toString();

                        if (!uuidString.equalsIgnoreCase(notificationCharacteristicUUID)) {
                            return;
                        }
                        performLargeDataWriteWithNotificationSendStateChangedListener(data, onLargeDataWriteWithNotificationSendStateChangedListener, writeLargeDataWithNotificationCurrentPackageCount, writeLargeDataWithNotificationPackageCount, largeData, autoFormat, wrongNotificationResultCount, maxTryCount, receivedNotification);
                    }
                };

                OnBleCharacteristicWriteListener onBleCharacteristicWriteListener = new OnBleCharacteristicWriteListener() {
                    @Override
                    public void onBleCharacteristicWrite(BluetoothGattCharacteristic gattCharacteristic, byte[] data) {
                        String uuidString = gattCharacteristic.getUuid().toString();
                        if (!uuidString.equalsIgnoreCase(writeDataCharacteristicUUID)) {
                            return;
                        }
                        writeLargeDataWithNotificationTryCount[0] = 0;
                        characteristicWriteSuccess[0] = true;
                        performLargeDataWriteWithNotificationSendProgressChangedListener(writeLargeDataWithNotificationPackageCount, packetData[0], writeLargeDataWithNotificationCurrentPackageCount[0], onLargeDataWriteWithNotificationSendStateChangedListener);
                        writeLargeDataWithNotificationCurrentPackageCount[0]++;
                    }
                };
                if (!addOnBleReceiveNotificationListener(onBleReceiveNotificationListener)) {
                    performLargeDataSendWithNotificationStartFailedListener(onLargeDataWriteWithNotificationSendStateChangedListener);
                    return;
                }
                if (!addOnBleCharacteristicWriteListener(onBleCharacteristicWriteListener)) {
                    performLargeDataSendWithNotificationStartFailedListener(onLargeDataWriteWithNotificationSendStateChangedListener);
                    return;
                }
                performLargeDataWriteWithNotificationSendStartListener(onLargeDataWriteWithNotificationSendStateChangedListener);
                long lastSystemTime = System.currentTimeMillis();
                doTransmission(packetData, writeLargeDataWithNotificationPackageCount, writeLargeDataWithNotificationCurrentPackageCount, writeLargeDataWithNotificationTryCount, receivedNotification, characteristicWriteSuccess, lastSystemTime, maxTryCount, onLargeDataWriteWithNotificationSendStateChangedListener, writeDataServiceUUID, writeDataCharacteristicUUID, packageDelayTime, largeData, autoFormat);
                removeOnBleReceiveNotificationListener(onBleReceiveNotificationListener);
                removeOnBleCharacteristicWriteListener(onBleCharacteristicWriteListener);
            }
        };
        BleManager.getThreadFactory().newThread(runnable).start();
    }

    /**
     * send large data to remote device
     *
     * @param data                                                     data
     * @param writeLargeDataWithNotificationPackageCount               total package count
     * @param writeLargeDataWithNotificationCurrentPackageCount        current package number
     * @param writeLargeDataWithNotificationTryCount                   try count
     * @param receivedNotification                                     received notification data
     * @param characteristicWriteSuccess                               Whether the write is successful
     * @param lastSystemTime                                           System time when the last write was performed
     * @param maxTryCount                                              max try count
     * @param onLargeDataWriteWithNotificationSendStateChangedListener Callback that write large data and require remote devices to notify collaboration
     * @param writeDataServiceUUID                                     service uuid to write data
     * @param writeDataCharacteristicUUID                              characteristic uuid to write data
     * @param packageDelayTime                                         Time interval between each packet of data
     * @param largeData                                                large data
     * @param autoFormat                                               whether to format the packet
     */
    private void doTransmission(@NonNull byte[][] data, int writeLargeDataWithNotificationPackageCount,
                                @NonNull int[] writeLargeDataWithNotificationCurrentPackageCount,
                                @NonNull int[] writeLargeDataWithNotificationTryCount,
                                @NonNull boolean[] receivedNotification,
                                @NonNull boolean[] characteristicWriteSuccess,
                                long lastSystemTime, int maxTryCount,
                                @Nullable OnLargeDataWriteWithNotificationSendStateChangedListener onLargeDataWriteWithNotificationSendStateChangedListener,
                                @NonNull String writeDataServiceUUID, String writeDataCharacteristicUUID,
                                int packageDelayTime,
                                @NonNull byte[] largeData, boolean autoFormat) {

        while (writeLargeDataWithNotificationContinueFlag) {
            if (!characteristicWriteSuccess[0]) {
                if (System.currentTimeMillis() - lastSystemTime >= sendLargeDataTimeOut) {
                    if (writeLargeDataWithNotificationTryCount[0] >= maxTryCount) {
                        performLargeDataWriteWithNotificationSendTimeOut(onLargeDataWriteWithNotificationSendStateChangedListener, writeLargeDataWithNotificationCurrentPackageCount[0] + 1, writeLargeDataWithNotificationPackageCount, data[0]);
                        break;
                    }
                    lastSystemTime = System.currentTimeMillis();
                    writeLargeDataWithNotificationTryCount[0]++;
                    if (data[0] != null) {
                        if (writeData(writeDataServiceUUID, writeDataCharacteristicUUID, data[0])) {
                            performLargeDataWriteWithNotificationSendTimeOutAndRetry(writeLargeDataWithNotificationPackageCount, data[0], writeLargeDataWithNotificationTryCount[0], writeLargeDataWithNotificationCurrentPackageCount[0] + 1, onLargeDataWriteWithNotificationSendStateChangedListener);
                            continue;
                        }
                        sleepTime(RETRY_DELAY_TIME);
                    }
                }
                continue;
            }
            DebugUtil.warnOut(TAG, "send state characteristicWriteSuccess");
            if (!receivedNotification[0]) {
                if (System.currentTimeMillis() - lastSystemTime >= sendLargeDataTimeOut) {
                    if (writeLargeDataWithNotificationTryCount[0] >= maxTryCount) {
                        performLargeDataWriteWithNotificationSendTimeOut(onLargeDataWriteWithNotificationSendStateChangedListener, writeLargeDataWithNotificationCurrentPackageCount[0] + 1, writeLargeDataWithNotificationPackageCount, data[0]);
                        break;
                    }
                    writeLargeDataWithNotificationTryCount[0]++;
                    lastSystemTime = System.currentTimeMillis();
                    if (data[0] != null) {
                        if (writeData(writeDataServiceUUID, writeDataCharacteristicUUID, data[0])) {
                            performLargeDataWriteWithNotificationSendTimeOutAndRetry(writeLargeDataWithNotificationPackageCount, data[0], writeLargeDataWithNotificationTryCount[0], writeLargeDataWithNotificationCurrentPackageCount[0] + 1, onLargeDataWriteWithNotificationSendStateChangedListener);
                            sleepTime(RETRY_DELAY_TIME);
                            continue;
                        }
                    }
                }
                continue;
            }
            DebugUtil.warnOut(TAG, "send state receivedNotification");
            sleepTime(packageDelayTime);
            data[0] = getCurrentPackageData(writeLargeDataWithNotificationCurrentPackageCount[0], largeData, writeLargeDataWithNotificationPackageCount, autoFormat);
            if (data[0] == null) {
                performLargeDataWriteWithNotificationSendFinishedListener(onLargeDataWriteWithNotificationSendStateChangedListener);
                break;
            }
            if (writeLargeDataWithNotificationTryCount[0] >= maxTryCount) {
                performLargeDataWriteWithNotificationSendFailedListener(writeLargeDataWithNotificationPackageCount, data[0], writeLargeDataWithNotificationCurrentPackageCount[0] + 1, onLargeDataWriteWithNotificationSendStateChangedListener);
                break;
            }
            if (!writeData(writeDataServiceUUID, writeDataCharacteristicUUID, data[0])) {
                performLargeDataWriteWithNotificationSendFailedAndRetryListener(writeLargeDataWithNotificationPackageCount, data[0], writeLargeDataWithNotificationTryCount[0], writeLargeDataWithNotificationCurrentPackageCount[0] + 1, onLargeDataWriteWithNotificationSendStateChangedListener);
                writeLargeDataWithNotificationTryCount[0]++;
                DebugUtil.warnOut(TAG, "writeData failed");
                sleepTime(RETRY_DELAY_TIME);
                continue;
            }
            DebugUtil.warnOut(TAG, "sendLargeDataPackageDelayTime = " + packageDelayTime);
        }
    }

    /**
     * Let the current thread delay for a while before continuing
     *
     * @param packageDelayTime delay time (unit:ms)
     */
    private void sleepTime(int packageDelayTime) {
        ThreadUtil.sleep(packageDelayTime);
    }

    /**
     * check close state to trigger call back
     */
    private void checkCloseStatus() {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (onBleConnectStateChangedListener != null) {
                    onBleConnectStateChangedListener.onCloseComplete();
                    onBleConnectStateChangedListener = null;
                }
            }
        });
    }

    /**
     * get broadcast receiver filter
     *
     * @return Broadcast receiver filter
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private IntentFilter makeBoundBLEIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.setPriority(Integer.MAX_VALUE);
        return intentFilter;
    }

    /**
     * get total package count
     *
     * @param dataLength data length
     * @param autoFormat whether to format the packet
     * @return total package count
     */
    private int getPageCount(int dataLength, boolean autoFormat) {

        if (autoFormat) {
            if (dataLength % LARGE_DATA_AUTO_FORMAT_TRANSFORM_PACKAGE_MAX_LENGTH == 0) {
                return dataLength / LARGE_DATA_AUTO_FORMAT_TRANSFORM_PACKAGE_MAX_LENGTH;
            } else {
                return (dataLength / LARGE_DATA_AUTO_FORMAT_TRANSFORM_PACKAGE_MAX_LENGTH) + 1;
            }
        } else {
            if (dataLength % PACKAGE_MAX_LENGTH == 0) {
                return dataLength / PACKAGE_MAX_LENGTH;
            } else {
                return (dataLength / PACKAGE_MAX_LENGTH) + 1;
            }
        }
    }

    /**
     * get package data by specified index
     *
     * @param packageIndex index
     * @param largeData    large data
     * @param pageCount    total count
     * @param autoFormat   whether to format the packet
     * @return packet data
     */
    @Nullable
    private byte[] getCurrentPackageData(int packageIndex, @NonNull byte[] largeData, int pageCount, boolean autoFormat) {
        if (packageIndex >= pageCount) {
            return null;
        }
        int largeDataLength = largeData.length;
        if (autoFormat) {
            if (packageIndex == pageCount - 1) {
                int remainder = largeDataLength % LARGE_DATA_AUTO_FORMAT_TRANSFORM_PACKAGE_MAX_LENGTH;
                if (remainder == 0) {
                    byte[] data = new byte[20];
                    data[0] = (byte) pageCount;
                    data[1] = (byte) (packageIndex + 1);
                    data[2] = LARGE_DATA_AUTO_FORMAT_TRANSFORM_PACKAGE_MAX_LENGTH;
                    System.arraycopy(largeData, packageIndex * LARGE_DATA_AUTO_FORMAT_TRANSFORM_PACKAGE_MAX_LENGTH, data, PACKAGE_MAX_LENGTH - LARGE_DATA_AUTO_FORMAT_TRANSFORM_PACKAGE_MAX_LENGTH, data.length - (PACKAGE_MAX_LENGTH - LARGE_DATA_AUTO_FORMAT_TRANSFORM_PACKAGE_MAX_LENGTH));
                    return data;
                } else {
                    byte[] data = new byte[remainder + PACKAGE_MAX_LENGTH - LARGE_DATA_AUTO_FORMAT_TRANSFORM_PACKAGE_MAX_LENGTH];
                    data[0] = (byte) pageCount;
                    data[1] = (byte) (packageIndex + 1);
                    data[2] = (byte) remainder;
                    System.arraycopy(largeData, packageIndex * LARGE_DATA_AUTO_FORMAT_TRANSFORM_PACKAGE_MAX_LENGTH, data, PACKAGE_MAX_LENGTH - LARGE_DATA_AUTO_FORMAT_TRANSFORM_PACKAGE_MAX_LENGTH, data.length - (PACKAGE_MAX_LENGTH - LARGE_DATA_AUTO_FORMAT_TRANSFORM_PACKAGE_MAX_LENGTH));
                    return data;
                }
            } else {
                byte[] data = new byte[20];
                data[0] = (byte) pageCount;
                data[1] = (byte) (packageIndex + 1);
                data[2] = (byte) LARGE_DATA_AUTO_FORMAT_TRANSFORM_PACKAGE_MAX_LENGTH;
                System.arraycopy(largeData, packageIndex * LARGE_DATA_AUTO_FORMAT_TRANSFORM_PACKAGE_MAX_LENGTH, data, PACKAGE_MAX_LENGTH - LARGE_DATA_AUTO_FORMAT_TRANSFORM_PACKAGE_MAX_LENGTH, data.length - (PACKAGE_MAX_LENGTH - LARGE_DATA_AUTO_FORMAT_TRANSFORM_PACKAGE_MAX_LENGTH));
                return data;
            }
        } else {
            if (packageIndex == pageCount - 1) {
                int remainder = largeDataLength % PACKAGE_MAX_LENGTH;
                if (remainder == 0) {
                    byte[] data = new byte[20];
                    System.arraycopy(largeData, packageIndex * PACKAGE_MAX_LENGTH, data, 0, data.length);
                    return data;
                } else {
                    byte[] data = new byte[remainder];
                    System.arraycopy(largeData, packageIndex * PACKAGE_MAX_LENGTH, data, 0, data.length);
                    return data;
                }
            } else {
                byte[] data = new byte[20];
                System.arraycopy(largeData, packageIndex * PACKAGE_MAX_LENGTH, data, 0, data.length);
                return data;
            }
        }
    }

    /**
     * create a thread to send large data
     *
     * @param serviceUuid                         service uuid to write data
     * @param characteristicUuid                  characteristic uuid to write data
     * @param largeData                           large data
     * @param dataLength                          large data length
     * @param packageDelayTime                    Time interval between each packet of data
     * @param maxTryCount                         max try count
     * @param onLargeDataSendStateChangedListener Callback during large data transmission
     * @param autoFormat                          whether to format the packet data
     */
    private void startThreadToWriteLargeData(@NonNull final String serviceUuid,
                                             @NonNull final String characteristicUuid,
                                             @NonNull final byte[] largeData, final int dataLength,
                                             final int packageDelayTime, final int maxTryCount,
                                             @Nullable final OnLargeDataSendStateChangedListener onLargeDataSendStateChangedListener,
                                             final boolean autoFormat) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int pageCount = getPageCount(dataLength, autoFormat);
                final int[] currentPackageCount = {0};
                final int[] tryCount = {0};
                byte[] data = null;
                writeLargeDataContinueFlag = true;
                final boolean[] characteristicWriteSuccess = {true};

                OnBleCharacteristicWriteListener onBleCharacteristicWriteListener = new OnBleCharacteristicWriteListener() {
                    @Override
                    public void onBleCharacteristicWrite(BluetoothGattCharacteristic gattCharacteristic, byte[] data) {
                        String uuidString = gattCharacteristic.getUuid().toString();
                        if (!uuidString.equalsIgnoreCase(characteristicUuid)) {
                            return;
                        }
                        tryCount[0] = 0;
                        currentPackageCount[0]++;
                        characteristicWriteSuccess[0] = true;
                    }
                };

                if (!addOnBleCharacteristicWriteListener(onBleCharacteristicWriteListener)) {
                    performLargeDataSendStartFailedListener(onLargeDataSendStateChangedListener);
                    return;
                }
                performLargeDataSendStartedListener(onLargeDataSendStateChangedListener);
                long lastSystemTime = System.currentTimeMillis();
                while (writeLargeDataContinueFlag) {
                    if (!characteristicWriteSuccess[0]) {
                        if (System.currentTimeMillis() - lastSystemTime >= sendLargeDataTimeOut) {
                            if (tryCount[0] >= maxTryCount) {
                                if (data != null) {
                                    performLargeDataWriteSendTimeOut(onLargeDataSendStateChangedListener, pageCount, currentPackageCount[0], data);
                                }
                                break;
                            }
                            lastSystemTime = System.currentTimeMillis();
                            tryCount[0]++;
                            if (data != null) {
                                if (writeData(serviceUuid, characteristicUuid, data)) {
                                    performLargeDataWriteSendTimeOutAndRetry(pageCount, data, tryCount[0], currentPackageCount[0] + 1, onLargeDataSendStateChangedListener);
                                    continue;
                                }
                                sleepTime(RETRY_DELAY_TIME);
                                continue;
                            }
                        }
                        continue;
                    }
                    sleepTime(packageDelayTime);
                    data = getCurrentPackageData(currentPackageCount[0], largeData, pageCount, autoFormat);
                    if (data == null) {
                        performLargeDataSendFinishedListener(onLargeDataSendStateChangedListener);
                        break;
                    }
                    performLargeDataSendProgressChangedListener(pageCount, data, currentPackageCount[0], onLargeDataSendStateChangedListener);
                    if (tryCount[0] >= maxTryCount) {
                        performLargeDataSendFailedListener(pageCount, data, currentPackageCount[0], onLargeDataSendStateChangedListener);
                        break;
                    }
                    if (!writeData(serviceUuid, characteristicUuid, data)) {
                        performLargeDataSendFailedAndRetryListener(pageCount, data, tryCount[0], currentPackageCount[0], onLargeDataSendStateChangedListener);
                        tryCount[0]++;
                        characteristicWriteSuccess[0] = true;
                        DebugUtil.warnOut(TAG, "writeData failed");
                        sleepTime(RETRY_DELAY_TIME);
                        continue;
                    }
                    characteristicWriteSuccess[0] = false;
                }
                removeOnBleCharacteristicWriteListener(onBleCharacteristicWriteListener);
            }
        };
        BleManager.getThreadFactory().newThread(runnable).start();
    }

    private void performLargeDataWriteWithNotificationSendStateChangedListener(@Nullable final byte[] values,
                                                                               @Nullable final OnLargeDataWriteWithNotificationSendStateChangedListener onLargeDataWriteWithNotificationSendStateChangedListener,
                                                                               @NonNull final int[] writeLargeDataWithNotificationCurrentPackageCount,
                                                                               final int writeLargeDataWithNotificationPackageCount,
                                                                               @NonNull final byte[] largeData,
                                                                               final boolean autoFormat,
                                                                               @NonNull final int[] wrongNotificationResultCount,
                                                                               final int maxTryCount,
                                                                               @NonNull final boolean[] receivedNotification) {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (onLargeDataWriteWithNotificationSendStateChangedListener != null) {
                    boolean result;
                    result = onLargeDataWriteWithNotificationSendStateChangedListener.onReceiveNotification(values, writeLargeDataWithNotificationCurrentPackageCount[0] + 1, writeLargeDataWithNotificationPackageCount, getCurrentPackageData(writeLargeDataWithNotificationCurrentPackageCount[0], largeData, writeLargeDataWithNotificationPackageCount, autoFormat));
                    DebugUtil.warnOut(TAG, "onLargeDataWriteWithNotificationSendStateChangedListener onReceiveNotification result = " + result);
                    if (!result) {
                        if (wrongNotificationResultCount[0] >= maxTryCount) {
                            performLargeDataWriteWithNotificationSendFailedWithWrongNotifyDataListener(onLargeDataWriteWithNotificationSendStateChangedListener);
                            writeLargeDataWithNotificationContinueFlag = false;
                        } else {
                            wrongNotificationResultCount[0]++;
                            performLargeDataWriteWithNotificationSendFailedWithWrongNotifyDataAndRetryListener(onLargeDataWriteWithNotificationSendStateChangedListener, wrongNotificationResultCount[0], writeLargeDataWithNotificationCurrentPackageCount[0] + 1, writeLargeDataWithNotificationPackageCount, getCurrentPackageData(writeLargeDataWithNotificationCurrentPackageCount[0], largeData, writeLargeDataWithNotificationPackageCount, autoFormat));
                        }
                    } else {
                        wrongNotificationResultCount[0] = 0;
                    }
                }
                receivedNotification[0] = true;
            }
        });
    }

    private void performLargeDataWriteWithNotificationSendStartListener(@Nullable final OnLargeDataWriteWithNotificationSendStateChangedListener onLargeDataWriteWithNotificationSendStateChangedListener) {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (onLargeDataWriteWithNotificationSendStateChangedListener != null) {
                    onLargeDataWriteWithNotificationSendStateChangedListener.onDataSendStart();
                }
            }
        });
    }

    private void performLargeDataWriteWithNotificationSendTimeOutAndRetry(final int packageCount,
                                                                          @NonNull final byte[] data,
                                                                          final int tryCount,
                                                                          final int currentPackageIndex,
                                                                          @Nullable final OnLargeDataWriteWithNotificationSendStateChangedListener onLargeDataWriteWithNotificationSendStateChangedListener) {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (onLargeDataWriteWithNotificationSendStateChangedListener != null) {
                    onLargeDataWriteWithNotificationSendStateChangedListener.onDataSendTimeOutAndRetry(data, tryCount, currentPackageIndex, packageCount);
                }
            }
        });
    }

    private void performLargeDataWriteWithNotificationSendTimeOut(@Nullable final OnLargeDataWriteWithNotificationSendStateChangedListener onLargeDataWriteWithNotificationSendStateChangedListener,
                                                                  final int currentPackageIndex,
                                                                  final int packageCount,
                                                                  @NonNull final byte[] data) {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (onLargeDataWriteWithNotificationSendStateChangedListener != null) {
                    onLargeDataWriteWithNotificationSendStateChangedListener.onDataSendTimeOut(currentPackageIndex, packageCount, data);
                }
            }
        });
    }

    private void performLargeDataWriteSendTimeOutAndRetry(final int pageCount, @NonNull final byte[] data,
                                                          final int tryCount, final int currentPackageIndex,
                                                          @Nullable final OnLargeDataSendStateChangedListener onLargeDataSendStateChangedListener) {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (onLargeDataSendStateChangedListener != null) {
                    onLargeDataSendStateChangedListener.onSendTimeOutAndRetry(tryCount, currentPackageIndex, pageCount, data);
                }
            }
        });
    }

    private void performLargeDataWriteSendTimeOut(@Nullable final OnLargeDataSendStateChangedListener onLargeDataSendStateChangedListener,
                                                  final int pageCount, final int currentPackageIndex,
                                                  @NonNull final byte[] data) {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (onLargeDataSendStateChangedListener != null) {
                    onLargeDataSendStateChangedListener.onSendTimeOut(currentPackageIndex, pageCount, data);
                }
            }
        });
    }

    private void performLargeDataSendProgressChangedListener(final int pageCount, @NonNull final byte[] data,
                                                             final int currentPackageCount,
                                                             @Nullable final OnLargeDataSendStateChangedListener onLargeDataSendStateChangedListener) {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (onLargeDataSendStateChangedListener != null) {
                    onLargeDataSendStateChangedListener.packageSendProgressChanged(currentPackageCount + 1, pageCount, data);
                }
            }
        });
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isInitialized() {
        return bluetoothLeService != null;
    }

    private void performLargeDataSendFailedAndRetryListener(final int pageCount, @NonNull final byte[] data,
                                                            final int tryCount, final int currentPackageCount,
                                                            @Nullable final OnLargeDataSendStateChangedListener onLargeDataSendStateChangedListener) {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (onLargeDataSendStateChangedListener != null) {
                    onLargeDataSendStateChangedListener.packageSendFailedAndRetry(currentPackageCount + 1, pageCount, tryCount, data);
                }
            }
        });
    }

    private void performLargeDataSendFailedListener(final int pageCount, @NonNull final byte[] data,
                                                    final int currentPackageCount,
                                                    @Nullable final OnLargeDataSendStateChangedListener onLargeDataSendStateChangedListener) {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (onLargeDataSendStateChangedListener != null) {
                    onLargeDataSendStateChangedListener.packageSendFailed(currentPackageCount + 1, pageCount, data);
                }
            }
        });
    }

    private void performLargeDataSendFinishedListener(@Nullable final OnLargeDataSendStateChangedListener onLargeDataSendStateChangedListener) {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (onLargeDataSendStateChangedListener != null) {
                    onLargeDataSendStateChangedListener.sendFinished();
                }
            }
        });
    }

    private void performLargeDataSendStartedListener(@Nullable final OnLargeDataSendStateChangedListener onLargeDataSendStateChangedListener) {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (onLargeDataSendStateChangedListener != null) {
                    onLargeDataSendStateChangedListener.sendStarted();
                }
            }
        });
    }

    private void performLargeDataWriteWithNotificationSendFinishedListener(@Nullable final OnLargeDataWriteWithNotificationSendStateChangedListener onLargeDataWriteWithNotificationSendStateChangedListener) {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (onLargeDataWriteWithNotificationSendStateChangedListener != null) {
                    onLargeDataWriteWithNotificationSendStateChangedListener.onDataSendFinished();
                }
            }
        });
    }

    private void performLargeDataWriteWithNotificationSendFailedListener(final int pageCount,
                                                                         @NonNull final byte[] data,
                                                                         final int currentPackageCount,
                                                                         @Nullable final OnLargeDataWriteWithNotificationSendStateChangedListener onLargeDataWriteWithNotificationSendStateChangedListener) {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (onLargeDataWriteWithNotificationSendStateChangedListener != null) {
                    onLargeDataWriteWithNotificationSendStateChangedListener.onDataSendFailed(currentPackageCount, pageCount, data);
                }
            }
        });
    }

    private void performLargeDataWriteWithNotificationSendFailedAndRetryListener(final int pageCount,
                                                                                 @NonNull final byte[] data,
                                                                                 final int tryCount,
                                                                                 final int currentPackageCount,
                                                                                 @Nullable final OnLargeDataWriteWithNotificationSendStateChangedListener onLargeDataWriteWithNotificationSendStateChangedListener) {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (onLargeDataWriteWithNotificationSendStateChangedListener != null) {
                    onLargeDataWriteWithNotificationSendStateChangedListener.onDataSendFailedAndRetry(currentPackageCount, pageCount, data, tryCount);
                }
            }
        });
    }

    private void performLargeDataWriteWithNotificationSendProgressChangedListener(final int pageCount,
                                                                                  @NonNull final byte[] data,
                                                                                  final int currentPackageCount,
                                                                                  @Nullable final OnLargeDataWriteWithNotificationSendStateChangedListener onLargeDataWriteWithNotificationSendStateChangedListener) {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (onLargeDataWriteWithNotificationSendStateChangedListener != null) {
                    onLargeDataWriteWithNotificationSendStateChangedListener.onDataSendProgressChanged(currentPackageCount + 1, pageCount, data);
                }
            }
        });
    }

    private void performLargeDataWriteWithNotificationSendFailedWithWrongNotifyDataListener(@Nullable final OnLargeDataWriteWithNotificationSendStateChangedListener onLargeDataWriteWithNotificationSendStateChangedListener) {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (onLargeDataWriteWithNotificationSendStateChangedListener != null) {
                    onLargeDataWriteWithNotificationSendStateChangedListener.onSendFailedWithWrongNotifyData();
                }
            }
        });
    }

    private void performLargeDataWriteWithNotificationSendFailedWithWrongNotifyDataAndRetryListener(@Nullable final OnLargeDataWriteWithNotificationSendStateChangedListener onLargeDataWriteWithNotificationSendStateChangedListener,
                                                                                                    final int tryCount,
                                                                                                    final int currentPackageIndex,
                                                                                                    final int packageCount,
                                                                                                    @Nullable final byte[] data) {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (onLargeDataWriteWithNotificationSendStateChangedListener != null) {
                    onLargeDataWriteWithNotificationSendStateChangedListener.onSendFailedWithWrongNotifyDataAndRetry(tryCount, currentPackageIndex, packageCount, data);
                }
            }
        });
    }

    private void performLargeDataSendWithNotificationStartFailedListener(@Nullable final OnLargeDataWriteWithNotificationSendStateChangedListener onLargeDataWriteWithNotificationSendStateChangedListener) {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (onLargeDataWriteWithNotificationSendStateChangedListener != null) {
                    onLargeDataWriteWithNotificationSendStateChangedListener.onStartFailed();
                }
            }
        });
    }

    private void performLargeDataSendStartFailedListener(@Nullable final OnLargeDataSendStateChangedListener onLargeDataSendStateChangedListener) {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (onLargeDataSendStateChangedListener != null) {
                    onLargeDataSendStateChangedListener.onStartFailed();
                }
            }
        });
    }

    public void setSendLargeDataPackageDelayTime(int i) {

    }
}
