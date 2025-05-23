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
 * Very small form: e-mail + password + two buttons.
 * Uses anonymous inner classes (no lambdas) to stay beginner-friendly.
 */
public class RegisterPanel extends JPanel {

    private final RegisterController controller;

    private final JTextField     emailFld   = new JTextField();
    private final JPasswordField passFld    = new JPasswordField();
    private final JButton        registerBtn= new JButton("Register");
    private final JButton        loginBtn   = new JButton("Already registered? Sign-in");
    private final JLabel         msgLabel   = new JLabel(" ");

    public RegisterPanel(RegisterController c) {
        controller = c;
        build();
    }

    private void build() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(centered(new JLabel("Create your account")));
        add(Box.createRigidArea(new Dimension(0, 5)));

        add(new JLabel("E-mail:"));
        add(emailFld);
        add(Box.createRigidArea(new Dimension(0, 5)));

        add(new JLabel("Password:"));
        add(passFld);
        add(Box.createRigidArea(new Dimension(0, 5)));

        add(registerBtn);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(loginBtn);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(msgLabel);

        /* listeners */
        registerBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) { submit(); }
        });
        loginBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) { controller.goToLogin(); }
        });
    }

    private void submit() {
        String email = emailFld.getText().trim();
        String pass  = new String(passFld.getPassword());
        String msg   = controller.onRegister(email, pass);
        msgLabel.setText(msg);
    }

    private Component centered(Component c) {
        ((JComponent) c).setAlignmentX(Component.CENTER_ALIGNMENT);
        return c;
    }
}
