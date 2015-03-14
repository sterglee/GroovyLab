## `August 09 - Starting GroovyLab  from native executable ` ##

`For both Linux64 and Windows64 you can start GroovyLab from the supplied native executables, i.e. for Linux64 ` **`LinuxGroovyLab`** ` and for Windows64` **`WinGroovyLab.exe`**.

`However, for Linux64, the ` **`LD_LIBRARY_PATH`** ` variable should be updated properly to include the path: ` **`$JDK_HOME/jre/lib/amd64/server`**, ` e.g. for my installation I have in .bashrc: `

`export LD_LIBRARY_PATH=$CUDA_HOME/lib64:/usr/lib:$JDK_HOME/jre/lib/amd64/server/:$LD_LIBRARY_PATH`
## `February 23` ##
`Better browsing of examples based on JTree`

## `February 10` ##
`The ` **`encog`** ` neural network library of Jeff Heaton, available as a GroovyLab toolbox, file: encog.jar at the SourceForge downloads. `
## `November 19 ` ##

`Discrete Wavelet Transform using CUDA (currently Win 64 support only). For example: `


```
// the in-development CUDA supported signal processing operations object

dwtObj = gExec.Interpreter.GlobalValues.cudaSigObj

 N = 2 << 18  // signal length
 x = new float[N]

 freq=0.000124
 k=0
while (k< N) { 
  x[k] = (float) (0.56*sin(freq*k)+rand())
  k++
  }
  
 dwtx = new float[N]

tic()
dwtObj.cudadwt(x, N, dwtx) 
tm=toc()

  
  figure(1); subplot(2,1,1); plot(x, "Signal");
  subplot(2,1,2); plot( dwtx, "Wavelet Transform")
 
```

# `Nov 07` #
`FFT based on JCUDA is supported. For example: `

```
N = 2 << 16
x = vrand(N).v
tic()
fx = jcufft(x)  // FFT with JCUDA
tmCUDA = toc()
tic()
ffx = fft(x)  // FFT with Java
tmJava =toc()
subplot(3,1,1); plot(x, "Signal");
subplot(3,1,2); plot(fx, "FFT CUDA, tm = "+tmCUDA);
subplot(3,1,3); plot(ffx.realFFTs, "FFT with Java, tm = "+tmJava)

```


# `Oct 27 ` #

`Latex displaying of evaluated formulas with the ` **`sym`** ` command, e.g. `

```
sym("Integrate[x^a,x]")
```

`A threshold on matrix size for switching to the multithreaded multiplication is computed automatically from GroovyLab, at the first run after its installation. It is stored in ` **`glab.props`** `configuration file. `

# `defaultToolboxes folder  (Aug 06) ` #
`The ` **`defaultToolboxes`** ` folder allows the user to place useful toolboxes that are installed by default automatically on startup. `

# `New convenient constructors for matrices  (July31)` #

`Matrices  can be constructed with M() by specifying their rows, separating each row with null, e.g. `

```
  xx = 8.3
  am = M(xx, 1-xx, cos(xx), null, xx+0.3*xx, 5.6, -3.4)
```


`You can also use as elements any matrices, if however they are of the same size, e.g. `

```
   xx = rand(2,3)
// append the matrix xx, two times as columns, three times as rows
   yy = M(xx, xx, null, xx, xx, null, xx, xx) 
```


# `Row/Column Append/Prepend methods (July 31) ` #

`We can easily append/prepend matrices using the ` **`RA(), RP(), CA(), CP()`** `methods. For example: `

```
x = rand(2,3)
y = ones(2,3)
xra = x.RA(y) // row append
xrp = x.RP(y) // row prepend
xca = x.CA(y) // column append
xcp = x.CP(y) // column prepend
```


# `Fixing a major problem in Matrix assignment (July 15) ` #

`We detected a significant problem with the basic matrix assignment, and we fixed by implementing GroovyLab matrix update as, e.g. `
```
x= rand(30,30)  // a GroovyLab Matrix object 
x[2][3] = 23.23  // update with a notation like Java's double [][] arrays
```
`Although, that notation is Java like, it works efficiently and we hope it is enough convenient. `

`Also, this accessing is very fast with Static Compilation, perhaps because it works using directly the Java double[][] array that keeps the data contents of the Matrix. `

# `Adaptive combination of Java matrix libraries for solving basic linear algebra problems (July09)` #

`We started to work on adaptively using the best library to accomplish basic linear algebra routines depending on the properties of the ` _`RichDouble2DArray`_

`For example we can compare SVD decompositions: `
```
// SVD test

// test Apache Common Maths
N=500; M=300
x = rand(N, M)
tic()
nrx = asvd(x)  // perform SVD
tm = toc()
shouldBeZero = nrx.U*diag(nrx.W)*t(nrx.V) - x
 shouldBeIdentity = nrx.V*t(nrx.V)  // matrix V is orthogonal

 
```

`and QR decompositions`
```
// QR test

// perform QR using Apache Commons
 N=10; M=20
 x = rand(N, M)
 qrxac = aqr(x)
 qrxac.Q* t(qrxac.Q)-eye(N, N)  // matrix Q is orthogonal, should be 0
 shouldBeZero = x-qrxac.Q*qrxac.R

// perform QR using Numerical Recipes
 qrxnr = qr(x)
 zorth = qrxnr.Q* t(qrxnr.Q)-eye(N, N)  // matrix Q is orthogonal, should be 0
 shouldBeZeronr = x-t(qrxnr.Q)*qrxnr.R
 

```

# `Compilation of methods as pure Java code (July 06)` #

`Sometimes even the @CompileStatic annotation fails to produce code close to Java's speed. For that reason, and by exploiting the source compatibility of Java-Groovy we have an experimental Java compilation mode. Suppose for example that we have the script. `

```



public double[][]  mulj(double[][] v1, double[][] v2) {  
        
        int v1Rows = v1.length;  // # rows of the result matrix
        int v2Cols = v2[0].length;  // # cols of the result matrix
        double [][] result = new double[v1Rows][v2Cols];
        int v1Cols = v1[0].length;
       double[] v1Colj = new double[v1Cols];
  
    for (int j = 0; j < v2Cols; j++) {
      for (int k = 0; k < v1Cols; k++) {
        v1Colj[k] =v2[k][j];
      }
      for (int i = 0; i < v1Rows; i++) {
        double[] Arowi = v1[i];
        double s = 0;
        for (int k = 0; k < v1Cols; k++) {
          s += Arowi[k]*v1Colj[k];
        }
       result[i][j] = s;
            }
      }
      return result;
   }

    N = 1000
    x = Ones(N,N)
    y = Ones(N, N)

  tic()
    z = mulj(x, y)
    tm = toc()

```

`We can produce the fastest possible code for the method ` _`mulj`_ `by` **`selecting`** ` its code and pressing ` **`F9`**

`GroovyLab wraps the code as a static method of a Java class, it compiles it and we have available the corresponding class in our classpath. Afterwards, we can execute easily the fast Java code from GroovyLab's scripts. `

# `July 04 updates` #

`More convenient syntax for generating vectors`
```
t = (0.0..100.0).step(0.1)  // like MATLAB's 0.0:0.1:100.0
plot(sin(0.45*t))
```



`Convenient assignment works for matrices, e.g. `
```

x = rand(80, 100)
x[2..3, 0..1] = 6.5353  // a rectangular subrange

yx = x[2..3][0..1]    // select the assigned range

x[4..5] = -0.45454  // assign rows 4 to 5 all columns


// assignment using ranges
x = rand(90, 90)
x[(0..20).by(2), 0..1] = 99
yx = x[(0..20).by(2)][ 0..1]  // select the assigned range

// assignment using ranges
x = rand(90,90)
x[0..1, (0..30).by(3)] = 33.3
yx =  x[0..1][ (0..30).by(3)]   // select the assigned range


// assignment using ranges
x = rand(90,90)
x[(2..14).by(5), (0..30).by(3)] = -77.3
yx = xx[(2..14).by(5)][(0..30).by(3)]  // select the assigned range



row=2; col = 3
y = x[row..row+2][col..col+5]  // get a matrix range

yy = x[(row..row+50).by(2)][ (col..col+30).by(3)] // like MATLAB's x(row:2:row+50, col:3:col+30)

```


# `June 30 updates` #
` Imports are issued to the GroovyShell with an ` _`ImportCustomizer`_ `This results in faster response to script execution.`

_`Package Completion`_ ` is supported in `_`Global Completion Mode`_

# `Two useful code completion modes for RSyntaxArea editor (June 23)` #

`The ` **`RSyntaxArea`** ` editor supports code completion (using ` **`CONTROL-SPACE`**` ) in two modes, that are switchable with a menu option of the ` _`Completion`_ `menu. These modes are: `

  1. **`Global completion mode. `** ` This mode is useful to remind the global GroovyLab methods, e.g. the many overloaded versions of ` _`plot().`_ `The global completion list can be extended, using library routines, that are detected using Java reflection. These libraries are available at the ` _`Completion`_ `menu. `
  1. **`Groovy completion mode. `** ` This mode works by exploiting the information acquired from  ` _`Java Reflection. `_ ` It is very useful to perform `  _`field and method`_ `completions.`


# Adaptive Functional Plotting (May 23) #

`Generally, we can improve the plot of a function significantly by adjusting the sampling density according to the rate of function change. The ` _`faplot() ` `method is a first attempt towards adaptive functional plotting. We illustrate it by means of an example: `_

```
f = {x ->  sin(x*x) }

closeAll()
Npoints = 200
figure(1)
linePlotsOn()
subplot(2, 1, 1)
fplot(f, 0, 10 )
xlabel("Fixed sampling functional ploting")
subplot(2, 1, 2)
res  = faplot(f, 0, 10,  Npoints)
xlabel("Adaptive sampling functional ploting")

figure(2)
linePlotsOn(); title("Functional plotting with adaptive sampling")
plot(res)

```

# `Customization of the pop-up menus of GroovyLab editor (May 15)` #

`In order GroovyLab to be even more convenient and user friendly, we started to customize the popup menu of the rsyntaxarea based editor. `

`Started from May 15 version, we have a ` _`plot`_ `popup menu option, that directly plots the signal under the caret position.  `

# Code Completion (May 13) #

`Code completion for an identifier can be used with ` **`F4`**

`We can select an item from the completion list using ` **` ENTER `** ` and we can clear the completion's list window using ` **`ESC`**  `Instead of` **`ENTER, `** `we can ` **`double click`** `the required entry.`

`For example for the code:`
```
randMat = rand(3,4)
```

`If we press ` **`F4`** `while we are within ` **`randMat`** `we get a completion list for the ` **`Matrix`** ` objects.`

`If we type:  ` _`randMat.ei`_ `and then type ` **`F4`** ` we get a completion list of all the member variables starting with ` _`ei`_

`If we press ` **`Shift-F4`** ` we get class completion help, for example we can examine the contents of the class` _`javax.swing.JFrame`_ `by typing ` _`Shift-F4`_

`Also code completion, displays ` _`static`_  `members in ` **`boldface`** `and the` _`instance`_ `members in plain text. `



`The autocompletion feature can be very useful, for example, suppose that we want to use the ` _`NormalDistribution`_ ` class of the Apache Common Maths library as follows: `
```

 x = new org.apache.commons.math3.distribution.NormalDistribution()
```

`Now we can easily reveal the contents of that class by pressing ` **`F4`** ` above x`, ` or by typing a substring, as method name, e.g. x.sa`




# Abbreviations (May 04) #

`GroovyLab rsyntaxtextarea based editor, support abbreviations. This can be a useful feature. When you like to use an abbreviation, type the text of the abbreviation and then ` **`F11`**`. Then the short abbreviated text is replaced with the full one.`

`For example, if we type ` **`pu`**` and then F11 we have the replacement ` **`public`**

`File ` **`Abbreviations.txt`** `defines a comma separated list of abbreviations. This file can be edited from the user to define the preferred abbreviations. `