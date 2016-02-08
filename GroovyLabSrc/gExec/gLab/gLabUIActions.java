
package gExec.gLab;

import groovy.lang.GroovyShell;
import gExec.ClassLoaders.ExtensionClassLoader;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

import gExec.gui.*;
import gExec.Interpreter.GlobalValues;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;

  
                 
           
           
       class paneFontAdjusterAction extends AbstractAction  {
        public void actionPerformed(ActionEvent e) {
 JFontChooser  myFontChooser = new JFontChooser(GlobalValues.gLabMainFrame);
 myFontChooser.setVisible(true);
 Font choosingFont = myFontChooser.getFont();
 if (choosingFont != null)
   GlobalValues.globalEditorPane.setFont(choosingFont);
        }
       }
         
   
   
       class FontAdjusterAction extends AbstractAction  {
        public void actionPerformed(ActionEvent e) {
 JFontChooser  myFontChooser = new JFontChooser(GlobalValues.gLabMainFrame);
 myFontChooser.setVisible(true);
 GlobalValues.jLabConsole.setFont(myFontChooser.getFont());
 
 GlobalValues.jLabConsole.setForeground(myFontChooser.getForegroundColor());
        }
       }

   class UIFontAdjusterAction extends AbstractAction  {
        public void actionPerformed(ActionEvent e) {
 JFontChooser  myFontChooser = new JFontChooser(GlobalValues.gLabMainFrame);
 myFontChooser.setVisible(true);
 GlobalValues.uifont = myFontChooser.getFont();
         }
       }

// popup font adjuster
 class pUIFontAdjusterAction extends AbstractAction  {
        public void actionPerformed(ActionEvent e) {
 JFontChooser  myFontChooser = new JFontChooser(GlobalValues.gLabMainFrame);
 myFontChooser.setVisible(true);
 GlobalValues.puifont = myFontChooser.getFont();
            }
       }



// GUI font adjuster
 class gUIFontAdjusterAction extends AbstractAction  {
        public void actionPerformed(ActionEvent e) {
 JFontChooser  myFontChooser = new JFontChooser(GlobalValues.gLabMainFrame);
 myFontChooser.setVisible(true);
 GlobalValues.guifont = myFontChooser.getFont();
            }
       }

// HTML fonts 
 class htmlFontAdjusterAction extends AbstractAction  {
        public void actionPerformed(ActionEvent e) {
 JFontChooser  myFontChooser = new JFontChooser(GlobalValues.gLabMainFrame);
 myFontChooser.setVisible(true);
 GlobalValues.htmlfont = myFontChooser.getFont();
            }
       }

// buttons font adjuster
 class bUIFontAdjusterAction extends AbstractAction  {
        public void actionPerformed(ActionEvent e) {
 JFontChooser  myFontChooser = new JFontChooser(GlobalValues.gLabMainFrame);
 myFontChooser.setVisible(true);
 GlobalValues.buifont = myFontChooser.getFont();
            }
       }

// adjust the fonts of the output Console
   class outConsFontAdjusterAction extends AbstractAction  {
        public void actionPerformed(ActionEvent e) {
 JFontChooser  myFontChooser = new JFontChooser(GlobalValues.gLabMainFrame);
 myFontChooser.setVisible(true);
 Font choosingFont = myFontChooser.getFont();
 GlobalValues.consoleOutputWindow.output.setFont(choosingFont);
         }
       }

 
       class controlMainToolBarAction extends AbstractAction {
           controlMainToolBarAction() {
               super("Show/Hide Main Toolbar");
           }
                    public void actionPerformed(ActionEvent e) {
                        GlobalValues.mainToolbarVisible = !GlobalValues.mainToolbarVisible;
                        
                        GlobalValues.toolbarFrame.setVisible(GlobalValues.mainToolbarVisible);
                    }
                }
                
       class LookAndFeelAdjusterAction extends AbstractAction {
           public void actionPerformed(ActionEvent e) {
               JFrame lookAndFeelFrame = new JFrame("Configure Look and Feel ");
               JButton nativeLookAndFeel = new JButton("Native Look and Feel");
               nativeLookAndFeel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
           try {
            String sysLookAndFeel = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(sysLookAndFeel);
            GlobalValues.nativeLookAndFeel = true;
            gExec.gLab.gLab.updateTree();
            JOptionPane.showMessageDialog(null,  "Native look and feel will be the default for the next session");
	} catch (Exception exc) {
	    System.err.println("Error loading L&F: " + exc);
	}
      }
        });
        
        JPanel lookAndFeelConfigPanel = new JPanel();
        
          JButton crossPlatformLookAndFeel = new JButton("Cross Platform Look and Feel");
               crossPlatformLookAndFeel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
           try {
            String  crossPlatformLookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
            UIManager.setLookAndFeel(crossPlatformLookAndFeel);
            GlobalValues.nativeLookAndFeel = false;
            gExec.gLab.gLab.updateTree();
            JOptionPane.showMessageDialog(null,  "Cross-platform look and feel will be the default for the next session");
	} catch (Exception exc) {
	    System.err.println("Error loading L&F: " + exc);
	}
      }
        });

        lookAndFeelConfigPanel.add(nativeLookAndFeel);
        lookAndFeelConfigPanel.add(crossPlatformLookAndFeel);
        lookAndFeelFrame.add(lookAndFeelConfigPanel);
        lookAndFeelFrame.setLocation(GlobalValues.gLabMainFrame.getLocation().x, GlobalValues.gLabMainFrame.getHeight()/2);
        lookAndFeelFrame.pack();
        lookAndFeelFrame.setVisible(true);
           }
       }
       

   class browseFileSysForPaths extends AbstractAction {

    public browseFileSysForPaths() {
      super("Browse File System For Updating Class Paths");
    }
       
    @Override
       public void actionPerformed(ActionEvent e) {
                           SwingUtilities.invokeLater(new Runnable() {
            @Override
           public void run() {  // run in  */
     String initialSelectionDir = "/";
     if (GlobalValues.hostIsUnix==false)
         initialSelectionDir = "c:\\";
     
     initialSelectionDir =    JOptionPane.showInputDialog("Specify the root of the file system to browse", initialSelectionDir);
     
                try {
                FileTreeExplorer ftree = new FileTreeExplorer(initialSelectionDir);
                GlobalValues.currentFileExplorer = ftree;
                JFrame treeFrame = new JFrame("Select directory");
                treeFrame.add(new JScrollPane(ftree.pathsTree));
                treeFrame.setSize(600, 500);
                treeFrame.setVisible(true);
                }
                catch (FileNotFoundException fnfexce) { 
                    System.out.println("File not found exception in FileTreeExplorer");
                    fnfexce.printStackTrace();
                } 
    }
                           });
    
    }
   }
   

            class browseGroovySciFilesAction extends AbstractAction {
       browseGroovySciFilesAction()  {super("Browse File System for specifying new GroovySciClassPath component"); }
       
    @Override
       public void actionPerformed(ActionEvent e) {
                           SwingUtilities.invokeLater(new Runnable() {
            @Override
           public void run() {  // run in  */
                     
    JFileChooser  chooser = new JFileChooser(GlobalValues.workingDir);
    chooser.setDialogTitle("Browse file system for specifying new GroovySciClassPath component");
    int retVal = chooser.showOpenDialog(GlobalValues.gLabMainFrame);
    
      if (retVal == JFileChooser.APPROVE_OPTION) { 
      File selectedFile = chooser.getSelectedFile();
      String SelectedFileWithPath = selectedFile.getAbsolutePath();
      String SelectedFilePathOnly = SelectedFileWithPath.substring(0, SelectedFileWithPath.lastIndexOf(File.separatorChar)+1);
        
      if (GlobalValues.GroovySciClassPath.indexOf(SelectedFilePathOnly)==-1)        
               {    // path not already exist 
          GlobalValues.GroovySciClassPathComponents.add(SelectedFilePathOnly);
              // IMPORTANT!! : update Groovy's Shell class path
            CompilerConfiguration cf = new CompilerConfiguration();
         
        if (GlobalValues.CompileDecimalsToDoubles)  {    // convert BigDecimalsToDoubles for efficiency
                cf.addCompilationCustomizers(new CompilationCustomizer(CompilePhase.CONVERSION) {
                
                    @Override
                    public void call(final SourceUnit source, final  GeneratorContext context, final ClassNode classNode)   {
                        new expandRunTime.ConstantTransformer(source).visitClass(classNode);
                    }
                });
        }
        
            LinkedList <String> pathsList = new LinkedList<String>();
            pathsList.add("." );   // current directory
            for (int k=0; k<GlobalValues.jartoolboxesForGroovySci.size(); k++)
               pathsList.add((String)GlobalValues.jartoolboxesForGroovySci.get(k));
         
         // append now the GroovySciClassPath components
            for (int k=0; k<GlobalValues.GroovySciClassPathComponents.size(); k++)
                pathsList.add((String) GlobalValues.GroovySciClassPathComponents.elementAt(k));
         
            pathsList.add(".");
            cf.setClasspathList(pathsList);
      
        StringBuilder fileStr = new StringBuilder();
      Enumeration enumDirs = GlobalValues.GroovySciClassPathComponents.elements();
      while (enumDirs.hasMoreElements())  {
         Object ce = enumDirs.nextElement();
         fileStr.append(File.pathSeparator+(String)ce);
    }
      GlobalValues.GroovySciClassPath = fileStr.toString();
      GlobalValues.settings.setProperty("GroovySciClassPath", fileStr.toString());
                        gExec.gLab.gLab.updateTree();
                        gExec.gLab.gLab.outerPane.revalidate();
    final ClassLoader parentClassLoader = GlobalValues.gLabMainFrame.getClass().getClassLoader();
            GlobalValues.GroovyShell  = new GroovyShell(parentClassLoader,  GlobalValues.groovyBinding, cf);
            GlobalValues.GroovyShell = null;   // recreate GroovyShell with the updated path
    
     }  
    }  // path not already exist
                }
                     
            
              });
                
                    }
   }
                
  

 class configAlphaAction extends AbstractAction {
     configAlphaAction() { super("Configuring alpha - transparency parameter"); }
     public void actionPerformed(ActionEvent e) {
                 
  SwingUtilities.invokeLater(new Runnable() {
public void run() {  // run in  */
      
String newAlpha = JOptionPane.showInputDialog("Alpha Parameter",  GlobalValues.alphaComposite);
GlobalValues.alphaComposite = Float.valueOf(newAlpha);
}
});
                 }
      
        
     }
 

 class promptConfigAction extends AbstractAction { 
     promptConfigAction()   {super("Switches Display/Hide working directory at prompt. Current displaying directory  is:  "+GlobalValues.displayDirectory); }
       public void actionPerformed(ActionEvent e) {
                           SwingUtilities.invokeLater(new Runnable() {
            @Override
           public void run() {  // run in  */
      GlobalValues.displayDirectory = !GlobalValues.displayDirectory;
      GlobalValues.gLabMainFrame.getPromptJMenuItem().setText("Switches Display/Hide working directory at prompt. Current displaying directory  is:  "+GlobalValues.displayDirectory); 
             }
                           }
                           );
                
                    }
   }
 
 
class browseJavaClassesAction extends AbstractAction {
       browseJavaClassesAction()  {super("Browse file system for specifyng new Java ClassPath component"); }
       
    @Override
       public void actionPerformed(ActionEvent e) {
                           SwingUtilities.invokeLater(new Runnable() {
           public void run() {  // run in  */
      
    JFileChooser  chooser = new JFileChooser(GlobalValues.workingDir);
    chooser.setDialogTitle("Browse file system for specifyng new Java ClassPath component");
    int retVal = chooser.showOpenDialog(GlobalValues.gLabMainFrame);
    if (retVal == JFileChooser.APPROVE_OPTION) { 
      File selectedFile = chooser.getSelectedFile();
      String SelectedFileWithPath = selectedFile.getAbsolutePath();
      String SelectedFilePathOnly = SelectedFileWithPath.substring(0, SelectedFileWithPath.lastIndexOf(File.separatorChar));
      if (GlobalValues.gLabClassPath.indexOf(SelectedFilePathOnly)==-1)  {
        GlobalValues.gLabClassPath = GlobalValues.gLabClassPath+File.pathSeparator+SelectedFilePathOnly;
        }
      
      ClassLoader parentClassLoader = getClass().getClassLoader();
      GlobalValues.extensionClassLoader = new  ExtensionClassLoader(GlobalValues.gLabClassPath, parentClassLoader);
      
    
                    gExec.gLab.gLab.updateTree();
                    gExec.gLab.gLab.outerPane.revalidate();
    
                }
                     }
              });
                
    }
}
    
   class commandHistoryAction extends AbstractAction {
       commandHistoryAction()  {super("Displays the command history"); }
       
        @Override
                     public void actionPerformed(ActionEvent e) {
                           SwingUtilities.invokeLater(new Runnable() {
                            @Override
     public void run() {  // run in  EDT context */
                gExec.gLab.commandHistory.loadCommandHistory(GlobalValues.GroovyLabCommandHistoryFile, GlobalValues.userConsole.previousCommands);  
               final JList  historyList = new JList(GlobalValues.userConsole.previousCommands);
               JPanel auxPanel = new JPanel();
               auxPanel.add(historyList);
               JScrollPane auxPane = new JScrollPane(auxPanel);
               JFrame  auxFrame = new JFrame("Command History");
               auxFrame.add(auxPane);
               auxFrame.setSize(300, 300);
               auxFrame.setLocation(100, 100);
               auxFrame.setVisible(true);
               
                historyList.addListSelectionListener(new        ListSelectionListener()
         {
            public void valueChanged(ListSelectionEvent event)
            {  
               Object[] values = historyList.getSelectedValues();

               StringBuilder text = new StringBuilder();
               for (int i = 0; i < values.length; i++)
               {  
                  String word = (String) values[i];
                  text.append(word);
                  text.append(" ");
               }
               
               // replace current command with next command
    	String textArea = GlobalValues.userConsole.getText();
    	int    pos1     = textArea.lastIndexOf("# ") + 2;
    	GlobalValues.userConsole.setText(textArea.substring(0,pos1)+text);
    	
    	// set cursor at the end of the text area
        GlobalValues.userConsole.setCaretPosition(GlobalValues.userConsole.getText().length());
            }
         });  // new ListSelectionListener

         
                            }  // run in EDT context
                          });  // new Runable()
                     }  // actionPerformed
                 }



  




   
              
              
       
 
             
    
       
