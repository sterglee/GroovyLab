package gExec.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JLabel;

public class AboutGroovyLab extends javax.swing.JPanel {
    
        private JLabel jLabLabel;
        private JLabel stergLabel;
        private JLabel imgLabel;
        private JLabel GroovySciLabel;
        private JLabel GroovySciDescrLabel;
        private JLabel seperatorLabel1;
        private JLabel seperatorLabel2;
        private JLabel seperatorLabel3;
        private JLabel seperatorLabel4;
        
    public AboutGroovyLab() {
        setLayout(new GridLayout(7,1));
        initComponents();
    }
    
    private void initComponents() {
        
                seperatorLabel1 = new JLabel("----------------------------------------------------------------------------------------------------");
                seperatorLabel1.setFont( new Font("Times New Roman", Font.BOLD, 24));
                seperatorLabel1.setForeground(Color.BLUE);
                
                seperatorLabel2 = new JLabel("----------------------------------------------------------------------------------------------------");
                seperatorLabel2.setFont( new Font("Times New Roman", Font.BOLD, 24));
                seperatorLabel2.setForeground(Color.GREEN);
                
                seperatorLabel3 = new JLabel("----------------------------------------------------------------------------------------------------");
                seperatorLabel3.setFont( new Font("Times New Roman", Font.BOLD, 24));
                seperatorLabel3.setForeground(Color.GREEN);
                
                
                jLabLabel = new JLabel("GroovyLab:    Scientific Scripting for the Java Platform with Groovy");
                jLabLabel.setFont(new Font("Times New Roman", Font.BOLD, 26));
                jLabLabel.setForeground(Color.RED);
		
                        
                stergLabel = new JLabel("Google  research project,  http://code.google.com/p/jlabgroovy/");
                stergLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
                stergLabel.setForeground(Color.BLUE);
                        
                imgLabel = new JLabel("Stergios Papadimitriou, Dept. of Informatics and Computer Engineering, Tech Education Institute of Kavalas,  Greece ");
                imgLabel.setFont(new Font("Times New Roman", Font.BOLD, 12));
                imgLabel.setForeground(Color.BLUE);
                
                GroovySciLabel = new JLabel("GroovySci ");
                GroovySciLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
                GroovySciLabel.setForeground(Color.DARK_GRAY);
		
                GroovySciDescrLabel = new JLabel("is the compiled scripting engine, adapted from the Groovy language ");
                GroovySciDescrLabel.setFont(new Font("Times New Roman", Font.BOLD, 16));
                GroovySciDescrLabel.setForeground(Color.DARK_GRAY);
                
                
                add(jLabLabel);
                add(seperatorLabel1);
                add(stergLabel);
                add(imgLabel);
                add(seperatorLabel3);
                add(GroovySciLabel);
                add(GroovySciDescrLabel);
                
                
    }

   

}
