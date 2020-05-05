package com.example.flappy2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

public class GameView extends View {

    private Sprite flappy;
    private Numbers numbers;
    private Numbers resultNumbers;
    Wall[] tube = new Wall[2];
    private Boolean point;

    Context cont;

    MediaPlayer swooshing;
    MediaPlayer die;
    MediaPlayer hit;
    MediaPlayer[] points = new MediaPlayer[2];
    MediaPlayer[] wings = new MediaPlayer[10];

    Bitmap redBird = BitmapFactory.decodeResource(getResources(), R.drawable.rbird);
    Bitmap blueBird = BitmapFactory.decodeResource(getResources(), R.drawable.bbird);
    Bitmap yellowBird = BitmapFactory.decodeResource(getResources(), R.drawable.ybird);
    Bitmap currentBird = yellowBird;

    Bitmap redDownTube = BitmapFactory.decodeResource(getResources(), R.drawable.reddowntube);
    Bitmap greenDownTube = BitmapFactory.decodeResource(getResources(), R.drawable.greendowntube);
    Bitmap currentDownTube = greenDownTube;

    Bitmap redUpTube = BitmapFactory.decodeResource(getResources(), R.drawable.reduptube);
    Bitmap greenUpTube = BitmapFactory.decodeResource(getResources(), R.drawable.greenuptube);
    Bitmap currentUpTube = greenUpTube;

    Bitmap darkFon = BitmapFactory.decodeResource(getResources(), R.drawable.darkfon);
    Bitmap lightFon = BitmapFactory.decodeResource(getResources(), R.drawable.lightfon);
    Bitmap currentFon = darkFon;

    Bitmap platinumMedal = BitmapFactory.decodeResource(getResources(), R.drawable.platinummedal);
    Bitmap goldMedal = BitmapFactory.decodeResource(getResources(), R.drawable.goldmedal);
    Bitmap silverMedal = BitmapFactory.decodeResource(getResources(), R.drawable.silvermedal);
    Bitmap bronzeMedal = BitmapFactory.decodeResource(getResources(), R.drawable.bronzemedal);
    Bitmap currentMedal = platinumMedal;

    Bitmap ground = BitmapFactory.decodeResource(getResources(), R.drawable.ground);
    Bitmap gameOver = BitmapFactory.decodeResource(getResources(), R.drawable.gameover);
    Bitmap results = BitmapFactory.decodeResource(getResources(), R.drawable.end);
    Bitmap num = BitmapFactory.decodeResource(getResources(), R.drawable.numbers);
    Bitmap smallNum = BitmapFactory.decodeResource(getResources(), R.drawable.smallnumbers2);

    private final int vx = -10;
    private final int groundHeight = 300;

    private int wingCounter;
    private int score;
    private static int maxScore = 0;
    private int tubeSpawn;
    private int resultY;
    private int resultEnd;
    private int timerInterval = 1;
    private int groundX = 0;
    private int stage = -3;

    /*
     * stage
     * -1 - skip first 5 frames
     *  0 - tap to start
     *  1 - game play
     *  2 - flappy was stopped by tube
     *  3 - flappy was stopped by ground
     *  4 - tap to restart
     * */

    public GameView(Context context) {
        super(context);
        cont = context;

        load();
        MainActivity.loadData(cont);


        //-------------------------------
        int w = num.getWidth() / 10;
        int h = num.getHeight();

        Rect firstFrame = new Rect(0, 0, w, h);
        numbers = new Numbers(0, 150, firstFrame, num);

        for (int i = 1; i < 10; i++)
            numbers.addFrame(new Rect(w * i, 0, w * (i + 1), h));

        //-------------------------------
        w = smallNum.getWidth() / 10;
        h = smallNum.getHeight();

        firstFrame = new Rect(0, 0, w, h);
        resultNumbers = new Numbers(0, 150, firstFrame, smallNum);

        for (int i = 1; i < 10; i++)
            resultNumbers.addFrame(new Rect(w * i, 0, w * (i + 1), h));
        //-------------------------------

        Timer t = new Timer();
        t.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (stage >= 0) {
            drawFon(canvas);
            for (Wall wall : tube) wall.draw(canvas);
            drawScore(canvas, score);
            if (stage == 0) drawStart(canvas);
            flappy.draw(canvas, stage);
            drawGround(canvas);
            drawResults(canvas);
        }
    }

    protected void update() {

        if (stage == 1 || stage == 2) {

            //-----------------------------------UPDATE-----------------------------------
            flappy.update(stage);

            if (stage == 1)
                for (Wall wall : tube)
                    wall.update(tubeSpawn, getHeight());

            //-----------------------------------POINT-----------------------------------
            int error = 15;

            if (flappy.getX() + error > tube[0].getEdge() && !point) {
                point = true;
                score++;
                soundPlay(points[0]);
            }

            if (flappy.getX() + error > tube[1].getEdge() && point) {
                point = false;
                score++;
                soundPlay(points[1]);
            }

            //-----------------------------------HIT-----------------------------------
            if (flappy.getY() + flappy.getFrameHeight() > getHeight() - groundHeight) {

                flappy.setY(getHeight() - flappy.getFrameHeight() - groundHeight);
                resultY = getHeight();
                stage = 3;

                resultEnd = -gameOver.getHeight();

                if (score > maxScore)
                    currentMedal = platinumMedal;
                else if (score > maxScore * 2 / 3)
                    currentMedal = goldMedal;
                else if (score > maxScore / 3)
                    currentMedal = silverMedal;
                else
                    currentMedal = bronzeMedal;

                soundPlay(hit);

                if (score > maxScore) maxScore = score;

                if (MainActivity.saveThis < maxScore) {
                    MainActivity.saveThis = maxScore;
                    MainActivity.saveData(cont);
                } else {
                    maxScore = MainActivity.saveThis;
                }
            }

            //-----------------------------------CHECK-----------------------------------
            if (stage == 1) {
                if (lose(flappy.getX() + error, flappy.getY())) {
                    stage = 2;
                    flappy.setVy(flappy.getVy() + 5);
                    soundPlay(die);
                }
                if (stage != 2)
                    if (lose(flappy.getX() + flappy.getFrameWidth() - error, flappy.getY())) {
                        stage = 2;
                        flappy.setVy(flappy.getVy() + 5);
                        soundPlay(die);
                    }
                if (stage != 2)
                    if (lose(flappy.getX() + error, flappy.getY() + flappy.getFrameHeight())) {
                        stage = 2;
                        flappy.setVy(flappy.getVy() + 5);
                        soundPlay(die);
                    }
                if (stage != 2)
                    if (lose(flappy.getX() + flappy.getFrameWidth() - error, flappy.getY() + flappy.getFrameHeight())) {
                        stage = 2;
                        flappy.setVy(flappy.getVy() + 5);
                        soundPlay(die);
                    }
            }
        }

        //-----------------------------------SKIP-FIRST-FRAME-----------------------------------
        if (stage < -1) {
            stage++;
            if (stage == -1) {
                timerInterval = 10;
                tubeSpawn = getWidth() + 400;

                tube[0] = new Wall(currentDownTube.getWidth(), 250, tubeSpawn + currentUpTube.getWidth(), vx, currentDownTube, currentUpTube, currentUpTube.getHeight(), groundHeight);
                tube[1] = new Wall(currentDownTube.getWidth(), 250, (float)tubeSpawn * 3 / 2 + currentUpTube.getWidth(), vx, currentDownTube, currentUpTube, currentUpTube.getHeight(), groundHeight);

                restart();
            }
        }

        invalidate();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int eventAction = event.getAction();
        if (eventAction == MotionEvent.ACTION_DOWN) {

            //-----------------------------------FLY-----------------------------------
            if (stage == 0 || stage == 1) {
                flappy.setVy(-25);
                stage = 1;
                soundPlay(wings[wingCounter]);
                wingCounter++;
                wingCounter %= wings.length;
            }

            //-----------------------------------RESTART-----------------------------------
            if (stage == 4) {
                soundPlay(swooshing);
                restart();
            }
        }
        return true;
    }

    //--@---@---@@@@@---@-------@-------@@@@@----------@----@---@---@@@@@---@@@@@---@-------@@@@----
    //--@---@---@-------@-------@-------@---@----------@----@---@---@---@---@-------@-------@---@---
    //--@@@@@---@@@@@---@-------@-------@---@-----------@--@-@-@----@---@---@-------@-------@---@---
    //--@---@---@-------@-------@-------@---@-----------@--@-@-@----@---@---@-------@-------@---@---
    //--@---@---@@@@@---@@@@@---@@@@@---@@@@@------------@----@-----@@@@@---@-------@@@@@---@@@@----

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

    private void load() {
        swooshing = MediaPlayer.create(cont, R.raw.sfx_swooshing);
        die = MediaPlayer.create(cont, R.raw.sfx_die);
        hit = MediaPlayer.create(cont, R.raw.sfx_hit);

        for (int i = 0; i < points.length; i++)
            points[i] = MediaPlayer.create(cont, R.raw.sfx_point);

        for (int i = 0; i < wings.length; i++)
            wings[i] = MediaPlayer.create(cont, R.raw.sfx_wing);
    }

    protected void soundPlay(MediaPlayer s) {
        s.start();
    }

    protected void restart() {

        Random rnd = new Random();

        int r = rnd.nextInt(3);
        switch (r) {
            case (0):
                currentBird = yellowBird;
                break;
            case (1):
                currentBird = redBird;
                break;
            default:
                currentBird = blueBird;
                break;
        }

        r = rnd.nextInt(2);
        if (r == 0)
            currentFon = lightFon;
        else
            currentFon = darkFon;


        r = rnd.nextInt(2);
        if (r == 0) {
            currentDownTube = greenDownTube;
            currentUpTube = greenUpTube;
        } else {
            currentDownTube = redDownTube;
            currentUpTube = redUpTube;
        }

        tube[0].x = tubeSpawn + currentUpTube.getWidth();
        tube[1].x = (float) tubeSpawn * 3 / 2 + currentUpTube.getWidth();

        int w = currentBird.getWidth() / 3;
        int h = currentBird.getHeight();

        Rect firstFrame = new Rect(0, 0, w, h);
        flappy = new Sprite(200, (float) (getHeight() / 2 + 100), 0, 0, firstFrame, currentBird);
        flappy.addFrame(new Rect(w, 0, w * 2, h));
        flappy.addFrame(new Rect(w * 2, 0, w * 3, h));

        for (Wall wall : tube) {
            wall.generate(getHeight());
        }

        point = false;
        wingCounter = 0;

        stage = 0;
        score = 0;
    }

    protected boolean lose(double x, double y) {
        if ((x > tube[0].getX() && x < tube[0].getEdge()) && (y < tube[0].getUpTube() || y > tube[0].getDownTube()))
            return true;
        else
            return (x > tube[1].getX() && x < tube[1].getEdge()) && (y < tube[1].getUpTube() || y > tube[1].getDownTube());
    }

    protected void drawResults(Canvas canvas) {
        if (stage == 3 || stage == 4) {
            Paint p = new Paint();

            p.setTextSize(90);
            p.setColor(Color.WHITE);

            if (resultEnd <= getHeight() / 2 - gameOver.getHeight() - 100) {
                resultEnd += 10;
            }
            canvas.drawBitmap(gameOver, (float) (getWidth() - gameOver.getWidth()) / 2, resultEnd, p);

            if (resultY >= getHeight() / 2 + 100) {
                resultY -= 10;
            } else {
                stage = 4;
            }
            canvas.drawBitmap(results, (float) (getWidth() - results.getWidth()) / 2, resultY, p);
            canvas.drawBitmap(currentMedal,
                    (float) (((getWidth() - results.getWidth()) / 2) + (results.getWidth() / 3) - currentMedal.getWidth()),
                    (float) (resultY + ((results.getHeight() - currentMedal.getHeight()) / 2)), p);

            resultNumbers.setX((float) ((getWidth() - results.getWidth()) / 2 + (results.getWidth() * 2 / 3)));
            resultNumbers.setY((float) (resultY + 100));
            resultNumbers.draw(canvas, score);

            resultNumbers.setX((float) ((getWidth() - results.getWidth()) / 2 + (results.getWidth() * 2 / 3)));
            resultNumbers.setY((float) (resultY + results.getHeight() / 2 + 100));
            resultNumbers.draw(canvas, maxScore);
        }
    }

    protected void drawGround(Canvas canvas) {
        Paint p = new Paint();
        canvas.drawBitmap(ground, groundX, getHeight() - groundHeight, p);
        canvas.drawBitmap(ground, groundX + ground.getWidth(), getHeight() - groundHeight, p);
        if (stage < 2)
            if (stage != 0)
                groundX += vx;
        if (-groundX > ground.getWidth()) groundX += ground.getWidth();
    }

    protected void drawScore(Canvas canvas, int score) {

        if (score < 10)
            numbers.setX((int) (getWidth() / 2 - numbers.getFrameWidth() / 2));
        else {
            if (score < 100) {
                numbers.setX((int) (getWidth() / 2 - numbers.getFrameWidth()));
            } else {
                numbers.setX((int) (getWidth() / 2 - numbers.getFrameWidth() * 3 / 2));
            }
        }

        numbers.draw(canvas, score);
    }

    protected void drawStart(Canvas canvas) {
        Paint p = new Paint();

        Bitmap tap = BitmapFactory.decodeResource(getResources(), R.drawable.taptostart);
        canvas.drawBitmap(tap, (float) (getWidth() - tap.getWidth()) / 2, (float) getHeight() / 2 + 100, p);

        Bitmap ready = BitmapFactory.decodeResource(getResources(), R.drawable.getready);
        canvas.drawBitmap(ready, (float) (getWidth() - ready.getWidth()) / 2, (float) getHeight() / 2 - 100 - ready.getHeight(), p);
    }

    protected void drawFon(Canvas canvas) {
        Paint p = new Paint();
        canvas.drawBitmap(currentFon, (float) (getWidth() - currentFon.getWidth()) / 2, (float) (getHeight() - currentFon.getHeight()) / 2, p);
    }
}