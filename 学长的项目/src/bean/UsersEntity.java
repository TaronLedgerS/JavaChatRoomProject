package bean;


/**
 * 用户实例
 * Created by wuhul on 2016/3/16.
 */
public class UsersEntity {
    private int id;
    private String name;
    private String password;
    private String sex;
    private int status;

    public UsersEntity() {
    }

    public UsersEntity(String name, String password, String sex, int status) {
        this.name = name;
        this.password = password;
        this.sex = sex;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return "UsersEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", sex='" + sex + '\'' +
                ", status=" + status +
                '}';
    }
}
