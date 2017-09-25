package com.zmm.bledeviceconnect.ui;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.zmm.bledeviceconnect.R;
import com.zmm.bledeviceconnect.adapter.BLERecyclerQueryAdapter;
import com.zmm.bledeviceconnect.gen.BleDeviceDao;
import com.zmm.bledeviceconnect.manager.GreenDaoManager;
import com.zmm.bledeviceconnect.model.BleDevice;
import com.zmm.bledeviceconnect.model.BleModel;
import com.zmm.bledeviceconnect.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Description:
 * Author:zhangmengmeng
 * Date:2017/9/20
 * Time:上午9:54
 */

public class QueryActivity extends AppCompatActivity {


    @InjectView(R.id.tv_device_name)
    TextView mTvDeviceName;
    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private String mBleName;
    private String mBleAddress;
    private static final int REQUEST_ENABLE_BT = 2;
    private List<BleModel> mBleDevicesList;
    private BLERecyclerQueryAdapter mRecyclerQueryAdapter;

    private static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;
    private static final int REQUEST_CODE_LOCATION_SETTINGS = 2;

    private BluetoothAdapter mBluetoothAdapter;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
        ButterKnife.inject(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            ToastUtils.SimpleToast("本设备不支持蓝牙！！");
            return;
        }
        initPermission();
        initRecycler();
        init();
    }

    private void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //判断是否具有权限
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //请求权限
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_CODE_ACCESS_COARSE_LOCATION);
            }
        }

        if(!isLocationEnable(this)){
            Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            this.startActivityForResult(locationIntent, REQUEST_CODE_LOCATION_SETTINGS);
        }
    }


    public static final boolean isLocationEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean networkProvider = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean gpsProvider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (networkProvider || gpsProvider) return true;
        return false;
    }


    private void initRecycler() {

        if (mBleDevicesList != null) {
            mBleDevicesList.clear();
            mBleDevicesList = null;
            mBleDevicesList = new ArrayList<>();
        }else {
            mBleDevicesList = new ArrayList<>();
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerQueryAdapter = new BLERecyclerQueryAdapter(mBleDevicesList);
        mRecyclerView.setAdapter(mRecyclerQueryAdapter);
        mRecyclerQueryAdapter.setOnItemClickListener(new BLERecyclerQueryAdapter.OnItemClickListener() {
            @Override
            public void OnClickListener(View view, int index) {
                if (mBleDevicesList != null && mBleDevicesList.size() > 0) {
                    System.out.println("当前点中条目：" + mBleDevicesList.get(index));
                    mBleName = mBleDevicesList.get(index).getName();
                    mBleAddress = mBleDevicesList.get(index).getAddress();
                    mTvDeviceName.setText(mBleName);
                }
            }
        });
    }




    private void init() {
        scanLeDevice();
    }

    private void scanLeDevice() {
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }


    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
                    String name = device.getName();
                    String address = device.getAddress();
                    if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(address)) {

                        if (!mBleDevicesList.toString().contains(name)) {
                            System.out.println("搜索到设备："+name);
                            BleModel bleModel = new BleModel(name,address);
                            mRecyclerQueryAdapter.add(bleModel);
//                            mBleDevicesList.add(bleModel);
//                            mRecyclerView.scrollToPosition(0);
                        }
                    }
                }
            };

    @Override
    protected void onPause() {
        super.onPause();
        if(mBluetoothAdapter != null){
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    @OnClick({R.id.btn_query, R.id.btn_add, R.id.btn_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_query:
                reQuery();
                break;
            case R.id.btn_add:
                if (!TextUtils.isEmpty(mBleName)) {
                    BleDeviceDao bleDeviceDao = GreenDaoManager.getInstance().getBleDevice();
                    BleDevice device = new BleDevice(null, mBleName, mBleAddress);
                    bleDeviceDao.insert(device);
                    finish();
                } else {
                    ToastUtils.SimpleToast("未选中设备");
                }
                break;
            case R.id.btn_back:
                finish();

                break;
        }
    }

    private void reQuery() {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            ToastUtils.SimpleToast("本设备不支持蓝牙！！");
            return;
        }
        ToastUtils.SimpleToast("---重新搜索---");

        initPermission();
        initRecycler();
        init();
    }
}
