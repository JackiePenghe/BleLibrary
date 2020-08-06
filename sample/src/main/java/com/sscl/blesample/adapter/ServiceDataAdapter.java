package com.sscl.blesample.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sscl.baselibrary.utils.ConversionUtil;
import com.sscl.blelibrary.systems.BleParcelUuid;

import java.util.List;
import java.util.Map;

/**
 * @author jackie
 */
public class ServiceDataAdapter extends BaseQuickAdapter<Map.Entry<BleParcelUuid, byte[]>, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public ServiceDataAdapter(@Nullable List<Map.Entry<BleParcelUuid, byte[]>> data) {
        super(android.R.layout.simple_list_item_2, data);
    }

    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * @param helper A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    @Override
    protected void convert(BaseViewHolder helper, Map.Entry<BleParcelUuid, byte[]> item) {
        helper.setText(android.R.id.text1, item.getKey().toString())
                .setText(android.R.id.text2, ConversionUtil.byteArrayToHexStr(item.getValue()));

    }
}