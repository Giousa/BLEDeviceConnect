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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class BloodActivity extends AppCompatActivity {
    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @InjectView(R.id.tv_content)
    TextView mTvContent;

    private BluetoothLeService mBluetoothLeService;
    private BleDeviceDao mBleDeviceDao;
    private List<BleDevice> mBleDevices;
    private PopupWindow mPopupWindow;
    private boolean mConnected = false;
    private boolean isBind = false;

    byte[] startByte = new byte[]{(byte) 0xBE, (byte) 0xB0, (byte) 0x01, (byte) 0xC0, (byte) 0x36};
    byte[] stopByte = new byte[]{(byte) 0xBE, (byte) 0xB0, (byte) 0x01, (byte) 0xC1, (byte) 0x68};
    byte[] sleepByte = new byte[]{(byte) 0xBE, (byte) 0xB0, (byte) 0x01, (byte) 0xD0, (byte) 0xAB};
    //DO C2 02 00 00 2F
    byte[] successByte = new byte[]{(byte) 0xD0, (byte) 0xC2, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x2F};




    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).
                    getService(CommonConfig.BLOOD_UUID_SERVICE,CommonConfig.BLOOD_UUID_NOTIFY);
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
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                //连接成功
                mConnected = true;
                mTvContent.append("设备连接:成功\n");
                ToastUtils.SimpleToast("---连接:成功---");
            } else if (BluetoothLeService.ACTION_GATT_FAILURE.equals(action)) {
                //失败
                mConnected = false;
                mTvContent.append("设备连接:失败\n");
                ToastUtils.SimpleToast("---连接:失败---");
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                //断开
                mConnected = false;
                mTvContent.append("设备连接:断开\n");
                ToastUtils.SimpleToast("---连接:断开---");
            }else if(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)){
                //逻辑处理
                mConnected = true;


            }else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                mConnected = true;

                //收到数据
                byte[] bytes = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                System.out.println("接收数据："+ Arrays.toString(bytes));
                boolean equals = Arrays.equals(successByte, bytes);
                if(equals){
                    mTvContent.append("获取【启动】返回数据");
                }
                mTvContent.append(Arrays.toString(bytes)+"\n");
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood);
        ButterKnife.inject(this);

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
                        LinearLayoutManager layoutManager = new LinearLayoutManager(BloodActivity.this);
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
        System.out.println("选中设备："+mBleDevices.get(index).getName());

        mBluetoothLeService.connect(mBleDevices.get(index).getAddress());

    }

    private void delete(Long id) {
        mBleDeviceDao.deleteByKey(id);
        initRecyclerView();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(isBind){
            unbindService(mServiceConnection);
        }
        unregisterReceiver(mGattUpdateReceiver);
        if(mConnected) {
            mBluetoothLeService.disconnect();
            mConnected = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mBluetoothLeService != null)
        {
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

    @OnClick({R.id.tv_insert, R.id.btn_start, R.id.btn_stop, R.id.btn_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_insert:
                Intent intent = new Intent(this, QueryActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_start:
                if (mConnected) {
                    startBle();
                } else {
                    ToastUtils.SimpleToast("蓝牙已断开，请重新连接！");
                }
                break;
            case R.id.btn_stop:
                if (mConnected) {
                    stopBle();
                } else {
                    ToastUtils.SimpleToast("蓝牙已断开，请重新连接！");
                }
                break;
            case R.id.btn_back:
                finish();
                break;
        }
    }

    private void startBle() {
        mBluetoothLeService.WriteValueByte(startByte);
        mTvContent.append("发送数据:开始测量\n");

    }

    private void stopBle() {
        mBluetoothLeService.WriteValueByte(stopByte);
        mTvContent.append("发送数据:停止测量\n");

    }
}
