package com.luo.article;

import java.io.Serializable;
import java.util.Vector;

public class LuoTanK extends Tank implements Serializable {
    //储存坦克子弹
    public Vector<Hero> vectorLuoHeroZiDuan = new Vector<>();

    public LuoTanK(int x, int y, int direct, int steep, int blood) {
        super(x, y, direct, steep, blood);
        //加文件io可修改steep和blood还有maxBlood
        setMaxBlood(blood);
//        setMaxBlood(10000);
//        setBlood(10000);
        //按天赋（以后更新）
        setForce(20);
    }

    //创建自己的子弹
    public void shotTank(){
        Hero hero = new Hero(getX(), getY(), getDirect());
        vectorLuoHeroZiDuan.add(hero);
        Thread thread = new Thread(hero);
        thread.start();
    }

}
