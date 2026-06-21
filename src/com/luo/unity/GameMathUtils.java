package com.luo.unity;

public class GameMathUtils {

    /**
     * 计算血量高度
     * @param height 初始高度
     * @param blood 当前血量
     * @param maxBlood 最大血量
     * @return
     */
    public static Integer bloodHeight(Double height, Double blood, Double maxBlood) {
        return (int) ((double) blood / maxBlood * height);
    }
}
