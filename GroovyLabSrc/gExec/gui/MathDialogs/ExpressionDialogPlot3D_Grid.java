package gExec.gui.MathDialogs;

import gExec.Interpreter.GlobalValues;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JTextArea;

        
public class ExpressionDialogPlot3D_Grid extends ExpressionDialog3DCommon
{  
      String  plotStr = "plot(x, y, z);";

  public  ExpressionDialogPlot3D_Grid(JFrame fr)
   {  
   super(fr, "3-D Grid Plot");
  
      addWindowListener(new WindowAdapter()
      {     @Override
  public void windowClosing(WindowEvent e)
         {  dispose();
            }
      });

        plotButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
         
         double lowX = Double.parseDouble(fXLow.getText());
         
         double upX = Double.parseDouble(fXUp.getText());
         double lowY = Double.parseDouble(fYLow.getText());
         double upY = Double.parseDouble(fYUp.getText());
 // compute default stepX and stepY values to use, not specified explicitly         
         double stepX = (upX-lowX)/(double)defaultSampleNumPoints;
         double stepY = (upY-lowY)/(double)defaultSampleNumPoints;
         
         String fXStr = fXStep.getText();
         if (fXStr.equals("")==false)
              stepX = Double.parseDouble(fXStep.getText());
         
         String fYStr = fYStep.getText();
         if (fYStr.equals("")==false)
              stepY = Double.parseDouble(fYStep.getText());
         
         String expr = fFun.getText();
         expr = expr.trim().toLowerCase();
         expr = expr.replaceAll("exp", "dummy");   // "x" in "exp" causes a problem so remove exp- temporarily
         expr = expr.replaceAll("x", "x[xi]");
         expr = expr.replaceAll("dummy", "exp");
         expr = expr.replaceAll("y", "y[yi]");
               
        String codeStr =  "int nx = (int)Math.floor((double)("+(upX-lowX)+")/(double)"+stepX+");  x = new double[nx];  \n"+
                                      "int ny =(int) Math.floor((double)("+(upY-lowY)+")/(double)"+stepY+");  y = new double[ny];  \n"+
                                    "z = new double [ny][nx]; \n"+
                                " for (int nxi=0; nxi<nx; nxi++) x[nxi] = nxi*"+stepX+" ; \n"+
                                "for (int nyi=0; nyi<ny; nyi++)  y[nyi] = nyi*"+stepY+"; \n"+
                                    "for (int xi=0; xi<nx; xi++) \n"+
                                    "  for (int yi=0; yi<ny; yi++) \n"+
                                    "       z[yi][xi] = "+expr+" ; \n"+
                                    plotStr+"\n";
        
                gExec.Interpreter.Interpreter.execWithGroovyShell(codeStr);       
            }

            
        });
       
        generateCodeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
         double lowX = Double.parseDouble(fXLow.getText());
         double upX = Double.parseDouble(fXUp.getText());
         
         double lowY = Double.parseDouble(fYLow.getText());
         double upY = Double.parseDouble(fYUp.getText());
   
  // compute default stepX and stepY values to use, not specified explicitly         
         double stepX = (upX-lowX)/(double)defaultSampleNumPoints;
         double stepY = (upY-lowY)/(double)defaultSampleNumPoints;
         
         String fXStr = fXStep.getText();
         if (fXStr.equals("")==false)
              stepX = Double.parseDouble(fXStep.getText());
         
         String fYStr = fYStep.getText();
         if (fYStr.equals("")==false)
              stepY = Double.parseDouble(fYStep.getText());
         
         String expr = fFun.getText();
         expr = expr.trim().toLowerCase();
         
         expr = expr.replaceAll("exp", "dummy");   // "x" in "exp" causes a problem so remove exp- temporarily
         expr = expr.replaceAll("x", "x[xi]");
         expr = expr.replaceAll("dummy", "exp");
         expr = expr.replaceAll("y", "y[yi]");
         
        String codeStr =  "int nx = (int)Math.floor((double)("+(upX-lowX)+")/(double)"+stepX+");  x = new double[nx];  \n"+
                                      "int ny =(int) Math.floor((double)("+(upY-lowY)+")/(double)"+stepY+");  y = new double[ny];  \n"+
                                    "z = new double [ny][nx]; \n"+
                                " for (int nxi=0; nxi<nx; nxi++) x[nxi] = nxi*"+stepX+" ; \n"+
                                "for (int nyi=0; nyi<ny; nyi++)  y[nyi] = nyi*"+stepY+"; \n"+
                                    "for (int xi=0; xi<nx; xi++) \n"+
                                    "  for (int yi=0; yi<ny; yi++) \n"+
                                    "       z[yi][xi] = "+expr+" ; \n"+
                                    plotStr+"\n";
        
         boolean GroovyOn = true;
         if (fCode != null)
           buttonsPanel.remove(fCode);
         fCode = new JTextArea(codeStr);
         buttonsPanel.add(fCode);
         pack();
         
            }
        });
    
  }   
}