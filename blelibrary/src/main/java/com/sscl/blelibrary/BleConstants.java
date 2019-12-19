package com.sscl.blelibrary;

/**
 * BLE constant
 *
 * @author jackie
 */
@SuppressWarnings("WeakerAccess")
public final class BleConstants {

    /*-----------------------------------package private constants-----------------------------------*/

    /**
     * UUID that will be used when enable a notification
     */
    static final String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    /*-----------------------------------public constants-----------------------------------*/

    /**
     * device bound request success
     */
    public static final int DEVICE_BOND_REQUEST_SUCCESS = 0;
    /**
     * device bound request failed
     */
    public static final int DEVICE_BOND_REQUEST_FAILED = 1;
    /**
     * Bluetooth manager is null
     */
    public static final int BLUETOOTH_MANAGER_NULL = 2;
    /**
     * Bluetooth adapter is null
     */
    public static final int BLUETOOTH_ADAPTER_NULL = 3;
    /**
     * Device is bound
     */
    public static final int DEVICE_BOND_BONDED = 4;
    /**
     * Device is bounding
     */
    public static final int DEVICE_BOND_BONDING = 5;
    /**
     * Wrong device address
     */
    public static final int BLUETOOTH_ADDRESS_INCORRECT = 6;
    /**
     * Context is null
     */
    public static final int CONTEXT_NULL = 7;

    /*-----------------------------------Constructor-----------------------------------*/

    /**
     * Constructor
     *
     * @throws InstantiationException Prohibit creating instances
     */
    private BleConstants() throws InstantiationException {
        throw new InstantiationException("Do not create an instance!");
    }
}
