package com.frowhy.dominate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import ezy.assist.compat.SettingsCompat;

public class MainActivity extends AppCompatActivity {
    private Intent mPpwButtonService;
    private Button mBtnOpenPopupWindow;
    private boolean mIsOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPpwButtonService = new Intent(this, PpwButtonService.class);

        mBtnOpenPopupWindow = (Button) findViewById(R.id.btn_handle_popup_window);
        mBtnOpenPopupWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SettingsCompat.canDrawOverlays(MainActivity.this)) {
                    if (!SettingsCompat.setDrawOverlays(MainActivity.this, true)) {
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
}
