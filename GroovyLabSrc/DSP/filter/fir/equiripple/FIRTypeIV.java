
package DSP.filter.fir.equiripple;

import DSP.Sequence;


  
/*
 Class for designing FIR type IV digital filters.  Even length filters with odd symmetry.
  
 See
  
 A Unified Approach to the Design of Optimum Linear Phase FIR Digital Filters,
 James H. McClellan and Thomas W. Parks (1973), IEEE Transactions on Circuit Theory, Vol. CT-20, 
 No. 6, pp. 697-701.
 
 and
  
 FIR Digital Filter Design Techniques Using Weighted Chebyshev Approximation, 
 Lawrence R. Rabiner, James H. McClellan and Thomas W. Parks (1975) PROCEEDINGS OF THE IEEE,
 VOL. 63, NO. 4, pp. 595-610.
 */
abstract class FIRTypeIV extends EquirippleFIRFilter {
    

  /**
     * Instantiates a new FIR type IV filter.
     *
     * @param numBands        int specifying the number of pass and stop bands
     * @param nHalf           int specifying the half-size of the filter, equal to the number
     *                          of approximating basis functions in the Remez algorithm in this case.
     */
    FIRTypeIV( int numBands, int nHalf ) {
      
    super( numBands, nHalf, 2*nHalf );
      
  }
 
  
  void    populateGrid( DesignGrid G ) {
      
    for ( int i = 0;  i < G.gridSize;  i++ ) {
      G.H[i]    = desiredResponse( G.grid[i] ) / Math.sin( G.grid[i]*Math.PI/2.0 );
      G.W[i]    = weight( G.grid[i] ) * Math.sin( G.grid[i]*Math.PI/2.0 );
    }

    G.containsZero = false;
    if ( Math.abs( G.grid[ G.gridSize - 1] - 1.0 ) < 1.0E-6 )
      G.containsPi   = true;
    else
      G.containsPi   = false;
  }
     
    
  double[] interpretCoefficients( double[] coefficients ) {
    
    double[] retval = new double[ Nc ];
    Sequence.circularShift( coefficients, N-1 );
    retval[0] = -0.5f*coefficients[0];
    for ( int i = 1;  i < Nc-1;  i++ ) {
      retval[i] = 0.5f*( coefficients[i-1] - coefficients[i] );
    }
    retval[ Nc-1 ] = 0.5f*coefficients[ Nc-2 ];
    
    return retval;
  }

}
