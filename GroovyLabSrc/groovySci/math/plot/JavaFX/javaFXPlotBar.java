
package groovySci.math.plot.JavaFX;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.layout.BorderPane; 
import javafx.scene.paint.Color; 
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javafx.scene.chart.*;
import javafx.scene.chart.XYChart.Series;

public class javaFXPlotBar   {
    static String [] labelsOfItems;     // the String labels of items
    static String [] namesOfAttributes;
    static double [][] valuesOfItemsForAllAttributes;
    static Series <String, Double> chartSeries;
    static double [] valuesOfItems;    // the values of items
    static String  plotName = "BarChart example";    // the plot name
    static String plotTitle = "JavaFX pie chart";
    static int xloc = 200;
    static int yloc = 200; 
    static int xsize = 600;
    static int  ysize = 500;

    // prepares the data for the BarChart
    private  static ObservableList<XYChart.Series<String, Double>> getChartData() {
        ObservableList<XYChart.Series<String, Double>>  answer = FXCollections.observableArrayList();
        int Nitems = labelsOfItems.length;
        int AttributesCnt = namesOfAttributes.length;
        for (int itemCnt = 0; itemCnt < Nitems;  itemCnt++)   {
            chartSeries = new Series <> ();
            chartSeries.setName(labelsOfItems[itemCnt]);
           for (int attributeCnt=0;  attributeCnt < AttributesCnt; attributeCnt++) {
               chartSeries.getData().add(new XYChart.Data(namesOfAttributes[attributeCnt],  valuesOfItemsForAllAttributes[itemCnt][attributeCnt]));
        
           }
           answer.add(chartSeries);
           }
        return answer;
    }

    // prepares the JavaFX plot in a Swing container
    private static void initAndShowPlot() {
        JFrame frame = new JFrame(plotTitle);
        final JFXPanel fxPanel = new JFXPanel();
        frame.add(fxPanel);
        frame.setLocation(xloc, yloc);
        frame.setSize(xsize, ysize);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        Platform.runLater(new Runnable() {
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

    private static Scene createScene() {
        
        javafx.scene.chart.CategoryAxis  attributes  = new javafx.scene.chart.CategoryAxis();
        javafx.scene.chart.NumberAxis  numbers = new javafx.scene.chart.NumberAxis();

        BarChart  barChart = new BarChart(attributes, numbers);
        barChart.setData(getChartData());
        
        BorderPane pane =  new BorderPane();
       
        pane.setCenter(barChart);
        
        Scene myScene = new Scene(pane, Color.ALICEBLUE); 
        
        return myScene;
    }

    public static void barChartfx() {
           SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
              initAndShowPlot();
            }
        });
    }
    
     public static void setbarChartLabelsOfItems(String [] labelsOfItemsParam) {
         int  Nitems = labelsOfItemsParam.length;
         labelsOfItems = new String[Nitems];
         for (int k=0; k<Nitems; k++)
             labelsOfItems[k] = labelsOfItemsParam[k];
     }
     
     public static void setbarChartAttributeNames (String [] attributeNamesParam) {
         int  Nattributes  = attributeNamesParam.length;
         namesOfAttributes   = new String[Nattributes];
         for (int k=0; k<Nattributes; k++)
             namesOfAttributes[k] = attributeNamesParam[k];
     }
     
     public static void setbarChartAttributeValues(double [][] attributeValuesParam) {
         int  Nitems = attributeValuesParam.length;
         int Nattributes = attributeValuesParam[0].length;
         valuesOfItemsForAllAttributes = new double[Nitems][Nattributes];         
         for (int item = 0; item < Nitems; item++)
             for (int attr = 0; attr < Nattributes; attr++)
                 valuesOfItemsForAllAttributes[item][attr] = attributeValuesParam[item][attr];
         
     }
     
      public static void setbarChartTitlefx(String title) {
          plotTitle = title;
      }      
      public static void setbarChartNamefx(String name) {
          plotName = name;
      }
      
}    

