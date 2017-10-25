package com.frowhy.dominate;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import ezy.assist.compat.SettingsCompat;

public class MainActivity extends AppCompatActivity {
    private Intent mPpwButtonService;
    private Button mBtnOpenPopupWindow;
    private boolean mIsOpen;
    private SharedPreferences mSp;
    private CheckBox mCbuseRoot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSp = getSharedPreferences(getPackageName(),MODE_PRIVATE);
        mPpwButtonService = new Intent(this, PpwButtonService.class);
        mBtnOpenPopupWindow = (Button) findViewById(R.id.btn_handle_popup_window);
        mCbuseRoot= (CheckBox) findViewById(R.id.checkBox);
        mCbuseRoot.setChecked(mSp.getBoolean("isUseRoot",false));
        mCbuseRoot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSp.edit().putBoolean("isUseRoot",isChecked).commit();
            }
        });
        mIsOpen = isServiceRunning();//检测服务运行状态
        if(mIsOpen) {//设置按钮文本
            mBtnOpenPopupWindow.setText(R.string.close_popup_window);
        }else{
            mBtnOpenPopupWindow.setText(R.string.open_popup_window);
        }
        mBtnOpenPopupWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SettingsCompat.canDrawOverlays(MainActivity.this)) {
                    if (!SettingsCompat.setDrawOverlays(MainActivity.this, true)) {
                        Toast.makeText(MainActivity.this,"请开启本应用的悬浮窗权限！",Toast.LENGTH_LONG).show();
                        SettingsCompat.manageDrawOverlays(MainActivity.this);
                    } else {
                        handlePopupWindow();
                    }
                } else {
                    handlePopupWindow();
                }
            }
        });
    }

    private void handlePopupWindow() {
        if (!mIsOpen) {
            mIsOpen = true;
            mBtnOpenPopupWindow.setText(R.string.close_popup_window);
            startService(mPpwButtonService);
        } else {
            mIsOpen = false;
            mBtnOpenPopupWindow.setText(R.string.open_popup_window);
            stopService(mPpwButtonService);
        }
    }

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ((getPackageName()+".PpwButtonService").equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
