
package DSP.filter.fir.equiripple;


/*
  Implements a centered FIR highpass filter - the point of symmetry falls on a sample.
  
 This class uses the Remez exchange algorithm to design a highpass filter of length 2N+1.
 The impulse response is a symmetric sequence about point N (counting from 0). The filter is linear 
 phase, with group delay a constant equal to N.  The design parameters are the order (N) specifying 
 the number (N+1) of approximating functions in the Remez algorithm and four parameters controlling 
 the cutoffs and design weights of the stopband and passband.  The design problem is performed on 
 a discrete-time frequency axis normalized to range between 0 and 1 (the folding frequency).  The
 stop band is the interval [0, OmegaS] and the passband is the interval [OmegaP, 1].  Note that
 OmegaP < OmegaS and the two bands must have non-zero width.  There also is a transition band,
 the open interval (OmegaS, OmegaP), that must have non-zero width.  The narrower any of these bands,
 the larger the order N must be to obtain a reasonable frequency response.  Weights are specified
 for each band to control the relative size of the maximum error between bands.
 For details on the design algorithm and characteristics of the filter response, see</p>
  
 A Unified Approach to the Design of Optimum Linear Phase FIR Digital Filters,
 James H. McClellan and Thomas W. Parks (1973), IEEE Transactions on Circuit Theory, Vol. CT-20, 
 No. 6, pp. 697-701.
 
 and
  
 FIR Digital Filter Design Techniques Using Weighted Chebyshev Approximation, 
  Lawrence R. Rabiner, James H. McClellan and Thomas W. Parks (1975) PROCEEDINGS OF THE IEEE,
  VOL. 63, NO. 4, pp. 595-610.
 
 and for order selection, consult:
 
 Approximate Design Relationships for Low-Pass FIR Digital Filters, Lawrence R. Rabiner (1973), 
  IEEE TRANSACTIONS ON AUDIO AND ELECTROACOUSTICS, VOL. AU-21, NO. 5, pp. 456-460.
 
 */
public class EquirippleHighpass extends FIRTypeI {
  
  /** double specifying the error weighting in the passband. */
  private double Wp;
  
  /** double specifying the error weighting in the stopband. */
  private double Ws;

  /**
   * Instantiates a new equiripple highpass filter.
   *
   * @param N         int specifying the design order of the filter.
   * @param OmegaS    double specifying the upper edge of the stop band.
   * @param Ws        double specifying the error weighting in the stop band.
   * @param OmegaP    double specifying the lower edge of the pass band.
   * @param Wp        double specifying the error weighting in the pass band.
   */
  public EquirippleHighpass( int N, double OmegaS, double Ws, double OmegaP, double Wp ) {
    
    super( 2, N );
    
    if ( OmegaS >= OmegaP ) throw new IllegalArgumentException( "OmegaS >= OmegaP " );
    if ( OmegaS <= 0.0  ||  OmegaS >= 1.0 ) 
      throw new IllegalArgumentException( "OmegaS: " + OmegaS + " out of bounds (0.0 < OmegaS < 1.0)" );
    if ( OmegaP <= 0.0  ||  OmegaP >= 1.0 ) 
      throw new IllegalArgumentException( "OmegaP: " + OmegaP + " out of bounds (0.0 < OmegaP < 1.0)" );
    
    bands[0][0] = 0.0;
    bands[0][1] = OmegaS;
    bands[1][0] = OmegaP;
    bands[1][1] = 1.0;
    
    this.Ws = Ws;
    this.Wp = Wp;

    generateCoefficients();
  }


  double desiredResponse( double Omega ) {
    
    double retval = 0.0;
    if ( LTE( bands[1][0], Omega )  &&  LTE( Omega, bands[1][1] ) )  retval = 1.0;
      
    return retval;
  }



  double weight( double Omega ) {
    
    double retval = 0.0;
    
    if ( LTE( bands[0][0], Omega )  &&  LTE( Omega, bands[0][1] ) ) 
      retval = Ws;
    else if ( LTE( bands[1][0], Omega )  &&  LTE( Omega, bands[1][1] ) ) 
      retval = Wp;
    
    return retval;
  }
  
}
