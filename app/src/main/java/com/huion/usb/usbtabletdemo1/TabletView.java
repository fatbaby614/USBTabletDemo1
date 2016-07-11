package com.huion.usb.usbtabletdemo1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class TabletView extends View
{
    private final String TAG = MainActivity.class.getSimpleName();
    float preX;
    float preY;
    private Path path;

    public TabletView(AppCompatActivity activity)
    {
        super(activity);
    }
    public TabletView(Context context, AttributeSet set)
    {
        super(context, set);

    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {

        return true;
    }
    @Override
    public void onDraw(Canvas canvas)
    {

    }
    public void clear()
    {
        Log.i(TAG, "View Clear");
    }
}
