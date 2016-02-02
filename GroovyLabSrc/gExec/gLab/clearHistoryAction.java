package gExec.gLab;

import gExec.Interpreter.GlobalValues;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class clearHistoryAction extends AbstractAction   {
  public  clearHistoryAction() {
    super("Clear History");
  }
    
    @Override
        public void actionPerformed(ActionEvent e) {
               GlobalValues.userConsole.previousCommands.clear();
               GlobalValues.gLabMainFrame.updateHistoryWindow();
               gLab.historyPanel.repaint();
                     }

        }


