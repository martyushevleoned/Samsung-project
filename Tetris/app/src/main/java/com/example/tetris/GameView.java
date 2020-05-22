package com.example.tetris;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.view.View;

@SuppressLint("ViewConstructor")
public class GameView extends View {

    Context cont;

    Bitmap block = BitmapFactory.decodeResource(getResources(), R.drawable.square3);

    Paint p = new Paint();

    private int stage = -10;
    private int counter = 0;
    private int size = block.getWidth() + 10;
    public static int timerInterval = 50;
    public static int h;
    public static int w = 10;
    public static int mainBlockX;
    public static int mainBlockY = -1;
    public static int direction = 0;
    public static int shapeNum = 0;
    public static int[][] area;
    public static int[][][][] shape = {
            {
                    {{0, 1, -1, 0}, {0, 0, 0, -1}},
                    {{0, 0, 0, 1}, {0, 1, -1, 0}},
                    {{0, 1, -1, 0}, {0, 0, 0, 1}},
                    {{0, 0, 0, -1}, {0, 1, -1, 0}}
            },
            {
                    {{0, 0, 0, 1}, {0, -1, 1, -1}},
                    {{0, -1, 1, 1}, {0, 0, 0, 1}},
                    {{0, 0, 0, -1}, {0, -1, 1, 1}},
                    {{0, -1, 1, -1}, {0, 0, 0, -1}}
            },
            {
                    {{0, 0, 0, -1}, {0, -1, 1, -1}},
                    {{0, 1, -1, 1}, {0, 0, 0, -1}},
                    {{0, 0, 0, 1}, {0, -1, 1, 1}},
                    {{0, 1, -1, -1}, {0, 0, 0, 1}}
            },
            {
                    {{0, 0, 1, -1}, {0, -1, -1, 0}},
                    {{0, -1, -1, 0}, {0, 0, -1, 1}}
            },
            {
                    {{0, 0, -1, 1}, {0, -1, -1, 0}},
                    {{0, 1, 1, 0}, {0, 0, -1, 1}}
            },
            {
                    {{0, 0, 0, 0}, {0, -1, 1, 2}},
                    {{0, -1, 1, 2}, {0, 0, 0, 0}}
            },
            {
                    {{0, 1, 0, 1}, {0, 1, 1, 0}}
            }
    };

    public GameView(Context context) {
        super(context);
        cont = context;
        p.setAntiAlias(true);

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

    //----------------------------------------------------------------------------------------------
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (stage >= 0) {

            canvas.drawColor(Color.DKGRAY);

            drawMatrix(canvas);

            for (int i = 0; i < shape[shapeNum][direction][0].length; i++) {
                drawPix(canvas, mainBlockX + shape[shapeNum][direction][0][i], mainBlockY + shape[shapeNum][direction][1][i]);
            }

//            drawTable(canvas);

        } else {
            canvas.drawARGB(255, 0, 0, 0);
        }
    }

    protected void update() {

        if (stage < -1) {
            stage++;
            if (stage == -1) {

                h = getHeight() / size;
                w = getWidth() / size;

                area = new int[w][h];

                mainBlockX = w / 2 - 1;
                stage = 0;
            }
        } else {

            if (counter == 0) {

                stopCheck();

            }
            counter++;
            counter %= 6;


        }
        invalidate();
    }

    protected void deleteLayer() {
        boolean t;

        for (int i1 = 0; i1 < h; i1++) {

            t = true;

            for (int i = 0; i < w; i++) {
                if (area[i][i1] == 0) {
                    t = false;
                    break;
                }
            }

            if (t) {
                for (int j1 = i1; j1 > 0; j1--) {
                    for (int j = 0; j < w; j++) {
                        area[j][j1] = area[j][j1 - 1];
                    }
                }
            }
        }
    }

    protected void stopCheck() {

        boolean t = false;

        for (int i = 0; i < shape[shapeNum][direction][0].length; i++) {
            if (mainBlockY + shape[shapeNum][direction][1][i] >= h - 1) {
                t = true;
                break;
            }
            if (mainBlockY + shape[shapeNum][direction][1][i] + 1 < h && mainBlockY + shape[shapeNum][direction][1][i] + 1 >= 0)
                if (area[mainBlockX + shape[shapeNum][direction][0][i]][mainBlockY + shape[shapeNum][direction][1][i] + 1] != 0) {
                    t = true;
                    break;
                }
        }

        if (t) {
            for (int i = 0; i < shape[shapeNum][direction][0].length; i++) {
                if (mainBlockY + shape[shapeNum][direction][1][i] < h)
                    if (mainBlockX + shape[shapeNum][direction][0][i] < h && mainBlockY + shape[shapeNum][direction][1][i] >= 0)
                        area[mainBlockX + shape[shapeNum][direction][0][i]][mainBlockY + shape[shapeNum][direction][1][i]] = 1;
            }

            deleteLayer();

            newShape();
        } else
            mainBlockY++;
    }

    protected void newShape() {
        boolean t = false;
        for (int[] ints : area) {
            if (ints[0] != 0) {
                t = true;
                break;
            }
        }

        if (t) {
            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    area[i][j] = 0;
                }
            }
        }

        timerInterval = 50;
        shapeNum++;
        shapeNum %= shape.length;
        direction = 0;
        mainBlockY = -4;
        mainBlockX = w / 2 - 1;
    }

    protected void drawPix(Canvas canvas, int x, int y) {
        if (y < h && y >= 0)
            canvas.drawBitmap(block, 4 + (float) getWidth() * x / w, 4 + (float) getHeight() * y / h, p);
    }

    protected void drawMatrix(Canvas canvas) {

        for (int i = 0; i < area.length; i++) {
            for (int j = 0; j < area[i].length; j++) {
                if (area[i][j] != 0)
                    drawPix(canvas, i, j);
            }
        }
    }

    protected void drawTable(Canvas canvas) {
        p.setColor(Color.BLACK);
        for (int i = 0; i <= w; i++) {
            canvas.drawLine((float) getWidth() * i / w, 0, (float) getWidth() * i / w, getHeight(), p);
        }
        for (int i = 0; i <= h; i++) {
            canvas.drawLine(0, (float) getHeight() * i / h, getWidth(), (float) getHeight() * i / h, p);
        }
    }

    public static boolean rightCheck(int x, int y) {

        if (x < w - 1)
            if (x == w - 2)
                return true;
            else return area[x + 1][y] == 0;
        else
            return false;
    }

    public static boolean leftCheck(int x, int y) {
        if (x > 0)
            if (x == 1)
                return true;
            else return area[x - 1][y] == 0;
        else
            return false;
    }

    public static boolean rotateCheck(int x, int y) {
        if (x >= 0 && x < w)
            return area[x][y] == 0;
        else
            return false;
    }
}