package gui;

import javax.swing.*;
import java.awt.*;

/**
 * 注册和登陆对话
 * Created by wuhul on 2016/3/18.
 */
public class Dialog {
    private boolean ok;
    private JDialog dialog;
    private JPanel panel;
    private JButton okButton;
    private Component parent;
    private String title;

    public Dialog(boolean ok, JDialog dialog, JPanel panel, JButton okButton, Component parent, String title) {
        this.ok = ok;
        this.dialog = dialog;
        this.panel = panel;
        this.okButton = okButton;
        this.parent = parent;
        this.title = title;
    }

    public boolean showDialog() {
        ok = false;

        // locate the owner frame

        Frame owner = null;
        if (parent instanceof Frame) owner = (Frame) parent;
        else owner = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);

        // if first time, or if owner has changed, make new dialog

        if (dialog == null || dialog.getOwner() != owner) {
            dialog = new JDialog(owner, true);
            dialog.add(panel);
            dialog.getRootPane().setDefaultButton(okButton);
            dialog.pack();
        }

        // set title and show dialog

        dialog.setTitle(title);
        dialog.setVisible(true);
        return ok;
    }
}
