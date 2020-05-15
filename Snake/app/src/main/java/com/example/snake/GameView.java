package com.example.snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.view.View;

import java.util.LinkedList;
import java.util.Random;

public class GameView extends View {

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
    Bitmap apple = BitmapFactory.decodeResource(getResources(), R.drawable.apple);
    Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.logo);


    private int timerInterval = 200;
    private int stage = -10;
    private int nx;
    private int ny;
    private int size = body_h.getWidth();
    public static int vx = 0;
    public static int vy = 0;
    public static int activeVx = 0;
    public static int activeVy = 0;
    private int headX = -1;
    private int headY = -1;
    private int appleX = -2;
    private int appleY = -2;

    public GameView(Context context) {
        super(context);
        cont = context;
        p.setAntiAlias(true);

        xm.add(-2);
        xm.add(-2);
        ym.add(-2);
        ym.add(-2);

        turn.add(true);
        turn.add(true);

        hrum = MediaPlayer.create(cont, R.raw.apple_sound);

        MainActivity.loadData(cont);

        Timer t = new Timer();
        t.start();
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

    protected void update() {

        if (stage < -1) {
            stage++;
            if (stage == -1) {
                nx = getWidth() / size;
                ny = getHeight() / size;

                headX = nx / 2;
                headY = ny / 2;

                appleX = headX;
                appleY = headY;

                spawn();

                stage = 0;
            }
        } else {

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

            appleSpawn();

            crash();
        }

        invalidate();
    }

    protected void appleSpawn() {
        if (headX == appleX && headY == appleY) {

            spawn();

            xm.add(xm.get(xm.size() - 1));
            ym.add(ym.get(ym.size() - 1));

            turn.add(turn.get(turn.size() - 1));

            hrum.start();
        }
    }

    protected void spawn() {

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

    }

    protected void crash() {
        boolean crash;
        crash = false;

        if (vx != 0 || vy != 0)
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

            spawn();

            vx = 0;
            vy = 0;

            xm.add(-2);
            xm.add(-2);
            ym.add(-2);
            ym.add(-2);

            turn.add(true);
            turn.add(true);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (stage >= 0) {
            drawTable(canvas);
            drawApple(canvas);
            drawSnake(canvas);
            drawScore(canvas);
        } else {
            vx = 0;
            vy = 0;
            canvas.drawARGB(255, 0, 0, 0);
            canvas.drawBitmap(logo, (float) (getWidth() - logo.getWidth()) / 2, (float) (getHeight() - logo.getHeight()) / 2, p);
        }
    }

    protected void drawTable(Canvas canvas) {

        p.setARGB(255, 0, 0, 0);
        canvas.drawBitmap(background, (float) (getWidth() - background.getWidth()) / 2, (float) (getHeight() - background.getHeight()) / 2, p);

        p.setARGB(40, 0, 0, 0);

        for (int i = 0; i < nx; i++)
            canvas.drawLine((float) getWidth() * i / nx, 0, (float) getWidth() * i / nx, getHeight(), p);
        for (int i = 0; i < ny; i++)
            canvas.drawLine(0, (float) getHeight() * i / ny, getWidth(), (float) getHeight() * i / ny, p);
    }

    protected void drawSnake(Canvas canvas) {

        for (int i = xm.size() - 1; i >= 0; i--) {
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

    protected void drawScore(Canvas canvas) {
        p.setARGB(150, 255, 255, 255);
        p.setTextSize(size);
        canvas.drawText("score: " + (xm.size() - 2), 10, getHeight() - size - 13, p);
        canvas.drawText("max score: " + (MainActivity.maxScore - 2), 10, getHeight() - 13, p);
    }
}
