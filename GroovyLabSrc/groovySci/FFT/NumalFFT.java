package scalaSci.FFT;

import org.apache.commons.math3.complex.Complex;


public class NumalFFT {
    
    
   static  public Complex [] fft(double [] sig)  {
       int siglen = sig.length;
       double [][] cdata = new double[3][siglen+1];  //  NUMAL type double array
       
       // copy the signal
       for (int k=0; k<siglen;k++)  {
           cdata[1][k+1] = sig[k];   // real part
           cdata[2][k+1] = 0.0; // imagibary part
       }

       numal.FFT.cfft2p(cdata, siglen);
       
       // return results as Complex array
       Complex [] ca = new Complex[siglen];
       for (int k=0; k<siglen; k++) 
           ca[k] = new Complex(cdata[1][k], cdata[2][k]);
       
    return ca;
   }
  
}
