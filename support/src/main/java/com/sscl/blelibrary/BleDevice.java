package com.sscl.blelibrary;

import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.sscl.blelibrary.systems.BleHashMap;
import com.sscl.blelibrary.systems.BleScanRecord;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * BLE device been
 *
 * @author jackie
 */
public final class BleDevice implements Serializable, Parcelable {

    /*-----------------------------------static constant-----------------------------------*/

    private static final long serialVersionUID = -2219219185665113265L;

    private static final String TAG = BleDevice.class.getSimpleName();

    /*-----------------------------------field variables-----------------------------------*/

    /**
     * Bluetooth Device
     */
    private BluetoothDevice bluetoothDevice;

    /**
     * rssi
     */
    private int rssi;
    /**
     * BleScanRecord
     */
    private BleScanRecord bleScanRecord;

    /**
     * the primary Physical Layer on which this advertisment was received
     */
    private int primaryPhy;

    /**
     * the secondary Physical Layer on which this advertisment was received
     */
    private int secondaryPhy;

    /**
     * the advertising set id.
     */
    private int advertisingSid;

    /**
     * the periodic advertising interval in units of 1.25ms.
     */
    private int periodicAdvertisingInterval;
    /**
     * the data status.
     */
    private int dataStatus;

    /**
     * the transmit power in dBm.
     */
    private int txPower;

    /**
     * timestamp since boot when the scan record was observed.
     */
    private long timestampNanos;

    /*-----------------------------------Constructor-----------------------------------*/

    /**
     * Constructor
     *
     * @param bluetoothDevice BluetoothDevice
     * @param rssi            rssi
     * @param bleScanRecord   BleScanRecord
     */
    BleDevice(@NonNull BluetoothDevice bluetoothDevice, int rssi, @NonNull BleScanRecord bleScanRecord) {
        this.bluetoothDevice = bluetoothDevice;
        this.rssi = rssi;
        this.bleScanRecord = bleScanRecord;
    }

    /*-----------------------------------getter-----------------------------------*/

    /**
     * get BluetoothDevice
     *
     * @return BluetoothDevice
     */
    @NonNull
    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    /**
     * get rssi
     *
     * @return rssi
     */
    public int getRssi() {
        return rssi;
    }

    /*-----------------------------------public method-----------------------------------*/

    /**
     * get device name
     *
     * @return device name
     */
    @Nullable
    public String getDeviceName() {
        String deviceName = bluetoothDevice.getName();
        if (deviceName != null) {
            return deviceName;
        }
        return bleScanRecord.getDeviceName();
    }

    /**
     * get device address
     *
     * @return device address
     */
    @NonNull
    public String getDeviceAddress() {
        return bluetoothDevice.getAddress();
    }

    /**
     * get AdvertiseRecord collection
     *
     * @return AdvertiseRecord collection
     */
    @Nullable
    public ArrayList<AdvertiseRecord> getAdvertiseRecords() {
        ArrayList<AdvertiseRecord> advertiseRecords = new ArrayList<>();
        BleHashMap<Byte, byte[]> manufacturerSpecificDatas = getManufacturerSpecificData();
        if (manufacturerSpecificDatas == null) {
            return null;
        }
        DebugUtil.warnOut(TAG, "manufacturerSpecificDatas size = " + manufacturerSpecificDatas.size());

        Set<Map.Entry<Byte, byte[]>> entries = manufacturerSpecificDatas.entrySet();
        for (Map.Entry<Byte, byte[]> next : entries) {
            int type = next.getKey();
            byte[] data = next.getValue();
            int length = data.length + 1;
            AdvertiseRecord advertiseRecord = new AdvertiseRecord(length, (byte) type, data);
            advertiseRecords.add(advertiseRecord);
        }
        return advertiseRecords;
    }

    /**
     * Obtain scan record data by the specified AD type
     *
     * @param type AD type
     * @return scan record data
     */
    @Nullable
    public byte[] getManufacturerSpecificData(byte type) {
        return bleScanRecord.getManufacturerSpecificData(type);
    }

    /**
     * Returns a sparse array of manufacturer identifier and its corresponding manufacturer specific
     * data.
     */
    @SuppressWarnings("WeakerAccess")
    public BleHashMap<Byte, byte[]> getManufacturerSpecificData() {
        return bleScanRecord.getManufacturerSpecificData();
    }

    /**
     * get scan record byte array
     *
     * @return scan record byte array
     */
    @NonNull
    public byte[] getScanRecordBytes() {
        return bleScanRecord.getBytes();
    }

    @NonNull
    public BleScanRecord getBleScanRecord() {
        return bleScanRecord;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getPrimaryPhy() {
        return primaryPhy;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getSecondaryPhy() {
        return secondaryPhy;
    }

    /**
     * Set the advertising set id.
     * @return advertisingSid
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getAdvertisingSid() {
        return advertisingSid;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getPeriodicAdvertisingInterval() {
        return periodicAdvertisingInterval;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getDataStatus() {
        return dataStatus;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getTxPower() {
        return txPower;
    }

    public long getTimestampNanos() {
        return timestampNanos;
    }

    /*-----------------------------------package method-----------------------------------*/

    /**
     * Set the primary Physical Layer on which this advertisment was received
     *
     * @param primaryPhy primaryPhy
     */
    void setPrimaryPhy(int primaryPhy) {
        this.primaryPhy = primaryPhy;
    }

    /**
     * Set the secondary Physical Layer on which this advertisment was received.
     *
     * @param secondaryPhy secondaryPhy
     */
    void setSecondaryPhy(int secondaryPhy) {
        this.secondaryPhy = secondaryPhy;
    }

    /**
     * Set the advertising set id.
     *
     * @param advertisingSid advertisingSid
     */
    void setAdvertisingSid(int advertisingSid) {
        this.advertisingSid = advertisingSid;
    }

    /**
     * Set the periodic advertising interval in units of 1.25ms.
     *
     * @param periodicAdvertisingInterval periodicAdvertisingInterval
     */
    void setPeriodicAdvertisingInterval(int periodicAdvertisingInterval) {
        this.periodicAdvertisingInterval = periodicAdvertisingInterval;
    }

    /**
     * Set the data status.
     *
     * @param dataStatus dataStatus
     */
    void setDataStatus(int dataStatus) {
        this.dataStatus = dataStatus;
    }

    /**
     * Set the transmit power in dBm.
     *
     * @param txPower txPower
     */
    void setTxPower(int txPower) {
        this.txPower = txPower;
    }

    /**
     * Set timestamp since boot when the scan record was observed.
     * @param timestampNanos timestamp
     */
    void setTimestampNanos(long timestampNanos) {
        this.timestampNanos = timestampNanos;
    }

    /*-----------------------------------override method-----------------------------------*/

    /**
     * Indicates whether some other object is "equal to" this one.
     * <p>
     * The {@code equals} method implements an equivalence relation
     * on non-null object references:
     * <ul>
     * <li>It is <i>reflexive</i>: for any non-null reference value
     * {@code x}, {@code x.equals(x)} should return
     * {@code true}.
     * <li>It is <i>symmetric</i>: for any non-null reference values
     * {@code x} and {@code y}, {@code x.equals(y)}
     * should return {@code true} if and only if
     * {@code y.equals(x)} returns {@code true}.
     * <li>It is <i>transitive</i>: for any non-null reference values
     * {@code x}, {@code y}, and {@code z}, if
     * {@code x.equals(y)} returns {@code true} and
     * {@code y.equals(z)} returns {@code true}, then
     * {@code x.equals(z)} should return {@code true}.
     * <li>It is <i>consistent</i>: for any non-null reference values
     * {@code x} and {@code y}, multiple invocations of
     * {@code x.equals(y)} consistently return {@code true}
     * or consistently return {@code false}, provided no
     * information used in {@code equals} comparisons on the
     * objects is modified.
     * <li>For any non-null reference value {@code x},
     * {@code x.equals(null)} should return {@code false}.
     * </ul>
     * <p>
     * The {@code equals} method for class {@code Object} implements
     * the most discriminating possible equivalence relation on objects;
     * that is, for any non-null reference values {@code x} and
     * {@code y}, this method returns {@code true} if and only
     * if {@code x} and {@code y} refer to the same object
     * ({@code x == y} has the value {@code true}).
     * <p>
     * Note that it is generally necessary to override the {@code hashCode}
     * method whenever this method is overridden, so as to maintain the
     * general contract for the {@code hashCode} method, which states
     * that equal objects must have equal hash codes.
     *
     * @param obj the reference object with which to compare.
     * @return {@code true} if this object is the same as the obj
     * argument; {@code false} otherwise.
     * @see #hashCode()
     * @see HashMap
     * <p>
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BleDevice)) {
            return false;
        }

        BleDevice bleDevice = (BleDevice) obj;
        BluetoothDevice bluetoothDevice = bleDevice.getBluetoothDevice();
        return bluetoothDevice.equals(getBluetoothDevice());
    }

    @Override
    public String toString() {
        return "BleDevice{" +
                "bluetoothDevice=" + bluetoothDevice +
                ", rssi=" + rssi +
                ", scanRecordBytes=" + Arrays.toString(getScanRecordBytes()) +
                ", bleScanRecord=" + bleScanRecord +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.bluetoothDevice, flags);
        dest.writeInt(this.rssi);
        dest.writeSerializable(this.bleScanRecord);
    }

    protected BleDevice(Parcel in) {
        this.bluetoothDevice = in.readParcelable(BluetoothDevice.class.getClassLoader());
        this.rssi = in.readInt();
        this.bleScanRecord = (BleScanRecord) in.readSerializable();
    }

    public static final Creator<BleDevice> CREATOR = new Creator<BleDevice>() {
        @Override
        public BleDevice createFromParcel(Parcel source) {
            return new BleDevice(source);
        }

        @Override
        public BleDevice[] newArray(int size) {
            return new BleDevice[size];
        }
    };
}
