
package groovySci.math.plot.JavaFX;


import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart; 
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color; 
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class javaFXPlotLine  {
    static double [] pointsX;     // the points on X axis
    static double [] pointsY;    // the points on Y axis
    static String  plotName = "Plot of values array";    // the  default plot name
    static String plotTitle = "JavaFX chart line plot";   // the default plot title
    static int xloc = 200; // default location
    static int yloc = 200; 
    static int xsize = 600;  // default size
    static int  ysize = 500;
    
    // this routine is called from the EDT of Swing
    private static void initAndShowPlot() {  
        JFrame frame = new JFrame(plotTitle);
        final JFXPanel fxPanel = new JFXPanel();  // JavaFX component
        frame.add(fxPanel);
        frame.setLocation(xloc, yloc);
        frame.setSize(xsize, ysize);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        Platform.runLater(new Runnable() {   // fill fxPanel at the context of JavaFX  Application thread
        @Override
         public void run() {
            initFX(fxPanel);
        }
    });
    }
    
    private static void initFX(JFXPanel fxPanel)  {
        Scene scene = createScene();
        fxPanel.setScene(scene);
    }

    // prepares the plot and places it in a scene
    private static Scene createScene() {
        
        javafx.scene.chart.NumberAxis  myaxis1 = new javafx.scene.chart.NumberAxis();
        javafx.scene.chart.NumberAxis  myaxis2 = new javafx.scene.chart.NumberAxis();

        LineChart  lc = new LineChart(myaxis1, myaxis2);
        XYChart.Series<Number,Number> series = new XYChart.Series<Number,Number>();
        series.setName(plotName);
        lc.setTitle(plotTitle);
        int N = pointsX.length;
        for (int k=0; k<N; k++) 
        series.getData().add(new XYChart.Data<Number,Number>(pointsX[k], pointsY[k]));

        lc.getData().add(series);
        
        BorderPane pane =  new BorderPane();
       
        pane.setCenter(lc);
        
        Scene myScene = new Scene(pane, Color.ALICEBLUE); 
        
        return myScene;
    }

/*
    e.g.
var xx = vrand(900)

scalaSci.math.plot.JavaFX.javaFXPlotLine.plot(xx)
    */    
    public static void lineplotfx(double [] values) {
        int L = values.length;
        pointsX = new double[L];
        pointsY = values;
        for (int l = 0; l<L;  l++)
            pointsX[l] =l;
        
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
              initAndShowPlot();
            }
        });
    }
    
    public static void lineplotfx(double [] valuesX, double [] valuesY)  {
        pointsX = valuesX;
        pointsY = valuesY;
        
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
              initAndShowPlot();
            }
        });
    }
    
      public static void setlinePlotTitlefx(String title) {
          plotTitle = title;
      }
      
      public static void setlinePlotNamefx(String name) {
          plotName = name;
      }
      
}    

