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
    private Wall tube1;
    private Wall tube2;
    private Boolean point;

    Context cont;

    MediaPlayer swooshing;
    MediaPlayer die;
    MediaPlayer hit;
    MediaPlayer[] points = new MediaPlayer[2];
    MediaPlayer[] wings = new MediaPlayer[10];


    Bitmap bird;
    Bitmap downTube;
    Bitmap upTube;
    Bitmap fon;
    Bitmap medal;
    Bitmap ground = BitmapFactory.decodeResource(getResources(), R.drawable.earth);
    Bitmap gameOver = BitmapFactory.decodeResource(getResources(), R.drawable.gameover);
    Bitmap results = BitmapFactory.decodeResource(getResources(), R.drawable.end);

    private final int groundVX = -10;
    private final int groundHeight = 100;
    private final int error = 8;
    private final int emptySpace = 550;

    private int wingCounter;
    private int score;
    private static int maxScore = 0;
    private int tubeSpawn;
    private int resultY;
    private int resultEnd;
    private int timerInterval = 1;
    private int groundX = 0;
    private int stage = -5;

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


        Timer t = new Timer();
        t.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (stage >= 0) {
            drawFon(canvas);
            tube1.draw(canvas, getHeight());
            tube2.draw(canvas, getHeight());
            drawScore(canvas, score);
            drawStart(canvas);
            flappy.draw(canvas);
            drawGround(canvas);
            drawResults(canvas);
        } else
            canvas.drawColor(Color.WHITE);
    }

    protected void update() {

        if (stage == 1 || stage == 2) {

            //-----------------------------------UPDATE-----------------------------------
            flappy.update(timerInterval, stage);
            if (stage == 0 || stage == 1)
                if (stage != 0) {
                    tube1.update(tubeSpawn, getHeight());
                    tube2.update(tubeSpawn, getHeight());
                }

            //-----------------------------------POINT-----------------------------------
            if (flappy.getX() + error > tube1.getEdge()) {
                if (!point) {
                    point = true;
                    score++;
                    soundPlay(points[0]);
                }
            }
            if (flappy.getX() + error > tube2.getEdge()) {
                if (point) {
                    point = false;
                    score++;
                    soundPlay(points[1]);
                }
            }

            //-----------------------------------HIT-----------------------------------
            if (flappy.getY() + flappy.getFrameHeight() > getHeight() - groundHeight) {
                flappy.setY(getHeight() - flappy.getFrameHeight() - groundHeight);
                stage = 3;
                resultY = getHeight();
                resultEnd = 0 - gameOver.getHeight();
                if (score > maxScore)
                    medal = BitmapFactory.decodeResource(getResources(), R.drawable.platinummedal);
                else if (score > maxScore * 2 / 3)
                    medal = BitmapFactory.decodeResource(getResources(), R.drawable.goldmedal);
                else if (score > maxScore / 3)
                    medal = BitmapFactory.decodeResource(getResources(), R.drawable.silvermedal);
                else medal = BitmapFactory.decodeResource(getResources(), R.drawable.bronzemedal);
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
        }
        if (stage == -1) {
            timerInterval = 15;
            tubeSpawn = getWidth() + 300;
            restart();
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

    //--------------------------------------------------------------------------------------------------
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

        tube1 = new Wall(emptySpace, 750, downTube.getWidth(), 250, tubeSpawn + upTube.getWidth(), groundVX, downTube, upTube, upTube.getHeight(), groundHeight);
        tube2 = new Wall(emptySpace, 750, downTube.getWidth(), 250, tubeSpawn * 3 / 2 + upTube.getWidth(), groundVX, downTube, upTube, upTube.getHeight(), groundHeight);

        int w = bird.getWidth() / 3;
        int h = bird.getHeight();
        Rect firstFrame = new Rect(0, 0, w, h);
        flappy = new Sprite(200, (float) (getHeight() / 2 + 100), 0, 0, firstFrame, bird);
        flappy.addFrame(new Rect(w, 0, w * 2, h));
        flappy.addFrame(new Rect(w * 2, 0, w * 3, h));

        tube1.generate(getHeight());
        tube2.generate(getHeight());

        point = false;
        wingCounter = 0;

        stage = 0;
        score = 0;
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
            canvas.drawBitmap(medal,
                    (float) (((getWidth() - results.getWidth()) / 2) + (results.getWidth() / 3) - medal.getWidth()),
                    (float) (resultY + ((results.getHeight() - medal.getHeight()) / 2)), p);

            canvas.drawText("" + score, (float) (((getWidth() - results.getWidth()) / 2) + (results.getWidth() * 2 / 3)), (float) (resultY + results.getHeight() * 6 / 16), p);
            canvas.drawText("" + maxScore, (float) (((getWidth() - results.getWidth()) / 2) + (results.getWidth() * 2 / 3)), (float) (resultY + results.getHeight() * 13 / 16), p);

        }
    }

    protected boolean lose(double x, double y) {
        if ((x > tube1.getX() && x < tube1.getEdge()) && (y < tube1.getUpTube() || y > tube1.getDownTube()))
            return true;
        else
            return (x > tube2.getX() && x < tube2.getEdge()) && (y < tube2.getUpTube() || y > tube2.getDownTube());
    }

    protected void drawGround(Canvas canvas) {
        Paint p = new Paint();
        canvas.drawBitmap(ground, groundX, getHeight() - groundHeight, p);
        canvas.drawBitmap(ground, groundX + ground.getWidth(), getHeight() - groundHeight, p);
        if (stage < 2)
            if (stage != 0)
                groundX += groundVX;
        if (-groundX > ground.getWidth()) groundX += ground.getWidth();

    }

    protected void drawScore(Canvas canvas, int score) {
        Paint p = new Paint();
        p.setSubpixelText(true);
        p.setColor(Color.WHITE);
        p.setTextSize(150);

        if (stage == 0 || stage == 1 || stage == 2) {
            if (score < 10)
                canvas.drawText("" + score, (float) (getWidth() / 2) - 50, 300, p);
            else if (score < 100)
                canvas.drawText("" + score, (float) (getWidth() / 2) - 100, 300, p);
            else
                canvas.drawText("" + score, (float) (getWidth() / 2) - 150, 300, p);
        }

    }

    protected void drawStart(Canvas canvas) {
        Paint p = new Paint();

        if (stage == 0) {
            Bitmap tap = BitmapFactory.decodeResource(getResources(), R.drawable.taptostart);
            canvas.drawBitmap(tap, (float) (getWidth() - tap.getWidth()) / 2, (float) getHeight() / 2 + 100, p);

            Bitmap ready = BitmapFactory.decodeResource(getResources(), R.drawable.getready);
            canvas.drawBitmap(ready, (float) (getWidth() - ready.getWidth()) / 2, (float) getHeight() / 2 - 100 - ready.getHeight(), p);
        }
    }

    protected void drawFon(Canvas canvas) {
        Paint p = new Paint();
        canvas.drawBitmap(fon, (float) (getWidth() - fon.getWidth()) / 2, (float) (getHeight() - fon.getHeight()) / 2, p);
    }
}