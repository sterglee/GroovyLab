
package gExec.gui;

import gExec.gui.MathDialogs.ExpressionDialogPlot3D_Grid;
import gExec.gui.MathDialogs.ExpressionDialogPlot2D_Line;
import gExec.Interpreter.GlobalValues;
import gExec.gui.MathDialogs.ExpressionDialogPlot2D_Histo;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class PlotOperationsToolbar  extends JPanel {
        JButton bwhos, bdir, bcd, bmd, bcls;
        JButton bsin, bcos, btan, basin, bacos, batan,  bsinh, bcosh, btanh, basinh, bacosh, batanh;
        JButton bsqrt, blog, bexp, babs, bfloor, bceil, bln;
        JButton  bplot2d_line, bplot2d_bar, bplot2d_histo, bplot2d_cloud, bplot2d_box, bplot3d_box, bplot3d_cloud, bplot3d_grid;
        
    public PlotOperationsToolbar() {
        JPanel plotPanel = new JPanel();
        
        setLayout(new GridLayout(2,1));

        
        bplot2d_line = new JButton("plot2d_line ...");
        bplot2d_line.setToolTipText("2D plots: Plotting a function y = f(x). Line plot");
        bplot2d_line.setFont(new Font("Times New Roman", Font.BOLD, 14));
        bplot2d_line.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
           ExpressionDialogPlot2D_Line  exprDialog = new ExpressionDialogPlot2D_Line(GlobalValues.gLabMainFrame);
           exprDialog.setLocation(GlobalValues.gLabMainFrame.getLocation());
           exprDialog.pack();
           exprDialog.setVisible(true);
                  
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);    
            }
        });
        
        bplot2d_histo = new JButton("plot2d_histo ...");
        bplot2d_histo.setToolTipText("2D plots: Histogram of a function y = f(x). ");
        bplot2d_histo.setFont(new Font("Times New Roman", Font.BOLD, 14));
        bplot2d_histo.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
           ExpressionDialogPlot2D_Histo  exprDialog = new ExpressionDialogPlot2D_Histo(GlobalValues.gLabMainFrame);
           exprDialog.setLocation(GlobalValues.gLabMainFrame.getLocation());
           exprDialog.pack();
           exprDialog.setVisible(true);
                  
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);    
            }
        });
        
        bplot3d_grid = new JButton("plot3d_grid ...");
        bplot3d_grid.setToolTipText("3D plots: Plotting a function z = f(x,y). Grid Plot");
        bplot3d_grid.setFont(new Font("Times New Roman", Font.BOLD, 14));
        bplot3d_grid.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
           ExpressionDialogPlot3D_Grid  exprDialog = new ExpressionDialogPlot3D_Grid(GlobalValues.gLabMainFrame);
           exprDialog.setLocation(GlobalValues.gLabMainFrame.getLocation());
           exprDialog.pack();
           exprDialog.setVisible(true);
                  
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);    
            }
        });
        plotPanel.add(bplot2d_line); plotPanel.add(bplot2d_histo);  plotPanel.add(bplot3d_grid);
                
        add(plotPanel);
        
   }
}

