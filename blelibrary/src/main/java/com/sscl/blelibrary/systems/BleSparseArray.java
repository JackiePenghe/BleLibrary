package com.sscl.blelibrary.systems;

import android.util.SparseArray;

import java.io.Serializable;

/**
 * extends SparseArray.Only implementation Serializable and Parcelable interface.
 *
 * @author jackie
 */
public final class BleSparseArray<T> extends SparseArray<T> implements Serializable {

    /*-----------------------------------static constant-----------------------------------*/

    private static final long serialVersionUID = -7680637790048655801L;


    /*------------------------implementation method----------------------------*/

    /**
     * Creates a new SparseArray containing no mappings.
     */
    @SuppressWarnings("unused")
    BleSparseArray() {
        super();
    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//    }
//
//    @SuppressWarnings("WeakerAccess")
//    protected BleSparseArray(Parcel in) {
//    }
//
//    public static final Parcelable.Creator<BleSparseArray> CREATOR = new Parcelable.Creator<BleSparseArray>() {
//        @Override
//        public BleSparseArray createFromParcel(Parcel source) {
//            return new BleSparseArray(source);
//        }
//
//        @Override
//        public BleSparseArray[] newArray(int size) {
//            return new BleSparseArray[size];
//        }
//    };
}
