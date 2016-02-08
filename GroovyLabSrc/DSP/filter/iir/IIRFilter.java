
package DSP.filter.iir;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

import DSP.filter.Rational;


/*
 Class to implement an Infinite Impulse Response digital filter.
 Implements the filter as a cascade of second-order sections.  The filter is obtained using a
 bilinear transformation of a prototype analog filter.  This implementation saves internal states
 from one invocation of the filter methods to the next allowing continuous processing of real time
 data streams or very large files in consecutive, contiguous blocks.
 */
public class IIRFilter {

  /** An ArrayList of second order sections. */
  protected ArrayList< SecondOrderSection > sections;
  
  /** Rational object containing the transfer function of the filter. */
  protected Rational                        T;
  
  
  
  /**
   * Instantiates a new IIR filter.
   *
   * @param baseFilter            The AnalogPrototype for this digital filter.
   * @param type                  PassbandType object specifying lowpass, highpass or bandpass type response.
   * @param f1                    double specifying the low cutoff frequency - used by highpass and bandpass types.
   * @param f2                    double specifying the high cutoff frequency - used by lowpass and bandpass types.
   * @param delta                 double specifying the sampling interval for which the filter is designed.
   */
  public IIRFilter( AnalogPrototype baseFilter, PassbandType type, double f1, double f2, double delta ) {
	  
	AnalogPrototype prototype;  
    
    switch (type) {
      
    case LOWPASS:
      prototype = baseFilter.lptolp( warp( f2, delta ) );
      break;
      
    case BANDPASS:
      prototype = baseFilter.lptobp( warp( f1, delta), warp( f2, delta ) );
      break;
    
    case HIGHPASS:
      prototype = baseFilter.lptohp( warp( f1, delta) );
      break;
      
    default:
      throw new IllegalStateException( "Undefined passband type" );
        
    }
    
    double[] tn = new double[2];
    double[] td = new double[2];
    tn[0] =  1.0;
    tn[1] = -1.0;
    td[0] =  1.0;
    td[1] =  1.0;
    Rational S = new Rational( tn, td );
    
    T = new Rational( 1.0 );
    
    sections = new ArrayList< SecondOrderSection >();
    
    for ( int i = 0;  i < prototype.nSections();  i++ ) {
      Rational R = prototype.getSection(i).map( S );
      T.timesEquals(R);
      double[] cn = R.numerator().coefficients();
      double[] cd = R.denominator().coefficients();
      double   s  = 1.0;
      if ( cd[0] != 0.0 ) s = cd[0];
      
      double b0 = cn[0]/s;
      double b1 = 0.0;
      if ( cn.length >= 2 ) b1 = cn[1]/s;
      double b2 = 0.0;
      if ( cn.length >= 3 ) b2 = cn[2]/s;
      double a1 = 0.0;
      if ( cd.length >= 2 ) a1 = cd[1]/s;
      double a2 = 0.0;
      if ( cd.length >= 3 ) a2 = cd[2]/s;
      sections.add( new SecondOrderSection( b0, b1, b2, a1, a2 ) );
    }
    
  }
  
  
  
  /**
   * Initializes the states of the filter, i.e. of each of the second-order sections.
   */
  public void initialize() {
	  for ( int i = 0;  i < sections.size();  i++ ) {
		  sections.get(i).initialize();
	  }
  }
  
  
  
  /**
   * Filters a single sample of a sequence.
   *
   * @param x       double containing the sequence sample.
   * @return        double value of the resulting filtered sequence.
   */
  public double filter( double x ) {
	  double retval = sections.get(0).filter(x);
	  for ( int i = 1;  i < sections.size();  i++ )
		  retval = sections.get(i).filter( retval );
	  
	  return retval;
  }
  
  
  
  /**
   * Filters an array of sequence samples.
   * 
   * Suitable for use in filtering a long file or continuous data stream broken into
   * consecutive, contiguous blocks.  Maintains state between invocations, allowing 
   * continuous processing.
   *
   * @param x    double[] containing samples of the sequence to be filtered.
   * @param y    double[] containing samples of the resulting filtered sequence.
   */
  public void filter( double[] x, double[] y ) {
	  Arrays.fill( y, 0.0f );
	  sections.get(0).filter( x, y );

	  for ( int i = 1;  i < sections.size();  i++ ) {
		  sections.get(i).filter( y, y );		  
	  }
  }
  
  
  
  /**
   * Filters an array of sequence samples in-place.
   * 
   * In this implementation, the source and destination arrays are identical, conserving
   * storage.
   *
   * @param x     double[] contains samples of the sequence to be filtered upon call and the filtered
   *              samples following execution
   */
  public void filter( double[] x ) {
    for ( SecondOrderSection section : sections ) {
      section.filter( x, x );     
    }
  }
  
  
  
  /**
   * Evaluates the transfer function of this IIR filter at a specified discrete time frequency.
   *
   * @param Omega      double containing the discrete frequency (in [0, pi]) for evaluation of the transfer function.
   * @return           Complex object containing the value of the transfer function at frequency Omega.
   */
  public Complex evaluate( double Omega ) {
	  Complex ejOmega = Complex.exp( new Complex(0.0, -Omega ) );
	  return T.evaluate( ejOmega );
  }
  
  
  
  /**
   * Computes the group delay of the IIR filter at a specified discrete time frequency.
   *
   * @param Omega       double containing the discrete frequency (in [0, pi]) for evaluation of the group delay.
   * @return            double containing the resulting group delay.
   */
  public double groupDelay( double Omega ) {
    return T.discreteTimeGroupDelay( Omega );
  }
  
  
  
  /**
   * Prints the coefficients and states of this IIR filter, section by section.
   *
   * @param ps      PrintStream object to which this filters coefficients and states are printed.
   */
  public void print( PrintStream ps ) {
	 
	  ps.println( "IIR Filter:" );
	  for ( int i = 0;  i < sections.size();  i++ ) {
		  ps.println( "\n  Section " + i + "\n" );
		  sections.get(i).print( ps );
		  ps.println();
	  }
  }
  
  
  // frequency warping for bilinear transformation
  
  /**
   * Method to prewarp cutoff frequencies to correct for the nonlinear frequency mapping of the bilinear transformation.
   *
   * @param f        double containing the analog frequency (typically a cutoff specification) before the bilinear transform.
   * @param delta    double specificying the sampling interval of the data.
   * @return         double containing the prewarped digital frequency correcting for the nonlinearity of the bilinear transform.
   */
  private double warp( double f, double delta ) {
    return Math.tan( Math.PI*f*delta );
  }

  
}