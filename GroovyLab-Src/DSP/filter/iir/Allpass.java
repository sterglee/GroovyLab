

package  DSP.filter.iir;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.Arrays;

import  DSP.filter.Polynomial;
import  DSP.filter.Rational;



/*
  Allpass filter class.
 
 Implements a digital allpass filter, using a lattice structure implementation.  Allpass 
 filters have unit gain by construction, and are used to alter the phase of signals with
 altering their magnitude responses.
 
 An allpass filter has the form:  z_{-N}A_{N}(z_{-1}) / A_{N}(z), where N is the order of the 
  filter and A_{N}(z) is a polynomial.  The numerator polynomial is a "reflection" of the
  denominator polynomial.  If
 
 A_N(z)  =  a_0 + a_1*z_-1> + a_2*z_-2 + ... + a_N*z_{-N}
 
 then the numerator has the following form:
 
 z_{-N} A_N(z_{-1})  =  a_N + a_{N-1}*z_{-1}) + ... + a_0*z_{-N}
 
 The allpass filter is represented internally as a set of reflection coefficients.  These
 are numbers k in the open interval (-1, 1) and define A_N<(z) by the recursion:
 
 A_i(z) = A_{i-1}(z) + z_{-i}*k(i)*A_{i-1}(z_{-1})     i = 1, ..., N 
 
 A_{0}(z) = 1 
 
  */
public class Allpass {
  
  /** double[] containing the reflection coefficients specifying this allpass filter. */
  protected double[] k;
  
  /** int containing the order of the filter. */
  protected int      order;
  
  /** double[] containing the state of the filter.  Used to assure continuity in the filtering operation
   *  when making repeated calls to the filter methods. */
  protected double[] state;
  
  /** A Rational object containing the rational response representation of the filter. */
  protected Rational T;
  
  
  
  /**
   * Instantiates a new allpass filter of a given order with zero reflection coefficients.
   *
   * @param order     int containing the order of the filter.
   */
  public Allpass( int order ) {
    this.order = order;
    k     = new double[ order ];
    state = new double[ order + 1 ];
    
    constructRationalRepresentation();
  }
  
  
  
  /**
   * Instantiates a new allpass filter object from a Polynomial representing the allpass denominator.
   *
   * @param A     Polynomial object containing the polynomial coefficient representation for the allpass filter.
   */
  public Allpass( Polynomial A ) {
    k     = A.reflectionCoefficients();
    order = k.length;
    state = new double[ order + 1 ];
    
    constructRationalRepresentation();
  }
  
  
  
  /**
   * Instantiates a new allpass filter object from a double[] containing the reflection coefficients.
   *
   * @param k    double[] containing the reflection coefficients.
   */
  public Allpass( double[] k ) {
    this.k = k.clone();
    order  = this.k.length;
    state  = new double[ order + 1 ];
    
    constructRationalRepresentation();
  }
  
  
  
  /**
   * Initializes the states of the filter to zero.
   */
  public void initialize() {
    Arrays.fill( state, 0.0 );
  }
  
  
  
  /**
   * Filters a single sample of a sequence.
   * 
   * Because the filter maintains state between calls to filter(x), a sequence (signal) can be filtered
   * by repeated calls to this method using successive continguous samples of the sequence as the argument.
   *
   * @param x      double containing the single sample of a sequence.
   * @return       double containing the corresponding single output sample of the filter.
   */
  public double filter( double x ) {
    
    int stage = order;
    while ( stage >= 0 ) {
      
      if ( stage > 0 ) {
        x           -= k[stage-1]*state[stage-1];
        state[stage] = k[stage-1]*x + state[stage-1];
      }
      else {
        state[stage] = x;
      }
      
      stage--;
    }
    
    return ( (double) state[order] );
  }
  
  
  
  /**
   * Filters a sequence or a segment of a sequence contained in a double array.
   * 
   * Because the filter maintains state between successive calls to this method, it is possible
   * to filter a continuous stream in finite consecutive, contiguous blocks with this method with
   * no startup transients between successive calls.  This feature permits filtering in real time
   * as blocks of data become available, or filtering very long sequences incrementally from large
   * files.
   *
   * @param x      double[] containing the sequence or segment of a sequence upon call.  Contains
   *               the filtered array following the call.
   */
  public void filter( double[] x ) {
    
    for ( int i = 0;  i < x.length;  i++ )
      x[i] = filter( x[i] );
    
  }
  
  
  
  /**
   * Evaluate the filters response at digital frequency omega, element of [0, pi].
   * 
   * Calculates A_N( e^(-j*omega) )
   *
   * @param omega     double specifying the discrete frequency for evaluation.
   * @return          Complex object containing the value of the filter transfer function at omega.
   */
  public Complex evaluate( double omega ) {
    Complex ejOmega = Complex.exp( new Complex(0.0, -omega ) );
    return T.evaluate( ejOmega );
  }
  
  
  
  /**
   * Evaluates the group delay of the allpass filter at digital frequency Omega, element of [0, pi].
   *
   * @param Omega     double containing the discrete frequency for evaluation of the group delay.
   * @return          double containing the group delay in samples.
   */
  public double groupDelay( double Omega ) {
    return T.discreteTimeGroupDelay( Omega );
  }
  
  
  
  /**
   * Constructs a Rational object with the transfer function representation of this allpass filter.
   *
   * @return       Rational object containing the transfer function representation.
   */
  protected void constructRationalRepresentation() {
    
    double[] a = new double[ order + 1 ];  
    double[] b = new double[ order + 1 ];
    
    a[0] = 1.0;
    for ( int p = 0;  p < order;  p++ ) {
      
      Arrays.fill( b, 0.0 );

      int i = 0;
      while ( i <= p ) {
        b[i]   += a[i];
        b[i+1] += k[p]*a[p-i];
        i++;
      }
      
      System.arraycopy( b, 0, a, 0, p+2 );
    }
    
    Arrays.fill( b, 0.0 );
    for ( int i = 0;  i <= order;  i++ ) b[i] = a[ order - i ];
    
    T = new Rational( (new Polynomial(b)), (new Polynomial(a)) );
  }
  
  
  
  /**
   * Accessor for the Rational representation of this allpass filter.
   *
   * @return      Rational object containing the rational transfer function for this filter.
   */
  public Rational rationalRepresentation() {
    return new Rational(T);
  }
  
  
  
  /**
   * Prints the reflection coefficients defining this allpass filter and the state vector.
   *
   * @param ps      PrintStream to which the allpass filter description is printed.
   */
  public void print( PrintStream ps ) {
    
    DecimalFormat DF = new DecimalFormat( "0.000000" );
    
    ps.println( "Allpass order:  " + order );
    for ( int i = 0;  i < order;  i++ ) {
      if ( i < order ) {
        if ( k[i] < 0.0 ) ps.println( "  "  + DF.format( k[i] ) + "  " + state[i] );
        else              ps.println( "   " + DF.format( k[i] ) + "  " + state[i] );
      }
      else             ps.println( "             " + state[i] );
    }
    
  }  
  
}
