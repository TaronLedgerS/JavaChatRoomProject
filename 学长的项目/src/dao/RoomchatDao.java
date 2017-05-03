package dao;

import bean.RoomchatEntity;
import bean.UsersEntity;
import db.DBConnect;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 聊天室操作
 * Created by wuhul on 2016/3/17.
 */
public class RoomchatDao {
    public void createRoom(RoomchatEntity room) throws SQLException {
        Connection conn = DBConnect.getConnection();
        String sql = "insert into roomchat values(NULL,?,?)";
        // 预编译
        PreparedStatement ptmt = conn.prepareStatement(sql);
        // 传参
        ptmt.setString(1, room.getRoomname());
        ptmt.setInt(2, room.getCreatorId());
        // 执行
        ptmt.execute();
    }

    public List<RoomchatEntity> queryRoom() throws SQLException {
        Connection conn = DBConnect.getConnection();
        Statement state = conn.createStatement();
        ResultSet rs = state.executeQuery("select id,roomname,creator_id from roomchat ");
        List<RoomchatEntity> rooms = new ArrayList<>();
        RoomchatEntity room = null;
        while (rs.next()) {
            room = new RoomchatEntity();
            room.setId(rs.getInt("id"));
            room.setRoomname(rs.getString("roomname"));
            room.setCreatorId(rs.getInt("creator_id"));
            rooms.add(room);
        }
        return rooms;
    }

    public List<UsersEntity> queryRoomMember(int id) throws SQLException {
        Connection conn = DBConnect.getConnection();
        Statement state = conn.createStatement();
        ResultSet rs = state.executeQuery("" +
                "SELECT u.id id,u.name name,u.sex sex,u.status status " +
                "FROM roommenber r,users u " +
                "WHERE r.user_id=u.id AND r.room_id = "+id);
        List<UsersEntity> members = new ArrayList<>();
        UsersEntity member = null;
        while (rs.next()) {
            member = new UsersEntity();
            member.setId(rs.getInt("id"));
            member.setName(rs.getString("name"));
            member.setSex(rs.getString("sex"));
            member.setStatus(rs.getInt("status"));
            members.add(member);
        }
        return members;
    }

    public boolean isMember(int roomId, int userId) throws SQLException {
        Connection conn = DBConnect.getConnection();
        Statement state = conn.createStatement();
        ResultSet rs = state.executeQuery("" +
                "SELECT user_id " +
                "FROM roommenber r " +
                "WHERE r.room_id = "+roomId+" AND r.user_id="+userId);
        //conn.close();
        return rs.next() && rs.getInt("user_id") != 0;
    }


    public void joinRoom(int roomId, int userId) throws SQLException {
        Connection conn = DBConnect.getConnection();
        String sql = "insert into roommenber values("+roomId+","+userId+")";
        Statement state = conn.createStatement();
        state.execute(sql);
    }
}
