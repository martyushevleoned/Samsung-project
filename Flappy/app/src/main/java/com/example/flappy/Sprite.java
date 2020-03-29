package com.example.flappy;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 10.11.2015.
 */
public class Sprite {
    private Bitmap bitmap;

    private List<Rect> frames;
    private int frameWidth;
    private int frameHeight;
    private int currentFrame;
    private int g = 1;
    private double frameTime;
    private double timeForCurrentFrame;

    private double x;
    private double y;

    private double vx;
    private double vy;

    private int padding;

    public Sprite(double x,
                  double y,
                  double vx,
                  double vy,
                  Rect initialFrame,
                  Bitmap bitmap) {

        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;

        this.bitmap = bitmap;

        this.frames = new ArrayList<Rect>();
        this.frames.add(initialFrame);

        this.bitmap = bitmap;

        this.timeForCurrentFrame = 0.0;
        this.frameTime = 25;
        this.currentFrame = 0;

        this.frameWidth = initialFrame.width();
        this.frameHeight = initialFrame.height();

        this.padding = 20;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getFrameWidth() {
        return frameWidth;
    }

    public int getFrameHeight() {
        return frameHeight;
    }

    public void setVy(double velocityY) {
        this.vy = velocityY;
    }

    public void addFrame(Rect frame) {
        frames.add(frame);
    }

    public void setNextFrame() {
        currentFrame += 1;
        currentFrame %= frames.size();
    }

    public void update(int ms) {
        timeForCurrentFrame += ms;
        if (timeForCurrentFrame >= frameTime) {
            setNextFrame();
            timeForCurrentFrame = timeForCurrentFrame - frameTime;
        }
        vy += g;
        x = x + vx;
        y = y + vy;
    }

    public void drawHitBox(Canvas canvas) {
        Paint p = new Paint();
        canvas.drawCircle((float) x, (float) y, 5, p);
        canvas.drawCircle((float) (x + frameWidth), (float) y, 5, p);
        canvas.drawCircle((float) x, (float) (y + frameHeight), 5, p);
        canvas.drawCircle((float) (x + frameWidth), (float) (y + frameHeight), 5, p);
    }

    public void draw(Canvas canvas) {
        Paint p = new Paint();

        double angle = vy / g * 2;

        canvas.rotate((int) angle, (float) (x + frameWidth / 2), (float) (y + frameHeight / 2));

        Rect destination = new Rect((int) x, (int) y, (int) (x + frameWidth), (int) (y + frameHeight));
        canvas.drawBitmap(bitmap, frames.get(currentFrame), destination, p);

        canvas.rotate((int) -angle, (float) (x + frameWidth / 2), (float) (y + frameHeight / 2));

        //drawHitBox(canvas);
    }

    public Rect getBoundingBoxRect() {
        return new Rect((int) x + padding,
                (int) y + padding,
                (int) (x + frameWidth - 2 * padding),
                (int) (y + frameHeight - 2 * padding));
    }

}