package com.zmm.bledeviceconnect.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.zmm.bledeviceconnect.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
    }

    @OnClick({R.id.btn_blood, R.id.btn_ipc})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_blood:
                Intent intent1 = new Intent(this, BloodActivity.class);
                startActivity(intent1);
                break;
            case R.id.btn_ipc:
                Intent intent2 = new Intent(this, IPCActivity.class);
                startActivity(intent2);
                break;
        }
    }
}
