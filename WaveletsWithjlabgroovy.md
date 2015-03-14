# Introduction #

`The first step is to download the jWave.jar toolbox and to install as a Glab toolbox. This is easily accomplished with the GroovySci toolboxes tab. In essence this step creates a new GroovyShell, that keeps the variable binding of the previous GroovyShell, while extending the classpath with the new toolboxes.`

`We can now proceed in using the toolbox with some examples. `


# Example 1 - Daubechies wavelet with the jWave toolbox #

```
// import the relevant material from the jWave toolbox
import  math.transform.jwave.handlers.wavelets.*
import  math.transform.jwave.handlers.*

// create a Daubechies Wavelet
daubWav = new Daub03()

// create a synthetic signal and inject to it some noise
N= 40000
F1 = 23.4; F2 = 0.45; F3= 9.8;
taxis  = inc(0, 5/N, 5-5/N)
sig = 2.5*sin(F1*taxis)+8.9*cos(F2*taxis)-9.8*sin(F3*taxis)
rndSig = rand(1,N)
sigAll = sig+rndSig


// create a FWT object
fwtObj = new FastWaveletTransform(daubWav)

// perform a Fast Wavelet Transform with the Daubechies Wavelet
transfSig = fwtObj.forwardWavelet(sigAll.getv())

// plot the results
figure(1); subplot(2,1,1); plot(sigAll); title("A Signal with Noise");
subplot(2,1,2); plot(transfSig); title("Wavelet Transformed")

```