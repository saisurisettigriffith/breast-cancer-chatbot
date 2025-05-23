package com.example.breastcancer;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Sign-in form mirroring RegisterPanel.
 */
public class LoginPanel extends JPanel {

    private final LoginController controller;

    private final JTextField     emailFld  = new JTextField();
    private final JPasswordField passFld   = new JPasswordField();
    private final JButton        loginBtn  = new JButton("Login");
    private final JButton        registerBtn = new JButton("Need an account? Register");
    private final JLabel         msgLabel  = new JLabel(" ");

    public LoginPanel(LoginController c) {
        controller = c;
        build();
    }

    private void build() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(centered(new JLabel("Sign-in")));
        add(Box.createRigidArea(new Dimension(0, 5)));

        add(new JLabel("E-mail:"));
        add(emailFld);
        add(Box.createRigidArea(new Dimension(0, 5)));

        add(new JLabel("Password:"));
        add(passFld);
        add(Box.createRigidArea(new Dimension(0, 5)));

        add(loginBtn);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(registerBtn);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(msgLabel);

        /* listeners */
        loginBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) { submit(); }
        });
        registerBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) { controller.goToRegister(); }
        });
    }

    private void submit() {
        String email = emailFld.getText().trim();
        String pass  = new String(passFld.getPassword());
        String err   = controller.onLogin(email, pass);
        if (err != null) {
            msgLabel.setText(err);
        }
    }

    private Component centered(Component c) {
        ((JComponent) c).setAlignmentX(Component.CENTER_ALIGNMENT);
        return c;
    }
}
