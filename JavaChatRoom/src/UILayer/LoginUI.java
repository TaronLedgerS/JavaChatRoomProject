package UILayer;

import BLLayer.UsersBL;
import BLLayer.UsersEntity;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


/*
 * 用户登录界面
 * 2017-05-03 16:39:20
 */
//登录界面是一个(Login)Panel，存在于ChatFrame框架下
public class LoginUI extends JPanel{
	private static final long serialVersionUID = 6329954484003010244L;//自动生成的版本号
	private static final int DEFAULTWIDTH = 240;
	private static final int DEFAULTHEIGHT = 150;
	private JTextField username;//存放用户名的输入框
	private JPasswordField password;//存放密码的输入框
	private JDialog dialog;//对话窗口
	private JButton loginButton;
	
	private UsersEntity user;//null登陆失败
	private boolean ok;

	
	public LoginUI(){
		//创建个小标题
		JLabel welcome = new JLabel("Welcome to ChatRoom!");
		Font font= new Font("Welcome", Font.HANGING_BASELINE, 20);
		welcome.setFont(font);
		//建子Panel装载账号、密码输入框等
		JPanel panel = new JPanel();
		panel.setSize(200,150);
		LayoutManager panelMgr = new GridLayout(2, 2);
		panel.setLayout(panelMgr);
		panel.add(new JLabel("      User name:"));
		panel.add(username = new JTextField());
		panel.add(new JLabel("       Password:"));
		panel.add(password = new JPasswordField());
		panel.setOpaque(false);//设置控件透明;如果为 true，则该组件绘制其边界内的所有像素。
		//登录按钮
		loginButton = new JButton("Login");
		loginButton.addActionListener(new ActionListener() {//登录响应
			public void actionPerformed(ActionEvent event) {
				String tname = username.getText();
				String tpassword = new String(password.getPassword());//char[]转换成String
				//登陆验证
				user = null;
				try {
					user = new UsersBL().loginCheckOut(tname,tpassword);
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Unknown error!Please try again" ,"Login failed", JOptionPane.ERROR_MESSAGE);
				}	
				if(user!=null){//登陆成功、获得用户信息
					ok = true;
					dialog.setVisible(false);//关闭对话框
				}else{
					JOptionPane.showMessageDialog(null,"Invalid username/password!" ,"Login failed", JOptionPane.ERROR_MESSAGE);
					ok = false;
					dialog.setVisible(true);
				}
			}
		});
		//注册按钮
		JButton signUpButton = new JButton("SignUp");
		signUpButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				 SignupUI signupUI = new SignupUI();
				 signupUI.showDialog(LoginUI.this, "Sign Up");
				
			}
		});
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(loginButton);
		buttonPanel.add(signUpButton);
		
		//为本容器(Login)panel设置布局管理（BorderLayout）
		LayoutManager loginMgr = new BorderLayout();
		setLayout(loginMgr);
		add(welcome, BorderLayout.NORTH);
		add(panel,BorderLayout.CENTER);
		add(buttonPanel,BorderLayout.SOUTH);
	}	//Login()
		
	public UsersEntity getUser() {
		return user;		
	}
	//显示登陆窗口
	public boolean showDialog(Component parent,String title){
		ok = false;
		//获取屏幕长度
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		//确认所属Frame窗口
		Frame owner = null;
		if(parent instanceof Frame) 
			owner = (Frame) parent;
		else
			owner = (Frame)SwingUtilities.getAncestorOfClass(Frame.class, parent);
		 // if first time, or if owner has changed, make new dialog
		if(dialog == null || dialog.getOwner() != owner){
			dialog = new JDialog(owner, true);//true表示只能先处理当前窗口
			dialog.add(this);//加载登陆界面到对话窗口
			dialog.getRootPane().setDefaultButton(loginButton);//设置默认按钮可以使用JRootPane类所提供的setDefaultButton()
			dialog.setSize(DEFAULTWIDTH, DEFAULTHEIGHT);
			dialog.setLocation((screenWidth - DEFAULTWIDTH )/2,(screenHeight - DEFAULTHEIGHT)/2 );//以左上角为原点
			dialog.addWindowListener(new WindowAdapter() {//关闭窗口即隐藏窗口
				public void windowClosing(WindowEvent arg0) {
					setVisible(false);
				}	
			});
		}
		dialog.setTitle(title);
		dialog.setVisible(true);//显示		
		return ok;
	}
	
}
