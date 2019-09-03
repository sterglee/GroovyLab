package javaPluggins;

import java.text.DecimalFormat;
import numal.*;

public class jLorenzMultistep extends Object   implements AP_multistep_methods {
	
	
 public void deriv(double df[], int n, double x[], double y[])  {
        
    double xx,yy,zz;

    xx=y[1];     yy=y[2];     zz=y[3];
    df[1] = 10*(yy-xx);
    df[2] = -xx*zz+143*xx - yy;
    df[3] = xx*yy - 2.66667*zz;
  }


    public boolean available(int n, double x[], double y[], double jac[][])  {
        jac[1][1] = -10;     jac[1][2] = 10;  jac[1][3] = 0;
        jac[2][1] = -y[3];   jac[2][2] = -1;  jac[2][3] = -y[1];
        jac[3][1] = y[2];    jac[3][2] = 0;  jac[3][3] = -2.66;
    
        return true;
    }
        
  public void out(double h, int k, int n, double x[], double y[]) {
      return;
}
 
 
}
