# Introduction #

`The power of MATLAB can be combined with GroovyLab with the ` **`matlabcontrol`** ` API. The project's page is `


http://code.google.com/p/matlabcontrol


`To utilize MATLAB commands from GroovyLab, it is very easy. The ` **`matlabcontrol-4.1.0.jar`** ` is integrated within the lib folder of GroovyLab. Of course, you must have installed MATLAB on your system. `

`We present some examples of using MATLAB from GroovyLab. `


## `Summing the elements of a GroovySci vector using MATLAB` ##
```


initMatlabConnection() // init connection to MATLAB

x = vrand(2000)  // create a GroovySci vector

mcode = "sm = sum(x)"   // the MATLAB code to evaluate

// at the following meval() call the whole meaningful 
// context of GroovyShell is passed to MATLAB.
// MATLAB uses it to evaluate the script assigned to the mcode String
// MATLAB then returns the parameters of the output list to GroovyLab,
// in the particular case the variable sm
meval(mcode, ["sm"])  
sm[0][0] == x.sum()  // verify using GroovySci, results from MATLAB are                  
    // returned as double[][] arrays, thus we access sm as double [][] 

```


## SVD using MATLAB ##

```

initMatlabConnection()  // init connection to MATLAB

N=500
x = Rand(N,N)  // a Java random 2D double array 

tic()
svdc = "[u, s, v] = svd(x);"   //  the MATLAB script for SVD computation

// call MATLAB to evaluate the svd
// the first parameter of meval(), i.e. svdc, is the MATLAB script to evaluate
// the second parameter of meval() is  the list of input parameters from GroovyLab to MATLAB
// the third parameter of meval() is the list of output parameters from MATLAB to GroovyLab
meval( svdc, ["x"], ["u", "s", "v"])

tmMatlab = toc()  // 0.152 sec, i5 3.2 GHz

 shouldBeZero = u*s*t(v)-x  // from the definition of SVD


// perform SVD with Java
tic()
svdJ = svd(x)
tmJava = toc()  // 2.74 sec, i5 3.2 GHz


```


## `Computing eigenvalues and eigenvectors` ##

`The ` **`MatlabComplex`** ` type is used to keep the complex results returned from MATLAB as real and imaginary arrays. That type is very simple: `

```

package gExec.Interpreter;

public class MatlabComplex {
  double [][] re;
  double [][]im;
}

```

`We use the ` **`MatlabComplex`** ` type to retrieve the results that MATLAB returns at the following eigendecomposition example.`



```


// perform an eigenvalue computation using MATLAB

initMatlabConnection()  // init connection to MATLAB

N=100
A = Rand(N, N)


// solve with MATLAB, eigenvalues only
mstr = "eA = eig( A) "

// evaluate the eigenvalues with the meval() that permits the returning
// of complex values with the MatlabComplex class
// The MatlabComplex class keeps the real values with the re field which is double [][]
// and the imaginary with the im field which is also double [][[]
tic()
meval(mstr,  ["A"], ["eA"] )
tmm = toc()

realParts = eA.re   // the real parts of the computed eigenvalues
imParts = eA.im   // the imaginary parts of the computed eigenvalues

// solve with MATLAB, eigenvalues and eigenvectors
mallstr = "[evals, evecs] = eig( A) "

// evaluate the eigenvalues with the meval() that permits the returning
// of complex values with the MatlabComplex class
// The MatlabComplex class keeps the real values with the re field which is double [][]
// and the imaginary with the im field which is also double [][[]
tic()
meval(mallstr,  ["A"], ["evals",  "evecs"] )
tmmall = toc()

realPartsEvals = evals.re   // the real parts of the computed eigenvalues
imPartsEvals = evals.im   // the imaginary parts of the computed eigenvalues
realPartsEvecs = evecs.re   // the real parts of the computed eigenvectors
imPartsEvecs = evecs.im   // the imaginary parts of the computed eigenvectors




// solve with Java
tic()
jeA = eig(A)
tmJ = toc()
```


## `Performing FFT computations` ##

```
// perform an FFT using MATLAB

initMatlabConnection()  // init connection to MATLAB

N=2**20
t = linspace(0, 1, N)

x = cos(23.6*t)+rand(1, N) // induce some noisy

fftstr = " y = fft(x); "

tic()
meval(fftstr, ["x"], ["y"])  // perform the FFT using MATLAB
tmMatlab = toc()

yre = y.re   // the real values of the FFT
yim = y.im  // the imaginary values of the FFT

tic()
yj = fft(x)
tmj = toc()

```

## `Integrating ODEs using MATLAB` ##

```

// the MATLAB expression to evaluate
mexpr = """
fv = @(t,x) [ 2*x(1)-0.001 - 0.001*x(1)*x(2); -10*x(2)+0.002*x(1)*x(2)];
initx = [5000 100]';
options = odeset('RelTol', acc);
[t x] = ode23(fv, [0 simtime], initx, options);
"""

acc = getDouble("accuracy", 0.001)
simtime = getDouble("enter runtime", 30.0)
initMatlabConnection()
meval(mexpr, ["acc", "simtime"], ["t", "x"])


xm = new Matrix(x)
xm = t(xm)

plot(xm)

```