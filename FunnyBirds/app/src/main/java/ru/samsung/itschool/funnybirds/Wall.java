package ru.samsung.itschool.funnybirds;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

public class Wall {

    private int emptySpace;
    private int height;
    private int width;
    private int x;
    private int vx;
    private int indent;

    public Wall(int emptySpace,
                int height,
                int width,
                int indent,
                int x,
                int vx) {

        this.x = x;
        this.vx = vx;
        this.width = 400;
        this.indent = 300;
        this.height = 100;
        this.emptySpace = 750;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return x;
    }

    public int getEdge() {
        return x + width;
    }

    public int getUpTube() {
        return height;
    }

    public int getDownTube() {
        return height + emptySpace;
    }

    public void update() {
        x += vx;
    }

    public void draw(Canvas canvas, int h, Boolean crash) {
        Paint p = new Paint();

        if (crash)
            p.setColor(Color.RED);
        else
            p.setColor(Color.GREEN);

        canvas.drawRect(x, 0, x + width, height, p);
        canvas.drawRect(x, height + emptySpace, x + width, h, p);
    }

    public void generate(int h) {
        Random rnd = new Random();
        //Scanner in = new Scanner(System.in);

        height = rnd.nextInt(h - indent - indent - emptySpace);

        height += indent;
    }
}
