package com.luo.draw;

import javax.lang.model.type.NullType;
import javax.swing.JFrame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MyFrame extends JFrame {
    private MyPanel myPanel = null;



    public MyFrame() {
        //创建画板
        myPanel = new MyPanel();
        Thread thread = new Thread(myPanel);
        //启动画板线程
        thread.start();
        this.add(myPanel);//把画板添加到画框
        //地图边界800,750
        //记录成绩边界800 + 300, 750
        this.setSize(1100, 750);//窗口大小
        this.addKeyListener(myPanel);//键盘监听
        this.addMouseListener(myPanel);//鼠标监听
        this.setVisible(true);//打开窗口
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//关闭窗口
        //检测窗口退出
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("监听到关闭窗口了");

                if (!myPanel.isMyFrame()){
                    System.out.println("没有数据储存");
                    System.exit(0);
                }

                //先把要储存的游戏数据保存到Recorder.Node 的内部类里的属性
                myPanel.node.nodeLuoTanK = myPanel.getLuoTanK();
                myPanel.node.nodeEnemyTanKArr = myPanel.getEnemyTanKArr();
                myPanel.node.nodeBuildArr = myPanel.getBuildArr();

                //这里才是把数据保存到文件里的方法
                myPanel.node.nodeOutputStream();
                System.exit(0);
            }
        });
    }

}
