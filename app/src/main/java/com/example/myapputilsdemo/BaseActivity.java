package com.example.myapputilsdemo;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        // 执行初始化方法
        setContentView(initView());

        init();

        setListener();
    }

    /**
     * 初始化
     * 设置布局文件
     *
     * @return
     */
    public abstract View initView();

    /**
     * 初始化操作
     *
     * @return
     */
    public abstract void init();

    /**
     * 点击事件
     *
     * @return
     */
    public abstract void setListener();

    /**
     * 日志
     * @param TAG
     * @param msg
     */
    protected void printLog(String TAG,String msg){
        Log.d(TAG,msg);
    }
}
