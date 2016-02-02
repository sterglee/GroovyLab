package groovySci.math.array;

public class SvdResults {
  public double [][] U;  // nrows X ncols matrix, i.e. same dims as the data array
  public double []W;  // ncols X 1 matrix
  public double [][] V;  // ncols X ncols matrix
  public double conditionNumber;
  public double norm;
  
  @Override
  public String toString() {
      StringBuilder sb = new StringBuilder("\nU = \n"+Matrix.printArray(U));
      sb.append("\nW = "+Matrix.printArray(W));
      sb.append("\nV = "+ Matrix.printArray(V));
      return sb.toString();
  }
}
