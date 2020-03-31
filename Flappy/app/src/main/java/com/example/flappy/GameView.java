package com.example.flappy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

public class GameView extends View {

    public static int score = 0;
    private Sprite flappy;
    private Wall tube1;
    private Wall tube2;

    private int viewWidth;
    private int viewHeight;
    private Boolean crash = false;
    private Boolean tubeCrash = false;
    Bitmap bird;
    Bitmap downTube;
    Bitmap upTube;
    Bitmap fon;
    Bitmap ground = BitmapFactory.decodeResource(getResources(), R.drawable.earth);
    private int groundX = 0;
    private int groundVX = -10;
    private int groundHeight = 100;
    private int measurement = 5;
    private final int timerInterval = 15;

    public GameView(Context context) {
        super(context);

        loadImagines();
        restart();

        Timer t = new Timer();
        t.start();
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        viewWidth = w;
        viewHeight = h;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawFon(canvas);
        tube1.draw(canvas, viewHeight, crash);
        tube2.draw(canvas, viewHeight, crash);
        flappy.draw(canvas);
        drawScore(canvas, score);
        drawGround(canvas);
    }

    protected void drawScore(Canvas canvas, int score) {
        Paint p = new Paint();
//        p.setAntiAlias(true);
        p.setSubpixelText(true);
        p.setColor(Color.WHITE);
        p.setTextSize(150);
        if (score < 9)
            canvas.drawText("" + score, (float) (viewWidth / 2) - 50, 300, p);
        else if (score < 99)
            canvas.drawText("" + score, (float) (viewWidth / 2) - 100, 300, p);
        else
            canvas.drawText("" + score, (float) (viewWidth / 2) - 150, 300, p);
//        if (crash){
//            canvas.drawText("Tap to restart", (float) (viewWidth / 2) - 450, (float) (viewHeight / 2), p);
//        }
    }

    protected void drawFon(Canvas canvas) {
        Paint p = new Paint();
        canvas.drawBitmap(fon, viewWidth - fon.getWidth(), getHeight() - fon.getHeight(), p);
    }

    protected void drawGround(Canvas canvas) {
        Paint p = new Paint();
        canvas.drawBitmap(ground, groundX, viewHeight - groundHeight, p);
        canvas.drawBitmap(ground, groundX + ground.getWidth(), viewHeight - groundHeight, p);
        if (!tubeCrash && !crash) {
            groundX += groundVX;
            if (-groundX > ground.getWidth()) groundX += ground.getWidth();
        }
    }

    protected void update() {

        flappy.update(timerInterval, crash, tubeCrash);

        if (!tubeCrash && !crash) {
            tube1.update(viewWidth, viewHeight);
            tube2.update(viewWidth, viewHeight);
        }

        if (flappy.getY() + flappy.getFrameHeight() > viewHeight - groundHeight) {
            flappy.setY(viewHeight - flappy.getFrameHeight() - groundHeight);
            //flappy.setVy(0);
            crash = true;
        }

        if (lose(flappy.getX() + measurement,
                flappy.getY() + measurement))
            tubeCrash = true;
        if (lose(flappy.getX() + flappy.getFrameWidth() - measurement,
                flappy.getY() + measurement))
            tubeCrash = true;
        if (lose(flappy.getX() + measurement,
                flappy.getY() + flappy.getFrameHeight() - measurement))
            tubeCrash = true;
        if (lose(flappy.getX() + flappy.getFrameWidth() - measurement,
                flappy.getY() + flappy.getFrameHeight() - measurement))
            tubeCrash = true;

        invalidate();
    }

    protected void restart() {
        flappy.setVy(0);
        flappy.setY((float) viewHeight / 2);
        tube1.setX(1500);
        tube2.setX(1500 * 3 / 2);
    }

    protected void loadImagines() {
        Random rnd = new Random();
        int r = rnd.nextInt(3);
        switch (r) {
            case (0):
                bird = BitmapFactory.decodeResource(getResources(), R.drawable.ybird);
                break;
            case (1):
                bird = BitmapFactory.decodeResource(getResources(), R.drawable.rbird);
                break;
            default:
                bird = BitmapFactory.decodeResource(getResources(), R.drawable.bbird);
                break;
        }
        r = 1; //rnd.nextInt(2);
        switch (r) {
            case (0):
                fon = BitmapFactory.decodeResource(getResources(), R.drawable.lightfon);
                break;
            default:
                fon = BitmapFactory.decodeResource(getResources(), R.drawable.darkfon);
                break;
        }
        r = rnd.nextInt(2);
        switch (r) {
            case (0):
                downTube = BitmapFactory.decodeResource(getResources(), R.drawable.downtube);
                upTube = BitmapFactory.decodeResource(getResources(), R.drawable.uptube);
                break;
            default:
                downTube = BitmapFactory.decodeResource(getResources(), R.drawable.reddowntube);
                upTube = BitmapFactory.decodeResource(getResources(), R.drawable.reduptube);
                break;
        }
        tube1 = new Wall(525, 750, downTube.getWidth(), 250, 1500, -10, downTube, upTube, upTube.getHeight(), groundHeight);
        tube2 = new Wall(525, 750, downTube.getWidth(), 250, 1500 * 3 / 2, -10, downTube, upTube, upTube.getHeight(), groundHeight);
        int w = bird.getWidth() / 3;
        int h = bird.getHeight();
        Rect firstFrame = new Rect(0, 0, w, h);
        flappy = new Sprite(200,200, 0, 0, firstFrame, bird);
        flappy.addFrame(new Rect(w, 0, w * 2, h));
        flappy.addFrame(new Rect(w * 2, 0, w * 3, h));
    }

    protected boolean lose(double x, double y) {
        if ((x > tube1.getX() && x < tube1.getEdge()) && (y < tube1.getUpTube() || y > tube1.getDownTube()))
            return true;
        else if ((x > tube2.getX() && x < tube2.getEdge()) && (y < tube2.getUpTube() || y > tube2.getDownTube()))
            return true;
        else
            return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int eventAction = event.getAction();
        if (eventAction == MotionEvent.ACTION_DOWN) {
            if (!tubeCrash)
                flappy.setVy(-25);
            if (crash) {
                score = 0;
                loadImagines();
                restart();
                flappy.setVy(-25);
                crash = false;
                tubeCrash = false;
            }
        }
        return true;
    }

    class Timer extends CountDownTimer {

        public Timer() {
            super(Integer.MAX_VALUE, timerInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            update();
        }

        @Override
        public void onFinish() {

        }
    }
}
