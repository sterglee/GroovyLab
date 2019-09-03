
package groovySci.math.array;

import Jama.QRDecomposition;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.EVD;
import no.uib.cipr.matrix.LQ;
import no.uib.cipr.matrix.LowerTriangDenseMatrix;
import no.uib.cipr.matrix.NotConvergedException;
import org.netlib.lapack.LAPACK;
import org.netlib.util.doubleW;
import org.netlib.util.intW;

public class JILapack {
    
    static int ld(int n) {
		return Math.max(1, n);
	}

    
    
     // compute eigenvalues using LAPACK
public static EigResults  EigMTJ(double [][] inM)   {
             DenseMatrix   DM = new  DenseMatrix(inM);
             EVD  evdObj = null;
        try {
            evdObj = no.uib.cipr.matrix.EVD.factorize(DM);
        } catch (NotConvergedException ex) {
            Logger.getLogger(JILapack.class.getName()).log(Level.SEVERE, null, ex);
        }
          
             EigResults er = new EigResults();
             er.realEvs  = evdObj.getRealEigenvalues();
             er.imEvs = evdObj.getImaginaryEigenvalues();
             er.realEvecs = denseMatrixToDoubleArray(evdObj.getLeftEigenvectors());
             er.imEvecs = denseMatrixToDoubleArray(evdObj.getRightEigenvectors());
    
            return er;    
    
}

public static EigResults  EigMTJ(double [][] inM, boolean left, boolean right )   {
             DenseMatrix   DM = new  DenseMatrix(inM);
             EVD  evdObj = new no.uib.cipr.matrix.EVD(inM.length, left, right);
        try {
            evdObj = evdObj.factor(DM);
        } catch (NotConvergedException ex) {
            Logger.getLogger(JILapack.class.getName()).log(Level.SEVERE, null, ex);
        }
          
             EigResults er = new EigResults();
             er.realEvs  = evdObj.getRealEigenvalues();
             er.imEvs = evdObj.getImaginaryEigenvalues();
             if (left)
               er.realEvecs = denseMatrixToDoubleArray(evdObj.getLeftEigenvectors());
             if (right)
               er.imEvecs = denseMatrixToDoubleArray(evdObj.getRightEigenvectors());
    
            return er;    
    
}


public static double [][] lowerTriangDenseMatrixToDoubleArray(LowerTriangDenseMatrix ltdm) {
  int nrows = ltdm.numRows();
  int ncols = ltdm.numColumns();
  double  [][] dres = new double[nrows][ncols];
  for (int r=0; r<nrows; r++ ) 
      for (int c=0; c<ncols; c++)
          dres[r][c] = ltdm.get(r, c);
  
  return dres;
          
}

public static double [][] denseMatrixToDoubleArray(DenseMatrix dm) {
  int nrows = dm.numRows();
  int ncols = dm.numColumns();
  double  [][] dres = new double[nrows][ncols];
  for (int r=0; r<nrows; r++ ) 
      for (int c=0; c<ncols; c++)
          dres[r][c] = dm.get(r, c);
  
  return dres;
          
}

public static double [] denseVectorToDoubleArray(DenseVector dv) {
  int vlen = dv.size();
  double  [] vres = new double[vlen];
  for (int v=0; v< vlen; v++ ) 
      vres[v] = dv.get(v);
  
  return vres;
          
}


public static LQResults LQ (double [][] inM) {
  DenseMatrix  DM = new DenseMatrix(inM);
  LQ  LQDecomp = LQ.factorize(DM);
  LQResults  lq = new LQResults();
  
  LowerTriangDenseMatrix ltdm = LQDecomp.getL();

  lq.Q = denseMatrixToDoubleArray(LQDecomp.getQ());
  lq.L = lowerTriangDenseMatrixToDoubleArray(LQDecomp.getL());
  
  return lq;
   
}


public static double [][] LU(double [][]inM) {
    DenseMatrix DM = new DenseMatrix(inM);
    DenseMatrix DMc = DM.copy();
    int numRows = inM.length;
    int numCols = inM[0].length;
    int [] lpiv = new int[Math.min(numRows, numCols)];
    intW info = new intW(0);
    LAPACK.getInstance().dgetrf(numRows, numCols, DMc.getData(),  ld(numRows), lpiv,  info);

    if (info.val != 0) 
        System.out.println("illegal LU with code:  "+info.val);
    
    return DMc.toDoubleArray();
}

// does't work correctly but why?
public static LUResults LU_PLU(double [][]inM ) {
    DenseMatrix DM = new DenseMatrix(inM);
    DenseMatrix DMc = DM.copy();
    int numRows = inM.length;
    int numCols = inM[0].length;
    int minRC = Math.min(numRows, numCols);
    int [] lpiv = new int[minRC];
    intW info = new intW(0);
    LAPACK.getInstance().dgetrf(numRows, numCols, DMc.getData(),  ld(numRows), lpiv,  info);

    if (info.val != 0) 
        System.out.println("illegal LU with code:  "+info.val);
    
    for (int k=0; k<minRC; k++)
        if (lpiv[k] >= minRC) lpiv[k]=k;
    
    double [][] rm =  DMc.toDoubleArray();
    LUResults lur = new LUResults();
    lur.P = new double[numRows][numCols];
    lur.L = new double[numCols][numRows];
    lur.U = new double[numRows][numCols];
    
    for (int k=0; k<lpiv.length; k++)
        lur.P[k][lpiv[k]] = 1.0;
    for (int r=0; r<numRows; r++) 
        for (int c=0; c<numCols; c++) {
            if (c >= r)
                lur.U[r][c] = rm[r][c];
            else
                lur.L[r][c] = rm[r][c];
         }
    
    for (int r=0; r<minRC; r++)
        lur.L[r][r] = 1.0;
    
    return lur;
        }
    


// invert the double [][] array using JLAPACK
public static double[][]  invLapack(double [][]inM) {
    DenseMatrix DM = new DenseMatrix(inM);
    DenseMatrix DMc = DM.copy();
    int numRows = inM.length;
    int numCols = inM[0].length;
    if (numRows != numCols) {
        System.out.println("invLapack() called for non-square matrix");
        return inM;
    }
    
    int [] lpiv = new int[Math.min(numRows, numCols)];
    intW info = new intW(0);
    LAPACK.getInstance().dgetrf(numRows, numCols, DMc.getData(),  ld(numRows), lpiv,  info);

    
    int workSize = -1; // issue workspace query
    double [] work = new double[2];
    LAPACK.getInstance().dgetri(numRows, DMc.getData(), ld(numRows), lpiv, work, workSize, info);
    workSize = (int) work[0];  // take the computed optimal workspace size
    
    work = new double[workSize];
    LAPACK.getInstance().dgetri(numRows, DMc.getData(), ld(numRows), lpiv, work, workSize, info);
    
    
    if (info.val != 0) 
        System.out.println("illegal invLapack with code:  "+info.val);
    
    return DMc.toDoubleArray();  // return the inverse matrix
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

public static double [][]  DGELS(double [][] A, double [][] B) {
    DenseMatrix dA = new DenseMatrix(A);
    DenseMatrix dB = new DenseMatrix(B);

    DenseMatrix dAc = dA.copy();
    DenseMatrix dBc = dB.copy();
    
    int  M = A.length;   // number of rows
    int  N = A[0].length;  // number of columns
    int  NRHS = B[0].length;  // number of right hand sides, i.e., the number of columns of the matrices B and X
    
    intW info = new intW(0);
    
    int workSize = -1; // issue workspace query
    double [] work = new double[2];
    
    int LDA = ld(M);  // leading dimension of A
    int LDB = Math.max(M, N);  // since LDB >= max(1, M, N)
    
    LAPACK.getInstance().dgels("N", M, N, NRHS, dAc.getData(), LDA, dBc.getData(), LDB, work, workSize, info);
    workSize = (int) work[0];  // take the computed optimal workspace size
    
    work = new double[workSize];
    System.out.println("using worksize = "+workSize);
    LAPACK.getInstance().dgels("N", M, N, NRHS, dAc.getData(), LDA, dBc.getData(), LDB, work, workSize, info);

    if (info.val != 0) 
        System.out.println("illegal DGELS with code:  "+info.val);
    
     return dBc.toDoubleArray();
  }


// simpler interface to LAPACK DGELD routine
// computes the minimum norm solutionto a real linear least squares problem:
//   minimise ||b-A*x|| using SVD

public static double [][]  DGELSD(double [][] A, double [][] B) {
    DenseMatrix dA = new DenseMatrix(A);
    DenseMatrix dB = new DenseMatrix(B);

    DenseMatrix dAc = dA.copy();
    DenseMatrix dBc = dB.copy();
    
    int  M = A.length;   // number of rows
    int  N = A[0].length;  // number of columns
    int  NRHS = B[0].length;  // number of right hand sides, i.e., the number of columns of the matrices B and X
    
    intW info = new intW(0);
    
    int workSize = -1; // issue workspace query
    double [] work = new double[2];
    int [] iwork = new int[2];
    
    int LDA = ld(M);  // leading dimension of A
    int LDB = Math.max(M, N);  // since LDB >= max(1, M, N)
    
    int minMN = Math.min(M, N);
    double []   S   = new double[minMN];  // singular values returned in decreasing order
    double Rcond = -1;  // use machine precision
    intW Rank = new intW(1);
    LAPACK.getInstance().dgelsd(M, N, NRHS, dAc.getData(), LDA, dBc.getData(), LDB, S, Rcond, Rank, work, workSize, iwork, info);
    workSize = (int) work[0];  // take the computed optimal workspace size
    
    work = new double[workSize];
    iwork = new int[workSize];
    System.out.println("using worksize in DGELSD = "+workSize);
    LAPACK.getInstance().dgelsd(M, N, NRHS, dAc.getData(), LDA, dBc.getData(), LDB, S, Rcond, Rank, work, workSize, iwork, info);
    
    if (info.val != 0) 
        System.out.println("illegal DGELS with code:  "+info.val);
    
     return dBc.toDoubleArray();
  }







public static int testoptBlockIAENV() {
    int optBlock = 1;
    String subroutineName = "dgels";
    String optionsToSubroutine = "N";
    int N1 = 100; int N2 = 200; 
    int N3 = 100; int N4 = 200; 
    int blockLen = LAPACK.getInstance().ilaenv(optBlock, subroutineName, optionsToSubroutine, N1, N2, N3, N4);
    return blockLen;
}

    
}



/*
 M= 5; N = 3;
 
 A = Rand(M, N)
 B = Rand(M, 2)
 
 x = DGELS(A, B)
 */
    
    

    







/*
 
 N=5
 dda = Rand(N, N)
 lr = LU_PLU(dda)
 
 N =15
 dda = Rand(N, N)
 ddaLU = invLapack(dda)

 shouldBeIdentity = ddaLU*dda
*/
/*
 var ddLQ = Rand(5)
 var ddlq = LQ(ddLQ)
 * 
 */
 
/*
 tic
 var dd  = Rand(9)
 var eigsDD =  jEig(dd)
 var tmalll = toc
 
 tic
 var eigsOnlyDD = jEig(dd, false, false)
 var tmOnlyEvs = toc
 
 * 
 */
