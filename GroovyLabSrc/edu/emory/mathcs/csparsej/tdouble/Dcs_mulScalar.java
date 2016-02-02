

package edu.emory.mathcs.csparsej.tdouble;

import edu.emory.mathcs.csparsej.tdouble.Dcs_common.Dcs;

public class Dcs_mulScalar {

    public static Dcs  cs_mulScalar(Dcs A, double alpha) {
        int p, j,  anz;
        int   m, n;
        Dcs C;
        if (!Dcs_util.CS_CSC(A) )
            return null; /* check inputs */
        
        m = A.m;             // number of rows
        n = A.n;  // number of columns
        anz = A.p[A.n];  // non-zero elements of matrix A
        
        C = new Dcs();
        C.nz = -1;   // compressed-col format
        C.m = m; 
        C.n = n;
        C.p = new int[n+1];   // column pointers
        C.i = new int[anz]; // row pointers
        C.x = new double[anz]; // values
        System.arraycopy(A.p, 0, C.p, 0, n+1);
        System.arraycopy(A.i, 0, C.i, 0, anz);
        // multiply values 
        for (int v=0; v<anz; v++)
            C.x[v] = A.x[v] * alpha;
        
            
        return C; /* success; free workspace, return C */

 }
}
