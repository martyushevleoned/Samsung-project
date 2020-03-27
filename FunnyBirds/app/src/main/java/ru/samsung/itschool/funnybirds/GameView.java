package ru.samsung.itschool.funnybirds;

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


public class GameView extends View {

    private Sprite playerBird;
    private Wall tube;

    private int viewWidth;
    private int viewHeight;
    private Boolean crash;

    private final int timerInterval = 15;

    public GameView(Context context) {
        super(context);

        tube = new Wall(100, 100, 100, 100, 100, -5);

        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.man);
        int w = b.getWidth() / 8;
        int h = b.getHeight() / 8;
        Rect firstFrame = new Rect(0, 0, w, h);
        playerBird = new Sprite(10, 0, 0, 100, firstFrame, b);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
//                if (i == 2 && j == 3) {
//                    continue;
//                }
                playerBird.addFrame(new Rect(j * w, i * h, (j + 1) * w, (i + 1) * w));
            }
        }

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
        canvas.drawARGB(250, 127, 199, 255);
        playerBird.draw(canvas);
        tube.draw(canvas, getHeight(), crash);

        Paint p = new Paint();
        p.setAntiAlias(true);
    }

    protected void update() {
        playerBird.update(timerInterval);
        crash = false;

        if (playerBird.getY() + playerBird.getFrameHeight() > viewHeight) {
            playerBird.setY(viewHeight - playerBird.getFrameHeight());
            playerBird.setVy(0);
        }
        if (playerBird.getY() < 0) {
            playerBird.setY(0);
            playerBird.setVy(0);
        }


        tube.update();

        if (tube.getEdge() < 0) {
            tube.setX(getWidth());
            tube.generate(getHeight());
        }

        if (lose(playerBird.getX(),
                playerBird.getY()))
            crash = true;
        if (lose(playerBird.getX() + playerBird.getFrameWidth(),
                playerBird.getY()))
            crash = true;
        if (lose(playerBird.getX(),
                playerBird.getY() + playerBird.getFrameHeight()))
            crash = true;
        if (lose(playerBird.getX() + playerBird.getFrameWidth(),
                playerBird.getY() + playerBird.getFrameHeight()))
            crash = true;


        invalidate();
    }

    protected boolean lose(double x, double y) {
        /*tube
         * x
         * x+width
         * hieght
         * hieght + emptySpace
         * */

        if ((x > tube.getX() && x < tube.getEdge()) && (y < tube.getUpTube() || y > tube.getDownTube()))
            return true;
        else
            return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int eventAction = event.getAction();
        if (eventAction == MotionEvent.ACTION_DOWN) {

            playerBird.setVy(-1000);

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
