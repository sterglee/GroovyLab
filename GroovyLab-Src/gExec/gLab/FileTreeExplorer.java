
package gExec.gLab;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import org.codehaus.groovy.control.CompilerConfiguration;
        
        
import java.io.*; 
import java.util.*; 
import java.awt.*;
import java.awt.event.*;

import javax.swing.*; 
import javax.swing.tree.*; 
import javax.swing.event.*;
import java.lang.reflect.*;

import gExec.Interpreter.GlobalValues;

import groovy.lang.GroovyShell;
import gExec.ClassLoaders.ExtensionClassLoader;
import java.io.IOException;
import javax.swing.text.BadLocationException;
import jdk.jshell.SnippetEvent;
import org.codehaus.groovy.ant.GroovycExt;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;


public class FileTreeExplorer  {
     public  JTree  pathsTree;    // the tree that represents the file system
     public  DefaultTreeModel  model;
     static   JPopupMenu  pathsPopupMenu;  // the popup menu that handles operations with right mouse clicks
// used for invoking the main method of a Java class
     static Class [] formals = { String [].class };
     static Object [] actuals = { new String [] {""}};
     
     
      // path: a string that specifies the path of the root of the file system to explore
    public FileTreeExplorer(String path) 
      throws FileNotFoundException, SecurityException {
  
        // Create the first node 
       FileTreeNode  rootNode = null;
try  {
       rootNode = new FileTreeNode(null, path); 
}
catch (FileNotFoundException ex)  {
    System.out.println("File not found exception to construct root with specified path: "+path);
    ex.printStackTrace();
}

  // Populate the root node with its subdirectories
  boolean addedNodes = rootNode.populateDirectories(true);
  model = new DefaultTreeModel(rootNode);   // a simple tree data model that uses TreeNodes
  
  pathsTree = new JTree(model);  // JTree displays the set of hierarchical data of model
    
        // Use horizontal and vertical lines
  pathsTree.putClientProperty("JTree.lineStyle", "Angled"); 
  
  pathsPopupMenu = new JPopupMenu();
  
  pathsTree.setToolTipText("The gLab's explorer helps in configuring paths and editing/running Java Files. " +
            "Use  right mouse click on a selected file to display the popup menu with the relevant operations");
          
    // single selection
    pathsTree.addMouseListener(new MouseListener() {
            @Override
        public void mouseClicked(MouseEvent e) {    }
            @Override
        public void mouseEntered(MouseEvent e) {        }
            @Override
        public void mouseExited(MouseEvent e) {        }
        public void mousePressed(MouseEvent e) {   
            if (e.isPopupTrigger()){
               pathsPopupMenu.show((Component) e.getSource(), e.getX(), e.getY());
             }
           }
            
        public void mouseReleased(MouseEvent e) { 
           if (e.isPopupTrigger()){
               pathsPopupMenu.show((Component) e.getSource(), e.getX(), e.getY());
             }       
      }
    } );
    

  // Listen for Tree Selection Events
  pathsTree.addTreeExpansionListener(new TreeExpansionHandler()); 
    
  // add the keyboard listener for the File Tree Explorer
    pathsTree.addKeyListener(new FileTreeKeyListener());
 
  // Listen for Tree Selection Events
  pathsTree.addTreeExpansionListener(new TreeExpansionHandler()); 
    // add a paths listener for this tree
   gLabPathsListener  pathsListener = new gLabPathsListener(pathsTree);  
   pathsTree.addTreeSelectionListener(pathsListener);
   pathsTree.addTreeSelectionListener(new gLabMultiplePathsListener(pathsTree));
   
   
             JMenuItem newFileItem = new JMenuItem("New File at the current top level");
             newFileItem.setFont(gExec.Interpreter.GlobalValues.puifont);
           newFileItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                FileTreeNode selectedNode = (FileTreeNode) gLabPathsListener.selectedNode;
                if (selectedNode == null)   return;
                
                FileTreeNode parent = (FileTreeNode)  selectedNode.getParent();
                
                String newFileName = JOptionPane.showInputDialog(null, "Name for your new file?", JOptionPane.QUESTION_MESSAGE);
                
                // create the specified file actually at the filesystem
                String filePath = gLabPathsListener.selectedPath;  // the path of the selected node
                String newFileFullPathName = filePath+File.separator+newFileName;
                File newFile = new File(newFileFullPathName);
                boolean OKforNewFile = true;
                int userResponse = JOptionPane.YES_OPTION;   // allows further processing if file either not exists or user responds to overwrite
                if (newFile.exists())   {  // file already exists
                    userResponse = JOptionPane.showConfirmDialog(null, "File: "+newFileFullPathName+" already exists. Overwrite? ", "File already exists",
                            JOptionPane.YES_NO_OPTION);
                    OKforNewFile = (userResponse == JOptionPane.YES_OPTION);
                    if (OKforNewFile) {
                      boolean deleteSuccess = newFile.delete();
                      if (deleteSuccess == false)
                          JOptionPane.showMessageDialog(null, "Failing to delete file: "+newFileFullPathName, "File delete failed", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                 
                if (OKforNewFile)   {  // create new file and update tree
                    // create new file
                     try {
                    newFile.createNewFile();
                     }
                     catch (IOException ioEx)  { 
    JOptionPane.showMessageDialog(null, "IOException trying to create file"+newFileFullPathName, "IO Exception", JOptionPane.INFORMATION_MESSAGE );
                     }
                
                      // update tree
                    int selectedIndex = 0;
                    if (parent != null) {
                selectedIndex = parent.getIndex(selectedNode);
                    }
                
                    // update the tree model
                    FileTreeNode  newNode = null;
                    try {
                      newNode = new FileTreeNode(new File(filePath),  newFileName);
                      newNode.setUserObject(filePath+File.separator+newFileName);
                    // now display the new node
                    model.insertNodeInto(newNode, parent, selectedIndex+1);
                    TreeNode [] nodes = model.getPathToRoot(newNode);
                    TreePath pathScroll = new TreePath(nodes);
                    pathsTree.expandPath(pathScroll);
                    }
                    catch (FileNotFoundException ex)  { System.out.println("File not  found exception in creating new File"); ex.printStackTrace();}
                    catch (SecurityException ex)  { System.out.println("Security exception in creating new File"); ex.printStackTrace();}
                    
                    
                }
               }
           });
       
             JMenuItem browseUpItem = new JMenuItem("Up Folder");
             browseUpItem.setFont(gExec.Interpreter.GlobalValues.puifont);
           browseUpItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                FileTreeNode selectedNode = (FileTreeNode) gLabPathsListener.selectedNode;
                if (selectedNode == null)   return;
                
                String selectedPath = selectedNode.toString();
                int idxLastPath = selectedPath.lastIndexOf(File.separatorChar);
                
                if (idxLastPath==-1) {
                    JOptionPane.showMessageDialog(null, "Already in root level", "There is no parent folder", JOptionPane.INFORMATION_MESSAGE);  
                    return;
                }
                
                String pathComponent = selectedPath.substring(0, idxLastPath);
                
                GlobalValues.selectedExplorerPath = pathComponent;
                 GlobalValues.gLabMainFrame.explorerPanel.updatePaths();
                
           }
           });
       
          
             
             JMenuItem newFileInDirItem = new JMenuItem("New File within the directory");
             newFileInDirItem.setFont(gExec.Interpreter.GlobalValues.puifont);
           newFileInDirItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                FileTreeNode selectedNode = (FileTreeNode) gLabPathsListener.selectedNode;
                if (selectedNode == null)   return;
                
                FileTreeNode parent = (FileTreeNode)  selectedNode.getParent();
                
                String newFileName = JOptionPane.showInputDialog(null, "Name for your new file?", JOptionPane.QUESTION_MESSAGE);
                
                // create the specified file actually at the filesystem
                String filePath = gLabPathsListener.selectedValue;  // the path of the selected node
                File directoryOfNewFile = new File(filePath);
                if (directoryOfNewFile.isDirectory()==false) {
                    JOptionPane.showMessageDialog(null, "Cannot place a file within another file!!", "Improper attempt to create a file within a directory", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                String newFileFullPathName = directoryOfNewFile+File.separator+newFileName;
                File newFile = new File(newFileFullPathName);
                boolean OKforNewFile = true;
                int userResponse = JOptionPane.YES_OPTION;   // allows further processing if file either not exists or user responds to overwrite
                if (newFile.exists())   {  // file already exists
                    userResponse = JOptionPane.showConfirmDialog(null, "File: "+newFileFullPathName+" already exists. Overwrite? ", "File already exists",
                            JOptionPane.YES_NO_OPTION);
                    OKforNewFile = (userResponse == JOptionPane.YES_OPTION);
                    if (OKforNewFile) {
                      boolean deleteSuccess = newFile.delete();
                      if (deleteSuccess == false)
                          JOptionPane.showMessageDialog(null, "Failing to delete file: "+newFileFullPathName, "File delete failed", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                 
                if (OKforNewFile)   {  // create new file and update tree
                    // create new file
                     try {
                    newFile.createNewFile();
                     }
                     catch (IOException ioEx)  { 
    JOptionPane.showMessageDialog(null, "IOException trying to create file"+newFileFullPathName, "IO Exception", JOptionPane.INFORMATION_MESSAGE );
                     }
                
                      // update tree
                    int selectedIndex = 0;
                    if (parent != null)
                        selectedIndex = parent.getIndex(selectedNode);
                
                    // update the tree model
                    FileTreeNode  newNode = null;
                    try {
                      newNode = new FileTreeNode(new File(filePath),  newFileName);
                      newNode.setUserObject(filePath+File.separator+newFileName);  // sets the user object for this node
                    // now display the new node
                    model.insertNodeInto(newNode, selectedNode, 0);
                    TreeNode [] nodes = model.getPathToRoot(newNode);
                    TreePath pathScroll = new TreePath(nodes);
                    pathsTree.expandPath(pathScroll);
                    }
                    catch (FileNotFoundException ex)  { System.out.println("File not  found exception in creating new File"); ex.printStackTrace();}
                    catch (SecurityException ex)  { System.out.println("Security exception in creating new File"); ex.printStackTrace();}
                    
                    
                }
               }
              
         
           });
                       JMenuItem newDirectoryItem = new JMenuItem("New Directory at the current directory");
                       newDirectoryItem.setFont(gExec.Interpreter.GlobalValues.puifont);
           newDirectoryItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                FileTreeNode selectedNode = (FileTreeNode) gLabPathsListener.selectedNode;
                if (selectedNode == null)   return;
                
                FileTreeNode parent = (FileTreeNode)  selectedNode.getParent();
                if (parent == null) {
                    JOptionPane.showMessageDialog(null, "Files cannot be created at the root level", "Please select inner nodes", JOptionPane.INFORMATION_MESSAGE);  
                    return;
                }
                
                String newDirectoryName = JOptionPane.showInputDialog(null, "Name for your new directory?", JOptionPane.QUESTION_MESSAGE);
                
                // create the specified directory actually at the filesystem
                String directoryPath = gLabPathsListener.selectedPath;  // the path of the selected node
                String newDirectoryFullPathName = directoryPath+File.separator+newDirectoryName;
                File newDirectory = new File(newDirectoryFullPathName);
                File pathOfDirectory  = new File(directoryPath);
            
                int userResponse = JOptionPane.YES_OPTION;  
                boolean  dirExists = newDirectory.exists();
                if (dirExists)     // directory already exists
                     JOptionPane.showMessageDialog(null, "Directory: "+newDirectoryFullPathName+" already exists");
                    
                if (dirExists==false)   {  // create new directory and update tree
                    // create new directory
                     try {
                newDirectory.mkdir();  // creates the corresponding directory
                     }
                     catch (SecurityException ioEx)  { 
    JOptionPane.showMessageDialog(null, "IOException trying to create file"+newDirectoryFullPathName, "IO Exception", JOptionPane.INFORMATION_MESSAGE );
                     }
                  
                     // update tree model to correspond to the altered filesystem structure 
                    int selectedIndex = parent.getIndex(selectedNode);
                
                    // update the tree model
                    try {
                    FileTreeNode  newNode = new FileTreeNode(pathOfDirectory, newDirectoryName);
                    
                    model.insertNodeInto(newNode, parent, selectedIndex+1);
                    // now display the new node
                    TreeNode [] nodes = model.getPathToRoot(newNode);
                    TreePath path = new TreePath(nodes);
                    pathsTree.scrollPathToVisible(path);
                  }
                    catch (SecurityException ex) { System.out.println("Security exception in creating pathOfDirectory"); ex.printStackTrace(); }
                    catch (FileNotFoundException ex) { System.out.println("FileNotFoundException exception in creating pathOfDirectory"); ex.printStackTrace(); }
                    
                    }
               }
           });
           

           JMenuItem  renameFileJItem = new JMenuItem("Rename");
           renameFileJItem.setFont(gExec.Interpreter.GlobalValues.puifont);
           renameFileJItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                FileTreeNode selectedNode = (FileTreeNode) gLabPathsListener.selectedNode;
                if (selectedNode == null)   return;
                
                FileTreeNode parent = (FileTreeNode)  selectedNode.getParent();
                if (parent == null) {
                    JOptionPane.showMessageDialog(null, "Files cannot be created at the root level", "Please select inner nodes", JOptionPane.INFORMATION_MESSAGE);  
                    return;
                }
                String filePath = gLabPathsListener.selectedValue;  // the path of the selected node
                int fileNameStartIdx = filePath.lastIndexOf(File.separatorChar)+1;
                String fileNameComponent = filePath.substring(fileNameStartIdx, filePath.length());
                String pathNameComponent = filePath.substring(0, fileNameStartIdx);
                String newFileName = JOptionPane.showInputDialog("Specify new file", fileNameComponent);  
                String newFullFileName = pathNameComponent + newFileName;
                File selectedFileToRename = new File(filePath);
                File renamedFile = new File(newFullFileName);
                selectedFileToRename.renameTo(renamedFile);   // rename the file at the filesystem
                
                // update tree
                int selectedIndex = parent.getIndex(selectedNode);  // index of the selected child in this node's child array
                model.removeNodeFromParent(selectedNode);
                FileTreeNode newNode = null;
                try {
                    File parentRenamedFile = new File(pathNameComponent);
                    newNode = new FileTreeNode(parentRenamedFile, newFileName);
                    newNode.setUserObject(newFullFileName);  // update the tree model
                    model.insertNodeInto(newNode, parent, parent.getChildCount());                    
                    // now display the new node
                    TreeNode [] nodes = model.getPathToRoot(newNode);
                    TreePath pathScroll = new TreePath(nodes);
                    pathsTree.expandPath(pathScroll);
            }
                    catch (SecurityException ex) { System.out.println("Security exception in creating pathOfDirectory"); ex.printStackTrace(); }
                    catch (FileNotFoundException ex) { System.out.println("FileNotFoundException exception in creating pathOfDirectory"); ex.printStackTrace(); }
                    }
           });
                
                        
           
           JMenuItem runGroovyScriptItem = new JMenuItem("Run Groovy script file with Groovy Shell");
           runGroovyScriptItem.setFont(gExec.Interpreter.GlobalValues.puifont);
           runGroovyScriptItem.setFont(gExec.Interpreter.GlobalValues.puifont);
           runGroovyScriptItem.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                   String scriptFile = gLabPathsListener.selectedValue;
                   int groovyIdx = scriptFile.indexOf(".groovy");
                   if (groovyIdx != -1) {    // valid groovy file
                             // set the compiler configuration to have the list of the specified .jar files in its classpath
       if (GlobalValues.GroovyShell == null)  {  // construct a properly inited GroovyShell
            CompilerConfiguration cf = new CompilerConfiguration();
            LinkedList <String> pathsList = new LinkedList<String>();
            pathsList.add("." );   // current directory
            for (int k=0; k<GlobalValues.jartoolboxesForGroovySci.size(); k++)
               pathsList.add((String)GlobalValues.jartoolboxesForGroovySci.get(k));
         // append now the GroovySciClassPath components
            for (int k=0; k<GlobalValues.GroovySciClassPathComponents.size(); k++)
                pathsList.add((String) GlobalValues.GroovySciClassPathComponents.elementAt(k));
            
            String jLabJarFileName = GlobalValues.jarFilePath;
            pathsList.add(jLabJarFileName);
            cf.setClasspathList(pathsList);
            final ClassLoader parentClassLoader = GlobalValues.gLabMainFrame.getClass().getClassLoader();
   
            GlobalValues.GroovyShell = new GroovyShell(parentClassLoader, GlobalValues.groovyBinding, cf);
        }
                        try {
                    File groovyFile = new File(scriptFile);         
                    GlobalValues.GroovyShell.evaluate(groovyFile);
                    }
            catch (Exception ex) {
                        ex.printStackTrace();
                        }
               }  // valid JavaScript file
                   else
                       JOptionPane.showMessageDialog(null, "Invalid JavaScript file: "+scriptFile+". JavaScript files should have an extension of .js");
               }
           });
           
              JMenuItem runGroovyScriptItemClassLoader = new JMenuItem("Run Groovy script file with GroovyClassLoader");
              runGroovyScriptItem.setFont(gExec.Interpreter.GlobalValues.puifont);
           runGroovyScriptItemClassLoader.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                   String scriptFile = gLabPathsListener.selectedValue;
                   int groovyIdx = scriptFile.indexOf(".groovy");
                   int classIdx = scriptFile.indexOf(".class"); // GroovyClassLoader can execute Java classes directly
                   if (groovyIdx != -1 || classIdx != -1 ) {    // valid groovy file
                        CompilerConfiguration  groovyCompilerConf = new CompilerConfiguration();
                        LinkedList <String> pathsList = new LinkedList<String>();
                        pathsList.add("." );   // current directory
                        for (int k=0; k<GlobalValues.jartoolboxesForGroovySci.size(); k++)
                            pathsList.add((String)GlobalValues.jartoolboxesForGroovySci.get(k));
         // append now the GroovySciClassPath components
                       for (int k=0; k<GlobalValues.GroovySciClassPathComponents.size(); k++)
                        pathsList.add((String) GlobalValues.GroovySciClassPathComponents.elementAt(k));
            
                String jLabJarFileName = GlobalValues.jarFilePath;
                pathsList.add(jLabJarFileName);
                
                      groovyCompilerConf.setClasspathList(pathsList);
                      GroovyClassLoader  grLoader = new GroovyClassLoader(getClass().getClassLoader(), groovyCompilerConf);
                      try {
                    File groovyFile = new File(scriptFile);         
                    Class grClass;
                    if (groovyIdx != -1) 
                        grClass = grLoader.parseClass(groovyFile);
                    else  // classIdx != -1, i.e. load precompiled class
                    {
                        String grClassName = scriptFile.substring(scriptFile.lastIndexOf(File.separatorChar)+1, scriptFile.indexOf("."));
                        grClass = grLoader.loadClass(grClassName);
                    }
                     
                    GroovyObject  grObj = (GroovyObject) grClass.newInstance();
                    Object [] args = {};
                    grObj.invokeMethod("main", args);
                        }
            catch (Exception ex) {
                        ex.printStackTrace();
                        }
               }  // valid JavaScript file
                   else
                       JOptionPane.showMessageDialog(null, "Invalid JavaScript file: "+scriptFile+". JavaScript files should have an extension of .js");
               }
           });
           
         
      
           JMenuItem specifyAsJarToolbox = new JMenuItem("Specify as .jar toolbox");
           specifyAsJarToolbox.setFont(gExec.Interpreter.GlobalValues.puifont);
           specifyAsJarToolbox.addActionListener(new ActionListener() {

           public void actionPerformed(ActionEvent e) {
                String selectedValue =  gLabPathsListener.selectedValue;
                        
               if ( (selectedValue.indexOf(".jar")!=-1)) {
GlobalValues.jartoolboxesForGroovySci.add(selectedValue);                   
               }
            }
           });
           
           JMenuItem deleteFileItem = new JMenuItem("Delete File");
           deleteFileItem.setFont(gExec.Interpreter.GlobalValues.puifont);
           deleteFileItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                FileTreeNode selectedNode = (FileTreeNode) gLabPathsListener.selectedNode;
                if (selectedNode == null)   return;
                
                FileTreeNode parent = (FileTreeNode)  selectedNode.getParent();
                if (parent == null) {
                    JOptionPane.showMessageDialog(null, "Files cannot be deleted at the root level", "Cannot delete top level root directories", JOptionPane.INFORMATION_MESSAGE);  
                    return;
                }
                // delete the specified file actually at the filesystem
                String fileToDelete= gLabPathsListener.selectedValue;  // the path of the selected node
                File newFile = new File(fileToDelete);
                boolean OKforDeleteFile = true;
                int userResponse = JOptionPane.YES_OPTION;   // confirm the user for file deletetion
                if (newFile.exists())   {  // file object exists
                    userResponse = JOptionPane.showConfirmDialog(null, "Delete File: "+fileToDelete+" ? ", "Confirm delete",
                            JOptionPane.YES_NO_OPTION);
                    OKforDeleteFile = (userResponse == JOptionPane.YES_OPTION);
                    if (OKforDeleteFile) {
                      boolean deleteSuccess = newFile.delete();
                      if (deleteSuccess == false)
                          JOptionPane.showMessageDialog(null, "Failing to delete file: "+fileToDelete, "File delete failed", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                 
                    model.removeNodeFromParent(selectedNode);
                
                }
           });
           
            
           JMenuItem editItem = new JMenuItem("Edit");
           editItem.setFont(gExec.Interpreter.GlobalValues.puifont);
           editItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedValue =  gLabPathsListener.selectedValue;
                        
               if ( (selectedValue.indexOf(".j")!=-1)  || (selectedValue.indexOf(".java")!=-1) || (selectedValue.indexOf(".groovy")!=-1) 
                        || (selectedValue.indexOf(".scala")!=-1)  || (selectedValue.indexOf(".gsci") !=-1) || (selectedValue.indexOf(".js")!=-1))  { // "usual" file for editing
                    if (GlobalValues.myGEdit != null) {
                        GlobalValues.myGEdit.gLabEdit(selectedValue);
                           }
                    else  { 
                        GlobalValues.myGEdit = new gLabEdit.gLabEditor(selectedValue);
                        // GlobalValues.myGEdit.gLabEdit(selectedValue);
                       } 
                     }
               else {   // if the file seems non-usual, first confirm that the user wants editing
                    int response = JOptionPane.showConfirmDialog(null, "Edit file "+selectedValue+ " ?", "Edit Confirmation Dialog", JOptionPane.YES_NO_OPTION);
                    if (response == JOptionPane.YES_OPTION)  {
                    if (GlobalValues.myGEdit != null) {
                        GlobalValues.myGEdit = new gLabEdit.gLabEditor(selectedValue);
                        //GlobalValues.myGEdit.gLabEdit(selectedValue);
                           }
                    else  {
                        GlobalValues.myGEdit = new gLabEdit.gLabEditor(selectedValue);
                        //GlobalValues.myGEdit.gLabEdit(selectedValue);
                       } 
                        
                    }
                        
               }
                   }
                });
                
           
                
            
           JMenuItem editInNewItem = new JMenuItem("Edit (new editor session)");
           editInNewItem.setFont(gExec.Interpreter.GlobalValues.puifont);
           editInNewItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedValue =  gLabPathsListener.selectedValue;
                        
               if ( (selectedValue.indexOf(".j")!=-1)  || (selectedValue.indexOf(".java")!=-1) || (selectedValue.indexOf(".groovy")!=-1) 
                        || (selectedValue.indexOf(".scala")!=-1)  || (selectedValue.indexOf(".gsci") !=-1) || (selectedValue.indexOf(".js")!=-1))   // "usual" file for editing
                    new  gLabEdit.gLabEditor(selectedValue);
               else {   // if the file seems non-usual, first confirm that the user wants editing
                    int response = JOptionPane.showConfirmDialog(null, "Edit file "+selectedValue+ " ?", "Edit Confirmation Dialog", JOptionPane.YES_NO_OPTION);
                    if (response == JOptionPane.YES_OPTION)  
                      new  gLabEdit.gLabEditor(selectedValue);    
                    }
                        
               }
                });
                
        JMenuItem favouritesPathsAddItem = new JMenuItem("Append the path to the favourites Paths");
        favouritesPathsAddItem.setFont(gExec.Interpreter.GlobalValues.puifont);
        favouritesPathsAddItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            
                // create the specified file actually at the filesystem
                String filePath = gLabPathsListener.selectedValue;  // the path of the selected node
                File directoryOfNewFile = new File(filePath);
                if (directoryOfNewFile.isDirectory()==false) {
                    JOptionPane.showMessageDialog(null, "Please select for paths only directories!!", "Improper attempt to select a file as a directory",  JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
     
                String directoryName = directoryOfNewFile.getAbsolutePath();
                if (GlobalValues.favouriteElements.contains(directoryName) == false) {
                        GlobalValues.favouriteElements.add(directoryName);
                        gLabExplorer.favouritePathsCB.addItem(directoryName);
        }
            }});
                
                
                JMenuItem addPathToGroovySciPathsItem = new JMenuItem("Append the path to the GroovySci Paths");
                addPathToGroovySciPathsItem.setFont(gExec.Interpreter.GlobalValues.puifont);
                addPathToGroovySciPathsItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            
                // create the specified file actually at the filesystem
                String filePath = gLabPathsListener.selectedValue;  // the path of the selected node
                File directoryOfNewFile = new File(filePath);
                if (directoryOfNewFile.isDirectory()==false) {
                    JOptionPane.showMessageDialog(null, "Please select for paths only directories!!", "Improper attempt to select a file as a directory",  JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
     
                // append the specified path to GroovySci paths if not already exists
                if (GlobalValues.GroovySciClassPathComponents.contains(filePath) == false)
                   GlobalValues.GroovySciClassPathComponents.add(filePath);
        
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
    GlobalValues.GroovyShell = null;   // recreate GroovyShell with the updated path
    }
        });
        
        
        JMenuItem compileGroovyItem = new JMenuItem("Compile *.groovy file");
        compileGroovyItem.setFont(gExec.Interpreter.GlobalValues.puifont);
        compileGroovyItem.setToolTipText("Compiles the selected .groovy file generating .class files on disk ");
        compileGroovyItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String baseDir = gLabPathsListener.selectedPath;
                String selectedFullFilename = gLabPathsListener.selectedValue;
                String  pathComponent = selectedFullFilename.substring(0, selectedFullFilename.lastIndexOf(File.separatorChar));
       
                GlobalValues.selectedExplorerPath = pathComponent; 
              
                int fileNameStartIdx = selectedFullFilename.lastIndexOf(File.separatorChar);
                int lenFullPath = selectedFullFilename.length();
                String groovyFileName = selectedFullFilename.substring(fileNameStartIdx+1, lenFullPath);
                GroovycExt.groovyCompile( baseDir,  selectedFullFilename);
                
                GlobalValues.gLabMainFrame.explorerPanel.updatePaths();
            }
            
        });

        
      JMenuItem runGroovyClassMenuItem = new JMenuItem("Run a .groovy  class  ");
             runGroovyClassMenuItem.setFont(gExec.Interpreter.GlobalValues.puifont);
           runGroovyClassMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            
              String selectedValue = gLabPathsListener.selectedValue;
             if (selectedValue.indexOf(".class")!=-1)  {   // not a compiled .class file
         
                 String  pathComponent = selectedValue.substring(0, selectedValue.lastIndexOf(File.separatorChar));
                 String fileNameComponent = selectedValue.substring( selectedValue.lastIndexOf(File.separatorChar)+1, selectedValue.length());
                 String classFile = fileNameComponent.substring(0,  fileNameComponent.indexOf('.'));
               
            
               GlobalValues.selectedExplorerPath = pathComponent; 
                 
                if (selectedValue.indexOf(".class")!=-1)  {   // file is class file
                 
                 String [] command  = new String[4];
                 int idx = GlobalValues.fullJarFilePath.lastIndexOf(File.separatorChar);
                 if (idx == -1) idx = GlobalValues.fullJarFilePath.lastIndexOf('/');  // try Java standard File seperator
                 
                 command[0] =  "java";
                 command[1] = "-cp";
                 command[2] =   "."+File.pathSeparator+GlobalValues.gLabLibPath+File.pathSeparator+
                         pathComponent+File.pathSeparator+
                         gLabGlobals.JavaGlobals.groovyJarFile +   File.pathSeparator+
                         gLabGlobals.JavaGlobals.ejmlFile+File.pathSeparator+
                         gLabGlobals.JavaGlobals.openblasfile+File.pathSeparator+
                         gLabGlobals.JavaGlobals.jblasFile+File.pathSeparator+
                         gLabGlobals.JavaGlobals.jsciFile+File.pathSeparator+
                         gLabGlobals.JavaGlobals.mtjColtSGTFile+File.pathSeparator+
                         gLabGlobals.JavaGlobals.ApacheCommonsFile+File.pathSeparator+
                         gLabGlobals.JavaGlobals.numalFile+File.pathSeparator+
                         gLabGlobals.JavaGlobals.LAPACKFile+File.pathSeparator+
                         gLabGlobals.JavaGlobals.ARPACKFile+File.pathSeparator+
                        GlobalValues.jarFilePath;
                 
                 command[3] =  classFile;
                 
                try {
                
                Runtime rt = Runtime.getRuntime();    
                Process javaProcess = rt.exec(command);
                // an error message?
                StreamGobbler errorGobbler = new StreamGobbler(javaProcess.getErrorStream(), "ERROR");
                
                // any output?
                StreamGobbler outputGobbler = new StreamGobbler(javaProcess.getInputStream(), "OUTPUT");
                
                // kick them off
                errorGobbler.start();
                outputGobbler.start();
                
                // any error???
                javaProcess.waitFor();
                int rv = javaProcess.exitValue();
                String commandString = command[0]+"  "+command[1]+"  "+command[2]+" "+command[3];
                if (rv==0)
                 System.out.println("Process:  "+commandString+"  exited successfully ");
                else
                 System.out.println("Process:  "+commandString+"  exited with error, error value = "+rv);
 
                GlobalValues.gLabMainFrame.explorerPanel.updatePaths();
 
                } catch (IOException ex) {
                    System.out.println("IOException trying to executing "+command);
                    ex.printStackTrace();
                            
                }
               catch (InterruptedException ie) {
                    System.out.println("Interrupted Exception  trying to executing "+command);
                    ie.printStackTrace();
                            
                }
                
            }
           }
            }
           });
           
   
           JMenuItem compileJavaWithJavaCMenuItem = new JMenuItem("Compile .java with javac");
        compileJavaWithJavaCMenuItem.setFont(gExec.Interpreter.GlobalValues.puifont);
           compileJavaWithJavaCMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedValue = gLabPathsListener.selectedValue;
                String  pathComponent = selectedValue.substring(0, selectedValue.lastIndexOf(File.separatorChar));
                GlobalValues.selectedExplorerPath = pathComponent; 
                
                String javaFile = selectedValue.substring(selectedValue.lastIndexOf(File.separatorChar)+1, selectedValue.length());
               
                 String  command ;
                 /*Map<String, String> envs = System.getenv();  // get the classpath variable
                 String classPath = envs.get("CLASSPATH");
                 System.out.println("CLASSPATH = "+classPath);  */
                 command =  "javac "+ selectedValue +  // the Java file to be compiled
                         " -d " +  // option to specify the directory where to output the compiled .class
                 pathComponent;
                 
                try {
                
                Runtime rt = Runtime.getRuntime();    
                Process javaProcess = rt.exec(command);
                // an error message?
                StreamGobbler errorGobbler = new StreamGobbler(javaProcess.getErrorStream(), "ERROR");
                
                // any output?
                StreamGobbler outputGobbler = new StreamGobbler(javaProcess.getInputStream(), "OUTPUT");
                
                // kick them off
                errorGobbler.start();
                outputGobbler.start();
                
                // any error???
                javaProcess.waitFor();
                int rv = javaProcess.exitValue();
                if (rv==0)
                 System.out.println("Process:  "+command+"  exited successfully ");
                else
                 System.out.println("Process:  "+command+"  exited with error, error value = "+rv);
 
                GlobalValues.gLabMainFrame.explorerPanel.updatePaths();
 
                } catch (IOException ex) {
                    System.out.println("IOException trying to executing "+command);
                    ex.printStackTrace();
                            
                }
                catch (InterruptedException ie) {
                    System.out.println("Interrupted Exception  trying to executing "+command);
                    ie.printStackTrace();
                            
                }
                
            }
           });
           
        JMenuItem runJavaWithJavaMenuItem = new JMenuItem("Run .class file with java");
        runJavaWithJavaMenuItem.setFont(gExec.Interpreter.GlobalValues.puifont);
               
               runJavaWithJavaMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            String selectedValue = gLabPathsListener.selectedValue;
             if (selectedValue.indexOf(".class")!=-1)  {   // not a compiled .class file
         
                 String  pathComponent = selectedValue.substring(0, selectedValue.lastIndexOf(File.separatorChar));
                 String fileNameComponent = selectedValue.substring( selectedValue.lastIndexOf(File.separatorChar)+1, selectedValue.length());
                 String classFile = fileNameComponent.substring(0,  fileNameComponent.indexOf('.'));
               
                 GlobalValues.selectedExplorerPath = pathComponent; 
                 
               
                 String [] command  = new String[4];
             
                 command[0] =  "java";
                 command[1] = "-cp";
                 command[2] = pathComponent+"."+File.pathSeparator+GlobalValues.gLabLibPath+
                         File.pathSeparator+GlobalValues.jarFilePath;
                 command[3] =  classFile;    
                 
                try {
                
                Runtime rt = Runtime.getRuntime();    
                Process javaProcess = rt.exec(command);
                // an error message?
                StreamGobbler errorGobbler = new StreamGobbler(javaProcess.getErrorStream(), "ERROR");
                // any output?
                StreamGobbler outputGobbler = new StreamGobbler(javaProcess.getInputStream(), "OUTPUT");
                
                // kick them off
                errorGobbler.start();
                outputGobbler.start();
                
                // any error???
                javaProcess.waitFor();
                int rv = javaProcess.exitValue();
                String commandString = command[0]+"  "+command[1]+"  "+command[2]+" "+command[3];
                if (rv==0)
                 System.out.println("Process:  "+commandString+"  exited successfully ");
                else
                 System.out.println("Process:  "+commandString+"  exited with error, error value = "+rv);
 
                GlobalValues.gLabMainFrame.explorerPanel.updatePaths();
 
                } catch (IOException ex) {
                    System.out.println("IOException trying to executing "+command);
                    ex.printStackTrace();
                            
                }
                catch (InterruptedException ie) {
                  System.out.println("Interrupted Exception  trying to executing "+command);
                  ie.printStackTrace();
                }
                
            }
           }
           });
               
               
               
                  JMenuItem executeWithJShell = new JMenuItem("Execute Code with Jshell (use also F7 key)");
           executeWithJShell.setFont(gExec.Interpreter.GlobalValues.puifont);
           executeWithJShell.addActionListener( new ActionListener() {
    @Override
            public void actionPerformed(ActionEvent e) {
         String currentText = GlobalValues.globalEditorPane.getSelectedText();
         if (currentText != null) {
                       
         java.util.List <SnippetEvent>  grResultSnippets =  gExec.Interpreter.GlobalValues.jshell.eval( currentText);
                     if (grResultSnippets != null) {
            String rmSuccess = grResultSnippets.toString().replace("Success", "");    
               
      //  GlobalValues.consoleOutputWindow.output.append(rmSuccess);
        GlobalValues.consoleOutputWindow.output.setCaretPosition(GlobalValues.consoleOutputWindow.output.getText().length());
                 }
           
    System.out.flush();
    
           }
            }
           });
           
           
           JMenuItem compileWithInternalJava = new JMenuItem("Compile with internal Java Compiler");
           compileWithInternalJava.setFont(gExec.Interpreter.GlobalValues.puifont);
           compileWithInternalJava.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
       
	  String jarFileName = GlobalValues.jarFilePath;
                  // get the user's home path
          String homePath = GlobalValues.homeDir;
          String selectedValue = gLabPathsListener.selectedValue;
          String  pathComponent = selectedValue.substring(0, selectedValue.lastIndexOf(File.separatorChar));
          GlobalValues.selectedExplorerPath = pathComponent; 
          String javaFile = selectedValue;
          String packageName = "";   // do not place the new Java Classes within some package !!
       
          String [] command  = new String[5];
          String toolboxes = "";
       for (int k=0; k<GlobalValues.GroovySciClassPathComponents.size();k++)
         toolboxes = toolboxes+File.pathSeparator+GlobalValues.GroovySciClassPathComponents.elementAt(k);
   
        // compile the temporary file

       command[0] =  "java";
       command[1] = "-classpath";
       command[2] =  "."+File.pathSeparator+GlobalValues.jarFilePath+File.pathSeparator+toolboxes+File.pathSeparator+homePath;
       command[3] =  "com.sun.tools.javac.Main";    // the name of the Java  compiler class
       command[4] = javaFile;
       String compileCommandString = command[0]+"  "+command[1]+"  "+command[2]+" "+command[3]+" "+command[4];

       System.out.println("compileCommand Java= "+compileCommandString);
       
       int rv =0;
            try {   // execute Java compile command
                Runtime rt = Runtime.getRuntime();
                Process javaProcess = rt.exec(command);
                // an error message?
                StreamGobbler errorGobbler = new StreamGobbler(javaProcess.getErrorStream(), "ERROR");

                // any output?
                StreamGobbler outputGobbler = new StreamGobbler(javaProcess.getInputStream(), "OUTPUT");

                // kick them off
                errorGobbler.start();
                outputGobbler.start();

                // any error???
                javaProcess.waitFor();
                rv = javaProcess.exitValue();
                String commandString = command[0]+"  "+command[1]+"  "+command[2];
                if (rv==0)
                 System.out.println("Process:  "+commandString+"  exited successfully ");
                else
                 System.out.println("Process:  "+commandString+"  exited with error, error value = "+rv);
       
                GlobalValues.gLabMainFrame.explorerPanel.updatePaths();
             
            } // execute Java compile command
            catch (IOException exio) {
                    System.out.println("IOException trying to executing "+command);
                    exio.printStackTrace();
                }
               catch (InterruptedException ie) {
                    System.out.println("Interrupted Exception  trying to executing "+command);
                    ie.printStackTrace();
            }
       
         }
   });
            
           JMenuItem compileRunItem = new JMenuItem("Compile .java file and run");
           compileRunItem.setFont(gExec.Interpreter.GlobalValues.puifont);
           compileRunItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GlobalValues.extensionClassLoader = new ExtensionClassLoader(GlobalValues.gLabClassPath);
                utils.JavaCompile javaCompileObj = new utils.JavaCompile();
                ExtensionClassLoader  extClassLoader = GlobalValues.extensionClassLoader;
                String selectedValue = gLabPathsListener.selectedValue;
                String  pathComponent = selectedValue.substring(0, selectedValue.lastIndexOf(File.separatorChar));
                GlobalValues.selectedExplorerPath = pathComponent; 
                String javaFile = selectedValue;
                String packageName = "";   // do not place the new Java Classes within some package !!
               if (selectedValue.indexOf(".java")!=-1)  {   // file is a Java source file
         boolean compilationResult = javaCompileObj.compileFile(javaFile);
         if (compilationResult == true)  // success
         {
             GlobalValues.gLabMainFrame.explorerPanel.updatePaths();
 
             System.out.println("Compilation success for file "+packageName+"."+javaFile);
             int lastPos = javaFile.length()-5;  // for ".java"
             
             String javaFileWithoutExt = javaFile.substring(0, javaFile.indexOf('.'));
             String javaFileWithoutExtWithoutPath = javaFileWithoutExt.substring(javaFileWithoutExt.lastIndexOf(File.separatorChar)+1, javaFileWithoutExt.length());
             pathComponent = javaFile.substring(0, javaFile.lastIndexOf(File.separatorChar));
             String command = "java";
             String arg1 = "-cp ";
             String arg2 = "."+File.pathSeparatorChar+GlobalValues.jarFilePath+File.pathSeparatorChar+pathComponent;
             
             String withoutExtJavaStr = javaFile.substring(0, lastPos);
                                
             String  classNameToLoad =withoutExtJavaStr.substring(withoutExtJavaStr.lastIndexOf(File.separatorChar)+1, withoutExtJavaStr.length());
         
             int javaNameIdx = javaFile.indexOf(classNameToLoad);
             String pathToAdd = javaFile.substring(0, javaNameIdx-1);   // path to add the the extension class loader class path
             extClassLoader.extendClassPath(pathToAdd);  // append the path
             extClassLoader.extendClassPath(GlobalValues.jarFilePath);
             extClassLoader.extendClassPath(".");
                         
           try {   // try to load the class
                        Class  loadedClass = extClassLoader.loadClass(classNameToLoad);
                        Method m = null;
                        try {
                            m = loadedClass.getMethod("main", formals);
                        }
                        catch (NoSuchMethodException exc) {
                            System.out.println(" no main in  "+classNameToLoad);
                            exc.printStackTrace();
                        }
                        
                        try {
                            m.invoke(null, actuals);
                        }
                        catch (Exception exc)  {
                            exc.printStackTrace();
                          }
           }  // try to load the class
                        
                        catch (ClassNotFoundException ex)  {
                            System.out.println("Class: "+classNameToLoad+" not found");
                            ex.printStackTrace();
                        } 
  
             GlobalValues.gLabMainFrame.explorerPanel.updatePaths();
                
            }   // compilation result success
               }  // file is a java source file
                   }   // actionPerformed
                });  // addActionListener
        
                JMenuItem compileGrammarItem = new JMenuItem("Compile ANTLR Grammar file");
           compileGrammarItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedValue = gLabPathsListener.selectedValue;
                String  pathComponent = selectedValue.substring(0, selectedValue.lastIndexOf(File.separatorChar));
       
                GlobalValues.selectedExplorerPath = pathComponent; 
                String grammarFile = selectedValue;
                 if (selectedValue.indexOf(".g")!=-1)  {   // file is a Grammar source file
         
               
             String  classNameToLoad ="org.antlr.Tool";
         
             Class [] gformals = { String [].class };
             Object [] gactuals = { new String [] {selectedValue}};
     
         
                         
           try {   // try to load the class
                         ExtensionClassLoader  extClassLoader = GlobalValues.extensionClassLoader;
          
                        Class  loadedClass = extClassLoader.loadClass(classNameToLoad);
                        Method m = null;
                        try {
                            m = loadedClass.getMethod("main", gformals);
                        }
                        catch (NoSuchMethodException exc) {
                            System.out.println(" no main in  "+classNameToLoad);
                            exc.printStackTrace();
                        }
                        
                        try {
                            m.invoke(null, gactuals);
                        }
                        catch (Exception exc)  {
                            exc.printStackTrace();
                          }
           }  // try to load the class
                        
                        catch (ClassNotFoundException ex)  {
                            System.out.println("Class: "+classNameToLoad+" not found");
                            ex.printStackTrace();
                        } 
  
             GlobalValues.gLabMainFrame.explorerPanel.updatePaths();
                
            }   // compilation result success
                                 }   // actionPerformed
                });  // addActionListener
     
                
                     JMenuItem updatePathsJMenuItem = new JMenuItem("Update the display of the selected object folder");
                     updatePathsJMenuItem.setFont(gExec.Interpreter.GlobalValues.puifont);
           compileGrammarItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedValue = gLabPathsListener.selectedValue;
                String  pathComponent = selectedValue.substring(0, selectedValue.lastIndexOf(File.separatorChar));
       
                GlobalValues.selectedExplorerPath = pathComponent; 
                
             GlobalValues.gLabMainFrame.explorerPanel.updatePaths();
                
            }  // actionPerformd
           });  // addActionListener
     
           JMenu  fileOpsItems = new JMenu("File Operations");
           fileOpsItems.setFont(gExec.Interpreter.GlobalValues.puifont);
           fileOpsItems.add(browseUpItem);
           fileOpsItems.add(newFileItem);
           fileOpsItems.add(newFileInDirItem);
           fileOpsItems.add(newDirectoryItem);
           fileOpsItems.add(renameFileJItem);
           fileOpsItems.add(deleteFileItem);
           pathsPopupMenu.add(fileOpsItems);
           
           pathsPopupMenu.add(specifyAsJarToolbox);
           
           
           JMenu JavaMenu = new JMenu("Java");
           JavaMenu.setFont(gExec.Interpreter.GlobalValues.puifont);
           JavaMenu.add(executeWithJShell);
           JavaMenu.add(compileWithInternalJava);
           JavaMenu.add(compileRunItem);
           JavaMenu.add(runJavaWithJavaMenuItem);
           JavaMenu.add(compileJavaWithJavaCMenuItem);
           pathsPopupMenu.add(JavaMenu);
                   
                      
           JMenu groovyMenu = new JMenu("Groovy");
           groovyMenu.setFont(gExec.Interpreter.GlobalValues.puifont);
           groovyMenu.add(runGroovyScriptItem);
           groovyMenu.add(runGroovyScriptItemClassLoader);
           groovyMenu.add(compileGroovyItem);
           groovyMenu.add(compileGroovyItem);
           groovyMenu.add(runGroovyClassMenuItem);
           
           pathsPopupMenu.add(groovyMenu);
           
              
           
           JMenu  pathsMenu = new JMenu("Paths");
           pathsMenu.setFont(gExec.Interpreter.GlobalValues.puifont);
           pathsMenu.add(addPathToGroovySciPathsItem);
           pathsMenu.add(favouritesPathsAddItem);
           pathsPopupMenu.add(pathsMenu);
           
           JMenu editMenu = new JMenu("Edit");
           editMenu.setFont(gExec.Interpreter.GlobalValues.puifont);
           editMenu.add(editItem);
           editMenu.add(editInNewItem);
           pathsPopupMenu.add(editMenu);
           
           pathsPopupMenu.add(updatePathsJMenuItem);
           

 
}
    
  

        
    

// Returns the full pathname for a path, or null
// if not a known path
public String getPathName(TreePath path) {
  Object o = path.getLastPathComponent();
  if (o instanceof FileTreeNode) {
     return ((FileTreeNode)o).file.getAbsolutePath();
  } return null;
} 

// Returns the File for a path, or null if not a known path
public File getFile(TreePath path) { 
  Object o = path.getLastPathComponent();
  if (o instanceof FileTreeNode) {
    return ((FileTreeNode)o).file;
  }
  return null;
} 

// Inner class that represents a node in this
// file system tree
protected static class FileTreeNode extends DefaultMutableTreeNode {
  public FileTreeNode(File parent, String name)
         throws SecurityException, FileNotFoundException { 
      this.name = name; 

  // See if this node exists and whether it
  // is a directory
  file = new File(parent, name);
  if (!file.exists()) {
    throw new FileNotFoundException("File " + name + " does not exist");
  }

  isDir = file.isDirectory(); 

  // Hold the File as the user object.
  setUserObject(file); 

} 

// Override isLeaf to check whether this is a directory 
public boolean isLeaf() {
  return !isDir; 
} 

// Override getAllowsChildren to check whether
// this is a directory
public boolean getAllowsChildren() {
  return isDir;
} 


// For display purposes, we return our own name 
public String toString() { return name; } 

// If we are a directory, scan our contents and populate
// with children. In addition, populate those children
// if the "descend" flag is true. We only descend once,
// to avoid recursing the whole subtree.
// Returns true if some nodes were added
boolean populateDirectories(boolean descend) {
  boolean addedNodes = false; 
// Do this only once 
if (populated == false) {
  if (interim == true) { 
    // We have had a quick look here before:
    // remove the dummy node that we added last time
    removeAllChildren();
    interim = false; 
  } 

  String[] names = file.list();// Get list of contents 

  // Process the directories
if (names != null) {
  for (int i = 0; i < names.length; i++) {
    String name = names[i];  
    File d = new File(file, name);
    try {
        FileTreeNode node =  new FileTreeNode(file, name);
        this.add(node);
      if (d.isDirectory()) {  // file is a directory
        if (descend) {
          node.populateDirectories(false); 
        }
        addedNodes = true;
        if (descend == false) {
          // Only add one node if not descending 
          break; 
        }
      } 
    } catch (Throwable t) {
      // Ignore phantoms or access problems
    } 
  } 

  // If we were scanning to get all subdirectories,
  // or if we found no subdirectories, there is no
  // reason to look at this directory again, so
  // set populated to true. Otherwise, we set interim
  // so that we look again in the future if we need to
  if (descend == true || addedNodes == false) {
    populated = true; 
  } else {
  // Just set interim state
          interim = true;
        }
      }
}
      return addedNodes; 
    } 

    protected File file;// File object for this node 
    protected String name;// Name of this node 
    protected boolean populated;    // true if we have been populated 
    protected boolean interim;    // true if we are in interim state 
    protected boolean isDir;// true if this is a directory
  } 

  // Inner class that handles Tree Expansion Events  (i.e. when a tree expands or collapses a node)
  protected class TreeExpansionHandler  implements TreeExpansionListener {
    public void treeExpanded(TreeExpansionEvent evt) { 
      TreePath path = evt.getPath();// The expanded path JTree tree  (JTree)evt.getSource();// The tree 

      // Get the last component of the path and
      // arrange to have it fully populated.
      FileTreeNode node =  (FileTreeNode)path.getLastPathComponent();
      if (node.populateDirectories(true)) {
          JTree tree = (JTree)evt.getSource();
        ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(node);
  } 
    } 

    public void treeCollapsed(TreeExpansionEvent evt) {
      // Nothing to do 
    } 
  } 
  
  public static void main(String [] args) {
      JFrame testFrame = new JFrame("testing FileTree");
      FileTreeExplorer   ftree=null;
              
              try {
          ftree = new FileTreeExplorer("/export/home/sterg");
      }
      catch (FileNotFoundException exc)  {}
      
      JPanel  myPanel = new JPanel();
      myPanel.add(ftree.pathsTree);
      testFrame.add(myPanel);
      testFrame.setSize(500, 600);
      testFrame.setVisible(true);
 }
  
  
} 

