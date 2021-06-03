package com.sscl.blelibrary.interfaces.implementations;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

import com.sscl.blelibrary.BleManager;
import com.sscl.blelibrary.interfaces.OnBleConnectStateChangedListener;


/**
 * @author jackie
 */
public class DefaultBleConnectStateChangedListener implements OnBleConnectStateChangedListener {


    private static final String TAG = DefaultBleConnectStateChangedListener.class.getSimpleName();

    /**
     * Device connected
     */
    @Override
    public void connected() {
        BleManager.warnOut(TAG, "connected");
    }

    /**
     * device disconnected
     */
    @Override
    public void disconnected() {
        BleManager.warnOut(TAG, "disconnected");
    }

    /**
     * GATT status code error
     *
     * @param errorStatus error status code
     */
    @Override
    public void gattStatusError(int errorStatus) {
        BleManager.warnOut(TAG, "gattStatusError");
    }

    /**
     * device connecting
     */
    @Override
    public void connecting() {
        BleManager.warnOut(TAG, "connecting");
    }

    /**
     * discover device service failed.After {@link OnBleConnectStateChangedListener#connected} triggered,
     * invoke methods {@link BluetoothGatt#discoverServices} return false.
     */
    @Override
    public void autoDiscoverServicesFailed() {
        BleManager.warnOut(TAG, "autoDiscoverServicesFailed");
    }

    /**
     * device disconnecting
     */
    @Override
    public void disconnecting() {
        BleManager.warnOut(TAG, "disconnecting");
    }

    /**
     * unknown status
     *
     * @param statusCode status error
     */
    @Override
    public void unknownStatus(int statusCode) {
        BleManager.warnOut(TAG, "unknownStatus");
    }

    /**
     * GATT perform task failed
     *
     * @param errorStatus error status code
     * @param methodName  method Name
     */
    @Override
    public void gattPerformTaskFailed(int errorStatus, String methodName) {
        BleManager.warnOut(TAG, "gattPerformTaskFailed methodName = " + methodName);
    }

    /**
     * device service discovered
     */
    @Override
    public void servicesDiscovered() {
        BleManager.warnOut(TAG, "servicesDiscovered");
    }

    /**
     * read remote device data
     *
     * @param characteristic BluetoothGattCharacteristic
     * @param data           data
     */
    @Override
    public void readCharacteristicData(BluetoothGattCharacteristic characteristic, byte[] data) {
        BleManager.warnOut(TAG, "readCharacteristicData");
    }

    /**
     * write data to remote device
     *
     * @param characteristic BluetoothGattCharacteristic
     * @param data           data
     */
    @Override
    public void writeCharacteristicData(BluetoothGattCharacteristic characteristic, byte[] data) {
        BleManager.warnOut(TAG, "writeCharacteristicData");
    }

    /**
     * received notification
     *
     * @param characteristic BluetoothGattCharacteristic
     * @param data           data
     */
    @Override
    public void receivedNotification(BluetoothGattCharacteristic characteristic, byte[] data) {
        BleManager.warnOut(TAG, "receivedNotification");
    }

    /**
     * read remote device descriptor
     *
     * @param bluetoothGattDescriptor BluetoothGattDescriptor
     * @param data                    descriptor data
     */
    @Override
    public void readDescriptor(BluetoothGattDescriptor bluetoothGattDescriptor, byte[] data) {
        BleManager.warnOut(TAG, "readDescriptor");
    }

    /**
     * write descriptor to remote device
     *
     * @param bluetoothGattDescriptor BluetoothGattDescriptor
     * @param data                    descriptor data
     */
    @Override
    public void writeDescriptor(BluetoothGattDescriptor bluetoothGattDescriptor, byte[] data) {
        BleManager.warnOut(TAG, "writeDescriptor");
    }

    /**
     * reliable data write completed
     */
    @Override
    public void reliableWriteCompleted() {
        BleManager.warnOut(TAG, "reliableWriteCompleted");
    }

    /**
     * read remote device rssi
     *
     * @param rssi rssi
     */
    @Override
    public void readRemoteRssi(int rssi) {
        BleManager.warnOut(TAG, "readRemoteRssi");
    }

    /**
     * mtu changed
     *
     * @param mtu mtu
     */
    @Override
    public void mtuChanged(int mtu) {
        BleManager.warnOut(TAG, "mtuChanged");
    }

    /**
     * remote device changing the PHY
     *
     * @param txPhy tx phy value
     * @param rxPhy rx phy value
     */
    @Override
    public void phyUpdate(int txPhy, int rxPhy) {
        BleManager.warnOut(TAG, "phyUpdate");
    }

    /**
     * read remote device PHY
     *
     * @param txPhy tx phy value
     * @param rxPhy rx phy value
     */
    @Override
    public void readPhy(int txPhy, int rxPhy) {
        BleManager.warnOut(TAG, "readPhy");
    }

    /**
     * Gatt closeGatt complete
     */
    @Override
    public void onCloseComplete() {
        BleManager.warnOut(TAG, "onCloseComplete");
    }

    /**
     * connect time out
     */
    @Override
    public void onConnectTimeOut() {
        BleManager.warnOut(TAG, "onConnectTimeOut");
    }
}
