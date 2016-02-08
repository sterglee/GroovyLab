
package DSP.filter.fir.equiripple;



/*
  Half band prototype filter used by EquirippleHalfBand for interpolation by a factor of 2.
 This class is intended be used by EquirippleHalfBand to design filters suitable for
  interpolating sequences by a factor of two.  It is used in conjuction with the half-band "trick"
  described in
 
 for the Design of FIR Half-Band Filters, P. P. VAIDYANATHAN AND TRUONG Q. NGUYEN (1987),
  IEEE TRANSACTIONS ON CIRCUITS AND SYSTEMS, VOL. CAS-34, NO. 3, pp. 297-300.
 */
class EquirippleHalfBandPrototype extends FIRTypeII {


  /**
   * Instantiates a new equiripple half band prototype.
   *
   * @param N         int specifying the design order of the filter.
   * @param OmegaP    double specifying the upper band edge of the single band used in this filter type.
   */
  EquirippleHalfBandPrototype( int N, double OmegaP ) {
    
    super( 1, N );
    
    if ( OmegaP <= 0.0  ||  OmegaP >= 1.0 ) 
      throw new IllegalArgumentException( "OmegaP: " + OmegaP + " out of bounds (0.0 < OmegaP < 1.0)" );

    
    bands[0][0] = 0.0;
    bands[0][1] = OmegaP;
    
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
