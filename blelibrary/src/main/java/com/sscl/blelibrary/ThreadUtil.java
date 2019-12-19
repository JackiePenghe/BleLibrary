package com.sscl.blelibrary;

/**
 * Thread operation tool class
 *
 * @author jackie
 */
class ThreadUtil {

    /**
     * Use an infinite loop to delay the current thread for a period of time to execute the next instruction
     *
     * @param sleepTime delay
     */
    static void sleep(int sleepTime) {
        long currentTimeMillis = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() - currentTimeMillis >= sleepTime) {
                break;
            }
        }
    }
}
