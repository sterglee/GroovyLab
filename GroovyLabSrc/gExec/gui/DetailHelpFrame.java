
package gExec.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import gExec.Interpreter.GlobalValues;
import utils.ReflectionUtils;

public class DetailHelpFrame extends JFrame    {
          JPanel  helpPanel;
          JTextArea helpText;
          JScrollPane  helpPane;
          String selectedItem;
          Point autoCompletionFrameLoc;
          
 public DetailHelpFrame() {          
     helpPanel = new JPanel();
     helpText =  new JTextArea(20, 30);
     helpPane =  new JScrollPane(helpText);
     helpPanel.setLayout(new BorderLayout());
     helpPanel.add(helpPane);
     
     setLayout(new BorderLayout());         
     
     setSize(GlobalValues.sizeX,   GlobalValues.sizeY);
    autoCompletionFrameLoc = GlobalValues.autoCompletionFrame.getLocation();
    setLocation(autoCompletionFrameLoc.x, autoCompletionFrameLoc.y+GlobalValues.autoCompletionFrame.getSize().height);
    helpText.setFont(new Font("Times New Roman", Font.BOLD, 16));
    helpText.setLineWrap(true);
         
    String fullItem = selectedItem = GlobalValues.detailHelpStringSelected;
    setTitle("Detailed help on "+selectedItem);
    int selIdx = fullItem.indexOf(GlobalValues.smallNameFullPackageSeparator);
    int seperatorLen = GlobalValues.smallNameFullPackageSeparator.length();
    String smallName = null;
    if ( selIdx != -1 )  { // extract the full Java name in order to provide help with reflection
            selectedItem = fullItem.substring(selIdx+seperatorLen, selectedItem.length());
            selectedItem = selectedItem.trim();
            smallName = (fullItem.substring(0, selIdx)).trim();
  }
    
    String helpString="";
    
    helpString = (String) AutoCompletionGroovySci.autoCompletionDetailsGroovySci.get(selectedItem);
         
    if (helpString==null) helpString="";
    
        try {
         Class selectedClass = Class.forName(selectedItem);
         helpString += ReflectionUtils.getConstructors(selectedClass);
         helpString += ReflectionUtils.getMethods(selectedClass);
         helpString += ReflectionUtils.getFields(selectedClass);
      }
        catch (ClassNotFoundException ex)  {
         }
    
    helpText.append(helpString);  
    
    if (smallName != null) {
      int idxStart = helpString.indexOf(smallName);
      helpText.setSelectionStart(idxStart);
      helpText.setSelectionEnd(idxStart+smallName.length());
    }
    
     helpText.setSelectedTextColor(Color.RED);
     helpPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
     helpPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
          
     add(helpPanel);
     setVisible(true);
     
     addComponentListener(new ComponentListener() {
         public void componentHidden(ComponentEvent e) {
         }
         public void componentMoved(ComponentEvent e) {
         }
         public void componentResized(ComponentEvent e) {
        repaint(); 
    }
         public void componentShown(ComponentEvent e) {
         }
     });
     
     
        }             
 

}
        
    
        

    
  
    

