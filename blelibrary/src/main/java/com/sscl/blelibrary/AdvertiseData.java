package com.sscl.blelibrary;

/**
 * @author jackie
 */
public final class AdvertiseData {

    private int manufacturerId;
    private byte[] data;

    public AdvertiseData(int manufacturerId, byte[] data) {
        this.manufacturerId = manufacturerId;
        this.data = data;
    }

    @SuppressWarnings("WeakerAccess")
    public int getManufacturerId() {
        return manufacturerId;
    }

    public byte[] getData() {
        return data;
    }
}
