package com.sscl.blesample.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sscl.baselibrary.utils.ConversionUtil;
import com.sscl.blelibrary.AdvertiseRecord;
import com.sscl.blesample.R;

import java.util.List;

import androidx.annotation.Nullable;


/**
 * @author jackie
 */
public class AdRecordRecyclerAdapter extends BaseQuickAdapter<AdvertiseRecord, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public AdRecordRecyclerAdapter(@Nullable List<AdvertiseRecord> data) {
        super(R.layout.adapter_ad_record_recycler, data);
    }

    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * @param helper A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    @Override
    protected void convert(BaseViewHolder helper, AdvertiseRecord item) {
        byte[] data = item.getData();
        if (data == null) {
            data = new byte[0];
        }
        helper.setText(R.id.length, String.valueOf(item.getLength()))
                .setText(R.id.type, ConversionUtil.bytesToHexStr(new byte[]{item.getType()}))
                .setText(R.id.data, ConversionUtil.bytesToHexStr(data));
    }
}
