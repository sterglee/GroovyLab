

package edu.emory.mathcs.csparsej.tdouble;

import edu.emory.mathcs.csparsej.tdouble.Dcs_common.Dcs;

public class Dcs_CSToTriplet {

    public static Dcs  cs_CSToTriplet(Dcs A) {
        int p, j, m, n, nzmax, nz, Ap[], Ai[];
        double Ax[];
        if (A == null) {
            System.out.print("(null)\n");
            return  null;
        }
        m = A.m;  //  number of rows
        n = A.n;   //  number of columns
        Ap = A.p;  // column pointers (size n+1) or col indices (size nzmax) 
        Ai = A.i;   // row indices, size nzmax
        Ax = A.x;    // numerical values, size nzmax
        nzmax = A.nzmax;
        nz = A.nz; 
        if (nz > 0) {
            System.out.println("Matrix not in compressed column form");
            return null;
        }
            
        // allocate a triplet matrix
        Dcs tripletDcs = Dcs_util.cs_spalloc(m, n, nzmax, true, true);
        
        for (j = 0; j < n; j++) {  // for all columns
            // for all row indices corresponding to the column
                for (int row = Ap[j]; row < Ap[j + 1]; row++) {
                double value = Ax[row];   // the value of (row, j)
                int rowIdx = Ai[row];  // the row index
                Dcs_entry.cs_entry(tripletDcs, rowIdx, j, value);  // triplet is: row, j, Ax[row]
                
                    }
                }
            
        return  tripletDcs;
    }

    // construct triplet matrix and changing element row, col to value if possible
    public static Dcs  cs_CSToTriplet(Dcs A, int r, int c, double [] valueAsRef) {
        double newValue = valueAsRef[0];
        boolean valueFound = false;
        int p, j, m, n, nzmax, nz, Ap[], Ai[];
        double Ax[];
        if (A == null) {
            System.out.print("(null)\n");
            return  null;
        }
        m = A.m;  //  number of rows
        n = A.n;   //  number of columns
        Ap = A.p;  // column pointers (size n+1) or col indices (size nzmax) 
        Ai = A.i;   // row indices, size nzmax
        Ax = A.x;    // numerical values, size nzmax
        nzmax = A.nzmax;
        nz = A.nz; 
        if (nz > 0) {
            System.out.println("Matrix not in compressed column form");
            return null;
        }
            
        // allocate a triplet matrix
        Dcs tripletDcs = Dcs_util.cs_spalloc(m, n, nzmax, true, true);
        
        for (j = 0; j < n; j++) {  // for all columns
            // for all row indices corresponding to the column
                for (int row = Ap[j]; row < Ap[j + 1]; row++) {
                double value = Ax[row];   // the value of (row, j)
                int rowIdx = Ai[row];  // the row index
                if (rowIdx == r && j == c) { // pass the new value
                    value = newValue;
                    valueFound = true;
                }
                Dcs_entry.cs_entry(tripletDcs, rowIdx, j, value);  // triplet is: row, j, Ax[row]
              }
        }  // for all columns
                
   if (valueFound==false) {  
       valueAsRef[0] =  -1.0;
   }
       
        return  tripletDcs;
    }

}
