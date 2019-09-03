
package groovySci.FFT;

import NROps.NROps;
import com.nr.Complex;
import gExec.Interpreter.GlobalValues;
import groovySci.math.array.Vec;
import java.io.IOException;
import java.text.DecimalFormat;

public class  FFTNR {

 public static  double  log2(double x)  {
    double  conv = java.lang.Math.log(2.0);
    return java.lang.Math.log(x)/conv;
}

  /**
   * Calculates the Fourier transform of a set of n real-valued data points.
   * Returns the positive frequency half of their complex Fourier transform. 
   * does not destroy data, and outputs FFT in realffts and imffts arrays
   *  n must be a power of 2. 
   */
  
  public static FFTResults  fft( Vec   data)  {
      return fft(data.getv());
  }

  public static FFTResults  fft( double [][]  data)  {
      if (data[0].length > data.length)
          return fft(data[0]);
      else
      {
          double [] y = new double[data.length];
          for (int row=0; row<data.length; row++)
              y[row] = data[row][0];
          return fft(y);
      }
  }
      
  
  public static FFTResults  fft( double []  data)  {
    int  N = data.length;
       
    double []  paddedData = data; // we will actually pad only for signal length not a power of 2
    int  p2N = (int) java.lang.Math.ceil(log2(N));
    int  newN =  (int) java.lang.Math.pow(2, p2N);
    
    if (newN != N) { // not a power of two, zero pad
      paddedData = new double[newN];
     int  k=0;
     while  (k<N) {
       paddedData[k] = data[k];
       k++;
     }
     // zero-pad
     while (k<newN) {
       paddedData[k] = 0.0;
       k++;
      }
    } 
         
    N = paddedData.length;
    int  N2 = (int) (N/2.0);
    double []  realffts = new double[N2];
    double []  imffts = new double[N2];
    double []  cpdata =NR.Common.copy(paddedData);  // copy the input data
  
    /*
    realft() replaces cpdata[0..2*n-1] by its discrete Fourier transform, if isign is
    input as 1; or replaces cpdata[0..2*n-1] by n times its inverse discrete
    Fourier transform, if isign is input as 1. data is a complex array of
    length n stored as a real array of length 2*n. n must be an integer power of 2.
   */
    com.nr.fft.FFT.realft(cpdata, 1);  // perform the FFT using the realft() Numerical Recipes routine
    
    // 
    int  cnt = 0;
    int   k = 0;
    while  ( k < N) {
        realffts[cnt] = cpdata[k];
        imffts[cnt] = cpdata[k+1];
        k += 2;
        cnt++;
    }
   FFTResults resultFFT = new FFTResults();
    resultFFT.realFFTs = realffts;
    resultFFT.imFFTs = imffts;
    
    return resultFFT;
  }

      
public static FFTResults  cfft( double []  data)  {
    int  N = data.length;
       
    double []  paddedData = data; // we will actually pad only for signal length not a power of 2
    int  p2N = (int) java.lang.Math.ceil(log2(N));
    int  newN =  (int) java.lang.Math.pow(2, p2N);
    
    if (newN != N) { // not a power of two, zero pad
      paddedData = new double[newN];
     int  k=0;
     while  (k<N) {
       paddedData[k] = data[k];
       k++;
     }
     // zero-pad
     while (k<newN) {
       paddedData[k] = 0.0;
       k++;
      }
    } 
         
    N = paddedData.length;
    int  N2 = (int) (N/2.0);
    double []  realffts = new double[N2];
    double []  imffts = new double[N2];
    double []  cpdata =NR.Common.copy(paddedData);  // copy the input data
  /**
   * Replaces cpdata[0..2*n-1] by its discrete Fourier transform, if isign is
   * input as 1; or replaces cpdata[0..2*n-1] by n times its inverse discrete
   * Fourier transform, if isign is input as 1. data is a complex array of
   * length n stored as a real array of length 2*n. n must be an integer power of 2.
   */
    NROps  nropsObj = gExec.Interpreter.NativeLibsObj.nrObj;
    nropsObj.cnrfft(cpdata, N, 0);
    
    //com.nr.fft.FFT.realft(cpdata, 1);  // perform the FFT
    int  cnt = 0;
    int   k = 0;
    while  ( k < N) {
        realffts[cnt] = cpdata[k];
        imffts[cnt] = cpdata[k+1];
        k += 2;
        cnt++;
    }
   FFTResults resultFFT = new FFTResults();
    resultFFT.realFFTs = realffts;
    resultFFT.imFFTs = imffts;
    
    return resultFFT;
  }

      // return also the frequency axis, when the Sampling Frequency is passed
  public static FFTResults fft(Vec data, double SFreq) {
      return fft(data.getv(), SFreq);
  }
  
  public static FFTResults  fft(double [] data, double  SFreq)  {
    int   N = data.length;
    
    double [] paddedData = data;  //  we will actually pad only for signal length not a power of 2 
    int  p2N = (int) java.lang.Math.ceil(log2(N));
    int  newN = (int) java.lang.Math.pow(2, p2N);
    
    if (newN != N)  { // not a power of two, zero pad
        paddedData = new double[newN];
        int  k=0;
        while (k<N)  {
          paddedData[k] = 0.0;
          k++;
          }
       }
   
    N = paddedData.length;
    int   N2 = (int) (N/2.0);
    double  Delta = 1.0/SFreq;  // the sampling interval
    double  ND = N * Delta;
    double [] freqs = new double[N2];  // the positive frequency axis half of the complex Fourier Transform
    double [] realffts = new double[N2];
    double [] imffts = new double[N2];
    double [] cpdata = NR.Common.copy(data);  // copy the input data
    com.nr.fft.FFT.realft(cpdata, 1);  // perform the FFT
    int   cnt = 0;
    int   k = 0;
    //positive frequencies
    while  ( k <= N2) {
        realffts[cnt] = cpdata[k];
        imffts[cnt] = cpdata[k+1];
        freqs[cnt] = cnt/ND;
        k += 2;
        cnt++;
    }
    
    FFTResults resultFFT = new FFTResults();
    resultFFT.realFFTs = realffts;
    resultFFT.imFFTs = imffts;
    resultFFT.freqs = freqs;
    
    return resultFFT;
    
  }
  

public static double []  ifft(double [] realffts,  double [] imffts)   {
  int  M = realffts.length;
  int  N = M*2;
  double []  rd = new double[N];
  int k = 0;
  int k2 = 0;
  while (k < M) {
    rd[k2] = realffts[k];
    rd[k2+1] = imffts[k];
    k++;
    k2+=2;
  }
  com.nr.fft.FFT.realft(rd, -1);  // perform the inverse FFT
  
    // normalize properly
  int Nd2 = N/2;
  k=0;
  while (k<N) {
    rd[k] /= Nd2;
    k++;
  }
  return rd;
}
  
public static void   four1(double [] data,  int  n,  int isign)   {
   com.nr.fft.FFT.four1(data, n, isign);
}

 public static void four1(double [] data,  int isign)  {
    com.nr.fft.FFT.four1(data, isign);
  }


  /**
   * Calculates the Fourier transform of a set of n real-valued data points.
   * Replaces these data (which are stored in array data[0..n-1]) by the
   * positive frequency half of their complex Fourier transform. The real-valued
   * first and last components of the complex transform are returned as elements
   * data[0] and data[1], respectively. n must be a power of 2. This routine
   * also calculates the inverse transform of a complex data array if it is the
   * transform of real data. (Result in this case must be multiplied by 2/n.)
   */
public static void  realft(double [] data, int isign)  {
  com.nr.fft.FFT.realft(data, isign);
}

  /**
   * Calculates the sine transform of a set of n real-valued data points stored
   * in array y[0..n-1]. The number n must be a power of 2. On exit, y is
   * replaced by its transform. This program, without changes, also calculates
   * the inverse sine transform, but in this case the output array should be
   * multiplied by 2/n.
   */
public static void  sinft(double [] y)  {
  com.nr.fft.FFT.sinft(y);
}

  
  /**
   * Calculates the cosine transform of a set y[0..n] of real-valued data
   * points. The transformed data replace the original data in array y. n must
   * be a power of 2. This program, without changes, also calculates the inverse
   * cosine transform, but in this case the output array should be multiplied by
   * 2/n.
   */

  public static void cosft1( double [] y)  {
    com.nr.fft.FFT.cosft1(y);
  }
  
  
  /**
   * Calculates the "staggered" cosine transform of a set y[0..n-1] of
   * real-valued data points. The transformed data replace the original data in
   * array y. n must be a power of 2. Set isign to C1 for a transform, and to 1
   * for an inverse transform. For an inverse transform, the output array should
   * be multiplied by 2/n.
   */
  public static void  cosft2(double [] y, int isign)   {
    com.nr.fft.FFT.cosft2(y, isign);
  }
    
  
  /**
   * Replaces data by its ndim-dimensional discrete Fourier transform, if isign
   * is input as 1. nn[0..ndim-1] is an integer array containing the lengths of
   * each dimension (number of com- plex values), which must all be powers of 2.
   * data is a real array of length twice the product of these lengths, in which
   * the data are stored as in a multidimensional complex array: real and
   * imaginary parts of each element are in consecutive locations, and the
   * rightmost index of the array increases most rapidly as one proceeds along
   * data. For a two-dimensional array, this is equivalent to storing the array
   * by rows. If isign is input as 1, data is replaced by its inverse transform
   * times the product of the lengths of all dimensions.
   * 
   */
 
  public static void fourn(double [] data,  int [] nn,  int isign )  {
    com.nr.fft.FFT.fourn(data, nn, isign);
  }
  
  /**
   * Given a three-dimensional real array data[0..nn1-1][0..nn2-1][0..nn3-1]
   * (where nn1 D 1 for the case of a logically two-dimensional array), this
   * routine returns (for isign=1) the complex fast Fourier transform as two
   * complex arrays: On output, data contains the zero and positive frequency
   * values of the third frequency component, while speq[0..nn1-1][0..2*nn2-1]
   * con- tains the Nyquist critical frequency values of the third frequency
   * component. First (and sec- ond) frequency components are stored for zero,
   * positive, and negative frequencies, in standard wraparound order. See text
   * for description of how complex values are arranged. For isign=-1, the
   * inverse transform (times nn1*nn2*nn3/2 as a constant multiplicative factor)
   * is performed, with output data (viewed as a real array) deriving from input
   * data (viewed as complex) and speq. For inverse transforms on data not
   * generated first by a forward transform, make sure the complex input data
   * array satisfies property (12.6.2). The dimensions nn1, nn2, nn3 must always
   * be integer powers of 2.
   * 
   */
  
  public static void rlft3(double [] data, double[]  speq, int isign, int  nn1, int nn2, int nn3) {
    com.nr.fft.FFT.rlft3(data, speq, isign, nn1, nn2, nn3);
  }
  
  public static void  rlft3(double [][] data,  double []speq, int isign)  {
    com.nr.fft.FFT.rlft3(data, speq, isign);
  }
  
 public static void   rlft3(double [][][] data, double [][] speq, int  isign)  {
   com.nr.fft.FFT.rlft3(data, speq, isign);
 }  

    
   // @param file
   // @param nn
   // @param isign
   // @throws IOException
  public static void  fourfs( java.nio.channels.FileChannel [] file,  int [] nn,  int isign )  {
        try {
            com.nr.fft.FFT.fourfs(file, nn, isign);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
  }
  

// print all  the FFT results
public static void  printft( double [] data)  {
    DecimalFormat  fiveDigit = new DecimalFormat("0.00000E0");
    int  L = data.length;
    int  Ld2 = (int) (L/2);  // half the data length, since each frequency occupies two slots
    
    System.out.println("\n\nFreq       Real Part                Imaginary Part\n");
    //  positive frequencies are described in data[0..Ld2-1] in increasing magnitudes
    //  and with two slots for each frequency (for the real and imaginary part of each coefficient)
    int   magnitude = 0;
    int   k = 0;
    while  (k < Ld2) {  
        System.out.println(""+ (magnitude) +"               "+ fiveDigit.format(data[k]+"            "+fiveDigit.format(data[k+1])));
        k += 2;
        magnitude++;
         }

    // negative frequencies are described in data[L-1..Ld2]  in increasing magnitudes
    //  and with two slots for each frequency (for the real and imaginary part of each coefficient)
    magnitude = 1;
    k = L-1;
    while (k > Ld2) {
System.out.println(""+(-magnitude) +"              "+ fiveDigit.format(data[k-1]+"            "+fiveDigit.format(data[k])));
magnitude++;
k -= 2;
    }
    
    }

// print from  the FFT results the first NFreqs
public static void   printft(double [] data, int nfreqs)    {
    DecimalFormat   fiveDigit = new DecimalFormat("0.00000E0");
    int   L = data.length;
    int   Ld2 = (int) (L/2);  // half the data length, since each frequency occupies two slots
    
    System.out.println("\n\nFreq       Real Part                Imaginary Part\n");
    //  positive frequencies are described in data[0..Ld2-1] in increasing magnitudes
    //  and with two slots for each frequency (for the real and imaginary part of each coefficient)
    int  magnitude = 0;
    int  k = 0;
    while (k<Ld2 )  {
      System.out.println(""+ (magnitude) +"               "+ fiveDigit.format(data[k]+"            "+fiveDigit.format(data[k+1])));
      k += 2;
      magnitude++; 
      if  (magnitude == nfreqs )  break;
                    
    }
    
    // negative frequencies are described in data[L-1..Ld2]  in increasing magnitudes
    //  and with two slots for each frequency (for the real and imaginary part of each coefficient)
    magnitude = 1;
    k = L-1;
    while (k>Ld2)  {
         System.out.println(""+(-magnitude) +"              "+ fiveDigit.format(data[k-1]+"            "+fiveDigit.format(data[k])));
         k -= 2;
         magnitude++;
         if (magnitude==nfreqs)  break;
       }

  }
  

 
    
}


/*  Example:
  N = 1024
  t = vlinspace(0, 1, N)
  x = cos(3.4*t)
  fftx = com.nr.fft.FFTNR.fft(x)
 */
