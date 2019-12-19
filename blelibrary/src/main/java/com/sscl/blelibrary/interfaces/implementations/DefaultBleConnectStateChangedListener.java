package com.sscl.blelibrary.interfaces.implementations;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

import com.sscl.blelibrary.DebugUtil;
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
        DebugUtil.warnOut(TAG, "connected");
    }

    /**
     * device disconnected
     */
    @Override
    public void disconnected() {
        DebugUtil.warnOut(TAG, "disconnected");
    }

    /**
     * GATT status code error
     *
     * @param errorStatus error status code
     */
    @Override
    public void gattStatusError(int errorStatus) {
        DebugUtil.warnOut(TAG, "gattStatusError");
    }

    /**
     * device connecting
     */
    @Override
    public void connecting() {
        DebugUtil.warnOut(TAG, "connecting");
    }

    /**
     * discover device service failed.After {@link OnBleConnectStateChangedListener#connected} triggered,
     * invoke methods {@link BluetoothGatt#discoverServices} return false.
     */
    @Override
    public void autoDiscoverServicesFailed() {
        DebugUtil.warnOut(TAG, "autoDiscoverServicesFailed");
    }

    /**
     * device disconnecting
     */
    @Override
    public void disconnecting() {
        DebugUtil.warnOut(TAG, "disconnecting");
    }

    /**
     * unknown status
     *
     * @param statusCode status error
     */
    @Override
    public void unknownStatus(int statusCode) {
        DebugUtil.warnOut(TAG, "unknownStatus");
    }

    /**
     * GATT perform task failed
     *
     * @param errorStatus error status code
     * @param methodName  method Name
     */
    @Override
    public void gattPerformTaskFailed(int errorStatus, String methodName) {
        DebugUtil.warnOut(TAG, "gattPerformTaskFailed methodName = " + methodName);
    }

    /**
     * device service discovered
     */
    @Override
    public void servicesDiscovered() {
        DebugUtil.warnOut(TAG, "servicesDiscovered");
    }

    /**
     * read remote device data
     *
     * @param characteristic BluetoothGattCharacteristic
     * @param data           data
     */
    @Override
    public void readCharacteristicData(BluetoothGattCharacteristic characteristic, byte[] data) {
        DebugUtil.warnOut(TAG, "readCharacteristicData");
    }

    /**
     * write data to remote device
     *
     * @param characteristic BluetoothGattCharacteristic
     * @param data           data
     */
    @Override
    public void writeCharacteristicData(BluetoothGattCharacteristic characteristic, byte[] data) {
        DebugUtil.warnOut(TAG, "writeCharacteristicData");
    }

    /**
     * received notification
     *
     * @param characteristic BluetoothGattCharacteristic
     * @param data           data
     */
    @Override
    public void receivedNotification(BluetoothGattCharacteristic characteristic, byte[] data) {
        DebugUtil.warnOut(TAG, "receivedNotification");
    }

    /**
     * read remote device descriptor
     *
     * @param bluetoothGattDescriptor BluetoothGattDescriptor
     * @param data                    descriptor data
     */
    @Override
    public void readDescriptor(BluetoothGattDescriptor bluetoothGattDescriptor, byte[] data) {
        DebugUtil.warnOut(TAG, "readDescriptor");
    }

    /**
     * write descriptor to remote device
     *
     * @param bluetoothGattDescriptor BluetoothGattDescriptor
     * @param data                    descriptor data
     */
    @Override
    public void writeDescriptor(BluetoothGattDescriptor bluetoothGattDescriptor, byte[] data) {
        DebugUtil.warnOut(TAG, "writeDescriptor");
    }

    /**
     * reliable data write completed
     */
    @Override
    public void reliableWriteCompleted() {
        DebugUtil.warnOut(TAG, "reliableWriteCompleted");
    }

    /**
     * read remote device rssi
     *
     * @param rssi rssi
     */
    @Override
    public void readRemoteRssi(int rssi) {
        DebugUtil.warnOut(TAG, "readRemoteRssi");
    }

    /**
     * mtu changed
     *
     * @param mtu mtu
     */
    @Override
    public void mtuChanged(int mtu) {
        DebugUtil.warnOut(TAG, "mtuChanged");
    }

    /**
     * remote device changing the PHY
     *
     * @param txPhy tx phy value
     * @param rxPhy rx phy value
     */
    @Override
    public void phyUpdate(int txPhy, int rxPhy) {
        DebugUtil.warnOut(TAG, "phyUpdate");
    }

    /**
     * read remote device PHY
     *
     * @param txPhy tx phy value
     * @param rxPhy rx phy value
     */
    @Override
    public void readPhy(int txPhy, int rxPhy) {
        DebugUtil.warnOut(TAG, "readPhy");
    }

    /**
     * Gatt closeGatt complete
     */
    @Override
    public void onCloseComplete() {
        DebugUtil.warnOut(TAG, "onCloseComplete");
    }

    /**
     * connect time out
     */
    @Override
    public void onConnectTimeOut() {
        DebugUtil.warnOut(TAG, "onConnectTimeOut");
    }
}
