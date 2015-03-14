# Introduction #

`We compare Java FFT code from Numerical Recipes vs Native Optimized code. The rather surprising result is that Java performs as fast FFT as optimized C, both in Linux with gcc, and in Windows with Microsoft's cl compiler. `


# The FFT experiment #

`We performed a rather interest experiment using the real FFT implementation of Numerical Recipes both in C++ and in Java.`

`The JNI interface to the C++ implementation is opened with the following Java class: `

```
package NROps;


import java.io.File;

// Java interface to Numerical Recipes C++ code
public class NROps
{
    static
    {
	
	String libName = "NROps.so";
	if (File.pathSeparatorChar==';')   // Windows OS
	   libName = "NROps.dll";
	   
	try
	{
	   System.out.println("Trying to load NR library [" + libName + "] ...");
	   File path = new File("");
	   System.out.println("Current Path = " + path.getAbsolutePath());
	   String libPath = path.getAbsolutePath() + File.separator + "lib"+File.separator + libName;

	   System.load(libPath);
	   System.out.println("Library loaded");
	}
	catch (Exception e)
	{
	   System.out.println("Error: " + e);
	 }
    }

       public native void cnrfft(double[] data,  int size, int isign);


}


```

`Initially, we do not optimized the C++ code, and to our surprise Java FFT performed about 1.5 to 2 times faster!`

`Subsequently we fully optimized the C++ code for speed, both with the gcc Linux compiler and the Microsoft's cl optimized compiler. Also, to our next surprise, C++ with full optimization ` **` runs at the same speed as Java! `**

`The following example script can demonstrate that fact (requires GroovyLab, 3 September and after): `

```



xx = new NROps.NROps()

N=8*512*512
N2=(int)(N/2)
data = vrand(N).getv()
data2 = data.clone()

tic()
xx.cnrfft(data, N2, 1)
tmNative = toc()

tic()
y = fft(data2)
tm=toc()


reNative = new double[N2]
imNative = new double[N2]

i1 = 0;  i2 = 0
while (i1<N) {
    reNative[i2] = data[i1]
    i1++
    imNative[i2] = data[i1]
    i1++
    i2++
 }
    
tic()
fftj = fft(data2)
tmJava=toc()
reJava = fftj.realFFTs

figure(1); subplot(2,1,1); plot( (double[])  reJava[0..100], "Java FFT, time = "+tmJava)
subplot(2,1,2); plot( (double[]) reNative[0..100], "Native FFT, time = "+tmNative)

```

`We obtained: Java time = 0.59, Optimized C++ time = 0.58, with Microsoft's cl optimized compiler, with Linux gcc the results are very similar.`