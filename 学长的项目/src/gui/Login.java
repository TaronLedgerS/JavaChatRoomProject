package gui;

import bean.UsersEntity;
import dao.UserDao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

/**
 * 用户登陆
 * A password chooser that is shown inside a dialog
 */
public class Login extends JPanel {
    private static final int DEFAULT_WTDTH = 220;
    private static final int DEFAULT_HEIGHT = 150;

    private JTextField username;
    private JPasswordField password;
    private JButton okButton;
    private boolean ok;
    private JDialog dialog;
    private UsersEntity user;

    public Login() {


        setLayout(new BorderLayout());

        // construct a panel with user name and password fields
        //add(new Font("Welcome"));
        JLabel welcome = new JLabel("欢迎进入CHAT聊天室");
        welcome.setFont(new Font("欢迎登陆CHAT",Font.HANGING_BASELINE,20));
        //welcome.setBounds(40,10,100,200);
        add(welcome,BorderLayout.NORTH);
        JPanel panel = new JPanel();
        panel.setSize(200, 150);
        panel.setLayout(new GridLayout(2, 2));
        panel.add(new JLabel("User name:"));
        panel.add(username = new JTextField(""));
        //username.setBounds(0,0,5,5);
        panel.add(new JLabel("Password:"));
        panel.add(password = new JPasswordField(""));
        panel.setOpaque(false);
        add(panel, BorderLayout.CENTER);

        // create Ok and Cancel buttons that terminate the dialog

        okButton = new JButton("Ok");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String tname = username.getText();
                String tpass = new String(password.getPassword());

                try {
                    user = new UserDao().login(tname, tpass);
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(Login.this, "出现未知错误了，请重试！");
                    ok = false;
                }
                if (user != null) {
                    ok = true;
                    dialog.setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(Login.this, "用户名或密码错误，请重新输入！");
                    ok = false;
                    dialog.setVisible(true);
                }
            }
        });

        JButton registButton = new JButton("Regist");
        registButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                Regist regist = new Regist();
                regist.showDialog(Login.this, "Regist");
            }
        });

        // add buttons to southern border

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(registButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }


    public UsersEntity getUser() {
        return user;
    }

    /**
     * Show the chooser panel in a dialog
     *
     * @param parent a component in the owner frame or null
     * @param title  the dialog window title
     */
    public boolean showDialog(Component parent, String title) {
        ok = false;
        //获取屏幕长宽
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        // locate the owner frame

        Frame owner = null;
        if (parent instanceof Frame) owner = (Frame) parent;
        else owner = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);

        // if first time, or if owner has changed, make new dialog

        if (dialog == null || dialog.getOwner() != owner) {
            dialog = new JDialog(owner, true);
            dialog.add(this);
            dialog.getRootPane().setDefaultButton(okButton);
            //        设置大小
            dialog.setSize(DEFAULT_WTDTH, DEFAULT_HEIGHT);
            //        设置位置
            dialog.setLocation((screenWidth - DEFAULT_WTDTH) / 2, (screenHeight - DEFAULT_HEIGHT) / 2);
            //dialog.pack();
            dialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });

        }

        // set title and show dialog

        dialog.setTitle(title);
        dialog.setVisible(true);
        return ok;
    }
}

