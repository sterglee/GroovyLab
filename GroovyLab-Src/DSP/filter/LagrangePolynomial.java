
package DSP.filter;



/**
 * Implements Lagrange Polynomials.
 * 
 * This class principally supports the design of equiripple FIR digital filters with the 
 * Remez exchange algorithm (package equiripple).
 */
public class LagrangePolynomial {
  
  
  /** Order of the polynomial. */
  private int      order;
  
  /** The ordinates of the points interpolated by this Lagrange polynomial. */
  private double[] x;
  
  /** The abscissas of the points interpolated by this Lagrange polynomial. */
  private double[] y;
  
  /** Barycentric weights for this Lagrange polynomial. */
  private double[] weights;                 // Barycentric weights
  
  /**
   * Instantiates a new Lagrange polynomial given the set of ordinate and matching abscissa values that this
   * polynomial interpolates.
   *
   * @param x    double[] containing the ordinates of the points interpolated by this polynomial.
   * @param y    double[] containing the abscissas of the points interpolated by this polynomial.
   */
  public LagrangePolynomial( double[] x, double[] y ) {
    
    if ( x.length != y.length ) throw new IllegalArgumentException( "Lengths of x and y arrays do not match" );
    this.x = x.clone();
    this.y = y.clone();
    
    order = x.length - 1;
    
    // calculate Barycentric weights
    
    weights = BarycentricWeights( x );
    
  }
  
  
  
  /**
   * Accessor - returns the order of the polynomial.
   *
   * @return the int
   */
  public int    order() { return order; }
  
  
  
  /**
   * Evaluates the Lagrange polynomial at real value xp.
   * 
   * Evaluates the Lagrange polynomial using the barycentric formula.
   *
   * @param xp    double containing the real value for evaluation of the polynomial.
   * @return      double containing the value of the polynomial at xp.
   */
  public double evaluate( double xp ) {
    
    double num   = 0.0;
    double denom = 0.0;
    for ( int j = 0;  j <= order;  j++ ) {
      if ( xp == x[j] ) {
        num   = y[j];
        denom = 1.0;
        break;
      }
      double term = weights[j]/( xp - x[j] );
      num   += term*y[j];
      denom += term;
    }
    
    return num/denom;
  }
  
  
  
  /**
   * Calculates barycentric weights for a collection of abscissa values.
   *
   * @param z     double[] containing the ordinate values for which the barycentric weights are computed.
   * @return      double[] containing the resulting barycentric weights.
   */
  public static double[] BarycentricWeights( double[] z ) {
    
    int n = z.length;
    
    double[] retval = new double[ n ];
    
    for ( int j = 0;  j < n;  j++ ) {
      double w = 1.0;
      for ( int i = 0;  i < n;  i++ ) {
        if ( i != j ) w *= ( z[j] - z[i] );
      }
      retval[j] = 1.0/w;
    }
    
    return retval;
  }
  
  
  
  /**
   * Computes Chebyshev nodes for approximation of a function on interval [a, b].
   * 
   * The Chebyshev nodes are a particularly good set of ordinate values for 
   * approximation of a function on the interval [a, b] by interpolation with
   * a Lagrange polynomial.
   *
   * @param a    double containing the starting point of the interval.
   * @param b    double containing the ending point of the interval.
   * @param n    int specifying the number of Chebyshev nodes desired.
   * @return     double[] containing the Chebyshev nodes.
   */
  public double[] ChebyshevNodes( double a, double b, int n ) {
    
    double t0 = (a+b)/2.0;
    double t1 = (b-1)/2.0;
    double[] retval = new double[n];
    for ( int i = 0;  i < n;  i++ ) 
      retval[i] = t0 + t1*Math.cos( (2*i+1)/(2*n)*Math.PI );
    
    return retval;
  }
  
  
  
  /**
   * The main method.
   *
   * @param args the arguments
   */
  public static void main( String[] args ) {
    
    double[] p = new double[3];
    p[0] = 6.0;
    p[1] = -11.0;
    p[2] = 6.0;
    Polynomial P = new Polynomial( p );
    
    double[] x = new double[3];
    x[0] = 1.0;
    x[1] = 2.0;
    x[2] = 3.0;
    
    double[] f = new double[3];
    f[0] = 1.0;
    f[1] = 8.0;
    f[2] = 27.0;   
    LagrangePolynomial L = new LagrangePolynomial( x, f );
    
    for ( int i = 0;  i < 21;  i++ ) {
      double z = 1.0 + i*0.1;
      System.out.println( P.evaluate(z) + "  " + L.evaluate(z) );
    }
    
  }
  
}
