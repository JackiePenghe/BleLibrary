package com.sscl.blesample.activity.bleadvertisement;

import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.DialogInterface;
import android.os.Build;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.sscl.baselibrary.activity.BaseAppCompatActivity;
import com.sscl.baselibrary.textwatcher.HexTextAutoAddEmptyCharInputWatcher;
import com.sscl.baselibrary.utils.ConversionUtil;
import com.sscl.baselibrary.utils.DebugUtil;
import com.sscl.baselibrary.utils.ToastUtil;
import com.sscl.blelibrary.AdvertiseData;
import com.sscl.blelibrary.BleAdvertiser;
import com.sscl.blelibrary.BleManager;
import com.sscl.blelibrary.enums.BleAdvertiseMode;
import com.sscl.blelibrary.enums.BleAdvertiseTxPowerLevel;
import com.sscl.blelibrary.interfaces.DefaultOnBleAdvertiseStateChangedListener;
import com.sscl.blelibrary.interfaces.DefaultOnConnectedByOtherDevicesListener;
import com.sscl.blesample.R;

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

    /**
     * 广播包的数据内容
     */
    private byte[] advertiseBytes;

    /**
     * 相应包的数据内容
     */
    private byte[] scanResponseBytes;

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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ToastUtil.toastLong(BleAdvertiseActivity.this, "系统版本过低，不支持蓝牙广播");
            onBackPressed();
        }
    }

    /**
     * 设置菜单
     *
     * @param menu 菜单
     * @return 只是重写 public boolean onCreateOptionsMenu(Menu menu)
     */
    @Override
    protected boolean createOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.activity_ble_advertise, menu);
        return true;
    }

    /**
     * 设置菜单监听
     *
     * @param item 菜单的item
     * @return true表示处理了监听事件
     */
    @Override
    protected boolean optionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.set_advertise_content:
                showSetAdvertiseContentDialog();
                break;
            case R.id.set_scan_response_content:
                showSetScanResponseContentDialog();
                break;
            case R.id.start_advertise:
                startAdvertise();
                break;
            case R.id.stop_advertise:
                stopAdvertiser();
                break;
            default:
                return false;
        }
        return true;
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


    /**
     * 显示设置广播内容的对话框
     */
    private void showSetAdvertiseContentDialog() {
        final EditText editText = (EditText) View.inflate(this, R.layout.dialog_set_advertise_content, null);
        editText.addTextChangedListener(new HexTextAutoAddEmptyCharInputWatcher(editText, 25));
        new AlertDialog.Builder(this).
                setTitle(R.string.set_advertise_content)
//                .setView(adStructureView)
                .setView(editText)
                .setCancelable(false)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            String content = editText.getText().toString();
                            if (content.isEmpty()) {
                                ToastUtil.toastLong(BleAdvertiseActivity.this, R.string.init_advertiser_failed);
                                return;
                            }
                            byte[] bytes = ConversionUtil.hexStringToByteArray(content);
                            if (bytes == null) {
                                ToastUtil.toastLong(BleAdvertiseActivity.this, R.string.advertise_data_null);
                                return;
                            }
                            if (bytes.length < 1) {
                                ToastUtil.toastLong(BleAdvertiseActivity.this, R.string.min_length);
                                return;
                            }
                            advertiseBytes = new byte[bytes.length];
                            System.arraycopy(bytes, 0, advertiseBytes, 0, advertiseBytes.length);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showSetScanResponseContentDialog() {
        final EditText editText = (EditText) View.inflate(this, R.layout.dialog_set_advertise_content, null);
        editText.addTextChangedListener(new HexTextAutoAddEmptyCharInputWatcher(editText, 25));
        new AlertDialog.Builder(this).
                setTitle(R.string.set_scan_response_content)
                .setView(editText)
                .setCancelable(false)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            String content = editText.getText().toString();
                            if (content.isEmpty()) {
                                ToastUtil.toastLong(BleAdvertiseActivity.this, R.string.init_advertiser_failed);
                                return;
                            }
                            byte[] bytes = ConversionUtil.hexStringToByteArray(content);
                            if (bytes == null) {
                                ToastUtil.toastLong(BleAdvertiseActivity.this, R.string.advertise_data_null);
                                return;
                            }
                            if (bytes.length < 1) {
                                ToastUtil.toastLong(BleAdvertiseActivity.this, R.string.min_length);
                                return;
                            }
                            scanResponseBytes = new byte[bytes.length];
                            System.arraycopy(bytes, 0, scanResponseBytes, 0, scanResponseBytes.length);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void stopAdvertiser() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (bleAdvertiser != null) {
                bleAdvertiser.stopAdvertising();
            } else {
                ToastUtil.toastLong(this, R.string.set_advertise_content_first);
            }
        }
    }

    private void startAdvertise() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (bleAdvertiser != null) {
                if (bleAdvertiser.isAdvertising()) {
                    bleAdvertiser.stopAdvertising();
                }
                BleManager.releaseBleAdvertiser(bleAdvertiser);
                bleAdvertiser = null;
            }
            bleAdvertiser = BleManager.newBleAdvertiser();
            if (bleAdvertiser == null) {
                ToastUtil.toastLong(BleAdvertiseActivity.this, R.string.init_advertiser_failed);
                return;
            }

            if (advertiseBytes == null && scanResponseBytes == null) {
                ToastUtil.toastLong(this, R.string.set_advertise_content_first);
                return;
            }

            if (advertiseBytes != null) {
                AdvertiseData advertiseData = getAdvertiseData(advertiseBytes);
                if (advertiseData != null) {
                    bleAdvertiser.addAdvertiseDataAdvertiseRecord(advertiseData);
                }
            }

            if (scanResponseBytes != null) {
                AdvertiseData advertiseData = getAdvertiseData(scanResponseBytes);
                if (advertiseData != null) {
                    bleAdvertiser.addScanResponseAdvertiseRecord(advertiseData);
                }
            }
            bleAdvertiser.setConnectable(false);
            bleAdvertiser.setAdvertiseDataIncludeDeviceName(false);
            bleAdvertiser.setAdvertiseDataIncludeTxPowerLevel(false);
            bleAdvertiser.setBleAdvertiseMode(BleAdvertiseMode.LOW_LATENCY);
            bleAdvertiser.setOnBleAdvertiseStateChangedListener(defaultOnBleAdvertiseStateChangedListener);
            bleAdvertiser.setTimeOut(0);
            bleAdvertiser.setScanResponseIncludeDeviceName(false);
            bleAdvertiser.setScanResponseIncludeTxPowerLevel(false);
            bleAdvertiser.setTxPowerLevel(BleAdvertiseTxPowerLevel.HIGH);
            boolean init = bleAdvertiser.init();
            if (!init) {
                ToastUtil.toastLong(BleAdvertiseActivity.this, R.string.init_advertiser_failed);
            }
            bleAdvertiser.startAdvertising();
        } else {
            ToastUtil.toastLong(this, R.string.set_advertise_content_first);
        }
    }

    @Nullable
    private AdvertiseData getAdvertiseData(byte[] bytes) {
        int manufacturerId;
        manufacturerId = ConversionUtil.byteArrayToInt(new byte[]{bytes[1], bytes[0]});
        byte[] data = new byte[bytes.length - 2];
        System.arraycopy(bytes, 2, data, 0, data.length);
        return new AdvertiseData(manufacturerId, data);
    }

}
