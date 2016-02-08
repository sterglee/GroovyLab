
package groovySci.help;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

public class JPEGHelpFrame  extends JFrame {
  BufferedImage [] images;
  
      
        
      public JPEGHelpFrame(File jpegFile) {
                    
          
          Box  box = Box.createVerticalBox();
          try {
       String suffix = "jpg";
       Iterator<ImageReader> iter  =  ImageIO.getImageReadersBySuffix(suffix);
       ImageReader reader = iter.next();
       ImageInputStream imageIn = ImageIO.createImageInputStream(jpegFile);
       reader.setInput(imageIn);
       int count = reader.getNumImages(true);
       images = new BufferedImage[count];
       for (int i = 0; i < count; i++) {
           images[i] = reader.read(i);
           box.add(new JLabel(new ImageIcon(images[i])));
         }
       }
          catch (IOException e) {
              JOptionPane.showMessageDialog(this, e);
          }
          
       setContentPane(new JScrollPane(box));
       validate();
       setSize(500,500);
       setVisible(true);
         
         
      }      

      
        
          

}