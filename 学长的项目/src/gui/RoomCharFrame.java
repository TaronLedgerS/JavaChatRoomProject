package gui;

import bean.RoomchatEntity;
import bean.UsersEntity;
import dao.ImagesDao;
import dao.RoomchatDao;
import dao.UserDao;
import fileChooser.FileIconView;
import fileChooser.ImagePreviewer;
import fileChooser.ImageViewerFrame;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * 聊天室界面
 * Created by wuhul on 2016/3/16.
 */
class RoomChatFrame extends JFrame {
    private ChatFrame chatFrame;
    private UsersEntity user;//用户
    private JPanel northPanel;//聊天室成员信息展示板
    private RoomchatEntity room;//聊天室
    private JTextPane textPane;//消息接收框
    private HTMLEditorKit htmledit;
    private HTMLDocument text_html;
    private JPanel roomMembersPanel;//聊天室成员展示板
    private java.util.List<UsersEntity> roomMembers;//聊天室成员
    private DefaultListModel<UsersEntity> membersDefaultListModel;
    private JList<UsersEntity> membersList;
    private JTextArea textInput;//消息发送框
    private Socket socket;
    private static final int DEFAULT_WTDTH = 600;
    private static final int DEFAULT_HEIGHT = 600;
    private static final int DEFAULT_SIZE = 16;
    private int onlineNum=0;//在线人数
    private int sumNum=0;//总人数
    private JLabel onlineLabel;
    private JLabel sunNumLabel;
    //文件选择
    private JFileChooser chooser;

    public DefaultListModel<UsersEntity> getMembersDefaultListModel() {
        return membersDefaultListModel;
    }

    public JList<UsersEntity> getMembersList() {
        return membersList;
    }


    //获取屏幕长宽
    Toolkit kit = Toolkit.getDefaultToolkit();
    Dimension screenSize = kit.getScreenSize();
    int screenWidth = screenSize.width;
    int screenHeight = screenSize.height;

    public RoomChatFrame(ChatFrame chatFrame,RoomchatEntity r) {
        this.chatFrame=chatFrame;
        this.room = r;
        try {
            this.user = new UserDao().findById(chatFrame.getUser().getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(user);
        this.socket = chatFrame.getSocket();

        //        设置标题
        setTitle(this.room.getRoomname());
        //        设置大小
        setSize(DEFAULT_WTDTH, DEFAULT_HEIGHT);
        //        设置位置
        setLocation((screenWidth - DEFAULT_WTDTH) / 2, (screenHeight - DEFAULT_HEIGHT) / 2);
        //        设置图标
        Image img = new ImageIcon("src/qqchat.png").getImage();
        setIconImage(img);
        setResizable(false);//禁止拉伸

        northPanel = new JPanel();//上
        JPanel centerPanel = new JPanel();//中
        JPanel southPanel = new JPanel();//下

        Border textBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.CYAN),
                "聊天室",
                TitledBorder.LEFT,TitledBorder.TOP,
                new Font("微软雅黑",Font.ITALIC,12));
        textPane = new JTextPane();//聊天室文本框
        textPane.setPreferredSize(new Dimension(450,400));
        // textPane.setBorder(textBorder);
        JScrollPane scrollPane = new JScrollPane(textPane);//滚动条
        htmledit = new HTMLEditorKit();
        //实例化一个HTMLEditorkit工具包，用来编辑和解析用来显示在jtextpane中的内容。
        text_html = (HTMLDocument) htmledit.createDefaultDocument();
        //使用HTMLEditorKit类的方法来创建一个文档类，HTMLEditorKit创建的类型默认为htmldocument。
        textPane.setEditorKit(htmledit);
        //设置jtextpane组件的编辑器工具包，是其支持html格式。
        textPane.setContentType("text/html");
        //设置编辑器要处理的文档内容类型，有text/html,text/rtf.text/plain三种类型。
        textPane.setDocument(text_html);
        //设置编辑器关联的一个文档
        textPane.setEditable(false);//只读

        //聊天室成员
        showMembers();

        textInput = new JTextArea(3,40);//文字输入框
        JScrollPane textInputScrollPane = new JScrollPane(textInput);//滚动条
        textInput.setBorder(BorderFactory.createLineBorder(Color.BLUE,1));//边框
        final JButton submit = new JButton("发送");//消息发送
        submit.setMnemonic(KeyEvent.VK_ENTER);
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //如果离线不能发送
                try {
                    user = new UserDao().findById(chatFrame.getUser().getId());
                } catch (SQLException ee) {
                    ee.printStackTrace();
                }
                if(user!=null&&user.getStatus()==0){
                    System.out.println(user.getStatus());
                    JOptionPane.showMessageDialog(RoomChatFrame.this, "您当前处于离线状态！不能发送消息！");
                }else if(textInput.getText().trim().equals("")){
                    JOptionPane.showMessageDialog(RoomChatFrame.this, "不能发送空白消息");
                }
                else
                {
                    //发送消息
                    try {
                        PrintWriter writer = new PrintWriter(socket.getOutputStream());
                        //消息封装成“room标签+聊天室ID+用户名+消息”
                        String message = "room#@" + room.getId() + "#@" + user.getName() + "#@" + textInput.getText().replaceAll("\\r", "  ").replaceAll("\\n", "  ");
                        writer.println(message);
                        writer.flush();
                        textInput.setText("");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                textInput.grabFocus();
            }
        });
        textInput.grabFocus();

        //文件选择
        chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "jpg", "jpeg", "gif");
        chooser.setFileFilter(filter);

        chooser.setAccessory(new ImagePreviewer(chooser));

        chooser.setFileView(new FileIconView(filter, new ImageIcon("palette.gif")));
        JButton file = new JButton("图片");
        file.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooser.setCurrentDirectory(new File("."));

                // show file chooser dialog
                int result = chooser.showOpenDialog(RoomChatFrame.this);

                // if image file accepted, set it as icon of the label
                if (result == JFileChooser.APPROVE_OPTION)
                {
                    String name = chooser.getSelectedFile().getPath();

                    //将图片存入数据库
                    try {
                        int imageId = new ImagesDao().putImages(name);
                        PrintWriter writer = new PrintWriter(socket.getOutputStream());
                        //消息封装成“room标签+聊天室ID+用户名+#image#图片ID”
                        String message = "room#@" + room.getId() + "#@" + user.getName() + "#@" + "#image#"+imageId;
                        writer.println(message);
                        writer.flush();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                }

            }
        });

        //添加组件到panel
        centerPanel.add(scrollPane,BorderLayout.WEST);
        centerPanel.add(roomMembersPanel,BorderLayout.EAST);
        southPanel.add(textInputScrollPane,BorderLayout.WEST);
        southPanel.add(submit,BorderLayout.EAST);
        southPanel.add(file,BorderLayout.EAST);

        //添加组件到窗体
        add(northPanel,BorderLayout.NORTH);
        add(centerPanel,BorderLayout.CENTER);
        add(southPanel,BorderLayout.SOUTH);
        pack();

        /**
         * 关闭程序提示
         */
        this.addWindowListener (new WindowAdapter()
        {
            @Override
            public void windowClosing ( WindowEvent e )
            {
                chatFrame.getRoomFrameMap().remove(room.getId());
                setVisible(false);//隐藏窗体
            }
        });

    }

    private void showMembers(){
        Border memsBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.CYAN),
                "群成员",
                TitledBorder.LEFT,TitledBorder.TOP,
                new Font("微软雅黑",Font.ITALIC,12));
        roomMembersPanel = new JPanel();
        //roomMembersPanel.setSize(new Dimension(150,400));
        try {
            roomMembers = new RoomchatDao().queryRoomMember(room.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        membersDefaultListModel = new DefaultListModel<>();
        if (roomMembers != null && roomMembers.size() > 0) {
            for(UsersEntity member : roomMembers) {
                if(member.getStatus()==1) onlineNum++;
                membersDefaultListModel.addElement(member);
            }
            sumNum=roomMembers.size();
        }
        membersList = new JList<>(membersDefaultListModel);
        JScrollPane membersScromllpane = new JScrollPane(membersList);
        membersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        membersList.setCellRenderer(new MembersListCellRenderer());
        membersList.setVisibleRowCount(10);
        membersList.setFixedCellWidth(60);
        membersList.setFixedCellHeight(38);
        membersList.setBorder(memsBorder);
        membersList.addMouseListener(new IntoPrivateChat(this,chatFrame));
        onlineLabel = new JLabel("当前在线人数:"+onlineNum);
        sunNumLabel = new JLabel("群内总人数:"+sumNum);
        northPanel.add(onlineLabel);
        northPanel.add(sunNumLabel);
        roomMembersPanel.add(membersScromllpane);
    }

    //接收消息
    public void receiveMsg(String msg, int b){
        SimpleAttributeSet attr=new SimpleAttributeSet();
        StyleConstants.setFontSize(attr,DEFAULT_SIZE);
        Document docs;//获得文本对象
        docs = textPane.getDocument();

        //是系统消息，有人进来了
        if(b==0) {
            String[] info = msg.split("#@");
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String now = sdf.format(new Date());
            int thisuserid=Integer.valueOf(info[3]);
            UsersEntity u=null;
            try {
                u = new UserDao().findById(thisuserid);
            } catch (SQLException e) {
                e.printStackTrace();
            }if(u!=null) {
                if(membersDefaultListModel.removeElement(u)){
                    sumNum--;
                }
                membersDefaultListModel.add(0, u);
                onlineNum++;
                sumNum++;
                onlineLabel.setText("当前在线人数:"+onlineNum);
                sunNumLabel.setText("群内总人数:"+sumNum);
                //message = "room#@" + info[1] + "#@" + "系统消息：" + "#@" + info[2] +"#@"+info[3]+ "进来了";
                String mesg = info[2] + " " + now + "<br>" + info[4] + "<br>";
                try {
                    textPane.setCaretPosition(docs.getLength());
                    htmledit.insertHTML(text_html, textPane.getCaretPosition(),
                            "<br>", 0, 0, HTML.Tag.BR);
                    htmledit.insertHTML(text_html, textPane.getCaretPosition(),
                            "<div>"+mesg+"</div>", 0, 0, HTML.Tag.DIV);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else if(b==1) {
            try {
                System.out.println(msg);
                textPane.setCaretPosition(docs.getLength());
                htmledit.insertHTML(text_html, textPane.getCaretPosition(),
                        "<br>", 0, 0, HTML.Tag.BR);
                htmledit.insertHTML(text_html, textPane.getCaretPosition(),
                        msg, 0, 0, HTML.Tag.P);

            } catch (BadLocationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(b==2){
            String[] info = msg.split("#@");
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String now = sdf.format(new Date());
            String mesg = "<p><span style=\"color: blue\"><b>"+info[2] + "</b></span> <span style=\"color: gray\"><i>" + now + "</i></span></p>";
            int imageId = Integer.valueOf(info[3].substring(7));
            String imagePath=null;
            try {
                imagePath = new ImagesDao().getImages(imageId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                textPane.setCaretPosition(docs.getLength());
                htmledit.insertHTML(text_html, textPane.getCaretPosition(),
                        "<br>", 0, 0, HTML.Tag.BR);
                htmledit.insertHTML(text_html, textPane.getCaretPosition(),
                        mesg, 0, 0, HTML.Tag.P);
                docs = textPane.getDocument();
                textPane.setCaretPosition(docs.getLength());
                htmledit.insertHTML(text_html, textPane.getCaretPosition(),
                        "<br>", 0, 0, HTML.Tag.BR);
                Image image = Toolkit.getDefaultToolkit().getImage(imagePath);
                image.getScaledInstance(300,300,Image.SCALE_DEFAULT);
                image.flush();
                textPane.insertIcon(new ImageIcon(image));
            } catch (BadLocationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void memOffline(UsersEntity user) {
        if(membersDefaultListModel.removeElement(user)){
            sumNum--;
            onlineNum--;
            membersDefaultListModel.addElement(user);
        }
        onlineLabel.setText("当前在线人数:"+onlineNum);
        sunNumLabel.setText("群内总人数:"+sumNum);
    }

}
