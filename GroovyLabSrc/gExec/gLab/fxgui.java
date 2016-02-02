
package gExec.gLab;


import java.awt.ScrollPane;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
 
public class fxgui extends Application {
 
    public static void main(String[] args) {
        launch(args);
    }
 
    @Override
    public void start(Stage stage) {
        stage.setTitle("GroovyLab Editor");
 
        
        Scene scene = new Scene(new VBox(), 400, 350);
        scene.setFill(Color.OLDLACE);
 
        
        MenuBar menuBar = new MenuBar();
 
        Menu recentPaneFilesMenu = new Menu("Recent Files");
 
        Menu codeBufferingMenu = new Menu("Code Buffering");
 
        MenuItem  codeBufferingMenuItem = new MenuItem("Buffer selected class(es) code");
        codeBufferingMenu.getItems().add(codeBufferingMenuItem);
        // --- Menu View
        Menu menuView = new Menu("Switch Libraries");
        MenuItem switchJBLASMenuItem = new MenuItem("Switch To JBLAS");

        Menu importsMenu = new Menu("Imports");
        MenuItem bufferImportsMenuItem = new MenuItem("Buffer Selected Imports");
        MenuItem clearBufferedImportsMenuItem = new MenuItem("Clear buffered Imports");
        
        importsMenu.getItems().addAll(bufferImportsMenuItem, clearBufferedImportsMenuItem);
        
        
        menuBar.getMenus().addAll(recentPaneFilesMenu, codeBufferingMenu, menuView, importsMenu);
 
 
        
        //ScrollPane scrpane = new ScrollPane();
        //scrpane.add(gExec.Interpreter.GlobalValues.globalEditorPane);
         
     
        ((VBox) scene.getRoot()).getChildren().addAll(menuBar);
        //gExec.Interpreter.GlobalValues.globalEditorPane);
 
        stage.setScene(scene);
        stage.show();
    }
 
 
}
