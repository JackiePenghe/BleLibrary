package com.sscl.blelibrary.systems;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author jackie
 */
public class BleHashMap<K, V> implements Serializable {


    private static final long serialVersionUID = -7019762642982101254L;
    private ArrayList<K> keys = new ArrayList<>();
    private ArrayList<V> values = new ArrayList<>();

    @Nullable
    public V get(K key) {
        if (keys.contains(key)) {
            int indexOf = keys.indexOf(key);
            if (values.size() > indexOf) {
                return values.get(indexOf);
            }
        }
        return null;
    }

    public int size() {
        return keys.size();
    }

    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> entries = new BleHashSet<>();
        for (int i = 0; i < keys.size(); i++) {
            final int finalI = i;
            Map.Entry<K, V> kvEntry = new Map.Entry<K, V>() {
                @Override
                public K getKey() {
                    return keys.get(finalI);
                }

                @Override
                public V getValue() {
                    if (values.size() > finalI) {
                        return values.get(finalI);
                    }
                    return null;
                }

                @Override
                public V setValue(V value) {
                    return values.set(finalI, value);
                }
            };
            entries.add(kvEntry);
        }
        return entries;
    }

    @SuppressWarnings("WeakerAccess")
    public void put(K key, V value) {
        keys.add(key);
        values.add(value);
    }

    @NonNull
    @Override
    public String toString() {
        String starts = "BleHashMap [";
        String ends = " ]";
        if (keys.size() == 0) {
            return starts + "{}" + ends;
        }
        StringBuilder buffer = new StringBuilder();
        buffer.append('{');
        for (int i = 0; i < keys.size(); ++i) {
            buffer.append(keys.get(i)).append("=").append(values.size() > i ? values.get(i) : null);
        }
        buffer.append('}');
        return starts + buffer.toString() + ends;
    }
}
