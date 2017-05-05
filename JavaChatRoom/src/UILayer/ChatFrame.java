package UILayer;

/*
 * 总结界面
 * 2017-05-03 00:40:54
 */
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import javax.swing.*;

import BLLayer.UsersBL;
import BLLayer.UsersEntity;

import java.awt.*;

public class ChatFrame extends JFrame {//ChatFrame是一个JFrame
	
	//获取屏幕尺寸
	Toolkit kit = Toolkit.getDefaultToolkit();
	Dimension screenSize = kit.getScreenSize();
	int screenHeight = screenSize.height;
	int screenWidth = screenSize.width;
	private static final long serialVersionUID = 1L;
	private static final int DEFAULTWIDTH = 800;
	private static final int DEFAULTHEIGHT = 400;
	//个人信息
	private JLabel nameLable;
	private JComboBox<Object> statusCombo;
	private UsersEntity user = null;
	JTextArea chatTextArea;//聊天信息接收区
	JTextField sendArea;//聊天信息发送区 
	JButton sendButton ;
	Thread  readerThread ;
	BufferedReader reader;
	PrintWriter writer;
	Socket sock;
	
	public ChatFrame(){//初始化客户端窗口界面
		setTitle("This is a simple ChatClient_V2.0");
		setSize(DEFAULTWIDTH, DEFAULTHEIGHT);
		setLocation((screenWidth - DEFAULTWIDTH) / 2, (screenHeight - DEFAULTHEIGHT) / 2);
		setJMenuBar(initMenuBar());//菜单栏初始化
		JPanel northPanel = initNorthPanel(); //个人信息显示区初始化
		JPanel mainPanel = initMainPanel();//聊天区初始化
		JPanel southPanel = initSouthPanel();//消息发送区初始化
		//添加组件到窗体
		getContentPane().add(BorderLayout.NORTH,northPanel );
		getContentPane().add(BorderLayout.CENTER,mainPanel);
		getContentPane().add(BorderLayout.SOUTH,southPanel);
		//判断用户是否登陆
		if(user == null){
			statusCombo.setEnabled(false);//禁用状态
			chatFrameLogin();
		}
	}

	/*
	 * 登陆
	 */
	private void chatFrameLogin(){
		if (user!= null) {
			JOptionPane.showMessageDialog(null, "Please logout first", "Login failed", JOptionPane.ERROR_MESSAGE);
		}else{
			LoginUI loginUI =new LoginUI();
			//登陆成功
			if(loginUI.showDialog(ChatFrame.this, "Login")){//传入父窗口，即本类，
				setUpNetworking();
				//开启线程持续地获取聊天信息
				readerThread  =  new Thread(new ChatTextReader());
				readerThread.start();
				user = loginUI.getUser();
				nameLable.setText(user.getName());
				statusCombo.setEnabled(true);//解禁状态
				sendButton.setEnabled(true);//解禁发送按钮
				user.setStatus(1);
				new UsersBL().setOnline(user.getID());
				statusCombo.setSelectedIndex(1);//设置为在线状态	
				
			}
		}
	}
	/*
	 * 用户注册
	 */
	private void chatFrameSignup(){
		SignupUI signupUI =new SignupUI();
		//登陆成功
		if(signupUI.showDialog(ChatFrame.this, "Sign Up")){//传入父窗口，即本类，
			 chatFrameLogin();
		}
	}
	/*
	 * 用户登出
	 */
	@SuppressWarnings("deprecation")
	private void ChatFrameLogout()  {
		if(user == null){
			JOptionPane.showMessageDialog(null, "Please login first", "Logout failed", JOptionPane.ERROR_MESSAGE);
		}else{
			nameLable.setText("UnLogin");
			statusCombo.setEnabled(false);
			sendButton.setEnabled(false); 
			tellServerOffline();
			user.setStatus(0);
			new UsersBL().setOffline(user.getID());
			user = null;
			readerThread.stop();
			try {
				sock.close();
				reader.close();
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	/*
	 * 关闭客户端
	 */
	private void ChatFrameExit() {
		if (user != null) {
			ChatFrameLogout();	
		}
		System.exit(0);
	}
	/*
	 * 连接服务器
	 */
	private void setUpNetworking() {
		try {
			sock = new Socket("127.0.0.1",50000);//服务器设为本机
			InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());//获取输入串流（到客户端）
			reader = new BufferedReader(streamReader);//Buffer链接输入串流
			writer = new PrintWriter(sock.getOutputStream());//获取输出串流（到服务器）
			System.out.println("Networking Established");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 线程任务：接收信息接收区的内容
	 */
	public class ChatTextReader implements Runnable {
		public void run(){
			String message;
			try {
				while((message = reader.readLine())!= null){
					System.out.println("read:"+message);
					chatTextArea.append(message+"\n");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
	}

	/*
	 * 发送消息给服务器说下线了
	 */
	 private void tellServerOffline() {
		 if(user.getStatus() == 1){
            String message = user.getName() + "离开了聊天室";
            writer.println(message);
            writer.flush();
		 }
    }
	 /*
	  * 
	  */
	 private void tellServerOnline() {
		 String message = "欢迎"+user.getName()+"进入聊天室！";
		 writer.println(message);
		 writer.flush();
	 }
	/*
	 * 菜单栏初始化 
	 */
	private JMenuBar initMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Menu");
		menuBar.add(menu);
		JMenuItem loginItem = new JMenuItem("Login");
		JMenuItem signUpItem = new JMenuItem("Sign Up");
		JMenuItem logoutItem = new JMenuItem("Logout");
		JMenuItem exitItem = new JMenuItem("Exit");
		menu.add(loginItem);
		menu.add(logoutItem);
		menu.add(signUpItem);
		menu.add(exitItem);
		loginItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chatFrameLogin();
			}
		});	 
		signUpItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chatFrameSignup();
			}
		});	 
		logoutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChatFrameLogout();
			}
		});	
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChatFrameExit();
			}
		});	
		return menuBar;
	}  
	/*
	 * 个人信息显示区初始化 
	 */
	private JPanel initNorthPanel() {
		JPanel northPanel = new JPanel();
		JLabel userLable = new JLabel("User：");
        nameLable = new JLabel("UnLogin");
    	JLabel statusLable = new JLabel("Status：");
        statusCombo = new JComboBox<>();
        statusCombo.addItem("Offline");
        statusCombo.addItem("Online");
        statusCombo.addItem("Hide");
      //状态改变事件
        statusCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int s = statusCombo.getSelectedIndex();
                if (s == 0) {
                	tellServerOffline();
                	user.setStatus(0);
                	new UsersBL().setOffline(user.getID());
                } else if (s == 1) {
                    new UsersBL().setOnline(user.getID());
                    tellServerOnline();
                    user.setStatus(1);
                } else {
                	tellServerOffline();
                	user.setStatus(2);
                    new UsersBL().setHide(user.getID());
                  
                }
            }
        });
        northPanel.add(userLable);
        northPanel.add(nameLable);
        northPanel.add(statusLable);
        northPanel.add(statusCombo);
        return northPanel;
	}
	/*
	 * 聊天区初始化 
	 */
	private JPanel initMainPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		chatTextArea = new JTextArea();
		chatTextArea.setLineWrap(true);//启动自动换行
		chatTextArea.setEditable(false);//不可编辑
		//更改为可垂直滚动区
		JScrollPane qScroller = new JScrollPane(chatTextArea);
		qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		mainPanel.add(qScroller,BorderLayout.CENTER);
		return mainPanel;
	}
	/*
	 * 消息发送区初始化 
	 */
	private JPanel initSouthPanel() {
		JPanel southPanel = new JPanel();
		sendArea = new JTextField(20);
		sendButton = new JButton("Send");
		sendButton.addActionListener(new ActionListener() {//给SEND按钮添加事件监听
			public void actionPerformed(ActionEvent ev){
				try {
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					String IDandTime = user.getName()+ "    " + dateFormat.format(new Date());
					writer.println(IDandTime);
					writer.println(sendArea.getText());//获取发送区的字段
					writer.flush();	//将输出流的所有数据（即使未满）发送到服务器情况
				} catch (Exception e) {
					e.printStackTrace();
				}
				sendArea.setText("");
				sendArea.requestFocus();//光标显示在编辑区中
			}
		});
		southPanel.add(sendArea);
		southPanel.add(sendButton);
		return southPanel;
	}
}
