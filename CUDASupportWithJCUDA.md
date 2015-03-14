# Introduction #

`GroovyLab can perform ultra parallel processing by exploiting the NVDIA CUDA framework and the JCUDA library that provides the Java bindings. ` **`Version 5.0`** `of CUDA is required (JCUDA is not yet adapted to the newest 5.5). `


`Currently, the download ` **`JCUDASupport.zip`** ` provides support only for Windows 64 bit. This download has a ` **`defaultToolboxes`** ` folder and a ` **`lib`** ` folder. The first one provides the necessary` **` .jar`** `files and the second the` **`.dll`** ` files. The ` **`installation`** ` is very simple, we simply copy these files to the corresponding GroovyLab's folders. `


`If you install the CUDA support, you can execute directly the examples. The speedup for large size problems is enormous, many hundrend or even thousand times faster!!  `

`The required steps to work with CUDA from GroovyLab are very simple: `

  1. **`Download version 5.0 of CUDA from NVDIA, since it is not the latest version, it is archieved at repositories. `**
  1. **`Unzip the CUDA-ready GroovyLab version and run it as usually. `**

**`JCUDA in GroovyLab is currently tested with Windows 8`**

## JCublas Example ##

```

/*
 * JCublas - Java bindings for CUBLAS, the NVIDIA CUDA BLAS library,
 * to be used with JCuda <br />
 * http://www.jcuda.org
 *
 * Copyright 2009 Marco Hutter - http://www.jcuda.org
 * Adapted to GroovyLab by Stergios Papadimitriou 
 */

import java.util.Random

import jcuda.*
import jcuda.jcublas.JCublas

     
/**
 * This is a sample class demonstrating the application of JCublas for
 * performing a BLAS 'sgemm' operation, i.e. for computing the matrix <br />
 * C = alpha * A * B + beta * C <br />
 * for single-precision floating point values alpha and beta, and matrices A, B
 * and C of size NxN.
 */

       N = 1000   // size of matrix
       testSgemm(N)   // test for that matrix size


    /**
     * Test the JCublas sgemm operation for matrices of size n x x
     *
     * @param n The matrix size
     */
    public static void testSgemm(int n)
    {
        float alpha = 0.3f
        float beta = 0.7f
        int nn = n * n

        println("Creating input data...")
        float [] h_A = createRandomFloatData(nn)
        float [] h_B = createRandomFloatData(nn)
        float [] h_C = createRandomFloatData(nn)
        float [] h_C_ref  = h_C.clone()

        tic()
        println("Performing Sgemm with Groovy ...")
        sgemmGroovy(n, alpha, h_A, h_B, beta, h_C_ref)
        String  tmGroovy = toc()
	
        System.out.println("Performing Sgemm with JCublas...")
        tic()
        sgemmJCublas(n, alpha, h_A, h_B, beta, h_C)
	   String tmCUDA = toc()

        boolean passed = isCorrectResult(h_C, h_C_ref)
        println("testSgemm "+(passed?"PASSED":"FAILED"))

	   def speedup = tmGroovy.toDouble() / tmCUDA.toDouble()
        println("Time Groovy = "+ tmGroovy + ", tmCUDA = "+tmCUDA+ " , speedup = "+ speedup)
    }

    /**
     * Implementation of sgemm using JCublas
     */
    private static void sgemmJCublas(int n, float alpha, float [] A, float [] B, float beta, float [] C)
    {
        int nn = n * n

        // Initialize JCublas
        JCublas.cublasInit()

        // Allocate memory on the device
        Pointer d_A = new Pointer()
        Pointer d_B = new Pointer()
        Pointer d_C = new Pointer()
        JCublas.cublasAlloc(nn, Sizeof.FLOAT, d_A)
        JCublas.cublasAlloc(nn, Sizeof.FLOAT, d_B)
        JCublas.cublasAlloc(nn, Sizeof.FLOAT, d_C)

        // Copy the memory from the host to the device
        JCublas.cublasSetVector(nn, Sizeof.FLOAT, Pointer.to(A), 1, d_A, 1)
        JCublas.cublasSetVector(nn, Sizeof.FLOAT, Pointer.to(B), 1, d_B, 1)
        JCublas.cublasSetVector(nn, Sizeof.FLOAT, Pointer.to(C), 1, d_C, 1)

        // Execute sgemm
        JCublas.cublasSgemm((char)'n', (char) 'n', n, n, n, alpha, d_A, n, d_B, n, beta, d_C, n)

        // Copy the result from the device to the host
        JCublas.cublasGetVector(nn, Sizeof.FLOAT, d_C, 1, Pointer.to(C), 1)

        // Clean up
        JCublas.cublasFree(d_A)
        JCublas.cublasFree(d_B)
        JCublas.cublasFree(d_C)

        JCublas.cublasShutdown()
    }

    /**
     * Simple implementation of sgemm, using plain Groovy
     */
    private static void sgemmGroovy(int n, float alpha, float []A, float [] B, float beta, float [] C)
    {
        for (int i = 0; i < n; ++i)
        {
            for (int j = 0; j < n; ++j)
            {
                float prod = 0;
                for (int k = 0; k < n; ++k)
                {
                    prod += A[k * n + i] * B[j * n + k]
                }
                C[j * n + i] = alpha * prod + beta * C[j * n + i]
            }
        }
    }


    /**
     * Creates an array of the specified size, containing some random data
     */
    private static float []  createRandomFloatData(int n)
    {
        Random random = new Random()
        float [] x = new float[n]
        for (int i = 0; i < n; i++)
        {
            x[i] = random.nextFloat()
        }
        return x;
    }

    /**
     * Compares the given result against a reference, and returns whether the
     * error norm is below a small epsilon threshold
     */
    private static boolean isCorrectResult(float [] result, float [] reference)
    {
        float errorNorm = 0;
        float refNorm = 0;
        for (int i = 0; i < result.length; ++i)
        {
            float diff = reference[i] - result[i]
            errorNorm += diff * diff
            refNorm += reference[i] * result[i]
        }
        errorNorm = (float) Math.sqrt(errorNorm)
        refNorm = (float) Math.sqrt(refNorm)
        if (Math.abs(refNorm) < 1e-6)
        {
            return false;
        }
        return (errorNorm / refNorm < 1e-6f)
    }


 
```

`On my computer this script diaplays`

```

Creating input data...
Performing Sgemm with Groovy ...
Performing Sgemm with JCublas...
testSgemm PASSED
Time Groovy = 6.34, tmCUDA = 0.012 , speedup = 528.3333333333333


```