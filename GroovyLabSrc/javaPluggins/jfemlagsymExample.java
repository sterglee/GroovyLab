package javaPluggins;

import java.text.DecimalFormat;
import numal.*;

public class jfemlagsymExample extends Object   implements AP_femhermsym_methods {
	
	
  public double p(double x)  {   return Math.exp(x);    }
  
  public double q(double x)  {   return 0.0; }
  
  public double r(double x)   {  return  Math.sin(x); }
  
  public double f(double x)  {
      return  Math.exp(x)*(Math.sin(x) - Math.cos(x) ) + Math.sin(2.0*x)/2.0;
  }
  
}
 
