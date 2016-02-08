package javaPluggins;

import java.text.DecimalFormat;
import numal.*;

public class jLorenz_rk4na extends Object   implements AP_rk4na_methods   {
	static int cnt=0;
        static final double  closenessToZeroPrecision = 0.001;
    	static int LimitNumEvaluations = 50000;
        
        // controls when to finish integration
    public double b(int n, double x[])  {
        if (Math.abs(x[1]) < closenessToZeroPrecision) return 0.0;  // integration criteria met
        cnt++;
        if (cnt > LimitNumEvaluations) return 0.0;    // stop the integration
        return 1.0;
    }
    
    public double fxj( int n, int k, double y[]) {
         double xx,yy,zz;
/*
    xx=y[1];     yy=y[2]; zz=y[3];
    y[1] = 10*(yy-xx);
    y[2] = -xx*zz+143*xx - yy;
    y[3] = xx*yy - 2.66667*zz;
        */
    if (k==1)     return  10*(y[2]-y[1]);    //  1st equation right side
    if (k==2)   return -y[1]*y[3]+143*y[1]-y[2];    //  2nd equation right side, etc
    
    return y[1]*y[2]-2.66667*y[3];   
                }
}
	
 
 
