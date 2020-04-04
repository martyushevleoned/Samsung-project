package com.example.flappy2;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

class Wall {

    private int emptySpace;
    private int height;
    private int width;
    private int x;
    private int vx;
    private int indent;
    private Bitmap downTube;
    private Bitmap upTube;
    private int hTube;
    private int groundHeight;

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

    void setX(int x) {
        this.x = x;
    }

    void update(int tubeSpawn, int h) {
        x += vx;
        if (x + width < 0) {
            x = tubeSpawn - width;
            generate(h);
            GameView.score++;
        }
    }

    void draw(Canvas canvas, int h) {
        Paint p = new Paint();

        canvas.drawBitmap(downTube, x, height + emptySpace, p);
        canvas.drawBitmap(upTube, x, height - hTube, p);
    }

    private void generate(int h) {
        Random rnd = new Random();
        height = rnd.nextInt(h - indent - indent - emptySpace - groundHeight);
        height += indent;
    }
}
