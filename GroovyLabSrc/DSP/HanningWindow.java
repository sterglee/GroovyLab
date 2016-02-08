

package DSP;


/**
 * Designs and implements Hanning windows (See Oppenheim and Schafer, 1975).
 */
public class HanningWindow extends Window {
  
  /**
   * Instantiates a new Hanning window of length N samples.
   *
   * @param N    int specifying the window length in samples.
   */
  public HanningWindow( int N ) {
    
    super(N);
    
    for ( int i = 0;  i < N;  i++ ) {
      w[i] = (double) (0.5 + 0.5*Math.cos( -Math.PI + i*2*Math.PI/(N-1) ) );
    }
    
  }

}
