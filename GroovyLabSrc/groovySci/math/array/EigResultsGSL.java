package groovySci.math.array;

import static groovySci.math.array.Matrix.printArray;
        
public class EigResultsGSL {
  public double [][] evals ;
  public  double [][] evecsReal;
  public  double [][] evecsImag;
  
  
  @Override
  public  String toString() {
      StringBuilder sb = new StringBuilder();
      String evalsStr = "eigenvalues: "+ printArray(evals);
      String evecsRealStr = "evecsReal: "+ printArray(evecsReal);
      String evecsImStr = "evecsImag: "+ printArray(evecsImag);
      
      sb.append(evalsStr); sb.append(evecsRealStr); sb.append(evecsImStr);
              
      return sb.toString();
  }
  
}
