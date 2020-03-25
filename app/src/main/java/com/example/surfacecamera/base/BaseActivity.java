package com.example.surfacecamera.base;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.example.surfacecamera.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * baseActivity
 *
 * @author lyh
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected BaseActivity activity;
    private Unbinder bun;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(getLayoutId());
        bun = ButterKnife.bind(this);
        activity = this;
        initStatusColor();
        initView();
        initData();
    }

    /**
     * 设置透明状态栏,这样才能让 ContentView 向上  6.0小米手机设置 tootlbar 会被挤上去
     * window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
     */
    private void initStatusColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            window.setStatusBarColor(getColor(R.color.theme));

            ViewGroup mContentView = activity.findViewById(Window.ID_ANDROID_CONTENT);
            View mChildView = mContentView.getChildAt(0);
            if (mChildView != null) {
                //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 使其不为系统 View 预留空间.
                ViewCompat.setFitsSystemWindows(mChildView, false);
            }
        }
    }

    /**
     * 继承activity的ID
     *
     * @return int
     */
    protected abstract int getLayoutId();

    /**
     * 设置view
     */
    protected abstract void initView();

    /**
     * 设置数据
     */
    protected abstract void initData();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bun.unbind();
    }
}
