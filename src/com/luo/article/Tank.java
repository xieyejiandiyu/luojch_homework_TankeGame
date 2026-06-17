package com.luo.article;

import java.io.Serializable;

public class Tank implements Serializable {
    private int x;

    private int y;

    private int direct;

    private int steep = 2;

    private int blood = 100;

    public Tank(int x, int y, int direct, int steep, int blood) {
        this.x = x;
        this.y = y;
        this.direct = direct;
        this.steep = steep;
        this.blood = blood;
    }

    public int getBlood() {
        return blood;
    }

    public void setBlood(int blood) {
        this.blood = blood;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getDirect() {
        return direct;
    }

    public void setDirect(int direct) {
        this.direct = direct;
    }

    public int getSteep() {
        return steep;
    }

    public void setSteep(int steep) {
        this.steep = steep;
    }

    public void moveUp(){//上
        y -= steep;
    }

    public void moveDown(){//下
        y += steep;
    }

    public void moveRight(){//右
        x += steep;
    }

    public void moveLeft(){//左
        x -= steep;
    }

}
