package gExec.gLab;

import gExec.Interpreter.GlobalValues;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class loadHistoryAction extends AbstractAction   {
    public loadHistoryAction()  {
        super("Load History");
    }
      @Override
        public void actionPerformed(ActionEvent e) {
         gExec.gLab.commandHistory.loadCommandHistory(gExec.Interpreter.GlobalValues.GroovyLabCommandHistoryFile, GlobalValues.userConsole.previousCommands);
                     }
        }
      
        


