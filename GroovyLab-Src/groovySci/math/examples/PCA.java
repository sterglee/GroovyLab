package groovySci.math.examples;
 
import groovySci.math.array.*;
import groovySci.math.plot.*;
 
// static import of all array methods : linear algebra and statistics
import   Jama.LinearAlgebra.*;
import static Jama.LinearAlgebra.*;
import static groovySci.math.array.StatisticSample.*;
 
/**
 * Copyright : BSD License
 * @author Yann RICHET
 */
 
public class PCA {
 
    double[][] X; // initial datas : lines = events and columns = variables
 
    double[] meanX, stdevX;
 
    double[][] Z; // X centered reduced
 
    double[][] cov; // Z covariance matrix
 
    double[][] U; // projection matrix
 
    double[] info; // information matrix
 
    public PCA(double[][] _X) {
        X = _X;
 
        stdevX = stddeviation(X);
        meanX = Jama.LinearAlgebra.mean(X);
 
        Z = center_reduce(X);
 
        cov = covariance(Z);
 
        Jama.EigenvalueDecomposition e = eigen(cov);
        U = DoubleArray.transpose(e.getV().getArray());
        info = e.getRealEigenvalues(); // covariance matrix is symetric, so only real eigenvalues...
 }
 
// normalization of x relatively to X mean and standard deviation
public double[][] center_reduce(double[][] x) {
    double[][] y = new double[x.length][x[0].length];
    for (int i = 0; i < y.length; i++)
        for (int j = 0; j < y[i].length; j++)
	y[i][j] = (x[i][j] - meanX[j]) / stdevX[j];
    return y;
}
 
// de-normalization of y relatively to X mean and standard deviation
public double[] inv_center_reduce(double[] y) {
    return inv_center_reduce(new double[][] { y })[0];
}
 
// de-normalization of y relatively to X mean and standard deviation
 public double[][] inv_center_reduce(double[][] y) {
    double[][] x = new double[y.length][y[0].length];
        for (int i = 0; i < x.length; i++)
	for (int j = 0; j < x[i].length; j++)
                    x[i][j] = (y[i][j] * stdevX[j]) + meanX[j];
  return x;
  }
 
 private void view() {
   // Plot
   Plot2DPanel plot = new Plot2DPanel();
 
    // initial Datas plot
   plot.addScatterPlot("datas", X);
 
  // line plot of principal directions
  plot.addLinePlot(Math.rint(info[0] * 100 / groovySci.math.array.StatisticSample.sum(info)) + " %", meanX, inv_center_reduce(U[0]));
  plot.addLinePlot(Math.rint(info[1] * 100 / groovySci.math.array.StatisticSample.sum(info)) + " %", meanX, inv_center_reduce(U[1]));
 
  // display in JFrame
  new FrameView(plot);
  }
 
 private void print() {
  // Command line display of results
 System.out.println("projection vectors\n" + DoubleArray.ToString(groovySci.math.array.DoubleArray.transpose(U)));
 System.out.println("information per projection vector\n" + DoubleArray.ToString(info));
 }
 
 public static void main(String[] args) {
 double[][] xinit = groovySci.math.array.StatisticSample.random(1000, 2, 0, 10);
 
 // artificial initialization of relations
 double[][] x = new double[xinit.length][];
 for (int i = 0; i < x.length; i++)
    x[i] = new double[] { xinit[i][0] + xinit[i][1], xinit[i][1] };
 
 PCA pca = new PCA(x);
 pca.print();
 pca.view();
 }
 
}
