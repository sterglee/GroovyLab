package groovySci;


//  parameters that determine the format with which numbers and arrays are printed
public class  PrintFormatParams {

    public static boolean  verboseFlag = true;   // controls verbosing the results of  toString()
    public static void  setVerbose(boolean vflag)  { verboseFlag = vflag; }
    
    public static boolean getVerbose() { return verboseFlag; }
    
    public static int  vecDigitsPrecision = 4;   // controls pecision in toString()  
    public static int getVecDigitsPrecision() { return  vecDigitsPrecision; }
    public static int vecMxElemsToDisplay = 20;    // controls maximum number of Vector elements to display
    public static int  setVecMxElemsToDisplay( int  mxElems) { 
        int prevElems = vecMxElemsToDisplay; 
        vecMxElemsToDisplay = mxElems; 
        return prevElems;}
    public static void  setVecDigitsPrecision(int precision)  { vecDigitsPrecision = precision; }
  
     public static int  matDigitsPrecision = 4;  // controls pecision in toString()  
     public static int  getMatDigitsPrecision() { return matDigitsPrecision; }
     public static int  matMxRowsToDisplay = 6;  
     public static int  matMxColsToDisplay = 6;
     public static int  getMatMxRowsToDisplay() { return  matMxRowsToDisplay; }
     public static int  getMatMxColsToDisplay() { return  matMxColsToDisplay; }
     public static void  setMatMxRowsToDisplay(int nrows)  { matMxRowsToDisplay = nrows; }
     public static void  setMatMxColsToDisplay(int ncols)  { matMxColsToDisplay = ncols; }
     public static void setMatDigitsPrecision(int precision)  { matDigitsPrecision = precision; vecDigitsPrecision = precision; }
}

