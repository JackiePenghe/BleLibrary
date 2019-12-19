package com.sscl.blelibrary.enums;

import android.bluetooth.BluetoothDevice;

/**
 * preferred transport for GATT connections to remote dual-mode devices {@link
 * BluetoothDevice#TRANSPORT_AUTO} or {@link BluetoothDevice#TRANSPORT_BREDR} or {@link
 * BluetoothDevice#TRANSPORT_LE}
 *
 * @author pengh
 */
public enum Transport {

    /**
     * No preferrence of physical transport for GATT connections to remote dual-mode devices
     */
    TRANSPORT_AUTO(0),

    /**
     * Prefer BR/EDR transport for GATT connections to remote dual-mode devices
     */
    TRANSPORT_BREDR(1),

    /**
     * Prefer LE transport for GATT connections to remote dual-mode devices
     */
    TRANSPORT_LE(2);

    private int value;

    Transport(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
