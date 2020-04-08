package com.example.flappy2;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Random;

class Wall {

    int emptySpace;
    int height;
    int width;
    int x;
    int vx;
    int indent;
    Bitmap downTube;
    Bitmap upTube;
    int hTube;
    int groundHeight;

    private int vy;

    Wall(int emptySpace,
         int height,
         int width,
         int indent,
         int x,
         int vx,
         Bitmap downTube,
         Bitmap upTube,
         int hTube,
         int groundHeight) {

        this.x = x;
        this.vx = vx;
        this.width = width;
        this.indent = indent;
        this.height = height;
        this.emptySpace = emptySpace;
        this.downTube = downTube;
        this.upTube = upTube;
        this.hTube = hTube;
        this.groundHeight = groundHeight;
    }

    int getX() {
        return x;
    }

    int getEdge() {
        return x + width;
    }

    int getUpTube() {
        return height;
    }

    int getDownTube() {
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

    void draw(Canvas canvas, int h) {
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
        height = rnd.nextInt(h - indent - indent - emptySpace - groundHeight);
        height += indent;
    }

    private void move(int h) {

        if (height < indent) vy *= -1;
        if (height + emptySpace > h - indent - groundHeight) vy *= -1;

        height += vy;
    }
}
