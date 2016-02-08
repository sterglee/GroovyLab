package gExec.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JLabel;

public class gLabOperatingModes extends javax.swing.JPanel {
    
        private JLabel GroovySciLabel;
        private JLabel GroovySciDescrLabel;
        private JLabel javaSciLabel;
        private JLabel javaSciDescrLabel;
        private JLabel jScriptsLabel;
        private JLabel jScriptsDescriptionLabel;
        
        private JLabel cjScriptsLabel;
        private JLabel cjScriptsDescriptionLabel;
        
        private JLabel seperatorLabel0;
        private JLabel seperatorLabel1;
        private JLabel seperatorLabel2;
        private JLabel seperatorLabel3;
        private JLabel seperatorLabel4;
        
    public gLabOperatingModes() {
        setLayout(new GridLayout(13,1));
        initComponents();
    }
    
    private void initComponents() {
        
                seperatorLabel0 = new JLabel("------------------------------------------------------------------------------------------------------------");
                seperatorLabel0.setFont( new Font("Times New Roman", Font.BOLD, 28));
                seperatorLabel0.setForeground(Color.BLUE);
                
                seperatorLabel1 = new JLabel("------------------------------------------------------------------------------------------------------------");
                seperatorLabel1.setFont( new Font("Times New Roman", Font.BOLD, 28));
                seperatorLabel1.setForeground(Color.BLUE);
                
                seperatorLabel2 = new JLabel("------------------------------------------------------------------------------------------------------------");
                seperatorLabel2.setFont( new Font("Times New Roman", Font.BOLD, 28));
                seperatorLabel2.setForeground(Color.BLUE);
                
                seperatorLabel3 = new JLabel("------------------------------------------------------------------------------------------------------------");
                seperatorLabel3.setFont( new Font("Times New Roman", Font.BOLD, 28));
                seperatorLabel3.setForeground(Color.BLUE);
                
                seperatorLabel4 = new JLabel("------------------------------------------------------------------------------------------------------------");
                seperatorLabel4.setFont( new Font("Times New Roman", Font.BOLD, 28));
                seperatorLabel4.setForeground(Color.BLUE);
                
                                
		GroovySciLabel = new JLabel("1.     GroovySci  (the default) ");
                GroovySciLabel.setFont(new Font("Times New Roman", Font.BOLD, 28));
                GroovySciLabel.setForeground(Color.DARK_GRAY);
		
                GroovySciDescrLabel = new JLabel("is the compiled scripting engine, an extension of the Groovy language with Matlab-like constructs");
                GroovySciDescrLabel.setFont(new Font("Times New Roman", Font.BOLD, 22));
		GroovySciDescrLabel.setForeground(Color.DARK_GRAY);
                
                jScriptsLabel = new JLabel("2.    jScripts");
                jScriptsLabel.setFont(new Font("Times New Roman", Font.BOLD, 28));
                jScriptsLabel.setForeground(Color.GREEN);
		
                jScriptsDescriptionLabel = new JLabel("is interpreted and has Matlab-like syntax");
                jScriptsDescriptionLabel.setFont(new Font("Times New Roman", Font.BOLD, 22));
		jScriptsDescriptionLabel.setForeground(Color.GREEN);
                
                
                cjScriptsLabel = new JLabel("3.     Compiled jScripts");
                cjScriptsLabel.setFont(new Font("Times New Roman", Font.BOLD, 28));
                cjScriptsLabel.setForeground(Color.RED);
		
                cjScriptsDescriptionLabel = new JLabel("is compiled to GroovySci code but has the syntax of Matlab-like jScripts");
                cjScriptsDescriptionLabel.setFont(new Font("Times New Roman", Font.BOLD, 22));
		cjScriptsDescriptionLabel.setForeground(Color.RED);
                
                javaSciLabel = new JLabel("4.   JavaSci");
                javaSciLabel.setFont(new Font("Times New Roman",  Font.BOLD, 28));
                javaSciDescrLabel = new JLabel("JavaSci is the Java Scripting framework for full numerical speed");
                javaSciDescrLabel.setFont(new Font("Times New Roman", Font.BOLD, 18 ));
                javaSciLabel.setForeground(Color.BLACK);
                javaSciDescrLabel.setForeground(Color.BLACK);
                        
                
                add(seperatorLabel0);
                add(GroovySciLabel);
                add(GroovySciDescrLabel);
                add(seperatorLabel1);
                add(jScriptsLabel);
                add(jScriptsDescriptionLabel);
                add(seperatorLabel2);
                add(cjScriptsLabel);
                add(cjScriptsDescriptionLabel);
                add(seperatorLabel3);
                add(javaSciLabel);
                add(javaSciDescrLabel);
                add(seperatorLabel4);
                
    }

   

}
