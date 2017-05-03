package gui;

import bean.UsersEntity;
import dao.PrivateDao;
import dao.RoomchatDao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.Socket;
import java.sql.SQLException;
import java.util.*;

/**
 * 进入私聊事件
 * Created by wuhul on 2016/3/18.
 */
public class IntoPrivateChat extends MouseAdapter {
    private ChatFrame chatFrame;
    private DefaultListModel<UsersEntity> model;
    private JList<UsersEntity> membersList;
    private UsersEntity toUser;
    private Map<Integer,PrivateChatFrame> privateFrameMap;//私聊窗口Map


    public IntoPrivateChat(RoomChatFrame parent, ChatFrame chatFrame) {
        this.membersList =parent.getMembersList() ;
        this.model = parent.getMembersDefaultListModel();
        this.chatFrame=chatFrame;
        this.privateFrameMap = chatFrame.getPrivateFrameMap();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        toUser = model.elementAt(membersList.locationToIndex(e.getPoint()));
        if (e.getClickCount() == 2) {
            //双击进入私聊
            if(privateFrameMap.containsKey(toUser.getId())&&privateFrameMap.get(toUser.getId()).isVisible()){
                privateFrameMap.get(toUser.getId()).setVisible(true);
            }else {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        PrivateChatFrame privateChatFrame = new PrivateChatFrame(chatFrame,toUser);
                        chatFrame.getFriendDefaultListModel().addElement(toUser);
                        privateFrameMap.put(toUser.getId(), privateChatFrame);
                        privateChatFrame.setVisible(true);
                        try {
                            java.util.List<String> history = new PrivateDao().getLog(chatFrame.getUser().getId(),toUser.getId());
                            for(String h : history){
                                privateChatFrame.getTextArea().append(h);
                            }
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                    }
                });

            }
        }

    }

}

