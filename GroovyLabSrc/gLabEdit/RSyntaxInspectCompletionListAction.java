package gLabEdit;

import gExec.Interpreter.GlobalValues;
import java.awt.event.ActionEvent;
import java.util.Vector;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

// this action inspects a completion list for the type of an identifier, e.g.   jf = new javax.swing.JFrame(), jf is the identifier
// and the completion list is formed by discovering the contents of the javax.swing.JFrame class using Java reflection
class RSyntaxInspectCompletionListAction extends javax.swing.AbstractAction {

    @Override
    public void actionPerformed(ActionEvent e) {

         RSyntaxTextArea  editor = gExec.Interpreter.GlobalValues.globalEditorPane;   // get RSyntaxArea based editor instance
         
         int  pos = editor.getCaretPosition()-1;      // position of the caret
         Document  doc = editor.getDocument();  // the document being edited
       
         GlobalValues.methodNameSpecified = false;
         GlobalValues.selectionStart = -1;
         
       boolean  exited = false;
        // take word part before cursor position
       String  wb = "";
       int  offset = pos;
       while (offset >= 0 && exited==false) {
         char  ch = 0;
            try {
                ch = doc.getText(offset, 1).charAt(0);
                if (ch == '.') {
                    GlobalValues.methodNameSpecified = true;
                    GlobalValues.selectionStart = offset+1;
                }
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
         boolean  isalphaNumeric = ( ch >= 'a' && ch <='z')  || (ch >= 'A' && ch <='Z') || (ch >= '0' && ch <='9') || ch=='.' || ch=='_';
         if (!isalphaNumeric)  exited = true;
          else {
           wb = wb + ch;
           offset--;
            }
          }
         
       if (GlobalValues.selectionStart == -1)  // a method name is not specified, thus set selection start to the beginning of the word
      GlobalValues.selectionStart = pos+1;
    
          // take word part after cursor position
       String  wa = "";
       int  docLen = doc.getLength();
       offset = pos+1;
       exited = false;
       while (offset < docLen && exited==false) {
         char  ch = 0;
            try {
                ch = doc.getText(offset, 1).charAt(0);
                if (ch == '.')  GlobalValues.methodNameSpecified = true;
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
         boolean  isalphaNumeric = ( ch >= 'a' && ch <='z')  || (ch >= 'A' && ch <='Z') || (ch >= '0' && ch <='9') || ch=='.' ||  ch=='_';
         if (!isalphaNumeric)  exited = true;
           else {
         wa = wa + ch;
         offset++;
           }
         }
       GlobalValues.selectionEnd = offset;
  
      //   form total word that is under caret position
         // reverse string at the left of cursor
       StringBuffer sb = new StringBuffer(wb);
       StringBuffer rsb = new StringBuffer(wb);
       int p = 0;
       for (int k= wb.length()-1; k>=0; k--)
           rsb.setCharAt(p++, sb.charAt(k));
        
       // concatenate to form the whole word
       String  wordAtCursor = rsb.toString()+wa;

       wordAtCursor = wordAtCursor.trim();  // trim any spaces

       GlobalValues.textForCompletion = wordAtCursor; 
       
         String  className = wordAtCursor;
         
         if (wordAtCursor != null) {
         if (wordAtCursor.indexOf(".") > -1) {   // the user has typed a method after the period
            String [] classNameSplitted = wordAtCursor.split("\\."); 
            className = classNameSplitted[0];   // the class name
            if (classNameSplitted.length > 1)   // method has been specified
             groovySciCommands.Inspect.methodSubString = classNameSplitted[1];  // method name
           }
          else
            groovySciCommands.Inspect.methodSubString = null;  // method not specified
            
      if (gExec.Interpreter.GlobalValues.inspectClass==false)  {
          // get the object that the variable "className" refers to
          Object myVar = gExec.Interpreter.GlobalValues.GroovyShell.getVariable(className);
          groovySciCommands.Inspect.inspectCompletionList(myVar);  // get completion info for our variable
      }
      else {
        groovySciCommands.Inspect.inspectg(wordAtCursor);
      }
          
            
             }

     }

       
}