package com.sscl.blelibrary.interfaces;

import androidx.annotation.NonNull;

/**
 * Callback during large data transmission
 *
 * @author jackie
 */
public interface OnLargeDataSendStateChangedListener {
    /**
     * Transmission start
     */
    void sendStarted();

    /**
     * Send progress changed
     *
     * @param currentPackageIndex The number of packets currently sent successfully
     * @param pageCount           Total number of packages
     * @param data                Data content
     */
    void packageSendProgressChanged(int currentPackageIndex, int pageCount, @NonNull byte[] data);

    /**
     * This packet data failed to be sent and is trying resent
     *
     * @param currentPackageIndex The number of packets that have failed to be sent currently
     * @param pageCount           Total number of packages
     * @param tryCount            Try count
     * @param data                Data content
     */
    void packageSendFailedAndRetry(int currentPackageIndex, int pageCount, int tryCount, @NonNull byte[] data);

    /**
     * Callback for data transmission timeout
     *
     * @param currentPackageIndex The number of packets that are currently timed out
     * @param pageCount           Total number of packages
     * @param data                Data content
     */
    void onSendTimeOut(int currentPackageIndex, int pageCount, @NonNull byte[] data);

    /**
     * Data transmission timeout retransmission data
     *
     * @param tryCount            Try count
     * @param currentPackageIndex The number of packets that need to resent
     * @param pageCount           Total number of packages
     * @param data                Data content
     */
    void onSendTimeOutAndRetry(int tryCount, int currentPackageIndex, int pageCount, @NonNull byte[] data);

    /**
     * Data transmission failed.If this method is triggered, it proves that this transmission has been terminated.
     *
     * @param currentPackageIndex The number of packets that have failed to be sent currently
     * @param pageCount           Total number of packages
     * @param data                Data content
     */
    void packageSendFailed(int currentPackageIndex, int pageCount, @NonNull byte[] data);

    /**
     * Transmission finished
     */
    void sendFinished();

    /**
     * start failed
     */
    void onStartFailed();
}
