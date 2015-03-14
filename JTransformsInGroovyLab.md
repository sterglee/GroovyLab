# Introduction #

**`JTransforms`** `of Piotr Wendykier, is a powerful Java library for Fourier transforms, integrated within the source of GroovyLab. We present here some examples of its use. `


# Real FFT Example #

```



N = 2 **13

dfft = new edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D(N )

data = new double[N]
cpdata = new double[N]
recons = new double[2*N]

// create a signal
for (k in 0..N-1)
  data[k] = 8.9*sin(0.0023*k)+18.9*cos(0.0223*k)+0.02*Math.random()
  
  // copy it
for (k in 0..N-1)
  cpdata[k] = data[k]
  
  // perform a real FFT
dfft.realForward(data)  
for (k in 0..N-1)
  recons[k] = data[k]


// perform an inverse FFT
dfft.realInverseFull(recons, true)
  
  
  figure(1); subplot(3,1,1); plot(cpdata, "original");
  
  validrecons = new double[N]
for (k in 0..N-1)
  validrecons[k] = recons[k]
  
  subplot(3,1,2); plot(data, "FFT")
  
  subplot(3,1,3); plot(validrecons, "reconstructed")
  

```