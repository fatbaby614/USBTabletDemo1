package com.huion.usb.usbtabletdemo1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;

public class TabletView extends View
{
    private final String TAG = MainActivity.class.getSimpleName();
    float preX;
    float preY;
    private Path m_Path = null;
    private Paint m_Paint = null;
    private Paint m_BitmapPaint = null;
    private Canvas m_Canvas = null;
    private SurfaceHolder m_surfaceHolder;
    private Bitmap m_cacheBitmap = null;
    private int m_view_width,m_view_height;

    public TabletView(AppCompatActivity activity)
    {
        super(activity);
        paint_init();
    }
    public TabletView(Context context, AttributeSet set)
    {
        super(context, set);
        paint_init();
    }
    public void paint_init()
    {
        m_view_width = getContext().getResources().getDisplayMetrics().widthPixels;	//获取屏幕宽度
        m_view_height = getContext().getResources().getDisplayMetrics().heightPixels;	//获取屏幕高度
        m_cacheBitmap = Bitmap.createBitmap(m_view_width, m_view_height, Bitmap.Config.ARGB_8888);

        m_Paint = new Paint(Paint.DITHER_FLAG);//Paint.DITHER_FLAG 防抖动
        m_Canvas = new Canvas();
        m_Canvas.setBitmap(m_cacheBitmap);
        m_Paint.setColor(Color.RED);
        m_Paint.setStyle(Paint.Style.STROKE);//设置填充方式为描边
        m_Paint.setStrokeJoin(Paint.Join.ROUND);//设置笔刷的图形样式
        m_Paint.setStrokeCap(Paint.Cap.ROUND);//设置画笔转弯处的连接风格
        m_Paint.setAntiAlias(true);//设置抗锯齿功能
        m_Paint.setStrokeWidth(2);//设置默认笔触的宽度为2像素
        m_BitmapPaint = new Paint(Paint.DITHER_FLAG);
        m_Path = new Path();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float pos_x,pos_y;
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                pos_x = event.getX();
                pos_y = event.getY();
                Log.i(TAG, "MotionEvent.ACTION_DOWN"+String.format("%f %f",pos_x,pos_y));
                m_Path.moveTo(event.getX(),event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                pos_x = event.getX();
                pos_y = event.getY();
                Log.i(TAG, "MotionEvent.ACTION_MOVE"+String.format("%f %f",pos_x,pos_y));
                m_Path.lineTo(pos_x,pos_y);
//                Canvas canvas = m_surfaceHolder.lockCanvas();
                m_Canvas.drawPath(m_Path,m_Paint);
  //              m_surfaceHolder.unlockCanvasAndPost(canvas);
                break;
            case MotionEvent.ACTION_UP:
                pos_x = event.getX();
                pos_y = event.getY();
                Log.i(TAG, "MotionEvent.ACTION_UP"+String.format("%f %f",pos_x,pos_y));
                break;
            default:break;
        }
        invalidate();
        return true;
    }
    @Override
    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        m_Canvas.drawColor(0xffffff);				//设置背景颜色
        m_Canvas.drawBitmap(m_cacheBitmap, 0, 0, m_BitmapPaint);		//绘制cacheBitmap
        m_Canvas.drawPath(m_Path,m_Paint);
        invalidate();
    }
    public void clear()
    {
        Log.i(TAG, "View Clear");
    }
}
