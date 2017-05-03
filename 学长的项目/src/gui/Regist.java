package gui;

import bean.UsersEntity;
import dao.UserDao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

/**
 * 用户注册
 * A password chooser that is shown inside a dialog
 */
public class Regist extends JPanel {
    private static final int DEFAULT_WTDTH = 200;
    private static final int DEFAULT_HEIGHT = 250;

    private JTextField username;
    private JPasswordField password;
    private ButtonGroup sexchose;
    private JButton okButton;
    private boolean ok;
    private JDialog dialog;

    public Regist() {

        setLayout(new BorderLayout());

        // construct a panel with user name and password fields

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2));
        panel.add(new JLabel("User name:"));
        panel.add(username = new JTextField(""));
        panel.add(new JLabel("Password:"));
        panel.add(password = new JPasswordField(""));

        //panel.add(new JLabel("Sex:"));

        JRadioButton sexM = new JRadioButton("♂",true);
        JRadioButton sexW = new JRadioButton("♀",false);
//        sexchose.add(sexM);
//        sexchose.add(sexW);
        panel.add(sexM);
        panel.add(sexW);

        add(panel, BorderLayout.CENTER);

        // create Ok and Cancel buttons that terminate the dialog

        okButton = new JButton("Ok");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String tname = username.getText();
                String tpass = new String(password.getPassword());
                String tsex=null;
                if(sexW.isSelected()) tsex="W";
                else tsex="M";

                UsersEntity u = new UsersEntity(tname,tpass,tsex,0);
                Boolean isSuccess = false;
                try {
                    isSuccess = new UserDao().regist(u);
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(Regist.this,"出现未知错误了，请重试！");
                }
                if(isSuccess) {
                    ok = true;
                    JOptionPane.showMessageDialog(Regist.this, "注册成功！");
                    dialog.setVisible(false);
                }else{
                    ok = false;
                    JOptionPane.showMessageDialog(Regist.this,"注册失败，此用户名被别人占用啦，请重试！");
                    dialog.setVisible(true);
                }
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                dialog.setVisible(false);
            }
        });

        // add buttons to southern border

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
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
        }

        // set title and show dialog

        dialog.setTitle(title);
        dialog.setVisible(true);
        return ok;
    }
}
