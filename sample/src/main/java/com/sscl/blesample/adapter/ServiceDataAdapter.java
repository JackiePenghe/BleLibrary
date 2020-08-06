package com.sscl.blesample.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sscl.blelibrary.systems.BleParcelUuid;

import java.util.List;

/**
 * @author jackie
 */
public class ServiceDataAdapter extends BaseQuickAdapter<BleParcelUuid, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public ServiceDataAdapter(@Nullable List<BleParcelUuid> data) {
        super(android.R.layout.simple_list_item_1, data);
    }

    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * @param helper A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    @Override
    protected void convert(BaseViewHolder helper, BleParcelUuid item) {
        helper.setText(android.R.id.text1, item.toString());

    }
}