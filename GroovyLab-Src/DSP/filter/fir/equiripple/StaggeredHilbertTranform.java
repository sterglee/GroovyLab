

package DSP.filter.fir.equiripple;



/**
  Implements an even-length Hilbert transform operator - the point of symmetry falls "between" samples.
  
 This class implements a Hilbert transformer with impulse response that exhibits odd symmetry on a
 staggered grid, i.e. the point of symmetry falls "between" samples.  This filter has a response that
 is accurate over a wider frequency band than the comparable "centered" (odd length) Hilbert transformer. 
 This advantage may be offset by a non-integer group delay ( (2N-1)/2 ) which shifts the waveform by 
 a fraction of a sample.  For example, to compute the envelope of a signal, a centered Hilbert transformer
 may be a better choice.  The envelope is the square root of sum of squares of the original signal and its
 Hilbert transform.  With this implementation, the signal and its Hilbert transform will be misaligned by
 a half sample (even after correcting for the bulk delay of the FIR impulse response - roughly half the filter
  length).

The quality of the design is controlled by two parameters:  the filter design order, N, which specifies 
the number of approximating basis functions in the Remez algorithm, and the passband low-frequency cutoff, 
OmegaP, which specifies the point near 0 frequency where the filter response begins its transition to zero.
The closer OmegaP is to zero, the larger N must be (and the longer the filter impulse response) to obtain 
an acceptable approximation to the ideal Hilbert frequency response.
 
 For details on the design algorithm and characteristics of the filter response, see
 
 A Unified Approach to the Design of Optimum Linear Phase FIR Digital Filters,
  James H. McClellan and Thomas W. Parks (1973), IEEE Transactions on Circuit Theory, Vol. CT-20, 
  No. 6, pp. 697-701.
 
 and
 
 FIR Digital Filter Design Techniques Using Weighted Chebyshev Approximation, 
  Lawrence R. Rabiner, James H. McClellan and Thomas W. Parks (1975) PROCEEDINGS OF THE IEEE,
  VOL. 63, NO. 4, pp. 595-610.
  
 */
public class StaggeredHilbertTranform extends FIRTypeIV  {


  /**
   * Instantiates a new staggered hilbert tranform.
   *
   * @param N the n
   * @param OmegaP the omega p
   */
  public StaggeredHilbertTranform( int N, double OmegaP ) {
      
    super( 1, N );
    
    if ( !( 0.0 < OmegaP  &&  OmegaP < 1.0 ) )
      throw new IllegalArgumentException( "Check 0.0 < OmegaP < 1.0" );
        
    bands[0][0] = OmegaP;
    bands[0][1] = 1.0;
      
    generateCoefficients();
  }


    
  /* (non-Javadoc)
   * @see com.oregondsp.signalProcessing.filter.fir.equiripple.EquirippleFIRFilter#desiredResponse(double)
   */
  double desiredResponse( double Omega ) {
      
    double retval = 0.0;
    if ( LTE( bands[0][0], Omega)  &&  LTE( Omega, bands[0][1] ) )  retval = 1.0;
        
    return retval;
  }



  /* (non-Javadoc)
   * @see com.oregondsp.signalProcessing.filter.fir.equiripple.EquirippleFIRFilter#weight(double)
   */
  double weight( double Omega ) {
      
    double retval = 0.0;
      
    if (  LTE( bands[0][0], Omega)  &&  LTE( Omega, bands[0][1] ) ) 
      retval = 1.0;
      
    return retval;
  }

}
