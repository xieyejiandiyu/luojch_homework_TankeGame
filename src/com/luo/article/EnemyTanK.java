package com.luo.article;

import java.io.IOException;
import java.io.Serializable;
import java.util.Random;
import java.util.Vector;

@SuppressWarnings({"all"})
public class EnemyTanK extends Tank implements Runnable, Serializable {

    //垂直方向左上角和左下角偏移参数X
    public static final int VERTICAL_TOP_LEFT_X_OFFSET_TANK = -2;

    //垂直方向右上角和右下角偏移参数X
    public static final int VERTICAL_TOP_RIGHT_X_OFFSET_TANK = 68;

    //垂直方向右上角和左上角偏移参数Y
    public static final int VERTICAL_TOP_ABOVE_Y_OFFSET_TANK = -26;

    //垂直方向右下角和左下角偏移参数Y
    public static final int VERTICAL_TOP_BELOW_Y_OFFSET_TANK = 30;


    //横向方向左上角和左下角偏移参数X
    public static final int VERTICAL_HORIZONTAL_LEFT_X_OFFSET_TANK = -2;

    //横向方向右上角和右下角偏移参数X
    public static final int VERTICAL_HORIZONTAL_RIGHT_X_OFFSET_TANK = 70;

    //横向方向右上角和左上角偏移参数Y
    public static final int VERTICAL_HORIZONTAL_ABOVE_Y_OFFSET_TANK = -26;

    //横向方向右下角和左下角偏移参数Y
    public static final int VERTICAL_HORIZONTAL_BELOW_Y_OFFSET_TANK = 24;

    //血量高度
    public static final double BLOOD_HEIGHT = 65;

    //坦克生命周期，false就死了
    public boolean isLent = true;

    //坦克碰撞体积，真说明没碰上，假说明碰上了
    public boolean collision = true;

    private Vector<EnemyTanK> enemyTanKArr = new Vector<>();

    private Vector<Build> buildArr = new Vector<>();

    //储存敌方坦克子弹
    public Vector<Hero> vectorEnemyHeroZiDuan = new Vector<>();

    public Vector<Build> getBuildArr() {
        return buildArr;
    }

    public void setBuildArr(Vector<Build> buildArr) {
        this.buildArr = buildArr;
    }

    public EnemyTanK(int x, int y, int direct, int steep, int blood) {
        super(x, y, direct, steep, blood);
        //下面更新血量
        setMaxBlood(blood);
        //按游戏难度（以后更新）
        setForce(20);
//        setSteep(0);
    }

    //创建敌方的子弹
    public void shotTank(){
        Hero hero = new Hero(getX(), getY(), getDirect());
        //子弹添加到vector集合里
        vectorEnemyHeroZiDuan.add(hero);
        //启动子弹线程
        Thread thread = new Thread(hero);
        thread.start();
    }

    @Override
    public void run() {
        while (true) {

            //敌方坦克子弹数量最多3
            //敌方坦克的多线程一直检测子弹数量有没有超过3,
            //超过就添加子弹
            if (isLent && vectorEnemyHeroZiDuan.size() < 3){
                shotTank();
            }

            //无线接近等于4，但是int类型所以只能在0到3之间
            int j = ((int) (Math.random() * 4));
//            int j = 0;

            //判断坦克行走方向
            switch (getDirect()){
                case 0:
                    //往上走7次
                    for (int i = 0; i < 13; i++) {
                        //collision如果为假就碰到了坦克，if判断变成真，结束往上走的循环10次，下面的下左右也一样
                        if (!collision){
                            //要立刻该方向不然回倒车走5次
                            setDirect(1);
                            //如果碰到坦克就往返方向走5次
                            for (int k = 0; k < 6; k++){
                                //查看有没有碰到建筑物，真碰到，假没碰到
                                if (!downEnemyCollisionBuild()){
                                    moveDown();
                                }
                                try {
                                    Thread.sleep(130);
                                } catch (InterruptedException e) {

                                }
                                collision = true;
                            }
                            break;
                        }

                        //查看有没有碰到建筑物，真碰到，假没碰到
                        if (!upEnemyCollisionBuild()) {
                            moveUp();
                        }
                        try {
                            //休眠130毫米后在走
                            Thread.sleep(130);
                        } catch (InterruptedException e){

                        }
                    }
                    break;
                case 1:
                    //下
                    for (int i = 0; i < 13; i++) {
                        if (!collision){
                            setDirect(0);
                            for (int k = 0; k < 6; k++){
                                //查看有没有碰到建筑物，真碰到，假没碰到
                                if(!upEnemyCollisionBuild()) {
                                    moveUp();
                                }

                                System.out.println("以撞到");
                                try {
                                    Thread.sleep(130);
                                } catch (InterruptedException e) {
                                }
                                collision = true;
                            }
                            break;
                        }

                        if (!downEnemyCollisionBuild()) {
                            moveDown();
                        }

                        try {
                            Thread.sleep(130);
                        } catch (InterruptedException e){

                        }
                    }
                    break;
                case 2:
                    //左
                    for (int i = 0; i < 13; i++) {
                        if (!collision){
                            setDirect(3);
                            for (int k = 0; k < 6; k++){
                                if (!rightEnemyCollisionBuild()) {
                                    moveRight();
                                }
                                System.out.println("以撞到");
                                try {
                                    Thread.sleep(130);
                                } catch (InterruptedException e) {
                                }

                                collision = true;
                            }
                            break;
                        }

                        if (!leftEnemyCollisionBuild()) {
                            moveLeft();
                        }
                        try {
                            Thread.sleep(130);
                        } catch (InterruptedException e){

                        }
                    }
                    break;
                case 3:
                    //右
                    for (int i = 0; i < 13; i++) {
                        if (!collision){
                            setDirect(2);
                            for (int k = 0; k < 6; k++){
                                if (!leftEnemyCollisionBuild()) {
                                    moveLeft();
                                }
                                System.out.println("以撞到");
                                try {
                                    Thread.sleep(130);
                                } catch (InterruptedException e) {

                                }
                                collision = true;
                            }
                            break;
                        }

                        if (!rightEnemyCollisionBuild()) {
                            moveRight();
                        }
                        try {
                            Thread.sleep(130);
                        } catch (InterruptedException e){

                        }
                    }
                    break;
            }

            //随机改变坦克方向
            switch (j){
                case 0:
                    //小于38为真，然后把方向改成反方向，往下，如果没超出边界就继续往上走
                    //其他下左右也如此
                    if (getY() < 40){
                        setDirect(1);
                        System.out.println("以往下");
                    } else {
                        setDirect(j);
                    }
                    break;
                case 1:
                    if (getY() > 660){
                        setDirect(0);
                        System.out.println("以往上");
                    } else {
                        setDirect(j);
                    }
                    break;
                case 2:
                    if (getX() < 45){
                        setDirect(3);
                        System.out.println("以往右");
                    } else {
                        setDirect(j);
                    }
                    break;
                case 3:
                    if (getX() > 715){
                        setDirect(2);
                        System.out.println("以往左");
                    } else {
                        setDirect(j);
                    }
                    break;
            }
            if (!isLent){
                break;
            }
            collision = true;
        }
    }

    //撞到为真，没撞到为假
    private boolean upEnemyCollisionBuild(){//方向上
        //if左边是参照物
        for (int i = 0; i < buildArr.size(); i++) {
            //上面方向的左上角
            if (buildArr.get(i).getX() <= this.getX() - 3
                    && buildArr.get(i).getX() + 35 >= this.getX() - 3
                    && buildArr.get(i).getY() <= this.getY()
                    && buildArr.get(i).getY() + 35 >= this.getY()){
                System.out.println("有建筑物");
                return true;
            }

            //上面方向的右上角
            if (buildArr.get(i).getX() <= this.getX() + 70
                    && buildArr.get(i).getX() + 35 >= this.getX() + 70
                    && buildArr.get(i).getY() <= this.getY()
                    && buildArr.get(i).getY() + 35 >= this.getY()){
                System.out.println("有建筑物");
                return true;
            }

            if (buildArr.get(i).getX() <= this.getX() + 35
                    && buildArr.get(i).getX() + 35 >= this.getX() + 35
                    && buildArr.get(i).getY() <= this.getY()
                    && buildArr.get(i).getY() + 35 >= this.getY()){
                System.out.println("有建筑物");
                return true;
            }
        }
        //如果一开始就没循环，说明场上没有建筑物了
        return false;
    }

    private boolean downEnemyCollisionBuild(){//方向下
        //if左边是参照物
        for (int i = 0; i < buildArr.size(); i++) {
            //下面方向的左下角
            if (buildArr.get(i).getX() <= this.getX() - 3
                    && buildArr.get(i).getX() + 35 >= this.getX() - 3
                    && buildArr.get(i).getY() <= this.getY() + 58
                    && buildArr.get(i).getY() + 35 >= this.getY() + 58){
                System.out.println("有建筑物");
                return true;
            }

            //下面方向的右下角
            if (buildArr.get(i).getX() <= this.getX() + 70
                    && buildArr.get(i).getX() + 35 >= this.getX() + 70
                    && buildArr.get(i).getY() <= this.getY() + 58
                    && buildArr.get(i).getY() + 35 >= this.getY() + 58){
                System.out.println("有建筑物");
                return true;
            }

            if (buildArr.get(i).getX() <= this.getX() + 35
                    && buildArr.get(i).getX() + 35 >= this.getX() + 35
                    && buildArr.get(i).getY() <= this.getY() + 58
                    && buildArr.get(i).getY() + 35 >= this.getY() + 58){
                System.out.println("有建筑物");
                return true;
            }
        }
        //如果一开始就没循环，说明场上没有建筑物了
        return false;
    }

    private boolean leftEnemyCollisionBuild(){//方向左
        //if左边是参照物
        for (int i = 0; i < buildArr.size(); i++) {
            //左面方向的左上角
            if (buildArr.get(i).getX() <= this.getX() - 3
                    && buildArr.get(i).getX() + 35 >= this.getX() - 3
                    && buildArr.get(i).getY() <= this.getY()
                    && buildArr.get(i).getY() + 35 >= this.getY()){
                System.out.println("有建筑物");
                return true;
            }

            //左面方向的左下角
            if (buildArr.get(i).getX() <= this.getX() - 3
                    && buildArr.get(i).getX() + 35 >= this.getX() - 3
                    && buildArr.get(i).getY() <= this.getY() + 53
                    && buildArr.get(i).getY() + 35 >= this.getY() + 53){
                System.out.println("有建筑物");
                return true;
            }

            if (buildArr.get(i).getX() <= this.getX() - 3
                    && buildArr.get(i).getX() + 35 >= this.getX() - 3
                    && buildArr.get(i).getY() <= this.getY() + 26
                    && buildArr.get(i).getY() + 35 >= this.getY() + 26){
                System.out.println("有建筑物");
                return true;
            }
        }
        //如果一开始就没循环，说明场上没有建筑物了
        return false;
    }

    private boolean rightEnemyCollisionBuild(){//方向右
        //if左边是参照物
        for (int i = 0; i < buildArr.size(); i++) {
            //右面方向的右上角
            if (buildArr.get(i).getX() <= this.getX() + 72
                    && buildArr.get(i).getX() + 35 >= this.getX() + 72
                    && buildArr.get(i).getY() <= this.getY()
                    && buildArr.get(i).getY() + 35 >= this.getY()){
                System.out.println("有建筑物");
                return true;
            }

            //右面方向的右下角
            if (buildArr.get(i).getX() <= this.getX() + 72
                    && buildArr.get(i).getX() + 35 >= this.getX() + 72
                    && buildArr.get(i).getY() <= this.getY() + 53
                    && buildArr.get(i).getY() + 35 >= this.getY() + 53){
                System.out.println("有建筑物");
                return true;
            }

            if (buildArr.get(i).getX() <= this.getX() + 72
                    && buildArr.get(i).getX() + 35 >= this.getX() + 72
                    && buildArr.get(i).getY() <= this.getY() + 26
                    && buildArr.get(i).getY() + 35 >= this.getY() + 26){
                System.out.println("有建筑物");
                return true;
            }
        }
        //如果一开始就没循环，说明场上没有建筑物了
        return false;
    }

}
