package com.specialEfficacy;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

public class Bomb{
    int x, y; //炸弹爆炸坐标

    public int life = 26;

    public Image image;

    public boolean isLive = true;

    public Bomb(int x, int y) {
        this.x = x;
        this.y = y;
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

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public void lifeDown(){
        if (life > 0){
            life--;
        } else {
            isLive = false;
        }
    }
}
