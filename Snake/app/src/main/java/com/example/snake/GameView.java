package com.example.snake;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;

import java.util.LinkedList;
import java.util.Random;

public class GameView extends View{

    Context cont;

    LinkedList<Integer> xm = new LinkedList<>();
    LinkedList<Integer> ym = new LinkedList<>();
    LinkedList<Boolean> turn = new LinkedList<>();

    Paint p = new Paint();

    MediaPlayer hrum;

    Bitmap head_down = BitmapFactory.decodeResource(getResources(), R.drawable.head_down);
    Bitmap head_up = BitmapFactory.decodeResource(getResources(), R.drawable.head_up);
    Bitmap head_right = BitmapFactory.decodeResource(getResources(), R.drawable.head_right);
    Bitmap head_left = BitmapFactory.decodeResource(getResources(), R.drawable.head_left);

    Bitmap head_down2 = BitmapFactory.decodeResource(getResources(), R.drawable.head_down_rot);
    Bitmap head_up2 = BitmapFactory.decodeResource(getResources(), R.drawable.head_up_rot);
    Bitmap head_right2 = BitmapFactory.decodeResource(getResources(), R.drawable.head_right_rot);
    Bitmap head_left2 = BitmapFactory.decodeResource(getResources(), R.drawable.head_left_rot);

    Bitmap body_h = BitmapFactory.decodeResource(getResources(), R.drawable.body_left_and_right);
    Bitmap body_v = BitmapFactory.decodeResource(getResources(), R.drawable.body_up_and_down);

    Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.bg);
    Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
    Bitmap apple = BitmapFactory.decodeResource(getResources(), R.drawable.apple);


    private int timerInterval = 200;
    private int stage = -10;
    private int nx;
    private int ny;
    private int size = 75;
    private int vx = 0;
    private int vy = 0;
    private int activeVx = 0;
    private int activeVy = 0;
    private int headX = 0;
    private int headY = 0;
    private int appleX = 0;
    private int appleY = 0;
    private double k = 0;

    /*
     * stage
     * -3 -1 - skip first frames
     * 0 - skip spawn
     * 1- game play
     * */

    public GameView(Context context) {
        super(context);
        cont = context;
        p.setAntiAlias(true);

        xm.add(headX);
        ym.add(headY);
        turn.add(true);

        hrum = MediaPlayer.create(cont, R.raw.apple_sound);

        MainActivity.loadData(cont);

        Timer t = new Timer();
        t.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (stage > 0) {
            drawTable(canvas);
            drawApple(canvas);
            drawSnake(canvas);
            drawScore(canvas);
        } else {
            canvas.drawARGB(255, 0, 0, 0);
            canvas.drawBitmap(logo, (float) (getWidth() - logo.getWidth()) / 2, (float) (getHeight() - logo.getHeight()) / 2, p);
        }
    }

    protected void update() {


        for (int i = xm.size() - 1; i > 0; i--) {
            xm.set(i, xm.get(i - 1));
            ym.set(i, ym.get(i - 1));
            turn.set(i, turn.get(i - 1));
        }

        xm.set(0, headX);
        ym.set(0, headY);

        if (vy == 0)
            turn.set(0, true);
        else
            turn.set(0, false);

        headX += vx;
        headY += vy;

        activeVx = vx;
        activeVy = vy;

        if (headX == -1) headX = nx - 1;
        if (headY == -1) headY = ny - 1;
        if (headX == nx) headX = 0;
        if (headY == ny) headY = 0;

        if (stage >= 0)
            appleSpawn();

        crashSnake();

        if (stage < -1) {
            stage++;
            if (stage == -1) {
                nx = getWidth() / size;
                ny = getHeight() / size;

                headX = nx / 2;
                headY = ny / 2;

                appleX = headX;
                appleY = headY;

                k = (double) getHeight() / getWidth();
                stage = 0;
            }
        }

        invalidate();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int eventAction = event.getAction();
        if (eventAction == MotionEvent.ACTION_DOWN) {
            double tapX = event.getX();
            double tapY = event.getY();

            if (tapY > k * tapX)
                if (tapY > -k * tapX + getHeight()) {
                    if (activeVy != -1) {
                        vx = 0;
                        vy = 1;
                    }
                } else {
                    if (activeVx != 1) {
                        vx = -1;
                        vy = 0;
                    }
                }
            else if (tapY > -k * tapX + getHeight()) {
                if (activeVx != -1) {
                    vx = 1;
                    vy = 0;
                }
            } else {
                if (activeVy != 1) {
                    vx = 0;
                    vy = -1;
                }
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

    protected void drawTable(Canvas canvas) {

        p.setARGB(255, 0, 0, 0);
        canvas.drawBitmap(background, 0, 0, p);

        p.setARGB(50, 0, 0, 0);

        canvas.drawLine(0, 0, getWidth(), getHeight(), p);
        canvas.drawLine(getWidth(), 0, 0, getHeight(), p);

        for (int i = 0; i < nx; i++)
            canvas.drawLine((float) getWidth() * i / nx, 0, (float) getWidth() * i / nx, getHeight(), p);
        for (int i = 0; i < ny; i++)
            canvas.drawLine(0, (float) getHeight() * i / ny, getWidth(), (float) getHeight() * i / ny, p);
    }

    protected void drawSnake(Canvas canvas) {

        for (int i = 0; i < xm.size(); i++) {
            if (turn.get(i))
                canvas.drawBitmap(body_h, (float) getWidth() * xm.get(i) / nx, (float) getHeight() * ym.get(i) / ny, p);
            else
                canvas.drawBitmap(body_v, (float) getWidth() * xm.get(i) / nx, (float) getHeight() * ym.get(i) / ny, p);
        }

        if ((headX - appleX) * (headX - appleX) <= 4 && (headY - appleY) * (headY - appleY) <= 4) {
            if (vx == 1 || (vx == 0 && vy == 0))
                canvas.drawBitmap(head_right, (float) getWidth() * headX / nx, (float) getHeight() * headY / ny, p);
            if (vx == -1)
                canvas.drawBitmap(head_left, (float) getWidth() * headX / nx, (float) getHeight() * headY / ny, p);
            if (vy == 1)
                canvas.drawBitmap(head_down, (float) getWidth() * headX / nx, (float) getHeight() * headY / ny, p);
            if (vy == -1)
                canvas.drawBitmap(head_up, (float) getWidth() * headX / nx, (float) getHeight() * headY / ny, p);
        } else {
            if (vx == 1 || (vx == 0 && vy == 0))
                canvas.drawBitmap(head_right2, (float) getWidth() * headX / nx, (float) getHeight() * headY / ny, p);
            if (vx == -1)
                canvas.drawBitmap(head_left2, (float) getWidth() * headX / nx, (float) getHeight() * headY / ny, p);
            if (vy == 1)
                canvas.drawBitmap(head_down2, (float) getWidth() * headX / nx, (float) getHeight() * headY / ny, p);
            if (vy == -1)
                canvas.drawBitmap(head_up2, (float) getWidth() * headX / nx, (float) getHeight() * headY / ny, p);
        }
    }

    protected void drawApple(Canvas canvas) {
        p.setColor(Color.RED);
        canvas.drawBitmap(apple, (float) getWidth() * appleX / nx, (float) getHeight() * appleY / ny, p);
    }

    protected void appleSpawn() {
        if (headX == appleX && headY == appleY) {
            Random rnd = new Random();

            boolean out;

            do {
                out = true;

                appleX = rnd.nextInt(nx);
                appleY = rnd.nextInt(ny);

                for (int i = 0; i < xm.size(); i++) {
                    if (appleX == xm.get(i) && appleY == ym.get(i)) out = false;
                }
                if (appleX == headX && appleY == headY) out = false;


            } while (!out);

            xm.add(xm.get(xm.size() - 1));
            ym.add(ym.get(ym.size() - 1));

            turn.add(turn.get(turn.size() - 1));

            stage = 1;

            hrum.start();
        }
    }

    protected void crashSnake() {
        boolean crash;
        crash = false;

        for (int i = 0; i < xm.size(); i++) {
            if (headX == xm.get(i) && headY == ym.get(i)) {
                crash = true;
            }
        }

        if (crash) {

            if (MainActivity.maxScore < xm.size()) {
                MainActivity.maxScore = xm.size();
                MainActivity.saveData(cont);
            }

            xm.clear();
            ym.clear();


            headX = nx / 2;
            headY = ny / 2;

            vx = 0;
            vy = 0;

            xm.add(headX);
            xm.add(headX);

            ym.add(headY);
            ym.add(headY);

            turn.add(true);
            turn.add(true);
        }
    }

    protected void drawScore(Canvas canvas) {
        p.setARGB(150, 255, 255, 255);
        p.setTextSize(size);
        canvas.drawText("score: " + xm.size(), 10, getHeight() - size - 13, p);
        canvas.drawText("max score: " + MainActivity.maxScore, 10, getHeight() - 13, p);
    }
}