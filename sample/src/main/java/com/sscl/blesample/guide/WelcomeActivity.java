package com.sscl.blesample.guide;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationManagerCompat;

import com.sscl.baselibrary.activity.BaseWelcomeActivity;
import com.sscl.baselibrary.utils.CrashHandler;
import com.sscl.baselibrary.utils.ToastUtil;
import com.sscl.blesample.R;
import com.sscl.blesample.main.MainActivity;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;


/**
 * @author jacke
 */
public class WelcomeActivity extends BaseWelcomeActivity implements EasyPermissions.PermissionCallbacks {

    /*-----------------------成员变量-----------------------*/
    /**
     * 进入设置界面的权限请求码
     */
    private static final int REQUEST_CODE_SETTING = 3;
    private static final int SD_CARD_REQUEST_CODE = 1;
    private static final int LOCATION_REQUEST_CODE = 2;


    private boolean sdCardRequesting;
    private boolean locationRequesting;

    private boolean isFist = true;

    private int requestCount;

    /*-----------------------实现父类函数-----------------------*/

    @Override
    protected void doAfterAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationManagerCompat manager = NotificationManagerCompat.from(WelcomeActivity.this.getApplicationContext());
            boolean isOpened = manager.areNotificationsEnabled();
            if (!isOpened) {
                ToastUtil.toastLong(WelcomeActivity.this, R.string.no_notification_permission);
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

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        requestCount = 0;
        if (requestCode == SD_CARD_REQUEST_CODE) {
            sdCardRequesting = false;
        } else if (requestCode == LOCATION_REQUEST_CODE) {
            locationRequesting = false;
            toNext();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        requestCount++;
        AppSettingsDialog.Builder builder = new AppSettingsDialog.Builder(this)
                .setPositiveButton(R.string.allow)
                .setNegativeButton(R.string.deny);
        if (requestCode == SD_CARD_REQUEST_CODE) {
            sdCardRequesting = false;
            if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                builder.setRationale(R.string.sd_card_rationale);
            }
        } else if (requestCode == LOCATION_REQUEST_CODE) {
            locationRequesting = false;
            if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                builder.setRationale(R.string.location_rationale);
            }
        }
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog appSettingsDialog = builder.build();
            appSettingsDialog.show();
        } else {
            finish();
        }
    }

    /*-----------------------重写父类函数-----------------------*/

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (isFist) {
            isFist = false;
            return;
        }
        if (sdCardRequesting) {
            return;
        }
        if (locationRequesting) {
            return;
        }
        if (requestCount <= 1) {
            return;
        }
        requestPermission();
    }

    /*-----------------------自定义函数-----------------------*/

    /**
     * 开始请求权限
     */
    private void requestPermission() {
        boolean hasPermissions = EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasPermissions) {
            requestLocationPermission();
        } else {
            sdCardRequesting = true;
            PermissionRequest permissionRequest = new PermissionRequest.Builder(this, SD_CARD_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .setPositiveButtonText(R.string.allow)
                    .setNegativeButtonText(R.string.deny)
                    .setRationale(R.string.sd_card_rationale)
                    .build();
            EasyPermissions.requestPermissions(permissionRequest);
        }
    }

    private void requestLocationPermission() {
        boolean hasPermissions = EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (hasPermissions) {
            toNext();
        } else {
            PermissionRequest permissionRequest = new PermissionRequest.Builder(this, LOCATION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .setPositiveButtonText(R.string.allow)
                    .setNegativeButtonText(R.string.deny)
                    .setRationale(R.string.location_rationale)
                    .build();
            EasyPermissions.requestPermissions(permissionRequest);
        }
    }

    /**
     * 进入主界面
     */
    private void toNext() {
        //权限完全请求并获取完毕后，初始化全局异常捕获类
        CrashHandler.getInstance().init(getApplicationContext());
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
