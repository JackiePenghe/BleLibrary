package com.sscl.blelibrary;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.sscl.blelibrary.interfaces.OnBleCharacteristicWriteListener;
import com.sscl.blelibrary.interfaces.OnBleConnectStateChangedListener;
import com.sscl.blelibrary.interfaces.OnBleDescriptorWriteListener;
import com.sscl.blelibrary.interfaces.OnBleReceiveNotificationListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * BLE Gatt callback
 *
 * @author jackie
 */

final class BleBluetoothGattCallback extends BluetoothGattCallback {

    /*-----------------------------------static constant-----------------------------------*/

    /**
     * TAG
     */
    private static final String TAG = BleBluetoothGattCallback.class.getSimpleName();

    /*-----------------------------------field variables-----------------------------------*/

    /**
     * callback list triggered when gatt descriptor write successful
     */
    private ArrayList<OnBleDescriptorWriteListener> onBleDescriptorWriteListeners = new ArrayList<>();
    /**
     * callback list triggered when gatt received notification data
     */
    private ArrayList<OnBleReceiveNotificationListener> onBleReceiveNotificationListeners = new ArrayList<>();
    /**
     * callback list triggered when gatt characteristic write data successful
     */
    private ArrayList<OnBleCharacteristicWriteListener> onBleCharacteristicWriteListeners = new ArrayList<>();
    /**
     * BluetoothGatt client
     */
    @Nullable
    private BluetoothGatt gatt;
    /**
     * BLE device connect status
     */
    private boolean connected;
    /**
     * BLE device uuid discover status
     */
    private boolean serviceDiscovered;

    /**
     * connect status changed listener
     */
    @Nullable
    private OnBleConnectStateChangedListener onBleConnectStateChangedListener;


    /*-----------------------------------override method-----------------------------------*/

    /**
     * Callback indicating when GATT client has connected/disconnected to/from a remote
     * GATT server.
     *
     * @param gatt     GATT client
     * @param status   Status of the connect or disconnect operation. {@link
     *                 BluetoothGatt#GATT_SUCCESS} if the operation succeeds.
     * @param newState Returns the new connection state. Can be one of {@link
     *                 BluetoothProfile#STATE_DISCONNECTED} or {@link BluetoothProfile#STATE_CONNECTED}
     */
    @Override
    public void onConnectionStateChange(final BluetoothGatt gatt, int status, int newState) {
        this.gatt = gatt;
        //Judge the current state
        switch (newState) {
            //disconnected
            case BluetoothGatt.STATE_DISCONNECTED:
                DebugUtil.warnOut(TAG, "status = " + status);
                if (status == BluetoothGatt.STATE_CONNECTED || status == BluetoothGatt.STATE_CONNECTING || status == BluetoothGatt.STATE_DISCONNECTED || status == BluetoothGatt.STATE_DISCONNECTING) {
                    DebugUtil.warnOut(TAG, "STATE_DISCONNECTED");
                    performDeviceDisconnectedListener();
                    connected = false;
                    serviceDiscovered = false;
                } else {
                    performGattStatusErrorListener(status);
                }
                break;
            //connecting
            case BluetoothGatt.STATE_CONNECTING:
                DebugUtil.warnOut(TAG, "STATE_CONNECTING");
                performDeviceConnectingListener();
                break;
            //connected
            case BluetoothGatt.STATE_CONNECTED:
                connected = true;
                DebugUtil.warnOut(TAG, "STATE_CONNECTED");
                performDeviceConnectedListener();
                if (!gatt.discoverServices()) {
                    DebugUtil.warnOut(TAG, "gatt.discoverServices() return false");
                    performAutoDiscoverServicesFailedListener();
                }
                break;
            //disconnecting
            case BluetoothGatt.STATE_DISCONNECTING:
                DebugUtil.warnOut(TAG, "STATE_DISCONNECTING");
                performDeviceDisconnectingListener();
                break;
            //others
            default:
                DebugUtil.warnOut(TAG, "other state:" + newState);
                performGattUnknownStatusListener(newState);
                break;
        }
    }

    /**
     * Callback invoked when the list of remote services, characteristics and descriptors
     * for the remote device have been updated, ie new services have been discovered.
     *
     * @param gatt   GATT client invoked {@link BluetoothGatt#discoverServices}
     * @param status {@link BluetoothGatt#GATT_SUCCESS} if the remote device has been explored
     *               successfully.
     */
    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        DebugUtil.warnOut(TAG, "onServicesDiscovered");
        if (BluetoothGatt.GATT_SUCCESS != status) {
            performGattPerformTaskFailedListener(status,"onServicesDiscovered");
        } else {
            serviceDiscovered = true;
            performDeviceServicesDiscoveredListener();
        }
    }

    /**
     * Callback reporting the result of a characteristic read operation.
     *
     * @param gatt           GATT client invoked {@link BluetoothGatt#readCharacteristic}
     * @param characteristic Characteristic that was read from the associated remote device.
     * @param status         {@link BluetoothGatt#GATT_SUCCESS} if the read operation was completed
     *                       successfully.
     */
    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        DebugUtil.warnOut(TAG, "onCharacteristicRead");
        if (BluetoothGatt.GATT_SUCCESS != status) {
            performGattPerformTaskFailedListener(status, "onCharacteristicRead");
        } else {
            byte[] value = characteristic.getValue();
            performGattReadCharacteristicDataListener(characteristic, value);
        }
    }

    /**
     * Callback indicating the result of a characteristic write operation.
     *
     * <p>If this callback is invoked while a reliable write transaction is
     * in progress, the value of the characteristic represents the value
     * reported by the remote device. An application should compare this
     * value to the desired value to be written. If the values don't match,
     * the application must abort the reliable write transaction.
     *
     * @param gatt           GATT client invoked {@link BluetoothGatt#writeCharacteristic}
     * @param characteristic Characteristic that was written to the associated remote device.
     * @param status         The result of the write operation {@link BluetoothGatt#GATT_SUCCESS} if the
     *                       operation succeeds.
     */
    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        DebugUtil.warnOut(TAG, "onCharacteristicWrite");
        if (BluetoothGatt.GATT_SUCCESS != status) {
            performGattPerformTaskFailedListener(status, "onCharacteristicWrite");
        } else {
            byte[] value = characteristic.getValue();
            performGattWriteCharacteristicDataListener(characteristic, value);
        }
    }

    /**
     * Callback triggered as a result of a remote characteristic notification.
     *
     * @param gatt           GATT client the characteristic is associated with
     * @param characteristic Characteristic that has been updated as a result of a remote
     *                       notification event.
     */
    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        DebugUtil.warnOut(TAG, "onReceivedNotification");
        byte[] value = characteristic.getValue();
        performReceivedNotificationListener(characteristic, value);
    }

    /**
     * Callback reporting the result of a descriptor read operation.
     *
     * @param gatt       GATT client invoked {@link BluetoothGatt#readDescriptor}
     * @param descriptor Descriptor that was read from the associated remote device.
     * @param status     {@link BluetoothGatt#GATT_SUCCESS} if the read operation was completed
     *                   successfully
     */
    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        DebugUtil.warnOut(TAG, "onDescriptorRead");
        if (BluetoothGatt.GATT_SUCCESS != status) {
            performGattPerformTaskFailedListener(status, "onDescriptorRead");
        } else {
            byte[] value = descriptor.getValue();
            performGattReadDescriptorListener(descriptor, value);
        }
    }

    /**
     * Callback indicating the result of a descriptor write operation.
     *
     * @param gatt       GATT client invoked {@link BluetoothGatt#writeDescriptor}
     * @param descriptor Descriptor that was writte to the associated remote device.
     * @param status     The result of the write operation {@link BluetoothGatt#GATT_SUCCESS} if the
     *                   operation succeeds.
     */
    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        DebugUtil.warnOut(TAG, "onDescriptorWrite");
        if (BluetoothGatt.GATT_SUCCESS != status) {
            performGattPerformTaskFailedListener(status, "onDescriptorWrite");
        } else {
            byte[] value = descriptor.getValue();
            performGattWriteDescriptorListener(descriptor, value);
        }

    }

    /**
     * Callback invoked when a reliable write transaction has been completed.
     *
     * @param gatt   GATT client invoked {@link BluetoothGatt#executeReliableWrite}
     * @param status {@link BluetoothGatt#GATT_SUCCESS} if the reliable write transaction was
     *               executed successfully
     */
    @Override
    public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
        DebugUtil.warnOut(TAG, "onReliableWriteCompleted");
        if (BluetoothGatt.GATT_SUCCESS != status) {
            performGattPerformTaskFailedListener(status, "onReliableWriteCompleted");
        } else {
            performGattReliableWriteCompletedListener();
        }
    }

    /**
     * Callback reporting the RSSI for a remote device connection.
     * <p>
     * This callback is triggered in response to the
     * {@link BluetoothGatt#readRemoteRssi} function.
     *
     * @param gatt   GATT client invoked {@link BluetoothGatt#readRemoteRssi}
     * @param rssi   The RSSI value for the remote device
     * @param status {@link BluetoothGatt#GATT_SUCCESS} if the RSSI was read successfully
     */
    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        DebugUtil.warnOut(TAG, "onReadRemoteRssi");
        if (BluetoothGatt.GATT_SUCCESS != status) {
            performGattPerformTaskFailedListener(status, "onReadRemoteRssi");
        } else {
            performGattReadRemoteRssiListener(rssi);
        }
    }

    /**
     * Callback indicating the MTU for a given device connection has changed.
     * <p>
     * This callback is triggered in response to the
     * {@link BluetoothGatt#requestMtu} function, or in response to a connection
     * event.
     *
     * @param gatt   GATT client invoked {@link BluetoothGatt#requestMtu}
     * @param mtu    The new MTU size
     * @param status {@link BluetoothGatt#GATT_SUCCESS} if the MTU has been changed successfully
     */
    @Override
    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
        DebugUtil.warnOut(TAG, "onMtuChanged");
        if (BluetoothGatt.GATT_SUCCESS != status) {
            performGattPerformTaskFailedListener(status, "onMtuChanged");
        } else {
            performGattMtuChangedListener(mtu);
        }
    }

    /**
     * Callback triggered as result of {@link BluetoothGatt#setPreferredPhy}, or as a result of
     * remote device changing the PHY.
     *
     * @param gatt   GATT client
     * @param txPhy  the transmitter PHY in use. One of {@link BluetoothDevice#PHY_LE_1M}, {@link
     *               BluetoothDevice#PHY_LE_2M}, and {@link BluetoothDevice#PHY_LE_CODED}.
     * @param rxPhy  the receiver PHY in use. One of {@link BluetoothDevice#PHY_LE_1M}, {@link
     *               BluetoothDevice#PHY_LE_2M}, and {@link BluetoothDevice#PHY_LE_CODED}.
     * @param status Status of the PHY update operation. {@link BluetoothGatt#GATT_SUCCESS} if the
     */
    @Override
    public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
        super.onPhyUpdate(gatt, txPhy, rxPhy, status);
        if (BluetoothGatt.GATT_SUCCESS != status) {
            performGattPerformTaskFailedListener(status, "onPhyUpdate");
        } else {
            performGattPhyUpdateListener(txPhy, rxPhy);
        }
    }

    /**
     * Callback triggered as result of {@link BluetoothGatt#readPhy}
     *
     * @param gatt   GATT client
     * @param txPhy  the transmitter PHY in use. One of {@link BluetoothDevice#PHY_LE_1M}, {@link
     *               BluetoothDevice#PHY_LE_2M}, and {@link BluetoothDevice#PHY_LE_CODED}.
     * @param rxPhy  the receiver PHY in use. One of {@link BluetoothDevice#PHY_LE_1M}, {@link
     *               BluetoothDevice#PHY_LE_2M}, and {@link BluetoothDevice#PHY_LE_CODED}.
     * @param status Status of the PHY read operation. {@link BluetoothGatt#GATT_SUCCESS} if the
     */
    @Override
    public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
        super.onPhyRead(gatt, txPhy, rxPhy, status);
        if (BluetoothGatt.GATT_SUCCESS != status) {
            performGattPerformTaskFailedListener(status, "onPhyRead");
        } else {
            performGattReadPhyListener(txPhy, rxPhy);
        }
    }

    /*-----------------------------------setter-----------------------------------*/

    /**
     * set BLE device connect status changed listener
     *
     * @param onBleConnectStateChangedListener BLE device connect status changed listener
     */
    void setOnBleConnectStateChangedListener(@Nullable OnBleConnectStateChangedListener onBleConnectStateChangedListener) {
        this.onBleConnectStateChangedListener = onBleConnectStateChangedListener;
    }

    /*-----------------------------------package private method-----------------------------------*/

    /**
     * get remote device service list
     *
     * @return service uuid list
     */
    @Nullable
    List<BluetoothGattService> getServices() {
        if (gatt == null) {
            return null;
        }
        return gatt.getServices();
    }

    /**
     * get remote device service by uuid
     *
     * @param uuid UUID
     * @return BluetoothGattService
     */
    @SuppressWarnings("unused")
    @Nullable
    BluetoothGattService getService(@NonNull UUID uuid) {
        if (gatt == null) {
            return null;
        }
        return gatt.getService(uuid);
    }

    /**
     * get connection status
     *
     * @return connection status
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * get service discover status
     *
     * @return service discover status
     */
    @SuppressWarnings("WeakerAccess")
    public boolean isServiceDiscovered() {
        return serviceDiscovered;
    }

    /**
     * add a callback triggered when descriptor write successful
     *
     * @param onDescriptorWriteListener callback triggered when descriptor write successful
     * @return true means successful
     */
    @SuppressWarnings("WeakerAccess")
    public boolean addOnBleDescriptorWriteListener(@NonNull OnBleDescriptorWriteListener onDescriptorWriteListener) {
        if (onBleDescriptorWriteListeners == null) {
            return false;
        }
        onBleDescriptorWriteListeners.add(onDescriptorWriteListener);
        return true;
    }

    /**
     * remove a callback triggered when descriptor write successful
     *
     * @param onBleDescriptorWriteListener callback triggered when descriptor write successful
     * @return true means successful
     */
    @SuppressWarnings("WeakerAccess")
    public boolean removeOnBleDescriptorWriteListener(@NonNull OnBleDescriptorWriteListener onBleDescriptorWriteListener) {
        if (onBleDescriptorWriteListeners == null) {
            return false;
        }
        return onBleDescriptorWriteListeners.remove(onBleDescriptorWriteListener);
    }

    /**
     * add a callback triggered when received notification data
     *
     * @param onBleReceiveNotificationListener callback triggered when descriptor write successful
     * @return true means successful
     */
    @SuppressWarnings("WeakerAccess")
    public boolean addOnBleReceiveNotificationListener(@NonNull OnBleReceiveNotificationListener onBleReceiveNotificationListener) {
        if (onBleReceiveNotificationListeners == null) {
            return false;
        }
        onBleReceiveNotificationListeners.add(onBleReceiveNotificationListener);
        return true;
    }

    /**
     * remove a callback triggered when received notification data
     *
     * @param onBleReceiveNotificationListener callback triggered when received notification data
     * @return true means successful
     */
    @SuppressWarnings("WeakerAccess")
    public boolean removeOnBleReceiveNotificationListener(@NonNull OnBleReceiveNotificationListener onBleReceiveNotificationListener) {
        if (onBleReceiveNotificationListeners == null) {
            return false;
        }
        return onBleReceiveNotificationListeners.remove(onBleReceiveNotificationListener);
    }

    /**
     * add a callback triggered when gatt characteristic write data successful
     *
     * @param onBleCharacteristicWriteListener callback triggered when gatt characteristic write data successful
     * @return true means successful
     */
    @SuppressWarnings("WeakerAccess")
    public boolean addOnBleCharacteristicWriteListener(@NonNull OnBleCharacteristicWriteListener onBleCharacteristicWriteListener) {
        if (onBleCharacteristicWriteListeners == null) {
            return false;
        }
        onBleCharacteristicWriteListeners.add(onBleCharacteristicWriteListener);
        return true;
    }

    /**
     * remove a callback triggered when gatt characteristic write data successful
     *
     * @param onBleCharacteristicWriteListener callback triggered when gatt characteristic write data successful
     * @return true means successful
     */
    @SuppressWarnings("WeakerAccess")
    public boolean removeOnBleCharacteristicWriteListener(@NonNull OnBleCharacteristicWriteListener onBleCharacteristicWriteListener) {
        if (onBleCharacteristicWriteListeners == null) {
            return false;
        }
        return onBleCharacteristicWriteListeners.remove(onBleCharacteristicWriteListener);
    }

    /*-----------------------------------private method-----------------------------------*/

    private void performDeviceDisconnectedListener() {
        BleManager.getHANDLER().post(new Runnable() {
            @Override
            public void run() {
                if (onBleConnectStateChangedListener != null) {
                    onBleConnectStateChangedListener.disconnected();
                }
            }
        });
    }

    private void performGattStatusErrorListener(final int status) {
        BleManager.getHANDLER().post(new Runnable() {
            @Override
            public void run() {
                if (onBleConnectStateChangedListener != null) {
                    onBleConnectStateChangedListener.gattStatusError(status);
                }
            }
        });
    }

    private void performDeviceConnectingListener() {
        BleManager.getHANDLER().post(new Runnable() {
            @Override
            public void run() {
                if (onBleConnectStateChangedListener != null) {
                    onBleConnectStateChangedListener.connecting();
                }
            }
        });
    }

    private void performDeviceConnectedListener() {
        BleManager.getHANDLER().post(new Runnable() {
            @Override
            public void run() {
                if (onBleConnectStateChangedListener != null) {
                    onBleConnectStateChangedListener.connected();
                }
            }
        });
    }

    private void performAutoDiscoverServicesFailedListener() {
        BleManager.getHANDLER().post(new Runnable() {
            @Override
            public void run() {
                if (onBleConnectStateChangedListener != null) {
                    onBleConnectStateChangedListener.autoDiscoverServicesFailed();
                }
            }
        });
    }

    private void performDeviceDisconnectingListener() {
        BleManager.getHANDLER().post(new Runnable() {
            @Override
            public void run() {
                if (onBleConnectStateChangedListener != null) {
                    onBleConnectStateChangedListener.disconnecting();
                }
            }
        });
    }

    private void performGattUnknownStatusListener(final int newState) {
        BleManager.getHANDLER().post(new Runnable() {
            @Override
            public void run() {
                if (onBleConnectStateChangedListener != null) {
                    onBleConnectStateChangedListener.unknownStatus(newState);
                }
            }
        });
    }

    private void performGattPerformTaskFailedListener(final int status, final String methodName) {
        BleManager.getHANDLER().post(new Runnable() {
            @Override
            public void run() {
                if (onBleConnectStateChangedListener != null) {
                    onBleConnectStateChangedListener.gattPerformTaskFailed(status,methodName);
                }
            }
        });
    }

    private void performDeviceServicesDiscoveredListener() {
        BleManager.getHANDLER().post(new Runnable() {
            @Override
            public void run() {
                if (onBleConnectStateChangedListener != null) {
                    onBleConnectStateChangedListener.servicesDiscovered();
                }
            }
        });
    }

    private void performGattReadCharacteristicDataListener(final BluetoothGattCharacteristic characteristic, final byte[] value) {
        BleManager.getHANDLER().post(new Runnable() {
            @Override
            public void run() {
                if (onBleConnectStateChangedListener != null) {
                    onBleConnectStateChangedListener.readCharacteristicData(characteristic, value);
                }
            }
        });
    }

    private void performGattWriteCharacteristicDataListener(final BluetoothGattCharacteristic characteristic, final byte[] value) {
        BleManager.getHANDLER().post(new Runnable() {
            @Override
            public void run() {
                if (onBleConnectStateChangedListener != null) {
                    onBleConnectStateChangedListener.writeCharacteristicData(characteristic, value);
                }

                for (int i = 0; i < onBleCharacteristicWriteListeners.size(); i++) {
                    OnBleCharacteristicWriteListener onBleCharacteristicWriteListener = onBleCharacteristicWriteListeners.get(i);
                    if (onBleCharacteristicWriteListener != null) {
                        onBleCharacteristicWriteListener.onBleCharacteristicWrite(characteristic, value);
                    }
                }
            }
        });
    }

    private void performReceivedNotificationListener(final BluetoothGattCharacteristic characteristic, final byte[] value) {
        BleManager.getHANDLER().post(new Runnable() {
            @Override
            public void run() {
                if (onBleConnectStateChangedListener != null) {
                    onBleConnectStateChangedListener.receivedNotification(characteristic, value);
                }
                for (int i = 0; i < onBleReceiveNotificationListeners.size(); i++) {
                    OnBleReceiveNotificationListener onBleReceiveNotificationListener = onBleReceiveNotificationListeners.get(i);
                    if (onBleReceiveNotificationListener != null) {
                        onBleReceiveNotificationListener.onBleReceiveNotification(characteristic, value);
                    }
                }
            }
        });
    }

    private void performGattReadDescriptorListener(final BluetoothGattDescriptor descriptor, final byte[] value) {
        BleManager.getHANDLER().post(new Runnable() {
            @Override
            public void run() {
                if (onBleConnectStateChangedListener != null) {
                    onBleConnectStateChangedListener.readDescriptor(descriptor, value);
                }
            }
        });
    }

    private void performGattWriteDescriptorListener(final BluetoothGattDescriptor descriptor, final byte[] value) {
        BleManager.getHANDLER().post(new Runnable() {
            @Override
            public void run() {
                if (onBleConnectStateChangedListener != null) {
                    onBleConnectStateChangedListener.writeDescriptor(descriptor, value);
                }
                for (int i = 0; i < onBleDescriptorWriteListeners.size(); i++) {
                    OnBleDescriptorWriteListener onBleDescriptorWriteListener = onBleDescriptorWriteListeners.get(i);
                    if (onBleDescriptorWriteListener != null) {
                        onBleDescriptorWriteListener.onBleDescriptorWrite(descriptor, value);
                    }
                }
            }
        });
    }

    private void performGattReliableWriteCompletedListener() {
        BleManager.getHANDLER().post(new Runnable() {
            @Override
            public void run() {
                if (onBleConnectStateChangedListener != null) {
                    onBleConnectStateChangedListener.reliableWriteCompleted();
                }
            }
        });
    }

    private void performGattReadRemoteRssiListener(final int rssi) {
        BleManager.getHANDLER().post(new Runnable() {
            @Override
            public void run() {
                if (onBleConnectStateChangedListener != null) {
                    onBleConnectStateChangedListener.readRemoteRssi(rssi);
                }
            }
        });
    }

    private void performGattMtuChangedListener(final int mtu) {
        BleManager.getHANDLER().post(new Runnable() {
            @Override
            public void run() {
                if (onBleConnectStateChangedListener != null) {
                    onBleConnectStateChangedListener.mtuChanged(mtu);
                }
            }
        });
    }

    private void performGattPhyUpdateListener(final int txPhy, final int rxPhy) {
        BleManager.getHANDLER().post(new Runnable() {
            @Override
            public void run() {
                if (onBleConnectStateChangedListener != null) {
                    onBleConnectStateChangedListener.phyUpdate(txPhy, rxPhy);
                }
            }
        });
    }

    private void performGattReadPhyListener(final int txPhy, final int rxPhy) {
        BleManager.getHANDLER().post(new Runnable() {
            @Override
            public void run() {
                if (onBleConnectStateChangedListener != null) {
                    onBleConnectStateChangedListener.readPhy(txPhy, rxPhy);
                }
            }
        });
    }
}
