package javaPluggins;

import java.text.DecimalFormat;
import numal.*;

public class jVanDerPol extends Object   implements AP_rk4na_methods {
	
	
    public double b(int n, double x[])  {
        return x[2];
    }
    
    public double fxj( int n, int k, double x[]) {
        return ((k==1) ? x[2] : 10.0*(1.0-x[1]*x[1])*x[2] - x[1]);
      }
}