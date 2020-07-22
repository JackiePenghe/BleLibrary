package com.sscl.blelibrary;

import androidx.annotation.NonNull;

import com.sscl.baselibrary.utils.ConversionUtil;
import com.sscl.blelibrary.interfaces.OnLargeDataSendStateChangedListener;

/**
 * default callback during large data transmission
 *
 * @author jackie
 */
final class DefaultLargeDataSendStateChangedListener implements OnLargeDataSendStateChangedListener {

    /*-------------------------静态常量-------------------------*/

    private static final String TAG = DefaultLargeDataSendStateChangedListener.class.getSimpleName();

    /*-------------------------重写父类方法-------------------------*/

    /**
     * 传输开始
     */
    @Override
    public void sendStarted() {
        DebugUtil.warnOut(TAG, "sendStarted");
    }

    /**
     * 传输完成
     */
    @Override
    public void sendFinished() {
        DebugUtil.warnOut(TAG, "sendFinished");
    }

    @Override
    public void onStartFailed() {
        DebugUtil.warnOut(TAG, "onStartFailed");
    }

    /**
     * 数据发送成功
     *
     * @param currentPackageIndex 当前发送成功的包数
     * @param pageCount           总包数
     * @param data                本包发送的数据
     */
    @Override
    public void packageSendProgressChanged(int currentPackageIndex, int pageCount, @NonNull byte[] data) {
        DebugUtil.warnOut(TAG, "packageSendProgressChanged : currentPackageIndex = " + currentPackageIndex + ",pageCount = " + pageCount + ",data = " + ConversionUtil.byteArrayToHexStr(data));
    }

    /**
     * 数据发送失败
     *
     * @param currentPackageIndex 当前发送失败的包数
     * @param pageCount           总包数
     * @param data                本包发送的数据
     */
    @Override
    public void packageSendFailed(int currentPackageIndex, int pageCount, @NonNull byte[] data) {
        DebugUtil.warnOut(TAG, "packageSendFailed : currentPackageIndex = " + currentPackageIndex + ",pageCount = " + pageCount + ",data = " + ConversionUtil.byteArrayToHexStr(data));
    }

    /**
     * 本包数据发送失败，正在重新发送
     *
     * @param currentPackageIndex 当前发送失败的包数
     * @param pageCount           总包数
     * @param tryCount            尝试次数
     * @param data                本包发送的数据
     */
    @Override
    public void packageSendFailedAndRetry(int currentPackageIndex, int pageCount, int tryCount, @NonNull byte[] data) {
        DebugUtil.warnOut(TAG, "packageSendFailedAndRetry : currentPackageIndex = " + currentPackageIndex + ",pageCount = " + pageCount + ",tryCount = " + tryCount + ",data = " + ConversionUtil.byteArrayToHexStr(data));
    }

    /**
     * 数据发送超时进行的回调
     *
     * @param currentPackageIndex 当前发送超时的包数
     * @param pageCount           总包数
     * @param data                发送超时的数据
     */
    @Override
    public void onSendTimeOut(int currentPackageIndex, int pageCount, @NonNull byte[] data) {
        DebugUtil.warnOut(TAG, "onSendTimeOut");
    }

    /**
     * 数据发送超时,尝试重发数据时进行的回调
     *
     * @param tryCount            重发次数
     * @param currentPackageIndex 当前重发的包数
     * @param pageCount           总包数
     * @param data                重发的数据内容
     */
    @Override
    public void onSendTimeOutAndRetry(int tryCount, int currentPackageIndex, int pageCount, @NonNull byte[] data) {
        DebugUtil.warnOut(TAG, "onSendTimeOutAndRetry : currentPackageIndex = " + currentPackageIndex + ",pageCount = " + pageCount + ",tryCount = " + tryCount + ",data = " + ConversionUtil.byteArrayToHexStr(data));
    }
}
