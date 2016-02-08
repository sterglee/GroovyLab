
package  DSP.filter.fir.equiripple;

import DSP.Sequence;


/**
  Class for designing FIR type II digital filters.  Even length filters with even symmetry.
  
 See 
  
 A Unified Approach to the Design of Optimum Linear Phase FIR Digital Filters,
 James H. McClellan and Thomas W. Parks (1973), IEEE Transactions on Circuit Theory, Vol. CT-20, 
  No. 6, pp. 697-701.
 
 and
 
FIR Digital Filter Design Techniques Using Weighted Chebyshev Approximation, 
 Lawrence R. Rabiner, James H. McClellan and Thomas W. Parks (1975) PROCEEDINGS OF THE IEEE,
VOL. 63, NO. 4, pp. 595-610.
 */
abstract class FIRTypeII extends EquirippleFIRFilter {
  
  // even length filters with even symmetry

  /**
   * Instantiates a new fIR type ii.
   *
   * @param numBands     int specifying the number of pass and stop bands.
   * @param nHalf        int specifying the half size of the filter - equal to the number of
   *                       approximating basis functions in this case.
   */
  FIRTypeII( int numBands, int nHalf ) {
    
    super( numBands, nHalf, 2*nHalf );
    
  }

  void populateGrid( DesignGrid G ) {
    
    for ( int i = 0;  i < G.gridSize;  i++ ) {
      G.H[i]    = desiredResponse( G.grid[i] ) / Math.cos( G.grid[i]*Math.PI/2.0 );
      G.W[i]    = weight( G.grid[i] ) * Math.cos( G.grid[i]*Math.PI/2.0 );
    }
    
    if ( Math.abs( G.grid[0] ) < 1.0E-6 )
      G.containsZero = true;
    else
      G.containsZero = false;
    
    G.containsPi   = false;
   }
   
  double[] interpretCoefficients( double[] coefficients ) {
    double[] retval = new double[ Nc ];
    Sequence.circularShift( coefficients, N-1 );
    retval[0] = 0.5f*coefficients[0];
    for ( int i = 1;  i < Nc-1;  i++ ) {
      retval[i] = 0.5f*( coefficients[i] + coefficients[i-1] );
    }
    retval[ Nc-1 ] = 0.5f*coefficients[ Nc-2 ];
    return retval;
  }

}
