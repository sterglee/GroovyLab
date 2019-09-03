
package SysUtils;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JTextArea;
import gExec.Interpreter.GlobalValues;

class clearOutputAction extends  AbstractAction {
    JTextArea areaToClear;
   public clearOutputAction(JTextArea _areaToClear) {
       super("clear Console");
       areaToClear = _areaToClear;
   }
 
    @Override
    public void actionPerformed(ActionEvent e) {
        GlobalValues.consoleOutputWindow.resetText( " ");
         
    }
}

 

class verboseOutputAction extends  AbstractAction {
    JTextArea areaToClear;
    
   public verboseOutputAction() {
       super("Toggles output verbose.");
   }

    @Override
    public void actionPerformed(ActionEvent e) {
       if (GlobalValues.displayAtOutputWindow==true) GlobalValues.displayAtOutputWindow=false;
       else GlobalValues.displayAtOutputWindow = true;
       System.out.println("verbose ="+GlobalValues.displayAtOutputWindow);
    }
  }
