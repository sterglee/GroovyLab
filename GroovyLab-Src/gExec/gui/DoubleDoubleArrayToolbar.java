
package gExec.gui;

import gExec.gui.MathDialogs.ExpressionDialogPlot3D_Grid;
import gExec.gui.MathDialogs.ExpressionDialogPlot2D_Line;
import gExec.Interpreter.GlobalValues;
import gExec.gui.MathDialogs.ExpressionDialogPlot2D_Histo;
import gExec.gLab.EditorPaneHTMLHelp;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;



public class DoubleDoubleArrayToolbar  extends JPanel {
        JButton bcolon;
        JButton bones, bzeros, brand, binv;
        JButton bsum, bdet, beig, blu, bsvd,  bqr;
        JButton bsin, bcos, btan,   bsinh, bcosh, btanh;
        JButton bcorr;
        
    public DoubleDoubleArrayToolbar() {
        JPanel matrixPanel1 = new JPanel();
        JPanel matrixPanel2 = new JPanel();
        JPanel matrixPanel3 = new JPanel();
        JPanel matrixPanel4 = new JPanel();
        
        setLayout(new GridLayout(4,1));

        
        bcorr = new JButton("corr");
        bcorr.setToolTipText("corr(M1. M2): correlation of matrices M1  and M2");
        bcorr.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
           GlobalValues.gLabMainFrame.jLabConsole.setText(GlobalValues.gLabMainFrame.jLabConsole.getText()+"corr(M1, M2)");
           GlobalValues.gLabMainFrame.jLabConsole.setCaretPosition(GlobalValues.gLabMainFrame.jLabConsole.getText().length());
        
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.gLabMainFrame.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.gLabMainFrame.jLabConsole.dispatchEvent(fe);
            }
        });
        matrixPanel4.add(bcorr);
        
        bdet = new JButton("det");
        bdet.setToolTipText("det(M): determinant of M");
        bdet.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
           GlobalValues.gLabMainFrame.jLabConsole.setText(GlobalValues.gLabMainFrame.jLabConsole.getText()+"det(M)");
           GlobalValues.gLabMainFrame.jLabConsole.setCaretPosition(GlobalValues.gLabMainFrame.jLabConsole.getText().length());
        
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.gLabMainFrame.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.gLabMainFrame.jLabConsole.dispatchEvent(fe);
            }
        });
        matrixPanel4.add(bdet);
      
        
        bcolon = new JButton("Inc");
        bcolon.setToolTipText("implements colon operator, e.g. var  t = Inc(0, 0.01, 10) is as t = 0:0.01:10");
        bcolon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
           GlobalValues.gLabMainFrame.jLabConsole.setText(GlobalValues.gLabMainFrame.jLabConsole.getText()+"Inc(");
           GlobalValues.gLabMainFrame.jLabConsole.setCaretPosition(GlobalValues.gLabMainFrame.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.gLabMainFrame.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.gLabMainFrame.jLabConsole.dispatchEvent(fe);
            }
        });
        matrixPanel1.add(bcolon);

     
     
        bones = new JButton("Ones");
        bones.setToolTipText("creating matrices that consist of ones    (e.g.: ones(2) will return a 2-by-2 matrix of ones)");
        
        bones.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
           GlobalValues.gLabMainFrame.jLabConsole.setText(GlobalValues.gLabMainFrame.jLabConsole.getText()+"ones(");
           GlobalValues.gLabMainFrame.jLabConsole.setCaretPosition(GlobalValues.gLabMainFrame.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.gLabMainFrame.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.gLabMainFrame.jLabConsole.dispatchEvent(fe);    
            }
        });
        
        bzeros = new JButton("Zeros");
        bzeros.setToolTipText("creating matrices that consist of zeros    (e.g.: zeroes(2,3) will return a 2-by-3 matrix of zeroes)");
        bzeros.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
           GlobalValues.gLabMainFrame.jLabConsole.setText(GlobalValues.gLabMainFrame.jLabConsole.getText()+"zeros(");
           GlobalValues.gLabMainFrame.jLabConsole.setCaretPosition(GlobalValues.gLabMainFrame.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.gLabMainFrame.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.gLabMainFrame.jLabConsole.dispatchEvent(fe);    
            }
        });
        
        brand = new JButton("Rand");
        brand.setToolTipText("construct a matrix filled with pseudorandom values(e.g.: rand(2,3)  will return a 2-by-3 matrix of pseudorandom values ");
                
        brand.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
           GlobalValues.gLabMainFrame.jLabConsole.setText(GlobalValues.gLabMainFrame.jLabConsole.getText()+"rand(");
           GlobalValues.gLabMainFrame.jLabConsole.setCaretPosition(GlobalValues.gLabMainFrame.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.gLabMainFrame.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.gLabMainFrame.jLabConsole.dispatchEvent(fe);    
            }
        });
        
        binv = new JButton("inv");
        binv.setToolTipText("determining the inverse of a matrix");
        binv.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
           GlobalValues.gLabMainFrame.jLabConsole.setText(GlobalValues.gLabMainFrame.jLabConsole.getText()+"inv(");
           GlobalValues.gLabMainFrame.jLabConsole.setCaretPosition(GlobalValues.gLabMainFrame.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.gLabMainFrame.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.gLabMainFrame.jLabConsole.dispatchEvent(fe);    
            }
        });
        
        
        bsum = new JButton("sum");
        bsum.setToolTipText( "the sum of all values within the matrix or structure.  Sums are computed columnwise on matrices");
        bsum.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
           GlobalValues.gLabMainFrame.jLabConsole.setText(GlobalValues.gLabMainFrame.jLabConsole.getText()+"sum(");
           GlobalValues.gLabMainFrame.jLabConsole.setCaretPosition(GlobalValues.gLabMainFrame.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.gLabMainFrame.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.gLabMainFrame.jLabConsole.dispatchEvent(fe);    
            }
        });
        
        bdet = new JButton("det");
        bdet.setToolTipText("computes the determinant of a square matrix A, d = det(Matrix A)");
        bdet.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
           GlobalValues.gLabMainFrame.jLabConsole.setText(GlobalValues.gLabMainFrame.jLabConsole.getText()+"det(");
           GlobalValues.gLabMainFrame.jLabConsole.setCaretPosition(GlobalValues.gLabMainFrame.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.gLabMainFrame.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.gLabMainFrame.jLabConsole.dispatchEvent(fe);    
            }
        });
        
        beig = new JButton("eig");
        beig.setToolTipText("computing eigenvalues and eigenVectors of an array");
        beig.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
           
                 EditorPaneHTMLHelp  eigHelpPane = new EditorPaneHTMLHelp("eig.html");
                  if (GlobalValues.useSystemBrowserForHelp==false) {
                 eigHelpPane.setSize(GlobalValues.figFrameSizeX, GlobalValues.figFrameSizeY);
                 eigHelpPane.setLocation(GlobalValues.sizeX/4, GlobalValues.sizeY/4);
                 eigHelpPane.setVisible(true);
                  }           
           GlobalValues.gLabMainFrame.jLabConsole.setText(GlobalValues.gLabMainFrame.jLabConsole.getText()+"eig(");
           GlobalValues.gLabMainFrame.jLabConsole.setCaretPosition(GlobalValues.gLabMainFrame.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.gLabMainFrame.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.gLabMainFrame.jLabConsole.dispatchEvent(fe);    
            }
        });
        
        blu = new JButton("lu");
        blu.setToolTipText("For an m-by-n matrix A with m >= n, the LU decomposition is \n"+
                  "an m-by-n unit lower triangular matrix L, an n-by-n upper triangular matrix U, \n"+
                  "and a permutation Mattor piv of length m so that A(piv,:) = L*U. \n"+
                  "If m < n, then L is m-by-m and U is m-by-n. \n\n"+
                  "The LU decompostion with pivoting always exists, even if the matrix is \n"+
                  "singular, so the constructor will never fail.  The primary use of the \n"+
                  "LU decomposition is in the solution of square systems of simultaneous \n"+
                  "linear equations.  This will fail if isNonsingular() returns false. \n\n"+
                  " usage: [L,U]   = lu (A) \n"+
                  "[L,U,P] = lu (A) \n"+
                  "[L]     = lu (A) \n"+
                  "x       = lu (A,B) as a solution to A*X = B; ");
        blu.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
           GlobalValues.gLabMainFrame.jLabConsole.setText(GlobalValues.gLabMainFrame.jLabConsole.getText()+"lu(");
           GlobalValues.gLabMainFrame.jLabConsole.setCaretPosition(GlobalValues.gLabMainFrame.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.gLabMainFrame.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.gLabMainFrame.jLabConsole.dispatchEvent(fe);    
            }
        });
        
        
        bsvd = new JButton("svd");
        bsvd.setToolTipText(" Singular Value Decomposition: \n\n"+
        "For an m-by-n matrix A with m >= n, the singular value decomposition is \n"+
        "an m-by-n orthogonal matrix U, an n-by-n diagonal matrix S, and \n"+
        "an n-by-n orthogonal matrix V so that A = U*S*V'. \n\n"+
        "The singular values, sigma[k] = S[k][k], are ordered so that \n"+
        "sigma[0] >= sigma[1] >= ... >= sigma[n]. \n"+
        "The singular value decompostion always exists, so the constructor will \n"+
        "never fail.  The matrix condition number and the effective numerical \n"+
        "rank can be computed from this decomposition. \n\n"+
        "usage: s = svd(A) \n"+
        "[U,S,V]=svd(A)");
        bsvd.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
           GlobalValues.gLabMainFrame.jLabConsole.setText(GlobalValues.gLabMainFrame.jLabConsole.getText()+"svd(");
           GlobalValues.gLabMainFrame.jLabConsole.setCaretPosition(GlobalValues.gLabMainFrame.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.gLabMainFrame.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.gLabMainFrame.jLabConsole.dispatchEvent(fe);    
            }
        });
        
        bqr = new JButton("qr");
        bqr.setToolTipText("QR Decomposition. \n\n"+
        "For an m-by-n matrix A with m >= n, the QR decomposition is an m-by-n \n"+
        "orthogonal matrix Q and an n-by-n upper triangular matrix R so that \n"+
        "A = Q*R. \n"+
        "The QR decompostion always exists, even if the matrix does not have \n"+
        "full rank, so the constructor will never fail.  The primary use of the \n"+
        "QR decomposition is in the least squares solution of nonsquare systems \n"+
        "of simultaneous linear equations.  This will fail if isFullRank() \n"+
        "returns false.");
        bqr.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
           GlobalValues.gLabMainFrame.jLabConsole.setText(GlobalValues.gLabMainFrame.jLabConsole.getText()+"qr(");
           GlobalValues.gLabMainFrame.jLabConsole.setCaretPosition(GlobalValues.gLabMainFrame.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.gLabMainFrame.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.gLabMainFrame.jLabConsole.dispatchEvent(fe);    
            }
        });
                
        
        

        bsin = new JButton("sin");
        bsin.setToolTipText("def sin( x: Mat): Mat ; \n Computes the sine of x");
        bsin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
           GlobalValues.gLabMainFrame.jLabConsole.setText(GlobalValues.gLabMainFrame.jLabConsole.getText()+"sin(");
           GlobalValues.gLabMainFrame.jLabConsole.setCaretPosition(GlobalValues.gLabMainFrame.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.gLabMainFrame.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.gLabMainFrame.jLabConsole.dispatchEvent(fe);
            }
        });

        bcos= new JButton("cos");
        bcos.setToolTipText("def cos( x: Mat): Mat ; \n Computes the cosine of x");
        bcos.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
           GlobalValues.gLabMainFrame.jLabConsole.setText(GlobalValues.gLabMainFrame.jLabConsole.getText()+"cos(");
           GlobalValues.gLabMainFrame.jLabConsole.setCaretPosition(GlobalValues.gLabMainFrame.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.gLabMainFrame.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.gLabMainFrame.jLabConsole.dispatchEvent(fe);
            }
        });

        btan= new JButton("tan");
        btan.setToolTipText("def tan( x: Mat): Mat; \n Computes the tangent of x");
        btan.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
             GlobalValues.gLabMainFrame.jLabConsole.setText(GlobalValues.gLabMainFrame.jLabConsole.getText()+"tan(");
             GlobalValues.gLabMainFrame.jLabConsole.setCaretPosition(GlobalValues.gLabMainFrame.jLabConsole.getText().length());
             // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.gLabMainFrame.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.gLabMainFrame.jLabConsole.dispatchEvent(fe);
          }
        });


        bsinh = new JButton("sinh");
        bsin.setToolTipText("def sinh( x: Mat): Mat ; \n Computes hyperbolic sine of x");
        bsinh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
             GlobalValues.gLabMainFrame.jLabConsole.setText(GlobalValues.gLabMainFrame.jLabConsole.getText()+"sinh(");
             GlobalValues.gLabMainFrame.jLabConsole.setCaretPosition(GlobalValues.gLabMainFrame.jLabConsole.getText().length());

           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.gLabMainFrame.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.gLabMainFrame.jLabConsole.dispatchEvent(fe);

            }
        });
        bcosh= new JButton("cosh");
        bcosh.setToolTipText("def cosh( x: Mat): Mat; \n Computes the hyperbolic cosine of x");
        bcosh.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
           GlobalValues.gLabMainFrame.jLabConsole.setText(GlobalValues.gLabMainFrame.jLabConsole.getText()+"cosh(");
           GlobalValues.gLabMainFrame.jLabConsole.setCaretPosition(GlobalValues.gLabMainFrame.jLabConsole.getText().length());

           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.gLabMainFrame.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.gLabMainFrame.jLabConsole.dispatchEvent(fe);
            }
        });

        btanh= new JButton("tanh");
        btanh.setToolTipText("def tanh( x: Mat): Mat; \n Computes the hyperbolic tangent of x");
        btanh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
           GlobalValues.gLabMainFrame.jLabConsole.setText(GlobalValues.gLabMainFrame.jLabConsole.getText()+"tanh(");
           GlobalValues.gLabMainFrame.jLabConsole.setCaretPosition(GlobalValues.gLabMainFrame.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.gLabMainFrame.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.gLabMainFrame.jLabConsole.dispatchEvent(fe);
            }
        });

        
        
        //matrixPanel.add(browAppend); matrixPanel.add(bcolAppend); 
        matrixPanel2.add(bones);  matrixPanel2.add(bzeros);  matrixPanel2.add(brand);
        matrixPanel2.add(binv); 
        matrixPanel2.add(bsum); 
        matrixPanel2.add(bdet); matrixPanel2.add(beig);  matrixPanel2.add(blu); matrixPanel2.add(bsvd);
        matrixPanel2.add(bqr);    
        
        
        matrixPanel3.add(bsin); matrixPanel3.add(bcos); matrixPanel3.add(btan);  
        matrixPanel3.add(bsinh); matrixPanel3.add(bcosh); matrixPanel3.add(btanh);

        
     
        add(matrixPanel1);       add(matrixPanel2);    add(matrixPanel3); add(matrixPanel4);
        
   }
}

