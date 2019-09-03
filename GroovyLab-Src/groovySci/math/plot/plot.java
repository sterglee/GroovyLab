package groovySci.math.plot;

import JSci.maths.wavelet.Signal;
import java.awt.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.matheclipse.symja.OutputTextPane;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;
// objects. In order to implement the grid like structure of Matlab subplots  a constructor
// is defined that builds these objects from an array of graphic panel objects of the same type.
// The FrameView class represents plotting frame objects similar to Matlab figures.
// They can contain a rectangular grid of subplot objects.

import gExec.Interpreter.GlobalValues;
import groovySci.FFT.FFTResults;
import groovySci.math.array.Matrix;
import groovySci.math.array.Vec;
import groovySci.math.plot.plotObjects.Axis;
import groovySci.math.plot.plotObjects.LatexImage;
import groovySci.math.plot.plotObjects.texLabel;
import groovySci.math.plot.render.AbstractDrawer;
import java.util.Vector;

import static groovySci.math.plot.utils.PArray.*;

public class plot  {

        static public boolean  scatterPlotOn = false;
        static int currentFigTableIndex=0;   // the identifier of the working figure as its index at the Figure table
        static int currentPlotCnt2D = 0;  // holds the number of the existing 2D figure  objects
        static int currentPlotCnt3D = 0; // holds the number of the existing 3D figure objects
        static int currentPlotCnt = 0;  // a counter for the current number of figures
        static int maxNumberOfFigs = 20;  // maximum number of figure frames
        static double  figTableIncreaseFactor = 1.5; // factor to increase dynamically the figure table size
        static PlotPanel [] [] [] allPlots = null;  // all the plot objects that belong to each Matlab-like figure object, e.g. allPlots[f][] is the table with the references to the plots
        static FrameView [] allFrames =  null; // holds the pointers to all the frames for all the figure objects

        static PlotPanel currentPlot = null;  // the handle where plot operations are directed

        static boolean new_figure = true;  // controls whether to create a new figure or to plot upon an existing one

        static boolean holdOnMode = true;  // controls holding previous plots, used to implement Matlab's hold("on"), hold("off")

        static public boolean fullPlotsOn() {
            boolean prevState = PlotGlobals.skipPointsOnPlot;
            PlotGlobals.skipPointsOnPlot = false;
            return prevState;
        }
        
        static public boolean fastPlotsOn() {
            boolean prevState = PlotGlobals.skipPointsOnPlot;
            PlotGlobals.skipPointsOnPlot = true;
            return prevState;
        }
        
        static public int setMaximumPlotPoints(int newMaxPointsToPlot) {
            int prevMaxPointsToPlot = PlotGlobals.limitPlotPoints;
            PlotGlobals.limitPlotPoints = newMaxPointsToPlot;
            return prevMaxPointsToPlot;
        }

        // controls for 3-D plots the max points at the X-dimension
        static public int setMaximumPlotXPoints(int newMaxPointsXToPlot) {
            int prevMaxXPointsToPlot = PlotGlobals.limitPlotPointsX;
            PlotGlobals.limitPlotPointsX = newMaxPointsXToPlot;
            return prevMaxXPointsToPlot;
        }

        // controls for 3-D plots the max points at the Y-dimension
        static public int setMaximumPlotYPoints(int newMaxPointsYToPlot) {
            int prevMaxYPointsToPlot = PlotGlobals.limitPlotPointsY;
            PlotGlobals.limitPlotPointsY = newMaxPointsYToPlot;
            return prevMaxYPointsToPlot;
        }
        
        static public PlotPanel getPlot(){
            return currentPlot;
        }
        
        static public Axis getXAxis() {
            return currentPlot.getAxis(0);
        }
        
        static public Axis getYAxis() {
            return currentPlot.getAxis(1);
        }
        
        static public Axis getZAxis() {
            return currentPlot.getAxis(2);
        }
        
        // turn scatter plots on, in scatter plots points are not connected by a line
        static public  boolean  scatterPlotsOn() {
            boolean prevLineMode = scatterPlotOn;
            scatterPlotOn = true;
            return prevLineMode;
        }

       // turn line plots off, in line plots points are connected by a line
        static public  boolean linePlotsOn() {
            boolean prevLineMode = scatterPlotOn;
            scatterPlotOn = false;
            return prevLineMode;
        }

static public double [] arrToDouble(float [] x)  // convert float [] array to double []
      {
          int N = x.length;
          double [] dx = new double[N];
          for (int k=0; k<N; k++)
              dx[k] = x[k];
          return dx;
      }

static public double [][] arrToDoubleDouble(float [][] x)  // convert float [][] array to double [][]
      {
          int N = x.length; int M=x[0].length;
          double [] [] dxy = new double[N][M];
          for (int r=0; r<N; r++)
              for (int c=0; c<M; c++)
               dxy[r][c] = x[r][c];
          return dxy;
      }


public static void subplot( int  p)  {
    int  r = p / 100;  
    int  rem = p % 100;
    int c = rem / 10;
    int  id = rem % 10;
    subplot2d(r, c, id);
}

public static void subplot2d( int  p)  {
    int  r = p / 100;  
    int  rem = p % 100;
    int c = p / 10;
    int  id = rem % 10;
    subplot2d(r, c, id);
}

public static void subplot3d( int  p)  {
    int  r = p / 100;  
    int  rem = p % 100;
    int c = p / 10;
    int  id = rem % 10;
    subplot3d(r, c, id);
}
// increase the size of the global figure table when required
  public   static void  increaseFigTable() {
            int maxNumberOfFigsLarge = (int)(maxNumberOfFigs*figTableIncreaseFactor);  // update number of figs
            FrameView []  cpAllFrames = new FrameView[maxNumberOfFigsLarge];   // table that holds indices to the new figure frames
            for (int k=0; k<allFrames.length; k++)  // copy previous to enlarged
                cpAllFrames[k] = allFrames[k];
            PlotPanel [][][] cpAllPlots = new PlotPanel[maxNumberOfFigsLarge][][];
            for (int k=0; k<maxNumberOfFigs; k++)  // copy previous to enlarged
                cpAllPlots[k] = allPlots[k];
            for (int k=maxNumberOfFigs; k<maxNumberOfFigsLarge; k++) {  // new entries to nulls
                cpAllPlots[k] = null;
                cpAllFrames[k] = null;
            }
            maxNumberOfFigs = maxNumberOfFigsLarge;  // update figure table size
            //   enlarged tables become the current
            allPlots = cpAllPlots;
            allFrames = cpAllFrames;
        }

        // increase the Figure tables to cover the specified Figure number
  public   static void  increaseFigTableSpecifiedSize(int specifiedFigNo) {
            if (specifiedFigNo <= maxNumberOfFigs)
                return;  // specified size smaller than the current Figure table size
            int maxNumberOfFigsLarge = (int)(figTableIncreaseFactor*specifiedFigNo);  // update number of figs
            FrameView []  cpAllFrames = new FrameView[maxNumberOfFigsLarge];
            for (int k=0; k<allFrames.length; k++)  // copy previous to enlarged
                cpAllFrames[k] = allFrames[k];
            PlotPanel [][][] cpAllPlots = new PlotPanel[maxNumberOfFigsLarge][][];
            for (int k=0; k<maxNumberOfFigs; k++)  // copy previous to enlarged
                cpAllPlots[k] = allPlots[k];
            for (int k=maxNumberOfFigs; k<maxNumberOfFigsLarge; k++) {  // new entries to nulls
                cpAllPlots[k] = null;
                cpAllFrames[k] = null;
            }
            maxNumberOfFigs = maxNumberOfFigsLarge;  // update figure table size
            //   enlarged tables become the current
            allPlots = cpAllPlots;
            allFrames = cpAllFrames;
        }


        // initializes the ploting system
  public  static void initplots() {
		if (allFrames == null)  {   // plotting system not initialized yet
                    allPlots = new PlotPanel[maxNumberOfFigs][][];  // all the "subplot" objects
                    for (int k=0; k<maxNumberOfFigs; k++)
                       allPlots[k] = null;
                    allFrames = new FrameView[maxNumberOfFigs];   // all the "figure" objects
                    for (int k=0; k<maxNumberOfFigs; k++)
                        allFrames[k] = null;
                    currentPlotCnt2D = 0;
                    currentPlotCnt3D = 0;
                    currentPlotCnt = 0;

                    currentFigTableIndex = 0;
                    currentPlot = null;
                    new_figure = true;
                }
          }

        // returns the current number of figure objects (e.g. FrameView objects), is the number of non-null entries of the allFrames[] table
    public static  int  getFigCount()  {
            int figCnt=0;
            for (int k=0; k<maxNumberOfFigs; k++)
                if (allFrames[k] != null)
                    figCnt++;

            return figCnt;
        }

        // create  a new 2D figure object if we do not focus on an existing one
  public  static void newPlot2D() {
            initplots();  // init plotting system if not yet initialized
            // if either we do not have a current 2D ploting panel or a new figure is explicitly requested we create a new figure
            if  ( (currentPlot == null) || (!(currentPlot instanceof Plot2DPanel) || new_figure))  {
	// assume a subplot2D(1,1,1), i.e.  the array of 2D subplots is initialized to a single subplot for the whole figure
                        Plot2DPanel [][] subplots2D = new Plot2DPanel[1][1];
                        Plot2DPanel  newPanel = new Plot2DPanel();
                        subplots2D[0][0] = newPanel;

                        // construct the new 2D plot frame
                        Plot2DPanel newplot = new Plot2DPanel(subplots2D, PlotPanel.NORTH);    // create a new 2D figure object

                        currentFigTableIndex = getFigureId();  // request an available free figure identifier

             // initialize the frame window of the new figure
                        String figTitle = "Fig. "+(currentFigTableIndex+1);
                        FrameView f = new FrameView(figTitle, newplot, currentFigTableIndex);
                        

                        allFrames[currentFigTableIndex] = f;   // keep the frame window of the new figure
                        allPlots[currentFigTableIndex] = subplots2D; // keep the subplot structure

                        currentPlotCnt++;
                        currentPlotCnt2D++;   // a new 2D plot is created
                        new_figure = false;
                        currentPlot = subplots2D[0][0];  // focus on a component with a canvas
                        currentPlot.setLegendOrientation("SOUTH");

		  }

	}


        // create  a new 3D figure object if we do not focus on an existing one
public static void  newPlot3D() {
	    initplots();  // init plotting system if not yet initialized
            // if either we do not have a current 3D ploting panel or a new figure is explicitly requested we create a new figure
            if  ( (currentPlot == null) || (!(currentPlot instanceof Plot3DPanel) || new_figure))  {
			// assume a subplot3D(1,1,1)
                        Plot3DPanel [][] subplots3D = new Plot3DPanel[1][1];  // the array of 3D-subplots
                        subplots3D[0][0] = new Plot3DPanel();
                        // construct the new 3D plot frame
                        Plot3DPanel newPlot = new Plot3DPanel(subplots3D, PlotPanel.NORTH);    // create a new 3D figure object
                        String figTitle = "Fig. "+(currentFigTableIndex+1);
                        FrameView f = new FrameView(figTitle, newPlot, currentFigTableIndex);
                        
                        currentFigTableIndex = getFigureId();  // get a free figure id

                        allFrames[currentFigTableIndex] = f; // keep the frame window of the new figure
                        allPlots[currentFigTableIndex] = subplots3D;   // keep the subplot structure
                        currentPlotCnt3D++;   // a new 3D plot is created

                        currentPlotCnt++;
                        new_figure = false;
                        currentPlot = subplots3D[0][0];  // focus on a component with a canvas
                        currentPlot.setLegendOrientation("SOUTH");

		  }

	}

// closes all the available figure objects
public static  void closeAll()   {
            if (allFrames != null)
             for (int figId=0; figId<maxNumberOfFigs; figId++)
                 if (allFrames[figId] != null)
                    close(figId+1);
            allFrames = null;
        }


public static void close(String all) {
    if (all.equalsIgnoreCase("all"))
        closeAll();
}


// returns an Id of an unused slot for figure
        public static int getFigureId()  {
            boolean  figIdsRemain = false;   // remain empty slots for new figures
            for (int k=0; k<maxNumberOfFigs; k++)   {
                if (allFrames[k]==null)  {
                  currentFigTableIndex = k;
                  figIdsRemain =  true;
                  currentFigTableIndex  = k;
                  break;
                }
              }
            if (figIdsRemain == false)  { // increase the size of figure tables
                int prevFigCnt = maxNumberOfFigs;  // previous count of Figures
                increaseFigTable();  // increase the size of figure table
                currentFigTableIndex = prevFigCnt;  // return the first id from the enlarged region
            }
            return currentFigTableIndex;
            }

	// constructs a 2d figure object. Returns the figure id
        static public int figure() {
          return  figure2d();
        }

        // constructs a new figure 2D object with a single subplot panel, i.e. subplot(1,1,1). Returns the figure id
static public  int figure2d() {   // initialize flags for creating a new figure at the next ploting operation
                initplots();
                new_figure=true;
                int currentFigTableIndex = getFigureId();  // returns the id of the available figure
                createSubplot2D(1,1,1, currentFigTableIndex);
                return currentFigTableIndex+1;
	}

	// constructs a new figure 3D object with a single subplot panel, i.e. subplot(1,1,1). Returns the figure id
static public  int figure3d() {   // initialize flags for creating a new 3D figure at the next ploting operation
                initplots();
                new_figure=true;
                int currentFigTableIndex = getFigureId();  // returns the id of the available figure
                createSubplot3D(1,1,1, currentFigTableIndex);
                return currentFigTableIndex+1;
	}

        // focus on the figure with the identifier figId
 static public PlotPanel  figure(int figId) {
            initplots();
            if (figId < 1)  // assume the smallest figId when a zero or negative figId is requested
                figId = 1;
            if (figId > maxNumberOfFigs)   // increase Figure table size
                increaseFigTableSpecifiedSize(figId);

            int figMinus1 = figId-1;   // indexes start at 0, figures numbered from 1 according to Matlab conventions
            if (allPlots[figMinus1] != null)  {  // figure id exists
               currentPlot = allPlots[figMinus1][0][0];  // the requested figure panel
// if the figure id exists and is Plot3DPanel then it should be closed explicitly in order to use the figure id for 3D plots
               if (currentPlot instanceof  Plot3DPanel)
                    return null;
               return  currentPlot;
             }
            else   {  // we do not have the requested figure object, create it explicitly
         currentFigTableIndex = figMinus1;
         return createSubplot2D(1,1,1, currentFigTableIndex);
            }
   }
        
        static public int  figure2d(int figId) {
          return figure2d();
  }

        // focus on the figure with the identifier figId
static public PlotPanel  figure3d(int figId) {
            initplots();
            if (figId < 1)  // assume the smallest figId when a zero or negative figId is requested
                figId = 1;
            int figMinus1 = figId-1;   // indexes start at 0, figures numbered from 1 according to Matlab conventions
            if (allPlots[figMinus1] != null)  {  // figure id exists
               currentPlot = allPlots[figMinus1][0][0];  // the requested figure panel
// if the figure id exists and is Plot2DPanel then it should be closed explicitly in order to use the figure id for 3D plots
               if (currentPlot instanceof  Plot2DPanel)
                    return  null;

               return  currentPlot;
             }
            else   {  // we do not have the requested figure object, create it explicitly
         currentFigTableIndex = figMinus1;
         return createSubplot3D(1,1,1, currentFigTableIndex);
                }
      }

/*
 var N=pow(2, 15).toInt;   var t=linspace(0, 10, N); 
 var (noise, sig) = (vrand(N), sin(2.3*t))
 var (fftNoise, fftSig) = (fft(noise), fft(sig))
 figure(1); subplot(2,1,1); plot(t, sig, Color.BLUE, "signal"); hold("on"); plot(t, noise, Color.RED, "noise");
 subplot(2,1,2); plot(fftSig, Color.BLUE, "FFT of signal"); hold("on"); plot(fftNoise,  Color.RED, "FFT of noise")
  
 */
static public boolean hold(boolean newHoldState) {
    boolean oldHoldState = holdOnMode;
    holdOnMode = newHoldState;
    return oldHoldState;
}



static public String hold(String newHoldState) {
    boolean oldHoldState = holdOnMode;
    boolean newHoldModeToSet = false;
    if (newHoldState.equalsIgnoreCase("on") || newHoldState.equalsIgnoreCase("1"))
        newHoldModeToSet = true;
    holdOnMode = newHoldModeToSet;
    return oldHoldState==true?"on":"off";
}


static public void title(String titleStr)  {
    int plotCnt = currentFigTableIndex;
    if (plotCnt < 0 )
         plotCnt = 0;
    FrameView currentView = allFrames[plotCnt];
    if (currentView != null)
        currentView.setTitle(titleStr);
}
 
/*
 t = inc(0, 0.01, 10); x = sin(0.12*t);  plot(x);
 */
// clears all the plots from the figure with figId
static public void clf(int figId)  {
    FrameView   figFrame = allFrames[figId-1];   // the frame of the figure
    if (figFrame == null)   return;   // no such figure
    PlotPanel [][] plots = allPlots[figId-1];
    int numRows = plots.length;
    int numCols = plots[0].length;
    for (int row=0; row < numRows; row++)
        for (int col=0; col < numCols; col++)   {  // for all subplots
            PlotPanel  currentPanel  =  plots[row][col];
            currentPanel.removeAllPlots();
        }
    }

// clears all the plots from the reference plotPanel
static public void clf(PlotPanel plotPanel)  {
            plotPanel.removeAllPlots();
    }


// clears all the plot with identifier plotId, from the figure with identifier figId, at subplot id: [xId, yId]
// the numbering of plot ids starts at 1 (as the fig ids)
static public void clf(int figId, int xId, int yId, int plotId)  {
    FrameView   figFrame = allFrames[figId-1];   // the frame of the figure
    if (figFrame == null)   return;   // no such figure
    PlotPanel [][] plots = allPlots[figId-1];
    int numRows = plots.length;
    int numCols = plots[0].length;
     if (numRows > yId || numCols > xId)   // requested subplot not exists
         return;
    PlotPanel  currentPanel = plots[xId][yId];
    currentPanel.remove(plotId-1);
}


// clears the plot with identifier plotId, from the figure with identifier figId
// the numbering of plot ids starts at 1 (as the fig ids)
// Example:
// var x=linspace(0, 2, 1000);   var y = sin(3.4*x); plot(x, y); hold("on")
// plot(x, 3*sin(8*y), Color.BLUE)
//  clf(1,1)  // clears the first plot
static public void clf(int figId, int plotId)  {
    FrameView   figFrame = allFrames[figId-1];   // the frame of the figure
    if (figFrame == null)   return;   // no such figure
    PlotPanel [][] plots = allPlots[figId-1];
    int numRows = plots.length;
    int numCols = plots[0].length;
    for (int row=0; row < numRows; row++)
        for (int col=0; col < numCols; col++)   {  // for all subplots
            PlotPanel  currentPanel  =  plots[row][col];
            currentPanel.removePlot(plotId-1);
        }

    }

// clears the plot with identifier plotId, from the figure with identifier figId, at subplot id: [xId, yId]
// the numbering of plot ids starts at 1 (as the fig ids)
static public void clf(int figId, int xId, int yId)  {
    FrameView   figFrame = allFrames[figId-1];   // the frame of the figure
    if (figFrame == null)   return;   // no such figure
    PlotPanel [][] plots = allPlots[figId-1];
    int numRows = plots.length;
    int numCols = plots[0].length;
     if (yId > numRows || xId > numCols)   // requested subplot not exists
         return;
    PlotPanel  currentPanel = plots[yId-1][xId-1];
    currentPanel.removeAllPlots();
}


            // creates a Matlab-like placement of multiple subplots in a figure with id figureId
            // the function overwrites (i.e. disposes) any plot object that was residing on slots allFrames[figureId], allPlots[figureId]
            // sets the currentPlot member variable to the focused subplot object
            // a figure identifier to use is explicitly requested
static public PlotPanel  createSubplot2D(int rows, int cols, int focusSubPlot, int figureId)  {
         initplots();
                   // create new figure
         Plot2DPanel [] [] newPlot2DPanel = new Plot2DPanel[rows][cols];
            for (int ni=0; ni<rows; ni++)    // initialize the new plot panel
                for (int mi=0; mi<cols; mi++)
                   newPlot2DPanel[ni][mi] = new Plot2DPanel();


         currentFigTableIndex = figureId;  // the requested figure id to create
         String figTitle = "Fig. "+(currentFigTableIndex+1);
         // construct the new 2D plot frame
         Plot2DPanel newplot = new Plot2DPanel(newPlot2DPanel, PlotPanel.NORTH);    // create a new 2D figure object

         FrameView  fprev = allFrames[currentFigTableIndex];  // any existing frame at that slot
         if (fprev ==  null) {
           currentPlotCnt2D++;
           currentPlotCnt++;
         }
        else {  // dispose the previous frameView
             fprev.dispose();
         }

         FrameView f = new FrameView(figTitle, newplot, currentFigTableIndex);
         f.setBackground(Color.WHITE);

         allFrames[currentFigTableIndex] = f;  // keep the currently constructed figure frame
         allPlots[currentFigTableIndex] = newPlot2DPanel;  // keep the current 2D figure array of plot panels

         new_figure = false;  // avoid creating new figure panel for subsequent calls

         // adjust the currentPlot parameter to point to the focused subplot requested by the focusSubPlot parameter
         int rowNo = (int)(focusSubPlot / cols);
	 if (rowNo*cols == focusSubPlot)  rowNo--;

	 int colNo = (int)(focusSubPlot - rowNo*cols) - 1;
         currentPlot = newPlot2DPanel[rowNo][colNo];
         currentPlot.setLegendOrientation("SOUTH");

         return (PlotPanel) newplot;
     }



            // creates a Matlab-like placement of multiple subplots in a figure with id figureId
            // the function overwrites any plot object that was residing on slots allFrames[currentFigTableIndex], allPlots[currentFigTableIndex]
            // sets the currentPlot member variable to the focused subplot object
            // a figure identifierto use  is explicitly requested
  static public PlotPanel  createSubplot3D(int rows, int cols, int focusSubPlot, int figureId)  {
         initplots();
                   // create new figure
         Plot3DPanel [] [] newPlot3DPanel = new Plot3DPanel[rows][cols];
            for (int ni=0; ni<rows; ni++)    // initialize the new plot panel
                for (int mi=0; mi<cols; mi++)
                   newPlot3DPanel[ni][mi] = new Plot3DPanel();


         currentFigTableIndex = figureId;  // the requested figure id to create
         String figTitle = "Fig. "+(currentFigTableIndex+1);
         // construct the new 3D plot frame
         Plot3DPanel newplot = new Plot3DPanel(newPlot3DPanel, PlotPanel.NORTH);    // create a new 3D figure object

         FrameView fprev = allFrames[currentFigTableIndex];   // any existing frame at that slot
         if (fprev == null) {
           currentPlotCnt3D++;
           currentPlotCnt++;
         }
         else {  // dispose the previous frameView
             fprev.dispose();
         }

         FrameView f = new FrameView(figTitle, newplot, currentFigTableIndex);

         allFrames[currentFigTableIndex] = f;  // keep the currently constructed figure frame
         allPlots[currentFigTableIndex] = newPlot3DPanel;  // keep the current 3D figure array of plot panels

         new_figure = false;  // avoid creating new figure panel for subsequent calls

         // adjust the currentPlot parameter to point to the focused subplot requested by the focusSubPlot parameter
         int rowNo = (int)(focusSubPlot / cols);
	 if (rowNo*cols == focusSubPlot)  rowNo--;

	 int colNo = (int)(focusSubPlot - rowNo*cols) - 1;
         currentPlot = newPlot3DPanel[rowNo][colNo];
         currentPlot.setLegendOrientation("SOUTH");

         return (PlotPanel) newplot;
     }


// creates a Matlab-like placement of multiple subplots in the current Figure with index currentFigTableIndex
// returns the id of the figure object
  static public int Subplot2D(int rows, int cols, int focusSubPlot)  {
         initplots();
         if (allFrames[currentFigTableIndex] == null) {  // create new 2D-figure panel
                   // create new figure
                Plot2DPanel [] [] newPlot2DPanel = new Plot2DPanel[rows][cols];
                for (int ni=0; ni<rows; ni++)    // initialize the new plot panel
                    for (int mi=0; mi<cols; mi++)
                        newPlot2DPanel[ni][mi] = new Plot2DPanel();


                        // construct the new 2D plot frame
                currentPlotCnt2D++;
                currentPlotCnt++;

                Plot2DPanel newplot = new Plot2DPanel(newPlot2DPanel, PlotPanel.NORTH);    // create a new 2D figure object
                String figTitle = "Fig. "+(currentFigTableIndex+1);
                FrameView f = new FrameView(figTitle, newplot, currentFigTableIndex);

                allFrames[currentFigTableIndex] = f;
                allPlots[currentFigTableIndex] = newPlot2DPanel;  // keep the current 2D figure frame

                new_figure = false;  // avoid creating new figure panel for subsequent calls
         // create the objects of the current plot. These are reused at subsequent calls of subplot2D()

                int rowNo = (int)(focusSubPlot / cols);
		if (rowNo*cols == focusSubPlot)  rowNo--;

		int colNo = (int)(focusSubPlot - rowNo*cols) - 1;
                currentPlot = newPlot2DPanel[rowNo][colNo];
                currentPlot.setLegendOrientation("SOUTH");

       }
         else {  // either focus on the requested plot if the subplot grid structure matches,
                    // or otherwise create a new subplot structure
             Plot2DPanel [][] currentPlot2DPanel = (Plot2DPanel[][]) allPlots[currentFigTableIndex];
             Plot2DPanel [] [] newPlot2DPanel = currentPlot2DPanel;

             int yGrid = currentPlot2DPanel.length;
             int xGrid = currentPlot2DPanel[0].length;
             if (yGrid != rows || xGrid != cols)   {    // a grid of different size is requested
                 newPlot2DPanel = new Plot2DPanel[rows][cols];

                 // dispose the previous frameView object
                 FrameView pf = allFrames[currentFigTableIndex];
                 pf.dispose();

                 // create new figure
                for (int ni=0; ni<rows; ni++)    // initialize the new plot panel
                    for (int mi=0; mi<cols; mi++)
                        newPlot2DPanel[ni][mi] = new Plot2DPanel();

                Plot2DPanel newplot = new Plot2DPanel(newPlot2DPanel, PlotPanel.NORTH);    // create a new 2D figure object
                String figTitle = "Fig. "+(currentFigTableIndex+1);
                FrameView f = new FrameView(figTitle, newplot, currentFigTableIndex);
                f.setBackground(Color.WHITE);

                allFrames[currentFigTableIndex] = f;
                allPlots[currentFigTableIndex] = newPlot2DPanel;  // keep the current 2D figure frame
                new_figure = false;

             }
             int rowNo = (int)(focusSubPlot / cols);
             if (rowNo*cols == focusSubPlot)  rowNo--;

             int colNo = (int)(focusSubPlot - rowNo*cols) - 1;
             currentPlot = newPlot2DPanel[rowNo][colNo];
             currentPlot.setLegendOrientation("SOUTH");
         }
         return currentFigTableIndex;
     }


        // creates a Matlab-like placement of multiple subplots in a figure.
        // returns the id of the figure object
     static public int Subplot3D(int rows, int cols, int focusSubPlot)  {
         initplots();
         if (allFrames[currentFigTableIndex] == null) {  // create new 3D-figure panel
                   // create new figure
                Plot3DPanel [] [] newPlot3DPanel = new Plot3DPanel[rows][cols];
                for (int ni=0; ni<rows; ni++)    // initialize the new plot panel
                    for (int mi=0; mi<cols; mi++)
                        newPlot3DPanel[ni][mi] = new Plot3DPanel();


                        // construct the new 3D plot frame
                currentPlotCnt3D++;
                currentPlotCnt++;

                Plot3DPanel newplot = new Plot3DPanel(newPlot3DPanel, PlotPanel.NORTH);    // create a new 3D figure object
                String figTitle = "Fig. "+(currentFigTableIndex+1);
                FrameView f = new FrameView(figTitle, newplot, currentFigTableIndex);
                f.setBackground(Color.WHITE);

                allFrames[currentFigTableIndex] = f;
                allPlots[currentFigTableIndex] = newPlot3DPanel;  // keep the current 3D figure frame

                new_figure = false;  // avoid creating new figure panel for subsequent calls
         // create the objects of the current plot. These are reused at subsequent calls of subplot3D()

                int rowNo = (int)(focusSubPlot / cols);
		if (rowNo*cols == focusSubPlot)  rowNo--;

		int colNo = (int)(focusSubPlot - rowNo*cols) - 1;
                currentPlot = newPlot3DPanel[rowNo][colNo];
                currentPlot.setLegendOrientation("SOUTH");

       }
          else {  // either focus on the requested plot if the subplot grid structure matches,
                    // or otherwise create a new subplot structure
             Plot3DPanel [][] currentPlot3DPanel = (Plot3DPanel[][]) allPlots[currentFigTableIndex];
             Plot3DPanel [] [] newPlot3DPanel = currentPlot3DPanel;

             int yGrid = currentPlot3DPanel.length;
             int xGrid = currentPlot3DPanel[0].length;
             if (yGrid != rows || xGrid != cols)   {    // a grid of different size is requested
                 newPlot3DPanel = new Plot3DPanel[rows][cols];

                 // dispose the previous frameView object
                 FrameView pf = allFrames[currentFigTableIndex];
                 pf.dispose();

                 // create new figure
                for (int ni=0; ni<rows; ni++)    // initialize the new plot panel
                    for (int mi=0; mi<cols; mi++)
                        newPlot3DPanel[ni][mi] = new Plot3DPanel();

                Plot3DPanel newplot = new Plot3DPanel(newPlot3DPanel, PlotPanel.NORTH);    // create a new 3D figure object
                String figTitle = "Fig. "+(currentFigTableIndex+1);
                FrameView f = new FrameView(figTitle, newplot, currentFigTableIndex);

                allFrames[currentFigTableIndex] = f;
                allPlots[currentFigTableIndex] = newPlot3DPanel;  // keep the current 3D figure frame
                new_figure = false;

             }
             int rowNo = (int)(focusSubPlot / cols);
             if (rowNo*cols == focusSubPlot)  rowNo--;

             int colNo = (int)(focusSubPlot - rowNo*cols) - 1;
             currentPlot = newPlot3DPanel[rowNo][colNo];
             currentPlot.setLegendOrientation("SOUTH");
         }
         return currentFigTableIndex;
     }


// overloaded call
    static public void subplot(int rows, int cols, int focusSubPlot)  {
        Subplot2D(rows,  cols,  focusSubPlot);
     }

// overloaded call
    static public void subplot2D(int rows, int cols, int focusSubPlot)  {
        Subplot2D(rows,  cols,  focusSubPlot);
     }

     // overloaded call
    static public void subplot2d(int rows, int cols, int focusSubPlot)  {
        Subplot2D(rows,  cols,  focusSubPlot);
     }


     // overloaded call
static public void subplot3D(int rows, int cols, int focusSubPlot)  {
        Subplot3D(rows,  cols,  focusSubPlot);
     }
    // overloaded call
static public void subplot3d(int rows, int cols, int focusSubPlot)  {
        Subplot3D(rows,  cols,  focusSubPlot);
     }


// closes the current  figure
static public int close() {
            // find next usable plot
            int currentFigSlotExamined = currentPlotCnt-1;

            FrameView closingFrameView = allFrames[currentFigTableIndex];  // the frame view of the current figure
       //  we have a current view,  close it, and refocus on another figure
      if (closingFrameView != null)  {
            closingFrameView.dispose();
            if (allPlots[currentFigTableIndex][0][0] instanceof  Plot2DPanel)
                currentPlotCnt2D--;
            else
                currentPlotCnt3D--;

            allPlots[currentFigTableIndex] = null;
            allFrames[currentFigTableIndex] = null;
            currentPlotCnt--;   // one less plot

            boolean  atLeastOnefigureRemains = false;
            for (int k=0; k<maxNumberOfFigs; k++)  {  // scan all possible slots
                if (allFrames[currentFigSlotExamined] != null)  {   // we have found an unused slot for focusing figure
                    atLeastOnefigureRemains = true;
                    break;
                 }
                if (currentFigSlotExamined  < 0)    // cycle from the highest position
                    currentFigSlotExamined = maxNumberOfFigs-1;
            }   // scan all possible slots

            if (atLeastOnefigureRemains == true)   {   // focus on the detected "survived" figure object
              PlotPanel [][] currentPlotPanel = (PlotPanel[][]) allPlots[currentFigSlotExamined];
              currentPlot = currentPlotPanel[0][0];    // make the first subplot of the figure object as the current one
              currentPlot.setLegendOrientation("SOUTH");
         }
      }         //  we have a current view,  close it, and refocus on another figure

            currentFigTableIndex = currentFigSlotExamined;
            return currentFigTableIndex+1;
    }

   // close an explicitly requested figure id
   // it focuses automatically on the previous figure object which it returns
    static public int close(int figId) {
        int currentFigSlotExamined = currentPlotCnt-1;

        if (allFrames != null) {   // plot system inited
         int  figMinusOne = figId-1;
             FrameView closingFrameView = allFrames[figMinusOne];
             // find next usable plot

            int len1 = allPlots[figMinusOne][0].length;
            int len2 = allPlots[figMinusOne].length;
       if (closingFrameView != null)     {   // figure frame exists
            closingFrameView.dispose();
            for (int i=0; i<len2; i++)
                for (int j=0; j<len1; j++)
                    allPlots[figMinusOne][i][j] = null;

            allPlots[figMinusOne] = null;  // TODOSterg: handle closing properly
            allFrames[figMinusOne] = null;
            currentPlotCnt--;   // one less plot

              boolean  atLeastOnefigureRemains = false;
              for (int k=0; k<maxNumberOfFigs; k++)  {  // scan all possible slots
                  if (allFrames[currentFigSlotExamined] != null)  {   // we have found an unused slot for focusing figure
                      atLeastOnefigureRemains = true;
                      break;
                 }
                currentFigSlotExamined--;  // check lowest figure id
                currentFigTableIndex = currentFigSlotExamined;
                if (currentFigSlotExamined  < 0)    // cycle from the highest position
                {
                    currentFigTableIndex = currentFigSlotExamined = maxNumberOfFigs-1;
                }
            }   // scan all possible slots

            if (atLeastOnefigureRemains == true)   {   // focus on the detected "survived" figure object
              PlotPanel [][] currentPlotPanel = (PlotPanel[][]) allPlots[currentFigSlotExamined];
              currentPlot = currentPlotPanel[0][0];    // make the first subplot of the figure object as the current one
              currentPlot.setLegendOrientation("SOUTH");
                }
            else
                new_figure  = true;   // if figures do not remain create one on next plot
              }   // close currentFigTableIndex
        }
            return currentFigSlotExamined+1;   // return index of the closed figure
        }

        static public void setColor(int red, int green, int blue)  {
            Color  newColor = new Color(red, green, blue);
            currentPlot.setForeground(newColor);
            currentPlot.setBackground(newColor);
            currentPlot.repaint();
         }


// var x = vrand(500); plot(x);  markX(100, 200, new Font("Arial", Font.BOLD, 30));  markX(10.0, 0.3)
        static public void xlabel(String xLabelStr) {
            currentPlot.setAxisLabel(groovySci.math.plot.PlotPanel.xAxisId, xLabelStr);
        }

        static public void ylabel(String yLabelStr) {
            currentPlot.setAxisLabel(groovySci.math.plot.PlotPanel.yAxisId, yLabelStr);
        }

        static public void zlabel(String zLabelStr) {
            if (currentPlot instanceof  Plot3DPanel)
                currentPlot.setAxisLabel(groovySci.math.plot.PlotPanel.zAxisId, zLabelStr);
        }

        
        // set the color for LaTex text and return the previous setting
        public static Color setLatexColor(Color col) {
            Color prevColor = PlotGlobals.latexColor;
            PlotGlobals.latexColor = col;
            return prevColor;
        }
        
        public static void  latexLabel(String latex)  {
            texLabel  txl = new groovySci.math.plot.plotObjects.texLabel(latex, 1, 1); 
            currentPlot.addPlotable(txl);
        }
        
        public static void  latexLabel(String latex, int coordx, int coordy)  {
            texLabel  txl = new groovySci.math.plot.plotObjects.texLabel(latex, coordx, coordy); 
            currentPlot.addPlotable(txl);
        }
        
        public static void  latexLabel(String latex, int size,  int coordx, int coordy)  {
            texLabel  txl = new groovySci.math.plot.plotObjects.texLabel(latex, size, coordx, coordy); 
            currentPlot.addPlotable(txl);
        }
        
        public static void  latexLabel(String latex, double coordx, double  coordy)  {
            texLabel  txl = new groovySci.math.plot.plotObjects.texLabel(latex, coordx, coordy); 
            currentPlot.addPlotable(txl);
        }
        
        public static void  latexLabel(String latex, int size,  double  coordx, double  coordy)  {
            texLabel  txl = new groovySci.math.plot.plotObjects.texLabel(latex, size, coordx, coordy); 
            currentPlot.addPlotable(txl);
        }
        
        
        
        
        public static void latexLabel3d(String latex, double nw1, double nw2, double nw3, double se1, double se2, double se3, double sw1, double sw2, double sw3) {
            LatexImage tximg = new groovySci.math.plot.plotObjects.LatexImage(latex, nw1,  nw2, nw3, se1, se2, se3, sw1, sw2, sw3);
            currentPlot.addPlotable(tximg);
        }
        
        public static void latexLabel3d(String latex, int xcoord, int ycoord) {
            LatexImage tximg = new groovySci.math.plot.plotObjects.LatexImage(latex, xcoord, ycoord);
            currentPlot.addPlotable(tximg);
        }
        
        public static void  latexRender(String latex)  {
                      TeXFormula tf = new TeXFormula(latex);
                      TeXIcon icontf = tf.createTeXIcon(TeXConstants.STYLE_DISPLAY, 20);
                      icontf.setInsets(new Insets(5, 5, 5, 5));
	
                      OutputTextPane otp = new OutputTextPane();
                      otp.addIcon(icontf,  0, true);
	    JPanel jp = new JPanel();
                      jp.add(otp);
                      
                      JFrame tstF = new JFrame("test");
                      tstF.add(jp);
                      tstF.setSize(200, 200);
                      tstF.setVisible(true);

	
    }    

        
        
        //  plotTypes  static routines
        
        
        static double [] getRange(double [] x, int low, int high) {
            int N = high-low+1;
            double [] nv = new double[N];
            int idx = low;
            for (int k=0; k<N; k++)
                nv[k] = x[idx++];
            return nv;
        }
    
     
          // some routines that define plot range     

        
        static public PlotPanel plot(double [] x, double [] y, double [] z, int low, int high, Color color, String name) {
         double [] nx = getRange(x, low, high);
         double [] ny = getRange(y, low, high);
         double [] nz = getRange(z, low, high);
         return plot(nx, ny, nz, color, name);
       }
      
    static public PlotPanel plot(double [] x, double [] y, double [] z, int low, int high,  String name) {
         double [] nx = getRange(x, low, high);
         double [] ny = getRange(y, low, high);
         double [] nz = getRange(z, low, high);
         return plot(nx, ny, nz,  name);
       }
       
    static public PlotPanel plot(double [] x, double [] y, double [] z, int low, int high) {
         double [] nx = getRange(x, low, high);
         double [] ny = getRange(y, low, high);
         double [] nz = getRange(z, low, high);
         return plot(nx, ny, nz);
       }
       
        
    static public PlotPanel plot(double [] x, double [] y,  int low, int high,  String name) {
         double [] nx = getRange(x, low, high); 
         double [] ny = getRange(y, low, high);
         return plot(nx, ny,  PlotGlobals.defaultAbstractDrawerColor, name);
       }
       
       static public PlotPanel plot(double [] x, double [] y,  int low, int high,  Color color, String name) {
         double [] nx = getRange(x, low, high); 
         double [] ny = getRange(y, low, high);
         return plot(nx, ny, color,  name);
       }
       
       static public PlotPanel plot(double [] x, double [] y,  int low, int high) {
         double [] nx = getRange(x, low, high); 
         double [] ny = getRange(y, low, high);
         return plot(nx, ny);
       }
       
  static public PlotPanel plot(double [] x, double [] y,  int low, int high, Color color) {
         double [] nx = getRange(x, low, high); 
         double [] ny = getRange(y, low, high);
         return plot(nx, ny, color);
       }

       
        
        static public PlotPanel plot3d_line(Object  x,  Object y,  Object z, String name) {
            double [][] xyz = new double [3][];
            xyz[0] = (double []) x;
            xyz[1] = (double []) y;
            xyz[2] = (double []) z;
            if (new_figure == true)  newPlot3D();
            if (scatterPlotOn)
                ((Plot3DPanel)currentPlot).addScatterPlot(name, xyz);
            else
	        ((Plot3DPanel)currentPlot).addLinePlot(name, xyz);
	    return currentPlot;
        }



        static public PlotPanel splinePlot( double [] xvals, double [] yvals, int NP, Color color, String name)
        {
            
            DhbScientificCurves.Curve  curve = new DhbScientificCurves.Curve();
            int len = xvals.length;
            for (int k=0; k<len; k++) 
                curve.addPoint(xvals[k], yvals[k]);
                    
            DhbInterpolation.SplineInterpolator  splines = new DhbInterpolation.SplineInterpolator(curve);
            int splineLen = len*NP;
            double [] xSpline = new double[splineLen];
            double [] ySpline = new double[splineLen];
            double dx = (xvals[1]-xvals[0])/NP;
            double x0 = xvals[0];
            for (int k=0; k<splineLen; k++) {
                xSpline[k] = x0+k*dx;
                ySpline[k] = splines.value(xSpline[k]);
            }
            ((Plot2DPanel) currentPlot).addLinePlot(name, color, xSpline, ySpline);
            
            
            return currentPlot;
        }
        
        static public PlotPanel splinePlot( double [] xvals, double [] yvals, int NP)
        {
            String name = "Spline Plot";
            Color color = Color.ORANGE;
            return splinePlot(xvals, yvals, NP, color, name);
        }
        
        static public PlotPanel splinePlot( double [] xvals, double [] yvals, int NP, String name)
        {
            Color color = Color.DARK_GRAY;
            return splinePlot(xvals, yvals, NP, color, name);
        }
        
        static public PlotPanel splinePlot( double [] xvals, double [] yvals, int NP, Color color)
        {
            String name = "Spline Plot";
            return splinePlot(xvals, yvals, NP, color, name);
        }
        
        
       static public PlotPanel plot(double [] x, double [] y, double [] z, Color color, String name) {
                newPlot3D();
	          if (scatterPlotOn)
                ((Plot3DPanel)currentPlot).addScatterPlot(name, color, x, y, z);
            else
	 	((Plot3DPanel)currentPlot).addLinePlot(name, color, x, y, z);

        	return currentPlot;
	}
       
       
       
       static public PlotPanel  plotMarkedLine(String name, Color color, double [] x, double [] y) {
           newPlot2D();
           ((Plot2DPanel)currentPlot).addMarkedLinePlot(name, color, x, y);
           return currentPlot;
       }

       static public PlotPanel  plotMarkedLine(double [] x, double [] y) {
           newPlot2D();
           ((Plot2DPanel)currentPlot).addMarkedLinePlot("Marked Line", x, y);
           return currentPlot;
       }

       static public PlotPanel  plotMarkedLine(Vec x, Vec y) {
           newPlot2D();
           ((Plot2DPanel)currentPlot).addMarkedLinePlot("Marked Line", x.getv(), y.getv());
           return currentPlot;
       }

       static public PlotPanel  plotMarkedLine(Vec x, Vec y, Color c) {
           newPlot2D();
           ((Plot2DPanel)currentPlot).addMarkedLinePlot("Marked Line", c, x.getv(), y.getv());
           return currentPlot;
       }

       static public PlotPanel  plotPoints(String name, Color color, double []x, double []y) {
           newPlot2D();
           PlotGlobals.defaultMarksColor = color;
           ((Plot2DPanel)currentPlot).addMarkedPlot(name, color,  x, y);
           return currentPlot;
       }
       
       static public PlotPanel  plotPoints(String name, Color color, Matrix x, Matrix y) {
           return plotPoints(name, color, x.getv(), y.getv());
       }

       static public PlotPanel  plotPoints(String name, Color color, char mark, Font font, int skipPoints,  double []x, double []y) {
           newPlot2D();
           PlotGlobals.defaultSkipPoints = skipPoints;
           PlotGlobals.defaultMarksColor = color;
           PlotGlobals.defaultMarkChar = mark;
           ((Plot2DPanel)currentPlot).addMarkedPlot(name, x, y);
           return currentPlot;
       }

       static public PlotPanel  plotPoints(String name, Color color, char mark, Font font, int skipPoints,  Matrix x, Matrix y) {
                 return plotPoints(name, color, mark, font, skipPoints, x.getv(), y.getv());
       }
       
       static public PlotPanel  plotPoints(String name, Color color, Vec x, Vec y) {
           newPlot2D();
           PlotGlobals.defaultMarksColor = color;
           ((Plot2DPanel)currentPlot).addMarkedPlot(name, color,  x.getv(), y.getv());
           return currentPlot;
       }

        static public PlotPanel  plotPoints(Matrix  x, Matrix  y) {
            return plotPoints(x.getv(), y.getv() );
        }

        static public PlotPanel  plotPoints(double []x, double []y) {
           newPlot2D();
           ((Plot2DPanel)currentPlot).addMarkedPlot("Marked Line", Color.BLACK,  x, y);
           return currentPlot;
       }

       static public PlotPanel plot(float  [] x, float [] y, float[] z, Color color, String name) {
         return plot(arrToDouble(x), arrToDouble(y), arrToDouble(z), color, name);
       }

       
	//////////////
	// 3D plots //
	//////////////

 static public PlotPanel plot(Matrix x, Matrix  y, Matrix  z, String name) {
     return plot(x.getv(),  y.getv(), z.getv(), name);
 }
        
 static public PlotPanel plot(double [] x, double [] y, double [] z, String name) {
        	newPlot3D();
            if (scatterPlotOn)
                ((Plot3DPanel)currentPlot).addScatterPlot(name, x, y, z);
            else
	 	((Plot3DPanel)currentPlot).addLinePlot(name, x, y, z);

                return currentPlot;
}

 static public PlotPanel plot(float  [] x, float [] y, float[] z, String name) {
         return plot(arrToDouble(x), arrToDouble(y), arrToDouble(z), name);
       }

static public PlotPanel plot(Matrix x, Matrix y, Matrix z) {
    return plot(x.getv(), y.getv(), z.getv());
}

 static public PlotPanel plot(double [] x, double [] y, double [] z) {
                String name = "3-D Plot";
		newPlot3D();
	if (scatterPlotOn)
                ((Plot3DPanel)currentPlot).addScatterPlot(name, x, y, z);
            else
	 	((Plot3DPanel)currentPlot).addLinePlot(name, x, y, z);

		return currentPlot;
}

static public PlotPanel plot(float  [] x, float [] y, float[] z) {
         return plot(arrToDouble(x), arrToDouble(y), arrToDouble(z));
       }

static public PlotPanel plot(Matrix x, Matrix y, Color c) {
    return plot(x.getv(), y.getv(), c);
}   


static public PlotPanel plot(Matrix x, Matrix y, String  title) {
    return plot(x.getv(), y.getv(), title);
}   


static public PlotPanel plot(Matrix x) {
       return plot(x.getArray());
   }
   
   static public PlotPanel plot(double [][] x) {
       int N = x.length;
       int M = x[0].length;
      
        String name = "2-D Plot";
        newPlot2D();

       if (M ==1) {
           double [] y1 = new double[N];
           double [] y2 = new double[N];
           for (int c=0; c<N; c++) {
               y1[c] = c;
               y2[c] = x[c][0];
           }
       if (scatterPlotOn)
                ((Plot2DPanel)currentPlot).addScatterPlot(name, y1, y2);
         else
                ((Plot2DPanel)currentPlot).addLinePlot(name, y1,  y2);
       }
       else if (N==1) {
           double [] y1 = new double[M];
           double [] y2 = new double[M];
           for (int c=0; c<M; c++) {
               y1[c] = c;
               y2[c] = x[0][c];
           }
       if (scatterPlotOn)
                ((Plot2DPanel)currentPlot).addScatterPlot(name, y1, y2);
         else
                ((Plot2DPanel)currentPlot).addLinePlot(name,  y1,  y2);
           
       }
       
       else {
       
      	if (scatterPlotOn)
                ((Plot2DPanel)currentPlot).addScatterPlot(name, x[0], x[1]);
            else
               ((Plot2DPanel)currentPlot).addLinePlot(name,  x[0], x[1]);
       }
 
       
  return currentPlot;
}

   static public PlotPanel plot(Matrix x, String name) {
      return plot(x.getArray(), name);
   }
   
  static public PlotPanel plot(double [][] x, String name) {
       int N = x.length;
       int M = x[0].length;
      
        newPlot2D();
if (M ==1) {
           double [] y1 = new double[N];
           double [] y2 = new double[N];
           for (int c=0; c<N; c++) {
               y1[c] = c;
               y2[c] = x[c][0];
           }
       if (scatterPlotOn)
                ((Plot2DPanel)currentPlot).addScatterPlot(name,  y1, y2);
         else
                ((Plot2DPanel)currentPlot).addLinePlot(name, y1,  y2);
       }
       else if (N==1) {
           double [] y1 = new double[M];
           double [] y2 = new double[M];
           for (int c=0; c<M; c++) {
               y1[c] = c;
               y2[c] = x[0][c];
           }
        if (scatterPlotOn)
                ((Plot2DPanel)currentPlot).addScatterPlot(name, y1, y2);
         else
                ((Plot2DPanel)currentPlot).addLinePlot(name,  y1,  y2);
       
       }
       
       else {
       
      	if (scatterPlotOn)
                ((Plot2DPanel)currentPlot).addScatterPlot(name, x[0], x[1]);
            else
               ((Plot2DPanel)currentPlot).addLinePlot(name,  x[0], x[1]);
       }
 
return currentPlot;
}

  static public PlotPanel plot(Matrix  x, Color color, String name) {
      return plot(x.getArray(), color, name);
  }

 static public void fxplot(double [] values) {
     //javaFXPlotLine.plot(values);
 }
 
  static public PlotPanel plot(double [][] x, Color color, String name) {
       int N = x.length;
       int M = x[0].length;
      
        newPlot2D();

     if (M ==1) {
           double [] y1 = new double[N];
           double [] y2 = new double[N];
           for (int c=0; c<N; c++) {
               y1[c] = c;
               y2[c] = x[c][0];
           }
       if (scatterPlotOn)
                ((Plot2DPanel)currentPlot).addScatterPlot(name, color, y1, y2);
         else
                ((Plot2DPanel)currentPlot).addLinePlot(name, color, y1,  y2);
       }
       else if (N==1) {
           double [] y1 = new double[M];
           double [] y2 = new double[M];
           for (int c=0; c<M; c++) {
               y1[c] = c;
               y2[c] = x[0][c];
           }
        if (scatterPlotOn)
                ((Plot2DPanel)currentPlot).addScatterPlot(name, color, y1, y2);
         else
                ((Plot2DPanel)currentPlot).addLinePlot(name, color, y1,  y2);
       
       }
       
       else {
       
      	if (scatterPlotOn)
                ((Plot2DPanel)currentPlot).addScatterPlot(name, color,  x[0], x[1]);
            else
               ((Plot2DPanel)currentPlot).addLinePlot(name, color, x[0], x[1]);
       }
 

return currentPlot;
}

  static public PlotPanel plot(Matrix  x, Color color) {
      return plot(x.getArray(), color);
  }
  
  static public PlotPanel plot(double [][] x, Color color) {
      String name = "2D Plot";
       int N = x.length;
       int M = x[0].length;
      
        newPlot2D();

     if (M ==1) {
           double [] y1 = new double[N];
           double [] y2 = new double[N];
           for (int c=0; c<N; c++) {
               y1[c] = c;
               y2[c] = x[c][0];
           }
       if (scatterPlotOn)
                ((Plot2DPanel)currentPlot).addScatterPlot(name, color, y1, y2);
         else
                ((Plot2DPanel)currentPlot).addLinePlot(name, color, y1,  y2);
       }
       else if (N==1) {
           double [] y1 = new double[M];
           double [] y2 = new double[M];
           for (int c=0; c<M; c++) {
               y1[c] = c;
               y2[c] = x[0][c];
           }
           if (scatterPlotOn)
                ((Plot2DPanel)currentPlot).addScatterPlot(name, color, y1, y2);
         else
                ((Plot2DPanel)currentPlot).addLinePlot(name, color, y1,  y2);
       
       }
       
       else {
       
      	if (scatterPlotOn)
                ((Plot2DPanel)currentPlot).addScatterPlot(name, color,  x[0], x[1]);
            else
               ((Plot2DPanel)currentPlot).addLinePlot(name, color, x[0], x[1]);
       }
 
     return currentPlot;
}
  
  
       static public PlotPanel plot(float  [] []x) {
         return plot(arrToDoubleDouble(x));
       }

        static public PlotPanel plot(double [] x, double [] y, double [][] z) {
                String name = "3-D Plot";
		newPlot3D();

                ((Plot3DPanel)currentPlot).addGridPlot(name, x, y, z);
		return currentPlot;
	}

       static public PlotPanel plot(float  [] x, float [] y, float[][] z) {
         return plot(arrToDouble(x), arrToDouble(y), arrToDoubleDouble(z));
       }

       
/*
        setMarkChar('*')   // set the character used to plot the marks
        
        var t=inc(0, 0.1, 10)
        var x= sin(2.3*t)
        plotMarks(t,x)
        
        */
       
       // sets the char used for mark plots
       static public void setMarkChar(char ch) {
           PlotGlobals.defaultMarkChar = ch;
       }
       
       static public void  plotMarks(Vec t,  Vec x) {
               String name = "2-D Mark plot";
                newPlot2D();
                 int  len = t.size();
         plot(t, x, Color.WHITE);   
         for (int k=0; k<len; k++)
             markX(t.get(k), x.get(k));

       }

       static public void  plotMarks(Matrix  t,  Matrix  x) {
            plotMarks(t.getv(), x.getv());
       }

       static public void  plotMarks(double []  t,  double [] x) {
               String name = "2-D Mark plot";
               newPlot2D();
	 int  len = t.length;
         plot(t, x, Color.WHITE);
         for (int k=0; k<len; k++)
             markX(t[k], x[k]);

       }

        // plot a JSci signal
        static public PlotPanel plot(Signal sig) {
            return plot(sig.getValues());
        }

        static public PlotPanel plot(Object  x,  Object y,  Object z, String name) {
            double [][] xyz = new double [3][];
            xyz[0] = (double []) x;
            xyz[1] = (double []) y;
            xyz[2] = (double []) z;
            if (new_figure == true)  newPlot3D();

            if (scatterPlotOn)
                ((Plot3DPanel)currentPlot).addScatterPlot(name, xyz);
            else
	 	((Plot3DPanel)currentPlot).addLinePlot(name, xyz);

	    return currentPlot;
        }


 static public PlotPanel plot3d_bar(double [] x, double [] y, double [] z, String name) {
		newPlot3D();
		((Plot3DPanel)currentPlot).addBarPlot(name, x, y, z);
		return currentPlot;
	}

static public PlotPanel plot3d_bar(float  [] x, float [] y, float[] z, String name) {
         return plot3d_bar(arrToDouble(x), arrToDouble(y), arrToDouble(z), name);
       }


	static public PlotPanel surf(double [] x, double [] y, double [][]z,  String name) {
               if (currentPlot == null)
                   figure3d();
                ((Plot3DPanel)currentPlot).addGridPlot(name,x, y, z);
		return currentPlot;
	}

        static public PlotPanel surf(double [] x, double [] y, double [][]z, Color color,  boolean drawLines, boolean fillShape, String name) {
               if (currentPlot == null)
                   figure3d();
                ((Plot3DPanel)currentPlot).addGridPlot(name, color, drawLines, fillShape, x, y, z);
		return currentPlot;
	}

        static public PlotPanel surf(Vec  x, double [] y, double [][]z,  String name) {
               if (currentPlot == null)
                   figure3d();
                ((Plot3DPanel)currentPlot).addGridPlot(name,x.getv(), y, z);
		return currentPlot;
	}
        
        
        static public PlotPanel surf(double []  x, Vec  y, double [][]z,  String name) {
               if (currentPlot == null)
                   figure3d();
                ((Plot3DPanel)currentPlot).addGridPlot(name,x, y.getv(), z);
		return currentPlot;
	}
        
        
        static public PlotPanel surf(Vec x, Vec  y, double [][]z,  String name) {
               if (currentPlot == null)
                   figure3d();
                ((Plot3DPanel)currentPlot).addGridPlot(name,x.getv(), y.getv(), z);
		return currentPlot;
	}
        

     static public PlotPanel surf(float  [] x, float [] y, float[] []z, String name) {
         return surf(arrToDouble(x), arrToDouble(y), arrToDoubleDouble(z), name);
       }

  static public PlotPanel surf(double [] x, double [] y, double [][]z,  Color color, String name) {
               if (currentPlot == null)
                   figure3d();
               ((Plot3DPanel)currentPlot).addGridPlot(name, color, x, y, z);
		return currentPlot;
	}


     static public PlotPanel surf(float  [] x, float [] y, float[] []z, Color color, String name) {
         return surf(arrToDouble(x), arrToDouble(y), arrToDoubleDouble(z), color,  name);
       }
// specify Color also
      
        static public PlotPanel surf(Vec  x, double [] y, double [][]z,   Color color, String name) {
               if (currentPlot == null)
                   figure3d();
                ((Plot3DPanel)currentPlot).addGridPlot(name, color, x.getv(), y, z);
		return currentPlot;
	}
        
        
        static public PlotPanel surf(double []  x, Vec  y, double [][]z, Color color, String name) {
               if (currentPlot == null)
                   figure3d();
                ((Plot3DPanel)currentPlot).addGridPlot(name, color, x, y.getv(), z);
		return currentPlot;
	}
        
        
        static public PlotPanel surf(Vec x, Vec  y, double [][]z,  Color color, String name) {
               if (currentPlot == null)
                   figure3d();
                ((Plot3DPanel)currentPlot).addGridPlot(name, color, x.getv(), y.getv(), z);
		return currentPlot;
	}
        

  // specify Color also
static public PlotPanel surf(double [] x, double [] y, double [][]z) {
                if (currentPlot == null)
                   figure3d();
                String name = "Surface Plot";
                ((Plot3DPanel)currentPlot).addGridPlot(name,x, y, z);
                return currentPlot;
	}


  static public PlotPanel surf(float  [] x, float [] y, float[] []z) {
         return surf(arrToDouble(x), arrToDouble(y), arrToDoubleDouble(z));
       }

static public PlotPanel surf(double [] x, double [] y, double [][]z, Color color) {
                String name = "Surface Plot";
                ((Plot3DPanel)currentPlot).addGridPlot(name, color, x, y, z);
                return currentPlot;
	}


   static public PlotPanel surf(float  [] x, float [] y, float[] []z, Color color) {
         return surf(arrToDouble(x), arrToDouble(y), arrToDoubleDouble(z), color);
       }
	//////////////
	// 2D plots //
	//////////////

static public PlotPanel plot2d_scatter(Object  x,  Object y, String name) {
            double [][] xy = new double [2][];
            xy[0] = (double []) x;
            xy[1] = (double []) y;
            if (new_figure == true)  newPlot2D();
            ((Plot2DPanel)currentPlot).addScatterPlot(name,xy);
            return currentPlot;
        }


static public PlotPanel plot2d_scatter(double []  x,  double [] y, String name) {
            double [][] xy = new double [2][];
            xy[0] = x;
            xy[1] = y;
            if (new_figure == true)  newPlot2D();
            ((Plot2DPanel)currentPlot).addScatterPlot(name, xy);
            return currentPlot;
        }


static public PlotPanel plot2d_scatter(float  [] x, float [] y, String name) {
         return plot2d_scatter(arrToDouble(x), arrToDouble(y), name);
       }

static public PlotPanel plot2d_line(Object  x,  Object y, String name) {
            double [][] xy = new double [2][];
            xy[0] = (double []) x;
            xy[1] = (double []) y;
            if (new_figure == true)  newPlot2D();
            ((Plot2DPanel)currentPlot).addLinePlot(name,xy);

            return currentPlot;
   }


        static public PlotPanel plot2d_line(double []  x,  double [] y, String name) {
            double [][] xy = new double [2][];
            xy[0] = x;
            xy[1] = y;
            if (new_figure == true)  newPlot2D();
            ((Plot2DPanel)currentPlot).addLinePlot(name,xy);

            return currentPlot;
        }

        
static public PlotPanel plot(Vec x, Vec y, char type, String name) {
    return      plot(x.getv(), y.getv(), type, name);
}



static public PlotPanel plot(double [] x, double [] y, char type) {
    return      plot(x, y, type, "2-D Plot");
}


          /*
         PLOT   Linear plot.
    PLOT(X,Y) plots vector Y versus vector X. If X or Y is a matrix,
    then the vector is plotted versus the rows or columns of the matrix,
    whichever line up.  If X is a scalar and Y is a vector, length(Y)
    disconnected points are plotted.

    PLOT(Y) plots the columns of Y versus their index.
    If Y is complex, PLOT(Y) is equivalent to PLOT(real(Y),imag(Y)).
    In all other uses of PLOT, the imaginary part is ignored.

    Various line types, plot symbols and colors may be obtained with
    PLOT(X,Y,S) where S is a character string made from one element
    from any or all the following 3 columns:

           b     blue          .     point              -     solid
           g     green         o     circle             :     dotted
           r     red           x     x-mark             -.    dashdot
           c     cyan          +     plus               --    dashed
           m     magenta       *     star             (none)  no line
           y     yellow        s     square
           k     black         d     diamond
                               v     triangle (down)
                               ^     triangle (up)
                               <     triangle (left)
                               >     triangle (right)
                               p     pentagram
                               h     hexagram

    For example, PLOT(X,Y,'c+:') plots a cyan dotted line with a plus
    at each data point; PLOT(X,Y,'bd') plots blue diamond at each data
    point but does not draw any line. */

          static public PlotPanel plot(Vec  x, Vec y, char plotType) {

              return  plot(x.getv(), y.getv(), plotType, "2-D plot");

	}
          
          static public PlotPanel  plot(Vec x,  Vec y)  {
              return plot(x.getv(), y.getv());
          }
          
          static public PlotPanel  plot(Vec x,  double[] y)  {
              return plot(x.getv(), y);
          }
          
          static public PlotPanel  plot(double []  x,  Vec y)  {
              return plot(x, y.getv());
          }
          
          
          
/*
 var t = inc(0, 0.01, 10)
 var x = sin(0.56*t)
 var plotType = "b"  // blue as color
 plot(t, x, plotType)
 jplot(t, x, plotType)   // the same plot using the JFreeChart  library
           
 */ 

          /*
 
 var tv = inc(0, 0.02, 10); var xv = sin(6.7*tv);
 var t = tv.getv; var x = xv.getv
 
figure(1); plot(t, x, "b", "Blue - width: 10 ",10)    // a blue curve with width 10

 figure(1); plot(t, x, ":", "Dotted")   // a dotted curve

 figure(2); plot(t, x, "--", "Dashed")
 figure(3); plot(t, x, "t5", "Thick-Line")   // a line with thickness 5
 */
static public PlotPanel plot(double [] x, double [] y, String  typeStr, String name) {
    return  plot(x, y, typeStr, name, 1);
}

static public PlotPanel plot(double [] x, double [] y, String  typeStr, int lineWidth) {
    String name = "2-D Plot";
    return plot(x, y, typeStr, name, lineWidth);
}

static public PlotPanel plot(double [] x, double [] y, String  typeStr, String name, int lineWidth) {
             double [][] xy = new double [2][];
            xy[0] = x;     xy[1] = y;
            if (new_figure == true)  newPlot2D();
            char type='k';
            if (typeStr.length()==1)
                type=typeStr.charAt(0);
            if (typeStr.equals("--"))  // dashed
                type='-';
            else if (typeStr.equals("-."))   // dash-dot
                type = '#';
            else if (typeStr.charAt(0)=='t') {  // control line thickness
                String thickness = typeStr.substring(1, typeStr.length());
                float fthick = Float.valueOf(thickness);
                AbstractDrawer.line_type = AbstractDrawer.THICK_LINE;
                AbstractDrawer.lineThickness = fthick;
                type='t';
            }

 switch (type) {
     case 'b':  ((Plot2DPanel)currentPlot).addLinePlot(name, Color.BLUE, xy, lineWidth); break;
     case 'g':  ((Plot2DPanel)currentPlot).addLinePlot(name, Color.GREEN, xy, lineWidth); break;
     case 'r':  ((Plot2DPanel)currentPlot).addLinePlot(name, Color.RED, xy, lineWidth); break;
     case 'c':  ((Plot2DPanel)currentPlot).addLinePlot(name, Color.CYAN, xy, lineWidth); break;
     case 'm':  ((Plot2DPanel)currentPlot).addLinePlot(name, Color.MAGENTA, xy, lineWidth); break;
     case 'y':  ((Plot2DPanel)currentPlot).addLinePlot(name, Color.YELLOW, xy, lineWidth); break;
     case 'k':  ((Plot2DPanel)currentPlot).addLinePlot(name, Color.BLACK, xy, lineWidth); break;
     case '.':  ((Plot2DPanel)currentPlot).addLinePlot(name, xy, true); break;
     case 'x':     ((Plot2DPanel)currentPlot).addMarkedLinePlot(name, Color.BLACK, new Font("Arial", Font.BOLD, 12), 'x', xy[0], xy[1]);    break;
     case '*':     ((Plot2DPanel)currentPlot).addMarkedLinePlot(name, Color.BLACK, new Font("Arial", Font.BOLD, 12), '*', xy[0], xy[1]);    break;
     case 'o':     ((Plot2DPanel)currentPlot).addMarkedLinePlot(name, Color.BLACK, new Font("Arial", Font.BOLD, 12), 'o', xy[0], xy[1]);    break;
     case 's':     ((Plot2DPanel)currentPlot).addMarkedLinePlot(name, Color.BLACK, new Font("Arial", Font.BOLD, 12), '*', xy[0], xy[1]);    break;
     case 'd':   ((Plot2DPanel)currentPlot).addMarkedLinePlot(name, Color.BLACK, new Font("Arial", Font.BOLD, 12),  '\u0101', xy[0], xy[1]);    break;
     case ':':   	((Plot2DPanel)currentPlot).addLinePlot(name, Color.GREEN, AbstractDrawer.DOTTED_LINE, lineWidth, xy);  break;
     case 't':    	((Plot2DPanel)currentPlot).addLinePlot(name, Color.GREEN, AbstractDrawer.THICK_LINE, (int)AbstractDrawer.lineThickness, xy);  break;
     case '-':    	((Plot2DPanel)currentPlot).addLinePlot(name, Color.GREEN, AbstractDrawer.PATTERN_LINE, lineWidth, xy);  break;


     default:  break;
 }
 return currentPlot;
}

static public PlotPanel plot(Vec  x, Vec  y, Color color, String  typeStr, String name, int lineWidth) {
  return   plot(x.getv(), y.getv(), color, typeStr, name, lineWidth);
}

static public PlotPanel plot(double []  x, Vec  y, Color color, String  typeStr, String name, int lineWidth) {
  return   plot(x, y.getv(), color, typeStr, name, lineWidth);
}

static public PlotPanel plot(Vec  x, double []  y, Color color, String  typeStr, String name, int lineWidth) {
  return   plot(x.getv(), y, color, typeStr, name, lineWidth);
}


static public PlotPanel plot(double [] x, double [] y, Color color, String  typeStr, String name, int lineWidth) {
             double [][] xy = new double [2][];
           
           xy[0] = x;     xy[1] = y;
            if (new_figure == true)  newPlot2D();
            char type='k';
            if (typeStr.length()==1)
                type=typeStr.charAt(0);
            if (typeStr.equals("--"))  // dashed
                type='-';
            else if (typeStr.equals("-."))   // dash-dot
                type = '#';
            //else if (typeStr.charAt(0)=='t') {  // control line thickness
              //  String thickness = typeStr.substring(1, typeStr.length());
               // float fthick = Float.valueOf(thickness);
                if (lineWidth > 1) {
                AbstractDrawer.lineThickness = lineWidth;
                type='t';
                }
            

 switch (type) {
     case 'l':   ((Plot2DPanel)currentPlot).addLinePlot(name,  color, xy, false); break;   // a line plot
     case '.':   ((Plot2DPanel)currentPlot).addLinePlot(name,  color,  xy, true); break;
     case 'x':     ((Plot2DPanel)currentPlot).addMarkedLinePlot(name, color, new Font("Arial", Font.BOLD, 12), 'x', xy[0], xy[1]);    break;
     case '*':     ((Plot2DPanel)currentPlot).addMarkedLinePlot(name, color, new Font("Arial", Font.BOLD, 12), '*', xy[0], xy[1]);    break;
     case 'o':     ((Plot2DPanel)currentPlot).addMarkedLinePlot(name, color, new Font("Arial", Font.BOLD, 12), 'o', xy[0], xy[1]);    break;
     case 's':     ((Plot2DPanel)currentPlot).addMarkedLinePlot(name, color, new Font("Arial", Font.BOLD, 12), '*', xy[0], xy[1]);    break;
     case 'd':   ((Plot2DPanel)currentPlot).addMarkedLinePlot(name, color, new Font("Arial", Font.BOLD, 12),  '\u0101', xy[0], xy[1]);    break;
     case ':':   	((Plot2DPanel)currentPlot).addLinePlot(name, color, AbstractDrawer.DOTTED_LINE, lineWidth, xy);  break;
     case 't':    	((Plot2DPanel)currentPlot).addLinePlot(name, color, AbstractDrawer.THICK_LINE, (int)AbstractDrawer.lineThickness, xy);  break;
     case '-':    	((Plot2DPanel)currentPlot).addLinePlot(name, color, AbstractDrawer.PATTERN_LINE, lineWidth, xy);  break;


     default:  break;
 }
 return currentPlot;
}

static public PlotPanel plot(double [] x,  double []  y, String  titleOfPlot) {
      return  plot(x, y, Color.BLACK, titleOfPlot);
}

static public PlotPanel plot(Vec x,  Vec  y, String  titleOfPlot) {
     return  plot(x.getv(), y.getv(),  titleOfPlot);
}

static public PlotPanel plot(Vec x,  Vec  y, String  typeStr, String name) {
      return  plot(x.getv(), y.getv(), typeStr, name);
}

static public PlotPanel plot(Vec x,  Vec  y, String  typeStr, String name, int lineWidth) {
      return  plot(x.getv(), y.getv(), typeStr, name, lineWidth);
}

static public PlotPanel plot(Vec x,  Vec  y, String  typeStr,  int lineWidth) {
    String name="2-D plot";
    return plot(x, y, typeStr, name, lineWidth);
}


// var t = inc(0, 0.1, 20); var x = sin(4.3*t); plot(t,x, true);
static public PlotPanel plot(Vec  x,  Vec  y, boolean dotted) {
            String name = "Line Plot";
            double [][] xy = new double [2][];
            xy[0] = x.getv();
            xy[1] = y.getv();
            if (new_figure == true)  newPlot2D();
	    ((Plot2DPanel)currentPlot).addLinePlot(name,xy, dotted);

	    return currentPlot;
        }

static public PlotPanel plot(double [] x, double [] y, double [] z, Color color)  {
            return plot(x, y, z, color, "Figure");
        }

static public PlotPanel plot(float  [] x, float [] y, float []z, Color color) {
         return plot(arrToDouble(x), arrToDouble(y), arrToDouble(z), color);
       }


          // plot Scala's Vectors
static public PlotPanel plot(Vec x)  {
            return plot(x.getv());
        }

static public PlotPanel plot(Vec x, String title) {
            return plot(x.getv(), title);
        }

static public PlotPanel plot(Vec x, Color color, String title) {
            return plot(x.getv(), color, title);
        }

        
 static public PlotPanel plot(Vec  x,  Color color) {
    return  plot(x.getv(), color);
 }

 static public PlotPanel plot( String name, Vec x, Vec y)  {
            return plot(x.getv(), y.getv(), name);
        }

 static public PlotPanel plot(Vec x, Vec y, String name, Color color)  {
            return plot(x.getv(), y.getv(), name, color);
        }

 static public PlotPanel plot(Vec x, Vec y, Color color, String name)  {
            return plot(x.getv(), y.getv(), color, name);
        }

 static public PlotPanel plot(Vec x, Vec y, Color color)  {
            return plot(x.getv(), y.getv(), color, "Figure ");
        }

 static public PlotPanel plot(Vec x, Vec y, Vec z, Color color, String name)  {
            return plot(x.getv(), y.getv(), z.getv(), color, name);
        }


 static public PlotPanel plot(Vec x, Vec y, Vec z)  {
            return plot(x.getv(), y.getv(), z.getv());
        }

 static public PlotPanel plot(Vec x, Vec y, Vec z, Color color)  {
            return plot(x.getv(), y.getv(), z.getv(), color);
        }


 
 static public PlotPanel plot(Matrix x, Matrix y, Matrix z, Color color, String name)  {
            return plot(x.getv(), y.getv(), z.getv(), color, name);
        }


 static public PlotPanel plot(Matrix x, Matrix y, Matrix z, Color color)  {
            return plot(x.getv(), y.getv(), z.getv(), color);
        }

 
    static public PlotPanel plot(Matrix x, Matrix y)  {
            return plot(x.getv(),  y.getv());
        }

    
        static public PlotPanel plot(Matrix x, Matrix y, String name, Color color)  {
            return plot(x.getv(), y.getv(), name, color);
        }

        static public PlotPanel plot(Matrix x, Matrix y, Color color, String name)  {
            return plot(x.getv(), y.getv(), color, name);
        }


        static public PlotPanel plot( String name, double []  x,  double [] y) {
            int xl = x.length;
            double [][] xy = new double [2][xl];
            xy[0] = new double[xl];
            for (int k=0; k<xl; k++)
                 xy[0][k] = x[k];
            xy[1] = new double[xl];
            for (int k=0; k<xl; k++)
                xy[1][k] = y[k];
            if (new_figure == true)  newPlot2D();
	    if (holdOnMode == false)
                    clf(currentPlot);

            if (scatterPlotOn)
            	((Plot2DPanel)currentPlot).addScatterPlot(name, xy);
	    else
	 	((Plot2DPanel)currentPlot).addLinePlot(name, xy);

            return currentPlot;
        }

        // end Scala matrices/vectors

        static public PlotPanel plot(double []  x,  double [] y,  String name, Color color) {
           return plot(x, y, color, name);
       }

       static public PlotPanel plot(float  [] x, float [] y, String name, Color color) {
         return plot(arrToDouble(x), arrToDouble(y), name, color);
       }

        static public PlotPanel plot(double []  x,  double [] y,  Color color, String name) {
            int xl = x.length;
            double [][] xy = new double [2][xl];
            xy[0] = new double[xl];
            for (int k=0; k<xl; k++)
                 xy[0][k] = x[k];
            xy[1] = new double[xl];
            for (int k=0; k<xl; k++)
                xy[1][k] = y[k];
            if (new_figure == true)  newPlot2D();
	    if (holdOnMode == false)
                    clf(currentPlot);
        if (scatterPlotOn)
            	((Plot2DPanel)currentPlot).addScatterPlot(name, color,  xy);
	    else
	 	((Plot2DPanel)currentPlot).addLinePlot(name, color, xy);

            return currentPlot;
        }

       static public PlotPanel plot(float  [] x, float [] y, Color color, String name) {
         return plot(arrToDouble(x), arrToDouble(y), color, name );
       }

       static public PlotPanel plot(double []  x,  double [] y) {
            String name = "2-D Line Plot";
            int xl = x.length;
            double [][] xy = new double [2][xl];
            xy[0] = new double[xl];
            for (int k=0; k<xl; k++)
                 xy[0][k] = x[k];
            xy[1] = new double[xl];
            for (int k=0; k<xl; k++)
                xy[1][k] = y[k];
            if (new_figure == true)  newPlot2D();
	    if (holdOnMode == false)
                    clf(currentPlot);
        if (scatterPlotOn)
            	((Plot2DPanel)currentPlot).addScatterPlot(name, xy);
	    else
	 	((Plot2DPanel)currentPlot).addLinePlot(name, xy);

	    return currentPlot;
        }

        static public PlotPanel plot(float  [] x, float [] y) {
         return plot(arrToDouble(x), arrToDouble(y));
       }


        static public void setName(String name) {
            currentPlot.setName(name);
        }
        
static public PlotPanel plot(double []  x,  double [] y, Color color ) {
            int xl = x.length;
            String name = "2-D Line Plot";
            double [][] xy = new double [2][xl];
            xy[0] = new double[xl];
            for (int k=0; k<xl; k++)
                 xy[0][k] = x[k];
            xy[1] = new double[xl];
            for (int k=0; k<xl; k++)
                xy[1][k] = y[k];
            if (new_figure == true)  newPlot2D();
	    if (holdOnMode == false)  // remove previous plots
                    clf(currentPlot);

            if (scatterPlotOn)
            	((Plot2DPanel)currentPlot).addScatterPlot(name, color, xy);
	    else
	((Plot2DPanel)currentPlot).addLinePlot(name, color, xy);

	    return currentPlot;
        }

        static public PlotPanel plot(float  [] x, float [] y, Color color) {
         return plot(arrToDouble(x), arrToDouble(y), color );
       }

static public PlotPanel plot(double []  x) {
        int xlen = x.length;
        String name = "2-D Line Plot";
        double [][] xy = new double [2][xlen];
        xy[0] = new double [ xlen ];
        for (int k=0; k<xlen; k++)
              xy[0][k] = k;
        for (int k=0; k<xlen; k++)
            xy[1][k] = x[k];
            if (new_figure == true)  newPlot2D();
               if (holdOnMode == false)
                    clf(currentPlot);

            if (scatterPlotOn)
            	((Plot2DPanel)currentPlot).addScatterPlot(name, xy);
	    else
	((Plot2DPanel)currentPlot).addLinePlot(name, xy);


       return currentPlot;
  }

 static public PlotPanel plot(float  [] x) {
         return plot(arrToDouble(x) );
   }

 static public PlotPanel plot(double []  x, Color color ) {
            int xlen = x.length;
            String name = "2-D Line Plot";
            double [][] xy = new double [2][xlen];
            xy[0] = new double [ xlen ];
            for (int k=0; k<xlen; k++)
                xy[0][k] = k;
            for (int k=0; k<xlen; k++) 
                xy[1][k] = x[k];
            if (new_figure == true)  newPlot2D();
               if (holdOnMode == false)
                    clf(currentPlot);

            if (scatterPlotOn)
            	((Plot2DPanel)currentPlot).addScatterPlot(name, color, xy);
	    else
 	((Plot2DPanel)currentPlot).addLinePlot(name, color, xy);

    return currentPlot;
  }

 

 static public PlotPanel plot(float  [] x,  Color color) {
         return plot(arrToDouble(x), color);
       }

   static public PlotPanel plot(double []  x, String name) {
            Color color =  Color.BLACK;
            if (name.length() == 1) {
                char colorChar = name.charAt(0);
            switch (colorChar) {
                case 'b':  color = Color.BLUE;
                               break;
                case 'g':  color = Color.GREEN;
                                break;
                case 'r':  color = Color.RED;
                                break;
                case 'm': color = Color.MAGENTA;
                                break;
                case 'y':  color = Color.YELLOW;
                                break;
                default:   color = Color.BLACK;
                                break;
              }

            }

            int xlen = x.length;
            double [][] xy = new double [2][xlen];
            xy[0] = new double [ xlen ];
            for (int k=0; k<xlen; k++)
            {
                xy[0][k] = k;
                xy[1][k] =  x[k];
            }
            if (new_figure == true)  newPlot2D();
	    if (holdOnMode == false)
                    clf(currentPlot);

            if (scatterPlotOn)
            	((Plot2DPanel)currentPlot).addScatterPlot(name, color, xy);
	    else
	((Plot2DPanel)currentPlot).addLinePlot(name, color, xy);

            return currentPlot;

      }

   /*
    
    var t = linspace(0, 10, 2000)
    var x = sin(2.3*t)
    rplot(x, "Real-Time Plot")
    */
   static public PlotPanel rplot(double []  x, String name) {
            Color color =  Color.BLACK;
            if (name.length() == 1) {
                char colorChar = name.charAt(0);
            switch (colorChar) {
                case 'b':  color = Color.BLUE;
                               break;
                case 'g':  color = Color.GREEN;
                                break;
                case 'r':  color = Color.RED;
                                break;
                case 'm': color = Color.MAGENTA;
                                break;
                case 'y':  color = Color.YELLOW;
                                break;
                default:   color = Color.BLACK;
                                break;
              }

            }

            int xlen = x.length;
            double [][] xy = new double [2][xlen];
            xy[0] = new double [ xlen ];
            for (int k=0; k<xlen; k++)
            {
                xy[0][k] = k;
                xy[1][k] =  x[k];
            }
            if (new_figure == true)  newPlot2D();
	    if (holdOnMode == false)
                    clf(currentPlot);

            ((Plot2DPanel)currentPlot).addRealTimePlot(name, color, xy);
	
            return currentPlot;

      }


   static public PlotPanel plot(float  [] x, String name) {
         return plot(arrToDouble(x), name );
       }

      static public PlotPanel plot(double []  x,  Color color, String name) {
            int xlen = x.length;
            double [][] xy = new double [2][xlen];
            xy[0] = new double [ xlen ];
            for (int k=0; k<xlen; k++) {
                xy[0][k] = k;
                xy[1][k] = x[k];
            }
            if (new_figure == true)  newPlot2D();
	    if (holdOnMode == false)
                    clf(currentPlot);

            if (scatterPlotOn)
            	((Plot2DPanel)currentPlot).addScatterPlot(name, color, xy);
	    else
	 	((Plot2DPanel)currentPlot).addLinePlot(name, color, xy);

            return currentPlot;
        }


       static public PlotPanel plot(float  [] x, Color color, String name) {
         return plot(arrToDouble(x), color, name );
       }

        /*
           b     blue          .     point              -     solid
           g     green         o     circle             :     dotted
           r     red           x     x-mark             -.    dashdot
           c     cyan          +     plus               --    dashed
           m     magenta       *     star             (none)  no line
           y     yellow        s     square
           k     black         d     diamond
                               v     triangle (down)
                               ^     triangle (up)
                               <     triangle (left)
                               >     triangle (right)
                               p     pentagram
                               h     hexagram
           */

        static public PlotPanel plot2d_staircase(Object  x,  Object y, String name) {
            double [][] xy = new double [2][];
            xy[0] = (double []) x;
            xy[1] = (double []) y;
            if (new_figure == true)  newPlot2D();
	    ((Plot2DPanel)currentPlot).addStaircasePlot(name, xy);
	    return currentPlot;
        }


        static public PlotPanel plot2d_staircase(double []  x,  double [] y, String name) {
            double [][] xy = new double [2][];
            xy[0] = x;
            xy[1] = y;
            if (new_figure == true)  newPlot2D();
	    ((Plot2DPanel)currentPlot).addStaircasePlot(name, xy);
	    return currentPlot;
        }

       static public PlotPanel plot2d_staircase(float  [] x, float [] y, String name) {
         return plot2d_staircase(arrToDouble(x), arrToDouble(y),  name );
       }

        static public PlotPanel plot2d_staircase(double []  x,  double [] y, Color color, String name) {
            double [][] xy = new double [2][];
            xy[0] = x;
            xy[1] = y;
            if (new_figure == true)  newPlot2D();
	    ((Plot2DPanel)currentPlot).addStaircasePlot(name, color, xy);
	    return currentPlot;
        }


       static public PlotPanel plot2d_staircase(float  [] x, float [] y, Color color, String name) {
         return plot2d_staircase(arrToDouble(x), arrToDouble(y),  color, name );
       }



        static public PlotPanel plot2d_bar(double []  x,  double [] y, String name) {
            double [][] xy = new double [2][];
            xy[0] = x;
            xy[1] =  y;
            if (new_figure == true)  newPlot2D();
	    ((Plot2DPanel)currentPlot).addBarPlot(name,xy);
	    return currentPlot;
        }



        
        static public PlotPanel plot2d_bar(double []  x,  double [] y, Color color, String name) {
            double [][] xy = new double [2][];
            xy[0] = x;
            xy[1] =  y;
            if (new_figure == true)  newPlot2D();
	    ((Plot2DPanel)currentPlot).addBarPlot(name, color, xy);
	    return currentPlot;
        }
        
           static public PlotPanel plot2d_contour(double [][]XY, String name) {
            	  
            if (new_figure == true)  newPlot2D();
	        if (holdOnMode == false)
                    clf(currentPlot);
		((Plot2DPanel)currentPlot).addContourPlot(name, XY);

                return currentPlot;

        }


       static public PlotPanel plot2d_countour(float  [][] xy, String name) {
         return plot2d_contour(arrToDoubleDouble(xy), name );
       }

        static public PlotPanel plot2d_contour(double [][]XY, Color color, String name) {
            	  
            if (new_figure == true)  newPlot2D();
	        if (holdOnMode == false)
                    clf(currentPlot);
		((Plot2DPanel)currentPlot).addContourPlot(name, XY);

                return currentPlot;
        }


       static public PlotPanel plot2d_countour(float  [][] xy, Color color, String name) {
         return plot2d_contour(arrToDoubleDouble(xy), color, name );
       }

        static public PlotPanel plot2d_contour(Matrix mXY, String name) {
                double [][] XY = mXY.getArray();
       if (new_figure == true)  newPlot2D();
	        ((Plot2DPanel)currentPlot).addContourPlot(name, XY);
                return currentPlot;
        }


        static public PlotPanel plot2d_contour(Matrix mXY,  Color color, String name) {
            double [][] XY = mXY.getArray();
       if (new_figure == true)  newPlot2D();
	        ((Plot2DPanel)currentPlot).addContourPlot(name, XY);
                return currentPlot;

        }

             

        static public PlotPanel plot2d_scalogram(double [][]XY, String name) {
            	if (new_figure == true)  newPlot2D();
	        ((Plot2DPanel)currentPlot).addScalogramPlot(name, XY);
                return currentPlot;
        }

       static public PlotPanel plot2d_scalogram(float  [][] xy, String name) {
         return plot2d_scalogram(arrToDoubleDouble(xy), name );
       }

        static public PlotPanel plot2d_scalogram(Matrix mXY, String name) {
            double [][] XY = mXY.getArray();
            if (new_figure == true)  newPlot2D();
	        ((Plot2DPanel)currentPlot).addScalogramPlot(name, XY);
                return currentPlot;
        }



        static public PlotPanel plot2d_cloud(double [][] sample, int slices_x,int slices_y, String name) {
		if (new_figure == true)  newPlot2D();
	    	((Plot2DPanel)currentPlot).addCloudPlot(name, sample, slices_x, slices_y);
		return currentPlot;
	}


       static public PlotPanel plot2d_cloud(float  [][] sample, int slices_x,int slices_y, String name) {
         return plot2d_cloud(sample, slices_x, slices_y,  name);
       }

        static public PlotPanel plot2d_cloud(double[][] sample, int slices_x,int slices_y, Color color, String name) {
		if (new_figure == true)  newPlot2D();
	    	((Plot2DPanel)currentPlot).addCloudPlot(name, color, sample, slices_x, slices_y);
		return currentPlot;
	}


       static public PlotPanel plot2d_cloud(float  [][] sample, int slices_x, int slices_y,  Color color, String name) {
         return plot2d_cloud(sample, slices_x, slices_y,  color, name);
       }


        static public PlotPanel plot3d_cloud(Matrix sample, int slices_x,int slices_y,int slices_z, String name) {
        	newPlot3D();
	((Plot3DPanel)currentPlot).addCloudPlot(name,sample.getArray(),slices_x, slices_y, slices_z);
	return currentPlot;
	}



 static public PlotPanel plot3d_cloud(Matrix sample, int slices_x,int slices_y,int slices_z, Color color, String name) {
            newPlot3D();
            ((Plot3DPanel)currentPlot).addCloudPlot(name, color, sample.getArray(),slices_x, slices_y, slices_z);
            return currentPlot;
	}



        static public PlotPanel plot2d_histogram(double [][] xy, int slices_x, String name) {
		if (new_figure == true)  newPlot2D();
                ((Plot2DPanel)currentPlot).addHistogramPlot(name, xy, slices_x);
                return currentPlot;
        }

        static public PlotPanel plot2d_histogram(float [][] xy, int slices_x, String name) {
             return    plot2d_histogram(xy, slices_x, name);
        }

        static public PlotPanel plot2d_histogram(double [] x, double [] y, int slices_x, String name) {
		if (new_figure == true)  newPlot2D();
                double [][] xy = { x, y};
                ((Plot2DPanel)currentPlot).addHistogramPlot(name, xy, slices_x);
                return currentPlot;
        }

        static public PlotPanel plot2d_histogram(double [] x, int slices_x, String name) {
		if (new_figure == true)  newPlot2D();
                ((Plot2DPanel)currentPlot).addHistogramPlot(name, x, slices_x);
                return currentPlot;
        }
        
        
        static public PlotPanel plot2d_histogram(Vec x, int slices_x, String name) {
		if (new_figure == true)  newPlot2D();
                ((Plot2DPanel)currentPlot).addHistogramPlot(name, x.getv(), slices_x);
                return currentPlot;
        }

        static public PlotPanel plot2d_histogram(float [] x, float [] y,  int slices_x, String name) {
           return  plot2d_histogram(x, y,  slices_x, name);
        }

        static public  void setAxes(int axe, String axisType)  {  // ?????
            currentPlot.setAxisScale(axe, axisType);
        }

        static public void  markX(double  x, double  y) {
            String sch = String.valueOf(PlotGlobals.defaultMarkChar);
            currentPlot.plotCanvas.addLabel(sch, Color.BLUE, x, y);
        }

      static public void  markX(int x, int y) {
            String sch = String.valueOf(PlotGlobals.defaultMarkChar);
            currentPlot.plotCanvas.addDLabel(sch, Color.BLUE, x, y);
        }

      static public void  markX(double  x, double  y, Color c) {
            String sch = String.valueOf(PlotGlobals.defaultMarkChar);
            currentPlot.plotCanvas.addLabel(sch, c, x, y);
        }

     static public void  markX(int x, int y, Color c) {
            String sch = String.valueOf(PlotGlobals.defaultMarkChar);
            currentPlot.plotCanvas.addDLabel(sch, c, x, y);
        }

     static public void  mark(char ch, double  x, double  y) {
            String sch = String.valueOf(PlotGlobals.defaultMarkChar);
            currentPlot.plotCanvas.addLabel(sch, Color.BLUE, x, y);
        }

      static public void  mark(char ch, int x, int y) {
            String sch = String.valueOf(PlotGlobals.defaultMarkChar);
            currentPlot.plotCanvas.addDLabel(sch, Color.BLUE, x, y);
        }

      static public void  mark(char ch, double  x, double  y, Color c) {
            String sch = String.valueOf(PlotGlobals.defaultMarkChar);
            currentPlot.plotCanvas.addLabel(sch, c, x, y);
        }

     static public void  mark(char ch, int x, int y, Color c) {
            String sch = String.valueOf(PlotGlobals.defaultMarkChar);
            currentPlot.plotCanvas.addDLabel(sch, c, x, y);
        }


static public void  markX(double  x, double  y, Font f) {
            String sch = String.valueOf(PlotGlobals.defaultMarkChar);
            currentPlot.plotCanvas.addLabel(sch, Color.BLUE, f, x, y);
        }

      static public void  markX(int x, int y, Font f) {
            String sch = String.valueOf(PlotGlobals.defaultMarkChar);
            currentPlot.plotCanvas.addDLabel(sch, Color.BLUE, f, x, y);
        }

      static public void  markX(double  x, double  y, Color c, Font f) {
            String sch = String.valueOf(PlotGlobals.defaultMarkChar);
            currentPlot.plotCanvas.addLabel(sch, c, f, x, y);
        }

     static public void  markX(int x, int y, Color c, Font f) {
             String sch = String.valueOf(PlotGlobals.defaultMarkChar);
             currentPlot.plotCanvas.addDLabel(sch, c, f, x, y);
        }

     static public void  mark(char ch, double  x, double  y, Font f) {
            String sch = String.valueOf(PlotGlobals.defaultMarkChar);
            currentPlot.plotCanvas.addLabel(sch, Color.BLUE, f, x, y);
        }

      static public void  mark(char ch, int x, int y, Font f) {
            String sch = String.valueOf(PlotGlobals.defaultMarkChar);
            currentPlot.plotCanvas.addDLabel(sch, Color.BLUE, f, x, y);
        }

      static public void  mark(char ch, double  x, double  y, Color c, Font f) {
            String sch = String.valueOf(PlotGlobals.defaultMarkChar);
            currentPlot.plotCanvas.addLabel(sch, c, f, x, y);
        }

     static public void  mark(char ch, int x, int y, Color c, Font f) {
            String sch = String.valueOf(PlotGlobals.defaultMarkChar);
            currentPlot.plotCanvas.addDLabel(sch, c, f, x, y);
        }

// take font size only
     static public void  markX(double  x, double  y, int fontSize ) {
            Font f = new Font("Arial", Font.BOLD, fontSize);
            String sch = String.valueOf(PlotGlobals.defaultMarkChar);
            currentPlot.plotCanvas.addLabel(sch, Color.BLUE, f, x, y);
        }

      static public void  markX(int x, int y, int fontSize ) {
            Font f = new Font("Arial", Font.BOLD, fontSize);
            String sch = String.valueOf(PlotGlobals.defaultMarkChar);
            currentPlot.plotCanvas.addDLabel(sch, Color.BLUE, f, x, y);
        }

      static public void  markX(double  x, double  y, Color c, int fontSize ) {
            Font f = new Font("Arial", Font.BOLD, fontSize);
            String sch = String.valueOf(PlotGlobals.defaultMarkChar);
            currentPlot.plotCanvas.addLabel(sch, c, f, x, y);
        }

     static public void  markX(int x, int y, Color c, int fontSize ) {
            Font f = new Font("Arial", Font.BOLD, fontSize);
            String sch = String.valueOf(PlotGlobals.defaultMarkChar);
            currentPlot.plotCanvas.addDLabel(sch, c, f, x, y);
        }

     static public void  mark(char ch, double  x, double  y, int fontSize ) {
            Font f = new Font("Arial", Font.BOLD, fontSize);
            String sch = String.valueOf(ch);
            currentPlot.plotCanvas.addLabel(sch, Color.BLUE, f, x, y);
        }

      static public void  mark(char ch, int x, int y, int fontSize ) {
            Font f = new Font("Arial", Font.BOLD, fontSize);
            String sch = String.valueOf(ch);
            currentPlot.plotCanvas.addDLabel(sch, Color.BLUE, f, x, y);
        }

      static public void  mark(char ch, double  x, double  y, Color c, int fontSize ) {
            Font f = new Font("Arial", Font.BOLD, fontSize);
            String sch = String.valueOf(ch);
            currentPlot.plotCanvas.addLabel(sch, c, f, x, y);
        }

     static public void  mark(char ch, int x, int y, Color c, int fontSize ) {
            Font f = new Font("Arial", Font.BOLD, fontSize);
            String sch = String.valueOf(ch);
            currentPlot.plotCanvas.addDLabel(sch, c, f, x, y);
        }

 

        static public PlotPanel plotv(Vector <Double>  x, Vector <Double> y, Vector <Double> z) {
           Color color = Color.BLACK;
           String name = "2-D  Vector Plot";
           return plotv(x, y, color, name);
       }

       static public PlotPanel plotV(Vector <double [] >  x) {
           Color color = Color.BLACK;
           String name = "2-D  Vector Plot";
         return plotV(x, color, name);
       }



       static public PlotPanel plotv(Vector <Double>  x, Vector <Double> y, Color color) {
           String name = "2-D  Vector Plot";
           return plotv(x,  y, color, name);
       }

       static public PlotPanel plotv(Vector <double [] >  x, Color color) {
           String name = "2-D  Vector Plot";
         return plotV(x, color, name);
       }

       static public PlotPanel plotv(Vector <Double>  x, Vector <Double> y, Vector <Double> z, String name) {
           Color color = Color.BLACK;
           return plotv(x, y, z, color, name);
       }

       static public PlotPanel plotV(Vector <double [] >  x, String name) {
           Color color = Color.BLACK;
           return plotV(x, color, name);
       }



static public PlotPanel plotv(Vector <Double>  x, Vector <Double> y, Color color, String name) {
           double [] xx = toArr(x); double [] yy = toArr(y);
           	newPlot2D();
	          if (scatterPlotOn)
                ((Plot2DPanel)currentPlot).addScatterPlot(name, color, xx, yy);
            else
	 	((Plot2DPanel)currentPlot).addLinePlot(name, color, xx, yy);

        	return currentPlot;
	}


static public PlotPanel plotv(Vector <Double>  x, Vector <Double> y, Vector <Double> z, Color color, String name) {
           double [] xx = toArr(x); double [] yy = toArr(y);  double [] zz = toArr(z);
           	newPlot3D();
	          if (scatterPlotOn)
                ((Plot3DPanel)currentPlot).addScatterPlot(name, color, xx, yy, zz);
            else
	 	((Plot3DPanel)currentPlot).addLinePlot(name, color, xx, yy, zz);

        	return currentPlot;
	}

     static public PlotPanel plotV(Vector <double [] >  x, Color color, String name) {
         double [][] elems = toArrv(x);
         int siz = elems.length;
         int dims = elems[0].length;   // dimensions of elements in the Vector
         if (dims == 2)  {  // two-dimensional case
               double [] xelem = elems[0];
               double [] yelem = elems[1];
               newPlot2D();
	          if (scatterPlotOn) {
                ((Plot2DPanel)currentPlot).addScatterPlot(name, color, xelem, yelem);
                  }
            else
	 	((Plot2DPanel)currentPlot).addLinePlot(name, color, xelem, yelem);
           }
           else { // three-dimensional case
               double [] xelem = elems[0];
               double [] yelem = elems[1];
               double [] zelem = elems[2];
               double [][] allelems = new double[3][];
               newPlot3D();
	         if (scatterPlotOn)
                ((Plot3DPanel)currentPlot).addScatterPlot(name, color, xelem, yelem, zelem);
            else
	 	((Plot3DPanel)currentPlot).addLinePlot(name, color, xelem, yelem, zelem);

           }

        	return currentPlot;
	}


     
  // Apache Commons plotting
  groovySci.math.plot.PlotPanel  plotApacheCommonsComplexArray( org.apache.commons.math3.complex.Complex  [] y) 
             {
              Color color = Color.GREEN;
              String plotName = "Real Values";
              int lineWidth = 1;
              String plotType = "l";
              
                 // construct time axis
               int  N = y.length; 
               double []  t = new  double[N];
               for  (int k = 0; k<N; k++)    t[k] = k;
             // construct array of real values
               double [] yreals = new double[N];
               for (int k=0; k < N; k++) 
                  yreals[k] = y[k].getReal();
               
        return groovySci.math.plot.plot.plot(t, yreals,  color, plotType,  plotName, lineWidth); 
      }


    // plot FFT results, the real values
  static public groovySci.math.plot.PlotPanel  plotfftReals( FFTResults  y) 
             {
              Color color = Color.GREEN;
              String plotName = "Real Values";
              int lineWidth = 1;
              String plotType = "l";
              
                 // construct time axis
                 // construct time axis
               int  N = y.realFFTs.length; 
               double []  t = new  double[N];
               for  (int k = 0; k<N; k++)    t[k] = k;
             
        return groovySci.math.plot.plot.plot(t, y.realFFTs,  color, plotType,  plotName, lineWidth); 
      }

  // plot FFT results, the imaginary values
static public   groovySci.math.plot.PlotPanel  plotfft( FFTResults  y)  {
     return plotfftReals(y);
  }
  
    // plot FFT results, the imaginary values
 static public  groovySci.math.plot.PlotPanel  plotfftImags( FFTResults  y) 
             {
              Color color = Color.GREEN;
              String plotName = "Real Values";
              int lineWidth = 1;
              String plotType = "l";
              
                 // construct time axis
                 // construct time axis
               int  N = y.realFFTs.length; 
               double []  t = new  double[N];
               for  (int k = 0; k<N; k++)    t[k] = k;
             
        return groovySci.math.plot.plot.plot(t, y.imFFTs,  color, plotType,  plotName, lineWidth); 
      }

}


