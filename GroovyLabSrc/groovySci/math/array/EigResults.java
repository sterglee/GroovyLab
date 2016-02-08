package groovySci.math.array;

import static groovySci.math.array.Matrix.printArray;
        
public class EigResults {
  public double [] realEvs;
  public  double [] imEvs;
  public  double [][] realEvecs;
  public double [][] imEvecs;

  
  // allocate space for n eigenvalues/eigenvectors
  public EigResults(int n) {
      realEvs = new double[n];
      imEvs = new double[n];
      realEvecs = new double[n][n];
      imEvecs = new double[n][n];
  }
  
 public EigResults() {
 }
 
  @Override
  public  String toString() {
      StringBuilder sb = new StringBuilder();
      String realEvsStr = "real eigenvalues: "+ printArray(realEvs);
      String imEvsStr = "imaginary eigenvalues: "+ printArray(realEvs);
      String leftEvecsStr = "real eigenvectors: "+ printArray(realEvecs);
      String rightEvecsStr = "imag  eigenvectors: "+printArray(imEvecs);
      
      sb.append(realEvsStr); sb.append(imEvsStr); sb.append(leftEvecsStr); sb.append(rightEvecsStr);
      return sb.toString();
  }
  
}
