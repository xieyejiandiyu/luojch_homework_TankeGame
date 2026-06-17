package com.luo.article;

public class Build {
    private int x;
    private int y;

    private int blood;

    private String id;

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

    public int getBlood() {
        return blood;
    }

    public void setBlood(int blood) {
        this.blood = blood;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Build(int x, int y, String id) {
        this.x = x;
        this.y = y;
        this.id = id;
    }

    //有写建筑可能有血量
    public Build(int x, int y, int blood, String id) {
        this.x = x;
        this.y = y;
        this.blood = blood;
        this.id = id;
    }
}
