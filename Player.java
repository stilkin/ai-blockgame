import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse the standard input according to the problem statement.
 **/
class Player {
    public static final int WIDTH = 6;
    public static final int HEIGHT = 12;
    public static final int FUTURE_BLOCKS = 8;
    public static boolean RUNNING = true;
    private GameField myField = new GameField();
    private GameField enField = new GameField();

    public static void main(String args[]) {
	final Scanner in = new Scanner(System.in);
	final Player aiPlayer = new Player();
	final int[][] myGrid = new int[WIDTH][HEIGHT];
	final int[][] enGrid = new int[WIDTH][HEIGHT];
	final int[][] nextBlocks = new int[FUTURE_BLOCKS][2];

	// game loop
	while (RUNNING) {

	    /* CONSOLE INPUT */
	    for (int f = 0; f < FUTURE_BLOCKS; f++) { // colors of new blocks
		nextBlocks[f][0] = in.nextInt();
		nextBlocks[f][1] = in.nextInt();
	    }

	    for (int y = HEIGHT - 1; y >= 0; y--) { // my grid
		final String row = in.next();
		final char[] letters = row.toCharArray();
		for (int x = 0; x < letters.length; x++) {
		    if (letters[x] == '.') {
			myGrid[x][y] = 0; // empty
		    } else if (letters[x] == '0') {
			myGrid[x][y] = 9; // skull
		    } else { // other colors
			myGrid[x][y] = Integer.parseInt("" + letters[x]);
		    }
		}
	    }

	    for (int y = HEIGHT - 1; y >= 0; y--) { // enemy grid
		final String row = in.next();
		final char[] letters = row.toCharArray();
		for (int x = 0; x < letters.length; x++) {
		    if (letters[x] == '.') {
			enGrid[x][y] = 0; // empty
		    } else if (letters[x] == '0') {
			enGrid[x][y] = 9; // skull
		    } else { // other colors
			enGrid[x][y] = Integer.parseInt("" + letters[x]);
		    }
		}
	    }

	    /* AI CODE */
	    aiPlayer.myField.setGrid(myGrid);
	    aiPlayer.enField.setGrid(enGrid);

	    aiPlayer.nextMove(nextBlocks);

	}

	in.close();
    }

    private void nextMove(final int[][] futureBlocks) {
	System.err.println(myField.toString());
	final int[] nextBlocks = futureBlocks[0];
	System.err.println("Next blocks: " + nextBlocks[0] + " " + nextBlocks[1]);

	final HashMap<GameField, Move> newMoves = new HashMap<GameField, Move>();
	for (int x = 0; x < WIDTH; x++) {

	    if (myField.getHeight(x) <= HEIGHT - 2) {
		final Move mv = new Move();
		mv.column = x;
		mv.orientation = Move.VERT;
		final GameField gameField = new GameField();

		gameField.setGrid(myField.grid);
		gameField.addToColumn(x, nextBlocks[0]);
		gameField.addToColumn(x, nextBlocks[1]);
		newMoves.put(gameField, mv);
		System.err.println(mv + ": " + gameField.printStats());
	    }
	    if (myField.getHeight(x) <= HEIGHT - 2) {
		final Move mv = new Move();
		mv.column = x;
		mv.orientation = Move.VERT_INV;
		final GameField gameField = new GameField();

		gameField.setGrid(myField.grid);
		gameField.addToColumn(x, nextBlocks[1]);
		gameField.addToColumn(x, nextBlocks[0]);
		newMoves.put(gameField, mv);
		System.err.println(mv + ": " + gameField.printStats());
	    }
	    if (x < WIDTH - 1) {
		if (myField.getHeight(x) <= HEIGHT - 1 && myField.getHeight(x + 1) <= HEIGHT - 1) {
		    final Move mv = new Move();
		    mv.column = x;
		    mv.orientation = Move.HORI;
		    final GameField gameField = new GameField();

		    gameField.setGrid(myField.grid);
		    gameField.addToColumn(x, nextBlocks[0]);
		    gameField.addToColumn(x + 1, nextBlocks[1]);
		    newMoves.put(gameField, mv);
		    System.err.println(mv + ": " + gameField.printStats());
		}
	    }
	    if (x > 1) {
		if (myField.getHeight(x) <= HEIGHT - 1 && myField.getHeight(x - 1) <= HEIGHT - 1) {
		    final Move mv = new Move();
		    mv.column = x;
		    mv.orientation = Move.HORI_INV;
		    final GameField gameField = new GameField();

		    gameField.setGrid(myField.grid);
		    gameField.addToColumn(x, nextBlocks[0]);
		    gameField.addToColumn(x - 1, nextBlocks[1]);
		    newMoves.put(gameField, mv);
		    System.err.println(mv + ": " + gameField.printStats());
		}
	    }
	}

	// sort moves
	final List<GameField> moves = new ArrayList<GameField>(newMoves.keySet());
	Collections.sort(moves);
	Collections.reverse(moves);

	final Move myMove = newMoves.get(moves.get(0));
	// Write an action using System.out.println()
	System.out.println(myMove); // "x": the column in which to drop your blocks
    }

    public static final class Move {
	public static final int HORI = 0;
	public static final int VERT = 1;
	public static final int HORI_INV = 2;
	public static final int VERT_INV = 3;
	public int column;
	public int orientation;

	@Override
	public String toString() {
	    return String.format("%d %d", column, orientation);
	}
    }

    public static final class GameField implements Comparable<GameField> {
	private final int[][] grid = new int[WIDTH][HEIGHT];
	private int[] heights = null;
	private int[] vInterrupts = null;
	private int[] hInterrupts = null;

	public GameField() {
	    resetCounters();
	}

	private void resetCounters() {
	    heights = null;
	    vInterrupts = null;
	    hInterrupts = null;
	}

	public void setGrid(final int[][] otherGrid) {
	    for (int x = 0; x < WIDTH; x++) {
		for (int y = 0; y < HEIGHT; y++) {
		    grid[x][y] = otherGrid[x][y];
		}
	    }
	    resetCounters();
	}

	private void addToColumn(final int x, final int color) {
	    for (int y = 0; y < HEIGHT; y++) {
		if (grid[x][y] == 0) {
		    grid[x][y] = color;
		    resetCounters();
		    break;
		}
	    }
	}

	private int popColumn(final int x) {
	    for (int y = HEIGHT - 1; y >= 0; y--) {
		if (grid[x][y] != 0) {
		    final int value = grid[x][y];
		    grid[x][y] = 0;
		    resetCounters();
		    return value;
		}
	    }
	    return 0;
	}

	private void calculateHeights() {
	    heights = new int[WIDTH];
	    for (int x = 0; x < WIDTH; x++) {
		for (int y = 0; y < HEIGHT; y++) {
		    if (grid[x][y] != 0) {
			heights[x]++;
		    }
		}
	    }
	}

	private void calculateInterrupts() {
	    vInterrupts = new int[WIDTH];
	    hInterrupts = new int[HEIGHT];

	    int lastColor;
	    for (int x = 0; x < WIDTH; x++) {
		lastColor = grid[x][0];
		for (int y = 1; y < HEIGHT; y++) {
		    if (grid[x][y] != lastColor) {
			if (grid[x][y] != 0 && lastColor != 0) {
			    vInterrupts[x]++;
			}
		    }
		    lastColor = grid[x][y];
		}
	    }

	    for (int y = 0; y < HEIGHT; y++) {
		lastColor = grid[0][y];
		for (int x = 1; x < WIDTH; x++) {
		    if (grid[x][y] != lastColor) {
			if (grid[x][y] != 0 && lastColor != 0) {
			    hInterrupts[y]++;
			}
		    }
		    lastColor = grid[x][y];
		}
	    }

	}

	public int getMaxHeight() {
	    int max = 0;
	    for (int x = 0; x < WIDTH; x++) {
		if (getHeight(x) > max) {
		    max = getHeight(x);
		}
	    }
	    return max;
	}

	public int getVISum() {
	    int sum = 0;
	    for (int x = 0; x < WIDTH; x++) {
		sum += getVInterrupts(x);
	    }
	    return sum;
	}

	public int getHISum() {
	    int sum = 0;
	    for (int y = 0; y < HEIGHT; y++) {
		sum += getHInterrupts(y);
	    }
	    return sum;
	}

	public int getHeight(final int x) {
	    if (heights == null) {
		calculateHeights();
	    }
	    return heights[x];
	}

	public int getVInterrupts(final int x) {
	    if (vInterrupts == null) {
		calculateInterrupts();
	    }
	    return vInterrupts[x];
	}

	public int getHInterrupts(final int y) {
	    if (hInterrupts == null) {
		calculateInterrupts();
	    }
	    return hInterrupts[y];
	}

	/**
	 * Compares this object with the specified object for order. Returns a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than
	 * the specified object.
	 */

	@Override
	public int compareTo(final GameField o) {
	    if (this.equals(o)) {
		return 0;
	    }

	    final int myInterrupts = this.getHISum() + this.getVISum();
	    final int otherInterrupts = o.getHISum() + o.getVISum();

	    if (myInterrupts > otherInterrupts) {
		return -1; // having more interrupts is a bad thing -> less than
	    } else if (myInterrupts < otherInterrupts) {
		return +1; // having fewer interrupts is a good thing -> better than
	    } else {
		if (this.getMaxHeight() > o.getMaxHeight()) {
		    return -1;
		} else if (this.getMaxHeight() < o.getMaxHeight()) {
		    return +1;
		}
		return 0;
	    }
	}

	@Override
	public String toString() {
	    String s = "";
	    for (int y = HEIGHT - 1; y >= 0; y--) {
		for (int x = 0; x < WIDTH; x++) {
		    s += grid[x][y];
		}
		s += "\n";
	    }
	    return s;
	}

	public String printStats() {
	    return String.format("%2d %2d %2d", getMaxHeight(), getHISum(), getVISum());
	}

    }

}
