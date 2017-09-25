package com.zmm.bledeviceconnect.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.view.View;

import com.zmm.bledeviceconnect.application.SGBApplication;

/**
 * Description:
 * Author:zhangmengmeng
 * Date:2017/3/17
 * Time:下午1:32
 */

public class UIUtils {

    /**
     * @return	全局的上下文环境
     */
    public static Context getContext(){
        return SGBApplication.getContext();
    }

    /**
     * @return	全局的hander对象
     */
    public static Handler getHandler(){
        return SGBApplication.getHandler();
    }

    /**
     * @return	返回主线程方法
     */
    public static Thread getMainThread(){
        return SGBApplication.getMainThread();
    }

    /**
     * @return	返回主线程id方法
     */
    public static int getMainThreadId(){
        return SGBApplication.getMainThreadId();
    }

    /**
     * 布局文件转换成view对象方法
     * @param layoutId	布局文件id
     * @return	布局文件转换成的view对象
     */
    public static View inflate(int layoutId){
        return View.inflate(getContext(), layoutId, null);
    }

    /**
     * @return	返回资源文件夹对象方法
     */
    public static Resources getResources(){
        return getContext().getResources();
    }

    /**
     * @param stringId	字符串在xml中对应R文件中的id
     * @return	string.xml某节点,对应的值
     */
    public static String getString(int stringId){
        return getResources().getString(stringId);
    }

    /**
     * @param runnable	将任务保证在主线程中运行的方法
     */
    public static void runInMainThread(Runnable runnable){
        if(android.os.Process.myTid() == getMainThreadId()){
            runnable.run();
        }else{
            getHandler().post(runnable);
        }
    }

    /**
     * 执行延迟任务的操作
     * @param runnableTask 延时任务
     * @param delayTime	   延时时间
     */
    public static void postDelayed(Runnable runnableTask, int delayTime) {
        getHandler().postDelayed(runnableTask,delayTime);

    }

}
