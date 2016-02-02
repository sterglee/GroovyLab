
package gExec.gui;

import gExec.Interpreter.GlobalValues;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class WaveletToolbar  extends JPanel {
        JButton bHaar, bfwt, bMP;
        
        
    public WaveletToolbar() {
        JPanel waveletPanel = new JPanel();
        setLayout(new BorderLayout());

        bHaar = new JButton("Haar");
        bHaar.setToolTipText("Haar Wavelet");
        bHaar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
        GlobalValues.gLabMainFrame.jLabConsole.setText(GlobalValues.gLabMainFrame.jLabConsole.getText()+"Haar()");
        GlobalValues.gLabMainFrame.jLabConsole.setCaretPosition(GlobalValues.gLabMainFrame.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.gLabMainFrame.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.gLabMainFrame.jLabConsole.dispatchEvent(fe);
            }
        });

        bfwt = new JButton("FWT");
        bfwt.setToolTipText("Forward Wavelet Transform");
        bfwt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
        GlobalValues.gLabMainFrame.jLabConsole.setText(GlobalValues.gLabMainFrame.jLabConsole.getText()+"fwt()");
        GlobalValues.gLabMainFrame.jLabConsole.setCaretPosition(GlobalValues.gLabMainFrame.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.gLabMainFrame.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.gLabMainFrame.jLabConsole.dispatchEvent(fe);
            }
        });

        bMP = new JButton("MP");
        bMP.setToolTipText("Matching Pursuit");
        bMP.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
        GlobalValues.gLabMainFrame.jLabConsole.setText(GlobalValues.gLabMainFrame.jLabConsole.getText()+"MP()");
        GlobalValues.gLabMainFrame.jLabConsole.setCaretPosition(GlobalValues.gLabMainFrame.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.gLabMainFrame.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.gLabMainFrame.jLabConsole.dispatchEvent(fe);
            }
        });

        
        waveletPanel.add(bHaar); waveletPanel.add(bfwt);
        waveletPanel.add(bMP);  
        
        add(waveletPanel);
     
   }
}

