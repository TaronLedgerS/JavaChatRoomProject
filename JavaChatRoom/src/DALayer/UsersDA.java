package DALayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


import BLLayer.DBConnect;
import BLLayer.UsersEntity;

/*
 *  2017-05-04 18:34:55
 *  users实体类的数据访问
 *  即对数据库中users表的“增删减更”
 */
public class UsersDA {
	//登陆验证并获取用户信息
	public UsersEntity findByNamePassword(String name,String password) throws SQLException{
		Connection connection = DBConnect.getConnection();
		Statement statement = connection.createStatement();
		//主语SQL语句的空格
		String sql = "SELECT  * FROM users WHERE name='"+name+"' AND password='"+password+"'";
		ResultSet rs = statement.executeQuery(sql);
		UsersEntity user = null;
		System.out.println("12312312");
		if (rs.next()) {
			user = new UsersEntity();
			user.setID(rs.getInt("id"));
			user.setName(rs.getString("name"));
			user.setPassword(rs.getString("password"));
			user.setSex(rs.getString("sex"));
			user.setStatus(rs.getInt("status"));	
			System.out.println(user.toString());
		}
		rs.close();//关闭结果集
		statement.close(); 
		return user;
	}
    public boolean insert(UsersEntity user) throws SQLException {
        Connection conn = DBConnect.getConnection();
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
        return true;
    }
    public UsersEntity findByName(String name) throws SQLException {
        Connection conn = DBConnect.getConnection();
        Statement state = conn.createStatement();
        ResultSet rs = state.executeQuery(
                "SELECT id,name,password,sex,status FROM users u WHERE u.name='"+name+"'");
        UsersEntity user = null;
        if (rs.next()) {
            user = new UsersEntity();
            user.setID(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
            user.setSex(rs.getString("sex"));
            user.setStatus(rs.getInt("status"));
        }
        return user;
    }
    public UsersEntity findById(int id) throws SQLException {
        Connection conn = DBConnect.getConnection();
        Statement state = conn.createStatement();
        ResultSet rs = state.executeQuery(
                "SELECT id,name,password,sex,status FROM users u WHERE u.id="+id);
        UsersEntity user = null;
        if (rs.next()) {
            user = new UsersEntity();
            user.setID(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
            user.setSex(rs.getString("sex"));
            user.setStatus(rs.getInt("status"));
        }
        return user;
    }
    public void offline(int id) throws SQLException {//用户离线status=0
        Connection conn = DBConnect.getConnection();
        Statement state = conn.createStatement();
        String sql = "UPDATE users SET status = 0 WHERE id="+id;
        state.execute(sql);
    }
	public void online(int id) throws SQLException {//用户上线status=1
        Connection conn = DBConnect.getConnection();
        Statement state = conn.createStatement();
        String sql = "UPDATE users SET status = 1 WHERE id="+id;
        state.execute(sql);
    }
    public void hide(int id) throws SQLException {//用户隐身status=2
        Connection conn = DBConnect.getConnection(); 
        Statement state = conn.createStatement();
        String sql = "UPDATE users SET status = 2 WHERE id="+id;
        state.execute(sql);
    }
	
	
}
