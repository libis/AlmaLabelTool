package be.libis.utils;


import javax.swing.JOptionPane;
import javax.swing.JTextField;

import javax.swing.JPasswordField;
import javax.swing.JPanel;
import javax.swing.JLabel;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.Arrays;

public class PasswordDialog
{
    private String user=null;
    private String pw=null;
    private boolean cancelled=false;
    
    public PasswordDialog(String username, String password, Component parent)
    {
        user=username;
        pw=password;
                
        // Using a JPanel as the message for the JOptionPane
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new GridLayout(2, 2));

        JLabel usernameLbl = new JLabel("Username:");
        JLabel passwordLbl = new JLabel("Password:");
        JTextField userText = new JTextField();
        JPasswordField passwordFld = new JPasswordField();
        
        //set default or saved user/pw
        if (user!=null)
        {    
            userText.setText(user);
        }
        if (pw!=null)
        {
            passwordFld.setText(pw);
        }
        
        userPanel.add(usernameLbl);
        userPanel.add(userText);
        userPanel.add(passwordLbl);
        userPanel.add(passwordFld);

        // As the JOptionPane accepts an object as the message
        // it allows us to use any component we like - in this case
        // a JPanel containing the dialog components we want
        int input = JOptionPane.showConfirmDialog(parent, userPanel, "Enter your Alma password:",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (input == 0) // OK Button = 0
        {
            this.user=userText.getText();
            // Retrieve password
            char[] enteredPassword = passwordFld.getPassword();
            this.pw= String.valueOf(enteredPassword);
            Arrays.fill(enteredPassword, '0');
        }
        else
        {
            cancelled=true;
        }
    }
    
    public String getPW()
    {
        return pw;              
    }
    
    public String getUser()
    {
        return user;
    }
    
    public boolean getCancelled()
    {
        return cancelled;
    }

}