package com.example.surfacecamera.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.widget.Toast;

public class ShowSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private SurfaceHolder mSurfaceHolder;
    /**
     * 绘图Canvas
     */
    private Canvas mCanvas;
    /**
     * 子线程标志位
     */
    private boolean mIsDrawing;
    private Bitmap bitmap;
    private Paint mPaint;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public ShowSurfaceView(Context context) {
        this(context, null);
    }

    public ShowSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShowSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        setFocusable(true);
        setKeepScreenOn(true);
        setFocusableInTouchMode(true);
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(5);
    }

    public ShowSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mIsDrawing = true;
        //开启子线程
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //改变，此处项目无需求
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsDrawing = false;
    }

    @Override
    public void run() {
        while (mIsDrawing) {
            drawWindow();
        }
    }

    private void drawWindow() {
        try {
            mCanvas = mSurfaceHolder.lockCanvas();
            if (bitmap != null) {
                mCanvas.drawBitmap(bitmap, 0, 0, mPaint);
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (mCanvas != null) {
                //释放Canvas对象并提交画布
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }
}
