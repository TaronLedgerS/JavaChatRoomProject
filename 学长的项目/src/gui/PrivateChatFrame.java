package gui;

import bean.RoomchatEntity;
import bean.UsersEntity;
import dao.PrivateDao;
import dao.RoomchatDao;
import dao.UserDao;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 聊天室界面
 * Created by wuhul on 2016/3/16.
 */
class PrivateChatFrame extends JFrame {
    private ChatFrame parent;
    private UsersEntity user;//用户
    private UsersEntity toUser;//用户
    private JTextArea textArea;//消息接收框
    private JTextArea textInput;//消息发送框
    private Socket socket;
    private static final int DEFAULT_WTDTH = 600;
    private static final int DEFAULT_HEIGHT = 600;
    private static final int DEFAULT_SIZE = 12;

    public JTextArea getTextArea() {
        return textArea;
    }

    public void setTextArea(JTextArea textArea) {
        this.textArea = textArea;
    }

    //获取屏幕长宽
    Toolkit kit = Toolkit.getDefaultToolkit();
    Dimension screenSize = kit.getScreenSize();
    int screenWidth = screenSize.width;
    int screenHeight = screenSize.height;

    public PrivateChatFrame(ChatFrame parent, UsersEntity tu) {
        this.parent=parent;
        this.toUser = tu;
        this.user = parent.getUser();
        this.socket = parent.getSocket();
        //        设置标题
        setTitle(this.toUser.getName());
        //        设置大小
        setSize(DEFAULT_WTDTH, DEFAULT_HEIGHT);
        //        设置位置
        setLocation((screenWidth - DEFAULT_WTDTH) / 2, (screenHeight - DEFAULT_HEIGHT) / 2);
        //        设置图标
        Image img = new ImageIcon("src/qqchat.png").getImage();
        setIconImage(img);

        JPanel northPanel = new JPanel();//上
        JPanel centerPanel = new JPanel();//中
        JPanel southPanel = new JPanel();//下

        Border textBorder = BorderFactory.createLineBorder(Color.CYAN);
        textArea = new JTextArea(10,50);//聊天室文本框
        textArea.setBorder(textBorder);
        JScrollPane scrollPane = new JScrollPane(textArea);//滚动条

        textInput = new JTextArea(3,40);//文字输入框
        textInput.setBorder(BorderFactory.createLineBorder(Color.BLUE,1));//边框
        JScrollPane textInputScrollPane = new JScrollPane(textInput);//滚动条
        final JButton submit = new JButton("发送");//消息发送
        submit.setMnemonic(KeyEvent.VK_ENTER);
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    user = new UserDao().findById(user.getId());
                } catch (SQLException ee) {
                    ee.printStackTrace();
                }
                //如果离线不能发送
                if(user!=null&&user.getStatus()==0){
                    JOptionPane.showMessageDialog(PrivateChatFrame.this, "您当前处于离线状态！不能发送消息！");
                }else {
                    //发送消息
                    try {
                        String inputMsg = textInput.getText();
                        //存储到数据库
                        try {
                            new PrivateDao().log(inputMsg,user.getId(),toUser.getId());
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }

                        //发送到服务器
                        PrintWriter writer = new PrintWriter(socket.getOutputStream());
                        //消息封装成“priv标签+私聊对象ID+用户名+消息”
                        String message = "priv#@" + toUser.getId() + "#@" + user.getName() + "#@" + inputMsg;
                        writer.println(message);
                        writer.flush();
                        textInput.setText("");
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                        String now = sdf.format(new Date());
                        textArea.append(user.getName() + " " + now + "\n" + inputMsg + "\n");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
            }
            }
        });


        //添加组件到panel
        centerPanel.add(scrollPane,BorderLayout.WEST);
        southPanel.add(textInputScrollPane,BorderLayout.WEST);
        southPanel.add(submit,BorderLayout.EAST);

        //添加组件到窗体
        add(northPanel,BorderLayout.NORTH);
        add(centerPanel,BorderLayout.CENTER);
        add(southPanel,BorderLayout.SOUTH);
        pack();

    }

    //接收消息
    public void receiveMsg(String msg){
        this.textArea.append(msg);
    }
}
