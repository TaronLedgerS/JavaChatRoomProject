package server;

import javax.swing.*;
import java.awt.*;

/**
 * server界面
 * Created by linyi on 16-4-26.
 */
public class ServerFrame extends JFrame {
    private JTextArea textArea1;
    private JPanel panel1;

    private static final int DEFAULT_WTDTH = 500;
    private static final int DEFAULT_HEIGHT = 500;

    public ServerFrame() {
        //获取屏幕长宽
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        //        设置标题
        setTitle("server");
        //        设置大小
        setSize(DEFAULT_WTDTH, DEFAULT_HEIGHT);
        //        设置位置
        setLocation((screenWidth - DEFAULT_WTDTH) / 2, (screenHeight - DEFAULT_HEIGHT) / 2);
        //        设置图标
        Image img = new ImageIcon("src/qqchat.png").getImage();
        setIconImage(img);
        textArea1 = new JTextArea();
        panel1=new JPanel();
        panel1.add(textArea1);
        add(panel1);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//关闭即程序退出
        //server
        new ChatServer(textArea1).startWork();
    }
}
