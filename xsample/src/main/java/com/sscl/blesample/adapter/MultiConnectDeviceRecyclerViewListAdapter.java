package com.sscl.blesample.adapter;

import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sscl.baselibrary.utils.ToastUtil;
import com.sscl.blelibrary.BleDevice;
import com.sscl.blesample.R;
import com.sscl.blesample.bean.BleDeviceWithBoolean;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 多连接时，设备列表的适配器
 *
 * @author jackie
 */
public class MultiConnectDeviceRecyclerViewListAdapter extends BaseQuickAdapter<BleDeviceWithBoolean, BaseViewHolder> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public MultiConnectDeviceRecyclerViewListAdapter(@Nullable List<BleDeviceWithBoolean> data) {
        super(R.layout.adapter_multi_connect_device_recycler_view_list, data);
    }

    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * @param helper A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    @Override
    protected void convert(BaseViewHolder helper, BleDeviceWithBoolean item) {
        final int position = helper.getLayoutPosition();
        helper.setText(R.id.text1, item.getBleDevice().getDeviceName())
                .setText(R.id.text2, item.getBleDevice().getDeviceAddress())
                .setTag(R.id.check_box, position)
                .setChecked(R.id.check_box, item.isSelected());
        CheckBox checkBox = helper.itemView.findViewById(R.id.check_box);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (position == (int) buttonView.getTag()) {
                    BleDeviceWithBoolean bleDeviceWithBoolean = mData.get(position);
                    bleDeviceWithBoolean.setSelected(isChecked);
                    mData.set(position, bleDeviceWithBoolean);
                }
            }
        });
    }

    /**
     * 获取被选中的设备的地址集合
     *
     * @return 被选中的设备的地址集合
     */
    @NonNull
    public ArrayList<String> getSelectedDeviceAddressList() {
        ArrayList<String> addressList = new ArrayList<>();

        for (int i = 0; i < mData.size(); i++) {
            BleDeviceWithBoolean bleDeviceWithBoolean = mData.get(i);
            if (bleDeviceWithBoolean.isSelected()) {
                addressList.add(bleDeviceWithBoolean.getBleDevice().getDeviceAddress());
            }
        }

        return addressList;
    }

    /**
     * 获取被选中的设备集合
     *
     * @return 被选中的设备集合
     */
    @NonNull
    public ArrayList<BleDevice> getSelectedDeviceList() {
        ArrayList<BleDevice> bleDevices = new ArrayList<>();

        for (int i = 0; i < mData.size(); i++) {
            BleDeviceWithBoolean bleDeviceWithBoolean = mData.get(i);
            if (bleDeviceWithBoolean.isSelected()) {
                bleDevices.add(bleDeviceWithBoolean.getBleDevice());
            }
        }

        return bleDevices;
    }

    /**
     * 全选
     */
    public void selectAll() {

        ToastUtil.toastL(mContext, "设备数量：" + mData.size());

        for (int i = 0; i < mData.size(); i++) {
            BleDeviceWithBoolean bleDeviceWithBoolean = mData.get(i);
            bleDeviceWithBoolean.setSelected(true);
            mData.set(i, bleDeviceWithBoolean);
        }
        notifyDataSetChanged();
    }

    /**
     * 取消全选
     */
    public void unSelectAll() {
        for (int i = 0; i < mData.size(); i++) {
            BleDeviceWithBoolean bleDeviceWithBoolean = mData.get(i);
            bleDeviceWithBoolean.setSelected(false);
            mData.set(i, bleDeviceWithBoolean);
        }
        notifyDataSetChanged();
    }
}
