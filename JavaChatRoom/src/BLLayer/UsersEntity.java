package BLLayer;
/*
 * 2017-05-04 18:12:19
 * 用户实体类
 */
public class UsersEntity {
	private int id;
	private String name;
	private String password;
	private String sex;//M\W==男\女
	private int status;//0下线、1上线
	
	public UsersEntity(){
	}
	
	public UsersEntity(String name,String pw,String sex,int status){
		this.name = name;
		this.password = pw;
		this.sex = sex;
		this.status = status;
	}
	//gets
	public int getID() {
		return this.id;
	}
	public String getName() {
		return this.name;
	}
	public String getPassword() {
		return this.password;
	}
	public String getSex() {
		return this.sex;
	}
	public int getStatus() {
		return this.status;
	}
	//set
	public void setID(int id) {
		this.id = id ;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	//信息整合成String
	public String toString(){
		return "UsersEntity{"+
				"id = "+ id +
				",name = \'"+ name +"\'"+
				",password = \'"+ password +"\'"+
				",sex = \'"+ sex +"\'"+
				",status = " + status+
				"}"	;
	}
	
}
