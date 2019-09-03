

package DSP.filter.fir.equiripple;


import DSP.Sequence;


/*
  Class for designing FIR type III digital filters.  Odd length filters with odd symmetry.
  
 See
 
 A Unified Approach to the Design of Optimum Linear Phase FIR Digital Filters,
 James H. McClellan and Thomas W. Parks (1973), IEEE Transactions on Circuit Theory, Vol. CT-20, 
  No. 6, pp. 697-701.
 
 and
  
 FIR Digital Filter Design Techniques Using Weighted Chebyshev Approximation, 
  Lawrence R. Rabiner, James H. McClellan and Thomas W. Parks (1975) PROCEEDINGS OF THE IEEE,
  VOL. 63, NO. 4, pp. 595-610.
 */
abstract class FIRTypeIII extends EquirippleFIRFilter {

  
  /**
   * Instantiates a new FIR type III filter.
   *
   * @param numBands      int specifying the number of pass and stop bands.
   * @param nHalf         int specifying the half-length of the filter - equal to one less
   *                        than the number of approximating basis functions in the Remez algorithm.
   */
  FIRTypeIII( int numBands, int nHalf ) {
    
    super( numBands, nHalf, 2*nHalf+1 );
    
  }
  
  
  void populateGrid( DesignGrid G ) {
    
    for ( int i = 0;  i < G.gridSize;  i++ ) {
      G.H[i]    = desiredResponse( G.grid[i] ) / Math.sin( G.grid[i]*Math.PI );
      G.W[i]    = weight( G.grid[i] ) * Math.sin( G.grid[i]*Math.PI );
    }

    G.containsZero = false;
    G.containsPi   = false;
  }
   
  double[] interpretCoefficients( double[] coefficients ) {
    double[] retval = new double[ Nc ];
    Sequence.circularShift( coefficients, N-1 );
    retval[0] = -0.5f*coefficients[0];
    retval[1] = -0.5f*coefficients[1];
    for ( int i = 2;  i < Nc-2;  i++ ) {
      retval[i] = 0.5f*( coefficients[i-2] - coefficients[i] );
    }
    retval[ Nc-2 ] = 0.5f*coefficients[ Nc-4 ];
    retval[ Nc-1 ] = 0.5f*coefficients[ Nc-3 ];
    return retval;
  }


}
