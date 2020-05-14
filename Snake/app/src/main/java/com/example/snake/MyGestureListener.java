package com.example.snake;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

/**
 * Created by petho on 2017-01-14.
 */

public class MyGestureListener implements GestureDetector.OnGestureListener {

    private Context context;

    public MyGestureListener(Context context) {
        this.context = context;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if(e1.getY() - e2.getY() > 50){
            Toast.makeText(context, "UP", Toast.LENGTH_SHORT).show();
            return true;
        }
        if(e2.getY() - e1.getY() > 50){
            Toast.makeText(context, "DOWN", Toast.LENGTH_SHORT).show();
            return true;
        }
        if(e1.getX() - e2.getX() > 50){
            Toast.makeText(context, "LEFT", Toast.LENGTH_SHORT).show();
            return true;
        }
        if(e2.getX() - e1.getX() > 50){
            Toast.makeText(context, "RIGHT", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}