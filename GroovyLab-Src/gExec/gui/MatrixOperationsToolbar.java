
package gExec.gui;

import gExec.gui.MathDialogs.ExpressionDialogPlot3D_Grid;
import gExec.gui.MathDialogs.ExpressionDialogPlot2D_Line;
import gExec.Interpreter.GlobalValues;
import gExec.gui.MathDialogs.ExpressionDialogPlot2D_Histo;
import gExec.gLab.EditorPaneHTMLHelp;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class MatrixOperationsToolbar  extends JPanel {
        JButton bconstr, bcolon,blinspace, blogspace, bsubm,  bsubmr, bsubmc, bcolAppend, browAppend, bdotProduct;
        JButton bones, bzeros, brand, beye, binv, butriag, breshape, bresample,   bany, bfind, bisEmpty;
        JButton band, bor, bxor, bsum, bdet, beig, blu, bsvd,  bqr;
        
    public MatrixOperationsToolbar() {
        JPanel matrixPanel1 = new JPanel();
        JPanel matrixPanel2 = new JPanel();
        JPanel matrixPanel3 = new JPanel();
        JPanel matrixPanel4 = new JPanel();
        JPanel matrixPanel5 = new JPanel();
        setLayout(new GridLayout(5,1));

        JButton      bhelp = new JButton("Help");
        bhelp.setToolTipText("HTML Help on Vector operations");
        bhelp.addActionListener(new ActionListener() {
  @Override
            public void actionPerformed(ActionEvent e) {
       EditorPaneHTMLHelp  inPlaceHelpPane = new EditorPaneHTMLHelp("Matrix.html");
       if (GlobalValues.useSystemBrowserForHelp==false) {
          inPlaceHelpPane.setSize(GlobalValues.figFrameSizeX, GlobalValues.figFrameSizeY);
          inPlaceHelpPane.setLocation(GlobalValues.sizeX/4, GlobalValues.sizeY/4);
          inPlaceHelpPane.setVisible(true);
                 }
               }
        });
        matrixPanel1.add(bhelp);

          
        bconstr = new JButton("M(\"..\")");
        bconstr.setToolTipText("specify a Matrix from its elements, e.g. M (\"3.4, 5, 6.7; 5.6, -5.6, 7.8\")");
        bconstr.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
           GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"M(\"  ... \"  )  ");
           GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);
            }
        });
        matrixPanel1.add(bconstr);
        
        bcolon = new JButton("inc");
        bcolon.setToolTipText("implements colon operator, e.g. t = inc(0, 0.01, 10) is as t = 0:0.01:10");
        bcolon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
           GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"inc(");
           GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);
            }
        });
        matrixPanel1.add(bcolon);

        blinspace = new JButton("linspace");
        blinspace.setToolTipText("Linearly spaced vector, linspace(x1, x2, N) generates N points between x1 and x2");
        blinspace.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
           GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"linspace(");
           GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);
            }
        });
        matrixPanel1.add(blinspace);
        
        blogspace = new JButton("logspace");
        blogspace.setToolTipText("Logarithmically spaced vector, logspace(x1, x2, N) generates N points between x1 and x2");
        blogspace.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
           GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"logspace(");
           GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);
            }
        });
        matrixPanel1.add(blogspace);
        
        bsubm = new JButton("grc");
        bsubm.setToolTipText("implements submatrix operator, e.g. m = M.grc(3, 2, 8, 2, 4, 16),  is as m  = M(3:2:8, 2:4:16)");
        bsubm.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
           GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"grc(");
           GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);
            }
        });
        matrixPanel1.add(bsubm);

        bsubmr = new JButton("gr");
        bsubmr.setToolTipText("implements row submatrix operator, e.g. mr = M.gr(3, 2, 8),  is as mr  = M(3:2:8, :)");
        bsubmr.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
           GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"gr(");
           GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);
            }
        });
        matrixPanel1.add(bsubmr);

        
        bsubmc = new JButton("gc");
        bsubmc.setToolTipText("implements column submatrix operator, e.g. mc = M.gc(2, 4, 16),  is as m  = M(:, 2:4:16)");
        bsubmc.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
           GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"gc(");
           GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);
            }
        });
        matrixPanel1.add(bsubmc);

     
        bdotProduct = new JButton(" dot ");
        bdotProduct.setToolTipText("implements Matrix dot product, e.g. mc = dot(M, M) is M . M");
        bdotProduct.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
           GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"dot(");
           GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);
            }
        });
        matrixPanel2.add(bdotProduct);

     
        bcolAppend = new JButton(">>>");
        bcolAppend.setToolTipText("Append columns to matrix");
        bcolAppend.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
           GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+">>>");
           GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);
            }
        });

        browAppend = new JButton(">>");
        browAppend.setToolTipText("Append rows to matrix");
        browAppend.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
           GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+">>");
           GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);
            }
        });

        bones = new JButton("ones");
        bones.setToolTipText("creating matrices that consist of ones    (e.g.: ones(2) will return a 2-by-2 matrix of ones)");
        
        bones.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
           GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"ones(");
           GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);    
            }
        });
        
        bzeros = new JButton("zeros");
        bzeros.setToolTipText("creating matrices that consist of zeros    (e.g.: zeroes(2,3) will return a 2-by-3 matrix of zeroes)");
        bzeros.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
           GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"zeros(");
           GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);    
            }
        });
        
        brand = new JButton("rand");
        brand.setToolTipText("construct a matrix filled with pseudorandom values(e.g.: rand(2,3)  will return a 2-by-3 matrix of pseudorandom values ");
                
        brand.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
           GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"rand(");
           GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);    
            }
        });
        
        beye = new JButton("eye");
        beye.setToolTipText( "creating matrices that consist of zeros."+
                   "everywhere except in the diagonal. The diagonal consists of ones.  \n\n"+
                   "e.g.: eye(3) will return a 3-by-3 matrix [1,0,0;0,1,0;0,0,1], \n"+
                   "eye(4,3) will return a 4-by-3 matrix with diagonal set to 1 "); 
        beye.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
           GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"eye(");
           GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);    
            }
        });
        
        binv = new JButton("inv");
        binv.setToolTipText("determining the inverse of a matrix");
        binv.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
           GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"inv(");
           GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);    
            }
        });
        
        butriag = new JButton("utriag");
        butriag.setToolTipText("converting a matrix into upper triangular form");
        butriag.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
           GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"utriag(");
           GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);    
            }
        });
        
        breshape = new JButton("reshape");
        breshape.setToolTipText("reshaping matrices  (e.g. reshape([1,2;3,4;5,6],2,3) return [1,5,4;3,2,6]) \n"+
                  "The original matrix is read column for column and rearranged  to a new dimension  ");
        breshape.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
           GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"reshape(");
           GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);    
            }
        });
                
        
        bresample = new JButton("resample");
        bresample.setToolTipText("resamples (downsamples) matrices, e.g. resample(2,3)");
        bresample.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
           GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"resample(");
           GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);    
            }
        });
                
        bany = new JButton("any");
        bany.setToolTipText("returns 1 if any element of the argument is nonzero");
        bany.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
           GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"any(");
           GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);    
            }
        });
        
        bfind = new JButton("find");
        bfind.setToolTipText("return a column vector which points to all nonzero elements of the function arguments ");
        bfind.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
           GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"find(");
           GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);    
            }
        });
                
        
        bisEmpty = new JButton("isEmpty");
        bisEmpty.setToolTipText("checking if a matrix is empty (no number or string)");
        bisEmpty.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
           GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"isEmpty(");
           GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);    
            }
        });
                
        band = new JButton("and");
        band.setToolTipText("computing the AND of two matrices");
        band.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
           GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"and(");
           GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);    
            }
        });
        
        bor = new JButton("or");
        bor.setToolTipText("computing the OR of two matrices");
        bor.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
           GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"or(");
           GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);    
            }
        });
        
        bxor = new JButton("xor");
        bxor.setToolTipText("computing the XOR of two matrices");
        bxor.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
           GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"xor(");
           GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);    
            }
        });
        
        bsum = new JButton("sum");
        bsum.setToolTipText( "the sum of all values within the matrix or structure.  Sums are computed columnwise on matrices");
        bsum.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
           GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"sum(");
           GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);    
            }
        });
        
        bdet = new JButton("det");
        bdet.setToolTipText("computes the determinant of a square matrix A, d = det(Matrix A)");
        bdet.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
           GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"det(");
           GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);    
            }
        });
        
        beig = new JButton("eig");
        beig.setToolTipText("computing eigenvalues and eigenvectors of an array");
        beig.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
           GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"eig(");
           GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);    
            }
        });
        
        blu = new JButton("lu");
        blu.setToolTipText("For an m-by-n matrix A with m >= n, the LU decomposition is \n"+
                  "an m-by-n unit lower triangular matrix L, an n-by-n upper triangular matrix U, \n"+
                  "and a permutation vector piv of length m so that A(piv,:) = L*U. \n"+
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
           GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"lu(");
           GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);    
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
           GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"svd(");
           GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);    
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
           GlobalValues.jLabConsole.setText(GlobalValues.jLabConsole.getText()+"qr(");
           GlobalValues.jLabConsole.setCaretPosition(GlobalValues.jLabConsole.getText().length());
           // construct an explicit focus event in order to display the cursor at the input console
        FocusEvent fe = new FocusEvent(GlobalValues.jLabConsole, FocusEvent.FOCUS_GAINED);
        GlobalValues.jLabConsole.dispatchEvent(fe);    
            }
        });
                
        matrixPanel2.add(browAppend); matrixPanel2.add(bcolAppend); matrixPanel2.add(bdotProduct);
        matrixPanel3.add(bones);  matrixPanel3.add(bzeros);  matrixPanel3.add(brand);
        matrixPanel3.add(beye); matrixPanel3.add(binv); 
        matrixPanel3.add(butriag);
        matrixPanel4.add(breshape); matrixPanel4.add(bresample); matrixPanel4.add(bany);
        matrixPanel4.add(bfind); matrixPanel4.add(bisEmpty); matrixPanel4.add(band);
        matrixPanel5.add(bor); matrixPanel5.add(bxor);  matrixPanel5.add(bsum); 
        matrixPanel5.add(bdet); matrixPanel5.add(beig);  matrixPanel5.add(blu); matrixPanel5.add(bsvd);
        matrixPanel5.add(bqr);    
     
        add(matrixPanel1);       add(matrixPanel2);   add(matrixPanel3);       add(matrixPanel4);    add(matrixPanel5);   
        
   }
}

