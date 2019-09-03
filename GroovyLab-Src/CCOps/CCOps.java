package CCOps;


import java.io.File;
import gExec.Interpreter.GlobalValues;


// this class intefaces to CCMath C library ( http://freecode.com/projects/ccmath )
// that has efficient C implementations of some basic algorithms
public class CCOps
{
    static
    {
        if (GlobalValues.hostIsWin64 || GlobalValues.hostIsLinux64 || GlobalValues.hostIsFreeBSD || GlobalValues.hostIsMac )  {
        String WinthreadsLib = "pthreadVC2.dll";   // this library is installed at the ./lib ScalaLab folder
   
        String libName = "libCCOps.so";  // assume  Linux
        for (;;) {   // set the proper library name for each OS
              if (GlobalValues.hostIsWin64) {   // Windows OS
	   libName = "CCOps.dll";
                     break;
                 }
              if (GlobalValues.hostIsFreeBSD)
                      libName = "libfreebsdCCOps.so";
              else if (GlobalValues.hostIsMac)
                      libName ="CCOps.dylib";

               break;   
           }
        
	try
	{
	   System.out.println("Trying to load CC library [" + libName + "] ...");
	   File path = new File("");
	   
	  if (File.pathSeparatorChar==';') {  // Windows OS : Windows by default does not support PThreads, therefore load it 
                      String PthreadsLibPath = path.getAbsolutePath() + File.separator + "lib"+File.separator + WinthreadsLib;
                      System.load(PthreadsLibPath);
          }
	                        
                     String libPath = path.getAbsolutePath() + File.separator + "lib"+File.separator + libName;
                    System.out.println("libPath =   " + libPath);
                     System.load(libPath);
	System.out.println("Native Library "+libName+" loaded. It supplies CCMath Operations.");
	}
	catch (Exception e)
	{
	   System.out.println("Error loading native library : "+libName +"Exception: "+ e);
	 }
        }
    }
	// multiplies two column indexed matrixes, a(n1 X n2), b (n2 X n3) returning result in c
 	// multithreading multiplication is implemented using the Pthreads library
                  public native void pt(double [] a, int n1, int n2, double [] b, int n3, double [] c);
	  
	// Linear Systems solution routines
	   public native int solv( double [] a, double [] b, int n);
	   
	   public native int solvps( double [] a, double [] b, int n);
	   
	   public native int solvtd( double [] a,  double [] b,  double []c, double [] x, int m);  
	   
	   public native int solvru( double [] a, double [] b, int n);
	   
	   
	   // SVD routines
       public  native int svduv(double[] d, double []a,  double []u, int m, double [] v, int n);
       
       public native int sv2u1v(double[] d, double [] a, int m, double [] v, int n);
       
       public native int sv2uv(double [] d, double [] a, double []u,  int m, double [] v, int n );
       
       public native int sv2val(double []d, double []a, int m, int n);
       
       public native int svdval(double []d, double []a, int m, int n);
       
       
// matrix inversion routines       
       public native int minv(double [] a, int n);
        
       public native int psinv(double [] v, int n);
	   
        public native int ruinv( double  [] a, int n);
	   
	   // Eigenanalysis routines
        public native  int eigval(double [] a, double [] ev, int n);
	   
        public native  int eigen(double [] a, double [] ev, int n);
	   
        public native  int evmax(double [] a, double [] u, int n);
	   
	   //matrix generation routines
        public native  int smgen( double [] a, double [] eval, double [] evec, int n);
	   
        public native  int ortho( double [] e, int n);
	   

        public native int ccfft( double [] iv, double outRe [], double outIm [], int N); 
	   
	   
/*
     Compute the power spectrum of a series.
     int pwspec(double *x,int n,int m)
       x = pointer to array containing input/output series
           (converted to a power spectra at exit)
       n = number of points in the series
       m = control flag specifying order of smoothing, with:
             m=0 -> no smoothing
             m>0 -> smooth output power spectra, using
                     an order m average (see smoo)
      return value: n = size of series used to compute power spectra
                        (n <= input n, even values required)
          The output power spectra is defined by
           ps(w[j]) = | ft[j] |^2 / <x^2>  ,  where
           <x^2> = { Sum(j=0 to n-1) x[j]^2 }/n .
          This normalization yields  Sum(j=0 to n-1) ps(w[j]) = 1  .
*/
        public native int pwspec(double [] x, int n, int m);	   
	   
	   
       
/*
 Linear Least Squares:
------------------------------------------------------------------------------
qrlsq
     Compute a linear least squares solution for A*x = b
     using a QR reduction of the matrix A.
     double qrlsq(double *a,double *b,int m,int n,int *f)
       a = pointer to m by n design matrix array A
           This is altered to upper right triangular
           form by the computation.
       b = pointer to array of measurement values b
           The first n components of b are overloaded
           by the solution vector x.
       m = number of measurements (dim(b)=m)
       n = number of least squares parameters (dim(x)=n)
           (dim(a)=m*n)
       f = pointer to store of status flag, with
              *f = 0  -> solution valid
              *f = -1 -> rank of A < n (no solution)
      return value: ssq = sum of squared fit residuals
     The QR algorithm employs an orthogonal transformation to reduce
     the matrix A to upper right triangular form, with

          A = Q*R  and  R = Q~*A .

     The matrix R has non-zero components confined to the range
     0 <= i <= j < n.  The system vector b is transformed to

          b' = U~*b  and  Sum(k=j to n-1) R[j,k]*x[k] = b'*[j]

     is solved for x, using 'solvru' (see Chapter 1). The sum
     of squared residuals is

          ssq = Sum(i=n to m-1){b~[i]^2} .

*/
       
public    native double qrlsq( double [] a, double [] b, int m, int n, int [] flag);

/*lsqsv
     Generate a SVD based solution for the linear least
     squares system  A*x = b.

     double lsqsv(double *x,int *pr,double *var,double *d,double *b, \
                  double *v,int m,int n,double th)
       x = pointer to array to be loaded with least squares
           parameters x
       pr = pointer to store for rank of system solution r
            (r<=n, and if r<n the parameter variance matrix
             is singular.)
       var = pointer to array for output of parameter variance
             matrix. (This output is suppressed if the input
             var = NULL.)
       d = pointer to input array of system singular values
       b = pointer to input array of rotated system vector
       v = pointer to input array of orthogonal transformation
           matrix V
           (Arrays d,b,v are the output of a singular value
            decomposition by svdlsq or sv2lsq. They are not
            altered by this function.)
       m = number of measurements (dim(b)=m)
       n = number of parameters
           (dim(x)=dim(d)=n,dim(v)=dim(var)=n*n)
       th = singular value threshold parameter
            Components corresponding to singular values
            d[i]<th are not used in the solution.
      return value: ssq = the sum of squared fit residuals


     This function operates on the output of 'svdlsq' or 'sv2lsq'.
     If a rank deficiency is detected (*pr < n), the returned ssq
     is given by

          ssq = Sum(i=n to m-1){b'[i]^2} + Sum([k]){b'[k]^2}

     where [k] denotes the indices of the zero singular values,and
     b'= U~*b.

     The parameter variance matrix (returned when requested) is

          V = V*K*V~ , with non-zero diagonal elements
          K[i,i] = (ssq/(m-r))/(d[i]^2).

     This variance matrix is singular when the rank r < n.
*/

  //public static native double lsqsv(double []x,int []pr,double *var,double *d,double *b, 
      //            double *v,int m,int n,double th)
   


          
  public native void  ccautcor(double [] x, int N, double [] res,  int mlag);
  
}


