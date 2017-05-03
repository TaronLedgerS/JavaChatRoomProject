package dao;

import db.DBConnect;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 图片存取数据库操作
 * Created by wuhul on 2016/3/19.
 */
public class ImagesDao {
    public int putImages(String images) throws Exception {
        InputStream in = null;
        int index=1;
        in=new FileInputStream(images);
        Connection conn = DBConnect.getConnection();
        Statement state = conn.createStatement();
        ResultSet rs = state.executeQuery(
                "SELECT COUNT(id) s FROM images");
        if(rs.next()&&rs.getInt("s")>=0) {
            index=rs.getInt("s")+1;
        }

        String sql = "insert into images values(?,?)";
        PreparedStatement ptmt = conn.prepareStatement(sql);
        ptmt.setInt(1,index);
        ptmt.setBlob(2,in);
        //ptmt.setBinaryStream(2,in,in.available());
        ptmt.execute();
        in.close();
        return index;
    }

    public String getImages(int id) throws Exception {
        Connection conn = DBConnect.getConnection();
        PreparedStatement ps=conn.prepareStatement("select id,image from images where id=?");
        ps.setInt(1,id);
        ResultSet rs=ps.executeQuery();
        rs.next();    //将光标指向第一行
        int iid = rs.getInt("id");
        InputStream in=rs.getBinaryStream("image");
        byte[] b=new byte[in.available()];    //新建保存图片数据的byte数组
        in.read(b);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String now = sdf.format(new java.util.Date());
        String imageName = "temp/"+now+iid+".jpg";
        OutputStream out=new FileOutputStream(imageName);
        out.write(b);
        out.flush();
        out.close();
        return imageName;
    }
}
