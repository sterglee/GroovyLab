
package DSP.fir;

import DSP.Sequence;
import DSP.filter.fir.equiripple.CenteredHilbertTransform;


/*
  Enables a complex analytic signal to be constructed from a real signal.
  This class uses the CenteredHilbertTransform class to construct the complex analytic counterpart
  of a real signal.  The class is perhaps most useful for obtaining the envelope of a signal and
  a method is supplied for this purpose.  This class is intended to manipulate finite duration 
  signals in one piece, not to process continuous streams in consecutive, contiguous blocks.
  
 */
public class ComplexAnalyticSignal {
  
  /** The real part of the signal. */
  double[] realPart;
  
  /** The imaginary part of the signal. */
  double[] imagPart;
  
  
  
  /**
   * Instantiates a new complex analytic signal.
   *
   * @param realSignal   double[] containing the original real signal.
   */
  public ComplexAnalyticSignal( double[] realSignal ) {
    realPart = realSignal.clone();
    CenteredHilbertTransform transformer = new CenteredHilbertTransform( 50, 0.03, 0.97 );
    double[] tmp = transformer.filter( realPart );
    Sequence.zeroShift( tmp, -50 );
    imagPart = new double[ realPart.length ];
    System.arraycopy( tmp, 0, imagPart, 0, realPart.length );
  }
  
  
  
  /**
   * Computes and returns the envelope of the signal.
   *
   * @return     double[] containing the signal envelope.
   */
  public double[] getEnvelope() {
    double[] retval = new double[ realPart.length ];
    for ( int i = 0;  i < realPart.length;  i++ ) {
      retval[i] = (double) Math.sqrt( realPart[i]*realPart[i] + imagPart[i]*imagPart[i] );
    }
    
    return retval;
  }
  
  
  
  /**
   * Accessor for the real part of the signal.
   *
   * @return     double[] containing the real part of the signal.
   */
  double[] getRealPart() {
    return realPart.clone();
  }
  
  
  
  /**
   * Accessor for the imaginary part of the signal.
   *
   * @return     double[] containing the imaginary part of the signal.
   */
  double[] getImagPart() {
    return imagPart.clone();
  }
  
}
