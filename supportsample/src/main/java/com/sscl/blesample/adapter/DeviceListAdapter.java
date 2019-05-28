package com.sscl.blesample.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sscl.blelibrary.BleDevice;
import com.sscl.blesample.R;

import java.util.List;


/**
 * @author jackie
 * 自定义适配器(显示自定义BLE设备列表)
 * Created by jackie on 2017/1/6 0006.
 */
public class DeviceListAdapter extends BaseQuickAdapter<BleDevice, BaseViewHolder> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public DeviceListAdapter(@Nullable List<BleDevice> data) {
        super(R.layout.adapter_device_list, data);
    }


    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * @param helper A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    @Override
    protected void convert(BaseViewHolder helper, BleDevice item) {
        helper.setText(android.R.id.text1, item.getBluetoothDevice().getName())
                .setText(android.R.id.text2, item.getBluetoothDevice().getAddress())
                .setText(R.id.rssi, String.valueOf(item.getRssi()));
    }
}
