
package DSP;

/**
 * Base class for implementing windows - partial implementation.
 * 
 * This class and its derived classes make it possible to "snip out" a portion of a signal 
 * beginning at a specified index and shape the resulting segment with a specified weighting
 * function.  The length of the resulting segment is the same length as the window.
 */
public class Window {
  
  /** double[] containing the window coefficients. */
  protected double[] w;
  
  
  /**
   * Instantiates a new Window from a vector of coefficients.
   *
   * @param w     double[] containing  the vector of window coefficients.
   */
  public Window( double[] w ) {
    this.w = w.clone();
  }
  
  
  
  /**
   * Instantiates a new length-N window containing zeros.
   *
   * @param N     int specifying the window length in samples.
   */
  public Window( int N ) {
    w = new double[ N ];
  }
  
  
  
  /**
   * Returns the length of the window in samples.
   *
   * @return    int containing the window length in samples.
   */
  public int length() { 
    return w.length;
  }
  
  
  
  /**
   * Allows a window to be modified in-place by multiplication by another window.
   *
   * @param x    double[] containing the coefficients of the second window, which modifies the first (this) window.
   */
  public void timesEquals( double[] x ) {
    if ( x.length != w.length ) throw new IllegalArgumentException( "Argument length does not match window length" );
    for ( int i = 0;  i < w.length;  i++ ) w[i] *= x[i];
  }
  
  
  
  /**
   * Returns a copy of the coefficients of this window.
   *
   * @return     double[] containing window coefficients.
   */
  public double[] getArray() { 
    return w.clone();
  }
  
  
  
  /**
   * Windows a sequence and places the result in a specified array.
   *
   * @param x          double[] containing the sequence to be windowed by this Window.
   * @param index      start point in the input sequence at which this Window is applied.
   * @param y          double[] containing the resulting windowed sequence.
   */
  public void window( double[] x, int index, double[] y ) {
    
    if ( y.length != w.length ) throw new IllegalArgumentException( "Destination array length does not match window length" );
    
    for ( int i = 0;  i < w.length;  i++ ) {
      int j = index + i;
      if ( j >= 0  &&  j < x.length ) 
        y[i] = w[i] * x[j];
      else 
        y[i] = 0.0f;
    }
    
  }

}
