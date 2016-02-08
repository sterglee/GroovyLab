
package gExec.gui;

import groovy.lang.Binding;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import gExec.Interpreter.*;
import java.math.BigDecimal;
import javax.swing.table.*;

public class WatchWorkspace  {
    Container container;
    static public JFrame watchFrame=null;
    private int varCnt=0;
    public  JPanel dispPanel;
        
            
    public WatchWorkspace()
    	{
            watchFrame = new JFrame("gLab workspace variables");
            dispPanel = new JPanel();
            watchFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            watchFrame.add(dispPanel);
    }

    
    static public int displayGroovySciBinding(final JPanel dPanel) {
        Vector varNames = new Vector();
        Vector varValues = new Vector();
        Object [][] varsCells;
        String[]  columnNames = {"GroovySci variable name", "Value"} ;
    
            int varCnt = 0;
	
            Binding groovyBinding = GlobalValues.groovyBinding;    // get the reference to the Groovy variable binding
            Map variables = groovyBinding.getVariables();  // get a Map of the binded variables at the Groovy's context
            Set  bindElemsSet = variables.keySet();  // return a set view of the variables in the Map
        
            Iterator bindedElemsIter  =  bindElemsSet.iterator();   // iterate through the Groovy's variables
            while (bindedElemsIter.hasNext())  { 
    String varName = (String) bindedElemsIter.next();  // get the name of the Groovy's variable
    
    Object currentVarValue = groovyBinding.getVariable(varName);
    if (currentVarValue != null) {
    String varValue="";
  if ( currentVarValue instanceof  double [])   // a Vector
            {  
    varValue = "double ["+((double [])currentVarValue).length+"]";
            }
  else
  if (( currentVarValue instanceof  double [][]))
       {
    varValue = "double ["+((double [][])currentVarValue).length+"]"+"["+((double [][])currentVarValue)[0].length+"]";
       }
  else  {  // floating point or String 
  if  (currentVarValue instanceof  Integer) 
             {
    varValue = "Integer = "+currentVarValue;
             }
  else if ( currentVarValue instanceof Long)  {
    varValue = "Long = "+currentVarValue;                 
             }
  else if ( currentVarValue instanceof Float)  {
    varValue = "Float = "+currentVarValue;                 
             }
  else if ( currentVarValue instanceof Double)  {
    varValue = "Double = "+currentVarValue;
  }
  else if ( currentVarValue instanceof groovySci.math.array.Matrix)  {
      groovySci.math.array.Matrix myMatrix = (groovySci.math.array.Matrix) currentVarValue;
      int nrows  =   myMatrix.getRowsNumber();
      int ncols = myMatrix.getColumnsNumber();
              
      varValue = "Matrix ["+nrows+","+ncols+"]";
    }
   
  else
       if ( currentVarValue instanceof BigDecimal)  {
    varValue = "BigDecimal = "+currentVarValue;                 
             }//  (currentVarValue instanceof String)   // simple scalar numeric values  or Strings can be treated as Strings and passed to gLab 
  else if ( currentVarValue instanceof String)  {
    varValue = "String ="+currentVarValue;                 
             } 
  else   varValue = currentVarValue.getClass().toString();
            
              }  // floating point or String
    varCnt++;
    varNames.add(varName);
    varValues.add(varValue);
            }  // binded variable elements
            }
        varsCells = new Object [varCnt][2];
        for (int k=0; k<varCnt; k++)
                {
          varsCells[k][0] = (Object) varNames.get(k);
          varsCells[k][1] = (Object) varValues.get(k);
            }
        
         DefaultTableModel  model = new DefaultTableModel(varCnt, 2);
         model.setColumnIdentifiers(columnNames);
         for (int k=0; k<varCnt; k++)  {
             model.setValueAt(varNames.get(k), k, 0 );
             model.setValueAt(varValues.get(k),  k, 1);
         }
         gExec.gLab.gLab.varsTable = new JTable(model);
         gExec.gLab.gLab.varsTable.setToolTipText("Right mouse click to inspect  variable contents");
    
         gExec.gLab.gLab.varsTable.setCellSelectionEnabled(true);
          JScrollPane  varsTablePane = new JScrollPane(gExec.gLab.gLab.varsTable);
    
        gExec.gLab.gLab.variablesPanelPopupMenu =  new JPopupMenu();
        JMenuItem  clearTableItem = new JMenuItem("Clear the contents of the table ");
           clearTableItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 dPanel.removeAll();
                 gExec.gLab.gLab.outerPane.revalidate();
                 
            }
           });
    JMenuItem  inspectTableItem = new JMenuItem("Inspect selected item ");
           inspectTableItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selRow = gExec.gLab.gLab.varsTable.getSelectedRow();
                String  selectedVariableName = (String)gExec.gLab.gLab.varsTable.getModel().getValueAt(selRow, 0);
                Object selectedObject = gExec.Interpreter.GlobalValues.groovyBinding.getVariable(selectedVariableName);
                groovy.inspect.swingui.ObjectBrowser.inspect(selectedObject);
               
                 
            }
           });
    
           gExec.gLab.gLab.variablesPanelPopupMenu.add(inspectTableItem);
           gExec.gLab.gLab.variablesPanelPopupMenu.add(clearTableItem);
    
          gExec.gLab.gLab.varsTable.addMouseListener(new MouseVariablesAdapter());
            
    
          dPanel.removeAll();
          dPanel.setLayout(new BorderLayout());
          dPanel.add(varsTablePane);
                    
          if (GlobalValues.globalEditorPane != null)
    gLabEdit.GCompletionProvider.installAutoCompletion();
    
    return varCnt;
        }
        
    
    
    static class MouseVariablesAdapter extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {   
           if (e.isPopupTrigger()){  
                 gExec.gLab.gLab.variablesPanelPopupMenu.show((Component) e.getSource(), e.getX(), e.getY());
             }
           }
    
        public void mouseReleased(MouseEvent e) { 
           if (e.isPopupTrigger()){
                 gExec.gLab.gLab.variablesPanelPopupMenu.show((Component) e.getSource(), e.getX(), e.getY());
             }       
      }
    } 
    
    
     }
    

