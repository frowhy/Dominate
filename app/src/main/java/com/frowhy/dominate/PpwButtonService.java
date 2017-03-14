package com.frowhy.dominate;

import android.app.Instrumentation;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.scalified.fab.FloatingActionButton;

import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings("ALL")
public class PpwButtonService extends Service {

    private FloatingActionButton gFabContent;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowParams;
    private View mWindowView;
    private int mStartX, mStartY;
    private boolean mIsTouch;

    @Override
    public void onCreate() {
        super.onCreate();
        initWindowParams();
        initView();
        initClick();
        addWindowView2Window();
    }

    private void initWindowParams() {
        mWindowManager = (WindowManager) getApplication().getSystemService(WINDOW_SERVICE);
        mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        mWindowParams.format = PixelFormat.TRANSLUCENT;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        mWindowParams.gravity = Gravity.START | Gravity.TOP;
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
    }

    private void initView() {
        mWindowView = LayoutInflater.from(getApplication()).inflate(R.layout.ppw_button, null);
        gFabContent = (FloatingActionButton) mWindowView.findViewById(R.id.fab_content);
    }

    private void addWindowView2Window() {
        mWindowManager.addView(mWindowView, mWindowParams);
    }

    private void initClick() {
        gFabContent.setOnTouchListener(new View.OnTouchListener() {
            private Timer timer;
            private int mEndX;
            private int mEndY;
            private TimerTask mTask;
            private int mCount;
            private boolean mIsMove;
            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (!mIsMove) {
                        if (mCount > 5) {
                            onLongClick();
                            mIsMove = true;
                        } else {
                            mCount++;
                        }
                    }
                    super.handleMessage(msg);
                }
            };

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mStartX = mEndX = (int) motionEvent.getRawX();
                        mStartY = mEndY = (int) motionEvent.getRawY();
                        mCount = 0;
                        mTask = new TimerTask() {
                            @Override
                            public void run() {
                                Message message = new Message();
                                handler.sendMessage(message);
                            }
                        };
                        timer = new Timer();
                        timer.schedule(mTask, 100, 100);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int disX = (int) (motionEvent.getRawX() - mEndX);
                        int disY = (int) (motionEvent.getRawY() - mEndY);
                        mWindowParams.x += disX;
                        mWindowParams.y += disY;
                        mWindowManager.updateViewLayout(mWindowView, mWindowParams);
                        mEndX = (int) motionEvent.getRawX();
                        mEndY = (int) motionEvent.getRawY();
                        if (disX != 0 && disY != 0) {
                            mIsMove = true;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        int x = (int) motionEvent.getRawX();
                        int y = (int) motionEvent.getRawY();
                        int upX = x - mStartX;
                        int upY = y - mStartY;
                        upX = Math.abs(upX);
                        upY = Math.abs(upY);
                        if (upX == 0 && upY == 0) {
                            if (mCount <= 5) {
                                onClick();
                            }
                        }
                        mIsMove = false;
                        timer.cancel();
                        timer.purge();
                        break;
                }
                return true;
            }
        });
    }

    private void onClick() {
        handlePlay();
    }

    private void handlePlay() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_HEADSETHOOK);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void handleNext() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_HEADSETHOOK);
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_HEADSETHOOK);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void onLongClick() {
        handleNext();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWindowView != null) {
            mWindowManager.removeView(mWindowView);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}