package gui;

import bean.RoomchatEntity;
import bean.UsersEntity;
import dao.RoomchatDao;
import dao.UserDao;
import service.Client;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 聊天室界面
 * Created by wuhul on 2016/3/16.
 */
public class ChatFrame extends JFrame {
    private UsersEntity user = null;
    private Client client;
    private Socket socket;
    private java.util.List<RoomchatEntity> rooms = null;
    private JLabel nameLable;
    private static final int DEFAULT_WTDTH = 360;
    private static final int DEFAULT_HEIGHT = 470;
    private JMenuItem addRoomMenu;//创建聊天室
    private DefaultListModel<RoomchatEntity> roomDefaultListModel;
    private JList<RoomchatEntity> roomsList;
    private JScrollPane roomsScromllpane;//群列表滚动条
    private JPanel centerPanel;//群列表展示板
    private Map<Integer, RoomChatFrame> roomFrameMap;//群聊天窗口Map
    private Map<Integer, PrivateChatFrame> privateFrameMap;//私聊窗口Map
    private JComboBox<String> statusCombo;//状态
    // 私聊好有列表
    private DefaultListModel<UsersEntity> friendDefaultListModel;
    private JList<UsersEntity> friendsList;
    private JScrollPane friendsScromllpane;
    private JPanel southPanel;
    private JPanel northPanel;

    public DefaultListModel<UsersEntity> getFriendDefaultListModel() {
        return friendDefaultListModel;
    }


    public JList<UsersEntity> getFriendsList() {
        return friendsList;
    }

    public DefaultListModel<RoomchatEntity> getRoomDefaultListModel() {
        return roomDefaultListModel;
    }


    public JList<RoomchatEntity> getRoomsList() {
        return roomsList;
    }


    public UsersEntity getUser() {
        return user;
    }


    public Socket getSocket() {
        return socket;
    }


    public Map<Integer, RoomChatFrame> getRoomFrameMap() {
        return roomFrameMap;
    }


    public Map<Integer, PrivateChatFrame> getPrivateFrameMap() {
        return privateFrameMap;
    }


    //获取屏幕长宽
    Toolkit kit = Toolkit.getDefaultToolkit();
    Dimension screenSize = kit.getScreenSize();
    int screenWidth = screenSize.width;
    int screenHeight = screenSize.height;

    public ChatFrame() {

        roomFrameMap = new HashMap<>();
        privateFrameMap = new HashMap<>();

//        设置标题
        setTitle("CHAT");
//        设置大小
        setSize(DEFAULT_WTDTH, DEFAULT_HEIGHT);
//        设置位置
        setLocation((screenWidth - DEFAULT_WTDTH) / 2, (screenHeight - DEFAULT_HEIGHT) / 2);
//        设置图标
        Image img = new ImageIcon("src/qqchat.png").getImage();
        setIconImage(img);

        /*背景图片*/
//        String path="/home/linyi/Documents/work/java/qqchat/src/background.jpeg";
//        ImageIcon backgound = new ImageIcon(path);
//        JLabel image = new JLabel(backgound);
//        image.setBounds(0,0,this.getWidth(),this.getHeight());
//        //JPanel imageback = (JPanel) this.getContentPane();
//        //imageback.setOpaque(false);
//        this.getLayeredPane().add(image,new Integer(Integer.MIN_VALUE));
//        add(image);

        //设置菜单
        JMenuBar menu = new JMenuBar();
        setJMenuBar(menu);
        JMenu logMenu = new JMenu("菜单");
        menu.add(logMenu);
        JMenuItem registItem = new JMenuItem("注册");
        registItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Regist regist = new Regist();
                regist.showDialog(ChatFrame.this, "Regist");
                login();
            }
        });
        JMenuItem loginItem = new JMenuItem("登陆");
        loginItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        JMenuItem logoutItem = new JMenuItem("注销");
        logoutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new UserDao().offline(user.getId());
                } catch (SQLException ee) {
                    ee.printStackTrace();
                }
                user = null;
                login();
            }
        });
        JMenuItem exitItem = new JMenuItem("退出");
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shutdown();
            }
        });
        logMenu.add(registItem);
        logMenu.add(loginItem);
        logMenu.add(logoutItem);
        logMenu.add(exitItem);
        JMenu actionMenu = new JMenu("操作");
        menu.add(actionMenu);
        addRoomMenu = new JMenuItem("创建聊天室");
        addRoomMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String roomTitle = JOptionPane.showInputDialog(ChatFrame.this, "请给聊天室起个响亮的名字吧：", "创建聊天室", JOptionPane.OK_CANCEL_OPTION);
                if (roomTitle != null && !roomTitle.trim().equals("")) {
                    RoomchatEntity newRoom = new RoomchatEntity(roomTitle, user.getId());
                    try {
                        new RoomchatDao().createRoom(newRoom);
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(ChatFrame.this, "出现未知错误了，请重试！");
                    }
                    JOptionPane.showMessageDialog(ChatFrame.this, "创建成功");
                    roomDefaultListModel.addElement(newRoom);
                }
            }
        });
        actionMenu.add(addRoomMenu);


        northPanel = new JPanel();//上
        centerPanel = new JPanel();//中,聊天列表
        southPanel = new JPanel();//下
//        northPanel.setOpaque(false);
//        centerPanel.setOpaque(false);
//        southPanel.setOpaque(false);

        //设置用户和状态
        JLabel userLable = new JLabel("用户：");
        nameLable = new JLabel("未登录");
        JLabel status = new JLabel("状态：");
        statusCombo = new JComboBox<>();
        statusCombo.addItem("离线");
        statusCombo.addItem("在线");
        statusCombo.addItem("隐身");
        //状态改变事件
        statusCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int s = statusCombo.getSelectedIndex();
                if (s == 0) {
                    try {
                        new UserDao().offline(user.getId());
                        tellServerOffline();
                    } catch (SQLException ee) {
                        ee.printStackTrace();
                    }
                } else if (s == 1) {
                    try {
                        new UserDao().online(user.getId());
                    } catch (SQLException ee) {
                        ee.printStackTrace();
                    }
                } else {
                    try {
                        new UserDao().camouflage(user.getId());
                        tellServerOffline();
                    } catch (SQLException ee) {
                        ee.printStackTrace();
                    }
                }
            }
        });
        JLabel color = new JLabel("换肤：");
        JComboBox colorCombo = new JComboBox<>();
        colorCombo.addItem("默认");
        colorCombo.addItem("蓝色");
        colorCombo.addItem("红色");
        colorCombo.addItem("粉色");
        colorCombo.addItem("黄色");
        colorCombo.addItem("灰色");
        colorCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int s = colorCombo.getSelectedIndex();
                switch (s) {
                    case 0:
                        changeColor(Color.WHITE);
                        break;
                    case 1:
                        changeColor(Color.BLUE);
                        break;
                    case 2:
                        changeColor(Color.RED);
                        break;
                    case 3:
                        changeColor(Color.PINK);
                        break;
                    case 4:
                        changeColor(Color.YELLOW);
                        break;
                    case 5:
                        changeColor(Color.GRAY);
                        break;
                    default:
                        break;
                }
            }
        });

        //添加组件到panel
        northPanel.add(userLable, BorderLayout.WEST);
        northPanel.add(nameLable, BorderLayout.WEST);
        northPanel.add(status, BorderLayout.CENTER);
        northPanel.add(statusCombo, BorderLayout.CENTER);
        northPanel.add(color, BorderLayout.EAST);
        northPanel.add(colorCombo, BorderLayout.EAST);

        //添加组件到窗体
        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
        //add(image);
        //pack();


        //判断用户是否登陆
        if (user == null) {
            addRoomMenu.setEnabled(false);//禁用创建聊天室
            statusCombo.setEnabled(false);//禁用状态
            this.login();
        }

        //聊天室列表
        if (user != null) {
            showRooms();
            showFriends();
        }

        /**
         * 关闭程序提示
         */
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                shutdown();
            }
        });

    }

    /**
     * 换肤
     */
    public void changeColor(Color c) {
        getContentPane().setBackground(c);
        setBackground(c);
        northPanel.setBackground(c);
        centerPanel.setBackground(c);
        southPanel.setBackground(c);
    }

    /**
     * 登陆
     */
    private void login() {
        Login login = new Login();
        if (login.showDialog(ChatFrame.this, "Login")) {
            user = login.getUser();
            nameLable.setText(user.getName());
            //连接服务器
            this.client = new Client();
            try {
                this.socket = client.connect();
                new ClientInputThread(socket, this).start();
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "网络出现错误，请重试！");
                shutdown();
            }
            //发送用户信息到服务器
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(socket.getOutputStream());
                //消息封装成“port标签+端口+userID”
                String message = "port#@" + Integer.toString(socket.getLocalPort()) + "#@" + user.getId();
                writer.println(message);
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            addRoomMenu.setEnabled(true);//解禁创建聊天室
            statusCombo.setEnabled(true);//解禁状态
            this.showRooms();//展示群
            statusCombo.setSelectedIndex(1);//设置为在线状态
            try {
                new UserDao().online(user.getId());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 展示所有群
     */
    private void showRooms() {

        if (roomsScromllpane != null)
            centerPanel.remove(roomsScromllpane);
        try {
            rooms = new RoomchatDao().queryRoom();
            roomDefaultListModel = new DefaultListModel<>();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (rooms != null && rooms.size() > 0) {
            for (RoomchatEntity room : rooms) {
                roomDefaultListModel.addElement(room);
            }
        }
        roomsList = new JList<>(roomDefaultListModel);
        roomsScromllpane = new JScrollPane(roomsList);
        roomsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomsList.setCellRenderer(new RoomsListCellRenderer());
        roomsList.setVisibleRowCount(10);
        roomsList.setFixedCellWidth(220);
        roomsList.setFixedCellHeight(30);
        roomsList.addMouseListener(new IntoRoomChat(this));
        Border roomsBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.CYAN),
                "聊天室",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("微软雅黑", Font.ITALIC, 12));
        roomsList.setBorder(roomsBorder);
        centerPanel.add(roomsScromllpane);
    }

    /**
     * 展示好友
     */
    private void showFriends() {
        friendDefaultListModel = new DefaultListModel<>();
        friendsList = new JList<>(friendDefaultListModel);
        friendsScromllpane = new JScrollPane(friendsList);

        friendsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        friendsList.setCellRenderer(new MembersListCellRenderer());
        friendsList.setVisibleRowCount(10);
        friendsList.setFixedCellWidth(50);
        friendsList.setFixedCellHeight(30);
        friendsList.addMouseListener(new IntoFriendChat(this));
        Border friendsBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.CYAN),
                "私聊列表",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("微软雅黑", Font.ITALIC, 12));
        friendsList.setBorder(friendsBorder);
        centerPanel.add(friendsScromllpane);
    }

    /**
     * 通知所有群用户下线
     */
    private void userOffline(int id) {
        try {
            UsersEntity u = new UserDao().findById(id);
            for (RoomChatFrame roomChatFrame : roomFrameMap.values()) {
                roomChatFrame.memOffline(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void tellServerOffline() {
        //发送消息给服务器说下线了
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(socket.getOutputStream());
            //消息封装成room标签+userID+"offline"+消息
            String message = "room#@" + user.getId() + "#@offline";
            writer.println(message);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭客户端
     */
    private void shutdown() {
        int exi = JOptionPane.showConfirmDialog(null, "要退出该程序吗？", "友情提示", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (exi == JOptionPane.YES_OPTION) {
            if (user != null) {
                try {
                    new UserDao().offline(user.getId());
                /*通知群用户下线*/
                    tellServerOffline();

                } catch (SQLException ee) {
                    ee.printStackTrace();
                }
            }
            //发送用户退出信息到服务器
            if (socket != null) {
                PrintWriter writer = null;
                try {
                    writer = new PrintWriter(socket.getOutputStream());
                    String message = "TUOGOL";
                    writer.println(message);
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            setVisible(false);//隐藏窗体
            System.exit(0);//退出程序
        } else {
            return;
        }
    }


    /**
     * 接收消息
     */
    private class ClientInputThread extends Thread {
        private Socket socket;
        private Component perent;

        public ClientInputThread(Socket socket, Component perent) {
            super();
            this.socket = socket;
            this.perent = perent;
        }

        @Override
        public void run() {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                String message = null;
                while (socket.isConnected()) {
                    synchronized (reader) {
                        message = reader.readLine();
                        System.out.println("接收：" + message);
                        if (message != null) {
                            // 当读服务器信息线程接收到bye，该线程退出
                            if (message.equals("TUOGOL")) {
                                JOptionPane.showMessageDialog(perent, "您的账号在其他地方异常！");
                                try {
                                    new UserDao().offline(user.getId());
                                } catch (SQLException ee) {
                                    ee.printStackTrace();
                                }
                                try {
                                    socket.close();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                                setVisible(false);//隐藏窗体
                                System.exit(0);//退出程序
                                break;
                            }
                            String[] info = message.split("#@");
                            if (info[0].equals("room")) {
                                if (info[2].equals("offline")) {
                                    userOffline(Integer.valueOf(info[1]));
                                } else if (roomFrameMap.containsKey(Integer.valueOf(info[1]))) {
                                    RoomChatFrame thisRoom = roomFrameMap.get(Integer.valueOf(info[1]));
                                    thisRoom.setVisible(true);
                                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                                    String now = sdf.format(new Date());
                                    if (info[2].equals("系统消息：")) {
                                        thisRoom.receiveMsg(message, 0);
                                    } else if (info[3].length() > 7 && info[3].substring(0, 7).equals("#image#")) {
                                        //图片消息
                                        thisRoom.receiveMsg(message, 2);
                                    } else
                                        thisRoom.receiveMsg("<p><span style=\"color: blue\"><b>" + info[2] + "</b></span> <span style=\"color: gray\"><i>" + now + "</i></span><br/>" + info[3] + "</p>", 1);
                                }
                            } else if (info[0].equals("priv")) {
                                if (privateFrameMap.containsKey(Integer.valueOf(info[1]))) {
                                    PrivateChatFrame thisPrivate = privateFrameMap.get(Integer.valueOf(info[1]));
                                    thisPrivate.setVisible(true);
                                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                                    String now = sdf.format(new Date());
                                    thisPrivate.receiveMsg(info[2] + " " + now + "\n" + info[3] + "\n");
                                } else {
                                    //有人私聊此用户，提醒
                                    try {
                                        UsersEntity u = new UserDao().findById(Integer.valueOf(info[1]));
                                        friendDefaultListModel.addElement(u);
                                        ChatFrame.this.setVisible(true);
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                //e.printStackTrace();
                System.out.println("Socket closed");
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
