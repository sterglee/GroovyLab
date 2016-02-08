
package DSP.filter;

import java.io.PrintStream;

import DSP.filter.iir.Complex;


/*`
  Class implementing rational functions.  Used to design and represent analog and digital filters.
  The rational function is of the form H(s) = N(s)/D(s), where N and D are polynomials.
 */
public class Rational {
	
	/** Numerator polynomial. */
	private Polynomial N;
	
	/** Denominator polynomial. */
	private Polynomial D;
	
	
	
	/**
	 * Instantiates a new rational function, given arrays specifying the numerator and denominator polynomials.
	 *
	 * @param num      double[] specifying coefficients of the numerator polynomial.
	 * @param denom    double[] specifying coefficients of the denominator polynomial.
	 */
	public Rational( double[] num, double[] denom ) {
		N = new Polynomial( num );
		D = new Polynomial( denom );
	}
	
	
	
	/**
	 * Instantiates a new rational function, given Polynomial objects for the numerator and denominator.
	 *
	 * @param N        Polynomial instance specifying the numerator polynomial.
	 * @param D        Polynomial instance specifying the denominator polynomial.
	 */
	public Rational( Polynomial N, Polynomial D ) {
		this.N = new Polynomial( N );
		this.D = new Polynomial( D );
	}
	
	
	
	/**
	 * Constructs a new rational function by copying an existing rational function object.
	 *
	 * @param R       Rational function object to be copied.
	 */
	public Rational( Rational R ) {
		this.N = new Polynomial( R.N );
		this.D = new Polynomial( R.D );
	}
	
	
	
	/**
	 * Instantiates a new rational function object from a constant.
	 *
	 * @param c      double specifying the constant of the numerator.
	 */
	public Rational( double c ) {
	  N = new Polynomial(c);
	  D = new Polynomial(1.0);
	}
	
	
	
	/**
	 * Returns the orders of the numerator and denominator polynomials.
	 *
	 * @return  int[] - first element is the order of the numerator and second element is the order of the denominator.
	 */
	public int[] order() {
	  int[] retval = { N.order(), D.order() };
	  return retval;
	}
	
	
	
	/**
	 * Returns a copy of the numerator polynomial.
	 *
	 * @return    Polynomial copy of the numerator.
	 */
	public Polynomial numerator() {
		return new Polynomial( N );
	}
	
	
	
	/**
	 * Returns a copy of the denominator polynomial.
	 *
	 * @return    Polynomial copy of the denominator.
	 */
	public Polynomial denominator() {
		return new Polynomial( D );
	}
	
	
	
	/**
	 * Puts the rational function representation in canonical form.
	 * 
	 * Normalizes the numerator and denominator polynomials to have unit lead coefficients.  Returns the
	 * gain factor required to perform the normalization.
	 *
	 * @return   double specifying the gain
	 */
	public double canonicalForm() {
		
		double scaleN = N.a[ N.order ];
		for ( int i = 0;  i < N.a.length;  i++ ) N.a[i] /= scaleN;
		double scaleD = D.a[ D.order ];
		for ( int i = 0;  i < D.a.length;  i++ ) D.a[i] /= scaleD;
		
		return scaleN/scaleD;
	}
	
	
	
	/**
	 * Scales (in-place) a rational function by a constant.
	 *
	 *
	 * @param A    double specifying the scale factor.
	 */
	public void timesEquals( double A ) {
		N.timesEquals(A);
	}
	
	
	
	/**
	 * Multiplies (in-place) a rational function by a polynomial.
	 *
	 * @param P    Polynomial object specifying the multiplicative polynomial factor.
	 */
	public void timesEquals( Polynomial P ) {
	  N.timesEquals( P );
	}
	
	
	
	/**
	 * Multiplies(in-place) a rational function by another rational function.
	 *
	 * @param R    Rational object specifying the multiplicative rational factor.
	 */
	public void timesEquals( Rational R ) {
	  N.timesEquals( R.N );
	  D.timesEquals( R.D );
	}
	
	
	
	/**
	 * Evaluates the rational function for a real argument.
	 *
	 * @param x     double specifying the argument to the rational function.
	 * @return      double specifying the resulting value of the rational function.
	 */
	public double evaluate( double x ) {
		double retval = 0.0;
		double num   = N.evaluate( x );
		double denom = D.evaluate( x );
		if ( denom != 0.0 ) retval = num/denom;
		
		return retval;
	}
	
	
	
	/**
	 * Evaluates a rational function for a complex argument.
	 *
	 * @param c    Complex object specifying the complex argument.
	 * @return     Complex object specifying the resulting complex value of the rational function.
	 */
	public Complex evaluate( Complex c ) {
		Complex retval = new Complex( 0.0, 0.0 );
		Complex num = N.evaluate( c );
		Complex denom = D.evaluate( c );
		if ( denom.abs() != 0.0 ) retval = num.over( denom );
		
		return retval;
	}
	
	
	
	/**
	 * Maps a rational function to a new rational function by substitution of a rational function for the argument.
	 * 
	 * Uses a modification of Horner's scheme to perform the mapping.
	 *
	 * @param S     Rational object specifying the mapping function.
	 * @return      Rational object specifying the resulting mapped rational function.
	 */
	public Rational map( Rational S ) {
	  
	  //  Modified Horner's scheme evaluation
	  
	  //    Numerator
	  
	  Polynomial P = new Polynomial( N.a[ N.order ] );
	  Polynomial T = new Polynomial( 1.0 );
	  for ( int i = N.order-1;  i >= 0;  i-- ) {
	    T = T.times( S.D );
	    P = P.times( S.N ).plus( T.times( N.a[i] ) );
	  }
	  
	  //    Denominator
	  
	  Polynomial Q = new Polynomial( D.a[ D.order ] );
	  T = new Polynomial( 1.0 );
      for ( int i = D.order-1;  i >= 0;  i-- ) {
        T = T.times( S.D );
        Q = Q.times( S.N ).plus( T.times( D.a[i] ) );
      }
      
      if ( D.order > N.order ) {
        for ( int i = 0;  i < D.order-N.order;  i++ )
          P = P.times( S.D );
      }
      else if ( N.order > D.order ) {
        for ( int i = 0;  i < N.order-D.order;  i++ ) 
          Q = Q.times( S.D );
      }
      
      P.trim();
      Q.trim();
      
      return new Rational( P, Q );
	}
	
	
	
	/**
	 * Calculates the residue of a real pole of the rational function.
	 * 
	 * Uses L'Hopital's rule to calculate the residue of a real pole.  Potentially useful to find parallel implementations
	 * of IIR digital filters.  Suitable only for simple poles.
	 *
	 * @param pole     double specifying the real pole (root of the denominator).
	 * @return         double specifying the residue.
	 */
	public double residue( double pole ) {
		
		// using L'Hopital's rule - assumes single pole
		
		return N.evaluate( pole ) / D.derivative().evaluate( pole );
	}
	
	
	
	/**
	 * Calculates the residue of a complex pole of the rational function.
   * 
   * Uses L'Hopital's rule to calculate the residue of a complex pole.  Potentially useful to find parallel implementations
   * of IIR digital filters.  Suitable only for simple poles.
	 *
	 * @param pole     Complex object specifying the pole.
	 * @return         Complex object specifying the residue.
	 */
	public Complex residue( Complex pole ) {
		
		// using L'Hopital's rule - assumes single pole
		
		return N.evaluate( pole ).over( D.derivative().evaluate( pole ) );
	}
	
	
	
	/**
	 * Calculates the group delay of an analog filter with transfer function specified by this rational function.
	 *
	 * @param omega      double specifying the radial frequency (2*pi*f) at which the group delay is evaluated.
	 * @return           double specifying the group delay in seconds.
	 */
	public double groupDelay( double omega ) {
	  return N.groupDelay(omega) - D.groupDelay(omega);
	}
	
	
	
	/**
	 * Calculates the group delay of a digital filter with transfer function specified by this rational function.
	 * 
	 * For this evaluation, the numerator and denominator are assumed to be polynomials in z^-1.
	 *
	 * @param Omega    double specifying the digital frequency [0 pi] at which the group delay is evaluated.
	 * @return         double specifying the group delay in samples.
	 */
	public double discreteTimeGroupDelay( double Omega ) {
	  return N.discreteTimeGroupDelay(Omega) - D.discreteTimeGroupDelay(Omega);
	}
	
	
	
	/**
	 * Prints the coefficients of the rational function.
	 *
	 * @param ps      Printstream to which the rational function coefficients are printed.
	 */
	public void print( PrintStream ps ) {
		ps.println( "Numerator: " );
		N.print( ps );
		ps.println( "Denominator: " );
		D.print( ps );
	}
	
	
	
	public static void main( String[] args ) {
	  double[] a = new double[4];
	  a[0] = 1.0;
	  a[1] = 2.0;
	  a[2] = 2.0;
	  a[3] = 1.0;
	  double[] b = new double[1];
	  b[0] = 1.0;
	  Rational R = new Rational( b, a );
	  
	  for ( int i = 0;  i < 100;  i++ ) {
	    double omega = i/25.0;
	    System.out.println( omega + "  " + R.evaluate( new Complex( 0.0, omega ) ).abs() + "   " + R.groupDelay(omega) );
	  }
	}


}
