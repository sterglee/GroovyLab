package gExec.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import gExec.Interpreter.GlobalValues;

import java.util.Enumeration;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import gExec.gLab.FileTreeExplorer;
import gExec.gLab.groovySciToolbox;
import gExec.gLab.groovySciToolboxes;
import gLabGlobals.JavaGlobals;
import java.awt.Component;
import java.util.Vector;

/**A function for displaying and configuring gLab's parameters */
public class groovySciScriptsPathsTree extends JPanel {
      private JTree GVarsTree;
      public  DefaultTreeModel  model;

 public void buildVariablesTree()  {
        // root node 
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("Global Path Variables");
    
    DefaultMutableTreeNode nGroovyShellPath  = new DefaultMutableTreeNode("GroovyShell Classpath");
    int numClassPathComponents = GlobalValues.GroovyShellPathsList.size();
    for (int k=0; k<numClassPathComponents; k++) {
        String currentToolbox = GlobalValues.GroovyShellPathsList.get(k);
        DefaultMutableTreeNode nGroovyShellJarPath = new DefaultMutableTreeNode(currentToolbox);
        nGroovyShellPath.add(nGroovyShellJarPath);
    }
     
    DefaultMutableTreeNode njLabJavaClassPath = new DefaultMutableTreeNode("GroovySci Classes Dir");
    int numOfDirsOfClassPath = GlobalValues.GroovySciClassPathComponents.size();
    for (int k=0; k<numOfDirsOfClassPath; k++) {
        String currentDirElem = GlobalValues.GroovySciClassPathComponents.elementAt(k).toString().trim();
        DefaultMutableTreeNode nvjLabClassPath = new DefaultMutableTreeNode(currentDirElem);
        njLabJavaClassPath.add(nvjLabClassPath);
        
    }
    root.add(nGroovyShellPath);
    root.add(njLabJavaClassPath);
     
    // workingDir: Current Working Directory
    String wkDir = GlobalValues.workingDir;
    DefaultMutableTreeNode cwdFilesDir = new DefaultMutableTreeNode("Current Working Directory");
    DefaultMutableTreeNode vcwdFilesDir = new DefaultMutableTreeNode(wkDir);
    root.add(cwdFilesDir); cwdFilesDir.add(vcwdFilesDir);

    model = new DefaultTreeModel(root);   // a simple tree data model that uses TreeNodes


    GVarsTree = new JTree(model);  // JTree displays the set of hierarchical data of model
    GVarsTree.setFont(GlobalValues.guifont);
    
    // single selection
    int mode = TreeSelectionModel.SINGLE_TREE_SELECTION;
    GVarsTree.getSelectionModel().setSelectionMode(mode);
 
    GVarsTree.expandRow(1);  // expand the tree in order the user to see it
    GVarsTree.setToolTipText("User paths for GroovySci Classpath. To remove a component from Groovy classpath press DEL and restart a new interpreter ");
       
    add(new JScrollPane(GVarsTree));

    GVarsTree.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                
            }

            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
              if (keyCode == e.VK_DELETE) {
               TreePath path = GVarsTree.getSelectionPath();
    		if (path==null) return;  // not any tree's node selected
    		DefaultMutableTreeNode selectedNode =
                 (DefaultMutableTreeNode) path.getLastPathComponent();
    		String selectedValue;
    		Enumeration enumVars;
    		String cVarName;
//  variable values are at depth 0, variable names at depth 1
    		if (selectedNode.getDepth() == 0)  {
    			selectedValue = (String) selectedNode.getUserObject();
               javax.swing.JOptionPane.showMessageDialog(null, "removing  "+selectedValue);
               
               model.removeNodeFromParent(selectedNode);

                     // remove the specified path from the groovySci paths if already exists
                if (GlobalValues.GroovySciClassPathComponents.contains(selectedValue) == true)
                   GlobalValues.GroovySciClassPathComponents.remove(selectedValue);

                
                StringBuilder fileStr = new StringBuilder();
      Enumeration enumDirs = GlobalValues.GroovySciClassPathComponents.elements();
      while (enumDirs.hasMoreElements())  {
         Object ce = enumDirs.nextElement();
         fileStr.append(File.pathSeparator+(String)ce.toString().trim());
    }
      GlobalValues.GroovySciClassPath   = fileStr.toString();

                        gExec.gLab.gLab.updateTree();
            }
        }
            } // keypressed

         
            @Override
            public void keyReleased(KeyEvent e) {
                
            }
        });

    GVarsTree.addTreeSelectionListener(new TreeSelectionListener() {
    	public void valueChanged(TreeSelectionEvent event) {
    		TreePath path = GVarsTree.getSelectionPath();
    		if (path==null) return;  // not any tree's node selected
    		DefaultMutableTreeNode selectedNode =
                 (DefaultMutableTreeNode) path.getLastPathComponent();
    		String selectedValue;
    		Enumeration enumVars;
    		String cVarName;
//  variable values are at depth 0, variable names at depth 1    		
    		if (selectedNode.getDepth() == 0)  {  
    			selectedValue = (String) selectedNode.getUserObject();
                File testExists = new File(selectedValue);
                if (testExists.exists() && testExists.isDirectory()) {
                try {
                FileTreeExplorer ftree = new FileTreeExplorer(selectedValue);
                GlobalValues.currentFileExplorer = ftree;
                JFrame treeFrame = new JFrame("Files under the directory "+selectedValue);
                treeFrame.add(new JScrollPane(ftree.pathsTree));
                treeFrame.setSize(600, 500);
                treeFrame.setVisible(true);
                 }
                        catch (FileNotFoundException ex) {
                            System.out.println("File not found exception for file "+selectedValue);
                            ex.printStackTrace();
                        } catch (SecurityException ex) {
                            System.out.println("Security exception for file "+selectedValue);
                            ex.printStackTrace();
                        }
                }
                else {
                selectedValue = (String) selectedNode.getUserObject();
                Object [] options = {  "Remove", "Display", "Cancel"};
               int  response = javax.swing.JOptionPane.showOptionDialog(
                       (Component) GVarsTree, (Object)"remove toolbox (OK)  or display toolbox contents (CANCEL)",
"remove or update", JOptionPane.YES_NO_CANCEL_OPTION,  JOptionPane.INFORMATION_MESSAGE, 
null, options, options[1]);
                       //this, "remove toolbox (OK)  or display toolbox contents (CANCEL)");
               if (response == 0) {  // Remove
                model.removeNodeFromParent(selectedNode);

                     // remove the specified path from the groovySci paths if already exists
                if (GlobalValues.GroovySciClassPathComponents.contains(selectedValue) == true)
                   GlobalValues.GroovySciClassPathComponents.remove(selectedValue);

                
                StringBuilder fileStr = new StringBuilder();
      Enumeration enumDirs = GlobalValues.GroovySciClassPathComponents.elements();
      while (enumDirs.hasMoreElements())  {
         Object ce = enumDirs.nextElement();
         fileStr.append(File.pathSeparator+(String)ce.toString().trim());
    }
      GlobalValues.GroovySciClassPath  = fileStr.toString();

                            gExec.gLab.gLab.updateTree();
            }  // OK to remove the node
               
               else if (response==1) 
               { // Display
                         
                String toolboxName = selectedValue;
               
            Vector toolboxClasses    =  gExec.ClassLoaders.JarClassLoader.scanAll(toolboxName);
            gExec.gui.WatchClasses  watchClassesOfToolbox = new gExec.gui.WatchClasses();
            
        
            watchClassesOfToolbox.displayClasses( toolboxClasses, toolboxName, gExec.gui.WatchClasses.watchXLoc+50, gExec.gui.WatchClasses.watchYLoc+50);
               }  // Display
               
        } // else               
                        }
                 }
                }
            );  // addTreeSelectionListener
 
 }
}
