package com.sscl.blesample.activity.blemulticonnect;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.sscl.baselibrary.activity.BaseAppCompatActivity;
import com.sscl.baselibrary.utils.DefaultItemDecoration;
import com.sscl.baselibrary.utils.ToastUtil;
import com.sscl.baselibrary.utils.Tool;
import com.sscl.blelibrary.BleDevice;
import com.sscl.blelibrary.BleManager;
import com.sscl.blelibrary.BleScanner;
import com.sscl.blelibrary.interfaces.OnBleScanStateChangedListener;
import com.sscl.blesample.R;
import com.sscl.blesample.adapter.MultiConnectDeviceRecyclerViewListAdapter;
import com.sscl.blesample.bean.BleDeviceWithBoolean;
import com.sscl.blesample.utils.Constants;

import java.util.ArrayList;
import java.util.List;


/**
 * 多连接时 选择设备列表（最多选择5个）
 *
 * @author jackie
 */
public class MultiConnectDeviceListActivity extends BaseAppCompatActivity {

    /**
     * 按钮点击时，计算当前是开始扫描，还是停止扫描的参数
     */
    private static final int CALCULATE_PARAMS = 2;

    /**
     * 最大可选设备数量
     */
    private static final int MAX_DEVICE_SELECT_COUNT = 5;

    /**
     * 显示设备列表的控件
     */
    private RecyclerView recyclerView;

    /**
     * 开始/停止扫描按钮
     */
    private Button button;

    /**
     * BLE扫描器
     */
    private BleScanner bleScanner;

    /**
     * 记录按钮的点击次数，用于计算是开始扫描还是停止扫描
     */
    private int buttonClickCount;

    /**
     * 设备列表适配器数据源
     */
    private ArrayList<BleDeviceWithBoolean> adapterList = new ArrayList<>();

    /**
     * 适配器
     */
    private MultiConnectDeviceRecyclerViewListAdapter adapter = new MultiConnectDeviceRecyclerViewListAdapter(adapterList);

    /**
     * 适配器的子选项被点击了
     */
    private BaseQuickAdapter.OnItemClickListener onItemClickListener = (adapter, view, position) -> {
        BleDeviceWithBoolean bleDeviceWithBoolean = adapterList.get(position);
        bleDeviceWithBoolean.setSelected(!bleDeviceWithBoolean.isSelected());
        adapterList.set(position, bleDeviceWithBoolean);
        adapter.notifyItemChanged(position);
    };

    /**
     * 点击事件的监听
     */
    private View.OnClickListener onClickListener = view -> {
        //noinspection SwitchStatementWithTooFewBranches
        switch (view.getId()) {
            case R.id.button:
                doButtonClicked();
                break;
            default:
                break;
        }
    };
    private OnBleScanStateChangedListener onBleScanStateChangedListener = new OnBleScanStateChangedListener() {
        @Override
        public void onScanFindOneDevice(BleDevice bleDevice) {

        }

        @Override
        public void onScanFindOneNewDevice(int index, @Nullable BleDevice bleDevice, @NonNull ArrayList<BleDevice> bleDevices) {
            if (bleDevice != null) {
                BleDeviceWithBoolean bleDeviceWithBoolean = new BleDeviceWithBoolean(bleDevice, false);
                adapterList.add(bleDeviceWithBoolean);
                adapter.notifyItemInserted(adapterList.size());
            } else {
                BleDeviceWithBoolean bleDeviceWithBoolean = adapterList.get(index);
                bleDeviceWithBoolean.setBleDevice(bleDevices.get(index));
                adapter.notifyItemChanged(index);
            }
        }

        @Override
        public void onScanComplete() {
            button.setText(R.string.start_scan);
            buttonClickCount++;
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {

        }

        @Override
        public void onScanFailed(int errorCode) {

        }
    };

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
        initBleScanner();
    }

    /**
     * 设置布局
     *
     * @return 布局id
     */
    @Override
    protected int setLayout() {
        return R.layout.activity_multi_connect_device_list;
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
        recyclerView = findViewById(R.id.device_list);
        button = findViewById(R.id.button);
    }

    /**
     * 初始化控件数据
     */
    @Override
    protected void initViewData() {
        initRecyclerViewData();
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
        button.setOnClickListener(onClickListener);
    }

    /**
     * 在最后进行的操作
     */
    @Override
    protected void doAfterAll() {

    }

    /**
     * 设置菜单
     *
     * @param menu 菜单
     * @return 只是重写 public boolean onCreateOptionsMenu(Menu menu)
     */
    @Override
    protected boolean createOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_multi_connect_device_list, menu);
        return true;
    }

    /**
     * 设置菜单监听
     *
     * @param item 菜单的item
     * @return true表示处理了监听事件
     */
    @Override
    protected boolean optionsItemSelected(MenuItem item) {
        //noinspection SwitchStatementWithTooFewBranches
        switch (item.getItemId()) {
            case R.id.confirm:
                toMultiConnectActivity();
                return true;
            default:
                return false;
        }
    }

    /**
     * 在activity被销毁的时候关闭扫描器
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭扫描器
        bleScanner.close();
        adapterList = null;
        button = null;
        adapter = null;
        buttonClickCount = 0;
        bleScanner = null;
        BleManager.releaseBleScannerInstance();
        //解除输入法内存泄漏
        Tool.releaseInputMethodManagerMemory(this);
    }

    /**
     * 初始化列表显示控件
     */
    private void initRecyclerViewData() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MultiConnectDeviceListActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        DefaultItemDecoration defaultItemDecoration = DefaultItemDecoration.newLine(Color.GRAY);
        recyclerView.addItemDecoration(defaultItemDecoration);
        adapter.setOnItemClickListener(onItemClickListener);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 初始化BLE扫描器
     */
    private void initBleScanner() {
        bleScanner = BleManager.getBleScannerInstance();
        if (bleScanner == null) {
            return;
        }
        bleScanner.init();
        bleScanner.setOnBleScanStateChangedListener(onBleScanStateChangedListener);
        bleScanner.setScanPeriod(20000);
        bleScanner.setAutoStartNextScan(false);
    }

    /**
     * 按钮被点击了
     */
    private void doButtonClicked() {

        //开始扫描
        if (buttonClickCount % CALCULATE_PARAMS == 0) {
            bleScanner.clearScanResults();
            adapterList.clear();
            adapter.notifyDataSetChanged();
            if (bleScanner.startScan()) {
                button.setText(R.string.stop_scan);
                buttonClickCount++;
            }
        }
        //停止扫描
        else {
            if (bleScanner.stopScan()) {
                button.setText(R.string.start_scan);
                buttonClickCount++;
            }
        }
    }

    /**
     * 跳转到多连接界面
     */
    private void toMultiConnectActivity() {
        ArrayList<BleDevice> selectedDeviceList = adapter.getSelectedDeviceList();

        if (bleScanner.isScanning()) {
            bleScanner.stopScan();
            button.setText(R.string.start_scan);
            buttonClickCount++;
        }
        if (selectedDeviceList.size() == 0) {
            ToastUtil.toastL(MultiConnectDeviceListActivity.this, R.string.nothing_selected);
            return;
        }

        if (selectedDeviceList.size() > MAX_DEVICE_SELECT_COUNT) {
            ToastUtil.toastL(MultiConnectDeviceListActivity.this, R.string.selected_more_than);
            return;
        }


        ArrayList<BluetoothDevice> bluetoothDevices = new ArrayList<>();

        for (int i = 0; i < selectedDeviceList.size(); i++) {
            bluetoothDevices.add(selectedDeviceList.get(i).getBluetoothDevice());
        }
        Intent intent = new Intent(MultiConnectDeviceListActivity.this, MultiConnectActivity.class);
        intent.putExtra(Constants.DEVICE_LIST, bluetoothDevices);
        startActivity(intent);
    }
}
