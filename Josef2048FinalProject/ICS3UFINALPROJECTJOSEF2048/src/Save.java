import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

/*
 * A program for saving a game in progress
 * @author Josef Gavronskiy (2023-12-28 -> 2024-01-21)
 * 
 * Recourses:
 * Mr. Benum's text file tutorials.
 */
public class Save {
    static int rows;
    static int cols;

    /**
     * Returns a grid representing the state of the board when you last saved it 
     * Postcondition: Returns a grid representing the state of the board when you last saved it 
     * */
    public static int[][] getLastSave() {
        File textFile = new File("lastSave.txt"); 
        
        // checks if the file doesn't exist
        if (!textFile.exists()) {
        	// returns a basic 4x4 board since the last save wasn't found
			int[][] returnArr = new int[4][4];
				
			for (int r = 0; r < 4; r++) {
				for (int c = 0; c < 4; c++) {
					if (r == 0 && c == 0 || r == 2 && c == 2) { // 2 tiles have to be populated by 2s
						returnArr[r][c] = 2;
					} else {
						returnArr[r][c] = 0;
					}
	            }
            }
			
			rows = 4;
			cols = 4;
			
			return returnArr;
        }
        
        // makes a scanner object
        Scanner input = null;
        try{
            input = new Scanner(textFile);
        }
        catch (FileNotFoundException e) { }
        
        
        String line;             // temporarily stores each line
        String[] row; 			 // temporarily stores each line as an array of tokens
        int[] intRow; 			 // temporarily stores each line as an array of integers
        int boardDimension = 0;  // stores the board's dimensions
        
                
        line = input.nextLine(); 		     // gets the first line of the save
        row = line.split(" ");      	     // makes each token into an array
        boardDimension = row.length;         // sets the board dimension based on the length of the row
        
        // creates a copy of row but as an integer 
        intRow = new int[boardDimension];    
        for (int c = 0; c < boardDimension; c++) {
            intRow[c] = Integer.parseInt(row[c]);
        }
        
        
        // initialize the grid with the newly initialized board dimension 
        int[][] grid = new int[boardDimension][boardDimension];
        
        // sets the first row of the grid 
        grid[0] = Arrays.copyOf(intRow,intRow.length); // Arrays.copOf returns an identically array

        // read remaining lines from the text file
        for(int lineNumber = 2; lineNumber <= boardDimension; lineNumber++){
            line = input.nextLine(); 					// gets next line of the save
            row = line.split(" ");						// makes each token into an array
            
            // creates an integer copy of the row
            for (int c = 0; c < row.length; c++) {
                intRow[c] = Integer.parseInt(row[c]);	
            }
            
            // sets a row of the grid
            grid[lineNumber-1] = Arrays.copyOf(intRow,intRow.length);
        }
        
        input.close();

        // sets the saves rows and columns
        rows = boardDimension;
        cols = boardDimension;
        
        // returns the grid array
        return grid;
    }
    
    /**
     * Gets the score of your game when you last saved it
     * Post-condition: Returns the score of your game when you last saved it
     * */
    public static int getLastSaveScore(int boardDimension) {
    	// this is the line number of the text file storing the SCORE
        int targetLineNumber = boardDimension + 1; 
        
        
        File textFile = new File("lastSave.txt"); 
       
        if (!textFile.exists()) {
            return 0; // if it is not found we assume that the score is zero. 
        }

        
        // creates scanner object
        Scanner input = null;
        try{
            input = new Scanner(textFile);
        }
        catch (FileNotFoundException e){

        }

        // counter
        int lineCount = 1;
        
        // reads lines until the desired line is reached
        while (lineCount != targetLineNumber) {
            lineCount ++;
            input.nextLine(); 
        }
        
        int score = Integer.parseInt(input.nextLine()); // integer value of the last line of the text file stores the score

        input.close();
        
        return score; 
    }

    /**
     * Saves your game 
     * Post-condition: updates a lastSave.txt file with the grid. Each index separated by spaces and each row separated by new line. The last line contains the score.
     * */
    public static void newSave(int[][] grid, int score) {
        File file = new File("lastSave.txt");
        PrintWriter out  = null;
        
        // creates a print writer
        try {
            out = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        
        // writes to the text file row by row
        for (int row = 0; row < grid.length; row++) {
            out.println(joinIntsAsString(grid[row]));
        }
        
        // writes the score on the last line of the file
        out.print(score);

        // close input
        out.close();
    }
    
    /**
     * Converts integer array to a string. Ex: {1,2,3} -> "1 2 3 "
     * Postcondition: returns the integer array as a string
     * */
    public static String joinIntsAsString(int[] nums) {
        String result = "";
        for (int el : nums) {
            result += el + " ";
        }
        return result;
    }
}