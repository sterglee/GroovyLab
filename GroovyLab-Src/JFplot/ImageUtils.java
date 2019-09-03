package JFplot;

import javax.swing.*;
import java.awt.*;
import java.util.*;


import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.io.IOException;
import java.io.*;

/**
* Various utilities for handling images.  OK, right now just one utility, 
* save component as JPEG.  Easy enough to do GIF or PNG also.  <br><br>
* 
* Supported Image types (Java 1.6 OS X): <br><br>
* [jpg, BMP, bmp, JPG, jpeg, wbmp, png, JPEG, PNG, WBMP, GIF, gif]
* 
*
*/ 
public class ImageUtils{
   
  public static void savePanelAsJPEG(JPanel p,String filename) throws IOException{
    savePanel(p,"jpeg",filename);
  } 
  
  public static void savePanelAsPNG(JPanel p,String filename) throws IOException{
    savePanel(p,"png",filename);
  } 
  
  public static void savePanelAsGIF(JPanel p,String filename) throws IOException{
    savePanel(p,"gif",filename);
  } 
     
  /**
  * I think it's more expensive to layout 1000 charts than it is to layout 1000 
  * already rendered (fixed) images. 
  */ 
  public static Image getPanelImage(JPanel p){
    JFrame frame;
    frame = new JFrame();
    frame.setContentPane(p);
    frame.pack();

    Dimension size = p.getPreferredSize();
    BufferedImage image = new BufferedImage((int)size.width,(int)size.height, 
                                             BufferedImage.TYPE_INT_RGB);
    p.paint(image.createGraphics());
  
    return(image);
  }   
     
     
  public static void savePanel(JPanel p, String format, String filename) throws IOException {
    JFrame frame;
    frame = new JFrame();
    frame.setContentPane(p);
    frame.pack();

    Dimension size = p.getPreferredSize();
    BufferedImage image = new BufferedImage((int)size.width,(int)size.height, 
                                             BufferedImage.TYPE_INT_RGB);
    p.paint(image.createGraphics());

    ImageIO.write(image,format, new File (filename));
     
    frame.dispose();
  }    
}
