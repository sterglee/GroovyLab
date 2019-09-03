

package DSP.filter.fir.equiripple;

/*
  Implements a centered equiripple Hilbert transform operator - the point of symmetry falls on a sample.
  
 This class uses the Remez exchange algorithm to design a Hilbert transformer of length 2N+1.
 The Nth sample is zero (counting from 0), and the impulse response is an anti-symmetric sequence 
  about this point.  The filter is linear phase, with group delay a constant equal to N.  The 
  design parameters are the order (N) specifying the number (N+1) of approximating functions in the 
  Remez algorithm, and two parameters specifying the band edge frequencies.  The design problem 
  is performed on a discrete-time frequency axis normalized to range between 0 and 1 (the folding 
  frequency).  Omega1, the lower band edge of the passband must be greater than 0 and less than
  Omega2, the upper band edge.  Omega2 must be strictly less than 1.  A tradeoff exists between the 
  filter order N and band edge frequencies.  As Omega1 approaches 0 or Omega2 approaches 1, the order
  must be increased to obtain an acceptable design.
 For details on the design algorithm and characteristics of the filter response, see
 
 A Unified Approach to the Design of Optimum Linear Phase FIR Digital Filters,
  James H. McClellan and Thomas W. Parks (1973), IEEE Transactions on Circuit Theory, Vol. CT-20, 
  No. 6, pp. 697-701.
 
 and
 
 FIR Digital Filter Design Techniques Using Weighted Chebyshev Approximation, 
  Lawrence R. Rabiner, James H. McClellan and Thomas W. Parks (1975) PROCEEDINGS OF THE IEEE,
  VOL. 63, NO. 4, pp. 595-610.
 
 */
public class CenteredHilbertTransform extends FIRTypeIII  {


  /*
    Instantiates a new centered Hilbert transform operator.
   
   N       int specifying the number (N+1) of approximating functions in the Remez design
      algorithm and the resulting number of FIR filter coefficients (2N+1).
   Omega1  double specifying the low passband edge of the filter.  Omega1 > 0
   Omega2  double specifying the high passband edge of the filter. Omega1 < Omega2 < 1.
   */
  public CenteredHilbertTransform( int N, double Omega1, double Omega2 ) {
      
    super( 1, N );
    
    if ( !( 0 < Omega1  &&  Omega1 < Omega2  &&  Omega2 < 1.0 ) )
      throw new IllegalArgumentException( "Check 0.0 < Omega1 < Omega2 < 1.0" );
        
    bands[0][0] = Omega1;
    bands[0][1] = Omega2;
      
    generateCoefficients();
  }


    
  double desiredResponse( double Omega ) {
      
    double retval = 0.0;
    if ( LTE( bands[0][0], Omega)  &&  LTE( Omega, bands[0][1] ) )  retval = 1.0;
        
    return retval;
  }



  double weight( double Omega ) {
      
    double retval = 0.0;
      
    if (  LTE( bands[0][0], Omega)  &&  LTE( Omega, bands[0][1] ) ) 
      retval = 1.0;
      
    return retval;
  }
    
}
