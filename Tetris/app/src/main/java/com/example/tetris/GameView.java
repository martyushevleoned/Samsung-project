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

    @SuppressLint("StaticFieldLeak")
    static Context cont;

    Bitmap block1 = BitmapFactory.decodeResource(getResources(), R.drawable.purple_block);
    Bitmap block2 = BitmapFactory.decodeResource(getResources(), R.drawable.dkblu_block);
    Bitmap block3 = BitmapFactory.decodeResource(getResources(), R.drawable.orange_block);
    Bitmap block4 = BitmapFactory.decodeResource(getResources(), R.drawable.green_block);
    Bitmap block5 = BitmapFactory.decodeResource(getResources(), R.drawable.red_block);
    Bitmap block6 = BitmapFactory.decodeResource(getResources(), R.drawable.ltblu_block);
    Bitmap block7 = BitmapFactory.decodeResource(getResources(), R.drawable.yelow_block);
    Bitmap currentBlock;
    Bitmap smallBlock1 = BitmapFactory.decodeResource(getResources(), R.drawable.purpleblock);
    Bitmap smallBlock2 = BitmapFactory.decodeResource(getResources(), R.drawable.dkblublock);
    Bitmap smallBlock3 = BitmapFactory.decodeResource(getResources(), R.drawable.orangeblock);
    Bitmap smallBlock4 = BitmapFactory.decodeResource(getResources(), R.drawable.greenblock);
    Bitmap smallBlock5 = BitmapFactory.decodeResource(getResources(), R.drawable.redblock);
    Bitmap smallBlock6 = BitmapFactory.decodeResource(getResources(), R.drawable.ltblublock);
    Bitmap smallBlock7 = BitmapFactory.decodeResource(getResources(), R.drawable.yelowblock);
    Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.bg);

    private Bitmap[] block = {block1, block2, block3, block4, block5, block6, block7};
    private Bitmap[] smallBlock = {smallBlock1, smallBlock2, smallBlock3, smallBlock4, smallBlock5, smallBlock6, smallBlock7};

    Paint p = new Paint();


    private int width;
    private int height;
    private int menu = (smallBlock1.getWidth() + 5) * 5;
    private int size = block1.getWidth() + 10;
    private int hei;
    private int[] futureShapes;

    public static int score = 0;
    public static int stage = -10;
    public static int counter = 0;
    public static int timerInterval = 25;
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
                    {{0, 0, 0, 0}, {0, -1, 1, -2}},
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
        p.setTextSize(size);
        MainActivity.loadData(context);

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

            drawMenu(canvas);

            for (int i = 0; i < shape[shapeNum][direction][0].length; i++) {
                drawPix(canvas, mainBlockX + shape[shapeNum][direction][0][i], mainBlockY + shape[shapeNum][direction][1][i], currentBlock);
            }

            drawScore(canvas);

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

                hei = getHeight() / (menu / 5);

                area = new int[w][h];

                Random rnd = new Random();

                futureShapes = new int[3];

                for (int i = 0; i < futureShapes.length; i++)
                    futureShapes[i] = rnd.nextInt(block.length);

                newShape();
                stage = 0;
            }
        } else {
            if (stage != 1) {
                if (counter == 0) {

                    stopCheck();

                }
                counter++;
                counter %= 10;

                crashCheck();
            }

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
                score++;
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

    protected void crashCheck() {
        boolean t = false;
        for (int[] ints : area) {
            if (ints[0] != 0) {
                t = true;
                break;
            }
        }

        if (t) {
            stage = 1;
        }
    }

    protected void newShape() {

        Random rnd = new Random();

        currentBlock = block[futureShapes[0]];
        shapeNum = futureShapes[0];

        System.arraycopy(futureShapes, 1, futureShapes, 0, futureShapes.length - 1);

        futureShapes[futureShapes.length - 1] = rnd.nextInt(block.length);
        timerInterval = 50;
        direction = 0;
        mainBlockY = -4;
        mainBlockX = w / 2;
    }

    protected void drawPix(Canvas canvas, int x, int y, Bitmap b) {
        if (y < h && y >= 0)
            canvas.drawBitmap(b, 5 + (float) width * x / w, 5 + (float) height * y / h, p);
    }

    protected void drawMenuPix(Canvas canvas, int x, int y, Bitmap b) {
        canvas.drawBitmap(b, 5 + width + (float) menu * x / 5, 5 + (float) getHeight() * y / hei, p);
    }

    protected void drawMatrix(Canvas canvas) {

        for (int i = 0; i < area.length; i++) {
            for (int j = 0; j < area[i].length; j++) {
                if (area[i][j] != 0)
                    drawPix(canvas, i, j, block[area[i][j] - 1]);
            }
        }
    }

    protected void drawMenu(Canvas canvas) {

//        p.setColor(Color.WHITE);
//        for (int i = 0; i <= 5; i++) {
//            canvas.drawLine(width + (float) menu * i / 5, 0, width + (float) menu * i / 5, getHeight(), p);
//        }
//
//        for (int i = 0; i <= hei; i++) {
//            canvas.drawLine(width, (float) getHeight() * i / hei, getWidth(), (float) getHeight() * i / hei, p);
//
//        }

        for (int j = 0; j < futureShapes.length; j++)
            for (int i = 0; i < shape[shapeNum][direction][0].length; i++) {
                drawMenuPix(canvas, 2 + shape[futureShapes[j]][0][0][i], 3 + 5 * j + shape[futureShapes[j]][0][1][i], smallBlock[futureShapes[j]]);
            }
    }

    protected void drawTable(Canvas canvas) {

        p.setColor(Color.BLACK);
        canvas.drawRect(0, 0, width, getHeight(), p);

        p.setColor(Color.DKGRAY);
        canvas.drawRect(width, 0, getWidth(), getHeight(), p);


        canvas.drawBitmap(background, width - background.getWidth(), getHeight() - background.getHeight(), p);

//        p.setColor(Color.WHITE);
//        for (int i = 0; i <= w; i++) {
//            canvas.drawLine((float) width * i / w, 0, (float) width * i / w, height, p);
//        }
//        for (int i = 0; i <= h; i++) {
//            canvas.drawLine(0, (float) height * i / h, width, (float) height * i / h, p);
//        }
    }

    protected void drawScore(Canvas canvas) {
        p.setARGB(255, 0, 0, 0);
        canvas.drawText("score", width + 3, getHeight() - size - 7, p);
        canvas.drawText("" + score, width + 3, getHeight() - 7, p);

        p.setARGB(255, 255, 255, 255);
        if (stage == 1) {
            if (MainActivity.maxScore < score)
                canvas.drawText("New record: " + score, (float) width / 2 - 220, (float) getHeight() / 2, p);
            canvas.drawText("Tap to restart", (float) width / 2 - 225, (float) getHeight() / 2 + size, p);
        }
    }

    public static boolean restart() {

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                area[i][j] = 0;
            }
        }

        if (score > MainActivity.maxScore) {
            MainActivity.maxScore = score;
            MainActivity.saveData(cont);
        }

        stage = 0;
        score = 0;

        return false;
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
            else return area[x - 1][0] == 0;
        else
            return false;
    }

    public static boolean rotateCheck(int x, int y) {
        if (x >= 0 && x < w && y >= 0 && y < h)
            return area[x][y] == 0;
        else
            return false;
    }
}