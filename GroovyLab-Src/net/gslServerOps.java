package net;

// operations using a remote GSL server

import groovySci.math.array.Matrix;
import java.io.IOException;


public class gslServerOps {
    
    final static double eigOperationCode = 1.0;
    
  public static double [][] gslServerEig(Matrix  A) throws IOException {
     return gslServerEig(A);
     }
    
  public static double [][] gslServerEig(double [][] A) throws IOException {
        
gExec.Interpreter.GlobalValues.writer.writeDouble(eigOperationCode);

// write #rows and #cols of the matrix
int Nrows = A.length;
double [][] evals = new double[2][Nrows];

gExec.Interpreter.GlobalValues.writer.writeDouble(Nrows*1.0);

for (int r=0; r < Nrows; r++) 
   for (int c=0; c < Nrows; c++) 
   gExec.Interpreter.GlobalValues.writer.writeDouble(A[r][c]);

  
for (int r=0; r < 2; r++)
 for (int c=0; c < Nrows; c++)
  evals[r][c] =  gExec.Interpreter.GlobalValues.reader.readDouble();

  return evals;
        
    }
    
}
