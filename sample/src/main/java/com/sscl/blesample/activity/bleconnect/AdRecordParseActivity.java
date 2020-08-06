package com.sscl.blesample.activity.bleconnect;

import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sscl.baselibrary.activity.BaseAppCompatActivity;
import com.sscl.baselibrary.utils.ConversionUtil;
import com.sscl.baselibrary.utils.DefaultItemDecoration;
import com.sscl.blelibrary.AdvertiseRecord;
import com.sscl.blelibrary.BleDevice;
import com.sscl.blelibrary.systems.BleArrayList;
import com.sscl.blelibrary.systems.BleParcelUuid;
import com.sscl.blelibrary.systems.BleScanRecord;
import com.sscl.blesample.R;
import com.sscl.blesample.adapter.AdRecordRecyclerAdapter;
import com.sscl.blesample.adapter.ServiceDataAdapter;
import com.sscl.blesample.utils.Constants;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author jackie
 */
public class AdRecordParseActivity extends BaseAppCompatActivity {

    /**
     * 蓝牙设备
     */
    private BleDevice bleDevice;
    /**
     * 广播包数据
     */
    private EditText scanRecordEditText;
    /**
     * 将广播包按照AdType分开显示的列表
     */
    private RecyclerView adDataRv;

    private RecyclerView serviceDataRv;
    /**
     * 广播包数据适配器数据源
     */
    private ArrayList<AdvertiseRecord> adRecords = new ArrayList<>();
    /**
     * 广播包服务适配器数据源
     */
    private ArrayList<BleParcelUuid> serviceDataList = new ArrayList<>();
    /**
     * 广播包数据列表适配器
     */
    private AdRecordRecyclerAdapter adRecordRecyclerAdapter = new AdRecordRecyclerAdapter(adRecords);
    /**
     * 广播包服务列表适配器
     */
    private ServiceDataAdapter serviceDataAdapter = new ServiceDataAdapter(serviceDataList);
    /**
     * 默认的分割线
     */
    private DefaultItemDecoration defaultItemDecoration = DefaultItemDecoration.newLine(Color.GRAY);

    /**
     * 标题栏的返回按钮被按下的时候回调此函数
     */
    @Override
    protected void titleBackClicked() {
        onBackPressed();
    }

    /**
     * 在设置布局之前需要进行的操作
     */
    @Override
    protected void doBeforeSetLayout() {
        getBleDevice();
    }

    /**
     * 设置布局
     *
     * @return 布局id
     */
    @Override
    protected int setLayout() {
        return R.layout.activity_ad_record_parse;
    }

    /**
     * 在设置布局之后，进行其他操作之前，所需要初始化的数据
     */
    @Override
    protected void doBeforeInitOthers() {
        setTitleText(R.string.app_name);
    }

    /**
     * 初始化布局控件
     */
    @Override
    protected void initViews() {
        scanRecordEditText = findViewById(R.id.scan_record_et);
        adDataRv = findViewById(R.id.scan_record_rv);
        serviceDataRv = findViewById(R.id.service_data_rv);
    }

    /**
     * 初始化控件数据
     */
    @Override
    protected void initViewData() {
        initEditText();
        initRecyclerView();
    }

    /**
     * 初始化其他数据
     */
    @Override
    protected void initOtherData() {

    }

    /**
     * 初始化事件
     */
    @Override
    protected void initEvents() {

    }

    /**
     * 在最后进行的操作
     */
    @Override
    protected void doAfterAll() {
        refreshRecyclerViewData();
    }

    /**
     * 设置菜单
     *
     * @param menu 菜单
     * @return 只是重写 public boolean onCreateOptionsMenu(Menu menu)
     */
    @Override
    protected boolean createOptionsMenu(@NonNull Menu menu) {
        return false;
    }

    /**
     * 设置菜单监听
     *
     * @param item 菜单的item
     * @return true表示处理了监听事件
     */
    @Override
    protected boolean optionsItemSelected(@NonNull MenuItem item) {
        return false;
    }

    private void getBleDevice() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        Serializable serializableExtra = intent.getSerializableExtra(Constants.DEVICE);
        if (serializableExtra == null) {
            return;
        }
        if (!(serializableExtra instanceof BleDevice)) {
            return;
        }
        bleDevice = (BleDevice) serializableExtra;
    }

    private void initEditText() {
        if (bleDevice == null) {
            return;
        }
        byte[] scanRecordBytes = bleDevice.getScanRecordBytes();
        scanRecordEditText.setText(ConversionUtil.byteArrayToHexStr(scanRecordBytes));
    }

    private void initRecyclerView() {
        initAdData();
        initServiceData();
    }

    private void initServiceData() {
        serviceDataRv.setLayoutManager(new LinearLayoutManager(this));
        serviceDataRv.addItemDecoration(defaultItemDecoration);
        serviceDataAdapter.bindToRecyclerView(serviceDataRv);
    }

    private void initAdData() {
        adDataRv.setLayoutManager(new LinearLayoutManager(this));
        adDataRv.addItemDecoration(defaultItemDecoration);
        adRecordRecyclerAdapter.bindToRecyclerView(adDataRv);
    }

    private void refreshRecyclerViewData() {
        if (bleDevice == null) {
            return;
        }
        refreshAdDataList();
        refreshServiceDataList();
    }

    private void refreshServiceDataList() {
        BleScanRecord bleScanRecord = bleDevice.getBleScanRecord();
        BleArrayList<BleParcelUuid> serviceUuids = bleScanRecord.getServiceUuids();
        serviceDataList.clear();
        if (serviceUuids != null) {
            serviceDataList.addAll(serviceUuids);
        }
        serviceDataAdapter.notifyDataSetChanged();
    }

    private void refreshAdDataList() {
        ArrayList<AdvertiseRecord> adRecords = bleDevice.getAdvertiseRecords();
        this.adRecords.clear();
        if (adRecords != null) {
            this.adRecords.addAll(adRecords);
        }
        adRecordRecyclerAdapter.notifyDataSetChanged();
    }
}
