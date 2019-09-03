package NROps;


import gExec.Interpreter.GlobalValues;
import java.io.File;

// Java interface to CUDA kernel operations  
public class NROps
{
    static
    {
        String libName = "libNROps.so";  // assume  Linux
        for (;;) {   // set the proper library name for each OS
              if (GlobalValues.hostIsWin64) {   // Windows OS
	   libName = "NROps.dll";
                     break;
                 }
              if (GlobalValues.hostIsFreeBSD)
                      libName = "libfreebsdNROps.so";
              else if (GlobalValues.hostIsMac)
                      libName ="NROps.dylib";

               break;   
           }
        
    
	
          
	try
	{
	   System.out.println("Trying to load NR library [" + libName + "] ...");
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
 public native void cnrfft(double[] data,  int size, int isign);
       
 public native void nfourn(final double[] data, final int[] nn, final int dim, final int isign); 

public  native void mul(double  [] a, int n, int m,  double  [] b, int k, double [] c);

}


