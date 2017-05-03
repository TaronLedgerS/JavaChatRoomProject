package bean;

import java.util.Date;

/**
 * 群聊天日志
 * Created by wuhul on 2016/3/16.
 */
public class RoomchatlogEntity {
    private int id;
    private int roomId;
    private int userId;
    private String content;
    private Date date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


}
