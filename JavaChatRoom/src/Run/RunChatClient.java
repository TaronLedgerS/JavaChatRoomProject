package Run;

import java.awt.EventQueue;
import javax.swing.*;
import UILayer.ChatFrame;

/*
 * 客户端执行入口
 * 2017-05-05 16:05:09
 */
public class RunChatClient {
	public static void main(String[] args){
		EventQueue.invokeLater(new Runnable() {//用于打开一个新的swing.fame的事件队列
			public void run() {
				ChatFrame chatClientFrame = new ChatFrame();
				chatClientFrame.setVisible(true);
				chatClientFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
		});
	}
}
