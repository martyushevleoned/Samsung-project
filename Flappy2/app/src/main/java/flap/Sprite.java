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
    private float frameWidth;
    private float frameHeight;
    private float g = 1;

    private float x;
    private float y;

    private float vx;
    private float vy;

    Sprite(float x,
           float y,
           float vx,
           float vy,
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

    float getX() {
        return x;
    }

    float getY() {
        return y;
    }

    void setY(float y) {
        this.y = y;
    }

    float getFrameWidth() {
        return frameWidth;
    }

    float getFrameHeight() {
        return frameHeight;
    }

    void setVy(float vy) {
        this.vy = vy;
    }

    float getVy() {
        return vy;
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

        canvas.rotate((int) angle, (int) (x + frameWidth / 2), (int) (y + frameHeight / 2));

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

        canvas.rotate((int) -angle, (int) (x + frameWidth / 2), (int) (y + frameHeight / 2));
    }
}