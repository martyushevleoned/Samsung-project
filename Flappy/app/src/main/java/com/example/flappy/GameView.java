package com.example.flappy;

import android.annotation.SuppressLint;
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

    private Boolean crash = false;
    private Boolean tubeCrash = false;
    private int invincible;

    Bitmap bird;
    Bitmap downTube;
    Bitmap upTube;
    Bitmap fon;
    Bitmap ground = BitmapFactory.decodeResource(getResources(), R.drawable.earth);

    private int groundX = 0;
    private final int groundVX = -10;
    private final int groundHeight = 100;
    private final int fault = 5;
    private final int timerInterval = 15;
    private final int tubeSpawn = 1500;
    private final int emptySpace = 525;

    public GameView(Context context) {
        super(context);

        loadImagines();
        restart();
        invincible = 0;

        Timer t = new Timer();
        t.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawFon(canvas);
        tube1.draw(canvas, getHeight(), crash);
        tube2.draw(canvas, getHeight(), crash);
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
            canvas.drawText("" + score, (float) (getWidth() / 2) - 50, 300, p);
        else if (score < 99)
            canvas.drawText("" + score, (float) (getWidth() / 2) - 100, 300, p);
        else
            canvas.drawText("" + score, (float) (getWidth() / 2) - 150, 300, p);
        if (crash)
            canvas.drawText("Tap to restart", (float) (getWidth() / 2) - 450, (float) (getHeight() / 2), p);

        if (invincible == 0)
            canvas.drawText("Tap to start", (float) (getWidth() / 2) - 400, (float) (getHeight() / 2), p);
    }

    protected void drawFon(Canvas canvas) {
        Paint p = new Paint();
        canvas.drawBitmap(fon, getWidth() - fon.getWidth(), getHeight() - fon.getHeight(), p);
    }

    protected void drawGround(Canvas canvas) {
        Paint p = new Paint();
        canvas.drawBitmap(ground, groundX, getHeight() - groundHeight, p);
        canvas.drawBitmap(ground, groundX + ground.getWidth(), getHeight() - groundHeight, p);
        if (!tubeCrash && !crash) {
            if (invincible != 0) groundX += groundVX;
            if (-groundX > ground.getWidth()) groundX += ground.getWidth();
        }
    }

    protected void update() {

        if (invincible > 0) {
            flappy.update(timerInterval, crash, tubeCrash);
            if (!tubeCrash && !crash) {
                tube1.update(tubeSpawn, getHeight());
                tube2.update(tubeSpawn, getHeight());
            }
            invincible++;
        }

        if (invincible > 10) {
            if (flappy.getY() + flappy.getFrameHeight() > getHeight() - groundHeight) {
                flappy.setY(getHeight() - flappy.getFrameHeight() - groundHeight);
//            flappy.setVy(0);
                crash = true;
            }

            if (lose(flappy.getX() + fault,
                    flappy.getY() + fault))
                tubeCrash = true;
            if (lose(flappy.getX() + flappy.getFrameWidth() - fault,
                    flappy.getY() + fault))
                tubeCrash = true;
            if (lose(flappy.getX() + fault,
                    flappy.getY() + flappy.getFrameHeight() - fault))
                tubeCrash = true;
            if (lose(flappy.getX() + flappy.getFrameWidth() - fault,
                    flappy.getY() + flappy.getFrameHeight() - fault))
                tubeCrash = true;
        }

        invalidate();
    }

    protected void restart() {
        flappy.setVy(0);
        flappy.setY(500);
        invincible = 1;
        tube1.setX(tubeSpawn);
        tube2.setX(tubeSpawn * 3 / 2);
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
        r = rnd.nextInt(2);
        if (r == 0) {
            fon = BitmapFactory.decodeResource(getResources(), R.drawable.lightfon);
        } else {
            fon = BitmapFactory.decodeResource(getResources(), R.drawable.darkfon);
        }

        r = rnd.nextInt(2);
        if (r == 0) {
            downTube = BitmapFactory.decodeResource(getResources(), R.drawable.downtube);
            upTube = BitmapFactory.decodeResource(getResources(), R.drawable.uptube);
        } else {
            downTube = BitmapFactory.decodeResource(getResources(), R.drawable.reddowntube);
            upTube = BitmapFactory.decodeResource(getResources(), R.drawable.reduptube);
        }
        tube1 = new Wall(emptySpace, 750, downTube.getWidth(), 250, tubeSpawn, -10, downTube, upTube, upTube.getHeight(), groundHeight);
        tube2 = new Wall(emptySpace, 750, downTube.getWidth(), 250, tubeSpawn * 3 / 2, -10, downTube, upTube, upTube.getHeight(), groundHeight);
//        tube1.setX(-tube1.getEdge());
        tube2.setX(-tubeSpawn);
        int w = bird.getWidth() / 3;
        int h = bird.getHeight();
        Rect firstFrame = new Rect(0, 0, w, h);
        flappy = new Sprite(200, 200, 0, 0, firstFrame, bird);
        flappy.addFrame(new Rect(w, 0, w * 2, h));
        flappy.addFrame(new Rect(w * 2, 0, w * 3, h));
    }

    protected boolean lose(double x, double y) {
        if ((x > tube1.getX() && x < tube1.getEdge()) && (y < tube1.getUpTube() || y > tube1.getDownTube()))
            return true;
        else
            return (x > tube2.getX() && x < tube2.getEdge()) && (y < tube2.getUpTube() || y > tube2.getDownTube());
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int eventAction = event.getAction();
        if (eventAction == MotionEvent.ACTION_DOWN) {
            if (!tubeCrash) {
                flappy.setVy(-25);
                invincible++;
            }

            if (crash) {
                score = 0;
                loadImagines();
                restart();
                crash = false;
                tubeCrash = false;
            }
        }
        return true;
    }

    class Timer extends CountDownTimer {

        Timer() {
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
