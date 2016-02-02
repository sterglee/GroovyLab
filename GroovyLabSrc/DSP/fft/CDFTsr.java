

package DSP.fft;


/**
 * Package-private class implementing an arbitrary power-of-two length complex DFT with the split radix algorithm.
 * Creates smaller CDFTsr instances recursively and calls these in the evaluation.
 */
class CDFTsr {
  
  /** Constant twiddle factor for N/2 butterfly. */
  private static final double SQRT2BY2 = (double) (Math.sqrt( 2.0 ) / 2.0); 

  /** double[] pair specifying complex input sequence. */
  protected double[] xr, xi;
  
  /** double[] pair specifying complex output transform. */
  protected double[] Xr, Xi;

  /** int specifying offset into the top-level length-N sequence arrays. */
  protected int     xoffset;
  
  /** int specifying the stride of the butterflies at this level. */
  protected int     xstride;
  
  /** int specifying the offset into the top-level length-N transform arrays. */
  protected int     Xoffset;
  
  /** Log base 2 of the length of this DFT. */
  protected int     m;
  
  /** int specifying the length of this transform. */
  protected int     N;
  
  /** N/8. */
  protected int     N8;
  
  /** N/4. */
  protected int     N4;

  /** CDFTsr instances created recursively from this level. */
  private CDFTsr    dft1, dft2, dft3;
  
  /** double[] containing a reference to the cos(x) table. */
  private double[]   c;
  
  /** double[] containing a reference to the cos(3*x) table. */
  private double[]   c3;
  
  /** double[] containing a reference to the sin(x) table. */
  private double[]   s;
  
  /** double[] containing a reference to the sin(3*x) table. */
  private double[]   s3;
  
  /** int factor specifying stride in cos/sin tables. */
  private int       f;
  
  /** int specifying offset into cos/sin tables. */
  private int       reflect;



  /**
   * Instantiates a top-level CDFTsr.
   *
   * @param m    int specifying the log base 2 of the length of this CDFTsr.
   * @param c    reference to the cos(x) table.
   * @param c3   reference to the cos(3*x) table.
   * @param s    reference to the sin(x) table.
   * @param s3   reference to the sin(3*x) table.
   */
  CDFTsr( int m, double[] c, double[] c3, double[] s, double[] s3 ) {

    this.m = m;
    N = 1 << m;
    N8 = N / 8;
    N4 = N / 4;
    xoffset = 0;
    xstride = 1;
    Xoffset = 0;

    this.c  = c;
    this.c3 = c3;
    this.s  = s;
    this.s3 = s3;
    
    f       = 1;
    reflect = 2*c.length;

    if ( m > 6 ) {
      dft1 = new CDFTsr( this, 0, 2, 0, m-1 );
      dft2 = new CDFTsr( this, 1, 4, N/2, m-2 );
      dft3 = new CDFTsr( this, 3, 4, 3*N/4, m-2 );
    }
    else if ( m == 6 ) {
      dft1 = new CDFTsr( this, 0, 2, 0, 5 );
      dft2 = new CDFTsr16(     1, 4, N/2 );
      dft3 = new CDFTsr16(     3, 4, 3*N/4 );
    }
    else if ( m == 5 ) {
      dft1 = new CDFTsr16(     0, 2, 0 );
      dft2 = new CDFTsr8(      1, 4, N/2 );
      dft3 = new CDFTsr8(      3, 4, 3*N/4 );
    }

  }



  /**
   * Default constructor.
   */
  protected CDFTsr() {
    dft1 = null;
    dft2 = null;
    dft3 = null;
  }



  /**
   * Instantiates a new CDFTsr - called by a parent CDFTsr.
   *
   * @param parent          CDFTsr reference to the parent
   * @param dataOffset      int specifying the offset into the top-level data arrays.
   * @param dataStride      int specifying the stride of butterflies in the top-level data arrays.
   * @param transformOffset int specifying the offset of this transform into the top-level transform arrays.
   * @param m               int specifying the log base 2 of the length of this DFT.
   */
  protected CDFTsr( CDFTsr parent, int dataOffset, int dataStride, int transformOffset, int m ) {

    c  = parent.c;
    c3 = parent.c3;
    s  = parent.s;
    s3 = parent.s3;

    this.m = m;
    N = 1 << m;
    N8 = N / 8;
    N4 = N / 4;
    this.xoffset = dataOffset;
    this.xstride = dataStride;
    this.Xoffset = transformOffset;

    f       = c.length / N8;
    reflect = 2*c.length;

    if ( m > 6 ) {
      dft1 = new CDFTsr( this, dataOffset,                dataStride*2, transformOffset,         m-1 );
      dft2 = new CDFTsr( this, dataOffset + dataStride,   dataStride*4, transformOffset + N/2,   m-2 );
      dft3 = new CDFTsr( this, dataOffset + 3*dataStride, dataStride*4, transformOffset + 3*N/4, m-2 );
    }
    else if ( m == 6 ) {
      dft1 = new CDFTsr(   this, dataOffset,                dataStride*2, transformOffset,       5 );
      dft2 = new CDFTsr16(       dataOffset + dataStride,   dataStride*4, transformOffset + N/2    );
      dft3 = new CDFTsr16(       dataOffset + 3*dataStride, dataStride*4, transformOffset + 3*N/4  );
    }
    else if ( m == 5 ) {
      dft1 = new CDFTsr16(       dataOffset,                dataStride*2, transformOffset );
      dft2 = new CDFTsr8(        dataOffset + dataStride,   dataStride*4, transformOffset + N/2 );
      dft3 = new CDFTsr8(        dataOffset + 3*dataStride, dataStride*4, transformOffset + 3*N/4 );
    }

  }
  
  
  
  /**
   * Links the user-supplied input sequence and output transform arrays.  Propagates the links to child DFTs.
   * 
   * @param xr  double[] containing the input sequence real part.
   * @param xi  double[] containing the input sequence imaginary part.
   * @param Xr  double[] containing the output sequence real part.
   * @param Xi  double[] containing the output sequence imaginary part.
   */
  void link( double[] xr, double[] xi, double[] Xr, double[] Xi ) {
    this.xr = xr;
    this.xi = xi;
    this.Xr = Xr;
    this.Xi = Xi;
    dft1.link( xr, xi, Xr, Xi );
    dft2.link( xr, xi, Xr, Xi );
    dft3.link( xr, xi, Xr, Xi );
  }



  /**
   * Evaluates the complex DFT.
   */
  void evaluate() {

    double T1r, T1i, T3r, T3i;
    double Rr, Ri, Sr, Si;
    double Wr, Wi;

    dft1.evaluate();
    dft2.evaluate();
    dft3.evaluate();

      // k = 0 butterfly

    int kp = Xoffset;
    int kpN4 = kp + N4;
    int kpN2 = kpN4 + N4;
    int kp3N4 = kpN2 + N4;

    Rr = Xr[kpN2] + Xr[kp3N4];
    Ri = Xi[kpN2] + Xi[kp3N4];
    Sr = Xi[kp3N4] - Xi[kpN2];
    Si = Xr[kpN2] - Xr[kp3N4];

    Xr[kpN2] = Xr[kp] - Rr;
    Xi[kpN2] = Xi[kp] - Ri;
    Xr[kp3N4] = Xr[kpN4] + Sr;
    Xi[kp3N4] = Xi[kpN4] + Si;

    Xr[kp] += Rr;
    Xi[kp] += Ri;
    Xr[kpN4] -= Sr;
    Xi[kpN4] -= Si;

    // k = 1 through N8-1 butterflies

    int fk;

    for ( int k = 1; k < N8; k++ ) {

      fk = f * k;
      kp = k + Xoffset;
      kpN4 = kp + N4;
      kpN2 = kpN4 + N4;
      kp3N4 = kpN2 + N4;

      // T1 = Wk*O1
      // T3 = W3k*O3

      Wr = c[fk];
      Wi = s[fk];
      T1r = Wr * Xr[kpN2] - Wi * Xi[kpN2];
      T1i = Wr * Xi[kpN2] + Wi * Xr[kpN2];
      Wr = c3[fk];
      Wi = s3[fk];
      T3r = Wr * Xr[kp3N4] - Wi * Xi[kp3N4];
      T3i = Wr * Xi[kp3N4] + Wi * Xr[kp3N4];

      // R = T1 + T3
      // S = i*(T1 - T3)

      Rr = T1r + T3r;
      Ri = T1i + T3i;
      Sr = T3i - T1i;
      Si = T1r - T3r;

      Xr[kpN2] = Xr[kp] - Rr;
      Xi[kpN2] = Xi[kp] - Ri;
      Xr[kp3N4] = Xr[kpN4] + Sr;
      Xi[kp3N4] = Xi[kpN4] + Si;

      Xr[kp] += Rr;
      Xi[kp] += Ri;
      Xr[kpN4] -= Sr;
      Xi[kpN4] -= Si;
    }
    
    // k = N/8 butterfly
    
    kp    = N8   + Xoffset;
    kpN4  = kp   + N4;
    kpN2  = kpN4 + N4;
    kp3N4 = kpN2 + N4;

    // T1 = Wk*O1
    // T3 = W3k*O3

    T1r = SQRT2BY2 * ( Xr[kpN2] + Xi[kpN2] );
    T1i = SQRT2BY2 * ( Xi[kpN2] - Xr[kpN2] );

    T3r =  SQRT2BY2 * ( Xi[kp3N4] - Xr[kp3N4] );
    T3i = -SQRT2BY2 * ( Xi[kp3N4] + Xr[kp3N4] );
    
    // R = T1 + T3
    // S = i*(T1 - T3)

    Rr = T1r + T3r;
    Ri = T1i + T3i;
    Sr = T3i - T1i;
    Si = T1r - T3r;

    Xr[kpN2] = Xr[kp] - Rr;
    Xi[kpN2] = Xi[kp] - Ri;
    Xr[kp3N4] = Xr[kpN4] + Sr;
    Xi[kp3N4] = Xi[kpN4] + Si;

    Xr[kp] += Rr;
    Xi[kp] += Ri;
    Xr[kpN4] -= Sr;
    Xi[kpN4] -= Si;

    // k = N/8+1 through N/4-1 butterflies
    
    for ( int k = N8+1; k < N4; k++ ) {

      fk = reflect - f * k;
      kp    = k    + Xoffset;
      kpN4  = kp   + N4;
      kpN2  = kpN4 + N4;
      kp3N4 = kpN2 + N4;

      // T1 = Wk*O1
      // T3 = W3k*O3
      
      Wr = -s[fk];
      Wi = -c[fk];
      T1r = Wr * Xr[kpN2] - Wi * Xi[kpN2];
      T1i = Wr * Xi[kpN2] + Wi * Xr[kpN2];
      Wr = s3[fk];
      Wi = c3[fk];
      T3r = Wr * Xr[kp3N4] - Wi * Xi[kp3N4];
      T3i = Wr * Xi[kp3N4] + Wi * Xr[kp3N4];

      // R = T1 + T3
      // S = i*(T1 - T3)

      Rr = T1r + T3r;
      Ri = T1i + T3i;
      Sr = T3i - T1i;
      Si = T1r - T3r;

      Xr[kpN2] = Xr[kp] - Rr;
      Xi[kpN2] = Xi[kp] - Ri;
      Xr[kp3N4] = Xr[kpN4] + Sr;
      Xi[kp3N4] = Xi[kpN4] + Si;

      Xr[kp] += Rr;
      Xi[kp] += Ri;
      Xr[kpN4] -= Sr;
      Xi[kpN4] -= Si;
    }

  }

}
