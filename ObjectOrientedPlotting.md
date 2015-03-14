# Introduction #

`Plotting routines tend to be complicated and can easily confuse the user. Therefore, we start to develop more convenient, object oriented plotting routines. These routines keep state information and permit the user to easily control the properties of the plots. A snapshot of such code is illustrated below. `

`Note: The code for the` _`PlotController`_ `object exists in GroovyLab, therefore we can execute directly the testing code that follows. It is presented for illustration purposes.`

# `Design of a Plot Facilitator Object (this code exists in GroovyLab source)` #
```
   package groovySci.math.plot;
import java.awt.Color;

// a class to plot in an object-oriented way
public class PlotController  {

// these routines affect AbstractDrawer properties and can be used to change the defaults 
// for external plotting routines that do not specify their own values
       // set drawing to continuous lines
    void  setContinuousLine()  { groovySci.math.plot.render.AbstractDrawer.line_type = groovySci.math.plot.render.AbstractDrawer.CONTINOUS_LINE; }
       // set drawing to dotted lines
    void  setDottedLine()  { groovySci.math.plot.render.AbstractDrawer.line_type = groovySci.math.plot.render.AbstractDrawer.DOTTED_LINE; }
       // set drawing to Round Dot
    void  setRoundDot()  { groovySci.math.plot.render.AbstractDrawer.dot_type = groovySci.math.plot.render.AbstractDrawer.ROUND_DOT; }
       // set drawing to cross dot
    void  setCrossDot()  { groovySci.math.plot.render.AbstractDrawer.dot_type = groovySci.math.plot.render.AbstractDrawer.CROSS_DOT; }
       // set font
    void  setFont(java.awt.Font font)  { groovySci.math.plot.PlotGlobals.defaultAbstractDrawerFont = font; } 
       // set Color
    void  setColor(java.awt.Color  color)   { groovySci.math.plot.PlotGlobals.defaultAbstractDrawerColor = color; } 
       // set line width
    void  setlineWidth(int lw)   { groovySci.math.plot.render.AbstractDrawer.line_width = lw; } 
    
      // references to the data for plot
    double []  xData = new double [1];
    double []  yData = new  double [1];
    double []  zData = new double [1]; 
        
      // adjust the data references for plotting to the actual data   
    void   setX( double [] x )  { xData = x; }  // sets the x-data for plotting
    void   setY( double [] y )  { yData = y; }  // sets the y-data for plotting
    void   setZ( double [] z )   { zData = z;  }  // sets the z-data for plotting
    
    void  setX( groovySci.math.array.Vec  x)  { xData = x.getv(); }  // sets the x-data using Vec 
    void  setY( groovySci.math.array.Vec  y)  { yData = y.getv(); }  // sets the y-data using Vec 
    void  setZ( groovySci.math.array.Vec  z)  { zData = z.getv(); }  // sets the z-data using Vec 
    
    void  setX( groovySci.math.array.Matrix  x)  { xData = x.getv(); }  // sets the x-data using Matrix
    void  setY( groovySci.math.array.Matrix  y)  { yData = y.getv(); }  // sets the y-data using Matrix
    void  setZ( groovySci.math.array.Matrix  z)  { zData = z.getv(); }  // sets the z-data using Matrix 
    
    String  xlabelStr = "X-axis";
    void  setxlabel( String  xl )  {  xlabelStr = xl; }
    
    String   ylabelStr = "Y-axis";
    void  setylabel( String yl )  { ylabelStr = yl; }
    
    String  zlabelStr = "Z-axis";
    void  setzlabel( String zl )  {  zlabelStr = zl; }
    
    String   plotTitle2D = "2-D Plot";
    void   setplotTitle2D(String  plTitle)  { plotTitle2D = plTitle; }
    String   plotTitle3D = "3-D Plot";
    void   setplotTitle3D(String  plTitle)  { plotTitle3D = plTitle; }
   
       
    // perform the plot using the object's properties
    void mkplot()  { 
        groovySci.math.plot.plot.plot(xData, yData, groovySci.math.plot.PlotGlobals.defaultAbstractDrawerColor, plotTitle2D);
        groovySci.math.plot.plot.xlabel(xlabelStr);
        groovySci.math.plot.plot.ylabel(ylabelStr);
        
       }
 
 // perform the plot using the object's properties
    void  mkplot3D()  { 
        
        groovySci.math.plot.plot.plot(xData, yData, zData, groovySci.math.plot.PlotGlobals.defaultAbstractDrawerColor,  plotTitle3D);
        groovySci.math.plot.plot.xlabel(xlabelStr);
        groovySci.math.plot.plot.ylabel(ylabelStr);
        groovySci.math.plot.plot.zlabel(zlabelStr);
       }
 }
    


```

## Testing the Plot Controller Object ##

`We can test the plot controller object as: `

```

   
   
    // Illustrates how to  construct a PlotController object to facilitate the plotting
     po = new PlotController()
    
    // construct some signals
     x = inc(0, 1, 100)
     y1 = sin(0.45*x)
     y2 = sin(0.778*x)+0.2*cos(3.4*x)
     y3 = cos(y1+y2)
        
    // construct the first plot object
    closeAll()  // close any previous figure
    po.setX(x)  
    po.setY(y1)
    po.setColor(Color.GREEN)  // use GREEN color for plotting
    po.setplotTitle2D(" Demonstrating 2-D plots")
    po.setDottedLine()
    po.mkplot()   // plot the first signal
    
    po.setContinuousLine()   // set now to continous line plotting
    po.setColor(Color.BLUE)
    po.setlineWidth(5)   // set to thicker width
    // redefine the new signals for plotting
    hold(true) 
    po.setY(y1)   // we change only the Y signal

    figure(); po.mkplot()
    
    z = cos(4.5*x)
    po.setZ(z)
    
    po.setColor(Color.BLUE)  // change the plot color
    po.setlineWidth(1)  // set the line width to 1
    po.mkplot3D()
 

   

```