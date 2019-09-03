package JFplot;

import java.util.*;
import java.io.*;
import java.lang.*;



// Parallel colt versions of these routines...
// I saw no speedup on MacBook Pro when using 
// these... would be interesting to try on a many core cpu.
// import cern.colt.matrix.tobject.*;
// import cern.colt.matrix.tobject.impl.*;
// import cern.colt.list.tdouble.*;

import cern.colt.matrix.*;
import cern.colt.matrix.tdcomplex.impl.*;
import cern.colt.list.*;
import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix1D;

import groovy.lang.*;
import groovy.lang.Closure;
import groovy.lang.IntRange;
import org.codehaus.groovy.runtime.*;


import java.util.*;
import java.io.*;
import java.lang.*;


// Parallel colt versions of these routines...
// I saw no speedup on MacBook Pro when using 
// these... would be interesting to try on a many core cpu.
// import cern.colt.matrix.tobject.*;
// import cern.colt.matrix.tobject.impl.*;
// import cern.colt.list.tdouble.*;


/***
* An iterator for a row or a column of a table
*/
class DoubleVectorIterator implements Iterator{
  DoubleVector table;
  int idx = 0;
  
  DoubleVectorIterator(DoubleVector t){
    table = t;
  }
  
  public boolean hasNext(){
    if (idx < table.size()) return(true);
    else return(false);
  }
  
  public Double next(){
    Double rval = table.get(idx);
    idx++;
    return(rval);
  }
  public void remove(){}
}

/***
* A row or a column in the table.   Specifically wrapper to make the 
* DenseDoubleMatrix1D returned by matrix.viewColumn/Row 
* into something that is iterable, since DenseDoubleMatrix1D doesn't
* implement iterable (probably because colt is old as dirt). <br>
* 
*/
//class DoubleVector extends DefaultGroovyMethodsSupport implements Iterable{
public class DoubleVector extends GroovyObjectSupport implements Iterable{
  
  DenseDoubleMatrix1D data;
	public String[] names;  	
	public HashMap<String,Integer> name2Idx = new HashMap<String,Integer>();


	public DenseDoubleMatrix1D getData(){return(data);}

	public DoubleVector(int n,String[] theNames,HashMap<String,Integer> nameMap){
		data = new DenseDoubleMatrix1D(n);
		names = theNames;
		name2Idx = nameMap;		
	}

  public DoubleVector(DoubleMatrix1D dom,String[] theNames,HashMap<String,Integer> nameMap){
    data = (DenseDoubleMatrix1D) dom;
		names = theNames;
		name2Idx = nameMap;
  }

	public DoubleVector(ArrayList<Double> vec,String[] theNames,HashMap<String,Integer> nameMap){
		data = new DenseDoubleMatrix1D(vec.size());
		for(int i = 0;i < vec.size();i++){
			data.setQuick(i,vec.get(i));
		}
		names = theNames;
		name2Idx = nameMap;
	}
	
	public DoubleVector(double[] values,String[] theNames,HashMap<String,Integer> nameMap){
		data = new DenseDoubleMatrix1D(values);
		names = theNames;
		name2Idx = nameMap;
	}


	public DoubleVector(int n){
		data = new DenseDoubleMatrix1D(n);
	}

  public DoubleVector(DoubleMatrix1D dom){
    data = (DenseDoubleMatrix1D) dom;
  }

	public DoubleVector(ArrayList<Double> vec){
		data = new DenseDoubleMatrix1D(vec.size());
		for(int i = 0;i < vec.size();i++){
			data.setQuick(i,vec.get(i));
		}
	}
	
	public DoubleVector(double[] values){
		data = new DenseDoubleMatrix1D(values);
	}
	
	
  
  public long size(){ return(data.size()); }
  public Double get(int idx){ return(data.get(idx)); }  
  public void set(int idx,Double value){ data.set(idx,value); }
  
  public DoubleVectorIterator iterator(){
    return(new DoubleVectorIterator(this));
  }

	public double sum(){
		return(data.zSum());
	}
	
	public double sd(){
		double sumDif = 0;
		double mean = mean();
		int N = (int) size();
		for(int i = 0;i < N;i++){
			sumDif += Math.pow((this.get(i) - mean),2);
		}
		sumDif = sumDif/(double)(N-1);
		double rval = Math.sqrt(sumDif);
		return(rval);
	}
	
	public double max(){
		double max = Double.NEGATIVE_INFINITY;
		for(int i = 0;i < data.size();i++){
			double val = data.getQuick(i);
			if (val > max) max = val;
		}
		return(max);
	}
	
	public double min(){
		double min = Double.POSITIVE_INFINITY;
		for(int i = 0;i < data.size();i++){
			double val = data.getQuick(i);
			if (val < min) min = val;
		}
		return(min);
	}
	
	public double mean(){
		return(data.zSum()/(double)data.size());
	}
  
	public void putAt(int idx,double val){
		if (idx < 0) idx =(int) data.size()+idx; // 5 -1 = 4
		data.setQuick(idx,val);
	}

  public Double getAt(int idx){
    if (idx < 0) idx =(int) data.size()+idx; // 5 -1 = 4
    return(data.getQuick(idx));
  }

 	public Double getAt(String name){
		int idx = name2Idx.get(name);
    return(data.getQuick(idx));
  }


	public double[] asArray(){
		double[] rval = new double[(int)size()];
		data.toArray(rval);	
		return(rval);
	}
    
	public String toString(){
		return(data.toString());
	}
	
	/**
	* Attempts to return the vector as an instance of the given class (e.g. ArrayList, Set, 
	* HashSet). 
	*/ 
	public Object asType(Class clazz) {
		if (clazz.equals(java.util.ArrayList.class)) {			
			ArrayList rval = new ArrayList();
			for(int i = 0;i < data.size();i++){
				rval.add(data.get(i));
			}
			return(rval);			
		}else if ((clazz.equals(java.util.Set.class) ||
							 clazz.equals(java.util.HashSet.class))){
			HashSet rval = new HashSet();
			for(int i = 0;i < data.size();i++){
				rval.add(data.get(i));
			}
			return(rval);
		}else{
			String msg = "Can't cast TableMatrix1D to "+clazz;
			throw new ClassCastException(msg);
		}
	}

	
	
	
	public DoubleVector getRange(int start,int end){
		return(getAt(new IntRange(start,end)));
	}
  
  /**
  * Returns a view corresponding to the given range. 
  */ 
  public DoubleVector getAt(IntRange r){       
		int from = r.getFromInt();
		int to = r.getToInt();
		
		// when one value is negative, to and from will be reversed...
		
		//System.err.println("from: "+from); 
		//System.err.println("to: "+to);		
		
		if (from < 0) {
			int oldto = to;
			to =(int) data.size()+from; // 5 -1 = 4
			from = oldto;						
			if (from < 0){
				from = (int)data.size()+from;
			}
		}
		//System.err.println("from adjusted: "+from); 
		//System.err.println("to adjusted: "+to);		

    int start = from;
    int width = to-start+1; 

		//System.err.println("start: "+start); 
		//System.err.println("width: "+width);		

    return(new DoubleVector(data.viewPart(start,width)));
  }  


	public static void createNameMap(String[] names,HashMap<String,Integer> name2IdxMap){	  
	  for(int i = 0;i < names.length;i++){
	    name2IdxMap.put(names[i],i);
	  }
	}

}
