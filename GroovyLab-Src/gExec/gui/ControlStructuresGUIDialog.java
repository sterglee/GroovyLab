package gExec.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**Simple dialog extension that creates a new non-modal and centered dialog.*/
public class ControlStructuresGUIDialog extends JFrame
{
	private static final int LEFT = 50;
        private JPanel   buttonsPanel;
	private JButton buttonIfThen;
        private JButton buttonForTo;
        private JButton buttonMatForTo;
	private JButton buttonWhileDo;
	private JButton buttonSwitch;
	private JTextArea  explainText;
        private String explainStr="you can press a button to get the corresponding help";

	public ControlStructuresGUIDialog()
	{
		super("Control Structures of gLab");
                this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);      
                buttonsPanel = new JPanel(new GridLayout(5,1));
		setLayout(new GridLayout(2,1));
		setSize(400, 500);
		// Let's initialize the buttons
		buttonIfThen = new JButton("If - Then - Else");
		buttonIfThen.setFont(new Font("Dialog", Font.BOLD, 16));
                buttonIfThen.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        explainStr = "grade = 6;  base=5; \n " +
                                "  result= \" Evaluation Test: \" ;  \n"+ 
   "if (grade < base) {  \n"+
  " diff = grade - base;  \n"+
  "result = result+\" Not Passed. You need \"+diff+\" points yet\"; \n"+
  "disp(result); \n"+
  "} \n"+
 "else { \n"+
  "diff = grade - base; \n"+
  "result = result +\" Passed. You have \"+diff+\" points in excess of required\"; \n"+
  "disp(result); \n"+
" } \n";
                        explainText.setText(explainStr);
                    }
                });
                
                buttonForTo = new JButton("For - To");
		buttonForTo.setFont(new Font("Dialog", Font.BOLD, 16));
                buttonForTo.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        explainStr = "for (k=1; k<20; k++), \n { \n str = \"forTest \" + k; \n   disp(str); \n } \n  \n";
                        explainText.setText(explainStr);
                    }
                });
                
                buttonMatForTo = new JButton("Matlab For - To");
		buttonMatForTo.setFont(new Font("Dialog", Font.BOLD, 16));
                buttonMatForTo.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
 explainStr =  "y=0; \n  for x=[1,2,3,4], \n { \n y=y+x; \ndisp(\"x=\" + x); \n disp(\"y=\" + y);  \n	} \n  \n";
                        explainText.setText(explainStr);
                    }
                });
                
		buttonWhileDo = new JButton("While - Do");
		buttonWhileDo.setFont(new Font("Dialog", Font.BOLD, 14));
		buttonWhileDo.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
     explainStr =   " upLimit = 50; \n sum = 0; \n elems = rand(1,200); \n"+
         " k=1; \n while (sum < upLimit) && (k<200) { \n"+
         " sum = sum+elems(k); \n  k=k+1; \n  }; \n"+
          "disp('added '+k+' elements until sum exceeds '+upLimit); \n";
             
             explainText.setText(explainStr);
                    }
                });
                
		buttonSwitch = new JButton("Switch");
                buttonSwitch.setFont(new Font("Dialog", Font.BOLD, 14));
		
                explainText = new JTextArea(explainStr);
                buttonSwitch.addActionListener(new ActionListener() {
                     public void actionPerformed(ActionEvent e) {
     explainStr = "switch(x) \n"+
             "case(1): \n"+
             "disp(\"your choice is x=1\"); \n"+
             "case(2): \n"+
             "disp(\"your choice is x=2\"); \n"+
             "case(3): \n"+
             "disp(\"your choice is x=3\"); \n"+
             "otherwise: \n"+
             "disp(\"invalid x\");\n"+
             "disp(x); \n"+
             "; \n";
             
     explainText.setText(explainStr);
                    }
                });
              
                buttonsPanel.add(buttonIfThen);
                buttonsPanel.add(buttonForTo);
                buttonsPanel.add(buttonMatForTo);
		buttonsPanel.add(buttonWhileDo);
		buttonsPanel.add(buttonSwitch);
                this.add(buttonsPanel);
                this.add(explainText);
		setVisible(true);
	}
}
