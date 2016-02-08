package gExec.gLab;

import gExec.Interpreter.GlobalValues;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class saveHistoryAction extends  AbstractAction  {
    public saveHistoryAction() {
        super("Save History");
    }        
         
         @Override
        public void actionPerformed(ActionEvent e) {
         gExec.gLab.commandHistory.saveCommandHistory(gExec.Interpreter.GlobalValues.GroovyLabCommandHistoryFile, GlobalValues.userConsole.previousCommands);
                     }
        }
              
      
        


