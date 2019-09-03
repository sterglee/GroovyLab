
package gLab.help;

import gExec.Interpreter.GlobalValues;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
// help on gLab's system functions'
 public class helpJLabSysFunctions extends JFrame   { 

    private     JTabbedPane MainPane;  // the main tabbed pane for help panes
    private     JPanel jPanelSystem;  // system functions help pane
    private     JTextField jTextFieldSystem;
    private     JTextArea  helpTextAreaSystem;
    private     JTextArea  helpTextAreaGraphics;
    private     JPanel jPanelGraphics;  // graphic functions help pane
    private     JTree  systemFunctionsTree; // available functions for help in a tree form
    private     JTree  graphicsFunctionsTree;
 //  holds the mapping of the help text to display for each keyword    
   private     HashMap availFunctions = new HashMap();
 
    public helpJLabSysFunctions() {
        MainPane = new  JTabbedPane();  // the TabbedPane componenet holds all the tabs
        MainPane.setName("");
       
       jPanelSystem = new  JPanel();
       helpForSystem(jPanelSystem); 
     
       jPanelGraphics = new JPanel();
       helpForGraphics(jPanelGraphics);
       
       MainPane.addTab("System", jPanelSystem);
       MainPane.addTab("Graphics", jPanelGraphics);

        Container frameContainer =  getContentPane();
        frameContainer.add(MainPane, java.awt.BorderLayout.CENTER);
   
        pack();
        setSize(500,  500); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
     }
    

     void  helpForSystem(JPanel jPanelSystem)  {
     
         jPanelSystem.setLayout(new GridLayout(2,1));
        
        // root node 
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("System Functions");
    DefaultMutableTreeNode nSystem = new DefaultMutableTreeNode("System");
    String valueToAdd = "addPath";
    availFunctions.put(valueToAdd,  
            "************************************************************* \n"+
            "\naddPath( String path)"+
           "\n************************************************************* \n"+
            "\n adds the string path to the search path\n"+
	  "\n********************Example ********************************* \n"+
           "\n addPath(\"/myNewFunctions\") " );
    DefaultMutableTreeNode nAddPath = new DefaultMutableTreeNode(valueToAdd );
    
    nSystem.add(nAddPath);
    valueToAdd = "classPath";
    DefaultMutableTreeNode nClassPath = new DefaultMutableTreeNode(valueToAdd );
    nSystem.add(nClassPath);
    availFunctions.put(valueToAdd,  
            "************************************************************* \n"+
            "\n returns  the current value of classPath, "+
            "\n i.e. the variable where the system searches for class files \n"+
            "\n*************  Example ********************************** "+
              "\nmyPath = classpath");
	      
   valueToAdd = "cd";
   DefaultMutableTreeNode ncd = new DefaultMutableTreeNode(valueToAdd);
   nSystem.add(ncd);
   availFunctions.put(valueToAdd,
             "\n cd(<new working dir>) \n"+
           " changes the current working directory to <new working dir>\n"+
            "\n  ************Example ********************************** \n"+
             "\n cd(\"/myJFiles/neural\")  \n"+
           "\n changes the current working directory to /myFiles/neural "
           );
   
   valueToAdd = "dir";
   DefaultMutableTreeNode ndir = new DefaultMutableTreeNode(valueToAdd);
   nSystem.add(ndir);
   availFunctions.put(valueToAdd,
            " \ndirectory listing of the current working directory\n"+
           "\n or of the specified directory\n"+
             "\n*******************Example ********************************** \n"+
             "\n dir  \n"+
            "\n dir(\"/home/user\")"
           
           );
    
    
   valueToAdd = "exist";
   DefaultMutableTreeNode nexist = new DefaultMutableTreeNode(valueToAdd);
   nSystem.add(nexist);
   availFunctions.put(valueToAdd,
            " \nexist(<filename>): tests if the named file  exists. Returns 0 if not, 1 if yes"+
             "\n*******************Example ********************************** \n"+
             "\n exist(\"testWhile.j\")    \n"
           );
    
   
   root.add(nSystem);
    
    systemFunctionsTree = new JTree(root);  // the tree of global system variables
    // single selection
    int mode = TreeSelectionModel.SINGLE_TREE_SELECTION;
    systemFunctionsTree.getSelectionModel().setSelectionMode(mode);
    
    systemFunctionsTree.addTreeSelectionListener(new TreeSelectionListener() {
    	public void valueChanged(TreeSelectionEvent event) {
    		TreePath path = systemFunctionsTree.getSelectionPath();
    		if (path==null) return;  // not any tree's node selected
    		DefaultMutableTreeNode selectedNode =
                 (DefaultMutableTreeNode) path.getLastPathComponent();
    		String selectedValue;
    		Enumeration enumVars;
    		String cVarName;
//  variable values are at depth 0, variable names at depth 1    		
    		if (selectedNode.getDepth() == 0)  {  
    			selectedValue = (String) selectedNode.getUserObject();
    			displayHelpOn(selectedValue, helpTextAreaSystem);
                        
                }
             }  // valueChanged
 });  // addTreeSelectionListener
 
 jPanelSystem.add(systemFunctionsTree); 
        
 helpTextAreaSystem = new JTextArea("Select  a function to get help here");
 jPanelSystem.add(helpTextAreaSystem);
        
 
 } 
   
     
     
  void  helpForGraphics(JPanel jPanelGraphics)  {
     
         jPanelGraphics.setLayout(new GridLayout(2,1));
        
        // root node 
    String valueToAdd;    
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("Graphics Functions");
    DefaultMutableTreeNode nFigHandling = new DefaultMutableTreeNode("Figure Handling");
    valueToAdd = "figure";
    DefaultMutableTreeNode nFigure = new DefaultMutableTreeNode(valueToAdd );
    availFunctions.put(valueToAdd,  
            "************************************************************* \n"+
            "\nfigure(<figure number>\n)"+
            "\n creates a new figure with number <figure number>\n"+
           " \n ****** Example  *******************\n"+
            "\n   figure(2)\n"+
            "\n the figure 2 is created and it becomes the current figure");       
          
    nFigHandling.add(nFigure);
    root.add(nFigHandling);
    
    valueToAdd = "grid";
    DefaultMutableTreeNode ngrid = new DefaultMutableTreeNode(valueToAdd );
    availFunctions.put(valueToAdd,  
            "************************************************************* \n"+
            "\n grid(\"on\"|1), grid(\"off\"|0)\n"+
            "\n adds, removes a grid from the axes of the current figure \n"+
            "\n ******** Example ******** \n"+
            "\n x=0:0.01:30; y = sin(1.14*x)+0.2*cos(1.67*x); figure(1); plot(x,y); grid(\"on\")\n"
            ); 
          
    nFigHandling.add(ngrid);
    
     valueToAdd = "hold";
    DefaultMutableTreeNode nhold = new DefaultMutableTreeNode(valueToAdd );
    availFunctions.put(valueToAdd,  
            "************************************************************* \n"+
            "\n hold(\"on\"|1), hold(\"off\"|0)\n"+
            "\n Holds the current axes. All subsequent uses of plot will accumulate in this axes\n"+
            "\n ******** Example ******** \n"+
            "\n x=0:0.01:30; y = sin(0.4*x)+0.2*cos(0.17*x); z=5*sin(0.9*x); \n"+
            "\n figure(1); plot(x,y); hold(\"on\"); plot(x,z); grid(\"on\")\n"+
            "\n figure(2); plot(y,z, \"b\"); grid(\"on\"); title(\"Changing the default color to blue also \"); \n"
            ); 
    nFigHandling.add(nhold);
    
    valueToAdd = "subplot";
    DefaultMutableTreeNode nsubplot = new DefaultMutableTreeNode(valueToAdd );
    availFunctions.put(valueToAdd,  
            "************************************************************* \n"+
           "\n subplot(y, x, axes):  divides a graph into subplots. \n"+
            "\n Example: \n"+
            "\n t=0:0.02:100; x1=sin(1.88*t); x2=sin(0.34*t); x3=sin(0.8*t)+6*cos(0.98*t); "+
            "\n subplot(2,2,1); plot(x1); title(\"x1\"); subplot(2,2,2); plot(x2); title(\"x2\"); "+
            "\n subplot(2,2,3); plot(x3); title(\"x3\"); subplot(2,2,4); plot(x1+x2); title(\"x1+x2\"); "
); 
    nFigHandling.add(nsubplot);
    
    valueToAdd = "plot";
    DefaultMutableTreeNode nplot = new DefaultMutableTreeNode(valueToAdd);
    availFunctions.put(valueToAdd,
    "***************************************************************** \n"+
    "\n    Plots a graph of the supplied points                                          \n"+
    "graph settings: \n"+
    "color:  y   yellow, m   magenta, c   cyan,  r   red,  g   green, b   blue, w   white, k   black \n"+
    "linestyle: -     solid,  :     dotted,  -.    dashdot,  --    dashed,  marker .     point,  o     circle \n"+
    "x     x-mark,  +     plus,  *     star,  s     square,  d     diamond,  v     triangle (down) \n"+
    "^     triangle (up),  <     triangle (left), >     triangle (right),  p     pentagram,  h     hexagram \n \n"+
     "\n Example: \n"+
    "\n t=0:0.01:10; x=sin(0.288*t); plot(t,x,'b' ) \n"
            );
    nFigHandling.add(nplot);
             
     valueToAdd = "plot3";
    DefaultMutableTreeNode nplot3 = new DefaultMutableTreeNode(valueToAdd);
    availFunctions.put(valueToAdd,
    "***************************************************************** \n"+
    "\n    Three-Dimensional Plot                                          \n"+
    "graph settings: \n"+
    "color:  y   yellow, m   magenta, c   cyan,  r   red,  g   green, b   blue, w   white, k   black \n"+
    "linestyle: -     solid,  :     dotted,  -.    dashdot,  --    dashed,  marker .     point,  o     circle \n"+
    "x     x-mark,  +     plus,  *     star,  s     square,  d     diamond,  v     triangle (down) \n"+
    "^     triangle (up),  <     triangle (left), >     triangle (right),  p     pentagram,  h     hexagram \n \n"+
     "\n Example: \n"+
    "\n t=0:0.1:100; x=sin(0.88*t); y=cos(0.88*t);  plot3(t,50*x,y, 'b' )\n"
            );
    nFigHandling.add(nplot3);
             
     valueToAdd = "surf";
    DefaultMutableTreeNode nsurf = new DefaultMutableTreeNode(valueToAdd);
    availFunctions.put(valueToAdd,
    "***************************************************************** \n"+
    "\n    Surface Plot, surf(matrix), Plots a 3 dimensional surface      \n"+
     "\n Example: \n"+
     "\n [x,y]=meshgrid([-20:0.2:20],[-20:0.2:20]); z = sin(0.3*x).*cos(0.2*y); surf(z);  "
            );
    nFigHandling.add(nsurf);
             
    
    valueToAdd = "zfig";
    DefaultMutableTreeNode nzfig = new DefaultMutableTreeNode(valueToAdd);
    availFunctions.put(valueToAdd,
            "******************************************************** \n"+
            "\n zfig(double [] x, double []  y, String figureTitle, String x-axis-Label, String y-axis-Label) "+
            "\n figure with zoom capability \n"+
            "\n Example: \n"+
            "\n t=0:0.01:10; x=sin(1.48*t); "+ 
            "\n zfig(t,x, \"Sine Plot\", \"time\", \"sine value\")"
            );
    nFigHandling.add(nzfig);
    
    
    graphicsFunctionsTree = new JTree(root);  
    // single selection
    int mode = TreeSelectionModel.SINGLE_TREE_SELECTION;
    graphicsFunctionsTree.getSelectionModel().setSelectionMode(mode);
    
    graphicsFunctionsTree.addTreeSelectionListener(new TreeSelectionListener() {
    	public void valueChanged(TreeSelectionEvent event) {
    		TreePath path = graphicsFunctionsTree.getSelectionPath();
    		if (path==null) return;  // not any tree's node selected
    		DefaultMutableTreeNode selectedNode =
                  (DefaultMutableTreeNode) path.getLastPathComponent();
    		String selectedValue;
    		Enumeration enumVars;
    		String cVarName;
//  variable values are at depth 0, variable names at depth 1    		
    		if (selectedNode.getDepth() == 0)  {  
    			selectedValue = (String) selectedNode.getUserObject();
    			displayHelpOn(selectedValue, helpTextAreaGraphics);
                        
                }
             }  // valueChanged
 });  // addTreeSelectionListener
 
 jPanelGraphics.add(graphicsFunctionsTree); 
        
 helpTextAreaGraphics = new JTextArea("Select  a function to get help here");
 jPanelGraphics.add(helpTextAreaGraphics);
        
 
 } 
     
     
     
     public void displayHelpOn (String selectedValue, JTextArea currentTextArea) 
     {
        String helpStr = availFunctions.get(selectedValue).toString();
        currentTextArea.setFont(GlobalValues.defaultTextFont);        
        currentTextArea.setText(helpStr);
              }
     }

