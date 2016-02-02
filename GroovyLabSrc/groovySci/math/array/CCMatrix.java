
package groovySci.math.array;


import edu.emory.mathcs.csparsej.tdouble.Dcs_common.Dcs;
import groovy.lang.GroovyObjectSupport;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.MatrixEntry;
import no.uib.cipr.matrix.sparse.CompColMatrix;
import no.uib.cipr.matrix.sparse.IterativeSolverNotConvergedException;
import no.uib.cipr.matrix.io.MatrixVectorReader;

public class CCMatrix extends GroovyObjectSupport {
    CompColMatrix  ccm;  // the representation of the sparse matrix
    public int numRows;
    public int numColumns;
    
  
    // construct from an MTJ CompColMatrix
    public CCMatrix(CompColMatrix Ccm) {
        numRows = Ccm.numRows();
        numColumns = Ccm.numColumns();
        ccm = new CompColMatrix(Ccm);
    }
    
    // construct from a matrix reader
    public CCMatrix(MatrixVectorReader r) throws IOException {
        ccm = new CompColMatrix(r);
        numRows = ccm.numRows();
        numColumns = ccm.numColumns();
    }
    
    // reads a matrix from a file
    public CCMatrix(String  fileName) throws FileNotFoundException, IOException {
        BufferedReader rd = new BufferedReader(new FileReader(fileName));
        MatrixVectorReader mr = new MatrixVectorReader(rd);
        ccm = new CompColMatrix(mr);
        numRows = ccm.numRows();
        numColumns = ccm.numColumns();
    }
    
    /* 
     filename = "C:\\matrixData\\t1"
     sp = Sparse.loadSparse(filename)
     csp = CSparseToCCMatrix(sp)
     
     */
    
      /*  
   // load as a CSparse matrix
 s = loadSparse("/home/sp/NBProjects/csparseJ/CSparseJ/matrix/t1")
    s =    loadSparse("L:\\NBProjects\\CSPARSEJ\\CSparseJ\\matrix\\t1")
     
b = vrand(s.Nrows)  // construct a random vector
b.getv()

    
   
   // convert to an MTJ CCMatrix
 ccms =    CSparseToCCMatrix(s)
  */
 
  
    // convert from CSparse to CCMatrix using a DenseMatrix as intermediate
public static CCMatrix   CSparseToCCMatrixDM(Sparse sm)  {
        Dcs  A = sm.csm;   // the CSparse compressed matrix representation
        int  m = A.m;  //  number of rows
        int  n = A.n;  //  number of columns
        DenseMatrix  dm = new no.uib.cipr.matrix.DenseMatrix(m, n); 
        int [] Ap = A.p;  // column pointers (size n+1) 
        int [] Ai = A.i;   // row indices, size nzmax
        double []  Ax = A.x;    // numerical values, size nzmax
        int  row = 0;  
        double  value = 0.0;
        for (int col = 0; col < n; col++) {  // column col
               for (int p =  Ap[col]; p < Ap[col + 1]; p++) {   // indices for storiung column j
                 row = Ai[p];   // row index
                 value = Ax[p]; // value        
                 dm.set(row, col, value);
                    }
        }
   return   new CCMatrix(dm);
      
     }


    // convert from CSparse to CCMatrix
public static CCMatrix   CSparseToCCMatrix(Sparse sm)  {
        Dcs  A = sm.csm;   // the CSparse compressed matrix representation
        int  m = A.m;  //  number of rows
        int  n = A.n;  //  number of columns
        int [] Ap = A.p;  // column pointers (size n+1) 
        int [] Ai = A.i;   // row indices, size nzmax
        double []  Ax = A.x;    // numerical values, size nzmax
        int dataLen = Ax.length;
        int numColumnPointers = Ap.length;
        int numRowIndices = Ai.length;
        double [] data = new double [dataLen];
        int [] columnPointer = new int[numColumnPointers];
        int [] rowIndex = new int[numRowIndices];
        
        
        //  copy column pointers
        for (int cp=0; cp<numColumnPointers; cp++)
            columnPointer[cp] = Ap[cp];
        // copy row indices
        for (int ri=0; ri<numRowIndices; ri++)
            rowIndex[ri] = Ai[ri];
        // copy data
        for (int di=0; di<dataLen; di++)
            data[di] = Ax[di];
        
        CompColMatrix ccm = new CompColMatrix(m, n, columnPointer, rowIndex, data);
        CCMatrix ccmatrix = new CCMatrix(ccm);
        
        return ccmatrix;
      
     }

    // construct a sparse matrix from a dense matrix
    public CCMatrix(DenseMatrix A) {
        ccm = new CompColMatrix(A, true);
    }
    
    // construct a sparse matrix from a double [][] array
    public CCMatrix(double [][]A) {
       this(new DenseMatrix(A));
    }
               
    // perform matrix addition
    public CCMatrix plus(CCMatrix that) {
        CCMatrix result = new CCMatrix(this.ccm);
        Iterator<MatrixEntry>  miter = that.ccm.iterator();
        while (miter.hasNext()) {
            MatrixEntry current = miter.next();
            result.ccm.add(current.row(), current.column(), current.get());
        }
        return result;
    }
    
    // perform addition with a scalar
    public CCMatrix plus(double that) {
        CCMatrix result = new CCMatrix(this.ccm);
        Iterator<MatrixEntry>  miter = this.ccm.iterator();
        while (miter.hasNext()) {
            MatrixEntry current = miter.next();
            int crow = current.row();  int ccol = current.column();
            double celem = this.ccm.get(crow, ccol);
            if (celem != 0.0)
              result.ccm.set(crow, ccol, that+celem);
        }
        return result;
    }
    
    // perform multiplication with a scalar
    public CCMatrix multiply(double that) {
        CCMatrix result = new CCMatrix(this.ccm);
        Iterator<MatrixEntry>  miter = this.ccm.iterator();
        while (miter.hasNext()) {
            MatrixEntry current = miter.next();
            int crow = current.row();  int ccol = current.column();
            double celem = this.ccm.get(crow, ccol);
            result.ccm.set(crow, ccol, that*celem);
        }
        return result;
    }
    
    // perform matrix subtraction
    public CCMatrix minus(CCMatrix that) {
        CCMatrix result = new CCMatrix(this.ccm);
        Iterator<MatrixEntry>  miter = that.ccm.iterator();
        while (miter.hasNext()) {
            MatrixEntry current = miter.next();
            result.ccm.add(current.row(), current.column(), -current.get());
        }
        return result;
    }
    
    // multiply with a GroovySci Vector
    public double []  multiply(Vec that) {
      DenseVector dv = new DenseVector(that.getv());
      
      return  this.ccm.multAdd(dv.getData());
    }
    
    
    // multiply with a double [] array
    public double []  multiply(double []  that) {
      
      return  this.ccm.multAdd(that);
    }
    
    public double getAt(int row, int col) {
        return ccm.get(row, col);
    }

    public void putAt(int row, int col, double value) {
        ccm.set(row, col, value);
    }
    

//  returns the column pointers
   public int[] getColumnPointers() {
        return ccm.getColumnPointers();
    }

    
 //  returns the row indices
    public int[] getRowIndices() {
        return ccm.getRowIndices();
    }

    // returns the internal data storage
    public double [] getData() {
        return ccm.getData();
    }
    
    public DenseVector multAdd(double alpha, DenseVector x, DenseVector y) {
        return  (DenseVector)ccm.multAdd(alpha, x, y);
    }
    
    public double []   multAdd(double alpha, double [] x, double [] y) {
        DenseVector dx = new DenseVector(x);
        DenseVector dy = new DenseVector(y);
        DenseVector result =  (DenseVector)ccm.multAdd(alpha, dx, dy);
        return result.getData();
    }
    
    public DenseVector transMult(DenseVector x, DenseVector y) {
        return (DenseVector) ccm.transMult(x, y);
    }
    
    public double []  transMult(double []  x, double [] y) {
        DenseVector dx = new DenseVector(x);
        DenseVector dy = new DenseVector(y);
        DenseVector result = (DenseVector) ccm.transMult(dx, dy);
        return result.getData();
    }

    
    public DenseVector transMultAdd(double alpha, DenseVector x, DenseVector y) {
        return (DenseVector) ccm.transMultAdd(alpha, x, y);
    }
    
    public double []  transMultAdd(double alpha, double []  x, double [] y) {
        DenseVector dx = new DenseVector(x);
        DenseVector dy = new DenseVector(y);
        DenseVector result = (DenseVector) ccm.transMultAdd(alpha, dx, dy);
        return result.getData();
    }
    
    
    public String toString() {
        return ccm.toString();
    }

    public static double [] BiCGSolve(CCMatrix A, double [] b) {
        DenseVector template = new DenseVector(b);
        no.uib.cipr.matrix.sparse.BiCG BiCCSolver = new no.uib.cipr.matrix.sparse.BiCG(template);
        DenseVector db = new DenseVector(b);
        double [] x = new double[db.size()];
        DenseVector dx = new DenseVector(x);
        DenseVector result = null;
        try {
            result = (DenseVector) BiCCSolver.solve(A.ccm, db, dx);
        } catch (IterativeSolverNotConvergedException ex) {
            System.out.println("Iterative BiCGSolver not converged");
            ex.printStackTrace();
        }
        
        return result.getData();
        
    }
    /*
     


a = [[1, 2, 0], [-1, 0, -2], [-3, -5, 1]] as double [][]

b = [3, -5, -4] as double []
am = new CCMatrix(a)

x = new double [3]

sol = am.BiCGSolve(a, b, x)



      
     * 
     */
}
