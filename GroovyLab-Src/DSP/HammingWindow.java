

package DSP;


/**
 * Designs and implements Hamming windows (see Oppenheim and Schafer, 1975).
 */
public class HammingWindow extends Window {
  
  
  /**
   * Instantiates a new Hamming window of length N samples.
   * @param N       int specifying the window length.
   */
  public HammingWindow( int N ) {
    
    super(N);
    
    for ( int i = 0;  i < N;  i++ ) {
      w[i] = (double) (0.54 + 0.46*Math.cos( -Math.PI + i*2*Math.PI/(N-1) ) );
    }
    
  }
  
}
