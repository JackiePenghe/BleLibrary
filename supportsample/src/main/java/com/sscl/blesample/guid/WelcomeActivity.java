package com.sscl.blesample.guid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;


import com.sscl.baselibrary.activity.BaseWelcomeActivity;
import com.sscl.baselibrary.utils.CrashHandler;
import com.sscl.baselibrary.utils.ToastUtil;
import com.sscl.blesample.R;
import com.sscl.blesample.main.MainActivity;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.List;


/**
 * @author jacke
 */
public class WelcomeActivity extends BaseWelcomeActivity {

    /*-----------------------成员变量-----------------------*/
    /**
     * 进入设置界面的权限请求码
     */
    private static final int REQUEST_CODE_SETTING = 2;
    private Action<List<String>> onGrantedListener = new Action<List<String>>() {
        @Override
        public void onAction(List<String> data) {
            toNext();
        }
    };
    private Action<List<String>> onDeniedListener = new Action<List<String>>() {
        @Override
        public void onAction(List<String> deniedPermissions) {
            if (!AndPermission.hasAlwaysDeniedPermission(WelcomeActivity.this, deniedPermissions)) {
                ToastUtil.toastL(WelcomeActivity.this, R.string.no_permission_exits);
                finish();
            } else {
                List<String> strings = Permission.transformText(WelcomeActivity.this, deniedPermissions);
                String permissionText = TextUtils.join(",\n", strings);
                new AlertDialog.Builder(WelcomeActivity.this)
                        .setTitle(R.string.no_permission)
                        .setMessage(permissionText)
                        .setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AndPermission.with(WelcomeActivity.this)
                                        .runtime()
                                        .setting()
                                        .start(REQUEST_CODE_SETTING);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        }
    };
    private Rationale<List<String>> rationaleListener = new Rationale<List<String>>() {
        @Override
        public void showRationale(Context context, List<String> data, RequestExecutor executor) {
            List<String> strings = Permission.transformText(WelcomeActivity.this, data);
            String permissionText = TextUtils.join(",\n", strings);
            new AlertDialog.Builder(WelcomeActivity.this)
                    .setTitle(R.string.no_permission)
                    .setMessage(permissionText)
                    .setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AndPermission.with(WelcomeActivity.this)
                                    .runtime()
                                    .setting()
                                    .start(REQUEST_CODE_SETTING);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    };

    /*-----------------------实现父类函数-----------------------*/

    @Override
    protected void doAfterAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationManagerCompat manager = NotificationManagerCompat.from(WelcomeActivity.this.getApplicationContext());
            boolean isOpened = manager.areNotificationsEnabled();
            if (!isOpened) {
                ToastUtil.toastL(WelcomeActivity.this, R.string.no_notification_permission);
                //去打开通知权限
                showOpenNotificationPermissionDialog();
                return;
            }
        }
        requestPermission();
    }

    /**
     * 设置ImageView的图片资源
     *
     * @return 图片资源ID
     */
    @Override
    protected int setImageViewSource() {
        return 0;
    }

    /*-----------------------重写父类函数-----------------------*/

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode 请求码
     * @param resultCode  返回码
     * @param data        返回的数据集
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_SETTING:
                requestPermission();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    /*-----------------------自定义函数-----------------------*/

    /**
     * 开始请求权限
     */
    private void requestPermission() {
        AndPermission.with(this)
                .runtime()
                .permission(Permission.Group.LOCATION, Permission.Group.STORAGE)
                .onGranted(onGrantedListener)
                .onDenied(onDeniedListener)
                .rationale(rationaleListener)
                .start();
    }

    /**
     * 进入主界面
     */
    private void toNext() {
        //权限完全请求并获取完毕后，初始化全局异常捕获类
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this.getApplicationContext());
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 跳转到设置中，让用户打开通知权限
     */
    private void showOpenNotificationPermissionDialog() {
        new AlertDialog.Builder(WelcomeActivity.this)
                .setTitle(R.string.no_notification_permission_title)
                .setMessage(R.string.no_notification_permission_message)
                .setPositiveButton(R.string.allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getApplication().getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestPermission();
                    }
                })
                .setCancelable(false)
                .show();

    }
}
