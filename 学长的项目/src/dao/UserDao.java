package dao;

import bean.UsersEntity;
import db.DBConnect;

import java.sql.*;

/**
 * 用户DAO层
 * Created by wuhul on 2016/3/17.
 */
public class UserDao {
    public boolean regist(UsersEntity user) throws SQLException {
        Connection conn = DBConnect.getConnection();
        Statement state = conn.createStatement();
        ResultSet rs = state.executeQuery(
                "SELECT id FROM users u WHERE u.name='"+user.getName()+"'");
        if(rs.next()&&rs.getInt("id")!=0) {
            //conn.close();
            return false;
        }

        String sql = "insert into users values(NULL,?,?,?,?)";
        // 预编译
        PreparedStatement ptmt = conn.prepareStatement(sql);
        // 传参
        ptmt.setString(1, user.getName());
        ptmt.setString(2, user.getPassword());
        ptmt.setString(3, user.getSex());
        ptmt.setInt(4, user.getStatus());
        // 执行
        ptmt.execute();
        //conn.close();
        return true;
    }

    public UsersEntity login(String name, String password) throws SQLException {
        Connection conn = DBConnect.getConnection();
        // 操作数据库
        Statement state = conn.createStatement();
        ResultSet rs = state.executeQuery(
                "SELECT id,name,password,sex,status FROM users u" +
                        " WHERE u.name='"+name+"' AND u.password='"+password+"'");
        UsersEntity user = null;
        if (rs.next()) {
            user = new UsersEntity();
            user.setId(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
            user.setSex(rs.getString("sex"));
            user.setStatus(rs.getInt("status"));
        }
        //conn.close();
        return user;
    }

    public UsersEntity findById(int id) throws SQLException {
        Connection conn = DBConnect.getConnection();
        // 操作数据库
        Statement state = conn.createStatement();
        ResultSet rs = state.executeQuery(
                "SELECT id,name,password,sex,status FROM users u WHERE u.id="+id);
        UsersEntity user = null;
        if (rs.next()) {
            user = new UsersEntity();
            user.setId(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
            user.setSex(rs.getString("sex"));
            user.setStatus(rs.getInt("status"));
        }
        return user;
    }

    public void online(int id) throws SQLException {
        Connection conn = DBConnect.getConnection();
        // 操作数据库
        Statement state = conn.createStatement();
        String sql = "UPDATE users SET status = 1 WHERE id="+id;
        state.execute(sql);
    }
    public void camouflage(int id) throws SQLException {
        Connection conn = DBConnect.getConnection();
        // 操作数据库
        Statement state = conn.createStatement();
        String sql = "UPDATE users SET status = 2 WHERE id="+id;
        state.execute(sql);
    }
    public void offline(int id) throws SQLException {
        Connection conn = DBConnect.getConnection();
        // 操作数据库
        Statement state = conn.createStatement();
        String sql = "UPDATE users SET status = 0 WHERE id="+id;
        state.execute(sql);
    }
}
