package UILayer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import BLLayer.UsersBL;
import BLLayer.UsersEntity;

/*
 * 注册界面
 * 2017-05-06 00:43:13
 */
public class SignupUI extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_WTDTH = 240;
    private static final int DEFAULT_HEIGHT = 150;

    private JTextField username;
    private JPasswordField password;
    private JButton okButton;
    private boolean ok;
    private JDialog dialog;

    public SignupUI() {
    	setLayout(new BorderLayout());
        // construct a panel with user name and password fields
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));
        panel.add(new JLabel("    User name:"));
        panel.add(username = new JTextField(""));
        panel.add(new JLabel("    Password:"));
        panel.add(password = new JPasswordField(""));
        JRadioButton sexM = new JRadioButton(" ♂",true);//0
        JRadioButton sexW = new JRadioButton(" ♀",false);//1
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
                if(sexW.isSelected()) tsex="F";
                else tsex="M";
                UsersEntity u = new UsersEntity(tname,tpass,tsex,0);
                int isSuccess =  new UsersBL().signUp(u);
                if (isSuccess == 2) {
                	ok = true;
                    JOptionPane.showMessageDialog(null, "Signup successfully！");
                    dialog.setVisible(false);
				}else
					if (isSuccess == 1) {
						 ok = false;
		                 JOptionPane.showMessageDialog(SignupUI.this,"Username has been used，please try again！","Signup Failed",JOptionPane.ERROR_MESSAGE);
		                 dialog.setVisible(true);
					}
					else {
						JOptionPane.showMessageDialog(null, "Unknown error!Please try again","Signup Failed" , JOptionPane.ERROR_MESSAGE);
						
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
            dialog.setSize(DEFAULT_WTDTH, DEFAULT_HEIGHT);
            dialog.setLocation((screenWidth - DEFAULT_WTDTH) / 2, (screenHeight - DEFAULT_HEIGHT) / 2);

        }
        dialog.setTitle(title);
        dialog.setVisible(true);
        return ok;
    }
	
}
