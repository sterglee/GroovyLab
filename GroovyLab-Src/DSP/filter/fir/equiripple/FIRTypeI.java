

package DSP.filter.fir.equiripple;


import DSP.Sequence;


/*
  Class for designing FIR type I digital filters.  Odd-length filters with even symmetry.
  
 See 
  A Unified Approach to the Design of Optimum Linear Phase FIR Digital Filters,
  James H. McClellan and Thomas W. Parks (1973), IEEE Transactions on Circuit Theory, Vol. CT-20, 
  No. 6, pp. 697-701.
  
 and
  
 FIR Digital Filter Design Techniques Using Weighted Chebyshev Approximation, 
 Lawrence R. Rabiner, James H. McClellan and Thomas W. Parks (1975) PROCEEDINGS OF THE IEEE,
 VOL. 63, NO. 4, pp. 595-610.
  
 */
abstract class FIRTypeI extends EquirippleFIRFilter {
   
  /**
   * Instantiates a new FIR type I filter.
   *
   * @param numBands     int specifying the number of pass and stop bands.
   * @param nHalf        int specifying the half size of the filter - one less than the number of 
   *                       approximating basis functions (cosines).
   */
  FIRTypeI( int numBands, int nHalf ) {
    
    super( numBands, nHalf+1, 2*nHalf+1 );
    
  }
  
  void    populateGrid( DesignGrid G ) {
    
    for ( int i = 0;  i < G.gridSize;  i++ ) {
      G.H[i]    = desiredResponse( G.grid[i] );
      G.W[i]    = weight( G.grid[i] );
    }
    
    G.containsZero = true;
    G.containsPi   = true;
  }
   
  
  
  double[] interpretCoefficients( double[] coefficients ) {
    
    double[] retval = new double[ Nc ];
    Sequence.circularShift( coefficients, N-1 );
    System.arraycopy( coefficients, 0, retval, 0, Nc );
    
    return retval;
  }

}
