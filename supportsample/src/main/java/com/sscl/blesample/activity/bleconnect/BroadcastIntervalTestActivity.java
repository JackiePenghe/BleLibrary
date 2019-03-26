package com.sscl.blesample.activity.bleconnect;

import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.sscl.baselibrary.activity.BaseAppCompatActivity;
import com.sscl.baselibrary.utils.DebugUtil;
import com.sscl.baselibrary.view.utils.DefaultItemDecoration;
import com.sscl.blelibrary.BleDevice;
import com.sscl.blelibrary.BleManager;
import com.sscl.blelibrary.BleScanner;
import com.sscl.blelibrary.interfaces.OnBleScanStateChangedListener;
import com.sscl.blesample.R;
import com.sscl.blesample.adapter.BroadcastIntervalAdapter;
import com.sscl.blesample.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author jackie
 */
public class BroadcastIntervalTestActivity extends BaseAppCompatActivity {

    private static final String TAG = BroadcastIntervalTestActivity.class.getSimpleName();
    private TextView timeTv;
    private TextView countsTv;
    private TextView rssiTv;
    private ArrayList<Integer> rssis = new ArrayList<>();
    private ScheduledExecutorService scheduledExecutorService = BleManager.newScheduledExecutorService();
    private OnBleScanStateChangedListener onBleScanStateChangedListener = new OnBleScanStateChangedListener() {
        @Override
        public void onScanFindOneDevice(BleDevice bleDevice) {
            if (bleDevice.getDeviceAddress().equals(deviceAddress)) {
                rssis.add(bleDevice.getRssi());
                counts++;
                DebugUtil.warnOut(TAG, "counts = " + counts);
                countsTv.setText(String.format(Locale.getDefault(), "%d times", counts));
                long currentTimeMillis = System.currentTimeMillis();
                if (lastTime == 0) {
                    lastTime = currentTimeMillis;
                    return;
                }
                long time = currentTimeMillis - lastTime;
                lastTime = currentTimeMillis;
                longArrayList.add(time);
                broadcastIntervalAdapter.notifyItemChanged(longArrayList.size());
            }
        }

        @Override
        public void onScanFindOneNewDevice(int index, @Nullable BleDevice bleDevice, @NonNull ArrayList<BleDevice> bleDevices) {

        }

        @Override
        public void onScanComplete() {

        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {

        }

        @Override
        public void onScanFailed(int errorCode) {

        }
    };

    private ArrayList<Long> longArrayList = new ArrayList<>();

    private BroadcastIntervalAdapter broadcastIntervalAdapter = new BroadcastIntervalAdapter(longArrayList);

    private DefaultItemDecoration defaultItemDecoration = DefaultItemDecoration.getDefaultItemDecoration(Color.GRAY, DefaultItemDecoration.ORIENTATION_VERTICAL);

    /**
     * 广播间隔时间
     */
    private RecyclerView broadcastIntervalRecyclerView;
    /**
     * 被测设备地址
     */
    private String deviceAddress;

    /**
     * BLE 扫描工具
     */
    private BleScanner bleScanner;

    private long lastTime;
    private long startTime;
    private long counts;

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
        getDeviceAddress();
    }

    /**
     * 设置布局
     *
     * @return 布局id
     */
    @Override
    protected int setLayout() {
        return R.layout.activity_broadcast_interval_test;
    }

    /**
     * 在设置布局之后，进行其他操作之前，所需要初始化的数据
     */
    @Override
    protected void doBeforeInitOthers() {
        initBleScanner();
    }

    /**
     * 初始化布局控件
     */
    @Override
    protected void initViews() {
        broadcastIntervalRecyclerView = findViewById(R.id.time_interval_list);
        timeTv = findViewById(R.id.time_tv);
        countsTv = findViewById(R.id.counts_tv);
        rssiTv = findViewById(R.id.rssi_tv);
    }

    /**
     * 初始化控件数据
     */
    @Override
    protected void initViewData() {
        initRecyclerViewData();
        countsTv.setText(String.valueOf(counts));
        timeTv.setText(String.valueOf(0));
        rssiTv.setText(String.valueOf(0));
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
        bleScanner.startScan();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                startTime++;
                BleManager.getHANDLER().post(new Runnable() {
                    @Override
                    public void run() {
                        timeTv.setText(String.format(Locale.getDefault(), "%d s", startTime));
                        setRssi();
                    }
                });
            }
        };
        scheduledExecutorService.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * 设置菜单
     *
     * @param menu 菜单
     * @return 只是重写 public boolean onCreateOptionsMenu(Menu menu)
     */
    @Override
    protected boolean createOptionsMenu(Menu menu) {
        return false;
    }

    /**
     * 设置菜单监听
     *
     * @param item 菜单的item
     * @return true表示处理了监听事件
     */
    @Override
    protected boolean optionsItemSelected(MenuItem item) {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bleScanner.stopScan();
        bleScanner.close();
        scheduledExecutorService.shutdownNow();
        scheduledExecutorService = null;
        bleScanner = null;
        onBleScanStateChangedListener = null;
        broadcastIntervalRecyclerView = null;
        deviceAddress = null;
        rssiTv = null;
        rssis.clear();
        rssis = null;
        lastTime = 0;
    }

    private void getDeviceAddress() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        deviceAddress = intent.getStringExtra(Constants.DEVICE_ADDRESS);

    }

    private void initBleScanner() {
        bleScanner = BleManager.newBleScanner();
        if (bleScanner == null) {
            return;
        }
        bleScanner.init();
        bleScanner.setAutoStartNextScan(true);
        bleScanner.setOnBleScanStateChangedListener(onBleScanStateChangedListener);
    }

    private void initRecyclerViewData() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        broadcastIntervalRecyclerView.setLayoutManager(linearLayoutManager);
        broadcastIntervalRecyclerView.addItemDecoration(defaultItemDecoration);
        broadcastIntervalAdapter.bindToRecyclerView(broadcastIntervalRecyclerView);
        broadcastIntervalAdapter.setEmptyView(R.layout.scanning);
    }

    private void setRssi() {
        int rssiAverage = getRssiAverage();
        rssiTv.setText(String.format(Locale.getDefault(),"rssi:%d", rssiAverage));
    }

    private int getRssiAverage() {
        double sun = 0;
        for (int i = 0; i < rssis.size(); i++) {
            Integer integer = rssis.get(i);
            sun += integer;
        }
        double v = sun / rssis.size();
        return (int) (v - 0.5);
    }
}
