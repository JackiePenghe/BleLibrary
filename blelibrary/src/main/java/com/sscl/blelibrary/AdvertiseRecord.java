package com.sscl.blelibrary;

import androidx.annotation.Nullable;

import java.io.Serializable;

/**
 * broadcast data keyBean.
 *
 * @author jacke
 */
public final class AdvertiseRecord implements Serializable {

    /*-----------------------------------static constant-----------------------------------*/

    private static final long serialVersionUID = 3717564472877619153L;

    /*-----------------------------------field variables-----------------------------------*/

    /**
     * Data length
     */
    private int length;
    /**
     * AD type
     */
    private byte type;
    /**
     * AD data
     */
    @Nullable
    private byte[] data;

    /*-----------------------------------Constructor-----------------------------------*/

    /**
     * Constructor
     *
     * @param length Data length
     * @param type   AD type
     * @param data   AD data
     */
    @SuppressWarnings("WeakerAccess")
    public AdvertiseRecord(int length, byte type, @Nullable byte[] data) {
        this.length = length;
        this.type = type;
        this.data = data;
    }

    /*-----------------------------------getter-----------------------------------*/

    /**
     * Get data length
     *
     * @return Data length
     */
    public int getLength() {
        return length;
    }

    /**
     * Get ad type
     *
     * @return Ad type
     */
    public byte getType() {
        return type;
    }

    /**
     * Get ad data
     *
     * @return Ad data
     */
    @Nullable
    public byte[] getData() {
        return data;
    }
}