


package DSP.fir;

import DSP.HammingWindow;
import DSP.Sequence;



/**
  Implements a finite impulse response (FIR) interpolator for resampling sequences up by an integer rate.
  This implementation uses a windowed sinc function as the interpolation kernel.  The length of the window
   is determined by the interpolation rate and a design factor which controls interpolation accuracy.  A
  Hamming window is used to window the sinc function.
 
 The window length is N = 2*M + 1, where M = (interpolation rate)*designFactor.  The interpolation kernel
  is:
 
  h[n] = ( 0.54 + 0.46*cos(pi/M * n) ) * sin( n*pi/R ) / ( n*pi/R )
  is the interpolation rate.
 
 This implementation assumes that the data will be processed in consecutive, contiguous blocks of uniform
  size.  The block size is specified in the constructor for the Interpolator, and thereafter calls to interpolate()
  will interpolate successive blocks of that size.  Very important:  the block size must not change.  State 
  information required to assure continuous interpolation from block to block is maintained by the class instance, 
  so that there are no edge effects in the interpolated result sequence (except at the very beginning and end of 
  the data stream).
 */
public class Interpolator {
  
  /** int containing the interpolation rate. */
  private int         rate;
  
  /** OverlapAdd instance that performs the interpolation by convolution with a windowed sinc function. */
  private OverlapAdd  overlapAdd;
  
  /** double[] that buffers the low-rate sequence to be interpolated. */
  private double[]     buffer;
  
  
  
  /**
   * Instantiates a new Interpolator.
   *
   * @param rate                int containing the interpolation rate.
   * @param designFactor        int containing parameter controlling the accuracy of interpolation
   *                            through the length of the interpolation filter
   * @param blockSize           int controlling the size of blocks in which the data will be processed
   */
  public Interpolator( int rate, int designFactor, int blockSize ) {
    
    this.rate = rate;
    
    int half = rate*designFactor;
    int N    = 2*half + 1;
    double[] kernel = ( new HammingWindow(N) ).getArray();
    for (int i = 1; i <= half; i++) {
      kernel[ half + i ] *= (double) ( Math.sin(Math.PI * i / rate) / ( Math.PI * i / rate ) );
      kernel[ half - i ]  = kernel[ half + i ];
    }

    overlapAdd = new OverlapAdd( kernel, blockSize*rate );
    
    buffer = new double[ blockSize*rate ];
  }
  
  
  
  /**
   * Interpolates blocks of a data stream.
   * 
   * 
   *
   * @param block                 double[] containing a block of the sequence to be interpolated
   * @param interpolatedBlock     double[] containing the interpolated block
   */
  public void interpolate( double[] block, double[] interpolatedBlock ) {
    Sequence.stretch( block, rate, buffer );
    overlapAdd.filter( buffer, 0, interpolatedBlock, 0 );
  }
  
}
