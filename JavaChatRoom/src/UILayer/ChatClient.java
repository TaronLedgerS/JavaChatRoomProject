package UILayer;


import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Date;
import java.text.SimpleDateFormat;

import javax.swing.*;



import java.awt.*;

public class ChatClient {
	
	JTextArea chatTextArea;//聊天信息文本区
	JTextField sendArea;//信息发送区 
	BufferedReader reader;
	PrintWriter writer;
	Socket sock;
	static int userID;
	
	public static void main(String[] args){
		ChatClient client = new ChatClient();
		userID = (int)(Math.random()*10000000);
		client.go();
	}
	
	public void go(){
		JFrame frame = new JFrame("This is a simple ChatClient_V1.0");
		JPanel mainpanel = new JPanel();
		//聊天区初始化
		chatTextArea = new JTextArea(15,50);//15行50个字
		chatTextArea.setLineWrap(true);//启动自动换行
		chatTextArea.setEditable(false);//不可编辑
			//更改为可垂直滚动区
		JScrollPane qScroller = new JScrollPane(chatTextArea);
		qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		//消息发送区初始化
		sendArea = new JTextField(20);
		JButton sendButton = new JButton("Send");
		sendButton.addActionListener(new SendButtonListener());
		
		mainpanel.add(qScroller);
		mainpanel.add(BorderLayout.SOUTH,sendArea);
		mainpanel.add(BorderLayout.SOUTH,sendButton);
		
		 setUpNetworking();
		
		//开启线程持续地获取聊天信息
		Thread  readerThread  =  new Thread(new ChatTextReader());
		readerThread.start();
		
		//显示界面
		frame.getContentPane().add(BorderLayout.CENTER,mainpanel);
		frame.setSize(800, 400);
		frame.setVisible(true);	
		
	}

	public class SendButtonListener implements ActionListener{
		//重载actionPerformed
		public void actionPerformed(ActionEvent ev){
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				String IDandTime = userID + "    " + dateFormat.format(new Date());
				writer.println(IDandTime);
				writer.println(sendArea.getText());//获取发送区的字段
				writer.flush();	//将输出流的所有数据（即使未满）发送到服务器情况
			} catch (Exception e) {
				e.printStackTrace();
			}
			sendArea.setText("");
			sendArea.requestFocus();//光标显示在编辑区中
		}
	}
	
	private void setUpNetworking() {//内部类
		try {
			sock = new Socket("127.0.0.1",50000);//服务器设为本机
			InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());//获取输入串流（到客户端）
			reader = new BufferedReader(streamReader);//Buffer链接输入串流
			writer = new PrintWriter(sock.getOutputStream());//获取输出串流（到服务器）
			System.out.println("networking established");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

		
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
	
	
}
