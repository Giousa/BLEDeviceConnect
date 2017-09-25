package com.zmm.bledeviceconnect.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description:
 * Author:zhangmengmeng
 * Date:2017/5/8
 * Time:下午3:12
 */

public class ThreadUtils {

    private static ExecutorService sExecutor = Executors.newCachedThreadPool();

    public static void runOnBackgroundThread(Runnable runnable) {
        sExecutor.execute(runnable);
    }
}
