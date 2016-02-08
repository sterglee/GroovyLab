package gExec.gLab;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.TreeSet;
import java.util.Iterator;
import javax.swing.text.JTextComponent;

public class WorkspaceKeyMouseHandler extends MouseAdapter implements KeyListener
{
	int caretPos = 0;
        int prevCaretPos = 0;
        JTextComponent inputTextComponent;
        
        public WorkspaceKeyMouseHandler()
	{
	}

        
         
    public void keyTyped(KeyEvent e){
       /* int  keyValue = e.getKeyChar();
       
        if (keyValue == KeyEvent.VK_F10);
                 display_help();      */
   }

	/**Interpret key presses*/
    public void keyPressed(KeyEvent e)
    {
        int keyValue = e.getKeyCode();
        inputTextComponent = (JTextComponent)e.getSource();
        prevCaretPos = caretPos;   
        
        switch (keyValue) {
                        
                      
            case   KeyEvent.VK_ENTER:
                break;
                
            case KeyEvent.VK_LEFT:
                break;
            
            case KeyEvent.VK_RIGHT:
                break;
                
            case KeyEvent.VK_UP:
                break;
                
            case KeyEvent.VK_DOWN:
                break;
            case KeyEvent.VK_F1:

                     break;
                     
            case KeyEvent.VK_ESCAPE:
                    break;
                
                    
            default:
                caretPos = inputTextComponent.getCaretPosition();
                
          }
    }
    
        public void mouseClicked(MouseEvent me)
        {
                JTextComponent inputTextComponent = ((JTextComponent)me.getSource());
                caretPos = inputTextComponent.getCaretPosition();
                String text = inputTextComponent.getText();
                int newLineCnt = 0;
                int idx = 0;
                while (idx<caretPos)   {
                    if (text.charAt(idx) == '\n') 
                       newLineCnt++;
                    idx++;
                }
                
          }
    
    
    
    public void keyReleased(KeyEvent e)
    {
    	        
    }	
}
