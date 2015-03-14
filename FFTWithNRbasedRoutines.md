# Introduction #

`The Numerical Recipes is a classic book, an excellent reference for any researcher and engineer in the field of numerical computation. GroovyLab present a Matlab-like interface to many routines from Numerical Recipes. Numerical Recipe routines are efficient and perhaps even more important they have extensive description in the book. We present some example code based on NR.`


# FFT with Numerical Recipes based routine #

## Example 1 ##
```
  N = 1024
  t = vlinspace(0, 1, N)
  x = cos(3.4*t)+3*sin(56.7*t)
  SFreq = 10   // sampling frequency
  fftx = fft(x, SFreq )
  plot(fftx.freqs, fftx.realFFTs)

```

## Power Spectrum Computation ##

```


import  static com.nr.NRUtil.SQR
import com.nr.sp.WelchWin
import com.nr.sp.Spectolap
import com.nr.sp.Spectreg

 

pi = acos(-1.0)

N = 1024
M = (int)(N/2)

spec = new double [M+1]
freq = new double[M+1]

   // Test Spectreg
 welch = new  WelchWin()
 sp=new Spectreg(M)
  
  
     // Generate a data set
  t = vlinspace(0, 10, N)
  data = sin(10.34*t)+3.4*cos(5.3*t)
      
     sp.adddataseg(data.v, welch)
     spec=sp.spectrum()
     freq=sp.frequencies()

  
    
    

figure(1); subplot(2, 1, 1); plot(data, "Data")

subplot(2, 1, 2); plot(freq, spec, "spectrum")



```

## `FFTs with the integrated FFT code within the GroovyLab source` ##

`We can perform FFTs and with the Java DSP code integrated within the GroovyLab's source. For example. `

```
import  DSP.fft.*
        
       N = 16384
       log2N  = 14
       xr = new double [N]
       xi = new  double [N]
       Xr = new double [N]
       Xi = new double [N]
       Xfm = new CDFT( log2N )
 
     for (i in  0..N-1) {
        xr[i] = sin(0.43*i)
        xi[i] =  cos(0.28*i)
     }
    // evaluate transform of data
     Xfm.evaluate( xr, xi, Xr, Xi )

 figure(1)
 subplot(2,1,1); plot(xr, "signal");
 subplot(2,1,2); plot(Xr, "FFT")


```