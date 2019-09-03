
// includes, system
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <math.h>

#include <memory>
#include <iostream>

#include "CUDAOps_KernelOps.h"

#include <cufft.h>

#include <cuda_runtime.h>
#include <cublas_v2.h>
#include <helper_cuda.h>


  // Thread block size 
#define BLOCK_SIZE 16
 

#define SDATA( index)      cutilBankChecker(sdata, index)


inline bool IsGPUCapableP2P(cudaDeviceProp *pProp)
{
#ifdef _WIN32
    return (bool)(pProp->tccDriver ? true : false);
#else
    return (bool)(pProp->major >= 2);
#endif
}

inline bool IsAppBuiltAs64()
{
#if defined(__x86_64) || defined(AMD64) || defined(_M_AMD64)
    return 1;
#else
    return 0;
#endif
}


void cudafft( cufftReal * hinData, int NX, float *hOut ) {
/*  cufftHandle plan;
  cufftComplex *dfftdata;
  
  cudaMalloc( (void **)&dfftdata, sizeof(cufftComplex)*(NX/2+1));
  if (cudaGetLastError() != cudaSuccess) {
    fprintf(stderr, "Cuda error: Failed to allocate\n");
    return;
    }
  
  // Copy input float array to cufftComplex device array 
 	cudaMemcpy(dfftdata,  hinData,  NX*sizeof(float), cudaMemcpyHostToDevice);
  
 	if (cufftPlan1d(&plan, NX, CUFFT_R2C, 1) != CUFFT_SUCCESS) {
		fprintf(stderr, "CUFFT error: Plan creation failed");
		return;
   }
  
   // use the CUFFT plan to transform the signal in place
   if (cufftExecR2C(plan,  (cufftReal *) dfftdata, dfftdata ) != CUFFT_SUCCESS) {
   fprintf(stderr, "CUFFT error: ExecC2C Forward failed");
   return;
   }
   
   if (cudaThreadSynchronize() != cudaSuccess) {
     fprintf(stderr, "Cuda error: Failed to synchronize \n");
     return;
  
  }

// copy computed results in device space to host
       cudaMemcpy( hOut, dfftdata,  NX*sizeof(float), cudaMemcpyDeviceToHost);
	   
    cufftDestroy(plan);
*/  
  }
     

	 JNIEXPORT void JNICALL Java_CUDAOps_KernelOps_cudafft
  (JNIEnv *env, jobject obj, jfloatArray inData, jint N, jfloatArray outRealImsFFT)
{
    jfloat *data = env->GetFloatArrayElements(inData, 0);
    jfloat *outRealIms = env->GetFloatArrayElements(outRealImsFFT, 0);
    
	// perform the FFT
   cudafft( data, N, outRealIms );
   
    env->ReleaseFloatArrayElements( inData, data, 0);
    env->ReleaseFloatArrayElements( outRealImsFFT, outRealIms, 0);
    
	}

static int  simple_sgemm(const float *h_A, int hA, int wA,
	const float *h_B, int wB,  float *h_C) {
  /*  int hB = wA; 
    int hC = hA;  int wC = wB;
    float alpha = 1.0f;
    float beta = 0.0f;
    float *d_A = 0;
    float *d_B = 0;
    float *d_C = 0;
    cublasHandle_t handle;
    cublasStatus_t status;
    

    // Initialize CUBLAS 
    status = cublasCreate(&handle);

    if (status != CUBLAS_STATUS_SUCCESS)
    {
        fprintf(stderr, "!!!! CUBLAS initialization error\n");
        return EXIT_FAILURE;
    }

    // Allocate device memory for the matrices 
    if (cudaMalloc((void **)&d_A, hA * wA * sizeof(d_A[0])) != cudaSuccess)
    {
        fprintf(stderr, "!!!! device memory allocation error (allocate A)\n");
        return EXIT_FAILURE;
    }

    if (cudaMalloc((void **)&d_B,  hB * wB * sizeof(d_B[0])) != cudaSuccess)
    {
        fprintf(stderr, "!!!! device memory allocation error (allocate B)\n");
        return EXIT_FAILURE;
    }

    if (cudaMalloc((void **)&d_C, hC * wC * sizeof(d_C[0])) != cudaSuccess)
    {
        fprintf(stderr, "!!!! device memory allocation error (allocate C)\n");
        return EXIT_FAILURE;
    }

    // Initialize the device matrices with the host matrices 
    cudaMemcpy( d_A, h_A, hA*wA*sizeof(float), cudaMemcpyHostToDevice);
    cudaMemcpy( d_B, h_B, hB*wB*sizeof(float), cudaMemcpyHostToDevice);
    
    int lda = hA;
    int ldb = hB;
    int ldc = hC;
 // Performs operation using cublas 
    status = cublasSgemm(handle, CUBLAS_OP_N, CUBLAS_OP_N, hA, wB,  wA, &alpha, 
    	    d_A, lda, d_B, ldb, &beta, d_C, ldc);

    if (status != CUBLAS_STATUS_SUCCESS)
    {
        fprintf(stderr, "!!!! kernel execution error.\n");
        return EXIT_FAILURE;
    }
    
    cudaMemcpy( h_C, d_C, hC*wC*sizeof(float), cudaMemcpyDeviceToHost);

 
    // Memory clean up 
    if (cudaFree(d_A) != cudaSuccess)
    {
        fprintf(stderr, "!!!! memory free error (A)\n");
        return EXIT_FAILURE;
    }

    if (cudaFree(d_B) != cudaSuccess)
    {
        fprintf(stderr, "!!!! memory free error (B)\n");
        return EXIT_FAILURE;
    }

    if (cudaFree(d_C) != cudaSuccess)
    {
        fprintf(stderr, "!!!! memory free error (C)\n");
        return EXIT_FAILURE;
    }

    // Shutdown 
    status = cublasDestroy(handle);

    if (status != CUBLAS_STATUS_SUCCESS)
    {
        fprintf(stderr, "!!!! shutdown error (A)\n");
        return EXIT_FAILURE;
    }
	*/
return 1;
}



static int  simple_dgemm(const double *h_A, int hA, int wA,
	const double *h_B, int wB,  double *h_C) {
 /*   int hB = wA; 
    int hC = hA;
    int wC = wB;
    double alpha = 1.0;
    double beta = 0.0;
    double *d_A = 0;
    double *d_B = 0;
    double *d_C = 0;
    cublasHandle_t handle;
    cublasStatus_t status;
    

    // Initialize CUBLAS 
    status = cublasCreate(&handle);

    if (status != CUBLAS_STATUS_SUCCESS)
    {
        fprintf(stderr, "!!!! CUBLAS initialization error\n");
        return EXIT_FAILURE;
    }

    // Allocate device memory for the matrices 
    if (cudaMalloc((void **)&d_A, hA * wA * sizeof(d_A[0])) != cudaSuccess)
    {
        fprintf(stderr, "!!!! device memory allocation error (allocate A)\n");
        return EXIT_FAILURE;
    }

    if (cudaMalloc((void **)&d_B,  hB * wB * sizeof(d_B[0])) != cudaSuccess)
    {
        fprintf(stderr, "!!!! device memory allocation error (allocate B)\n");
        return EXIT_FAILURE;
    }

    if (cudaMalloc((void **)&d_C, hC * wC * sizeof(d_C[0])) != cudaSuccess)
    {
        fprintf(stderr, "!!!! device memory allocation error (allocate C)\n");
        return EXIT_FAILURE;
    }

    // Initialize the device matrices with the host matrices 
    cudaMemcpy( d_A, h_A, hA*wA*sizeof(double), cudaMemcpyHostToDevice);
    cudaMemcpy( d_B, h_B, hB*wB*sizeof(double), cudaMemcpyHostToDevice);
    
    int lda = hA;
    int ldb = hB;
    int ldc = hC;
 // Performs operation using cublas 
    status = cublasDgemm(handle, CUBLAS_OP_N, CUBLAS_OP_N, hA, wB,  wA, &alpha, 
    	    d_A, lda, d_B, ldb, &beta, d_C, ldc);

    if (status != CUBLAS_STATUS_SUCCESS)
    {
        fprintf(stderr, "!!!! kernel execution error.\n");
        return EXIT_FAILURE;
    }
    
    cudaMemcpy( h_C, d_C, hC*wC*sizeof(double), cudaMemcpyDeviceToHost);

 
    // Memory clean up 
    if (cudaFree(d_A) != cudaSuccess)
    {
        fprintf(stderr, "!!!! memory free error (A)\n");
        return EXIT_FAILURE;
    }

    if (cudaFree(d_B) != cudaSuccess)
    {
        fprintf(stderr, "!!!! memory free error (B)\n");
        return EXIT_FAILURE;
    }

    if (cudaFree(d_C) != cudaSuccess)
    {
        fprintf(stderr, "!!!! memory free error (C)\n");
        return EXIT_FAILURE;
    }

    // Shutdown 
    status = cublasDestroy(handle);

    if (status != CUBLAS_STATUS_SUCCESS)
    {
        fprintf(stderr, "!!!! shutdown error (A)\n");
        return EXIT_FAILURE;
    }
	*/
return 1;
}


    
__global__ void add_matrix(float *a, float *b, float *c, int N)
{
    int idx = blockIdx.x * blockDim.x + threadIdx.x;
    if (idx < N) c[idx] = a[idx] + b[idx];
}


__global__ void dadd_matrix(double *a, double *b, double *c, int N)
{
    int idx = blockIdx.x * blockDim.x + threadIdx.x;
    if (idx < N) c[idx] = a[idx] + b[idx];
}

__global__ void subtract_matrix(float *a, float *b, float *c, int N)
{
    int idx = blockIdx.x * blockDim.x + threadIdx.x;
    if (idx < N) c[idx] = a[idx] - b[idx];
}

__global__ void dsubtract_matrix(double *a, double *b, double *c, int N)
{
    int idx = blockIdx.x * blockDim.x + threadIdx.x;
    if (idx < N) c[idx] = a[idx] - b[idx];
}

__global__ void dmul_Scalar_matrix(double *a, double value, double *c, int N)
{
    int idx = blockIdx.x * blockDim.x + threadIdx.x;
    if (idx < N) c[idx] = a[idx]*value;
}
	

__global__ void mul_Scalar_matrix(float *a, float value, float *c, int N)
{
    int idx = blockIdx.x * blockDim.x + threadIdx.x;
    if (idx < N) c[idx] = a[idx]*value;
}

void cuda_matrixAdd(float *a_h, float *b_h, float *c_h, int N)
{
    float *a_d, *b_d, *c_d;
   
    size_t size = N * sizeof (float);
        
    // allocate memory in the GPU device for a, b and c
    cudaMalloc((void **) & a_d, size);
    cudaMalloc((void **) & b_d, size);
    cudaMalloc((void **) & c_d, size);
    // copy from host to GPU device
    cudaMemcpy(a_d, a_h, size, cudaMemcpyHostToDevice);
    cudaMemcpy(b_d, b_h, size, cudaMemcpyHostToDevice);
    // do calculations on device
    int block_size = 256;
    int n_blocks = N / block_size + (N % block_size == 0 ? 0 : 1);
    add_matrix <<<n_blocks, block_size >>>(a_d, b_d, c_d, N);
    // Retrieve results from the device
    cudaMemcpy(c_h, c_d, size, cudaMemcpyDeviceToHost);
        // Cleanup
        
    cudaFree(a_d);
    cudaFree(b_d);
    cudaFree(c_d);
    
}


void dcuda_matrixAdd(double *a_h, double *b_h, double *c_h, int N)
{
    double *a_d, *b_d, *c_d;
   
    size_t size = N * sizeof (double);
        
    // allocate memory in the GPU device for a, b and c
    cudaMalloc((void **) & a_d, size);
    cudaMalloc((void **) & b_d, size);
    cudaMalloc((void **) & c_d, size);
    // copy from host to GPU device
    cudaMemcpy(a_d, a_h, size, cudaMemcpyHostToDevice);
    cudaMemcpy(b_d, b_h, size, cudaMemcpyHostToDevice);
    // do calculations on device
    int block_size = 256;
    int n_blocks = N / block_size + (N % block_size == 0 ? 0 : 1);
    dadd_matrix <<<n_blocks, block_size >>>(a_d, b_d, c_d, N);
    // Retrieve results from the device
    cudaMemcpy(c_h, c_d, size, cudaMemcpyDeviceToHost);
        // Cleanup
        
    cudaFree(a_d);
    cudaFree(b_d);
    cudaFree(c_d);
    
}


void cuda_matrixSubtract(float *a_h, float *b_h, float *c_h, int N)
{
    float *a_d, *b_d, *c_d;
   
    size_t size = N * sizeof (float);
        
    // allocate memory in the GPU device for a, b and c
    cudaMalloc((void **) & a_d, size);
    cudaMalloc((void **) & b_d, size);
    cudaMalloc((void **) & c_d, size);
    // copy from host to GPU device
    cudaMemcpy(a_d, a_h, size, cudaMemcpyHostToDevice);
    cudaMemcpy(b_d, b_h, size, cudaMemcpyHostToDevice);
    // do calculations on device
    int block_size = 256;
    int n_blocks = N / block_size + (N % block_size == 0 ? 0 : 1);
    subtract_matrix <<<n_blocks, block_size >>>(a_d, b_d, c_d, N);
    // Retrieve results from the device
    cudaMemcpy(c_h, c_d, size, cudaMemcpyDeviceToHost);
        // Cleanup
        
    cudaFree(a_d);
    cudaFree(b_d);
    cudaFree(c_d);
    
}


void dcuda_matrixSubtract(double *a_h, double *b_h, double *c_h, int N)
{
    double *a_d, *b_d, *c_d;
   
    size_t size = N * sizeof (double);
        
    // allocate memory in the GPU device for a, b and c
    cudaMalloc((void **) & a_d, size);
    cudaMalloc((void **) & b_d, size);
    cudaMalloc((void **) & c_d, size);
    // copy from host to GPU device
    cudaMemcpy(a_d, a_h, size, cudaMemcpyHostToDevice);
    cudaMemcpy(b_d, b_h, size, cudaMemcpyHostToDevice);
    // do calculations on device
    int block_size = 256;
    int n_blocks = N / block_size + (N % block_size == 0 ? 0 : 1);
    dsubtract_matrix <<<n_blocks, block_size >>>(a_d, b_d, c_d, N);
    // Retrieve results from the device
    cudaMemcpy(c_h, c_d, size, cudaMemcpyDeviceToHost);
        // Cleanup
        
    cudaFree(a_d);
    cudaFree(b_d);
    cudaFree(c_d);
    
}
  
void cuda_matrixMulScalar(float *a_h, float scalarValue, float *c_h, int N)
{
    float *a_d,  *c_d;
   
    size_t size = N * sizeof (float);
        
    // allocate memory in the GPU device for a, b and c
    cudaMalloc((void **) & a_d, size);
    cudaMalloc((void **) & c_d, size);
    // copy from host to GPU device
    cudaMemcpy(a_d, a_h, size, cudaMemcpyHostToDevice);
    // do calculations on device
    int block_size = 256;
    int n_blocks = N / block_size + (N % block_size == 0 ? 0 : 1);

  mul_Scalar_matrix  <<<n_blocks, block_size >>>(a_d, scalarValue, c_d, N);
    // Retrieve results from the device
    cudaMemcpy(c_h, c_d, size, cudaMemcpyDeviceToHost);
        // Cleanup
        
    cudaFree(a_d);
    cudaFree(c_d);
    
}


void dcuda_matrixMulScalar(double *a_h, double scalarValue, double *c_h, int N)
{
    double *a_d,  *c_d;
   
    size_t size = N * sizeof (double);
        
    // allocate memory in the GPU device for a, b and c
    cudaMalloc((void **) & a_d, size);
    cudaMalloc((void **) & c_d, size);
    // copy from host to GPU device
    cudaMemcpy(a_d, a_h, size, cudaMemcpyHostToDevice);
    // do calculations on device
    int block_size = 256;
    int n_blocks = N / block_size + (N % block_size == 0 ? 0 : 1);

  dmul_Scalar_matrix  <<<n_blocks, block_size >>>(a_d, scalarValue, c_d, N);
    // Retrieve results from the device
    cudaMemcpy(c_h, c_d, size, cudaMemcpyDeviceToHost);
        // Cleanup
        
    cudaFree(a_d);
    cudaFree(c_d);
    
}
 

// Device multiplication function called by Mul() 
// Compute C = A * B
//   hA is the height of A (i.e. # rows) 
//   wA is the width of A (i.e. # columns)
//   wB is the width of B 
__global__ void Muld(float* A, float* B, int hA, int wA, int wC, float* C) 
{ 
   // each thread computes one element of C
   // by accumulating results into Cvalue
     float Cvalue = 0.0;
	 int row = blockIdx.y * blockDim.y + threadIdx.y;
	 int col = blockIdx.x * blockDim.x + threadIdx.x;
	   
	 if (row >= hA || col >= wC) return;
	    
	 for (int e=0; e<wA; ++e)
	  Cvalue += (A[row*wA+e]) *(B[e*wC+col]);
     
	 C[row*wC+col] = Cvalue;
 } 
	 
	  

// Device multiplication function called by Mul() 
// Compute C = A * B
//   hA is the height of A (i.e. # rows) 
//   wA is the width of A (i.e. # columns)
//   wB is the width of B 
__global__ void dMuld(double* A, double* B, int hA, int wA, int wC, double* C) 
{ 
   // each thread computes one element of C
   // by accumulating results into Cvalue
     	 double Cvalue = 0.0;
	 int row = blockIdx.y * blockDim.y + threadIdx.y;
	 int col = blockIdx.x * blockDim.x + threadIdx.x;
	   
	 if (row >= hA || col >= wC) return;
	    
	 for (int e=0; e<wA; ++e)
	  Cvalue += (A[row*wA+e]) *(B[e*wC+col]);
     
	 C[row*wC+col] = Cvalue;
 } 
	 
	  
	      
 
 
// Host multiplication function 
// Compute C = A * B 
//   hA is the height of A (i.e. # rows)
//   wA is the width of A (i.e. # cols)
//   wB is the width of B 
void Mul(const float* A, const float* B, int hA, int wA, int wB, float* C) 
{ 
    int size; 
 
    // Load A and B to the device 
    float* Ad; 
    size = hA * wA * sizeof(float); 
    cudaError_t err = cudaMalloc((void**)&Ad, size);
    //printf("CUDA malloc A: %s \n", cudaGetErrorString(err));
    err = cudaMemcpy(Ad, A, size, cudaMemcpyHostToDevice);
    //printf("Copy A to device: %s \n", cudaGetErrorString(err));
    
    float* Bd; 
    int hB = wA;   // #rows of B == #columns of A 
    size = hB * wB * sizeof(float); 
    err = cudaMalloc((void**)&Bd, size); 
   // printf("CUDA malloc B: %s \n", cudaGetErrorString(err));
    err = cudaMemcpy(Bd, B, size, cudaMemcpyHostToDevice);
    //printf("Copy B to device: %s \n", cudaGetErrorString(err));
 
    // Allocate C on the device  
    float* Cd; 
    int hC = hA;   // #rows of C == #rows of A
    int wC = wB;   // #columns of C == #columns of B
    size = hC * wC * sizeof(float);
    err = cudaMalloc((void**)&Cd, size); 
   // printf("CUDA malloc C: %s \n", cudaGetErrorString(err));
    
    // Compute the execution configuration assuming 
    // the matrix dimensions are multiples of BLOCK_SIZE 
    
    /******************** 
    calculates the execution configuration
    effectively the kernel function <Muld> will be
    executed concurrently by BLOCK_SIZE^2 GPU threads
    ************************/
    dim3 dimBlock(BLOCK_SIZE, BLOCK_SIZE); 
    dim3 dimGrid((wB + dimBlock.x-1)/dimBlock.x, (hA+dimBlock.y-1) / dimBlock.y); 
    // Launch the device computation 
    Muld<<<dimGrid, dimBlock>>>(Ad, Bd, hA, wA, wC, Cd); 

    err = cudaThreadSynchronize();
    //printf("Run kernel:   %s \n", cudaGetErrorString(err));
    
    
    // Read C from the device 
    err = cudaMemcpy(C, Cd, size, cudaMemcpyDeviceToHost);
   // printf("Copy C off the device:  %s \n", cudaGetErrorString(err));
    
 
    // Free device memory 
    cudaFree(Ad); 
    cudaFree(Bd); 
    cudaFree(Cd); 
}      


 
// Host multiplication function 
// Compute C = A * B 
//   hA is the height of A (i.e. # rows)
//   wA is the width of A (i.e. # cols)
//   wB is the width of B 
void dMul(const double* A, const double* B, int hA, int wA, int wB, double* C) 
{ 
    int size; 
 
    // Load A and B to the device 
    double* Ad; 
    size = hA * wA * sizeof(double); 
    cudaError_t err = cudaMalloc((void**)&Ad, size);
    //printf("CUDA malloc A: %s \n", cudaGetErrorString(err));
    err = cudaMemcpy(Ad, A, size, cudaMemcpyHostToDevice);
    //printf("Copy A to device: %s \n", cudaGetErrorString(err));
    
    double* Bd; 
    int hB = wA;   // #rows of B == #columns of A 
    size = hB * wB * sizeof(double); 
    err = cudaMalloc((void**)&Bd, size); 
   // printf("CUDA malloc B: %s \n", cudaGetErrorString(err));
    err = cudaMemcpy(Bd, B, size, cudaMemcpyHostToDevice);
    //printf("Copy B to device: %s \n", cudaGetErrorString(err));
 
    // Allocate C on the device  
    double* Cd; 
    int hC = hA;   // #rows of C == #rows of A
    int wC = wB;   // #columns of C == #columns of B
    size = hC * wC * sizeof(double);
    err = cudaMalloc((void**)&Cd, size); 
   // printf("CUDA malloc C: %s \n", cudaGetErrorString(err));
    
    // Compute the execution configuration assuming 
    // the matrix dimensions are multiples of BLOCK_SIZE 
    
    /******************** 
    calculates the execution configuration
    effectively the kernel function <Muld> will be
    executed concurrently by BLOCK_SIZE^2 GPU threads
    ************************/
    dim3 dimBlock(BLOCK_SIZE, BLOCK_SIZE); 
    dim3 dimGrid((wB + dimBlock.x-1)/dimBlock.x, (hA+dimBlock.y-1) / dimBlock.y); 
    // Launch the device computation 
    dMuld<<<dimGrid, dimBlock>>>(Ad, Bd, hA, wA, wC, Cd); 

    err = cudaThreadSynchronize();
    //printf("Run kernel:   %s \n", cudaGetErrorString(err));
    
    
    // Read C from the device 
    err = cudaMemcpy(C, Cd, size, cudaMemcpyDeviceToHost);
   // printf("Copy C off the device:  %s \n", cudaGetErrorString(err));
    
 
    // Free device memory 
    cudaFree(Ad); 
    cudaFree(Bd); 
    cudaFree(Cd); 
}      


// add matrices
extern "C"
JNIEXPORT void JNICALL Java_CUDAOps_KernelOps_cma(JNIEnv *env, jobject obj, jfloatArray aArray, jfloatArray bArray, jfloatArray cArray)
{
    
    jfloat *a = env->GetFloatArrayElements( aArray, 0);
    jfloat *b = env->GetFloatArrayElements( bArray, 0);
    jfloat *c = env->GetFloatArrayElements( cArray, 0);
    
    jsize N = env->GetArrayLength( aArray);
    
    cuda_matrixAdd(a, b, c, N);
    
    env->ReleaseFloatArrayElements( aArray, a, 0);
    env->ReleaseFloatArrayElements( bArray, b, 0);
    env->ReleaseFloatArrayElements( cArray, c, 0);
  
}
   

// add matrices
extern "C"
JNIEXPORT void JNICALL Java_CUDAOps_KernelOps_cmad(JNIEnv *env, jobject obj, jdoubleArray aArray, jdoubleArray bArray, jdoubleArray cArray)
{
    
    jdouble *a = env->GetDoubleArrayElements( aArray, 0);
    jdouble *b = env->GetDoubleArrayElements( bArray, 0);
    jdouble *c = env->GetDoubleArrayElements( cArray, 0);
    
    jsize N = env->GetArrayLength( aArray);
    
    dcuda_matrixAdd(a, b, c, N);
    
    env->ReleaseDoubleArrayElements( aArray, a, 0);
    env->ReleaseDoubleArrayElements( bArray, b, 0);
    env->ReleaseDoubleArrayElements( cArray, c, 0);
  
}
   


// multiply with a scalar
extern "C"
JNIEXPORT void JNICALL Java_CUDAOps_KernelOps_cmscalar(JNIEnv *env, jobject obj, jfloatArray aArray, jfloat value, jfloatArray cArray)
{
    
    jfloat  *a = env->GetFloatArrayElements( aArray, 0);
    jfloat  *c = env->GetFloatArrayElements( cArray, 0);
    
    jsize N = env->GetArrayLength( aArray);
    
    cuda_matrixMulScalar(a, value, c, N);
    
    env->ReleaseFloatArrayElements( aArray, a, 0);
    env->ReleaseFloatArrayElements( cArray, c, 0);
}

// multiply with a scalar
extern "C"
JNIEXPORT void JNICALL Java_CUDAOps_KernelOps_cmscalard(JNIEnv *env, jobject obj, jdoubleArray aArray, jdouble value, jdoubleArray cArray)
{
    
    jdouble  *a = env->GetDoubleArrayElements( aArray, 0);
    jdouble  *c = env->GetDoubleArrayElements( cArray, 0);
    
    jsize N = env->GetArrayLength( aArray);
    
    dcuda_matrixMulScalar(a, value, c, N);
    
    env->ReleaseDoubleArrayElements( aArray, a, 0);
    env->ReleaseDoubleArrayElements( cArray, c, 0);
}

  // subtract matrices
extern "C"
JNIEXPORT void JNICALL Java_CUDAOps_KernelOps_cms(JNIEnv *env, jobject obj, jfloatArray aArray, jfloatArray bArray, jfloatArray cArray)
{
    
    jfloat *a = env->GetFloatArrayElements( aArray, 0);
    jfloat *b = env->GetFloatArrayElements( bArray, 0);
    jfloat *c = env->GetFloatArrayElements( cArray, 0);
    
    jsize N = env->GetArrayLength( aArray);
    
    cuda_matrixSubtract(a, b, c, N);
    
    env->ReleaseFloatArrayElements( aArray, a, 0);
    env->ReleaseFloatArrayElements( bArray, b, 0);
    env->ReleaseFloatArrayElements( cArray, c, 0);
}


  // subtract matrices
extern "C"
JNIEXPORT void JNICALL Java_CUDAOps_KernelOps_cmsd(JNIEnv *env, jobject obj, jdoubleArray aArray, jdoubleArray bArray, jdoubleArray cArray)
{
    
    jdouble *a = env->GetDoubleArrayElements( aArray, 0);
    jdouble *b = env->GetDoubleArrayElements( bArray, 0);
    jdouble *c = env->GetDoubleArrayElements( cArray, 0);
    
    jsize N = env->GetArrayLength( aArray);
    
    dcuda_matrixSubtract(a, b, c, N);
    
    env->ReleaseDoubleArrayElements( aArray, a, 0);
    env->ReleaseDoubleArrayElements( bArray, b, 0);
    env->ReleaseDoubleArrayElements( cArray, c, 0);
  
}


// multiply matrices
extern "C"
JNIEXPORT void JNICALL Java_CUDAOps_KernelOps_cmm(JNIEnv *env, jobject obj, jfloatArray aArray, jfloatArray bArray, jfloatArray cArray, jint hA, jint wA, jint wB)
  {
    jfloat *a = env->GetFloatArrayElements( aArray, 0);
    jfloat *b = env->GetFloatArrayElements( bArray, 0);
    jfloat *c = env->GetFloatArrayElements( cArray, 0);
    
	// call the C multiplication routine 
	Mul(a,  b, hA, wA, wB, c); 
	 
	env->ReleaseFloatArrayElements( aArray, a, 0);
	env->ReleaseFloatArrayElements( bArray, b, 0);
	env->ReleaseFloatArrayElements( cArray, c, 0);
	
	}


	
// multiply matrices
extern "C"
JNIEXPORT void JNICALL Java_CUDAOps_KernelOps_cmmd(JNIEnv *env, jobject obj, jdoubleArray aArray, jdoubleArray bArray, jdoubleArray cArray, jint hA, jint wA, jint wB)
  {
    jdouble *a = env->GetDoubleArrayElements( aArray, 0);
    jdouble *b = env->GetDoubleArrayElements( bArray, 0);
    jdouble *c = env->GetDoubleArrayElements( cArray, 0);
    
	// call the C multiplication routine 
	dMul(a,  b, hA, wA, wB, c); 
	 
	env->ReleaseDoubleArrayElements( aArray, a, 0);
	env->ReleaseDoubleArrayElements( bArray, b, 0);
	env->ReleaseDoubleArrayElements( cArray, c, 0);
	
	} 

extern "C"
 JNIEXPORT jint JNICALL Java_CUDAOps_KernelOps_sgemm
  (JNIEnv *env, jobject obj, jfloatArray h_a, jint hA, jint wA, jfloatArray h_b, jint wB, jfloatArray h_c)
  {
    jfloat *ha = env->GetFloatArrayElements(h_a, 0);
    jfloat *hb = env->GetFloatArrayElements(h_b, 0);
    jfloat *hc = env->GetFloatArrayElements(h_c, 0);
    
    simple_sgemm(ha,  hA, wA, hb, wB, hc);
  
    env->ReleaseFloatArrayElements( h_a, ha, 0);
    env->ReleaseFloatArrayElements( h_b, hb, 0);
    env->ReleaseFloatArrayElements( h_c, hc, 0);
	
    return 0;
  }

extern "C"
JNIEXPORT jint JNICALL Java_CUDAOps_KernelOps_dgemm
  (JNIEnv *env, jobject obj, jdoubleArray h_a, jint hA, jint wA, jdoubleArray h_b, jint wB, jdoubleArray h_c) 
   {
    jdouble *ha = env->GetDoubleArrayElements(h_a, 0);
    jdouble *hb = env->GetDoubleArrayElements(h_b, 0);
    jdouble *hc = env->GetDoubleArrayElements(h_c, 0);
    
    simple_dgemm(ha,  hA, wA, hb, wB, hc);
  
    env->ReleaseDoubleArrayElements( h_a, ha, 0);
    env->ReleaseDoubleArrayElements( h_b, hb, 0);
    env->ReleaseDoubleArrayElements( h_c, hc, 0);
	
    return 0;
  }
 
 
extern "C"
 JNIEXPORT jstring JNICALL Java_CUDAOps_KernelOps_getCUDADeviceInfo
  (JNIEnv *env, jobject obj )

{
      FILE * pFile = fopen("temp.txt", "w"); 
      jstring ret;
	  
	  char *s = "Fail";
	  
      fprintf(pFile, " CUDA Device Query (Runtime API) version (CUDART static linking)\n\n");
	  
    int deviceCount = 0;
    cudaError_t error_id = cudaGetDeviceCount(&deviceCount);

	
    if (error_id != cudaSuccess)
    {
        
		fprintf(pFile,"cudaGetDeviceCount returned %d\n-> %s\n", (int)error_id, cudaGetErrorString(error_id));
		

 	   ret = env->NewStringUTF(s);

       return ret;	
       
    }

    // This function call returns 0 if there are no CUDA capable devices.
    if (deviceCount == 0)
    {
        fprintf(pFile, "There are no available device(s) that support CUDA\n");
		
    }
    else
    {
        fprintf(pFile, "Detected %d CUDA Capable device(s)\n", deviceCount);
		
	}

    int dev, driverVersion = 0, runtimeVersion = 0;

    for (dev = 0; dev < deviceCount; ++dev)
    {
        cudaSetDevice(dev);
        cudaDeviceProp deviceProp;
        cudaGetDeviceProperties(&deviceProp, dev);

        fprintf(pFile, "\nDevice %d: \"%s\"\n", dev, deviceProp.name);
		
		
        // Console log
        cudaDriverGetVersion(&driverVersion);
        cudaRuntimeGetVersion(&runtimeVersion);
        fprintf(pFile, "  CUDA Driver Version / Runtime Version          %d.%d / %d.%d\n", driverVersion/1000, (driverVersion%100)/10, runtimeVersion/1000, (runtimeVersion%100)/10);
		fprintf(pFile, "  CUDA Capability Major/Minor version number:    %d.%d\n", deviceProp.major, deviceProp.minor);
		
        char msg[256];
        sprintf(msg, "  Total amount of global memory:                 %.0f MBytes (%llu bytes)\n",
                (float)deviceProp.totalGlobalMem/1048576.0f, (unsigned long long) deviceProp.totalGlobalMem);
        fprintf(pFile, "%s", msg);
		

        fprintf(pFile, "  (%2d) Multiprocessors, (%3d) CUDA Cores/MP:     %d CUDA Cores\n",
               deviceProp.multiProcessorCount,
               _ConvertSMVer2Cores(deviceProp.major, deviceProp.minor),
               _ConvertSMVer2Cores(deviceProp.major, deviceProp.minor) * deviceProp.multiProcessorCount);
					   
        fprintf(pFile, "  GPU Clock rate:                                %.0f MHz (%0.2f GHz)\n", deviceProp.clockRate * 1e-3f, deviceProp.clockRate * 1e-6f);

		

#if CUDART_VERSION >= 5000
        // This is supported in CUDA 5.0 (runtime API device properties)
        fprintf(pFile, "  Memory Clock rate:                             %.0f Mhz\n", deviceProp.memoryClockRate * 1e-3f);
		
        fprintf(pFile, "  Memory Bus Width:                              %d-bit\n",   deviceProp.memoryBusWidth);
		
        if (deviceProp.l2CacheSize)
        {
            fprintf(pFile, "  L2 Cache Size:                                 %d bytes\n", deviceProp.l2CacheSize);
			
		}
#else
        // This only available in CUDA 4.0-4.2 (but these were only exposed in the CUDA Driver API)
        int memoryClock;
        cuDeviceGetAttribute(&memoryClock, CU_DEVICE_ATTRIBUTE_MEMORY_CLOCK_RATE, dev);
        fprintf(pFile, "  Memory Clock rate:                             %.0f Mhz\n", memoryClock * 1e-3f);
		
        int memBusWidth;
        cuDeviceGetAttribute(&memBusWidth, CU_DEVICE_ATTRIBUTE_GLOBAL_MEMORY_BUS_WIDTH, dev);
        fprintf(pFile, "  Memory Bus Width:                              %d-bit\n", memBusWidth);
		
        int L2CacheSize;
        cuDeviceGetAttribute(&L2CacheSize, CU_DEVICE_ATTRIBUTE_L2_CACHE_SIZE, dev);

        if (L2CacheSize)
        {
            fprintf(pFile, "  L2 Cache Size:                                 %d bytes\n", L2CacheSize);
			
        }
#endif

        fprintf(pFile, "  Maximum Texture Dimension Size (x,y,z)         1D=(%d), 2D=(%d, %d), 3D=(%d, %d, %d)\n",
               deviceProp.maxTexture1D   , deviceProp.maxTexture2D[0], deviceProp.maxTexture2D[1],
               deviceProp.maxTexture3D[0], deviceProp.maxTexture3D[1], deviceProp.maxTexture3D[2]);
        				
        fprintf(pFile, "  Maximum Layered 1D Texture Size, (num) layers  1D=(%d), %d layers\n",
               deviceProp.maxTexture1DLayered[0], deviceProp.maxTexture1DLayered[1]);
        
		fprintf(pFile, "  Maximum Layered 2D Texture Size, (num) layers  2D=(%d, %d), %d layers\n",
			   deviceProp.maxTexture2DLayered[0], deviceProp.maxTexture2DLayered[1], deviceProp.maxTexture2DLayered[2]);
		

        fprintf(pFile, "  Total amount of constant memory:               %lu bytes\n", deviceProp.totalConstMem);
		
        fprintf(pFile, "  Total amount of shared memory per block:       %lu bytes\n", deviceProp.sharedMemPerBlock);
		
        fprintf(pFile, "  Total number of registers available per block: %d\n", deviceProp.regsPerBlock);
		
        fprintf(pFile, "  Warp size:                                     %d\n", deviceProp.warpSize);
		
        fprintf(pFile, "  Maximum number of threads per multiprocessor:  %d\n", deviceProp.maxThreadsPerMultiProcessor);
		
        fprintf(pFile, "  Maximum number of threads per block:           %d\n", deviceProp.maxThreadsPerBlock);
		
        fprintf(pFile, "  Max dimension size of a thread block (x,y,z): (%d, %d, %d)\n",
               deviceProp.maxThreadsDim[0],
               deviceProp.maxThreadsDim[1],
               deviceProp.maxThreadsDim[2]);
		
        fprintf(pFile, "  Max dimension size of a grid size    (x,y,z): (%d, %d, %d)\n",
               deviceProp.maxGridSize[0],
               deviceProp.maxGridSize[1],
               deviceProp.maxGridSize[2]);
			   
        fprintf(pFile, "  Maximum memory pitch:                          %lu bytes\n", deviceProp.memPitch);
    	
		fprintf(pFile, "  Texture alignment:                             %lu bytes\n", deviceProp.textureAlignment);
        
		fprintf(pFile, "  Concurrent copy and kernel execution:          %s with %d copy engine(s)\n", (deviceProp.deviceOverlap ? "Yes" : "No"), deviceProp.asyncEngineCount);
        
		fprintf(pFile, "  Run time limit on kernels:                     %s\n", deviceProp.kernelExecTimeoutEnabled ? "Yes" : "No");
        
		fprintf(pFile, "  Integrated GPU sharing Host Memory:            %s\n", deviceProp.integrated ? "Yes" : "No");
        
		fprintf(pFile, "  Support host page-locked memory mapping:       %s\n", deviceProp.canMapHostMemory ? "Yes" : "No");
        
		fprintf(pFile, "  Alignment requirement for Surfaces:            %s\n", deviceProp.surfaceAlignment ? "Yes" : "No");
        
		fprintf(pFile, "  Device has ECC support:                        %s\n", deviceProp.ECCEnabled ? "Enabled" : "Disabled");
#ifdef WIN32
        
		fprintf(pFile, "  CUDA Device Driver Mode (TCC or WDDM):         %s\n", deviceProp.tccDriver ? "TCC (Tesla Compute Cluster Driver)" : "WDDM (Windows Display Driver Model)");
#endif
        
		fprintf(pFile, "  Device supports Unified Addressing (UVA):      %s\n", deviceProp.unifiedAddressing ? "Yes" : "No");
        
		fprintf(pFile, "  Device PCI Bus ID / PCI location ID:           %d / %d\n", deviceProp.pciBusID, deviceProp.pciDeviceID);
		
		
        const char *sComputeMode[] =
        {
            "Default (multiple host threads can use ::cudaSetDevice() with device simultaneously)",
            "Exclusive (only one host thread in one process is able to use ::cudaSetDevice() with this device)",
            "Prohibited (no host thread can use ::cudaSetDevice() with this device)",
            "Exclusive Process (many threads in one process is able to use ::cudaSetDevice() with this device)",
            "Unknown",
            NULL
        };
        fprintf(pFile, "  Compute Mode:\n");
		
        fprintf(pFile, "     < %s >\n", sComputeMode[deviceProp.computeMode]);
		
    }

    // If there are 2 or more GPUs, query to determine whether RDMA is supported
    if (deviceCount >= 2)
    {
        cudaDeviceProp prop[64];
        int gpuid[64]; // we want to find the first two GPU's that can support P2P
        int gpu_p2p_count = 0;

        for (int i=0; i < deviceCount; i++)
        {
            checkCudaErrors(cudaGetDeviceProperties(&prop[i], i));

            // Only boards based on Fermi or later can support P2P
            if ((prop[i].major >= 2)
#ifdef _WIN32
                // on Windows (64-bit), the Tesla Compute Cluster driver for windows must be enabled to supprot this
                && prop[i].tccDriver
#endif
               )
            {
                // This is an array of P2P capable GPUs
                gpuid[gpu_p2p_count++] = i;
            }
        }

        // Show all the combinations of support P2P GPUs
        int can_access_peer_0_1, can_access_peer_1_0;

        if (gpu_p2p_count >= 2)
        {
            for (int i = 0; i < gpu_p2p_count-1; i++)
            {
                for (int j = 1; j < gpu_p2p_count; j++)
                {
                    checkCudaErrors(cudaDeviceCanAccessPeer(&can_access_peer_0_1, gpuid[i], gpuid[j]));
                    fprintf(pFile, "> Peer access from %s (GPU%d) -> %s (GPU%d) : %s\n", prop[gpuid[i]].name, gpuid[i],
                           prop[gpuid[j]].name, gpuid[j] ,
                           can_access_peer_0_1 ? "Yes" : "No");

                }
            }

            for (int j = 1; j < gpu_p2p_count; j++)
            {
                for (int i = 0; i < gpu_p2p_count-1; i++)
                {
                    checkCudaErrors(cudaDeviceCanAccessPeer(&can_access_peer_1_0, gpuid[j], gpuid[i]));
                    fprintf(pFile, "> Peer access from %s (GPU%d) -> %s (GPU%d) : %s\n", prop[gpuid[j]].name, gpuid[j],
                           prop[gpuid[i]].name, gpuid[i] ,
                           can_access_peer_1_0 ? "Yes" : "No");

						   
                }
            }
        }
    }

    // csv masterlog info
    // *****************************
    // exe and CUDA driver name
    fprintf(pFile, "\n");
		
    std::string sProfileString = "deviceQuery, CUDA Driver = CUDART";
    char cTemp[16];

    // driver version
    sProfileString += ", CUDA Driver Version = ";
#ifdef WIN32
    sprintf_s(cTemp, 10, "%d.%d", driverVersion/1000, (driverVersion%100)/10);
#else
    sprintf(cTemp, "%d.%d", driverVersion/1000, (driverVersion%100)/10);
#endif
    sProfileString +=  cTemp;

    // Runtime version
    sProfileString += ", CUDA Runtime Version = ";
#ifdef WIN32
    sprintf_s(cTemp, 10, "%d.%d", runtimeVersion/1000, (runtimeVersion%100)/10);
#else
    sprintf(cTemp, "%d.%d", runtimeVersion/1000, (runtimeVersion%100)/10);
#endif
    sProfileString +=  cTemp;

    // Device count
    sProfileString += ", NumDevs = ";
#ifdef WIN32
    sprintf_s(cTemp, 10, "%d", deviceCount);
#else
    sprintf(cTemp, "%d", deviceCount);
#endif
    sProfileString += cTemp;

    // Print Out all device Names
    for (dev = 0; dev < deviceCount; ++dev)
    {
#ifdef _WIN32
        sprintf_s(cTemp, 13, ", Device%d = ", dev);
#else
        sprintf(cTemp, ", Device%d = ", dev);
#endif
        cudaDeviceProp deviceProp;
        cudaGetDeviceProperties(&deviceProp, dev);
        sProfileString += cTemp;
        sProfileString += deviceProp.name;
    }

    sProfileString += "\n";
    fprintf(pFile, "%s", sProfileString.c_str());
	

	
	fclose(pFile);

	// read back the text contents of the file
	long f_size;
	char* code;
	size_t code_s, result;
	FILE* fp = fopen("temp.txt", "r");
	fseek(fp, 0, SEEK_END);
	f_size = ftell(fp); /* This returns 29696, but file is 85 bytes */
	fseek(fp, 0, SEEK_SET);
	code_s = sizeof(char) * f_size;
	code = (char *)malloc(code_s);
	result = fread(code, 1, f_size, fp); 
		

	  ret = env->NewStringUTF(code);

   return ret;	
	
}

   
   
