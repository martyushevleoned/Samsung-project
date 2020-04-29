package com.example.flappy2;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

class Sprite {
    private Bitmap bitmap;

    private List<Rect> frames;
    private int frameWidth;
    private int frameHeight;
    private int g = 1;
    private double timeForCurrentFrame;

    private double x;
    private double y;

    private double vx;
    private double vy;

    Sprite(double x,
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

        this.frames = new ArrayList<>();
        this.frames.add(initialFrame);

        this.bitmap = bitmap;

        this.frameWidth = initialFrame.width();
        this.frameHeight = initialFrame.height();
    }

    double getX() {
        return x;
    }

    double getY() {
        return y;
    }

    void setY(double y) {
        this.y = y;
    }

    int getFrameWidth() {
        return frameWidth;
    }

    int getFrameHeight() {
        return frameHeight;
    }

    void setVy(double vy) {
        this.vy = vy;
    }

    void addFrame(Rect frame) {
        frames.add(frame);
    }

    void update(int stage) {
        if (stage < 3) {
            vy += g;
            y = y + vy;
        }
        x = x + vx;
    }

    void draw(Canvas canvas, int stage) {
        Paint p = new Paint();

        double angle = vy / g * 2;

        canvas.rotate((int) angle, (float) (x + frameWidth / 2), (float) (y + frameHeight / 2));

        Rect destination = new Rect((int) x, (int) y, (int) (x + frameWidth), (int) (y + frameHeight));

        if (stage == 0)
            canvas.drawBitmap(bitmap, frames.get(0), destination, p);
        else {
            if (vy < -2)
                canvas.drawBitmap(bitmap, frames.get(2), destination, p);
            if (vy >= -2 && vy <= 4)
                canvas.drawBitmap(bitmap, frames.get(1), destination, p);
            if (vy > 4)
                canvas.drawBitmap(bitmap, frames.get(0), destination, p);
        }

        canvas.rotate((int) -angle, (float) (x + frameWidth / 2), (float) (y + frameHeight / 2));
    }

    double getVy() {
        return vy;
    }
}