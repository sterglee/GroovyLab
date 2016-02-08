
package gExec.gLab;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import java.io.*;
import gExec.Interpreter.GlobalValues;
import gExec.gui.GBC;

// the gLab Explorer acts as a file system manager for the file system components
import java.net.URL;

// handles operations on the gLab paths, i.e.  the jLabClassPath for Java classes and sources
import javax.script.ScriptContext;
// The gLab Explorer allows compiling and running Java classes, running  
// editing files and creating new files and directories

public    class  gLabExplorer extends JPanel   {
       JPanel   ScriptsClassesPanel = new JPanel(new GridBagLayout());
       JPanel   TreePanel = new JPanel(new BorderLayout());
       JComboBox  GroovyScriptClassesCB;
       public static JComboBox  favouritePathsCB;
       JCheckBox removeFavourite;
       Vector groovyClassesPaths = new Vector();

       JTextField  specifyPathsField;
       
       private class MouseCBAdapterForGroovySci  extends  MouseAdapter {
          public void mousePressed(MouseEvent event) {
              if ((event.getModifiersEx() & event.BUTTON3_DOWN_MASK) != 0)    // right mouse click
                  new browseGroovySciFilesAction().actionPerformed(null);
          }
       }

       
       private class MouseCBAdapterForJava   extends  MouseAdapter {
          public void mousePressed(MouseEvent event) {
              if ((event.getModifiersEx() & event.BUTTON3_DOWN_MASK) != 0)    // right mouse click
                  new browseJavaClassesAction().actionPerformed(null);
          }
       }
       
          
    public gLabExplorer() {
      super();
                
    }

       public  void updatePaths() {
          String pathSelected="";
           try {
         pathSelected = GlobalValues.selectedExplorerPath;
         FileTreeExplorer  currentJPathTree = new FileTreeExplorer(pathSelected);
         GlobalValues.currentFileExplorer = currentJPathTree;
         TreePanel.removeAll();
         JScrollPane   scriptsPane = new JScrollPane(currentJPathTree.pathsTree);
         TreePanel.add(scriptsPane);
         TreePanel.revalidate();
    }
    catch (FileNotFoundException ex) {
        System.out.println("Error exploring "+pathSelected); 
        ex.printStackTrace();
      }
       }
       
               public void buildClassScriptPathsTree()  {
       // the ScriptsClassesPanel has components that enable the user to navigate to the
      // Java classes and Groovy classes and scripts filesystems   
    Font smallFont = new Font("Times New Roman", Font.PLAIN, 10);                   
    ScriptsClassesPanel.removeAll();
    ScriptsClassesPanel.setLayout(new GridBagLayout());
    int numOfDirsOfGroovySciPath = GlobalValues.GroovySciClassPathComponents.size();
    
            groovyClassesPaths.clear();
            groovyClassesPaths.add("GroovySci Script/Classes Directories");
               for (int k=0; k<numOfDirsOfGroovySciPath; k++) {
        String currentDirElem = GlobalValues.GroovySciClassPathComponents.elementAt(k).toString();
        groovyClassesPaths.add(currentDirElem);
    }
    GroovyScriptClassesCB  = new JComboBox(groovyClassesPaths);
    GroovyScriptClassesCB.setFont(smallFont);
    
    JLabel groovySciClassPathLabel = new JLabel("GroovySci Scripts/Classes filesystem roots");
    groovySciClassPathLabel.setFont(smallFont);
    groovySciClassPathLabel.setToolTipText("Controls where to search for Groovy scripts. Right Mouse Click to update.");
    groovySciClassPathLabel.addMouseListener(new MouseCBAdapterForGroovySci());
    
    ScriptsClassesPanel.add(groovySciClassPathLabel, 
            new GBC(0,4).setFill(GridBagConstraints.NONE).setAnchor(GridBagConstraints.NORTH));
    
    ScriptsClassesPanel.add( GroovyScriptClassesCB, 
            new GBC(0,5).setFill(GridBagConstraints.NONE).setAnchor(GridBagConstraints.NORTH));
    
    GroovyScriptClassesCB.addMouseListener(new MouseCBAdapterForGroovySci());   // handles right mouse clicks
      
    GroovyScriptClassesCB.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String currentDirElem = (String)GroovyScriptClassesCB.getSelectedItem();
                   File elemFile = new File(currentDirElem);
                        if (elemFile.exists()) {
                    try {    
         FileTreeExplorer  currentJPathTree = new FileTreeExplorer(currentDirElem);
         GlobalValues.currentFileExplorer = currentJPathTree;
         TreePanel.removeAll();
         JScrollPane   classesPane = new JScrollPane(currentJPathTree.pathsTree);
         TreePanel.add(classesPane);
         TreePanel.revalidate();
      }
      catch (FileNotFoundException  ex) { 
          System.out.println("File: "+currentDirElem+" not found in jLabExplorer");
          ex.printStackTrace();
                    }
                  }    // file exists
                }
            });
    
    JLabel  specifyPathLabel = new JLabel("Specify Path");
    specifyPathLabel.setFont(smallFont);
    JPanel browsingPanel = new JPanel();
    browsingPanel.add(specifyPathLabel);
    specifyPathsField = new JTextField(5);
    specifyPathsField.setFont(smallFont);
    if (GlobalValues.hostIsUnix)  specifyPathsField.setText("/");  else specifyPathsField.setText("c:/");
     
    browsingPanel.add(specifyPathsField);
    JButton specifyPathButton = new JButton("Browse");
    specifyPathButton.setFont(smallFont);
    browsingPanel.add(specifyPathButton);
    specifyPathButton.addActionListener(new ActionListener() {

           public void actionPerformed(ActionEvent e) {
              String  pathSelected = specifyPathsField.getText();
    try {
        boolean favoritePathExists = GlobalValues.favouriteElements.contains(pathSelected);
        
        if (favoritePathExists==false)  {
            favouritePathsCB.addItem(pathSelected);
            GlobalValues.favouriteElements.add(pathSelected);
        }
        
           GlobalValues.selectedExplorerPath = pathSelected;
         FileTreeExplorer  currentJPathTree = new FileTreeExplorer(pathSelected);
         GlobalValues.currentFileExplorer = currentJPathTree;
         TreePanel.removeAll();
         JScrollPane   scriptsPane = new JScrollPane(currentJPathTree.pathsTree);
         TreePanel.add(scriptsPane);
         TreePanel.revalidate();
    }
    catch (FileNotFoundException ex) {
        System.out.println("Error exploring "+pathSelected); 
        ex.printStackTrace();
      }
              
            }
        });
    
        ScriptsClassesPanel.add(browsingPanel,
             new GBC(0,6).setFill(GridBagConstraints.NONE).setAnchor(GridBagConstraints.NORTH));
 
    JLabel  favouritePathsLabel = new JLabel("Favourite Paths");
    favouritePathsLabel.setFont(smallFont);
    JPanel favouritePathsPanel = new JPanel(new GridLayout(5,1));
    favouritePathsPanel.add(favouritePathsLabel);
    favouritePathsCB = new JComboBox();
    favouritePathsCB.setFont(smallFont);
    favouritePathsCB.setToolTipText("Displays a list with the favourite paths and allows to browse  them in exploler");
        gExec.gLab.favouritePaths.loadFavouritePaths(GlobalValues.GroovyLabFavoritePathsFile, favouritePathsCB);   // add also the favourite paths from the file
            
    if (GlobalValues.hostIsUnix) {
        if (GlobalValues.favouriteElements.contains("/")==false) {
           favouritePathsCB.addItem("/");
           GlobalValues.favouriteElements.add("/");
        }
    }
    else {
        if (GlobalValues.favouriteElements.contains("c:\\")==false)  {    
         favouritePathsCB.addItem("c:\\");
         GlobalValues.favouriteElements.add("c:\\");
     }
    }
        
        favouritePathsPanel.add(favouritePathsCB);    
    
    JLabel checkboxIsForRemoveLabel = new JLabel("Remove on click: ");
    checkboxIsForRemoveLabel.setFont(smallFont);
    checkboxIsForRemoveLabel.setToolTipText("When selected the path selected from the favourite paths list is removed");
    JButton clearFavouritePathsButton = new JButton("Clear Paths");
    clearFavouritePathsButton.setFont(smallFont);
    clearFavouritePathsButton.setToolTipText("Clears the favourite paths list");
    clearFavouritePathsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
          favouritePathsCB.removeAllItems();
          GlobalValues.favouriteElements.removeAllElements();
          groovyClassesPaths.clear();
          GlobalValues.GroovySciClassPathComponents.clear();
          GroovyScriptClassesCB.removeAllItems();
            }
        });
    removeFavourite = new JCheckBox();
    removeFavourite.setToolTipText("When checked the selected favourite path is removed");
    favouritePathsPanel.add(checkboxIsForRemoveLabel);
    favouritePathsPanel.add(removeFavourite);
    favouritePathsPanel.add(clearFavouritePathsButton);
            
    favouritePathsCB.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               if (favouritePathsCB.getItemCount() >= 1)  { // items exist
               int emask = e.getModifiers();
            if (removeFavourite.isSelected()) {
      int selIdx = favouritePathsCB.getSelectedIndex();
      favouritePathsCB.removeItemAt(selIdx);
      GlobalValues.favouriteElements.removeElementAt(selIdx);
           }  else  {
                
               String  pathSelected = (String) favouritePathsCB.getSelectedItem();
    try {
         GlobalValues.selectedExplorerPath = pathSelected;
         FileTreeExplorer  currentJPathTree = new FileTreeExplorer(pathSelected);
         GlobalValues.currentFileExplorer = currentJPathTree;
         TreePanel.removeAll();
         JScrollPane   scriptsPane = new JScrollPane(currentJPathTree.pathsTree);
         TreePanel.add(scriptsPane);
         TreePanel.revalidate();
    }
    catch (FileNotFoundException ex) {
        System.out.println("Error exploring "+pathSelected); 
        ex.printStackTrace();
            } 
        }
      }   // items exist
     
    }
    });
    
    ScriptsClassesPanel.add(favouritePathsPanel,
            new GBC(0,7).setFill(GridBagConstraints.NONE).setAnchor(GridBagConstraints.NORTH));
    
    
    JPanel keepScriptClassesNorth = new JPanel( new BorderLayout());
   keepScriptClassesNorth.add(ScriptsClassesPanel, BorderLayout.NORTH);
    JSplitPane PathsExplorerPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, keepScriptClassesNorth, TreePanel);
    PathsExplorerPane.setDividerLocation(0.5);
    add(PathsExplorerPane);    

    try {
         FileTreeExplorer  currentJPathTree = new FileTreeExplorer(GlobalValues.workingDir);
         GlobalValues.currentFileExplorer = currentJPathTree;
         TreePanel.removeAll();
         JScrollPane   scriptsPane = new JScrollPane(currentJPathTree.pathsTree);
         TreePanel.add(scriptsPane);
         TreePanel.revalidate();
    }
    catch (FileNotFoundException ex) {
        System.out.println("Error exploring "+GlobalValues.workingDir); 
        ex.printStackTrace();
      }
    }
    
     }  // buildScriptPathsTree
               
       

               