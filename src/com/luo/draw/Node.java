package com.luo.draw;

import com.luo.article.*;
import com.luo.level.Level;

import java.io.*;
import java.util.Vector;

//Node节点类 临时存放上一局还没打完的数据
@SuppressWarnings({"all"})
public class Node {
    //输出流，储存上局游戏类

    //缓冲流字符输出流
    private BufferedWriter oos = null;
    //缓冲流字符输入流
    private BufferedReader ois = null;

    public LuoTanK nodeLuoTanK = null;
    public Vector<EnemyTanK> nodeEnemyTanKArr = new Vector<>();

    public Vector<Build> nodeBuildArr = new Vector<>();

    //储存上局游戏的文件路径
    public String filePath = System.getProperty("user.dir") + "\\out\\record\\file.txt";

    public void nodeOutputStream() {
        try {
            oos = new BufferedWriter(new FileWriter(filePath));
            //我方坦克输出到文件
            oos.write(nodeLuoTanK.getX() + " " + nodeLuoTanK.getY() + " "
                    + nodeLuoTanK.getDirect() + " " + nodeLuoTanK.getBlood() + " \n");

            //敌人坦克输出到文件
            for (int i = 0; i < nodeEnemyTanKArr.size(); i++) {
                oos.write(nodeEnemyTanKArr.get(i).getX() + " " + nodeEnemyTanKArr.get(i).getY() + " "
                        + nodeEnemyTanKArr.get(i).getDirect() + " " + nodeEnemyTanKArr.get(i).getBlood() + " \n");
            }
            oos.write("Tank\n");

            //建筑输出到文件
            for (int i = 0; i < nodeBuildArr.size(); i++) {
                //建筑没有方向默认为0
                oos.write(nodeBuildArr.get(i).getX() + " " + nodeBuildArr.get(i).getY() + " "
                        + nodeBuildArr.get(i).getId() + " " + nodeBuildArr.get(i).getBlood()+ " \n");
            }
            oos.write("Build\n");

            System.out.println("存档成功");

            oos.close();
        } catch (FileNotFoundException e) {
            System.out.println("文件不存在，请检查文件路径");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("存档失败");
            e.printStackTrace();
        }
    }

    //把游戏文件读取到内存中
    public void nodeInputStream() throws NullPointerException{
        try {
            ois = new BufferedReader(new FileReader(filePath));
            //先读取文件第一行，第一行的数据是自己的
            String line = ois.readLine();
            //初始化字符串
            String articleStr = "";
            //存放数据的数组，0代表x. 1 y, 2 方向, 3血量
            String[] articleStrArr = new String[4];
            //在文件里读取的数据转成 char数组
            char[] c = line.toCharArray();
            //取出我方的资料，顺序x y 方向 血量
            //临时变量，s负责把x y 方向 血量这些数据放到strArr数组里
            int s = 0;
            //char数组多长循环就多少次
            for (int i = 0; i < c.length; i++) {
                //如果是空格，就把空格前面的数据，存到articleStrArr里，然后articleStr重新赋值
                if (String.valueOf(c[i]).equals(" ")) {
                    //把之前在空格前面的数据拼接好的放到articleStrArr里，然后重置articleStr
                    articleStrArr[s++] = articleStr;
                    articleStr = "";
                    continue;
                }
                //如果不是空格，就把数据存到str里，拼接起来
                articleStr += String.valueOf(c[i]);
            }
            System.out.println(articleStrArr[0] + " " + articleStrArr[1] + " " + articleStrArr[2] + " " + articleStrArr[3]);
            //数组的元素数据传给我方坦克
            nodeLuoTanK = new LuoTanK(Integer.valueOf(articleStrArr[0]), Integer.valueOf(articleStrArr[1]),
                    Integer.valueOf(articleStrArr[2]), 7, Integer.valueOf(articleStrArr[3]));

            /*
                敌方坦克数据读取
             */
            //临时变量t，负责启动线程
            int t = 0;
            //每次循环读取文件的内容一行，读取到的数据給到line
            while ((line = ois.readLine()) != null) {
                //如果读取到的内容是Tank，就跳出循环，说明文件里Tank的内容结束了
                if (line.equals("Tank")){
                    break;
                }

                //s变成0，负责重新在赋值数组
                s = 0;
                //在文件里读取的数据转成 char数组
                c = line.toCharArray();
                //真正把IO流文件里的数据，暂时放入数组的
                for (int i = 0; i < c.length; i++) {
                    //如果是空格，就把空格前面的数据，存到strArr里，然后str重新赋值
                    if (String.valueOf(c[i]).equals(" ")) {
                        articleStrArr[s++] = articleStr;
                        articleStr = "";
                        continue;
                    }
                    //如果不是空格，就把数据存到str里，拼接起来
                    articleStr += String.valueOf(c[i]);
                }

                nodeEnemyTanKArr.add(new EnemyTanK(Integer.valueOf(articleStrArr[0]), Integer.valueOf(articleStrArr[1]),
                            Integer.valueOf(articleStrArr[2]), 7, Integer.valueOf(articleStrArr[3])));
                new Thread(nodeEnemyTanKArr.get(t)).start();
                t++;
            }


            /*
                建筑物数据读取
             */
            while ((line = ois.readLine())!= null){
                //说明建筑物方面的数据已经读取完毕了
                if (line.equals("Build")){
                    break;
                }

                //s变成0，负责重新在赋值数组
                s = 0;
                //在文件里读取的数据转成 char数组
                c = line.toCharArray();
                for (int i = 0; i < c.length; i++) {
                    //如果是空格，就把空格前面的数据，存到strArr里，然后str重新赋值
                    if (String.valueOf(c[i]).equals(" ")) {
                        articleStrArr[s++] = articleStr;
                        articleStr = "";
                        continue;
                    }
                    //如果不是空格，就把数据存到str里，拼接起来
                    articleStr += String.valueOf(c[i]);
                }

                if (articleStrArr[2].equals("001")) {
                    System.out.println("001");
                    nodeBuildArr.add(new Wood(Integer.valueOf(articleStrArr[0]), Integer.valueOf(articleStrArr[1]),
                            "001"));
                } else if (articleStrArr[2].equals("002")){
                    System.out.println("002");
                    nodeBuildArr.add(new Brick(Integer.valueOf(articleStrArr[0]), Integer.valueOf(articleStrArr[1]),
                            "002"));
                }
            }

            System.out.println("读取存档成功");
        } catch (IOException e) {
            System.out.println("读取存档失败");
            e.printStackTrace();
        } finally {
            try {
                ois.close();
            } catch (IOException e) {
                System.out.println("关闭对象输入流失败");
            }
        }
    }

}
