package com.sscl.blelibrary;

import com.sscl.blelibrary.interfaces.OnBleScanStateChangedListener;

import java.lang.ref.WeakReference;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 扫描的定时器
 *
 * @author jackie
 */

final class ScanTimer {

    /*-----------------------------------field variables-----------------------------------*/
    /**
     * ScheduledExecutorService
     */
    private ScheduledExecutorService scheduledExecutorService;
    /**
     * task for execute
     */
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            final BleScanner bleScanner = bleScannerWeakReference.get();
            if (bleScanner == null) {
                return;
            }

            if (!bleScanner.isAutoStartNextScan()) {
                bleScanner.stopScan();
                BleManager.getHANDLER().post(new Runnable() {
                    @Override
                    public void run() {
                        if (onBleScanStateChangedListener != null) {
                            onBleScanStateChangedListener.onScanComplete();
                        }
                    }
                });
            }
        }
    };
    /**
     * BleScanner weak reference
     */
    private WeakReference<BleScanner> bleScannerWeakReference;
    /**
     * BLE scan result changed listener
     */
    @Nullable
    private OnBleScanStateChangedListener onBleScanStateChangedListener;

    /*-----------------------------------Constructor-----------------------------------*/

    /**
     * Constructor
     *
     * @param bleScanner BleScanner
     */
    ScanTimer(@NonNull BleScanner bleScanner) {
        bleScannerWeakReference = new WeakReference<>(bleScanner);
    }

    /*-----------------------------------package private method-----------------------------------*/

    /**
     * start timer
     *
     * @param delayTime delay time
     */
    void startTimer(long delayTime) {

        scheduledExecutorService = new ScheduledThreadPoolExecutor(1, BleManager.getThreadFactory());
        scheduledExecutorService.schedule(runnable, delayTime, TimeUnit.MILLISECONDS);
    }

    /**
     * stop timer
     */
    void stopTimer() {
        scheduledExecutorService.shutdownNow();
        scheduledExecutorService = null;
        BleScanner bleScanner = bleScannerWeakReference.get();
        bleScanner.setScanningFalse();
    }

    /**
     * set  BLE scan result changed listener
     *
     * @param onBleScanStateChangedListener BLE scan result changed listener
     */
    void setOnBleScanStateChangedListener(@Nullable OnBleScanStateChangedListener onBleScanStateChangedListener) {
        this.onBleScanStateChangedListener = onBleScanStateChangedListener;
    }
}