package gExec.gui.MathDialogs;

import gExec.Interpreter.GlobalValues;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

        
public class ExpressionDialogPlot2D_Histo extends ExpressionDialogPlot2DCommon
{
    JPanel slicesPanel = new JPanel();
    JLabel slicesLabel = new JLabel("SlicesX: ");
    int  slicesX = 10;
    JTextField slicesTextField = new JTextField(String.valueOf(slicesX));
    String plotHistStr = "plot2d_histogram(x, y, slicesX, plotName);";
    String  plotName = "2D Histogram plot";
    
    
  public  ExpressionDialogPlot2D_Histo(JFrame fr)
   {  
       super(fr, "Histogram of Function of one variable");
   
       slicesPanel.add(slicesLabel);
       slicesPanel.add(slicesTextField);
       limitsAndFormatPanel.add(slicesPanel);

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
         double stepX = (upX-lowX)/(double)defaultSampleNumPoints;
    
         slicesX = Integer.parseInt(slicesTextField.getText());
         String fXStr = fXStep.getText();
         if (fXStr.equals("")==false)
              stepX = Double.parseDouble(fXStep.getText());
         
         String expr = fFun.getText();
         expr = expr.trim().toLowerCase();
         
         expr = expr.replaceAll("exp", "dummy");   // "x" in "exp" causes a problem so remove exp- temporarily
         expr = expr.replaceAll("x", "x[xi]");
         expr = expr.replaceAll("dummy", "exp");
               
        String codeStr =  "int nx = (int)Math.floor((double)("+(upX-lowX)+")/(double)"+stepX+");  x = new double[nx];  \n"+
                                      "y = new double [nx]; \n"+
                                " for (int nxi=0; nxi<nx; nxi++) x[nxi] = nxi*"+stepX+" ; \n"+
                                    "for (int xi=0; xi<nx; xi++) \n"+
                                    "       y[xi] = "+expr+" ; \n"+
                                    "slicesX = "+slicesX+" ; \n"+
                                    "plotName = "+ "\"" + plotName +  "\""+ " ;\n"+
                                    plotHistStr+"\n";
        
                gExec.Interpreter.Interpreter.execWithGroovyShell(codeStr);       
            }

            
        });
       
        generateCodeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
         double lowX = Double.parseDouble(fXLow.getText());
         double upX = Double.parseDouble(fXUp.getText());
      
        double stepX = (upX-lowX)/(double)defaultSampleNumPoints;
             String fXStr = fXStep.getText();
         if (fXStr.equals("")==false)
              stepX = Double.parseDouble(fXStep.getText());
      
         
         String expr = fFun.getText();
         expr = expr.trim().toLowerCase();
         
         expr = expr.replaceAll("exp", "dummy");   // "x" in "exp" causes a problem so remove exp- temporarily
         expr = expr.replaceAll("x", "x[xi]");
         expr = expr.replaceAll("dummy", "exp");
       
        String codeStr =  "int nx = (int)Math.floor((double)("+(upX-lowX)+")/(double)"+stepX+");  x = new double[nx];  \n"+
                                      "y = new double [nx]; \n"+
                                " for (int nxi=0; nxi<nx; nxi++) x[nxi] = nxi*"+stepX+" ; \n"+
                                    "for (int xi=0; xi<nx; xi++) \n"+
                                    "       y[xi] = "+expr+" ; \n"+
                                    "slicesX = "+slicesX+" ; \n"+
                                    "plotName = "+ "\"" + plotName +  "\""+ " ;\n"+
                                    plotHistStr+"\n";
        
        
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