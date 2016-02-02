
package groovySci.asynch;

import gExec.Interpreter.GlobalValues;
import groovySci.math.array.EigResults;
import groovySci.math.array.Matrix;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;


// this class supports asynchronous  Matrix addition computation 
public class asynchPlus  implements  Callable<double [][] > {


    double [] [] A, B;   // the multiplied data arrays 
    
    asynchPlus(double [][] pA, double [][] pB ) {
        A = pA; B = pB;
    }
 
         
    @Override
    public  double [][]   call() throws Exception {
       
       return  groovySci.math.array.Matrix.plus(A, B);
        
    }
    
    public static Future<double [][] > asynchplus( double [][] A, double [][] B) {
        asynchPlus  oplus = new asynchPlus(A, B);  // the multiplication object
      
        // submit the future computation
        Future <double [][]>plusresults = GlobalValues.execService.submit(oplus);        
        return plusresults;
        
    }

    public static Future<double [][] > asynchplus( Matrix  A, double [][] B) {
        return asynchplus(A.getArray(), B);
    }

    public static Future<double [][] > asynchplus( double [][] A, Matrix  B) {
        return asynchplus(A, B.getArray());
    }

    public static Future<double [][] > asynchplus( Matrix  A, Matrix  B) {
        return asynchplus(A.getArray(), B.getArray());
    }

    
}
