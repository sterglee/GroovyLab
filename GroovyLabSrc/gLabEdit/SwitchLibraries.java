package gLabEdit;

public class SwitchLibraries {

    
    public static void switchJBLAS() {
        gExec.Interpreter.GlobalValues.GroovyShell.evaluate(
                // reimplement Matrix-Matrix  multiplication using JBLAS
"groovySci.math.array.Matrix.metaClass.multiply = { \n"+
   "groovySci.math.array.Matrix m ->   // the input Matrix \n"+

 // transform the input matrix to the JBLAS representation
     "dm =  new org.jblas.DoubleMatrix(m.toDoubleArray()) \n"+
 // transform the receiver to the JBLAS representation
     "dmthis = new org.jblas.DoubleMatrix(delegate.toDoubleArray()) \n"+
 // fast multiply using JBLAS Native BLAS
     "mulRes = dmthis.mmul(dm) \n"+

 // return back result as a double [][] array
    "groovySci.math.array.JBLASUtils.JBLASDoubleMatrixToDouble2D(mulRes) \n }"+
                "\nprintln(\"multiplying with JBLAS\") \n");
  }

                
    
}
