package com.sscl.blesample.bean;

import com.sscl.blelibrary.BleDevice;

/**
 * 带有布尔值（被选中状态）的BleDevice类
 *
 * @author jackie
 */
public class BleDeviceWithBoolean {
    private BleDevice bleDevice;
    private boolean selected;

    public BleDeviceWithBoolean(BleDevice bleDevice, boolean selected) {
        this.bleDevice = bleDevice;
        this.selected = selected;
    }

    public BleDeviceWithBoolean() {
    }

    public BleDevice getBleDevice() {
        return bleDevice;
    }

    public void setBleDevice(BleDevice bleDevice) {
        this.bleDevice = bleDevice;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BleDeviceWithBoolean that = (BleDeviceWithBoolean) o;
        return bleDevice.equals(that.bleDevice);
    }

    @Override
    public int hashCode() {
        return bleDevice.hashCode();
    }
}
