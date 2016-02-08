package groovySci.math.array;

import groovy.lang.GroovyObjectSupport;

import cern.colt.matrix.*;
import cern.colt.matrix.tdouble.*;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;


// a matrix class based on Parallel Colt
public class PMatrix  extends GroovyObjectSupport  {
  public  DenseDoubleMatrix2D m;
  
  public PMatrix(double [][]da) {
      m = new DenseDoubleMatrix2D(da);
  }
  
  
  public PMatrix(DenseDoubleMatrix2D dm) {
      m = dm;
  }
  
  
  public PMatrix(cern.colt.matrix.tdouble.DoubleMatrix2D  dm) {
      m = (DenseDoubleMatrix2D)dm;
  }
  
  public PMatrix multiply(PMatrix A) {
      return  new PMatrix(m.zMult(m, A.m));
  }
  
  @Override
  public String  toString() {
      return DoubleArray.toString(m.toArray());
  }
  
}
