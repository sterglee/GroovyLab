
package gExec.gui;

import gExec.Interpreter.GlobalValues;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class AutoCompletionFrame extends JFrame   {
    
    
    /** Creates a new instance of AutoCompletionFrame */
  public AutoCompletionFrame(String title) {
        super(title);
    }
    
  public  void displayMatches( JList resultsList )
     {
        JPanel  helpPanel = new JPanel();
        JScrollPane  listScroll = new JScrollPane(resultsList);
  
       
        helpPanel.setLayout(new BorderLayout());             
        helpPanel.add(listScroll);
        add(listScroll);
        
        setSize((int)(GlobalValues.sizeX/1.5), (int) (GlobalValues.sizeY/1.5));
        setLocation(100, 100);
        setVisible(true);
    }
    
    
    public void windowActivated(WindowEvent e) {
   // JOptionPane.showMessageDialog(null, "ok");
    }

    
}
