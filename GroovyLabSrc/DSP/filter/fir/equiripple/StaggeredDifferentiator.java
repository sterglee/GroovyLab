

package  DSP.filter.fir.equiripple;


/*
  Implements an even-length differentiator - the point of symmetry falls between samples.
 
 This class implements a differentiator with impulse response that exhibits odd symmetry on a
 staggered grid, i.e. the point of symmetry falls "between" samples.  This filter has an accurate 
 differentiator response that extends from 0 to the folding frequency.  This advantage may be
 offset by a non-integer group delay ( (2N-1)/2 ) which shifts the waveform by a fractional sample.
  The quality of the design is controlled by a single parameter (order N), which specifies the number
  of approximating basis functions in the Remez algorithm.</p>
  
 For details on the design algorithm and characteristics of the filter response, see</p>
  
 A Unified Approach to the Design of Optimum Linear Phase FIR Digital Filters,
  James H. McClellan and Thomas W. Parks (1973), IEEE Transactions on Circuit Theory, Vol. CT-20, 
  No. 6, pp. 697-701.
 
 and
 
 FIR Digital Filter Design Techniques Using Weighted Chebyshev Approximation, 
  Lawrence R. Rabiner, James H. McClellan and Thomas W. Parks (1975) PROCEEDINGS OF THE IEEE,
  VOL. 63, NO. 4, pp. 595-610.
  
 */
public class StaggeredDifferentiator extends FIRTypeIV {

  /** double representing the sampling interval of the data to be differentiated. */
  private double delta;


  /**
   * Instantiates a new differentiator.
   *
   * @param N       int specifying the filter order design parameter.  The larger this value, the
   *                  more accurate the differentiator response.
   * @param delta   double specifying the sampling interval of the data to be differentiated.
   */
  public StaggeredDifferentiator( int N, double delta ) {
    
    super( 1, N );
      
    bands[0][0] = 1.0/(2*N);
    bands[0][1] = 1.0;
    
    this.delta = delta;
    
    generateCoefficients();
  }


  
  /* (non-Javadoc)
   * @see com.oregondsp.signalProcessing.filter.fir.equiripple.EquirippleFIRFilter#desiredResponse(double)
   */
  double desiredResponse( double Omega ) {
    
    double retval = 0.0;
    if ( LTE( bands[0][0], Omega)  &&  LTE( Omega, bands[0][1] ) )  retval = -Math.PI*Omega/delta;
      
    return retval;
  }



  /* (non-Javadoc)
   * @see com.oregondsp.signalProcessing.filter.fir.equiripple.EquirippleFIRFilter#weight(double)
   */
  double weight( double Omega ) {
    
    double retval = 0.0;
    
    if (  LTE( bands[0][0], Omega)  &&  LTE( Omega, bands[0][1] ) ) 
      retval = 1.0/Omega;
    
    return retval;
  }
  
}
