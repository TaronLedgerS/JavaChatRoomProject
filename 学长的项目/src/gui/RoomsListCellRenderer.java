package gui;

import bean.RoomchatEntity;
import bean.UsersEntity;
import dao.RoomchatDao;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.Socket;
import java.sql.SQLException;
import javax.swing.*;

/**
 * A cell renderer for Font objects that renders the font name in its own font.
 */
public class RoomsListCellRenderer extends JLabel implements ListCellRenderer<RoomchatEntity>
{
    private RoomchatEntity room;
    private static final int SIZE = 24;

    public Component getListCellRendererComponent(JList<? extends RoomchatEntity> list,
                                                  RoomchatEntity value, int index, boolean isSelected, boolean cellHasFocus)
    {
        this.room = value;
        setFont(new Font("Serif", Font.PLAIN, SIZE));
        setText(room.getRoomname());
        //setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
        //setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
        return this;
    }
}
