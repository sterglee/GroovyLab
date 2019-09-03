
package DSP.filter.fir.equiripple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import DSP.fft.RDFT;
import DSP.fir.OverlapAdd;


/**
 * Base class for equiripple FIR filters designed with the Parks-McClellan algorithm.
 */
abstract class EquirippleFIRFilter {
  
  /** Constant specifying a tolerance for checking band edge inclusion in the design grid. */
  protected static double MACHINETOLERANCE = 1.0E-6;
  
  /** double[][] specifying band edge information */
  protected double[][]         bands;             // specifies band edges
  
  /** int specifying the number of bands */
  protected int                numBands;          // number of bands
  
  /** int specifying the number of approximating functions in the Remez algorithm */
  protected int                N;                 // number of approximating degrees of freedom
  
  /** double[] containing the FIR filter coefficients */
  protected double[]            coefficients;      // filter coefficients
  
  /** int containing the number of filter coefficients */
  protected int                Nc;                // number of coefficients
  
  /** An OverlapAdd instance that can be used to filter data with the filter. */
  protected OverlapAdd         implementation;
  
  
  /**
   * Instantiates a new equiripple FIR filter.
   *
   * @param numBands    int specifying the number of pass and stop bands.
   * @param N           int specifying the design order of the filter.
   * @param Nc          int specifying the number of FIR filter coefficients
   */
  EquirippleFIRFilter( int numBands, int N, int Nc ) {
    this.numBands = numBands;
    bands         = new double[ numBands ][2];
    this.N        = N;
    this.Nc       = Nc;
  }
  
  
  
  /**
   * Method to create the design grid.
   *
   * @return    DesignGrid object used by the Remez exchange algorithm
   */
  protected DesignGrid createGrid() {
    
    DesignGrid G = new DesignGrid();
    
    //  initial guess for extreme points - need N + 1 approximately equally spaced among pass and stop bands
    //    include band edges, 0 and pi
    
    int[] nextrema = new int[ numBands ];  // defines allocation of extrema among bands
    
    double totalBandwidth = 0.0;
    for ( int ib = 0;  ib < numBands;  ib++ ) totalBandwidth  +=  bands[ib][1] - bands[ib][0];
        
    int m           = N+1 - 2*numBands;
    int np          = 0;
    int largestBand = 0;
    int nmax        = 0;
    for ( int ib = 0;  ib < numBands;  ib++ ) {
      double B  = bands[ib][1] - bands[ib][0];
      nextrema[ib] = (int) Math.round( m*B/totalBandwidth ) + 2;
      if ( nextrema[ib] > nmax ) {
        nmax = nextrema[ib];
        largestBand = ib;
      }
      np += nextrema[ib];
    }
    
    // Add or delete extrema to largest band to assure exactly N+1 extrema are specified
    //   Because of rounding the last step may not produce the correct number
    
    while ( np < N+1 ) {
      nextrema[ largestBand ]++;
      np++;
    }
    while ( np > N+1 ) {
      nextrema[ largestBand]--;
      np--;
    }
    
    // set up dense grid and initial estimate of extrema
    
    G.bandEdgeIndices = new int[ numBands*2 ];
    G.extremaIndices  = new int[ N + 1 ];
    
    //   grid sampling proportional to band widths
    
    ArrayList< Double > gridArray = new ArrayList< Double >();
    int gridpt        = 0;
    int extremum      = 0;
    int bandEdgeCount = 0;
    int perturbation  = 0;
    Random R = new Random();
    for ( int ib = 0;  ib < numBands;  ib++ ) {
      double B      = bands[ib][1] - bands[ib][0];
      int    n      = 1 + (nextrema[ib]-1)*DesignGrid.GRIDDENSITY;
      double dB = B/(n-1);
      double base   = bands[ib][0];
      for ( int i = 0;  i < n;  i++ ) {
        
        double Omega = base + dB*i;
        gridArray.add( Omega );
        
        if ( i % DesignGrid.GRIDDENSITY  ==  0 ) {
          if ( i != 0  &&  i != n-1 )
            perturbation = R.nextInt(3) - 1;
          else
            perturbation = 0;
          G.extremaIndices[ extremum++ ] = gridpt + perturbation;
        }
        if ( i == 0  ||  i == n-1 ) {
          G.bandEdgeIndices[ bandEdgeCount ] = gridpt;
          bandEdgeCount++;
        }
        
        gridpt++;
      }
    }
    G.gridSize = gridArray.size();
    G.grid     = new double[ G.gridSize ];
    G.X        = new double[ G.gridSize ];
    G.H        = new double[ G.gridSize ];
    G.W        = new double[ G.gridSize ];
    for ( int i = 0;  i < G.gridSize;  i++ ) {
      G.grid[i] = gridArray.get( i );
      G.X[i]    = Math.cos( G.grid[i]*Math.PI );
    }
    
    return G;
  }
  
  
  
  /**
   * Method made concrete by TypeI-IV filters to populate the unified weight and objective functions.
   *
   * @param G      DesignGrid object used by the Remez exchange algorithm.
   */
  abstract  void       populateGrid( DesignGrid G );
  
  
  
  /**
   * Method made concrete by specific filter classes (e.g. EquirippleLowpass) to specify the objective function.
   *
   * @param Omega      double specifying the normalized frequency at which the desired response is evaluated.
   * @return           double containing the desired response at this frequency.
   */
  abstract  double     desiredResponse( double Omega );
  
  

  /**
   * Weight.
   *
   * @param Omega      double specifying the normalized frequency at which the weight function is evaluated.
   * @return           double containing the weight function at this frequency.
   */
  abstract  double     weight( double Omega );
  
  
  
  /**
   * Method to interpret cosine basis coefficients as TypeI-TypeIV FIR filter coefficients.
   *
   * @param coefficients     double[] containing the cosine sequence coefficients
   * @return                 double[] containing the corresponding FIR filter coefficients.
   */
  abstract  double[]    interpretCoefficients( double[] coefficients );
  
  
  
  /**
   * Method to generate cosine basis coefficients from response function on a dense grid.
   */
  public    void       generateCoefficients() {
    DesignGrid G = createGrid();
    populateGrid( G );
    EquirippleDesigner.remez( G );
    coefficients = interpretCoefficients( EquirippleDesigner.calculateCoefficients( G, Nc ) );
  }
  
  
  
  /**
   * Method to access the FIR filter coefficients for this design.
   *
   * @return the coefficients
   */
  public    double[]    getCoefficients() {
    return coefficients.clone();
  }
  
  
  
  /**
   * Method to provide a new OverlapAdd instance to implement the filter.
   *
   * @param blockSize the block size
   * @return the implementation
   */
  public    OverlapAdd getImplementation( int blockSize ) {
    return new OverlapAdd( getCoefficients(), blockSize );
  }
  
  
  
  /**
   * Method to filter a fixed-length sequence with this filter.
   *
   * @param x       double[] containing the input sequence.
   * @return        double[] containing the resulting filtered sequence.
   */
  public    double[]    filter( double[] x ) {
    
    int nfft     = 16;
    int log2nfft = 4;
    int n        = x.length + coefficients.length - 1;
    while ( nfft < n ) {
      nfft *= 2;
      log2nfft++;
    }
    
    RDFT    fft = new RDFT( log2nfft );
    double[] tmp       = new double[ nfft ];
    double[] transform = new double[ nfft ];
    double[] kernel    = new double[ nfft ];
    
    System.arraycopy( x, 0, tmp, 0, x.length );
    fft.evaluate( tmp, transform  );
    
    Arrays.fill( tmp, 0.0f );
    System.arraycopy( coefficients, 0, tmp, 0, coefficients.length );
    fft.evaluate( tmp, kernel );
    
    RDFT.dftProduct( kernel, transform, 1.0f );
    fft.evaluateInverse( transform, tmp );
    
    // trim off trailing zeros
    
    kernel = new double[ n ];
    System.arraycopy( tmp, 0, kernel, 0, n );
    
    return kernel;
  }
  
  
  
  /**
   * Method to determine whether one double is close to another.
   *
   * @param x     the first double
   * @param y     the second double
   * @return true, if the numbers are close, false otherwise
   */
  protected boolean    LTE( double x, double y ) {
    boolean retval = false;
    
    if ( x < y ) retval = true;
    
    if ( Math.abs( x - y ) < MACHINETOLERANCE ) retval = true;
    
    return retval;
  }

}
