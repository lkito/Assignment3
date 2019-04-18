package assign3;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;


 public class SudokuFrame extends JFrame {
 	 private JPanel panel;
 	 private JTextArea puzzle;
	 private JTextArea solution;
	 private JButton checkButton;
	 private JCheckBox aCheck;
	 private Sudoku sudoku;

	 private void initLayout(){
		 setLayout(new BorderLayout(4, 4));

		 panel = new JPanel();

		 puzzle = new JTextArea(15, 20);
		 puzzle.setBorder(new TitledBorder("Puzzle"));
		 solution = new JTextArea(15, 20);
		 solution.setBorder(new TitledBorder("Solution"));

		 checkButton = new JButton("Check");
		 aCheck = new JCheckBox("Auto Check");
		 aCheck.setSelected(true);

		 add(puzzle, BorderLayout.CENTER);
		 add(solution, BorderLayout.EAST);
		 panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		 panel.add(checkButton, BorderLayout.SOUTH);
		 panel.add(aCheck);
		 add(panel, BorderLayout.SOUTH);

		 // Could do this:
		 setLocationByPlatform(true);
		 setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 pack();
		 setVisible(true);
	 }

	 private void solveProblem(){
		try {
			sudoku = new Sudoku(puzzle.getText());
			int nSolution = sudoku.solve();
			String solutionStr = sudoku.getSolutionText();
			long elapsedTime = sudoku.getElapsed();
			if(nSolution == 0){
				solutionStr = "";
			} else {
				solutionStr += "\n";
			}
			solution.setText(solutionStr + "Solutions: "
					+ nSolution + '\n' + "Elapsed: " + elapsedTime + " ms");
		} catch (Exception ex){
			solution.setText("Parsing problem");
		}
	 }

	 private void initListeners(){
	 	puzzle.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent documentEvent) {
				if(aCheck.isSelected()) solveProblem();
			}

			@Override
			public void removeUpdate(DocumentEvent documentEvent) {
				if(aCheck.isSelected()) solveProblem();
			}

			@Override
			public void changedUpdate(DocumentEvent documentEvent) {
				if(aCheck.isSelected()) solveProblem();
			}
		});

		checkButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				solveProblem();
			}
		});
	 }

	 public SudokuFrame() {
		super("Sudoku Solver");
		initLayout();
		initListeners();
	}
	
	
	public static void main(String[] args) {
		// GUI Look And Feel
		// Do this incantation at the start of main() to tell Swing
		// to use the GUI LookAndFeel of the native platform. It's ok
		// to ignore the exception.
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) { }
		
		SudokuFrame frame = new SudokuFrame();
	}

}
