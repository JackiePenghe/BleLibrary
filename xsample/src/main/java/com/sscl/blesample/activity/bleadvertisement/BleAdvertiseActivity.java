package com.sscl.blesample.activity.bleadvertisement;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.os.Build;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.sscl.baselibrary.activity.BaseAppCompatActivity;
import com.sscl.baselibrary.utils.DebugUtil;
import com.sscl.baselibrary.utils.ToastUtil;
import com.sscl.blelibrary.AdvertiseData;
import com.sscl.blelibrary.BleAdvertiser;
import com.sscl.blelibrary.BleManager;
import com.sscl.blelibrary.enums.BleAdvertiseMode;
import com.sscl.blesample.R;
import com.sscl.blesample.callback.DefaultOnBleAdvertiseStateChangedListener;
import com.sscl.blesample.callback.DefaultOnConnectedByOtherDevicesListener;

import java.util.UUID;

/**
 * @author jacke
 */
public class BleAdvertiseActivity extends BaseAppCompatActivity {

    private static final String TAG = "BleAdvertiseActivity";

    /*--------------------成员变量--------------------*/

    /**
     * BLE广播实例
     */
    private BleAdvertiser bleAdvertiser;
    /**
     * 显示广播开启状态的文本
     */
    private TextView broadcastStatusTv;

    private DefaultOnBleAdvertiseStateChangedListener defaultOnBleAdvertiseStateChangedListener = new DefaultOnBleAdvertiseStateChangedListener() {
        /**
         * Callback triggered in response to {@link BluetoothLeAdvertiser#startAdvertising} indicating
         * that the advertising has been started successfully.
         *
         * @param settingsInEffect The actual settings used for advertising, which may be different from
         *                         what has been requested.
         */
        @Override
        public void onBroadCastStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onBroadCastStartSuccess(settingsInEffect);
            broadcastStatusTv.setText(R.string.open_broadcast_success);
        }

        /**
         * Callback when advertising could not be started.
         *
         * @param errorCode Error code (see ADVERTISE_FAILED_* constants) for advertising start
         *                  failures.
         */
        @Override
        public void onBroadCastStartFailure(int errorCode) {
            super.onBroadCastStartFailure(errorCode);
            broadcastStatusTv.setText(R.string.open_broadcast_failed);
            DebugUtil.warnOut(TAG, "errorCode = " + errorCode);
        }

        /**
         * 如果设置了超时时间，在超时结束后，会执行此回调
         */
        @Override
        public void onBroadCastStopped() {
            super.onBroadCastStopped();
            broadcastStatusTv.setText(R.string.broadcast_stopped);
        }
    };

    private DefaultOnConnectedByOtherDevicesListener defaultOnBluetoothGattServerCallbackListener = new DefaultOnConnectedByOtherDevicesListener();

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bleAdvertiser = BleManager.getBleAdvertiserInstance();
            if (bleAdvertiser == null) {
                return;
            }
            byte[] bytes = new byte[4];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = (byte) 0xFF;
            }
            AdvertiseData advertiseData = new AdvertiseData(0x0000FFFF, bytes);
            bleAdvertiser.addAdvertiseDataAdvertiseRecord(advertiseData);
            bleAdvertiser.setAdvertiseDataIncludeDeviceName(true);
            bleAdvertiser.setScanResponseIncludeDeviceName(false);
            bleAdvertiser.setAdvertiseDataIncludeTxPowerLevel(true);
            bleAdvertiser.setBleAdvertiseMode(BleAdvertiseMode.LOW_LATENCY);
            bleAdvertiser.setConnectable(false);
            bleAdvertiser.setOnBleAdvertiseStateChangedListener(defaultOnBleAdvertiseStateChangedListener);
            bleAdvertiser.setTimeOut(20000);
            //初始化
            if (!bleAdvertiser.init()) {
                DebugUtil.warnOut(TAG, "初始化失败");
            } else {
                DebugUtil.warnOut(TAG, "初始化成功");
                bleAdvertiser.setOnBluetoothGattServerCallbackListener(defaultOnBluetoothGattServerCallbackListener);
                BluetoothGattServer bluetoothGattServer = bleAdvertiser.getBluetoothGattServer();
                if (bluetoothGattServer != null) {
                    BluetoothGattService bluetoothGattService1 = new BluetoothGattService(UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb"), BluetoothGattService.SERVICE_TYPE_PRIMARY);
                    BluetoothGattCharacteristic bluetoothGattCharacteristic = new BluetoothGattCharacteristic(UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb"), BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_NOTIFY, BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE);
                    bluetoothGattService1.addCharacteristic(bluetoothGattCharacteristic);
                    bluetoothGattServer.addService(bluetoothGattService1);
                }
            }
        } else {
            ToastUtil.toastL(BleAdvertiseActivity.this, "系统版本过低，不支持蓝牙广播");
        }
    }


    /**
     * 设置布局
     *
     * @return 布局id
     */
    @Override
    protected int setLayout() {
        return R.layout.activity_ble_broadcast;
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
        broadcastStatusTv = findViewById(R.id.broad_cast_status_tv);
    }

    /**
     * 初始化控件数据
     */
    @Override
    protected void initViewData() {

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
        if (bleAdvertiser != null) {
            boolean b = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                b = bleAdvertiser.startAdvertising();
            }
            if (b) {
                DebugUtil.warnOut(TAG, "广播请求发起成功（是否真的成功，在init的advertiseCallback回调中查看）");
            } else {
                DebugUtil.warnOut(TAG, "广播请求发起失败（这是真的失败了，连请求都没有发起成功）");
            }
            DebugUtil.warnOut(TAG, "startAdvertising = " + b);
        }
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

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (bleAdvertiser != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (bleAdvertiser.isAdvertising()) {
                    bleAdvertiser.stopAdvertising();
                }
                bleAdvertiser.close();
                bleAdvertiser = null;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BleManager.releaseBleAdvertiserInstance();
        }
    }
}
