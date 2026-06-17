package com.luo.draw;

import com.luo.article.EnemyTanK;
import com.luo.article.LuoTanK;
import com.luo.level.Level;

import java.io.*;
import java.util.Properties;
import java.util.Vector;

//记录档案Recorder类
@SuppressWarnings({"all"})
public class Recorder {
    Properties properties = new Properties();

    //count 计数，记录自己坦克击杀情况
    //我方坦克击杀敌方情况
    private long myTankKillCount = 0;
    //我方坦克击杀敌方总数情况
    private long myTotalKillCount = 0;

    //记录击杀所以数的文件路径
    public String filePathKillEnemy = System.getProperty("user.dir") + "\\out\\record\\myRecord.properties";


    //取出v值的属性
    public static final String KILLENEMYTANKCOUNT = "killEnemyTankCount";
    public static final String KILLENEMYTOTALCOUNT = "killEnemyTotalCount";

    public long getMyTankKillCount() {
        return myTankKillCount;
    }

    public void setMyTankKillCount(long myTankKillCount) {
        this.myTankKillCount = myTankKillCount;
    }

    public long getMyTotalKillCount() {
        return myTotalKillCount;
    }

    public void setMyTotalKillCount(long myTotalKillCount) {
        this.myTotalKillCount = myTotalKillCount;
    }

    //record 初始化文件
    public void record() throws IOException{
        //加载指定配置文件
        try {
            properties.load(new FileInputStream(filePathKillEnemy));
        } catch (FileNotFoundException e) {
            // 如果出现异常，设置默认值
            properties.setProperty(KILLENEMYTANKCOUNT, "0");
            properties.setProperty(KILLENEMYTOTALCOUNT, "0");
        }

        try {
            //取出来的v值判断是否null
            //如果为null，说明v值说明都没有，如果不是null说明v值是有数据的
            //记录坦克判断是否有k-v
            if (properties.getProperty(KILLENEMYTANKCOUNT) == null){
                properties.setProperty(KILLENEMYTANKCOUNT, "0");
            }

            //记录总数判断是否有k-v
            if (properties.getProperty(KILLENEMYTOTALCOUNT) == null){
                properties.setProperty(KILLENEMYTOTALCOUNT, "0");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        //把字符串转型成long类型
        //把之前记录的值取出来给到需要初始化的属性，文件已有的值会给到long属性
        myTankKillCount = Long.valueOf(properties.getProperty(KILLENEMYTANKCOUNT));
        myTotalKillCount = Long.valueOf(properties.getProperty(KILLENEMYTOTALCOUNT));

        //将k-v真正储存到文件里
        properties.store(new FileOutputStream(filePathKillEnemy), null);

        System.out.println("击杀敌方坦克记录成功");
    }

    public void enemyTankRecord() throws IOException{
        //加载指定配置文件
        properties.load(new FileInputStream(filePathKillEnemy));

        //++记录击杀敌人总数和敌方坦克
        myTotalKillCount++;
        myTankKillCount++;
        //每一次++都要把新的v值传入到文件里
        properties.setProperty(KILLENEMYTANKCOUNT, String.valueOf(myTankKillCount));
        properties.setProperty(KILLENEMYTOTALCOUNT, String.valueOf(myTotalKillCount));
        System.out.println("击杀次数" + myTankKillCount);
        //将k-v真正储存到文件里
        properties.store(new FileOutputStream(filePathKillEnemy), null);

    }

    //清空坦克之前记录的方法
    public void closeRecord() {
        try {
            //加载指定配置文件
            properties.load(new FileInputStream(filePathKillEnemy));
            //覆盖k对应的v值
            properties.setProperty(KILLENEMYTANKCOUNT, "0");
            properties.setProperty(KILLENEMYTOTALCOUNT, "0");

            //将k-v真正储存到文件里
            properties.store(new FileOutputStream(filePathKillEnemy), null);
            System.out.println("以清空");
        } catch (IOException e) {
            System.out.println("清空记录失败");
        }
    }

}
