
package gExec.gui;

import gExec.Interpreter.GlobalValues;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;


public class ToolboxesToolbar  extends JPanel {
        JButton [] toolboxesButtons;
                
    public ToolboxesToolbar() {
        JFrame frame = new JFrame("GroovyLab's  loaded Toolboxes ");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocation(100, 200);
        
        int toolboxesCnt = GlobalValues.currentToolboxId;  // count of loaded toolboxes
        toolboxesButtons = new JButton[toolboxesCnt];
        for (int k=0; k<toolboxesCnt;k++)  {
            String toolboxName = GlobalValues.loadedToolboxesNames[k];
            toolboxesButtons[k] = new JButton(toolboxName);
            toolboxesButtons[k].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
 //JFrame           
             }
          });
        }
      
        
        Container box = Box.createHorizontalBox();
        for (int k=0; k<toolboxesCnt;k++)  
            box.add(toolboxesButtons[k]);
                
        frame.getContentPane().add(box, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    
   }
 }
