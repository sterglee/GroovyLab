
package SysUtils;

import gExec.Interpreter.GlobalValues;
import  java.awt.*;
import  java.awt.event.*;
import  javax.swing.*;
import static javax.swing.JScrollPane.*;
import  java.io.*;
import javax.swing.text.DefaultEditorKit;

 
public class ConsoleWindow {
    public static PrintStream consoleStream;
    
    JPopupMenu  consolePopup = new JPopupMenu();
    JMenuItem cutJMenuItem = new JMenuItem(new DefaultEditorKit.CutAction());
    JMenuItem copyJMenuItem = new JMenuItem(new DefaultEditorKit.CopyAction());
    JMenuItem pasteJMenuItem = new JMenuItem(new DefaultEditorKit.PasteAction());
    JMenuItem verboseJMenuItem = new JMenuItem(new verboseOutputAction());
    public JTextArea  output = new JTextArea();
    JMenuItem clearOutputItem = new JMenuItem(new clearOutputAction(output));
        
    int totalLength=0; 
         
    
        public ConsoleWindow()
	{
 
     GlobalValues.outputPane  =  new JScrollPane(output,  VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_ALWAYS  );

     consolePopup.setFont(GlobalValues.puifont);
     pasteJMenuItem.setFont(GlobalValues.puifont);
     consolePopup.add(pasteJMenuItem);
     copyJMenuItem.setFont(GlobalValues.puifont);
     consolePopup.add(copyJMenuItem);
     cutJMenuItem.setFont(GlobalValues.puifont);
     consolePopup.add(cutJMenuItem);
     clearOutputItem.setFont(GlobalValues.puifont);
    consolePopup.add(clearOutputItem);
     verboseJMenuItem.setFont(GlobalValues.puifont);
     consolePopup.add(verboseJMenuItem);

     output.add(consolePopup);
     output.setToolTipText("Displays Java's output sent at System.out stream and messages produced from Java's runtime exceptions");

      output.addMouseListener(new MouseAdapterForConsole());   // handles right mouse clicks

      
      int locX = 0; // loc.x;
      int locY = 0; //loc.y;

         output.setEditable(false);
        // output.setLineWrap( true );
         output.setBackground( Color.black );
         output.setForeground( Color.white );
    
         output.setToolTipText("Displays the program output redirecting the System.out.print(), System.err.print(), System.out.println() etc commands");
         
         
         // define a PrintStream that sends its bytes to the output text area
         consoleStream = new PrintStream( new OutputStream () {
            @Override
                public void write(int b)  {}   // never called
                public void write(  byte []  b, int off, int len )
                {
                    String outStr = new String(b, off, len);
                    append(outStr);
                }
            });
         
         // set both System.out and System.err to that stream
      System.setOut(consoleStream);
      System.setErr(consoleStream);
         
        
     }
   
        public  void  append(  String str  )  {
         output.append( str );
         totalLength += str.length();
         updateCaret();
      }

                  
             public void resetText( String str ) {
         
                 output.setText(str);
         if ( str == null ) 
             totalLength = 0;
         else
             totalLength = str.length();
         updateCaret();
             }
             

      public void  updateCaret()  {
         int pos = totalLength-1;
          int prevPos = pos;
          String txt = output.getText();
          while (txt.charAt(pos)!='\n' && pos > 0) {
              pos--;
          }
          int caretPos = Math.max( 0, pos+1);
          if (GlobalValues.gLabMainFrame !=null) {
           int xSize = GlobalValues.gLabMainFrame.getSize().width;
           Font consoleFont = GlobalValues.consoleOutputWindow.output.getFont();
           int pts = consoleFont.getSize();
               
           GlobalValues.consoleCharsPerLine =(int) (0.8*(xSize/pts));
          }           
          if (prevPos-pos > GlobalValues.consoleCharsPerLine)  {
              // caretPos = pos+1;
              output.append("\n");
              caretPos = output.getText().length();
                      
          }
          
          try {
            output.setCaretPosition( caretPos );  // totalLength - 1 ));
         }
         catch (Exception e) {
             e.printStackTrace();
         }
      }
      
   
      
   
            
   private class MouseAdapterForConsole  extends  MouseAdapter {
          public void mousePressed(MouseEvent e) {
              if (e.isPopupTrigger()){
                consolePopup.show((Component) e.getSource(), e.getX(), e.getY());
             }
           }

        public void mouseReleased(MouseEvent e) {
           if (e.isPopupTrigger()){
                 consolePopup.show((Component) e.getSource(), e.getX(), e.getY());
             }

          }

        }

     

}