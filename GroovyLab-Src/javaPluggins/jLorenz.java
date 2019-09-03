package javaPluggins;

import java.text.DecimalFormat;
import numal.*;

public class jLorenz extends Object   implements AP_rke_methods {
	
	
  public void der(int n, double t, double y[])
  {
    double xx,yy,zz;

    xx=y[1];
    yy=y[2];
    zz=y[3];
    y[1] = 10*(yy-xx);
    y[2] = -xx*zz+143*xx - yy;
    y[3] = xx*yy - 2.66667*zz;
  }
  


  public void out(int n, double t[], double te[], double y[],
                  double data[])
  {
/*  SOS - temporarily diabled for speed 
     double et,t2,aex,aey,aez,rex,rey,rez;
   DecimalFormat oneDigit = new DecimalFormat("0.0E0");
    if (t[0] == te[0]) {
      et=Math.exp(t[0]);
      t2=2.0*t[0];
      rex = -et*Math.sin(t2);
      aex=rex-y[1];
      rex=Math.abs(aex/rex);
      rey=et*et*(8.0+2.0*t2-Math.sin(2.0*t2))/8.0-t2-1.0;
      rez=et*(Math.sin(t2)+2.0*Math.cos(t2))+rey;
      aey=rey-y[2];
      rey=Math.abs(aey/rey);
      aez=rez-y[3];
      rez=Math.abs(aez/rez);
      System.out.println("\nT =  " + t[0] +
        "\nRelative and absolute errors in x, y and z:\n" +
        "   RE(X)    RE(Y)    RE(Z)    AE(X)    AE(Y)    AE(Z)" +
        "\n  " + oneDigit.format(rex) + "   " +
        oneDigit.format(rey) + "   " +
        oneDigit.format(rez) + "   " +
        oneDigit.format(Math.abs(aex)) + "   " +
        oneDigit.format(Math.abs(aey)) + "   " +
        oneDigit.format(Math.abs(aez)) + 
        "\nNumber of integration steps performed :  " +
        (int)data[4] +
        "\nNumber of integration steps skipped   :  " +
        (int)data[6] +
        "\nNumber of integration steps rejected  :  " +
        (int)data[5]);
    }
  */
 }
 
}
