package groovySci.math.array;

// conversion routines for NUMAL 1-indexed arrays


public class NUMALConvArrays {


// convert a zero indexed array to a one-indexed one 
  static double [][]  AA1( double [][] a)  {
     int   N = a.length;
     int   M = a[0].length;
     double [][]  a1 = new double [N+1][M+1];
     for (int r=0; r < N; r++)
       for (int c=0;  c < M; c++)
         a1[r+1][c+1] = a[r][c];
     
   return a1;
   }
   
  // convert a one indexed array to a zero-indexed one 
  static double [][]  AA0( double [][] a)  {
     int   N = a.length;
     int   M = a[0].length;
     double [][]  a0 = new double [N-1][M-1];
     for (int r=1; r < N; r++)
       for (int c=1;  c < M; c++)
         a0[r-1][c-1] = a[r][c];
     
   return a0;
   }
   
  
    static double [] A1( double []  a)   {
     int   N = a.length;
     double []   a1 = new double [N+1];
     for (int k=0; k<N; k++)
         a1[k+1] = a[k];
     
    return a1;
   }

    
  static double [] A0( double []  a)   {
     int   N = a.length;
     double []   a0 = new double [N-1];
     for (int k=1; k<N; k++)
         a0[k-1] = a[k];
     
    return a0;
   }
  
}
