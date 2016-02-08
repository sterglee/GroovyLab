package gExec.gui;

import gExec.Interpreter.GlobalValues;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.TreeSet;
import java.util.Iterator;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class ConsoleKeyHandler implements KeyListener 
{
       private Console  inputConsole;   // keeps the inputCons Console object
       private String text;
       private int lastCommandStartIdx;
       private   boolean  multipleCommands = false;   // multiple commands at the same line
       private  char endCommandChar = ';';   // character denoting the end of the previous command
       private  int previousCommandStartIdx;
       private  int  eqIndex;  // index of equality  (in order to match the text after it
       private int posAutoCompletion;
       private String [] matches;
       private JList  resultsList;
       
	public ConsoleKeyHandler()
	{
	}

        
         
    public void keyTyped(KeyEvent e){
        
   }

	/**Interpret key presses*/
    public void keyPressed(KeyEvent e)
    {
  Object consObj = e.getSource();
  Console inputCons = ((Console)e.getSource());  // get the source Object of the event
  
  GlobalValues.userConsole = inputCons;
    	int keyValue = e.getKeyCode();
        
        switch (keyValue) {
                        
            case   KeyEvent.VK_UP:			//up cursor
			e.consume();
        // display previous command only when in line mode                        
                        ((gLabConsole)inputCons).prevCommand();
                        break;
                        
                         
            case  KeyEvent.VK_DOWN:		//down cursor
	    		e.consume();
        // display next command only when in line mode                        
                        ((gLabConsole)inputCons).nextCommand();
                        break;
                        
            case   KeyEvent.VK_LEFT:       //left cursor
			//check the cursor isn't moving off the current line
                        inputCons.checkPosition();
                        break;

            case KeyEvent.VK_BACK_SPACE:
            //check the cursor isn't moving off the current line
                        inputCons.checkPosition();
                        break;
            

            case    KeyEvent.VK_ENTER:  // at commandLineMode, each newline causes the execution of the current line
	gExec.Interpreter.GlobalValues.userConsole.textArea = "";  // reset the text from the current command
                     break; 

                        
            case KeyEvent.VK_TAB: 
                gExec.Interpreter.GlobalValues.autoCompletionWorkspace = new gExec.gui.AutoCompletionWorkspace();
                    inputConsole = (Console)e.getSource();
                    e.consume();
                  		//get the text on the current line
                    text = gExec.Interpreter.GlobalValues.userConsole.getText();
                                // get the starting point of the current command start                    
                    lastCommandStartIdx = text.lastIndexOf(GlobalValues.groovyLabPromptChar)+1;
                
                    multipleCommands = false;   // multiple commands at the same line
                    endCommandChar = ';';   // character denoting the end of the previous command
                    previousCommandStartIdx = text.lastIndexOf(endCommandChar)+1;
                    if (previousCommandStartIdx > lastCommandStartIdx)  {  // multiple commands per line separated by ';''
                        lastCommandStartIdx = previousCommandStartIdx;
                        multipleCommands = true;
                     }
                    eqIndex = text.lastIndexOf('=')+1;  // index of equality  (in order to match the text after it
                    posAutoCompletion = lastCommandStartIdx;
                    if (eqIndex > lastCommandStartIdx) 
                        posAutoCompletion = eqIndex;
                   
                     text = text.substring(posAutoCompletion, text.length());
                    String inputString = text; //= text.substring(startPos, text.length());
                    inputString = inputString.trim();

                     matches = null;
                     matches = gExec.Interpreter.GlobalValues.autoCompletionWorkspace.getMatched(inputString);
                        
                      resultsList = new JList(matches);
                         
                     resultsList.addListSelectionListener(new ListSelectionListener() {
                         public void valueChanged(ListSelectionEvent lse) {
                             
                             String  selValue = resultsList.getSelectedValue().toString();
                             
                             String currentText = inputConsole.getText();
                             int    posPreviousText  =  currentText.lastIndexOf(GlobalValues.groovyLabPromptChar) + 2;
                             int    posEndPreviousCommand = currentText.lastIndexOf(endCommandChar)+1;
                             if  (posEndPreviousCommand > posPreviousText)
                                 posPreviousText = posEndPreviousCommand;
                             int lastEqualsIdx = currentText.lastIndexOf('=')+1;
                             if (lastEqualsIdx > posPreviousText)
                                 posPreviousText = lastEqualsIdx;
                             inputConsole.setText(currentText.substring(0,posPreviousText)+" "+selValue);
                             GlobalValues.selectedStringForAutoCompletion = selValue;
                             inputConsole.setCaretPosition(inputConsole.getText().length());   	// set cursor at the end of the text area
	                 }
                     }
         );
            
          GlobalValues.autoCompletionFrame = new AutoCompletionFrame("jLabConsole AutoCompletion, Workspace variables");
          GlobalValues.autoCompletionFrame.displayMatches(resultsList);
          break;
                
            
            case  KeyEvent.VK_F5:  // update variable display
                    gExec.gui.WatchWorkspace.displayGroovySciBinding(gExec.gLab.gLab.variablesWorkSpacePanel);
                    GlobalValues.gLabMainFrame.updateHistoryWindow();
                    gExec.gLab.gLab.updateTree();
                    gExec.gLab.gLab.outerPane.revalidate();
                    GlobalValues.consoleOutputWindow.resetText( " ");
         
                
                break;
                        
                
            case KeyEvent.VK_F1:
            case KeyEvent.VK_F3:
                
                    inputConsole = (Console)e.getSource();
                    e.consume();
                  		//get the text on the current line
                     text = gExec.Interpreter.GlobalValues.userConsole.getText();
                                // get the starting point of the current command start                    
                    lastCommandStartIdx = text.lastIndexOf(GlobalValues.groovyLabPromptChar)+1;
                
                    multipleCommands = false;   // multiple commands at the same line
                    endCommandChar = ';';   // character denoting the end of the previous command
                    previousCommandStartIdx = text.lastIndexOf(endCommandChar)+1;
                    if (previousCommandStartIdx > lastCommandStartIdx)  {  // multiple commands per line separated by ';''
                        lastCommandStartIdx = previousCommandStartIdx;
                        multipleCommands = true;
                     }
                    eqIndex = text.lastIndexOf('=')+1;  // index of equality  (in order to match the text after it
                    posAutoCompletion = lastCommandStartIdx;
                    if (eqIndex > lastCommandStartIdx) 
                        posAutoCompletion = eqIndex;
                   
                     text = text.substring(posAutoCompletion, text.length());
                    inputString = text; //= text.substring(startPos, text.length());
                    inputString = inputString.trim();

                    matches = null;
                    if (keyValue==KeyEvent.VK_F1) {
                       matches = gExec.Interpreter.GlobalValues.AutoCompletionGroovySci.getMatched(inputString);
                    }
                    else {
                       matches = gExec.Interpreter.GlobalValues.AutoCompletionGroovySci.getMatchedRegEx(inputString);
                    }
                        
                      resultsList = new JList(matches);
                     autocompleteListHandler   detailHelpAdapter = new autocompleteListHandler();
                     resultsList.addKeyListener(detailHelpAdapter); 
                         
                     resultsList.addListSelectionListener(new ListSelectionListener() {
                         public void valueChanged(ListSelectionEvent lse) {
                             
                             String  selValue = resultsList.getSelectedValue().toString();
                            
                             GlobalValues.selectedStringForAutoCompletion = selValue;
                        
                                                }
                     }
         );
         
         String fullGroovySciAutoCompletion =   "Glab Console AutoCompletion,  F1  on the selected item for detailed help";
                    
            GlobalValues.autoCompletionFrame = new AutoCompletionFrame(fullGroovySciAutoCompletion);
                
            GlobalValues.autoCompletionFrame.displayMatches(resultsList);
            break;
                
            
            case KeyEvent.VK_ESCAPE:
                int i,j, lc;
            // Erase the current editing line
                e.consume();
                ((gLabConsole)inputCons).clearCommandLine();
                break;
                        
                                        
                
            default:
                break;
                
                    
        }
    }
    

   
        
    
    public void keyReleased(KeyEvent e)
    {
    	Console inputCons = ((Console)e.getSource());
    	int keyValue = e.getKeyCode();
        if(keyValue == KeyEvent.VK_ENTER) {  
                ((gLabConsole)inputCons).interpretLine();  
                gExec.Interpreter.GlobalValues.gLabMainFrame.jLabConsole.displayPrompt();
                }
                else if(keyValue == KeyEvent.VK_HOME)
	inputCons.home();
               else if(keyValue == KeyEvent.VK_UP || keyValue == KeyEvent.VK_DOWN)
	inputCons.end();
                        
                
    }	
    
        
 class autocompleteListHandler extends KeyAdapter {
        public void keyPressed(KeyEvent ktev) {
            int  keyCode = ktev.getKeyCode();
            if (keyCode == KeyEvent.VK_F1) {
                display_detailed_help(GlobalValues.selectedStringForAutoCompletion);
            }
            if (keyCode == KeyEvent.VK_SPACE) {
                ktev.consume();
                GlobalValues.autoCompletionFrame.dispose();
            }
            
        }
}

 void display_help() {
        JFrame helpFrame = new AutoCompletionFrame("Glab  help");
        helpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        helpFrame.setSize(400, 400);
        Container container = helpFrame.getContentPane();
        JTextArea helpText = new JTextArea();

        int classCnt = 0;
        JScrollPane  helpPane = new JScrollPane(helpText);
        
        container.add(helpPane);
        helpFrame.setVisible(true);  
      }

 
    
    // displays detailed help for the selected item
    public static void display_detailed_help(String selectedItem) {
GlobalValues.detailHelpStringSelected = selectedItem;
DetailHelpFrame detailFrame = new DetailHelpFrame();
detailFrame.setVisible(true);
        
      }
    


}
