package groovySci.math.array;

import gExec.Interpreter.GlobalValues;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class eigReIm {
  public double [] realEvs;
  public  double [] imEvs;
  public  double [][] realEvecs;
  public double [][] imEvecs;
  
  // allocate space for n eigenvalues/eigenvectors
  public eigReIm(int n) {
      realEvs = new double[n];
      imEvs = new double[n];
      realEvecs = new double[n][n];
      imEvecs = new double[n][n];
  }
  
  
  @Override
public String toString() {
  String  formatString = "0.";
  for (int k = 0; k < groovySci.PrintFormatParams.vecDigitsPrecision; k++) 
       formatString += "0";
    DecimalFormat digitFormat = new DecimalFormat(formatString);
    digitFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(new Locale("us")));
   
    StringBuilder sb = new StringBuilder();
      int N = realEvs.length;
      if (groovySci.PrintFormatParams.matMxRowsToDisplay < N)
          N = groovySci.PrintFormatParams.matMxRowsToDisplay;
      sb.append("\nEigenvalues: \n");
      for (int k=0; k<N; k++) {
          double imPart = imEvs[k];
          if (Math.abs(imPart) > GlobalValues.nearZeroValue)
      sb.append(digitFormat.format(realEvs[k])+"+  i * "+digitFormat.format(imEvs[k])+"\n");
          else
      sb.append(digitFormat.format(realEvs[k])+"\n");        
              }
      
      // columns are eigenvectors 
      for (int col = 0; col<N; col++) {
          sb.append("\nEigenvector["+col+"] = \n");
          for (int row = 0; row<N; row++) {
            if (Math.abs(imEvecs[row][col]) > GlobalValues.nearZeroValue)
              sb.append(digitFormat.format(realEvecs[row][col])+" +  i * "+digitFormat.format(imEvecs[row][col])+ "     ");
            else
                sb.append(digitFormat.format(realEvecs[row][col])+"    ");
          }
          
      }
      
      return sb.toString();
          
          
  }          


public String print() {
  String  formatString = "0.";
   for (int k = 0; k < groovySci.PrintFormatParams.vecDigitsPrecision; k++) 
       formatString += "0";
    DecimalFormat digitFormat = new DecimalFormat(formatString);
    digitFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(new Locale("us")));
   
    StringBuilder sb = new StringBuilder();
      int N = realEvs.length;
      sb.append("\nEigenvalues: \n");
      for (int k=0; k<N; k++) {
          double imPart = imEvs[k];
          if (Math.abs(imPart) > GlobalValues.nearZeroValue)
      sb.append(digitFormat.format(realEvs[k])+"+  i * "+digitFormat.format(imEvs[k])+"\n");
          else
      sb.append(digitFormat.format(realEvs[k])+"\n");        
              }
      
      // columns are eigenvectors 
      for (int col = 0; col<N; col++) {
          sb.append("\nEigenvector["+col+"] = \n");
          for (int row = 0; row<N; row++) {
            if (Math.abs(imEvecs[row][col]) > GlobalValues.nearZeroValue)
              sb.append(digitFormat.format(realEvecs[row][col])+" +  i * "+digitFormat.format(imEvecs[row][col])+ "     ");
            else
                sb.append(digitFormat.format(realEvecs[row][col])+"    ");
          }
          
      }
      
      return sb.toString();
          
          
  }          
}
