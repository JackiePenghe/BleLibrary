package com.sscl.blesample.main;

import android.content.Intent;
import android.os.Build;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.sscl.baselibrary.activity.BaseAppCompatActivity;
import com.sscl.baselibrary.utils.ToastUtil;
import com.sscl.blelibrary.BleManager;
import com.sscl.blesample.R;
import com.sscl.blesample.activity.bleadvertisement.BleAdvertiseActivity;
import com.sscl.blesample.activity.bleconnect.DeviceListActivity;
import com.sscl.blesample.activity.blemulticonnect.MultiConnectDeviceListActivity;


/**
 * @author alm
 */
public class MainActivity extends BaseAppCompatActivity {

    private Button simpleUseBtn;
    private Button multiConnectBtn;
    private Button bleBroadCastBtn;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.simple_use_button:
                    toDeviceListActivity();
                    break;
                case R.id.multi_connect_button:
                    toMultiConnectActivity();
                    break;
                case R.id.ble_broadcast_button:
                    toBroadcastActivity();
                    break;
                default:
                    break;
            }
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
    }

    /**
     * 设置布局
     *
     * @return 布局id
     */
    @Override
    protected int setLayout() {
        return R.layout.activity_main;
    }

    /**
     * 在设置布局之后，进行其他操作之前，所需要初始化的数据
     */
    @Override
    protected void doBeforeInitOthers() {
        hideTitleBackButton();
        setTitleText(R.string.app_name);
    }

    /**
     * 初始化布局控件
     */
    @Override
    protected void initViews() {
        simpleUseBtn = findViewById(R.id.simple_use_button);
        multiConnectBtn = findViewById(R.id.multi_connect_button);
        bleBroadCastBtn = findViewById(R.id.ble_broadcast_button);
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
        simpleUseBtn.setOnClickListener(onClickListener);
        multiConnectBtn.setOnClickListener(onClickListener);
        bleBroadCastBtn.setOnClickListener(onClickListener);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        simpleUseBtn.setOnClickListener(null);
        simpleUseBtn = null;
        BleManager.releaseAll();
    }

    /**
     * 跳转到设备搜索界面
     */
    private void toDeviceListActivity() {

        if (!BleManager.isSupportBle()) {
            ToastUtil.toastLong(this, R.string.ble_not_supported);
            return;
        }
        Intent intent = new Intent(MainActivity.this, DeviceListActivity.class);
        startActivity(intent);
    }

    /**
     * 跳转到多连接界面
     */
    private void toMultiConnectActivity() {

        if (!BleManager.isSupportBle()) {
            ToastUtil.toastLong(this, R.string.ble_not_supported);
            return;
        }
        Intent intent = new Intent(MainActivity.this, MultiConnectDeviceListActivity.class);
        startActivity(intent);
    }

    /**
     * 跳转到BLE蓝牙广播界面
     */
    private void toBroadcastActivity() {
        if (!BleManager.isSupportBle()) {
            ToastUtil.toastLong(this, R.string.ble_not_supported);
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ToastUtil.toastLong(this, R.string.android_version_too_low);
            return;
        }

        Intent intent = new Intent(MainActivity.this, BleAdvertiseActivity.class);
        startActivity(intent);
    }
}
