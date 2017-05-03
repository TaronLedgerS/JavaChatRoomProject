package gui;

import bean.UsersEntity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

/**
 * 进入私聊事件
 * Created by wuhul on 2016/3/18.
 */
public class IntoFriendChat extends MouseAdapter {
    private ChatFrame parent;
    private DefaultListModel<UsersEntity> model;
    private JList<UsersEntity> friendsList;
    private UsersEntity toUser;
    private Map<Integer,PrivateChatFrame> privateFrameMap;//私聊窗口Map


    public IntoFriendChat(ChatFrame parent) {
        this.friendsList =parent.getFriendsList() ;
        this.model = parent.getFriendDefaultListModel();
        this.parent=parent;
        this.privateFrameMap = parent.getPrivateFrameMap();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        toUser = model.elementAt(friendsList.locationToIndex(e.getPoint()));
        if (e.getClickCount() == 2) {
            //双击进入私聊
            if(privateFrameMap.containsKey(toUser.getId())&&privateFrameMap.get(toUser.getId()).isVisible()){
                privateFrameMap.get(toUser.getId()).setVisible(true);
            }else {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        PrivateChatFrame privateChatFrame = new PrivateChatFrame(parent,toUser);
                        privateFrameMap.put(toUser.getId(), privateChatFrame);
                        privateChatFrame.setVisible(true);
                    }
                });

            }
        }

    }

}

