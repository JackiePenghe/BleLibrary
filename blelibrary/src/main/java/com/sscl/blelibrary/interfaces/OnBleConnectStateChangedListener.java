package com.sscl.blelibrary.interfaces;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

/**
 * BLE device connect status changed listener
 *
 * @author jackie
 */
public interface OnBleConnectStateChangedListener {

    /**
     * Device connected
     */
    void connected();

    /**
     * device disconnected
     */
    void disconnected();

    /**
     * GATT status code error
     *
     * @param errorStatus error status code
     */
    void gattStatusError(int errorStatus);

    /**
     * device connecting
     */
    void connecting();

    /**
     * discover device service failed.After {@link OnBleConnectStateChangedListener#connected} triggered,
     * invoke methods {@link BluetoothGatt#discoverServices} return false.
     */
    void autoDiscoverServicesFailed();

    /**
     * device disconnecting
     */
    void disconnecting();

    /**
     * unknown status
     *
     * @param statusCode status error
     */
    void unknownStatus(int statusCode);

    /**
     * GATT perform task failed
     *
     * @param errorStatus error status code
     * @param methodName  方法名
     */
    void gattPerformTaskFailed(int errorStatus, String methodName);

    /**
     * device service discovered
     */
    void servicesDiscovered();

    /**
     * read remote device data
     *
     * @param characteristic BluetoothGattCharacteristic
     * @param data           data
     */
    void readCharacteristicData(BluetoothGattCharacteristic characteristic, byte[] data);

    /**
     * write data to remote device
     *
     * @param characteristic BluetoothGattCharacteristic
     * @param data           data
     */
    void writeCharacteristicData(BluetoothGattCharacteristic characteristic, byte[] data);

    /**
     * received notification
     *
     * @param characteristic BluetoothGattCharacteristic
     * @param data           data
     */
    void receivedNotification(BluetoothGattCharacteristic characteristic, byte[] data);

    /**
     * read remote device descriptor
     *
     * @param bluetoothGattDescriptor BluetoothGattDescriptor
     * @param data                    descriptor data
     */
    void readDescriptor(BluetoothGattDescriptor bluetoothGattDescriptor, byte[] data);

    /**
     * write descriptor to remote device
     *
     * @param bluetoothGattDescriptor BluetoothGattDescriptor
     * @param data                    descriptor data
     */
    void writeDescriptor(BluetoothGattDescriptor bluetoothGattDescriptor, byte[] data);

    /**
     * reliable data write completed
     */
    void reliableWriteCompleted();

    /**
     * read remote device rssi
     *
     * @param rssi rssi
     */
    void readRemoteRssi(int rssi);

    /**
     * mtu changed
     *
     * @param mtu mtu
     */
    void mtuChanged(int mtu);

    /**
     * remote device changing the PHY
     *
     * @param txPhy tx phy value
     * @param rxPhy rx phy value
     */
    void phyUpdate(int txPhy, int rxPhy);

    /**
     * read remote device PHY
     *
     * @param txPhy tx phy value
     * @param rxPhy rx phy value
     */
    void readPhy(int txPhy, int rxPhy);

    /**
     * Gatt closeGatt complete
     */
    void onCloseComplete();

    /**
     * connect time out
     */
    void onConnectTimeOut();
}
