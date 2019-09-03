
package DSP.fft;


/**
 * Package-private class implementing a length-16 complex DFT with a split-radix algorithm.
 */
class CDFTsr16 extends CDFTsr {

  /** Constant twiddle factor  */
  static final double C_1_16   = (double) Math.cos(2.0 * Math.PI / 16);
  
  /** Constant twiddle factor */
  static final double C_3_16   = (double) Math.cos(2.0 * Math.PI*3.0 / 16);
  
  /** Constant twiddle factor */
  static final double SQRT2BY2 = (double) ( Math.sqrt(2.0)/2.0 );


  /** Input sequence indices */
  private int n0, n1, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15;
  
  /** Output transform indices */
  private int m0, m1, m2, m3, m4, m5, m6, m7, m8, m9, m10, m11, m12, m13, m14, m15;


  /**
   * Instantiates a new CDFTsr16 instance.
   *
   * @param dataOffset       int specifying offset into the top-level length-N sequence array.
   * @param dataStride       int specifying the stride of butterflies into the top-level length-N sequence array.
   * @param transformOffset  int specifying the offset into the length-N transform array.
   */
  CDFTsr16( int dataOffset, int dataStride, int transformOffset ) {

     m = 4;
     N = 16;
     xoffset = dataOffset;
     xstride = dataStride;
     Xoffset = transformOffset;

     n0 = xoffset;
     n1 = n0 + xstride;
     n2 = n1 + xstride;
     n3 = n2 + xstride;
     n4 = n3 + xstride;
     n5 = n4 + xstride;
     n6 = n5 + xstride;
     n7 = n6 + xstride;
     n8 = n7 + xstride;
     n9 = n8 + xstride;
     n10 = n9 + xstride;
     n11 = n10 + xstride;
     n12 = n11 + xstride;
     n13 = n12 + xstride;
     n14 = n13 + xstride;
     n15 = n14 + xstride;

     m0 = Xoffset;
     m1 = m0 + 1;
     m2 = m1 + 1;
     m3 = m2 + 1;
     m4 = m3 + 1;
     m5 = m4 + 1;
     m6 = m5 + 1;
     m7 = m6 + 1;
     m8 = m7 + 1;
     m9 = m8 + 1;
     m10 = m9 + 1;
     m11 = m10 + 1;
     m12 = m11 + 1;
     m13 = m12 + 1;
     m14 = m13 + 1;
     m15 = m14 + 1;

  }
  
  
  
  /** 
   * Links the user-supplied input sequence and output transform arrays.
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
  }
  
  
  
  /** 
   * Evaluates the length-16 complex DFT.
   */
  void evaluate() {
    
    double T1r, T1i, T3r, T3i; 
    double Rr, Ri, Sr, Si;


// Length 2 DFT

    Xr[m0] = xr[n0] + xr[n8];
    Xi[m0] = xi[n0] + xi[n8];
    Xr[m1] = xr[n0] - xr[n8];
    Xi[m1] = xi[n0] - xi[n8];

  // length 4 dft


  // k = 0 butterfly

    Rr = xr[n4]  + xr[n12];
    Ri = xi[n4]  + xi[n12];
    Sr = xi[n12] - xi[n4];
    Si = xr[n4]  - xr[n12];
  
    Xr[m2] = Xr[m0] - Rr;
    Xi[m2] = Xi[m0] - Ri;
    Xr[m3] = Xr[m1] + Sr;
    Xi[m3] = Xi[m1] + Si;
  
    Xr[m0] += Rr;
    Xi[m0] += Ri;
    Xr[m1] -= Sr;
    Xi[m1] -= Si;


// Length 2 DFT

    Xr[m4] = xr[n2] + xr[n10];
    Xi[m4] = xi[n2] + xi[n10];
    Xr[m5] = xr[n2] - xr[n10];
    Xi[m5] = xi[n2] - xi[n10];

// Length 2 DFT

    Xr[m6] = xr[n6] + xr[n14];
    Xi[m6] = xi[n6] + xi[n14];
    Xr[m7] = xr[n6] - xr[n14];
    Xi[m7] = xi[n6] - xi[n14];



  // length 8 dft


  // k = 0 butterfly

    Rr = Xr[m4]  + Xr[m6];
    Ri = Xi[m4]  + Xi[m6];
    Sr = Xi[m6]  - Xi[m4];
    Si = Xr[m4]  - Xr[m6];
  
    Xr[m4] = Xr[m0] - Rr;
    Xi[m4] = Xi[m0] - Ri;
    Xr[m6] = Xr[m2] + Sr;
    Xi[m6] = Xi[m2] + Si;
  
    Xr[m0] += Rr;
    Xi[m0] += Ri;
    Xr[m2] -= Sr;
    Xi[m2] -= Si;


  // all other butterflies


  // T1 = Wk*O1
  // T3 = W3k*O3

    T1r =  SQRT2BY2 * ( Xr[m5] + Xi[m5] );
    T1i =  SQRT2BY2 * ( Xi[m5] - Xr[m5] );
    T3r =  SQRT2BY2 * ( Xi[m7] - Xr[m7] );
    T3i = -SQRT2BY2 * ( Xi[m7] + Xr[m7] );

  // R = T1 + T3
  // S = i*(T1 - T3)

    Rr = T1r + T3r;
    Ri = T1i + T3i;
    Sr = T3i - T1i;
    Si = T1r - T3r;

    Xr[m5] = Xr[m1] - Rr;
    Xi[m5] = Xi[m1] - Ri;
    Xr[m7] = Xr[m3] + Sr;
    Xi[m7] = Xi[m3] + Si;

    Xr[m1] += Rr;
    Xi[m1] += Ri;
    Xr[m3] -= Sr;
    Xi[m3] -= Si;


// Length 2 DFT

    Xr[m8] = xr[n1] + xr[n9];
    Xi[m8] = xi[n1] + xi[n9];
    Xr[m9] = xr[n1] - xr[n9];
    Xi[m9] = xi[n1] - xi[n9];

  // length 4 dft


  // k = 0 butterfly

    Rr = xr[n5]  + xr[n13];
    Ri = xi[n5]  + xi[n13];
    Sr = xi[n13] - xi[n5];
    Si = xr[n5]  - xr[n13];
  
    Xr[m10] = Xr[m8] - Rr;
    Xi[m10] = Xi[m8] - Ri;
    Xr[m11] = Xr[m9] + Sr;
    Xi[m11] = Xi[m9] + Si;
  
    Xr[m8] += Rr;
    Xi[m8] += Ri;
    Xr[m9] -= Sr;
    Xi[m9] -= Si;


// Length 2 DFT

    Xr[m12] = xr[n3] + xr[n11];
    Xi[m12] = xi[n3] + xi[n11];
    Xr[m13] = xr[n3] - xr[n11];
    Xi[m13] = xi[n3] - xi[n11];


  
  // length 4 dft


  // k = 0 butterfly

    Rr = xr[n7]  + xr[n15];
    Ri = xi[n7]  + xi[n15];
    Sr = xi[n15] - xi[n7];
    Si = xr[n7]  - xr[n15];
  
    Xr[m14] = Xr[m12] - Rr;
    Xi[m14] = Xi[m12] - Ri;
    Xr[m15] = Xr[m13] + Sr;
    Xi[m15] = Xi[m13] + Si;
  
    Xr[m12] += Rr;
    Xi[m12] += Ri;
    Xr[m13] -= Sr;
    Xi[m13] -= Si;



  // length 16 dft


  // k = 0 butterfly

    Rr = Xr[m8]  + Xr[m12];
    Ri = Xi[m8]  + Xi[m12];
    Sr = Xi[m12] - Xi[m8];
    Si = Xr[m8]  - Xr[m12];
  
    Xr[m8]  = Xr[m0] - Rr;
    Xi[m8]  = Xi[m0] - Ri;
    Xr[m12] = Xr[m4] + Sr;
    Xi[m12] = Xi[m4] + Si;
  
    Xr[m0] += Rr;
    Xi[m0] += Ri;
    Xr[m4] -= Sr;
    Xi[m4] -= Si;


  // all other butterflies

  // k = 1
  // T1 = Wk*O1   
  // T3 = W3k*O3

    T1r = C_1_16 * Xr[m9]  + C_3_16 * Xi[m9];
    T1i = C_1_16 * Xi[m9]  - C_3_16 * Xr[m9];
    T3r = C_3_16 * Xr[m13] + C_1_16 * Xi[m13];
    T3i = C_3_16 * Xi[m13] - C_1_16 * Xr[m13];

  // R = T1 + T3
  // S = i*(T1 - T3)

    Rr = T1r + T3r;
    Ri = T1i + T3i;
    Sr = T3i - T1i;
    Si = T1r - T3r;

    Xr[m9] = Xr[m1] - Rr;
    Xi[m9] = Xi[m1] - Ri;
    Xr[m13] = Xr[m5] + Sr;
    Xi[m13] = Xi[m5] + Si;

    Xr[m1] += Rr;
    Xi[m1] += Ri;
    Xr[m5] -= Sr;
    Xi[m5] -= Si;

  // k = 2
  // T1 = Wk*O1
  // T3 = W3k*O3

    T1r =  SQRT2BY2 * ( Xr[m10] + Xi[m10] );
    T1i =  SQRT2BY2 * ( Xi[m10] - Xr[m10] );
    T3r =  SQRT2BY2 * ( Xi[m14] - Xr[m14] );
    T3i = -SQRT2BY2 * ( Xi[m14] + Xr[m14] );

  // R = T1 + T3
  // S = i*(T1 - T3)

    Rr = T1r + T3r;
    Ri = T1i + T3i;
    Sr = T3i - T1i;
    Si = T1r - T3r;

    Xr[m10] = Xr[m2] - Rr;
    Xi[m10] = Xi[m2] - Ri;
    Xr[m14] = Xr[m6] + Sr;
    Xi[m14] = Xi[m6] + Si;

    Xr[m2] += Rr;
    Xi[m2] += Ri;
    Xr[m6] -= Sr;
    Xi[m6] -= Si;

  // k = 3
  // T1 = Wk*O1
  // T3 = W3k*O3

    T1r =  C_3_16 * Xr[m11] + C_1_16 * Xi[m11];
    T1i =  C_3_16 * Xi[m11] - C_1_16 * Xr[m11];
    T3r = -C_1_16 * Xr[m15] - C_3_16 * Xi[m15];
    T3i = -C_1_16 * Xi[m15] + C_3_16 * Xr[m15];

  // R = T1 + T3
  // S = i*(T1 - T3)

    Rr = T1r + T3r;
    Ri = T1i + T3i;
    Sr = T3i - T1i;
    Si = T1r - T3r;

    Xr[m11] = Xr[m3] - Rr;
    Xi[m11] = Xi[m3] - Ri;
    Xr[m15] = Xr[m7] + Sr;
    Xi[m15] = Xi[m7] + Si;

    Xr[m3] += Rr;
    Xi[m3] += Ri;
    Xr[m7] -= Sr;
    Xi[m7] -= Si;
    
  }

}
