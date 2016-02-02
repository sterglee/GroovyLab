package groovySci.math.array;

import gExec.Interpreter.GlobalValues;
import groovy.lang.GroovyObjectSupport;
import groovySci.PrintFormatParams.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.StringTokenizer;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;


// Vectors are dynamically resizable while double[] arrays are not

public  class Vec  extends  GroovyObjectSupport   { 
    
    public double  [] v;
    public int  len;
    public static final int mxVecLenToString = 100;
    
    
    public Vec(int n) {
        v = new double[n];
        len = n;
    }
    
    // initialize by copy
    public Vec(double [] vin) {
        int N = vin.length;
        len = N;
        v = new double[N];
        for (int n=0; n<N; n++)
          v[n] = vin[n];
    }
    
    // initialize by reference
    public Vec(double [] vin, boolean isRef) {
      len = vin.length;  
      v = vin;    
    }
    
    public Vec clone() {
        double [] vc = new double[this.length()];
        return new Vec(vc);
    }
    
    public Vec copy() {
        double [] vc = new double[this.length()];
        for (int k=0; k<length(); k++)
            vc[k] = v[k];
        return new Vec(vc);
    }
    
   public double [] getv() { return  v; }

   public int size() { return len; }
   public int length() { return len; }


// construct a zero-indexed Vec from values, e.g. 
//  v = V(3.4, -6.7, -1.2, 5.6)
public static Vec V(double ...values)   {
    int   nl = values.length;  // number of elements
    Vec    sv = new Vec( nl );  // create a Vec
    for (int k = 0; k < nl;  k++)
        sv.v[k] =  values[k];  // copy value
           
    return sv;  // return the constructed matrix

  }   
public static Vec Vec(double ...values)   {
      return V(values);
}

// construct a zero-indexed Vec from a String, e.g. 
//  v = V("3.4 -6.7 -1.2 5.6")
public static Vec   V(String s)   {
    int nelems  = 0;
      
// count how many numbers each row has. Assuming that each line has the same number of elements
 StringTokenizer strtok = new java.util.StringTokenizer(s, ", ");  // elements are separated by ',' or ' '
 while (strtok.hasMoreTokens()) {
   String  tok = strtok.nextToken();
   nelems++;
}

 double []  data  =  new double[nelems];
  strtok = new java.util.StringTokenizer(s, ", ");  // elements are separated by ',' or ' '
  int k=0;
 while (strtok.hasMoreTokens()) {
   String  tok = strtok.nextToken();
   data[k++] =  Double.parseDouble(tok);
 }
 
 return new Vec(data, true);
 }  

   
final public double apply(int k) {
   return v [k];
 }

final public double getAt(int k) {
    return v[k];
}
  
final public void putAt(int k, double value) {
    v[k] = value;
}  

// assign a whole double [] array at k th position
final public void putAt(int k,  double[] value) {
  int vl = value.length;
  if (k+vl > this.v.length) return;  // out of range
  for (int n=0; n<vl; n++)
      v[k+n] = value[n];
      
}

// assign a whole Vec at k th position
final public void putAt(int k,  Vec value) {
  int vl = value.v.length;
  if (k+vl > this.v.length) return;  // out of range
  for (int n=0; n<vl; n++)
      v[k+n] = value.v[n];
      
}

// puts the value at each element of the passed range r
final public void putAt(groovy.lang.IntRange  range,  double value)  {
  int rs = range.getFrom(); int re = range.getTo();
  for (int k=rs; k<=re; k++)
      v[k] = value;
}
 	
// gets a range of values
final public Vec getAt(groovy.lang.IntRange range) {
    int rs = range.getFrom(); int re = range.getTo();
    int rangeLen = re-rs+1;
    Vec rv = new Vec(rangeLen);  // the return vector
    for (int k=0; k<rangeLen; k++)
        rv.putAt(k, this.v[rs+k]);
    return rv;
}
    
final public void set(int i, Number vn) {
    v[i] =  vn.doubleValue();
}

final public double get(int i) {
   return  v[i];
}

  
public Vec eachValue(groovy.lang.Closure c) {
    Vec vr = new Vec(this.length());
    for (int i = 0; i < this.len; i++)
        vr.set(i, (Number)c.call(get(i)));
     return vr;
}
	
public Vec  map(groovy.lang.Closure c) {
    Vec vr = new Vec(this.length());
    for (int i = 0; i < this.len; i++)
        vr.set(i, (Number)c.call(get(i)));
     return vr;
}
 
public void i_eachValue(groovy.lang.Closure c) {  // in-place
    for (int i = 0; i < this.len; i++)
        set(i, (Number)c.call(get(i)));
}
	
public void i_map(groovy.lang.Closure c) { // in-place map
    for (int i = 0; i < this.len; i++)
        set(i, (Number)c.call(get(i)));
}

public  double sum() {
      double smAll=0.0; 
       for (int k=0; k<len; k++)
            smAll +=  v[k];
        return  smAll;
    }

public  double ssum() {
 return DoubleStream.of( v).parallel().reduce((x, y) -> x+y).getAsDouble();
    }

public  double sprod() {
 return DoubleStream.of( v).parallel().reduce(1.0, (x, y) -> x*y);
    }


public  double prod() {
      double prodAll= 1.0; 
       for (int k=0; k<len; k++)
            prodAll *=  v[k];
        return  prodAll;
    }


public  double mean() {
      double mnAll=0.0; 
       for (int k=0; k<len; k++)
            mnAll +=  v[k];
        return  mnAll/len;
    }


public double  norm1()  {
  double sm=0.0;
  double cv=0.0;
  for (int k=0; k<length(); k++) {
    cv = v[k];
    if (cv < 0) cv = -cv;
    sm += cv;
    }
    return sm;
    }
    
public double  norm2()   {
  double nrm = 0.0;
  double  cv = 0.0;
  for (int k=0; k < length(); k++) {
    cv = v[k];
    nrm += (cv*cv);
      }
 return   java.lang.Math.sqrt( nrm);
    }
  
  
 public double norm2_robust() { 
   double  scale = 0.0;  double   ssq = 1.0;
        for (int i=0; i< length(); i++)
            if (v[i] != 0) {
                double absxi = java.lang.Math.abs(v[i]);
                if (scale < absxi) {
                    ssq = 1 + ssq * (scale / absxi) * (scale / absxi);
                    scale = absxi;
                } else
                    ssq += (absxi / scale) * (absxi / scale);
            }
        return scale * java.lang.Math.sqrt(ssq);
  }
  
public double  normInf()   {
  double  max = 0.0;
  for (int i = 0; i<length(); i++)
    max = Math.max(Math.abs(v[i]), max);
   return max;
  }
  
public Vec plus(Vec that) {
    int thislen = this.length();
    double [] vp = new double[thislen];
    double [] vthat = that.getv();
    for (int k=0; k<thislen; k++)
        vp[k] =  v[k]+vthat[k];
    return new Vec(vp, true);
}


public Vec plus(double [] that) {
    int thislen = this.length();
    double [] vp = new double[thislen];
    for (int k=0; k<thislen; k++)
        vp[k] =  v[k]+that[k];
    return new Vec(vp, true);
}

public Vec plus(double  that) {
    int thislen = this.length();
    double [] vp = new double[thislen];
    for (int k=0; k<thislen; k++)
        vp[k] =  v[k]+that;
    return new Vec(vp, true);
}


public Vec minus(double  that) {
    int thislen = this.length();
    double [] vp = new double[thislen];
    for (int k=0; k<thislen; k++)
        vp[k] =  v[k]-that;
    return new Vec(vp, true);
}

public Vec negative() {
    Vec vr = new Vec(this.length());
    for (int k=0; k< this.length(); k++)
        vr.set(k, -this.getAt(k));
    return vr;
}



public Vec minus(Vec that) {
    int thislen = this.length();
    double [] vp = new double[thislen];
    double [] vthat = that.getv();
    for (int k=0; k<thislen; k++)
        vp[k] =  v[k]-vthat[k];
    return new Vec(vp, true);
}

public Vec minus(double [] that) {
    int thislen = this.length();
    double [] vp = new double[thislen];
    for (int k=0; k<thislen; k++)
        vp[k] =  v[k]-that[k];
    return new Vec(vp, true);
}
/*
public Vec  multiply(Matrix  that ) {
    return that.multiply(this);
}
*/
public Vec multiply(Vec that) {
    int thislen = this.length();
    double [] vp = new double[thislen];
    double [] vthat = that.getv();
    for (int k=0; k<thislen; k++)
        vp[k] =  v[k]*vthat[k];
    return new Vec(vp, true);
}


public Vec multiply(double [] that) {
    int thislen = this.length();
    double [] vp = new double[thislen];
    for (int k=0; k<thislen; k++)
        vp[k] =  v[k]*that[k];
    return new Vec(vp, true);
}

public Vec multiply(double  that) {
    int thislen = this.length();
    double [] vp = new double[thislen];
    for (int k=0; k<thislen; k++)
        vp[k] =  v[k]*that;
    return new Vec(vp, true);
}


public Vec div(double  that) {
    int thislen = this.length();
    double [] vp = new double[thislen];
    for (int k=0; k<thislen; k++)
        vp[k] =  v[k]/that;
    return new Vec(vp, true);
}


public Vec div(Vec that) {
    int thislen = this.length();
    double [] vp = new double[thislen];
    double [] vthat = that.getv();
    for (int k=0; k<thislen; k++)
        vp[k] =  v[k]/vthat[k];
    return new Vec(vp, true);
}


public Vec power(Vec that) {
    int thislen = this.length();
    double [] vp = new double[thislen];
    double [] vthat = that.getv();
    for (int k=0; k<thislen; k++)
        vp[k] =  Math.pow(v[k], vthat[k]);
    return new Vec(vp, true);
}


public Vec or(Vec that) {
    int thislen = this.length();
    double [] vp = new double[thislen];
    double [] vthat = that.getv();
    for (int k=0; k<thislen; k++)
        vp[k] =  (int) v[k] | (int) vthat[k];
    return new Vec(vp, true);
}


public Vec and(Vec that) {
    int thislen = this.length();
    double [] vp = new double[thislen];
    double [] vthat = that.getv();
    for (int k=0; k<thislen; k++)
        vp[k] =  (int) v[k] & (int) vthat[k];
    return new Vec(vp, true);
}


public Vec xor(Vec that) {
    int thislen = this.length();
    double [] vp = new double[thislen];
    double [] vthat = that.getv();
    for (int k=0; k<thislen; k++)
        vp[k] =  (int) v[k] ^ (int) vthat[k];
    return new Vec(vp, true);
}

public static Vec  vones(int n) {
    double [] data = new double[n];
    for (int i=0; i<n; i++)
        data[i] = 1.0;
    return new Vec(data, true);
}

public static Vec  vzeros(int n) {
    double [] data = new double[n];
    for (int i=0; i<n; i++)
        data[i] = 0.0;
    return new Vec(data, true);
}


public static Vec  vrand(int n) {
    double [] data = new double[n];
    for (int i=0; i<n; i++)
        data[i] = Math.random();
    return new Vec(data, true);
}

public static Vec vfill( int n, double value) {
    double [] data = new double[n];
    for (int i=0; i<n; i++)
        data[i] = value;
    return new Vec(data, true);
}
    
public static Vec   vlinspace(double startv,  double endv)   {
    int  nP = 100;  // use 100 as default number of points
    double []  v = new double [nP];
    double  dx = (startv-endv)/(nP-1);
    for (int i = 0; i< nP; i++)
	v[i] = startv +  i * dx;

    return new Vec(v, true);
}

public static Vec  vlinspace(double startv,  double endv, int nP)   {
    double []  v = new double [nP];
    double  dx = (startv-endv)/(nP-1);
    for (int i = 0; i< nP; i++)
	v[i] = startv +  i * dx;

    return new Vec(v, true);
}

// use by default logspace=10
public static Vec  vlogspace(double  startOrig,  double endOrig) {
    int nP = 100;
    return vlogspace(startOrig, endOrig, nP, 10.0);
}

// use by default logspace=10
public static Vec  vlogspace(double  startOrig,  double endOrig,  int nP) {
    return vlogspace(startOrig, endOrig, nP, 10.0);
}

public  static Vec  vlogspace(double startOrig,  double endOrig, int  nP,  double  logBase)  {
    boolean  positiveTransformed = false;
    double    transformToPositive = 0.0;
                
    double  start = startOrig;  double   end=endOrig;   // use these values to handle negative values
    boolean  axisReversed = false;
    if (start > end)   {   // reverse axis
            start = endOrig; end = startOrig; axisReversed = true;
        }
                
     if (start <= 0)  {  // make values positive
             transformToPositive = -start+1;  start = 1;     
             end = end+transformToPositive;  positiveTransformed = true;
        }
     double    logBaseFactor = 1.0/java.lang.Math.log10(logBase);
     double    start_tmp = java.lang.Math.log10(start)*logBaseFactor;
     double    end_tmp = java.lang.Math.log10(end)*logBaseFactor;
     //println("logBaseFactor = "+logBaseFactor+"  start_tmp = "+start_tmp+"  end_tmp = "+end_tmp)
                
    double  []   values = new double [nP];
    double  dx     = (end_tmp-start_tmp) / (nP-1);
    for (int i=0; i<nP; i++) 
        values[i] = java.lang.Math.pow( logBase, (start_tmp +  i * dx));
		
        if  (positiveTransformed)    // return to the original range of values
                {
              for ( int i=0; i<nP; i++)
		  values[ i ]  -=  -transformToPositive;
		  start -= transformToPositive;
                }

                if (axisReversed)  {
                    double [] valuesNew = new double [nP];
                    valuesNew[0] = values[nP-1];
                    for ( int i = 1; i<nP; i++)  {
                        valuesNew[i]  = valuesNew[ i-1]-(values[i]-values[i-1]);
                    }
                    return new Vec(valuesNew, true);
                }
                          
     return new Vec(values, true);
}


                
    public static Vec vinc(double begin, double pitch, double end)  {
           if (begin > end && pitch >0)
               return new Vec(DoubleArray.increment(end, pitch, begin));
           if (begin < end && pitch < 0)
               return new Vec(DoubleArray.increment(end, pitch, begin));
	   return new Vec(DoubleArray.increment(begin, pitch, end ));
    }
        
       
// operations returning double []
    public static double []  Vones(int n) {
    double [] data = new double[n];
    for (int i=0; i<n; i++)
        data[i] = 1.0;
    return data;
}

public static double []  Vzeros(int n) {
    double [] data = new double[n];
    for (int i=0; i<n; i++)
        data[i] = 0.0;
    return data;
}


public static double []   Vrand(int n) {
    double [] data = new double[n];
    for (int i=0; i<n; i++)
        data[i] = Math.random();
    return data;
}

public static double []  Vfill( int n, double value) {
    double [] data = new double[n];
    for (int i=0; i<n; i++)
        data[i] = value;
    return  data;
}
    
public static  double []   Vlinspace(double startv,  double endv)   {
    int  nP = 100;  // use 100 as default number of points
    double []  v = new double [nP];
    double  dx = (startv-endv)/(nP-1);
    for (int i = 0; i< nP; i++)
	v[i] = startv +  i * dx;

    return v;
}

public static double []   Vlinspace(double startv,  double endv, int nP)   {
    double []  v = new double [nP];
    double  dx = (startv-endv)/(nP-1);
    for (int i = 0; i< nP; i++)
	v[i] = startv +  i * dx;

    return v;
}

// use by default logspace=10
public static  double [] Vlogspace(double  startOrig,  double endOrig) {
    int nP = 100;
    return Vlogspace(startOrig, endOrig, nP, 10.0);
}

// use by default logspace=10
public static  double []  Vlogspace(double  startOrig,  double endOrig,  int nP) {
    return Vlogspace(startOrig, endOrig, nP, 10.0);
}

public  static double [] Vlogspace(double startOrig,  double endOrig, int  nP,  double  logBase)  {
    boolean  positiveTransformed = false;
    double    transformToPositive = 0.0;
                
    double  start = startOrig;  double   end=endOrig;   // use these values to handle negative values
    boolean  axisReversed = false;
    if (start > end)   {   // reverse axis
            start = endOrig; end = startOrig; axisReversed = true;
        }
                
     if (start <= 0)  {  // make values positive
             transformToPositive = -start+1;  start = 1;     
             end = end+transformToPositive;  positiveTransformed = true;
        }
     double    logBaseFactor = 1.0/java.lang.Math.log10(logBase);
     double    start_tmp = java.lang.Math.log10(start)*logBaseFactor;
     double    end_tmp = java.lang.Math.log10(end)*logBaseFactor;
     //println("logBaseFactor = "+logBaseFactor+"  start_tmp = "+start_tmp+"  end_tmp = "+end_tmp)
                
    double  []   values = new double [nP];
    double  dx     = (end_tmp-start_tmp) / (nP-1);
    for (int i=0; i<nP; i++) 
        values[i] = java.lang.Math.pow( logBase, (start_tmp +  i * dx));
		
        if  (positiveTransformed)    // return to the original range of values
                {
              for ( int i=0; i<nP; i++)
		  values[ i ]  -=  -transformToPositive;
		  start -= transformToPositive;
                }

                if (axisReversed)  {
                    double [] valuesNew = new double [nP];
                    valuesNew[0] = values[nP-1];
                    for ( int i = 1; i<nP; i++)  {
                        valuesNew[i]  = valuesNew[ i-1]-(values[i]-values[i-1]);
                    }
                    return valuesNew;
                }
                          
     return values;
}


                
    public double [] Vinc(double begin, double pitch, double end)  {
           if (begin > end && pitch >0)
               return DoubleArray.increment(end, pitch, begin);
           if (begin < end && pitch < 0)
               return DoubleArray.increment(end, pitch, begin);
	   return DoubleArray.increment(begin, pitch, end );
    }
        
       


public String   toString() {
   StringBuilder  sb = new StringBuilder();
     
  if  (groovySci.PrintFormatParams.getVerbose()==true)  {
 
   String  formatString = "0.";
   for (int k = 0; k < groovySci.PrintFormatParams.vecDigitsPrecision; k++) 
       formatString += "0";
   
    DecimalFormat digitFormat = new DecimalFormat(formatString);
    digitFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(new Locale("us")));
    
     int  mxElems = length();
     String  moreElems = "";
     if (mxElems > groovySci.PrintFormatParams.vecMxElemsToDisplay )  {
          // vector has more elements than we can display
         mxElems = groovySci.PrintFormatParams.vecMxElemsToDisplay;
         moreElems = " .... ";
     }
    int  i=0;
     while (i < mxElems) {
       sb.append(digitFormat.format(v[i])+"  ");
        i++;
       }
     
  }
  
   return sb.toString();
}

/*
public String toString() {
    StringBuilder cb = new StringBuilder("Vec("+this.length()+") =  [");
    int mx = mxVecLenToString;
    if (mx > this.length())
        mx = this.length();
    for (int k=0; k < mx-1; k++)
        cb.append(GlobalValues.fmtString.format(v[k])+", ");
    cb.append(GlobalValues.fmtString.format(v[mx-1])+"]");
    
    return cb.toString();
}
*/
 }