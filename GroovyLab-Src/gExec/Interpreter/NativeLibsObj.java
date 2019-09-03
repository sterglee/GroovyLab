package gExec.Interpreter; 

import CCOps.CCOps;
import NROps.NROps;

// keeps the objects implemented with native libraries
public class NativeLibsObj {

    static public NROps  nrObj = new NROps();   // Numerical recipes based routines
    static public CCOps  ccObj = new CCOps();    // CCMath based routines
    
    static public CUDAOps.KernelOps  cudaObj = null; 
    static public CUDASig.CUDASig  cudaSigObj = null;

}