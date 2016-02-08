
package gExec.gui;

import gExec.gui.MathDialogs.ExpressionDialogPlot3D_Grid;
import gExec.gui.MathDialogs.ExpressionDialogPlot2D_Line;
import gExec.Interpreter.GlobalValues;
import gExec.gui.MathDialogs.ExpressionDialogPlot2D_Histo;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class BasicControlOperationsToolbar  extends JPanel {
        JButton bwhos, bdir, bcd, bmd, bcls, bclearAll, bclearVar, bcloseAll, bDecformat;
        JButton bmatLoad, bmatSave;
         
    public BasicControlOperationsToolbar() {
        JPanel controlPanel1 = new JPanel();
        JPanel controlPanel2 = new JPanel();
        JPanel matlabPanel = new JPanel();
        
        setLayout(new GridLayout(4,1));

        bclearAll = new JButton("clear(\"all\")");
        bclearAll.setToolTipText("Clears the contents of the variable workspace");
        bclearAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
        GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"clear(\"all\")");
        GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);
            }
        });


        bclearVar = new JButton("clear (\"all\")");
        bclearVar.setToolTipText("Clears the contents of the specified variable");
        bclearVar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
        GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"clear(");
        GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);
            }
        });

        bcloseAll = new JButton("close(\"all\")");
        bcloseAll.setToolTipText("Closes all the open figures");
        bcloseAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
        GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"close(\"all\")");
        GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);
            }
        });
        
        
        bcls = new JButton("cls");
        bcls.setToolTipText("Clears the screen contents");
        bcls.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
        GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"cls()");
        GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);
            }
        });


        bcd = new JButton("cd");
        bcd.setToolTipText("Changes working directory, e.g. cd(\"/export/home/user/Java\")");
        bcd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
        GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"cd(");
        GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);
            }
        });

        bmd = new JButton("md");
        bmd.setToolTipText("Creates a new directory, e.d. md(\"myProgs\")");
        bmd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
        GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"md(");
        GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);
            }
        });

        bwhos = new JButton("whos");
        bwhos.setToolTipText("Displays the variables of the workspace");
        bwhos.addActionListener(new ActionListener() {
             @Override
            public void actionPerformed(ActionEvent e) {
        GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"whos()");
        GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);
    }
        });


        bdir = new JButton("dir/ls");
        bdir.setToolTipText("Lists the current directory: dir(), or lists the specified directory: dir(<nameOfDirectory>)");
        bdir.addActionListener(new ActionListener() {
             @Override
            public void actionPerformed(ActionEvent e) {
        GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"dir()");
        GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);
            }
        });

        bDecformat = new JButton("format()");
        bDecformat.setToolTipText("controls how many decimal points to display for doubles: format(decPoints),  sets to decPoints, returns previous setting");
        bDecformat.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
        GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"format(<decPoints>");;
        GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);
            }
        });
        
        controlPanel1.add(bclearAll); controlPanel1.add(bcloseAll); 
        controlPanel1.add(bwhos); controlPanel1.add(bdir); 
        controlPanel2.add(bcd); controlPanel2.add(bmd); controlPanel2.add(bcls);
        controlPanel2.add(bDecformat);
        add(controlPanel1); add(controlPanel2);       



        bmatLoad = new JButton("load .mat");
        bmatLoad.setToolTipText("Loads the contents of a Matlab .mat file in workspace, load(<matFileName>");
        bmatLoad.addActionListener(new ActionListener() {
             @Override
            public void actionPerformed(ActionEvent e) {
        GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"load( ");
        GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);
            }
        });

        bmatSave = new JButton("save .mat");
        bmatSave.setToolTipText("Saves the contents of the whole workspace to a  Matlab .mat file format:  save(<MatFileName>), or a specific only  variable:  save(<MatFileName, VariableToSave>)");
        bmatSave.addActionListener(new ActionListener() {
             @Override
            public void actionPerformed(ActionEvent e) {
        GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"save(");
        GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);
            }
        });
        
        matlabPanel.add(bmatLoad);   
        matlabPanel.add(bmatSave);
        
        add(matlabPanel);
   }
}

