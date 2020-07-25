package com.sscl.blesample.activity.bleconnect;

import android.Manifest;
import android.bluetooth.le.ScanResult;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.sscl.baselibrary.activity.BaseAppCompatActivity;
import com.sscl.baselibrary.utils.DebugUtil;
import com.sscl.baselibrary.utils.DefaultItemDecoration;
import com.sscl.baselibrary.utils.ToastUtil;
import com.sscl.baselibrary.utils.Tool;
import com.sscl.blelibrary.BleDevice;
import com.sscl.blelibrary.BleManager;
import com.sscl.blelibrary.BleScanner;
import com.sscl.blelibrary.enums.BleCallbackType;
import com.sscl.blelibrary.enums.BleMatchMode;
import com.sscl.blelibrary.enums.BleNumOfMatches;
import com.sscl.blelibrary.enums.BleScanMode;
import com.sscl.blelibrary.enums.ScanPhy;
import com.sscl.blelibrary.interfaces.OnBleScanStateChangedListener;
import com.sscl.blelibrary.interfaces.OnBluetoothStateChangedListener;
import com.sscl.blesample.R;
import com.sscl.blesample.adapter.DeviceListAdapter;
import com.sscl.blesample.utils.Constants;
import com.sscl.blesample.watcher.EditTextWatcherForMacAddress;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 扫描设备列表的界面
 *
 * @author alm
 * Created by jackie on 2017/1/12 0012.
 */
public class DeviceListActivity extends BaseAppCompatActivity {

    /**
     * TAG
     */
    private static final String TAG = "DeviceListActivity";

    /**
     * 权限请求的requestCode
     */
    private static final int REQUEST_CODE_ASK_ACCESS_COARSE_LOCATION = 1;

    private DefaultItemDecoration defaultItemDecoration = DefaultItemDecoration.newLine(Color.GRAY);
    /**
     * 要过滤的设备名
     */
    private String filterName;
    /**
     * 要过滤的设备地址
     */
    private String filterAddress;

    /**
     * 适配器添加的设备列表
     */
    private ArrayList<BleDevice> adapterList = new ArrayList<>();
    private RecyclerView recyclerView;
    private Button button;
    private DeviceListAdapter adapter = new DeviceListAdapter(adapterList);
    /**
     * BLE扫描器
     */
    private BleScanner bleScanner;
    private static final int TWO = 2;
    private BaseQuickAdapter.OnItemClickListener onItemClickListener = new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            doListViewItemClick(position);
        }
    };

    /**
     * 点击事件的监听
     */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //noinspection SwitchStatementWithTooFewBranches
            switch (v.getId()) {
                case R.id.button:
                    checkAPIVersion();
                    break;
                default:
                    break;
            }
        }
    };
    /**
     * 列表子选项被长按时进行的回调
     */
    private BaseQuickAdapter.OnItemLongClickListener onItemLongClickListener = new BaseQuickAdapter.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
            BleDevice bleDevice = adapterList.get(position);
            showOptionsDialog(bleDevice);
            return true;
        }
    };

    private void showOptionsDialog(final BleDevice bleDevice) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.options)
                .setItems(R.array.device_list_options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bleScanner.stopScan();
                        switch (which) {
                            case 0:
                                toBroadcastIntervalTestActivity(bleDevice.getDeviceAddress());
                                break;
                            case 1:
                                toAdRecordParseActivity(bleDevice);
                                break;
                            default:
                                break;
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .setCancelable(false)
                .show();
    }

    private OnBleScanStateChangedListener onBleScanStateChangedListener = new OnBleScanStateChangedListener() {
        @Override
        public void onScanFindOneDevice(BleDevice bleDevice) {
            String deviceName = bleDevice.getDeviceName();
            String deviceAddress = bleDevice.getDeviceAddress();
            if (filterName != null) {
                if (deviceName == null || deviceName.isEmpty()) {
                    return;
                }
                if (!deviceName.startsWith(filterName)) {
                    return;
                }
            }

            if (filterAddress != null) {
                if (!deviceAddress.equals(filterAddress)) {
                    return;
                }
            }
            if (adapterList == null) {
                return;
            }
            //只要发现一个设备就会回调此函数
            if (!adapterList.contains(bleDevice)) {
                adapterList.add(bleDevice);
                adapter.notifyItemInserted(adapterList.size() - 1);
            } else {
                int indexOf = adapterList.indexOf(bleDevice);
                adapterList.set(indexOf, bleDevice);
                adapter.notifyItemChanged(indexOf);
            }
        }

        @Override
        public void onScanFindOneNewDevice(int index, @Nullable BleDevice bleDevice, @NonNull ArrayList<BleDevice> bleDevices) {

        }

        @Override
        public void onScanComplete() {
            button.setText(R.string.start_scan);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            DebugUtil.warnOut(TAG, "onBatchScanResults");
            if (results == null) {
                return;
            }
            for (int i = 0; i < results.size(); i++) {
                ScanResult scanResult = results.get(i);
                DebugUtil.warnOut(TAG, "scanResult[" + i + "] = " + scanResult.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            DebugUtil.warnOut(TAG, "onScanFailed:errorCode = " + errorCode);
        }
    };
    private OnBluetoothStateChangedListener onBluetoothStateChangedListener = new OnBluetoothStateChangedListener() {
        @Override
        public void onBluetoothEnabling() {

        }

        @Override
        public void onBluetoothEnable() {
            button.setText(R.string.stop_scan);
            adapterList.clear();
            adapter.notifyDataSetChanged();
            bleScanner.startScan(true);
        }

        @Override
        public void onBluetoothDisabling() {

        }

        @Override
        public void onBluetoothDisable() {
            button.setText(R.string.start_scan);
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
        BleManager.addOnBluetoothStateChangedListener(onBluetoothStateChangedListener);
        //初始化BLE扫描器
        initBleScanner();
    }

    /**
     * 设置布局
     *
     * @return 布局id
     */
    @Override
    protected int setLayout() {
        return R.layout.activity_device_list;
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(defaultItemDecoration);
        adapter.setOnItemClickListener(onItemClickListener);
        adapter.setOnItemLongClickListener(onItemLongClickListener);
        recyclerView.setAdapter(adapter);
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
    protected boolean createOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.activity_device_list, menu);
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
        switch (item.getItemId()) {
            case R.id.filter_name:
                showSetFilterNameDialog();
                return true;
            case R.id.filter_address:
                showSetFilterAddressDialog();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //noinspection SwitchStatementWithTooFewBranches
        switch (requestCode) {
            case REQUEST_CODE_ASK_ACCESS_COARSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doButtonClick();
                } else {
                    ToastUtil.toastLong(DeviceListActivity.this, R.string.no_permission_for_local);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
        bleScanner = null;
        BleManager.releaseBleScannerInstance();
        //解除输入法内存泄漏
        Tool.releaseInputMethodManagerMemory(this);
    }

    /**
     * 初始化扫描器
     */
    private void initBleScanner() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (BleManager.isLeCodedPhySupported()) {
                ToastUtil.toastLong(this, R.string.le_coded_supported);
            }
        }
        //创建扫描器实例
        bleScanner = BleManager.getBleScannerInstance();
        //如果手机不支持蓝牙的话，这里得到的是null,所以需要进行判空
        if (bleScanner == null) {
            ToastUtil.toastLong(DeviceListActivity.this, R.string.ble_not_supported);
            return;
        }
        //设置扫描周期，扫描会在自动在一段时间后自动停止
        bleScanner.setScanPeriod(10000);
        //设置是否一直持续扫描，true表示一直扫描，false表示在扫描结束后不再进行扫描
        bleScanner.setAutoStartNextScan(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bleScanner.setBleScanMode(BleScanMode.LOW_LATENCY);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bleScanner.setBleMatchMode(BleMatchMode.AGGRESSIVE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bleScanner.setBleCallbackType(BleCallbackType.CALLBACK_TYPE_ALL_MATCHES);
            bleScanner.setBleNumOfMatches(BleNumOfMatches.MATCH_NUM_MAX_ADVERTISEMENT);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            bleScanner.setLegacy(false);
            bleScanner.setScanPhy(ScanPhy.PHY_LE_CODED);
        }
        //设置相关回调
        bleScanner.setOnBleScanStateChangedListener(onBleScanStateChangedListener);
//        bleScanner.addFilterUuid("0000AA00-0000-1000-8000-00805f9b34fb");
        bleScanner.init();
    }


    /**
     * 判断安卓版本执行权限请求
     */
    private void checkAPIVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int checkAccessCoarseLocationPermission = ContextCompat.checkSelfPermission(DeviceListActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (checkAccessCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(DeviceListActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_ASK_ACCESS_COARSE_LOCATION);
            } else {
                doButtonClick();
            }
        } else {
            doButtonClick();
        }
    }

    /**
     * ListView的Item被点击时调用
     *
     * @param position ListView被点击的位置
     */
    private void doListViewItemClick(int position) {
        if (bleScanner.isScanning()) {
            bleScanner.stopScan();
            button.setText(R.string.start_scan);
        }
        BleDevice bleDevice = adapterList.get(position);
        Intent intent = new Intent(DeviceListActivity.this, ConnectActivity.class);
        intent.putExtra(Constants.DEVICE, (Serializable) bleDevice);
        startActivity(intent);
    }

    /**
     * 扫描/停止扫描
     */
    private void doButtonClick() {
        if (!BleManager.isBluetoothOpened()) {
            BleManager.enableBluetooth(true);
            return;
        }
        if (!bleScanner.isScanning()) {
            button.setText(R.string.stop_scan);
            adapterList.clear();
            adapter.notifyDataSetChanged();
            bleScanner.startScan(true);
        } else {
            button.setText(R.string.start_scan);
            bleScanner.stopScan();
        }
    }

    /**
     * 显示广播包内容的对话框
     *
     * @param bleDevice 广播包
     */
    private void toAdRecordParseActivity(BleDevice bleDevice) {
        Intent intent = new Intent(DeviceListActivity.this, AdRecordParseActivity.class);
        intent.putExtra(Constants.DEVICE, (Serializable) bleDevice);
        startActivity(intent);
    }

    /**
     * 显示设置过滤设备名的对话框
     */
    private void showSetFilterNameDialog() {
        final EditText editText = (EditText) View.inflate(DeviceListActivity.this, R.layout.dialog_set_filter_name, null);
        editText.setText(filterName);
        new AlertDialog.Builder(DeviceListActivity.this)
                .setTitle(R.string.filter_name)
                .setView(editText)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = editText.getText().toString();
                        if (text.isEmpty()) {
                            filterName = null;
                        } else {
                            filterName = text;
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .setCancelable(false)
                .show();
    }

    private void showSetFilterAddressDialog() {
        final EditText editText = (EditText) View.inflate(DeviceListActivity.this, R.layout.dialog_set_filter_address, null);
        EditTextWatcherForMacAddress editTextWatcherForMacAddress = new EditTextWatcherForMacAddress(editText);
        editText.addTextChangedListener(editTextWatcherForMacAddress);
        editText.setText(filterAddress);
        new AlertDialog.Builder(DeviceListActivity.this)
                .setTitle(R.string.filter_address)
                .setView(editText)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = editText.getText().toString();
                        if (text.isEmpty()) {
                            filterAddress = null;
                        } else {
                            filterAddress = text;
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .setCancelable(false)
                .show();
    }

    private void toBroadcastIntervalTestActivity(String deviceAddress) {
        Intent intent = new Intent(DeviceListActivity.this, BroadcastIntervalTestActivity.class);
        intent.putExtra(Constants.DEVICE_ADDRESS, deviceAddress);
        startActivity(intent);
    }
}
