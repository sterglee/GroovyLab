package groovySci.math.array;

public class JBLASUtils {
  public static Matrix JBLASDoubleMatrixToDouble2D(org.jblas.DoubleMatrix dm)     {
      int nr = dm.rows;
      int nc = dm.columns;
      Matrix rm = new Matrix(nr, nc);
      for (int r=0; r<nr; r++)
          for (int c=0; c<nc; c++)
              rm.d[r][c] = dm.get(r, c);
      
      return rm;
  
  }
  
  public static Matrix JBLASDoubleMatrixToMatrix(org.jblas.DoubleMatrix dm)     {
      return JBLASDoubleMatrixToDouble2D(dm);
      
  }
}


/*
 
groovySci.math.array.Matrix.metaClass.multiply = { 
   groovySci.math.array.Matrix m ->   // the input Matrix

     dm =  new org.jblas.DoubleMatrix(m.toDoubleArray())
     dmthis = new org.jblas.DoubleMatrix(delegate.toDoubleArray())
     mulRes = dmthis.mmul(dm)

    groovySci.math.array.JBLASUtils.JBLASDoubleMatrixToDouble2D(mulRes)
}


x = rand(2000)
x.toDoubleArray()
x.length()

tic()
y = x*x
tm=toc()

xx=Rand(2000)
tic()
yy=xx*xx
tmJ = toc()
 */