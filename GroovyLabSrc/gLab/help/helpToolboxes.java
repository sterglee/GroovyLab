
package gLab.help;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;

 public class helpToolboxes extends JFrame   { 

    private     JTabbedPane MainPane;  // the main tabbed pane for help panes
    private     JPanel jPanelNeural;  // neural toolbox help pane
    private     JTextField jTextFieldNeural;
    private     JTextArea  helpTextAreaNeural;
    private     JTextArea  helpTextAreaFuzzy;
    private     JPanel jPanelFuzzy;
    private     JTree  neuralFunctionsTree;  // available functions for help in a tree form
    private     JTree  fuzzyFunctionsTree;
//  holds the mapping of the help text to display for each keyword    
    private    HashMap availFunctions = new HashMap(); 
 
    public helpToolboxes() {
        MainPane = new  JTabbedPane();
        MainPane.setName("");
       
       jPanelNeural = new  JPanel();
       helpForNeural(jPanelNeural); 
     
       jPanelFuzzy = new JPanel();
       helpForFuzzy(jPanelFuzzy);
       
       MainPane.addTab("Neural", jPanelNeural);
       MainPane.addTab("Fuzzy", jPanelFuzzy);

        Container frameContainer =    getContentPane();
        frameContainer.add(MainPane, java.awt.BorderLayout.CENTER);
   
        pack();
        setSize(500,  500); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
     }
    

     void  helpForNeural(JPanel jPanelNeural)  {
     
         jPanelNeural.setLayout(new GridLayout(2,1));
        
        // root node 
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("Neural Functions");
    DefaultMutableTreeNode nSVM = new DefaultMutableTreeNode("Support Vector Machine (SVM)");
    String valueToAdd = "svmTrain";
    availFunctions.put(valueToAdd,  
            "************************************************************* \n"+
            "\ndouble [][] svmTrain(double [][] train, double [] labels, String svmTrainFile)"+
            "\n svmTrain: trains an SVM model and writes the trained SVM to a file"+
	   "\n************************************************************* \n"+
            "\ntrain: the input data train (a matrix for which each row represents a training vector"+
	  "\nlabels: the corresponding output labels for each data vector"+
	  "\nsvmTrainFile: the file which will represent the trained SVM model"+
            "\n[svmKernelType] Kernel type "+
	  "\nreturns: double[][] svValues -- the computed SV values");
        
    DefaultMutableTreeNode nSVMTrain = new DefaultMutableTreeNode(valueToAdd );
    nSVM.add(nSVMTrain);
    
    valueToAdd = "svmPredict";
    availFunctions.put(valueToAdd,  
            "************************************************************* \n"+
            "\n svmPredict(double [][]testData, String svmTrainedFile)"+
            "\n************************************************************* "+
              "\nsvmPredict: predicts the testData using a previously trained SVM model"+
	      "\ntestData: the vectors on which to evaluate outputs"+
	      "\nsvmTrainedFile: the trained SVM model that will be evaluated on the data"+
	      "\nreturns: the preducted outpur of the SVM");
    DefaultMutableTreeNode nSVMPredict = new DefaultMutableTreeNode(valueToAdd );
    nSVM.add(nSVMPredict);
    root.add(nSVM);
    
    neuralFunctionsTree = new JTree(root);  // the tree of global system variables
    // single selection
    int mode = TreeSelectionModel.SINGLE_TREE_SELECTION;
    neuralFunctionsTree.getSelectionModel().setSelectionMode(mode);
    
    
    neuralFunctionsTree.addTreeSelectionListener(new TreeSelectionListener() {
    	public void valueChanged(TreeSelectionEvent event) {
    		TreePath path = neuralFunctionsTree.getSelectionPath();
    		if (path==null) return;  // not any tree's node selected
    		DefaultMutableTreeNode selectedNode =
                 (DefaultMutableTreeNode) path.getLastPathComponent();
    		String selectedValue;
    		Enumeration enumVars;
    		String cVarName;
//  variable values are at depth 0, variable names at depth 1    		
    		if (selectedNode.getDepth() == 0)  {  
    			selectedValue = (String) selectedNode.getUserObject();
    		        displayHelpOn(selectedValue, helpTextAreaNeural);
                        
                }
             }  // valueChanged
 });  // addTreeSelectionListener
 
 jPanelNeural.add(neuralFunctionsTree); 
        
 helpTextAreaNeural = new JTextArea("Select  a function to get help here");
 jPanelNeural.add(helpTextAreaNeural);
        
 
 } 
   
     
     
  void  helpForFuzzy(JPanel jPanelFuzzy)  {
     
         jPanelFuzzy.setLayout(new GridLayout(2,1));
        
        // root node 
    String valueToAdd;    
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("Fuzzy Functions");
    DefaultMutableTreeNode mFs = new DefaultMutableTreeNode("Membership Functions");
    valueToAdd = "gaussianFS";
    DefaultMutableTreeNode nFuzzyMFs = new DefaultMutableTreeNode(valueToAdd );
    availFunctions.put(valueToAdd,  
            "************************************************************* \n"+
            "\ndouble [] GaussianFS(String fsName, double alphaCut, double gaussianCenter, double gaussianSpread)"+
            "\n GaussianFS: constructs a Gaussian Set" +
            "\n************************************************************* \n"+
            "\fsName: the name of the Gaussian fuzzy set"+
	  "\nalphaCut: membeships smaller than alphaCut are considered as zero"+
	  "\ngaussianCenter: the center of the Gaussian Fuzzy Set"+
            "\ngaussianSpread: controls the spreading (i.e. variance) of the Gaussian center "+
	  "\nreturns: double[] mfValues -- the computed Membership Function values of the gaussian fuzzy set");
        
    mFs.add(nFuzzyMFs);
    root.add(mFs);
    
    fuzzyFunctionsTree = new JTree(root);  
    // single selection
    int mode = TreeSelectionModel.SINGLE_TREE_SELECTION;
    fuzzyFunctionsTree.getSelectionModel().setSelectionMode(mode);
    
    fuzzyFunctionsTree.addTreeSelectionListener(new TreeSelectionListener() {
    	public void valueChanged(TreeSelectionEvent event) {
    		TreePath path = fuzzyFunctionsTree.getSelectionPath();
    		if (path==null) return;  // not any tree's node selected
    		DefaultMutableTreeNode selectedNode =
                  (DefaultMutableTreeNode) path.getLastPathComponent();
    		String selectedValue;
    		Enumeration enumVars;
    		String cVarName;
//  variable values are at depth 0, variable names at depth 1    		
    		if (selectedNode.getDepth() == 0)  {  
    			selectedValue = (String) selectedNode.getUserObject();
    			displayHelpOn(selectedValue, helpTextAreaFuzzy);
                        
                }
             }  // valueChanged
 });  // addTreeSelectionListener
 
 jPanelFuzzy.add(fuzzyFunctionsTree); 
        
 helpTextAreaFuzzy = new JTextArea("Select  a function to get help here");
 jPanelFuzzy.add(helpTextAreaFuzzy);
        
 
 } 
     
     
     
     public void displayHelpOn (String selectedValue, JTextArea currentTextArea) 
     {
        String helpStr = availFunctions.get(selectedValue).toString();
        
        currentTextArea.setText(helpStr);
              }
     }

