package bean;


/**
 * 私聊实例
 * Created by wuhul on 2016/3/16.
 */
public class PrivatechatEntity {
    private int id;
    private int user1Id;
    private int user2Id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser1Id() {
        return user1Id;
    }

    public void setUser1Id(int user1Id) {
        this.user1Id = user1Id;
    }

    public int getUser2Id() {
        return user2Id;
    }

    public void setUser2Id(int user2Id) {
        this.user2Id = user2Id;
    }
}
