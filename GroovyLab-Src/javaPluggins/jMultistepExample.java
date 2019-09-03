package javaPluggins;

import java.text.DecimalFormat;
import numal.*;

public class jMultistepExample extends Object   implements AP_multistep_methods {
	
	
 public void deriv(double f[], int n, double x[], double y[])  {
        
    double r;

    f[2] = r = 3.0e7*y[1]*y[1]; 
    f[1] = 0.04*(1-y[1]-y[2])-1.0e4*y[1]*y[2]-r;
  }


    public boolean available(int n, double x[], double y[],
                           double jac[][])
  {
    double r;

    jac[2][1]=r=6.0e7*y[1];
    jac[1][1] = -0.04-1.0e4*y[2]-r;
    jac[1][2] = -0.04-1.0e4*y[1];
    jac[2][2]=0.0;
    return true;
  }


  public void out(double h, int k, int n, double x[], double y[])
  {
    return;
  }
}
 

