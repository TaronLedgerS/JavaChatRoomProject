package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by wuhul on 2016/3/17.
 */
public class DBConnect {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/qqchat?useUnicode=true&characterEncoding=utf8";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    private static Connection conn= null;
    static{
        try {
            // 加载驱动程序
            Class.forName("com.mysql.jdbc.Driver");
            // 获得数据库的链接
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static Connection getConnection(){
        return conn;
    }

}
