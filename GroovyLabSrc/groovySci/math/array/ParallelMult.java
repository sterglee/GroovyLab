package groovySci.math.array;

import edu.emory.mathcs.utils.ConcurrencyUtils;
import gExec.Interpreter.GlobalValues;
import java.util.concurrent.Future;

public class ParallelMult {

    


    public static double [][]  pmul( final double [][] v1, final  double [][] v2)  {
     if (GlobalValues.hostIsLinux64)  // fast BLAS works only for Linux
         return pmulblas(v1, v2);
     else   // Java multithreaded based multiplication
       return pmulJavaMulti(  v1, v2);
        
    }
    
    
    // parallel multiplication using Java multithreading
    public static double [][]  pmulJavaMulti( final double [][] v1, final  double [][] v2)  {
      // Java multithreaded based multiplication
        
        final int    rN = v1.length;   final int  rM = v1[0].length;
        int    sN = v2.length;  int   sM = v2[0].length;
        

      
    // transpose first matrix that. This operation is very important in order to exploit cache locality
final double [][]  thatTrans = new double[sM][sN];
for (int  r=0; r < sN; r++)
    for (int c = 0; c  < sM; c++) 
       thatTrans[c][r] = v2[r][c];
    
  final double [][]   vr = new double[rN][sM];   // for computing the return Matrix
  int  nthreads = ConcurrencyUtils.getNumberOfThreads();
  nthreads = Math.min(nthreads, rN);  // larger number of threads than the number of cores of the system deteriorate performance
  
  
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
                    s += v1[b][c] * thatTrans[a][c];
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

   // wait for all the multiplication worker threads to complete
  ConcurrencyUtils.waitForCompletion(futures);
  
  return vr;
	
     }
    
    
    
  // fast multiply using native BLAS and Java multithreading
public final static double [][]  pmulblas(final double [][] v1, final double [][] v2) {
   int   Arows = v1.length;   final int  Acols = v1[0].length;
   final int   Ccols = v2[0].length;
   final double [][]  C = new double [Arows][Ccols];  // the result matrix
   
   int     nthreads = ConcurrencyUtils.getNumberOfThreads();
   nthreads = Math.min(nthreads,  Arows);
   Future []  futures = new Future[nthreads];

   int   rowsPerThread = (int) (Arows / nthreads);   // how many rows the thread processes
  
  int  threadId = 0;  // the current threadId
  while (threadId < nthreads)  {  // for all threads 
    final int  firstRow = threadId * rowsPerThread;
    int lastRowVar = firstRow + rowsPerThread;
    if ( threadId == (nthreads-1)) lastRowVar = Arows;
    final int lastRow = lastRowVar;
    final int   numRowsForThread = lastRow-firstRow;
 
       
    futures[threadId] = GlobalValues.execService.submit(new Runnable() {
     public void run()  {
 
      double []  a = new double[numRowsForThread*Acols];
      double [] b = new double[Acols*Ccols];
      double [] c = new double[numRowsForThread*Ccols];
      
            //  copy the part of the v1 matrix to a linear 1-D array
      int   cnt = 0;
      int   col = 0;
      while (col < Acols) {
       int  row = firstRow;
       while (row < lastRow) {  
             a[cnt] = v1[row][col];  
             row++;  // column major
             cnt++;
             }
           col++;
           }
             
            // copy the v2 matrix to a linear 1-D array
         cnt = 0;
         col = 0;
         while (col < Ccols) {
           int  row = 0;
           while (row < Acols) {
              b[cnt] = v2[row][col];
              row++;   // column major
              cnt++;
              }
        col++;
        }
                      
            //NativeBlas.dgemm('N', 'N', c.rows, c.columns, a.columns, 
              //               alpha, a.data, 0,
		//		a.rows, b.data, 0, b.rows, 
                                //beta, c.data, 0, c.rows);
		
   org.jblas.NativeBlas.dgemm('N', 'N', numRowsForThread,  Ccols,  Acols, 
                              1.0,  a,  0, 
                              numRowsForThread,  b, 0,  Acols,
                              0.0, c, 0, numRowsForThread); 
    	   
   
 //_root_.jeigen.JeigenJna.Jeigen.dense_multiply(numRowsForThread,  Acols , Ccols, a, b, c)
  
  cnt = 0;
  col = 0;
  while (col < Ccols) {
    int row = firstRow;
    while (row < lastRow) {
          C[row][col] = c[cnt];
          row++;
          cnt++;
          }
        col++;
        }  
      
  
        } // run
    }); // Runnable
            
     threadId++;
        
  
 }  // for all threads

  ConcurrencyUtils.waitForCompletion(futures);
  

  return C;
 }

}


