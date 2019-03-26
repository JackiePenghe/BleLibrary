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

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.UUID;

/**
 * This class is a Parcelable wrapper around {@link UUID} which is an
 * immutable representation of a 128-bit universally unique
 * identifier.
 *
 * @author jackie
 */
public final class BleParcelUuid implements Serializable, Parcelable {


    private static final long serialVersionUID = 784994389071873086L;
    private final UUID mUuid;

    /**
     * Constructor creates a BleParcelUuid instance from the
     * given {@link UUID}.
     *
     * @param uuid UUID
     */
    @SuppressWarnings("WeakerAccess")
    public BleParcelUuid(UUID uuid) {
        mUuid = uuid;
    }

    /**
     * Creates a new BleParcelUuid from a string representation of {@link UUID}.
     *
     * @param uuid the UUID string to parse.
     * @return a BleParcelUuid instance.
     * @throws NullPointerException     if {@code uuid} is {@code null}.
     * @throws IllegalArgumentException if {@code uuid} is not formatted correctly.
     */
    @SuppressWarnings("WeakerAccess")
    public static BleParcelUuid fromString(String uuid) {
        return new BleParcelUuid(UUID.fromString(uuid));
    }

    /**
     * Get the {@link UUID} represented by the BleParcelUuid.
     *
     * @return UUID contained in the BleParcelUuid.
     */
    public UUID getUuid() {
        return mUuid;
    }

    /**
     * Returns a string representation of the BleParcelUuid
     * For example: 0000110B-0000-1000-8000-00805F9B34FB will be the return value.
     *
     * @return a String instance.
     */
    @Override
    public String toString() {
        return mUuid.toString();
    }


    @Override
    public int hashCode() {
        return mUuid.hashCode();
    }

    /**
     * Compares this BleParcelUuid to another object for equality. If {@code object}
     * is not {@code null}, is a BleParcelUuid instance, and all bits are equal, then
     * {@code true} is returned.
     *
     * @param object the {@code Object} to compare to.
     * @return {@code true} if this BleParcelUuid is equal to {@code object}
     * or {@code false} if not.
     */
    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }

        if (this == object) {
            return true;
        }

        if (!(object instanceof BleParcelUuid)) {
            return false;
        }

        BleParcelUuid that = (BleParcelUuid) object;

        return (this.mUuid.equals(that.mUuid));
    }

    public static final Creator<BleParcelUuid> CREATOR = new Creator<BleParcelUuid>() {
                @Override
                public BleParcelUuid createFromParcel(Parcel source) {
                    long mostSigBits = source.readLong();
                    long leastSigBits = source.readLong();
                    UUID uuid = new UUID(mostSigBits, leastSigBits);
                    return new BleParcelUuid(uuid);
                }

                @Override
                public BleParcelUuid[] newArray(int size) {
                    return new BleParcelUuid[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mUuid.getMostSignificantBits());
        dest.writeLong(mUuid.getLeastSignificantBits());
    }
}
