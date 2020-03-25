package com.example.surfacecamera;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.surfacecamera.base.BaseActivity;
import com.example.surfacecamera.util.AppConstant;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;

import java.lang.reflect.Method;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;

/**
 * @author lyh
 */
public class MainActivity extends BaseActivity {

    @BindView(R.id.take_pic)
    Button takePic;
    @BindView(R.id.camera2)
    Button camera2;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivityForResult(intent, 0);
            }
        });
        camera2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CameraShowActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    protected void initData() {
        requestPermission();
        //关闭网络
        setDataConnectionState(this, false);
    }

    @SuppressLint("WrongConstant")
    public static void setDataConnectionState(Context cxt, boolean state) {
        ConnectivityManager connectivityManager = null;
        Class connectivityManagerClz = null;
        try {
            connectivityManager = (ConnectivityManager) cxt
                    .getSystemService("connectivity");
            connectivityManagerClz = connectivityManager.getClass();
            Method method = connectivityManagerClz.getMethod(
                    "setMobileDataEnabled", new Class[]{boolean.class});
            method.invoke(connectivityManager, state);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != AppConstant.RESULT_CODE.RESULT_OK) {
            return;
        }
        if (requestCode == AppConstant.RESULT_CODE.RESULT_CANCELED) {
//            String imgPath = data.getStringExtra(AppConstant.KEY.IMG_PATH);
//            int picWidth = data.getIntExtra(AppConstant.KEY.PIC_WIDTH, 0);
//            int picHeight = data.getIntExtra(AppConstant.KEY.PIC_HEIGHT, 0);
//            Intent intent = new Intent(activity, ShowPicActivity.class);
//            intent.putExtra(AppConstant.KEY.PIC_WIDTH, picWidth);
//            intent.putExtra(AppConstant.KEY.PIC_HEIGHT, picHeight);
//            intent.putExtra(AppConstant.KEY.IMG_PATH, imgPath);
//            startActivity(intent);
        } else if (requestCode == AppConstant.RESULT_CODE.RESULT_ERROR) {
        }
    }

    /**
     * 动态申请  (电话/位置/存储)
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void requestPermission() {
        AndPermission.with(this)
                .permission(Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO)
                .rationale(new Rationale() {
                    @Override
                    public void showRationale(Context context, List<String> permissions, RequestExecutor executor) {
                        executor.execute();
                    }
                })
                .onGranted(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        Toast.makeText(getApplicationContext(), R.string.getPermission, Toast.LENGTH_SHORT).show();
                    }
                })
                .onDenied(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        if (AndPermission.hasAlwaysDeniedPermission(MainActivity.this, permissions)) {
                            // 打开权限设置页
                            AndPermission.permissionSetting(MainActivity.this).execute();
                            return;
                        }
                        Toast.makeText(getApplicationContext(), R.string.denyPermission, Toast.LENGTH_SHORT).show();
                    }
                })
                .start();
    }
}
