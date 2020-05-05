package com.example.flappy2;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

class Numbers {
    private Bitmap bitmap;

    private List<Rect> frames;
    private float frameWidth;
    private float frameHeight;

    private float x;
    private float y;

    Numbers(float x,
            float y,
            Rect initialFrame,
            Bitmap bitmap) {

        this.x = x;
        this.y = y;

        this.bitmap = bitmap;

        this.frames = new ArrayList<>();
        this.frames.add(initialFrame);

        this.bitmap = bitmap;

        this.frameWidth = initialFrame.width();
        this.frameHeight = initialFrame.height();
    }

    void setY(float y) {
        this.y = y;
    }

    void setX(float x) {
        this.x = x;
    }

    float getFrameWidth() {
        return frameWidth;
    }

    void addFrame(Rect frame) {
        frames.add(frame);
    }

    void draw(Canvas canvas, int score) {
        Paint p = new Paint();

        Rect destination = new Rect((int) x, (int) y, (int) (x + frameWidth), (int) (y + frameHeight));

        if (score < 10)
            canvas.drawBitmap(bitmap, frames.get(score), destination, p);
        else {
            if (score < 100) {
                canvas.drawBitmap(bitmap, frames.get(score / 10), destination, p);
                destination = new Rect((int) (x + frameWidth), (int) y, (int) (x + (frameWidth * 2)), (int) (y + frameHeight));
                canvas.drawBitmap(bitmap, frames.get(score % 10), destination, p);
            } else {
                canvas.drawBitmap(bitmap, frames.get(score / 100), destination, p);
                destination = new Rect((int) (x + frameWidth), (int) y, (int) (x + (frameWidth * 2)), (int) (y + frameHeight));
                canvas.drawBitmap(bitmap, frames.get(score / 10 % 10), destination, p);
                destination = new Rect((int) (x + (frameWidth * 2)), (int) y, (int) (x + (frameWidth * 3)), (int) (y + frameHeight));
                canvas.drawBitmap(bitmap, frames.get(score % 10), destination, p);
            }
        }

    }
}
