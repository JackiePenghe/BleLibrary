package com.sscl.blelibrary;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;

import java.util.HashMap;

/**
 * BLE Gatt callback for multiple connections
 *
 * @author jacke
 */

public final class BleBluetoothMultiGattCallback extends BluetoothGattCallback {

    /*-----------------------------------static constant-----------------------------------*/

    private static final String TAG = BleBluetoothMultiGattCallback.class.getSimpleName();

    /*-----------------------------------field variables-----------------------------------*/

    HashMap<String, BaseBleConnectCallback> callbackHashMap = new HashMap<>();

    /*-----------------------------------override method-----------------------------------*/

    /**
     * Callback triggered as result of {@link BluetoothGatt#setPreferredPhy}, or as a result of
     * remote device changing the PHY.
     *
     * @param gatt   GATT client
     * @param txPhy  the transmitter PHY in use. One of {@link BluetoothDevice#PHY_LE_1M},
     *               {@link BluetoothDevice#PHY_LE_2M}, and {@link BluetoothDevice#PHY_LE_CODED}.
     * @param rxPhy  the receiver PHY in use. One of {@link BluetoothDevice#PHY_LE_1M},
     *               {@link BluetoothDevice#PHY_LE_2M}, and {@link BluetoothDevice#PHY_LE_CODED}.
     * @param status Status of the PHY update operation.
     *               {@link BluetoothGatt#GATT_SUCCESS} if the operation succeeds.
     */
    @Override
    public void onPhyUpdate(final BluetoothGatt gatt, final int txPhy, final int rxPhy, final int status) {
        String gattAddress = gatt.getDevice().getAddress();
        BaseBleConnectCallback baseBleConnectCallback = callbackHashMap.get(gattAddress);
        triggerDevicePhyUpdateCallback(gatt, txPhy, rxPhy, status, baseBleConnectCallback);
    }


    /**
     * Callback triggered as result of {@link BluetoothGatt#readPhy}
     *
     * @param gatt   GATT client
     * @param txPhy  the transmitter PHY in use. One of {@link BluetoothDevice#PHY_LE_1M},
     *               {@link BluetoothDevice#PHY_LE_2M}, and {@link BluetoothDevice#PHY_LE_CODED}.
     * @param rxPhy  the receiver PHY in use. One of {@link BluetoothDevice#PHY_LE_1M},
     *               {@link BluetoothDevice#PHY_LE_2M}, and {@link BluetoothDevice#PHY_LE_CODED}.
     * @param status Status of the PHY read operation.
     *               {@link BluetoothGatt#GATT_SUCCESS} if the operation succeeds.
     */
    @Override
    public void onPhyRead(final BluetoothGatt gatt, final int txPhy, final int rxPhy, final int status) {
        String gattAddress = gatt.getDevice().getAddress();
        BaseBleConnectCallback baseBleConnectCallback = callbackHashMap.get(gattAddress);
        triggerDevicePhyReadCallback(gatt, txPhy, rxPhy, status, baseBleConnectCallback);
    }

    /**
     * BaseBleConnectCallback indicating when GATT client has connected/disconnected to/from a remote
     * GATT server.
     *
     * @param gatt     GATT client
     * @param status   Status of the connect or disconnect operation.
     *                 {@link BluetoothGatt#GATT_SUCCESS} if the operation succeeds.
     * @param newState Returns the new connection state. Can be one of
     *                 {@link BluetoothProfile#STATE_DISCONNECTED} or
     *                 {@link BluetoothProfile#STATE_CONNECTED}
     */
    @Override
    public void onConnectionStateChange(final BluetoothGatt gatt, int status, int newState) {

        String gattAddress = gatt.getDevice().getAddress();
        BaseBleConnectCallback baseBleConnectCallback = callbackHashMap.get(gattAddress);
        switch (newState) {
            case BluetoothGatt.STATE_DISCONNECTED:
                triggerDeviceDisconnectedCallback(gatt, baseBleConnectCallback);
                break;
            case BluetoothGatt.STATE_CONNECTING:
                triggerDeviceConnectingCallback(gatt, baseBleConnectCallback);
                break;
            case BluetoothGatt.STATE_CONNECTED:
                triggerDeviceConnectedCallback(gatt, baseBleConnectCallback);
                if (!gatt.discoverServices()) {
                    triggerDeviceAutoDiscoverServiceFailedCallback(gatt, baseBleConnectCallback);
                }
                break;
            case BluetoothGatt.STATE_DISCONNECTING:
                triggerDeviceDisconnectingCallback(gatt, baseBleConnectCallback);
                break;
            default:
                triggerDeviceUnknownStateCallback(newState, gatt, baseBleConnectCallback);
                DebugUtil.warnOut(TAG, gatt.getDevice().getAddress() + "other state");
                break;
        }
    }

    /**
     * Callback invoked when the list of remote services, characteristics and descriptors
     * for the remote device have been updated, ie new services have been discovered.
     *
     * @param gatt   GATT client invoked {@link BluetoothGatt#discoverServices}
     * @param status {@link BluetoothGatt#GATT_SUCCESS} if the remote device
     */
    @Override
    public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
        String gattAddress = gatt.getDevice().getAddress();
        BaseBleConnectCallback baseBleConnectCallback = callbackHashMap.get(gattAddress);
        triggerDeviceServiceDiscoveredCallback(gatt, status, baseBleConnectCallback);
    }

    /**
     * Callback reporting the result of a characteristic read operation.
     *
     * @param gatt           GATT client invoked {@link BluetoothGatt#readCharacteristic}
     * @param characteristic Characteristic that was read from the associated
     *                       remote device.
     * @param status         {@link BluetoothGatt#GATT_SUCCESS} if the read operation
     */
    @Override
    public void onCharacteristicRead(final BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, final int status) {
        String gattAddress = gatt.getDevice().getAddress();
        BaseBleConnectCallback baseBleConnectCallback = callbackHashMap.get(gattAddress);
        byte[] values = characteristic.getValue();
        triggerDeviceCharacteristicReadCallback(gatt, characteristic, status, baseBleConnectCallback, values);
    }

    /**
     * Callback indicating the result of a characteristic write operation.
     * <p>
     * If this callback is invoked while a reliable write transaction is
     * in progress, the value of the characteristic represents the value
     * reported by the remote device. An application should compare this
     * value to the desired value to be written. If the values don't match,
     * the application must abort the reliable write transaction.
     *
     * @param gatt           GATT client invoked {@link BluetoothGatt#writeCharacteristic}
     * @param characteristic Characteristic that was written to the associated
     *                       remote device.
     * @param status         The result of the write operation
     *                       {@link BluetoothGatt#GATT_SUCCESS} if the operation succeeds.
     */
    @Override
    public void onCharacteristicWrite(final BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, final int status) {
        String gattAddress = gatt.getDevice().getAddress();
        byte[] values = characteristic.getValue();
        BaseBleConnectCallback baseBleConnectCallback = callbackHashMap.get(gattAddress);
        triggerDeviceCharacteristicWriteCallback(gatt, characteristic, status, values, baseBleConnectCallback);
    }

    /**
     * Callback triggered as a result of a remote characteristic notification.
     *
     * @param gatt           GATT client the characteristic is associated with
     * @param characteristic Characteristic that has been updated as a result
     */
    @Override
    public void onCharacteristicChanged(final BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        String gattAddress = gatt.getDevice().getAddress();
        BaseBleConnectCallback baseBleConnectCallback = callbackHashMap.get(gattAddress);
        byte[] values = characteristic.getValue();
        triggerDeviceCharacteristicChangedCallback(gatt, characteristic, values, baseBleConnectCallback);
    }

    /**
     * Callback reporting the result of a descriptor read operation.
     *
     * @param gatt       GATT client invoked {@link BluetoothGatt#readDescriptor}
     * @param descriptor Descriptor that was read from the associated
     *                   remote device.
     * @param status     {@link BluetoothGatt#GATT_SUCCESS} if the read operation
     */
    @Override
    public void onDescriptorRead(final BluetoothGatt gatt, BluetoothGattDescriptor descriptor, final int status) {
        String gattAddress = gatt.getDevice().getAddress();
        byte[] values = descriptor.getValue();
        BaseBleConnectCallback baseBleConnectCallback = callbackHashMap.get(gattAddress);
        triggerDeviceDescriptorReadCallback(gatt, descriptor, status, values, baseBleConnectCallback);
    }

    /**
     * Callback indicating the result of a descriptor write operation.
     *
     * @param gatt       GATT client invoked {@link BluetoothGatt#writeDescriptor}
     * @param descriptor Descriptor that was writte to the associated
     *                   remote device.
     * @param status     The result of the write operation
     *                   {@link BluetoothGatt#GATT_SUCCESS} if the operation succeeds.
     */
    @Override
    public void onDescriptorWrite(final BluetoothGatt gatt, BluetoothGattDescriptor descriptor, final int status) {
        String gattAddress = gatt.getDevice().getAddress();
        byte[] values = descriptor.getValue();
        BaseBleConnectCallback baseBleConnectCallback = callbackHashMap.get(gattAddress);
        triggerDeviceDescriptorWriteCallback(gatt, descriptor, status, values, baseBleConnectCallback);
    }

    /**
     * Callback invoked when a reliable write transaction has been completed.
     *
     * @param gatt   GATT client invoked {@link BluetoothGatt#executeReliableWrite}
     * @param status {@link BluetoothGatt#GATT_SUCCESS} if the reliable write
     */
    @Override
    public void onReliableWriteCompleted(final BluetoothGatt gatt, final int status) {
        String gattAddress = gatt.getDevice().getAddress();
        BaseBleConnectCallback baseBleConnectCallback = callbackHashMap.get(gattAddress);
        triggerDeviceReliableWriteCompletedCallback(gatt, status, baseBleConnectCallback);
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
    public void onReadRemoteRssi(final BluetoothGatt gatt, final int rssi, final int status) {
        String gattAddress = gatt.getDevice().getAddress();
        BaseBleConnectCallback baseBleConnectCallback = callbackHashMap.get(gattAddress);
        triggerDeviceReadRemoteRssiCallback(gatt, rssi, status, baseBleConnectCallback);
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
    public void onMtuChanged(final BluetoothGatt gatt, final int mtu, final int status) {
        String gattAddress = gatt.getDevice().getAddress();
        if (callbackHashMap.containsKey(gattAddress)) {
            final BaseBleConnectCallback baseBleConnectCallback = callbackHashMap.get(gattAddress);
            triggerDeviceMtuChangedCallback(gatt, mtu, status, baseBleConnectCallback);
        }
    }

    public void close() {
        callbackHashMap.clear();
        callbackHashMap = null;
    }

    /*-----------------------------------private method-----------------------------------*/

    private void triggerDevicePhyUpdateCallback(final BluetoothGatt gatt, final int txPhy, final int rxPhy, final int status, final BaseBleConnectCallback baseBleConnectCallback) {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (baseBleConnectCallback != null) {
                    if (BluetoothGatt.GATT_SUCCESS != status) {
                        baseBleConnectCallback.onBluetoothGattOptionsNotSuccess(gatt, "onPhyUpdate", status);
                    } else {
                        baseBleConnectCallback.onPhyUpdate(gatt, txPhy, rxPhy);
                    }
                }
            }
        });
    }

    private void triggerDevicePhyReadCallback(final BluetoothGatt gatt, final int txPhy, final int rxPhy, final int status, final BaseBleConnectCallback baseBleConnectCallback) {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (baseBleConnectCallback != null) {
                    if (BluetoothGatt.GATT_SUCCESS != status) {
                        baseBleConnectCallback.onBluetoothGattOptionsNotSuccess(gatt, "onPhyRead", status);
                    } else {
                        baseBleConnectCallback.onPhyRead(gatt, txPhy, rxPhy);
                    }
                }
            }
        });
    }

    private void triggerDeviceDisconnectedCallback(final BluetoothGatt gatt, final BaseBleConnectCallback baseBleConnectCallback) {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (baseBleConnectCallback != null) {
                    baseBleConnectCallback.setConnected(false);
                    baseBleConnectCallback.setServiceDiscovered(false);
                    baseBleConnectCallback.onDisConnected(gatt);
                }
            }
        });
    }

    private void triggerDeviceConnectingCallback(final BluetoothGatt gatt, final BaseBleConnectCallback baseBleConnectCallback) {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (baseBleConnectCallback != null) {
                    baseBleConnectCallback.onConnecting(gatt);
                }
            }
        });
    }

    private void triggerDeviceConnectedCallback(final BluetoothGatt gatt, final BaseBleConnectCallback baseBleConnectCallback) {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (baseBleConnectCallback != null) {
                    baseBleConnectCallback.setConnected(true);
                    baseBleConnectCallback.onConnected(gatt);
                }
            }
        });
    }

    private void triggerDeviceAutoDiscoverServiceFailedCallback(final BluetoothGatt gatt, final BaseBleConnectCallback baseBleConnectCallback) {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (baseBleConnectCallback != null) {
                    baseBleConnectCallback.onServicesAutoDiscoverFailed(gatt);
                }
            }
        });
    }

    private void triggerDeviceDisconnectingCallback(final BluetoothGatt gatt, final BaseBleConnectCallback baseBleConnectCallback) {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (baseBleConnectCallback != null) {
                    baseBleConnectCallback.onDisconnecting(gatt);
                }
            }
        });
    }

    private void triggerDeviceUnknownStateCallback(final int state, final BluetoothGatt gatt, final BaseBleConnectCallback baseBleConnectCallback) {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (baseBleConnectCallback != null) {
                    baseBleConnectCallback.onUnknownState(gatt, state);
                }
            }
        });
    }

    private void triggerDeviceServiceDiscoveredCallback(final BluetoothGatt gatt, final int status, final BaseBleConnectCallback baseBleConnectCallback) {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (baseBleConnectCallback != null) {
                    if (BluetoothGatt.GATT_SUCCESS != status) {
                        baseBleConnectCallback.onBluetoothGattOptionsNotSuccess(gatt, "onServicesDiscovered", status);
                    } else {
                        baseBleConnectCallback.setServiceDiscovered(true);
                        baseBleConnectCallback.onServicesDiscovered(gatt);
                    }
                }
            }
        });
    }

    private void triggerDeviceCharacteristicReadCallback(final BluetoothGatt gatt, final BluetoothGattCharacteristic gattCharacteristic, final int status, final BaseBleConnectCallback baseBleConnectCallback, final byte[] values) {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (baseBleConnectCallback != null) {
                    if (BluetoothGatt.GATT_SUCCESS != status) {
                        baseBleConnectCallback.onBluetoothGattOptionsNotSuccess(gatt, "onCharacteristicRead", status);
                    } else {
                        baseBleConnectCallback.onCharacteristicRead(gatt, gattCharacteristic, values);
                    }
                }
            }
        });
    }

    private void triggerDeviceCharacteristicWriteCallback(final BluetoothGatt gatt, final BluetoothGattCharacteristic gattCharacteristic, final int status, final byte[] values, final BaseBleConnectCallback baseBleConnectCallback) {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (baseBleConnectCallback != null) {
                    if (BluetoothGatt.GATT_SUCCESS != status) {
                        baseBleConnectCallback.onBluetoothGattOptionsNotSuccess(gatt, "onCharacteristicWrite", status);
                    } else {
                        baseBleConnectCallback.onCharacteristicWrite(gatt, gattCharacteristic, values);
                    }
                }
            }
        });
    }

    private void triggerDeviceCharacteristicChangedCallback(final BluetoothGatt gatt, final BluetoothGattCharacteristic gattCharacteristic, final byte[] values, final BaseBleConnectCallback baseBleConnectCallback) {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (baseBleConnectCallback != null) {
                    baseBleConnectCallback.onReceivedNotification(gatt, gattCharacteristic, values);
                }
            }
        });
    }

    private void triggerDeviceDescriptorReadCallback(final BluetoothGatt gatt, final BluetoothGattDescriptor gattDescriptor, final int status, final byte[] values, final BaseBleConnectCallback baseBleConnectCallback) {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (baseBleConnectCallback != null) {
                    if (BluetoothGatt.GATT_SUCCESS != status) {
                        baseBleConnectCallback.onBluetoothGattOptionsNotSuccess(gatt, "onDescriptorRead", status);
                    } else {
                        baseBleConnectCallback.onDescriptorRead(gatt, gattDescriptor, values);
                    }
                }
            }
        });
    }

    private void triggerDeviceDescriptorWriteCallback(final BluetoothGatt gatt, final BluetoothGattDescriptor gattDescriptor, final int status, final byte[] values, final BaseBleConnectCallback baseBleConnectCallback) {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (baseBleConnectCallback != null) {
                    if (BluetoothGatt.GATT_SUCCESS != status) {
                        baseBleConnectCallback.onBluetoothGattOptionsNotSuccess(gatt, "onDescriptorWrite", status);
                    } else {
                        baseBleConnectCallback.onDescriptorWrite(gatt, gattDescriptor, values);
                    }
                }
            }
        });
    }

    private void triggerDeviceReliableWriteCompletedCallback(final BluetoothGatt gatt, final int status, final BaseBleConnectCallback baseBleConnectCallback) {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (baseBleConnectCallback != null) {
                    if (BluetoothGatt.GATT_SUCCESS != status) {
                        baseBleConnectCallback.onBluetoothGattOptionsNotSuccess(gatt, "onReliableWriteCompleted", status);
                    } else {
                        baseBleConnectCallback.onReliableWriteCompleted(gatt);
                    }
                }
            }
        });
    }

    private void triggerDeviceReadRemoteRssiCallback(final BluetoothGatt gatt, final int rssi, final int status, final BaseBleConnectCallback baseBleConnectCallback) {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (baseBleConnectCallback != null) {
                    if (BluetoothGatt.GATT_SUCCESS != status) {
                        baseBleConnectCallback.onBluetoothGattOptionsNotSuccess(gatt, "onReliableWriteCompleted", status);
                    } else {
                        baseBleConnectCallback.onReadRemoteRssi(gatt, rssi);
                    }
                }
            }
        });
    }

    private void triggerDeviceMtuChangedCallback(final BluetoothGatt gatt, final int mtu, final int status, final BaseBleConnectCallback baseBleConnectCallback) {
        BleManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (baseBleConnectCallback != null) {
                    if (BluetoothGatt.GATT_SUCCESS != status) {
                        baseBleConnectCallback.onBluetoothGattOptionsNotSuccess(gatt, "onReliableWriteCompleted", status);
                    } else {
                        baseBleConnectCallback.onMtuChanged(gatt, mtu);
                    }
                }
            }
        });
    }
}
