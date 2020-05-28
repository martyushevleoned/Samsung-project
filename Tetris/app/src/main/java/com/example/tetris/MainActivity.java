package com.example.tetris;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import static com.example.tetris.GameView.direction;
import static com.example.tetris.GameView.mainBlockX;
import static com.example.tetris.GameView.mainBlockY;
import static com.example.tetris.GameView.shape;
import static com.example.tetris.GameView.shapeNum;
import static com.example.tetris.GameView.stage;
import static com.example.tetris.GameView.counter;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    public GestureDetectorCompat gd;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
        setContentView(R.layout.activity_main);
        ((FrameLayout) findViewById(R.id.idFlame)).addView(new GameView(this));

        gd = new GestureDetectorCompat(this, this);
        gd.setOnDoubleTapListener(this);
    }

    static int maxScore = 0;
    static String SAVED_NUM = "NUMBER";
    static SharedPreferences sharedPreferences;

    static void saveData(Context context) {
        sharedPreferences = context.getSharedPreferences(SAVED_NUM, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SAVED_NUM, Integer.parseInt(String.valueOf(maxScore)));
        editor.apply();
    }

    static void loadData(Context context) {
        sharedPreferences = context.getSharedPreferences(SAVED_NUM, MODE_PRIVATE);
        maxScore = sharedPreferences.getInt(SAVED_NUM, 0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gd.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    void right() {
        boolean t = true;
        for (int i = 0; i < shape[shapeNum][direction][0].length; i++) {
            if (!GameView.rightCheck(mainBlockX + shape[shapeNum][direction][0][i], mainBlockY + shape[shapeNum][direction][1][i])) {
                t = false;
                break;
            }
        }
        if (t) {
            counter = 1;
            mainBlockX++;
        }
    }

    void left() {
        boolean t = true;
        for (int i = 0; i < shape[shapeNum][direction][0].length; i++) {
            if (!GameView.leftCheck(mainBlockX + shape[shapeNum][direction][0][i], mainBlockY + shape[shapeNum][direction][1][i])) {
                t = false;
                break;
            }
        }
        if (t) {
            counter = 1;
            mainBlockX--;
        }
    }

    void up() {
    }

    void down() {
    }

    //==============================================================================================
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;


    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        boolean result = false;
        try {
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        right();
                    } else {
                        left();
                    }
                    result = true;
                }
            } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
                    down();
                } else {
                    up();
                }
                result = true;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
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
        if (stage == 1) {
            GameView.restart();
        } else {
            boolean t = true;

            int d = direction + 1;
            d %= shape[shapeNum].length;

            for (int i = 0; i < shape[shapeNum][direction][0].length; i++) {
                if (!GameView.rotateCheck(mainBlockX + shape[shapeNum][d][0][i], mainBlockY + shape[shapeNum][d][1][i])) {
                    t = false;
                    break;
                }
            }

            if (t) {
                counter = 1;
                direction++;
                direction %= shape[shapeNum].length;
            }
        }
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
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }
}