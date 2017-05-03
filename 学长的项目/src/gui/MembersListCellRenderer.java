package gui;

import bean.RoomchatEntity;
import bean.UsersEntity;

import javax.swing.*;
import java.awt.*;

/**
 * A cell renderer for Font objects that renders the font name in its own font.
 */
public class MembersListCellRenderer extends JLabel implements ListCellRenderer<UsersEntity>
{
    private UsersEntity user;
    private static final int SIZE = 14;

    public Component getListCellRendererComponent(JList<? extends UsersEntity> list,
                                                  UsersEntity value, int index, boolean isSelected, boolean cellHasFocus)
    {
        this.user = value;
        setFont(new Font("Serif", Font.PLAIN, SIZE));
        setText(user.getName());
        if(user.getStatus()!=1){
            setForeground(isSelected ? Color.BLACK : Color.BLACK);
        }else {
            if (user.getSex().equals("M"))
                setForeground(isSelected ? Color.BLUE : Color.BLUE);
            else
                setForeground(isSelected ? Color.RED : Color.RED);
        }
        setBackground(isSelected ? Color.CYAN : Color.WHITE);
        return this;
    }
}
