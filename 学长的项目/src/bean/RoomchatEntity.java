package bean;



/**
 * 群实例
 * Created by wuhul on 2016/3/17.
 */
public class RoomchatEntity {
    private int id;
    private String roomname;
    private int creatorId;

    public RoomchatEntity() {
    }

    public RoomchatEntity(String roomname, int creatorId) {
        this.roomname = roomname;
        this.creatorId = creatorId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomname() {
        return roomname;
    }

    public void setRoomname(String roomname) {
        this.roomname = roomname;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

}
