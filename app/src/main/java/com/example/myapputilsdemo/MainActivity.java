package com.example.myapputilsdemo;

import androidx.annotation.NonNull;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;

import com.example.myapputilsdemo.databinding.ActivityMainBinding;

import java.lang.ref.WeakReference;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private final static String TAG = "MainActivity";

    private ActivityMainBinding mMainBinding;

    //线程新启动方式
    private HandlerThread mHandlerThread;
    private Handler mWorkHandler;

    //主线程中的Handler
    private Handler mMainHandler;

    @Override
    public View initView() {
        mMainBinding = ActivityMainBinding.inflate(LayoutInflater.from(this));

        return mMainBinding.getRoot();
    }

    /**
     * 做一些初始化操作
     */
    @Override
    public void init() {

        initMainHandler();

        initHandlerThread();
    }

    /**
     * 主线程中的handler初始化及逻辑处理
     * 静态内部内+弱引用，防止内存溢出
     */
    private void initMainHandler() {
        mMainHandler = new InnerHandler(this);
    }

    /**
     * 开启一个工作线程中的Handler，可执行耗时操作
     */
    private void initHandlerThread() {
        mHandlerThread = new HandlerThread("WorkHandlerThread");
        mHandlerThread.start();

        mWorkHandler = new Handler(mHandlerThread.getLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        //工作线程立即发送消息到主线程
                        Message msg0 = Message.obtain();
                        msg0.what = 0;
                        mMainHandler.sendMessage(msg0);

                        break;

                    case 1:
                        //工作线程延时一秒发送消息到主线程
                        try{
                            Thread.sleep(1000);
                            Message msg1 = Message.obtain();
                            msg1.what = 1;
                            mMainHandler.sendMessage(msg1);

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        break;

                    default:
                        break;
                }
            }
        };
    }

    @Override
    public void setListener() {
        mMainBinding.tvMainHandler.setOnClickListener(this);
        mMainBinding.tvMainHandlerDelay.setOnClickListener(this);
        mMainBinding.tvWorkHandler.setOnClickListener(this);
        mMainBinding.tvWorkHandlerDelay.setOnClickListener(this);
    }

    /**
     * 销毁工作线程中的Handler
     */
    private void destroyHandlerThread(){
        mWorkHandler.removeCallbacksAndMessages(null);
        mHandlerThread.quit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_main_handler://主线程立马更新
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mMainBinding.tvInfo.setText(getResources().getString(R.string.main_handler));
                    }
                });

                break;

            case R.id.tv_main_handler_delay://主线程延时一秒更新
                mMainHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mMainBinding.tvInfo.setText(getResources().getString(R.string.main_handler_delay));
                    }
                },1000);

                break;

            case R.id.tv_work_handler://工作线程立马更新
                Message msg = Message.obtain();
                msg.what = 0;
                mWorkHandler.sendMessage(msg);

                break;

            case R.id.tv_work_handler_delay://工作线程延时一秒更新
                Message msgDelay = Message.obtain();
                msgDelay.what = 1;
                mWorkHandler.sendMessage(msgDelay);

                break;

            default:

                break;
        }
    }

    /**
     * 静态内部类弱引用方式，防止内存溢出
     */
    private static class InnerHandler extends Handler {

        private WeakReference<MainActivity> mWeakReference;

        public InnerHandler(MainActivity activity) {
            super();
            this.mWeakReference = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mWeakReference == null || mWeakReference.get() == null) {
                return;
            }
            MainActivity activity = mWeakReference.get();
            if (activity == null) {
                return;
            }
            if (msg == null) {
                return;
            }
            switch (msg.what) {
                case 0:
                    //工作线程处理完逻辑后，发送消息到主线程的Handler，立马更新ui
                    activity.mMainBinding.tvInfo.setText(activity.getResources().getString(R.string.work_handler));
                    break;

                case 1:
                    //工作线程处理完逻辑后，发送消息到主线程的Handler，立马更新ui
                    activity.mMainBinding.tvInfo.setText(activity.getResources().getString(R.string.work_handler_delay));
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        destroyHandlerThread();
    }
}
