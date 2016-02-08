
package groovySci.asynch;

import gExec.Interpreter.GlobalValues;
import groovySci.math.array.EigResults;
import groovySci.math.array.Matrix;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import static groovySci.math.array.ParallelMult.pmul;

// this class supports asynchronous  Matrix multiplication computation 
public class asynchMul  implements  Callable<double [][] > {


    double [] [] A, B;   // the multiplied data arrays 
    
    asynchMul(double [][] pA, double [][] pB ) {
        A = pA; B = pB;
    }
 
         
    @Override
    public  double [][]   call() throws Exception {
       
       return  pmul(A, B);
        
    }
    
    public static Future<double [][] > asynchmul( double [][] A, double [][] B) {
        asynchMul  omul = new asynchMul(A, B);  // the multiplication object
      
        // submit the future computation
        Future <double [][]> mulresults = GlobalValues.execService.submit(omul);        
        return mulresults;
        
    }

    public static Future<double [][] > asynchmul( Matrix  A, double [][] B) {
        return asynchmul(A.getArray(), B);
    }

    public static Future<double [][] > asynchmul( double [][] A, Matrix  B) {
        return asynchmul(A, B.getArray());
    }

    public static Future<double [][] > asynchmul( Matrix  A, Matrix  B) {
        return asynchmul(A.getArray(), B.getArray());
    }

    
}
