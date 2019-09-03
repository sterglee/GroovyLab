
package gExec.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;


public class MatrixFunctionToolbar  extends JPanel {
        JButton bones, bzeros, beye, binv, butriag, breshape,
                bany, bfind, bisEmpty, 
                band, bor, bxor, bsum,
                bdet, beig, blu, bsvd,    
                bqr;
        
    public MatrixFunctionToolbar() {
        JFrame frame = new JFrame("gLab Matrix Functions ");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocation(100, 200);
        
        bones = new JButton("ones");
        bones.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
          JOptionPane.showMessageDialog(null, "creating matrices that consist of ones  \n (e.g.: ones(2) will return a 2-by-2 matrix of ones)", "Mathematical Functions - ones(N, M)", JOptionPane.INFORMATION_MESSAGE);
            }
        });
      
        bzeros = new JButton("zeros");
        bzeros.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
          JOptionPane.showMessageDialog(null, "creating matrices that consist of zeros  \n (e.g.: zeroes(2,3) will return a 2-by-3 matrix of zeroes)", "Mathematical Functions - sin(x)", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        beye = new JButton("eye");
        beye.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
          JOptionPane.showMessageDialog(null, "creating matrices that consist of zeros \n"+
                   "everywhere except in the diagonal. The diagonal consists of ones.  \n\n"+
                   "e.g.: eye(3) will return a 3-by-3 matrix [1,0,0;0,1,0;0,0,1], \n"+
                   "eye(4,3) will return a 4-by-3 matrix with diagonal set to 1 ", 
                  "Mathematical Functions - eye(N), eye(N,M)", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        binv = new JButton("inv");
        binv.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
          JOptionPane.showMessageDialog(null, "determining the inverse of a matrix",
                  "Mathematical Functions - inv(A)", JOptionPane.INFORMATION_MESSAGE);
            }
        });
       
        butriag = new JButton("utriag");
        butriag.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
          JOptionPane.showMessageDialog(null, "converting a matrix into upper triangular form", "Mathematical Functions - utriag(Matrix A)", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        breshape = new JButton("reshape");
        breshape.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
          JOptionPane.showMessageDialog(null, "reshaping matrices \n"+
                  "(e.g. reshape([1,2;3,4;5,6],2,3) return [1,5,4;3,2,6]) \n"+
                  "The original matrix is read column for column and rearranged \n"+
                  "to a new dimension  ", "Mathematical Functions - reshape(Matrix A, int M, int N)",
                  JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        bany = new JButton("any");
        bany.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
          JOptionPane.showMessageDialog(null, "returns 1 if any element of the argument is nonzero", "Mathematical Functions - any(A)", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        bfind = new JButton("find");
        bfind.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
          JOptionPane.showMessageDialog(null, "return a column vector which points to all nonzero \n"+
                  "elements of the function arguments \n\n"+
                  "e.g. find([1,2,3;0,0,4]) returns [1,3,5,6]", "Mathematical Functions - any(Matrix A)", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        
        bisEmpty = new JButton("isEmpty");
        bisEmpty.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
          JOptionPane.showMessageDialog(null, "checking if a matrix is empty (no number or string)", "Mathematical Functions - empty(Matrix A)", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        band = new JButton("and");
        band.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
          JOptionPane.showMessageDialog(null, "computing the AND of two matrices", "Mathematical Functions: Matrix C = and(Matrix A, Matrix B)", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        bor = new JButton("or");
        bor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
          JOptionPane.showMessageDialog(null, "computing the OR of two matrices", "Mathematical Functions: Matrix C = or(Matrix A, Matrix B)", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        bxor = new JButton("xor");
        bxor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
          JOptionPane.showMessageDialog(null, "creating matrices that consist of ones  \n (e.g.: ones(2) will return a 2-by-2 matrix)", "Mathematical Functions - sin(x)", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        bsum = new JButton("sum");
        bsum.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
          JOptionPane.showMessageDialog(null, "the sum of all values within the matrix or structure\n"+
                                "sums are computed columnwise on matrices", 
                                "Mathematical Functions - sum(A)", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        bdet = new JButton("det");
        bdet.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
          JOptionPane.showMessageDialog(null, "computes the determinant of a square matrix A \n", "Mathematical Functions - d = det(Matrix A)", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        beig = new JButton("eig");
        beig.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
          JOptionPane.showMessageDialog(null, "computing eigenvalues and eigenvectors of an array ", "Mathematical Functions - eig(A)", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        blu = new JButton("lu");
        blu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
          JOptionPane.showMessageDialog(null, "For an m-by-n matrix A with m >= n, the LU decomposition is \n"+
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
                  "x       = lu (A,B) as a solution to A*X = B; ", 
                  "Mathematical Functions - lu(Matrix A)", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        
        bsvd = new JButton("svd");
        bsvd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
          JOptionPane.showMessageDialog(null, " Singular Value Decomposition: \n\n"+
        "For an m-by-n matrix A with m >= n, the singular value decomposition is \n"+
        "an m-by-n orthogonal matrix U, an n-by-n diagonal matrix S, and \n"+
        "an n-by-n orthogonal matrix V so that A = U*S*V'. \n\n"+
        "The singular values, sigma[k] = S[k][k], are ordered so that \n"+
        "sigma[0] >= sigma[1] >= ... >= sigma[n-1]. \n"+
        "The singular value decompostion always exists, so the constructor will \n"+
        "never fail.  The matrix condition number and the effective numerical \n"+
        "rank can be computed from this decomposition. \n\n"+
        "usage: s = svd(A) \n"+
        "[U,S,V]=svd(A)",
        "Mathematical Functions - svd(Matrix A)", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        bqr = new JButton("qr");
        bqr.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
          JOptionPane.showMessageDialog(null, " QR Decomposition. \n\n"+
        "For an m-by-n matrix A with m >= n, the QR decomposition is an m-by-n \n"+
        "orthogonal matrix Q and an n-by-n upper triangular matrix R so that \n"+
        "A = Q*R. \n"+
        "The QR decompostion always exists, even if the matrix does not have \n"+
        "full rank, so the constructor will never fail.  The primary use of the \n"+
        "QR decomposition is in the least squares solution of nonsquare systems \n"+
        "of simultaneous linear equations.  This will fail if isFullRank() \n"+
        "returns false.", 
         "Mathematical Functions - qr(Matrix A)", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        Container box = Box.createHorizontalBox();
        box.add(bones);  box.add(bzeros); box.add(beye); box.add(binv); 
        box.add(butriag); box.add(breshape); box.add(bany);
        box.add(bfind); box.add(bisEmpty); box.add(band);
        box.add(bor); box.add(bxor);  box.add(bsum); 
        box.add(bdet); box.add(beig);  box.add(blu); box.add(bsvd); box.add(bqr);    
                
        frame.getContentPane().add(box, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    
   }
}

