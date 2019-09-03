
package groovySci.math.array;

import groovy.lang.GroovyObjectSupport;
import edu.emory.mathcs.csparsej.tdouble.*;
import edu.emory.mathcs.csparsej.tdouble.Dcs_common.Dcs;
import edu.jas.arith.BigDecimal;
import java.text.DecimalFormat;

public class Sparse extends  GroovyObjectSupport {
     public Dcs   tsm;   // the sparse matrix representation in triplet form
     public Dcs   csm; // the sparse matrix representation in compressed form
     
     public int Nrows;
     public int Ncols;
     
     public int numRows() { return Nrows; }
     public int numColumns() { return Ncols; }
     
     public Sparse() {
         tsm = Dcs_util.cs_spalloc(1, 1, 1, true, true);
     }
     /*
   s =    loadSparse("L:\\NBProjects\\CSPARSEJ\\CSparseJ\\matrix\\t1")
   s = loadSparse("/home/sp/NBProjects/csparseJ/CSparseJ/matrix/t1")
 
      */
     
     // returns 1 if matrix is symmetric upper, -1 if symmetric lower, 0 otherwise
    public static int  is_symm(Sparse A)   
    {
        int n = A.Ncols; 
        int m = A.Nrows;
        int [] Ap = A.csm.p;  // column pointers
        int [] Ai = A.csm.i;  // row indices
        if (m!=n) return 0;
        boolean is_upper = true; 
        boolean is_lower = true;
        for (int col=0; col<n; col++)  // column indexes
            for (int p = Ap[col]; p<Ap[col+1]; p++) {
                int row = Ai[p]; // row index
                if (row > col)  is_upper = false;
                if (row < col)  is_lower = false;
            }
    return (is_upper ?  1: (is_lower ? -1: 0));
    }
        
    
     // convert to triplet representation
     public static Dcs  toTriplet(Dcs _csm) {
         return Dcs_CSToTriplet.cs_CSToTriplet(_csm);
     }
     
     // convert to triplet representation, setting element r, c to value
     public static Dcs  toTriplet(Dcs _csm, int r, int c, double [] value) {
         return Dcs_CSToTriplet.cs_CSToTriplet(_csm, r, c, value);
     }
     
      
     // convert to triplet representation
     public static Dcs  toTriplet(Sparse sm) {
         return Dcs_CSToTriplet.cs_CSToTriplet(sm.csm);
     }
     
     // convert to triplet representation, setting element r, c to value
     public static Dcs  toTriplet(Sparse  sm, int r, int c, double [] value) {
         return Dcs_CSToTriplet.cs_CSToTriplet(sm.csm, r, c, value);
     }
    
     public static double [][] SparseToDoubleArray(Sparse sm) {
         return Dcs_toDouble.cs_toDouble(sm.csm);
     }
         
     public static Sparse SparseFromDoubleArray(double [][] a) {
         Dcs dcsa = Dcs_fromDoubleArray.cs_fromDoubleArray(a);
         Sparse sm = new Sparse();
         sm.csm = dcsa;
         sm.Nrows = dcsa.m;
         sm.Ncols = dcsa.n;
         return sm;
     }
     
     
     public static Sparse SparseFromDoubleArray(double [][] a, boolean keepDimension) {
         Dcs dcsa = Dcs_fromDoubleArray.cs_fromDoubleArray(a);
         Sparse sm = new Sparse();
         sm.csm = dcsa;
         dcsa.m = a.length;
         dcsa.n = a[0].length;
         sm.Nrows = dcsa.m;
         sm.Ncols = dcsa.n;
         return sm;
     }
     
     // negation
     public Sparse negative() {
         Sparse ns = new Sparse();
         ns.Nrows = this.Nrows;
         ns.Ncols = this.Ncols;
         ns.csm = Dcs_negate.cs_negate(this.csm);
         return ns;
     }

     // matrix scalar multiplication
     public Sparse  multiply(double alpha) {
         Sparse result = new Sparse();
         result.csm = Dcs_mulScalar.cs_mulScalar(csm, alpha);
         result.Nrows = this.Nrows;
         result.Ncols = this.Ncols;
         return result;
     }
     
     // matrix vector multiplication
     public double [] multiply(double [] x) {
         double [] y = new double[x.length];
         boolean success = Dcs_gaxpy.cs_gaxpy(csm, x, y);
         if (success == false)
             System.out.println("failure in Dcs_gaxpy.cs_gaxpy");
         return y;  
     }
          
     // matrix vector multiplication
     public Vec  multiply(Vec x) {
         return new Vec(multiply(x.getv()));
     }
     
     // multiply sparse matrices
     public Sparse multiply(Sparse s) {
       Sparse result = new Sparse();
       result.csm = Dcs_multiply.cs_multiply(csm, s.csm);
       result.Nrows = result.csm.m;
       result.Ncols = result.csm.n;
       return result;
     }
     
     // return the transpose
     public static Sparse sparse_t(Sparse sm) {
         Sparse tsparse = new Sparse();
         Dcs  tcs = Dcs_transpose.cs_transpose(sm.csm, true) ;
         tsparse.Nrows = sm.Ncols;
         tsparse.Ncols = sm.Nrows;
         tsparse.csm = tcs;
         return tsparse;
     }
     
     // clone the Sparse matrix
     public Sparse clone() {
         Sparse cloneSparse = new Sparse();
         cloneSparse.Nrows = this.Nrows;
         cloneSparse.Ncols = this.Ncols;
         cloneSparse.csm = Dcs_clone.cs_clone(csm);
         return cloneSparse;
     }
     
     public Sparse  map(groovy.lang.Closure c)  {
         Sparse ms = new Sparse();
         ms.Nrows = this.Nrows;
         ms.Ncols = this.Ncols;
         ms.csm =   Dcs_map.cs_map(this.csm, c);
         return ms;
     }
             
     public Sparse plus( Sparse that)  {
       Sparse result = new Sparse();
       result.Nrows = this.Nrows;  result.Ncols = this.Ncols;
       Dcs  sm = Dcs_add.cs_add(this.csm, that.csm, 1.0, 1.0);
       
       result.csm = sm;
       return result;
     }
     
     public Sparse minus( Sparse that)  {
       Sparse result = new Sparse();
       result.Nrows = this.Nrows;  result.Ncols = this.Ncols;
       Dcs  sm = Dcs_add.cs_add(this.csm, that.csm, 1.0, -1.0);
       
       result.csm = sm;
       return result;
     }

     public Sparse plus( double  that)  {
       Sparse result = new Sparse();
       result.Nrows = this.Nrows;  result.Ncols = this.Ncols;
       Dcs  sm = Dcs_addScalar.cs_addScalar(this.csm, that);
       
       result.csm = sm;
       return result;
     }
     
     public Sparse minus( double  that)  {
       Sparse result = new Sparse();
       result.Nrows = this.Nrows;  result.Ncols = this.Ncols;
       Dcs  sm = Dcs_addScalar.cs_addScalar(this.csm, -that);
       
       result.csm = sm;
       return result;
     }
     
     private static int  scanPositives(int [] pi)  {
       int  k = 1;
       int N = pi.length-1;
       while (pi[k] > 0 && k<N)  {
          k++;
       }
       return k;
     }
     
     private static int  max (int [] x) {
         int mx = x[0];
         for (int k=1; k<x.length; k++)
             if (x[k]>mx) mx = x[k];
         return mx;
     }
     
     public static Sparse  loadSparse(String filename) {
       Dcs     dcs  =  Dcs_load.cs_load(filename);
       Sparse  loadedSparse = new Sparse();
       loadedSparse.tsm = dcs;
       loadedSparse.csm = Dcs_compress.cs_compress(dcs);
       
       loadedSparse.Ncols = scanPositives(loadedSparse.csm.p);     // no of columns
       loadedSparse.Nrows = max(dcs.i)+1;  // no of rows
        
       return loadedSparse;
     }
       
     public void putAt(int i, int j, java.math.BigDecimal x) {
         String xs = x.toString();
         double xd = Double.valueOf(xs).doubleValue();
         putAt(i, j, xd);        
         
     }

     public void putAt(int i, int j, double x) {
          if (i >= Nrows)  Nrows = i+1;  
          if (j >= Ncols)  Ncols = j+1;
           // convert the representation to triplet format
         double [] xd = new double[1];
         xd[0] = x;  // wrap double value
         Dcs triplet = toTriplet(csm, i, j, xd);
          // add the new entry
          boolean success;
          if (xd[0] == -1)   // value not already in the sparse matrix, thus add it
           success = Dcs_entry.cs_entry(triplet, i, j, x);
          else
           success = true;   
         if (success == false) {
             System.out.println("Failing to insert element ["+i+","+"j"+", "+x+" ] ");
             return;
         }
         // convert back to compressed matrix format
         csm = Dcs_compress.cs_compress(triplet);
     }
     
     public double  getAt(int i, int j) {
         int columnStart = csm.p[j];  // get column start
         int columnEnd = csm.p[j+1];  // get column end
         // scan to find the row
         
         int rowFound=-1;
         for (int row = columnStart; row < columnEnd; row++) 
             if (csm.i[row] == i)  { rowFound = row;  break; }
         if (rowFound>=0)
             return csm.x[rowFound];
         else
             return -1.0;
     }
       
         
         

 public void  display()   {
   DecimalFormat  digitFormat = gExec.Interpreter.GlobalValues.fmtMatrix;
   int maxCol = this.csm.p.length-1;  // max no of columns
   int maxRow = max(this.csm.i);
    for (int r = 0; r<=maxRow; r++) {
          for (int c= 0; c< maxCol; c++) 
            System.out.print(digitFormat.format(this.getAt(r, c)) + "  ");
          System.out.println("\n");
      }
   }
     
     public void print() {
         boolean brief = false;
         Dcs_print.cs_print(csm,  brief);
          }
     
     public void print(boolean brief) {
         Dcs_print.cs_print(csm,  brief);
          }
     
     public void printCS(boolean brief) {
         Dcs_print.cs_print(csm, brief);
     }
     
     
     public void printTriplet(boolean brief) {
         Dcs_print.cs_print(tsm, brief);
     }
     
     public static int  dropzeros(Sparse A) {
         return Dcs_dropzeros.cs_dropzeros(A.csm);
     }
     
     
     // implement the primary CSparse routines
     
     // adds two sparse matrices, C = alpha*A+beta*B
     public static Sparse  cs_add(Sparse A, Sparse B, double alpha, double beta) {
         Sparse result = new Sparse();
         result.csm = Dcs_add.cs_add(A.csm, B.csm, alpha, beta);
         result.Nrows = A.Nrows;
         result.Ncols = A.Ncols;
         return result;
      }
     
     // solve Ax = b using Cholesky factorization
     // int cs_cholsol( int order, Sparse A, double [] b)
     //    order:  in, ordering method to use (0 or 1)
     //    A:  in, sparse matrix; only upper triangular part used
     //    b:  in/out,  size n, b on input, x on output
    //          returns   true  if successful; false  on error
     public static boolean  cs_cholsol(int order, Sparse B, double [] b) {
        Dcs csm = B.csm;
        boolean  success = Dcs_cholsol.cs_cholsol(order, csm, b);
        return success;
      }
         
     // remove duplicate entries
     // Removes and sums duplicate entries in a sparse matrix
     //  A:   in/out,    sparse matrix; duplicates summed on output
     //        returns:   true  if successful; false on error
     public static boolean  cs_dupl( Sparse A) {
        return Dcs_dupl.cs_dupl(A.csm); 
     }

     // sparse matrix times dense column vector, y = Ax+y
     //    A:   in,   sparse matrix
     //    x:    in,   size n
     //    y:    in/out, size m
     //          returns true if successful, false on error
     public static boolean  cs_gaxpy(Sparse A, double [] x, double [] y) {
         return Dcs_gaxpy.cs_gaxpy(A.csm, x,  y);
     }
     
     // solve Ax=b using LU factorization
     //  solves Ax = b, where A is square and nonsingular
     //    order:  in,   ordering method to use (0 to 3),
     // 0 results in natural orderin, 1 is a minimum degree ordering of A + A^T
     // 2 is a minimum degree ordering of S^T \cdot S where S = A, except rows 
     // with more than 10\sqrt(n) entries are removed
     // and 3 is a minimum degree ordering of A^T \cdot A
     //    A:        in,    sparse matrix
     //    b:         in/out,  size n; b on input, x on output
     //   tol:        in, partial pivoting tolerance
     //               returns,  true if successful, false on error
     public static  boolean cs_lusol(int order, Sparse A, double [] b, double tol) {
         return Dcs_lusol.cs_lusol(order, A.csm, b, tol);
   }

     // a simplified interface
     public static double [] sparseSolve(Sparse A, double [] b)  {
         double [] bc = new double[b.length];
         for (int k=0; k<b.length; k++)
             bc[k] = b[k];
         int order = 0;
         double tol = 0.00001;
         boolean success = cs_lusol(order, A, bc, tol);
         if (success) return bc;
         else return b;
     }

     // matrix 1-norm
    public static double cs_norm(Sparse A) {
        return Dcs_norm.cs_norm(A.csm);
    }
    
    // solve a least squares or underdetermined problem
    // Solves a least squares problem (min||Ax-b||_{2}, where A is m-by-n with m>=n),
    // or an underdetermined system (Ax=b, where m<n)
    //   order: in,  ordering method to use (0 or 3)
    //   A:      in,   sparse matrix
    //   b:       in/out,  size max(m, n); b(size m) on input, x(size n) on output
    //             returns,  true if successful; false on error
    public  static  boolean cs_qrsol(int order, Sparse A, double [] b) {
      return Dcs_qrsol.cs_qrsol(order, A.csm, b);
     }

     
    

}