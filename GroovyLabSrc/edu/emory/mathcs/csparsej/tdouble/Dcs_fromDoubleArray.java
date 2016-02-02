

package edu.emory.mathcs.csparsej.tdouble;

import edu.emory.mathcs.csparsej.tdouble.Dcs_common.Dcs;

public class Dcs_fromDoubleArray {
   private static int row=-1, col=0;
   private static int nonZeroRow, nonZeroCol;
   private static int Nrows, Ncols;
   private static double  value;
   private static double [][] values;
   
   public static Dcs cs_fromDoubleArray(double [][] A) {
       if (A == null) {
            System.out.print("(null)\n");
            return null;
        }
       
        values = A;
        Nrows = A.length; 
        Ncols = A[0].length;
        int maxCol = -1;
        int maxRow = -1;
        
      
        Dcs T;
        T = Dcs_util.cs_spalloc(0, 0, 1, true, true); /* allocate result */
        while (nextTripletValue() != false) {  // while exist more triplets
                if (!Dcs_entry.cs_entry(T, nonZeroRow, nonZeroCol, value))
                    return (null);
                else {
                    if (nonZeroRow > maxRow) maxRow = nonZeroRow;
                    if (nonZeroCol > maxCol)  maxCol = nonZeroCol;
                }
            }
        T.m = maxRow+1;
        T.n  = maxCol+1;
        
        return Dcs_compress.cs_compress(T);
    }

// returns the next triplet value from the double[][] array in column order
   private static boolean nextTripletValue() {
     while (true) {
        row++;
        if (col > Ncols-1)   // end of columns
               return false;
           
        if (row == Nrows-1)  // next column
        {
         // take the current value
           value = values[row][col];
           nonZeroRow = row;  
           nonZeroCol = col;
         // reset for start of next column  
           row = -1;
           col++;
           
           if (value != 0.0)
               return true;
       } // next column
        else {  //  current column
       value = values[row][col];
       nonZeroRow = row;  
       nonZeroCol = col;
       if (value != 0.0)
         return true;
        }  // current column
        
     }
   }  
   
}
