package com.example.surfacecamera;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.surfacecamera.base.BaseActivity;
import com.example.surfacecamera.util.AppConstant;
import com.example.surfacecamera.util.BitmapUtils;
import com.example.surfacecamera.util.CameraUtil;
import com.example.surfacecamera.view.ShowSurfaceView;

import java.io.ByteArrayOutputStream;
import java.io.File;

import butterknife.BindView;


/**
 * @author lyh
 */
public class CameraActivity extends BaseActivity implements SurfaceHolder.Callback, View.OnClickListener {
    private static final String TAG = "CameraActivity";
    @BindView(R.id.surfaceView2)
    ShowSurfaceView svShow;
    @BindView(R.id.surfaceView)
    SurfaceView svContent;
    @BindView(R.id.ib_camera)
    ImageButton ibCamera;
    @BindView(R.id.camera_flash)
    ImageButton ibFlash;
    @BindView(R.id.camera_switch)
    ImageButton ibSwitch;
    @BindView(R.id.ib_back)
    ImageButton ibBack;

    private Camera mCamera;
    private SurfaceHolder mHolder;
    private CameraUtil cameraInstance;
    /**
     * 屏幕宽高
     */
    private int screenWidth;
    private int screenHeight;
    /**
     * 图片宽高
     */
    private int picWidth;

    /**
     * 是否有界面
     */
    private boolean isView = true;
    /**
     * 拍照id  1： 前摄像头  0：后摄像头
     */
    private int mCameraId = 0;
    /**
     * 闪光灯类型 0 ：关闭 1： 打开 2：自动
     */
    private int light_type = 0;

    /**
     * 图片高度
     */
    private int picHeight;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_camera;
    }

    @Override
    protected void initView() {
        ibCamera.setOnClickListener(this);
        ibFlash.setOnClickListener(this);
        ibSwitch.setOnClickListener(this);
        ibBack.setOnClickListener(this);
        mHolder = svContent.getHolder();
        mHolder.addCallback(this);
    }

    @Override
    protected void initData() {
        cameraInstance = CameraUtil.getInstance();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            mCamera = getCamera(mCameraId);
            if (mHolder != null) {
                startPreview(mCamera, mHolder);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //点击拍照
            case R.id.ib_camera:
                switch (light_type) {
                    case 0:
                        cameraInstance.turnLightOff(mCamera);
                        break;
                    case 1:
                        cameraInstance.turnLightOn(mCamera);
                        break;
                    case 2:
                        cameraInstance.turnLightAuto(mCamera);
                        break;
                    default:
                        break;
                }
                takePhoto();
                break;
            case R.id.camera_flash:
                if (mCameraId == 1) {
                    Toast.makeText(this, R.string.please_change_camera, Toast.LENGTH_SHORT).show();
                    return;
                }
                Camera.Parameters parameters = mCamera.getParameters();
                switch (light_type) {
                    case 0:
                        light_type = 1;
                        ibFlash.setBackground(getDrawable(R.drawable.light_auto));
//                        ibFlash.setImageResource(R.drawable.light_on);
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        mCamera.setParameters(parameters);
                        break;
                    case 1:
                        light_type = 2;
                        ibFlash.setBackground(getDrawable(R.drawable.light_on));
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        mCamera.setParameters(parameters);
                        break;
                    case 2:
                        light_type = 0;
//                        ibFlash.setImageResource(R.drawable.light_on);
                        ibFlash.setBackground(getDrawable(R.drawable.light_on));
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        mCamera.setParameters(parameters);
                        break;
                    default:
                        break;
                }
                break;
            case R.id.camera_switch:
                switchCamera();
                break;
            case R.id.ib_back:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 切换前后摄像头
     */
    public void switchCamera() {
        releaseCamera();
        mCameraId = (mCameraId + 1) % Camera.getNumberOfCameras();
        mCamera = getCamera(mCameraId);
        if (mHolder != null) {
            startPreview(mCamera, mHolder);
        }
    }

    private void takePhoto() {
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                isView = false;
                //将data 转换为位图 或者你也可以直接保存为文件使用 FileOutputStream
                //这里我相信大部分都有其他用处把 比如加个水印 后续再讲解
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                Bitmap saveBitmap = cameraInstance.setTakePicktrueOrientation(mCameraId, bitmap);
                saveBitmap = Bitmap.createScaledBitmap(saveBitmap, screenWidth, screenHeight, true);
                String imgpath = getExternalFilesDir(Environment.DIRECTORY_DCIM).getPath() +
                        File.separator + System.currentTimeMillis() + ".jpeg";
                Log.e(TAG, "imgpath: ---  " + imgpath);
                BitmapUtils.saveJPGE_After(getApplicationContext(), saveBitmap, imgpath, 100);
                if (!bitmap.isRecycled()) {
                    bitmap.recycle();
                }
                if (!saveBitmap.isRecycled()) {
                    saveBitmap.recycle();
                }
                Intent intent = new Intent();
                intent.putExtra(AppConstant.KEY.IMG_PATH, imgpath);
                intent.putExtra(AppConstant.KEY.PIC_WIDTH, picWidth);
                intent.putExtra(AppConstant.KEY.PIC_HEIGHT, picHeight);
                setResult(AppConstant.RESULT_CODE.RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startPreview(mCamera, holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mCamera.stopPreview();
        startPreview(mCamera, holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }

    /**
     * 释放相机资源
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 预览相机
     *
     * @param camera
     * @param holder
     */
    private void startPreview(Camera camera, SurfaceHolder holder) {
//        try{
        setupCamera(camera);
        camera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                Camera.Size size = camera.getParameters().getPreviewSize();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                try {
                    YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
                    image.compressToJpeg(new Rect(0, 0, size.width / 2, size.height / 2), 20, stream);
                    Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                    svShow.setBitmap(BitmapUtils.rotateMyBitmap(BitmapUtils.ImgaeToNegative(bmp)));
                    stream.close();
                } catch (Exception e) {
                    Log.e("Sys", "Error:" + e.getMessage());
                }
            }
        });
        cameraInstance.setCameraDisplayOrientation(this, mCameraId, camera);
        camera.startPreview();
        isView = true;
//        }catch (IOException e){
//            e.printStackTrace();
//        }
    }

    /**
     * 设置surfaceView的尺寸 因为camera默认是横屏，所以取得支持尺寸也都是横屏的尺寸
     * 我们在startPreview方法里面把它矫正了过来，但是这里我们设置设置surfaceView的尺寸的时候要注意 previewSize.height<previewSize.width
     * previewSize.width才是surfaceView的高度
     * 一般相机都是屏幕的宽度 这里设置为屏幕宽度 高度自适应 你也可以设置自己想要的大小
     */
    private void setupCamera(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        //根据屏幕尺寸获取最佳屏幕大小
        Camera.Size previewSize = cameraInstance.getPicPreviewSize(parameters.getSupportedPreviewSizes(), screenHeight, screenWidth);
        parameters.setPreviewSize(previewSize.width, previewSize.height);

        Camera.Size pictureSize = cameraInstance.getPicPreviewSize(parameters.getSupportedPictureSizes(),
                screenHeight, screenWidth);
        parameters.setPictureSize(pictureSize.width, pictureSize.height);
        camera.setParameters(parameters);
        picWidth = pictureSize.width;
        picHeight = pictureSize.height;
    }


    /**
     * 获取Camera实例
     *
     * @param id
     * @return Camera
     */
    private Camera getCamera(int id) {
        Camera camera = null;
        try {
            camera = Camera.open(id);
        } catch (Exception e) {
            Log.e(TAG, "getCamera: " + e);
        }
        return camera;
    }
}
