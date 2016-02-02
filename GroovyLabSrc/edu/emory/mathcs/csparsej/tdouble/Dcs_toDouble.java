

package edu.emory.mathcs.csparsej.tdouble;

import edu.emory.mathcs.csparsej.tdouble.Dcs_common.Dcs;

public class Dcs_toDouble {

    public static double [][] cs_toDouble(Dcs A) {
        int p, j, m, n, nzmax, nz, Ap[], Ai[];
        double Ax[];
        if (A == null) {
            System.out.print("(null)\n");
            return null;
        }
        m = A.m;  //  number of rows
        n = A.n;   //  number of columns
        double [][]x = new double [m][n]; 
        
        Ap = A.p;  // column pointers (size n+1) or col indices (size nzmax) 
        Ai = A.i;   // row indices, size nzmax
        Ax = A.x;    // numerical values, size nzmax
        nzmax = A.nzmax;
        nz = A.nz;
        for (j = 0; j < n; j++) 
            for (p = Ap[j]; p < Ap[j + 1]; p++)  {
                int row = Ai[p];  
                x[row][j] = Ax[p];
            }
        
        return x;
    }

}
