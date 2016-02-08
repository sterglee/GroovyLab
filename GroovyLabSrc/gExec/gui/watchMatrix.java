
package gExec.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

/*  class watchMatrix displays graphically the contents of a 2-d double array using a Swing JTable component.
 * The user can scroll the values and edit them.
 * 
 */
public class watchMatrix
{
    
    public static void display( groovySci.math.array.Matrix M) {
        display(M.getArray());
    }
    
     public static void display( groovySci.math.array.Matrix M,  String varName) {
        display(M.getArray(),varName);
    }
    
    
    
    public static void display( final double [][] vals) {
        EventQueue.invokeLater(new Runnable()
         {
            public void run()
            {

               MatrixRenderFrame  frame = new MatrixRenderFrame(vals);
               frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
               
               frame.setVisible(true);
            }
         });
    }
    
    public static void display( final double [][] vals,   final String varName) {
        EventQueue.invokeLater(new Runnable()
         {
            public void run()
            {

               MatrixRenderFrame  frame = new MatrixRenderFrame(vals,  varName);
               frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
               
               frame.setVisible(true);
            }
         });
    }
    
    
    
    public static void main(String[] args)
   {
       final double testVals[][] = {{3, 4}, {5, 6}};
    
       display(testVals);
    }
}

/**
 * This frame contains a table of planet data.
 */
class MatrixRenderFrame extends JFrame
{
    int  cell_width = 50;
    int  cell_height = 50;

   TableModel model=null;
   
   public MatrixRenderFrame(double [][] vals)  {
        this(vals, "");
   }
   
           public MatrixRenderFrame(double [][] vals, String varName)
   {
        setTitle("Browsing zero-indexed Matrix "+varName+"  contents. You can modify them also, by editing the cells");
    
      model = new MatrixTableModel(vals);
      int nrows = vals.length;
      int ncols = vals[0].length;
      
      setSize((nrows+1)*cell_width,  (ncols+2)*cell_height);

      JTable table = new JTable(model);

      JTableHeader header = table.getTableHeader();
      header.setBackground(Color.yellow);

      table.setRowSelectionAllowed(false);
      table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      // set up renderers and editors
   //  table.setDefaultEditor(Double.class, new MatrixCellEditor());

      TableColumnModel columnModel = table.getColumnModel();

      // set the width of the table's columns
      for (int col=0; col<ncols; col++) {
          columnModel.getColumn(col).setResizable(false);
          columnModel.getColumn(col).setWidth(cell_width);
      }
      // show table
     table.setRowHeight(cell_height);
     
     JScrollPane tablePane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,  JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
     // table.addMouseListener(new MouseAdapter() {});  // TODO: mouse listener that displays which cell the mouse cursor is over
     add(tablePane);
   }

}


// The Matrix table model specifies the values, rendering and editing properties for the Matrix data.

class MatrixTableModel extends AbstractTableModel
{
    double [][] matrix;
    String[] columnNames;
    public MatrixTableModel(double [][] vals) {
        matrix = vals;   // keep the double[][] array to display
        int colNum = matrix[0].length;
        columnNames = new String[colNum];
                 for (int c=0; c<colNum; c++ )
               columnNames[c] = "C"+Integer.toString(c);
    }
    
   public String getColumnName(int c)
   {
      return columnNames[c];
   }

   public Class<?> getColumnClass(int c)
   {
      return  Double.class; 
   }

   public int getColumnCount()
   {
          return matrix[0].length;
   }

   public int getRowCount()
   {
          return matrix.length;
   }

   public Object getValueAt(int r, int c)
   {
         return Double.valueOf(matrix[r][c]);
   }

   public void setValueAt(Object obj, int r, int c)
   {
          matrix[r][c] = ((Double)obj).doubleValue();
   }

   public boolean isCellEditable(int r, int c)
   {
       //if (r>=1) return true;
       return true;
   }

   
}
/**
 * This editor pops up a color dialog to edit a cell value
 */
class MatrixCellEditor extends AbstractCellEditor implements TableCellEditor
{
   public MatrixCellEditor()
   {
      panel = new JPanel();
      // prepare color dialog

      colorChooser = new JColorChooser();
      colorDialog = JColorChooser.createDialog(null, "Matrix Color", false, colorChooser,
            new ActionListener() // OK button listener
               {
                  public void actionPerformed(ActionEvent event)
                  {
                     stopCellEditing();
                  }
               }, new ActionListener() // Cancel button listener
               {
                  public void actionPerformed(ActionEvent event)
                  {
                     cancelCellEditing();
                  }
               });
      colorDialog.addWindowListener(new WindowAdapter()
         {
            public void windowClosing(WindowEvent event)
            {
               cancelCellEditing();
            }
         });
   }

   public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
         int row, int column)
   {
      // this is where we get the current Color value. We store it in the dialog in case the user
      // starts editing
      colorChooser.setColor((Color) value);
      return panel;
   }

   public boolean shouldSelectCell(EventObject anEvent)
   {
      // start editing
      colorDialog.setVisible(true);

      // tell caller it is ok to select this cell
      return true;
   }

   public void cancelCellEditing()
   {
      // editing is canceled--hide dialog
      colorDialog.setVisible(false);
      super.cancelCellEditing();
   }

   public boolean stopCellEditing()
   {
      // editing is complete--hide dialog
      colorDialog.setVisible(false);
      super.stopCellEditing();

      // tell caller is is ok to use color value
      return true;
   }

   public Object getCellEditorValue()
   {
      return colorChooser.getColor();
   }

   private JColorChooser colorChooser;
   private JDialog colorDialog;
   private JPanel panel;
}
