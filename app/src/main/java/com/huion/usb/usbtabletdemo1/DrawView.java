package com.huion.usb.usbtabletdemo1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by tanhuang on 7/12/16.
 */
public class DrawView extends View
    {
        private final String TAG = MainActivity.class.getSimpleName();
        float preX;
        float preY;
        private Path path;
        public Paint paint = null;
        final int m_VIEW_WIDTH = getContext().getResources().getDisplayMetrics().widthPixels;
        final int m_VIEW_HEIGHT = getContext().getResources().getDisplayMetrics().heightPixels;
        Bitmap cacheBitmap = null;
        Canvas cacheCanvas = null;
        public DrawView(Context context)
        {
            super(context);
            paint_init();
        }

        public DrawView(Context context, AttributeSet set)
        {
            super(context, set);
            paint_init();
        }
        public void paint_init()
        {
            Log.i(TAG, "paint_init()"+String.format("width:%d height:%d",m_VIEW_WIDTH,m_VIEW_HEIGHT));
            cacheBitmap = Bitmap.createBitmap(m_VIEW_WIDTH , m_VIEW_HEIGHT ,Bitmap.Config.ARGB_8888);
            cacheCanvas = new Canvas();
            path = new Path();
            cacheCanvas.setBitmap(cacheBitmap);
            paint = new Paint(Paint.DITHER_FLAG);
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1);
            paint.setAntiAlias(true);
            paint.setDither(true);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event)
        {
            float x = event.getX();
            float y = event.getY();
            Log.i(TAG, "MotionEvent"+String.format("%f %f",x,y));
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(x, y);
                    preX = x;
                    preY = y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    path.quadTo(preX , preY , x, y);
                    preX = x;
                    preY = y;
                    break;
                case MotionEvent.ACTION_UP:
                    cacheCanvas.drawPath(path, paint);
                    path.reset();
                    break;
            }
            invalidate();
            return true;
        }
        @Override
        public void onDraw(Canvas canvas)
        {
            Log.i(TAG, "DrawView onDraw()");
            Paint bmpPaint = new Paint();
            canvas.drawBitmap(cacheBitmap , 0 , 0 , bmpPaint);
            canvas.drawPath(path, paint);
        }
}
