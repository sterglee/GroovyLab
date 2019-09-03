
package gExec.gui;

import gExec.Interpreter.GlobalValues;
import gExec.gLab.clearHistoryAction;
import gExec.gLab.loadHistoryAction;
import gExec.gLab.saveHistoryAction;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.Vector;
import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import java.io.*;
import gExec.gLab.StreamGobbler;

public class gLabConsole  extends Console {
     JPopupMenu  consolePopup = new JPopupMenu();
      JMenuItem helpItem = new JMenuItem(new ConsoleHelpAction());
      JMenuItem cutJMenuItem = new JMenuItem(new DefaultEditorKit.CutAction());
      JMenuItem copyJMenuItem = new JMenuItem(new DefaultEditorKit.CopyAction());
      JMenuItem pasteJMenuItem = new JMenuItem(new DefaultEditorKit.PasteAction());
      JMenuItem clearHistoryItem = new JMenuItem(new clearHistoryAction());
      JMenuItem loadHistoryItem = new  JMenuItem(new loadHistoryAction());
      JMenuItem saveHistorItem = new JMenuItem(new saveHistoryAction());
      JMenuItem inspectItem = new JMenuItem("Inspect Selected Variable");
      JMenuItem adjustFontItem = new JMenuItem("Adjust Console's font");
      JMenuItem clearConsoleItem = new JMenuItem("Clear Console"); 
      JMenuItem clearOutputPanelItem = new JMenuItem("Clear Output Panel (F5)"); 
      JMenuItem jeditJMenuItem = new JMenuItem(new jeditAction());
      public static int fontSize=10;
          

      
      private class jeditAction extends AbstractAction {
       public jeditAction()  { super("jEdit Editor ");}
       public void actionPerformed(ActionEvent e) {
               String [] command  = new String[6];
               command[0] =  "java";
// classpath to the jedit seems that is not used, but in any case it  is not harmful
               String  jeditClassPath =  "-classpath";
               jeditClassPath +=  " "+GlobalValues.jarFilePath+File.pathSeparator;
                       
               command[1] = "-cp";
               command[2] = jeditClassPath;
               
               command[3] = "-jar";
               String jeditPath = GlobalValues.gLabLibPath+ "4.3.2"+File.separator+"jedit.jar";
               command[4] =   jeditPath;
               
           
               
               String fileName = "Untitled.groovy";
               command[5] = fileName;
               
            String jEditcommandString = command[0]+"  "+command[1]+"  "+command[2]+" "+command[3];
            System.out.println("jEditCommandString = "+jEditcommandString); 
            try {
                Runtime rt = Runtime.getRuntime();
                Process javaProcess = rt.exec(command);
                // an error message?
                StreamGobbler errorGobbler = new StreamGobbler(javaProcess.getErrorStream(), "ERROR");

                // any output?
                StreamGobbler outputGobbler = new StreamGobbler(javaProcess.getInputStream(), "OUTPUT");

                // kick them off
                errorGobbler.start();
                outputGobbler.start();

                } catch (IOException exio) {
                    System.out.println("IOException trying to executing "+command);
                    exio.printStackTrace();

                }
               
           }
                     
                    }
                
   

      private class ConsoleHelpAction extends AbstractAction {
               ConsoleHelpAction() {
                   super("Help");
               }
         public void actionPerformed(ActionEvent e) {
          Font  df = new Font(GlobalValues.uiFontName, Font.BOLD, Integer.parseInt(GlobalValues.uiFontSize));
         
          JFrame consHelpFrame = new JFrame("Basic Help");
          JPanel consHelpPanel = new JPanel(new BorderLayout());
          JTextArea consHelpTextArea = new JTextArea();
          consHelpFrame.setLocation(xloc, yloc);
          consHelpFrame.add(consHelpPanel);
          consHelpFrame.setVisible(true);
          Graphics2D gc = (Graphics2D) consHelpPanel.getGraphics();
         FontRenderContext context = gc.getFontRenderContext();
           Rectangle2D bounds = df.getStringBounds(" ", context);
          int mwidth =(int) bounds.getWidth()*150;
          int mheight =(int) bounds.getHeight()*20;
         
           consHelpFrame.setSize(mwidth, mheight);
         
          consHelpTextArea.setFont(df);
          consHelpTextArea.setText(groovySciCommands.BasicCommands.commands);
          JScrollPane spHelpText = new JScrollPane(consHelpTextArea);
          consHelpPanel.add(spHelpText, BorderLayout.CENTER);
          }

        
          
      }
      
      private    class ConsoleFontAdjusterAction extends AbstractAction  {
        public void actionPerformed(ActionEvent e) {
 JFontChooser  myFontChooser = new JFontChooser(GlobalValues.gLabMainFrame);
 myFontChooser.setVisible(true);
 Font choosingFont = myFontChooser.getFont();
        
 Color fontColorChoosed =  myFontChooser.getForegroundColor();
 int rgbAlpha = fontColorChoosed.getAlpha(), rgbRed = fontColorChoosed.getRed(), rgbBlue = fontColorChoosed.getBlue(), rgbGreen = fontColorChoosed.getGreen();
 GlobalValues.gLabMainFrame.jLabConsole.setFont(choosingFont);
 GlobalValues.gLabMainFrame.jLabConsole.fontSize = choosingFont.getSize();
 
  GlobalValues.gLabMainFrame.jLabConsole.setForeground(myFontChooser.getForegroundColor());
 GlobalValues.settings.setProperty("alphaProp",  Integer.toString(rgbAlpha));
 GlobalValues.settings.setProperty("redProp",  Integer.toString(rgbRed));
 GlobalValues.settings.setProperty("greenProp",  Integer.toString(rgbGreen));
 GlobalValues.settings.setProperty("blueProp",  Integer.toString(rgbBlue));

 int  isBold = 0;   if (choosingFont.isBold()) isBold = 1;
 int  isItalic = 0;   if (choosingFont.isItalic()) isItalic = 1;
 GlobalValues.settings.setProperty("isBoldProp", Integer.toString(isBold));
 GlobalValues.settings.setProperty("isItalicProp", Integer.toString(isItalic));
         }
       }

     
   

        public gLabConsole()
	{
         setToolTipText("Console style work is convenient for small scripts. For large scripts the GroovyLab editor is more convenient");
         
         jeditJMenuItem.setMnemonic('J');
         jeditJMenuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl J"));
            
            consolePopup.add(pasteJMenuItem);
            consolePopup.add(copyJMenuItem);
            consolePopup.add(cutJMenuItem);
            consolePopup.add(helpItem);
            consolePopup.add(jeditJMenuItem);
            consolePopup.add(clearHistoryItem);
            consolePopup.add(saveHistorItem);
            consolePopup.add(loadHistoryItem);
            consolePopup.add(adjustFontItem);
            consolePopup.add(clearConsoleItem);
            consolePopup.add(clearOutputPanelItem);
          
            
      adjustFontItem.addActionListener(new  ConsoleFontAdjusterAction());
            
 clearOutputPanelItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
          GlobalValues.consoleOutputWindow.resetText( " ");
         
            }
        });
         
  clearConsoleItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
          groovySciCommands.BasicCommands.cls();
          displayPrompt();
            }
        });

            inspectItem.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          JTextArea currentTextArea = gExec.Interpreter.GlobalValues.jLabConsole;
          String selectedText = currentTextArea.getSelectedText();
          selectedText = selectedText.trim();
          if (selectedText.isEmpty()==false)  {
            Object selectedObject = gExec.Interpreter.GlobalValues.groovyBinding.getVariable(selectedText);
            if (selectedObject != null)
                groovy.inspect.swingui.ObjectBrowser.inspect(selectedObject);
          }
        }
    });
      
            consolePopup.add(inspectItem);
            this.add(consolePopup);
            this.addMouseListener(new MouseAdapterForConsole());   // handles right mouse clicks
            
            GlobalValues.jLabConsole = this;
            this.setBackground(Color.WHITE);
            commandNo = 0;
            previousCommands = new Vector(10, 10);
            lineStart = getText().length() + 1;
		
            keyHandler = new ConsoleKeyHandler();
		
            addKeyListener(keyHandler);
            
            setFont(new Font(GlobalValues.gLabConsoleFontName, Font.PLAIN,  Integer.parseInt(GlobalValues.gLabConsoleFontSize)));
            
        }	
        
             /**display the previous command in the list*/
	public void prevCommand()
	{
		commandNo--;
    	
    	String text = "";
    	if(commandNo >= 0 && commandNo < previousCommands.size())
    	{
    		text = ((String)previousCommands.elementAt(commandNo)).trim();
    	}
    	else if(commandNo < 0)
    	{
    		text = "";
    		commandNo = -1;
    	}

    	// replace current command with previous command
    	textArea = getText();
        int    pos1     = textArea.lastIndexOf(GlobalValues.groovyLabPromptString) + 2;
    	String prev = textArea.substring(0, pos1);
        
    	setText(textArea.substring(0, pos1)+text);
    	
// set cursor at the end of the text area
	setCaretPosition(getText().length());
	}	
	
	/**display the next command in the list*/
	public void nextCommand()
	{
	commandNo++;

    	String text = "";
    	if(commandNo >= 0 && commandNo < previousCommands.size())
    	{
    		text = ((String)previousCommands.elementAt(commandNo)).trim();
    	}
    	else if(commandNo >= previousCommands.size())
    	{
    		text = "";
    		commandNo = previousCommands.size();
    	}		    	

    	// replace current command with next command
    	textArea = getText();
    	int    pos1     = textArea.lastIndexOf(GlobalValues.groovyLabPromptString) + 2;
    	setText(textArea.substring(0,pos1)+text);
    	
    	// set cursor at the end of the text area
		setCaretPosition(getText().length());
	}

        
	/** clears the current command line */
	public void clearCommandLine()
	{
	
    	String text = "";
    	
    	// replace current command with next command
    	textArea = getText();
    	int    pos1     = textArea.lastIndexOf(GlobalValues.groovyLabPromptString) + 2;
    	setText(textArea.substring(0,pos1)+text);
    	
    	// set cursor at the end of the text area
	setCaretPosition(getText().length());
	}

        
	/**Display the command prompt*/
	public  void displayPrompt()
	{
        if (GlobalValues.displayDirectory==true)     
          append("\n"+GlobalValues.workingDir+GlobalValues.groovyLabPromptString);
        else 
          append("\n"+GlobalValues.groovyLabPromptString);
        String currentText = getText();
        lineStart = currentText.length();
        setCaretPosition(lineStart);
	}
	
   
           private class MouseAdapterForConsole  extends  MouseAdapter {
        @Override
          public void mousePressed(MouseEvent e) {   
              GlobalValues.userConsole =  (Console) e.getSource();
              xloc = e.getX();
              yloc = e.getY();
              if (e.isPopupTrigger()){  
                consolePopup.show((Component) e.getSource(), e.getX(), e.getY());
             }
           }
    
        @Override
        public void mouseReleased(MouseEvent e) { 
           if (e.isPopupTrigger()){
                 consolePopup.show((Component) e.getSource(), e.getX(), e.getY());
             }       
             
          }
       }

           	/**Interpret the current line*/
	public void interpretLine()
	{
		//get the text on the current line
        String text = getText();
        String inputString = text.substring(text.lastIndexOf(GlobalValues.groovyLabPromptString) + 2, text.length());
        inputString = inputString.trim();
        
   		/* exit application */
        if (inputString.equals("quit") || inputString.equals("exit"))
		{
			GlobalValues.gLabMainFrame.closeGUI();   // call the main close() routine
		}

        if (!inputString.equals(""))
        {
                if (inputString.trim().length()>0)
                    previousCommands.addElement(inputString);
                
                 
        	GlobalValues.gLabMainFrame.interpretLine(inputString);
                
	        commandNo = previousCommands.size();
           }
	 
	}
}
