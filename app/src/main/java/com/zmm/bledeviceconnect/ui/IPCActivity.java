package com.zmm.bledeviceconnect.ui;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zmm.bledeviceconnect.R;
import com.zmm.bledeviceconnect.adapter.BLERecyclerViewAdapter;
import com.zmm.bledeviceconnect.gen.BleDeviceDao;
import com.zmm.bledeviceconnect.manager.GreenDaoManager;
import com.zmm.bledeviceconnect.model.BleDevice;
import com.zmm.bledeviceconnect.service.BluetoothLeService;
import com.zmm.bledeviceconnect.utils.ThreadUtils;
import com.zmm.bledeviceconnect.utils.ToastUtils;
import com.zmm.bledeviceconnect.utils.UIUtils;
import com.zmm.bledeviceconnect.utils.conf.CommonConfig;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Description:
 * Author:zhangmengmeng
 * Date:2017/9/25
 * Time:上午10:02
 */

public class IPCActivity extends AppCompatActivity {
    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @InjectView(R.id.tv_state)
    TextView mTvState;
    @InjectView(R.id.tv_lock)
    TextView mTvLock;
    @InjectView(R.id.tv_type)
    TextView mTvType;
    @InjectView(R.id.tv_model)
    TextView mTvModel;
    @InjectView(R.id.tv_time)
    TextView mTvTime;
    @InjectView(R.id.tv_power)
    TextView mTvPower;
    @InjectView(R.id.tv_power2)
    TextView mTvPower2;
    @InjectView(R.id.et_model)
    EditText mEtModel;
    @InjectView(R.id.et_time)
    EditText mEtTime;
    @InjectView(R.id.et_power)
    EditText mEtPower;
    @InjectView(R.id.tv_content)
    TextView mTvContent;

    @InjectView(R.id.cb_box1)
    CheckBox mCbBox1;
    @InjectView(R.id.cb_box2)
    CheckBox mCbBox2;
    @InjectView(R.id.cb_box3)
    CheckBox mCbBox3;
    @InjectView(R.id.cb_box4)
    CheckBox mCbBox4;

    //开始数据
    //AA 0A 07 11 09 09 DD 11 01 9E FB EE
    byte[] startByte = new byte[]{(byte) 0xAA, (byte) 0x0A, (byte) 0x07, (byte) 0x11, (byte) 0x09,
            (byte) 0x09, (byte) 0xDD, (byte) 0x11, (byte) 0x01, (byte) 0x9E, (byte) 0xFB, (byte) 0xEE};

    //停止数据
    //AA 0A 07 11 09 09 DD 11 00 5E 3A EE
    byte[] stopByte = new byte[]{(byte) 0xAA, (byte) 0x0A, (byte) 0x07, (byte) 0x11, (byte) 0x09,
            (byte) 0x09, (byte) 0xDD, (byte) 0x11, (byte) 0x00, (byte) 0x5E, (byte) 0x3A, (byte) 0xEE};

    //设置数据
    //AA 0F 07 11 09 09 DD 13 06 1E 00 00 64 00 3C BB EE
//    byte[] setByte = new byte[]{(byte) 0xAA, (byte) 0x0F, (byte) 0x07, (byte) 0x11, (byte) 0x09,
//            (byte) 0x09, (byte) 0xDD, (byte) 0x13, (byte) 0x06, (byte) 0x1E, (byte) 0x00,
//            (byte) 0x00, (byte) 0x64, (byte) 0x00, (byte) 0x3C, (byte) 0xBB, (byte) 0xEE};

    //查询数据
    //AA 09 07 11 09 09 DD 10 BA 25 EE
    byte[] queryByte = new byte[]{(byte) 0xAA, (byte) 0x09, (byte) 0x07, (byte) 0x11, (byte) 0x09,
            (byte) 0x09, (byte) 0xDD, (byte) 0x10, (byte) 0xBA, (byte) 0x25, (byte) 0xEE};

    //返回查询数据
    //AA 10 07 11 09 09 DD 00 00 00 07 08 0D 55 00 BE 6E EE
    byte[] queryBackByte = new byte[]{(byte) 0xAA, (byte) 0x10, (byte) 0x07, (byte) 0x11, (byte) 0x09,
            (byte) 0x09, (byte) 0xDD, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x07,
            (byte) 0x08, (byte) 0x0D, (byte) 0x55, (byte) 0x00, (byte) 0xBE, (byte) 0x6E, (byte) 0xEE};
    //返回开始数据
    //AA 0A 07 11 09 09 DD 11 90 32 3A EE
    byte[] startBackByte = new byte[]{(byte) 0xAA, (byte) 0x0A, (byte) 0x07, (byte) 0x11, (byte) 0x09,
            (byte) 0x09, (byte) 0xDD, (byte) 0x11, (byte) 0x90, (byte) 0x32, (byte) 0x3A, (byte) 0xEE};
    //返回停止数据
    //AA 0A 07 11 09 09 DD 11 90 32 3A EE
    byte[] stopBackByte = new byte[]{(byte) 0xAA, (byte) 0x0A, (byte) 0x07, (byte) 0x11, (byte) 0x09,
            (byte) 0x09, (byte) 0xDD, (byte) 0x11, (byte) 0x90, (byte) 0x32, (byte) 0x3A, (byte) 0xEE};
    //返回设置数据
    //AA 0A 07 11 09 09 DD 13 90 52 3B EE
    byte[] setBackByte = new byte[]{(byte) 0xAA, (byte) 0x0A, (byte) 0x07, (byte) 0x11, (byte) 0x09,
            (byte) 0x09, (byte) 0xDD, (byte) 0x13, (byte) 0x90, (byte) 0x52, (byte) 0x3B, (byte) 0xEE};



    private PopupWindow mPopupWindow;
    private List<BleDevice> mBleDevices;
    private BleDeviceDao mBleDeviceDao;
    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private boolean isBind = false;
    private byte mModelByte;
    private byte mTimeByte;
    private byte mPowerByte;
    private boolean mCheckBoolean1;
    private boolean mCheckBoolean2;
    private boolean mCheckBoolean3;
    private boolean mCheckBoolean4;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).
                    getService(CommonConfig.PIC_UUID_SERVICE, CommonConfig.PIC_UUID_NOTIFY);
            if (!mBluetoothLeService.initialize()) {
                finish();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            System.out.println("BroadcastReceiver action = " + action);
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                //连接成功
                mConnected = true;
                mTvContent.setText("设备连接:成功");
                ToastUtils.SimpleToast("---连接:成功---");
            } else if (BluetoothLeService.ACTION_GATT_FAILURE.equals(action)) {
                //失败
                mConnected = false;
                mTvContent.setText("设备连接:失败");
                ToastUtils.SimpleToast("---连接:失败---");
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                //断开
                mConnected = false;
                mTvContent.setText("设备连接:断开");
                ToastUtils.SimpleToast("---连接:断开---");
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                //逻辑处理
                mConnected = true;


            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                mConnected = true;

                //收到数据
                byte[] bytes = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
//                System.out.println("接收数据：" + Arrays.toString(bytes));
//                mTvContent.setText(Arrays.toString(bytes) + "\n");
                if (Arrays.equals(startBackByte, bytes)) {
                    mTvContent.setText("<接收【启动】返回数据：" + Arrays.toString(bytes) + ">");
                }

                if (Arrays.equals(setBackByte, bytes)) {
                    mTvContent.setText("<接收【设置】返回数据：" + Arrays.toString(bytes) + ">");
                }

                if (Arrays.equals(stopBackByte, bytes)) {
                    mTvContent.setText("<接收【停止】返回数据：" + Arrays.toString(bytes) + ">");
                }

                if (bytes[0] == (byte) 0xAA && bytes[1] == (byte) 0x10) {
                    mTvContent.setText("<接收【查询】返回数据：" + Arrays.toString(bytes) + ">");
                    parseQueryData(bytes);
                }

            }
        }
    };

    private void parseQueryData(byte[] bytes) {
        byte state = bytes[8];
        byte lock = bytes[9];
        byte type = bytes[10];
        byte model = bytes[11];
        byte time = bytes[12];
        byte power = bytes[13];
        byte power2 = bytes[14];

        switch (state) {
            case 0x00:
                mTvState.setText("工作状态：空闲");
                break;

            case 0x01:
                mTvState.setText("工作状态：运行");
                break;

            case 0x10:
                mTvState.setText("工作状态：开始运行");
                break;

            case 0x11:
                mTvState.setText("工作状态：停止运行");
                break;

            case 0x12:
                mTvState.setText("工作状态：运行时间结束");
                break;
        }

        switch (lock) {
            case 0x00:
                mTvLock.setText("锁定状态：开锁");
                break;

            case 0x01:
                mTvLock.setText("锁定状态：锁定");
                break;
        }

        switch (type) {
            case 0x00:
                mTvType.setText("治疗仪型号：IPC1200");
                break;

            case 0x01:
                mTvType.setText("治疗仪型号：IPC600");
                break;

            case 0x02:
                mTvType.setText("治疗仪型号：IPC600A");
                break;

            case 0x03:
                mTvType.setText("治疗仪型号：IPC600D");
                break;

            case 0x04:
                mTvType.setText("治疗仪型号：IPC400");
                break;

            case 0x05:
                mTvType.setText("治疗仪型号：IPC400A");
                break;

            case 0x06:
                mTvType.setText("治疗仪型号：IPC400E");
                break;

            case 0x07:
                mTvType.setText("治疗仪型号：IPC420F");
                break;
        }

        switch (model) {

            case 0x01:
                mTvModel.setText("模式：静脉");
                break;

            case 0x02:
                mTvModel.setText("模式：水肿");
                break;

            case 0x03:
                mTvModel.setText("模式：动脉");
                break;

            case 0x04:
                mTvModel.setText("模式：按摩");
                break;

            case 0x05:
                mTvModel.setText("模式：DVT");
                break;

            case 0x06:
                mTvModel.setText("模式：A");
                break;

            case 0x07:
                mTvModel.setText("模式：B");
                break;

            case 0x08:
                mTvModel.setText("模式：A+B");
                break;

        }

        if (time < 100 && time > 0) {
            mTvTime.setText("运行时间：" + time);

        } else if (time >= 100) {
            mTvTime.setText("运行时间：持续运行");
        } else {
            mTvTime.setText("运行时间：有误");
        }

        if (power >= 0) {
            mTvPower.setText("压力：" + power + "mmHg");
        } else {
            mTvPower.setText("压力：有误");
        }

        String s = chamberPower(power2);

        mTvPower2.setText(s);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipc);
        ButterKnife.inject(this);

        mCbBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mCheckBoolean1 = b;
            }
        });

        mCbBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mCheckBoolean2 = b;

            }
        });

        mCbBox3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mCheckBoolean3 = b;

            }
        });

        mCbBox4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mCheckBoolean4 = b;

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        isBind = bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        initRecyclerView();

    }

    //---------------------------------------
    private void initRecyclerView() {
        ThreadUtils.runOnBackgroundThread(new Runnable() {
            @Override
            public void run() {
                mBleDeviceDao = GreenDaoManager.getInstance().getBleDevice();
                mBleDevices = mBleDeviceDao.loadAll();

                UIUtils.runInMainThread(new Runnable() {
                    @Override
                    public void run() {
                        LinearLayoutManager layoutManager = new LinearLayoutManager(IPCActivity.this);
                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        mRecyclerView.setLayoutManager(layoutManager);
                        BLERecyclerViewAdapter recyclerViewAdapter = new BLERecyclerViewAdapter(mBleDevices);
                        mRecyclerView.setAdapter(recyclerViewAdapter);
                        recyclerViewAdapter.setOnItemClickListener(new BLERecyclerViewAdapter.OnItemClickListener() {
                            @Override
                            public void OnClickListener(View view, int index) {
                                System.out.println("查询设备名称：" + mBleDevices.get(index).getName());
                                Long id = mBleDevices.get(index).getId();
                                popupItem(view, index, id);
                            }
                        });
                    }
                });

            }
        });
    }

    private void popupItem(View v, int index, Long id) {
        View contentView = getPopupWindowContentView(index, id);
        mPopupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//        int xOffset = v.getWidth() / 2 - contentView.getMeasuredWidth() / 2;
        int xOffset = v.getWidth() / 2;
        mPopupWindow.showAsDropDown(v, xOffset, 0);
    }

    private View getPopupWindowContentView(final int index, final Long id) {
        int layoutId = R.layout.pop_device_setting;
        View contentView = LayoutInflater.from(this).inflate(layoutId, null);
        TextView name = contentView.findViewById(R.id.pop_device_name);
        name.setText("设备名称：" + mBleDevices.get(index).getName());

        View.OnClickListener menuItemOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                    case R.id.pop_device_connect:
                        connect(index);
                        break;

                    case R.id.pop_device_delete:
                        delete(id);
                        break;

                }
                if (mPopupWindow != null) {
                    mPopupWindow.dismiss();
                }
            }
        };
        contentView.findViewById(R.id.pop_device_connect).setOnClickListener(menuItemOnClickListener);
        contentView.findViewById(R.id.pop_device_delete).setOnClickListener(menuItemOnClickListener);
        return contentView;
    }

    private void connect(int index) {
        mTvContent.setText("正在连接......");
        mBluetoothLeService.connect(mBleDevices.get(index).getAddress());
    }

    private void delete(Long id) {
        mBleDeviceDao.deleteByKey(id);
        initRecyclerView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isBind) {
            unbindService(mServiceConnection);
        }
        unregisterReceiver(mGattUpdateReceiver);
        if (mConnected) {
            mBluetoothLeService.disconnect();
            mConnected = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mBluetoothLeService != null) {
            mBluetoothLeService.close();
            mBluetoothLeService = null;
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_FAILURE);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothDevice.ACTION_UUID);
        return intentFilter;
    }


    @OnClick({R.id.btn_back, R.id.btn_add, R.id.btn_query, R.id.btn_start, R.id.btn_set, R.id.btn_stop})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_add:
                Intent intent = new Intent(this, QueryActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_query:
                if (mConnected) {
                    queryBle();
                } else {
                    ToastUtils.SimpleToast("蓝牙已断开，请重新连接！");
                }
                break;
            case R.id.btn_start:
                if (mConnected) {
                    reStartBle();
                } else {
                    ToastUtils.SimpleToast("蓝牙已断开，请重新连接！");
                }
                break;
            case R.id.btn_set:
                if (mConnected) {
                    setBle();
                } else {
                    ToastUtils.SimpleToast("蓝牙已断开，请重新连接！"+byteToCo());
                }
                break;
            case R.id.btn_stop:
                if (mConnected) {
                    stopBle();
                } else {
                    ToastUtils.SimpleToast("蓝牙已断开，请重新连接！");
                }
                break;

        }
    }

    private void queryBle() {
        mBluetoothLeService.WriteValueByte(queryByte);
        mTvContent.setText("发送【查询】数据");
    }

    private void reStartBle() {
        mBluetoothLeService.WriteValueByte(startByte);
        mTvContent.setText("发送【开始】数据");
    }

    private void setBle() {

        if (!TextUtils.isEmpty(mEtModel.getText())) {
            String modelStr = mEtModel.getText().toString().trim();
            mModelByte = Byte.parseByte(modelStr);
        } else {
            ToastUtils.SimpleToast("模式数据不能为空");
            return;
        }

        if (!TextUtils.isEmpty(mEtTime.getText())) {
            String timeStr = mEtTime.getText().toString().trim();
            mTimeByte = Byte.parseByte(timeStr);
        } else {
            ToastUtils.SimpleToast("时间数据不能为空");
            return;
        }

        if (!TextUtils.isEmpty(mEtPower.getText())) {
            String powerStr = mEtPower.getText().toString().trim();
            mPowerByte = Byte.parseByte(powerStr);
        } else {
            ToastUtils.SimpleToast("压力数据不能为空");
            return;
        }

        byte chamberByte = getChamberByte();

        final byte[] setByte = new byte[]{(byte) 0xAA, (byte) 0x0F, (byte) 0x07, (byte) 0x11, (byte) 0x09,
                (byte) 0x09, (byte) 0xDD, (byte) 0x13, mModelByte, mTimeByte, (byte) 0x00,
                (byte) 0x00, mPowerByte, chamberByte, (byte) 0x3C, (byte) 0xBB, (byte) 0xEE};

        mBluetoothLeService.WriteValueByte(setByte);
        mTvContent.setText("发送【设置】数据");
    }

    private void stopBle() {
        mBluetoothLeService.WriteValueByte(stopByte);
        mTvContent.setText("发送【停止】数据");
    }


//            0000
//            0001
//            0010
//            0011
//
//            0100
//            0101
//            0110
//            0111
//
//            1000
//            1001
//            1010
//            1011
//
//            1100
//            1101
//            1110
//            1111

    //4321
    private String chamberPower(byte b){

        String content = "";

        switch (b){
            case 0:
                content = "四:0,三:0,二:0,一:0";
                break;

            case 1:
                content = "四:0,三:0,二:0,一:1";
                break;

            case 2:
                content = "四:0,三:0,二:1,一:0";
                break;

            case 3:
                content = "四:0,三:0,二:1,一:1";
                break;

            case 4:
                content = "四:0,三:1,二:0,一:0";
                break;

            case 5:
                content = "四:0,三:1,二:0,一:1";
                break;

            case 6:
                content = "四:0,三:1,二:1,一:0";
                break;

            case 7:
                content = "四:0,三:1,二:1,一:1";
                break;

            case 8:
                content = "四:1,三:0,二:0,一:0";
                break;

            case 9:
                content = "四:1,三:0,二:0,一:1";
                break;

            case 10:
                content = "四:1,三:0,二:1,一:0";
                break;

            case 11:
                content = "四:1,三:0,二:1,一:1";
                break;

            case 12:
                content = "四:1,三:1,二:0,一:0";
                break;

            case 13:
                content = "四:1,三:1,二:0,一:1";
                break;

            case 14:
                content = "四:1,三:1,二:1,一:0";
                break;

            case 15:
                content = "四:1,三:1,二:1,一:1";
                break;
        }

        return content;
    }

    //4321
    private byte getChamberByte(){

        byte chamberByte;

        if(mCheckBoolean1){
            if(mCheckBoolean2){
                if(mCheckBoolean3){
                    if(mCheckBoolean4){
//                        chamberByte = (byte) 0x1111;
                        chamberByte = 15;
                    }else {
//                        chamberByte = (byte) 0x0111;
                        chamberByte = 7;
                    }
                }else {
                    if(mCheckBoolean4){
//                        chamberByte = (byte) 0x1011;
                        chamberByte = 11;
                    }else {
//                        chamberByte = (byte) 0x0011;
                        chamberByte = 3;
                    }
                }

            }else {
                if(mCheckBoolean3){
                    if(mCheckBoolean4){
//                        chamberByte = (byte) 0x1101;
                        chamberByte = 13;
                    }else {
//                        chamberByte = (byte) 0x0101;
                        chamberByte = 5;
                    }
                }else {
                    if(mCheckBoolean4){
//                        chamberByte = (byte) 0x1001;
                        chamberByte = 9;
                    }else {
//                        chamberByte = (byte) 0x0001;
                        chamberByte = 1;
                    }
                }
            }

        }else {

            if(mCheckBoolean2){
                if(mCheckBoolean3){
                    if(mCheckBoolean4){
//                        chamberByte = (byte) 0x1110;
                        chamberByte = 14;
                    }else {
//                        chamberByte = (byte) 0x0110;
                        chamberByte = 6;
                    }
                }else {
                    if(mCheckBoolean4){
//                        chamberByte = (byte) 0x1010;
                        chamberByte = 10;
                    }else {
//                        chamberByte = (byte) 0x0010;
                        chamberByte = 2;
                    }
                }

            }else {
                if(mCheckBoolean3){
                    if(mCheckBoolean4){
//                        chamberByte = (byte) 0x1100;
                        chamberByte = 12;
                    }else {
//                        chamberByte = (byte) 0x0100;
                        chamberByte = 4;
                    }
                }else {
                    if(mCheckBoolean4){
//                        chamberByte = (byte) 0x1000;
                        chamberByte = 8;
                    }else {
//                        chamberByte = (byte) 0x0000;
                        chamberByte = 0;
                    }
                }
            }

        }



        return chamberByte;
    }

    private char[] c = {'1','1','0','1'};

    private byte byteToCo(){

        byte b = 0;
        for (byte i = 0; i < 4; i++) {
            b += c[i]<<i;
        }

        return b;
    }
}
