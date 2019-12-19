package com.sscl.blelibrary.interfaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Callback that write large data and require remote devices to notify collaboration
 *
 * @author jackie
 */
public interface OnLargeDataWriteWithNotificationSendStateChangedListener {
    /**
     * Transmission start
     */
    void onDataSendStart();

    /**
     * Callback when receiving notification from the remote device
     *
     * @param currentPackageData  Data content
     * @param currentPackageIndex Current index of packages
     * @param packageCount        Total number of packages
     * @param values              Notification data from remote device
     * @return True means received data correct,and the next packet of data will be sent
     */
    boolean onReceiveNotification(byte[] currentPackageData, int currentPackageIndex, int packageCount, @Nullable byte[] values);

    /**
     * Data failed to send and attempt to resend
     *
     * @param currentPackageIndex Current index of packages
     * @param pageCount           Total number of packages
     * @param data                Data content
     * @param tryCount            Try count
     */
    void onDataSendFailedAndRetry(int currentPackageIndex, int pageCount, @NonNull byte[] data, int tryCount);

    /**
     * Data transmission progress has changed
     *
     * @param currentPackageIndex Current index of packages
     * @param pageCount           Total number of packages
     * @param data                Data content
     */
    void onDataSendProgressChanged(int currentPackageIndex, int pageCount, @NonNull byte[] data);

    /**
     * data send failed
     *
     * @param tryCount            Try count
     * @param currentPackageIndex Current index of packages
     * @param packageCount        Total number of packages
     * @param data                Data content
     */
    void onSendFailedWithWrongNotifyDataAndRetry(int tryCount, int currentPackageIndex, int packageCount, @Nullable byte[] data);

    /**
     * data send timeout
     *
     * @param currentPackageIndex current index
     * @param packageCount        total packet count
     * @param data                data
     */
    void onDataSendTimeOut(int currentPackageIndex, int packageCount, @NonNull byte[] data);

    /**
     * 通知回复超时时，进行重发尝试时的回调
     *
     * @param data                Data content
     * @param tryCount            Try count
     * @param currentPackageIndex Current index of packages
     * @param packageCount        Total number of packages
     */
    void onDataSendTimeOutAndRetry(@NonNull byte[] data, int tryCount, int currentPackageIndex, int packageCount);

    /**
     * Callback triggered while {@link OnLargeDataWriteWithNotificationSendStateChangedListener#onReceiveNotification(byte[], int, int, byte[])}
     * return false and resend count more than the number of attempts.If this method is triggered,it proves that this transmission has been terminated.
     */
    void onSendFailedWithWrongNotifyData();

    /**
     * data sent failed.If this method is triggered, it proves that this transmission has been terminated.
     *
     * @param currentPackageIndex Current index of packages
     * @param pageCount           Total number of packages
     * @param data                Data content
     */
    void onDataSendFailed(int currentPackageIndex, int pageCount, byte[] data);

    /**
     * transmission finished
     */
    void onDataSendFinished();

    /**
     * start send failed
     */
    void onStartFailed();
}
