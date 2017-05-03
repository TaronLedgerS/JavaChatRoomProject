package bean;

import java.util.Date;

/**
 * 用户登陆日志
 * Created by wuhul on 2016/3/16.
 */
public class UserlogEntity {
    private int id;
    private int userId;
    private String operate;
    private Date date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getOperate() {
        return operate;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
