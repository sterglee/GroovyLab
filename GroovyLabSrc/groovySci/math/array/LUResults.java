package groovySci.math.array;

import static groovySci.math.array.Matrix.*; 

public class LUResults {
   public double  [][] L;
    public double [][] U;
    public double [][] P;
    public int [] Pi;  // pivot indexes
    public double [][] getL() { return L; }
    public double [][] getU() { return U; }
    public int [] getP() { return Pi; }


    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if  (L!=null)
          sb.append("L  = "+ printArray(L));
        else
          sb.append("L  =  null \n");
        if (U !=null)
          sb.append("U = "+ printArray(U));
        else
          sb.append("U = null \n");  
        if (P != null)
         sb.append("P = "+printArray(P));
        else
         sb.append("P = null \n");   
        
        return sb.toString();
    }
}
