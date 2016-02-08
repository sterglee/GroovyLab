package javaPluggins;

import java.text.DecimalFormat;
import numal.*;

public class jLorenzDiffsys extends Object   implements AP_diffsys_methods  {
	
	
 public void derivative(int n, double x, double y[], double dy[])  {
        
    double xx,yy,zz;

    xx=y[1];     yy=y[2];     zz=y[3];
    dy[1] = 10*(yy-xx);
    dy[2] = -xx*zz+143*xx - yy;
    dy[3] = xx*yy - 2.66667*zz;
  }

        
  public void output(int n, double x[],  double xe, double y[], double dy[])  {
      return;
}
 
 
}
