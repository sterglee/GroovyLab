
// A multithreaded matrix for GroovyLab

// Tt has a lot of operations covering basic mathematical tasks.
// Also, it offers an extensive range of static operations that help to perform conveniently many things.
// Convenient syntax is offered using Groovy's freatures.
// Also, some native optimized C libraries and NVIDIA CUDA support is offered for faster maths.

package groovySci.math.array;

import Jama.CholeskyDecomposition;
import Jama.LUDecomposition;
import Jama.LinearAlgebra;
import Jama.QRDecomposition;
import Jama.jMatrix;


import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;
import edu.emory.mathcs.utils.ConcurrencyUtils;
import gExec.Interpreter.GlobalValues;
import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.IntRange;
import static groovySci.PrintFormatParams.*;
import static groovySci.math.array.NUMALConvArrays.*;
import groovySci.math.io.files.ASCIIFile;
import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import static java.lang.invoke.MethodType.methodType;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.concurrent.Future;
import java.util.function.DoubleUnaryOperator;
import java.util.function.UnaryOperator;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;
import jcuda.jcufft.*;
import no.uib.cipr.matrix.DenseMatrix;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.ejml.data.DenseMatrix64F;
import org.jblas.ComplexDoubleMatrix;
import org.jblas.DoubleMatrix;

import gExec.Interpreter.GlobalValues;

/** Matrix class to provide similar behavior that high level math languages like MATLAB, Scilab, R, ...
	
	
  Following operators are available: 
        Matrix + Matrix,         Matrix + Number,        Matrix - Matrix
        Matrix - Number,         Matrix * Matrix,         Matrix * Number
        Matrix / Matrix,         Matrix / Number,        Matrix ** int"
		
    Following static operations are available:
        sum(Matrix),        prod(Matrix),        cumsum(Matrix)
        cumprod(Matrix),        inverse(Matrix),        inv(Matrix)
        solve(Matrix A, Matrix b)	//returns X Matrix verifying A*X = b. 
        rank(Matrix),        trace(Matrix),         det(Matrix)
        cond(Matrix),        norm1(Matrix),        norm2(Matrix)
        normF(Matrix),        normInf(Matrix),        dot(Matrix A, Matrix b)  // dot product of the two matrices
		
    Following static Linear Algebra (from JAMA) are available:
        Cholesky decomposition:,         CholeskyL(Matrix),        CholeskySPD(Matrix)
        QR decomposition:,        QR_Q(Matrix),        QR_H(Matrix)
        QR_R(Matrix),        LU decomposition:,        LU_L(Matrix)
        LU_U(Matrix),        LU_P(Matrix)

 Singular values decomposition:
        svd_S(Matrix),        svd_U(Matrix),        svd_V(Matrix),        svd_values(Matrix)

 Eigenvalues decomposition:
        eig_D(Matrix),        eig_V(Matrix)

 //  computes the eigenvalues and eigenvectors of a real matrix
        //  the return matrix is as follows:
        //       column 0:    the real parts of the eigenvalues
        //       column 1:    the imaginary parts of the eigenvalues
        //       columns   n..2+n-1   :  the real parts of the eigenvectors
        //       columns   2+n..2+n+n  : the imaginary parts of the eigenvectors
        public static Matrix eig(Matrix M)

	
                    The following static constructors are available:
    Matrix(double[][])
    Matrix(double[])	// one row Matrix constructor
    Matrix(ArrayList)	// compatible with ArrayList of Numbers or ArrayList of ArrayList of Numbers
	
    identity(int n)                  // identity Matrix of size n*n alias to id(int n)
    diagonal(int, double)	// diagonal Matrix of constant values, alias to diag(int, double)
    diagonal(double[])	// diagonal Matrix with given diagonal values, alias to diag(double[])
    one(int, int)		// constant Matrix of given size, filled with 1.0 values 
    ones(int, int)	// constant Matrix of given size, filled with 1.0 values 
    ones(int)		// constant Matrix of given size, filled with 1.0 values 
    zeros(int, int)	// constant Matrix of given size, filled with 0.0 values 
    zeros(int, int)	// constant Matrix of given size, filled with 0.0 values 
    zeros(int)          	// constant Matrix of given size, filled with 0.0 values 
	
    fill(int, int, double)	// constant Matrix of given size, filled with given values 
    increment(int, int, double begin, double pitch) // Matrix of given size with row incrementing values from given beginning value wsith given pitch increment
    increment(int, int, double[] begin, double[] pitch) // Matrix of given size with row incrementing values from given beginning values with given pitchs increment

 Following statistic sample constructors are available (random generator from RngPack):
    random(int, int)								// independent random values (between 0.0 and 1.0) Matrix of given size, alias to rand(int, int)
    random(int, int, double min, double max)		// independent random values (between min and max) Matrix of given size, alias to rand(int, int, double min, double max)
    randomUniform(int m, int n, double min, double max)  
    randomDirac(int m, int n, double[] values, double[] prob)  
    randomNormal(int m, int n, double mu, double sigma)  
    randomChi2(int m, int n, int d)  
    randomLogNormal(int m, int n, double mu, double sigma)  
    randomExponential(int m, int n, double lambda)  
    randomTriangular(int m, int n, double min, double max)  
    randomTriangular(int m, int n, double min, double med, double max)  
    randomBeta(int m, int n, double a, double b)  
    randomCauchy(int m, int n, double mu, double sigma)  
    randomWeibull(int m, int n, double lambda, double c)  
	
Following static sort/find methods are available:
    sort(Matrix)
    sort(Matrix, int columnIndex)
    min(Matrix)
    max(Matrix)
	
Following static transformation methods are available:
    transpose(Matrix)	// alias to t(Matrix)
    resize(Matrix, int, int)
    rowsMatrix >> Matrix	// appends rowsMatrix to Matrix at last position (i.e. add last row)
    columnsMatrix >>> Matrix	// appends columnsMatrix to Matrix at last position (i.e. add last column)
    Matrix << rowsMatrix	// appends rowsMatrix to Matrix at first position (i.e. add first row)
	
 Following static statistic sample methods are available:
    mean(Matrix)
    variance(Matrix)
    covariance(Matrix,Matrix)
    correlation(Matrix,Matrix)
 * 
 * 
*/

public class Mat  extends GroovyObjectSupport  {

    static public int numOfThreads = ConcurrencyUtils.getNumberOfThreads();
    
    private  static Mat    extractedMatrixObject;   // used for cascading calls at indexing using IntRanges
    // e.g. x = rand(30,30)
    // after row extraction, extractedMatrixObject points to the Matrix x[1..2]
    // thus, [3..4] is interpreted as column range instead of row
    // y = x[1..2][3..4]  
    
    // The storage of data as double [][] array. We can manipulate it directly for efficiency.
     public    double[][] d;

     // get the data array by reference
     final public  double [][] getArray() { return d; } 
   
        // gets the first row of the matrix as double [] array 
     final public double [] getv() {
            int ncol = d[0].length; // number of columns
            double [] firstRow =  new double[ncol];
            for (int c=0; c<ncol; c++)
                firstRow[c] = d[0][c];
            
            return firstRow;
        }

final public int numRows()  { return d.length; }
final public int numCols() { return d[0].length; }
final public int numColumns() { return d[0].length; }
final public int Nrows() { return d.length; }
final public int Ncols() { return d[0].length; }
  // keep row and column numbers as as field for further efficiency
final public int fnrows;
final public int fncols;

// returns size as an array of two ints, i.e. int[2]
final public int  [] size() {
    int [] siz = new int[2];
    siz[0] = Nrows();
    siz[1] = Ncols();
    return  siz;
}
	
final public  static int[] size(Mat M) {
    return M.size();
}

// for a single row matrix return the number of columns, for a single column return the number of rows,
// for a general matrix return the number of rows
final public int length() {
 int r = this.size()[0];
 int c = this.size()[1];
 if (r==1) return c;   // the array has one row, return the number of its columns
 if (c==1) return r;  // the array has one column, return the number of its rows
 return r;  // for a general matrix return the number of rows
 }

       // set the reference to double[][] storage of this matrix object to a different array 	
   final  public void setRef(double[][] a) {
        d = a;
    }
      // sets the reference to a row of this matrix to a different row   	
   final  public  void setRowRef(int i, double[] row) {
        d[i-1] = row;
    }

     
    // get  the data array by value
final public double [][] toDoubleArray() { 
    int Nrows = d.length; int Ncols = d[0].length;
    double [][] data = new double[Nrows][Ncols];
    for (int r = 0; r < Nrows; r++)
        for (int c=0; c < Ncols; c++ )
            data[r][c] = d[r][c];
    return data;
}

 
// create a Matrix from values
// first entry is the number of rows, then the number of columns
// then the matrux values follow, e.g. 
/*
 x = Mat(2, 2, //  two rows, two columns
        0.2, 0.5, // first row
        -3.4, 0.5) // second row
*/
public static Mat Mat(double ...values)   {
    int   nrows = (int)values[0];  // number of rows
    int   ncols = (int)values[1];   // number of cols
    int  cpos = 2;  // current position in array
    Mat    sm = new Mat( nrows, ncols);  // create a Mat
    for (int r = 0; r < nrows; r++)
      for (int c = 0; c < ncols; c++)
         {
           sm.d[r][c] = values[cpos];  // copy value
           cpos++;
         }

   return sm;  // return the constructed matrix

  }
 
/* 
Creates a matrix from values. As a row separator null is used, e.g.
   xx = 9.6 ; yy=9.8 
   A = M( 3.4, 3.4, xx, sin(xx),  null,   // new row
             -2.3, -xx, -0.34, xx+3.4*cos(xx+0.3*yy) )
We can synthesize matrices using components matrices, e.g.

x = rand(2,2)
xx = M(x,x, null, x, x)

*/

final static  Mat M( Object ...values)    {
    // count number of nulls, number of nulls will be the number of rows 
    int  nullCnt = 0;
    for (Object value : values) {
            if (value == null) {
                nullCnt++;
            }
        }
    int  rowCnt = nullCnt+1;  // number of rows of the new Matrix
      
    // count number of columns
     int  colCnt = 0;
     int  vl = values.length;
     while (colCnt < vl && values[colCnt] != null)  // null starts a new row
        colCnt++;
        
    // take the first element.
    // It can be either a Matrix or a double number
    int  cpos = 0;
    Object   vv = values[cpos]; 
       
     if (vv instanceof Mat ) { // we synthesize our Matrix from Matrices
           
           // take parameters of the submatrices
         Mat vv0 = (Mat) vv;
           // the number of rows and columns of the submatrices
         int  nrowsSubm = vv0.numRows();
         int  ncolsSubm = vv0.numColumns();
         
     // construct the new Matrix
   Mat  nm = new Mat(rowCnt*nrowsSubm, colCnt*ncolsSubm);
   
   vv = values[cpos]; 
   for (int r=0; r < rowCnt; r++)
     for (int c=0; c < colCnt; c++)
         {
        Mat   cv = (Mat) values[cpos];
        if (cv == null) cpos++;
        cv = (Mat) values[cpos];
              
              if (cv!=null) 
               {
        int  crow = r*nrowsSubm;
        int  ccol = c*ncolsSubm;
               
        for (int  rs=0; rs < nrowsSubm; rs++)
           for (int cs=0; cs < ncolsSubm; cs++)
               nm.d[crow+rs][ccol+cs] = cv.d[rs][cs];
               }
                 
         cpos++;  // next element
         }   
         return nm;
         }
         else {

     // construct the new Matrix
    Mat  nm = new Mat(rowCnt, colCnt);
   
   cpos = 0;
   for (int r=0; r < rowCnt; r++)
     for (int c=0; c < colCnt; c++)
         {
           vv = values[cpos]; 
           if (vv == null) cpos++;
           vv = values[cpos];
             
           Double cv = 0.0;
       if (vv instanceof Integer)      {
            cv = (double)((Integer) vv);
            nm.d[r][c] =  cv;
       }
       else if (vv instanceof Double) {
           cv = (Double) vv;
           nm.d[r][c] =  cv;
       }
                    
      cpos++;
      }
         
     return nm;
     }
    }
    
  
// clone the Matrix object, e.g.
//  x = rand(2,3); xc = x.clone()
  final public Mat  clone()  {
    return new Mat(this.d, false);  // return a new Matrix object copying data contents
}  
  
  // copy the Matrix object, e.g.
//  x = rand(2,3); xc = x.copy()
  final public  Mat copy()  {  // same as clone()
    return clone();
  }
  
        
public Mat(double[][] _a, boolean isRef) {
     if (isRef)
        d=_a;
    else 
d=DoubleArray.copy(_a);
fnrows = d.length;
fncols = d[0].length;
}
		
public  Mat(double[][] _a) {
   d= DoubleArray.copy(_a);
    fnrows = d.length;
    fncols = d[0].length;
}


public Mat(int n, int m)  {  // creates a zero Matrix 
    d = DoubleArray.zero(n, m);

    fnrows = d.length;
    fncols = d[0].length;   
}

public Mat(int n, int m, double v)  {  // creates a Matrix filled with the value
    d = DoubleArray.fill(n, m, v);
    fnrows = d.length;
    fncols = d[0].length;
}

public Mat(Mat m) {    // matrix to copy
 d = DoubleArray.copy(m.d);   
 
    fnrows = d.length;
    fncols = d[0].length;
}

// inVec elements are zero-indexed arrays of doubles
public  Mat(int vecDim, Vector  inVec)  {
    int npoints = inVec.size();  // dimension of vector elements
    double [][] values = new double[vecDim][npoints];
    for (int xv=0; xv<npoints; xv++)   
        for (int yv=0; yv<vecDim; yv++)
            values[yv][xv] =( (double [])  (inVec.elementAt(xv)))[yv];
    
    d = values;    
    fnrows = vecDim;
    fncols = npoints;
}

// construct a Matrix to keep a Vector of zero-indexed point coordinates. Each column of the Matrix keeps a point
public  Mat(Vector  inVec)  {
    int npoints = inVec.size();
    Object  vecElem = inVec.get(0);  // get a vector element in order to find its dimension
    int vecDim = ((double [])vecElem).length;
    double [][] values = new double[vecDim][npoints];
    for (int xv=0; xv<npoints; xv++)   
        for (int yv=0; yv<vecDim; yv++)
            values[yv][xv] =( (double [])  (inVec.elementAt(xv)))[yv];
    
    d = values;    
    
    fnrows = d.length;
    fncols = d[0].length;
}


public  Mat(int vecDim, Vector  xOut, Vector yOut)  {
    int npoints = xOut.size();
    double [][] values = new double[vecDim+1][npoints];
    for (int xv=0; xv<npoints; xv++)   {
        values[0][xv] = ( (double[] ) xOut.elementAt(xv))[0];
        for (int yv=0; yv<vecDim; yv++)
            values[yv+1][xv] =( (double [])  (yOut.elementAt(xv)))[yv+1];
    }
        
    d = values;    
    fnrows = d.length;
    fncols = d[0].length;
    
}

public Mat(double [] _a,  boolean isRef)  {
     d = new double[1][1];
     if (isRef) {
         d[0] = _a;
     }
     else
         d[0] = DoubleArray.copy(_a);
     
    fnrows = d.length;
    fncols = d[0].length;
}


public Mat(double [] _a)  {
    d = new double[1][1]; 
    d[0] = DoubleArray.copy(_a);
    
    fnrows = d.length;
    fncols = d[0].length;
}

public Mat(groovySci.math.array.Vec _a)  {
    d = new double[1][1]; 
    d[0] = DoubleArray.copy(_a.getv());
    
    fnrows = d.length;
    fncols = d[0].length;
}

// resamples the Matrix every n rows and every m cols
final public Mat resample(int n, int m) {
    double [][] matArr = getArray();
    int rows = matArr.length;
    int cols = matArr[0].length;
    int rRows =  (int)(rows/n);
    int rCols = (int) (cols/m);
    double [][] newMatArr = new double[rRows][rCols];
    for (int r=0; r<rRows; r++)
        for (int c=0; c<rCols; c++)
            newMatArr[r][c] = matArr[r*n][m*c];
    
    Mat resampledMat = new Mat(newMatArr);
    return resampledMat;
}

final public Mat resample( Mat M, int m, int n) {
    return M.resample(n, m);
}

  public Mat (double vals[], int m) {
      int n = (m != 0 ? vals.length/m : 0);
      if (m*n != vals.length) {
         throw new IllegalArgumentException("Array length must be a multiple of m.");
      }
      d = new double[m][n];
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            d[i][j] = vals[i+j*m];
         }
      }
      
    fnrows = d.length;
    fncols = d[0].length;
   }

// construct a Matrix from a double []  array
  public Mat Matrix(double[] _a) { 
    return new Mat(_a, false);
}
	
// construct a Matrix from a double value        
public Mat(double _a) {
    d = new double [1][1];
    d[0][0]=_a;
    
    
    fnrows = d.length;
    fncols = d[0].length;
}
	
public  Mat Matrix(double _a) {
 return new Mat(_a);
}



// construct a Matrix from an ArrayList
// e.g. 
/*
 x = [[8,9.7,7], [6.7, 7.8, 9.9]] as ArrayList
 1xy = new Matrix(x)
 * */
public  Mat(ArrayList _al) {
    if (_al.get(0) instanceof ArrayList) {  // two dimensional array
        ArrayList  arObj = (ArrayList) _al.get(0);
        d =new double[_al.size()][arObj.size()];
        for (int i=0; i <_al.size(); i++)   {
            ArrayList  arObjRow = (ArrayList) _al.get(i);
            for ( int j = 0; j < arObjRow.size(); j++)  
	set(i, j,  (Number) arObjRow.get(j));
              }
    } else {
    d= new double[1][_al.size()];
    for (int i=0; i < _al.size(); i++) {
    Number  arObjRow = (Number) _al.get(i);
    set(1, i, arObjRow);
    }	
 }
    
    fnrows = d.length;
    fncols = d[0].length;
}
	
public static Mat read(File f)  {
    return new Mat(ASCIIFile.readDoubleArray(f) );
}
	
public static Mat read(String filename)  {
    return read(new File(filename));
}
	
public static void write(File f, Mat M)  {
    ASCIIFile.writeDoubleArray(f, M.d);
}
	
public static void write(String filename,Mat M)  {
    write(new File(filename), M);
}

final public double get(int i, int j) {
   return  d[i][j];
}
	
final public double[][] getRef() {
   return d;
}
	
final  public  double[][] getColumnCopy(int j) {
    return DoubleArray.getColumnsCopy(d,j-1);
}
	
final  public double[] getColumnCopyasRow(int j) {
 return DoubleArray.getColumnCopy(d,j-1);
}
	
final  public  double[] getRowRef(int i) {
    return d[i-1];
}
	
final  public int getRowsNumber() {
    return d.length;
}
	
final  public int getColumnsNumber() {
    return d[0].length;
}
	
final  public void set(int i, int j, Number v) {
    d[i][j] = v.doubleValue();
}
	
final public void set(int i, int j, Integer v) {
    d[i-1][j-1] = (double)v.intValue();
}
	
// resize the array (i.e. shrink or grow it)
final  public void resize( int m, int n) {
    d = DoubleArray.resize(d, m, n);
}

	
public  String toString() {
    
    String mstr = "";
    //if (groovySci.PrintFormatParams.verboseFlag==true)
      //  mstr += "Matrix["+Nrows()+","+Ncols()+"] =\n";
    return mstr+printArray(d);
}

public String toString(String format) {
    String mstr = "Matrix["+Nrows()+","+Ncols()+"] =\n";
    return mstr+printArray(d);  //DoubleArray.toString(format,  d);
}

public String print() {
    String mstr = "Matrix["+Nrows()+","+Ncols()+"] =\n";
    return mstr+printArray(d); // DoubleArray.print(d);
}

// appends the String s to the String representation of the matrix returned with toString()
public String  plus(String s) {
    return DoubleArray.toString(d) + s;
}

// this method is used to overload the indexing operator e.g.,
// A = rand(8, 9);   a23 = A[2,3]
final public double   getAt(int row, int col)  { 
    return d[row][col];
}

// indexing is performed using either integer indices or ranges
final public Object  getAt(java.util.List<Object>  rc) {
    if (rc.get(0) instanceof  Integer) // dispatches the usual case of indexing, e.g. A[2,4]
        return d[(int)rc.get(0)][(int)rc.get(1)];
    else { //  ranges are specified. 
        
    groovy.lang.IntRange rc0 =(groovy.lang.IntRange)rc.get(0);
    groovy.lang.IntRange rc1 =(groovy.lang.IntRange)rc.get(1);
    if (rc0 instanceof  IntRangeWithStep && rc1 instanceof IntRangeWithStep)
         // check first for the subclass IntRangeWithStep of IntRange 
       return (Mat) getAt((IntRangeWithStep)rc0, (IntRangeWithStep)rc1);
    else 
    if (rc0 instanceof  groovy.lang.IntRange && rc1 instanceof IntRangeWithStep)
       return (Mat) getAt((groovy.lang.IntRange)rc0, (IntRangeWithStep)rc1);
    else
    if (rc0 instanceof   IntRangeWithStep && rc1 instanceof groovy.lang.IntRange)
      return (Mat) getAt((IntRangeWithStep)rc0, (groovy.lang.IntRange)rc1);
    else
      return (Mat) getAt((groovy.lang.IntRange)rc0, (groovy.lang.IntRange)rc1);

 }
}


/*     get a submatrix consisting of the rows within the rr range, e.g.
     A = rand(8, 9);   a25 = A[2..5]
 the routine also is used to cascade calls, using the extractedMatrixObject static variable, e.g. for 
     a_rc = A[2..5][1..3]
   getAt() is called two times:
      *   the first time extracts rows 2 to 5 and sets extractedMatrix object to that extracted Matrix object
      *   the second time the receiver of the getAt() call becomes the extractedMatrix object
           and a column select of colymns 1 to 3 is performed
*/
final public Mat  getAt(groovy.lang.IntRange rcr) {
     if (extractedMatrixObject == this ) { // column select 
         // here the receiver of the call is the same as the matrix subrange extracted from the row subrange extract
         extractedMatrixObject =  gc(rcr.getFromInt(), rcr.getToInt());
         return extractedMatrixObject;
     }
     else {
     extractedMatrixObject = gr(rcr.getFromInt(), rcr.getToInt());
     return extractedMatrixObject;
    } 
}


// get a submatrix consisting of the rows within the rr range 
/* e.g. 
x = rand(20,20)
y = x[(1..10).by(2)]
*/
final public Mat  getAt(IntRangeWithStep  rr) {
    if (extractedMatrixObject == this ) { // column  select 
         extractedMatrixObject =  grc(0, 1, this.Nrows()-1, rr.getFromInt(), rr.mby, rr.getToInt());
         return extractedMatrixObject;
     }
     else {
     extractedMatrixObject =  grc( rr.getFromInt(), rr.mby, rr.getToInt(), 0, 1, this.Ncols()-1);
     return extractedMatrixObject;
    } 
    
}


// get a submatrix with row range rr and column range cr
/* e.g.
 x = rand(20,20)
 y = x[1..2, 3..4]
 */


final public Mat  getAt(groovy.lang.IntRange rr, groovy.lang.IntRange cr) {
    if (rr.getFromInt() < 0)  // negative first range means to extract the whole column range
     return grc(0, 1, this.Nrows()-1, cr.getFromInt(), 1, cr.getToInt());

   return grc(rr, cr);  
}

// get a submatrix with row range rr and column range cr
/*
 x = rand(20,20)
 y = x[(1..10).by(2), 1..2]
 */
final public Mat  getAt(IntRangeWithStep  rr, groovy.lang.IntRange cr) {
    if (rr.getFromInt() < 0)  // negative first range means to extract the whole column range
     return grc(0, 1, this.Nrows()-1, cr.getFromInt(), 1, cr.getToInt());

   return grc(rr.getFromInt(), rr.mby, rr.getToInt(), cr.getFromInt(), 1, cr.getToInt());  
}

/* e.g.
 x = rand(20,20)
 y = x[1..3, (1..10).by(4)]
 */
final public Mat  getAt(groovy.lang.IntRange  rr, IntRangeWithStep  cr) {
 
    if (rr.getFromInt() < 0)  // negative first range means to extract the whole column range
     return grc(0, 1, this.Nrows()-1, cr.getFromInt(), cr.mby, cr.getToInt());

   return grc(rr.getFromInt(), 1, rr.getToInt(), cr.getFromInt(), cr.mby, cr.getToInt());  
}

/* e.g.
 x = rand(20,20)
 y = x[(1..16).by(3), (1..10).by(4)]
 */
final public Mat  getAt(IntRangeWithStep   rr, IntRangeWithStep  cr) {
 
    if (rr.getFromInt() < 0)  // negative first range means to extract the whole column range
     return grc(0, 1, this.Nrows()-1, cr.getFromInt(), cr.mby, cr.getToInt());

   return grc(rr.getFromInt(), rr.mby, rr.getToInt(), cr.getFromInt(), cr.mby, cr.getToInt());  
}

public double [] getAt(int r) {
    return d[r];
}

// assign to a submatrix consisting of the rows within the rr range the value value 
public void   putAt(groovy.lang.IntRange rr, double value) {
     sr(rr, value);  
}


 public void   putAt(IntRangeWithStep  rr, double value) {
     src(rr.getFromInt(),  rr.mby,  rr.getToInt(), 0, 1, numCols()-1, value);  
}


private  void putAtWithRange(java.util.ArrayList <groovy.lang.IntRange>  paramList,  double value)  {
         groovy.lang.IntRange  rowR = paramList.get(0);
         groovy.lang.IntRange  colR = paramList.get(1);
         if (rowR.getFrom()<0)
             sc(colR, value);
         else if (colR.getFrom()<0)
             sr(rowR, value);
                     else
         src(rowR, colR, value);

        }


// since type erasure forbids to distinguise lists of Integers from lists of
// IntRanges we use the following routine for handling lists of Integers
private  void putAtWithInt(Integer a1, Integer a2, double value) {
    d[a1][a2] = value;
}

final public void  putAt(java.util.List<Object>  rc, double value) {
    if (rc.get(0) instanceof  Integer)
         d[(int)rc.get(0)][(int)rc.get(1)] = value;
    else {
        
    Object a1 = (IntRange) rc.get(0);
    Object a2 = (IntRange) rc.get(1);

    groovy.lang.IntRange  rowR = (groovy.lang.IntRange) rc.get(0);
    groovy.lang.IntRange  colR = (groovy.lang.IntRange) rc.get(1);
    if (rowR instanceof  IntRangeWithStep && colR instanceof IntRangeWithStep)  {
        putAt( (IntRangeWithStep)rowR, (IntRangeWithStep) colR, value);
        return;
    }
    else 
    if (rowR instanceof  IntRangeWithStep && colR instanceof groovy.lang.IntRange)  {
        putAt((IntRangeWithStep)rowR, (groovy.lang.IntRange) colR, value);
        return;
    }
    else 
    if (rowR instanceof  groovy.lang.IntRange && colR instanceof IntRangeWithStep)  {
        putAt((groovy.lang.IntRange)rowR, (IntRangeWithStep) colR, value);
        return;
    }
    else 
       if(a1 instanceof IntRange && a2 instanceof IntRange) {
         putAt((groovy.lang.IntRange)rowR, (IntRange) colR, value);
         return;
    }
  }  // else
}


/* e,g,
 x = rand(12, 15)
 x[2..3, 1..2] = 12.22  // subrange 2 to 3 tows, 1 to 2 cols to 12.22
 x[-1..-1, 3..5] = 35  // cols  3 to 5  to 35
 x[0..1, -1..-1] = -11.11 // rows 0 to 1 to -11.11

 */

public void putAt(ArrayList paramList,  double value)  {
    if (paramList.get(0) instanceof  Integer) {
         d[(int)paramList.get(0)][(int)paramList.get(1)] = value;
         return;
    }
    
    Object a1 = paramList.get(0);
    Object a2 = paramList.get(1);

    groovy.lang.IntRange  rowR = (groovy.lang.IntRange) paramList.get(0);
    groovy.lang.IntRange  colR = (groovy.lang.IntRange) paramList.get(1);
    if (rowR instanceof  IntRangeWithStep && colR instanceof IntRangeWithStep)  {
        putAt( (IntRangeWithStep)rowR, (IntRangeWithStep) colR, value);
        return;
    }
    else 
    if (rowR instanceof  IntRangeWithStep && colR instanceof groovy.lang.IntRange)  {
        putAt((IntRangeWithStep)rowR, (groovy.lang.IntRange) colR, value);
        return;
    }
    else 
    if (rowR instanceof  groovy.lang.IntRange && colR instanceof IntRangeWithStep)  {
        putAt((groovy.lang.IntRange)rowR, (IntRangeWithStep) colR, value);
        return;
    }
    else 
       if(a1 instanceof IntRange && a2 instanceof IntRange) {
         putAtWithRange(paramList, value);
         return;
    }

    if(a1 instanceof Integer && a2 instanceof Integer) {
        putAtWithInt((Integer)a1, (Integer)a2, value);
        return;
    }

    
}
        


/* assign to a submatrix with row range rr and column range cr the value value,  e.g.   
x = rand(40, 34)
x.putAt(1..3, 0..2, 0.034)
*/
public void  putAt(groovy.lang.IntRange rr, groovy.lang.IntRange cr, double value) {
    if (rr.getFromInt() < 0)  // negative first range means to extract the whole column range
    {
        sc(cr, value);
        return;
    }
    src(rr, cr, value);
}


/* assign to a submatrix with row range rr and column range cr the value value,  e.g.   
x = rand(40, 34)
x.putAt((1..30).by(3), 0..2, -10.034)
*/
public void  putAt(IntRangeWithStep  rr, groovy.lang.IntRange cr, double value) {
    if (rr.getFromInt() < 0)  // negative first range means to extract the whole column range
    {
        sc(cr, value);
        return;
    }
    src(rr.getFromInt(), rr.mby, rr.getToInt(), cr.getFromInt(), 1, cr.getToInt(), value);
}


/* assign to a submatrix with row range rr and column range cr the value value,  e.g.   
x = rand(30, 34)
x.putAt(1..5, (0..30).by(4),  -55.034)
*/
public void  putAt( groovy.lang.IntRange rr, IntRangeWithStep cr, double value) {
    if (rr.getFromInt() < 0)  // negative first range means to extract the whole column range
    {
        sc(cr, value);
        return;
    }
    src(rr.getFromInt(), 1, rr.getToInt(), cr.getFromInt(), cr.mby, cr.getToInt(), value);
}


/* assign to a submatrix with row range rr and column range cr the value value,  e.g.   
x = rand(40, 44)
x.putAt((1..30).by(2), (0..30).by(4),  335.034)
*/
public void  putAt( IntRangeWithStep  rr, IntRangeWithStep cr, double value) {
    if (rr.getFromInt() < 0)  // negative first range means to extract the whole column range
    {
        sc(cr, value);
        return;
    }
    src(rr.getFromInt(), rr.mby, rr.getToInt(), cr.getFromInt(), cr.mby, cr.getToInt(), value);
}


// column select 
 final public Mat  gc( int colL,  int inc, int  colH)   {
     return grc( 0, 1, this.Nrows()-1, colL, inc, colH);
 }
 

// row select 
 final public Mat  gr( int rowL,  int inc, int  rowH)   {
     return grc(rowL, inc, rowH, 0, 1, this.Ncols()-1);
 }
 
 /* extract the columns specified with indices specified with  the array colIndices.
 The new matrix is formed by using all the rows of the original matrix 
 but with using only the specified columns.
 The columns at the new matrix are arranged in the order specified with the array colIndices
 e.g. 
  testMat = M(" 1.0 2.0 3.0 4.0; 5.0 6.0 7.0 8.0; 9 10 11 12")
  colIndices  = [3, 1] as int []
   extract3_1cols = testMat.gc( colIndices)
   */
 
  final public Mat gc( int [] colIndices)  {
    int  lv = colIndices.length;
    if (lv > Ncols())  // do nothing
      {
        System.out.println("array indices length = "+lv+" is greater than the number of columns of the matrix = "+Ncols());
        return this;
      }
      else {  // dimension of array with column indices to use is correct
      // allocate array
      Mat   colFiltered = new Mat(Nrows(), lv);
      for (int col = 0; col < lv; col++)  {
           int   currentColumn = colIndices[col];  // the specified column
           for (int row = 0; row < Nrows(); row++)  // copy the corresponding row
               colFiltered.d[row][col] =   this.d[row][currentColumn];
       }  
    
      return colFiltered;    // return the column filtered array
    } // dimension of array with column indices to use is correct
  }
  

  /* extract the rows specified with indices specified with  the array rowIndices.
 The new matrix is formed by using all the columns of the original matrix 
 but with using only the specified rows.
 The rows at the new matrix are arranged in the order specified with the array rowIndices
 e.g. 
 testMat = M(" 1.0 2.0 3.0 4.0; 5.0 6.0 7.0 8.0; 9 10 11 12; 13 14 15 16; 17 18 19 20")
 rowIndices = [3, 1] as int []
 extract3_1rows = testMat.gr(rowIndices)
   */
  final public Mat gr(int [] rowIndices)  {
    int  lv = rowIndices.length;
    if (lv > Nrows())  // do nothing
      {
        System.out.println("array indices length = "+lv+" is greater than the number of rows of the matrix = "+Nrows());
        return this;
      }  
      else {  // dimension of array with column indices to use is correct
      // allocate array
      Mat   rowFiltered = new Mat(lv,  Ncols());
      for (int row = 0; row <  lv; row++)  {
           int   currentRow = rowIndices[row];  // the specified row
           for (int col = 0; col < Ncols(); col++)  // copy the corresponding row
               rowFiltered.d[row][col] = this.d[currentRow][col];
       }  
    
      return rowFiltered;    // return the column filtered array
   } // dimension of array with column indices to use is correct
  }
  

  
/* extract the columns specified with true values at the array  colIndices.
 The new matrix is formed by using all the rows of the original matrix 
 but with using only the specified columns.
 e.g. 
 testMat = M(" 1.0 2.0 3.0 4.0; 5.0 6.0 7.0 8.0; 9 10 11 12")
 colIndices = [true, false, true, false] as boolean []
 extract0_2cols = testMat.gc(colIndices)
   */
  final public Mat gc(boolean [] colIndices)   {   
    int  lv = colIndices.length; 
    if (lv != Ncols())  // do nothing
      {
        System.out.println("array indices length = "+lv+" is not the number of columns of the matrix = "+Ncols());
        return this;
      }
      else {  // dimension of array with column indices to use is correct
        // count the number of trues
        int  ntrues = 0;
        for (int k=0; k<Ncols(); k++)
          if (colIndices[k]==true)  
            ntrues++;
        
      // allocate array
      Mat  colFiltered = new Mat(Nrows(), ntrues);
      int   currentColumn=0;
      for (int col = 0; col<Ncols(); col++)  {
         if (colIndices[col])   { // copy the corresponding column
             for (int row=0; row<Nrows(); row++) 
               colFiltered.d[row][currentColumn] =  this.d[row][col];
             currentColumn++;
         }  // copy the corresponding column
      }        
    
      return colFiltered;    // return the column filtered array
      } // dimension of array with column indices to use is correct
  }
  
  
    
/* extract the rows specified with true values at the array rowIndices.
 The new matrix is formed by using all the columns of the original matrix 
 but with using only the specified rows.
 e.g. 
 testMat = M(" 1.0 2.0 3.0 ; 5.0 6.0 7.0 ; 8 9 10 ; 11 12 13")
 rowIndices = [false, true, false, true] as boolean[] 
 extract1_3rows = testMat.gr(rowIndices)
   */
  
  final  public Mat gr(boolean [] rowIndices) {
    int   lv = rowIndices.length;
    if (lv != Nrows())  // do nothing
      {
        System.out.println("array indices length = "+lv+" is not the number of rows of the matrix = "+Nrows());
        return this;
      }
      else {  // dimension of array with row indices to use is correct
        // count the number of trues
        int  ntrues = 0;
        for (int k = 0; k < Nrows(); k++)
          if (rowIndices[k])  
            ntrues++;
        
      // allocate array
       Mat  rowFiltered = new Mat(ntrues, Ncols());
       int currentRow=0;
       for (int row = 0; row < Nrows(); row++) 
          if (rowIndices[row])  {  // copy the corresponding row
            for (int col = 0; col < Ncols(); col++)
               rowFiltered.d[currentRow][col] =  this.d[row][col];
               currentRow++;
          }
        return rowFiltered;
      }  // dimension of array with row indices to use is correct 
          
    }
    
  
 final public Mat  gr( int rowL,  int  rowH)  {
     int Nrows = d.length; int Ncols = d[0].length;
     int  rowStart = rowL;  int  rowEnd = rowH;
     int  colStart = 0;   int  colEnd =  Ncols - 1;   // all columns
     int  colNum = Ncols;
     int  colInc = 1;
     

if (rowStart <= rowEnd) {   // positive increment
    int  rowInc = 1;
    if (rowEnd == -1) { rowEnd = Nrows-1; }  // if -1 is specified take all the rows
    int  rowNum = rowEnd-rowStart+1;
    Mat  subMatr = new Mat(rowNum, colNum);   // create a Mat to keep the extracted range
      // fill the created matrix with values
    int  crow = rowStart;  // indexes current row
    int  ccol = colStart;
    int rowIdx =0;  int  colIdx = 0;  // indexes at the new Matrix
    while  ( crow <= rowEnd )   {
          ccol = colStart;  colIdx = 0;
          while  (ccol <= colEnd )   { 
                subMatr.d[rowIdx][colIdx] = d[crow][ccol];
                colIdx++;
                ccol += colInc;
               }
            rowIdx++;
            crow += rowInc;
       } // crow <= rowEnd
return subMatr;   // return the submatrix

} // rowStart <= rowEnd
else { // rowStart > rowEnd
    int  rowInc = -1;
    int  rowNum = rowStart-rowEnd+1;
    Mat  subMatr = new Mat(rowNum, colNum);   // create a Mat to keep the extracted range
      // fill the created matrix with values
    int  crow = rowStart;  // indexes current row at the source matrix
    int  ccol = colStart;
    int  rowIdx =0;  int  colIdx = 0;  // indexes at the new Mat
    while  ( crow >= rowEnd )   {
          ccol = colStart;  colIdx = 0;
          while  (ccol <= colEnd)   {
                subMatr.d[rowIdx][colIdx] = d[crow][ccol];
                colIdx++;
                ccol += colInc;
               }
            rowIdx++;
            crow += rowInc;
       }

return subMatr;   // return the submatrix

} // rowStart > rowEnd

}

 
// extracts a submatrix specifying rows only, take all columns, e.g. m.gr(2, 3) corresponds to Matlab's m(2:3, :)'
 final public double [][]  grs( int rowL,  int  rowH)  {
     
     
    int  rowNum = rowH-rowL+1;

    double[][]dr = new double[rowNum][];
    
    int cnt=0;
    for (int r = rowL; r<= rowH; r++)
    {
  dr[cnt] =  DoubleStream.of(d[r]).toArray();
  cnt++;
    }
return dr;   // return the submatrix


}

 final public double [][]  grsp( int rowL,  int  rowH)  {
     
     
    int  rowNum = rowH-rowL+1;

    double[][]dr = new double[rowNum][];
       
    int cnt=0;
    for (int r = rowL; r<= rowH; r++)
    {
  dr[cnt] =  DoubleStream.of(d[r]).parallel().toArray();
  cnt++;
    }
return dr;   // return the submatrix


}

 
// extracts a submatrix, e.g. m.gc( 2,  12 ) corresponds to Matlab's m(:, 2:12)'
 final public Mat  gc(int colLow, int  colHigh)  {
     int Nrows = d.length; int Ncols = d[0].length;
     int rowStart = 0;     int rowEnd =  Nrows-1;   // all rows
     int colStart = colLow;  int  colEnd = colHigh;
     int rowInc = 1;
     int colInc = 1;
     int rowNum = Nrows;    // take all the rows

    if  (colStart <= colEnd)   {    // positive increment
        if (colEnd == -1)  { colEnd = Ncols-1; } // if -1 is specified take all the columns
        int colNum = colEnd-colStart+1;
        Mat  subMatr = new Mat(rowNum, colNum);   // create a Matrix to keep the extracted range
      // fill the created matrix with values
    int  crow = rowStart;  // indexes current row
    int  ccol = colStart;  // indexes current column
    int  rowIdx = 0;  int colIdx = 0;  // indexes at the new Matrix

           while  ( crow <= rowEnd )   {
          ccol = colStart;  colIdx = 0;
          while  (ccol <= colEnd)   {
                subMatr.d[rowIdx][colIdx] = d[crow][ccol];
                colIdx++;
                ccol += colInc;
               }
            rowIdx += rowInc;
            crow += rowInc;
     } // crow <= rowEnd
 return subMatr;
} // positive increment
  else {  // negative increment
    int  colNum = colEnd-colStart+1;
    Mat  subMatr = new Mat(rowNum, colNum);   // create a Matrix to keep the extracted range
      // fill the created matrix with values
    int  crow = rowStart;  // indexes current row
    int  ccol = colStart;  // indexes current column
    int  rowIdx = 0;  int  colIdx = 0;  // indexes at the new Matrix

           while  ( crow <= rowEnd )   {
          ccol = colStart;  colIdx = 0;
          while  (ccol >= colEnd)   {
                subMatr.d[rowIdx][colIdx] = d[crow][ccol];
                colIdx++;
                ccol += colInc;
               }
            rowIdx++;
            crow += rowInc;
     } // crow <= rowEnd
 return subMatr;   // return the submatrix
   }
   }



      /*  return rows according to the predicate e.g. 
     rdd = rand(20, 30)
     pred  = { int k ->  if (k % 2 == 0) true else false }
     evenRdd = rdd.filterRows(pred)
   */
  final public Mat   filterRows( Closure predicate)  {
      int  rowCnt = 0;
      for (int r = 0; r <  this.numRows(); r++) {
          if ((Boolean)predicate.call(r) == true)
           rowCnt++;
     }
 
    Mat   newMat = new Mat(rowCnt, this.numColumns());
    int  rCnt = 0;
    for (int r = 0; r < this.numRows(); r++)  {
      if ((Boolean) predicate.call(r) == true) {  // copy the row
      for (int c = 0; c < this.numColumns(); c++)
          newMat.d[rCnt][c] =  this.d[r][c];
          rCnt++;
                    }
     }
                    
    return newMat;
  
  }		 

  	 


  
  
  // return cols according to the predicate
 final public Mat   filterColumns( Closure predicate)  {
    int  colCnt = 0;
    for (int c = 0; c < this.numColumns(); c++) {
      if ((Boolean)predicate.call(c) ==  true)
       colCnt++;
  }
  int  cCnt = 0;
  Mat   newMat = new Mat(this.numRows(), colCnt);
    for (int c = 0; c < this.numColumns(); c++) {
      if ((Boolean)predicate.call(c) == true )  {  // copy the column
      for (int r = 0; r < this.numRows(); r++) 
        newMat.d[r][cCnt] =  this.d[r][c];
      cCnt++;
      }
    }
      return newMat;
  
   }		 

 // extracts a submatrix, e.g. m.grc( 2,  12, 4,   8 )  corresponds to Matlab's m(2:12, 4:8)'
final public  Mat  grc(int rowLow,  int rowHigh, int  colLow,  int  colHigh)  {
     int  Nrows = d.length; int Ncols = d[0].length;
     int  rowStart = rowLow;     int  rowEnd =  rowHigh;
     int  colStart = colLow;    int  colEnd = colHigh;
     int  rowInc = 1;
     if  (rowHigh < rowLow) rowInc = -1;
     int colInc = 1;
     if (colHigh < colLow) colInc = -1;

        int  rowNum = (int)Math.floor((rowEnd-rowStart) / rowInc)+1;
        int  colNum = (int) Math.floor( (colEnd-colStart) / colInc)+1;
        Mat  subMatr = new Mat(rowNum, colNum);   // create a Matrix to keep the extracted range

    if  (rowStart <= rowEnd && colStart <= colEnd)   {    // positive increment at rows and columns
        int  crow = rowStart;  // indexes current row
        int  ccol = colStart;  // indexes current column
        int  rowIdx = 0;  int  colIdx = 0;  // indexes at the new Matrix
            while  ( crow <= rowEnd )   {
          ccol = colStart;  colIdx = 0;
          while  (ccol <= colEnd)   {
                subMatr.d[rowIdx][colIdx] = d[crow][ccol];
                colIdx++;
                ccol += colInc;
               }
            rowIdx++;
            crow += rowInc;
     } // crow <= rowEnd
  return subMatr;
} // positive increment
  else if  (rowStart >= rowEnd && colStart <= colEnd)   {
      // fill the created matrix with values
    int  crow = rowStart;  // indexes current row
    int  ccol = colStart;  // indexes current column
    int  rowIdx = 0;  int colIdx = 0;  // indexes at the new Matrix

           while  ( crow >= rowEnd )   {
          ccol = colStart;  colIdx = 0;
          while  (ccol <= colEnd)   {
                subMatr.d[rowIdx][colIdx] = d[crow][ccol];
                colIdx++;
                ccol += colInc;
               }
            rowIdx++;
            crow += rowInc;
     } // crow <= rowEnd
 return subMatr;   // return the submatrix
   }
else if  (rowStart <= rowEnd && colStart >= colEnd)   {
      // fill the created matrix with values
    int  crow = rowStart;  // indexes current row
    int  ccol = colStart;  // indexes current column
    int  rowIdx = 0; int  colIdx = 0;  // indexes at the new Matrix

           while  ( crow <= rowEnd )   {
          ccol = colStart;  colIdx = 0;
          while  (ccol >= colEnd)   {
                subMatr.d[rowIdx][colIdx] = d[crow][ccol];
                colIdx++;
                ccol += colInc;
               }
            rowIdx++;
            crow += rowInc;
     } // crow <= rowEnd
 return subMatr;   // return the submatrix
   }
else {
      // fill the created matrix with values
    int  crow = rowStart;  // indexes current row
    int  ccol = colStart;  // indexes current column
    int  rowIdx = 0;  int  colIdx = 0;  // indexes at the new Matrix

           while  ( crow >= rowEnd )   {
          ccol = colStart;  colIdx = 0;
          while  (ccol >= colEnd)   {
                subMatr.d[rowIdx][colIdx] = d[crow][ccol];
                colIdx++;
                ccol += colInc;
               }
            rowIdx++;
            crow += rowInc;
     } // crow > rowEnd
 return subMatr;   // return the submatrix
   }

   }




 
 // extracts a submatrix, e.g. m.grc( 2, 3, 12, 4, 2,  8 )  corresponds to Matlab's m(2:3:12, 4:2:8)'
  final public Mat  grc(int rowLow, int  rowInc,  int rowHigh,  int  colLow,  int  colInc, int  colHigh)  {
    int Nrows = d.length; int Ncols = d[0].length;
    int  rowStart = rowLow;     int  rowEnd =  rowHigh;
    int  colStart = colLow;   int colEnd = colHigh;

        int   rowNum = (int) Math.floor((rowEnd-rowStart) / rowInc)+1;
        int   colNum = (int) Math.floor( (colEnd-colStart) / colInc)+1;
       
    Mat   subMatr = new Mat(rowNum, colNum);   // create a Matrix to keep the extracted range

    if  (rowStart <= rowEnd && colStart <= colEnd)   {    // positive increment at rows and columns
        int   crow = rowStart;  // indexes current row
        int   ccol = colStart;  // indexes current column
        int   rowIdx = 0;  int  colIdx = 0;  // indexes at the new Matrix
            while  ( crow <= rowEnd )   {
          ccol = colStart;  colIdx = 0;
          while  (ccol <= colEnd)   {
                subMatr.d[rowIdx][colIdx] = d[crow][ccol];
                colIdx++;
                ccol += colInc;
               }
            rowIdx++;
            crow += rowInc;
     } // crow <= rowEnd
 return subMatr;
} // positive increment
  else if  (rowStart >= rowEnd && colStart <= colEnd)   {
      // fill the created matrix with values
    int  crow = rowStart;  // indexes current row
    int  ccol = colStart;  // indexes current column
    int  rowIdx = 0; int  colIdx = 0;  // indexes at the new Matrix

           while  ( crow >= rowEnd )   {
          ccol = colStart;  colIdx = 0;
          while  (ccol <= colEnd)   {
                subMatr.d[rowIdx][colIdx] = d[crow][ccol];
                colIdx++;
                ccol += colInc;
               }
            rowIdx++;
            crow += rowInc;
     } // crow <= rowEnd
 return subMatr;   // return the submatrix
   }
else if  (rowStart <= rowEnd && colStart >= colEnd)   {
      // fill the created matrix with values
    int   crow = rowStart;  // indexes current row
    int   ccol = colStart;  // indexes current column
    int   rowIdx = 0;  int  colIdx = 0;  // indexes at the new Matrix

           while  ( crow <= rowEnd )   {
          ccol = colStart;  colIdx = 0;
          while  (ccol >= colEnd)   {
                subMatr.d[rowIdx][colIdx] = d[crow][ccol];
                colIdx++;
                ccol += colInc;
               }
            rowIdx++;
            crow += rowInc;
     } // crow <= rowEnd
 return subMatr;   // return the submatrix
   }
else {
      // fill the created matrix with values
    int   crow = rowStart;  // indexes current row
    int   ccol = colStart;   // indexes current column
    int   rowIdx = 0;  int  colIdx = 0;  // indexes at the new Matrix

           while  ( crow >= rowEnd )   {
          ccol = colStart;  colIdx = 0;
          while  (ccol >= colEnd)   {
                subMatr.d[rowIdx][colIdx] = d[crow][ccol];
                colIdx++;
                ccol += colInc;
               }
            rowIdx++;
            crow += rowInc;
     } // crow > rowEnd
 return subMatr;   // return the submatrix
   }

   }



 
// extracts a specific row, take all columns, e.g. m.gr(2) corresponds to Matlab's m(2, :)'
final public Mat gr(int row)  {
     int Nrows = d.length; int Ncols = d[0].length;
    
      int   colStart = 0;     int  colEnd =  Ncols-1;   // all columns
      int   rowNum = 1;    int  colNum = colEnd-colStart+1;
    Mat   subMatr = new Mat(rowNum, colNum);   // create a Matrix to keep the extracted range
      // fill the created matrix with values
    int  ccol = colStart;
    while  (ccol <= colEnd)   {
          subMatr.d[0][ccol] = d[row][ccol];
          ccol++;
         }

     return subMatr;
}


// extracts a specific column, take all rows, e.g. m.gc( 2) corresponds to Matlab's m(:,2:)'  
 final public Mat gc( int col )  { 
      int Nrows = d.length; int Ncols = d[0].length;
    
     int  rowStart = 0;     int  rowEnd =  Nrows-1;   // all rows
     int  colNum = 1;      int   rowNum = rowEnd-rowStart+1;
    Mat   subMatr = new Mat(rowNum, colNum);   // create a Matrix to keep the extracted range
      // fill the created matrix with values
    int  crow = rowStart;
    while  (crow <= rowEnd)   {
          subMatr.d[crow-rowStart][0] = d[crow][col];
          crow++;
         }

     return subMatr;
}

     
 final public Mat  gr(groovy.lang.IntRange rowRange)  {
    int rowS = rowRange.getFromInt();
    int rowE  = rowRange.getToInt();
    return gr(rowS, rowE);
 }

  final public Mat  gc(groovy.lang.IntRange colRange)  {
    int colS = colRange.getFromInt();
    int colE  = colRange.getToInt();
    return gc(colS, colE);
 }

 final public Mat  grc( groovy.lang.IntRange rowRange, groovy.lang.IntRange colRange)  {
    int rowS = rowRange.getFromInt();
    int rowE  = rowRange.getToInt();
    int colS = colRange.getFromInt();
    int colE  = colRange.getToInt();
    return grc(rowS, rowE, colS, colE);
 }


  

  /* e.g.
   x = rand(12, 15)
   y = ones(2, 3)
   x.s(2, 5,  y)
   */
  final public void   s(int rowS, int colS, Mat M)  
      {
         int  rowsNum = M.Nrows();
         int colsNum = M.Ncols();
         int cc = 0; int rr = 0;
         for (int r = rowS; r < rowS + rowsNum; r++) {
           cc = 0;
        for (int c = colS; c < colS + colsNum; c++) {
            d[r][c] = M.getAt(rr, cc);
            cc++;
           }
           rr++;
         }
      }
   
/* 
 a = rand(5,9)
 a.sr(1..2, 8)    // sets rows 1 and 2 to 8
 */
 final public void  sr(groovy.lang.IntRange rowRange, double value)  {
    int rowS = rowRange.getFromInt();
    int rowE  = rowRange.getToInt();
    
    for (int rows=rowS; rows <= rowE; rows++ )
       for  (int cols=0; cols < d[0].length; cols++)
        d[rows][cols] = value;
    }

 final public void  sr(int rowS, int rowE, double value)  {
     for (int rows=rowS; rows <= rowE; rows++ )
       for  (int cols=0; cols < d[0].length; cols++)
        d[rows][cols] = value;
    }

 final public void  src( groovy.lang.IntRange   rowRange, groovy.lang.IntRange colRange, double value)  {
         int rowS = rowRange.getFromInt(); 
         int rowE = rowRange.getToInt();
         int colsS = colRange.getFromInt();
         int colsE  = colRange.getToInt();
            
            for  (int rows=rowS; rows <= rowE; rows++)
                for (int cols=colsS; cols <= colsE; cols++ )
                    d[rows][cols] = value;
        }

     final  public void  sc( groovy.lang.IntRange colRange, double value)  {
            
            int colsS = colRange.getFromInt();
            int colsE  = colRange.getToInt();
            
            for  (int rows=0; rows < d.length; rows++)
                for (int cols=colsS; cols <= colsE; cols++ )
                    d[rows][cols] = value;
        }

        
     final  public void  sc(IntRangeWithStep colRange, double value)  {
            
            int colsS = colRange.getFromInt();
            int colsE  = colRange.getToInt();
            int step = colRange.mby;
            
            for  (int rows=0; rows < d.length; rows++)
                for (int cols=colsS; cols <= colsE; cols+=step )
                    d[rows][cols] = value;
        }

     
     final  public void  sc( int colsS, int colsE,  double value)  {
            
            for  (int rows=0; rows < d.length; rows++)
                for (int cols=colsS; cols <= colsE; cols++ )
                    d[rows][cols] = value;
        }
        
        final  public void  src(int rowS, int rowE, int colsS, int colsE, double value)  {
            for  (int rows=rowS; rows <= rowE; rows++)
                for (int cols=colsS; cols <= colsE; cols++ )
                    d[rows][cols] = value;
        }

        final  public void  src(int rowS, int rowInc,  int rowE, int colsS, int colInc, int colsE, double value)  {
            for  (int rows=rowS; rows <= rowE; rows+=rowInc)
                for (int cols=colsS; cols <= colsE; cols+=colInc )
                    d[rows][cols] = value;
        }

        
        final  public void  s(int [] rng, double value)  {
            int rowsS = rng[0];  int rowsE = rng[1];
            int colsS = rng[2]; int colsE = rng[3];
            for  (int rows=rowsS; rows <= rowsE; rows++)
                for (int cols=colsS; cols <= colsE; cols++ )
                    d[rows][cols] = value;
        }

        
    final  public void rightShift(Mat M) {		
        if (getColumnsNumber() != M.getColumnsNumber())
        throw
 new IllegalArgumentException("Rows append needs same number of columns. Upper (=right) Matrix has "+M.getColumnsNumber()+" columns, while left Matrix has "+getColumnsNumber()+" columns");
				
 M.setRef(DoubleArray.insertRows(M.getRef(),M.getRowsNumber(),getRef()));
}
	
 final  public void leftShift(Mat M) {
    if (getColumnsNumber() != M.getColumnsNumber())
  throw new IllegalArgumentException("Rows append needs same number of columns. Upper (=right) Matrix has "+M.getColumnsNumber()+" columns, while left Matrix has "+getColumnsNumber()+" columns");
			
 setRef(DoubleArray.insertRows(M.getRef(),M.getRowsNumber(),getRef()));
		
 setRef(DoubleArray.insertColumns(getRef(),getColumnsNumber(),M.getRef()));
}
 	
 final public  void rightShiftUnsigned(Mat M) {
   if (getRowsNumber() != M.getRowsNumber())
    throw new IllegalArgumentException("Columns append needs same number of rows. Right Matrix has "+M.getRowsNumber()+" rows, while left Matrix has "+getRowsNumber()+" rows");

 M.setRef(DoubleArray.insertColumns(M.getRef(),M.getColumnsNumber(),t(this).getRef()));
 }

 
 
  
// Row append and prepend routines    
/*
  mm = rand(4, 5)
  mmo = ones(2, 5)
  mmappend = mm.RA(mmo)  // prepend mmo 
  mmprepend = mmRP(mmo) // append mmo 
   */   
final public Mat  RA(Mat rowsToAppend)  {
    if (rowsToAppend.fncols != this.fncols)   // incompatible number of columns
      return this;
    // create a new extended matrix to have also the added rows
    int    exrows = fnrows+rowsToAppend.fnrows;   //  number of rows of the new matrix
    Mat  res = new Mat(exrows, this.fncols);

    // copy "this" Matrix
  int   r = 0;  int  c = 0;
  while (r <  this.fnrows) {
       c = 0;
       while  (c < this.fncols) {
          res.d[r][c] = this.d[r][c];
          c++;
       }
       r++;
    }

  //  append the passed matrix at the end
    r = 0;  
    while  (r < rowsToAppend.fnrows)  {
      c = 0;
      while  (c < rowsToAppend.fncols)  {
         res.d[fnrows + r][c] = rowsToAppend.d[r][c];
         c++;
          }
          r++;
    }
        return res;
}


  
final Mat   RA(double [] rowToAppend) {
    if (rowToAppend.length != this.fncols )   // incompatible number of columns
      return this;
    // create a new extended matrix to have also the added rows
    int   exrows =  fnrows+1;    // new number of rows
    Mat   res = new Mat( exrows, this.fncols);
    
    // copy "this" Matrix
  int  r = 0;  int  c = 0;
  while (r <  this.fnrows) {
       c = 0;
       while  (c < this.fncols) {
          res.d[r][c] = this.d[r][c];
          c++;
       }
       r++;
    }

    c = 0;
    while  (c < rowToAppend.length) {
         res.d[fnrows][c] = rowToAppend[c];
         c++;
      }
      
    return  res;
}

  
 final Mat   RA(Vec rowToAppend)  {
     return (this.RA(rowToAppend.getv()));  
 }
 
 
// prepend rowwise the Matrix Ncols
final  Mat   RP(Mat  rowsToPrepend)  {
    if (rowsToPrepend.fncols  != this.fncols )   // incompatible number of columns
      return this;
    // create a new extended matrix to have also the added rows
    int    exrows = fnrows + rowsToPrepend.fnrows;   // new number of rows
    Mat  res = new Mat(exrows, this.fncols);
    // copy prepended  Matrix
    int  r = 0;
    while  (r <  rowsToPrepend.fnrows)   {
      int  c = 0;
      while  (c < rowsToPrepend.fncols)  {
         res.d[r][c] = rowsToPrepend.d[r][c];
         c++;
      }
      r++;
    }
    
    // copy "this" matrix
    int rowsPrepended = rowsToPrepend.fnrows;
    r = 0;
    while  (r < this.fnrows)  {
      int  c = 0; 
      while  (c < this.fncols)  {
         res.d[rowsPrepended+r][c] = this.d[r][c];
         c++;
        }
      r++;
  }
    return res;
}




  // prepend rowwise the 1-d array rowToPrepend
final Mat  RP(double [] rowToPrepend) {
    if (rowToPrepend.length != this.fncols )   // incompatible number of columns
      return this;
    // create a new extended matrix to have also the added rows
   int   exrows = fnrows+1;  // new number of rows
   Mat res = new Mat(exrows, this.fncols);
   
    // prepend the passed 1-d array
    int  c = 0; 
    while  (c < this.fncols)  {
         res.d[0][c] = rowToPrepend[c];
         c++;
        }
       
// copy "this" Matrix
    int  r = 0;
    while  (r <  this.fnrows)   {
      c = 0;
      int    r1 = r + 1; 
      while  (c < this.fncols)  {
         res.d[r1][c] = this.d[r][c];
         c++;
      }
      r++;
    }
          
    return res;
}

  // prepend rowwise the 1-d array rowToPrepend
final Mat    RP(Vec  rowToPrepend) {
    return this.RP(rowToPrepend.getv());
}
  
    // Column append and prepend routines, e.g.
/* 
 mm = rand(4, 5)
 mmo = ones(4, 2)
 mmprepend = mm.CA(mmo) // append mmo 
 */

final Mat   CA(Mat colsToAppend)   {
    if (colsToAppend.fnrows  != this.fnrows )   // incompatible number of rows
      return this;
    // create a new extended matrix to have also the added columns
    int   excols = this.fncols + colsToAppend.fncols;  // new number of columns
    Mat   res = new Mat(this.fnrows, excols);
 
    // copy "this" Matrix
    int  r = 0;
    while  (r <  this.fnrows) {
      int   c = 0;
      while (c < this.fncols)  {
         res.d[r][c] = this.d[r][c];
         c++;
      }
      r++;
    }
    
    r = 0;
    while  (r < colsToAppend.fnrows)   {
      int  c = 0; 
      while  (c < colsToAppend.fncols)  {
         res.d[r][fncols+c] = colsToAppend.d[r][c];
         c++;
       }
      r++;
    }
    return res;
}



  // append an Array[Double] as the last column
final Mat   CA(double [] colsToAppend) {
    if (colsToAppend.length != this.fnrows )   // incompatible number of rows
      return this;
    // create a new extended matrix to have also the added columns
    int    excols = this.fncols+1; // new number of columns
    Mat   res = new Mat(this.fnrows, excols);
 
    // copy "this" Matrix
    int  r = 0;
    while  (r <  this.fnrows) {
      int   c = 0; 
      while (c < this.fncols)  {
         res.d[r][c] = this.d[r][c];
         c++;
      }
      r++;
    }
    
    // copy the double array
    r = 0;
    while  (r < colsToAppend.length)   {
         res.d[r][fncols]  = colsToAppend[r];
         r++;
       }
      
    return res;
}

  
   // append a Vec as the last column
  final Mat  CA (Vec colsToAppend){
     return this.CA(colsToAppend.getv());  
  }
  

// prepend a Mat  
  final Mat  CP(Mat colsToPrepend)  {
    if (colsToPrepend.fnrows  != this.fnrows )   // incompatible number of rows
      return this;
    // create a new extended matrix to have also the added columns
    int   excols = this.fncols + colsToPrepend.fncols;  // new number of columns
    Mat   res = new Mat(this.fnrows, excols);

    // copy prepended matrix
    int   r = 0;
    while  (r < colsToPrepend.fnrows)   {
      int  c = 0; 
      while  (c < colsToPrepend.fncols)  {
         res.d[r][c] = colsToPrepend.d[r][c];
         c++;
       }
      r++;
    }
    
    int   ncolsPrepended  = colsToPrepend.fncols;
// copy "this" Matrix
    r = 0;
    while  (r <  this.fnrows) {
      int  c = 0; 
      while (c < this.fncols)  {
         res.d[r][c+ncolsPrepended]= this.d[r][c];
         c++;
      }
      r++;
    }
    
    return res;
}

 
  
// prepend an Array[Double] to matrix
Mat   CP(double [] colsToPrepend) {
    int   arrayLen = colsToPrepend.length;
    if (arrayLen!= this.fnrows )   // incompatible number of rows
      return this;
    // create a new extended matrix to have also the added columns
    int   excols = this.fncols+1;  // new number of columns
    Mat  res = new Mat(this.fnrows, excols);
    
    // copy Array[Double]
    int   r = 0;
     while ( r < colsToPrepend.length)  {
         res.d[r][0] = colsToPrepend[r];
         r++;
      }
      
    // copy "this" Matrix
    r = 0;
    while  (r < this.fnrows) {
      int c = 0;
      while  (c < this.fncols)  {
         res.d[r][c+1] = this.d[r][c];
         c++;
      }
      r++;
    }

    return res;
}

 // prepend a Vec
Mat  CP(Vec colToPrepend) {
   return this.CP(colToPrepend.getv()); 
 }

 
 
 
 final  public  static void eachValue(Mat M, groovy.lang.Closure c) {
  M.eachValue(c);
 }
	
 final  public void eachValue(groovy.lang.Closure c) {
   for (int i = 0; i < fnrows; i++)
     for (int j = 0; j < fncols; j++)
         d[i][j] = (double)c.call(d[i][j]);
      
  }

 /*
 final public void lmap(groovy.lang.Closure c) {
 
     class dscclass implements  DoubleSupplier {
       groovy.lang.Closure gc;
      
       dscclass(groovy.lang.Closure c)  { gc = c; }
      
            @Override
            public double getAsDouble() {
        return c.cal
            }
   }
     
     DoubleSupplier dsc = new DoubleSupplier() {

       @Override
       public double getAsDouble() {
           throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
       }
   }
 }
 */
 // perform inplace map
  final  public void imap(groovy.lang.Closure c) {
       for (int i = 0; i < fnrows; i++)
        for (int j=0; j < fncols; j++)
          d[i][j] = (double)c.call(d[i][j]);
 }
 
  // perform a map operation returning a result Matrix
  final  public Mat  map(groovy.lang.Closure c) {
    double [][] dm = new double[fnrows][fncols];
    
      for (int i = 0; i < fnrows; i++)
        for (int j = 0; j < fncols; j++)
          dm[i][j] = (double)c.call(d[i][j]);
      
      return new Mat(dm);
 }
 
  // map Java 8 lambda function 
  final public Mat map(UnaryOperator  <Double> myfun) {
      double [][] dm = new double[fnrows][fncols];
    
      for (int i = 0; i < fnrows; i++)
        for (int j = 0; j < fncols; j++)
          dm[i][j] = myfun.apply(d[i][j]);
      
      return new Mat(dm);
  }
  
  
  
  // parallel in-place map of a the Closure c to all the elements of the matrix
  final public void pimap(final groovy.lang.Closure c)  {
      
     int rN = fnrows;
     int  nthreads = ConcurrencyUtils.getNumberOfThreads();
  
  // the matrix is split by row regions and the mutliplication in each such region
  // will be performed in parallel by a seperate thread
     nthreads = Math.min(nthreads, rN);
  
     Future<?>[] futures = new Future[nthreads];
            
      int   rowsPerThread = (int)(rN / nthreads)+1;  // how many rows the thread processes

      int threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
        // define the row region for the current thread
      final int  firstRow = threadId * rowsPerThread;   // start of rows range 
      final int  lastRow =   threadId == nthreads-1? rN: firstRow+rowsPerThread;  // end of rows range
        
 futures[threadId] = GlobalValues.execService.submit(new Runnable() {
    public void run()  {
      int  a = firstRow;   // the first row of the matrix that this thread processes
      while (a < lastRow) {  // the last row of the matrix that this thread processes
             for (int j=0; j < fncols; j++)
          d[a][j] = (double)c.call(d[a][j]);
        a++;     
 }
    } // run
 }
      );
        threadId++;
        
  }  // for all threads

  ConcurrencyUtils.waitForCompletion(futures);
  }
  
  
  
  
  
  
  final static  double []  smap(DoubleUnaryOperator  f, double [] da){
      DoubleStream ds = DoubleStream.of(da);
  
        double [] dr = ds.map(f).toArray();
      
       return dr;
  }
  
  final static  double []  psmap(DoubleUnaryOperator  f, double [] da){
      DoubleStream ds = DoubleStream.of(da);
  
        double [] dr = ds.parallel().map(f).toArray();
      
       return dr;
  }
  
  // parallel  map of the function f to all the elements of the matrix
  final public Mat  psmap(final DoubleUnaryOperator f)  {
      
      final double [][] dm = new double[fnrows][fncols];
      
     int rN = fnrows;
     int  nthreads = ConcurrencyUtils.getNumberOfThreads();
  
  // the matrix is split by row regions and the mutliplication in each such region
  // will be performed in parallel by a seperate thread
     nthreads = Math.min(nthreads, rN);
  
     Future<?>[] futures = new Future[nthreads];
            
      int   rowsPerThread = (int)(rN / nthreads)+1;  // how many rows the thread processes

      int threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
        // define the row region for the current thread
      final int  firstRow = threadId * rowsPerThread;   // start of rows range 
      final int  lastRow =   threadId == nthreads-1? rN: firstRow+rowsPerThread;  // end of rows range
        
 futures[threadId] = GlobalValues.execService.submit(new Runnable() {
    public void run()  {
      int  a = firstRow;   // the first row of the matrix that this thread processes
      while (a < lastRow) {  // the last row of the matrix that this thread processes
       DoubleStream ds = DoubleStream.of(d[a]);
  
        dm[a] = ds.map(f).toArray();
      
        a++;     
 }
    } // run
 }
      );
        threadId++;
        
  }  // for all threads

  ConcurrencyUtils.waitForCompletion(futures);
  
  
  return new Mat(dm);
  }
  
  // parallel  map of a the Closure c to all the elements of the matrix
  final public Mat  ppsmap(final DoubleUnaryOperator f)  {
      
      final double [][] dm = new double[fnrows][fncols];
      
     int rN = fnrows;
     int  nthreads = ConcurrencyUtils.getNumberOfThreads();
  
  // the matrix is split by row regions and the mutliplication in each such region
  // will be performed in parallel by a seperate thread
     nthreads = Math.min(nthreads, rN);
  
     Future<?>[] futures = new Future[nthreads];
            
      int   rowsPerThread = (int)(rN / nthreads)+1;  // how many rows the thread processes

      int threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
        // define the row region for the current thread
      final int  firstRow = threadId * rowsPerThread;   // start of rows range 
      final int  lastRow =   threadId == nthreads-1? rN: firstRow+rowsPerThread;  // end of rows range
        
 futures[threadId] = GlobalValues.execService.submit(new Runnable() {
    public void run()  {
      int  a = firstRow;   // the first row of the matrix that this thread processes
      while (a < lastRow) {  // the last row of the matrix that this thread processes
       DoubleStream ds = DoubleStream.of(d[a]);
  
        dm[a] = ds.map(f).parallel().toArray();
      
        a++;     
 }
    } // run
 }
      );
        threadId++;
        
  }  // for all threads

  ConcurrencyUtils.waitForCompletion(futures);
  
  
  return new Mat(dm);
  }
  
  
  // parallel  map of a the Closure c to all the elements of the matrix
  final public Mat  pmap(final groovy.lang.Closure c)  {
      
      final double [][] dm = new double[fnrows][fncols];
      
     int rN = fnrows;
     int  nthreads = ConcurrencyUtils.getNumberOfThreads();
  
  // the matrix is split by row regions and the mutliplication in each such region
  // will be performed in parallel by a seperate thread
     nthreads = Math.min(nthreads, rN);
  
     Future<?>[] futures = new Future[nthreads];
            
      int   rowsPerThread = (int)(rN / nthreads)+1;  // how many rows the thread processes

      int threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
        // define the row region for the current thread
      final int  firstRow = threadId * rowsPerThread;   // start of rows range 
      final int  lastRow =   threadId == nthreads-1? rN: firstRow+rowsPerThread;  // end of rows range
        
 futures[threadId] = GlobalValues.execService.submit(new Runnable() {
    public void run()  {
      int  a = firstRow;   // the first row of the matrix that this thread processes
      while (a < lastRow) {  // the last row of the matrix that this thread processes
             for (int j=0; j < fncols; j++)
          dm[a][j] = (double)c.call(d[a][j]);
        a++;     
 }
    } // run
 }
      );
        threadId++;
        
  }  // for all threads

  ConcurrencyUtils.waitForCompletion(futures);
  
  
  return new Mat(dm);
  }
  
  
   final  public void mapf(computeFunction cf) {
       for (int i = 0; i < fnrows; i++)
        for (int j=0; j < fncols; j++)
          d[i][j] = cf.f(d[i][j]);   // apply the compute function to the map
 }
 
   /*
   final  public Stream mapfp(Function <Object, Object> cf) {
       return  Stream.of(d).parallel().map(cf);
       }
   */
   final public static double testHandle(double x)  {
       return x+x+x;
   }
   
   final public void mapd() throws NoSuchMethodException, IllegalAccessException, Throwable {
            Lookup lookup = MethodHandles.lookup();
            MethodHandle  hm = lookup.findStatic(Mat.class, "testHandle", methodType(double.class, double.class));
       for (int i = 0; i < fnrows; i++)
        for (int j=0; j < fncols; j++)
          d[i][j] = (double)hm.invokeExact(d[i][j]);   // apply the compute function to the map
 }     
        
       
   
   
  final  public  static void map(Mat M, groovy.lang.Closure c) {
    M.map(c);
 }
	
  final  public  static void mapf(Mat M, computeFunction c) {
    M.mapf(c);
 }
 
// compare matrices by value
final  public boolean equals(Object M) {
    if (!(M instanceof Mat)) 
        return false;
    else {
        Mat MM = (Mat) M;
        if (MM.getRowsNumber() != getRowsNumber()) return false;
        if (MM.getColumnsNumber() != getColumnsNumber()) return false;
            for (int i=1; i<= getRowsNumber(); i++)
                for (int j=1; j <= getColumnsNumber(); j++)
                    if (MM.get(i,j) != get(i,j)) return false;
	return true;
	}
}
	

	
final  public static void resize(Mat M, int m, int n) {
    M.resize(m, n);
}
	
final public static int  any(Mat M) {
    double [][] vals = M.getArray();
    for (int r=0; r<vals.length; r++)
        for (int c=0; c<vals[0].length; c++)
            if (vals[r][c] != 0.0) return 1;
     return 0;
  }
	

// returns the indexes of the non-zero elements of the matrix
  final  public static Mat find(Mat M) {
        double [][] vals = M.getArray();
        int Rows = vals.length;
        int Cols = vals[0].length;
          // find number of nonzero elements
        int no = 0;
        for (int yi=0; yi<Rows ; yi++)
            for (int xi=0; xi<Cols ; xi++)
	if (vals[yi][xi] != 0.0)
                        no++;
    // build return vector
    double[][] values = new double[no][1];
    int i = 0;
    for (int xi=0; xi<Cols; xi++)
        for (int yi=0; yi<Rows; yi++)
            if (vals[yi][xi] != 0.0)
	{
    // nonzero element found
    // put element position into return column vector
    values[i][0] = yi + xi*Rows;
    i++;
    }
                
    return new Mat(values);
    }
                
final  public  static Mat sum(Object vals) {
        Mat Matr = new Mat((double [][] )vals, false);
        return sum(Matr);
}

 final  public  static Mat sum(Mat M)  {
    if (M.getRowsNumber()==1)
        return new Mat(DoubleArray.sum(M.getRowRef(1)));
    else
    return new Mat(DoubleArray.sum( M.getRef()), false );
}

final  public static Mat sin(Mat M) {
    return new Mat(DoubleArray.sin( M.getRef() ), false);
}

// in-place sin() function
 final  public  Mat isin() {
    int N = this.d.length;
    int M = this.d[0].length;
    for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           this.d[k][c] = Math.sin(this.d[k][c]);
     
     return this;
}
 
 
// parallel in-place sin() function 
 final  public  Mat pisin() {
    int rN = fnrows;
    int  nthreads = ConcurrencyUtils.getNumberOfThreads();
  
  // the matrix is split by row regions and the mutliplication in each such region
  // will be performed in parallel by a seperate thread
     nthreads = Math.min(nthreads, rN);
  
     Future<?>[] futures = new Future[nthreads];
            
      int   rowsPerThread = (int)(rN / nthreads)+1;  // how many rows the thread processes

      int threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
        // define the row region for the current thread
      final int  firstRow = threadId * rowsPerThread;   // start of rows range 
      final int  lastRow =   threadId == nthreads-1? rN: firstRow+rowsPerThread;  // end of rows range
        
 futures[threadId] = GlobalValues.execService.submit(new Runnable() {
    public void run()  {
      int  a = firstRow;   // the first row of the matrix that this thread processes
      while (a < lastRow) {  // the last row of the matrix that this thread processes
             for (int j=0; j < fncols; j++)
          d[a][j] = Math.sin(d[a][j]);
        a++;     
 }
    } // run
 }
      );
        threadId++;
        
  }  // for all threads

  ConcurrencyUtils.waitForCompletion(futures);
 
 
     return this;
}
 
 
 // parallel sin() function
 final  public  Mat psin() {
    int rN = fnrows;
    int  nthreads = ConcurrencyUtils.getNumberOfThreads();
    int M = this.d[0].length;
    final double [][]r = new double[rN][M];
    
  // the matrix is split by row regions and the mutliplication in each such region
  // will be performed in parallel by a seperate thread
     nthreads = Math.min(nthreads, rN);
  
     Future<?>[] futures = new Future[nthreads];
            
      int   rowsPerThread = (int)(rN / nthreads)+1;  // how many rows the thread processes

      int threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
        // define the row region for the current thread
      final int  firstRow = threadId * rowsPerThread;   // start of rows range 
      final int  lastRow =   threadId == nthreads-1? rN: firstRow+rowsPerThread;  // end of rows range
        
 futures[threadId] = GlobalValues.execService.submit(new Runnable() {
    public void run()  {
      int  a = firstRow;   // the first row of the matrix that this thread processes
      while (a < lastRow) {  // the last row of the matrix that this thread processes
             for (int j=0; j < fncols; j++)
          r[a][j] = Math.sin(d[a][j]);
        a++;     
 }
    } // run
 }
      );
        threadId++;
        
  }  // for all threads

  ConcurrencyUtils.waitForCompletion(futures);
 
 
     return new Mat(r);
}
 
 
 // sin() function
 final  public  Mat sin() {
    int N = this.d.length;
    int M = this.d[0].length;
    double [][]r = new double[N][M];
    for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           r[k][c] = Math.sin(this.d[k][c]);
     
     return new Mat(r);
}   
 
 
 // in-place cos() function
 final public  Mat icos() {
      int N = this.d.length;
     int M = this.d[0].length;
     for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           this.d[k][c] = Math.cos(this.d[k][c]);
     
     return this;
}   
 
 // parallel in-place cos() function
 final  public  Mat picos() {
    int rN = fnrows;
    int  nthreads = ConcurrencyUtils.getNumberOfThreads();
  
  // the matrix is split by row regions and the mutliplication in each such region
  // will be performed in parallel by a seperate thread
     nthreads = Math.min(nthreads, rN);
  
     Future<?>[] futures = new Future[nthreads];
            
      int   rowsPerThread = (int)(rN / nthreads)+1;  // how many rows the thread processes

      int threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
        // define the row region for the current thread
      final int  firstRow = threadId * rowsPerThread;   // start of rows range 
      final int  lastRow =   threadId == nthreads-1? rN: firstRow+rowsPerThread;  // end of rows range
        
 futures[threadId] = GlobalValues.execService.submit(new Runnable() {
    public void run()  {
      int  a = firstRow;   // the first row of the matrix that this thread processes
      while (a < lastRow) {  // the last row of the matrix that this thread processes
             for (int j=0; j < fncols; j++)
          d[a][j] = Math.cos(d[a][j]);
        a++;     
 }
    } // run
 }
      );
        threadId++;
        
  }  // for all threads

  ConcurrencyUtils.waitForCompletion(futures);
 
 
     return this;
}
 
 
 // parallel cos() function
 final  public  Mat pcos() {
    int rN = fnrows;
    int  nthreads = ConcurrencyUtils.getNumberOfThreads();
    int M = this.d[0].length;
    final double [][]r = new double[rN][M];
    
  // the matrix is split by row regions and the mutliplication in each such region
  // will be performed in parallel by a seperate thread
     nthreads = Math.min(nthreads, rN);
  
     Future<?>[] futures = new Future[nthreads];
            
      int   rowsPerThread = (int)(rN / nthreads)+1;  // how many rows the thread processes

      int threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
        // define the row region for the current thread
      final int  firstRow = threadId * rowsPerThread;   // start of rows range 
      final int  lastRow =   threadId == nthreads-1? rN: firstRow+rowsPerThread;  // end of rows range
        
 futures[threadId] = GlobalValues.execService.submit(new Runnable() {
    public void run()  {
      int  a = firstRow;   // the first row of the matrix that this thread processes
      while (a < lastRow) {  // the last row of the matrix that this thread processes
             for (int j=0; j < fncols; j++)
          r[a][j] = Math.cos(d[a][j]);
        a++;     
 }
    } // run
 }
      );
        threadId++;
        
  }  // for all threads

  ConcurrencyUtils.waitForCompletion(futures);
 
 
     return new Mat(r);
}
 
 
 
 // cos() function
 final  public  Mat cos() {
    int N = this.d.length;
    int M = this.d[0].length;
    double [][]r = new double[N][M];
    for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           r[k][c] = Math.cos(this.d[k][c]);
     
     return new Mat(r);
}   
 
 // in-place tan() function
 final  public  Mat itan() {
      int N = this.d.length;
     int M = this.d[0].length;
     for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           this.d[k][c] = Math.tan(this.d[k][c]);
     
     return this;
}   
 
 
 // parallel in-place tan() function
 final  public  Mat pitan() {
    int rN = fnrows;
    int  nthreads = ConcurrencyUtils.getNumberOfThreads();
  
  // the matrix is split by row regions and the mutliplication in each such region
  // will be performed in parallel by a seperate thread
     nthreads = Math.min(nthreads, rN);
  
     Future<?>[] futures = new Future[nthreads];
            
      int   rowsPerThread = (int)(rN / nthreads)+1;  // how many rows the thread processes

      int threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
        // define the row region for the current thread
      final int  firstRow = threadId * rowsPerThread;   // start of rows range 
      final int  lastRow =   threadId == nthreads-1? rN: firstRow+rowsPerThread;  // end of rows range
        
 futures[threadId] = GlobalValues.execService.submit(new Runnable() {
    public void run()  {
      int  a = firstRow;   // the first row of the matrix that this thread processes
      while (a < lastRow) {  // the last row of the matrix that this thread processes
             for (int j=0; j < fncols; j++)
          d[a][j] = Math.tan(d[a][j]);
        a++;     
 }
    } // run
 }
      );
        threadId++;
        
  }  // for all threads

  ConcurrencyUtils.waitForCompletion(futures);
 
 
     return this;
}
 
 
 // parallel tan() function
 final  public  Mat ptan() {
    int rN = fnrows;
    int  nthreads = ConcurrencyUtils.getNumberOfThreads();
    int M = this.d[0].length;
    final double [][]r = new double[rN][M];
    
  // the matrix is split by row regions and the mutliplication in each such region
  // will be performed in parallel by a seperate thread
     nthreads = Math.min(nthreads, rN);
  
     Future<?>[] futures = new Future[nthreads];
            
      int   rowsPerThread = (int)(rN / nthreads)+1;  // how many rows the thread processes

      int threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
        // define the row region for the current thread
      final int  firstRow = threadId * rowsPerThread;   // start of rows range 
      final int  lastRow =   threadId == nthreads-1? rN: firstRow+rowsPerThread;  // end of rows range
        
 futures[threadId] = GlobalValues.execService.submit(new Runnable() {
    public void run()  {
      int  a = firstRow;   // the first row of the matrix that this thread processes
      while (a < lastRow) {  // the last row of the matrix that this thread processes
             for (int j=0; j < fncols; j++)
          r[a][j] = Math.tan(d[a][j]);
        a++;     
 }
    } // run
 }
      );
        threadId++;
        
  }  // for all threads

  ConcurrencyUtils.waitForCompletion(futures);
 
 
     return new Mat(r);
}
 
 
 
 // tan() function
 final  public  Mat tan() {
    int N = this.d.length;
    int M = this.d[0].length;
    double [][]r = new double[N][M];
    for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           r[k][c] = Math.tan(this.d[k][c]);
     
     return new Mat(r);
}   
 
 final  public  Mat iasin() {
      int N = this.d.length;
     int M = this.d[0].length;
     for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           this.d[k][c] = Math.asin(this.d[k][c]);
     
     return this;
}   
 
 
 
 final  public  Mat asin() {
    int N = this.d.length;
    int M = this.d[0].length;
    double [][]r = new double[N][M];
    for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           r[k][c] = Math.asin(this.d[k][c]);
     
     return new Mat(r);
}   
 
 
 
 final  public  Mat iacos() {
      int N = this.d.length;
     int M = this.d[0].length;
     for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           this.d[k][c] = Math.acos(this.d[k][c]);
     
     return this;
}   
 
 
 final  public  Mat piacos() {
    int rN = fnrows;
    int  nthreads = ConcurrencyUtils.getNumberOfThreads();
  
  // the matrix is split by row regions and the mutliplication in each such region
  // will be performed in parallel by a seperate thread
     nthreads = Math.min(nthreads, rN);
  
     Future<?>[] futures = new Future[nthreads];
            
      int   rowsPerThread = (int)(rN / nthreads)+1;  // how many rows the thread processes

      int threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
        // define the row region for the current thread
      final int  firstRow = threadId * rowsPerThread;   // start of rows range 
      final int  lastRow =   threadId == nthreads-1? rN: firstRow+rowsPerThread;  // end of rows range
        
 futures[threadId] = GlobalValues.execService.submit(new Runnable() {
    public void run()  {
      int  a = firstRow;   // the first row of the matrix that this thread processes
      while (a < lastRow) {  // the last row of the matrix that this thread processes
             for (int j=0; j < fncols; j++)
          d[a][j] = Math.acos(d[a][j]);
        a++;     
 }
    } // run
 }
      );
        threadId++;
        
  }  // for all threads

  ConcurrencyUtils.waitForCompletion(futures);
 
 
     return this;
}
 
 
 final  public  Mat pacos() {
    int rN = fnrows;
    int  nthreads = ConcurrencyUtils.getNumberOfThreads();
    int M = this.d[0].length;
    final double [][]r = new double[rN][M];
    
  // the matrix is split by row regions and the mutliplication in each such region
  // will be performed in parallel by a seperate thread
     nthreads = Math.min(nthreads, rN);
  
     Future<?>[] futures = new Future[nthreads];
            
      int   rowsPerThread = (int)(rN / nthreads)+1;  // how many rows the thread processes

      int threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
        // define the row region for the current thread
      final int  firstRow = threadId * rowsPerThread;   // start of rows range 
      final int  lastRow =   threadId == nthreads-1? rN: firstRow+rowsPerThread;  // end of rows range
        
 futures[threadId] = GlobalValues.execService.submit(new Runnable() {
    public void run()  {
      int  a = firstRow;   // the first row of the matrix that this thread processes
      while (a < lastRow) {  // the last row of the matrix that this thread processes
             for (int j=0; j < fncols; j++)
          r[a][j] = Math.acos(d[a][j]);
        a++;     
 }
    } // run
 }
      );
        threadId++;
        
  }  // for all threads

  ConcurrencyUtils.waitForCompletion(futures);
 
 
     return new Mat(r);
}
 
 
 
 
 
 final  public  Mat acos() {
    int N = this.d.length;
    int M = this.d[0].length;
    double [][]r = new double[N][M];
    for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           r[k][c] = Math.acos(this.d[k][c]);
     
     return new Mat(r);
}   
 
 
 
 final  public  Mat iatan() {
      int N = this.d.length;
     int M = this.d[0].length;
     for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           this.d[k][c] = Math.atan(this.d[k][c]);
     
     return this;
}   
 
 
 
 final  public  Mat atan() {
    int N = this.d.length;
    int M = this.d[0].length;
    double [][]r = new double[N][M];
    for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           r[k][c] = Math.atan(this.d[k][c]);
     
     return new Mat(r);
}   
 
 
 final  public  Mat piatan() {
    int rN = fnrows;
    int  nthreads = ConcurrencyUtils.getNumberOfThreads();
  
  // the matrix is split by row regions and the mutliplication in each such region
  // will be performed in parallel by a seperate thread
     nthreads = Math.min(nthreads, rN);
  
     Future<?>[] futures = new Future[nthreads];
            
      int   rowsPerThread = (int)(rN / nthreads)+1;  // how many rows the thread processes

      int threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
        // define the row region for the current thread
      final int  firstRow = threadId * rowsPerThread;   // start of rows range 
      final int  lastRow =   threadId == nthreads-1? rN: firstRow+rowsPerThread;  // end of rows range
        
 futures[threadId] = GlobalValues.execService.submit(new Runnable() {
    public void run()  {
      int  a = firstRow;   // the first row of the matrix that this thread processes
      while (a < lastRow) {  // the last row of the matrix that this thread processes
             for (int j=0; j < fncols; j++)
          d[a][j] = Math.atan(d[a][j]);
        a++;     
 }
    } // run
 }
      );
        threadId++;
        
  }  // for all threads

  ConcurrencyUtils.waitForCompletion(futures);
 
 
     return this;
}
 
  
 
 final  public  Mat patan() {
    int rN = fnrows;
    int  nthreads = ConcurrencyUtils.getNumberOfThreads();
    int M = this.d[0].length;
    final double [][]r = new double[rN][M];
    
  // the matrix is split by row regions and the mutliplication in each such region
  // will be performed in parallel by a seperate thread
     nthreads = Math.min(nthreads, rN);
  
     Future<?>[] futures = new Future[nthreads];
            
      int   rowsPerThread = (int)(rN / nthreads)+1;  // how many rows the thread processes

      int threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
        // define the row region for the current thread
      final int  firstRow = threadId * rowsPerThread;   // start of rows range 
      final int  lastRow =   threadId == nthreads-1? rN: firstRow+rowsPerThread;  // end of rows range
        
 futures[threadId] = GlobalValues.execService.submit(new Runnable() {
    public void run()  {
      int  a = firstRow;   // the first row of the matrix that this thread processes
      while (a < lastRow) {  // the last row of the matrix that this thread processes
             for (int j=0; j < fncols; j++)
          r[a][j] = Math.atan(d[a][j]);
        a++;     
 }
    } // run
 }
      );
        threadId++;
        
  }  // for all threads

  ConcurrencyUtils.waitForCompletion(futures);
 
 
     return new Mat(r);
}
 
 
 
 
 final  public  Mat isinh() {
      int N = this.d.length;
     int M = this.d[0].length;
     for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           this.d[k][c] = Math.sinh(this.d[k][c]);
     
     return this;
}   
 
 
 
 final  public  Mat pisinh() {
    int rN = fnrows;
    int  nthreads = ConcurrencyUtils.getNumberOfThreads();
  
  // the matrix is split by row regions and the mutliplication in each such region
  // will be performed in parallel by a seperate thread
     nthreads = Math.min(nthreads, rN);
  
     Future<?>[] futures = new Future[nthreads];
            
      int   rowsPerThread = (int)(rN / nthreads)+1;  // how many rows the thread processes

      int threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
        // define the row region for the current thread
      final int  firstRow = threadId * rowsPerThread;   // start of rows range 
      final int  lastRow =   threadId == nthreads-1? rN: firstRow+rowsPerThread;  // end of rows range
        
 futures[threadId] = GlobalValues.execService.submit(new Runnable() {
    public void run()  {
      int  a = firstRow;   // the first row of the matrix that this thread processes
      while (a < lastRow) {  // the last row of the matrix that this thread processes
             for (int j=0; j < fncols; j++)
          d[a][j] = Math.sinh(d[a][j]);
        a++;     
 }
    } // run
 }
      );
        threadId++;
        
  }  // for all threads

  ConcurrencyUtils.waitForCompletion(futures);
 
 
     return this;
}
 
 
 final  public  Mat sinh() {
    int N = this.d.length;
    int M = this.d[0].length;
    double [][]r = new double[N][M];
    for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           r[k][c] = Math.sinh(this.d[k][c]);
     
     return new Mat(r);
}   
 
 final  public  Mat icosh() {
      int N = this.d.length;
     int M = this.d[0].length;
     for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           this.d[k][c] = Math.cosh(this.d[k][c]);
     
     return this;
}   
 
  
 
 final  public  Mat psinh() {
    int rN = fnrows;
    int  nthreads = ConcurrencyUtils.getNumberOfThreads();
    int M = this.d[0].length;
    final double [][]r = new double[rN][M];
    
  // the matrix is split by row regions and the mutliplication in each such region
  // will be performed in parallel by a seperate thread
     nthreads = Math.min(nthreads, rN);
  
     Future<?>[] futures = new Future[nthreads];
            
      int   rowsPerThread = (int)(rN / nthreads)+1;  // how many rows the thread processes

      int threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
        // define the row region for the current thread
      final int  firstRow = threadId * rowsPerThread;   // start of rows range 
      final int  lastRow =   threadId == nthreads-1? rN: firstRow+rowsPerThread;  // end of rows range
        
 futures[threadId] = GlobalValues.execService.submit(new Runnable() {
    public void run()  {
      int  a = firstRow;   // the first row of the matrix that this thread processes
      while (a < lastRow) {  // the last row of the matrix that this thread processes
             for (int j=0; j < fncols; j++)
          r[a][j] = Math.sinh(d[a][j]);
        a++;     
 }
    } // run
 }
      );
        threadId++;
        
  }  // for all threads

  ConcurrencyUtils.waitForCompletion(futures);
 
 
     return new Mat(r);
}
 
 
 
 
 final  public  Mat cosh() {
    int N = this.d.length;
    int M = this.d[0].length;
    double [][]r = new double[N][M];
    for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           r[k][c] = Math.cosh(this.d[k][c]);
     
     return new Mat(r);
}   
 
 
 final  public  Mat picosh() {
    int rN = fnrows;
    int  nthreads = ConcurrencyUtils.getNumberOfThreads();
  
  // the matrix is split by row regions and the mutliplication in each such region
  // will be performed in parallel by a seperate thread
     nthreads = Math.min(nthreads, rN);
  
     Future<?>[] futures = new Future[nthreads];
            
      int   rowsPerThread = (int)(rN / nthreads)+1;  // how many rows the thread processes

      int threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
        // define the row region for the current thread
      final int  firstRow = threadId * rowsPerThread;   // start of rows range 
      final int  lastRow =   threadId == nthreads-1? rN: firstRow+rowsPerThread;  // end of rows range
        
 futures[threadId] = GlobalValues.execService.submit(new Runnable() {
    public void run()  {
      int  a = firstRow;   // the first row of the matrix that this thread processes
      while (a < lastRow) {  // the last row of the matrix that this thread processes
             for (int j=0; j < fncols; j++)
          d[a][j] = Math.cosh(d[a][j]);
        a++;     
 }
    } // run
 }
      );
        threadId++;
        
  }  // for all threads

  ConcurrencyUtils.waitForCompletion(futures);
 
 
     return this;
}
 
 
 final  public  Mat pcosh() {
    int rN = fnrows;
    int  nthreads = ConcurrencyUtils.getNumberOfThreads();
    int M = this.d[0].length;
    final double [][]r = new double[rN][M];
    
  // the matrix is split by row regions and the mutliplication in each such region
  // will be performed in parallel by a seperate thread
     nthreads = Math.min(nthreads, rN);
  
     Future<?>[] futures = new Future[nthreads];
            
      int   rowsPerThread = (int)(rN / nthreads)+1;  // how many rows the thread processes

      int threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
        // define the row region for the current thread
      final int  firstRow = threadId * rowsPerThread;   // start of rows range 
      final int  lastRow =   threadId == nthreads-1? rN: firstRow+rowsPerThread;  // end of rows range
        
 futures[threadId] = GlobalValues.execService.submit(new Runnable() {
    public void run()  {
      int  a = firstRow;   // the first row of the matrix that this thread processes
      while (a < lastRow) {  // the last row of the matrix that this thread processes
             for (int j=0; j < fncols; j++)
          r[a][j] = Math.cosh(d[a][j]);
        a++;     
 }
    } // run
 }
      );
        threadId++;
        
  }  // for all threads

  ConcurrencyUtils.waitForCompletion(futures);
 
 
     return new Mat(r);
}
 
 
 
 
 final public  Mat itanh() {
      int N = this.d.length;
     int M = this.d[0].length;
     for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           this.d[k][c] = Math.tanh(this.d[k][c]);
     
     return this;
}   
 
 
 
 final  public  Mat tanh() {
    int N = this.d.length;
    int M = this.d[0].length;
    double [][]r = new double[N][M];
    for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           r[k][c] = Math.tanh(this.d[k][c]);
     
     return new Mat(r);
}   
 
 
 
 final  public  Mat ipow(double v) {
      int N = this.d.length;
     int M = this.d[0].length;
     for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           this.d[k][c] = Math.pow(this.d[k][c], v);
     
     return this;
}   
  
 
 
 final  public  Mat pow(double v) {
    int N = this.d.length;
    int M = this.d[0].length;
    double [][]r = new double[N][M];
    for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           r[k][c] = Math.pow(this.d[k][c], v);
     
     return new Mat(r);
}   
 
 
 final  public  Mat pipow(final double v) {
    int rN = fnrows;
    int  nthreads = ConcurrencyUtils.getNumberOfThreads();
  
  // the matrix is split by row regions and the mutliplication in each such region
  // will be performed in parallel by a seperate thread
     nthreads = Math.min(nthreads, rN);
  
     Future<?>[] futures = new Future[nthreads];
            
      int   rowsPerThread = (int)(rN / nthreads)+1;  // how many rows the thread processes

      int threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
        // define the row region for the current thread
      final int  firstRow = threadId * rowsPerThread;   // start of rows range 
      final int  lastRow =   threadId == nthreads-1? rN: firstRow+rowsPerThread;  // end of rows range
        
 futures[threadId] = GlobalValues.execService.submit(new Runnable() {
    public void run()  {
      int  a = firstRow;   // the first row of the matrix that this thread processes
      while (a < lastRow) {  // the last row of the matrix that this thread processes
             for (int j=0; j < fncols; j++)
          d[a][j] = Math.pow(d[a][j], v);
        a++;     
 }
    } // run
 }
      );
        threadId++;
        
  }  // for all threads

  ConcurrencyUtils.waitForCompletion(futures);
 
 
     return this;
}
 
 
 
 final  public  Mat ppow(final double v) {
    int rN = fnrows;
    int  nthreads = ConcurrencyUtils.getNumberOfThreads();
    int M = this.d[0].length;
    final double [][]r = new double[rN][M];
    
  // the matrix is split by row regions and the mutliplication in each such region
  // will be performed in parallel by a seperate thread
     nthreads = Math.min(nthreads, rN);
  
     Future<?>[] futures = new Future[nthreads];
            
      int   rowsPerThread = (int)(rN / nthreads)+1;  // how many rows the thread processes

      int threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
        // define the row region for the current thread
      final int  firstRow = threadId * rowsPerThread;   // start of rows range 
      final int  lastRow =   threadId == nthreads-1? rN: firstRow+rowsPerThread;  // end of rows range
        
 futures[threadId] = GlobalValues.execService.submit(new Runnable() {
    public void run()  {
      int  a = firstRow;   // the first row of the matrix that this thread processes
      while (a < lastRow) {  // the last row of the matrix that this thread processes
             for (int j=0; j < fncols; j++)
          r[a][j] = Math.pow(d[a][j], v);
        a++;     
 }
    } // run
 }
      );
        threadId++;
        
  }  // for all threads

  ConcurrencyUtils.waitForCompletion(futures);
 
 
     return new Mat(r);
}
 
 
 
 final  public  Mat ilog() {
      int N = this.d.length;
     int M = this.d[0].length;
     for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           this.d[k][c] = Math.log(this.d[k][c]);
     
     return this;
}   
 
 
 
 final  public  Mat pilog() {
    int rN = fnrows;
    int  nthreads = ConcurrencyUtils.getNumberOfThreads();
  
  // the matrix is split by row regions and the mutliplication in each such region
  // will be performed in parallel by a seperate thread
     nthreads = Math.min(nthreads, rN);
  
     Future<?>[] futures = new Future[nthreads];
            
      int   rowsPerThread = (int)(rN / nthreads)+1;  // how many rows the thread processes

      int threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
        // define the row region for the current thread
      final int  firstRow = threadId * rowsPerThread;   // start of rows range 
      final int  lastRow =   threadId == nthreads-1? rN: firstRow+rowsPerThread;  // end of rows range
        
 futures[threadId] = GlobalValues.execService.submit(new Runnable() {
    public void run()  {
      int  a = firstRow;   // the first row of the matrix that this thread processes
      while (a < lastRow) {  // the last row of the matrix that this thread processes
             for (int j=0; j < fncols; j++)
          d[a][j] = Math.log(d[a][j]);
        a++;     
 }
    } // run
 }
      );
        threadId++;
        
  }  // for all threads

  ConcurrencyUtils.waitForCompletion(futures);
 
 
     return this;
}
 
 
 final  public  Mat plog() {
    int rN = fnrows;
    int  nthreads = ConcurrencyUtils.getNumberOfThreads();
    int M = this.d[0].length;
    final double [][]r = new double[rN][M];
    
  // the matrix is split by row regions and the mutliplication in each such region
  // will be performed in parallel by a seperate thread
     nthreads = Math.min(nthreads, rN);
  
     Future<?>[] futures = new Future[nthreads];
            
      int   rowsPerThread = (int)(rN / nthreads)+1;  // how many rows the thread processes

      int threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
        // define the row region for the current thread
      final int  firstRow = threadId * rowsPerThread;   // start of rows range 
      final int  lastRow =   threadId == nthreads-1? rN: firstRow+rowsPerThread;  // end of rows range
        
 futures[threadId] = GlobalValues.execService.submit(new Runnable() {
    public void run()  {
      int  a = firstRow;   // the first row of the matrix that this thread processes
      while (a < lastRow) {  // the last row of the matrix that this thread processes
             for (int j=0; j < fncols; j++)
          r[a][j] = Math.log(d[a][j]);
        a++;     
 }
    } // run
 }
      );
        threadId++;
        
  }  // for all threads

  ConcurrencyUtils.waitForCompletion(futures);
 
 
     return new Mat(r);
}
 
 
 
 final  public  Mat log() {
    int N = this.d.length;
    int M = this.d[0].length;
    double [][]r = new double[N][M];
    for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           r[k][c] = Math.log(this.d[k][c]);
     
     return new Mat(r);
}   
 
 
 final public  Mat ilog2() {
     int N = this.d.length;
     int M = this.d[0].length;
     double  conv = Math.log(2.0);
     
     for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           this.d[k][c] = Math.log(this.d[k][c]/conv);
     
     return this;
}   
 
 
 
 final  public  Mat pilog2() {
    final double conv = Math.log(2.0);
    int rN = fnrows;
    int  nthreads = ConcurrencyUtils.getNumberOfThreads();
  
  // the matrix is split by row regions and the mutliplication in each such region
  // will be performed in parallel by a seperate thread
     nthreads = Math.min(nthreads, rN);
  
     Future<?>[] futures = new Future[nthreads];
            
      int   rowsPerThread = (int)(rN / nthreads)+1;  // how many rows the thread processes

      int threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
        // define the row region for the current thread
      final int  firstRow = threadId * rowsPerThread;   // start of rows range 
      final int  lastRow =   threadId == nthreads-1? rN: firstRow+rowsPerThread;  // end of rows range
        
 futures[threadId] = GlobalValues.execService.submit(new Runnable() {
    public void run()  {
      int  a = firstRow;   // the first row of the matrix that this thread processes
      while (a < lastRow) {  // the last row of the matrix that this thread processes
             for (int j=0; j < fncols; j++)
          d[a][j] = Math.log(d[a][j]/conv);
        a++;     
 }
    } // run
 }
      );
        threadId++;
        
  }  // for all threads

  ConcurrencyUtils.waitForCompletion(futures);
 
 
     return this;
}
 
 
 final  public  Mat plog2() {
    int rN = fnrows;
    int  nthreads = ConcurrencyUtils.getNumberOfThreads();
    int M = this.d[0].length;
    final double [][]r = new double[rN][M];
    final double conv = Math.log(2.0);
    
  // the matrix is split by row regions and the mutliplication in each such region
  // will be performed in parallel by a seperate thread
     nthreads = Math.min(nthreads, rN);
  
     Future<?>[] futures = new Future[nthreads];
            
      int   rowsPerThread = (int)(rN / nthreads)+1;  // how many rows the thread processes

      int threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
        // define the row region for the current thread
      final int  firstRow = threadId * rowsPerThread;   // start of rows range 
      final int  lastRow =   threadId == nthreads-1? rN: firstRow+rowsPerThread;  // end of rows range
        
 futures[threadId] = GlobalValues.execService.submit(new Runnable() {
    public void run()  {
      int  a = firstRow;   // the first row of the matrix that this thread processes
      while (a < lastRow) {  // the last row of the matrix that this thread processes
             for (int j=0; j < fncols; j++)
          r[a][j] = Math.log(d[a][j]/conv);
        a++;     
 }
    } // run
 }
      );
        threadId++;
        
  }  // for all threads

  ConcurrencyUtils.waitForCompletion(futures);
 
 
     return new Mat(r);
}
 
 
 
 final  public  Mat log2() {
    int N = this.d.length;
    int M = this.d[0].length;
    double conv = Math.log(2.0);
    
    double [][]r = new double[N][M];
    for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           r[k][c] = Math.log(this.d[k][c]/conv);
     
     return new Mat(r);
}   
 
 
 final  public  Mat ilog10() {
     int N = this.d.length;
     int M = this.d[0].length;
     double  conv = Math.log(10.0);
     
     for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           this.d[k][c] = Math.log(this.d[k][c]/conv);
     
     return this;
}   
 
 
 
 final  public  Mat log10() {
    int N = this.d.length;
    int M = this.d[0].length;
    double conv = Math.log(10.0);
    
    double [][]r = new double[N][M];
    for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           r[k][c] = Math.log(this.d[k][c]/conv);
     
     return new Mat(r);
}   
 
 
 
 final  public  Mat pilog10() {
    int rN = fnrows;
    int  nthreads = ConcurrencyUtils.getNumberOfThreads();
    final double conv = Math.log(10.0);
    
  // the matrix is split by row regions and the mutliplication in each such region
  // will be performed in parallel by a seperate thread
     nthreads = Math.min(nthreads, rN);
  
     Future<?>[] futures = new Future[nthreads];
            
      int   rowsPerThread = (int)(rN / nthreads)+1;  // how many rows the thread processes

      int threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
        // define the row region for the current thread
      final int  firstRow = threadId * rowsPerThread;   // start of rows range 
      final int  lastRow =   threadId == nthreads-1? rN: firstRow+rowsPerThread;  // end of rows range
        
 futures[threadId] = GlobalValues.execService.submit(new Runnable() {
    public void run()  {
      int  a = firstRow;   // the first row of the matrix that this thread processes
      while (a < lastRow) {  // the last row of the matrix that this thread processes
             for (int j=0; j < fncols; j++)
          d[a][j] = Math.log(d[a][j]/conv);
        a++;     
 }
    } // run
 }
      );
        threadId++;
        
  }  // for all threads

  ConcurrencyUtils.waitForCompletion(futures);
 
 
     return this;
}
 
 
 
 final  public  Mat plog10() {
    int rN = fnrows;
    int  nthreads = ConcurrencyUtils.getNumberOfThreads();
    int M = this.d[0].length;
    final double [][]r = new double[rN][M];
    final double conv = Math.log(10.0);
    
  // the matrix is split by row regions and the mutliplication in each such region
  // will be performed in parallel by a seperate thread
     nthreads = Math.min(nthreads, rN);
  
     Future<?>[] futures = new Future[nthreads];
            
      int   rowsPerThread = (int)(rN / nthreads)+1;  // how many rows the thread processes

      int threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
        // define the row region for the current thread
      final int  firstRow = threadId * rowsPerThread;   // start of rows range 
      final int  lastRow =   threadId == nthreads-1? rN: firstRow+rowsPerThread;  // end of rows range
        
 futures[threadId] = GlobalValues.execService.submit(new Runnable() {
    public void run()  {
      int  a = firstRow;   // the first row of the matrix that this thread processes
      while (a < lastRow) {  // the last row of the matrix that this thread processes
             for (int j=0; j < fncols; j++)
          r[a][j] = Math.log(d[a][j]/conv);
        a++;     
 }
    } // run
 }
      );
        threadId++;
        
  }  // for all threads

  ConcurrencyUtils.waitForCompletion(futures);
 
 
     return new Mat(r);
}
 
 
 final public  Mat iceil() {
     int N = this.d.length;
     int M = this.d[0].length;
     double  conv = Math.log(10.0);
     
     for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           this.d[k][c] = Math.ceil(this.d[k][c]/conv);
     
     return this;
}   
 
 
 
 final  public  Mat ceil() {
    int N = this.d.length;
    int M = this.d[0].length;
    double [][]r = new double[N][M];
    for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           r[k][c] = Math.ceil(this.d[k][c]);
     
     return new Mat(r);
}   
 
 
 final  public  Mat piceil() {
    int rN = fnrows;
    int  nthreads = ConcurrencyUtils.getNumberOfThreads();
  
  // the matrix is split by row regions and the mutliplication in each such region
  // will be performed in parallel by a seperate thread
     nthreads = Math.min(nthreads, rN);
  
     Future<?>[] futures = new Future[nthreads];
            
      int   rowsPerThread = (int)(rN / nthreads)+1;  // how many rows the thread processes

      int threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
        // define the row region for the current thread
      final int  firstRow = threadId * rowsPerThread;   // start of rows range 
      final int  lastRow =   threadId == nthreads-1? rN: firstRow+rowsPerThread;  // end of rows range
        
 futures[threadId] = GlobalValues.execService.submit(new Runnable() {
    public void run()  {
      int  a = firstRow;   // the first row of the matrix that this thread processes
      while (a < lastRow) {  // the last row of the matrix that this thread processes
             for (int j=0; j < fncols; j++)
          d[a][j] = Math.ceil(d[a][j]);
        a++;     
 }
    } // run
 }
      );
        threadId++;
        
  }  // for all threads

  ConcurrencyUtils.waitForCompletion(futures);
 
 
     return this;
}
 
 
 
 final  public  Mat pceil() {
    int rN = fnrows;
    int  nthreads = ConcurrencyUtils.getNumberOfThreads();
    int M = this.d[0].length;
    final double [][]r = new double[rN][M];
    
  // the matrix is split by row regions and the mutliplication in each such region
  // will be performed in parallel by a seperate thread
     nthreads = Math.min(nthreads, rN);
  
     Future<?>[] futures = new Future[nthreads];
            
      int   rowsPerThread = (int)(rN / nthreads)+1;  // how many rows the thread processes

      int threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
        // define the row region for the current thread
      final int  firstRow = threadId * rowsPerThread;   // start of rows range 
      final int  lastRow =   threadId == nthreads-1? rN: firstRow+rowsPerThread;  // end of rows range
        
 futures[threadId] = GlobalValues.execService.submit(new Runnable() {
    public void run()  {
      int  a = firstRow;   // the first row of the matrix that this thread processes
      while (a < lastRow) {  // the last row of the matrix that this thread processes
             for (int j=0; j < fncols; j++)
          r[a][j] = Math.ceil(d[a][j]);
        a++;     
 }
    } // run
 }
      );
        threadId++;
        
  }  // for all threads

  ConcurrencyUtils.waitForCompletion(futures);
 
 
     return new Mat(r);
}
 
 
 final  public  Mat ifloor() {
     int N = this.d.length;
     int M = this.d[0].length;
     double  conv = Math.log(10.0);
     
     for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           this.d[k][c] = Math.floor(this.d[k][c]/conv);
     
     return this;
}   
 
 
 
 final  public  Mat floor() {
    int N = this.d.length;
    int M = this.d[0].length;
    double [][]r = new double[N][M];
    for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           r[k][c] = Math.floor(this.d[k][c]);
     
     return new Mat(r);
}   
 
 
 final  public  Mat piround() {
    int rN = fnrows;
    int  nthreads = ConcurrencyUtils.getNumberOfThreads();
  
  // the matrix is split by row regions and the mutliplication in each such region
  // will be performed in parallel by a seperate thread
     nthreads = Math.min(nthreads, rN);
  
     Future<?>[] futures = new Future[nthreads];
            
      int   rowsPerThread = (int)(rN / nthreads)+1;  // how many rows the thread processes

      int threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
        // define the row region for the current thread
      final int  firstRow = threadId * rowsPerThread;   // start of rows range 
      final int  lastRow =   threadId == nthreads-1? rN: firstRow+rowsPerThread;  // end of rows range
        
 futures[threadId] = GlobalValues.execService.submit(new Runnable() {
    public void run()  {
      int  a = firstRow;   // the first row of the matrix that this thread processes
      while (a < lastRow) {  // the last row of the matrix that this thread processes
             for (int j=0; j < fncols; j++)
          d[a][j] = Math.round(d[a][j]);
        a++;     
 }
    } // run
 }
      );
        threadId++;
        
  }  // for all threads

  ConcurrencyUtils.waitForCompletion(futures);
 
 
     return this;
}
 
 
 
 
 final  public  Mat pround() {
    int rN = fnrows;
    int  nthreads = ConcurrencyUtils.getNumberOfThreads();
    int M = this.d[0].length;
    final double [][]r = new double[rN][M];
    
  // the matrix is split by row regions and the mutliplication in each such region
  // will be performed in parallel by a seperate thread
     nthreads = Math.min(nthreads, rN);
  
     Future<?>[] futures = new Future[nthreads];
            
      int   rowsPerThread = (int)(rN / nthreads)+1;  // how many rows the thread processes

      int threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
        // define the row region for the current thread
      final int  firstRow = threadId * rowsPerThread;   // start of rows range 
      final int  lastRow =   threadId == nthreads-1? rN: firstRow+rowsPerThread;  // end of rows range
        
 futures[threadId] = GlobalValues.execService.submit(new Runnable() {
    public void run()  {
      int  a = firstRow;   // the first row of the matrix that this thread processes
      while (a < lastRow) {  // the last row of the matrix that this thread processes
             for (int j=0; j < fncols; j++)
          r[a][j] = Math.round(d[a][j]);
        a++;     
 }
    } // run
 }
      );
        threadId++;
        
  }  // for all threads

  ConcurrencyUtils.waitForCompletion(futures);
 
 
     return new Mat(r);
}
 
 
 
 final  public  Mat iround() {
     int N = this.d.length;
     int M = this.d[0].length;
     
     for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           this.d[k][c] = Math.round(this.d[k][c]);
     
     return this;
}   
 
 
 
 final  public  Mat round() {
    int N = this.d.length;
    int M = this.d[0].length;
    double [][]r = new double[N][M];
    for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           r[k][c] = Math.round(this.d[k][c]);
     
     return new Mat(r);
}   
 
 
 final public  Mat isqrt() {
     int N = this.d.length;
     int M = this.d[0].length;
     
     for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           this.d[k][c] = Math.sqrt(this.d[k][c]);
     
     return this;
}   
 
  
 
 
 final  public  Mat sqrt() {
    int N = this.d.length;
    int M = this.d[0].length;
    double [][]r = new double[N][M];
    for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           r[k][c] = Math.sqrt(this.d[k][c]);
     
     return new Mat(r);
}   
 
 
 final  public  Mat pisqrt() {
    int rN = fnrows;
    int  nthreads = ConcurrencyUtils.getNumberOfThreads();
  
  // the matrix is split by row regions and the mutliplication in each such region
  // will be performed in parallel by a seperate thread
     nthreads = Math.min(nthreads, rN);
  
     Future<?>[] futures = new Future[nthreads];
            
      int   rowsPerThread = (int)(rN / nthreads)+1;  // how many rows the thread processes

      int threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
        // define the row region for the current thread
      final int  firstRow = threadId * rowsPerThread;   // start of rows range 
      final int  lastRow =   threadId == nthreads-1? rN: firstRow+rowsPerThread;  // end of rows range
        
 futures[threadId] = GlobalValues.execService.submit(new Runnable() {
    public void run()  {
      int  a = firstRow;   // the first row of the matrix that this thread processes
      while (a < lastRow) {  // the last row of the matrix that this thread processes
             for (int j=0; j < fncols; j++)
          d[a][j] = Math.sqrt(d[a][j]);
        a++;     
 }
    } // run
 }
      );
        threadId++;
        
  }  // for all threads

  ConcurrencyUtils.waitForCompletion(futures);
 
 
     return this;
}
 
 
 
 
 final  public  Mat psqrt() {
    int rN = fnrows;
    int  nthreads = ConcurrencyUtils.getNumberOfThreads();
    int M = this.d[0].length;
    final double [][]r = new double[rN][M];
    
  // the matrix is split by row regions and the mutliplication in each such region
  // will be performed in parallel by a seperate thread
     nthreads = Math.min(nthreads, rN);
  
     Future<?>[] futures = new Future[nthreads];
            
      int   rowsPerThread = (int)(rN / nthreads)+1;  // how many rows the thread processes

      int threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
        // define the row region for the current thread
      final int  firstRow = threadId * rowsPerThread;   // start of rows range 
      final int  lastRow =   threadId == nthreads-1? rN: firstRow+rowsPerThread;  // end of rows range
        
 futures[threadId] = GlobalValues.execService.submit(new Runnable() {
    public void run()  {
      int  a = firstRow;   // the first row of the matrix that this thread processes
      while (a < lastRow) {  // the last row of the matrix that this thread processes
             for (int j=0; j < fncols; j++)
          r[a][j] = Math.sqrt(d[a][j]);
        a++;     
 }
    } // run
 }
      );
        threadId++;
        
  }  // for all threads

  ConcurrencyUtils.waitForCompletion(futures);
 
 
     return new Mat(r);
}
 
 
 final  public static double [] sin(double [] x)
 {
     int N = x.length;
     double [] r = new double[N];
     for (int k=0; k<N; k++)
         r[k] = Math.sin(x[k]);
     
     return r;
 }
 
final  public static double [][] sin(double [] []x)
 {
     int N = x.length;
     int M = x[0].length;
     double [][] r = new double[N][M];
     for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           r[k][c] = Math.sin(x[k][c]);
     
     return r;
 }
 
  final  public  static double  sin(double d) {
    return Math.sin(d);
}   

  final  public static Mat cos(Mat M) {
    return new Mat(DoubleArray.cos( M.getRef() ), false);
}   

  final public static double  cos(double d) {
    return Math.cos(d);
}   

  
 final  public static double [] cos(double [] x) {
     int N = x.length;
     double [] r = new double[N];
     for (int k=0; k<N; k++)
         r[k] = Math.cos(x[k]);
   return r;
 }
   
 
 final  public static double [][] cos(double [] []x)
 {
     int N = x.length;
     int M = x[0].length;
     double [][] r = new double[N][M];
     for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           r[k][c] = Math.cos(x[k][c]);
     
     return r;
 }
 
  final  public static Mat tan(Mat M) {
    return new Mat(DoubleArray.tan( M.getRef() ), false);
}   

  final  public  static double  tan(double d) {
    return Math.tan(d);
}   

 final  public static double []  tan(double [] x) {
       int N = x.length;
     double [] r = new double[N];
     for (int k=0; k<N; k++)
         r[k] = Math.tan(x[k]);
   return r;
 }
         

 final  public static double [][] tan(double [] []x)
 {
     int N = x.length;
     int M = x[0].length;
     double [][] r = new double[N][M];
     for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           r[k][c] = Math.tan(x[k][c]);
     
     return r;
 }
   final public  static Mat atan(Mat M) {
   	return new Mat(DoubleArray.atan( M.getRef() ), false);
}   

   final  public  static double  atan(double d) {
      	return Math.atan(d);
}   
    
  final  public static double []  asin(double [] x) {
       int N = x.length;
     double [] r = new double[N];
     for (int k=0; k<N; k++)
         r[k] = Math.asin(x[k]);
     
     return r;
 }

  
 final  public static double [][] asin(double [] []x)
 {
     int N = x.length;
     int M = x[0].length;
     double [][] r = new double[N][M];
     for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           r[k][c] = Math.asin(x[k][c]);
     
     return r;
 }
 
 final public static Mat asin(Mat M) {
      	return new Mat(DoubleArray.asin( M.getRef() ), false);
}   

 final public  static double  asin(double d) {
      	return Math.asin(d);
}   
  
final public  static Mat acos(Mat M) {
      	return new Mat(DoubleArray.acos( M.getRef() ), false);
}   

 final public static double []  acos(double [] x) {
       int N = x.length;
     double [] r = new double[N];
     for (int k=0; k<N; k++)
         r[k] = Math.acos(x[k]);
     
     return r;
 }

 
 final public static double [][] acos(double [] []x)
 {
     int N = x.length;
     int M = x[0].length;
     double [][] r = new double[N][M];
     for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           r[k][c] = Math.acos(x[k][c]);
     
     return r;
 }
 
final public static double  acos(double d) {
      	return Math.acos(d);
}   
  
 
final public static Mat  sinh(Mat M) {
      	return new Mat(DoubleArray.sinh( M.getRef() ), false);
}   

 final public static double []  sinh(double [] x) {
       int N = x.length;
     double [] r = new double[N];
     for (int k=0; k<N; k++)
         r[k] = Math.sinh(x[k]);
     
     return r;
   
 }
 
 
 final public static double [][] sinh(double [] []x)
 {
     int N = x.length;
     int M = x[0].length;
     double [][] r = new double[N][M];
     for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           r[k][c] = Math.sinh(x[k][c]);
     
     return r;
 }

final public  static double  sinh(double d) {
      	return Math.sinh(d);
}   
   
final public  static Mat cosh(Mat M) {
      	return new Mat(DoubleArray.cosh( M.getRef() ), false);
}   

 final public static double []  cosh(double [] x) {
       int N = x.length;
     double [] r = new double[N];
     for (int k=0; k<N; k++)
         r[k] = Math.cosh(x[k]);
   
     return r;
 }
         
 
 final public static double [][] cosh(double [] []x)
 {
     int N = x.length;
     int M = x[0].length;
     double [][] r = new double[N][M];
     for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           r[k][c] = Math.cosh(x[k][c]);
     
     return r;
 }
 
final public static double  cosh(double d) {
      	return Math.cosh(d);
}   
  
final public  static Mat tanh(Mat M) {
      	return new Mat(DoubleArray.tanh( M.getRef() ), false);
}   

 final public static double []  tanh(double [] x) {
       int N = x.length;
     double [] r = new double[N];
     for (int k=0; k<N; k++)
         r[k] = Math.tanh(x[k]);
     
     return r;
 }
 
 
 final public static double [][] tanh(double [] []x)
 {
     int N = x.length;
     int M = x[0].length;
     double [][] r = new double[N][M];
     for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           r[k][c] = Math.tanh(x[k][c]);
     
     return r;
 }
 
         
final public static double  tanh(double d) {
      	return Math.tanh(d);
}   
  
 final public static Mat exp(Mat M) {
      	return new Mat(DoubleArray.exp( M.getRef() ), false);
}   

 final public static double [] exp(double [] x) {
       int N = x.length;
     double [] r = new double[N];
     for (int k=0; k<N; k++)
         r[k] = Math.exp(x[k]);
     
     return r;
 }

 
 final public static double [][] exp(double [] []x)
 {
     int N = x.length;
     int M = x[0].length;
     double [][] r = new double[N][M];
     for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           r[k][c] = Math.exp(x[k][c]);
     
     return r;
 }
 
final public  static double  exp(double d) {
    return Math.exp(d);
}   
 
final public static Mat log(Mat M) {
    return new Mat(DoubleArray.log( M.getRef() ), false);
}   

final public static double []  log(double [] x) {
    int N = x.length;
    double [] r = new double[N];
    for (int k=0; k<N; k++)
         r[k] = Math.log(x[k]);
   
    return r;
 }


 final public static double [][] log(double [] []x)
 {
     int N = x.length;
     int M = x[0].length;
     double [][] r = new double[N][M];
     for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           r[k][c] = Math.log(x[k][c]);
     
     return r;
 }

final public static double  log(double d) {
      	return Math.log(d);
}   
 
final public static Mat log2(Mat M) {
      	return new Mat(DoubleArray.log2( M.getRef() ), false);
}   

final public static double []  log2(double [] x) {
    int N = x.length;
     double [] r = new double[N];
     for (int k=0; k<N; k++)
         r[k] = Math.log(x[k])/GlobalValues.log2Conv;
   
     return r;
 }


 final public static double [][] log2(double [] []x)
 {
     int N = x.length;
     int M = x[0].length;
     double [][] r = new double[N][M];
     for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           r[k][c] = Math.log(x[k][c])/GlobalValues.log2Conv;
     
     return r;
 }

final public static double  log2(double d) {
    return Math.log(d)/GlobalValues.log2Conv;
}   
 

final public static Mat log10(Mat M) {
    return new Mat(DoubleArray.log10( M.getRef() ), false);
}   

 final public static double []  log10(double [] x) {
       int N = x.length;
     double [] r = new double[N];
     for (int k=0; k<N; k++)
         r[k] = Math.log10(x[k]);
   
     return r;
 }
         
 
 final public static double [][] log10(double [] []x)
 {
     int N = x.length;
     int M = x[0].length;
     double [][] r = new double[N][M];
     for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           r[k][c] = Math.log10(x[k][c]);
     
     return r;
 }
 
final public static double  log10(double d) {
    return Math.log10(d);
}   
     
final public static Mat  abs(Mat M) {
    return new Mat(DoubleArray.abs( M.getRef() ), false);
}   

 final public static double [] abs(double [] x) {
       int N = x.length;
     double [] r = new double[N];
     for (int k=0; k<N; k++)
         r[k] = Math.abs(x[k]);
   return r;
 }
 
 
 final public static double [][] abs(double [] []x)
 {
     int N = x.length;
     int M = x[0].length;
     double [][] r = new double[N][M];
     for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           r[k][c] = Math.abs(x[k][c]);
     
     return r;
 }
         
final public static double  abs(double d) {
    return Math.abs(d);
}   


final  public static Mat  ceil(Mat M) {
    return new Mat(DoubleArray.ceil( M.getRef() ), false);
}   

 final public static double [] ceil(double [] x) {
       int N = x.length;
     double [] r = new double[N];
     for (int k=0; k<N; k++)
         r[k] = Math.ceil(x[k]);
   return r;
 }
 
 
 final public static double [][] ceil(double [] []x)
 {
     int N = x.length;
     int M = x[0].length;
     double [][] r = new double[N][M];
     for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           r[k][c] = Math.ceil(x[k][c]);
     
     return r;
 }
         
final public static int  ceil(double d) {
    return (int)Math.ceil(d);
}   

final public static Mat  floor(Mat M) {
   return new Mat(DoubleArray.floor( M.getRef() ), false);
}   

 final public static double []   floor(double [] x) {
   int N = x.length;
   double [] r = new double[N];
   for (int k=0; k<N; k++)
    r[k] = Math.floor(x[k]);
   return r;
 }
         
 
 final public static double [][] floor(double [] []x)
 {
     int N = x.length;
     int M = x[0].length;
     double [][] r = new double[N][M];
     for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           r[k][c] = Math.floor(x[k][c]);
     
     return r;
 }
 
final public static int  floor(double d) {
    return (int)Math.floor(d);
}   

final public static Mat  round(Mat M) {
    return new Mat(DoubleArray.round( M.getRef() ), false);
}   

 final public static double []  round(double [] x) {
       int N = x.length;
     double [] r = new double[N];
     for (int k=0; k<N; k++)
         r[k] = Math.round(x[k]);
  return r; 
 }
 
 
 final public static double [][] round(double [] []x)
 {
     int N = x.length;
     int M = x[0].length;
     double [][] r = new double[N][M];
     for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           r[k][c] = Math.round(x[k][c]);
     
     return r;
 }
         
final public static int  round(double d) {
    return (int)Math.round(d);
}   


final public static Mat  sqrt(Mat M) {
    return new Mat(DoubleArray.sqrt( M.getRef() ), false);
}   

 final public static double [] sqrt(double [] x) {
    int N = x.length;
    double [] r = new double[N];
    for (int k=0; k<N; k++)
        r[k] = Math.sqrt(x[k]);
   return r;
 }
         
 
 
 final public static double [][] sqrt(double [] []x)
 {
     int N = x.length;
     int M = x[0].length;
     double [][] r = new double[N][M];
     for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           r[k][c] = Math.sqrt(x[k][c]);
     
     return r;
 }
 
 
final public static double  sqrt(double d) {
   return Math.sqrt(d);   
}

final public  static Mat  toDegrees(Mat M) {
   return new Mat(DoubleArray.toDegrees( M.getRef() ), false);
}   

 final public static double [] toDegrees(double [] x) {
   int N = x.length;
   double [] r = new double[N];
   for (int k=0; k<N; k++)
         r[k] = Math.toDegrees(x[k]);
   return r;
 }
 
 
 final public static double [][] toDegrees(double [] []x)
 {
     int N = x.length;
     int M = x[0].length;
     double [][] r = new double[N][M];
     for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           r[k][c] = Math.toDegrees(x[k][c]);
     
     return r;
 }
         
final public static double   toDegrees(double d) {
    return Math.toDegrees(d);
}   


final public static Mat  toRadians(Mat M) {
    return new Mat(DoubleArray.toRadians( M.getRef() ), false);
}   

 final public static double [] toRadians(double [] x) {
       int N = x.length;
     double [] r = new double[N];
     for (int k=0; k<N; k++)
         r[k] = Math.toRadians(x[k]);
   return r;
 }
         
 
 
 final public static double [][] toRadians(double [] []x)
 {
     int N = x.length;
     int M = x[0].length;
     double [][] r = new double[N][M];
     for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           r[k][c] = Math.toRadians(x[k][c]);
     
     return r;
 }

final public static double  toRadians(double d) {
    return Math.toRadians(d);
}   

final public static Mat  pow(Mat M, double exponent) {
    return new Mat(DoubleArray.pow( M.getRef(), exponent ), false);
}   

 final public static double []  pow(double [] x, double exponent) {
       int N = x.length;
     double [] r = new double[N];
     for (int k=0; k<N; k++)
         r[k] = Math.pow(x[k],exponent);
   return r;
 }

 
 
 final public static double [][] pow(double [] []x, double exponent)
 {
     int N = x.length;
     int M = x[0].length;
     double [][] r = new double[N][M];
     for (int k=0; k<N; k++)
         for (int c=0; c<M; c++)
           r[k][c] = Math.pow(x[k][c], exponent);
     
     return r;
 }
        
         
final public  static double  pow(double val, double exponent) {
   return Math.pow(val, exponent);
}   

final public  static int pow(double val, int exponent) {
   return (int) Math.pow(val, exponent);
}   

final public  static Mat prod(Mat M)  {
    if (M.getRowsNumber()==1)
        return new Mat(DoubleArray.product( M.getRowRef(1)));
    else
    return new Mat(DoubleArray.product( M.getRef()), false );
}

 final public static Mat  prod(double [] x)  {
     Mat m = new Mat(x);
     return  prod(m);
 }

final public static Mat cumsum(Mat M)  {
    if (M.getRowsNumber()==1)
        return new Mat(DoubleArray.cumSum( M.getRowRef(1)), false );
    else
    return new Mat(DoubleArray.cumSum( M.getRef()), false );
}


 final public static Mat  cumsum(double [] x)  {
     Mat m = new Mat(x);
     return  cumsum(m);
 }

final public static Mat cumprod(Mat M)  {
    if (M.getRowsNumber()==1)
        return new Mat(DoubleArray.cumProduct( M.getRowRef(1)), false );
    else
    return new Mat(DoubleArray.cumProduct( M.getRef()), false );
}

 final public static Mat  cumprod(double [] x)  {
     Mat m = new Mat(x);
     return  cumprod(m);
 }

	
final public static Mat sort(Mat M)  {
    if (M.getRowsNumber()==1)
        return new Mat(DoubleArray.sort( M.getRowRef(1)), false );
    else
        return new Mat(DoubleArray.sort( M.getRef(), 0), false );
    }
	

final public  static Mat sort(Mat M, int column)  {
    return new Mat(DoubleArray.sort( M.getRef(), column), false );
}
	
	
final public  static Mat transpose(Mat M)  {
    return new Mat(DoubleArray.transpose( M.getRef()), false );
}

 final public static double[][]  transpose(double [][] x)  {
     Mat m = new Mat(x);
     return DoubleArray.transpose( x);
 }
 
 final public static Mat  transpose(double [] x)  {
     Mat m = new Mat(x);
     return transpose(m);
 }
	
 final public static Mat t(Mat M)  {
    return transpose(M);
    }

 final public static Mat t(double [] x)  {
    return transpose(x);
    }
 
 final public static double [][]  t(double [] [] x)  {
       return transpose(x);
}

 final public static Object  min(Mat M)  {
    if (M.getRowsNumber()==1)
        return DoubleArray.min( M.getRowRef(1));
    else 
    if (M.getColumnsNumber()==1)
    return DoubleArray.min( M.getColumnCopy(1));
    else
    return new Mat(DoubleArray.min( M.getRef()), false );
}
	
	
 final public static Object  max(Mat M)  {
    if (M.getRowsNumber()==1)
        return DoubleArray.max( M.getRowRef(1));
    else if (M.getColumnsNumber() == 1)
        return DoubleArray.max( M.getColumnCopy(1));
    else
    return new Mat(DoubleArray.max( M.getRef()), false );
}
	
 
 final public static double [] max(double [][] M)  {
     return DoubleArray.max( M);
}
	
 final public static double  max(double [] M)  {
     return DoubleArray.max( M);
}
 
 
 final public static double [] min(double [][] M)  {
     return DoubleArray.min( M);
}
	
 final public static double  min(double [] M)  {
     return DoubleArray.min( M);
}

 final public static Mat identity(int m)  {
    return new Mat(DoubleArray.identity(m), false );
}
	
final public  static Mat id(int m)  {
    return identity(m);
}

final public static double [][]  Identity(int m)  {
    return  DoubleArray.identity(m);
}
	
final public  static double[][]  Id(int m)  {
    return Identity(m);
}
	
final public  static Mat diagonal(int m, double c)  {
    return new Mat(DoubleArray.diagonal(m, c), false );
}
	
final public  static Mat diag(int m, double c)  {
    return diagonal( m,  c);
}
        
final public static Mat diag(int m) {
    return diagonal(m, m);
}
	
final public static Mat diag(double [] c) {
    return diagonal(c);
 }
	
final public static Mat diag(Mat diagm) {
    int Nrows = diagm.Nrows();
    int Ncols = diagm.Ncols();
    if (Nrows != 1 && Ncols != 1) 
        return diagm;
    if (Nrows==1) {
        Mat diagmr = new Mat(Ncols, Ncols);
        for (int c = 0; c < Ncols; c++)
            diagmr.d[c][c] = diagm.d[0][c];
        return diagmr;
    }
    else {
        Mat diagmr = new Mat(Nrows, Nrows);
        for (int r = 0; r< Nrows; r++)
            diagmr.d[r][r] = diagm.d[r][0];
       return diagmr;
    }
}
        

final public  static Mat diagonal(double[] c)  {
    return new Mat(DoubleArray.diagonal( c) , false);
}
	
	
final public  static Mat diagonal(Mat M, int m)  {
    return new Mat(DoubleArray.getDiagonal( M.d, m), false );
}
	
  final public  static Mat diag(Mat M, int m)  {
    return diagonal( M, m);
}
	
final public  static double [][] Diagonal(int m, double c)  {
    return DoubleArray.diagonal(m, c);
}
	
final public  static double [][] Diag(int m, double c)  {
    return Diagonal( m,  c);
}
        
final public static double [][] Diag(int m) {
    return Diagonal(m, m);
 }
	
final public  static double [][] Diagonal(double[] c)  {
    return DoubleArray.diagonal( c);
    }
	
final public  static double [][] Diag(double[] c)  {
    return Diagonal( c);
}
	
final public  static Mat one(int m, int n)  {
    return new Mat(DoubleArray.one(m, n), false );
}
	
final public  static Mat ones(int m, int n)  {
    return new Mat(DoubleArray.one(m, n), false );
}
	
final public  static Mat ones(int n)  {
    return new Mat(DoubleArray.one(n, n), false );
}
	
final public  static double [][] Ones(int m, int n)  {
    return DoubleArray.one(m, n);
}
	
final public  static double []  Ones(int n)  {
    double [] vec = new double[n];
    for (int k=0; k<n; k++)
        vec[k] = 1.0;
    return vec;
    
}
	
final public  static double [][]  One(int m, int n)  {
    return DoubleArray.one(m, n);
}
	
final public  static Mat zero(int m, int n)  {
    return new Mat(DoubleArray.zero(m, n), false );
}

final public  static Mat zeros(int m, int n)  {
    return new Mat(DoubleArray.zero(m, n), false );
}
	
final public  static Mat zeros(int  n)  {
    return new Mat(DoubleArray.zero(n, n), false );
}
        
final public  static double [][]  Zero(int m, int n)  {
    return DoubleArray.zero(m, n);
}
		
final public  static double [][] Zeros(int m, int n)  {
    return DoubleArray.zero(m, n);
 }
	
final public  static double [] Zeros(int  n)  {
    double [] vec = new double[n];
    return vec;
     }
        
 final public static Mat eye(int m, int n) {
    Mat mat = new Mat(DoubleArray.zero(m,n), false);
    for (int r=0; r<m; r++) {
        if (r>n) break;
            mat.d[r][r] = 1;
             }
        return mat;
        }
                
final public static Mat eye(int m) {
    return eye(m, m);
    }
     
final public static double [][]  Eye(int m, int n) {
    double [][]  mat = DoubleArray.zero(m,n);
     for (int r=0; r<m; r++) {
            if (r>n) break;
                  mat[r][r] = 1;
             }
        return mat;
        }
                
final public static double [][] Eye(int m) {
    return Eye(m, m);
}
     
final public  static Mat fill(int m, int n, double c)  {
    return new Mat(DoubleArray.fill(m, n, c), false );
 }
	
final public  static double [][] Fill(int m, int n, double c)  {
    return DoubleArray.fill(m, n, c);
}

final public  static double [] Fill(int m,  double c)  {
    double [] vec = new double[m];
    for (int k=0; k<m; k++)   vec[k] = c;
        return vec;
}

final public   static Mat random(int m)  {
    return random(m, m);
}
	
final public  static Mat rand(int m)  {
    return  random(m);
}
	
final public  static double  rand()  {
    return  Math.random();
}
        
final public  static Mat random(int m, int  n)  {
    return new Mat(DoubleArray.random(m, n), false );
}
	
final public static Mat rand( int [] dims) {
    return new Mat(DoubleArray.random(dims[0], dims[1]), false);
   }
        
final public static Mat random( int [] dims) {
    return new Mat(DoubleArray.random(dims[0], dims[1]), false);
}

final public  static Mat rand(int m,int  n)  {
    return random( m,  n);
}
	
final public static Mat random(int m, int n, double min, double max)  {
    return new Mat(DoubleArray.random(m, n, min, max), false );
}
	
final public static Mat rand(int m, int n, double min, double max)  {
        return random( m,  n,  min,  max);
 }
	
final public static Mat random(int m, int n, double[] min, double[] max)  {
        return new Mat(DoubleArray.random(m, n, min, max), false );
 }
	
 final public static Mat rand(int m, int n, double[] min, double[] max)  {
        return random( m,  n,  min,  max);
    }
        
final   public   static double [][] Random(int m)  {
        return Random(1, m);
    }
	
final     public  static double [] Rand(int n)  {
        double [] vec = new double[n];
        for (int k=0; k<n; k++)
            vec[k] = Math.random();
        return vec;
    }
    
final     public  static double [][] Random(int m, int  n)  {
           return DoubleArray.random(m, n);
    }
	
final      public static double [][] Rand( int [] dims) {
            return DoubleArray.random(dims[0], dims[1]);
    }
        
final      public static double [][] Random( int [] dims) {
            return DoubleArray.random(dims[0], dims[1]);
    }
    
final      public  static double [][] Rand(int m,int  n)  {
            return Random( m,  n);
    }
	
final      public static double [][] Random(int m, int n, double min, double max)  {
            return DoubleArray.random(m, n, min, max);
    }
	
final       public static double [][] Rand(int m, int n, double min, double max)  {
        return Random( m,  n,  min,  max);
    }
	
final     public static double [][] Random(int m, int n, double[] min, double[] max)  {
        return DoubleArray.random(m, n, min, max);
    }
	
final     public static double [][] Rand(int m, int n, double[] min, double[] max)  {
        return Random( m,  n,  min,  max);
    }
	
final     public static Mat increment(int m, int n, double begin, double pitch)  {
        return new Mat(DoubleArray.increment(m, n, begin, pitch), false );
    }
                
final     public static Mat inc(double begin, double pitch, double end)  {
           if (begin > end && pitch >0)
               return new Mat(DoubleArray.increment(end, pitch, begin));
           if (begin < end && pitch < 0)
               return new Mat(DoubleArray.increment(end, pitch, begin));
	   return new Mat(DoubleArray.increment(begin, pitch, end ));
    }
        
       
final      public static double [] Inc(double begin, double pitch, double end)  {
           if (begin > end && pitch >0)
               return  DoubleArray.increment(end, pitch, begin);
           if (begin < end && pitch < 0)
               return DoubleArray.increment(end, pitch, begin);
	   return  DoubleArray.increment(begin, pitch, end );
    }
     
final      public static Mat increment(double begin, double pitch, double end)  {
        return inc(begin, pitch, end );
    }
    	
final     public static Mat inc(int m, int n, double begin, double pitch)  {
            return increment( m,  n,  begin,  pitch);
    }
	
final     public static Mat increment(int m, int n, double[] begin, double[] pitch)  {
            return new Mat(DoubleArray.increment(m, n, begin, pitch), false );
    }
	
final     public static Mat inc(int m, int n, double[] begin, double[] pitch)  {
        return increment( m,  n,  begin,  pitch); 
    }
	
        
final public static Mat  linspace(double startv,  double endv)   {
    int  nP = 100;  // use 100 as default number of points
    double []  v = new double [nP];
    double  dx = (endv-startv)/(nP-1);
    for (int i = 0; i< nP; i++)
        v[i] = startv +  i * dx;

    return new Mat(v, true);
}

final public static Mat  linspace(double startv,  double endv, int nP)   {
    double []  v = new double [nP];
    double  dx = (endv-startv)/(nP-1);
    for (int i = 0; i< nP; i++)
        v[i] = startv +  i * dx;

    return new Mat(v, true);
}


// use by default logspace=10
final public static Mat  logspace(double  startOrig,  double endOrig,  int nP) {
    return logspace(startOrig, endOrig, nP, 10.0);
}

final public  static Mat  logspace(double startOrig,  double endOrig, int  nP,  double  logBase)  {
    boolean  positiveTransformed = false;
    double    transformToPositive = 0.0;
                
    double  start = startOrig;  double   end=endOrig;   // use these values to handle negative values
    boolean  axisReversed = false;
    if (start > end)   {   // reverse axis
            start = endOrig; end = startOrig; axisReversed = true;
        }
                
     if (start <= 0)  {  // make values positive
             transformToPositive = -start+1;  start = 1;     
             end = end+transformToPositive;  positiveTransformed = true;
        }
     double    logBaseFactor = 1.0/java.lang.Math.log10(logBase);
     double    start_tmp = java.lang.Math.log10(start)*logBaseFactor;
     double    end_tmp = java.lang.Math.log10(end)*logBaseFactor;
     //println("logBaseFactor = "+logBaseFactor+"  start_tmp = "+start_tmp+"  end_tmp = "+end_tmp)
                
    double  []   values = new double [nP];
    double  dx     = (end_tmp-start_tmp) / (nP-1);
    for (int i=0; i<nP; i++) 
        values[i] = java.lang.Math.pow( logBase, (start_tmp +  i * dx));
		
        if  (positiveTransformed)    // return to the original range of values
                {
              for ( int i=0; i<nP; i++)
		  values[ i ]  -=  -transformToPositive;
		  start -= transformToPositive;
                }

                if (axisReversed)  {
                    double [] valuesNew = new double [nP];
                    valuesNew[0] = values[nP-1];
                    for ( int i = 1; i<nP; i++)  {
                        valuesNew[i]  = valuesNew[ i-1]-(values[i]-values[i-1]);
                    }
                    return new Mat(valuesNew, true);
                }
                          
     return new Mat(values, true);
}

    final public static Mat copy(Mat M)  {
        return new Mat(DoubleArray.copy( M.getRef()), false );
}
	
///////////////////////////////////////////////////////////////////
// Linear algebra methods, coming from JMathArray, based on JAMA // 
///////////////////////////////////////////////////////////////////
	
    	
    final public Mat minus( Mat v2)  {
        
         // larger number of threads than the number of cores of the system deteriorate performance
        int nthreads  = Math.min(numOfThreads, fnrows);
        Future<?>[] futures = new Future[nthreads];
        
        int rowsPerThread = (int)(fnrows/nthreads)+1; // how many rows the thread processes
        
        double [][] v2d = new double[fnrows][fncols];
        
  int threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
    final int  firstRow = threadId * rowsPerThread;
    final int  lastRow =   threadId == nthreads-1? fnrows: firstRow+rowsPerThread;
    
    
 futures[threadId] = GlobalValues.execService.submit(new Runnable() {
    public void run()  {
      int  row = firstRow;   // the first row of the matrix that this thread processes
      while (row < lastRow) {  // the last row of the matrix that this thread processes
             double [] rd = d[row];
             double [] rv2 = v2.d[row];
             double [] rv2d = v2d[row];
             int  col = 0;
             while (col < fncols )  {
                 rv2d[col] =  rd[col] - rv2[col]; 
                 col++;
             }
             row++;
      }
   }
 });
        threadId++;
        
  }  // for all threads

   // wait for all the multiplication worker threads to complete
  ConcurrencyUtils.waitForCompletion(futures);
  
        
        return new Mat(v2d, true);
    }
	
    final public Mat minus( double [][] v2)  {
          
         // larger number of threads than the number of cores of the system deteriorate performance
        int nthreads  = Math.min(numOfThreads, fnrows);
        Future<?>[] futures = new Future[nthreads];
        
        int rowsPerThread = (int)(fnrows/nthreads)+1; // how many rows the thread processes
        
        double [][] v2d = new double[fnrows][fncols];
        
  int threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
    final int  firstRow = threadId * rowsPerThread;
    final int  lastRow =   threadId == nthreads-1? fnrows: firstRow+rowsPerThread;
    
    
 futures[threadId] = GlobalValues.execService.submit(new Runnable() {
    public void run()  {
      int  row = firstRow;   // the first row of the matrix that this thread processes
      while (row < lastRow) {  // the last row of the matrix that this thread processes
             double [] rd = d[row];
             double [] rv2 = v2[row];
             double [] rv2d = v2d[row];
             int  col = 0;
             while (col < fncols )  {
                 rv2d[col] =  rd[col] - rv2[col]; 
                 col++;
             }
             row++;
      }
   }
 });
        threadId++;
        
  }  // for all threads

   // wait for all the multiplication worker threads to complete
  ConcurrencyUtils.waitForCompletion(futures);
  
        
        return new Mat(v2d, true);
    }

	
    final public Mat minus(Number v2)  {
        return new Mat(LinearAlgebra.minus( getRef(), v2.doubleValue()), false );
    }
	
    final public Mat plus( Mat v2)  {
        
         // larger number of threads than the number of cores of the system deteriorate performance
        int nthreads  = Math.min(numOfThreads, fnrows);
        Future<?>[] futures = new Future[nthreads];
        
        int rowsPerThread = (int)(fnrows/nthreads)+1; // how many rows the thread processes
        
        double [][] v2d = new double[fnrows][fncols];
        
  int threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
    final int  firstRow = threadId * rowsPerThread;
    final int  lastRow =   threadId == nthreads-1? fnrows: firstRow+rowsPerThread;
    
    
 futures[threadId] = GlobalValues.execService.submit(new Runnable() {
    public void run()  {
      int  row = firstRow;   // the first row of the matrix that this thread processes
      while (row < lastRow) {  // the last row of the matrix that this thread processes
             double [] rd = d[row];
             double [] rv2 = v2.d[row];
             double [] rv2d = v2d[row];
             int  col = 0;
             while (col < fncols )  {
                 rv2d[col] =  rd[col] + rv2[col]; 
                 col++;
             }
             row++;
      }
   }
 });
        threadId++;
        
  }  // for all threads

   // wait for all the multiplication worker threads to complete
  ConcurrencyUtils.waitForCompletion(futures);
  
        
        return new Mat(v2d, true);
    }
	
    final public Mat plus( double [][] v2)  {
          
         // larger number of threads than the number of cores of the system deteriorate performance
        int nthreads  = Math.min(numOfThreads, fnrows);
        Future<?>[] futures = new Future[nthreads];
        
        int rowsPerThread = (int)(fnrows/nthreads)+1; // how many rows the thread processes
        
        double [][] v2d = new double[fnrows][fncols];
        
  int threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
    final int  firstRow = threadId * rowsPerThread;
    final int  lastRow =   threadId == nthreads-1? fnrows: firstRow+rowsPerThread;
    
    
 futures[threadId] = GlobalValues.execService.submit(new Runnable() {
    public void run()  {
      int  row = firstRow;   // the first row of the matrix that this thread processes
      while (row < lastRow) {  // the last row of the matrix that this thread processes
             double [] rd = d[row];
             double [] rv2 = v2[row];
             double [] rv2d = v2d[row];
             int  col = 0;
             while (col < fncols )  {
                 rv2d[col] =  rd[col] + rv2[col]; 
                 col++;
             }
             row++;
      }
   }
 });
        threadId++;
        
  }  // for all threads

   // wait for all the multiplication worker threads to complete
  ConcurrencyUtils.waitForCompletion(futures);
  
        
        return new Mat(v2d, true);
    }

    // add v2 to all the columns 
    final public Mat plus(double [] v2) {
        int nrows = this.Nrows();
        int ncols = this.Ncols();
        double [][]res = new double[nrows][ncols];
        for (int col=0; col<ncols; col++)
          for (int row=0; row<nrows; row++)
             res[row][col] = d[row][col]+v2[row];
        
        return new Mat(res, true);
    }
                
    final public Mat minus(double [] v2) {
        int nrows = this.Nrows();
        int ncols = this.Ncols();
        double [][]res = new double[nrows][ncols];
        for (int col=0; col<ncols; col++)
          for (int row=0; row<nrows; row++)
             res[row][col] = d[row][col]-v2[row];
        
        return new Mat(res, true);
    }
                
        
    final public static Mat plus(Mat v,  double [][] v2)  {
       return v.plus(v2);
    }
	
  final public static Mat plus(double [][] v2, Mat v)  {
       return v.plus(v2);
    }
  
  
  final public static Mat minus(double [][] v2, Mat v)  {
       return new Mat(LinearAlgebra.minus( v2, v.d), false );
    }


  final public Mat negative(Mat v)  {
          return  multiply(-1.0);
    }

  final public static Mat neg(Mat v)  {
          return  v.negative(v);
    }

  final public Mat plus( Double v2)  {
        return new Mat(LinearAlgebra.plus( getRef(), v2.doubleValue()), false );
    }
	
        
    final public Mat div( Double v)  {
        return new Mat(LinearAlgebra.divide( getRef(), v.doubleValue()), false );
    }
	
       
final public Mat power(int n, Mat m)  {
    if (n < 0)
        return power(-n, inv(m));
    else if (n == 1) 
        return copy(m); 
    else {
        Mat  recurMat = power(n-1, m); 
        jMatrix  thisMatrix = new jMatrix(recurMat.d);
        jMatrix  mMatrix = new jMatrix(m.d);
        jMatrix  product = thisMatrix.times(mMatrix);   
        Mat   matProd = new Mat(product.getArray());
        return matProd;
     }
   }
	
        
    final public Mat power( Number n)  {
        double nv = n.doubleValue();
        int  niv = n.intValue();
            if (niv - nv  == 0)
                return power(n.intValue(), this);
                else 
            throw new IllegalArgumentException("Value "+n+" no yet supported for power method");
}
	
    final public Mat raise( Number n)  {
        return new Mat(LinearAlgebra.raise( getRef(), n.doubleValue()), false );
        }

    final public int  rows() {
            return this.size()[0];
        }
        
    final public int cols() {
            return this.size()[1];
        }
        
    // used to evaluate the point at matrix sizes where multithreading becomes faster
    public Mat multiplyTest( Mat v2, boolean useMultithreading)  {
        
            // return new Matrix(LinearAlgebra.times( getRef(),  v2.getRef()), false );
        final int    rN = d.length;   final int  rM = d[0].length;
        int    sN = v2.fnrows;  int   sM = v2.fncols;
  
    if (useMultithreading )  {
    
       
    // transpose first matrix that. This operation is very important in order to exploit cache locality
final double [][]  thatTrans = new double[sM][sN];
int  r=0; int c = 0;
while (r<sN) {
  c=0;
  while (c<sM) {
    thatTrans[c][r] = v2.d[r][c];
    c++;
  }
  r++;
}

  final double [][]   vr = new double[rN][sM];   // for computing the return Matrix
  int  nthreads = ConcurrencyUtils.getNumberOfThreads();
  nthreads = Math.min(nthreads, rN);
  
  Future<?>[] futures = new Future[nthreads];
            
  int   rowsPerThread = (int)(sM / nthreads)+1;  // how many rows the thread processes

  int threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
    final int  firstRow = threadId * rowsPerThread;
    final int  lastRow =   threadId == nthreads-1? sM: firstRow+rowsPerThread;
    
 futures[threadId] = GlobalValues.execService.submit(new Runnable() {
    public void run()  {
      int  a = firstRow;   // the first row of the matrix that this thread processes
      while (a < lastRow) {  // the last row of the matrix that this thread processes
             int  b = 0;
             while (b < rN )  {
                 double  s = 0.0;
                 int  c = 0;
                 while (c < rM) {
                    s += d[b][c] * thatTrans[a][c];
                    c++;
                   }
                vr[b][a]   = s;
                b++;
             }
             a++;
      }
   }
 });
        threadId++;
        
  }  // for all threads

  ConcurrencyUtils.waitForCompletion(futures);
  return new Mat(vr);
	}
   else  // serial multiplication
   {
       
       return new Mat(LinearAlgebra.times(getRef(), v2.d), false);
    }
    }
    
    
    
  
// native C multiplication using p-threads
public  Mat  pt  (double [][] that )  {
   double []  oneDThis = oneDDoubleArray( this.getArray()  );   // construct a DoubleMatrix for the receiver
   double []  oneDThat = oneDTransposeDoubleArray(that);   // construct a DoubleMatrix for the argument
   int  Arows = Nrows();  int   Acolumns = Ncols();
   int  Ccolumns = that[0].length;
   double []  result = new double [Arows*Ccolumns];
   
    gExec.Interpreter.NativeLibsObj.ccObj.pt(oneDThis, Arows, Acolumns, oneDThat, Ccolumns, result);
    
    double [][] rd = new double[Arows][Ccolumns];
    int cnt = 0;
    for (int  r = 0;  r < Arows;  r++) {
      for (int c=0;  c< Ccolumns; c++)  {
        rd[r][c] = result[cnt];
        cnt++;
      }
    }
    return new Mat(rd);
} 


public  Mat  pt  (Mat that )  {
  return pt(that.getArray());
}

final public Mat multiply( Mat v)  {
    final int    rN = Nrows();   final int  rM = Ncols();
    final int   sM = v.Ncols();
    double siz = (double)rN*(double)rM*(double)sM;
    boolean  useMultithreading = false;
    if (siz  >  gExec.Interpreter.GlobalValues.mulMultithreadingLimit) 
        useMultithreading = true;
   
    if (useMultithreading )   
         return new Mat( ParallelMult.pmul(d, v.d));
         else 
          return new Mat( SerialMult.times(this.d, v.d) );
     }
    
    
    public Mat multiplySerial( Mat v2)  {
       return new Mat(LinearAlgebra.times(getRef(), v2.d), false);
    }
    
    
    public Mat multiplySerial( double [][] d)  {
       return new Mat(LinearAlgebra.times(getRef(), d), false);
    }
    
    
    
final public Mat multiply( double  v)  {
        return new Mat(LinearAlgebra.times( getRef(), v), false );
    }

    

    public Mat multiply( double [][] v)  {
    final int    rN = Nrows();   final int  rM = Ncols();
    final int    sN = v.length;  int   sM = v[0].length;
    double siz = rN*rM*sM;
    boolean  useMultithreading = false;
    if (siz  >  gExec.Interpreter.GlobalValues.mulMultithreadingLimit) 
        useMultithreading = true;
   
    if (useMultithreading )  
         return new Mat( ParallelMult.pmul(d, v));
       else 
          return new Mat( SerialMult.times(this.d, v) );
     }
    
    
    
   
    
    public Mat multiply( double [] v2)  {
        int nrows = v2.length;
        double [][] vc = new double [nrows][1];
        for (int r=0; r<nrows; r++)
            vc[r][0] = v2[r];
        
        
          return new Mat(LinearAlgebra.times( getRef(),  vc), false );
	}
    
    final public Vec  multiply( Vec that) {
        int  rN = this.Ncols();   int  rM = this.Ncols();  // receiver's dimensions
        int  sN = that.length();
        double  sm = 0.0;
   
    if (rN == sN) {  // multiply column-wise
     Vec  rv = new Vec(rM);
      for (int c=0; c<rM; c++)  {
           sm = 0.0;
         for (int r=0; r<rN; r++) 
             sm += (d[r][c]*that.v[r]);
          rv.set(c,  sm);
        }
     return  rv;
 }
 else if (rM == sN) { // multiply row-wise
     Vec rv = new Vec(rN);
       for (int r=0; r < rN; r++) {
           sm = 0.0;
          for (int c=0; c<sN; c++)
            sm += (d[r][c]*that.v[c]);
          rv.set(r, sm);
       }
         return rv;
 }
 else
    return new Vec(1);
  
        
    }
		
    
    final public static Mat multiply( Mat mat, double [][] v2)  {
           return mat.multiply(v2);
	}
        

    final public Mat mult( Mat v2)  {
            return multiply(v2);
	}

 
    // convert using row storage the 2-D double array to 1-D float array
final public static float [] oneDFloatArray(double [][] x)  
  {
    int Nrows = x.length;
    int Ncols = x[0].length;
    float [] fa = new float[Nrows*Ncols];
    int  cnt = 0;
    int  r = 0;  int  c;
    while (r < Nrows)  {
      c = 0;
      while (c < Ncols) {
           fa[cnt] = (float)x[r][c];
           cnt++;
           c++;
      }
      r++;
      
    }
    return fa;
  }  
  
  // convert using row storage the 2-D double array to 1-D float array
final public static double [] oneDDoubleArray(double [][]x) 
  {
    int  Nrows = x.length;
    int  Ncols = x[0].length;
    double []  fa = new double[Nrows*Ncols];
    int  cnt = 0;
    int  r = 0; int  c;
    while (r < Nrows)  {
      c = 0;
      while (c < Ncols) {
           fa[cnt] = x[r][c];
           cnt++;
           c++;
      }
      r++;
      
    }
    return fa;
  }  
  
  

  
final public static double [] oneDTransposeDoubleArray(double [][]x)  
  {
    int Nrows = x.length;
    int Ncols = x[0].length;
    double []  fa = new double[Nrows*Ncols];
    int cnt = 0;
    int r = 0; int c = 0;
    while (c < Ncols) {
      r = 0;
      while (r < Nrows)  {
           fa[cnt] = x[r][c];
           cnt++;
           r++;
      }
      c++;
      
    }
    return fa;
  }  

    
// fast CUDA multiplication using CUBLAS, double precision
final public Mat  fmul( double [][] that)  {
   int  Arows = Nrows(); int  Acolumns = Ncols();
   int  Ccolumns = that[0].length;
   
   double siz = (double)Arows*(double)Acolumns*(double)Ccolumns;
   
   if ( siz < GlobalValues.mulMultithreadingLimit)
      return multiplySerial(that);
   else {
   float [] flmThis = oneDFloatArray( this.d  );   // construct a FloatMatrix for the receiver
   float [] flmThat = oneDFloatArray(that);  // construct a FloatMatrix for the argument
   float [] result = new float[Arows*Ccolumns];
   CUDAOps.KernelOps  km = new CUDAOps.KernelOps();  // construct a CUDA Operations object
    // perform the multiplication using CUDA
   km.sgemm(flmThis, Arows, Acolumns, flmThat, Ccolumns, result);
   
    double [][] rd = new double [Arows][Ccolumns];
    int cnt = 0;
    int  r = 0;  int  c;
    while (r < Arows ) {
      c = 0;
      while (c < Ccolumns) {
        rd[r][c] = (double)result[cnt];
        cnt++;
        c++;
      }
      r++;
    }
    return new Mat(rd);
   }
}   

final public Mat fmul(Mat that) {
    return this.fmul(that.d);
}

// fast CUDA multiplication, single precision
final  public double [][] fmmul( double[][] that)  { 
   float [] flmThis = oneDFloatArray( this.d  );   // construct a FloatMatrix for the receiver
   float [] flmThat = oneDFloatArray(that);  // construct a FloatMatrix for the argument
   int  Arows = Nrows();  int  Acolumns = Ncols();
   int  Ccolumns = that[0].length;
   float [] result = new float[Arows*Ccolumns];
   CUDAOps.KernelOps  km = new CUDAOps.KernelOps();  // construct a CUDA Operations object
    // perform the multiplication using CUDA
   km.cmm(flmThis, flmThat, result, Arows, Acolumns, Ccolumns);
   
    double [][]  rd = new double [Arows][Ccolumns];
    int cnt = 0;
    int  r = 0; int c;
    while (r < Arows ) {
      c = 0;
      while (c < Ccolumns) {
        rd[r][c] = (double)result[cnt];
        cnt++;
        c++;
      }
      r++;
    }
    return rd;
}   
  

final public Mat fmmul(Mat that) {
    return new Mat(this.fmmul(that.d));
}
  
// fast CUDA multiplication using CUBLAS, double precision
final public double [][]  dmul( double [][] that) {
   double []   flmThis = oneDDoubleArray( this.d  );   // construct a DoubleMatrix for the receiver
   double []  flmThat = oneDDoubleArray(that);  // construct a DoubleMatrix for the argument
   int   Arows = Nrows(); int  Acolumns = Ncols();
   int  Ccolumns = that[0].length;
   double  [] result = new double[Arows*Ccolumns];
   CUDAOps.KernelOps  km = new CUDAOps.KernelOps();  // construct a CUDA Operations object
    // perform the multiplication using CUDA
   km.dgemm(flmThis, Arows, Acolumns, flmThat, Ccolumns, result);
   
    double [] []  rd = new double [Arows][Ccolumns];
    int  cnt = 0;
    int  r = 0; int  c;
    while (r < Arows ) {
      c = 0;
      while (c < Ccolumns) {
        rd[r][c] = result[cnt];
        cnt++;
        c++;
      }
      r++;
    }
    return rd;
}   
  

final public Mat dmul(Mat that) {
    return new Mat(this.dmul(that.d));
}

// fast CUDA multiplication, double precision
final public double [][] dmmul(double [][] that) {
   double [] flmThis = oneDDoubleArray( this.d  );   // construct a FloatMatrix for the receiver
   double [] flmThat = oneDDoubleArray(that);  // construct a FloatMatrix for the argument
   int  Arows = Nrows();  int  Acolumns = Ncols();
   int  Ccolumns = that[0].length;
   double [] result = new double[Arows*Ccolumns];
   CUDAOps.KernelOps  km = new CUDAOps.KernelOps();  // construct a CUDA Operations object
    // perform the multiplication using CUDA
   km.cmmd(flmThis, flmThat, result, Arows, Acolumns, Ccolumns);
   
    double [][] rd = new double [Arows][Ccolumns];
    int  cnt = 0;
    int  r = 0;  int c;
    while (r < Arows ) {
      c = 0;
      while (c < Ccolumns) {
        rd[r][c] = result[cnt];
        cnt++;
        c++;
      }
      r++;
    }
    return rd;
}   
  

final public Mat dmmul(Mat that) {
    return new Mat(this.dmmul(that.d));
}
    final public Mat divide_LU( Mat v2)  {
        return new Mat(LinearAlgebra.divideLU( getRef(), v2.getRef()), false );
}
	
final public Mat divide_QR( Mat v2)  {
        return new Mat(LinearAlgebra.divideQR( getRef(), v2.getRef()), false );
}
	
/*
N=5;  A = rand(N,N);
b = rand(N,1)
x = A / b  // solve the system using the overloaded division operator 
shouldBeZero = A*x-b   // verify the solution
*/
final public Mat divide( Mat v2)  {
    return solve(this, v2);    
}
	
final public Mat div( Mat v2)  {
        return solve(this, v2);
}

	
final public double [][] div( double [][] v2)  {
        return solve(this.getArray(), v2);
}
	
final public double [] div( double []v2)  {
        return solve(this.getArray(), v2);
}
	
final public static Mat inverse_LU(Mat M)  {
        return new Mat(LinearAlgebra.inverseLU( M.getRef()), false );
}
	
        
final public static double [][]  inverse_LU(double [][]  dM)  {
        return  LinearAlgebra.inverseLU( dM);
}
	
final public static Mat inverse_QR(Mat M)  {
        return new Mat(LinearAlgebra.inverseQR( M.getRef()), false );
}
	
final public static double [][] inverse_QR(double [][] dM)  {
        return LinearAlgebra.inverseQR( dM );
}
	        
final public static Mat inverse(Mat M)  {
        return new Mat(LinearAlgebra.inverse( M.getRef()), false );
}
	
final public static Mat inv(Mat M)  {
        return inverse(M);
}
	
	
final public static double [][]  inverse(double [][]  dM)  {
    return LinearAlgebra.inverse( dM );
}
	
final public static double [][] inv(double [][] dM)  {
    return inverse(dM);
}

final public static Mat and(Mat a, Mat b) {
    double [][]am = a.getArray();
    int a_dx = am.length;
    int a_dy = am[0].length;
    double [][]bm = b.getArray();
    int b_dx = bm.length;
    int b_dy = bm[0].length;
            
	// both matrices must have the same size
    if ( (a_dy != b_dy) || (a_dx != b_dx) ) return null; 
     // create matrix
    double[][] values = new double[a_dy][a_dx];
    for (int xi=0; xi<a_dx ; xi++)
        for (int yi=0; yi<a_dy ; yi++)
                if ( (am[yi][xi] != 0.0) && (bm[yi][xi] != 0.0) )
                    values[yi][xi] = 1.0;
		else
                    values[yi][xi] = 0.0;
        
        return new Mat(values);
        }


       final  public static Mat or(Mat a, Mat b) {
            double [][]am = a.getArray();
            int a_dx = am.length;
            int a_dy = am[0].length;
            double [][]bm = b.getArray();
            int b_dx = bm.length;
            int b_dy = bm[0].length;
            
	// both matrices must have the same size
	if ( (a_dy != b_dy) || (a_dx != b_dx) ) return null; 
                // create matrix
	double[][] values = new double[a_dy][a_dx];
	for (int xi=0; xi<a_dx ; xi++)
	   for (int yi=0; yi<a_dy ; yi++)
                if ( (am[yi][xi] != 0.0) || (bm[yi][xi] != 0.0) )
                    values[yi][xi] = 1.0;
		else
		   values[yi][xi] = 0.0;
        
        return new Mat(values);
        }
        
       final  public static Mat xor(Mat a, Mat b) {
            double [][]am = a.getArray();
            int a_dx = am.length;
            int a_dy = am[0].length;
            double [][]bm = b.getArray();
            int b_dx = bm.length;
            int b_dy = bm[0].length;
            
	// both matrices must have the same size
	if ( (a_dy != b_dy) || (a_dx != b_dx) ) return null; 
                // create matrix
	double[][] values = new double[a_dy][a_dx];
	for (int xi=0; xi<a_dx ; xi++)
	   for (int yi=0; yi<a_dy ; yi++)
        	if (   ((am[yi][xi] != 0.0) && (bm[yi][xi] != 0.0))
				    || ((am[yi][xi] == 0.0) && (bm[yi][xi] == 0.0)) )
				{
					values[yi][xi] = 0.0;
				}
				else
				{
					values[yi][xi] = 1.0;
				}
	
        return new Mat(values);
        }
        
        
        
        
final public static Mat CholeskyL(Mat M) {
    CholeskyDecomposition choleskDec = LinearAlgebra.cholesky(M.getRef());
    Mat choleskMat = new Mat(choleskDec.getL().getArray());
    return choleskMat;
}
	
final public static double [][] CholeskyL(double [][] dM) {
    CholeskyDecomposition choleskDec = LinearAlgebra.cholesky(dM);
    return choleskDec.getL().getArray();
}
	
final public static Mat Cholesky_SPD(Mat M) {
    CholeskyDecomposition choleskDec = LinearAlgebra.cholesky(M.getRef());
    Mat choleskMat = new Mat(choleskDec.getL().getArray());
    return choleskMat;
}
	
final public static double [][] CholeskySPD(double [][] dM) {
    CholeskyDecomposition choleskDec = LinearAlgebra.cholesky(dM);
    double [][]  choleskMat = choleskDec.getL().getArray();
    return choleskMat;
}
        
final public static Mat CholeskySolve(Mat A,Mat b) {
    CholeskyDecomposition choleskDec = LinearAlgebra.cholesky(A.getRef());
    jMatrix jb = new jMatrix(b.d);
    jMatrix solvedMat = choleskDec.solve(jb);
    return new Mat(solvedMat.getArray());
}
	
final public static double [][] CholeskySolve(double [][] dA, double [][] db) {
    CholeskyDecomposition choleskDec = LinearAlgebra.cholesky(dA);
    jMatrix jb = new jMatrix(db);
    jMatrix solvedMat = choleskDec.solve(jb);
    return solvedMat.getArray();
}
	
final public static double [][] CholeskySolve(double [][] dA, double [] dbv) {
    double [][] db = new double[dbv.length][1];
    for (int k=0; k<dbv.length; k++)
        db[k][0] = dbv[k];
    
    CholeskyDecomposition choleskDec = LinearAlgebra.cholesky(dA);
    jMatrix jb = new jMatrix(db);
    jMatrix solvedMat = choleskDec.solve(jb);
    return solvedMat.getArray();
}
	
final public static Mat LU_L(Mat M) {
    return new Mat(LinearAlgebra.LU( M.getRef()).getL().getArray(), false );
}
	
final public static double [][] LU_L(double [][] dM) {
    return LinearAlgebra.LU( dM).getL().getArray();
}
        
final public static Mat L(Mat M) {
    return LU_L( M);
    }

final public static double [][]  L(double [][] M) {
    return LU_L( M);
    }
        
final public static Mat LU_P(Mat M) {
    return new Mat(LinearAlgebra.LU( M.getRef()).getL().getArray(), false );
}
	
final public static double [][] LU_P(double [][] dM) {
    return LinearAlgebra.LU( dM).getL().getArray();
}
	
final public static Mat LU_U(Mat M) {
    return new Mat(LinearAlgebra.LU( M.getRef()).getU().getArray(), false );
}
	
final public static double [][] LU_U(double [][] dM) {
    return  LinearAlgebra.LU( dM).getU().getArray();
}
	
final public static Mat U(Mat M) {
    return LU_U( M);
}
	
final public static double [][]  U(double [][] M) {
    return LU_U( M);
}
	
final public static double LU_det(Mat M) {
    return LinearAlgebra.LU( M.getRef()).det();
}
	
final public static double LU_det(double [][] dM) {
    return LinearAlgebra.LU( dM).det();
}
        
final public static double det(Mat M) {
    return LinearAlgebra.det(M.getRef());
}
        
 final public static double det(double [][] Ma) {
    return LinearAlgebra.det(Ma);
  }
	
 
final public static Mat LU_solve(Mat A, Mat b) {
    LUDecomposition LUDec = LinearAlgebra.LU(A.getRef());
    jMatrix jb = new jMatrix(b.d);
    jMatrix solvedMat = LUDec.solve(jb);
    return new Mat(solvedMat.getArray());
}
	
final public static double [][] LU_solve(double [][] A, double [][] b) {
    LUDecomposition LUDec = LinearAlgebra.LU(A);
    jMatrix jb = new jMatrix(b);
    jMatrix solvedMat = LUDec.solve(jb);
    return solvedMat.getArray();
}
	
final public static Mat QR_H(Mat M) {
    return new Mat(LinearAlgebra.QR( M.getRef()).getH().getArray(), false );
}
	
final public static double [][] QR_H(double [][] M) {
    return LinearAlgebra.QR( M).getH().getArray();
}
	
final public static Mat QR_Q(Mat M) {
    return new Mat(LinearAlgebra.QR( M.getRef()).getQ().getArray(), false );
}
	
final public static double [][] QR_Q(double [][] M) {
    return LinearAlgebra.QR( M).getQ().getArray();
}
	
final public static Mat Q(Mat M) {
    return QR_Q( M);
}
	
final public static double [][] Q(double [][] M) {
    return QR_Q( M);
}
	
final public static Mat QR_R(Mat M) {
    return new Mat(LinearAlgebra.QR( M.getRef()).getR().getArray(), false);
}

final public static double [][] QR_R(double [][] M) {
    return LinearAlgebra.QR( M).getR().getArray();
}
	
final public static Mat R(Mat M) {
    return QR_R( M); 
}
	
final public static double [][] R(double [][] M) {
    return QR_R( M); 
}
	
final public static Mat QR_solve(Mat A, Mat b) {
    QRDecomposition QRDec = LinearAlgebra.QR(A.getRef());
    jMatrix jb = new jMatrix(b.d);
    jMatrix solvedMat = QRDec.solve(jb);
    return new Mat(solvedMat.getArray());
}
	        
final public static double [][]  QR_solve(double [][] dA, double [][] db) {
    QRDecomposition QRDec = LinearAlgebra.QR(dA);
    jMatrix jb = new jMatrix(db);
    jMatrix solvedMat = QRDec.solve(jb);
    return solvedMat.getArray();
}
        
final public static Mat solve(Mat A, Mat b) {
    return new Mat(LinearAlgebra.solve( A.getRef(),b.getRef()), false);
}

final public static double [][]  solve(double [][]  dA,  double [][] db) {
    return LinearAlgebra.solve( dA, db);
}

final public static double []  solve(double [][]  dA,  double [] db) {
    double [][] bb = new double[1][1];
    bb[0] = db;
    double [][] res2d = LinearAlgebra.solve( dA, bb);
    double [] res = new double[db.length];
    for (int k=0; k<db.length; k++)
        res[k] = res2d[0][k];
return res;                
    
}

// solve with the CSparse
final public static double [] solve(Sparse A, double [] b) {
    return groovySci.math.array.Sparse.sparseSolve(A, b);
}

// solve with the MTJ Column Compressed Matrix
final public static double [] solve(CCMatrix A, double [] b) {
    return groovySci.math.array.CCMatrix.BiCGSolve(A, b);
}

// solve using EJML library
// EJML

/*
   A = Mat(3,3, 
   2.3, 4.5,  -0.23,
   0.34, -7.7, 0.4,
   -3.4, 6.5, -0.44)
   
   b = Vec(5.6, 4.5, -4.5)
   
   x = solveEJML(A, b)
   
   A*x-b
  */ 
final public static double []  solveEJML(double [][]  dA,  double [] db) {
    DenseMatrix64F    ddA = new DenseMatrix64F(dA);
    DenseMatrix64F ddb = new DenseMatrix64F(db.length, 1, true, db);
    DenseMatrix64F dsol = new DenseMatrix64F(db.length, 1);
            
    boolean solutionOK = org.ejml.ops.CommonOps.solve(ddA, ddb, dsol);
    
    return dsol.getData();
}


final public static double []  solveEJML(Mat A,  Vec  b) {
    return solveEJML(A.getArray(), b.getv());
}

final public static Mat solveEJML(Mat A,  Mat  b) {
    //  solve for the matrix b columns one by one
    int arows = A.Nrows(); int acols = A.Ncols();
    int brows = b.Nrows(); int bcols = b.Ncols();
    if (arows != brows) {
        System.out.println("in solveEJML incompatible matrix dimensions, arows = "+arows+", brows = "+brows);
        return A;
    }
    Mat results = new Mat(arows, bcols);
    for (int col = 0; col < bcols; col++) {
        double [] currentCol = new double[brows];
        for (int r=0; r<brows; r++)  currentCol[r] = b.getAt(r, col);
          // solve for the current column
        double [] colSolution = solveEJML(A.getArray(), currentCol);
           // copy solution
        for (int r=0; r<brows; r++)
            results.d[r][col] =  colSolution[r];
    }
    return results;
    }
  
/*
a = rand(8)
svdbasedcond = svd_cond(a)
directcond = cond(a)
 */
       
final public static double svd_cond(Mat M) {
    return LinearAlgebra.singular( M.getRef()).cond();
}
	
final public static double svd_cond(double [][] dM) {
    return LinearAlgebra.singular( dM).cond();
}
	
final public static double cond(Mat M) {
    return LinearAlgebra.cond( M.getRef());
}
	
final public static double cond(double [][] dM) {
    return LinearAlgebra.cond( dM);
}
	
final public static Mat svd_S(Mat M) {
    return new Mat(LinearAlgebra.singular(M.getRef()).getS().getArray(), false);
}
	
final public static double [][] svd_S(double [][] dM) {
    return  LinearAlgebra.singular(dM).getS().getArray();
}
	
final public static Mat S(Mat M) {
    return  svd_S( M);
}
	
final public static double [][] S(double [][] dM) {
    return  svd_S( dM);
}
	
final public static Mat svd_values(Mat M) {
    return new Mat(LinearAlgebra.singular(M.getRef()).getSingularValues(), false);
}
	

final public static double [] svd_values(double [][] dM) {
    return LinearAlgebra.singular(dM).getSingularValues();
}

final public static double [] svd_W(double [][] dM) {
    return svd_values(dM);
}

// using Apache Common Maths
  /*
N=500; M=300
x = rand(N, M)
tic()
nrx = asvd(x)  // perform SVD
tm = toc()
shouldBeZero = nrx.U*diag(nrx.W)*t(nrx.V) - x
 shouldBeIdentity = nrx.V*t(nrx.V)  // matrix V is orthogonal
*/
final public static  SvdResults asvd(Mat xm) {
    double [][] x = xm.getArray();
    return asvd(x);
}

final public static  SvdResults svd(Mat xm) {
    return asvd(xm);
}


final public static  SvdResults svd(double [][]  xd) {
    return asvd(xd);
}

// SVD using the Apache Commons Maths
final public static  SvdResults asvd(double[][] x) {

    Array2DRowRealMatrix rmA = new Array2DRowRealMatrix(x);  // transform it to an Apache Commons Array2DRealMatrix
    SingularValueDecomposition  svdObj = new SingularValueDecomposition(rmA);   // perform an SVD decomposition on A
    SvdResults  svdR = new SvdResults();
    svdR.U = svdObj.getU().getData();  // get U matrix as an Array[Array[Double]]
    svdR.W = svdObj.getSingularValues(); 
    svdR.V = svdObj.getV().getData();
    svdR.conditionNumber = svdObj.getConditionNumber();
    svdR.norm = svdObj.getNorm();
    return svdR;
}


// perform QR using Numerical Recipes
/*
 N=10; M=20
x = rand(N, M)
 qrx = qr(x)
 zorth = qrx.Q* t(qrx.Q)-eye(N, N)  // matrix Q is orthogonal, should be 0
 shouldBeZero = x-t(qrx.Q)*qrx.R
 
 */
 final public static QRResults nrqr(double [][] x )  {
    com.nr.la.QRdcmp qrdcmp = new com.nr.la.QRdcmp(x);
     QRResults qr = new QRResults();
     qr.Q = qrdcmp.qt;
     qr.R =  qrdcmp.r;
     return qr;
 }
final public static QRResults nrqr(Mat x )  {
    return nrqr(x.getArray());
}

/*
 Apache Commons Maths QR() is much faster than Numerical  Recipes
 , e.g. 0.48sec vs. 4.1sec
 */
 final public static QRResults qr(double [][] x )  {
     return aqr(x);
 }
 final public static QRResults qr(Mat x )  {
     return aqr(x);
 }
 
// perform QR using Apache Commons
/*N=10; M=20
x = rand(N, M)
 qrx = aqr(x)
 qrx.Q* t(qrx.Q)-eye(N, N)  // matrix Q is orthogonal, should be 0
 shouldBeZero = x-qrx.Q*qrx.R
 */
 final public static QRResults aqr(double [][] x )  {
     Array2DRowRealMatrix  rx = new Array2DRowRealMatrix(x);
     org.apache.commons.math3.linear.QRDecomposition qrObj = new org.apache.commons.math3.linear.QRDecomposition(rx);
     QRResults qr = new QRResults();
     qr.Q = qrObj.getQ().getData();
     qr.R = qrObj.getR().getData();
     return qr;
 }
 
final public static QRResults aqr(Mat mx)  {
       return aqr(mx.getArray());
}

// perform an SVD using NR
final public static SvdResults nrsvd(double [][] dM) {
    SvdResults rsvd = new SvdResults();
    com.nr.la.SVD nrsvd = new com.nr.la.SVD(dM);
    rsvd.U = nrsvd.u;
    rsvd.W = nrsvd.w;
    rsvd.V = nrsvd.v;
    return rsvd;
}

// perform SVD using Numerical Recipes 
final public static SvdResults nrsvd(Mat  dM) {
    SvdResults rsvd = new SvdResults();
    com.nr.la.SVD nrsvd = new com.nr.la.SVD(dM.getArray());
    rsvd.U = nrsvd.u;
    rsvd.W = nrsvd.w;
    rsvd.V = nrsvd.v;
    return rsvd;
}

final public static Mat svd_U(Mat M) {
    return new Mat(LinearAlgebra.singular(M.getRef()).getU().getArray(), false);
}	
	
final public static double [][] svd_U(double [][] M) {
    return  LinearAlgebra.singular(M).getU().getArray();
}	
	
final public static Mat svd_V(Mat M) {
    return new Mat(LinearAlgebra.singular(M.getRef()).getV().getArray(), false);
}
	
final public static double [][] svd_V(double [][] M) {
    return  LinearAlgebra.singular(M).getV().getArray();
}
        
final public static double svd_norm2(Mat M) {
    return LinearAlgebra.singular( M.getRef()).norm2();
}
	        
final public static double svd_norm2(double [][] M) {
    return LinearAlgebra.singular( M).norm2();
	}
        
	

// One norm:  maximum column sum
final public static double norm1(Mat M) {
    return LinearAlgebra.norm1( M.getRef());
}
	

final public static double norm1(double [][] dM) {
    return LinearAlgebra.norm1(dM);
}

// Two norm:  maximum singular value.
final public static double norm2(Mat M) {
    return LinearAlgebra.norm2( M.getRef());
}
	
final public static double norm2(double [][] dM) {
    return LinearAlgebra.norm2(dM);
}
	
//  Frobenius norm:    sqrt of sum of squares of all elements.
final public static double  normF(Mat M)  {
    return LinearAlgebra.normF( M.getRef());
}
	
final public static double normF(double [][] dM) {
    return LinearAlgebra.normF( dM);
}

final public static double normInf(double [][] dM) {
    org.ejml.data.DenseMatrix64F dm = new org.ejml.data.DenseMatrix64F(dM);
    double normInfValue = org.ejml.ops.NormOps.normPInf(dm);
    return normInfValue;
}

final public static double normInf(Mat dM) {
    org.ejml.data.DenseMatrix64F dm = new org.ejml.data.DenseMatrix64F(dM.getArray());
    double normInfValue = org.ejml.ops.NormOps.normPInf(dm);
    return normInfValue;
}
        
final public static int svd_rank(Mat M) {
    return LinearAlgebra.singular( M.getRef()).rank();
}
	
final public static int svd_rank(double [][] dM) {
    return LinearAlgebra.singular( dM).rank();
}
	
final public static int rank(Mat M) {
    return LinearAlgebra.rank( M.getRef());
}
	        
final public static int rank(double [][] dM) {
    return LinearAlgebra.rank( dM);
}
	
final public static double  trace(Mat M) {
    return LinearAlgebra.trace( M.getRef());
}

final public static double  trace(double [][] dM) {
    return LinearAlgebra.trace( dM);
}

final public static Mat eig_V(Mat M) {
    return new Mat(LinearAlgebra.eigen(M.getRef()).getV().getArray(), false);
}

final public static double [][] eig_V(double [][] dM) {
    return LinearAlgebra.eigen(dM).getV().getArray();
}
	
final public static Mat eigV(Mat M) {
    return eig_V( M); 
}
	
final public static double [][] eigV(double [][] dM) {
    return eig_V(dM); 
}
	
final public static Mat eig_D(Mat M) {
    return new Mat(LinearAlgebra.eigen(M.getRef()).getD().getArray(), false);
}
	
final public static double [][] eig_D(double [][] dM) {
    return LinearAlgebra.eigen(dM).getD().getArray();
}

final public static Mat eigD(Mat M) {
    return new Mat(LinearAlgebra.eigen(M.getRef()).getD().getArray(), false);
}
	
final public static double [][] eigD(double [][] dM) {
    return LinearAlgebra.eigen(dM).getD().getArray();
}

/* test example:  Compute eigenvalues with JAMA and Native BLAS and compare times
 N = 10
 A = Rand(N, N)
 tic()
 eigJama = eig_D(A)
 tmJama = toc()
 tic()
 eigJBLAS = jblas_eigenvalues(A)
 tmJBLAS = toc()
 * 
 */

final public static Mat D(Mat M) {
    return eig_D( M);
}
	
final public static double [][] D(double [][] dM) {
    return eig_D( dM);
}
	
        
        //  computes the eigenvalues and eigenvectors of a real matrix
        //  the return matrix is as follows:
        //       column 0:    the real parts of the eigenvalues
        //       column 1:    the imaginary parts of the eigenvalues
        //       columns   n..2+n-1   :  the real parts of the eigenvectors
        //       columns   2+n..2+n+n  : the imaginary parts of the eigenvectors
/*
 tm = M("3 0 1; 0 -3 0; 1 0 3")
 enumaltm = eigNUMAL(tm)
 */
final public static eigReIm eigNUMAL(Mat M) {
    return eigNUMAL(M.getArray());
}

final public static eigReIm  eigNUMAL(double [][] M) {
            
            int n = M.length;
            int m = M[0].length;
            if (n != m)  return null;
            double [] em = new double[8];
        
          // take the matrix indexes to start at 1
            double  [][] M1 = AA1(M);
            
            double [][] Mim = new double[n+1][n+1];
            double [] re = new double[n+1];
            double [] im = new double[n+1];
            double [][] vecRe = new double[n+1][n+1];
            double [][] vecIm = new double[n+1][n+1];
            em[0]=5.0e-6;  em[2]=1.0e-5;  em[4]=10.0;  em[6] = 10.0;
            
            numal.Linear_algebra.eigcom(M1,Mim,  n, em, re, im, vecRe, vecIm);
    
            eigReIm eigResults = new eigReIm(n);
            
            for (int k=1; k<=n; k++)  { // copy eigenvalues
                eigResults.realEvs[k-1] = re[k];   
                eigResults.imEvs[k-1] = im[k];
            }
              // copy real parts of eigenvectors : columns 2:2+n-1 of return matrix
             for (int r=0; r<n; r++)   // columns are eigenvectors
              for (int c=0; c<n; c++) {
                 eigResults.realEvecs[c][r] = vecRe[c+1][r+1];
                 eigResults.imEvecs[c][r] = vecIm[c+1][r+1];
            }
              
   
            return eigResults;
            
        }
        

   /* Linear equation solution by Gauss-Jordan elimination.
   * The input matrix is a[0..n-1][0..n-1]. b[0..n-1][0..m-1] is
   * input containing the m right-hand side vectors. On output,
   * returns the inverse matrix, and b is replaced by
   * the corresponding set of solution vectors.
   * 
   */
  public static double [][] gaussj(final double[][] a,  final double[][] b) {
   double [][] inva = DoubleArray.copy(a);  // keeps the inverse
   com.nr.la.gaussj.gaussj(inva, b); 
   return inva;
}
  
  public Mat gaussj( final double[][] b) {
   double [][] inva = DoubleArray.copy(this.getArray());  // keeps the inverse 
   com.nr.la.gaussj.gaussj(inva, b); 
   return new Mat(inva, true );
}
  
  
  public Mat gaussj( Mat b) {
   return gaussj(b.getArray());
}
  
  // eig() defaults to the Numerical Recipes implementation
       public eigReIm eig() {
           return eigNR(this, true, false);
       }
       
       final  public static eigReIm eig(Mat a) {
            return eigNR(a, true, false);
        }
        
       final  public static eigReIm eig(Mat a, boolean computeEigVecs) {
            return eigNR(a, computeEigVecs, false);
        }
        
       final  public static eigReIm eig(double [][] a) {
            return eigNR(a, true, false);
        }
        
       final  public static eigReIm eig(double [][]  a, boolean computeEigVecs) {
            return eigNR(a, computeEigVecs, false);
        }
        
       final  public static eigReIm eigNR(Mat a,  boolean yesvec,  boolean hessenb) {
           return eigNR(a.getArray(), yesvec, hessenb);
        }
        
        /*
mm = Mat(3, 3, 8, -9, 0.4, 9, 7, 6.5, -3, 0.4, 9)
emm = eigNR(mm, true, false)
  */
       final  public static eigReIm eigNR(double [][] a,  boolean yesvec,  boolean hessenb) {
            int N = a.length;
            com.nr.eig.Unsymmeig  nrUnsymmeig = new com.nr.eig.Unsymmeig(a, yesvec, hessenb);
            eigReIm  eigResults  = new eigReIm(N);
            // extract the eigenvalues
            com.nr.Complex  [] eigs = nrUnsymmeig.wri;   // the computed eigenvalues
            for (int k=0; k<N; k++) {
                eigResults.realEvs[k] = eigs[k].re();  // the real part of the eigenvalue
                eigResults.imEvs[k] = eigs[k].im();  // the imaginary part
            }
            double [][] eigvecs =  nrUnsymmeig.zz;
           for (int c=0; c<N; c++) { // for all eigenvectors
              for (int r=0; r<N; r++)   // columns are eigenvectors
                 eigResults.realEvecs[c][r] = eigvecs[c][r];
            }
       
          return eigResults;      
            
        }
        
       final  public static eigReIm eigNR(Mat a) {
            return eigNR(a.getArray());
        }
        
       final  public static eigReIm eigNR(double [][] a) {
            return eigNR(a, true, false);
        }
        
       final  public static eigReIm eigNR(Mat a, boolean yesvec) {
            return eigNR(a.getArray(), yesvec);
        }
        
       final  public static eigReIm eigNR(double [][] a, boolean  yesvec) {
            return eigNR(a, yesvec, false);
        }
        
  

// compute eigenvalue decomposition using LAPACK
/*
 a = [[3.4, 6.7, -4.5], [3.4, -3.4, 9], [-1.2, 3.4, 6.7]] as double [][]
 ma = new Matrix(a)
 eiga = eigMTJ(ma)
 // check
 realEvs = eiga.realEvs
 imEvs = eiga.imEvs
 leftEvecs = eiga.leftEvecs
 rightEvecs = eiga.rightEvecs

 */

final public EigResults  eigMTJ() {  
  EigResults eigA = groovySci.math.array.JILapack.EigMTJ(this.getArray());
  return eigA;
}

final public static EigResults  eigMTJ(Mat A) {  
  EigResults eigA = groovySci.math.array.JILapack.EigMTJ(A.getArray());
  return eigA;
}

final public static EigResults  eigMTJ(double [][] A) {  
  EigResults eigA = groovySci.math.array.JILapack.EigMTJ(A);
  return eigA;
}


/* EJML based routines */


final public static double [][]  EJMLDenseMatrixtoDoubleArray(org.ejml.data.DenseMatrix64F dm)  {
  double [][]  da = new double [dm.numRows][dm.numCols];
  for (int r=0; r < dm.numRows; r++)
    for (int c=0; c <dm.numCols; c++)
      da[r][c] = dm.get(r,c);
  
  return da;
} 
  
// get the inverse using EJML (slightly faster than inv())
final public static Mat invEJML(Mat A) {
  org.ejml.data.DenseMatrix64F da = new org.ejml.data.DenseMatrix64F(A.getArray());
  org.ejml.ops.CommonOps.invert(da);
  return new Mat( EJMLDenseMatrixtoDoubleArray(da));
  
}

// compute eigenvalues  using EJML library
// returns an array where each row stores an eigenvalue 
// first the real part and then the imaginary part
final public static double  [] []  eigDEJML(double [][] da) {
    org.ejml.data.DenseMatrix64F dm_da = new org.ejml.data.DenseMatrix64F(da);
    org.ejml.alg.dense.decomposition.eig.SwitchingEigenDecomposition  eig_da = new org.ejml.alg.dense.decomposition.eig.SwitchingEigenDecomposition(dm_da.numRows);
    eig_da.decompose(dm_da);
    int neigs = eig_da.getNumberOfEigenvalues();
    double  [] []    eigVals = new double [neigs][2];   // each row stores real and imaginary part of the eigenvalue
    for (int k=0; k<neigs; k++) {
        eigVals[k][0] = eig_da.getEigenvalue(k).getReal();
        eigVals[k][1] = eig_da.getEigenvalue(k).getImaginary();
    }
    
    return eigVals;
}

// compute eigenvalues  using EJML library
final public static double [] []  eigDEJML(Mat a) {
    return eigDEJML(a.getArray());
}


// compute eigenvectors  using EJML library
// returns an array where each column keeps an eigenvector
final public static double  [] []  eigVEJML(double [][] da) {
    org.ejml.data.DenseMatrix64F dm_da = new org.ejml.data.DenseMatrix64F(da);
    org.ejml.alg.dense.decomposition.eig.SwitchingEigenDecomposition   eig_da = new org.ejml.alg.dense.decomposition.eig.SwitchingEigenDecomposition(dm_da.numRows);
    eig_da.decompose(dm_da);
    int neigs = eig_da.getNumberOfEigenvalues();
    double  [] []    eigVecs = new double [neigs][da.length];   // each row stores real and imaginary part of the eigenvalue
    for (int k=0; k<neigs; k++)  {
        DenseMatrix64F  currentEVec = (DenseMatrix64F) eig_da.getEigenVector(k);
        if (currentEVec != null)
         for (int r=0; r<da.length; r++) 
            eigVecs[r][k] = currentEVec.get(r);
        
    }
    
    return eigVecs;
}

// compute eigenvalues  and eigenvectors using EJML library
// returns an array where each row stores an eigenvalue 
// first the real part and then the imaginary part
final public static eigReIm eigEJML(double [][] da) {
    eigReIm er = new eigReIm(da.length);
            
    org.ejml.data.DenseMatrix64F dm_da = new org.ejml.data.DenseMatrix64F(da);
    org.ejml.alg.dense.decomposition.eig.SwitchingEigenDecomposition  eig_da = new org.ejml.alg.dense.decomposition.eig.SwitchingEigenDecomposition(dm_da.numRows);
    eig_da.decompose(dm_da);
    int neigs = eig_da.getNumberOfEigenvalues();
    double  [] []    eigVals = new double [neigs][2];   // each row stores real and imaginary part of the eigenvalue
    for (int k=0; k<neigs; k++) {
        er.realEvs[k] = eig_da.getEigenvalue(k).getReal();
        er.imEvs[k]  = eig_da.getEigenvalue(k).getImaginary();
        DenseMatrix64F  currentEVec = (DenseMatrix64F) eig_da.getEigenVector(k);
        if (currentEVec != null)
         for (int r=0; r<da.length; r++) 
            er.realEvecs[r][k] = currentEVec.get(r);
        
    }
    
    return er; 
}

final public static eigReIm eigEJML(Mat  A) {
    return eigEJML(A.getArray());
}

final public static double  [] []  eigVEJML(Mat da) { 
      return  eigVEJML(da.getArray());
}      
        // STATISTICAL ROUTINES
final public static Object  mean(Mat v)  {
    if (v.getRowsNumber()==1)
        return StatisticSample.mean(v.getRowRef(1));
    else if (v.getColumnsNumber()==1)
        return StatisticSample.mean(v.getColumnCopy(1))[0];
    else
return new Mat(StatisticSample.mean( v.getRef()), false );
}
        
final public static double []  mean(double [][] dv)  {
    return  StatisticSample.mean( dv);
}
	
final public static Object  variance(Mat v)  {
    if (v.getRowsNumber()==1)
        return StatisticSample.variance(v.getRowRef(1));
    else if (v.getColumnsNumber()==1)
        return StatisticSample.variance(v.getColumnCopy(1))[0];
    else
    return new Mat(StatisticSample.variance( v.getRef()), false );
}
	
final public static double []  variance(double [][] dv)  {
    return  StatisticSample.variance( dv);
}

	
final public static Object  std(Mat v)  {
    if (v.getRowsNumber()==1)
        return StatisticSample.stddeviation(v.getRowRef(1));
    else if (v.getColumnsNumber()==1)
        return StatisticSample.stddeviation(v.getColumnCopy(1))[0];
    else
        return new Mat(StatisticSample.stddeviation( v.getRef()), false );
}
        
final public static double []  std(double [][] dv)  {
    return  StatisticSample.stddeviation( dv);
}
		
final public static Object var(Mat v)  {
    return variance( v);
}
	
final public static double []  var(double [][] dv)  {
    return  StatisticSample.variance(dv);
}
	        
final public static Object   covariance(Mat v1, Mat v2)  {
    if (v1.getRowsNumber()==1 && v2.getRowsNumber()==1)
        return StatisticSample.covariance(v1.getRowRef(1),v2.getRowRef(1));
    else if (v1.getColumnsNumber()==1 && v2.getColumnsNumber()==1)
        return StatisticSample.covariance(v1.getColumnCopy(1),v2.getColumnCopy(1))[0];
    else
    return new Mat(StatisticSample.covariance( v1.getRef(),  v2.getRef()), false );
}
	
final public static double [][]  covariance(double [][] dv1, double [][] dv2)  {
    return  StatisticSample.covariance( dv1, dv2);
}
	
final public static Object cov(Mat v1, Mat v2)  {
    return covariance( v1,  v2);
}
	        
final public static double [][]  cov(double [][] dv1, double [][] dv2)  {
    return  covariance( dv1, dv2);
}
	
final public static Object  covariance(Mat v)  {
    return covariance( v,  v);
}
	        
final public static double [][]  covariance(double [][] dv)  {
    return cov( dv, dv);
}
        
final public static Object  cov(Mat v)  {
    return covariance( v); 
}
	
final public static double [][]  cov(double [][] dv)  {
    return covariance( dv); 
}
	
final public static Object  correlation(Mat v1, Mat v2)  {
    if (v1.getRowsNumber()==1 && v2.getRowsNumber()==1)
        return StatisticSample.correlation(v1.getRowRef(1),v2.getRowRef(1));
    else if (v1.getColumnsNumber()==1 && v2.getColumnsNumber()==1)
        return StatisticSample.correlation(v1.getColumnCopy(1),v2.getColumnCopy(1));
    else
    return new Mat(StatisticSample.correlation( v1.getRef(),  v2.getRef()), false );
}
	        
final public static double [][]  correlation(double [][] dv1, double [][] dv2)  {
    return  StatisticSample.correlation( dv1,  dv2);
}
        
final public static Object  corr(Mat v1, Mat v2)  {
    return correlation( v1,  v2);
}
	        
final public static double [][]  corr(double [][] dv1, double [][] dv2)  {
    return  correlation( dv1,  dv2);
}
	
final public static Object  correlation(Mat v)  {
    return correlation( v,v);
}
	
final public static double [][]  correlation(double [][] v)  {
    return correlation( v,v);
}
        
 final public static Object  corr(Mat v)  {
    return correlation( v);
}
	        
final public static double [][]  corr (double [][] v)  {
    return correlation( v,v);
}
                
final public static double mean(double [] v)  {
    return StatisticSample.mean(v);
}
        
final public static double  variance(double [] v)  {
    return StatisticSample.variance(v);
}
        
final public static double var(double [] v)  {
    return variance( v);
}

final public static  double  std(double [] v)  {
    return StatisticSample.stddeviation(v);
}
        
final public static double   covariance(double [] v1, double [] v2)  {
    return StatisticSample.covariance(v1, v2);
}
	
final public static double  cov(double [] v1, double [] v2)  {
    return covariance( v1,  v2);
}
		
final public static double   correlation(double [] v1, double [] v2)  {
    return StatisticSample.correlation(v1, v2);
}
	
final public static double corr(double [] v1, double [] v2)  {
    return correlation( v1,  v2);
}

final public static Mat randomUniform(int m, int n, double min, double max)  {
    return new Mat(StatisticSample.randomUniform(m, n, min, max), false );
}
	
final public static Mat randomDirac(int m, int n, double[] values, double[] prob)  {
    return new Mat(StatisticSample.randomDirac(m, n, values, prob), false );
}
	
final public static Mat randomNormal(int m, int n, double mu, double sigma)  {
    return new Mat(StatisticSample.randomNormal(m, n, mu, sigma), false );
}
	
final public static Mat randomChi2(int m, int n, int d)  {
    return new Mat(StatisticSample.randomChi2(m, n, d), false );
}
	
final public static Mat randomLogNormal(int m, int n, double mu, double sigma)  {
    return new Mat(StatisticSample.randomLogNormal(m, n, mu, sigma), false );
}
	
final public static Mat randomExponential(int m, int n, double lambda)  {
    return new Mat(StatisticSample.randomExponential(m, n, lambda), false );
}
	
final public static Mat randomTriangular(int m, int n, double min, double max)  {
    return new Mat(StatisticSample.randomTriangular(m, n, min, max), false );
}

final public static Mat randomTriangular(int m, int n, double min, double med, double max)  {
    return new Mat(StatisticSample.randomTriangular(m, n, min, med, max), false );
}
	
final public static Mat randomBeta(int m, int n, double a, double b)  {
    return new Mat(StatisticSample.randomBeta(m, n, a, b), false );
}
	
final public static Mat randomCauchy(int m, int n, double mu, double sigma)  {
    return new Mat(StatisticSample.randomCauchy(m, n, mu, sigma), false );
}
	
final public static Mat randomWeibull(int m, int n, double lambda, double c)  {
    return new Mat(StatisticSample.randomWeibull(m, n, lambda, c), false );
}
        
        
final public static double [][]  RandomUniform(int m, int n, double min, double max)  {
    return StatisticSample.randomUniform(m, n, min, max);
}
	
final public static double [][] RandomDirac(int m, int n, double[] values, double[] prob)  {
    return StatisticSample.randomDirac(m, n, values, prob);
}
	
final public static double [][]  RandomNormal(int m, int n, double mu, double sigma)  {
    return StatisticSample.randomNormal(m, n, mu, sigma);
}
	
final public static double [][]  RandomChi2(int m, int n, int d)  {
    return StatisticSample.randomChi2(m, n, d);
}
	
final public static double [][] RandomLogNormal(int m, int n, double mu, double sigma)  {
    return StatisticSample.randomLogNormal(m, n, mu, sigma);
}
	
final public static double [][] RandomExponential(int m, int n, double lambda)  {
    return StatisticSample.randomExponential(m, n, lambda);
}
	
final public static double [][] RandomTriangular(int m, int n, double min, double max)  {
    return StatisticSample.randomTriangular(m, n, min, max);
}
	
final public static double [][] RandomTriangular(int m, int n, double min, double med, double max)  {
    return StatisticSample.randomTriangular(m, n, min, med, max);
}
	
final public static double [][] RandomBeta(int m, int n, double a, double b)  {
    return StatisticSample.randomBeta(m, n, a, b);
}
	
final public static double [][] RandomCauchy(int m, int n, double mu, double sigma)  {
    return StatisticSample.randomCauchy(m, n, mu, sigma);
}
	
final public static double [][]  RandomWeibull(int m, int n, double lambda, double c)  {
    return StatisticSample.randomWeibull(m, n, lambda, c);
}
        
final public static Mat dot(Mat X, Mat Y) {
    double [][] xvals = X.getArray();
    double [][] yvals = Y.getArray();
    double [][] res = DoubleArray.dot(xvals, yvals);
    return new Mat(res);
    }

// reshape(M: matrix to reshape, n: no of rows,m: no of columns)
//  Reshapes a matrix.
 final public static Mat reshape(Mat M, int n, int m)
   {
		// get data from arguments
   double[][] x =      M.getArray();

   int x_dy     = x.length;
   int x_dx     = x[0].length;
	// size(x) == n*m
   if ((x_dy * x_dx) != (n*m)) 		{			return null;		}

     // create matrix
   double[][] values = new double[n][m];
   int yii=0;
    int xii=0;
    for (int xi=0; xi<m ; xi++) {
         for (int yi=0; yi<n ; yi++)  {  // reshape
           values[yi][xi] = x[yii][xii];
            // read original matrix columnwise
            yii++;
            if (yii >= x_dy)  {
                yii=0;
                xii++;
            }
         }
        }
 return new Mat(values);
} 

 
 
// reshape(dM: double [][] to reshape, n: no of rows,m: no of columns)
//  Reshapes a double [][] array.
 final public static double [][] reshape(double [][] x, int n, int m)
   {
        int x_dy     = x.length;
        int x_dx     = x[0].length;
	// size(x) == n*m
        if ((x_dy * x_dx) != (n*m))	{  return null; }

        // create matrix
        double[][] values = new double[n][m];
        int yii=0;
        int xii=0;
        for (int xi=0; xi<m ; xi++) {
            for (int yi=0; yi<n ; yi++)  {  // reshape
	values[yi][xi] = x[yii][xii];
          // read original matrix columnwise
	yii++;
	if (yii >= x_dy)  {
                    yii=0;
	xii++;
          }
        }
      }
 return values;
} 

 
/* 
 n = 8;
 am = rand(n, n);
 om = dec(am);
 */
 /* Decomposes the nXn matrix A in the form LU = PA, where L is lower triangular,
  * U is unit upper traingular, and P is a permutation matrix
  * 
  * Procedure parameters: 
  *    Matrix dec( Matrix am)
  */
final public static Mat  NUMALdec( Mat am )  {
     double [][] a = am.getArray();
     int  n = a.length;  // the order of the Matrix
     int  m = a[0].length;
     if (n != m)  {
         System.out.println("Matrix dec(Matrix am)  only possible for square matrices");
         return null;
     }
     double [][] ac = new double[n][n]; 
     for (int r=0; r<n; r++)
         for (int c=0; c<n; c++)
             ac[r][c] = a[r][c];
     
     double [] aux = new double [4];
     aux[2] =  0.000001;  // a default relative tolerance
     int [] p = new int[n];
     numal.Linear_algebra.dec(ac, n-1, aux, p);
     
     Mat outMat = new Mat(ac);   // construct an output Matrix 
     return  outMat;  
   }

  
/* 
 * solves a well-conditioned linear system of equations Ax=b whose order is small relative to the number of 
 * binary digits in the number representation
 * */
final public  Mat  NUMALdecsol(double [] aux, double [] b)  {
    double [][] a = this.getArray();
    
    int n=b.length;
    double [] sol = new double[n+1];   // will hold the solution vector
    for (int k=0; k<n+1; k++)   sol[k] = b[k];
    
    n = a.length;
    int m = a[0].length;
    if (n !=m)  {
        System.out.println("Use sol() only for square matrices");
        return null;
    }
    numal.Linear_algebra.decsol(a, n-1, aux , sol);
    
    return new Mat(sol);
}


/*
 * Determines the least squares solution of the overdetermined system A*x = b, where A is an nXm matrix (n >= m).
 *  (n linear equations in m unknowns), and computes the principal diagonal elements of the inverse of A^T*A.
 * 
 * Procedure parameters:
 *      void  NUMALlsqortdecsol( a, n, m, aux, diag, b)
 
 *   a:   double a[1:n, 1:m];
 *        entry:   the coefficient matrix of the linear least squares problem;
 *        exit:     in the upper triangle of a (the elements a[i,j] with i<j) the superdiagonal elements of the upper triangular matrix produced by the Householder transformation;
 *                   in the other part of the columns of a the significant elements of the generating vectors of the Householder matrices used for the Householder triangularization
 *   aux:  double aux[2:5];
 *      entry:  aux[2]:    contains a relative tolerance used for calculating the diagonal elements of the upper triangular matrix
 *                aux[3]:    delivers the number of the diagonal elements of the upper triangular matrix which are found not negligible, normal exit aux[3]=m
 *                aux[5]:    the maximum of the Euclidean norms of the columns of the given matrix
 *   diag:   double diag[1:m];
 *                exit:  the diagonal elements of the inverse of A^T * A, where A is the matrix of the linear least squares problem
       b:   double  b[1:n];
 *         entry:   contains the right-hand side of a linear least squares problem
 *         exit:  b[1:m]   contains the solution to the problem
 *                 b[m+1:n]  contains a vector with Euclidean length equal to the Euclidean length of the residual vector
 * */
final public  Mat  NUMALlsqortdecsol(double [] aux,  double [] diag, double []b )  {
    double [][] a = this.getArray();
    
    int n = a.length;
    int m = a[0].length;
    
    double [] sol = new double[n];   // will hold the solution vector
    for (int k=0; k<n; k++)   sol[k] = b[k];
    
    numal.Linear_algebra.lsqortdecsol(a, n-1, m-1, aux, diag, sol);
    
    return new Mat(sol);
}






/*
 *   Calculates the inverse of the matrix A^T*A, where A is the coefficient matrix of a linear least squares problem.
 *   lsqinv can be used for the calculation of the covariance matrix of a linear least squares problem.
 * 
 *     void  lsqinv(a, m, aid, c)
 *  
 *   a:     double a[1:m, 1:m];
 *          entry:  in the upper triangle of a (the elements a[i,j] with 1<=i<j<=m) the superdiagonal elements should be given of the upper 
 *                    triangular matrix (U above) that is produced by the Householder triangularization in a call of the procedure lsqortdec with normal exit (aux[3]=m)
 *          exit:    the upper triangle of the symmetric inverse matrix is delivered in the upper triangular elements of the array a (a[i,j], 1<=i<=j<=m)
 *   m:   int: 
 *         entry:  the number of columns of the matrix of the linear least squares problem
 *   aid:  contains the diagonal elements of the upper triangular matrix produced by lsqortdec
 *   ci:  int ci[1:m];
 *         entry:  contains the pivotal indices produced by a call of lsqortdec
 * 
 * */
// SOS - not checked
final public  Mat NUMALlsqinv( int m, double [] aid, int [] c )  {
    double [][] a = this.getArray();
    
    
    numal.Linear_algebra.lsqinv(a, m, aid, c);
    
    return new Mat(a);
}
    
/*
 A = rand(4, 4)
 b = rand(4, 1)
 xl = A.lsolve(b)  // solve with LAPACK through MTJ
 
 
 xlu = LU_solve(A, b)  // solve with LU of JAMA
 
 xqr = QR_solve(A, b)  // solve with QR of JAMA
 */
// MTJ routines
    
final public static Mat solveMTJ(Mat A, Mat b) {
    return A.solveMTJ(b);
}

final public Mat solveMTJ(Mat B) {
        DenseMatrix Adm = new DenseMatrix(this.d);
        DenseMatrix bdm = new DenseMatrix(B.d);
        DenseMatrix xdm = new DenseMatrix(B.d);
        DenseMatrix X =  Adm.solve(bdm, xdm);
        
        return new Mat(xdm.toDoubleArray());
    }



// simpler interface to LAPACK DGELS routine
// solves overdetermined/underdetermined linear systems involving an m-by-n matrix A
// using a QR or LQ factorization of A. It is assumed that A has full rank.
//  The followiiiiig options are provided:
//   1.     if  m>=n: find the least squares solution of an overdetermined system, i.e., solve the least squares problem
//      minimize || b-A*x ||_{2}
//   2.    if m < n:  find the minimum norm solution of an undetermined system A*x = B
// Several right hand side vectors b and solution vectors x can be handled in a single call;
// they are stored as the columns of the m-by-nrhs right hand side matrix B and the n-by-nrhs solution matrix X

final public static double [][]  DGELS(double [][] A, double [][] B) {
    return groovySci.math.array.JILapack.DGELS(A, B);
}

final public static Mat   DGELS(Mat A, Mat B) {
    return new Mat(groovySci.math.array.JILapack.DGELS(A.getArray(), B.getArray()));
}


// compute LQ decomposition using LAPACK
/*
 a = [[3.4, 6.7, -4.5], [3.4, -3.4, 9], [-1.2, 3.4, 6.7]] as double [][]
 ma = new Matrix(a)
 lqa = lLQ(ma)
 // check
 residual = lqa.L*lqa.Q-ma  // should be zero
 */
final public LQResults  lLQ() {  
  LQResults lqA = JILapack.LQ(this.getArray());
  return lqA;
}

final public static LQResults  lLQ(Mat A) {  
  LQResults lqB = JILapack.LQ(A.getArray());
  return lqB;
}

/*
 a = rand(3,5)
 b = new Matrix(5,3)
 c = EJMLpinv(a, b)
 
 */

// get the pseudo-inverse using EJML 
final public static Mat EJMLpinv(Mat A, Mat invA) {
  org.ejml.data.DenseMatrix64F da = new org.ejml.data.DenseMatrix64F(A.getArray());
  org.ejml.data.DenseMatrix64F dinva = new org.ejml.data.DenseMatrix64F(invA.getArray());
  
  org.ejml.ops.CommonOps.pinv(da, dinva);
  invA.d = EJMLDenseMatrixtoDoubleArray(dinva);
  
  return invA;
}

 // Creates a random symmetric positive definite matrix.
    // @param width The width of the square matrix it returns.
     // @param rg Random number generator used to make the matrix.
     // @return The random symmetric  positive definite matrix.
   /* e.g.
     rnd = new java.util.Random()
     width = 8
      symmPosDefEJML = EJMLcreateSymmPosDef(width, rnd)
     */
final public static Mat EJMLcreateSymmPosDef(int width,  java.util.Random rg)  {
    DenseMatrix64F spdDense64 = org.ejml.ops.RandomMatrices.createSymmPosDef(width, rg);
    double [][] dd = EJMLDenseMatrixtoDoubleArray(spdDense64);
    return new Mat(dd, true);
}


// sets the provided square matrix to be a random symmetric matrix whose values are selected
  // from a uniform distribution, from min to max inclusive
/* e.g.
 N = 10; mn = -5.4; mx = 4.7; 
 rnd = new java.util.Random()
 symmMat = createSymmetric(N, mn, mx, rnd)
 * 
 */
  final public static Mat createSymmetric(int N, double mn, double mx, java.util.Random rg)  {
      
      DenseMatrix64F dmejml = org.ejml.ops.RandomMatrices.createSymmetric(N,  mn, mx, rg);
      double [][] dd = EJMLDenseMatrixtoDoubleArray(dmejml);
    return new Mat(dd, true);

  }
  
  
//  Creates an upper triangular matrix whose values are selected from a uniform distribution.  If hessenberg
  //     is greater than zero then a hessenberg matrix of the specified degree is created instead.
//   @param dimen Number of rows and columns in the matrix..
//  @param hessenberg 0 for triangular matrix and > 0 for hessenberg matrix.
//  @param mn minimum value an element can be.
//  @param mx maximum value an element can be.
//  @param rg random number generator used.
//  @return The randomly generated matrix.
  /* e.g.
     rg = new java.util.Random()
     dimen = 8;  hessenberg = 0;  mn = -7.5;  mx = 4.5
     triagEJML = createUpperTriangle(dimen, hessenberg, mn, mx,  rg)
     hessenberg = 1
     hessenbergEJML = createUpperTriangle(dimen, hessenberg, mn, mx, rg)
     */
final public static Mat createUpperTriangle(int dimen,  int hessenberg, double mn,  double mx, java.util.Random rg ) {
     DenseMatrix64F  upperTriangleDense64 = org.ejml.ops.RandomMatrices.createUpperTriangle(dimen, hessenberg, mn, mx, rg);
     double [][] dd = EJMLDenseMatrixtoDoubleArray(upperTriangleDense64);
     return new Mat(dd, true);
}
  
  
  
  // Solves a well-conditioned linear system of equations Ax = b whose order is small relative to the number 
  // of binary digits in the number representation
   /*
     a=rand(3,3)
     b = AD("6 7 8")
     aa = decsol0(a,b)
     vr = a*aa-b // verify
    */
  final public static double [] NUMALdecsol0(Mat a,  double [] b)   {
    double []  aux = new double [4];
    aux[2] = 1.0e-5;   // relative tolerance
    
    double [] [] a1 = AA1(a.getArray());  // convert array to 1-indexed
    int  n = a.numRows();
    
    double [] b1 = A1(b);
    
    numal.Linear_algebra.decsol(a1, n, aux, b1);    //  call the NUMAL routine
    
   double [] rb = new double [n];
    for (int r=0; r<n; r++)
      rb[r] = b1[r+1];
      
    return rb;
  }
  
  /*
     a=rand(3,3)
     b = AD("6 7 8")
     aa = gsssol0(a,b)
     vr = a*aa-b // verify
    */
  // Solves a linear system of equations Ax = b 
  final public static double []  NUMALgsssol0(Mat a, double []  b)  {
    double []  aux = new double [8];
    aux[2] = 1.0e-5;   // relative tolrerance
    aux[4] = 8; 
    
    double [][]  a1 = AA1(a.getArray());  // convert array to 1-indexed
    int  n = a.length();
    
    double []  b1 =  A1(b);
    
    numal.Linear_algebra.gsssol(a1, n, aux, b1);    //  call the NUMAL routine
    
   double [] rb = new double [n];
    for (int r=0; r<n; r++)
      rb[r] = b1[r+1];
      
  return  rb;
  }
  
  
  // Solves the nXn system of equations Ax=b, and provides an upper bound
  // for the relative error in x
  
  /*
     a = rand(3,3)
     b = AD("6 7 8")
     estimatedErrorBound = new double[1]
     aa = gsssolerb0(a,b, estimatedErrorBound)
     vr = a*aa-b // verify
    */
  final public static double []  NUMALgsssolerb0(Mat a, double [] b, double [] estimatedErrorBound)  {
    double [] aux = new double [12];
    aux[0] = 1.0e-14;   // the machine's precision'
    aux[2] = 1.0e-14;   //  a relative tolerance
    aux[4] = 8;   // a value used for controling pivoting. usually aux(4)=8 will give good results
    aux[6] = 1.0e-14;   // an upper bound for the relative precision of the given matrix elements
    
    double [][]  a1 = AA1(a.getArray());  // convert array to 1-indexed
    int  n = a.length();
    
    double []  b1 =  A1(b);
    
    numal.Linear_algebra.gsssolerb(a1, n, aux, b1);    //  call the NUMAL routine
    
   double [] rb = new double [n];
    for (int r=0; r<n; r++)
      rb[r] = b1[r+1];
      
    estimatedErrorBound[0]  = aux[11];
    return  rb; 
  }
  
// construct a zero-indexed Matrix from a String, e.g. 
//  m = M("3.4 -6.7; -1.2 5.6")
final public static Mat  M(String s)   {
    int nRows = 1;
    int nCols = 0;
    for (int i=0; i<s.length(); i++)   // count how many rows are specified
      if  (s.charAt(i)==';')
        nRows++;

  // seperate rows to an ArrayBuffer
   Vector <String>   buf = new Vector<String> ();
   StringTokenizer  strtok = new java.util.StringTokenizer(s, ";");  // rows are separated by ';'
   while (strtok.hasMoreElements()) {
         String  tok = strtok.nextToken();
         buf.add(tok);
      }    

// count how many numbers each row has. Assuming that each line has the same number of elements
 String  firstLine = buf.firstElement();
 strtok = new java.util.StringTokenizer(firstLine, ", ");  // elements are separated by ',' or ' '
 while (strtok.hasMoreTokens()) {
   String  tok = strtok.nextToken();
   nCols++;
}

 double [][]  numbersArray =  new double[nRows][nCols];
    
    for (int k=0; k<nRows; k++)  {  // read array
      String   currentLine = buf.elementAt(k);
   strtok = new java.util.StringTokenizer(currentLine, ", ");  // elements are separated by ',' or ' '
int c=0;
while (strtok.hasMoreTokens()) {  // read row
   String  tok = strtok.nextToken();
   numbersArray[k][c] =  Double.parseDouble(tok);
    c++;
     }   // read row
   }  // read array
   return new Mat(numbersArray);
 }  



// construct a zero-indexed double [][] from a String, e.g. 
//   dd = AAD("3.4 -6.7; -1.2 5.6")
final public static double [][]  AAD(String s)   {
    int nRows = 1;
    int nCols = 0;
    for (int i=0; i<s.length(); i++)   // count how many rows are specified
      if  (s.charAt(i)==';')
        nRows++;

  // seperate rows to an ArrayBuffer
   Vector <String>   buf = new Vector<String> ();
   StringTokenizer  strtok = new java.util.StringTokenizer(s, ";");  // rows are separated by ';'
   while (strtok.hasMoreElements()) {
         String  tok = strtok.nextToken();
         buf.add(tok);
      }    

// count how many numbers each row has. Assuming that each line has the same number of elements
 String  firstLine = buf.firstElement();
 strtok = new java.util.StringTokenizer(firstLine, ", ");  // elements are separated by ',' or ' '
 while (strtok.hasMoreTokens()) {
   String  tok = strtok.nextToken();
   nCols++;
}

 double [][]  numbersArray =  new double[nRows][nCols];
    
    for (int k=0; k<nRows; k++)  {  // read array
      String   currentLine = buf.elementAt(k);
   strtok = new java.util.StringTokenizer(currentLine, ", ");  // elements are separated by ',' or ' '
int c=0;
while (strtok.hasMoreTokens()) {  // read row
   String  tok = strtok.nextToken();
   numbersArray[k][c] =  Double.parseDouble(tok);
    c++;
     }   // read row
   }  // read array
   return numbersArray;
 }  

// construct a zero-indexed double [] from a String, e.g. 
//   ad = AD("3.4 -6.7; -1.2 5.6")
final public static double []  AD(String s)   {
    
// count how many numbers 
StringTokenizer  strtok = new java.util.StringTokenizer(s, ", ");  // elements are separated by ',' or ' '
int  nCols = 0;
while (strtok.hasMoreTokens()) {
   strtok.nextToken();
   nCols++;
}

double []  numbersArray = new double[nCols];
strtok = new java.util.StringTokenizer(s,  ", ");  // elements are separated by ',' or ' '
int c=0; 
while (strtok.hasMoreTokens()) {  // read row
   String  tok = strtok.nextToken();
   numbersArray[c] = Double.valueOf(tok);
   c++;
}

 return numbersArray;
 }  


public static void main(String args[]) {

    int i,j;
    double b[] = new double[5];
    double aux[] = new double[4];
    double a[][] = new double[5][5];
    
    for (i=1; i<=4; i++) {
      for (j=1; j<=4; j++) a[i][j]=1.0/(i+j-1);
      b[i]=a[i][3];
    }
    aux[2]=1.0e-5;
    Mat  A = new Mat(a);
    Mat newMatrix = A.NUMALdecsol(aux, b);
    
    DecimalFormat fiveDigit = new DecimalFormat("0.00000E0");
    System.out.println("Solution:  " + fiveDigit.format(b[1]) +
      "  " + fiveDigit.format(b[2]) + "  " +
      fiveDigit.format(b[3]) + "  " + fiveDigit.format(b[4]));
    System.out.println("Sign(Det) = " + (int)aux[1] + 
      "\nNumber of eliminations = " + (int)aux[3]);
}


    
    // Vector related static routines (we place here for convenient import static)
    final public static Vec  sin(Vec v) {
    double [] rv = new double[v.length()];
    double [] sv = v.getv();
    for (int k=0; k<v.length(); k++) 
        rv[k] = Math.sin(sv[k]);
    return new Vec(rv, true);
}

final public static Vec  cos(Vec v) {
    double [] rv = new double[v.length()];
    double [] sv = v.getv();
    for (int k=0; k<v.length(); k++) 
        rv[k] = Math.cos(sv[k]);
    return new Vec(rv, true);
}

final public static Vec  tan(Vec v) {
    double [] rv = new double[v.length()];
    double [] sv = v.getv();
    for (int k=0; k<v.length(); k++) 
        rv[k] = Math.tan(sv[k]);
    return new Vec(rv, true);
}


final public static Vec  asin(Vec v) {
    Vec rv = new Vec(v.length());
    for (int k=0; k<v.length(); k++) 
        rv.putAt(k,  Math.asin(v.v[k]));
    return rv;
}

final public static Vec  acos(Vec v) {
    Vec rv = new Vec(v.length());
    for (int k=0; k<v.length(); k++) 
        rv.putAt(k,  Math.acos(v.v[k]));
    return rv;
}

final public static Vec  atan(Vec v) {
    Vec rv = new Vec(v.length());
    for (int k=0; k<v.length(); k++) 
        rv.putAt(k,  Math.atan(v.v[k]));
    return rv;
}


final public static Vec  tanh(Vec v) {
    Vec rv = new Vec(v.length());
    for (int k=0; k<v.length(); k++) 
        rv.putAt(k,  Math.tanh(v.v[k]));
    return rv;
}


final public static Vec  cosh(Vec v) {
    Vec rv = new Vec(v.length());
    for (int k=0; k<v.length(); k++) 
        rv.putAt(k,  Math.cosh(v.v[k]));
    return rv;
}


final public static Vec  sinh(Vec v) {
    Vec rv = new Vec(v.length());
    for (int k=0; k<v.length(); k++) 
        rv.putAt(k,  Math.sinh(v.v[k]));
    return rv;
}


final public static Vec  round(Vec v) {
    Vec rv = new Vec(v.length());
    for (int k=0; k<v.length(); k++) 
        rv.putAt(k,  Math.round(v.v[k]));
    return rv;
}


final public static Vec  floor(Vec v) {
    Vec rv = new Vec(v.length());
    for (int k=0; k<v.length(); k++) 
        rv.putAt(k,  Math.floor(v.v[k]));
    return rv;
}


final public static Vec  ceil(Vec v) {
    Vec rv = new Vec(v.length());
    for (int k=0; k<v.length(); k++) 
        rv.putAt(k,  Math.ceil(v.v[k]));
    return rv;
}


final public static Vec  sqrt(Vec v) {
    Vec rv = new Vec(v.length());
    for (int k=0; k<v.length(); k++) 
        rv.putAt(k,  Math.sqrt(v.v[k]));
    return rv;
}



final public static Vec  exp(Vec v) {
    Vec rv = new Vec(v.length());
    for (int k=0; k<v.length(); k++) 
        rv.putAt(k,  Math.exp(v.v[k]));
    return rv;
}


final public static Vec  log(Vec v) {
    Vec rv = new Vec(v.length());
    for (int k=0; k<v.length(); k++) 
        rv.putAt(k,  Math.log(v.v[k]));
    return rv;
}


final public static Vec  log10(Vec v) {
    Vec rv = new Vec(v.length());
    for (int k=0; k<v.length(); k++) 
        rv.putAt(k,  Math.log10(v.v[k]));
    return rv;
}


final public static Vec  log1p(Vec v) {
    Vec rv = new Vec(v.length());
    for (int k=0; k<v.length(); k++) 
        rv.putAt(k,  Math.log1p(v.v[k]));
    return rv;
}

final public static int length(Vec v) {
    return v.length();
}

final public static int length(Mat m) {
    return m.Nrows();
}

final public void  browse() {
    gExec.gui.watchMatrix.display(this.getArray());
}

final public void  browse(String matrixName) {
    gExec.gui.watchMatrix.display(this.getArray(), matrixName);
}


 

/* test example:  Compute eigenvalues with JAMA and Native BLAS and compare times
 N = 10
 A = Rand(N, N)
 tic()
 eigJama = eig_D(A)
 tmJama = toc()
 tic()
 eigJBLAS = jblas_eigenvalues(A)
 tmJBLAS = toc()
 */ 


    //  Computes the eigenvalues of a general matrix.
final public static ComplexDoubleMatrix jblas_eigenvalues(double [][]dM) {
    return org.jblas.Eigen.eigenvalues(new DoubleMatrix(dM));
}



final public static ComplexDoubleMatrix jblas_eigenvalues(Mat dM) {
     return jblas_eigenvalues(dM.getArray());
}


   //   Computes the eigenvalues and eigenvectors of a general matrix.
   //   returns an array of ComplexDoubleMatrix objects containing the eigenvectors
   //          stored as the columns of the first matrix, and the eigenvalues as the
   //         diagonal elements of the second matrix.
final public static ComplexDoubleMatrix [] jblas_eigenvectors(double [][]dM) {
    return org.jblas.Eigen.eigenvectors(new DoubleMatrix(dM));
}

   
final public static ComplexDoubleMatrix [] jblas_eigenvectors(Mat dM) {
     return jblas_eigenvectors(dM.getArray());
}


//  Compute the eigenvalues for a symmetric matrix.
final public static DoubleMatrix  jblas_symmetricEigenvalues(double [][]dM) {
    return org.jblas.Eigen.symmetricEigenvalues(new DoubleMatrix(dM));
}

final public static DoubleMatrix  jblas_symmetricEigenvalues(Mat dM) {
    return jblas_symmetricEigenvalues(dM.getArray());
}


//  Computes the eigenvalues and eigenvectors for a symmetric matrix.
//  returns an array of DoubleMatrix objects containing the eigenvectors
//         stored as the columns of the first matrix, and the eigenvalues as
//         diagonal elements of the second matrix.
final public static DoubleMatrix []  jblas_symmetricEigenvectors(double [][]dM) {
    return org.jblas.Eigen.symmetricEigenvectors(new DoubleMatrix(dM));
}

final public static DoubleMatrix  [] jblas_symmetricEigenvectors(Mat dM) {
    return  jblas_symmetricEigenvectors(dM.getArray());
}

//  Computes generalized eigenvalues of the problem A x = L B x.
// @param A symmetric Matrix A. Only the upper triangle will be considered.
//  @param B symmetric Matrix B. Only the upper triangle will be considered.
//  @return a vector of eigenvalues L.
final public static DoubleMatrix jblas_symmetricGeneralizedEigenvalues( double [][] A, double [][] B) {
    return org.jblas.Eigen.symmetricGeneralizedEigenvalues(new DoubleMatrix(A), new DoubleMatrix(B));
}

final public static DoubleMatrix jblas_symmetricGeneralizedEigenvalues( Mat A, Mat B) {
    return jblas_symmetricGeneralizedEigenvalues(A.getArray(), B.getArray());
}


final public static DoubleMatrix [] jblas_symmetricGeneralizedEigenvectors( double [][] A, double [][] B) {
    return org.jblas.Eigen.symmetricGeneralizedEigenvectors(new DoubleMatrix(A), new DoubleMatrix(B));
}

final public static DoubleMatrix [] jblas_symmetricGeneralizedEigenvectors( Mat A, Mat B) {
    return jblas_symmetricGeneralizedEigenvectors(A.getArray(), B.getArray());
}

 //
   // Compute Cholesky decomposition of A
     
     //@param A symmetric, positive definite matrix (only upper half is used)
     // @return upper triangular matrix U such that  A = U' * U
     //
final public static  DoubleMatrix  jblas_cholesky(double [][]A) {
  return org.jblas.Decompose.cholesky(new DoubleMatrix(A));
}

final public static  DoubleMatrix  jblas_cholesky(Mat A) {
  return jblas_cholesky(A.getArray());
}


    // 
     // Solve a general problem A x = L B x.
     
     // @param A symmetric matrix A
     // @param B symmetric matrix B
     // @return an array of matrices of length two. The first one is an array of the eigenvectors X
     //         The second one is A vector containing the corresponding eigenvalues L.
     

final public static DoubleMatrix jblas_solve(double [][]A, double [][] B) {
    return org.jblas.Solve.solve(new DoubleMatrix(A),  new DoubleMatrix(B));
}

final public static DoubleMatrix jblas_solve(Mat A, Mat B) {
    return jblas_solve(A.getArray(),  B.getArray());
}

final public static DoubleMatrix jblas_solveSymmetric(double [][]A, double [][] B) {
    return org.jblas.Solve.solveSymmetric(new DoubleMatrix(A),  new DoubleMatrix(B));
}

final public static DoubleMatrix jblas_solveSymmetric(Mat A, Mat B) {
    return jblas_solveSymmetric(A.getArray(),  B.getArray());
}


final public static DoubleMatrix jblas_solvePositive(double [][]A, double [][] B) {
    return org.jblas.Solve.solvePositive(new DoubleMatrix(A),  new DoubleMatrix(B));
}

final public static DoubleMatrix jblas_solvePositive(Mat A, Mat B) {
    return jblas_solvePositive(A.getArray(),  B.getArray());
}

//
   // Compute a singular-value decomposition of A.
  
  // @return A DoubleMatrix[3] array of U, S, V such that A = U * diag(S) * V'
     
final public static DoubleMatrix []  jblas_fullSVD( double [][]A) {
    return org.jblas.Singular.fullSVD(new DoubleMatrix(A));
}

final public static DoubleMatrix []  jblas_fullSVD( Mat  A) {
    return  jblas_fullSVD(A.getArray());
}


    //
     // Compute a singular-value decomposition of A (sparse variant).
     // Sparse means that the matrices U and V are not square but
     // only have as many columns (or rows) as possible.
      
     // @param A
     // @return A DoubleMatrix[3] array of U, S, V such that A = U * diag(S) * V'
     



final public static ComplexDoubleMatrix []  jblas_sparseSVD( double [][]Areal, double [][] Aimag) {
    return org.jblas.Singular.sparseSVD(
            new ComplexDoubleMatrix(new DoubleMatrix(Areal),  new DoubleMatrix(Aimag)));
}

final public static ComplexDoubleMatrix []  jblas_sparseSVD( Mat Areal, Mat Aimag) {
    return jblas_sparseSVD(Areal.getArray(), Aimag.getArray());
}



  

final public static DoubleMatrix jblas_SPDValues(double [][]Areal, double [][]Aimag) {
    return  org.jblas.Singular.SVDValues(
            new ComplexDoubleMatrix(new DoubleMatrix(Areal), new DoubleMatrix(Aimag)));
}

  
     // Compute the singular values of a complex matrix.
      // @param Areal, Aimag : the real and imaginary components of a  ComplexDoubleMatrix of dimension m * n
     // @return A real-valued (!) min(m, n) vector of singular values.
     
final public static DoubleMatrix jblas_SPDValues(Mat Areal, Mat Aimag) {
    return jblas_SPDValues(Areal.getArray(), Aimag.getArray());
}

public static String   printArray(double [] va) {
     if (va != null) {
   StringBuilder  sb = new StringBuilder();
   String  formatString = "0.";
   for (int k = 0; k < groovySci.PrintFormatParams.vecDigitsPrecision; k++) 
       formatString += "0";
    DecimalFormat digitFormat = new DecimalFormat(formatString);
    digitFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(new Locale("us")));
    
     int  mxElems = va.length;
     String  moreElems = "";
     if (mxElems > groovySci.PrintFormatParams.vecMxElemsToDisplay )  {
          // vector has more elements than we can display
         mxElems = groovySci.PrintFormatParams.vecMxElemsToDisplay;
         moreElems = " .... ";
     }
    int  i=0;
     while (i < mxElems) {
       sb.append(digitFormat.format(va[i])+"  ");
        i++;
       }
     sb.append(moreElems+"\n");
     
 
   return sb.toString(); 
     }
     else return "";
}


  // global routine used to display 2-d arrays with toString() that truncates presentation of rows/cols and
  // controls the digits of precision that the numbers are displayed
  public static String  printArray( double [][] a)  {
    
    if (a!=null) {
 if  (groovySci.PrintFormatParams.getVerbose()==true)  {
    String  formatString = "0.";
    for (int k =0; k <  matDigitsPrecision; k++)  
       formatString += "0";
    
    DecimalFormat  digitFormat = new DecimalFormat(formatString);
    digitFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(new Locale("us")));
     
    int  rowsToDisplay = a.length;
    int  colsToDisplay = a[0].length;
    boolean   truncated = false;

    String  truncationIndicator = "";
    if  ( matMxRowsToDisplay < rowsToDisplay ) {
        rowsToDisplay = matMxRowsToDisplay;
        truncationIndicator = " ... ";
        truncated = true;
      }
     if  (matMxColsToDisplay < colsToDisplay) {
        colsToDisplay  = matMxColsToDisplay;
        truncationIndicator = " ... ";
      }
     int  i=0; int  j=0;
     StringBuilder sb = new StringBuilder("\n");
    while (i < rowsToDisplay) {
        j = 0;
        while (j < colsToDisplay ) {
       sb.append(digitFormat.format(a[i][j])+"  ");
       j++;
        }
      sb.append(truncationIndicator+"\n");
     i++;
    }
   if (truncated)     // handle last line
    sb.append( ".........................................................................................................................................");

     return sb.toString();
      }
    }
  
  
 return "";
 }

  /* Apache Common Routines */
  
  /* e,g
  
   A = Mat(3, 3, 
           0.5, -1.2, 4.5,
           -1.2, 6.7, -1.1,
            0.3, 0.55, -1.1)
            
   B = Mat(3, 1, 3.4, 9.3, -1.1)

   x = solveAC(A, B)

   shouldBeZero = A*x-B
   */
  public static Mat solveAC(Mat A, Mat B) {
      
  org.apache.commons.math3.linear.Array2DRowRealMatrix  rmA = 
          new org.apache.commons.math3.linear.Array2DRowRealMatrix(A.d);  // transform it to an Apache Commons Array2DRealMatrix
  org.apache.commons.math3.linear.LUDecomposition   luObj =
          new org.apache.commons.math3.linear.LUDecomposition(rmA);   // perform an LU decomposition on A


// solve for  one  right-hand side

  org.apache.commons.math3.linear.Array2DRowRealMatrix  rmB = 
          new org.apache.commons.math3.linear.Array2DRowRealMatrix(B.d);  // transform it to an Apache Commons Array2DRealMatrix        

 org.apache.commons.math3.linear.DecompositionSolver solver = luObj.getSolver();
Mat x = new Mat(solver.solve(rmB).getData());

return x;
  } 

  // JBLAS routines
  
    //  Computes the eigenvalues of the matrix.using JBLAS
public ComplexDoubleMatrix jblas_eigenvalues() {
    DoubleMatrix dM = new DoubleMatrix(this.getArray());
    return org.jblas.Eigen.eigenvalues(dM);
}

 
   //   Computes the eigenvalues and eigenvectors of a general matrix.
   //   returns an array of ComplexDoubleMatrix objects containing the eigenvectors
   //          stored as the columns of the first matrix, and the eigenvalues as the
   //         diagonal elements of the second matrix.
public  ComplexDoubleMatrix[]  jblas_eigenvectors() {
    DoubleMatrix dM = new DoubleMatrix(this.getArray());
    return org.jblas.Eigen.eigenvectors(dM);
}
     
//  Compute the eigenvalues for a symmetric matrix.
public  DoubleMatrix  jblas_symmetricEigenvalues() {
    DoubleMatrix dM = new DoubleMatrix(this.getArray());
    return org.jblas.Eigen.symmetricEigenvalues(dM);
}


//  Computes the eigenvalues and eigenvectors for a symmetric matrix.
//  returns an array of DoubleMatrix objects containing the eigenvectors
//         stored as the columns of the first matrix, and the eigenvalues as
//         diagonal elements of the second matrix.
public  DoubleMatrix []  jblas_symmetricEigenvectors() {
    DoubleMatrix dM = new DoubleMatrix(this.getArray());
    return org.jblas.Eigen.symmetricEigenvectors(dM);
}

//  Computes generalized eigenvalues of the problem A x = L B x.
// @param A symmetric Matrix A (A is this). Only the upper triangle will be considered.
//  @param B symmetric Matrix B. Only the upper triangle will be considered.
//  @return a vector of eigenvalues L.
    
public  DoubleMatrix jblas_symmetricGeneralizedEigenvalues( double [][] B) {
    return org.jblas.Eigen.symmetricGeneralizedEigenvalues(new DoubleMatrix(this.getArray()), new DoubleMatrix(B));
}

    /**
     * Solve a general problem A x = L B x.
     *
     * @param A symmetric matrix A ( A is this )
     * @param B symmetric matrix B
     * @return an array of matrices of length two. The first one is an array of the eigenvectors X
     *         The second one is A vector containing the corresponding eigenvalues L.
     */
public DoubleMatrix [] jblas_symmetricGeneralizedEigenvectors( double [][] B) {
    return org.jblas.Eigen.symmetricGeneralizedEigenvectors(new DoubleMatrix(this.getArray()), new DoubleMatrix(B));
}

    /**
     * Compute Cholesky decomposition of A
     *
     * @param A symmetric, positive definite matrix (only upper half is used)
     * @return upper triangular matrix U such that  A = U' * U
     */
public DoubleMatrix  jblas_cholesky() {
  return org.jblas.Decompose.cholesky(new DoubleMatrix(this.getArray()));
}

/** Solves the linear equation A*X = B.  ( A is this) */
public DoubleMatrix jblas_solve( double [][] B) {
    return org.jblas.Solve.solve(new DoubleMatrix(this.getArray()),  new DoubleMatrix(B));
}

/** Solves the linear equation A*X = B for symmetric A.  ( A is this) */
public DoubleMatrix jblas_solveSymmetric(double [][] B) {
    return org.jblas.Solve.solveSymmetric(new DoubleMatrix(this.getArray()),  new DoubleMatrix(B));
}

/** Solves the linear equation A*X = B for symmetric and positive definite A. ( A is this ) */
public DoubleMatrix jblas_solvePositive(double [][] B) {
    return org.jblas.Solve.solvePositive(new DoubleMatrix(this.getArray()),  new DoubleMatrix(B));
}

 /**
     * Compute a singular-value decomposition of A. ( A is this )
     *
     * @return A DoubleMatrix[3] array of U, S, V such that A = U * diag(S) * V'
     */

public DoubleMatrix []  jblas_fullSVD( ) {
    return org.jblas.Singular.fullSVD(new DoubleMatrix(this.getArray()));
}


    /**
     * Compute a singular-value decomposition of A (sparse variant) (A is this)
     * Sparse means that the matrices U and V are not square but
     * only have as many columns (or rows) as possible.
     * 
     * @param A
     * @return A DoubleMatrix[3] array of U, S, V such that A = U * diag(S) * V'
     */

public  DoubleMatrix []  jblas_sparseSVD( ) {
    return org.jblas.Singular.sparseSVD(new DoubleMatrix(this.getArray()));
}


public ComplexDoubleMatrix []  jblas_sparseSVD( double [][] Aimag) {
    return org.jblas.Singular.sparseSVD(
            new ComplexDoubleMatrix(new DoubleMatrix(this.getArray()),  new DoubleMatrix(Aimag)));
}


  /**
     * Compute the singular values of a matrix.
     *
     * @param A DoubleMatrix of dimension m * n
     * @return A min(m, n) vector of singular values.
     */

public  DoubleMatrix jblas_SPDValues() {
    return  org.jblas.Singular.SVDValues(new DoubleMatrix(this.getArray()));
}

    /**
     * Compute the singular values of a complex matrix.
     *
     * @param Areal, Aimag : the real and imaginary components of a  ComplexDoubleMatrix of dimension m * n
     * @return A real-valued (!) min(m, n) vector of singular values.
     */

public  DoubleMatrix  jblas_SPDValues( double [][]Aimag) {
    return  org.jblas.Singular.SVDValues(
            new ComplexDoubleMatrix(new DoubleMatrix(this.getArray()), new DoubleMatrix(Aimag)));
}



// native C multiplication 
public  Mat  cc( double [][] that) {
    double [][] This = this.getArray();
    return new Mat( cc ( This, that));
}


public  Mat  cc( Mat that) {
    double [][] This = this.getArray();
    return new Mat( cc ( This, that.getArray()));
}

// native C multiplication 
public static double [][]  cc( double [][] This, double [][] that) {
    double [] oneDThis = oneDDoubleArray( This );   // construct a DoubleMatrix for the receiver
    double [] oneDThat = oneDTransposeDoubleArray(that);  // construct a DoubleMatrix for the argument
    int  Arows = This.length;  int  Acolumns = This[0].length;
    int  Ccolumns = that[0].length;
    double [] result = new double[Arows*Ccolumns];
   
    gExec.Interpreter.NativeLibsObj.nrObj.mul(oneDThis, Arows, Acolumns, oneDThat, Ccolumns, result);
    
    double [][]  rd = new double [Arows][Ccolumns];
    int cnt = 0;
    int  r = 0; int c = 0;
    while (r < Arows ) {
      c = 0;
      while (c < Ccolumns) {
        rd[r][c] = result[cnt];
        cnt++;
        c++;
      }
      r++;
    }
    return rd;
}   




// native C multithreaded multiplication 
public static double [][]  pt( double [][] This, double [][] that) {
    double [] oneDThis = oneDDoubleArray( This );   // construct a DoubleMatrix for the receiver
    double [] oneDThat = oneDTransposeDoubleArray(that);  // construct a DoubleMatrix for the argument
    int  Arows = This.length;  int  Acolumns = This[0].length;
    int  Ccolumns = that[0].length;
    double [] result = new double[Arows*Ccolumns];
   
    gExec.Interpreter.NativeLibsObj.ccObj.pt(oneDThis, Arows, Arows, oneDThat, Arows, result);
    
    double [][]  rd = new double [Arows][Ccolumns];
    int cnt = 0;
    int  r = 0; int c = 0;
    while (r < Arows ) {
      c = 0;
      while (c < Ccolumns) {
        rd[r][c] = result[cnt];
        cnt++;
        c++;
      }
      r++;
    }
    return rd;
}   

/*
Solve a general linear system  A*x = b.

     int solv(double a[],double b[],int n)
       a = array containing system matrix A in row order
            (altered to L-U factored form by computation)
       b = array containing system vector b at entry and
           solution vector x at exit
       n = dimension of system
      return:  0 -> normal exit
              -1 -> singular input
*/
/* Example:
 
A = [ [0.23, -0.5, -1.2], [5.6, 3.4, -0.01], [-0.23, 0.33, -0.3]] as double [][]
b = [-0.01, 0.2, 9.2] as double []

x = ccsolv(A, b)
A*x-b
*/
public static  double []  ccsolv(double [][] A,  double [] b)   {
    CCOps.CCOps   ccObj = gExec.Interpreter.NativeLibsObj.ccObj;

    double []  bc = b.clone();
      // get a one-D representation of the RichDouble2DArray
    double []  ad = oneDDoubleArray(A);
    int  M = A.length;
   
    // solve using C routine
    ccObj.solv(ad, bc, M);
    
   return  bc;
    }


public static  Mat   ccsolv(Mat A,  Mat b)   {
    double [] sol = ccsolv(A.getArray(), b.getv());
    return t(new Mat(sol));
} 

/*
Solve a symmetric positive definite linear system S*x = b.

     int solvps(double a[],double b[],int n)
       a = array containing system matrix S (altered to
            Cholesky upper right factor by computation)
       b = array containing system vector b as input and
           solution vector x as output
       n = dimension of system
      return: 0 -> normal exit
              1 -> input matrix not positive definite
*/
public static double [] ccsolvps( double [][] A, double [] b)  {
  CCOps.CCOps   ccObj = gExec.Interpreter.NativeLibsObj.ccObj;

  double [] bc = b.clone();
      // get a one-D representation of the RichDouble2DArray
    double [] ad = oneDDoubleArray(A);
    int  M = A.length;
    
    // solve using C routine
    ccObj.solvps( ad, bc, M);
    
    return bc;
    }
    
  
  

    
  // Compute the singular values of a real m by n matrix A.
  public static double []  ccsvdval(double [][]A)  {
    CCOps.CCOps   ccObj = gExec.Interpreter.NativeLibsObj.ccObj;
    
    double [] dlmThis = oneDDoubleArray(A);
    int  M = A.length;   int  N = A[0].length;
            
    double [] d = new double [N];   // for the singular values
    
    ccObj.svdval(d, dlmThis, M, N);
    
    
    return d;   // return the singular values
    
    }
    
  /* Example:
   

A = [ [0.23, -0.5, -1.2], [5.6, 3.4, -0.01], [-0.23, 0.33, -0.3]] as double [][]

// perform SVD using native C library
x = ccsvd(A)

shouldBeZero = diag (3, 1) - x.U* (t(x.U))  // test matrix U orthogonality

shouldBeIdentity = x.V*(t(x.V))  // test matrix V orthogonality

shouldBeZeroSVDCondition = x.U*diag(x.W)*t(x.V) - A   // should be zero
   */
 
  public static double [][] twoDFromOneArray(double [] u, int M, int N) {
      
    double [][] rv = new double[M][N];
    int  cnt = 0;
    int  r = 0;  int  c = 0;
    while (r <  M ) {
      c = 0;
      while (c < N) {
        rv[r][c] = u[cnt];
        cnt++;
        c++;
      }
      r++;
    }
    return rv;
  }
  
  
  public static SvdResults  ccsvd(double [][] A)  {
    CCOps.CCOps   ccObj = gExec.Interpreter.NativeLibsObj.ccObj;

    double []  dlmThis = oneDDoubleArray(A);
    int  M = A.length; int  N = A[0].length;
    double [] d = new double [N]; // for the singular values
    double [] u =  new double [M*M];
    double [] v = new double [N*N];
    
    ccObj.svduv(d, dlmThis, u, M, v, N);
  
    
    double [][] ud = twoDFromOneArray(u, M, M);
        
    double [][] vd = twoDFromOneArray(v, N, N);
    
    SvdResults rsvd = new SvdResults();
    rsvd.U = ud;
    rsvd.V = vd;
    rsvd.W = d;
    
    return rsvd;
    
  }
  // SOS: continue here
  
  public static SvdResults  ccsvd(Mat  A)  {
     return ccsvd(A.d);
  }
  
  public static double [][] ccinv(double [][] A)  {
    CCOps.CCOps   ccObj = gExec.Interpreter.NativeLibsObj.ccObj;
  
    double [] dlmThis = oneDDoubleArray(A);
    int M = A.length;
    ccObj.minv(dlmThis, M);  // invert in-place
    
    // construct the inverse output array as 2-D array
    double [][] dd = new double [M][M];
    int cnt = 0;
    int r = 0; int c = 0;
    while (r < M) {
        c = 0;
        while (c < M) {
            dd[r][c] = dlmThis[cnt];
            cnt++;
            c++;
        }
        r++;
        }
    return dd;
    
    }

  public static Mat ccinv(Mat  A)  {
      return new Mat(ccinv(A.d));
  }

  
/*
Compute the eigenvalues and eigenvectors of a real symmetric
     matrix A.

     void eigen(double *a,double *ev,int n)
     double *a,*ev; int n;
       a = pointer to store for symmetric n by n input
           matrix A. The computation overloads this with an
           orthogonal matrix of eigenvectors E.
       ev = pointer to the array of the output eigenvalues
       n = dimension parameter (dim(a)= n*n, dim(ev)= n)

     The input and output matrices are related by

          A = E*D*E~ where D is the diagonal matrix of eigenvalues
          D[i,j] = ev[i] if i=j and 0 otherwise.

     The columns of E are the eigenvectors.
*/

public static EigResults ceigen(double[][]a) {
    double [] ca = oneDDoubleArray(a);
    int N = a.length;
    double [] ev = new double [N];
    CCOps.CCOps   ccObj = gExec.Interpreter.NativeLibsObj.ccObj;
  
    ccObj.eigen(ca, ev, N);
    double [][] evecs = new double[N][N];
    int cnt=0;
    for (int r = 0; r < N; r++)
        for (int c=0; c<N; c++) 
            evecs[r][c] = ca[cnt++];
        
    EigResults er = new EigResults();
    er.realEvecs = evecs;
    er.realEvs = ev;
    
    return er;
}

public static EigResults ceigen(Mat a) {
    return ceigen(a.getArray());
}


  // FFT routines  based on CUDA
public static  float []  jcufft(float [] x) {
           float []   outputJCufft = x.clone();
           cufftHandle    plan = new cufftHandle();
           int  siz = x.length;
           JCufft.cufftPlan1d(plan, siz, cufftType.CUFFT_C2C, 1);
           JCufft.cufftExecC2C(plan, outputJCufft, outputJCufft, JCufft.CUFFT_FORWARD);
           JCufft.cufftDestroy(plan);
           return outputJCufft;
 }
  
 public static  double [] jcufft(double [] xd ) {
      int  siz = xd.length;
      float []  x =  new float[siz];
      int   k = 0;
      while (k<siz) {
        x[k]= (float) xd[k];
        k++;
      }
       float []  rf = jcufft(x);
       double []   xr = new double [siz];
        k = 0;
      while (k<siz) {
        xr[k] = rf[k]; 
        k++;
      }
        
    return xr;
  }  
    
 // implement sone transforms using Parallel Colt library
 
    /**
     * Computes the 2D discrete cosine transform (DCT-II) of this matrix.
     * 
     * @param scale
     *            if true then scaling is performed
     * 
     */
public Mat    dct2(boolean scale) {
    DenseDoubleMatrix2D dm = new DenseDoubleMatrix2D(getArray());
    dm.dct2(scale);
    return new Mat(dm.toArray());
}


    /**
     * Computes the 2D discrete cosine transform (DCT-II) of this matrix.
     * 
     * @param scale
     *            if true then scaling is performed
     * 
     */
public Mat    dctColumns(boolean scale) {
    DenseDoubleMatrix2D dm = new DenseDoubleMatrix2D(getArray());
    dm.dctColumns(scale);
    return new Mat(dm.toArray());
}


    /**
     * Computes the discrete cosine transform (DCT-II) of each row of this
     * matrix.
     * 
     * @param scale
     *            if true then scaling is performed
     * 
     */
public Mat    dctRows(boolean scale) {
    DenseDoubleMatrix2D dm = new DenseDoubleMatrix2D(getArray());
    dm.dctRows(scale);
    return new Mat(dm.toArray());
}

/**
     * Computes the 2D discrete Hartley transform (DHT) of this matrix.
     * 
     */
public Mat    dht2() {
    DenseDoubleMatrix2D dm = new DenseDoubleMatrix2D(getArray());
    dm.dht2();
    return new Mat(dm.toArray());
}


    /**
     * Computes the discrete Hartley transform (DHT) of each column of this
     * matrix.
     * 
     */
public Mat    dhtColumns() {
    DenseDoubleMatrix2D dm = new DenseDoubleMatrix2D(getArray());
    dm.dhtColumns();
    return new Mat(dm.toArray());
}


    /**
     * Computes the discrete Hartley transform (DHT) of each row of this
     * matrix.
     * 
     */
public Mat    dhtRows() {
    DenseDoubleMatrix2D dm = new DenseDoubleMatrix2D(getArray());
    dm.dhtRows();
    return new Mat(dm.toArray());
}


    /**
     * Computes the 2D discrete sine transform (DST-II) of this matrix.
     * 
     * @param scale
     *            if true then scaling is performed
     * 
     */
public Mat    dst2(boolean scale) {
    DenseDoubleMatrix2D dm = new DenseDoubleMatrix2D(getArray());
    dm.dst2(scale);
    return new Mat(dm.toArray());
}


    /**
     * Computes the discrete sine transform (DST-II) of each column of this
     * matrix.
     * 
     * @param scale
     *            if true then scaling is performed
     * 
     */
public Mat    dstColumns(boolean scale) {
    DenseDoubleMatrix2D dm = new DenseDoubleMatrix2D(getArray());
    dm.dstColumns(scale);
    return new Mat(dm.toArray());
}


    /**
     * Computes the discrete sine transform (DST-II) of each row of this matrix.
     * 
     * @param scale
     *            if true then scaling is performed
     * 
     */
public Mat    dstRows(boolean scale) {
    DenseDoubleMatrix2D dm = new DenseDoubleMatrix2D(getArray());
    dm.dstRows(scale);
    return new Mat(dm.toArray());
}

/**
     * Computes the 2D discrete Fourier transform (DFT) of this matrix. The
     * physical layout of the output data is as follows:
     * 
     * <pre>
     * this[k1][2*k2] = Re[k1][k2] = Re[rows-k1][columns-k2], 
     * this[k1][2*k2+1] = Im[k1][k2] = -Im[rows-k1][columns-k2], 
     *       0&lt;k1&lt;rows, 0&lt;k2&lt;columns/2, 
     * this[0][2*k2] = Re[0][k2] = Re[0][columns-k2], 
     * this[0][2*k2+1] = Im[0][k2] = -Im[0][columns-k2], 
     *       0&lt;k2&lt;columns/2, 
     * this[k1][0] = Re[k1][0] = Re[rows-k1][0], 
     * this[k1][1] = Im[k1][0] = -Im[rows-k1][0], 
     * this[rows-k1][1] = Re[k1][columns/2] = Re[rows-k1][columns/2], 
     * this[rows-k1][0] = -Im[k1][columns/2] = Im[rows-k1][columns/2], 
     *       0&lt;k1&lt;rows/2, 
     * this[0][0] = Re[0][0], 
     * this[0][1] = Re[0][columns/2], 
     * this[rows/2][0] = Re[rows/2][0], 
     * this[rows/2][1] = Re[rows/2][columns/2]
     * </pre>
     * 
     * This method computes only half of the elements of the real transform. The
     * other half satisfies the symmetry condition. If you want the full real
     * forward transform, use <code>getFft2</code>. To get back the original
     * data, use <code>ifft2</code>.
     * 
     * @throws IllegalArgumentException
     *             if the row size or the column size of this matrix is not a
     *             power of 2 number.
     * 
     */
    
public Mat    fft2(boolean scale) {
    DenseDoubleMatrix2D dm = new DenseDoubleMatrix2D(getArray());
    dm.fft2();
    return new Mat(dm.toArray());
}

}

