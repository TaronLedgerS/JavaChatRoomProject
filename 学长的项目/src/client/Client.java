package client;

import gui.ChatFrame;

import javax.swing.*;
import java.awt.*;

/**
 * 客户端
 * Created by wuhul on 2016/3/16.
 */
public class Client {
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ChatFrame frame = new ChatFrame();
                frame.setVisible(true);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//关闭即程序退出
            }
        });
    }
}

