
package DSP.filter;


import java.io.PrintStream;
import java.util.Arrays;

import DSP.filter.iir.Complex;


/*
  Class to implement polynomial functions
  
  This class represents polynomials by their coefficients, stored in a double array, with the
   lowest coefficient (constant) stored at the 0 location.  Consequently the polynomial is of the
  form:
 
   A(x) = a[0] + a[1]*x + a[2]*x^2< + ... + a[N]^N
 
  The class implements basic polynomial arithmetic and other functions useful in designing 
  analog and digital filters.  An example of the latter includes the calculation of reflection 
  coefficients for use in allpass filters.
 */
public class Polynomial {
  
  /** double array containing the coefficients of the polynomial.  The low order coefficient is
   *    in a[0] and the high order coefficient is in a[order].  
   */
  protected double[] a;
  
  /** integer containing the order (degree:  N) of the polynomial. */
  protected int      order;
  
  
  /**
   * Instantiates a new polynomial from a double array containing the coefficients.
   *
   * @param a    double[] containing the polynomial coefficients.
   */
  public Polynomial( double[] a ) {
    order  = a.length - 1;
    this.a = new double[ a.length ];
    System.arraycopy( a, 0, this.a, 0, a.length );
  }
  
  
  /**
   * Instantiates (copies) a new polynomial from an existing Polynomial instance.
   *
   * @param B       Polynomial to be copied into the new Instance.
   */
  public Polynomial( Polynomial B ) {
	  order = B.order;
	  a = new double[ order + 1 ];
	  System.arraycopy( B.a, 0, a, 0, a.length );
  }
  
  
  /**
   * Instantiates a new zero polynomial.
   *
   * @param order     int containing the order (degree) of the polynomial.
   */
  public Polynomial( int order ) {
    this.order = order;
    this.a = new double[ order + 1 ];
    Arrays.fill( a, 0.0 );
  }
  
  
  /**
   * Instantiates a new constant polynomial.
   *
   * @param c        double containing the constant.
   */
  public Polynomial( double c ) {
    order = 0;
    a     = new double[1];
    a[0]  = c;
  }
  
  
  /**
   * Removes leading zero coefficients in a polynomial.
   * 
   * This method is used by the Rational class when new polynomials have been constructed in a rational
   * mapping operation.
   */
  public void trim() {
    
    int i = order;
    int n = 0;
    while ( a[i] == 0.0 ) {
      n++;
      i--;
    }
    
    if ( n > 0 ) {
      double[] b = new double[ order + 1 - n ];
      System.arraycopy(  a, 0, b, 0, a.length - n );
      a = b;
      order -= n;
    }
    
  }
  
  
  
  /**
   * Returns the order (degree) of the polynomal.
   *
   * @return      int containing the degree of the polynomial.
   */
  public int        order() {
    return order;
  }
  
  
  
  /**
   * Returns the polynomial coefficients.
   *
   * @return      double[] containing a copy of the coefficients.
   */
  public double[] coefficients() {
    double[] retval = new double[ order + 1 ];
    System.arraycopy(  a, 0, retval, 0, order + 1 );
    return retval;
  }
  
  
  
  /**
   * Returns a new Polynomial object containing the sum polynomial of this and a constant.
   * 
   * The original Polynomial is unchanged.
   *
   * @param c        double constant to be added to this polynomial.
   * @return         The new polynomial containing the sum of this polynomial and the constant.
   */
  public Polynomial plus( double c ) {
    Polynomial retval = new Polynomial( order );
    System.arraycopy( a, 0, retval.a, 0, a.length );
    retval.a[0] += c;
    return retval;
  }
  
  
  /**
   * Adds a constant to this polynomial.  Alters this to contain the sum.
   *
   * @param c       double constant to be added to this polynomial.
   */
  public void plusEquals( double c ) {
    a[0] += c;
  }
  
  
  /**
   * Returns a new Polynomial object containing the sum of this and the argument polynomial.
   * 
   * The original Polynomial is unchanged.
   *
   * @param B       Polynomial to be added to this polynomial.
   * @return        Polynomial object containing the new sum.
   */
  public Polynomial plus( Polynomial B ) {
    Polynomial retval = new Polynomial( Math.max( order, B.order ) );
    for ( int i = 0;  i <=   order;  i++ ) retval.a[i]  =   a[i];
    for ( int i = 0;  i <= B.order;  i++ ) retval.a[i] += B.a[i];
    return retval;
  }

  
  /**
   * Adds a polynomial to this polynomial.  Alters this to contain the sum.
   *
   * @param B       Polynomial object containing the polynomial to be added to this polynomial.
   */
  public void plusEquals( Polynomial B ) {
    double[] A = new double[ Math.max( order, B.order ) ];
    for ( int i = 0;  i <=   order;  i++ ) A[i]  =   a[i];
    for ( int i = 0;  i <= B.order;  i++ ) A[i] += B.a[i];
    a = A;
    order = A.length - 1;
  }
  
  
  /**
   * Returns a new Polynomial containing the difference between this polynomial and a constant.
   * 
   * This polynomial is unchanged.
   *
   * @param c        double containing the constant to be subtracted from this polynomial.
   * @return         The new Polynomial object containing the difference polynomial.
   */
  public Polynomial minus( double c ) {
	  return plus( -c );
  }
  
  
  /**
   * Subtracts a constant from this polynomial.  Alters this to contain the difference.
   *
   * @param c        double constant to be subtracted from this polynomial.
   */
  public void minusEquals( double c ) {
	  plusEquals( -c );
  }
  
  
  /**
   * Subtracts a polynomial from this polynomial.  Returns the difference as a new Polynomial.
   * 
   * This polynomial is unchanged.
   *
   * @param B            Polynomial to be subtracted from this Polynomial.
   * @return             Polynomial containing the difference.
   */
  public Polynomial minus( Polynomial B ) {
    Polynomial retval = new Polynomial( Math.max( order, B.order ) );
    for ( int i = 0;  i <=   order;  i++ ) retval.a[i]  =   a[i];
    for ( int i = 0;  i <= B.order;  i++ ) retval.a[i] -= B.a[i];
    return retval;
  }

  
  /**
   * Subtracts a polynomial from this polynomial.  Alters this to contain the difference.
   *
   * @param B        Polynomial to be subtracted from this polynomial.
   */
  public void minusEquals( Polynomial B ) {
    double[] A = new double[ Math.max( order, B.order ) ];
    for ( int i = 0;  i <=   order;  i++ ) A[i]  =   a[i];
    for ( int i = 0;  i <= B.order;  i++ ) A[i] -= B.a[i];
    a = A;
    order = A.length - 1;
  }
  
  
  /**
   * Computes the product of a constant and this polynomial.  The product is returned as a new Polynomial.
   * 
   * This polynomial is unchanged.
   *
   * @param c        The double constant factor multiplying this polynomial.
   * @return         The resulting product polynomial.
   */
  public Polynomial times( double c ) {
    Polynomial retval = new Polynomial( order );
    for ( int i = 0;  i <= order;  i++ ) retval.a[i] = c*a[i];
    return retval;
  }
  
  
  /**
   * Multiplies (scales) this polynomial by a constant.  This polynomial is changed to contain the product.
   *
   * @param c        The constant multiplicative factor.
   */
  public void timesEquals( double c ) {
    for ( int i = 0;  i <= order;  i++ ) a[i] *= c;
  }
  
  
  
  /**
   * Computes the product of this polynomial with another polynomial.  The product is returned in a new Polynomial.
   *
   * @param B        Polynomial object containing the multiplicative factor.
   * @return         New Polynomial object containing the product.
   */
  public Polynomial times ( Polynomial B ) {
	  
	  double[] b = B.a;
	  double[] prod = new double[ order + B.order + 1 ];
	  Arrays.fill( prod, 0.0 );
	  
	  for ( int i = 0;  i <= B.order;  i++ ) {
		  for ( int j = 0;  j <= order;  j++ ) {
			  prod[i+j] += b[i]*a[j]; 
		  }
	  }
	  
	  return new Polynomial( prod );
  }
  
  
  
  /**
   * Multiplies this by a Polynomial factor.  Alters this polynomial to contain the product.
   *
   * @param B        Polynomial object containing the multiplicative factor.
   */
  public void timesEquals( Polynomial B ) {
	  
	  double[] b = B.a;
	  double[] prod = new double[ order + B.order + 1 ];
	  Arrays.fill( prod, 0.0 );
	  
	  for ( int i = 0;  i <= B.order;  i++ ) {
		  for ( int j = 0;  j <= order;  j++ ) {
			  prod[i+j] += b[i]*a[j]; 
		  }
	  }
	  
	  a      = prod;
	  order += B.order;
  }
  
  
  
  /**
   * Divides this polynomial by a constant and returns the result of division in a new Polynomial object.
   * 
   * This polynomial is unchanged by this method.
   *
   * @param c        The double divisor.
   * @return         New Polynomial object containing the result of division.
   */
  public Polynomial over( double c ) {
	  double [] tmp = new double[ order + 1 ];
	  for ( int i = 0;  i < order + 1;  i++ )
		  tmp[i] = a[i] / c;
	  
	  return new Polynomial( tmp );
  }
  
  
  
  /**
   * Divides this polynomial by a constant.  This polynomial is altered to contain the result of division.
   *
   * @param c          The double divisor.
   */
  public void overEquals( double c) {
	  for ( int i = 0;  i < order + 1;  i++ ) 
		  a[i] /= c;
  }
  
  
  
  /**
   * Divides this polynomial by another polynomial.  Returns the result of division in a new Rational object.
   * 
   * This polynomial is unchanged by this operation.
   *
   * @param B         The Polynomial divisor.
   * @return          New Rational object containing the result of division.
   */
  public Rational over( Polynomial B ) {
	  return new Rational( this, B );
  }
  
  
  
  /**
   * Computes the derivative of a polynomial.  Returns the derivative as a new Polynomial object.
   *
   * @return          New Polynomial object containing the derivative of this polynomial.
   */
  public Polynomial derivative() {
	  double[] tmp = new double[ order ];
	  for ( int i = 0;  i < order;  i++ ) {
		  tmp[i] = (i+1)*a[i+1];
	  }
	  
	  return new Polynomial( tmp );
  }
  
  
  
  /**
   * Evaluates this polynomial for a real argument.
   *
   * @param x        double containing the argument of the polynomial.
   * @return         double containing the value of the polynomial at this argument.
   */
  public double evaluate( double x ) {
	  
	  double retval = a[order];
	  
	  for ( int i = order-1;  i >= 0;  i-- ) {
		  retval = x*retval + a[i];
	  }
	  
	  return retval;
  }
  
  
  
  /**
   * Evaluates this polynomial for a complex argument.
   *
   * @param c        Complex object containing the argument of the polynomial.
   * @return         Complex object containing the value of the polynomial at this argument.
   */
  public Complex evaluate( Complex c ) {
	  
	  Complex retval = new Complex( a[order] );
	  
	  for ( int i = order-1;  i >= 0;  i-- ) {
		  retval = retval.times(c).plus( a[i] );
	  }
	  
	  return retval;
  }
  
  
  
  /**
   * Evaluates the group delay of an analog filter transfer function specified by this polynomial.
   *
   * @param omega    double specifying the radial frequency (2*pi*f) at which the group delay is evaluated.
   * @return         double containing the resulting group delay in seconds.
   */
  public double groupDelay( double omega ) {
    
    if ( order == 0 ) return 0;
    
    else {
      Complex c = new Complex( 0.0, omega );
      Complex N = derivative().evaluate(c);
      Complex D = evaluate(c);
    
      return -(N.over(D)).real();
    }
    
  }
  
  
  
  /**
   * Evaluates the group delay of a discrete time filter transfer function specified by this polynomial.
   *
   * @param Omega         double specifying the value discrete-time frequency [0 pi] at which the group delay is evaluated.
   * @return              double containing the resulting group delay in samples.
   */
  public double discreteTimeGroupDelay( double Omega ) {
    
    Complex c = Complex.exp( new Complex(0.0, -Omega ) );
    
    Complex N = new Complex( a[order]*order );
    for ( int i = order-1;  i >= 0;  i-- ) {
      N = N.times(c).plus( a[i]*i );
    }
    
    Complex D = evaluate( c );

    return ( N.over(D) ).real();
  }
  
  
  
  /**
   * Computes reflection coefficients for this polynomial.
   *
   * @return         double[] containing the reflection coefficient representation for this polynomial.
   */
  public double[] reflectionCoefficients() {
    
    double[] k = new double[order];
    
    // assure that polynomial is monic
    
    double[] b = new double[ order + 1 ];
    b[0] = 1.0;
    for ( int i = 0;  i < order;  i++ ) b[i+1] = a[i+1]/a[0];
  
    // recursion to calculate reflection coefficients
    
    double[] c = new double[ order ];
    
    for ( int i = order;  i > 0;  i-- ) {
      
      k[i-1] = b[i];
      
      double scale = 1.0 - k[i-1]*k[i-1];
      
      Arrays.fill( c, 0.0 );
      for ( int j = 0;  j < i;  j++ ) {
        c[j] = ( b[j] - k[i-1]*b[i-j] )/scale;
      }
      
      System.arraycopy( c, 0, b, 0, i );
    }
    
    return k;
  }
  
  
  
  /**
   * Prints the coefficients of this polynomial to a PrintStream.
   *
   * @param ps            PrintStream object to which this polynomial's coefficients are printed.
   */
  public void print( PrintStream ps ) {
    for ( int i = 0;  i <= order;  i++ ) {
      if ( i >= 0  &&  i < 10 ) 
        ps.println( i + "    " + a[i] );
      else if ( i >= 10  &&  i <= 100 )
        ps.println( i + "   "  + a[i] );
    }
  }
  
}
