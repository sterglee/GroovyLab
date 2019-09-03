package groovySci.math.array;

public class DoubleRangeWithStep extends groovy.lang.ObjectRange {
    public double mfrom, mto, mstep;
    public DoubleRangeWithStep(double from, double to) {
        super(from, to); mfrom = from; mto = to; mstep = 1.0;
    }
    
    
    public DoubleRangeWithStep(double from, double  to, double step) {
        super(from, to); mfrom = from; mto = to;  mstep = step;
    }
    
    public Vec  step(double step) { mstep = step; return Vec.vinc(step, step, mto); } 
}
