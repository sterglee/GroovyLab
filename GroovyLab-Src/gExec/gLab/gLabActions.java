
package gExec.gLab;

import java.awt.event.*;
import javax.swing.*;
import java.io.*;



import gLabEdit.*;

import gExec.Interpreter.GlobalValues;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.Properties;
import java.util.Vector;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


   class autoCompletionHelpAction extends AbstractAction {
       autoCompletionHelpAction() {
           super("AutoCompletion with regular expression matching-- F3  (JScript-GroovySci), F11 (Java)");
       }    
     public void actionPerformed(ActionEvent e)  {
         
                 JFrame  regExpFrame = new JFrame("Help on Regular Expression based autocompletion");
                 regExpFrame.setSize(400, 300);
                 regExpFrame.setLayout(new BorderLayout());
                 
                JTextArea  regExpArea = new JTextArea();
                regExpArea.setFont(GlobalValues.defaultTextFont);
                regExpArea.setText("Examples of regular expression based matching: \n"+
 "1.   p.*             // All commands that start with p \n"+
 "2.   [ap].*         // All commands that start with a or p \n"+
 "3.   [^a].*         // All commands that do not start with a \n"+
 "4.   .*cos.*        // All commands that include \"cos\" somewhere in their description \n"+
 "5.   [b-e].*        // All commands that start with a letter at the range b-e (i.e. b,c,d,e) \n+" +
 "6.   [B-De-h].*     // All commands that start with B-D (i.e. B, C, D) or e-h (i.e. e,f,g,h) \n"+
 "7.   [^A-Z^a-e].*  // All commands that do not start with A-Z and also not with a-e\n"+
 "8.   [^A-Z&&[^a]].*   // not with A-Z and not with a \n"+
 "9.  ^p.*           // p at the start \n"+
 "10.  [^a-c].*     // first character not in  [a-c] \n"+
 "11.   *.component  // last word is 'component'\n"
                        );
                
                regExpFrame.add(regExpArea);
                regExpFrame.setVisible(true);
               }
    }
   
   


   class groovySciExamplesJTreeAction extends AbstractAction {
       groovySciExamplesJTreeAction() {
           super("groovySci Examples with JTree displaying");
       }

    public void actionPerformed(ActionEvent e) {
        
   gExec.gui.watchExamples weObj = new gExec.gui.watchExamples();
   weObj.scanMainJarFile(".gsci");
   weObj.displayExamples("GroovySci Examples");
   }
   }



   class groovySciPlotExamplesJTreeAction extends AbstractAction {
       groovySciPlotExamplesJTreeAction() {
           super("groovySci Plot Examples with JTree displaying");
       }

    public void actionPerformed(ActionEvent e) {
        
   gExec.gui.watchExamples weObj = new gExec.gui.watchExamples();
   weObj.scanMainJarFile(".plots-gsci");
   weObj.displayExamples("GroovySci Plot Examples");
   }
   }

   class GroovySciExamplesAction extends AbstractAction {
       GroovySciExamplesAction() {
           super("GroovySci Examples");
       }

    public void actionPerformed(ActionEvent e) {
        String jLabJarFile = GlobalValues.jarFilePath;
        
        gExec.gLab.ProcessGroovySciInJar  processGroovySciInJar = new gExec.gLab.ProcessGroovySciInJar(jLabJarFile);
    }
   }
   
   
   class GroovySciPlotsExamplesAction extends AbstractAction {
       GroovySciPlotsExamplesAction() {
           super("GroovySci Plot Examples");
       }

    public void actionPerformed(ActionEvent e) {
        String jLabJarFile = GlobalValues.jarFilePath;
        
        gExec.gLab.ProcessGroovySciPlotsInJar   processGroovySciPlotsInJar = new gExec.gLab.ProcessGroovySciPlotsInJar(jLabJarFile);
    }
   }
   
      
     class JavaSGTExamplesPlottingAction extends AbstractAction {
       JavaSGTExamplesPlottingAction() {
           super("Scientific Graphics Toolbox Plotting Examples");
       }

    public void actionPerformed(ActionEvent e) {
        String gLabJarFile = GlobalValues.jarFilePath;
        
        gExec.gLab.ProcessPlottingSGTExamplesInJar   processPlottingInJar = new gExec.gLab.ProcessPlottingSGTExamplesInJar(gLabJarFile);
    }
   }
   class GroovySciWaveletExamplesAction extends AbstractAction {
       GroovySciWaveletExamplesAction() {
           super("GroovySci  Wavelet Examples");
       }

    public void actionPerformed(ActionEvent e) {
        String jLabJarFile = GlobalValues.jarFilePath;
        
        gExec.gLab.ProcessGroovySciWaveletsInJar  processGroovySciWaveletInJar = new gExec.gLab.ProcessGroovySciWaveletsInJar(jLabJarFile);
    }
   }
   
   class GroovySciWEKAExamplesAction extends AbstractAction {
       GroovySciWEKAExamplesAction() {
           super("GroovySci  WEKA Examples");
       }

    public void actionPerformed(ActionEvent e) {
        String jLabJarFile = GlobalValues.jarFilePath;
        
        gExec.gLab.ProcessGroovySciWEKAInJar   processGroovySciWEKAInJar = new gExec.gLab.ProcessGroovySciWEKAInJar(jLabJarFile);
    }
   }
   
   
     
   
   
   
   
   
   
   class ODEWizardGroovySciAction extends AbstractAction {
       ODEWizardGroovySciAction() { super("ODE Wizard GroovySci, Java implementation"); }
       public void actionPerformed(ActionEvent e) {
      SwingUtilities.invokeLater(new Runnable() {
           public void run() {  // run in  */
                           gExec.Wizards.ODEWizardGroovySci  odeWizardGroovySci  = new gExec.Wizards.ODEWizardGroovySci();
           }
                     });
                }
   }
   
    class ODEWizardGroovySciGroovyAction extends AbstractAction {
       ODEWizardGroovySciGroovyAction() { super("ODE Wizard GroovySci, all Groovy"); }
       public void actionPerformed(ActionEvent e) {
      SwingUtilities.invokeLater(new Runnable() {
           public void run() {  // run in  */
                           gExec.Wizards.ODEWizardGroovySciGroovy  odeWizardGroovySciGroovy = new gExec.Wizards.ODEWizardGroovySciGroovy(false);
           }
                     });
                }
   }
   

    class ODEWizardGroovySciGroovyActionTypedCode extends AbstractAction {
       ODEWizardGroovySciGroovyActionTypedCode() { super("ODE Wizard GroovySci, all Groovy, Compile Static"); }
       public void actionPerformed(ActionEvent e) {
      SwingUtilities.invokeLater(new Runnable() {
           public void run() {  // run in  */
                           gExec.Wizards.ODEWizardGroovySciGroovy  odeWizardGroovySciGroovy = new gExec.Wizards.ODEWizardGroovySciGroovy(true);
           }
                     });
                }
   }
 
    

   class editAction extends AbstractAction {
       public editAction()  { super("GroovyLab Editor");}
       public void actionPerformed(ActionEvent e) {
                           SwingUtilities.invokeLater(new Runnable() {
           public void run() {  // run in  */
                   GlobalValues.myGEdit = new  gLabEditor(GlobalValues.workingDir+File.separatorChar+"Untitled");
           }
                     });
                    }
       }
   
   
   
   
      class jeditAction extends AbstractAction {
       public jeditAction()  { super("jEdit Editor ");}
       public void actionPerformed(ActionEvent e) {
               String [] command  = new String[6];
               command[0] =  "java";
// classpath to the jedit seems that is not used, but in any case it  is not harmful
               String  jeditClassPath =  "-classpath";
               jeditClassPath +=  " "+GlobalValues.jarFilePath+File.pathSeparator;
                       
               command[1] = "-cp";
               command[2] = jeditClassPath;
               
               command[3] = "-jar";
               String jeditPath = GlobalValues.gLabLibPath+ "4.3.2"+File.separator+"jedit.jar";
               command[4] =   jeditPath;
               
           
               
               String fileName = "Untitled.groovy";
               command[5] = fileName;
               
            String jEditcommandString = command[0]+"  "+command[1]+"  "+command[2]+" "+command[3];
            System.out.println("jEditCommandString = "+jEditcommandString); 
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

                } catch (IOException exio) {
                    System.out.println("IOException trying to executing "+command);
                    exio.printStackTrace();

                }
               
           }
                     
                    }
                
   

             
   class openProjectAction extends AbstractAction {
      public  openProjectAction() { super("Open GroovySci Project"); }
    @Override
       public void actionPerformed(ActionEvent e) {
           SwingUtilities.invokeLater(new Runnable() {
           
               public void run() {
    
                   Properties settings = new Properties();  // for load/save global properties
    
                   boolean foundConfigFileFlag = false;
                   
                    String [] extensions = {"gsciProps", "props"};
//                    weka.gui.ExtensionFileFilter  groovySciPrFilter = new weka.gui.ExtensionFileFilter(extensions, "GroovySci Project Files (\".props\", \".gsciProps\")");
                    JFileChooser  chooser = new JFileChooser(GlobalValues.workingDir);
  //                  chooser.setFileFilter(groovySciPrFilter);
                   
                    chooser.setDialogTitle("Specify the GroovySci project properties configuration file");
                    
                    
                     int retVal = chooser.showOpenDialog(GlobalValues.gLabMainFrame);
                    if (retVal == JFileChooser.APPROVE_OPTION) { 
                            File selectedFile = chooser.getSelectedFile();
                            String projectPropsFileName = selectedFile.getAbsolutePath();
                            FileInputStream in = null;
                            File configFile = new File(projectPropsFileName);
               if (configFile.exists())   {
                    try {
                        in = new FileInputStream(configFile);
                    } catch (FileNotFoundException ex) {
                        System.out.println("Cannot open file "+configFile+" for open Project, exception "+ex.getMessage());
                    }
                    
                  foundConfigFileFlag = true;
                 }
            
             if (foundConfigFileFlag==true)  { // load configuration info
                    try {
                        // load configuration info
                        settings.load(in);
                    } catch (IOException ex) {
                        System.out.println("Failing to read settings from "+configFile+" for open Project, exception "+ex.getMessage());
                    }
                    try {
                        in.close();
                    } catch (IOException ex) {
                        System.out.println("Failing to close "+configFile+" for open Project, exception "+ex.getMessage());
                    }
                    
                    GlobalValues.passPropertiesFromSettingsToWorkspace(settings);
                    
                        gExec.gLab.gLab.updateTree();
                        gExec.gLab.gLab.outerPane.revalidate();
              }
            }
           
                  }
               });
           };
       }
   
   
   
    class newProjectAction extends AbstractAction {
       newProjectAction() { super("New GroovySci Project"); }
       public void actionPerformed(ActionEvent e) {
           SwingUtilities.invokeLater(new Runnable() {
               public void run() {
       JFrame newProjectFrame = new JFrame("New GroovySci Frame Wizard");
       JPanel newProjectPanel  = new JPanel();
       JPanel projectRootPanel = new JPanel(new GridLayout(1, 3));
       JTextField newProjectField = new JTextField(30);
       JLabel newProjectLabel = new JLabel("GroovySci root folder: ");
       JButton newProjectBrowse = new JButton("Browse");
       projectRootPanel.add(newProjectLabel);
       projectRootPanel.add(newProjectField);
       projectRootPanel.add(newProjectBrowse);
       newProjectPanel.add(projectRootPanel);
       newProjectFrame.add(newProjectPanel);
       newProjectFrame.pack();
       newProjectFrame.setVisible(true);
               }
               });
           };
       }
   
    
   class saveProjectAction extends AbstractAction {
       saveProjectAction() { super("Save GroovySci Project"); }
    @Override
       public void actionPerformed(ActionEvent e) {
           SwingUtilities.invokeLater(new Runnable() {
           
               public void run() {
    
                   Properties settings = new Properties();  // for load/save global properties
    
                   GlobalValues.passPropertiesFromWorkspaceToSettings(settings);  // pass the current properties
                   
                   boolean foundConfigFileFlag = false;
                   
                    String [] extensions = {"gsciProps", "props"};
                    //weka.gui.ExtensionFileFilter  groovySciPrFilter = new weka.gui.ExtensionFileFilter(extensions, "GroovySci Project Files (\".props\", \".gsciProps\")");
                    JFileChooser  chooser = new JFileChooser(GlobalValues.workingDir);
                    //chooser.setFileFilter(groovySciPrFilter);
                   
                    chooser.setDialogTitle("Specify the GroovySci project properties configuration file");
                    
                    
                     int retVal = chooser.showSaveDialog(GlobalValues.gLabMainFrame);
                    if (retVal == JFileChooser.APPROVE_OPTION) { 
                            File selectedFile = chooser.getSelectedFile();
                            String projectPropsFileName = selectedFile.getAbsolutePath();
                            FileOutputStream out = null;
                            File configFile = new File(projectPropsFileName);
                    try {
                        out = new FileOutputStream(configFile);
                    } catch (FileNotFoundException ex) {
                        System.out.println("Cannot open file "+configFile+" for saving Project, exception "+ex.getMessage());
                    }
                    
                    try {
                        // save configuration info
                        settings.store(out, "");
                    } catch (IOException ex) {
                        System.out.println("Failing to save settings to "+configFile+" for saving Project, exception "+ex.getMessage());
                    }
                    try {
                        out.close();
                    } catch (IOException ex) {
                        System.out.println("Failing to close "+configFile+" for saving  Project, exception "+ex.getMessage());
                    }
                    
                    
              }
            }
               });
           };
       }
   
   class closeProjectAction extends AbstractAction {
       closeProjectAction() { super("Close GrovySci Project"); }
       public void actionPerformed(ActionEvent e) {
           SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                  }
               });
           };
   }
           

   
   class groovyLabServerAction extends AbstractAction {
       groovyLabServerAction() { super("Control IP of GroovyLab server"); }
       public void actionPerformed(ActionEvent e) {
           SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                   GlobalValues.serverIP =
                           JOptionPane.showInputDialog(null,  "Specify IP of GroovyLab server", GlobalValues.serverIP);
                   GlobalValues.settings.setProperty("serverIPProp", GlobalValues.serverIP);
                           
                  }
               });
           };
   }
           

    // this handler removes the selected loaded toolbox
    class MouseAdapterForGSILoadedToolboxes extends  MouseAdapter {
             JPopupMenu  gsciToolboxesPanelPopupMenu;
             public void mouseClicked(MouseEvent evt) {
                 if (evt.getClickCount() == 2) {
                 
               System.out.println("Double Click");

                 }
             }

           public void mousePressed(MouseEvent e) {   
               
                  gsciToolboxesPanelPopupMenu  = new JPopupMenu(); 
                 JMenuItem removeLocalItem = new JMenuItem("Remove loaded toolbox");
    
                 removeLocalItem.addActionListener(new ActionListener() {
   @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = groovySciToolboxes.selectedLoadedToolboxIndex;   // the index of the selected toolbox from the loaded toolboxes list
                int toolboxesListSize = groovySciToolboxes.groovySciToolboxesLoadedListModel.getSize();
                if (selectedIndex >=0 && toolboxesListSize > 0)   {  // currently exist a selected toolbox for removal 
                    
                String toolboxName = (String) groovySciToolboxes.groovySciToolboxesLoadedListModel.getElementAt(selectedIndex);
                groovySciToolboxes.groovySciToolboxesLoadedListModel.removeElementAt(selectedIndex);
                
        if (groovySciToolboxes.gsciToolboxes != null)       {  // already loaded toolboxes 
            int loadedToolboxesCnt = groovySciToolboxes.gsciToolboxes.size();
            
            System.out.println("loadedToolboxesCnt = "+loadedToolboxesCnt); 
            
        // remove the toolbox from the groovySciToolboxes.gsciToolboxes table that keeps 
        // the name of each loaded toolbox and a Vector of the loaded classes    
            for (int toolboxIdx=0; toolboxIdx<loadedToolboxesCnt; toolboxIdx++)  {  // search for the selected toolbox
               groovySciToolbox currentToolbox = groovySciToolboxes.gsciToolboxes.get(toolboxIdx);    
               String currentToolboxName = currentToolbox.toolboxName;   // the name of the current toolbox
               if (currentToolboxName.equalsIgnoreCase(toolboxName) == true)   
                     {    // selected toolbox detected at the table, so remove it
               System.out.println("removing loaded toolbox : "+toolboxIdx+"  with name : "+currentToolboxName);
               groovySciToolboxes.gsciToolboxes.remove(toolboxIdx);      // remove the Vector entry that keeps the toolbox classes
               break;
               }
            }    // search for the selected toolbox  at groovySciToolboxes.gsciToolboxes table
            
        // remove the frame  of the toolbox classes if one exists
            JFrame  toolboxFrame = groovySciToolboxes.framesOfToolboxClasses.get(toolboxName);
            if (toolboxFrame != null) {
                toolboxFrame.dispose();   // destroy the window displaying the toolbox classes
                groovySciToolboxes.framesOfToolboxClasses.remove(toolboxName);
            }
          }   // already loaded toolboxes
                
                 int jarToolboxesCnt = GlobalValues.jartoolboxesForGroovySci.size();
            for (int toolboxIdx=0; toolboxIdx<jarToolboxesCnt; toolboxIdx++)  {  // search for the selected toolbox at the jar toolboxes for GroovySci table
               String currentToolboxName = (String) GlobalValues.jartoolboxesForGroovySci.elementAt(toolboxIdx);
               if (currentToolboxName.equalsIgnoreCase(toolboxName) == true)    {
                 System.out.println("removing jar toolbox entry from list,  toolboxId = "+toolboxIdx+", name = "+toolboxName);    
                 GlobalValues.jartoolboxesForGroovySci.removeElementAt(toolboxIdx);   
                 groovySciToolboxes.groovySciToolboxesLoadedListModel.removeElementAt(toolboxIdx);   
                 GlobalValues.jartoolboxesLoadedFlag.remove(toolboxName);   // remove the toolbox name in order to reinitialize the toolbox later
                 break;
               }
        }   // search for the selected toolbox  at the jar toolboxes for GroovySci table
      
                }  // currently exist a selected toolbox for removal 
   }  // actionPerformed
 });
                 gsciToolboxesPanelPopupMenu.add(removeLocalItem);
                 
                  
JMenuItem displayLocalItem = new JMenuItem("Display contents of loaded toolbox");
    
                 displayLocalItem.addActionListener(new ActionListener() {
   @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = groovySciToolboxes.selectedLoadedToolboxIndex;   // the index of the selected toolbox from the loaded toolboxes list
                int toolboxesListSize = groovySciToolboxes.groovySciToolboxesLoadedListModel.getSize();
                if (selectedIndex >=0 && toolboxesListSize > 0)   {  // currently exist a selected toolbox for removal 
                    
            String toolboxName = (String) groovySciToolboxes.groovySciToolboxesLoadedListModel.getElementAt(selectedIndex);
            Vector toolboxClasses    =  gExec.ClassLoaders.JarClassLoader.scanAll(toolboxName);
            gExec.gui.WatchClasses  watchClassesOfToolbox = new gExec.gui.WatchClasses();
            
        
            watchClassesOfToolbox.displayClasses( toolboxClasses, toolboxName, gExec.gui.WatchClasses.watchXLoc+50, gExec.gui.WatchClasses.watchYLoc+50);
            
           }
            }
           });
                 
                 gsciToolboxesPanelPopupMenu.add(displayLocalItem);
     
               if (e.isPopupTrigger()){  
               gsciToolboxesPanelPopupMenu.show((Component) e.getSource(), e.getX(), e.getY());
             }
           }
    
            public void mouseReleased(MouseEvent e) { 
                   if (e.isPopupTrigger()){
                gsciToolboxesPanelPopupMenu.show((Component) e.getSource(), e.getX(), e.getY());
             }       
   
          }
       }
       

    class MouseAdapterForGSIAvailableToolboxes extends  MouseAdapter {
             JPopupMenu  gsciToolboxesPanelPopupMenu;
             public void mouseClicked(MouseEvent evt) {
                 if (evt.getClickCount() == 2) {
                 
               System.out.println("Double Click");

                 }
             }

           public void mousePressed(MouseEvent e) {   
               
                  gsciToolboxesPanelPopupMenu  = new JPopupMenu(); 
                 JMenuItem removeLocalItem = new JMenuItem("Remove from the available list");
    
                 removeLocalItem.addActionListener(new ActionListener() {
   @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = groovySciToolboxes.selectedAvailableToolboxIndex;   // the index of the selected toolbox from the available toolboxes list
                if (selectedIndex >= 0)
                    groovySciToolboxes.groovySciToolboxesAvailableListModel.removeElementAt(selectedIndex);
        
   }  // actionPerformed
 });
                 gsciToolboxesPanelPopupMenu.add(removeLocalItem);
                 
               if (e.isPopupTrigger()){  
               gsciToolboxesPanelPopupMenu.show((Component) e.getSource(), e.getX(), e.getY());
             }
           }
    
            public void mouseReleased(MouseEvent e) { 
                   if (e.isPopupTrigger()){
                gsciToolboxesPanelPopupMenu.show((Component) e.getSource(), e.getX(), e.getY());
             }       
   
          }
       }
       
          
         
        
           class displayHistoryAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
                           SwingUtilities.invokeLater(new Runnable() {
                            @Override
     public void run() {  // run in  EDT context */
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

                            });
                           }
        
        });
      }
     }



class controlPrecisionAction extends AbstractAction {

    public controlPrecisionAction() {
      super("Controls precision of displayed numbers and truncation of large matrices");
    }

    public void actionPerformed(ActionEvent e)  {
        JFrame precisionConfigFrame = new JFrame("Display Precision and Matrix display truncation Configuration");
        precisionConfigFrame.setLayout(new GridLayout(6,2));
        
        precisionConfigFrame.add(new JLabel("Decimal digits for matrices"));
        final JTextField  precisionMatEditField = new JTextField(java.lang.String.valueOf(groovySci.PrintFormatParams.getMatDigitsPrecision()));
        precisionMatEditField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              groovySci.PrintFormatParams.setMatDigitsPrecision(java.lang.Integer.parseInt(precisionMatEditField.getText()));
            }
        });
        precisionConfigFrame.add(precisionMatEditField);
        
        precisionConfigFrame.add(new JLabel("Decimal digits for vectors"));
        final JTextField  precisionVecEditField = new JTextField(java.lang.String.valueOf(groovySci.PrintFormatParams.getVecDigitsPrecision()));
        precisionVecEditField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                groovySci.PrintFormatParams.setVecDigitsPrecision(java.lang.Integer.parseInt(precisionVecEditField.getText()));
                
            }
        });
        precisionConfigFrame.add(precisionVecEditField);
        
        
        precisionConfigFrame.add(new JLabel("Number of rows for matrices"));
        final JTextField  rowsMatEditField = new JTextField(java.lang.String.valueOf(groovySci.PrintFormatParams.getMatMxRowsToDisplay()));
        rowsMatEditField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              groovySci.PrintFormatParams.setMatMxRowsToDisplay(java.lang.Integer.parseInt(rowsMatEditField.getText()));
            }
        });
        precisionConfigFrame.add(rowsMatEditField);
        
        precisionConfigFrame.add(new JLabel("Number of columns  for matrices"));
        final JTextField colsMatEditField = new JTextField(java.lang.String.valueOf(groovySci.PrintFormatParams.getMatMxColsToDisplay()));
        colsMatEditField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                groovySci.PrintFormatParams.setMatMxColsToDisplay(java.lang.Integer.parseInt(colsMatEditField.getText()));
                
            }
        });
        precisionConfigFrame.add(colsMatEditField);
      
        
        precisionConfigFrame.add(new JLabel("Verbose"));
        final JCheckBox verboseCheckBox = new JCheckBox("Verbose Flag ");
        verboseCheckBox.setToolTipText("Verbose off stops the output of some results");
        verboseCheckBox.setSelected(groovySci.PrintFormatParams.getVerbose());
        verboseCheckBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
              groovySci.PrintFormatParams.setVerbose(verboseCheckBox.isSelected());
            }
        });
        precisionConfigFrame.add(verboseCheckBox);
                
        JButton acceptButton = new JButton("Accept all text field values");
        acceptButton.setToolTipText("Press to read all the contents of text fields (each individual text field is readed by pressing ENTER");
        acceptButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
        groovySci.PrintFormatParams.setMatDigitsPrecision(java.lang.Integer.parseInt(precisionMatEditField.getText()));
        groovySci.PrintFormatParams.setVecDigitsPrecision(java.lang.Integer.parseInt(precisionVecEditField.getText()));
        groovySci.PrintFormatParams.setMatMxRowsToDisplay(java.lang.Integer.parseInt(rowsMatEditField.getText()));
        groovySci.PrintFormatParams.setMatMxColsToDisplay(java.lang.Integer.parseInt(colsMatEditField.getText()));
            }
        });
        precisionConfigFrame.add(acceptButton);
        
        JButton displayButton = new JButton("Display the current parameters");
        displayButton.setToolTipText("Displays the current parameter setting");
        displayButton.addActionListener(new ActionListener() {
        
            @Override
            public void actionPerformed(ActionEvent e) {
    System.out.println("Matrix Digits Precision = "+groovySci.PrintFormatParams.getMatDigitsPrecision());
    System.out.println("Vector Digits Precision = "+groovySci.PrintFormatParams.getVecDigitsPrecision());
    System.out.println("Rows to display = "+groovySci.PrintFormatParams.getMatMxRowsToDisplay());
    System.out.println("Columns to display = "+groovySci.PrintFormatParams.getMatMxColsToDisplay());
            }
          });
       precisionConfigFrame.add(displayButton);         
        
        precisionConfigFrame.pack();
        precisionConfigFrame.setLocation(GlobalValues.gLabMainFrame.getX()+200, GlobalValues.gLabMainFrame.getY());
        precisionConfigFrame.setVisible(true);
    
    }
    
    
    
}


