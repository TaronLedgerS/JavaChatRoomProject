package BLLayer;
/*
 * 连接数据库
 * 2017-05-04 21:57:57
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {
	public static String DRIVER_MAYSQL = "com.mysql.jdbc.Driver";//MySQL JDBC驱动字符串
	//数据库URL,用来标识要连接的数据库，其中数据库名、用户名、密码是根据数据库情况设定
	public static String URL = "jdbc:mysql://localhost:3306/chatroom?"
							 + "user=root&password=123456&useUnicode=true&charcterEncoding=UTF8" ;
	private static Connection conn = null;
	static{//静态初始化程序
		try {
			Class.forName(DRIVER_MAYSQL);//加载JDBC驱动
			System.out.println("Driver Load Success.");
			//创建数据库连接对象
			 conn = DriverManager.getConnection(URL);
			System.out.println("GetConnectionToDB");
        } catch (SQLException e){
        	e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static Connection getConnection(){
		return conn;
	}
	public static void closeDB()throws SQLException{
		conn.close();
	}
	
}
