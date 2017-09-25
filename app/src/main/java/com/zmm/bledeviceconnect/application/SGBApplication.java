package com.zmm.bledeviceconnect.application;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.zmm.bledeviceconnect.manager.GreenDaoManager;

/**
 * Description:
 * Author:zhangmengmeng
 * Date:2017/3/20
 * Time:上午9:42
 */

public class SGBApplication extends Application {
    //全局上下文环境
    private static Context mContext;
    //全局的handler
    private static Handler mHandler;
    //主线程
    private static Thread mMainThread;
    //主线程id
    private static int mMainThreadId;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

        mHandler = new Handler();

        mMainThread = Thread.currentThread();

        mMainThreadId = android.os.Process.myTid();

        GreenDaoManager.getInstance();
    }

    public static Context getContext() {
        return mContext;
    }

    public static Handler getHandler() {
        return mHandler;
    }

    public static Thread getMainThread() {
        return mMainThread;
    }

    public static int getMainThreadId() {
        return mMainThreadId;
    }

}
