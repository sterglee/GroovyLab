

package edu.emory.mathcs.csparsej.tdouble;

import edu.emory.mathcs.csparsej.tdouble.Dcs_common.Dcs;

public class Dcs_negate {

    public static Dcs  cs_negate(Dcs A) {
        int p, j,  anz;
        int   m, n;
        Dcs C;
        if (!Dcs_util.CS_CSC(A) )
            return null; /* check inputs */
        
        m = A.m;             // number of rows
        n = A.n;  // number of columns
        anz = A.p[A.n];  // non-zero elements of matrix A
        
        C = new Dcs();
        C.m = m; 
        C.n = n;
        C.p = new int[n+1];   // column pointers
        C.i = new int[anz]; // row pointers
        C.x = new double[anz]; // values
        System.arraycopy(A.p, 0, C.p, 0, n+1);
        System.arraycopy(A.i, 0, C.i, 0, anz);
        // negate values 
        for (int v=0; v<anz; v++)
            C.x[v] = -A.x[v];
        
            
        return C; /* success; free workspace, return C */

 }
}
