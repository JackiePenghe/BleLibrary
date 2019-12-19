package com.sscl.blelibrary.enums;

import android.bluetooth.BluetoothDevice;

/**
 * preferred PHY for connections to remote LE device. Bitwise OR of any of {@link
 * BluetoothDevice#PHY_LE_1M_MASK}, {@link BluetoothDevice#PHY_LE_2M_MASK}, and {@link
 * BluetoothDevice#PHY_LE_CODED_MASK}. This option does not take effect if {@code autoConnect}
 *
 * @author pengh
 */
public enum PhyMask {


    /**
     * Bluetooth LE 1M PHY mask. Used to specify LE 1M Physical Channel as one of many available
     * options in a bitmask.
     */
    PHY_LE_1M_MASK(1),

    /**
     * Bluetooth LE 2M PHY mask. Used to specify LE 2M Physical Channel as one of many available
     * options in a bitmask.
     */
    PHY_LE_2M_MASK(2),

    /**
     * Bluetooth LE Coded PHY mask. Used to specify LE Coded Physical Channel as one of many
     * available options in a bitmask.
     */
    PHY_LE_CODED_MASK(4);

    private int value;

    PhyMask(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
