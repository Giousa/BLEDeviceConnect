package com.zmm.bledeviceconnect.manager;


import com.zmm.bledeviceconnect.gen.BleDeviceDao;
import com.zmm.bledeviceconnect.gen.DaoMaster;
import com.zmm.bledeviceconnect.gen.DaoSession;
import com.zmm.bledeviceconnect.utils.UIUtils;
import com.zmm.bledeviceconnect.utils.conf.CommonConfig;

/**
 * Description:
 * Author:zhangmengmeng
 * Date:2017/3/23
 * Time:下午2:52
 */

public class GreenDaoManager {

    private static GreenDaoManager mInstance;
    private DaoSession mDaoSession;

    public static GreenDaoManager getInstance() {
        if (mInstance == null) {
            mInstance = new GreenDaoManager();
        }
        return mInstance;
    }

    public GreenDaoManager() {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(UIUtils.getContext(), CommonConfig.DATABASENAME, null);
        DaoMaster mDaoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();

    }

    public BleDeviceDao getBleDevice(){
        return mDaoSession.getBleDeviceDao();
    }

}
