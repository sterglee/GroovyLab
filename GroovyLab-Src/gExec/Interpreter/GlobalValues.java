package gExec.Interpreter;

import CCOps.CCOps;
import NROps.NROps;
import edu.emory.mathcs.utils.ConcurrencyUtils;
import gExec.ClassLoaders.ExtensionClassLoader;
import gExec.ClassLoaders.JarClassLoader;
import gExec.gLab.*;
import gExec.gui.*;
import gLabEdit.*;
import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import groovy.util.GroovyScriptEngine;
import groovySci.math.array.Matrix;

import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.swing.*;
import jdk.jshell.JShell;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.eval.TeXUtilities;
import org.matheclipse.core.expression.F;
import org.scilab.modules.javasci.Scilab;


public class GlobalValues
{  
    static public   String  dateOfBuild = " 13-12-2019";       // tracks the date of build    
    
    static public JShell jshell=null;   // evaluate expressions with JShell
    static public jdk.jshell.SourceCodeAnalysis srcAnalyzer=null;
    
    static public TreeMap<String,Object> jshellBindingValues=new TreeMap<String,Object>();
    
    static public TreeMap<String,Object> jshellBindingTypes=new TreeMap<String,Object>();
       // the executor service is used to exploit Java multithreading for asynchronous computation operations and for
    // other tasks, as e.g. matrix multiplication 
  static public ExecutorService execService =   Executors.newFixedThreadPool(ConcurrencyUtils.getNumberOfProcessors());
    
  static public boolean AutoCompletionInitialized = false;
  
    static public ServerSocket groovyLabServerSocket;    // socket for GroovyLab's server
    
    // variables for multithreaded operations
    static public   int multithreadingOpLimit  = 1000;  // a matrix with more elements than this is conidered large, thus use multithreading
     // a multiplication that involves more than that number of elements, is large thus use multithreading
    // if this parameter is not specified by a property, it is computed dynamically at run time
    static public   int mulMultithreadingLimit = -1; 
    static public String  mulMultithreadingLimitProp="undefined";
      // the pendingThreads class allows to cancel task started with the Shift-F6 keystroke
     // however, cancelling Java threads that are not designed for interruption, is an involved and problematic issue
    static public gExec.Interpreter.PendingThreads pendingThreads = new gExec.Interpreter.PendingThreads();
    
    
    static public   boolean   CompileIndy  = false;  // controls whethadder the Groovy compiler uses invokedynamic
    // an AST transformation that transforms BigDecimals to Doubles and results in much faster code
    static public   boolean   CompileDecimalsToDoubles = true;  
    static public   String  jdkTarget = "1.12";
    
    // in "Global" mode code completion of the rsyntaxArea based GroovyLab's editor, provides
    // code completion help based on fixed input strings. This help concerns basic commands 
    // of GroovyLab e.g. the many overloaded versions of the plot() routine etc.
    // in "Groovy" mode the code completion provides code completion help based on
    // information that is extracted with Java Reflection
    static public boolean rsyntaxInGroovyCompletionMode = false; 
    static public String   rsyntaxInGroovyCompletionModeProp = "Global"; 
    static public java.util.ArrayList<String>  groovyResultsForCompletion = new java.util.ArrayList();
   // used for auto-completion,  inspect a class name or inspect an identifier,
    // e.g. we can type javax.swing.JFrame, select it and press Shift-F4 in order to
    // display information on the class javax.swing.JFrame
    static public  boolean inspectClass = false;   
    
   static public GCompletionProvider  providerObject;
   static public DefaultCompletionProvider   provider;
   
  // variables for implementing code completion using Java reflection
   static public JFrame completionFrame;  // the open completion Frame, it is used in order to dispose it with ESC
   static public String  textForCompletion;  // replace this text with the user selection at completion
   static public String  completionText;  // the text that the completion operation inserts
   static public int selectionStart;
   static public int selectionEnd;
   static public boolean methodNameSpecified = false;   // method name specified at completion
   static public boolean performPackageCompletion = false;
      
   // at code completion (with F12) static members are denoted in bold font
   static public Font staticsFont = new Font("Arial", Font.BOLD, 11);
   // at code completion (with F12) instance members are denoted in plain font text
   static public Font instancesFont = new Font("Arial", Font.PLAIN, 11);
   static public Font fontForCompletionListItem = instancesFont; // font to use for rendering the current item
   static public String staticsMarker = "#####";
   static public String nonStaticsMarker = "%%%%%";
   static public boolean [] isStaticMarks;
   static public int maxItemsToDisplayAtCompletions = 15;
    
    /*
    since Groovy Shell does not caches previously executed import statements and classes, 
    GroovyLab implements a code and import buffering scheme based on source code buffers.
    The following variables are used to display and edit convenienly such code
    */ 
    static public RTextScrollPane   bufferedImportsScrollPane;
    static public RTextScrollPane   t;
    static public RSyntaxTextArea  bufferedImportsTextArea;
    static public RSyntaxTextArea  bufferedCodeTextArea;
    
    
    static public String scriptClassName = "gScript";   // for standalone scripts
    static public boolean nativeLookAndFeel = false;
           
   
   static public boolean  rememberSizesFlag = true;   // remembers the configured sizes for the frames
   static public boolean useAlwaysDefaultImports = true;

   static public int consoleCharsPerLine;
  
   static public Desktop  desktop;
   static public boolean useSystemBrowserForHelp = true;

   static public String  userPathsFileName = "GroovyLabUserPaths.txt";  
   static public  JScrollPane  outputPane;     // the System Console output scroll pane

   static public RSyntaxTextArea   globalEditorPane;   // the rsyntaxarea component based GroovyLab editor

   static public String  smallNameFullPackageSeparator = "-->";  
          
   static public org.codehaus.groovy.control.customizers.ImportCustomizer globallmportCustomizer;
           
   static public GroovyClassLoader groovyClassLoader = null;
   static public CompilerConfiguration GroovyClassLoaderCompilerConfiguration = null;
                   
   static  public String paneFontName = "Times New Roman";
   static  public int paneFontSize = 16;
   static  public boolean paneFontSpecified = true;
        
        // for main menus
   static  public String uiFontName = "Times New Roman";
   static  public String  uiFontSize = "14";
   static public Font uifont = new Font(uiFontName, Font.PLAIN, Integer.parseInt(uiFontSize));
    
        // for popup menus
   static  public String puiFontName = "Times New Roman";
   static  public String  puiFontSize = "14";
   static public Font puifont = new Font(puiFontName, Font.PLAIN, Integer.parseInt(puiFontSize));
    
        // for rest gui drawing
    static  public String guiFontName = "Times New Roman";
    static  public String  guiFontSize = "14";
    static public Font guifont = new Font(guiFontName, Font.PLAIN, Integer.parseInt(guiFontSize));
    
        // for buttons drawing
        static  public String buiFontName = "Times New Roman";
        static  public String  buiFontSize = "14";
        static public Font buifont = new Font(guiFontName, Font.PLAIN, Integer.parseInt(buiFontSize));
    
        // for Help html
        static  public String htmlFontName = "Times New Roman";
        static  public String htmlFontSize = "16";
        static public Font htmlfont = new Font(htmlFontName, Font.PLAIN, Integer.parseInt(htmlFontSize));
    
        static  public String outConsoleFontName = "Times New Roman";
        static  public String  outConsoleFontSize = "14";

        static  public String  gLabConsoleFontName = "Times New Roman";
        static  public String  gLabConsoleFontSize = "14";
        
        
   static public boolean pathUpdateOnSaves = false;
     // Defaults for Main Help
    static public FileTreeExplorer currentFileExplorer = null;
    
    static public  String toolboxStartUpcode = "";
    static public String selectedExplorerPath; 

    static public double log2Conv = Math.log(2.0);   
    
    static public String currentExpression;   // holds the text of the current expression in execution
    
    public static SysUtils.ConsoleWindow  consoleOutputWindow;
     
    public static Dimension ScreenDim;

    
    static public boolean truncateOutput  = false;   // displays all the output results from the gLab interpreter without truncating
    static public boolean globalVerboseOff = false;
    static public boolean displayAtOutputWindow = false;   // controls displaying at output window
        
        static public   DecimalFormat fmtString = new  DecimalFormat("0.0000");
        static public   DecimalFormat fmtMatrix = new DecimalFormat("0.000"); // format Matrix results
        static public   int  doubleFormatLen = 4; // how many digits to display for doubles, Matrix
        // these colors of the console input window  indicate the corresponding gLab operating modes
        static public  Color  ColorGroovySci = new Color(255, 255, 255);
        static public Map desktophints;
    
        static public boolean effectsEnabled = false;
        static public boolean mainToolbarVisible = true;
        static public float alphaComposite = 0.5f;

         /**Constant with the application title.*/
        static public String TITLE=buildTitle();


        static public long  timeForTic; // save the current time in milliseconds to implement tic-toc functionality
        
        static public Binding groovyBinding = null;   // the Groovy binding of all the worskpace variables
        static public boolean groovyJarClassesLoaded = false;  // controls the loading/reloading of Groovy classes
       
        static public GroovyShell  GroovyShell = null;  // the Groovy shell used
        static public GroovyScriptEngine groovyScriptEngine = null;
        
        static public  java.util.LinkedList<String>  GroovyShellPathsList = null;  // the current paths list with which the GroovyShell's classloader is inited
        static public boolean retrieveAlsoMethods = false; // retrieve also declared methods from toolboxes
        static public boolean jarToolboxesClassPathUpdatedForGroovyShell = false;  // controls whether we have the .jar toolboxes at the classpath
        static public boolean jarToolboxesClassPathUpdatedForGroovyClassLoader = false;  // controls whether we have the .jar toolboxes at the classpath
        static public boolean jarToolboxesClassPathUpdatedForGroovyScriptEngine = false;  // controls whether we have the .jar toolboxes at the classpath
        static public ExtensionClassLoader extensionClassLoader = null;
        static public Vector bindingVarValues = new Vector();  // bindings for scripting interfacing 
        static public Vector bindingVarNames = new Vector();
        static public int bindingCnt = 0; // count of binded variables
        
        static public ImageIcon groovyIcon;        
        static public String bufferingImports ="";
        static public String bufferingCode="";
        
        static public String [] jshellBasicGlobalImports={
                  "import groovySci.math.array.Vec; ",  // Vector class
                    "import static groovySci.math.array.Vec.*; ",
        
                    "import groovySci.math.array.Matrix; ",  // Matrix class
                    "import static groovySci.math.array.Matrix.*; ",  
                    "import groovySci.math.array.Mat1D; ",  // Matrix class
                    "import static groovySci.math.array.Mat1D.*; ",  // Matrix class
         "import org.jblas.DoubleMatrix; ", // Matrix class
                    "import org.jblas.DoubleMatrix.*; ",  // Matrix class
        
                    "import groovySci.math.array.PMatrix; ",  // Matrix class
                    "import static groovySci.math.array.PMatrix.*; ",  
                    "import groovySci.math.array.CCMatrix; ",  // Matrix class
                    "import static groovySci.math.array.CCMatrix.*; ",  
                    "import groovySci.math.array.Sparse; ",  // Sparse Matrix class
                    "import static groovySci.math.array.Sparse.*; ",  
                    "import groovySci.math.array.JILapack; ",  // JLapack Matrix class
                    "import static groovySci.math.array.JILapack.*; ",  
                    "import Jama.*;",
                   "import static groovySci.math.plot.plot.*;  ",     // plotting routines 
                   "import static groovySci.math.plot.plotFunctional.*;   ",
                   "import static groovySci.math.plot.plotAdaptiveFunctional.*;   ",
    
                   "import groovySci.math.plot.PlotController;   ",
                
                    "import static groovySci.math.array.MatrixConvs.*;   ",  // conversions between GroovySci Matrix and matrices of other libraries
                
                // JFreeChart imports
                  "import JFreePlot.*;   ",
                  "import static JFreePlot.jFigure.*;   ",
                  "import static JFreePlot.jPlot.*;  ",
                
                   "import java.awt.*;   ",
                   "import javax.swing.*;   ",   // Java standard UI and graphics support
                   "import static groovySci.math.io.MatIO.*;  ",   // support for .mat Matlab files
                   "import java.awt.event.*;   ", 
                   "import java.text.DecimalFormat;   ",
                   "import static groovySciCommands.BasicCommands.*;  ",  // support for GroovySci's console commands
                   "import  static  groovySci.math.array.DoubleArray.*;  ",
                  "import groovySci.math.array.*;   ",
                   "import groovySci.FFT.ApacheFFT;   ",
                  "import static groovySci.FFT.ApacheFFT.*;  ",
                  "import static groovySci.FFT.FFTCommon.*;  ",
                
                
                "import static java.lang.Math.*;    ",    // standard Java math routines, allows calling directly e.g sin(9.8) instead of Math.sin(9.8)
        };
        static public    String  basicGlobalImports = 
                    "import groovySci.math.array.PMatrix; \n"+  // Matrix class
                    "import static groovySci.math.array.PMatrix.*; \n"+  
                    "import groovySci.math.array.CCMatrix; \n"+  // Matrix class
                    "import static groovySci.math.array.CCMatrix.*; \n"+  
                    "import groovySci.math.array.Sparse; \n"+  // Sparse Matrix class
                    "import static groovySci.math.array.Sparse.*; \n"+  
                    "import groovySci.math.array.JILapack; \n"+  // JLapack Matrix class
                    "import static groovySci.math.array.JILapack.*; \n"+  
                    "import Jama.*;\n"+
                   "import static groovySci.math.plot.plot.*;\n"+     // plotting routines 
                   "import static groovySci.math.plot.plotFunctional.*; \n"+
                   "import static groovySci.math.plot.plotAdaptiveFunctional.*; \n"+
    
                   "import groovySci.math.plot.PlotController; \n"+
                
                    "import static groovySci.math.array.MatrixConvs.*; \n"+  // conversions between GroovySci Matrix and matrices of other libraries
                
                    "import static gExec.Interpreter.MatlabConnection.*; \n"+
                    "import static gExec.Interpreter.SciLabConnection.*; \n"+
                
                // JFreeChart imports
                  "import JFreePlot.*; \n"+
                  "import static JFreePlot.jFigure.*; \n"+
                  "import static JFreePlot.jPlot.*;\n"+
                
                   "import java.awt.*; \n"+
                   "import javax.swing.*; \n"+   // Java standard UI and graphics support
                   "import static groovySci.math.io.MatIO.*;\n"+   // support for .mat Matlab files
                   "import java.awt.event.*; \n"+ 
                   "import groovy.swing.SwingBuilder; \n"+
                   "import java.text.DecimalFormat; \n"+
                   "import static groovySciCommands.BasicCommands.*;\n"+  // support for GroovySci's console commands
                   "import  static  groovySci.math.array.DoubleArray.*;\n"+
                  " import JSci.maths.*; \n"+
                  "import JSci.maths.wavelet.*; \n"+
                  "import JSci.maths.wavelet.daubechies2.*; \n"+
                  "import groovySci.math.array.*; \n"+
                   "import groovySci.FFT.ApacheFFT; \n"+
                  "import static groovySci.FFT.ApacheFFT.*;\n"+
                  "import static groovySci.FFT.FFTCommon.*;\n"+
                
                // some very useful packages from Numerical Recipes
                "import NR.*;\n"+
                "import static NR.gaussj.* ;\n"+
                "import com.nr.sp.*;\n"+
                
                "import static java.lang.Math.*;  \n"+    // standard Java math routines, allows calling directly e.g sin(9.8) instead of Math.sin(9.8)
                   
                           "import groovySci.math.array.Vec; \n"+  // Vector class
                    "import static groovySci.math.array.Vec.*; \n"+  
                    "import groovySci.math.array.Matrix; \n"+  // Matrix class
                    "import static groovySci.math.array.Matrix.*; \n"+  
                    "import groovySci.math.array.Mat1D; \n"+  // Matrix class
                    "import static groovySci.math.array.Mat1D.*; \n"+  // Matrix class
         "import org.jblas.DoubleMatrix; \n"+  // Matrix class
                    "import org.jblas.DoubleMatrix.*; \n";  // Matrix class
        
        
       
          static public String standAloneImports = basicGlobalImports+
                   "\n\n // Expand Groovy's runtime \n"+
                   "\n expG = new expandRunTime.expandGroovy(); \n"+
                    "\nexpG.run(); \n\n";

        
           // Console Configuration
        static String defaultFontName = "Times New Roman";
        static String  defaultFontSize = "16";
        static public Font   defaultTextFont; 
        
        static public String detailHelpStringSelected="";
     
        static public boolean hostIsUnix = File.pathSeparatorChar == ':'?  true  :  false;   // Unix like system or Windows?
        static public boolean hostIsWin = !hostIsUnix;
        static public boolean hostIsWin64 = hostIsWin && System.getProperty("os.arch").toLowerCase().contains("amd64");
        static public boolean hostIsLinux = System.getProperty("os.name").toLowerCase().contains("linux");
        static public boolean hostIsLinux64 = hostIsLinux && System.getProperty("os.arch").toLowerCase().contains("amd64");
        static public boolean hostIsFreeBSD = System.getProperty("os.name").toLowerCase().contains("freebsd");
        static public boolean hostIsSolaris = System.getProperty("os.name").toLowerCase().contains("sunos");
        static public boolean hostIsMac =    System.getProperty("os.name", "").toLowerCase().contains("mac");
      
        
        static public boolean hostNotWinNotLinux = ( (hostIsUnix==true)  &&  (hostIsLinux==false) ); // Unix-like OS, not Linux  e.g. FreeBSD, MacOS, Solaris etc.
        
        static public boolean startingFromNetbeans = false;
        static public String jarFilePath;  // the path that contains the main jar file
        static public String fullJarFilePath;
        static public String gLabLibPath="";
        static public String gLabHelpPath = "";
        
        
        
        
// jLabClassPath is the directory that serves as the "root" for external class retrieval. 
 // This directory and all its subdirectories are searched for both .j script files and .class files        
        static public String gLabClassPath="";
        
  // GroovySciClassPath is the directory that serves as the "root" for Groovy Class Loader class retrieval. 
 // This directory and all its subdirectories are searched for both .groovy script files and .class files        
        static public String GroovySciClassPath; 
        static public Vector GroovySciClassPathComponents=new Vector();
        
        static public Vector  favouriteElements = new Vector();


        
        static public String gLabPropertiesFile;  // the file for obtaining configuration properties
        static public String workingDir;  // the current working directory
        static public String homeDir;  // the user's home directory
        
        static public Vector jartoolboxesForGroovySci=new Vector();  // load Java classes for Groovy mode from these toolboxes 
        static public HashMap<String, Boolean>  jartoolboxesLoadedFlag;  // associates each jar toolbox name with a flag that indicates whether it was loaded or not
        static public JarClassLoader groovyToolboxesLoader;  // the loader that handles the classes supplied with toolboxes
        static public int sizeX = 600;  // the gLab's main console window jFrame size
        static public int sizeY = 400;  
        static public int locX = 100;   // location of gLab's main window
        static public int locY = 100; 
        
        static public int rsizeX = 600;  // the gLab's RSyntaxEditor jFrame size
        static public int rsizeY = 400;  
        static public int rlocX = 10;   // location 
        static public int rlocY = 10; 
        
        static public double figAreaRelSize = 0.9;  // the relative area of the figure plot area
        
    static public int threadCnt = 0;  // the number of threads created

    static public int maxNumberOfRecentFiles = 20;
    static public String gLabRecentFilesList = "gLabRecentFiles.txt";   // the file for storing list of recent files
    
    static public boolean displayDirectory = true; // controls the displaying of working directory at the prompt
    static public char groovyLabPromptChar = '#';  // used to display the command prompt
    static public String groovyLabPromptString = groovyLabPromptChar+" ";
    
    static public WatchWorkspace groovyLabWorkspace = null;  // handles the current workspace configuration

    static public String rulesFound=null;     // keeps the String of the extracted association rules 
    
    public static String [] loadedToolboxesNames;  // the names of the toolboxes
    public static int currentToolboxId = 0; 
    public static int maxNumOfToolboxes = 30;

    public static final double nearZeroValue = 1.0e-10;
    
      
    
    // Graphics Configuration
    public static int  maxPointsToPlot = 40;  // limit on the number of points to plot when in point plot mode
    public static int  plotPointWidth = 2;    // control the size of the point at the plots 
    public static int  plotPointHeight =2; 
    public static int  markLineSize = 5;
    public static int  figGridSizeX = 30; 
    public static int  figGridSizeY = 30;     
    public static int  figFrameSizeX = 800; 
    public static int  figFrameSizeY = 600;     
    public static int  limitForLargeRangeOfValues = 10;
    public static double figZoomScaleFactor = 0.5;
    public static int currentMaxNumberOfZooms = 5;

         // the tabs of the main UI 
    public static final int mainTab = 0;
    public static final int groovySciTab = 1;
    
    
    public  static  JFrame toolbarFrame; 
        
    
    // global variables that change dynamically during a working session
    static public String LibNumAlDir = "numal/";   // where to look for the NUMAL library functions
    static public String  GroovyLabCommandHistoryFile = "GroovyLabCommandHistory.log";
    static public String GroovyLabFavoritePathsFile = "GroovyLabFavoritePaths.log";
    static public int  numOfHistoryCommandsToKeep  = 10;  // size of the command history list 
    static public String DirHavingFile;  // directory having the currently requested file
    static public Properties settings;  // for load/save global properties
    static public String selectedStringForAutoCompletion;
    
    public static AutoCompletionGroovySci  AutoCompletionGroovySci;
    public static AutoCompletionWorkspace autoCompletionWorkspace;
    
    public static Hashtable  autoCompletionDescr = new Hashtable(300);
    public static Hashtable autoCompletionDetails = new Hashtable(300);
    public static int numTokTypes = 0;   // counts the number of token types that the lexical analyzer returns
    
    // Java Help Files
    static public  String  JavaHelpSetName  = "jdk6Help";
    static public  String  GroovyHelpSetName = "GroovySciHelp";
    
    // keep main objects
    public static gLab   gLabMainFrame = null;
        public static int xSizeTab;
    public static int ySizeTab;
    public static AutoCompletionFrame autoCompletionFrame = null;
    public static  gLabEditor  myGEdit = null;
    public static gExec.gui.gLabConsole  jLabConsole  = null;   
    public static gExec.gui.Console  userConsole;         //  used to retrieve the text buffer
  
    public static int  nextTokenType;
    public static int FunctionObjectType;  // keep function token type to avoid multiple if-then (i.e. for speed)
    
    public static JLabel availMemLabel;
    public static Runtime rt;  // the runtime for observing available memory
    public static long memAvailable;  // free memory available
    public static double helpMagnificationFactor=1.0;
    public static File forHTMLHelptempFile;
   
    static public  CSHObject  JavaCSHObject = null;
    static public  CSHObject  GroovyCSHObject = null;
    
    
    static public org.matheclipse.core.eval.EvalUtilities  symUtil;  // symbolic Algebra object 
    static public TeXUtilities texUtil;  // for LaTex displaying\
    static public boolean displayLatexOnEval = true;
    static public int FONT_SIZE_TEX = 18;
  
        // variables for GroovyLab - MATLAB interface
    static public boolean matlabInitedFlag = false;   // whether  GroovyLab - MATLAB connection is inited
    static public MatlabProxyFactory factory = null;
    static public MatlabProxy proxy = null; 
    
    // variables for GroovyLab - SciLab interface
    static public boolean sciLabInitedFlag = false;   // whether  GroovyLab - SciLab connection is inited
    static public Scilab scilabObj  = null; 
    
// variables for NVIDIA CUDA  interface   
    static public boolean useCUDAflag = false;
    static public String useCUDAprop = "false";
    
 static public boolean timedInterruptScriptingOn = false;    //  interruption of scripts after timeout?
    static public long timedInterruptValue = 5000L;   // the default timeout value for scripts
    static public String timedInterruptScriptingOnFlag="false";
    static public String timedInterruptValueProp="5000";
 
  static public Socket sclient = null;     // client socket
  static public InputStream   clientReadStream = null;   // client's read stream
  static public OutputStream  clientWriteStream =  null;   // client's write stream 
  static public DataInputStream    reader = null;   
  static public DataOutputStream     writer = null;
 
    
   // codes for GroovyLab server computations
    static public final int exitCode = -1;   // code for server to exit
    static public final int svdCode = 1;   // code to perform an SVD computation

    static public String serverIP = "127.0.0.1";  // the IP address of the server
    static public int groovyLabServerPort = 8000;   // port on which GroovyLab server is listening


    
    static public String buildTitle() {
      String mainFrameTitle =        "GroovyLab based on Groovy "+org.codehaus.groovy.util.ReleaseInfo.getVersion()+
                   ",   "+System.getProperty("java.vm.name", "").toLowerCase()+",  "+ System.getProperty("os.name", "").toLowerCase()+
                   "  "+ System.getProperty("os.arch", "").toLowerCase()+" ,   "+ "( "+ dateOfBuild+" ) ";
    return mainFrameTitle;
    }
    
    public static void initGlobals()
    {
        
        new GlobalValues();    // it is required to init properly
        
        if (hostIsLinux64)
         new NativeLibsObj(); // init the native libraries
        
           if (Desktop.isDesktopSupported()) 
            desktop = Desktop.getDesktop();
        else
            useSystemBrowserForHelp = false;  // cannot use system browser
     
        GroovySciClassPathComponents = new Vector();
        GlobalValues.fmtString.setDecimalFormatSymbols(new DecimalFormatSymbols(new Locale("us")));
            
        Toolkit tk =Toolkit.getDefaultToolkit();
        desktophints = (Map)(tk.getDesktopProperty("awt.font.desktophints"));

        hostIsUnix = true;
        if (File.separatorChar!='/') 
            hostIsUnix=false;

        myGEdit = null;
        loadedToolboxesNames = new String[maxNumOfToolboxes];
        
        jartoolboxesForGroovySci = new Vector();
        jartoolboxesLoadedFlag = new HashMap<String, Boolean>();
        
        GlobalValues.fmtMatrix.setDecimalFormatSymbols(new DecimalFormatSymbols(new Locale("us")));
        GlobalValues.fmtString.setDecimalFormatSymbols(new DecimalFormatSymbols(new Locale("us")));

        java.util.Map<String, String>  userEnv = System.getenv();

            int idx = GlobalValues.jarFilePath.lastIndexOf(File.separatorChar);
            if (idx==-1) {
                GlobalValues.homeDir = ".";
            }
            else
                GlobalValues.homeDir = GlobalValues.jarFilePath.substring(0, idx);
 
                System.out.println("homeDir= "+GlobalValues.homeDir);
          
            
        GlobalValues.workingDir = System.getProperty("user.dir");
        if (GlobalValues.GroovySciClassPath==null)  GlobalValues.GroovySciClassPath = homeDir;
        
        GlobalValues.DirHavingFile = GlobalValues.workingDir;
    //   GlobalValues.pascalWorkingDir = homeDir;
        
        hostIsUnix = true;
        if (File.separatorChar!='/') 
            hostIsUnix=false;

        myGEdit = null;
        loadedToolboxesNames = new String[maxNumOfToolboxes];
        
        boolean foundConfigFileFlag = false;   //exists configuration file?
     try
        {  
           settings = new Properties();
       
           FileInputStream in = null;
        
             // the GroovyLab's configuration file
           String configFileName = workingDir+File.separatorChar+"Glab.props";
           File configFile = new File(configFileName);
           if (configFile.exists())   {  // configuration file exists
                  in = new FileInputStream(configFile);
                  settings.load(in);   // load the settings
                  foundConfigFileFlag = true;
                  GlobalValues.gLabPropertiesFile = configFileName;
                  }
           }

        catch (IOException e) 
        {
           e.printStackTrace();
        }
         ScreenDim = Toolkit.getDefaultToolkit().getScreenSize();

        if (foundConfigFileFlag == false)   { // configuration file not exists, thus pass default configuration
            
               rememberSizesFlag = false;   // since configured sizes for frames do not exist use the default sizes
            
                //position the frame in the centre of the screen
                int xSizeMainFrame = (int)((double)ScreenDim.width/1.4);
                int ySizeMainFrame = (int)((double)ScreenDim.height/1.4);
                GlobalValues.locX  = (int)((double)ScreenDim.width/10.0);  
                GlobalValues.locY = (int)((double)ScreenDim.height/10.0); 
                GlobalValues.rlocX  = 10;  
                GlobalValues.rlocY = 10; 
               
                GlobalValues.sizeX = xSizeMainFrame;
                GlobalValues.sizeY = ySizeMainFrame;
                
                GlobalValues.rsizeX = xSizeMainFrame;
                GlobalValues.rsizeY = ySizeMainFrame;
                
       
        if (GlobalValues.CompileIndy)
            settings.put("indyProp", "true");
        else 
            settings.put("indyProp", "false");
        
        if (GlobalValues.CompileDecimalsToDoubles)
            settings.put("compileDecimalsToDoublesProp", "true");
        else
            settings.put("compileDecimalsToDoublesProp", "false");
        
            
        if (GlobalValues.useAlwaysDefaultImports)
            settings.put("useAlwaysDefaultImportsProp", "true");
        else
            settings.put("useAlwaysDefaultImportsProp", "false");
            
        
        settings.put("widthProp",  String.valueOf(xSizeMainFrame));
        settings.put("heightProp", String.valueOf(ySizeMainFrame));
        settings.put("xlocProp",  String.valueOf(GlobalValues.locX));
        settings.put("ylocProp",  String.valueOf(GlobalValues.locY));
        
        settings.put("rwidthProp",  String.valueOf(xSizeMainFrame));
        settings.put("rheightProp", String.valueOf(ySizeMainFrame));
        settings.put("rxlocProp",  String.valueOf(GlobalValues.rlocX));
        settings.put("rylocProp",  String.valueOf(GlobalValues.rlocY));
        
        
        settings.setProperty("uiFontNameProp","Times New Roman");
        settings.setProperty("uiFontSizeProp", "14");
        settings.setProperty("outConsFontNameProp", "Lucida");
        settings.setProperty("outConsFontSizeProp", "14");
   
        String  prefix = "/";
        if (hostIsUnix==false)
            prefix = "C:\\";
            
        String initialgLabPath  =prefix;
                
        String userDir = workingDir;
        
        settings.put("GroovySciClassPathProp", initialgLabPath); 
        
        settings.put("gLabWorkingDirProp", userDir);
        
        } // configuration file not exists
            
        
        GlobalValues.fmtMatrix.setDecimalFormatSymbols(new DecimalFormatSymbols(new Locale("us")));
        GlobalValues.fmtString.setDecimalFormatSymbols(new DecimalFormatSymbols(new Locale("us")));
        myGEdit = null;
        
            // init Computer Algebra
         F.initSymbols();
         symUtil = new EvalUtilities();
         EvalEngine EVAL_ENGINE = null;
         texUtil = new TeXUtilities(EVAL_ENGINE);
   
        
    }   
    
    // Initialises the global values 
    public GlobalValues()
    {
        ClassLoader parentClassLoader = getClass().getClassLoader();
        extensionClassLoader = new  ExtensionClassLoader(GlobalValues.gLabClassPath+File.separator+"."+File.separator+GlobalValues.jarFilePath, parentClassLoader);
                   
    }   

    
    
    public static void incrementToolboxCount() {
        currentToolboxId++;
        if (currentToolboxId > maxNumOfToolboxes)  { 
            JOptionPane.showMessageDialog(null, "Maximum toolbox count exceeded", "Cannot load additional toolboxes", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // approximates the matrix size for which multithreading becomes faster than serial implementation
    public static int computeProperMatrixSizeThresholdForMultithreading() {
    
        int testedSize  = 50;  // starting value
        
        while (true)  {
            Matrix m = Matrix.rand(testedSize, testedSize);  // create a matrix to test the serial case vs the multithreaded case
            
            groovySciCommands.BasicCommands.tic();
            
            // benchmark serial multiplication
            Matrix mm = m.multiplyTest(m, false).multiplyTest(m, false).multiplyTest(m, false).multiplyTest(m, false);  // perform a multiplication in order to time it
            double delaySerial = groovySciCommands.BasicCommands.toc();
            
            // benchmark parallel multiplication
            groovySciCommands.BasicCommands.tic();
            Matrix mm2 = m.multiplyTest(m, true).multiplyTest(m, true).multiplyTest(m, true).multiplyTest(m, true);  // perform a multiplication in order to time it
            double delayParallel = groovySciCommands.BasicCommands.toc();
            
            if (delayParallel > delaySerial)   // increment to larger size
                testedSize += testedSize;
            else break;  // exit the loop
                  
        }
        
        return testedSize;  // return size about at which parallel implementation becomes faster
        
        
    } 
    
    
// pass properties readed from settings Property String to the gLab workspace structures
    public static void passPropertiesFromSettingsToWorkspace(Properties settings)
     {
          
         // check if CUDA_PATH is installed
             String cudaPath = System.getenv("CUDA_PATH");
             boolean tryToInitCUDA = false;
             
             if (cudaPath == null)  // CUDA is not installed
                     {
        GlobalValues.useCUDAflag = false;
        settings.setProperty("useCUDAProp", "false");
        System.out.println("You do not have CUDA installed properly, CUDA_PATH is null. Therefore, we cannot activate CUDA related operations");
                  }
             else 
                 tryToInitCUDA = true;
             
         if (tryToInitCUDA  == true) {  // initialize objects for performing CUDA operations
                 if (NativeLibsObj.cudaObj == null) {
                      NativeLibsObj.cudaObj = new CUDAOps.KernelOps();
                      NativeLibsObj.cudaSigObj = new CUDASig.CUDASig();
                  }
             }
         
             String useCUDAPropSetting = settings.getProperty("useCUDAProp");
         if (useCUDAPropSetting != null) {
              GlobalValues.useCUDAflag = Boolean.parseBoolean(useCUDAPropSetting);
         }
             
         String  timedInterruptSetting = settings.getProperty("timedInterruptScriptingOnProp");
         if  (timedInterruptSetting != null) {
             GlobalValues.timedInterruptScriptingOn = Boolean.parseBoolean(timedInterruptSetting);
         }
              
         String timedInterruptValuePropSetting = settings.getProperty("timedInterruptValueProp");
         if (timedInterruptValuePropSetting != null) {
             GlobalValues.timedInterruptValue = Long.parseLong(timedInterruptValuePropSetting);
             }
         
         String mulMultithreadingLimitPropSetting = settings.getProperty("mulMultithreadingLimitProp");
         if (mulMultithreadingLimitPropSetting != null) {
             GlobalValues.mulMultithreadingLimit =Integer.parseInt(mulMultithreadingLimitPropSetting);
         }
         else {
             int matrixSizeForMultithreading = 50; //  computeProperMatrixSizeThresholdForMultithreading();
             GlobalValues.mulMultithreadingLimit = matrixSizeForMultithreading*matrixSizeForMultithreading*matrixSizeForMultithreading;
             System.out.println("setting matrix size threshold for multithreading to: "+GlobalValues.mulMultithreadingLimit);
         }
         
         
         String rsyntaxInGroovyGlobalModeSetting = settings.getProperty("rsyntaxInGroovyCompletionModeProp");
         if (rsyntaxInGroovyGlobalModeSetting != null)
          if (rsyntaxInGroovyGlobalModeSetting.equalsIgnoreCase("Groovy")) {
              GlobalValues.rsyntaxInGroovyCompletionMode = true;
              GlobalValues.rsyntaxInGroovyCompletionModeProp = "Groovy";
          }
          else {
              GlobalValues.rsyntaxInGroovyCompletionMode = false;
                GlobalValues.rsyntaxInGroovyCompletionModeProp = "Global";
          }

         String IPServerProperty = settings.getProperty("serverIPProp");
         if (IPServerProperty != null)
             GlobalValues.serverIP = IPServerProperty;
         
            // native Look and Feel
             String nativeLFProperty =  settings.getProperty("nativeLFProp");
             if (nativeLFProperty != null)
              if (nativeLFProperty.equalsIgnoreCase("true"))
                 GlobalValues.nativeLookAndFeel = true;
             else GlobalValues.nativeLookAndFeel = false;
         
    String indyProperty = settings.getProperty("indyProp");
    if (indyProperty != null)
         if (indyProperty.equalsIgnoreCase("true"))
             GlobalValues.CompileIndy = true;
         else GlobalValues.CompileIndy = false;
       
     String compileDecimalsToDoublesProperty = settings.getProperty("compileDecimalsToDoublesProp");
     if (compileDecimalsToDoublesProperty != null)
         if (compileDecimalsToDoublesProperty.equalsIgnoreCase("true"))
             GlobalValues.CompileDecimalsToDoubles = true;
         else GlobalValues.CompileDecimalsToDoubles = false;
     
 if (GlobalValues.gLabMainFrame != null)
 {
    GlobalValues.gLabMainFrame.setTitle(GlobalValues.TITLE+"  indy = "+GlobalValues.CompileIndy);
    GlobalValues.gLabMainFrame.controlIndyJMenuItem.setText("Toggle INDY - Current state is  "+GlobalValues.CompileIndy);
    GlobalValues.gLabMainFrame.controlDefaultlmportsJMenuItem.setText("Toggle useAlwaysDefaultImports - Current state is  "+GlobalValues.useAlwaysDefaultImports);
      
 }       
        String useAlwaysDefaultImportsProp = settings.getProperty("useAlwaysDefaultImportsProp");
        if (useAlwaysDefaultImportsProp != null)
            if (useAlwaysDefaultImportsProp.equalsIgnoreCase("true"))
                   GlobalValues.useAlwaysDefaultImports = true;
             else GlobalValues.useAlwaysDefaultImports = false;
          
         
        String specifiedDir =  settings.getProperty("gLabWorkingDirProp");         
        if (specifiedDir != null) workingDir = specifiedDir;
        
        String locXstr = settings.getProperty("xlocProp");
        if (locXstr != null)      locX = Integer.parseInt(locXstr);  // locX specified
        String locYstr = settings.getProperty("ylocProp");
        if (locYstr != null)      locY = Integer.parseInt(locYstr);  // locY specified
        
       String sizeXstr = settings.getProperty("widthProp");
        if (sizeXstr != null)      sizeX = Integer.parseInt(sizeXstr);  // sizeX specified
        String sizeYstr = settings.getProperty("heightProp");
        if (sizeYstr != null)      sizeY = Integer.parseInt(sizeYstr);  // sizeY specified
        
        // RSyntaxArea editor frame size
        String rlocXstr = settings.getProperty("rxlocProp");
        if (rlocXstr != null)      rlocX = Integer.parseInt(rlocXstr);  // rlocX specified
        String rlocYstr = settings.getProperty("rylocProp");
        if (rlocYstr != null)      rlocY = Integer.parseInt(rlocYstr);  // rlocY specified
        
       String rsizeXstr = settings.getProperty("rwidthProp");
        if (rsizeXstr != null)      rsizeX = Integer.parseInt(rsizeXstr);  // rsizeX specified
        String rsizeYstr = settings.getProperty("rheightProp");
        if (rsizeYstr != null)      rsizeY = Integer.parseInt(rsizeYstr);  // rsizeY specified
             
        // main menus
        String uiFontName = settings.getProperty("uiFontNameProp");
        if (uiFontName==null) uiFontName= GlobalValues.uiFontName;
        String uiFontSize = settings.getProperty("uiFontSizeProp");
        if (uiFontSize==null) uiFontSize= GlobalValues.uiFontSize;
        GlobalValues.uiFontName = uiFontName;
        GlobalValues.uiFontSize = uiFontSize;
        GlobalValues.uifont = new Font(GlobalValues.uiFontName, Font.PLAIN, Integer.parseInt(GlobalValues.uiFontSize));
                
          // pop-up menus
        String puiFontName = settings.getProperty("puiFontNameProp");
        if (puiFontName==null)  puiFontName= GlobalValues.puiFontName;
        String puiFontSize = settings.getProperty("puiFontSizeProp");
        if (puiFontSize==null) puiFontSize= GlobalValues.puiFontSize;
        GlobalValues.puiFontName = puiFontName;
        GlobalValues.puiFontSize = puiFontSize;
        GlobalValues.puifont = new Font(GlobalValues.puiFontName, Font.PLAIN, Integer.parseInt(GlobalValues.puiFontSize));
        
          // general GUI components
        String guiFontName = settings.getProperty("guiFontNameProp");
        if (guiFontName==null)  guiFontName= GlobalValues.guiFontName;
        String guiFontSize = settings.getProperty("guiFontSizeProp");
        if (guiFontSize==null) guiFontSize= GlobalValues.guiFontSize;
        GlobalValues.guiFontName = guiFontName;
        GlobalValues.guiFontSize = guiFontSize;
        GlobalValues.guifont = new Font(GlobalValues.guiFontName, Font.PLAIN, Integer.parseInt(GlobalValues.guiFontSize));
        
        
          // html components
        String htmlFontName = settings.getProperty("htmlFontNameProp");
        if (htmlFontName==null)  guiFontName= GlobalValues.htmlFontName;
        String htmlFontSize = settings.getProperty("htmlFontSizeProp");
        if (htmlFontSize==null) htmlFontSize= GlobalValues.htmlFontSize;
        GlobalValues.htmlFontName = htmlFontName;
        GlobalValues.htmlFontSize = htmlFontSize;
        GlobalValues.htmlfont = new Font(GlobalValues.htmlFontName, Font.PLAIN, Integer.parseInt(GlobalValues.htmlFontSize));
    
              // buttons components
        String buiFontName = settings.getProperty("buiFontNameProp");
        if (buiFontName==null)  buiFontName= GlobalValues.buiFontName;
        String buiFontSize = settings.getProperty("buiFontSizeProp");
        if (buiFontSize==null) buiFontSize= GlobalValues.buiFontSize;
        GlobalValues.buiFontName = buiFontName;
        GlobalValues.buiFontSize = buiFontSize;
        GlobalValues.buifont = new Font(GlobalValues.buiFontName, Font.PLAIN, Integer.parseInt(GlobalValues.buiFontSize));
        
        String outConsFontName = settings.getProperty("outConsFontNameProp");
        if (outConsFontName==null) outConsFontName= GlobalValues.outConsoleFontName;
        String outConsFontSize = settings.getProperty("outConsFontSizeProp");
        if (outConsFontSize==null) outConsFontSize= GlobalValues.outConsoleFontSize;
        GlobalValues.outConsoleFontName = outConsFontName;
        GlobalValues.outConsoleFontSize = outConsFontSize;
        
        
        String gLabConsFontName = settings.getProperty("gLabConsFontNameProp");
        if (gLabConsFontName==null) gLabConsFontName= GlobalValues.gLabConsoleFontName;  
        String gLabConsFontSize = settings.getProperty("gLabConsFontSizeProp");
        if (gLabConsFontSize==null)  gLabConsFontSize= GlobalValues.gLabConsoleFontSize;
        GlobalValues.gLabConsoleFontName = gLabConsFontName;
        GlobalValues.gLabConsoleFontSize = gLabConsFontSize;
        
        //  Decimal digit formatting properties
        String vecDigitsSetting = settings.getProperty("VecDigitsProp");
        if (vecDigitsSetting != null) {
            int vprec = Integer.parseInt(vecDigitsSetting);
            groovySci.PrintFormatParams.setVecDigitsPrecision(vprec);
          }
         
        String matDigitsSetting = settings.getProperty("MatDigitsProp");
        if (matDigitsSetting != null) {
            int mprec = Integer.parseInt(matDigitsSetting);
            groovySci.PrintFormatParams.setMatDigitsPrecision(mprec);
        }
         
         String mxRowsSetting = settings.getProperty("mxRowsProp");
         if (mxRowsSetting != null) {
            int mxrows = Integer.parseInt(mxRowsSetting);
            groovySci.PrintFormatParams.setMatMxRowsToDisplay(mxrows);
         }
        
         String mxColsSetting = settings.getProperty("mxColsProp");
         if (mxColsSetting != null)  {
            int mxcols = Integer.parseInt(mxColsSetting);
            groovySci.PrintFormatParams.setMatMxColsToDisplay(mxcols);
         }
         
         String verboseOutputSetting = settings.getProperty("verboseOutputProp");
         if (verboseOutputSetting!=null)
           if (verboseOutputSetting.equalsIgnoreCase("true"))
              groovySci.PrintFormatParams.setVerbose(true);
           else
              groovySci.PrintFormatParams.setVerbose(false);
        
          
        boolean paneFontSpecified = true;
        String paneFontName = settings.getProperty("paneFontNameProp");
        if (paneFontName!=null)  
             GlobalValues.paneFontName = paneFontName;
        else
            paneFontSpecified = false;
        String paneFontSize = settings.getProperty("paneFontSizeProp");
        if (paneFontSize!=null)   
            GlobalValues.paneFontSize =  Integer.valueOf(paneFontSize);
        else
            paneFontSpecified = false;
      
        GlobalValues.paneFontSpecified = paneFontSpecified;
      
    
        
        GroovySciClassPath  = settings.getProperty("GroovySciClassPathProp");
        if (GroovySciClassPath== null)
            GroovySciClassPath = homeDir;
        GroovySciClassPathComponents.clear();
        if (GroovySciClassPath != null)
         GlobalValues.updatePathVectors(GroovySciClassPathComponents, GroovySciClassPath, false);
        if (GlobalValues.GroovySciClassPath.contains(homeDir)==false)
             GroovySciClassPathComponents.add(homeDir);
        
        
         
    } 
    
 // updates the paths of targetVector by adding additionalPaths and any subdirectories
public static void updatePathVectors(Vector targetVector,  String additionalPaths, boolean recurse) {
    if (targetVector != null)  {
            StringTokenizer  tokenizer;
            if (hostIsUnix) tokenizer = new StringTokenizer(additionalPaths, "\n:\t ");
            else tokenizer = new StringTokenizer(additionalPaths,"\n;\t ");
            while (tokenizer.hasMoreTokens())  {  // construct full paths to search for j-files
                String nextToken = tokenizer.nextToken()+File.separatorChar;
                if (recurse == false)
                    targetVector.add(nextToken);
                else
                    gLabUtils.appendAllSubDirectories(nextToken, targetVector);
          }
          
    }
}
    
// pass properties from the gLab workspace structures to the settings Property String to 
    public static void passPropertiesFromWorkspaceToSettings(Properties settings)
     {
         
         settings.setProperty("serverIPProp", GlobalValues.serverIP);
         
         settings.setProperty("useCUDAProp", Boolean.toString(GlobalValues.useCUDAflag));
         
         settings.setProperty("timedInterruptScriptingOnProp",  Boolean.toString(GlobalValues.timedInterruptScriptingOn));
         
         settings.setProperty("timedInterruptValueProp", Long.toString(GlobalValues.timedInterruptValue));
         
         settings.setProperty("mulMultithreadingLimitProp", Integer.toString(GlobalValues.mulMultithreadingLimit));
         
         if (GlobalValues.rsyntaxInGroovyCompletionMode == true)
             settings.setProperty("rsyntaxInGroovyCompletionModeProp", "Groovy");
         else
             settings.setProperty("rsyntaxInGroovyCompletionModeProp", "Global");

        settings.setProperty("nativeLFProp", Boolean.toString(GlobalValues.nativeLookAndFeel));
        
        settings.setProperty("indyProp", Boolean.toString(GlobalValues.CompileIndy));
        settings.setProperty("compileDecimalsToDoublesProp", Boolean.toString(GlobalValues.CompileDecimalsToDoubles));
        
        settings.setProperty("useAlwaysDefaultImportsProp", Boolean.toString(GlobalValues.useAlwaysDefaultImports));
        
        settings.setProperty("widthProp", String.valueOf(gLabMainFrame.getSize().width));
        settings.setProperty("heightProp", String.valueOf(gLabMainFrame.getSize().height));
        int xloc = gLabMainFrame.getLocation().x;
        int yloc = gLabMainFrame.getLocation().y;
        settings.setProperty("xlocProp", String.valueOf(xloc));
        settings.setProperty("ylocProp", String.valueOf(yloc));
        
        // RSyntaxArea editor
        settings.setProperty("rwidthProp", String.valueOf(gLabEditor.currentFrame.getWidth()));
        settings.setProperty("rheightProp", String.valueOf(gLabEditor.currentFrame.getHeight()));
        int rxloc = gLabEditor.currentFrame.getLocation().x;
        int ryloc = gLabEditor.currentFrame.getLocation().y;
        settings.setProperty("rxlocProp", String.valueOf(rxloc));
        settings.setProperty("rylocProp", String.valueOf(ryloc));
        
     settings.setProperty("paneFontNameProp", String.valueOf(GlobalValues.globalEditorPane.getFont().getName()));
     int paneFontSize =   GlobalValues.globalEditorPane.getFont().getSize();
     settings.setProperty("paneFontSizeProp", String.valueOf(paneFontSize)); 
        
        settings.setProperty("gLabConsFontNameProp", String.valueOf(gLabMainFrame.jLabConsole.getFont().getName()));
        settings.setProperty("gLabConsFontSizeProp", String.valueOf(gLabMainFrame.jLabConsole.getFont().getSize()));
        
        Font outConsFont =  GlobalValues.outputPane.getFont();
        settings.setProperty("outConsFontNameProp", outConsFont.getName());
        settings.setProperty("outConsFontSizeProp", String.valueOf(outConsFont.getSize()));
        
        // main menus
        settings.setProperty("uiFontNameProp", GlobalValues.uifont.getName());
        settings.setProperty("uiFontSizeProp", String.valueOf(GlobalValues.uifont.getSize()));
        
        // popup menus
        settings.setProperty("puiFontNameProp", GlobalValues.puifont.getName());
        settings.setProperty("puiFontSizeProp", String.valueOf(GlobalValues.puifont.getSize()));
        
        // html help
        settings.setProperty("htmlFontNameProp", GlobalValues.htmlfont.getName());
        settings.setProperty("htmlFontSizeProp", String.valueOf(GlobalValues.htmlfont.getSize()));
        
        // rest GUI components
        settings.setProperty("guiFontNameProp", GlobalValues.guifont.getName());
        settings.setProperty("guiFontSizeProp", String.valueOf(GlobalValues.guifont.getSize()));
        
        // GUI buttons
        settings.setProperty("buiFontNameProp", GlobalValues.buifont.getName());
        settings.setProperty("buiFontSizeProp", String.valueOf(GlobalValues.buifont.getSize()));
        
        settings.setProperty("outConsFontNameProp", String.valueOf(GlobalValues.consoleOutputWindow.output.getFont().getName()));
        settings.setProperty("outConsFontSizeProp", String.valueOf(GlobalValues.consoleOutputWindow.output.getFont().getSize()));
        
        
        //  Decimal digit formatting properties
        
         int vprec = groovySci.PrintFormatParams.getVecDigitsPrecision();
         settings.setProperty("VecDigitsProp", String.valueOf(vprec));
         
         int mprec = groovySci.PrintFormatParams.getMatDigitsPrecision();
         settings.setProperty("MatDigitsProp", String.valueOf(vprec));
         
         int mxrows = groovySci.PrintFormatParams.getMatMxRowsToDisplay();
         settings.setProperty("mxRowsProp", String.valueOf(mxrows));
         
         int mxcols = groovySci.PrintFormatParams.getMatMxColsToDisplay();
         settings.setProperty("mxColsProp", String.valueOf(mxcols));
         
         
         if (groovySci.PrintFormatParams.getVerbose()==true)
             settings.setProperty("verboseOutputProp", "true");
         else
             settings.setProperty("verboseOutputProp", "false");
         
        
          // a set that keeps non-duplicated class paths
        TreeSet<String> nonDuplicateClassPaths = new TreeSet<>();
             if (GlobalValues.GroovySciClassPathComponents!=null)  {
            int userSpecPathsCnt = GlobalValues.GroovySciClassPathComponents.size();
            String userPathsSpecString;
             userPathsSpecString = "";
            for (int k=0; k<userSpecPathsCnt; k++) {
                String  eli = GlobalValues.GroovySciClassPathComponents.elementAt(k).toString().trim();
                int idx = eli.lastIndexOf(File.separatorChar);
                if (idx ==eli.length()-1)  { // i.e. "/" is at the end of the path name, trim repeated "/"
                  int ki=idx;
                 while (eli.indexOf(ki)==File.separatorChar)  ki--;
                 eli=eli.substring(0, ki);
                }
                 nonDuplicateClassPaths.add(eli);
            }
            nonDuplicateClassPaths.add(GlobalValues.homeDir);
            
            Iterator allPaths = nonDuplicateClassPaths.iterator();
            while (allPaths.hasNext()) {
                String currElem = (String) allPaths.next();
                userPathsSpecString += (currElem+File.pathSeparatorChar);
              }
            
            settings.setProperty("GroovySciClassPathProp", userPathsSpecString); 
          }
         
    }
 
   
    // read the user defined  classpath components 
    public static void readUserPaths() {
     try {
      File file = new File(GlobalValues.userPathsFileName);  // the file name that keeps the user paths
      FileReader fr = new FileReader(file);
      BufferedReader in = new BufferedReader(fr);
      String currentLine;
      GlobalValues.GroovySciClassPathComponents.clear();
      
      while ( (currentLine = in.readLine())!= null)  {
          if (GlobalValues.GroovySciClassPathComponents.contains(currentLine)==false)
             GlobalValues.GroovySciClassPathComponents.add(currentLine);
       }
     }
            catch (IOException ioe) {
                System.out.println("Exception trying to read "+GlobalValues.userPathsFileName);
                 return;
            }
        
    }
    
    
    
    public static void writeUserPaths() {
    
        StringBuffer sb = new StringBuffer();
        
        // handle any specified additional user specified paths
        if (GlobalValues.GroovySciClassPathComponents !=null)  {
            int userSpecPathsCnt = GlobalValues.GroovySciClassPathComponents.size();
            String userPathsSpecString="";
            for (int k=0; k<userSpecPathsCnt; k++) {
                String currentToolbox  = GlobalValues.GroovySciClassPathComponents.elementAt(k).toString().trim();
                sb.append(currentToolbox+"\n");
             }
          }
         
        try {
                // take the program's text and save it to a temporary file
                File tempFile = new File(GlobalValues.userPathsFileName);
                FileWriter fw = new FileWriter(tempFile);
                fw.write(sb.toString(), 0, sb.length());
                fw.close();
             }
            catch (IOException ioe) {
                System.out.println("Exception trying to write Glab user paths ");
                 return;
            }
    }
    
    
// clear the properties 
    public static void clearProperties()  {
       settings.setProperty("GroovySciClassPathProp", homeDir);
       settings.setProperty("gLabWorkingDirProp","");
       }
       
    /** @return actual working directory */
    protected String getWorkingDirectory()
    {
        return workingDir;
    }

    /** @param set working directory */
    protected void setWorkingDirectory(String _workingDir)
    {
        workingDir = _workingDir;
        
    }

    
    // prepare the standard imports GroovyLab uses
public static void prepareImports() {
 if (globallmportCustomizer == null) {
      globallmportCustomizer  = new ImportCustomizer();
        
    globallmportCustomizer.addStaticStars(
            "groovySci.math.array.BasicDSP", 
            "groovySci.math.array.Vec", 
            "groovySci.math.array.Matrix",
            "org.jblas.DoubleMatrix",
            "groovySci.math.array.Mat1D",
            "org.jblas.DoubleMatrix", 
            "groovySci.math.array.CCMatrix", "groovySci.math.array.Sparse",
            "groovySci.math.array.JILapack", "groovySci.math.plot.plot",
            "groovySci.math.plot.plotAdaptiveFunctional", 
            "groovySci.math.array.MatrixConvs",
            "groovySci.FFT.FFTNR",
             "JFreePlot.jFigure", "JFreePlot.jPlot",
            "groovySci.math.io.MatIO",
            "groovySciCommands.BasicCommands",
            "groovySci.math.array.DoubleArray",
            "groovySci.FFT.ApacheFFT",
            "groovySci.FFT.FFTCommon",
            
            "NR.gaussj", 
            "gExec.Interpreter.MatlabConnection",
            "gExec.Interpreter.SciLabConnection",
            "java.lang.Math",
            
            "JFplot.DoubleVector",
            "JFplot.Charts", 
            
            "org.sound.SoundUtils",
            "net.GroovyLabNet",
            "net.NetSVD",
            "net.gslServerOps",
            
            "groovySci.asynch.asynchSvd",
            "groovySci.asynch.asynchEig",
            "groovySci.asynch.asynchSolve",
            "groovySci.asynch.asynchInv",
            "groovySci.asynch.asynchMul",
            "groovySci.asynch.asynchPlus"
            
             
            
            );
    
    
    globallmportCustomizer.addImports("groovySci.math.array.Vec", 
            "groovySci.math.array.CCMatrix", "groovySci.math.array.Sparse",
            "groovySci.math.array.JILapack", "groovySci.math.plot.PlotController",
            "groovy.swing.SwingBuilder", "java.text.DecimalFormat",
            "groovySci.FFT.ApacheFFT", 
            
            "JFplot.DoubleVector",
            "JFplot.Charts", 
            
            "org.sound.SoundUtils",
            
            "net.GroovyLabNet",
            "net.gslServerOps",
         
            "groovySci.asynch.asynchSvd",
            "groovySci.asynch.asynchEig",
            "groovySci.asynch.asynchSolve",
            "groovySci.asynch.asynchInv",
            "groovySci.asynch.asynchMul",
            "groovySci.asynch.asynchPlus",
            
            "groovy.transform.TimedInterrupt",
            "java.util.concurrent.TimeoutException",
            "groovySci.math.array.Matrix"
            
            );
    
    globallmportCustomizer.addStarImports("Jama", "numal",
                     "java.util.stream",
             "java.util.function",
             "expandRunTime",
             "groovy.transform",
    
            "jplot",  
            "java.awt",  "javax.swing", "java.awt.event",
            "JSci.maths", "JSci.maths.wavelet", "JSci.maths.wavelet.daubechies2",
            "groovySci.math.array", 
            "NR", "com.nr.sp",
            // Apache Commons imports
             "org.apache.commons.math3",
            "org.apache.commons.math3.analysis",
            "org.apache.commons.math3.analysis.differentiation",
            "org.apache.commons.math3.analysis.function",
            "org.apache.commons.math3.analysis.integration",
            "org.apache.commons.math3.analysis.integration.gauss",
            "org.apache.commons.math3.analysis.interpolation",
            "org.apache.commons.math3.analysis.polynomials",
            "org.apache.commons.math3.analysis.solvers",
            "org.apache.commons.math3.complex",
            "org.apache.commons.math3.dfp",
            "org.apache.commons.math3.distribution",
            "org.apache.commons.math3.distribution.fitting",
            "org.apache.commons.math3.exception",
            "org.apache.commons.math3.exception.util",
            "org.apache.commons.math3.filter",
            "org.apache.commons.math3.fitting",
            "org.apache.commons.math3.fitting.leastsquares",
            "org.apache.commons.math3.fraction",
            "org.apache.commons.math3.genetics",
            "org.apache.commons.math3.geometry",
            "org.apache.commons.math3.geometry.enclosing",
            "org.apache.commons.math3.geometry.euclidean.oned",
            "org.apache.commons.math3.geometry.euclidean.twod",
            "org.apache.commons.math3.geometry.euclidean.twod.hull",
            "org.apache.commons.math3.geometry.euclidean.threed",
            "org.apache.commons.math3.geometry.hull",
            "org.apache.commons.math3.geometry.partitioning",
            "org.apache.commons.math3.geometry.partitioning.utilities",
            "org.apache.commons.math3.geometry.spherical.oned",
            "org.apache.commons.math3.geometry.spherical.twod",
            "org.apache.commons.math3.linear",
            "org.apache.commons.math3.ml",
            "org.apache.commons.math3.ml.clustering",
            "org.apache.commons.math3.ml.clustering.evaluation",
            "org.apache.commons.math3.ml.distance",
            "org.apache.commons.math3.ml.neuralnet",
            "org.apache.commons.math3.ml.neuralnet.oned",
            "org.apache.commons.math3.ml.neuralnet.sofm",
            "org.apache.commons.math3.ml.neuralnet.sofm.util",
            "org.apache.commons.math3.ml.neuralnet.twod",
            "org.apache.commons.math3.ode",
            "org.apache.commons.math3.ode.events",
            "org.apache.commons.math3.ode.nonstiff",
            "org.apache.commons.math3.ode.sampling",
            "org.apache.commons.math3.optim",
            "org.apache.commons.math3.optim.linear",
            "org.apache.commons.math3.optim.nonlinear.scalar",
            "org.apache.commons.math3.optim.nonlinear.scalar.gradient",
            "org.apache.commons.math3.optim.nonlinear.scalar.noderiv",
            "org.apache.commons.math3.optim.nonlinear.vector",
            "org.apache.commons.math3.optim.nonlinear.vector.jacobian",
            "org.apache.commons.math3.optim.univariate",
            "org.apache.commons.math3.optimization",
            "org.apache.commons.math3.optimization.direct",
            "org.apache.commons.math3.optimization.fitting",
            "org.apache.commons.math3.optimization.general",
            "org.apache.commons.math3.optimization.linear",
            "org.apache.commons.math3.optimization.univariate",
            "org.apache.commons.math3.primes",
            "org.apache.commons.math3.random",
            "org.apache.commons.math3.special",
            "org.apache.commons.math3.stat",
            "org.apache.commons.math3.stat.clustering",
            "org.apache.commons.math3.stat.correlation",
            "org.apache.commons.math3.stat.descriptive",
            "org.apache.commons.math3.stat.descriptive.moment",
            "org.apache.commons.math3.stat.descriptive.rank",
            "org.apache.commons.math3.stat.descriptive.summary",
            "org.apache.commons.math3.stat.inference",
            "org.apache.commons.math3.stat.interval",
            "org.apache.commons.math3.stat.ranking",
            "org.apache.commons.math3.stat.regression",
            "org.apache.commons.math3.transform",
             "org.apache.commons.math3.util",
             "org.bytedeco.javacpp.gsl");
             
    
            
 }    
            
}      
            
 }

