package com.luo.article;

//子弹类
@SuppressWarnings({"all"})
public class Hero implements Runnable{
    private int ziDuanX;

    private int ziDuanY;

    private int direct;

    private int steep = 11;

    private boolean trueOrFalse = true;;

    public int getZiDuanX() {
        return ziDuanX;
    }

    public void setZiDuanX(int ziDuanX) {
        this.ziDuanX = ziDuanX;
    }

    public int getZiDuanY() {
        return ziDuanY;
    }

    public void setZiDuanY(int ziDuanY) {
        this.ziDuanY = ziDuanY;
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

    public boolean isTrueOrFalse() {
        return trueOrFalse;
    }

    public void setTrueOrFalse(boolean trueOrFalse) {
        this.trueOrFalse = trueOrFalse;
    }

    public Hero(int ziDuanX, int ziDuanY, int direct) {
        //微调子弹让子弹在坦克炮口发射
        this.ziDuanX = ziDuanX + 38;
        this.ziDuanY = ziDuanY - 3;
        this.direct = direct;
    }

    public void run() {
        while (trueOrFalse) {
//            myPanelTanKe.repaint();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {

            }
            switch (direct) {//控制方向
                case 0: //表示向上
                    ziDuanY -= steep;
                    break;
                case 1: //下
                    ziDuanY += steep;
                    break;
                case 2: //左
                    ziDuanX -= steep;
                    break;
                case 3: //右
                    ziDuanX += steep;
                    break;
            }

            //System.out.println("子弹更改 x:" + ziDuanX + " y:" + ziDuanY);
            //子弹判断边界位置，到了边界就结束循环
            if (!(ziDuanY >= -20 && ziDuanY <= 666 && ziDuanX >= -6 && ziDuanX <= 790)){
                trueOrFalse = false;
                break;
            }
        }
    }
}
