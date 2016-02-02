
package DSP.filter.fir.equiripple;


/**
  Designs a half-band FIR equiripple filter using the "half-band trick" and the Remez algorithm.
 
This class designs a half-band filter (a filter suitable for interpolating data by a factor of 2).
It uses the "half-band trick" described in 
 
 for the Design of FIR Half-Band Filters, P. P. VAIDYANATHAN AND TRUONG Q. NGUYEN (1987),
  IEEE TRANSACTIONS ON CIRCUITS AND SYSTEMS, VOL. CAS-34, NO. 3, pp. 297-300.
 
 The filter is obtained as a transformation of a EquirippleHalfBandPrototype, which is designed
  with the Remez exchange algorithm.  As with other filters, the prototype is specified by a design
  order parameter (N) and a band edge parameter (OmegaP).  The resulting FIR filter has 2N+1 coefficients
  and is evenly symmetric about coefficient N, counting from 0.  The band edge parameter should be close
  to 0.5, though slightly less: 0 < OmegaP < 0.5.  A value of 0.45 is reasonable, and the closer OmegaP
  is to 0.5, the larger N must be to obtain a reasonable response.</p>
 */
public class EquirippleHalfBand {
  
  /** double[] containing the FIR filter coefficients. */
  private double[] coefficients;
  
  
  /**
   * Instantiates a new equiripple half band filter.
   *
   * @param N         int specifying the design order.
   * @param OmegaP    double specifying the upper passband cutoff.
   */
  public EquirippleHalfBand( int N, double OmegaP ) {
    
    EquirippleHalfBandPrototype EHBP = new EquirippleHalfBandPrototype( N, 2*OmegaP );
    
    double[] c = EHBP.getCoefficients();
    
    coefficients = new double[ 2*c.length - 1 ];
    for ( int i = 0;  i < c.length;  i++ ) {
      coefficients[ 2*i ] = 0.5f*c[i];
    }
    coefficients[ c.length - 1 ] = 0.5f;
    
  }
  
  
  
  /**
   * Accessor for the FIR filter coefficients.
   *
   * @return    double[] containing the filter coefficients.
   */
  public double[] getCoefficients() {
    return coefficients.clone();
  }
  
}
