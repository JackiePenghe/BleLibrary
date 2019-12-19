package com.sscl.blelibrary;

import android.os.Build;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.lang.ref.WeakReference;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * BLE Advertiser timer
 *
 * @author jackie
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
final class AdvertiserTimer {

    /*-----------------------------------field variables-----------------------------------*/

    /**
     * can schedule commands to run after a given after the given delay, or to execute periodically.
     */
    private ScheduledExecutorService scheduledExecutorService;
    /**
     * Task to perform
     */
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            BleAdvertiser bleAdvertiser = bleAdvertiserWeakReference.get();
            if (bleAdvertiser == null) {
                return;
            }
            bleAdvertiser.stopAdvertising();
        }
    };
    /**
     * BleAdvertiser weak reference
     */
    private WeakReference<BleAdvertiser> bleAdvertiserWeakReference;

    /*-----------------------------------Constructor-----------------------------------*/

    /**
     * Constructor
     *
     * @param bleAdvertiser BLE broadcast util class
     */
    AdvertiserTimer(@NonNull BleAdvertiser bleAdvertiser) {
        bleAdvertiserWeakReference = new WeakReference<>(bleAdvertiser);
    }

    /*------------------------package private methods----------------------------*/

    /**
     * start timer
     *
     * @param delayTime Delayed time
     */
    void startTimer(@IntRange(from = 0) long delayTime) {
        scheduledExecutorService = BleManager.newScheduledExecutorService();
        scheduledExecutorService.schedule(runnable, delayTime, TimeUnit.MILLISECONDS);
    }

    /**
     * stop timer
     */
    void stopTimer() {
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdownNow();
            scheduledExecutorService = null;
        }
    }
}