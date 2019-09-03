// the main GUI of gLab
package gExec.Wizards;

import gExec.Interpreter.GlobalValues;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.Vector;

import gExec.gui.GBC;
import gExec.gLab.StreamGobbler;

// synthesize the proper Java code and the appropriate GroovySci-Script
import java.util.Enumeration;
// for Java-based solution of ODEs implementation
  
     // processes generation of script code
     public   class ODEWizardGroovySciGroovy  {
         String editingClassName = "";
         int systemOrder;   // the order of ODE system

         static final  int   ODErke = 0;
         static final  int   ODErk4na = 1;
         static final  int   ODEmultistep = 2;
         static final  int   ODEdiffsys = 3;
         
         static public String packageName = "";
         static  int   ODESolveMethod = ODErke; 
         
        Vector availODEMethods = new Vector();     
         
         JButton  copyTemplateButton, generateEditingButton,  compileJavaInternalClassButton,  generateScriptCodeButton;
         
         JFrame ODEWizardFrame;
         static JFrame scriptFrame;
         
         JPanel editPanel, bottomPanel, paramPanel, statusPanel;
         
        JTextField groovyFileTextBox; 
        JTextField systemOrderText;
        
        JTextArea statusAreaTop, statusAreaBottom;
         
         JTextArea templateTextArea;
         static String classImplementationString;
         
         String  templateText;
             // template text for rke() ODE method
         final String   templateTextRKE= "public void der(int n, double t, double []y) \n"+
                "{ \n"+ "  double xx,yy,zz; \n"+ "\n    xx=y[1];     yy=y[2];     zz=y[3]; \n"+
                 "\n    y[1] = 10*(yy-xx);  \n" +"    y[2] = -xx*zz+143*xx - yy;  \n"+
                 "    y[3] = xx*yy - 2.66667*zz;  \n"+"  } \n"+
                 " \n\npublic void out(int n, double []t, double []te, double []y, double [] data)  {  } \n";
         
             // template text for rk4na() ODE method
         final String   templateTextRK4na = "     // controls when to finish integration \n"+
    " public double b(int n, double [] x)  { \n"+
     "   if (Math.abs(x[1]) < closenessToZeroPrecision) return 0.0;   // integration criteria met  \n"+
     "           cnt++; \n"+
     "   if (cnt > LimitNumEvaluations)   return 0.0;  // stop the integration  \n"+
     "   return 1.0; \n"+" } \n\n"+
     "    public double fxj( int n, int k, double []y) { \n"+
         "    double xx,yy,zz;\n"+
         "    if (k==1)     return  10*(y[2]-y[1]);    //  1st equation right side  \n"+
         "    if (k==2)   return -y[1]*y[3]+143*y[1]-y[2];    //  2nd equation right side, etc "+
        "\n \n     return y[1]*y[2]-2.66667*y[3];  \n } \n ";

        // template text for multistep() ODE method
         final String   templateTextMultistep = "  // an example template implementing the Lorenz attractor \n"+
                 "public void deriv(double []df, int n, double []x, double []y)  { \n"+
            "double xx,yy,zz; \n"+
            "    xx=y[1];     yy=y[2];     zz=y[3]; \n"+
            "   df[1] = 10*(yy-xx); \n     df[2] = -xx*zz+143*xx - yy; \n     df[3] = xx*yy - 2.66667*zz; \n   } \n \n"+
            "     public boolean available(int n, double []x, double []y, double [][]jac)  { \n"+
            "       jac[1][1] = -10;     jac[1][2] = 10;  jac[1][3] = 0; \n"+
            "       jac[2][1] = -y[3];   jac[2][2] = -1;  jac[2][3] = -y[1]; \n"+
            "       jac[3][1] = y[2];    jac[3][2] = 0;  jac[3][3] = -2.66; \n"+
            "\n        return true; \n     } \n\n"+
                 "    public void out(double h, int k, int n, double []x, double []y) { \n"+
                " return; \n } \n"; 

         // template text for diffsys() ODE method
         final String   templateTextDiffsys = "  // an example template implementing the Lorenz attractor \n"+
            "public void derivative(int n, double x, double []y, double []dy)  { \n"+
            "double xx,yy,zz; \n"+
            "xx=y[1];     yy=y[2];     zz=y[3]; \n"+
            "dy[1] = 10*(yy-xx); \n"+
            "dy[2] = -xx*zz+143*xx - yy; \n"+
            "dy[3] = xx*yy - 2.66667*zz; \n   }\n\n\n"+
            " public void output(int n, double []x,  double xe, double []y, double []dy)  { \n"+
            "return; \n  } \n";
 
                 
        final String [] implementingInterfaces ={  "AP_rke_methods", "AP_rk4na_methods", "AP_multistep_methods",
          "AP_diffsys_methods" };
                           
         JScrollPane templateScrollPane;
         JViewport templateViewPort;
         
         JTextArea ODEWizardTextArea;
         JScrollPane ODEWizardScrollPane;
         String    ODEWizardText;
         JViewport wizardViewPort;
         
         JPanel paramMethodPanel;
         JLabel  ODEselectMethodLabel;
         JComboBox ODEselectMethodComboBox;
         JLabel  currentlySelectedLabel;

         static boolean   typedCode  = false; // use @grrovy.transform.CompileStatic  for static code
         
         
             public ODEWizardGroovySciGroovy(boolean typed) {
            typedCode = typed;
            editingClassName = "Lorenz";
            systemOrder=3;   // the order of ODE system
            
            copyTemplateButton = new JButton("1. Copy and Edit Template", new ImageIcon("/gLab.jar/yellow-ball.gif"));  
            generateEditingButton = new JButton("2. Generate Groovy Class", new ImageIcon("./blue-ball.gif"));
            generateScriptCodeButton = new JButton("Generate GroovySci Script", new ImageIcon("red-ball.gif"));
            
            ODEWizardFrame = new JFrame("ODE Wizard for GroovySci ");
         
            editPanel = new JPanel();
            editPanel.setLayout(new GridLayout(1,2));

            
            paramPanel = new JPanel(new GridLayout(1, 4));
            availODEMethods.add("ODErke"); 
            availODEMethods.add("ODErk4na");
            availODEMethods.add("ODEmultistep");
            availODEMethods.add("ODEdiffsys");
            ODEselectMethodLabel = new JLabel("ODE method: ");
            ODEselectMethodComboBox = new JComboBox(availODEMethods);
            ODEselectMethodComboBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ODESolveMethod = ODEselectMethodComboBox.getSelectedIndex();
                    updateTemplateText();
                    templateTextArea.setText(templateText);
                    currentlySelectedLabel.setText("Selected Method: "+(String)availODEMethods.get(ODESolveMethod));
                       }
            });
            
            JLabel  groovyFileTextLabel = new JLabel("Groovy File Name: ");
            groovyFileTextBox = new JTextField(editingClassName);
            groovyFileTextBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                  editingClassName = groovyFileTextBox.getText();
                   String updatedStatusText = prepareStatusText();
                  statusAreaTop.setText(updatedStatusText);
                }
            });
           
            JLabel  systemOrderLabel = new JLabel("System order: ");
            systemOrderText = new JTextField(String.valueOf(systemOrder));
            systemOrderText.addActionListener(new editSystemOrder());
            JPanel  groovyFilePanel = new JPanel();
            groovyFilePanel.add(groovyFileTextLabel);
            groovyFilePanel.add(groovyFileTextBox);
            JPanel systemOrderPanel = new JPanel();
            systemOrderPanel.add(systemOrderLabel);
            systemOrderPanel.add(systemOrderText);
         
            paramMethodPanel = new JPanel();
            paramMethodPanel.add(ODEselectMethodLabel);
            paramMethodPanel.add(ODEselectMethodComboBox);
            paramPanel.add(paramMethodPanel);
            paramPanel.add(groovyFilePanel);
            paramPanel.add(systemOrderPanel);
            currentlySelectedLabel = new JLabel("Selected Method: "+(String)availODEMethods.get(ODESolveMethod));
            paramPanel.add(currentlySelectedLabel);
            
            statusPanel = new JPanel(new GridLayout(2,1));
            statusAreaTop = new JTextArea();
            statusAreaTop.setFont(new Font("Arial", Font.BOLD, 12));
            String statusText = prepareStatusText();
          
            statusAreaTop.setText(statusText);
            statusAreaBottom = new JTextArea();
            statusPanel.add(statusAreaTop);
            statusPanel.add(statusAreaBottom);
            
            
            templateTextArea = new JTextArea();
            updateTemplateText();
            
            templateTextArea.setFont(new Font("Arial", Font.ITALIC, 12));
            templateTextArea.setText(templateText);
            templateScrollPane = new JScrollPane();
            templateViewPort = templateScrollPane.getViewport();
            templateViewPort.add(templateTextArea);
	 
            ODEWizardTextArea = new JTextArea();
            ODEWizardText = "";
            ODEWizardTextArea.setText(ODEWizardText);
            ODEWizardTextArea.setFont(new Font("Arial", Font.BOLD, 12));
 
            ODEWizardScrollPane = new JScrollPane(ODEWizardTextArea);
            
            editPanel.add(ODEWizardScrollPane);
            editPanel.add(templateScrollPane);
                  
         // Step 1: copy template of ODE implementation from the
         // templateTextArea to ODEWizardTextArea
         copyTemplateButton.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
           ODEWizardTextArea.setText(templateTextArea.getText());
           generateEditingButton.setEnabled(true);    
           statusAreaBottom.setText("Step2:  If you have implemented correctly your ODE, the wizard completes the ready to compile Java class");
             }
          }                 
         );
         
         // Step 2: generate Groovy Class from template 
         JPanel buttonPanel = new JPanel();
         generateEditingButton.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
                 
           String isTyped = "";
           if (typedCode)
               isTyped = "\n@groovy.transform.CompileStatic\n";
           String editingODE = ODEWizardTextArea.getText();
            classImplementationString =  
            isTyped+
                    
            "class "+editingClassName+" extends Object \n             implements "+implementingInterfaces[ODESolveMethod]+
                   " \n \n "+ "{ \n";
           if (ODESolveMethod==ODErk4na)
               classImplementationString += "\n static int cnt=0; \n" +
                       "static  double  closenessToZeroPrecision = 0.001; \n static int LimitNumEvaluations = 50000; \n";
           
           classImplementationString += ( editingODE+"}\n");
             
           ODEWizardTextArea.setText(classImplementationString);
           statusAreaBottom.setText("Step3:  The generated Java source is ready, you can check it, and then proceed to save.");
         
           generateScriptCodeButton.setEnabled(true);
             }
         });
         
                 
         // Step 5: Generate Script Code
         JPanel generateScriptCodePanel = new JPanel();
         generateScriptCodeButton.addActionListener( 
           new  ActionListener() {
              public void actionPerformed(ActionEvent e) {
         new scriptCodeGenerationFrameGroovySci("ODE Script code", editingClassName, systemOrder);
             } 
         });
            
          
         buttonPanel.add(copyTemplateButton);  // Step 1
         buttonPanel.setEnabled(true);
         buttonPanel.add(generateEditingButton);  // Step 2
         generateEditingButton.setEnabled(false);
         buttonPanel.add(generateScriptCodeButton); // Step 5
         generateScriptCodeButton.setEnabled(false);
         
         ODEWizardFrame.add(buttonPanel, BorderLayout.NORTH);
         ODEWizardFrame.add(editPanel, BorderLayout.CENTER);
         JPanel bottomPanel = new JPanel(new GridLayout(2,1));
         
         bottomPanel.add(paramPanel);
         bottomPanel.add(statusPanel);
         ODEWizardFrame.add(bottomPanel, BorderLayout.SOUTH);
         ODEWizardFrame.setLocation(100, 100);
         ODEWizardFrame.setSize(1400, 800);
         ODEWizardFrame.setVisible(true); 
         
           }
         
         private void updateTemplateText() {
                 switch (ODESolveMethod) {
                case   ODErke:                 
                        templateText = templateTextRKE;
                        break;
                case ODErk4na:  
                        templateText = templateTextRK4na;
                        break;
                case ODEmultistep:
                         templateText = templateTextMultistep;
                         break;
                 case ODEdiffsys:
                         templateText = templateTextDiffsys;
                         break;
                default:
                        templateText = templateTextRKE;
                        break;
                }
         }
         
         private String prepareStatusText() {
              String statusText = "Groovy Class Name to generate: "+"\""+editingClassName+"\""+
               ",   ODE System Order = "+systemOrder;
              return statusText;
          }  
         
         
         class editSystemOrder  implements ActionListener  {
             public void actionPerformed(ActionEvent e)  {
                  JTextField  fieldEdited = (JTextField) e.getSource();
                  try {
                  systemOrder = Integer.parseInt(fieldEdited.getText());
                  }
                  catch (NumberFormatException nfex) {
                      System.out.println("Number format exception in getting system order");
                      nfex.printStackTrace();
                  }

                  String updatedStatusText = prepareStatusText();
                  statusAreaTop.setText(updatedStatusText);
                }
           }
         
         class editJavaClassFileName   implements ActionListener  {
             public void actionPerformed(ActionEvent e)  {
                  JTextField  fieldEdited = (JTextField) e.getSource();
                  try {
                editingClassName = fieldEdited.getText();
                  }
                  catch (NullPointerException npex) {
                      System.out.println("Null pointer exception in getting Groovy class name");
                      npex.printStackTrace();
                  }

                  String updatedStatusText = prepareStatusText();
                  statusAreaTop.setText(updatedStatusText);
                }
           }
  
     }
     
     class scriptCodeGenerationFrameGroovySci extends JFrame  {
                static JPanel  scriptPanel;
                
                static JTextArea scriptTextArea;
                static JScrollPane scriptScrollPane;
                static JViewport scriptViewPort;
                static String scriptText;
                static String editingClassName;
                static int systemOrder;
                static double xStart = 0.0;  static double xEnd = 40;  // some values
           
                JLabel xStartLabel; 
                JTextField  xStartField;
                JLabel xEndLabel;
                JTextField xEndField;
             
         scriptCodeGenerationFrameGroovySci(String title,  String _editingClassName, int _systemOrder) {
             super(title);
             GridBagLayout layout = new GridBagLayout();
             setLayout(layout);
             editingClassName = _editingClassName;
             systemOrder = _systemOrder;
             getParamsForScript();
         }
         
         public void getParamsForScript() {
             scriptTextArea = new JTextArea();
             JScrollPane scriptScrollPane = new JScrollPane();
             JPanel  scriptTextPane = new JPanel();
             scriptTextPane.add(scriptScrollPane);
             
             xStartLabel = new JLabel("Start Value");
             xStartField = new JTextField(String.valueOf(xStart));
             xStartField.addActionListener(new ActionListener() {
                 public void actionPerformed(ActionEvent e) {
                try  {
                     xStart = Double.valueOf(xStartField.getText());
                }
                catch (NumberFormatException nfe) {
                    System.out.println("Number format exception for xStart value");
                    nfe.printStackTrace();
                   }
                 }
             });
             
             xEndLabel = new JLabel("End Value");
             xEndField = new JTextField(String.valueOf(xEnd));
             xEndField.addActionListener(new ActionListener() {
                 public void actionPerformed(ActionEvent e) {
                try  {
                     xEnd = Double.valueOf(xEndField.getText());
                }
                catch (NumberFormatException nfe) {
                    System.out.println("Number format exception for xEnd value");
                    nfe.printStackTrace();
                   }
                 }
             });
             
             JButton proceedButton = new JButton("Proceed to script ");
             proceedButton.addActionListener(new ActionListener() {
                 public void actionPerformed(ActionEvent e) {
                     xStart = Double.valueOf(xStartField.getText());
                     xEnd = Double.valueOf(xEndField.getText());
                     scriptCodeGenerationFrameGroovySci.makeGroovySciCodeFromParams(ODEWizardGroovySci.ODESolveMethod);
                    };
             });
             
             JPanel startParamsPanel = new JPanel(new GridLayout(1,2));
             startParamsPanel.add(xStartLabel);   startParamsPanel.add(xStartField); 
             JPanel endParamsPanel = new JPanel(new GridLayout(1,2));
             endParamsPanel.add(xEndLabel);   endParamsPanel.add(xEndField); 
             JPanel proceedPanel = new JPanel();
             proceedPanel.add(proceedButton);
             setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
             add(startParamsPanel);  add(endParamsPanel); add(proceedPanel);
             setLocation(100, 100);
             pack();
             setVisible(true);
         
         }
         
           public static void makeGroovySciCodeFromParams(int odeSolveMethod)   {
               switch  (odeSolveMethod)  {
                   case  ODEWizardGroovySci.ODErke:
                scriptText = " import numal.*; \n import java.util.Vector;  \n import groovySci.math.array.*;  \n"+
                        " import static groovySci.math.array.Matrix.*;  \n import static groovySci.math.plot.plot.*;  \n"+
                        " import gExec.Interpreter.GlobalValues\n\n";
                
                scriptText += ODEWizardGroovySciGroovy.classImplementationString;
                
                scriptText +=          " \n n= "+systemOrder+";  // the number of equations of the system\n"+
                    "x = new double [1];   // entry:   x[0] is the initial value of the independent variable  \n"+
                    "xe = new double[1];   //  entry:  xe[0] is the final value of the independent variable    \n"+
                    "y = new double[n+1];   // entry: the dependent variable, the initial values at x = x0 \n"+
                    "data = new double[7];   // in array data one should give: \n"+
                    "                                        //     data[1]:   the relative tolerance \n"+
                    "                                        //     data[2]:  the absolute tolerance  \n"+
                    "                                        //  after each step data[3:6] contains:  \n"+
                    "                                        //      data[3]:  the steplength used for the last step \n"+
                    "                                       //      data[4]:  the number of integration steps performed \n"+
                    "                                       //      data[5]:  the number of integration steps rejected  \n "+
                    "                                       //      data[6]:  the number of integration steps skipped \n"+
                    "                                 // if upon completion of rke data[6] > 0, then results should be considered most criticallly \n"+
                    " fi = true;                        // if fi is true then the integration starts at x0 with a trial step xe-x0;  \n"+
                    "                                        // if fi is false then the integration is continued with a step length data[3]* sign(xe-x0) \n"+
                    "data[1]=data[2]=1.0e-6; \n\n"+
                    "xOut = new Vector();   yOut = new Vector();  \n"+
    "// a Java class that implements the AP_rke_methods interface should be specified \n"+
    "// The AP_rke_methods interface requires the implementation of two procedures: \n"+
    "//    void der(int n, double t, double v[]) \n"+
    "//              this procedure performs an evaluation of the right-hand side of the syste4m with dependent variable v[1:n] and \n"+
    "//              and independent variable t; upon completion of der the right-hand side should be overwritten on v[1:n] \n"+
    "//    void out(int n, double x[], double xe[], double y[], double data[]) \n"+
    "//              after each integration step performed, out can be used to obtain information from the solution process, \n"+
    "//              e.g., the values of x, y[1:n], and data[3:6]; out can also be used to update data, but x and xe remain unchanged \n\n";
                    
                        
         
                scriptText +=    "\n xStart = "+xStart+";"+
                        "\n xEnd =  "+xEnd+";   // start and end values of integration \n \n";  
                for (int k=1; k<=systemOrder; k++) 
                    scriptText += "y["+k+"] = "+Math.random()+"; \n";
                
                scriptText +=  " x[0] = xStart; \n  xe[0] = xEnd; \n"; 
                scriptText += "groovyClassName  = "+"\""+ editingClassName+"\""+";   // name of the Groovy class that implements the ODE \n";

                scriptText += "long start = System.currentTimeMillis(); \n";
                scriptText +=   "\n  invocationObject = Class.forName(groovyClassName, false, gExec.Interpreter.GlobalValues.GroovyShell.getClassLoader()).newInstance(); \n"+
        "\nAnalytic_problems.rke(x, xe, n, y, invocationObject,  data, fi,  xOut, yOut); \n"+
"\nend =  System.currentTimeMillis(); \n"+
"runTime = end-start; \n\n"+
"lorenzOut = new Matrix(n, yOut); \n"+
"plot(lorenzOut.getRowRef(1), lorenzOut.getRowRef(2), lorenzOut.getRowRef(3), \"Time: \"+runTime); \n";
               
                    break;
                    
                   case ODEWizardGroovySci.ODErk4na:
                 scriptText = "fName = "+"\""+editingClassName+"\""+";"+
                         "\n n = "+systemOrder+";"+"for (k=1; k<="+systemOrder+";k++)  xa(k)= "+Math.random()+"; \n"+
                         "\n continueFlag = \"true\"; \n"+
                         "\n\n sysOut = jrk4na(xa, fName, n, continueFlag); \n"+
                         "\n \n x= sysOut(1,:); \n y = sysOut(2,:); \n z = sysOut(3,:); \n \n plot3(x,y,z);   \n"; 
                     break;
                 
                   case ODEWizardGroovySci.ODEmultistep:
             scriptText = " import numal.*; \n import java.util.Vector;  \n import groovySci.math.array.*;  \n"+
                        " import static groovySci.math.array.Matrix.*;  \n import static groovySci.math.plot.plot.*;  \n"+
                        " import gExec.Interpreter.BasicCommands.*; \n\n";
             
             scriptText += "\n n= "+systemOrder+"; // the number of equations of the system \n"+
               "first = new boolean[1];   // if first is true then the procedure starts the integration with a first order Adams method \n"+
                            "// and a steplength equal to hmin,  upon completion of a call, first is set to false \n"+
            "first[0]=true; \n"+
            "btmp = new boolean[2]; \n"+
            "itmp = new int[3]; \n"+
            "xtmp = new double[7]; \n"+
            "x = new double[1]; \n"+
            "y = new double[6*n+1]; \n"+
            "ymax = new double[n+1]; \n"+
            "save = new double[6*n+39];  //    in this array the procedure stores information which can be used in a continuing call \n"+
    "                   // with first = false; also the following messages are delivered: \n"+
    "                   //      save[38] == 0;  an Adams method has been used  \n"+
    "                   //      save[38] == 1;  the procedure switched to Gear's method \n"+
    "                   //      save[37] == 0;  no error message  \n"+
    "                   //      save[37] == 1; with the hmin specified the procedure cannot handle the nonlinearity (decrease hmin!) \n"+
    "                   //      save[36] ;  number of times that the requested local error bound was exceeded   \n"+
    "                   //      save[35] ;  if save[36] is nonzero then save[35] gives an estimate of the maximal local error bound, otherwise save[35]=0 \n\n"+
    "jac = new double[n+1][n+1];  \n"+
    "xout = new Vector();  \n"+
    "yout = new Vector();  \n\n"+
    "hmin=1.0e-10;  \n"+
    "eps=1.0e-9; \n"+
    "x[0]=0.0;  \n";
                        
  for (int k=1; k<=systemOrder; k++) 
         scriptText += "y["+k+"] = "+Math.random()+"; \n";
  for (int k=1; k<=systemOrder; k++) 
         scriptText += "ymax["+k+"] = 0.00001; \n";
                
 scriptText += "prompt = \"Specify the upper bound for integration\";  \n"+
                            "xEnd= "+xEnd+";   // initial end point of integration  \n"+
           "groovyClassName = \"gExec.Functions.Chaotic.LorenzMultiStep\"; \n"+
            "\nstartTime =  System.currentTimeMillis(); \n"+
            "AP_multistep_methods  invocationObject = (AP_multistep_methods) Class.forName(groovyClassName, false, GlobalValues.extensionClassLoader).newInstance(); \n"+
            "Analytic_problems.multistep(x,xEnd,y,hmin,5,ymax,eps,first, save,invocationObject, jac, false,n,btmp,itmp,xtmp, xout, yout); \n"+
            "\nendTime =  System.currentTimeMillis(); \n"+
            "runTime =  endTime-startTime; \n"+
            "multiStepOut = new Matrix(n, yout); \n"+
            "plot3(multiStepOut, \"Time: \"+runTime); ";

             
             
             break;     
                   
                   case  ODEWizardGroovySci.ODEdiffsys:
                scriptText = " import numal.*; \n import java.util.Vector;  \n import groovySci.math.array.*;  \n"+
                        " import static groovySci.math.array.Matrix.*;  \n import static groovySci.math.plot.plot.*;  \n"+
                        " import gExec.Interpreter.GlobalValues;\n import static groovySciCommands.BasicCommands.*;\n"+
                        "\n n= "+systemOrder+"; // the number of equations of the system \n"+
                         "x = new double[1];   // entry:   x[0] is the initial value of the independent variable  \n"+
                         "y = new double[n+1];   // entry: the dependent variable, the initial values at x = x0 \n"+
                         "tol =  0.0000000000004;  \n"+
                         "aeta = tol;  // aeta: required absolute precision in the integration process \n"+
                         "reta = tol; // reta: required relative precision in the integration process \n"+
                         "s = new double[n+1];\n"+
                         "for ( k in 0..n)  s[k]=0.0; \n\n"+
                         "h0=0.000001;  // h0: the initial step to be taken \n\n"+
                        "Vector xOut = new Vector(); \n Vector yOut = new Vector(); \n\n"+      
                        "groovyClassName = "+"\""+editingClassName+"\""+";"+
                        "\n x[0] = "+xStart+";"+
                        "\n xe =  "+xEnd+"; \n\n";
                
                for (int k=1; k<=systemOrder; k++) 
                    scriptText += "y["+k+"] = "+Math.random()+"; \n";
                
                scriptText = scriptText+ 
                        "AP_diffsys_methods  invocationObject = (AP_diffsys_methods) Class.forName(groovyClassName, false, GlobalValues.extensionClassLoader).newInstance(); \n"+
                        "tic(); \n"+
                        "Analytic_problems.diffsys(x,xe, n, y, invocationObject, aeta, reta , s, h0, xOut, yOut); \n"+
                        "timeCompute = toc(); \n"+
                       "plotTitle = \"Double Scroll attractor with Java code, time \"+timeCompute; \n"+
                       "diffSysOut = new Matrix(n, yOut); \n"+
                        "plot3(diffSysOut, plotTitle);   \n"+
                        "diffSysOut = new Matrix(n, yOut); \n"+
                        "plot3(diffSysOut, \"Time: \"+timeCompute); \n";
                  break;
                         
                        default:
                  scriptText = "";
               };
                       
                scriptTextArea.setRows(30);
                scriptTextArea.setFont(new Font("TimesNewRoman", Font.PLAIN, 11));
                scriptTextArea.setText(scriptText);
                
                scriptPanel = new JPanel();
                scriptScrollPane = new JScrollPane(scriptTextArea);
                scriptPanel.add(scriptScrollPane);
                
                JButton scriptSaveButton = new JButton("Save Script Code");
                scriptSaveButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                      String currentWorkingDirectory = GlobalValues.workingDir;       
                      JFileChooser chooser = new JFileChooser(currentWorkingDirectory);
                      chooser.setSelectedFile(new File(editingClassName+".gsci"));
                      int ret = chooser.showSaveDialog(ODEWizardGroovySci.scriptFrame);

                     if (ret != JFileChooser.APPROVE_OPTION) {         return;      }
                     File f = chooser.getSelectedFile();
                        try {
                            PrintWriter out = new PrintWriter(f);
                            String  scriptCodeText = scriptTextArea.getText();
                            out.write(scriptCodeText);
                            out.close();
                        }
                        catch (java.io.FileNotFoundException enf) {
                              System.out.println("File "+f.getName()+ " not found");
                              enf.printStackTrace();
                      }
                 }  
                });
                        
                JButton scriptRunButton = new JButton("Run Script Code (INDY should be false)");
                scriptRunButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                  GlobalValues.jLabConsole.append(scriptText+"\n");
                    }
                }  );
                JPanel buttonsPanel = new JPanel();
                buttonsPanel.add(scriptSaveButton);
                buttonsPanel.add(scriptRunButton);        
                
                ODEWizardGroovySci.scriptFrame = new JFrame("Your script code");
                ODEWizardGroovySci.scriptFrame.setLayout(new BorderLayout());
                ODEWizardGroovySci.scriptFrame.add(scriptPanel,  BorderLayout.CENTER);
                ODEWizardGroovySci.scriptFrame.add(buttonsPanel, BorderLayout.SOUTH);
                ODEWizardGroovySci.scriptFrame.setSize(GlobalValues.figFrameSizeX, GlobalValues.figFrameSizeY);
                ODEWizardGroovySci.scriptFrame.setVisible(true);
         }      
           
           
           
          
     } 
                 
          