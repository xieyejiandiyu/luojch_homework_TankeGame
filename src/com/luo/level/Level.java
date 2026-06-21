package com.luo.level;

import com.luo.article.*;

import java.util.Vector;

public class Level {
    public LuoTanK levelLuoTanK = null;
    public Vector<EnemyTanK> levelEnemyTanKArr = new Vector<>();

    public Vector<Build> levelBuildArr = new Vector<>();

    //选择关卡
    public void wholeLevel(int s) {
        switch (s) {
            case 0:
                level001();
                break;
            case 1:
                level002();
                break;
            case 2:
                level003();
                break;
            case 3:
                level003();
                break;
        }
    }

    private void level001() {
        levelLuoTanK = new LuoTanK(550, 500, 0, 5, 100);
        for (int i = 0; i < 3; i++) {
            levelEnemyTanKArr.add(new EnemyTanK((i + 1) * 100, 100, 1, 5, 100));
            //启动线程
            new Thread(levelEnemyTanKArr.get(i)).start();
        }
    }

    private void level002() {
        levelLuoTanK = new LuoTanK(550, 500, 0, 5, 100);
        for (int i = 0; i < 10; i++) {
            levelBuildArr.add(new Brick((i + 1) * 35 + 135, 150, "002"));
        }

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                levelBuildArr.add(new Brick((j + 1) * 35 + 275, (i + 1) * 35 + 230, "002"));
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 8; j++) {
                levelBuildArr.add(new Wood((j + 1) * 35 + 170, (i + 1) * 185, "001"));
            }
        }

        for (int i = 0; i < 4; i++) {
            levelEnemyTanKArr.add(new EnemyTanK(70, (i + 1) * 135, 1, 5, 100));
            //建筑物对象赋给敌方坦克
            levelEnemyTanKArr.get(i).setBuildArr(levelBuildArr);
            //启动线程
            new Thread(levelEnemyTanKArr.get(i)).start();
        }


    }

    private void level003() {
        levelLuoTanK = new LuoTanK(150, 640, 0, 5, 100);
        for (int i = 0; i < 17; i++) {
            levelBuildArr.add(new Wood((i + 1) * 35 + 85, 200, "001"));
        }
        for (int i = 0; i < 17; i++) {
            levelBuildArr.add(new Wood((i + 1) * 35 + 85, 500, "001"));
        }
        for (int i = 0; i < 8; i++) {
            levelBuildArr.add(new Wood(105, (i + 1) * 35 + 200, "001"));
        }
        for (int i = 0; i < 8; i++) {
            levelBuildArr.add(new Wood(700, (i + 1) * 35 + 200, "001"));
        }

        for (int i = 0; i < 4; i++) {
            levelEnemyTanKArr.add(new EnemyTanK((i + 1) * 150, 300, 1, 5, 100));
            levelEnemyTanKArr.get(i).setBuildArr(levelBuildArr);
            //启动线程
            new Thread(levelEnemyTanKArr.get(i)).start();
        }
    }
}
