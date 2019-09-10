package gLabEdit;

import gExec.Interpreter.GlobalValues;
import gExec.gLab.GlobalAutoCompletion;
import gExec.gui.AutoCompletionFrame;
import gExec.gui.DetailHelpFrame;
import java.awt.Container;
import java.awt.Point;

import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import jdk.jshell.Snippet;
import jdk.jshell.SnippetEvent;
import jdk.jshell.SourceCodeAnalysis;
import jdk.jshell.VarSnippet;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

public class EditorKeyMouseHandler extends MouseAdapter implements KeyListener
{
    IntStream x;
        int caretPos = 0;      // track the cursor position 
        int prevCaretPos = 0;
        int  textLen = 0;  // the text lenngth
        int fromLoc = 0;
        int toLoc = 0;
       
        public RSyntaxTextArea  editorPane=null;    // the component that keeps and handles the editing text
        public RSyntaxDocument  docVar=null; 
        public RSyntaxDocument syntaxDocument=null;
        
public EditorKeyMouseHandler()
	{
	}


// update fields denoting the document in the editor, necessary when a new document is edited
  public  RSyntaxDocument updateDocument()  {
         
          docVar = (RSyntaxDocument) editorPane.getDocument();
          syntaxDocument = docVar;
          
          return syntaxDocument;
  }
               
     
  
   public  String  getCurrentLine() {
       if (docVar==null)
           updateDocument();
           
       RSyntaxDocument  myDoc = syntaxDocument;
       
       int caretpos = editorPane.getCaretPosition();
       int startpos = editorPane.getCaretOffsetFromLineStart();
       int scanpos = caretpos-startpos;
       String s = "";
       try {
            char ch = myDoc.charAt(scanpos);
       while (ch!='\n') {
                s += myDoc.charAt(scanpos);
            
           scanpos += 1;
           ch = myDoc.charAt(scanpos);
       }
       } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
       
       return s;
   }
       
     
   public  String   getSelectedTextOrCurrentLine() {
       String selectedTextOrCurrentLine = editorPane.getSelectedText();
       if (selectedTextOrCurrentLine==null)
           selectedTextOrCurrentLine = getCurrentLine();
       
       return selectedTextOrCurrentLine;
   }
     

        
         
    public void keyTyped(KeyEvent e){
       /* int  keyValue = e.getKeyChar();
       
        if (keyValue == KeyEvent.VK_F10);
                 display_help();      */
   }

	/**Interpret key presses*/
    public void keyPressed(final KeyEvent e)
    {
        gLabEditor.documentEditsPendable = true;
        int keyValue = e.getKeyCode();
        editorPane  = (RSyntaxTextArea)e.getSource();  // editor's keystrokes have as source the inputTextComponent JTextComponent
        prevCaretPos = caretPos;   
        
        switch (keyValue) {
                        

            case   KeyEvent.VK_ENTER:
                caretPos = editorPane.getCaretPosition();
                String text = editorPane.getText();
                int newLineCnt = 0;
                int idx = 0;
                while (idx<caretPos)   {
                    if (text.charAt(idx) == '\n') 
                       newLineCnt++;
                    idx++;
                    
                }
                break;

                
          case KeyEvent.VK_TAB:
                text = editorPane.getSelectedText();
     
                text = text.trim();
          if (text.isEmpty()==false)  {
            Object selectedObject = gExec.Interpreter.GlobalValues.groovyBinding.getVariable(text);
            if (selectedObject != null)
                groovy.inspect.swingui.ObjectBrowser.inspect(selectedObject);
          }

                e.consume();
                break;

           case KeyEvent.VK_SPACE:
                if (GlobalValues.rsyntaxInGroovyCompletionMode  == true)
                {
            
              if (e.isControlDown()) {
                new RSyntaxGroovyCompletionAction().performCompletion();
                  }
                }
              break;

                        
            case KeyEvent.VK_F6:
  
                if (e.isShiftDown() == true) {   // run with a seperate thread,  Swing is not thread-safe, thus this is not safe!!

                     
   try {
                    final String currentText = getSelectedTextOrCurrentLine();

                    // execute GroovySci script in a seperate thread
     Runnable newThreadRunnable  = new Runnable() {

            public void run() {
         
     String result = gExec.Interpreter.Interpreter.execWithGroovyShell(currentText);
     if (result != null)  {
        GlobalValues.consoleOutputWindow.output.append(result+"\n\n");
        GlobalValues.consoleOutputWindow.output.setCaretPosition(GlobalValues.consoleOutputWindow.output.getText().length());
         //gExec.gui.WatchWorkspace.displayGroovySciBinding(gExec.gLab.gLab.variablesWorkSpacePanel);
      }
 
            }
        };  //  newThreadRunnable
     
     Thread execThread  = new Thread(newThreadRunnable);
     execThread.start();
     
     // add the future to the recent future list
     if (execThread.getState() == Thread.State.RUNNABLE)
       gExec.Interpreter.GlobalValues.pendingThreads.addThread(execThread);

     //execFuture.get();
     //Thread execThread = new Thread(newThreadRunnable);
     //GlobalValues.currentThread = execThread;
     //execThread.start();
   }

            catch (Exception ex) {
                ex.printStackTrace();
            }

      e.consume();
   }  //// isShiftDown()
  else    {    //  if Shift is down, F6 runs safely at the context of the Event  Dispatch Thread

                                
          //      gLabEdit.gLabEditor.progressComputationLabel.setVisible(true);
                  SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
     String currentText = getSelectedTextOrCurrentLine();
     
     String result = gExec.Interpreter.Interpreter.execWithGroovyShell(currentText);
     if (result != null)  {
        GlobalValues.consoleOutputWindow.output.append(result+"\n\n");
        GlobalValues.consoleOutputWindow.output.setCaretPosition(GlobalValues.consoleOutputWindow.output.getText().length());
         //gExec.gui.WatchWorkspace.displayGroovySciBinding(gExec.gLab.gLab.variablesWorkSpacePanel);
     }
 
            gLabEdit.gLabEditor.progressComputationLabel.setVisible(false);
     
             }
                  }
       );
  }
                  
     e.consume();
    
    break;
    
            case KeyEvent.VK_F8: 
                String currentText = getSelectedTextOrCurrentLine();
        
     if (e.isControlDown()) { // buffer the selected code
         GlobalValues.bufferingCode += "\n"+ currentText;
     }
     else
            GlobalValues.bufferingImports += "\n"+currentText;
      
      e.consume();
      break;
                
                // use the Java evaluator  to evaluate then expression
            case KeyEvent.VK_F7:
                
             currentText = getSelectedTextOrCurrentLine();
         
           if (currentText != null) {
       SourceCodeAnalysis sca = gExec.Interpreter.GlobalValues.jshell.sourceCodeAnalysis();
       
       List<String> snippets = new ArrayList<>();
       do {
           SourceCodeAnalysis.CompletionInfo info = sca.analyzeCompletion(currentText);
           snippets.add(info.source());
           currentText = info.remaining();
       } while (currentText.length() > 0);
       
        List<SnippetEvent> grResultSnippets = snippets.stream().map(gExec.Interpreter.GlobalValues.jshell::eval).flatMap(List::stream).collect(Collectors.toList());       
         if (grResultSnippets != null) {
            String rmSuccess = grResultSnippets.toString().replace("Success", "");    
   
            for (SnippetEvent snippetEvent: grResultSnippets) {
                Snippet currentSnippet = snippetEvent.snippet();
                
                System.out.println("value = "+snippetEvent.value());
            }
                     
       // GlobalValues.consoleOutputWindow.output.append(rmSuccess);
        GlobalValues.consoleOutputWindow.output.setCaretPosition(GlobalValues.consoleOutputWindow.output.getText().length());
        
         GlobalValues.jshell.variables().forEach(v->{{
             String vn = v.name();
              
             VarSnippet varX = gExec.Interpreter.GlobalValues.jshell.variables().
                     filter(x->vn.equals(x.name())).findFirst().get() ;
             String typeOfVar = varX.typeName();
             String valueOfVar = gExec.Interpreter.GlobalValues.jshell.eval(vn).get(0).value();
          // GlobalValues.GroovyShell.setVariable(vn, valueOfVar);
           
         //   System.out.println("Variable: "+vn+" type: "+typeOfVar+" value = "+valueOfVar);
             if (vn.contains("$")==false) {
              GlobalValues.jshellBindingValues.put(vn, valueOfVar);
              GlobalValues.jshellBindingTypes.put(vn, typeOfVar);
                     }
         }
                     });
                 }
           
    System.out.flush();
           }

                e.consume();
     break;
                 /*
       StandAloneApplicationActionGroovy standAloneAppl = new StandAloneApplicationActionGroovy();
       standAloneAppl.transformScriptToStandAlone();
                         */
                 /*
       StandAloneApplicationActionGroovy standAloneAppl = new StandAloneApplicationActionGroovy();
       standAloneAppl.transformScriptToStandAlone();
                         */
                 /*
       StandAloneApplicationActionGroovy standAloneAppl = new StandAloneApplicationActionGroovy();
       standAloneAppl.transformScriptToStandAlone();
                         */
                 /*
       StandAloneApplicationActionGroovy standAloneAppl = new StandAloneApplicationActionGroovy();
       standAloneAppl.transformScriptToStandAlone();
                         */
       
                
            case KeyEvent.VK_F4:
                gExec.gui.WatchWorkspace.displayGroovySciBinding(gExec.gLab.gLab.variablesWorkSpacePanel);
                gExec.Interpreter.GlobalValues.gLabMainFrame.repaint();
                
                break;
                
            
             case KeyEvent.VK_F12:
                  if (GlobalValues.AutoCompletionInitialized==false) {
                   GlobalAutoCompletion.initAutoCompletion();
                   gExec.Interpreter.GlobalValues.AutoCompletionGroovySci = new gExec.gui.AutoCompletionGroovySci();  // create the autocompletion object
                   GlobalValues.AutoCompletionInitialized = true;
                }
             //  treat the identifier under the cursor as a type, e.g. var jf = new javax.swing.JFrame(), javax.swing.JFrame() is a type
                if (e.isAltDown())
                    GlobalValues.gLabMainFrame.closeGUI();
                else {         
                    if (e.isShiftDown())
                        GlobalValues.inspectClass=true;
                    else
                GlobalValues.inspectClass = false;   
                gLabEdit.RSyntaxInspectCompletionListAction  inspect = new gLabEdit.RSyntaxInspectCompletionListAction();
                inspect.actionPerformed(null);
                  }
                
                e.consume();            
                break;
            
                 
     case KeyEvent.VK_F9:
        if (e.isShiftDown())  {   // we compile for Embedded Java
          new gLabEdit.compileExecuteTextJava().compileExecuteTextJavaEmbedded();
          e.consume();
        }
        else {  
         new gLabEdit.compileExecuteTextJava().compileExecuteTextJava();
          e.consume();
        }     
          break;
            
          
     case KeyEvent.VK_F11:
         if (e.isShiftDown()==false)  {   // treat the identifier under the cursor as a variable, e.g.  var  jf = new javax.swing.JFrame(), jf is a variable
                   Abbreviations.detectAndReplaceWordAtCaret();
         }
         else {
         new gLabEdit.compileExecuteTextJava().executeTextExternalJava(); 
         e.consume();
         }
         break;

     
        
     case KeyEvent.VK_F5:
         GlobalValues.consoleOutputWindow.resetText( " ");
         e.consume();
         break;
        
            case KeyEvent.VK_F2:
     String etext =  editorPane.getText();
     int currentTextLen = etext.length();
     if  (currentTextLen != textLen)   // text altered at the time between F2 clicks
      {
         fromLoc = 0;    // reset
     }
    
     int cursorLoc = editorPane.getCaretPosition();
     if (cursorLoc < toLoc)  {
     // reset if cursor is within the already executed part
         fromLoc = 0;
     }
     toLoc = cursorLoc;
     String textToExec = etext.substring(fromLoc, toLoc);
     
     editorPane.setSelectionStart(fromLoc);
     editorPane.setSelectionEnd(toLoc);
     editorPane.setSelectedTextColor(java.awt.Color.RED);
     textToExec = textToExec.substring(0, textToExec.lastIndexOf("\n"));
     fromLoc += textToExec.length();
     
      String grResult = gExec.Interpreter.Interpreter.execWithGroovyShell(textToExec);
     GlobalValues.consoleOutputWindow.output.append("\n"+grResult);
     GlobalValues.consoleOutputWindow.output.setCaretPosition(GlobalValues.consoleOutputWindow.output.getText().length());

        gExec.gui.WatchWorkspace.displayGroovySciBinding(gExec.gLab.gLab.variablesWorkSpacePanel);
      e.consume();
    break;

            
            case KeyEvent.VK_F1:
            case KeyEvent.VK_F3:    
                    e.consume();  // consume this event so it will not be processed in the default manner by the source that originated it
                  		//get the text on the current line
    
                    String inputString  = editorPane.getSelectedText();
                    if (inputString != null)   {   // some text is selected
               String [] matches = null;
               if (keyValue==KeyEvent.VK_F1) 
                       matches = gExec.Interpreter.GlobalValues.AutoCompletionGroovySci.getMatched(inputString);
               else 
                       matches = gExec.Interpreter.GlobalValues.AutoCompletionGroovySci.getMatchedRegEx(inputString);
                     
                    
                     final JList  resultsList = new JList(matches);
                     autocompleteListHandler  detailHelpAdapter = new autocompleteListHandler();
                     resultsList.addKeyListener(detailHelpAdapter); 
                     
                     
                     resultsList.addListSelectionListener(new ListSelectionListener() {
                         public void valueChanged(ListSelectionEvent e) {
                             String  selValue = resultsList.getSelectedValue().toString();
                             GlobalValues.selectedStringForAutoCompletion = selValue;
                        
                         }
                     }
                            );
                            
                GlobalValues.autoCompletionFrame = new AutoCompletionFrame("Glab editor autocompletion, Press F1  for detailed help on the selected entry");
                GlobalValues.autoCompletionFrame.displayMatches(resultsList);
               }    // some text is selected   
                    e.consume(); 
                    break;
                     
                    
            default:
                caretPos = editorPane.getCaretPosition();
                
          }
    }
    
         
    public void mouseClicked(MouseEvent me)
        { 
            
   if (me.getClickCount()>=2)  {  //only on ndouble-clicks
       RSyntaxTextArea    editor = (RSyntaxTextArea) me.getSource();
       Point  pt = new Point(me.getX(), me.getY());
       int  pos = editor.viewToModel(pt);
       javax.swing.text.Document  doc = editor.getDocument();
       
       boolean  exited = false;
       String  wb = "";
       int  offset = pos;
         // extract the part of the word before the mouse click position
       while (offset >= 0 && exited==false) {
         char  ch=' ';
                try {
                    ch = doc.getText(offset, 1).charAt(0);
                } catch (BadLocationException ex) {
                    System.out.println("Bad Location exception");
                    ex.printStackTrace();
                }
         boolean  isalphaNumeric = ( ch >= 'a' && ch <='z')  || (ch >= 'A' && ch <='Z') || (ch >= '0' && ch <='9');
         if (!isalphaNumeric)  exited=true;
          else {
           wb = wb + ch;
           offset -= 1;
           }
          }
       
       String  wa = "";
       int  docLen = doc.getLength();
       offset = pos+1;
       exited = false;
         // extract the part of the word after the mouse click position
       while (offset < docLen && exited==false) {
         char ch=' ';
                try {
                    ch = doc.getText(offset, 1).charAt(0);
                } catch (BadLocationException ex) {
                     System.out.println("Bad Location exception");
                     ex.printStackTrace();
               }
         boolean  isalphaNumeric = ( ch >= 'a' && ch <='z')  || (ch >= 'A' && ch <='Z') || (ch >= '0' && ch <='9');
         if (!isalphaNumeric)  exited=true;
           else {
         wa = wa + ch;
         offset += 1;
           }
         }
         
         StringBuffer wbreverse = new StringBuffer();
         for (int k=wb.length()-1; k>=0; k--)
             wbreverse.append(wb.charAt(k));
         
         String  wordAtCursor = wbreverse.toString()+wa;       
          
         Object clickedVar = GlobalValues.GroovyShell.getVariable(wordAtCursor);
         
         
     String result = gExec.Interpreter.Interpreter.execWithGroovyShell(wordAtCursor);
     if (result != null)  {
        GlobalValues.consoleOutputWindow.output.append("\n"+result+"\n");
        GlobalValues.consoleOutputWindow.output.setCaretPosition(GlobalValues.consoleOutputWindow.output.getText().length());
            gExec.gui.WatchWorkspace.displayGroovySciBinding(gExec.gLab.gLab.variablesWorkSpacePanel);
     }
      
       if (clickedVar != null) {
            GlobalValues.consoleOutputWindow.output.setCaretPosition(GlobalValues.consoleOutputWindow.output.getText().length());
         }
       }
      }
              
    
    
    void display_help() {
        JFrame helpFrame = new JFrame("Glab help");
        helpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        helpFrame.setSize(400, 400);
        Container container = helpFrame.getContentPane();
        JTextArea helpText = new JTextArea();

        int classCnt = 0;
        Hashtable  clTable= new Hashtable(); 
        Enumeration enumer = clTable.elements();
        TreeSet  sortedClasses =  new TreeSet();
        while(enumer.hasMoreElements())
		{
		    Object next = (Object)enumer.nextElement();
		    Class currentClass = (Class)next;
                    String className = currentClass.getCanonicalName();
                    sortedClasses.add(className);
                    classCnt++;
        }

          Iterator iter = sortedClasses.iterator();
          while (iter.hasNext()) {
                    String className = (String)iter.next();
                    helpText.append(className+"\n");
            }
          JScrollPane  helpPane = new JScrollPane(helpText);
        
        container.add(helpPane);
        helpFrame.setVisible(true);  
                
      }
    
        
    
    public void keyReleased(KeyEvent e)
    {
    	        
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
    
    
    
    // displays detailed help for the selected item
    public static void display_detailed_help(String selectedItem) {
GlobalValues.detailHelpStringSelected = selectedItem;
DetailHelpFrame detailFrame = new DetailHelpFrame();
detailFrame.setVisible(true);
        
      }

       
 
}
