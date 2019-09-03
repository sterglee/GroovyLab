
package gExec.gLab;


import gExec.Interpreter.GlobalValues;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.Method;
import java.util.jar.*;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Segment;



public class ProcessJavaInJar   extends JFrame {
   private String JarName;

   // used for invoking the main method of a Java class
   static  Class [] formals = { String [].class };
   static Object [] actuals = { new String [] {""}};

   public static final int DEFAULT_WIDTH = 800;
   public static final int DEFAULT_HEIGHT = 500;  

   private JComboBox fileCombo;
   private JButton saveButton;
   private JButton compileAndRunButton;
   private JTextArea fileText;
       
/**
   A frame with a text area to show the contents of a file inside
   a Jar archive, a combo box to select different files in the
   archive, and a menu to load a new archive.
*/

   public ProcessJavaInJar(String jarFile)
   {
           setTitle("Java Examples -- Copy & Paste and run with F9");
      setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

      setVisible(true);
      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      
     
      // add the text area and combo box
      fileText = new JTextArea();
      fileText.setFont(GlobalValues.defaultTextFont);
      fileCombo = new JComboBox();
      fileCombo.addActionListener(new
         ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
                loadJarFile((String) fileCombo.getSelectedItem());
            }
         });

      JPanel bottomPanel = new JPanel(new BorderLayout());
      JLabel examplesLabel = new JLabel();
      examplesLabel.setFont(new Font("Times New Roman", Font.BOLD, 16));
      examplesLabel.setText("Selected Java example: ");
      bottomPanel.add(fileCombo, BorderLayout.EAST);
      bottomPanel.add(examplesLabel, BorderLayout.CENTER);
      add(bottomPanel, BorderLayout.SOUTH);
               
      add(new JScrollPane(fileText), BorderLayout.CENTER);
   
     JarName = jarFile;
     scanJarFile();
   }

      
   /**
      Scans the contents of the Jar archive and populates
      the combo box.
   */
   public void scanJarFile()
   {  
      fileCombo.removeAllItems();
      try
      {  
         JarInputStream zin = new JarInputStream(new FileInputStream(JarName));
         JarEntry entry;
         while ((entry = zin.getNextJarEntry()) != null)
         {  
            String nameOfEntry = entry.getName();
            if (nameOfEntry.endsWith(".javaVISAD") || nameOfEntry.endsWith(".javaSGT") ) 
            {
                fileCombo.addItem(nameOfEntry);
             }
            
            zin.closeEntry();
         }
         zin.close();
      }
      catch (IOException e)
      {  
         e.printStackTrace(); 
      }
   }

   /**
      Loads a file from the Jar archive into the text area
      @param name the name of the file in the archive
   */
   public void loadJarFile(String name)
   {  
      try
      {  
         JarInputStream zin = new JarInputStream(new FileInputStream(JarName));
         JarEntry entry;
         fileText.setText("");

         // find entry with matching name in archive
         while ((entry = zin.getNextJarEntry()) != null)
         {  
            if (entry.getName().equals(name))
            {  
               // read entry into text area
               BufferedReader in = new BufferedReader(new InputStreamReader(zin));
               String line;
               while ((line = in.readLine()) != null)
               {
                  fileText.append(line);
                  fileText.append("\n");
               }
            }
            zin.closeEntry();
         }
         zin.close();
      }
      catch (IOException e)
      {  
         e.printStackTrace(); 
      }
   }

/**
   This file filter matches all files with a given set of 
   extensions. From FileChooserTest in chapter 9
*/
class ExtensionFileFilter extends FileFilter
{
   /**
      Adds an extension that this file filter recognizes.
      @param extension a file extension (such as ".txt" or "txt")
   */
   public void addExtension(String extension)
   {
      if (!extension.startsWith("."))
         extension = "." + extension;
      extensions.add(extension.toLowerCase());     
   }

   /**
      Sets a description for the file set that this file filter
      recognizes.
      @param aDescription a description for the file set
   */
   public void setDescription(String aDescription)
   {
      description = aDescription;
   }

   /**
      Returns a description for the file set that this file
      filter recognizes.
      @return a description for the file set
   */
   public String getDescription()
   {
      return description; 
   }

   public boolean accept(File f)
   {
      if (f.isDirectory()) return true;
      String name = f.getName().toLowerCase();

      // check if the file name ends with any of the extensions
      for (String e : extensions)
         if (name.endsWith(e))
            return true;
      return false;
   }
   
   private String description = "";
   private ArrayList<String> extensions = new ArrayList<String>();
}

 /**
     * Thread to save a document to file
     */
    class FileSaver extends Thread {
        Document doc;
        File f;

	FileSaver(File f, Document doc) {
	    setPriority(4);
	    this.f = f;
	    this.doc = doc;
	}

        @Override
        public void run() {
	    try {
		// initialize the statusbar
		JProgressBar progress = new JProgressBar();
		progress.setMinimum(0);
		progress.setMaximum((int) doc.getLength());
		
		// start writing
		Writer out = new FileWriter(f);
                Segment text = new Segment();
                text.setPartialReturn(true);
                int charsLeft = doc.getLength();
		int offset = 0;
                while (charsLeft > 0) {
                    doc.getText(offset, Math.min(4096, charsLeft), text);
                    out.write(text.array, text.offset, text.count);
                    charsLeft -= text.count;
                    offset += text.count;
                    progress.setValue(offset);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                out.flush();
                out.close();
	    }
	    catch (IOException e) {
                final String msg = e.getMessage();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        JOptionPane.showMessageDialog(null,
                                "Could not save file: " + msg,
                                "Error saving file",
                                JOptionPane.ERROR_MESSAGE);
	    }
                });
	    }
	    catch (BadLocationException e) {
		System.err.println(e.getMessage());
	    }
          
	}
    }
    
 
}
