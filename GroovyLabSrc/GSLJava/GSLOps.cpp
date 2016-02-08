
// gcc -lgsl  -lgslcblas -fPIC -fpermissive -I"/home/sterg/jdk1.8.0_20/include" -I"/home/sterg/jdk1.8.0_20/include/linux" -shared -o  libGSLOps.so  GSLOps.cpp 

#include <jni.h>
/* Header for class GSLOps_GSLOps */

#include "GSLJava_GSLOps.h"
#include <gsl/gsl_sf_bessel.h>

extern "C" {
/*
 * Class:     GSLOps_GSLOps
 * Method:    gslsfbesselJ0
 * Signature: (D)D
 */
JNIEXPORT jdouble JNICALL Java_GSLJava_GSLOps_gslSfBesselJ0
  (JNIEnv *env, jobject obj, jdouble  x)
 {

  double y = gsl_sf_bessel_J0(x);	
 return y;
	
}
}

