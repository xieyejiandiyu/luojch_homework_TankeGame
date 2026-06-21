package com.luo.draw;

import com.luo.article.*;
import com.luo.level.Level;
import com.luo.music.AePlayWave;
import com.luo.specialEfficacy.Bomb;
import com.luo.unity.GameMathUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

@SuppressWarnings({"all"})
public class MyPanel extends JPanel implements Runnable, KeyListener, MouseListener {

    //自己的坦克
    private LuoTanK luoTanK;

    //暂时存储敌人坦克
    private EnemyTanK enemyTanK;

    //储存敌方坦克数量
    private Vector<EnemyTanK> enemyTanKArr = new Vector<>();

    //储存建筑物，只要继承了建筑物类都能放入vector
    private Vector<Build> buildArr = new Vector<>();

    //敌方坦克数量
    private int enemyTankShuLiang = 3;

    //typeface 字体  life 一生
    private int typefaceLife = 0;

    //初始化没有字体
    private int write = -1;

    //我方坦克死亡显示信息
    private int typefaceLuoTankIsKill = 0;

    //敌方所有死亡显示信息
    private int typefaceEnemyIsKill = 0;

    //储存死亡后的特效
    private Vector<Bomb> bombArrKill = new Vector<>();

    //储存子弹打中坦克的特效
    private Vector<Bomb> bombArrHero = new Vector<>();

    //储存游戏进度的节点
    public Node node = new Node();

    //设计关卡类
    private Level level = new Level();

    //储存特性图片路径
    private Image imageBomb1 = null;
    private Image imageBomb2 = null;
    private Image imageBomb3 = null;

    //储存zhuUi图的azs图片
    private Image imageAZS = null;

    //建筑砖头
    private Image imageBrick = null;
    //建筑木头
    private Image imageWork = null;

    //记录器类，记录我方坦克击杀数，游戏结束后保存
    private Recorder recorder = null;

    //假代表没有数据要储存
    boolean isMyFrame = false;


    //真代表在ZhuUi页面里的所有按钮能按，假说明ZhuUi页面里的所有按钮都不能按
    //鼠标判断的加了mouse
    boolean mouseZhuUi = true;

    //真代表还在主页面，假代表已经进入游戏了，显示页面的加了UI
    boolean zhuUiTrueOrFalse = true;

    //关卡ui页面判断，真打开，假不打开
    boolean levelUi = false;

    //设置ui页面判断，真打开，假不打开
    boolean setUpUi = false;

    //制作人ui页面判断，真打开，假不打开
    boolean luoMakeGameUi = false;


    //上一次的移动时长
    private long lastMoveTime = 0;
    //毫秒, 控制长按的移动频率
//    private static final long MOVE_INTERVAL = 100;
    private static final long MOVE_INTERVAL = 0;


    public Vector<EnemyTanK> getEnemyTanKArr() {
        return enemyTanKArr;
    }

    public void setEnemyTanKArr(Vector<EnemyTanK> enemyTanKArr) {
        this.enemyTanKArr = enemyTanKArr;
    }

    public MyPanel() {
        //创建记录我方坦克击杀类
        recorder = new Recorder();
        //初始化我方击杀次数
        try {
            recorder.record();
        } catch (IOException e) {
            System.out.println("初始化异常");
            e.printStackTrace();
        }

        try {
            //图片内容放入
            imageBomb1 = ImageIO.read(new File("out\\material\\specialEffects\\bomb_1.gif"));
            imageBomb2 = ImageIO.read(new File("out\\material\\specialEffects\\bomb_2.gif"));
            imageBomb3 = ImageIO.read(new File("out\\material\\specialEffects\\bomb_3.gif"));
            imageAZS = ImageIO.read(new File("out\\material\\ui\\azsNaoPo.jpg"));
            imageBrick = ImageIO.read(new File("out\\material\\bulid\\brick.png"));
            imageWork = ImageIO.read(new File("out\\material\\bulid\\work.png"));
        } catch (IOException e) {
            System.out.println("图片异常");
            e.printStackTrace();
        }

    }

    //画板画东西的方法
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        //显示主页面，真就显示，假就不显示
        if (zhuUiTrueOrFalse) {
            zhuUi(g);
            //提示字体方法
            //主页面不显示代表字体也不显示
            promptFont(g, write);
            return;
        }

        //显示关卡ui页面，真代表打开，假代表不打开
        if (levelUi) {
            levelDesign(g);
            return;
        }

        //显示设置ui页面，真代表打开，假代表不打开
        if (setUpUi){
            setUpDesign(g);
            return;
        }

        //制作人
        if (luoMakeGameUi) {
            luoMakeGameUidesign(g);
            return;
        }
        
        //填充游戏画面
        g.fillRect(0, 0, 800, 750);

        //为空说明我方坦克死亡
        if (luoTanK == null) {
            g.setColor(Color.black);
            g.setFont(new Font("宋体", Font.BOLD, 60));
            g.drawString("你嘎了，游戏结束", 280, 100);
            //已经输了不用保存数据了
            isMyFrame = false;
            //如果typefaceLuoTankIsKill我方坦克死亡信息，大于等于0代表还需要显示信息，小于等于0代表不需要显示信息
            if (typefaceLuoTankIsKill <= 0) {
                for (int i = 0; i < enemyTanKArr.size(); i++) {
                    enemyTanKArr.get(i).isLent = false;
                }
                //清空节点里的坦克和建筑物
                node.nodeLuoTanK = null;
                node.nodeBuildArr = new Vector<>();
                node.nodeEnemyTanKArr = new Vector<>();
                //清空子弹打中特效
                bombArrHero =  new Vector<>();
                //清空坦克死亡特效
                bombArrKill = new Vector<>();
                //我方坦克死亡后把敌方坦克清空，清空了画板的关卡的也要清空
                enemyTanKArr = new Vector<>();
                level.levelEnemyTanKArr = new Vector<>();
                //重置建筑物
                buildArr = new Vector<>();
                level.levelBuildArr = new Vector<>();
                //可以显示主页面
                zhuUiTrueOrFalse = true;
                //可以主页面的按钮能按
                mouseZhuUi = true;

            }
            System.out.println(typefaceLuoTankIsKill + "以--");
            typefaceLuoTankIsKill--;
            return;
        }

        //如果没有敌方坦克，说明你赢了，可以显示主页面
        if (enemyTanKArr.size() == 0) {
            g.setColor(Color.black);
            g.setFont(new Font("宋体", Font.BOLD, 60));
            g.drawString("恭喜你，获胜了", 280, 100);
            isMyFrame = false;
            if (typefaceEnemyIsKill <= 0) {
                //清空节点里的坦克和建筑物
                node.nodeLuoTanK = null;
                node.nodeBuildArr = new Vector<>();
                node.nodeEnemyTanKArr = new Vector<>();
                //清空子弹打中特效
                bombArrHero =  new Vector<>();
                //清空坦克死亡特效
                bombArrKill = new Vector<>();
                //我方坦克死亡后把敌方坦克清空
                enemyTanKArr = new Vector<>();
                level.levelEnemyTanKArr = new Vector<>();
                //重置建筑物
                buildArr = new Vector<>();
                level.levelBuildArr = new Vector<>();
                //可以显示主页面
                zhuUiTrueOrFalse = true;
                //可以主页面的按钮能按
                mouseZhuUi = true;
            }
            System.out.println(typefaceEnemyIsKill + "以--");
            typefaceEnemyIsKill--;
            return;
        }

        //显示记录击杀数
        playGameAchievement(g);

        //画模型坦克
        huaTanKe(820, 50, g, 10, 3, null);

        //画建筑物
        for (int i = 0; i < buildArr.size(); i++) {
            huaBuild(buildArr.get(i).getX(), buildArr.get(i).getY(), g, buildArr.get(i));
        }

        //判断我方坦克是否还活着
        //如果活着就画我方坦克
        if (luoTanK != null) {
            //调用huaTanKe这个方法画自己的坦克，x、y坐标，g画笔，direct方向，自己坦克颜色
            huaTanKe(luoTanK.getX(), luoTanK.getY(), g, luoTanK.getDirect(), 0, luoTanK);
        }

        //画敌方坦克和子弹 i坦克，j子弹
        for (int i = 0; i < enemyTanKArr.size(); i++) {
            huaTanKe(enemyTanKArr.get(i).getX(),
                    enemyTanKArr.get(i).getY(),
                    g, enemyTanKArr.get(i).getDirect(), 1,
                    enemyTanKArr.get(i));

            for (int j = 0; j < enemyTanKArr.get(i).vectorEnemyHeroZiDuan.size(); j++) {
                //判断敌方坦克子弹还存不存在
                if (enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).isTrueOrFalse()) {
                    //画敌方子弹
                    g.fillOval(enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).getZiDuanX() - 10,
                            enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).getZiDuanY() + 30, 3, 3);
//                System.out.println("敌方子弹发射中");
                } else {
                    //当子弹发射完毕后，调用当前发射子弹完的敌方坦克，删了里面的子弹，然后在添加新的子弹
//                    System.out.println("敌方子弹发射完毕，删除子弹 还剩下子弹=" + enemyTanKArr.get(i).vectorHeroS.size());
                    //删除敌方坦克子弹，在vectorHeroS里面的子弹
                    //用了vertor集合拿了锁后EnemyTanK无法执行，只能暂时堵在那
                    enemyTanKArr.get(i).vectorEnemyHeroZiDuan.remove(j);
                    //添加新子弹
                    enemyTanKArr.get(i).shotTank();
                }
            }
        }


        try {
            //判断我方坦克是否还活着，我方坦克如果为空就嘎了
            if (luoTanK != null) {
                //判断我方子弹有没有打中敌方
                collisionHeroLouTank();
            }
        } catch (NullPointerException e) {
            if (luoTanK != null) {
                System.out.println("我方坦克已经被消灭，没有子弹了");
                return;
            }
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("我方坦克存放子弹的vettor为空");
        }


        try {
            if (luoTanK != null) {
                //判断敌方子弹有没有打中我方
                collisionHeroEnemyTanK();
            }
        } catch (NullPointerException e) {
            if (luoTanK != null) {
                System.out.println("我方坦克已经被消灭，已经无法打中我方了");
                return;
            }
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("敌方坦克存放子弹的vettor为空");
        }

        //判断敌方坦克子弹有没有打中障碍物
        try {
            collisonHeroEnemyTankBulid();
        } catch (ArrayIndexOutOfBoundsException e){
            System.out.println("继续执行");
        }

        //判断我方坦克子弹有没有打中障碍物
        try {
            collisonHeroLuoTankBulid();
        } catch (NullPointerException e){
            if (luoTanK != null) {
                System.out.println("我方坦克以死亡，没有子弹，无法检测建筑物");
                return;
            }
            e.printStackTrace();
        }

        try {
            //一定要调用，不然敌方坦克和敌方坦克的碰撞体积就无法检测
            //跟敌方坦克还有我方坦克的碰撞体积检测
            collisionTanKIsTank();
        } catch (NullPointerException e) {
            if (luoTanK != null) {
                System.out.println("我方坦克已经被消灭，无法检测碰撞体积了");
                return;
            }
            e.printStackTrace();
        }

        //显示子弹打中坦克的爆炸特效
        for (int i = 0; i < bombArrHero.size(); i++) {
            Bomb bomb = bombArrHero.get(i);
            if (bomb.getLife() > 10) {
                g.drawImage(imageBomb3, bomb.getX() - 10, bomb.getY(), 40, 40, this);
            } else {
                bomb.setLife(0);
            }
            bomb.lifeDown();

            if (bomb.getLife() <= 0) {
                bombArrHero.remove(i);
            }
        }

        //显示坦克死亡的爆炸特效
        for (int i = 0; i < bombArrKill.size(); i++) {
            //取出炸弹
            Bomb bomb = bombArrKill.get(i);
            //根据当前这个bomb对象的life值去画出对应的图片
            if (bomb.getLife() > 16) {
                g.drawImage(imageBomb1, bomb.getX(), bomb.getY(), 40, 40, this);
//                System.out.println("特效1");
            } else if (bomb.getLife() > 8) {
                g.drawImage(imageBomb2, bomb.getX(), bomb.getY(), 40, 40, this);
//                System.out.println("特效2");
            } else {
                g.drawImage(imageBomb3, bomb.getX(), bomb.getY(), 40, 40, this);
//                System.out.println("特效3");
            }

            if (bomb.getLife() <= 0) {
                bombArrKill.remove(i);
            }

            //每画一次就调用一次，然后life-1，减到0后就销毁
            bomb.lifeDown();

        }

        //判断我方坦克是否还活着
        if (luoTanK != null) {
            //循环发射我方子弹
            for (int i = 0; i < luoTanK.vectorLuoHeroZiDuan.size(); i++) {
                //判断自己的坦克，子弹还存不存在，isTrueOrFalse如果为假，说明i的子弹线程结束了
                // ，但线程结束了在luoTanK里的vector属性里存放的对象子弹还在，就需要删除，所以就执行else删除子弹
                //线程也不会互斥，因为画板线程和子弹线程是两个线程，互不干扰
                if (luoTanK.vectorLuoHeroZiDuan.get(i).isTrueOrFalse()) {
                    g.setColor(Color.cyan);
                    g.fillOval(luoTanK.vectorLuoHeroZiDuan.get(i).getZiDuanX() - 10,
                            luoTanK.vectorLuoHeroZiDuan.get(i).getZiDuanY() + 30, 3, 3);
//                System.out.println("坦克子弹数量 " + luoTanK.vectorHeroS.size());
                } else {
                    //子弹直接删除就行，子弹超出边界就会自动退崔线程，只需要删除Vertor里面的坦克子弹就行
                    luoTanK.vectorLuoHeroZiDuan.remove(i);
                    System.out.println("vector坦克子弹删除了" + " vector剩下=" + luoTanK.vectorLuoHeroZiDuan.size() + "子弹");
                }
            }
        }

    }


    /**
     * 处理我方子弹打中的效果
     * i代表我方子弹luoTanK.vectorHeroS.get(i)  j代表敌方坦克enemyTanKArr.get(j)
     * @param hitBombX 子弹打中时发生的特性的偏移参数
     * @param hitBombY 子弹打中时发生的特性的偏移参数
     */
    public void handleLuoBulletHitEnemy(int i, int j, int hitBombX, int hitBombY) {
        //创建子弹打中的特效打Bomb对象
        Bomb bombHero = new Bomb(luoTanK.vectorLuoHeroZiDuan.get(i).getZiDuanX() + hitBombX,
                luoTanK.vectorLuoHeroZiDuan.get(i).getZiDuanY() + hitBombY);
        bombArrHero.add(bombHero);
        //打中敌方坦克后，删除我方子弹
        luoTanK.vectorLuoHeroZiDuan.remove(i);
        //打中敌方坦克减血
        enemyTanKArr.get(j).setBlood(enemyTanKArr.get(j).getBlood() - luoTanK.getForce());
        System.out.println("减血了");
        //血量等于0和小于0就说明敌方坦克以死亡
        if (enemyTanKArr.get(j).getBlood() <= 0) {
            //创建Bomb对象
            Bomb bomb = new Bomb(enemyTanKArr.get(j).getX() + 7,
                    enemyTanKArr.get(j).getY() + 1);
            bombArrKill.add(bomb);
            //false代表坦克死亡，结束敌方坦克线程
            enemyTanKArr.get(j).isLent = false;
            //在vector集合删除坦克，不在画坦克
            enemyTanKArr.remove(j);
            //记录击杀的敌方坦克数量
            try {
                recorder.enemyTankRecord();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("被击中,敌方坦克消失");
        }
    }

    /**
     * 处理我方子弹打中的效果
     * 敌方坦克i，j代表i这个坦克有多少发子弹
     * @param hitBombX 子弹打中时发生的特性的偏移参数
     * @param hitBombY 子弹打中时发生的特性的偏移参数
     */
    public void handleEnemyBulletHitLuo(int i, int j, int hitBombX, int hitBombY) {
        //创建子弹打中的特效打Bomb对象
        Bomb bombHero = new Bomb(enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).getZiDuanX() + hitBombX,
                enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).getZiDuanY() + hitBombY);
        bombArrHero.add(bombHero);
        //打中我方坦克后，删除敌方子弹
        enemyTanKArr.get(i).vectorEnemyHeroZiDuan.remove(j);
        //打中我方坦克减血
        luoTanK.setBlood(luoTanK.getBlood() - enemyTanKArr.get(i).getForce());
        System.out.println("我方减血了");
        //血量等于0和小于0就说明我方坦克以死亡
        if (luoTanK.getBlood() <= 0) {
            //创建Bomb对象
            Bomb bomb = new Bomb(luoTanK.getX() + 7,
                    luoTanK.getY() + 1);
            bombArrKill.add(bomb);
            //我方坦克被打中后，死亡
            luoTanK = null;
            System.out.println("被击中,我方坦克消失");
        }
    }

    //判断我方子弹有没有碰到敌方坦克
    public void collisionHeroLouTank() {
        //判断我方子弹还在不在敌方身上，在就销毁敌方坦克和我方子弹
        //i代表我方子弹luoTanK.vectorHeroS.get(i)  j代表敌方坦克enemyTanKArr.get(j)
        for (int i = 0; i < luoTanK.vectorLuoHeroZiDuan.size(); i++) {
            for (int j = 0; j < enemyTanKArr.size(); j++) {
                //判断敌方坦克方向 上下方向
                if (enemyTanKArr.get(j).getDirect() == 0 || enemyTanKArr.get(j).getDirect() == 1) {
                    //长方形1
                    if (enemyTanKArr.get(j).getX() + 5 < luoTanK.vectorLuoHeroZiDuan.get(i).getZiDuanX()
                            && (enemyTanKArr.get(j).getX() + 25) > luoTanK.vectorLuoHeroZiDuan.get(i).getZiDuanX()
                            && (enemyTanKArr.get(j).getY() + -36) < luoTanK.vectorLuoHeroZiDuan.get(i).getZiDuanY()
                            && (enemyTanKArr.get(j).getY() + 34) > luoTanK.vectorLuoHeroZiDuan.get(i).getZiDuanY()) {
                        handleLuoBulletHitEnemy(i, j, -24,11);
                        break;
                    } //长方形2
                    else if (enemyTanKArr.get(j).getX() + 55 < luoTanK.vectorLuoHeroZiDuan.get(i).getZiDuanX()
                            && (enemyTanKArr.get(j).getX() + 68) > luoTanK.vectorLuoHeroZiDuan.get(i).getZiDuanX()
                            && (enemyTanKArr.get(j).getY() + -36) < luoTanK.vectorLuoHeroZiDuan.get(i).getZiDuanY()
                            && (enemyTanKArr.get(j).getY() + 34) > luoTanK.vectorLuoHeroZiDuan.get(i).getZiDuanY()) {
                        handleLuoBulletHitEnemy(i, j, -10, 11);
                        break;
                    } //正方形
                    else if (enemyTanKArr.get(j).getX() + 19 < luoTanK.vectorLuoHeroZiDuan.get(i).getZiDuanX()
                            && (enemyTanKArr.get(j).getX() + 54) > luoTanK.vectorLuoHeroZiDuan.get(i).getZiDuanX()
                            && (enemyTanKArr.get(j).getY() + -26) < luoTanK.vectorLuoHeroZiDuan.get(i).getZiDuanY()
                            && (enemyTanKArr.get(j).getY() + 13) > luoTanK.vectorLuoHeroZiDuan.get(i).getZiDuanY()) {
                        handleLuoBulletHitEnemy(i, j, -18, 7);
                        break;
                    }


                } else {
                    //不是上下就只有左右方向了
                    //碰撞体积
                    //长方形一
                    if (enemyTanKArr.get(j).getX() + 3 < luoTanK.vectorLuoHeroZiDuan.get(i).getZiDuanX()
                            && (enemyTanKArr.get(j).getX() + 70) > luoTanK.vectorLuoHeroZiDuan.get(i).getZiDuanX()
                            && (enemyTanKArr.get(j).getY() + -36) < luoTanK.vectorLuoHeroZiDuan.get(i).getZiDuanY()
                            && (enemyTanKArr.get(j).getY() + -15) > luoTanK.vectorLuoHeroZiDuan.get(i).getZiDuanY()) {
                        handleLuoBulletHitEnemy(i, j, -15,12);
                        break;
                    } //长方形2
                    else if (enemyTanKArr.get(j).getX() + 3 < luoTanK.vectorLuoHeroZiDuan.get(i).getZiDuanX()
                            && (enemyTanKArr.get(j).getX() + 70) > luoTanK.vectorLuoHeroZiDuan.get(i).getZiDuanX()
                            && (enemyTanKArr.get(j).getY() + 15) < luoTanK.vectorLuoHeroZiDuan.get(i).getZiDuanY()
                            && (enemyTanKArr.get(j).getY() + 33) > luoTanK.vectorLuoHeroZiDuan.get(i).getZiDuanY()) {
                        handleLuoBulletHitEnemy(i, j, -15,12);
                        break;
                    } //正方形
                    else if (enemyTanKArr.get(j).getX() + 14 < luoTanK.vectorLuoHeroZiDuan.get(i).getZiDuanX()
                            && (enemyTanKArr.get(j).getX() + 58) > luoTanK.vectorLuoHeroZiDuan.get(i).getZiDuanX()
                            && (enemyTanKArr.get(j).getY() + -14) < luoTanK.vectorLuoHeroZiDuan.get(i).getZiDuanY()
                            && (enemyTanKArr.get(j).getY() + 14) > luoTanK.vectorLuoHeroZiDuan.get(i).getZiDuanY()) {
                        handleLuoBulletHitEnemy(i, j, -14,12);
                        break;
                    }

                }
            }
        }
    }

    //判断敌方坦克子弹有没有打中我方坦克
    public void collisionHeroEnemyTanK() {
        //敌方坦克i，j代表i这个坦克有多少发子弹
        for (int i = 0; i < enemyTanKArr.size(); i++) {
            for (int j = 0; j < enemyTanKArr.get(i).vectorEnemyHeroZiDuan.size(); j++) {
                //判断我方坦克方向 上下方向
                if (luoTanK.getDirect() == 0 || luoTanK.getDirect() == 1) {
                    //敌方子弹碰撞体积
                    //长方形1
                    if (luoTanK.getX() + 5 < enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).getZiDuanX()
                            && (luoTanK.getX() + 25) > enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).getZiDuanX()
                            && (luoTanK.getY() + -36) < enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).getZiDuanY()
                            && (luoTanK.getY() + 34) > enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).getZiDuanY()) {
                        handleEnemyBulletHitLuo(i, j, -24, 11);
                        break;
                    } //长方形2
                    else if (luoTanK.getX() + 55 < enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).getZiDuanX()
                            && (luoTanK.getX() + 68) > enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).getZiDuanX()
                            && (luoTanK.getY() + -36) < enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).getZiDuanY()
                            && (luoTanK.getY() + 34) > enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).getZiDuanY()) {
                        handleEnemyBulletHitLuo(i, j, -10, 11);
                        break;
                    } //正方形
                    else if (luoTanK.getX() + 19 < enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).getZiDuanX()
                            && (luoTanK.getX() + 54) > enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).getZiDuanX()
                            && (luoTanK.getY() + -26) < enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).getZiDuanY()
                            && (luoTanK.getY() + 13) > enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).getZiDuanY()) {
                        handleEnemyBulletHitLuo(i, j, -18, 7);
                        break;
                    }

                } else {
                    //不是上下就只有左右方向了
                    //敌方子弹碰撞体积
                    //长方形1
                    if (luoTanK.getX() + 3 < enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).getZiDuanX()
                            && (luoTanK.getX() + 70) > enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).getZiDuanX()
                            && (luoTanK.getY() + -36) < enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).getZiDuanY()
                            && (luoTanK.getY() + -15) > enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).getZiDuanY()) {
                        handleEnemyBulletHitLuo(i, j, -15, 12);
                        break;
                    } //长方形2
                    else if (luoTanK.getX() + 3 < enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).getZiDuanX()
                            && (luoTanK.getX() + 70) > enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).getZiDuanX()
                            && (luoTanK.getY() + 15) < enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).getZiDuanY()
                            && (luoTanK.getY() + 33) > enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).getZiDuanY()) {
                        handleEnemyBulletHitLuo(i, j, -15, 12);
                        break;
                    } //正方形
                    else if (luoTanK.getX() + 14 < enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).getZiDuanX()
                            && (luoTanK.getX() + 58) > enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).getZiDuanX()
                            && (luoTanK.getY() + -14) < enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).getZiDuanY()
                            && (luoTanK.getY() + 14) > enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).getZiDuanY()) {
                        handleEnemyBulletHitLuo(i, j, -14, 12);
                        break;
                    }
                }
            }
        }
    }

    //敌方坦克的体积碰撞
    public void collisionTanKIsTank() {
        /**
         *  先判断敌方的坦克，我方的坦克最后判断，如果我方坦克嘎了直接返回方法
         *  判断敌方坦克和敌方坦克之间的体积碰撞
         *  i是代表是参照物坦克，要跟j坦克去判断是不是碰到了i坦克
         *  i要跟j坦克一个一个判断是不是碰到了i坦克
         *  碰到了让敌方坦克里的collosoon属性变成false，敌方坦克类里的有相应的对应方式
         **/
        //从i坦克参照物开始，
        for (int i = 0; i < enemyTanKArr.size(); i++) {
            //嵌套循环，判断j的坦克坐标有没有碰到i参照物坦克，
            for (int j = 0; j < enemyTanKArr.size(); j++) {
                //不是上下就只有左右方向了
                //判断坦克是否是自己的坦克
                if (i == j) {
                    //跳过j循环
                    continue;
                }

                //碰撞体积
                //判断参照物i坦克是上面还是下面方向
                // 上下方向
                if (enemyTanKArr.get(i).getDirect() == 0 || enemyTanKArr.get(i).getDirect() == 1) {
                    //判断j坦克是上面还是下面
                    switch (enemyTanKArr.get(j).getDirect()) {
                        //i代表 i坦克的面积和 j坦克的每一个角判断在不在 在i坦克面积里，如果在就就是真，j坦克碰到了i坦克
                        //j坦克的上面和下面的情况
                        //j坦克的上下方向，跟参照物坦克i的上下方向对比
                        case 0:
                        case 1:
                            //j坦克的左上角x判断i坦克
                            if ((enemyTanKArr.get(i).getX() - 3) <= (enemyTanKArr.get(j).getX() - 3)
                                    && (enemyTanKArr.get(i).getX() + 70) >= (enemyTanKArr.get(j).getX() - 3)
                                    && (enemyTanKArr.get(i).getY() - 28) <= (enemyTanKArr.get(j).getY() - 28)
                                    && (enemyTanKArr.get(i).getY() + 32) >= (enemyTanKArr.get(j).getY() - 28)) {
                                System.out.println("碰到坦克");
                                enemyTanKArr.get(j).collision = false;
                            }

                            //j坦克的右上角x判断i坦克
                            if ((enemyTanKArr.get(i).getX() - 3) <= (enemyTanKArr.get(j).getX() + 70)
                                    && (enemyTanKArr.get(i).getX() + 70) >= (enemyTanKArr.get(j).getX() + 70)
                                    && (enemyTanKArr.get(i).getY() - 28) <= (enemyTanKArr.get(j).getY() - 28)
                                    && (enemyTanKArr.get(i).getY() + 32) >= (enemyTanKArr.get(j).getY() - 28)) {
                                System.out.println("碰到坦克");
                                enemyTanKArr.get(j).collision = false;
                            }

                            //j坦克的左下角x判断i坦克
                            if ((enemyTanKArr.get(i).getX() - 3) <= (enemyTanKArr.get(j).getX() - 3)
                                    && (enemyTanKArr.get(i).getX() + 70) >= (enemyTanKArr.get(j).getX() - 3)
                                    && (enemyTanKArr.get(i).getY() - 28) <= (enemyTanKArr.get(j).getY() + 32)
                                    && (enemyTanKArr.get(i).getY() + 32) >= (enemyTanKArr.get(j).getY() + 32)) {
                                System.out.println("碰到坦克");
                                enemyTanKArr.get(j).collision = false;
                            }

                            //j坦克的右下角x判断i坦克
                            if (enemyTanKArr.get(i).getX() <= (enemyTanKArr.get(j).getX() + 70)
                                    && (enemyTanKArr.get(i).getX() + 70) >= (enemyTanKArr.get(j).getX() + 70)
                                    && (enemyTanKArr.get(i).getY() - 28) <= (enemyTanKArr.get(j).getY() + 32)
                                    && (enemyTanKArr.get(i).getY() + 32) >= (enemyTanKArr.get(j).getY() + 32)) {
                                System.out.println("碰到坦克");
                                enemyTanKArr.get(j).collision = false;
                            }
                            break;
                        //j坦克左右方向的情况
                        //j坦克的左右方向，跟参照物坦克i的左右方向对比
                        case 2:
                        case 3:
                            //j坦克的左上角x判断i坦克
                            if ((enemyTanKArr.get(i).getX() - 3) <= (enemyTanKArr.get(j).getX() - 3)
                                    && (enemyTanKArr.get(i).getX() + 70) >= (enemyTanKArr.get(j).getX() - 3)
                                    && (enemyTanKArr.get(i).getY() - 28) <= (enemyTanKArr.get(j).getY() - 28)
                                    && (enemyTanKArr.get(i).getY() + 32) >= (enemyTanKArr.get(j).getY() - 28)) {
                                System.out.println("碰到坦克");
                                enemyTanKArr.get(j).collision = false;
                            }
                            //j坦克的右上角x判断i坦克
                            if ((enemyTanKArr.get(i).getX() - 3) <= (enemyTanKArr.get(j).getX() + 72)
                                    && (enemyTanKArr.get(i).getX() + 70) >= (enemyTanKArr.get(j).getX() + 72)
                                    && (enemyTanKArr.get(i).getY() - 28) <= (enemyTanKArr.get(j).getY() - 28)
                                    && (enemyTanKArr.get(i).getY() + 32) >= (enemyTanKArr.get(j).getY() - 28)) {
                                System.out.println("碰到坦克");
                                enemyTanKArr.get(j).collision = false;
                            }

                            //j坦克的左下角x判断i坦克
                            if ((enemyTanKArr.get(i).getX() - 3) <= (enemyTanKArr.get(j).getX() - 3)
                                    && (enemyTanKArr.get(i).getX() + 70) >= (enemyTanKArr.get(j).getX() - 3)
                                    && (enemyTanKArr.get(i).getY() - 28) <= (enemyTanKArr.get(j).getY() + 24)
                                    && (enemyTanKArr.get(i).getY() + 32) >= (enemyTanKArr.get(j).getY() + 24)) {
                                System.out.println("碰到坦克");
                                enemyTanKArr.get(j).collision = false;
                            }
                            //j坦克的右下角x判断i坦克
                            if (enemyTanKArr.get(i).getX() <= (enemyTanKArr.get(j).getX() + 72)
                                    && (enemyTanKArr.get(i).getX() + 70) >= (enemyTanKArr.get(j).getX() + 72)
                                    && (enemyTanKArr.get(i).getY() - 28) <= (enemyTanKArr.get(j).getY() + 24)
                                    && (enemyTanKArr.get(i).getY() + 32) >= (enemyTanKArr.get(j).getY() + 24)) {
                                System.out.println("碰到坦克");
                                enemyTanKArr.get(j).collision = false;
                            }
                            break;
                    }
                } else {
                    //参照物坦克i不是上下就只有左右方向了
                    //判断j坦克是上面方向还是下面方向
                    switch (enemyTanKArr.get(j).getDirect()) {
                        //j坦克的上下方向，跟参照物坦克i的上下方向对比
                        case 0:
                        case 1:
                            //j坦克的左上角x判断i坦克
                            if ((enemyTanKArr.get(i).getX() - 3) <= (enemyTanKArr.get(j).getX() - 3)
                                    && (enemyTanKArr.get(i).getX() + 73) >= (enemyTanKArr.get(j).getX() - 3)
                                    && (enemyTanKArr.get(i).getY() - 28) <= (enemyTanKArr.get(j).getY() - 28)
                                    && (enemyTanKArr.get(i).getY() + 24) >= (enemyTanKArr.get(j).getY()) - 28) {
                                System.out.println("碰到坦克");
                                enemyTanKArr.get(j).collision = false;
                            }

                            //j坦克的右上角x判断i坦克
                            if ((enemyTanKArr.get(i).getX() - 3) <= (enemyTanKArr.get(j).getX() + 70)
                                    && (enemyTanKArr.get(i).getX() + 73) >= (enemyTanKArr.get(j).getX() + 70)
                                    && (enemyTanKArr.get(i).getY() - 28) <= (enemyTanKArr.get(j).getY() - 28)
                                    && (enemyTanKArr.get(i).getY() + 24) >= (enemyTanKArr.get(j).getY() - 28)) {
                                System.out.println("碰到坦克");
                                enemyTanKArr.get(j).collision = false;
                            }

                            //j坦克的左下角x判断i坦克
                            if ((enemyTanKArr.get(i).getX() - 3) <= (enemyTanKArr.get(j).getX() - 3)
                                    && (enemyTanKArr.get(i).getX() + 73) >= (enemyTanKArr.get(j).getX() - 3)
                                    && (enemyTanKArr.get(i).getY() - 28) <= (enemyTanKArr.get(j).getY() + 32)
                                    && (enemyTanKArr.get(i).getY() + 24) >= (enemyTanKArr.get(j).getY() + 32)) {
                                System.out.println("碰到坦克");
                                enemyTanKArr.get(j).collision = false;
                            }

                            //j坦克的右下角x判断i坦克
                            if ((enemyTanKArr.get(i).getX() - 3) <= (enemyTanKArr.get(j).getX() + 70)
                                    && (enemyTanKArr.get(i).getX() + 73) >= (enemyTanKArr.get(j).getX() + 70)
                                    && (enemyTanKArr.get(i).getY() - 28) <= (enemyTanKArr.get(j).getY() + 32)
                                    && (enemyTanKArr.get(i).getY() + 24) >= (enemyTanKArr.get(j).getY() + 32)) {
                                System.out.println("碰到坦克");
                                enemyTanKArr.get(j).collision = false;
                            }
                            break;
                        //j坦克的左右方向，跟参照物坦克i的左右方向对比
                        case 2:
                        case 3:
                            //j坦克的左上角x判断i坦克
                            if ((enemyTanKArr.get(i).getX() - 3) <= (enemyTanKArr.get(j).getX() - 3)
                                    && (enemyTanKArr.get(i).getX() + 70) >= (enemyTanKArr.get(j).getX() - 3)
                                    && (enemyTanKArr.get(i).getY() - 28) <= (enemyTanKArr.get(j).getY() - 28)
                                    && (enemyTanKArr.get(i).getY() + 32) >= (enemyTanKArr.get(j).getY() - 28)) {
                                System.out.println("碰到坦克");
                                enemyTanKArr.get(j).collision = false;
                            }
                            //j坦克的右上角x判断i坦克
                            if ((enemyTanKArr.get(i).getX() - 3) <= (enemyTanKArr.get(j).getX() + 72)
                                    && (enemyTanKArr.get(i).getX() + 70) >= (enemyTanKArr.get(j).getX() + 72)
                                    && (enemyTanKArr.get(i).getY() - 28) <= (enemyTanKArr.get(j).getY() - 28)
                                    && (enemyTanKArr.get(i).getY() + 32) >= (enemyTanKArr.get(j).getY() - 28)) {
                                System.out.println("碰到坦克");
                                enemyTanKArr.get(j).collision = false;
                            }

                            //j坦克的左下角x判断i坦克
                            if ((enemyTanKArr.get(i).getX() - 3) <= (enemyTanKArr.get(j).getX() - 3)
                                    && (enemyTanKArr.get(i).getX() + 70) >= (enemyTanKArr.get(j).getX() - 3)
                                    && (enemyTanKArr.get(i).getY() - 28) <= (enemyTanKArr.get(j).getY() + 24)
                                    && (enemyTanKArr.get(i).getY() + 32) >= (enemyTanKArr.get(j).getY() + 24)) {
                                System.out.println("碰到坦克");
                                enemyTanKArr.get(j).collision = false;
                            }
                            //j坦克的右下角x判断i坦克
                            if (enemyTanKArr.get(i).getX() <= (enemyTanKArr.get(j).getX() + 72)
                                    && (enemyTanKArr.get(i).getX() + 70) >= (enemyTanKArr.get(j).getX() + 72)
                                    && (enemyTanKArr.get(i).getY() - 28) <= (enemyTanKArr.get(j).getY() + 24)
                                    && (enemyTanKArr.get(i).getY() + 32) >= (enemyTanKArr.get(j).getY() + 24)) {
                                System.out.println("碰到坦克");
                                enemyTanKArr.get(j).collision = false;
                            }
                            break;

                    }
                }
            }
        }

        //如果我方坦克等于null，下面检测我方坦克的敌方坦克的体积碰撞就不用去判断了
        if (luoTanK == null) {
            return;
        }

        /**
         * 上面是敌方坦克和敌方坦克之间的判断体积
         * 下面就是判断敌方坦克和我方坦克之间的碰撞
         * 我方坦克变成参照物，i坦克要跟参照物坦克判断
         * 如果敌方坦克碰到我方坦克，双方坦克都爆炸
         */
        for (int i = 0; i < enemyTanKArr.size(); i++) {
            //碰撞体积
            //判断我方坦克参照物是上面还是下面方向
            // 上下方向
            if (luoTanK.getDirect() == 0 || luoTanK.getDirect() == 1) {
                //判断i敌方坦克是上面还是下面
                switch (enemyTanKArr.get(i).getDirect()) {
                    //我方坦克的上面和下面的情况
                    //我方参照物坦克的上下方向，跟敌方坦克i的上下方向对比
                    case 0:
                    case 1:
                        //i坦克的左上角x判断我方坦克
                        if ((luoTanK.getX()) <= (enemyTanKArr.get(i).getX() + EnemyTanK.VERTICAL_TOP_LEFT_X_OFFSET_TANK)
                                && (luoTanK.getX() + 68) >= (enemyTanKArr.get(i).getX() + EnemyTanK.VERTICAL_TOP_LEFT_X_OFFSET_TANK)
                                && (luoTanK.getY() - 26) <= (enemyTanKArr.get(i).getY() + EnemyTanK.VERTICAL_TOP_ABOVE_Y_OFFSET_TANK)
                                && (luoTanK.getY() + 30) >= (enemyTanKArr.get(i).getY() + EnemyTanK.VERTICAL_TOP_ABOVE_Y_OFFSET_TANK)) {
                            System.out.println("碰到坦克");

                            //如果撞到了敌方和我方坦克就创建Bomb对象
                            //强制击杀
                            Bomb luoBomb = new Bomb(luoTanK.getX() + 7,
                                    luoTanK.getY() + 1);
                            Bomb enemyBomb = new Bomb(enemyTanKArr.get(i).getX() + 7,
                                    enemyTanKArr.get(i).getY() + 1);
                            bombArrKill.add(luoBomb);
                            bombArrKill.add(enemyBomb);
                            //false代表敌方坦克死亡，结束敌方坦克线程
                            enemyTanKArr.get(i).isLent = false;
                            //在vector集合删除坦克，不在画坦克
                            enemyTanKArr.remove(i);
                            //我方坦克变成null，我方坦克死亡
                            luoTanK = null;
                            System.out.println("撞到了和我方敌方坦克消失");
                            break;
                        }

                        //i坦克的右上角x判断我方坦克
                        if ((luoTanK.getX()) <= (enemyTanKArr.get(i).getX() + EnemyTanK.VERTICAL_TOP_RIGHT_X_OFFSET_TANK)
                                && (luoTanK.getX() + 28) >= (enemyTanKArr.get(i).getX() + EnemyTanK.VERTICAL_TOP_RIGHT_X_OFFSET_TANK)
                                && (luoTanK.getY() - 26) <= (enemyTanKArr.get(i).getY() + EnemyTanK.VERTICAL_TOP_ABOVE_Y_OFFSET_TANK)
                                && (luoTanK.getY() + 30) >= (enemyTanKArr.get(i).getY() + EnemyTanK.VERTICAL_TOP_ABOVE_Y_OFFSET_TANK)) {
                            System.out.println("碰到坦克");

                            //如果撞到了敌方和我方坦克就创建Bomb对象
                            //强制击杀
                            Bomb luoBomb = new Bomb(luoTanK.getX() + 7,
                                    luoTanK.getY() + 1);
                            Bomb enemyBomb = new Bomb(enemyTanKArr.get(i).getX() + 7,
                                    enemyTanKArr.get(i).getY() + 1);
                            bombArrKill.add(luoBomb);
                            bombArrKill.add(enemyBomb);
                            //false代表敌方坦克死亡，结束敌方坦克线程
                            enemyTanKArr.get(i).isLent = false;
                            //在vector集合删除坦克，不在画坦克
                            enemyTanKArr.remove(i);
                            //我方坦克变成null，我方坦克死亡
                            luoTanK = null;
                            System.out.println("撞到了和我方敌方坦克消失");
                        }

                        //i坦克的左下角x判断我方坦克
                        if ((luoTanK.getX()) <= (enemyTanKArr.get(i).getX() + EnemyTanK.VERTICAL_TOP_LEFT_X_OFFSET_TANK)
                                && (luoTanK.getX() + 68) >= (enemyTanKArr.get(i).getX() + EnemyTanK.VERTICAL_TOP_LEFT_X_OFFSET_TANK)
                                && (luoTanK.getY() - 26) <= (enemyTanKArr.get(i).getY() + EnemyTanK.VERTICAL_TOP_BELOW_Y_OFFSET_TANK)
                                && (luoTanK.getY() + 30) >= (enemyTanKArr.get(i).getY() + EnemyTanK.VERTICAL_TOP_BELOW_Y_OFFSET_TANK)) {
                            System.out.println("碰到坦克");

                            //如果撞到了敌方和我方坦克就创建Bomb对象
                            //强制击杀
                            Bomb luoBomb = new Bomb(luoTanK.getX() + 7,
                                    luoTanK.getY() + 1);
                            Bomb enemyBomb = new Bomb(enemyTanKArr.get(i).getX() + 7,
                                    enemyTanKArr.get(i).getY() + 1);
                            bombArrKill.add(luoBomb);
                            bombArrKill.add(enemyBomb);
                            //false代表敌方坦克死亡，结束敌方坦克线程
                            enemyTanKArr.get(i).isLent = false;
                            //在vector集合删除坦克，不在画坦克
                            enemyTanKArr.remove(i);
                            //我方坦克变成null，我方坦克死亡
                            luoTanK = null;
                            System.out.println("撞到了和我方敌方坦克消失");
                        }

                        //i坦克的右下角x判断我方坦克
                        if (luoTanK.getX() <= (enemyTanKArr.get(i).getX() + EnemyTanK.VERTICAL_TOP_RIGHT_X_OFFSET_TANK)
                                && (luoTanK.getX() + 68) >= (enemyTanKArr.get(i).getX() + EnemyTanK.VERTICAL_TOP_RIGHT_X_OFFSET_TANK)
                                && (luoTanK.getY() - 26) <= (enemyTanKArr.get(i).getY() + EnemyTanK.VERTICAL_TOP_BELOW_Y_OFFSET_TANK)
                                && (luoTanK.getY() + 30) >= (enemyTanKArr.get(i).getY() + EnemyTanK.VERTICAL_TOP_BELOW_Y_OFFSET_TANK)) {
                            System.out.println("碰到坦克");

                            //如果撞到了敌方和我方坦克就创建Bomb对象
                            //强制击杀
                            Bomb luoBomb = new Bomb(luoTanK.getX() + 7,
                                    luoTanK.getY() + 1);
                            Bomb enemyBomb = new Bomb(enemyTanKArr.get(i).getX() + 7,
                                    enemyTanKArr.get(i).getY() + 1);
                            bombArrKill.add(luoBomb);
                            bombArrKill.add(enemyBomb);
                            //false代表敌方坦克死亡，结束敌方坦克线程
                            enemyTanKArr.get(i).isLent = false;
                            //在vector集合删除坦克，不在画坦克
                            enemyTanKArr.remove(i);
                            //我方坦克变成null，我方坦克死亡
                            luoTanK = null;
                            System.out.println("撞到了和我方敌方坦克消失");
                        }
                        break;
                    //i坦克左右方向的情况
                    //i坦克的左右方向，跟参照物我方的左右方向对比
                    case 2:
                    case 3:
                        //i坦克的左上角x判断i坦克
                        if ((luoTanK.getX()) <= (enemyTanKArr.get(i).getX() + EnemyTanK.VERTICAL_HORIZONTAL_LEFT_X_OFFSET_TANK)
                                && (luoTanK.getX() + 68) >= (enemyTanKArr.get(i).getX() + EnemyTanK.VERTICAL_HORIZONTAL_LEFT_X_OFFSET_TANK)
                                && (luoTanK.getY() - 26) <= (enemyTanKArr.get(i).getY() + EnemyTanK.VERTICAL_HORIZONTAL_ABOVE_Y_OFFSET_TANK)
                                && (luoTanK.getY() + 30) >= (enemyTanKArr.get(i).getY() + EnemyTanK.VERTICAL_HORIZONTAL_ABOVE_Y_OFFSET_TANK)) {
                            System.out.println("碰到坦克");

                            //如果撞到了敌方和我方坦克就创建Bomb对象
                            //强制击杀
                            Bomb luoBomb = new Bomb(luoTanK.getX() + 7,
                                    luoTanK.getY() + 1);
                            Bomb enemyBomb = new Bomb(enemyTanKArr.get(i).getX() + 7,
                                    enemyTanKArr.get(i).getY() + 1);
                            bombArrKill.add(luoBomb);
                            bombArrKill.add(enemyBomb);
                            //false代表敌方坦克死亡，结束敌方坦克线程
                            enemyTanKArr.get(i).isLent = false;
                            //在vector集合删除坦克，不在画坦克
                            enemyTanKArr.remove(i);
                            //我方坦克变成null，我方坦克死亡
                            luoTanK = null;
                            System.out.println("撞到了和我方敌方坦克消失");
                        }

                        //i坦克的右上角x判断i坦克
                        if ((luoTanK.getX()) <= (enemyTanKArr.get(i).getX() + EnemyTanK.VERTICAL_HORIZONTAL_RIGHT_X_OFFSET_TANK)
                                && (luoTanK.getX() + 68) >= (enemyTanKArr.get(i).getX() + EnemyTanK.VERTICAL_HORIZONTAL_RIGHT_X_OFFSET_TANK)
                                && (luoTanK.getY() - 26) <= (enemyTanKArr.get(i).getY() + EnemyTanK.VERTICAL_HORIZONTAL_ABOVE_Y_OFFSET_TANK)
                                && (luoTanK.getY() + 30) >= (enemyTanKArr.get(i).getY() + EnemyTanK.VERTICAL_HORIZONTAL_ABOVE_Y_OFFSET_TANK)) {
                            System.out.println("碰到坦克");

                            //如果撞到了敌方和我方坦克就创建Bomb对象
                            //强制击杀
                            Bomb luoBomb = new Bomb(luoTanK.getX() + 7,
                                    luoTanK.getY() + 1);
                            Bomb enemyBomb = new Bomb(enemyTanKArr.get(i).getX() + 7,
                                    enemyTanKArr.get(i).getY() + 1);
                            bombArrKill.add(luoBomb);
                            bombArrKill.add(enemyBomb);
                            //false代表敌方坦克死亡，结束敌方坦克线程
                            enemyTanKArr.get(i).isLent = false;
                            //在vector集合删除坦克，不在画坦克
                            enemyTanKArr.remove(i);
                            //我方坦克变成null，我方坦克死亡
                            luoTanK = null;
                            System.out.println("撞到了和我方敌方坦克消失");
                        }

                        //i坦克的左下角x判断i坦克
                        if ((luoTanK.getX()) <= (enemyTanKArr.get(i).getX() + EnemyTanK.VERTICAL_HORIZONTAL_LEFT_X_OFFSET_TANK)
                                && (luoTanK.getX() + 68) >= (enemyTanKArr.get(i).getX() + EnemyTanK.VERTICAL_HORIZONTAL_LEFT_X_OFFSET_TANK)
                                && (luoTanK.getY() - 26) <= (enemyTanKArr.get(i).getY() + EnemyTanK.VERTICAL_HORIZONTAL_BELOW_Y_OFFSET_TANK)
                                && (luoTanK.getY() + 30) >= (enemyTanKArr.get(i).getY() + EnemyTanK.VERTICAL_HORIZONTAL_BELOW_Y_OFFSET_TANK)) {
                            System.out.println("碰到坦克");

                            //如果撞到了敌方和我方坦克就创建Bomb对象
                            //强制击杀
                            Bomb luoBomb = new Bomb(luoTanK.getX() + 7,
                                    luoTanK.getY() + 1);
                            Bomb enemyBomb = new Bomb(enemyTanKArr.get(i).getX() + 7,
                                    enemyTanKArr.get(i).getY() + 1);
                            bombArrKill.add(luoBomb);
                            bombArrKill.add(enemyBomb);
                            //false代表敌方坦克死亡，结束敌方坦克线程
                            enemyTanKArr.get(i).isLent = false;
                            //在vector集合删除坦克，不在画坦克
                            enemyTanKArr.remove(i);
                            //我方坦克变成null，我方坦克死亡
                            luoTanK = null;
                            System.out.println("撞到了和我方敌方坦克消失");
                        }

                        //i坦克的右下角x判断i坦克
                        if (luoTanK.getX() <= (enemyTanKArr.get(i).getX() + EnemyTanK.VERTICAL_HORIZONTAL_RIGHT_X_OFFSET_TANK)
                                && (luoTanK.getX() + 68) >= (enemyTanKArr.get(i).getX() + EnemyTanK.VERTICAL_HORIZONTAL_RIGHT_X_OFFSET_TANK)
                                && (luoTanK.getY() - 26) <= (enemyTanKArr.get(i).getY() + EnemyTanK.VERTICAL_HORIZONTAL_BELOW_Y_OFFSET_TANK)
                                && (luoTanK.getY() + 30) >= (enemyTanKArr.get(i).getY() + EnemyTanK.VERTICAL_HORIZONTAL_BELOW_Y_OFFSET_TANK)) {
                            System.out.println("碰到坦克");

                            //如果撞到了敌方和我方坦克就创建Bomb对象
                            //强制击杀
                            Bomb luoBomb = new Bomb(luoTanK.getX() + 7,
                                    luoTanK.getY() + 1);
                            Bomb enemyBomb = new Bomb(enemyTanKArr.get(i).getX() + 7,
                                    enemyTanKArr.get(i).getY() + 1);
                            bombArrKill.add(luoBomb);
                            bombArrKill.add(enemyBomb);
                            //false代表敌方坦克死亡，结束敌方坦克线程
                            enemyTanKArr.get(i).isLent = false;
                            //在vector集合删除坦克，不在画坦克
                            enemyTanKArr.remove(i);
                            //我方坦克变成null，我方坦克死亡
                            luoTanK = null;
                            System.out.println("撞到了和我方敌方坦克消失");
                        }
                        break;
                }
            } else {
                //我方坦克不是上下就只有左右方向了
                //判断i坦克是上面方向还是下面方向
                switch (enemyTanKArr.get(i).getDirect()) {
                    //j坦克的上下方向，跟参照物坦克i的左右方向对比
                    case 0:
                    case 1:
                        //i坦克的左上角x判断i坦克
                        if ((luoTanK.getX()) <= (enemyTanKArr.get(i).getX() + EnemyTanK.VERTICAL_TOP_LEFT_X_OFFSET_TANK)
                                && (luoTanK.getX() + 72) >= (enemyTanKArr.get(i).getX() + EnemyTanK.VERTICAL_TOP_LEFT_X_OFFSET_TANK)
                                && (luoTanK.getY() - 27) <= (enemyTanKArr.get(i).getY() + EnemyTanK.VERTICAL_TOP_ABOVE_Y_OFFSET_TANK)
                                && (luoTanK.getY() + 24) >= (enemyTanKArr.get(i).getY()) + EnemyTanK.VERTICAL_TOP_ABOVE_Y_OFFSET_TANK) {
                            System.out.println("碰到坦克");

                            //如果撞到了敌方和我方坦克就创建Bomb对象
                            //强制击杀
                            Bomb luoBomb = new Bomb(luoTanK.getX() + 7,
                                    luoTanK.getY() + 1);
                            Bomb enemyBomb = new Bomb(enemyTanKArr.get(i).getX() + 7,
                                    enemyTanKArr.get(i).getY() + 1);
                            bombArrKill.add(luoBomb);
                            bombArrKill.add(enemyBomb);
                            //false代表敌方坦克死亡，结束敌方坦克线程
                            enemyTanKArr.get(i).isLent = false;
                            //在vector集合删除坦克，不在画坦克
                            enemyTanKArr.remove(i);
                            //我方坦克变成null，我方坦克死亡
                            luoTanK = null;
                            System.out.println("撞到了和我方敌方坦克消失");
                        }

                        //i坦克的右上角x判断i坦克
                        if ((luoTanK.getX()) <= (enemyTanKArr.get(i).getX() + EnemyTanK.VERTICAL_TOP_RIGHT_X_OFFSET_TANK)
                                && (luoTanK.getX() + 72) >= (enemyTanKArr.get(i).getX() + EnemyTanK.VERTICAL_TOP_RIGHT_X_OFFSET_TANK)
                                && (luoTanK.getY() - 27) <= (enemyTanKArr.get(i).getY() + EnemyTanK.VERTICAL_TOP_ABOVE_Y_OFFSET_TANK)
                                && (luoTanK.getY() + 24) >= (enemyTanKArr.get(i).getY() + EnemyTanK.VERTICAL_TOP_ABOVE_Y_OFFSET_TANK)) {
                            System.out.println("碰到坦克");

                            //如果撞到了敌方和我方坦克就创建Bomb对象
                            //强制击杀
                            Bomb luoBomb = new Bomb(luoTanK.getX() + 7,
                                    luoTanK.getY() + 1);
                            Bomb enemyBomb = new Bomb(enemyTanKArr.get(i).getX() + 7,
                                    enemyTanKArr.get(i).getY() + 1);
                            bombArrKill.add(luoBomb);
                            bombArrKill.add(enemyBomb);
                            //false代表敌方坦克死亡，结束敌方坦克线程
                            enemyTanKArr.get(i).isLent = false;
                            //在vector集合删除坦克，不在画坦克
                            enemyTanKArr.remove(i);
                            //我方坦克变成null，我方坦克死亡
                            luoTanK = null;
                            System.out.println("撞到了和我方敌方坦克消失");
                        }

                        //i坦克的左下角x判断i坦克
                        if ((luoTanK.getX()) <= (enemyTanKArr.get(i).getX() + EnemyTanK.VERTICAL_TOP_LEFT_X_OFFSET_TANK)
                                && (luoTanK.getX() + 72) >= (enemyTanKArr.get(i).getX() + EnemyTanK.VERTICAL_TOP_LEFT_X_OFFSET_TANK)
                                && (luoTanK.getY() - 27) <= (enemyTanKArr.get(i).getY() + EnemyTanK.VERTICAL_TOP_BELOW_Y_OFFSET_TANK)
                                && (luoTanK.getY() + 24) >= (enemyTanKArr.get(i).getY() + EnemyTanK.VERTICAL_TOP_BELOW_Y_OFFSET_TANK)) {
                            System.out.println("碰到坦克");

                            //如果撞到了敌方和我方坦克就创建Bomb对象
                            //强制击杀
                            Bomb luoBomb = new Bomb(luoTanK.getX() + 7,
                                    luoTanK.getY() + 1);
                            Bomb enemyBomb = new Bomb(enemyTanKArr.get(i).getX() + 7,
                                    enemyTanKArr.get(i).getY() + 1);
                            bombArrKill.add(luoBomb);
                            bombArrKill.add(enemyBomb);
                            //false代表敌方坦克死亡，结束敌方坦克线程
                            enemyTanKArr.get(i).isLent = false;
                            //在vector集合删除坦克，不在画坦克
                            enemyTanKArr.remove(i);
                            //我方坦克变成null，我方坦克死亡
                            luoTanK = null;
                            System.out.println("撞到了和我方敌方坦克消失");
                        }

                        //i坦克的右下角x判断i坦克
                        if ((luoTanK.getX()) <= (enemyTanKArr.get(i).getX() + EnemyTanK.VERTICAL_TOP_RIGHT_X_OFFSET_TANK)
                                && (luoTanK.getX() + 72) >= (enemyTanKArr.get(i).getX() + EnemyTanK.VERTICAL_TOP_RIGHT_X_OFFSET_TANK)
                                && (luoTanK.getY() - 27) <= (enemyTanKArr.get(i).getY() + EnemyTanK.VERTICAL_TOP_BELOW_Y_OFFSET_TANK)
                                && (luoTanK.getY() + 24) >= (enemyTanKArr.get(i).getY() + EnemyTanK.VERTICAL_TOP_BELOW_Y_OFFSET_TANK)) {
                            System.out.println("碰到坦克");

                            //如果撞到了敌方和我方坦克就创建Bomb对象
                            //强制击杀
                            Bomb luoBomb = new Bomb(luoTanK.getX() + 7,
                                    luoTanK.getY() + 1);
                            Bomb enemyBomb = new Bomb(enemyTanKArr.get(i).getX() + 7,
                                    enemyTanKArr.get(i).getY() + 1);
                            bombArrKill.add(luoBomb);
                            bombArrKill.add(enemyBomb);
                            //false代表敌方坦克死亡，结束敌方坦克线程
                            enemyTanKArr.get(i).isLent = false;
                            //在vector集合删除坦克，不在画坦克
                            enemyTanKArr.remove(i);
                            //我方坦克变成null，我方坦克死亡
                            luoTanK = null;
                            System.out.println("撞到了和我方敌方坦克消失");
                        }
                        break;
                    //i敌方坦克的左右方向，跟参照物我方坦克的左右方向对比
                    case 2:
                    case 3:
                        //i坦克的左上角x判断i坦克
                        if ((luoTanK.getX()) <= (enemyTanKArr.get(i).getX() + EnemyTanK.VERTICAL_HORIZONTAL_LEFT_X_OFFSET_TANK)
                                && (luoTanK.getX() + 70) >= (enemyTanKArr.get(i).getX() + EnemyTanK.VERTICAL_HORIZONTAL_LEFT_X_OFFSET_TANK)
                                && (luoTanK.getY() - 27) <= (enemyTanKArr.get(i).getY() + EnemyTanK.VERTICAL_HORIZONTAL_ABOVE_Y_OFFSET_TANK)
                                && (luoTanK.getY() + 32) >= (enemyTanKArr.get(i).getY() + EnemyTanK.VERTICAL_HORIZONTAL_ABOVE_Y_OFFSET_TANK)) {
                            System.out.println("碰到坦克");

                            //如果撞到了敌方和我方坦克就创建Bomb对象
                            //强制击杀
                            Bomb luoBomb = new Bomb(luoTanK.getX() + 7,
                                    luoTanK.getY() + 1);
                            Bomb enemyBomb = new Bomb(enemyTanKArr.get(i).getX() + 7,
                                    enemyTanKArr.get(i).getY() + 1);
                            bombArrKill.add(luoBomb);
                            bombArrKill.add(enemyBomb);
                            //false代表敌方坦克死亡，结束敌方坦克线程
                            enemyTanKArr.get(i).isLent = false;
                            //在vector集合删除坦克，不在画坦克
                            enemyTanKArr.remove(i);
                            //我方坦克变成null，我方坦克死亡
                            luoTanK = null;
                            System.out.println("撞到了和我方敌方坦克消失");
                        }
                        //i坦克的右上角x判断i坦克
                        if ((luoTanK.getX()) <= (enemyTanKArr.get(i).getX() + EnemyTanK.VERTICAL_HORIZONTAL_RIGHT_X_OFFSET_TANK)
                                && (luoTanK.getX() + 70) >= (enemyTanKArr.get(i).getX() + EnemyTanK.VERTICAL_HORIZONTAL_RIGHT_X_OFFSET_TANK)
                                && (luoTanK.getY() - 27) <= (enemyTanKArr.get(i).getY() + EnemyTanK.VERTICAL_HORIZONTAL_ABOVE_Y_OFFSET_TANK)
                                && (luoTanK.getY() + 32) >= (enemyTanKArr.get(i).getY() + EnemyTanK.VERTICAL_HORIZONTAL_ABOVE_Y_OFFSET_TANK)) {
                            System.out.println("碰到坦克");

                            //如果撞到了敌方和我方坦克就创建Bomb对象
                            //强制击杀
                            Bomb luoBomb = new Bomb(luoTanK.getX() + 7,
                                    luoTanK.getY() + 1);
                            Bomb enemyBomb = new Bomb(enemyTanKArr.get(i).getX() + 7,
                                    enemyTanKArr.get(i).getY() + 1);
                            bombArrKill.add(luoBomb);
                            bombArrKill.add(enemyBomb);
                            //false代表敌方坦克死亡，结束敌方坦克线程
                            enemyTanKArr.get(i).isLent = false;
                            //在vector集合删除坦克，不在画坦克
                            enemyTanKArr.remove(i);
                            //我方坦克变成null，我方坦克死亡
                            luoTanK = null;
                            System.out.println("撞到了和我方敌方坦克消失");
                        }

                        //i坦克的左下角x判断i坦克
                        if ((luoTanK.getX()) <= (enemyTanKArr.get(i).getX() + EnemyTanK.VERTICAL_HORIZONTAL_LEFT_X_OFFSET_TANK)
                                && (luoTanK.getX() + 70) >= (enemyTanKArr.get(i).getX() + EnemyTanK.VERTICAL_HORIZONTAL_LEFT_X_OFFSET_TANK)
                                && (luoTanK.getY() - 27) <= (enemyTanKArr.get(i).getY() + EnemyTanK.VERTICAL_HORIZONTAL_BELOW_Y_OFFSET_TANK)
                                && (luoTanK.getY() + 32) >= (enemyTanKArr.get(i).getY() + EnemyTanK.VERTICAL_HORIZONTAL_BELOW_Y_OFFSET_TANK)) {
                            System.out.println("碰到坦克");

                            //如果撞到了敌方和我方坦克就创建Bomb对象
                            //强制击杀
                            Bomb luoBomb = new Bomb(luoTanK.getX() + 7,
                                    luoTanK.getY() + 1);
                            Bomb enemyBomb = new Bomb(enemyTanKArr.get(i).getX() + 7,
                                    enemyTanKArr.get(i).getY() + 1);
                            bombArrKill.add(luoBomb);
                            bombArrKill.add(enemyBomb);
                            //false代表敌方坦克死亡，结束敌方坦克线程
                            enemyTanKArr.get(i).isLent = false;
                            //在vector集合删除坦克，不在画坦克
                            enemyTanKArr.remove(i);
                            //我方坦克变成null，我方坦克死亡
                            luoTanK = null;
                            System.out.println("撞到了和我方敌方坦克消失");
                        }
                        //i坦克的右下角x判断i坦克
                        if (luoTanK.getX() <= (enemyTanKArr.get(i).getX() + EnemyTanK.VERTICAL_HORIZONTAL_RIGHT_X_OFFSET_TANK)
                                && (luoTanK.getX() + 70) >= (enemyTanKArr.get(i).getX() + EnemyTanK.VERTICAL_HORIZONTAL_RIGHT_X_OFFSET_TANK)
                                && (luoTanK.getY() - 27) <= (enemyTanKArr.get(i).getY() + EnemyTanK.VERTICAL_HORIZONTAL_BELOW_Y_OFFSET_TANK)
                                && (luoTanK.getY() + 32) >= (enemyTanKArr.get(i).getY() + EnemyTanK.VERTICAL_HORIZONTAL_BELOW_Y_OFFSET_TANK)) {
                            System.out.println("碰到坦克");

                            //如果撞到了敌方和我方坦克就创建Bomb对象
                            //强制击杀
                            Bomb luoBomb = new Bomb(luoTanK.getX() + 7,
                                    luoTanK.getY() + 1);
                            Bomb enemyBomb = new Bomb(enemyTanKArr.get(i).getX() + 7,
                                    enemyTanKArr.get(i).getY() + 1);
                            bombArrKill.add(luoBomb);
                            bombArrKill.add(enemyBomb);
                            //false代表敌方坦克死亡，结束敌方坦克线程
                            enemyTanKArr.get(i).isLent = false;
                            //在vector集合删除坦克，不在画坦克
                            enemyTanKArr.remove(i);
                            //我方坦克变成null，我方坦克死亡
                            luoTanK = null;
                            System.out.println("撞到了和我方敌方坦克消失");
                        }
                        break;

                }
            }
        }

    }

    //判断敌方坦克的子弹跟建筑物的碰撞体积
    public void collisonHeroEnemyTankBulid(){
        //i敌方坦克
        for (int i = 0; i < enemyTanKArr.size(); i++) {
            //j是i坦克的子弹
            for (int j = 0; j < enemyTanKArr.get(i).vectorEnemyHeroZiDuan.size(); j++) {
                //b是建筑物
                for (int b = 0; b < buildArr.size(); b++) {
                    if (buildArr.get(b) instanceof Wood){
                        if (buildArr.get(b).getX() < enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).getZiDuanX() - 3
                                && (buildArr.get(b).getX() + 35) > enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).getZiDuanX() - 13
                                && (buildArr.get(b).getY()) < enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).getZiDuanY() + 32
                                && (buildArr.get(b).getY() + 35) > enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).getZiDuanY() + 32) {
                            buildArr.remove(b);
                            //爆炸特性
                            bombArrHero.add(new Bomb(enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).getZiDuanX() - 15,
                                    enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).getZiDuanY() - 2));
                            enemyTanKArr.get(i).vectorEnemyHeroZiDuan.remove(j);
                            break;
                        }
                    } else if (buildArr.get(b) instanceof Brick){
                        if (buildArr.get(b).getX() < enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).getZiDuanX() - 3
                                && (buildArr.get(b).getX() + 35) > enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).getZiDuanX() - 13
                                && (buildArr.get(b).getY()) < enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).getZiDuanY() + 32
                                && (buildArr.get(b).getY() + 35) > enemyTanKArr.get(i).vectorEnemyHeroZiDuan.get(j).getZiDuanY() + 32) {
                            enemyTanKArr.get(i).vectorEnemyHeroZiDuan.remove(j);
                            break;
                        }
                    } else {
                        System.out.println("没有这类建筑");
                    }

                }
            }
        }
    }

    //判断我方坦克的子弹跟建筑物的碰撞体积
    public void collisonHeroLuoTankBulid(){
        //i是我方坦克的子弹，b是建筑物
        for (int i = 0; i < luoTanK.vectorLuoHeroZiDuan.size(); i++){
            for (int b = 0; b < buildArr.size(); b++) {
                if (buildArr.get(b) instanceof Wood){
                    if (buildArr.get(b).getX() < luoTanK.vectorLuoHeroZiDuan.get(i).getZiDuanX() - 3
                            && (buildArr.get(b).getX() + 35) > luoTanK.vectorLuoHeroZiDuan.get(i).getZiDuanX() - 13
                            && (buildArr.get(b).getY()) < luoTanK.vectorLuoHeroZiDuan.get(i).getZiDuanY() + 32
                            && (buildArr.get(b).getY() + 35) > luoTanK.vectorLuoHeroZiDuan.get(i).getZiDuanY() + 32) {
                        buildArr.remove(b);
                        //爆炸特性
                        bombArrHero.add(new Bomb(luoTanK.vectorLuoHeroZiDuan.get(i).getZiDuanX() - 16,
                                luoTanK.vectorLuoHeroZiDuan.get(i).getZiDuanY() - 2));
                        luoTanK.vectorLuoHeroZiDuan.remove(i);
                        break;
                    }
                } else if (buildArr.get(b) instanceof Brick){
                    if (buildArr.get(b).getX() < luoTanK.vectorLuoHeroZiDuan.get(i).getZiDuanX() - 3
                            && (buildArr.get(b).getX() + 35) > luoTanK.vectorLuoHeroZiDuan.get(i).getZiDuanX() - 13
                            && (buildArr.get(b).getY()) < luoTanK.vectorLuoHeroZiDuan.get(i).getZiDuanY() + 32
                            && (buildArr.get(b).getY() + 35) > luoTanK.vectorLuoHeroZiDuan.get(i).getZiDuanY() + 32) {
                        luoTanK.vectorLuoHeroZiDuan.remove(i);
                        break;
                    }
                } else {
                    System.out.println("没有这类建筑");
                }

            }
        }
    }

    //显示玩家成绩的方法
    public void playGameAchievement(Graphics g) {
        //字体改成黑色
        g.setColor(Color.black);
        //字体调整
        g.setFont(new Font("宋体", Font.BOLD, 18));
        g.drawString("我方积累击杀敌方总数:", 800, 30);
        //总击杀数显示 把recorder里的long类型转型成字符串
        g.drawString(String.valueOf("x" + recorder.getMyTankKillCount()), 1003, 30);
        //敌方坦克显示击杀
        g.drawString(String.valueOf("x" + recorder.getMyTotalKillCount()), 900, 80);
    }


    /**
     * 我方坦克样子，可以在TankGame里的画板里画出来
     *
     * @param x      坦克的左上角x 坐标
     * @param y      坦克的右上角y 坐标
     * @param g      画笔
     * @param direct 正常坦克方向(上下左右) 0-3 。 模型坦克方向 10-13
     * @param type   坦克类型 0自己的坦克， 1敌人的坦克， 2我方坦克模型， 3敌方坦克模型
     * @param tank   坦克对象
     */
    public void huaTanKe(int x, int y, Graphics g, int direct, int type, Tank tank) {

        //向上转型
        Tank tankLuoOrEnery = null;
        //判断敌方还是我方，还是我方模型和敌方模型
        switch (type) {
            case 0: //我们的坦克
                //我方坦克颜色改成青色
                g.setColor(Color.cyan);
                //如果type 0就转型成我方坦克
                tankLuoOrEnery = (LuoTanK) tank;
                break;
            case 1: //敌人的坦克
                //敌方坦克颜色改成黄色
                g.setColor(Color.yellow);
                //如果type 1就转型成敌人坦克
                tankLuoOrEnery = (EnemyTanK) tank;
                break;
            case 2: //我方模型坦克观看用
                //我方坦克颜色改成青色
                g.setColor(Color.cyan);
                break;
            case 3: //敌方模型坦克观看用
                //敌方坦克颜色改成黄色
                g.setColor(Color.yellow);
                break;
        }

        //坦克图像的方向
        //0-3代表坦克
        //10-13代表模型坦克
        Integer bloodHeight = 65;
        switch (direct) {//控制方向
            case 0: //表示向上
                //以左腿为中心
                g.fill3DRect(x, y, 15, 60, false);//左脚
                g.fill3DRect(x + 45, y, 15, 60, false);//右脚
                g.fill3DRect(x + 15, y + 10, 30, 35, false);//身体
                g.fillOval(x + 20, y + 15, 20, 20);//盖子
                g.drawLine(x + 30, y + 30, x + 30, y - 5);//炮口
                g.setColor(Color.red);
                //上的血量在height形参里
                g.fill3DRect(x - 2, y - 2, 3,
                        GameMathUtils.bloodHeight((double) EnemyTanK.BLOOD_HEIGHT, (double) tankLuoOrEnery.getBlood(), (double) tankLuoOrEnery.getMaxBlood()), true);

                //上面血条改了颜色，在改一次敌方我方坦克颜色
                switch (type) {
                    case 0: //我们的坦克
                        g.setColor(Color.cyan);
                        break;
                    case 1: //敌人的坦克
                        g.setColor(Color.yellow);
                        break;
                }
                break;
            case 1: //下
                g.fill3DRect(x, y, 15, 60, false);//左脚
                g.fill3DRect(x + 45, y, 15, 60, false);//右脚
                g.fill3DRect(x + 15, y + 10, 30, 35, false);//身体
                g.fillOval(x + 20, y + 15, 20, 20);//盖子
                g.drawLine(x + 30, y + 30, x + 30, y + 65);//炮口
                g.setColor(Color.red);
                //下的血量在height形参里
                g.fill3DRect(x - 2, y - 2, 3,
                        GameMathUtils.bloodHeight((double) EnemyTanK.BLOOD_HEIGHT, (double) tankLuoOrEnery.getBlood(), (double) tankLuoOrEnery.getMaxBlood()), true);

                switch (type) {
                    case 0: //我们的坦克
                        g.setColor(Color.cyan);
                        break;
                    case 1: //敌人的坦克
                        g.setColor(Color.yellow);
                        break;
                }
                break;
            case 2: //左
                g.fill3DRect(x, y, 60, 15, false);//左脚
                g.fill3DRect(x, y + 45, 60, 15, false);//右脚
                g.fill3DRect(x + 10, y + 15, 35, 30, false);//身体
                g.fillOval(x + 15, y + 20, 20, 20);//盖子
                g.drawLine(x + 30, y + 30, x - 5, y + 30);//炮口
                g.setColor(Color.red);
                //左的血量在width形参里
                g.fill3DRect(x - 2, y - 7,
                        GameMathUtils.bloodHeight((double) EnemyTanK.BLOOD_HEIGHT, (double) tankLuoOrEnery.getBlood(), (double) tankLuoOrEnery.getMaxBlood()), 3, true);

                switch (type) {
                    case 0: //我们的坦克
                        g.setColor(Color.cyan);
                        break;
                    case 1: //敌人的坦克
                        g.setColor(Color.yellow);
                        break;
                }
                break;
            case 3: //右
                g.fill3DRect(x, y, 60, 15, false);//左脚
                g.fill3DRect(x, y + 45, 60, 15, false);//右脚
                g.fill3DRect(x + 10, y + 15, 35, 30, false);//身体
                g.fillOval(x + 15, y + 20, 20, 20);//盖子
                g.drawLine(x + 30, y + 30, x + 65, y + 30);//炮口
                g.setColor(Color.red);
                //右的血量在width形参里
                g.fill3DRect(x - 2, y - 7,
                        GameMathUtils.bloodHeight((double) EnemyTanK.BLOOD_HEIGHT, (double) tankLuoOrEnery.getBlood(), (double) tankLuoOrEnery.getMaxBlood()), 3, true);

                switch (type) {
                    case 0: //我们的坦克
                        g.setColor(Color.cyan);
                        break;
                    case 1: //敌人的坦克
                        g.setColor(Color.yellow);
                        break;
                }
                break;
            //10上 11下 12左 13右
            case 10: //模型坦克
                //以左腿为中心
                g.fill3DRect(x, y, 15, 60, false);//左脚
                g.fill3DRect(x + 45, y, 15, 60, false);//右脚
                g.fill3DRect(x + 15, y + 10, 30, 35, false);//身体
                g.fillOval(x + 20, y + 15, 20, 20);//盖子
                g.drawLine(x + 30, y + 30, x + 30, y - 5);//炮口
                //因为没有血条所以不用再次改颜色
                break;
            case 11:
                g.fill3DRect(x, y, 15, 60, false);//左脚
                g.fill3DRect(x + 45, y, 15, 60, false);//右脚
                g.fill3DRect(x + 15, y + 10, 30, 35, false);//身体
                g.fillOval(x + 20, y + 15, 20, 20);//盖子
                g.drawLine(x + 30, y + 30, x + 30, y + 65);//炮口
                break;
            case 12:
                g.fill3DRect(x, y, 60, 15, false);//左脚
                g.fill3DRect(x, y + 45, 60, 15, false);//右脚
                g.fill3DRect(x + 10, y + 15, 35, 30, false);//身体
                g.fillOval(x + 15, y + 20, 20, 20);//盖子
                g.drawLine(x + 30, y + 30, x - 5, y + 30);//炮口
                break;
            case 13:
                g.fill3DRect(x, y, 60, 15, false);//左脚
                g.fill3DRect(x, y + 45, 60, 15, false);//右脚
                g.fill3DRect(x + 10, y + 15, 35, 30, false);//身体
                g.fillOval(x + 15, y + 20, 20, 20);//盖子
                g.drawLine(x + 30, y + 30, x + 65, y + 30);//炮口
                break;
        }
    }

    /**
     *
     * @param x     建筑的x坐标
     * @param y     建筑的y坐标
     * @param g     画板
     * @param build 建筑类
     */
    public void huaBuild(int x, int y, Graphics g, Build build){
        Build b = build;
        if (b instanceof Brick){
            g.drawImage(imageBrick , x, y, 35, 35, null);
        } else if (b instanceof Wood){
            g.drawImage(imageWork, x, y, 35, 35, null);
        }
    }

    //主页面显示方法
    public void zhuUi(Graphics g) {
        //填充主ui页面
        g.fillRect(0, 0, 2000, 2000);
        //我方坦克模型部署，y每次加100
        for (int i = 0; i < 5; i++) {
            huaTanKe(230, ((i + 1) * 100) + 100, g, 10, 2, null);
        }
        //敌方坦克模型部署，y每次加100
        for (int i = 0; i < 5; i++) {
            huaTanKe(630, ((i + 1) * 100) + 100, g, 10, 3, null);
        }

        //设置颜色橙色
        g.setColor(Color.orange);
        //设置长方形
        g.fill3DRect(314, 210, 290, 41, false);
        g.fill3DRect(314, 310, 290, 41, false);
        g.fill3DRect(314, 410, 290, 41, false);
        g.fill3DRect(314, 510, 290, 41, false);
        g.fill3DRect(314, 610, 290, 41, false);

        //设置字体颜色，和设置字体大小
        g.setColor(Color.black);
        g.setFont(new Font("宋体", Font.BOLD, 20));
        //页面字幕
        g.drawString("开始新游戏", 403, 233);
        g.drawString("继续上局游戏", 395, 333);
        g.drawString("设置", 440, 433);
        g.drawString("制作人", 430, 533);
        g.drawString("退出", 440, 633);

        //ui的图片
        g.drawImage(imageAZS, 60, 350, 100, 100, null);
    }

    //关卡页面显示方法   design 设计
    public void levelDesign(Graphics g) {
        //填充关卡ui页面
        g.fillRect(0, 0, 2000, 2000);
        //设置字体颜色，和设置字体大小
        g.setFont(new Font("宋体", Font.BOLD, 30));
        g.setColor(Color.orange);
        g.drawString("选择关卡", 435, 30);
        g.drawString("注:暂时只开启了3关", 735, 30);
        //关卡的图片设计，j代表左右，i代表上下，s代表数字
        int s = 1;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 11; j++) {
                g.setColor(Color.orange);
                g.fill3DRect(j * 100, (i + 1) * 85, 60, 60, false);
                g.setColor(Color.black);
                g.drawString(String.valueOf(s),  (j * 100) + 15, (i + 1) * 110);
                s++;
            }
        }
    }

    public void setUpDesign(Graphics g){
        g.fillRect(0, 0, 2000, 2000);
        //设置字体颜色，和设置字体大小
        g.setFont(new Font("宋体", Font.BOLD, 30));
        g.setColor(Color.orange);
        g.drawString("设置", 510, 30);
        g.fill3DRect(475, 500, 130, 40, false);
        g.fill3DRect(505, 630, 60, 30, false);
        //设置字体颜色，和设置字体大小
        g.setColor(Color.black);
        g.setFont(new Font("宋体", Font.BOLD, 20));
        g.drawString("退出", 514, 650);
        g.drawString("清空历史记录", 480, 525);
    }

    public void luoMakeGameUidesign(Graphics g) {
        g.fillRect(0, 0, 2000, 2000);
        //设置字体颜色，和设置字体大小
        g.setFont(new Font("宋体", Font.BOLD, 30));
        g.setColor(Color.orange);
        g.drawString("策划:", 510, 30);
        g.drawString("程序:", 510, 150);
        g.drawString("美术:", 510, 270);
        g.drawString("音乐:", 510, 390);
        g.drawString("测试:", 510, 510);
        g.drawString("素材:", 510, 630);
        g.fill3DRect(15, 650, 130, 40, false);
        g.setColor(Color.black);
        g.drawString("onlineName:luo  offlineName:jch", 300, 60);
        g.drawString("onlineName:luo  offlineName:jch", 300, 180);
        g.drawString("onlineName:luo  offlineName:jch", 300, 300);
        g.drawString("onlineName:luo  offlineName:jch", 300, 420);
        g.drawString("onlineName:luo  offlineName:jch", 300, 540);
        g.drawString("自创的和百度下的", 417, 660);

        //设置字体颜色，和设置字体大小
        g.setColor(Color.black);
        g.setFont(new Font("宋体", Font.BOLD, 20));
        g.drawString("退出", 60, 677);
    }


    //prompt font 提示字体    write 写字
    /**
     *
     * @param g 画板
     * @param write 数字多少代表要写什么字
     */

    public void promptFont(Graphics g, int write){
        //字体的生命周期
        if (typefaceLife <= 0){
            //字体死亡后 变成-1，-1说明没有字体
            this.write = -1;
            return;
        }

        //设置字体颜色，和设置字体大小
        g.setColor(Color.black);
        g.setFont(new Font("宋体", Font.BOLD, 30));

        switch (write) {
            case 0:
                g.drawString("没有上一局游戏的记录", 370, 133);
                break;
            case 1:
        }

        //每调用一次字体生命周期-1
        this.typefaceLife--;
    }

    //撞到为真，没撞到为假
    //我方坦克上面的角判断建筑物体积碰撞
    private boolean upLuoTankCollisionBuild(){//方向上
        //if左边是参照物
        for (int i = 0; i < buildArr.size(); i++) {
            //上面方向的左上角
            if (buildArr.get(i).getX() <= luoTanK.getX() - 3
                    && buildArr.get(i).getX() + 35 >= luoTanK.getX() - 3
                    && buildArr.get(i).getY() <= luoTanK.getY()
                    && buildArr.get(i).getY() + 35 >= luoTanK.getY()){
                System.out.println("有建筑物");
                return true;
            }

            //上面方向的右上角
            if (buildArr.get(i).getX() <= luoTanK.getX() + 70
                    && buildArr.get(i).getX() + 35 >= luoTanK.getX() + 70
                    && buildArr.get(i).getY() <= luoTanK.getY()
                    && buildArr.get(i).getY() + 35 >= luoTanK.getY()){
                System.out.println("有建筑物");
                return true;
            }

            //上面方向的上中间
            if (buildArr.get(i).getX() <= luoTanK.getX() + 35
                    && buildArr.get(i).getX() + 35 >= luoTanK.getX() + 35
                    && buildArr.get(i).getY() <= luoTanK.getY()
                    && buildArr.get(i).getY() + 35 >= luoTanK.getY()){
                System.out.println("有建筑物");
                return true;
            }
        }
        //如果一开始就没循环，说明场上没有建筑物了
        return false;
    }

    //我方坦克下面的角判断建筑物体积碰撞
    private boolean downLuoTankCollisionBuild(){//方向下
        //if左边是参照物
        for (int i = 0; i < buildArr.size(); i++) {
            //下面方向的左下角
            if (buildArr.get(i).getX() <= luoTanK.getX() - 3
                    && buildArr.get(i).getX() + 35 >= luoTanK.getX() - 3
                    && buildArr.get(i).getY() <= luoTanK.getY() + 58
                    && buildArr.get(i).getY() + 35 >= luoTanK.getY() + 58){
                System.out.println("有建筑物");
                return true;
            }

            //下面方向的右下角
            if (buildArr.get(i).getX() <= luoTanK.getX() + 70
                    && buildArr.get(i).getX() + 35 >= luoTanK.getX() + 70
                    && buildArr.get(i).getY() <= luoTanK.getY() + 58
                    && buildArr.get(i).getY() + 35 >= luoTanK.getY() + 58){
                System.out.println("有建筑物");
                return true;
            }

            //下面方向的下中间
            if (buildArr.get(i).getX() <= luoTanK.getX() + 35
                    && buildArr.get(i).getX() + 35 >= luoTanK.getX() + 35
                    && buildArr.get(i).getY() <= luoTanK.getY() + 58
                    && buildArr.get(i).getY() + 35 >= luoTanK.getY() + 58){
                System.out.println("有建筑物");
                return true;
            }
        }
        //如果一开始就没循环，说明场上没有建筑物了
        return false;
    }

    //我方坦克左面的角判断建筑物体积碰撞
    private boolean leftLuoTankCollisionBuild(){//方向左
        //if左边是参照物
        for (int i = 0; i < buildArr.size(); i++) {
            //左面方向的左上角
            if (buildArr.get(i).getX() <= luoTanK.getX() - 3
                    && buildArr.get(i).getX() + 35 >= luoTanK.getX() - 3
                    && buildArr.get(i).getY() <= luoTanK.getY()
                    && buildArr.get(i).getY() + 35 >= luoTanK.getY()){
                System.out.println("有建筑物");
                return true;
            }

            //左面方向的左下角
            if (buildArr.get(i).getX() <= luoTanK.getX() - 3
                    && buildArr.get(i).getX() + 35 >= luoTanK.getX() - 3
                    && buildArr.get(i).getY() <= luoTanK.getY() + 53
                    && buildArr.get(i).getY() + 35 >= luoTanK.getY() + 53){
                System.out.println("有建筑物");
                return true;
            }

            //左面方向的左边中间
            if (buildArr.get(i).getX() <= luoTanK.getX() - 3
                    && buildArr.get(i).getX() + 35 >= luoTanK.getX() - 3
                    && buildArr.get(i).getY() <= luoTanK.getY() + 26
                    && buildArr.get(i).getY() + 35 >= luoTanK.getY() + 26){
                System.out.println("有建筑物");
                return true;
            }
        }
        //如果一开始就没循环，说明场上没有建筑物了
        return false;
    }

    //我方坦克右面的角判断建筑物体积碰撞
    private boolean rightLuoTankCollisionBuild(){//方向右
        //if左边是参照物
        for (int i = 0; i < buildArr.size(); i++) {
            //右面方向的右上角
            if (buildArr.get(i).getX() <= luoTanK.getX() + 72
                    && buildArr.get(i).getX() + 35 >= luoTanK.getX() + 72
                    && buildArr.get(i).getY() <= luoTanK.getY()
                    && buildArr.get(i).getY() + 30 >= luoTanK.getY()){
                System.out.println("有建筑物");
                return true;
            }

            //右面方向的右下角
            if (buildArr.get(i).getX() <= luoTanK.getX() + 72
                    && buildArr.get(i).getX() + 35 >= luoTanK.getX() + 72
                    && buildArr.get(i).getY() <= luoTanK.getY() + 53
                    && buildArr.get(i).getY() + 35 >= luoTanK.getY() + 53){
                System.out.println("有建筑物");
                return true;
            }

            //右面方向的右边中间
            if (buildArr.get(i).getX() <= luoTanK.getX() + 72
                    && buildArr.get(i).getX() + 35 >= luoTanK.getX() + 72
                    && buildArr.get(i).getY() <= luoTanK.getY() + 26
                    && buildArr.get(i).getY() + 35 >= luoTanK.getY() + 26){
                System.out.println("有建筑物");
                return true;
            }

        }
        //如果一开始就没循环，说明场上没有建筑物了
        return false;
    }


    //下面接口实现的方法
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                System.out.println("线程异常");
            }

            this.repaint();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (luoTanK == null) {
            System.out.println("luoTanK坦克已死亡");
            return;
        }

        //获取当前时间
        long currentTime = System.currentTimeMillis();
        //检查是否超过了移动间隔
        boolean canMove = (currentTime - lastMoveTime >= MOVE_INTERVAL);

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_W && !(luoTanK.getY() < -5) && !upLuoTankCollisionBuild()) {
            // 只有 canMove 为 true 才执行移动，或者如果是第一次按下lastMoveTime == 0也移动
            if (canMove || lastMoveTime == 0) {
                luoTanK.moveUp();
                luoTanK.setDirect(0);
                lastMoveTime = currentTime; // 更新移动时间
            }
        } else if (key == KeyEvent.VK_S && !(luoTanK.getY() > 665) && !downLuoTankCollisionBuild()) {
            if (canMove || lastMoveTime == 0) {
                luoTanK.moveDown();
                luoTanK.setDirect(1);
                lastMoveTime = currentTime;
            }
        } else if (key == KeyEvent.VK_A && !(luoTanK.getX() < -5) && !leftLuoTankCollisionBuild()) {
            if (canMove || lastMoveTime == 0) {
                luoTanK.moveLeft();
                luoTanK.setDirect(2);
                lastMoveTime = currentTime;
            }
        } else if (key == KeyEvent.VK_D && !(luoTanK.getX() > 732) && !rightLuoTankCollisionBuild()) {
            if (canMove || lastMoveTime == 0) {
                luoTanK.moveRight();
                luoTanK.setDirect(3);
                lastMoveTime = currentTime;
            }
        }

        // 子弹发射逻辑不变
        if (key == KeyEvent.VK_J) {
            System.out.println("按下j");
            if (luoTanK.vectorLuoHeroZiDuan.size() >= 3) {
                System.out.println("子弹数量过多");
                return;
            }
            luoTanK.shotTank();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    //负责处理单击鼠标触发的事件
    @Override
    public void mouseClicked(MouseEvent e) {
        /*
            下面是判断鼠标点击主页面
         */
        //判断是否在主页面，如果不在哪肯定按不了
        if (zhuUiTrueOrFalse) {
            //监听玩家是否点击了开始新游戏
            if (e.getButton() == 1 && e.getX() >= 314 && e.getX() <= 610 && e.getY() >= 238 && e.getY() <= 279 && mouseZhuUi) {
                System.out.println("监听成功");
                //点击了开始游戏不在显示主页面
                zhuUiTrueOrFalse = false;
                //点击了开始游戏，主页面按钮无法点击
                mouseZhuUi = false;
                //关卡选择信息页面可显示
                levelUi = true;
            }

            //监听玩家是否点击了继续上局游戏
            if (e.getButton() == 1 && e.getX() >= 320 && e.getX() <= 610 && e.getY() >= 338 && e.getY() <= 377 && mouseZhuUi) {
                System.out.println("监听成功");

                //把文件存放的上一局的记录，在node.nodeInputStream()方法里取出来
                try {
                    node.nodeInputStream();
                    //如果检测到null指针异常，说明文件里面跟本没有是方坦克的数据
                } catch (NullPointerException ex) {
                    System.out.println("没有上一局的记录");

                    //说明没有数据要保存 变成false
                    isMyFrame = false;
                    //字体生命周期设计成30
                    typefaceLife = 60;
                    //访问字体
                    write = 0;
                    return;
                }

                //点击了继续上局游戏后，把主页面的按钮无法点击
                mouseZhuUi = false;
                //不在显示主页面
                zhuUiTrueOrFalse = false;

                //没有空指针异常说明文件有数据，
                // 就把node里的取出来的我方坦克和敌方坦克给到画板类里的属性
                luoTanK = node.nodeLuoTanK;
                enemyTanKArr = node.nodeEnemyTanKArr;
                //建筑物也是
                buildArr = node.nodeBuildArr;
                //把建筑物的信息赋值到敌方坦克类里
                for (int i = 0; i < enemyTanKArr.size(); i++) {
                    enemyTanKArr.get(i).setBuildArr(node.nodeBuildArr);
                }

                System.out.println("以开始上一局游戏");
                System.out.println(e.getX() + " " + e.getY());
                //开始了游戏，说明有数据要保存了 变成true
                isMyFrame = true;
                //每开始新游戏把我方坦克死亡信息变成70，说明显示死亡信息画70次，每画一次--
                typefaceLuoTankIsKill = 70;
                typefaceEnemyIsKill = 70;
                //这里，播放指定音乐
                new AePlayWave("out\\material\\music\\111.wav").start();
            }

            //监听玩家是否点击了设置
            if (e.getButton() == 1 && e.getX() >= 320 && e.getX() <= 610 && e.getY() >= 439 && e.getY() <= 480 && mouseZhuUi) {
                setUpUi = true;
                zhuUiTrueOrFalse = false;
                System.out.println("点击了设置");
            }

            //监听玩家是否点击了退出
            if (e.getButton() == 1 && e.getX() >= 320 && e.getX() <= 610 && e.getY() >= 640 && e.getY() <= 678 && mouseZhuUi) {
                System.exit(0);
                System.out.println("点击了退出");
            }

            //监听玩家是否点击了制作人
            if (e.getButton() == 1 && e.getX() >= 320 && e.getX() <= 610 && e.getY() >= 540 && e.getY() <= 581 && mouseZhuUi) {
                luoMakeGameUi = true;
                zhuUiTrueOrFalse = false;
                System.out.println("点击了制作人");
            }

        }

        /*
            下面是鼠标点击关卡判断
         */
        //真代表在关卡页面，假代表不在关卡页面
        if (levelUi) {
            //每循环一次++ ，从0开始，代表
            // 0代表第一关，1代表第二关，以此类推
            int s = 0;
            //循环多次次代表有多少关
            for (int i = 0; i < 3; i++) {
                //判断是否点击了关卡
                if (e.getButton() == 1 && e.getX() >= (i * 100) && e.getX() <= ((i * 100) + 60)
                        && e.getY() >= 105 && e.getY() <= 105 + 60) {
                    //判断选择了第几关
                    level.wholeLevel(s);
                    //关卡初始化好的数据，传入到画板
                    luoTanK = level.levelLuoTanK;
                    enemyTanKArr = level.levelEnemyTanKArr;
                    buildArr = level.levelBuildArr;
                    //数据可储存
                    isMyFrame = true;
                    //关闭选择关卡ui页面
                    levelUi = false;
                    //每开始新游戏把我方坦克死亡信息变成70，说明显示死亡信息画70次，每画一次--
                    //敌方也是如此
                    typefaceLuoTankIsKill = 70;
                    typefaceEnemyIsKill = 70;
                    //这里，播放指定音乐
                    new AePlayWave("out\\material\\music\\111.wav").start();
                    System.out.println("点击了关卡");
                    break;
                }
                s++;
            }
        }

        /*
            下面是鼠标点击设置页面
         */
        //设置页面按钮的判断
        if (setUpUi){
            //检测有没有点击清空按钮
            if (e.getButton() == 1 && e.getX() >= 482 && e.getX() <= 610
                    && e.getY() >= 526 && e.getY() <= 565){
                System.out.println("鼠标点击了清空");
                recorder.closeRecord();
            }

            //检测有没有点击退出按钮
            if (e.getButton() == 1 && e.getX() >= 511 && e.getX() <= 572
                    && e.getY() >= 659 && e.getY() <= 688){
                zhuUiTrueOrFalse = true;
                setUpUi = false;
                System.out.println("鼠标点击了退出");
            }
        }
        //制作者页面按钮的判断
        if (luoMakeGameUi) {
            //检测有没有点击退出按钮
            if (e.getButton() == 1 && e.getX() >= 20 && e.getX() <= 150
                    && e.getY() >= 680 && e.getY() <= 720){
                zhuUiTrueOrFalse = true;
                luoMakeGameUi = false;
                System.out.println("鼠标点击了退出");
            }
        }
        System.out.println(e.getX() + " " + e.getY());
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public LuoTanK getLuoTanK() {
        return luoTanK;
    }

    public void setLuoTanK(LuoTanK luoTanK) {
        this.luoTanK = luoTanK;
    }

    public EnemyTanK getEnemyTanK() {
        return enemyTanK;
    }

    public void setEnemyTanK(EnemyTanK enemyTanK) {
        this.enemyTanK = enemyTanK;
    }

    public Vector<Build> getBuildArr() {
        return buildArr;
    }

    public void setBuildArr(Vector<Build> buildArr) {
        this.buildArr = buildArr;
    }

    public int getEnemyTankShuLiang() {
        return enemyTankShuLiang;
    }

    public void setEnemyTankShuLiang(int enemyTankShuLiang) {
        this.enemyTankShuLiang = enemyTankShuLiang;
    }

    public int getTypefaceLife() {
        return typefaceLife;
    }

    public void setTypefaceLife(int typefaceLife) {
        this.typefaceLife = typefaceLife;
    }

    public int getWrite() {
        return write;
    }

    public void setWrite(int write) {
        this.write = write;
    }

    public int getTypefaceLuoTankIsKill() {
        return typefaceLuoTankIsKill;
    }

    public void setTypefaceLuoTankIsKill(int typefaceLuoTankIsKill) {
        this.typefaceLuoTankIsKill = typefaceLuoTankIsKill;
    }

    public int getTypefaceEnemyIsKill() {
        return typefaceEnemyIsKill;
    }

    public void setTypefaceEnemyIsKill(int typefaceEnemyIsKill) {
        this.typefaceEnemyIsKill = typefaceEnemyIsKill;
    }

    public Vector<Bomb> getBombArrKill() {
        return bombArrKill;
    }

    public void setBombArrKill(Vector<Bomb> bombArrKill) {
        this.bombArrKill = bombArrKill;
    }

    public Vector<Bomb> getBombArrHero() {
        return bombArrHero;
    }

    public void setBombArrHero(Vector<Bomb> bombArrHero) {
        this.bombArrHero = bombArrHero;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Image getImageBomb1() {
        return imageBomb1;
    }

    public void setImageBomb1(Image imageBomb1) {
        this.imageBomb1 = imageBomb1;
    }

    public Image getImageBomb2() {
        return imageBomb2;
    }

    public void setImageBomb2(Image imageBomb2) {
        this.imageBomb2 = imageBomb2;
    }

    public Image getImageBomb3() {
        return imageBomb3;
    }

    public void setImageBomb3(Image imageBomb3) {
        this.imageBomb3 = imageBomb3;
    }

    public Image getImageAZS() {
        return imageAZS;
    }

    public void setImageAZS(Image imageAZS) {
        this.imageAZS = imageAZS;
    }

    public Image getImageBrick() {
        return imageBrick;
    }

    public void setImageBrick(Image imageBrick) {
        this.imageBrick = imageBrick;
    }

    public Image getImageWork() {
        return imageWork;
    }

    public void setImageWork(Image imageWork) {
        this.imageWork = imageWork;
    }

    public Recorder getRecorder() {
        return recorder;
    }

    public void setRecorder(Recorder recorder) {
        this.recorder = recorder;
    }

    public boolean isMyFrame() {
        return isMyFrame;
    }

    public void setMyFrame(boolean myFrame) {
        isMyFrame = myFrame;
    }

    public boolean isMouseZhuUi() {
        return mouseZhuUi;
    }

    public void setMouseZhuUi(boolean mouseZhuUi) {
        this.mouseZhuUi = mouseZhuUi;
    }

    public boolean isZhuUiTrueOrFalse() {
        return zhuUiTrueOrFalse;
    }

    public void setZhuUiTrueOrFalse(boolean zhuUiTrueOrFalse) {
        this.zhuUiTrueOrFalse = zhuUiTrueOrFalse;
    }

    public boolean isLevelUi() {
        return levelUi;
    }

    public void setLevelUi(boolean levelUi) {
        this.levelUi = levelUi;
    }

    public boolean isSetUpUi() {
        return setUpUi;
    }

    public void setSetUpUi(boolean setUpUi) {
        this.setUpUi = setUpUi;
    }


}
