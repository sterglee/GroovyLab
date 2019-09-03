
package CUDAOps;

import java.io.File;
import gExec.Interpreter.GlobalValues;

// Java interface to CUDA kernel operations  
public class KernelOps
{
    static
    {
	if (GlobalValues.hostIsWin64  || GlobalValues.hostIsLinux64)  {  // Windows and Linux only can have CUDA support
	String libName = "KernelOps.so";
	if (File.pathSeparatorChar==';')   // Windows OS
	   libName = "KernelOps.dll";
	   
	try
	{
	   System.out.println("Trying to load CUDA library [" + libName + "] ...");
	   File path = new File("");
	   System.out.println("Current Path = " + path.getAbsolutePath());
	   String libPath = path.getAbsolutePath() + File.separator + "lib"+File.separator + libName;

	   System.load(libPath);
	   System.out.println("Library loaded");
	}
	catch (Exception e)
	{
	   System.out.println("Error: " + e);
	 }
        }
    }

    // matrix addition
    public native void cma(float[] a, float[] b, float[] c);	

    // matrix subtraction
    public native void cms(float[] a, float[] b, float[] c);	

    // matrix multiplication with a float
    public native void cmscalar(float[] a, float value, float[] c);	

	// matrix multiplication
    public native void cmm(float[] a, float [] b, float [] c, int m, int n, int k);
		
	
    // matrix addition
    public native void cmad(double[] a, double[] b, double[] c);	

    // matrix subtraction
    public native void cmsd(double[] a, double[] b, double[] c);	

    // matrix multiplication with a float
    public native void cmscalard(double[] a, double value, double[] c);	

	// matrix multiplication
     public native void cmmd(double[] a, double [] b, double [] c, int m, int n, int k);

	 // CUBLAS based matrix multiplication
     public native  int  sgemm(float [] h_A, int hA, int wA, float [] h_B, int wB, float [] h_C); 
	 
  public native  int  dgemm(double [] h_A, int hA, int wA, double [] h_B, int wB, double [] h_C);
  
  public native String   getCUDADeviceInfo( );

  public native void cudafft( float [] inData, int N, float [] realImsFFT); 

 public static void main(String[] args)
    {
	  int SIZE=10;
        System.out.println("Hello CUDA through JNI!");
	// make an instance of our class to access the native method
        KernelOps m = new KernelOps();
	// declare three arras
        float[] a = new float[SIZE];
        float[] b = new float[SIZE];
        float[] c = new float[SIZE];
	// initialize two arrays
        for (int i = 0; i < a.length; i++)
            a[i] = b[i] = i;
        System.out.println("J: Arrays initialized, calling C.");
	// call the native method, which in turn will execute kernel code on the device
        m.cma(a, b, c);
        
	// print the results
        for (int i = 0; i < SIZE; i++)
            System.out.print(c[i] + "| ");
        System.out.println();
    }
}
