package dao;

import db.DBConnect;

import java.sql.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * 私聊信息存储
 * Created by wuhul on 2016/3/20.
 */
public class PrivateDao {

    public void log(String inputMsg, int id1, int id2) throws SQLException {
        Connection conn = DBConnect.getConnection();
        Statement statement = conn.createStatement();
        String sql="SELECT id FROM privatechat " +
                "WHERE user1_id IN (" + id1 + "," + id2 + ") AND user2_id IN (" + id1 + "," + id2 + ")";
        ResultSet resultSet = statement.executeQuery(sql);
        int pid=0;
        if(resultSet.next()) {
            pid = resultSet.getInt("id");
        }
        if (pid==0) {
            //建立朋友关系
            statement.execute("INSERT INTO privatechat VALUES (NULL ," + id1 + "," + id2 + ")");
            statement=conn.createStatement();
            resultSet = statement.executeQuery(sql);
            resultSet.next();
            pid = resultSet.getInt("id");
        }
        System.out.println("pid"+pid);
        sql = "insert into privatechatlog (pid, user_id, content, date ) values (?,?,?,?)";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1,pid);
        preparedStatement.setInt(2,id1);
        preparedStatement.setString(3,inputMsg);
        preparedStatement.setDate(4,new Date(new java.util.Date().getTime()));
        preparedStatement.execute();

    }

    public List<String> getLog(int id1, int id2) throws SQLException {
        List<String> list = new ArrayList<>();
        Connection conn = DBConnect.getConnection();
        Statement statement = conn.createStatement();
        String sql="SELECT id FROM privatechat " +
                "WHERE user1_id IN (" + id1 + "," + id2 + ") AND user2_id IN (" + id1 + "," + id2 + ")";
        ResultSet resultSet = statement.executeQuery(sql);
        int pid=0;
        if(resultSet.next())
            pid = resultSet.getInt("id");
        statement = conn.createStatement();
        sql="SELECT name,content,date FROM users u,privatechatlog p " +
                "WHERE p.pid ="+pid+" AND u.id = p.user_id AND p.user_id IN ("+id1+","+id2+")";
        ResultSet rs=statement.executeQuery(sql);
        if(rs.next()){
            String s = rs.getString("name") + " " + rs.getDate("date") + "\n" + rs.getString("content") + "\n";
            list.add(s);
        }
        return list;
    }
}
