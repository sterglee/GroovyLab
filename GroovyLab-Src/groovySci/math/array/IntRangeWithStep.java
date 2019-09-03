package groovySci.math.array;

public class IntRangeWithStep extends groovy.lang.IntRange {

    public IntRangeWithStep(int from, int to) {
        super(from, to); mby=1;
    }
    
    
    public IntRangeWithStep(int from, int to, int sb) {
        super(from, to);  mby = sb;
    }
    public int mby;
    public IntRangeWithStep  by(int st) { mby = st; return this;  } 
}
