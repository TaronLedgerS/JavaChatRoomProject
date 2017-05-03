package gui;

import bean.RoomchatEntity;
import bean.UsersEntity;
import dao.RoomchatDao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by wuhul on 2016/3/18.
 */
public class IntoRoomChat extends MouseAdapter {
    private JList<RoomchatEntity> roomsList;
    private DefaultListModel<RoomchatEntity> model;
    private RoomchatEntity room;
    private UsersEntity user;
    private Socket socket;
    private ChatFrame parent;
    private Map<Integer,RoomChatFrame> roomFrameMap;//群聊天窗口Map


    public IntoRoomChat(ChatFrame parent) {
        this.roomsList = parent.getRoomsList();
        this.model = parent.getRoomDefaultListModel();
        this.user = parent.getUser();
        this.socket = parent.getSocket();
        this.parent = parent;
        this.roomFrameMap=parent.getRoomFrameMap();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        room = model.elementAt(roomsList.locationToIndex(e.getPoint()));
        //判断是否为群成员
        try {
            if (new RoomchatDao().isMember(room.getId(), user.getId())) {
                //是群成员，进入群聊
                if(roomFrameMap.containsKey(room.getId())&&roomFrameMap.get(room.getId()).isVisible()){
                    roomFrameMap.get(room.getId()).setVisible(true);
                }else {
                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            //发送用户信息到服务器提示入群聊天
                            PrintWriter writer = null;
                            try {
                                writer = new PrintWriter(socket.getOutputStream());
                                //消息封装成“join标签+roomID+userID+userName”
                                String message = "join#@" + room.getId()+"#@" + user.getId() + "#@" + user.getName();
                                writer.println(message);
                                writer.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            RoomChatFrame roomChatFrame = new RoomChatFrame(parent,room);
                            roomFrameMap.put(room.getId(), roomChatFrame);
                            roomChatFrame.setVisible(true);
                        }
                    });
                }
            } else {
                //不是群成员，询问是否加入该群
                int isJoin = JOptionPane.showConfirmDialog(parent, "是否加入该群？", "加群", JOptionPane.OK_OPTION);
                if (isJoin == JOptionPane.OK_OPTION) {
                    //加群
                    new RoomchatDao().joinRoom(room.getId(), user.getId());
                    JOptionPane.showMessageDialog(parent, "您已经是群成员，欢迎进入群聊！");
                }
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

    }

}

