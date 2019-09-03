package CUDASig;


import gExec.Interpreter.GlobalValues;
import java.io.File;

// Java interface to CUDA kernel operations  for signal processing
public class CUDASig
{
    static
    {
        if (GlobalValues.hostIsWin64  || GlobalValues.hostIsWin64)  {    // support for CUDA only for Win64 and Linu64
	String libName = "CUDASig.so";
	if (GlobalValues.hostIsWin)  {   // Windows OS
	   libName = "CUDASig.dll";
	   
	try
	{
	   System.out.println("Trying to load CUDASig library [" + libName + "] ...");
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
    }
       public  native void  cudadwt(float[ ] data,  int size, float [] output);
       

}

