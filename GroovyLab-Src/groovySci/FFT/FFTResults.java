package groovySci.FFT;

import static groovySci.math.array.Matrix.*; 

public class FFTResults {

    public double [] realFFTs;   // real part of FFT
    public double [] imFFTs;   // imaginary part of FFT
    public double[] freqs; // the frequency axis
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (realFFTs != null)
          sb.append("realFFTs = "+ printArray(realFFTs));
        else
          sb.append("realFFTs = null \n");  
        if (imFFTs != null)
          sb.append("imFFTs = "+ printArray(imFFTs));
        else
          sb.append("imFFTs = null \n"); 
        if (freqs!=null)
          sb.append("freqs = "+ printArray(freqs));
        else
          sb.append("freqs = null");  
        
        return sb.toString();
    }
}
