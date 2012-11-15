package org.games.ukodus;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
//import android.view.Gravity;
//import android.widget.Toast;

public class Game extends Activity {
	private static final String TAG = "Ukodus";
	
	public static final String KEY_DIFFICULTY = "org.games.ukodus.difficulty";
	public static final int DIFFCULTY_1 = 0;
	public static final int DIFFCULTY_2 = 1;
	public static final int DIFFCULTY_3 = 2;
	public static final int DIFFCULTY_4 = 3;
	public static final int DIFFCULTY_5 = 4;

	/**
	 * symbolSets is an array of the symbols set that can be used:
	 * numbers,greek, hebrew, roman numerals, and wingdings
	 */
	private static final String symbolSets[][] = 
	{{"","\u0031", "\u0032", "\u0033", "\u0034", "\u0035", "\u0036", "\u0037", "\u0038","\u0039",},
		{"", "\u03B1", "\u03B2", "\u03B3", "\u03B4", "\u03B5", "\u03B6", "\u03B7", "\u03B8", "\u03B9"},
		{"", "\u05D0", "\u05D1", "\u05D2", "\u05D3", "\u05D4", "\u05D5", "\u05D6", "\u05D7", "\u05D8"},
		{"", "\u2150", "\u2151", "\u2152", "\u2153", "\u2154", "\u2155", "\u2156", "\u2157", "\u2158"},
		{"", "\u00A3", "\u00A4", "\u00A5", "\u00A6", "\u00A7", "\u00A8", "\u00A9", "\u00AA", "\u00AB"}
	};

	private int N;
	private String symbols[];
	private int used[][][][];
	private int puzzle[][][];
	private PuzzleView puzzleView;
	private int difficulty;

	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		this.difficulty = getIntent().getIntExtra(KEY_DIFFICULTY, DIFFCULTY_1);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		this.N = Integer.parseInt(prefs.getString("N", "4"));
		this.symbols = symbolSets[Integer.parseInt(prefs.getString("symbol", "0"))];
		this.used = new int[N][N][N][];
		createPuzzle();
		calculateUsedTiles();
		puzzleView = new PuzzleView(this);
		setContentView(puzzleView);
		puzzleView.requestFocus();
	}
	/**
	 * returns a new unique(not yet) puzzle 
	 * @param diff the difficulty level of the puzzle to be returned
	 * @return puzzle
	 */

	private void createPuzzle(){
		puzzle = new int[N][N][N];
		for (int i = 0; i < N ; i++)
			for (int j = 0; j < N; j++)
				for (int k = 0; k < N; k++)
					puzzle[i][j][k] = (i+j+k)%N + 1;
		// generate # of permutations : odd
		int numPermutations = N*2 + 1;
		permutePuzzle(numPermutations);
		removeRandomTiles();
	}

	// permutePuzzle takes a number of permutations
	void permutePuzzle(int n){
		Log.d(TAG, "Permuting puzzle " + n + " times");
		int m = n / 3;
		permuteDims(m);
		permuteRows(m);
		permuteCols(n-2*m);
		// Technically column-biased.  I'm fine with this.
	}
	void permuteCols(int n){
		Log.d(TAG, "Permuting columns " + n + " times");
		for(int i = 0; i < n; i++){
			double c1 = Math.random(), c2 = Math.random();
			while(c1 == c2){
				c2 = Math.random();
			}
			int x = (int) (c1*N), y = (int) (c2*N);
			switchCols(x, y);
		}
	}
	void permuteRows(int n){
		Log.d(TAG, "Permuting rows " + n + " times");
		for(int i = 0; i < n; i++){
			double r1 = Math.random(), r2 = Math.random();
			while(r1 == r2){
				r2 = Math.random();
			}
			int x = (int) (r1*N), y = (int) (r2*N);
			switchRows(x,y);
		}
	}
	void permuteDims(int n){
		Log.d(TAG, "Permuting dimensions " + n + " times");
		for(int i = 0; i < n; i++){
			double d1 = Math.random(), d2 = Math.random();
			while(d1 == d2){
				d2 = Math.random();
			}
			int x = (int) (d1*N), y = (int) (d2*N);
			switchDims(x,y);
		}
	}

	/*
	 * Each one takes a former and latter [row/column/stack]
	 * and swaps each value
	 */ 
	void switchCols(int former, int latter){
		int holder;
		Log.d(TAG, "Swapping cols: " + (former+1) + ", " + (latter+1));
		for (int i = 0; i < N; i++){
			for(int j = 0; j < N; j++){
				holder = puzzle[i][j][former];
				puzzle[i][j][former] = puzzle[i][j][latter];
				puzzle[i][j][latter] = holder;
			}
		}
	}
	void switchRows(int former, int latter){
		int holder;
		Log.d(TAG, "Swapping rows: " + (former+1) + ", " + (latter+1));
		for (int i = 0; i < N; i++){
			for(int j = 0; j < N; j++){
				holder = puzzle[i][former][j];
				puzzle[i][former][j] = puzzle[i][latter][j];
				puzzle[i][latter][j] = holder;
			}
		}
	}
	void switchDims(int former, int latter){
		int holder;
		Log.d(TAG, "Swapping dims: " + (former+1) + ", " + (latter+1));
		for (int i = 0; i < N; i++){
			for(int j = 0; j < N; j++){
				holder = puzzle[former][i][j];
				puzzle[former][i][j] = puzzle[latter][i][j];
				puzzle[latter][i][j] = holder;
			}
		}
	}

	void removeRandomTiles(){
		// generate # tiles to empty; empty them
		int random = (int) (Math.pow(N, 3) - (2 + (.2 * difficulty)) * Math.pow(N,2));
		Log.d(TAG, "removing " + random + " tiles");
		for (int i = 0; i < random; i++){
			int a = (int)(Math.random() * N),
			b = (int)(Math.random() * N),
			c = (int)(Math.random() * N);
			puzzle[a][b][c] = 0;
			Log.d(TAG, "removing tile "+i+": ["+a+"]["+b+"]["+c+"]");
		}

	}

	/**
	 * returns N, the dimension of the board
	 * @return N
	 */

	public int getN(){
		return this.N;
	}

	/**
	 * returns the value at location x,y,z
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */

	protected int getTile(int z, int y, int x){
		return puzzle[z][y][x];
	}

	/**
	 * returns true and sets location to value
	 * if the given value can be put in to the
	 *  given location legally.
	 *  returns false if the given value can not
	 *  bet set to location legally.
	 * 
	 * @param x 	the x index of location
	 * @param y 	the y index of location
	 * @param z		the z index of location
	 * @param value	the value to set in location if able
	 * @return	boolean value
	 */

	protected int setTileIfValid(int z, int y, int x, int value){
		int tiles[] = getUsedTiles(z,y,x);
		if(value != 0){
			for(int tile :tiles){
				if (tile == value){
					Log.d(TAG, "setTileIfValid: Invalid tile");
					return -1;
				}
			}
		}
		
		setTile(z, y, x, value);
		if (calculateUsedTiles()){
			Log.d(TAG, "setTileIfVaild: WIN");
			return 1;	
		}else{
			Log.d(TAG, "setTileIfVaild: Valid tile");
			return 0;
		}
	}

	/**
	 * sets the given location in puzzle to value
	 * 
	 * @param x 	the x index of the location
	 * @param y		the y index of the location
	 * @param z 	the z index of the location
	 * @param value the value to be set
	 */
	private void setTile(int z, int y, int x, int value){
		puzzle[z][y][x] = value;
	}

	/**
	 * returns an array of the symbols that already
	 * appear in the same row, col, or file for the 
	 * given location.
	 *  
	 * @param x the x index of the Location
	 * @param y the y index of the Location
	 * @param z the z index of the Location
	 * @return an array of used locations
	 * 
	 */
	protected int[] getUsedTiles(int z, int y, int x){
		return used[z][y][x];
	}


	/**
	 * updates used array.
	 * 
	 * @return true if puzzle is full
	 */

	private boolean calculateUsedTiles(){
		boolean b = true;
		for(int z = 0; z < N; z++){
			for (int y = 0; y < N ; y++){
				for (int x = 0; x < N; x++){
					used[z][y][x] = calculateUsedTiles(z,y,x);
					if (used[z][y][x].length != N)
						b = false;
				}
			}
		}
		return b;
	}

	/**
	 * returns a array of the used symbols for a given location
	 * checks to see which values appear in the row, col, and
	 * file for the location and returns them
	 * 
	 * @param x		the x index of location
	 * @param y		the y index of location
	 * @param z		the z index of location
	 * @return		array of used symbols. 
	 */

	private int[] calculateUsedTiles(int z ,int y, int x){
		int c[] = new int[N];
		//horizontal
		for (int i = 0; i < N; i++){
			int t = puzzle[z][y][i];
			if (t != 0)
				c[t-1] = t;
		}
		//vertical
		for (int i = 0; i < N; i++){
			int t = puzzle[z][i][x];
			if (t != 0)
				c[t-1] = t;
		}
		//deep
		for (int i = 0; i < N; i++){
			int t = puzzle[i][y][x];
			if (t != 0)
				c[t-1] = t;
		}
		//compress
		int nused = 0;
		for(int t : c){
			if(t != 0)
				nused++;
		}
		int c1[] = new int[nused];
		nused = 0;
		String uStr = "{";
		for (int t : c){
			if (t != 0){
				uStr += t + ",";
				c1[nused++] = t;
			}
		}
		uStr += "}";
		return c1;
	}

	public String puzzleToString(int puzzle[][][]){
		String str = "";
		for (int i = 0; i < N; i++){
			for(int j = 0; j < N; j++){
				for(int k = 0; k < N; k++)
					str += Integer.toString(puzzle[i][j][k]) + " ";
				str += "\n";
			}
			str += "++++++++++\n";
		}

		return str;
	}

	/**
	 * returns a string of the given value at location x, y, z
	 * @param z
	 * @param y
	 * @param x
	 * @return a String of the given value at Tile
	 */

	public String getTileString(int z, int y, int x) {
		int value = getTile(z,y,x);
		return symbols[value];
	}
	/**
	 * Returns the given symbol form the symbol set at index i
	 * @param i the index to be used
	 * @return a symbol from the symbol set at index i
	 */

	public String getSymbol(int i) {
		return symbols[i];
	}

}