package com.sscl.blelibrary;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.support.annotation.Nullable;

/**
 * Base class for connection callback when BLE want connect to remote device
 *
 * @author jackie
 */

public abstract class BaseBleConnectCallback {

    /*-----------------------------------static constant-----------------------------------*/

    /**
     * TAG
     */
    private static final String TAG = BaseBleConnectCallback.class.getSimpleName();

    /**
     * Used to record whether the connection has been successfully connected
     */
    private boolean isConnected;

    /**
     * Used to record whether the GATT service has been successfully discovered
     */
    private boolean isServiceDiscovered;

    /*-----------------------------------package private methods-----------------------------------*/

    /**
     * Set the connection status
     *
     * @param connected connection status
     */
    final void setConnected(boolean connected) {
        isConnected = connected;
    }

    /**
     * set service discovered status
     *
     * @param serviceDiscovered service discovered status
     */
    final void setServiceDiscovered(boolean serviceDiscovered) {
        isServiceDiscovered = serviceDiscovered;
    }

    /*-----------------------------------public methods-----------------------------------*/

    /**
     * Callback triggered while disconnected from the remote device
     *
     * @param gatt BluetoothGatt instance
     */
    public void onDisConnected(BluetoothGatt gatt) {
        DebugUtil.warnOut(TAG, gatt.getDevice().getAddress() + " onDisConnected");
    }

    /**
     * Callback triggered when the phone is connecting to a remote device
     *
     * @param gatt BluetoothGatt instance
     */
    @SuppressWarnings("WeakerAccess")
    public void onConnecting(BluetoothGatt gatt) {
        DebugUtil.warnOut(TAG, gatt.getDevice().getAddress() + " onConnecting");
    }

    /**
     * Callback triggered when the phone is connected to a remote device
     *
     * @param gatt BluetoothGatt instance
     */
    public void onConnected(BluetoothGatt gatt) {
        DebugUtil.warnOut(TAG, gatt.getDevice().getAddress() + " onConnected");
    }

    /**
     * Callback triggered when the phone is disconnecting to a remote device
     *
     * @param gatt BluetoothGatt instance
     */
    @SuppressWarnings("WeakerAccess")
    public void onDisconnecting(BluetoothGatt gatt) {
        DebugUtil.warnOut(TAG, gatt.getDevice().getAddress() + " onDisconnecting");
    }

    /**
     * After the device is successfully connected, device UUID discovery will be performed automatically..
     * Callback triggered when the phone discover remote device uuid services finished
     *
     * @param gatt BluetoothGatt instance
     */
    public void onServicesDiscovered(BluetoothGatt gatt) {
        DebugUtil.warnOut(TAG, gatt.getDevice().getAddress() + " onServicesDiscovered");
    }

    /**
     * Callback triggered when read data from the remote device
     *
     * @param gatt                BluetoothGatt instance
     * @param gattCharacteristics BluetoothGattCharacteristic
     * @param values              Data from remote device
     */
    @SuppressWarnings("WeakerAccess")
    public void onCharacteristicRead(BluetoothGatt gatt, @SuppressWarnings("unused") BluetoothGattCharacteristic gattCharacteristics,@SuppressWarnings("unused")  byte[] values) {
        DebugUtil.warnOut(TAG, gatt.getDevice().getAddress() + " onCharacteristicRead");
    }

    /**
     * Callback triggered  when the property of the phy layer of Bluetooth update
     *
     * @param gatt  BluetoothGatt instance
     * @param txPhy Tx phy value
     * @param rxPhy Rx phy value
     */
    @SuppressWarnings("WeakerAccess")
    public void onPhyUpdate(BluetoothGatt gatt,@SuppressWarnings("unused")  int txPhy,@SuppressWarnings("unused")  int rxPhy) {
        DebugUtil.warnOut(TAG, gatt.getDevice().getAddress() + " onPhyUpdate");
    }

    /**
     * Callback triggered when get the property of the phy layer of Bluetooth
     *
     * @param gatt  BluetoothGatt instance
     * @param txPhy Tx phy value
     * @param rxPhy Rx phy value
     */
    @SuppressWarnings("WeakerAccess")
    public void onPhyRead(BluetoothGatt gatt,@SuppressWarnings("unused")  int txPhy,@SuppressWarnings("unused")  int rxPhy) {
        DebugUtil.warnOut(TAG, gatt.getDevice().getAddress() + " onPhyRead");
    }

    /**
     * Callback triggered when write data to the remote device finished
     *
     * @param gatt                BluetoothGatt instance
     * @param gattCharacteristics BluetoothGattCharacteristic
     * @param values              Data write finished
     */
    @SuppressWarnings("WeakerAccess")
    public void onCharacteristicWrite(BluetoothGatt gatt,@SuppressWarnings("unused")  BluetoothGattCharacteristic gattCharacteristics,@SuppressWarnings("unused")  byte[] values) {
        DebugUtil.warnOut(TAG, gatt.getDevice().getAddress() + " onCharacteristicWrite");
    }

    /**
     * Callback triggered when received notification data form the remote device
     *
     * @param gatt                BluetoothGatt instance
     * @param gattCharacteristics BluetoothGattCharacteristic
     * @param values              notification data received form the remote device
     */
    @SuppressWarnings("WeakerAccess")
    public void onReceivedNotification(BluetoothGatt gatt,@SuppressWarnings("unused")  BluetoothGattCharacteristic gattCharacteristics,@SuppressWarnings("unused")  byte[] values) {
        DebugUtil.warnOut(TAG, gatt.getDevice().getAddress() + " onReceivedNotification");
    }

    /**
     * Callback triggered when read descriptor from the remote device
     *
     * @param gatt           BluetoothGatt instance
     * @param gattDescriptor BluetoothGattDescriptor
     * @param values         Descriptor from the remote device
     */
    @SuppressWarnings("WeakerAccess")
    public void onDescriptorRead(BluetoothGatt gatt,@SuppressWarnings("unused")  BluetoothGattDescriptor gattDescriptor,@SuppressWarnings("unused")  byte[] values) {
        DebugUtil.warnOut(TAG, gatt.getDevice().getAddress() + " onDescriptorRead");
    }

    /**
     * Callback triggered when write descriptor to the remote device finished
     *
     * @param gatt           BluetoothGatt instance
     * @param gattDescriptor BluetoothGattDescriptor
     * @param values         descriptor write finished
     */
    @SuppressWarnings("WeakerAccess")
    public void onDescriptorWrite(BluetoothGatt gatt,@SuppressWarnings("unused")  BluetoothGattDescriptor gattDescriptor,@SuppressWarnings("unused")  byte[] values) {
        DebugUtil.warnOut(TAG, gatt.getDevice().getAddress() + " onDescriptorWrite");
    }

    /**
     * Callback triggered when reliable data writing is complete
     *
     * @param gatt BluetoothGatt instance
     */
    @SuppressWarnings("WeakerAccess")
    public void onReliableWriteCompleted(BluetoothGatt gatt) {
        DebugUtil.warnOut(TAG, gatt.getDevice().getAddress() + " onReliableWriteCompleted");
    }

    /**
     * Callback triggered when reading rssi from the remote device
     *
     * @param gatt BluetoothGatt instance
     * @param rssi rssi value
     */
    @SuppressWarnings("WeakerAccess")
    public void onReadRemoteRssi(BluetoothGatt gatt,@SuppressWarnings("unused")  int rssi) {
        DebugUtil.warnOut(TAG, gatt.getDevice().getAddress() + " onReadRemoteRssi");
    }

    /**
     * Callback triggered when Mtu is changed
     *
     * @param gatt BluetoothGatt instance
     * @param mtu  mtu value
     */
    @SuppressWarnings("WeakerAccess")
    public void onMtuChanged(BluetoothGatt gatt,@SuppressWarnings("unused")  int mtu) {
        DebugUtil.warnOut(TAG, gatt.getDevice().getAddress() + " onMtuChanged");
    }

    /**
     * Get connect state with remote device
     *
     * @return true means remote device is connected
     */
    public final boolean isConnected() {
        return isConnected;
    }

    /**
     * get service Discovered state
     *
     * @return service Discovered state
     */
    @SuppressWarnings("WeakerAccess")
    public final boolean isServiceDiscovered() {
        return isServiceDiscovered;
    }

    /*-------------------------public abstract methods-------------------------*/

    /**
     * callback triggered if auto discovered GATT service failed
     *
     * @param gatt BluetoothGatt
     */
    public abstract void onServicesAutoDiscoverFailed(BluetoothGatt gatt);

    /**
     * callback triggered if GATT has been closed
     *
     * @param address remote device
     */
    public abstract void onGattClosed(@Nullable BluetoothDevice address);

    /**
     * callback trigger if an operation fails to execute
     *
     * @param gatt        BluetoothGatt
     * @param methodName  method name
     * @param errorStatus error state code
     */
    public abstract void onBluetoothGattOptionsNotSuccess(BluetoothGatt gatt, String methodName, int errorStatus);

    /**
     * GATT state unknown
     *
     * @param gatt  GATT
     * @param state state code
     */
    public abstract void onUnknownState(BluetoothGatt gatt, int state);

    /**
     * connect time out
     *
     * @param gatt BluetoothGatt
     */
    public abstract void onConnectTimeOut(BluetoothGatt gatt);
}
