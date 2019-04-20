package assign3;

import java.util.*;

/*
 * Encapsulates a Sudoku grid to be solved.
 * CS108 Stanford.
 */
public class Sudoku {
	// Provided grid data for main/testing
	// The instance variable strategy is up to you.

	// Provided easy 1 6 grid
	// (can paste this text into the GUI too)
	public static final int[][] easyGrid = Sudoku.stringsToGrid(
			"1 6 4 0 0 0 0 0 2",
			"2 0 0 4 0 3 9 1 0",
			"0 0 5 0 8 0 4 0 7",
			"0 9 0 0 0 6 5 0 0",
			"5 0 0 1 0 2 0 0 8",
			"0 0 8 9 0 0 0 3 0",
			"8 0 9 0 4 0 2 0 0",
			"0 7 3 5 0 9 0 0 1",
			"4 0 0 0 0 0 6 7 9");


	// Provided medium 5 3 grid
	public static final int[][] mediumGrid = Sudoku.stringsToGrid(
			"530070000",
			"600195000",
			"098000060",
			"800060003",
			"400803001",
			"700020006",
			"060000280",
			"000419005",
			"000080079");

	// Provided hard 3 7 grid
	// 1 solution this way, 6 solutions if the 7 is changed to 0
	public static final int[][] hardGrid = Sudoku.stringsToGrid(
			"3 7 0 0 0 0 0 8 0",
			"0 0 1 0 9 3 0 0 0",
			"0 4 0 7 8 0 0 0 3",
			"0 9 3 8 0 0 0 1 2",
			"0 0 0 0 4 0 0 0 0",
			"5 2 0 0 0 6 7 9 0",
			"6 0 0 0 2 1 0 4 0",
			"0 0 0 5 3 0 9 0 0",
			"0 3 0 0 0 0 0 5 1");


	public static final int SIZE = 9;  // size of the whole 9x9 puzzle
	public static final int PART = 3;  // size of each 3x3 part
	public static final int MAX_SOLUTIONS = 100;

	// Provided various static utility methods to
	// convert data formats to int[][] grid.

	/**
	 * Returns a 2-d grid parsed from strings, one string per row.
	 * The "..." is a Java 5 feature that essentially
	 * makes "rows" a String[] array.
	 * (provided utility)
	 * @param rows array of row strings
	 * @return grid
	 */
	public static int[][] stringsToGrid(String... rows) {
		int[][] result = new int[rows.length][];
		for (int row = 0; row<rows.length; row++) {
			result[row] = stringToInts(rows[row]);
		}
		return result;
	}


	/**
	 * Given a single string containing 81 numbers, returns a 9x9 grid.
	 * Skips all the non-numbers in the text.
	 * (provided utility)
	 * @param text string of 81 numbers
	 * @return grid
	 */
	public static int[][] textToGrid(String text) {
		int[] nums = stringToInts(text);
		if (nums.length != SIZE*SIZE) {
			throw new RuntimeException("Needed 81 numbers, but got:" + nums.length);
		}

		int[][] result = new int[SIZE][SIZE];
		int count = 0;
		for (int row = 0; row<SIZE; row++) {
			for (int col=0; col<SIZE; col++) {
				result[row][col] = nums[count];
				count++;
			}
		}
		return result;
	}


	/**
	 * Given a string containing digits, like "1 23 4",
	 * returns an int[] of those digits {1 2 3 4}.
	 * (provided utility)
	 * @param string string containing ints
	 * @return array of ints
	 */
	public static int[] stringToInts(String string) {
		int[] a = new int[string.length()];
		int found = 0;
		for (int i=0; i<string.length(); i++) {
			if (Character.isDigit(string.charAt(i))) {
				a[found] = Integer.parseInt(string.substring(i, i+1));
				found++;
			}
		}
		int[] result = new int[found];
		System.arraycopy(a, 0, result, 0, found);
		return result;
	}

	// Provided -- the deliverable main().
	// You can edit to do easier cases, but turn in
	// solving hardGrid.
	public static void main(String[] args) {
		Sudoku sudoku;
		sudoku = new Sudoku(hardGrid);

		System.out.println(sudoku); // print the raw problem
		int count = sudoku.solve();
		System.out.println("solutions:" + count);
		System.out.println("elapsed:" + sudoku.getElapsed() + "ms");
		System.out.println(sudoku.getSolutionText());
	}


	private void initGridAndMaps(int[][] ints){
		rowSets = new HashMap<Integer, HashSet<FullSpot>>();
		colSets = new HashMap<Integer, HashSet<FullSpot>>();
		sqrSets = new HashMap<Integer, HashSet<FullSpot>>();
		for(int i = 0; i < ints.length; i++){
			rowSets.put(i, new HashSet<FullSpot>());
			colSets.put(i, new HashSet<FullSpot>());
			sqrSets.put(i, new HashSet<FullSpot>());
		}
		for(int i = 0; i < ints.length; i++){
			for(int j = 0; j < ints[0].length; j++){
				grid[i][j] = ints[i][j];
				if(ints[i][j] != 0){
					FullSpot newSpot = new FullSpot(i, j, ints[i][j]);
					rowSets.get(i).add(newSpot);
					colSets.get(j).add(newSpot);
					sqrSets.get((i/3) * 3 + j/3).add(newSpot);
				}
			}
		}
	}

	private void initEmptyList(){
		emptySpotList = new ArrayList<EmptySpot>();
		for(int i = 0; i < grid.length; i++){
			for(int j = 0; j < grid[0].length; j++){
				if(grid[i][j] == 0){
					EmptySpot emptyS = new EmptySpot(i, j);
					emptySpotList.add(emptyS);
				}
			}
		}
		Collections.sort(emptySpotList, new EmptySpot(0, 0));
	}

	/**
	 * Sets up based on the given ints.
	 */
	public Sudoku(int[][] ints) {
		grid = new int[ints.length][ints[0].length];
		solveTime = -1;
		initGridAndMaps(ints);
		initEmptyList();
		solved = null;
		solutionCount = 0;
	}

	public Sudoku(String text){
		this(textToGrid(text));
	}

	private abstract class Spot {
		private int row, col, val;

		public Spot(int row, int col, int val){
			this.row = row;
			this.col = col;
			this.val = val;
		}


		public int getRow() {
			return row;
		}

		public int getCol() {
			return col;
		}

		public int getVal() {
			return val;
		}
	}

	private class FullSpot extends Spot{

		public FullSpot(int row, int col, int val){
			super(row, col, val);
		}

		//Ok to use this as hashcode, because it'll never be
		//repeated in rows, cols or squares.
		@Override
		public int hashCode() {
			return this.getVal();
		}

		@Override
		public boolean equals(Object obj) {
			if(this == obj) return true;
			if(obj == null || obj.getClass() != this.getClass())
				return false;
			if(((FullSpot)obj).getVal() == this.getVal()) return true;
			return false;
		}

	}

	private class EmptySpot extends Spot implements Comparator<EmptySpot> {

		private int valVariants;

		public EmptySpot(int row, int col){
			super(row, col, 0);
			getVariants();
		}

		@Override
		public int hashCode() {
			return this.getRow() * 1000 + this.getCol();
		}

		public int getVariants(){
			if(valVariants != 0) return valVariants;
			Set<FullSpot> neighbours = new HashSet<FullSpot>();
			neighbours.addAll(rowSets.get(this.getRow()));
			neighbours.addAll(colSets.get(this.getCol()));
			neighbours.addAll(sqrSets.get((this.getRow()/3) * 3 + this.getCol()/3));
			valVariants = SIZE - neighbours.size();
			return valVariants;
		}

		//Checks if the spot can be placed in grid
		public boolean isLegalPlacement(int value){
			if(grid[this.getRow()][this.getCol()] != 0) return false;
			FullSpot fp = new FullSpot(this.getRow(), this.getCol(), value);
			return (!rowSets.get(fp.getRow()).contains(fp)
					&& !colSets.get(fp.getCol()).contains(fp)
					&& !sqrSets.get((fp.getRow()/3) * 3 + fp.getCol()/3).contains(fp));
		}

		public int compare(EmptySpot a, EmptySpot b){
			return a.getVariants() - b.getVariants();
		}

	}

	private void copyTwoDArr(int[][] from, int[][] to){
		for(int i = 0; i < from.length; i++){
			for(int j = 0; j < from[0].length; j++){
				to[i][j] = from[i][j];
			}
		}
	}

	private void recurSolve(int index, int[][] solveGrid){
		if(solutionCount >= MAX_SOLUTIONS) return;
		if(index == emptySpotList.size() - 1){
			for(int i = 1; i <= SIZE; i++){
				if(emptySpotList.get(index).isLegalPlacement(i)){
					if(solved == null) {
						solved = new int[solveGrid.length][solveGrid[0].length];
						solveGrid[emptySpotList.get(index).getRow()][emptySpotList.get(index).getCol()] = i;
						copyTwoDArr(solveGrid, solved);
						solveGrid[emptySpotList.get(index).getRow()][emptySpotList.get(index).getCol()] = 0;
					}
					solutionCount++;
                }
			}
			return;
		}
		for(int i = 1; i <= SIZE; i++){
			if(emptySpotList.get(index).isLegalPlacement(i)){
				EmptySpot curSpot = emptySpotList.get(index);
				int curRow = curSpot.getRow();
				int curCol = curSpot.getCol();
				solveGrid[curRow][curCol] = i;
				rowSets.get(curRow).add(new FullSpot(curSpot.getRow(), curSpot.getCol(), i));
				colSets.get(curCol).add(new FullSpot(curSpot.getRow(), curSpot.getCol(), i));
				sqrSets.get((curRow /3)*3 + curCol /3).add(new FullSpot(curSpot.getRow(), curSpot.getCol(), i));
				recurSolve(index + 1, solveGrid);
				rowSets.get(curRow).remove(new FullSpot(curSpot.getRow(), curSpot.getCol(), i));
				colSets.get(curCol).remove(new FullSpot(curSpot.getRow(), curSpot.getCol(), i));
				sqrSets.get((curRow /3)*3 + curCol /3).remove(new FullSpot(curSpot.getRow(), curSpot.getCol(), i));
				solveGrid[curRow][curCol] = 0;
			}
		}
	}

	/**
	 * Solves the puzzle, invoking the underlying recursive search.
	 */
	public int solve() {
		int[][] solveGrid = new int[grid.length][grid[0].length];
		copyTwoDArr(grid, solveGrid);
		long startTime = System.currentTimeMillis();
		recurSolve(0, solveGrid);
		long endTime = System.currentTimeMillis();
		solveTime = endTime - startTime;
		return solutionCount;
	}

	@Override
	public String toString(){
		StringBuilder str = new StringBuilder();
		for(int i = 0; i < grid.length; i++){
			for(int j = 0; j < grid[0].length; j++){
				str.append(grid[i][j]);
				if(j != grid[0].length - 1){
					str.append(' ');
					if(j % PART == PART - 1) str.append("| ");
				}
			}
			if(i != grid.length - 1){
				str.append('\n');
				if(i % PART == PART - 1){
					for(int j = 0; j < (grid[0].length + 1) * 2 + 1; j++){
						str.append('-');
					}
					str.append("\n");
				}
			}
		}
		str.append('\n');
		return str.toString();
	}

	public String getSolutionText() {
	    if(solutionCount == 0) return "";
		return new Sudoku(solved).toString();
	}

	public long getElapsed() {
		return solveTime;
	}

	private long solveTime;
	private int[][] grid;
	private int[][] solved;
	private int solutionCount;
	private Map<Integer, HashSet<FullSpot>> rowSets;
	private Map<Integer, HashSet<FullSpot>> colSets;
	private Map<Integer, HashSet<FullSpot>> sqrSets;
	private List<EmptySpot> emptySpotList;

}

/*

"1 6 4 7 0 5 3 8 2
2 8 7 4 6 3 9 1 5
9 3 5 2 8 1 4 6 7

3 9 1 8 7 6 5 2 4
5 4 6 1 3 2 7 9 8
7 2 8 9 5 4 1 3 6

8 1 9 6 4 7 2 5 3
6 7 3 5 2 9 8 4 1
4 5 2 3 1 8 6 7 9"
 */
