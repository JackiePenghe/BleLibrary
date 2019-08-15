package com.sscl.blelibrary.enums;

/**
 * @author jackie
 */
public enum ScanPhy {
    /**
     * PHY_LE_ALL_SUPPORTED
     */
    PHY_LE_ALL_SUPPORTED(255),
    /**
     * PHY_LE_1M
     */
    PHY_LE_1M(1),
    /**
     * PHY_LE_CODED
     */
    PHY_LE_CODED(3)
    ;

    private int value;

    ScanPhy(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }}
