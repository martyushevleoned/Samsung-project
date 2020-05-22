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

import java.util.Random;

@SuppressLint("ViewConstructor")
public class GameView extends View {

    Context cont;

    Bitmap block1 = BitmapFactory.decodeResource(getResources(), R.drawable.firstblock);
    Bitmap block2 = BitmapFactory.decodeResource(getResources(), R.drawable.secondblock);
    Bitmap block3 = BitmapFactory.decodeResource(getResources(), R.drawable.thirdblock);
    Bitmap block4 = BitmapFactory.decodeResource(getResources(), R.drawable.fourthblock);
    Bitmap block5 = BitmapFactory.decodeResource(getResources(), R.drawable.fifthblock);

    Bitmap currentBlock;

    Paint p = new Paint();

    private int stage = -10;
    private int counter = 0;
    private int width;
    private int height;
    private int menu = 100;
    private int size = block1.getWidth() + 10;
    public static int timerInterval = 30;
    public static int h;
    public static int w = 10;
    public static int mainBlockX;
    public static int mainBlockY = -1;
    public static int direction = 0;
    public static int shapeNum = 0;

    private Bitmap[] futureColors;
    public static int[] futureShapes;

    private Bitmap[] block = {block1, block2, block3, block4, block5};
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

            drawTable(canvas);

            drawMatrix(canvas);

            for (int i = 0; i < shape[shapeNum][direction][0].length; i++) {
                drawPix(canvas, mainBlockX + shape[shapeNum][direction][0][i], mainBlockY + shape[shapeNum][direction][1][i], currentBlock);
            }

        } else {
            canvas.drawARGB(255, 0, 0, 0);
        }
    }

    protected void update() {

        if (stage < -1) {
            if (getWidth() != 0 && getHeight() != 0)
                stage++;
            if (stage == -1) {

                width = getWidth() - menu;
                height = getHeight();

                w = width / size;
                h = height / size;

                area = new int[w][h];

                newShape();
                stage = 0;
            }
        } else {

            if (counter == 0) {

                stopCheck();

            }
            counter++;
            counter %= 10;


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

            int n = 0;


            for (int i = 0; i < block.length; i++) {
                if (block[i].equals(currentBlock)) {
                    n = i + 1;
                    break;
                }
            }


            for (int i = 0; i < shape[shapeNum][direction][0].length; i++) {
                if (mainBlockY + shape[shapeNum][direction][1][i] < h)
                    if (mainBlockX + shape[shapeNum][direction][0][i] < h && mainBlockY + shape[shapeNum][direction][1][i] >= 0)
                        area[mainBlockX + shape[shapeNum][direction][0][i]][mainBlockY + shape[shapeNum][direction][1][i]] = n;
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

        Random rnd = new Random();

        currentBlock = block[rnd.nextInt(block.length)];

        timerInterval = 50;
        shapeNum = rnd.nextInt(shape.length);
        direction = 0;
        mainBlockY = -4;
        mainBlockX = w / 2 - 1;
    }

    protected void drawPix(Canvas canvas, int x, int y, Bitmap b) {
        if (y < h && y >= 0)
            canvas.drawBitmap(b, 4 + (float) width * x / w, 4 + (float) height * y / h, p);
    }

    protected void drawMatrix(Canvas canvas) {

        for (int i = 0; i < area.length; i++) {
            for (int j = 0; j < area[i].length; j++) {
                if (area[i][j] != 0)

                    drawPix(canvas, i, j, block[area[i][j] - 1]);


            }
        }
    }

    protected void drawTable(Canvas canvas) {

        p.setColor(Color.DKGRAY);
        canvas.drawRect(0, 0, width, getHeight(), p);

        p.setColor(Color.BLUE);
        canvas.drawRect(width, 0, getWidth(), getHeight(), p);

//        p.setColor(Color.WHITE);
//        for (int i = 0; i <= w; i++) {
//            canvas.drawLine((float) width * i / w, 0, (float) width * i / w, height, p);
//        }
//        for (int i = 0; i <= h; i++) {
//            canvas.drawLine(0, (float) height * i / h, width, (float) height * i / h, p);
//        }
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
        if (x >= 0 && x < w && y >= 0)
            return area[x][y] == 0;
        else
            return false;
    }
}