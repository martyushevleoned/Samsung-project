package com.example.flappy2;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Random;

class Wall {

    private float emptySpace = 575;
    private float height;
    private float width;
    float x;
    private float vx;
    private float indent;
    private Bitmap downTube;
    private Bitmap upTube;
    private float hTube;
    private float groundHeight;
    private float vy;

    Wall(float width,
         float indent,
         float x,
         float vx,
         Bitmap downTube,
         Bitmap upTube,
         float hTube,
         float groundHeight) {

        this.x = x;
        this.vx = vx;
        this.width = width;
        this.indent = indent;
        this.downTube = downTube;
        this.upTube = upTube;
        this.hTube = hTube;
        this.groundHeight = groundHeight;
    }

    float getX() {
        return x;
    }

    float getEdge() {
        return x + width;
    }

    float getUpTube() {
        return height;
    }

    float getDownTube() {
        return height + emptySpace;
    }

    void update(int tubeSpawn, int h) {
        x += vx;
        move(h);
        if (x + width < 0) {
            x = tubeSpawn - width;
            generate(h);
        }
    }

    void draw(Canvas canvas) {
        Paint p = new Paint();

        canvas.drawBitmap(downTube, x, height + emptySpace, p);
        canvas.drawBitmap(upTube, x, height - hTube, p);
    }

    void generate(int h) {
        Random rnd = new Random();
        vy = rnd.nextInt(2);
        vy *= 2;
        vy -= 1;
        vy *= 2;
        height = rnd.nextInt((int)(h - indent - indent - emptySpace - groundHeight));
        height += indent;
    }

    private void move(int h) {

        if (height < indent) vy *= -1;
        if (height + emptySpace > h - indent - groundHeight) vy *= -1;

        height += vy;
    }
}
