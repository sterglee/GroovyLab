

package DSP;

import java.util.Arrays;


/**
 * Class to implement basic signal processing operations on scalar sequences used by other classes in this package.
 * 
 * <p>This class can be used in two ways.  Objects of this class may be instantiated to represent sequences and in that
 * case will serve as a containers for sequence values (double precision).  For this use, methods are supplied
 * that alter or operate on the values of the contained sequence.  Alternatively, the methods (where it makes
 * sense) are supplied in static form.  The class may be used to operate on the user's double arrays without
 * need to instantiate a Sequence object.</p>
 */
public class Sequence {
  
  
  /** double [] containing sequence values */
  protected double [] x;
  
  
  /**
   * Instantiates a new sequence from an array of double values.
   *
   * @param x    double[] containing the Sequence values.
   */
  public Sequence( double[] x ) {
    this.x = new double[ x.length ];
    System.arraycopy( x, 0, this.x, 0, x.length );
  }
  
  
  
  /**
   * Instantiates a new sequence of all zeros, of length N samples.
   *
   * @param N    int specifying the length of the sequence.
   */
  public Sequence( int N ) {
    x = new double[ N ];
  }
  
  
  
  /**
   * Method to alias a source sequence into a destination sequence.
   * 
   * Source value src[n] is added to dst[ mod(n,dst.length) ].
   *
   * @param src     double[] containing the source sequence.
   * @param dst     double[] containing the destination sequence.
   */
  public static void alias( double[] src, double[] dst ) {
    
    int slength = src.length;
    int dlength = dst.length;
    
    Arrays.fill( dst, 0.0f );
    
    for ( int i = 0;  i < slength;  i++ ) 
      dst[ i % dlength ] += src[i];
    
  }
  
  
  
  /**
   * Aliases the current sequence into a smaller sequence.  Modifies this Sequence.
   *
   * @param N    New length of the sequence, and alias modulus.
   */
  public void alias( int N ) {
    double[] newx = new double[N];
    alias( x, newx );
    x = newx;
  }
  
  
  
  /**
   * Accessor for an individual value of the sequence.
   *
   * @param index     int containing the index of the desired sequence value.
   * @return          Sequence value at index.
   */
  public double get( int index ) {
    double retval = 0.0f;
    if ( index >= 0  &&  index < x.length ) retval = x[index];
    return retval;
  }
  
  
  
  /**
   * Accessor for the entire array of sequence values.  Allows the sequence to be modified.
   *
   * @return      double[] reference to the sequence.  Does not return a copy.
   */
  public double[] getArray() {
    return x;
  }
  
  
  
  /**
   * Reverses a sequence in place.
   *
   * @param  y   double[] containing the sequence to be reversed.
   */
  public static void reverse( double[] y ) {
    int i = 0;  
    int j = y.length-1;
    while ( i < j ) {
      double tmp = y[i];
      y[i] = y[j];
      y[j] = tmp;
      i++;
      j--;
    }
  }
  
  
  
  /**
   * Reverses this sequence in-place.
   */
  public void reverse() {
    reverse(x);
  }
  
  
  
  /**
   * Removes the mean of a sequence.
   *
   * @param y    double[] specifying the sequence to be demeaned.
   */
  public static void rmean( double[] y ) {
    double mean = 0.0f;
    for ( int i = 0;  i < y.length;  i++ ) mean += y[i];
    mean /= y.length;
    for ( int i = 0;  i < y.length;  i++ ) y[i] -= mean;
  }
  
  
  
  /**
   * Removes the mean of this sequence in-place.
   */
  public void rmean() {
    rmean(x);
  }
  
  
  
  /**
   * Performs a circular shift of a sequence.
   *
   * @param y       double[] containing sequence to be shifted.
   * @param shift   int specifying the size and direction of the shift.  A negative
   *                number specifies a left shift, a positive number, a right shift.
   *                A zero shift leaves the sequence unchanged.
   */
  public static void circularShift( double[] y, int shift ) {
    
    int N = y.length;
    int s = shift % N;
    
    // minimize shift - consider alternative shift in opposite direction
    
    if ( s > 0  &&  N-s < s ) 
      s -= N;
    else if ( s < 0  &&  N+s < -s ) 
      s += N;
    
    // right shift
    
    double[] tmp = new double[ Math.abs( s ) ];

    if ( s > 0 ) {
      for ( int i = 0;  i < s;  i++ ) 
        tmp[i] = y[N-s+i];
      for ( int i = N-1-s;  i >= 0;  i-- ) 
        y[i+s] = y[i];
      for ( int i = 0;  i < s;  i++ ) 
        y[i] = tmp[i];
    }
    
    // left shift
    
    if ( s < 0 ) {
      for ( int i = 0;  i < -s;  i++ )
        tmp[i] = y[i];
      for ( int i = -s;  i < N;  i++ )
        y[i+s] = y[i];
      for ( int i = 0;  i < -s;  i++ ) 
        y[N+s+i] = tmp[i];
    }
    
    
  }
  
  
  
  /**
   * Performs a circular shift on this sequence, in-place
   *
   * @param shift     int specifying the size and direction of the shift.  A negative number
   *                  specifies a left shift and a positive number, a right shift.  A zero shift
   *                  leaves the sequence unchanged.
   */
  public void circularShift( int shift ) {
    circularShift( x, shift );
  }
  
  
  
  /**
   * Shifts a sequence left or right and pads with zeros (unlike the circular shift, sequence values are lost).
   *
   * @param y       double[] containing the sequence to be shifted.
   * @param shift   int specifying the direction and size of the shift.  A negative shift is to the left.
   *                Zeros are fed in from the right in that case.  A positive shift is to the right.  Zeros
   *                are fed in from the left in that case.  A zero shift leaves the sequence unchanged.
   */
  public static void zeroShift( double[] y, int shift ) {
    
    if ( Math.abs( shift) >= y.length )
      Arrays.fill( y, 0.0f );
    
    else if ( shift > 0 ) {
      for ( int i = y.length-1;  i >= shift;  i-- )
        y[i] = y[i-shift];
      for ( int i = 0;  i < shift;  i++ ) 
        y[i] = 0.0f;
    }
    
    else if ( shift < 0 ) {
      for ( int i = 0;  i < y.length+shift;  i++ ) 
        y[i] = y[i-shift];
      for ( int i = y.length+shift;  i < y.length;  i++ ) 
        y[i] = 0.0f;
    }
    
  }
  
  
  
  /**
   * Performs a shift on this sequence with zero-fill.
   *
   * @param shift   int specifying the direction and size of the shift.  A negative shift is to the left.
   *                Zeros are fed in from the right in that case.  A positive shift is to the right.  Zeros
   *                are fed in from the left in that case.  A zero shift leaves the sequence unchanged.
   */
  public void zeroShift( int shift ) {
    zeroShift( x, shift );
  }
  
  
  
  /**
   * Decimates a sequence by a specified rate.
   *
   * @param y            double[] containing the sequence to be decimated.
   * @param decrate      int specifying the decimation rate.
   * @param ydecimated   double[] containing the decimated sequence.  
   */
  public static void decimate( double[] y, int decrate, double[] ydecimated ) {
    int n = Math.min( ydecimated.length, y.length/decrate );
    for ( int i = 0;  i < n;  i++ ) ydecimated[i] = y[i*decrate];
  }
  
  
  
  /**
   * Decimates this sequence in-place.
   *
   * @param decrate   int specifying the decimation rate.
   */
  public void decimate( int decrate ) {
    double[] tmp = new double[ x.length/decrate ];
    decimate( x, decrate, tmp );
    x = tmp;
  }
  
  
  
  /**
   * Stretches a sequence by a specified rate.
   * 
   * This operation spreads the sequence values out and fills between them with interstitial zeros.
   * It is a basic operation needed for interpolation by an integer rate.
   *
   * @param y            double[] containing the sequence to be stretched.
   * @param rate         int containing the stretch rate.
   * @param ystretched   double[] containing the stretched sequence.
   */
  public static void stretch( double[] y, int rate, double[] ystretched ) {
    int n = Math.min( y.length, ystretched.length/rate );
    Arrays.fill( ystretched, 0.f );
    for ( int i = 0;  i < n;  i++ ) ystretched[i*rate] = y[i];
  }
  
  
  
  /**
   * Stretches this sequence by a specified rate, in place.
   *
   * This operation spreads the sequence values out and fills between them with interstitial zeros.
   * It is a basic operation needed for interpolation by an integer rate.
   * 
   * @param rate     int containing the stretch rate (factor).
   */
  public void stretch( int rate ) {
    double[] tmp = new double[ x.length*rate ];
    stretch( x, rate, tmp );
    x = tmp;
  }
  
  

  /**
   * Multiplies a sequence by a constant.
   *
   * @param y       double[] containing the sequence to be scaled.
   * @param f       double containing the multiplicative constant.
   */
  public static void timesEquals( double[] y, double f ) {
    for ( int i = 0;  i < y.length;  i++ ) y[i] *= f;
  }
  
  
  
  /**
   * Multiplies this sequence by a constant, in-place.
   *
   * @param f   double specifying the multiplicative constant.
   */
  public void timesEquals( double f ) {
    timesEquals( x, f );
  }
  
  
  
  /**
   * Pad a sequence with zeros (on the right)
   * 
   * If ypadded.length < y.length, this method performs a truncation of y.
   * 
   * @param y           double[] containing original sequence
   * @param ypadded     double[] containing padded sequence
   */
  public static void pad( double[] y, double[] ypadded ) {
    if ( y.length < ypadded.length ) {
      Arrays.fill( ypadded, 0.0f );
      System.arraycopy( y, 0, ypadded, 0, y.length );
    }
    else {
      System.arraycopy( y, 0, ypadded, 0, ypadded.length );
    }
  }
  
  
  
  /**
   * Pads this sequence to length n, by zero filling on right if n > length of this sequence, no-op otherwise.
   * 
   * @param n           int specifying desired length of padded sequence
   */
  public void pad( int n ) {
    if ( n > x.length ) {
      double[] tmp = new double[ n ];
      pad( x, tmp );
      x = tmp;
    }
  }
  
}
