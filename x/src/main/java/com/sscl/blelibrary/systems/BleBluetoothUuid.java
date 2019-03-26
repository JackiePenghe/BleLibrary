/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sscl.blelibrary.systems;


import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.UUID;

/**
 * Static helper methods and constants to decode the BleParcelUuid of remote devices.
 *
 * @author jackie
 */
public final class BleBluetoothUuid implements Serializable {

    private static final long serialVersionUID = -6971552374569074712L;

    /**
     * See Bluetooth Assigned Numbers document - SDP section, to get the values of UUIDs
     * for the various services.
     * <p>
     * The following 128 bit values are calculated as:
     * uuid * 2^96 + BASE_UUID
     */
    @SuppressWarnings("WeakerAccess")
    public static final BleParcelUuid AUDIO_SINK =
            BleParcelUuid.fromString("0000110B-0000-1000-8000-00805F9B34FB");
    @SuppressWarnings("WeakerAccess")
    public static final BleParcelUuid AUDIO_SOURCE =
            BleParcelUuid.fromString("0000110A-0000-1000-8000-00805F9B34FB");
    @SuppressWarnings("WeakerAccess")
    public static final BleParcelUuid ADV_AUDIO_DIST =
            BleParcelUuid.fromString("0000110D-0000-1000-8000-00805F9B34FB");
    @SuppressWarnings("WeakerAccess")
    public static final BleParcelUuid HSP =
            BleParcelUuid.fromString("00001108-0000-1000-8000-00805F9B34FB");
    @SuppressWarnings("unused")
    public static final BleParcelUuid HSP_AG =
            BleParcelUuid.fromString("00001112-0000-1000-8000-00805F9B34FB");
    @SuppressWarnings("WeakerAccess")
    public static final BleParcelUuid HANDSFREE =
            BleParcelUuid.fromString("0000111E-0000-1000-8000-00805F9B34FB");
    @SuppressWarnings("unused")
    public static final BleParcelUuid HANDSFREE_AG =
            BleParcelUuid.fromString("0000111F-0000-1000-8000-00805F9B34FB");
    @SuppressWarnings("WeakerAccess")
    public static final BleParcelUuid AVRCP_CONTROLLER =
            BleParcelUuid.fromString("0000110E-0000-1000-8000-00805F9B34FB");
    @SuppressWarnings("WeakerAccess")
    public static final BleParcelUuid AVRCP_TARGET =
            BleParcelUuid.fromString("0000110C-0000-1000-8000-00805F9B34FB");
    @SuppressWarnings("WeakerAccess")
    public static final BleParcelUuid OBEX_OBJECT_PUSH =
            BleParcelUuid.fromString("00001105-0000-1000-8000-00805f9b34fb");
    @SuppressWarnings("WeakerAccess")
    public static final BleParcelUuid HID =
            BleParcelUuid.fromString("00001124-0000-1000-8000-00805f9b34fb");
    @SuppressWarnings("unused")
    public static final BleParcelUuid HOGP =
            BleParcelUuid.fromString("00001812-0000-1000-8000-00805f9b34fb");
    @SuppressWarnings("WeakerAccess")
    public static final BleParcelUuid PANU =
            BleParcelUuid.fromString("00001115-0000-1000-8000-00805F9B34FB");
    @SuppressWarnings("WeakerAccess")
    public static final BleParcelUuid NAP =
            BleParcelUuid.fromString("00001116-0000-1000-8000-00805F9B34FB");
    @SuppressWarnings("WeakerAccess")
    public static final BleParcelUuid BNEP =
            BleParcelUuid.fromString("0000000f-0000-1000-8000-00805F9B34FB");
    @SuppressWarnings("unused")
    public static final BleParcelUuid PBAP_PCE =
            BleParcelUuid.fromString("0000112e-0000-1000-8000-00805F9B34FB");
    @SuppressWarnings("unused")
    public static final BleParcelUuid PBAP_PSE =
            BleParcelUuid.fromString("0000112f-0000-1000-8000-00805F9B34FB");
    @SuppressWarnings("WeakerAccess")
    public static final BleParcelUuid MAP =
            BleParcelUuid.fromString("00001134-0000-1000-8000-00805F9B34FB");
    @SuppressWarnings("WeakerAccess")
    public static final BleParcelUuid MNS =
            BleParcelUuid.fromString("00001133-0000-1000-8000-00805F9B34FB");
    @SuppressWarnings("WeakerAccess")
    public static final BleParcelUuid MAS =
            BleParcelUuid.fromString("00001132-0000-1000-8000-00805F9B34FB");
    @SuppressWarnings("WeakerAccess")
    public static final BleParcelUuid SAP =
            BleParcelUuid.fromString("0000112D-0000-1000-8000-00805F9B34FB");
    @SuppressWarnings("unused")
    public static final BleParcelUuid HEARING_AID =
            BleParcelUuid.fromString("0000FDF0-0000-1000-8000-00805f9b34fb");

    @SuppressWarnings("WeakerAccess")
    public static final BleParcelUuid BASE_UUID =
            BleParcelUuid.fromString("00000000-0000-1000-8000-00805F9B34FB");

    /**
     * Length of bytes for 16 bit UUID
     */
    @SuppressWarnings("WeakerAccess")
    public static final int UUID_BYTES_16_BIT = 2;
    /**
     * Length of bytes for 32 bit UUID
     */
    @SuppressWarnings("WeakerAccess")
    public static final int UUID_BYTES_32_BIT = 4;
    /**
     * Length of bytes for 128 bit UUID
     */
    @SuppressWarnings("WeakerAccess")
    public static final int UUID_BYTES_128_BIT = 16;

    @SuppressWarnings("unused")
    public static final BleParcelUuid[] RESERVED_UUIDS = {
            AUDIO_SINK, AUDIO_SOURCE, ADV_AUDIO_DIST, HSP, HANDSFREE, AVRCP_CONTROLLER, AVRCP_TARGET,
            OBEX_OBJECT_PUSH, PANU, NAP, MAP, MNS, MAS, SAP};

    @SuppressWarnings("unused")
    public static boolean isAudioSource(BleParcelUuid uuid) {
        return uuid.equals(AUDIO_SOURCE);
    }

    @SuppressWarnings("unused")
    public static boolean isAudioSink(BleParcelUuid uuid) {
        return uuid.equals(AUDIO_SINK);
    }

    @SuppressWarnings("unused")
    public static boolean isAdvAudioDist(BleParcelUuid uuid) {
        return uuid.equals(ADV_AUDIO_DIST);
    }

    @SuppressWarnings("unused")
    public static boolean isHandsfree(BleParcelUuid uuid) {
        return uuid.equals(HANDSFREE);
    }

    @SuppressWarnings("unused")
    public static boolean isHeadset(BleParcelUuid uuid) {
        return uuid.equals(HSP);
    }

    @SuppressWarnings("unused")
    public static boolean isAvrcpController(BleParcelUuid uuid) {
        return uuid.equals(AVRCP_CONTROLLER);
    }

    @SuppressWarnings("unused")
    public static boolean isAvrcpTarget(BleParcelUuid uuid) {
        return uuid.equals(AVRCP_TARGET);
    }

    @SuppressWarnings("unused")
    public static boolean isInputDevice(BleParcelUuid uuid) {
        return uuid.equals(HID);
    }

    @SuppressWarnings("unused")
    public static boolean isPanu(BleParcelUuid uuid) {
        return uuid.equals(PANU);
    }

    @SuppressWarnings("unused")
    public static boolean isNap(BleParcelUuid uuid) {
        return uuid.equals(NAP);
    }

    @SuppressWarnings("unused")
    public static boolean isBnep(BleParcelUuid uuid) {
        return uuid.equals(BNEP);
    }

    @SuppressWarnings("unused")
    public static boolean isMap(BleParcelUuid uuid) {
        return uuid.equals(MAP);
    }

    @SuppressWarnings("unused")
    public static boolean isMns(BleParcelUuid uuid) {
        return uuid.equals(MNS);
    }

    @SuppressWarnings("unused")
    public static boolean isMas(BleParcelUuid uuid) {
        return uuid.equals(MAS);
    }

    @SuppressWarnings("unused")
    public static boolean isSap(BleParcelUuid uuid) {
        return uuid.equals(SAP);
    }

    /**
     * Returns true if BleParcelUuid is present in uuidArray
     *
     * @param uuidArray - Array of ParcelUuids
     * @param uuid      BleParcelUuid
     */
    @SuppressWarnings("unused")
    public static boolean isUuidPresent(BleParcelUuid[] uuidArray, BleParcelUuid uuid) {
        boolean result = uuidArray == null || uuidArray.length == 0;
        if (result && uuid == null) {
            return true;
        }

        if (uuidArray == null) {
            return false;
        }

        for (BleParcelUuid element : uuidArray) {
            if (element.equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if there any common ParcelUuids in uuidA and uuidB.
     *
     * @param uuidA - List of ParcelUuids
     * @param uuidB - List of ParcelUuids
     */
    @SuppressWarnings("unused")
    public static boolean containsAnyUuid(BleParcelUuid[] uuidA, BleParcelUuid[] uuidB) {
        if (uuidA == null && uuidB == null) {
            return true;
        }

        if (uuidA == null) {
            return uuidB.length == 0;
        }

        if (uuidB == null) {
            return uuidA.length == 0;
        }

        BleHashSet<BleParcelUuid> uuidSet = new BleHashSet<>(Arrays.asList(uuidA));
        for (BleParcelUuid uuid : uuidB) {
            if (uuidSet.contains(uuid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if all the ParcelUuids in ParcelUuidB are present in
     * ParcelUuidA
     *
     * @param uuidA - Array of ParcelUuidsA
     * @param uuidB - Array of ParcelUuidsB
     */
    @SuppressWarnings("unused")
    public static boolean containsAllUuids(BleParcelUuid[] uuidA, BleParcelUuid[] uuidB) {
        if (uuidA == null && uuidB == null) {
            return true;
        }

        if (uuidA == null) {
            return uuidB.length == 0;
        }

        if (uuidB == null) {
            return true;
        }

        BleHashSet<BleParcelUuid> uuidSet = new BleHashSet<>(Arrays.asList(uuidA));
        for (BleParcelUuid uuid : uuidB) {
            if (!uuidSet.contains(uuid)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Extract the Service Identifier or the actual uuid from the Parcel Uuid.
     * For example, if 0000110B-0000-1000-8000-00805F9B34FB is the parcel Uuid,
     * this function will return 110B
     *
     * @param parcelUuid BleParcelUuid
     * @return the service identifier.
     */
    @SuppressWarnings("WeakerAccess")
    public static int getServiceIdentifierFromParcelUuid(BleParcelUuid parcelUuid) {
        UUID uuid = parcelUuid.getUuid();
        long value = (uuid.getMostSignificantBits() & 0xFFFFFFFF00000000L) >>> 32;
        return (int) value;
    }

    /**
     * Parse UUID from bytes. The {@code uuidBytes} can represent a 16-bit, 32-bit or 128-bit UUID,
     * but the returned UUID is always in 128-bit format.
     * Note UUID is little endian in Bluetooth.
     *
     * @param uuidBytes Byte representation of uuid.
     * @return {@link BleParcelUuid} parsed from bytes.
     * @throws IllegalArgumentException If the {@code uuidBytes} cannot be parsed.
     */
    @SuppressWarnings("WeakerAccess")
    public static BleParcelUuid parseUuidFrom(byte[] uuidBytes) {
        if (uuidBytes == null) {
            throw new IllegalArgumentException("uuidBytes cannot be null");
        }
        int length = uuidBytes.length;
        if (length != UUID_BYTES_16_BIT && length != UUID_BYTES_32_BIT
                && length != UUID_BYTES_128_BIT) {
            throw new IllegalArgumentException("uuidBytes length invalid - " + length);
        }

        // Construct a 128 bit UUID.
        if (length == UUID_BYTES_128_BIT) {
            ByteBuffer buf = ByteBuffer.wrap(uuidBytes).order(ByteOrder.LITTLE_ENDIAN);
            long msb = buf.getLong(8);
            long lsb = buf.getLong(0);
            return new BleParcelUuid(new UUID(msb, lsb));
        }

        // For 16 bit and 32 bit UUID we need to convert them to 128 bit value.
        // 128_bit_value = uuid * 2^96 + BASE_UUID
        long shortUuid;
        if (length == UUID_BYTES_16_BIT) {
            shortUuid = uuidBytes[0] & 0xFF;
            shortUuid += (uuidBytes[1] & 0xFF) << 8;
        } else {
            shortUuid = uuidBytes[0] & 0xFF;
            shortUuid += (uuidBytes[1] & 0xFF) << 8;
            shortUuid += (uuidBytes[2] & 0xFF) << 16;
            shortUuid += (uuidBytes[3] & 0xFF) << 24;
        }
        long msb = BASE_UUID.getUuid().getMostSignificantBits() + (shortUuid << 32);
        long lsb = BASE_UUID.getUuid().getLeastSignificantBits();
        return new BleParcelUuid(new UUID(msb, lsb));
    }

    /**
     * Parse UUID to bytes. The returned value is shortest representation, a 16-bit, 32-bit or
     * 128-bit UUID, Note returned value is little endian (Bluetooth).
     *
     * @param uuid uuid to parse.
     * @return shortest representation of {@code uuid} as bytes.
     * @throws IllegalArgumentException If the {@code uuid} is null.
     */
    @SuppressWarnings("unused")
    public static byte[] uuidToBytes(BleParcelUuid uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("uuid cannot be null");
        }

        if (is16BitUuid(uuid)) {
            byte[] uuidBytes = new byte[UUID_BYTES_16_BIT];
            int uuidVal = getServiceIdentifierFromParcelUuid(uuid);
            uuidBytes[0] = (byte) (uuidVal & 0xFF);
            uuidBytes[1] = (byte) ((uuidVal & 0xFF00) >> 8);
            return uuidBytes;
        }

        if (is32BitUuid(uuid)) {
            byte[] uuidBytes = new byte[UUID_BYTES_32_BIT];
            int uuidVal = getServiceIdentifierFromParcelUuid(uuid);
            uuidBytes[0] = (byte) (uuidVal & 0xFF);
            uuidBytes[1] = (byte) ((uuidVal & 0xFF00) >> 8);
            uuidBytes[2] = (byte) ((uuidVal & 0xFF0000) >> 16);
            uuidBytes[3] = (byte) ((uuidVal & 0xFF000000) >> 24);
            return uuidBytes;
        }

        // Construct a 128 bit UUID.
        long msb = uuid.getUuid().getMostSignificantBits();
        long lsb = uuid.getUuid().getLeastSignificantBits();

        byte[] uuidBytes = new byte[UUID_BYTES_128_BIT];
        ByteBuffer buf = ByteBuffer.wrap(uuidBytes).order(ByteOrder.LITTLE_ENDIAN);
        buf.putLong(8, msb);
        buf.putLong(0, lsb);
        return uuidBytes;
    }

    /**
     * Check whether the given parcelUuid can be converted to 16 bit bluetooth uuid.
     *
     * @param parcelUuid BleParcelUuid
     * @return true if the parcelUuid can be converted to 16 bit uuid, false otherwise.
     */
    @SuppressWarnings("WeakerAccess")
    public static boolean is16BitUuid(BleParcelUuid parcelUuid) {
        UUID uuid = parcelUuid.getUuid();
        if (uuid.getLeastSignificantBits() != BASE_UUID.getUuid().getLeastSignificantBits()) {
            return false;
        }
        return ((uuid.getMostSignificantBits() & 0xFFFF0000FFFFFFFFL) == 0x1000L);
    }


    /**
     * Check whether the given parcelUuid can be converted to 32 bit bluetooth uuid.
     *
     * @param parcelUuid BleParcelUuid
     * @return true if the parcelUuid can be converted to 32 bit uuid, false otherwise.
     */
    @SuppressWarnings("WeakerAccess")
    public static boolean is32BitUuid(BleParcelUuid parcelUuid) {
        UUID uuid = parcelUuid.getUuid();
        if (uuid.getLeastSignificantBits() != BASE_UUID.getUuid().getLeastSignificantBits()) {
            return false;
        }
        if (is16BitUuid(parcelUuid)) {
            return false;
        }
        return ((uuid.getMostSignificantBits() & 0xFFFFFFFFL) == 0x1000L);
    }
}
