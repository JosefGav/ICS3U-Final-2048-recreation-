import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

/**
 * A program for demonstrating file objects.
 */
public class Save {
    static int rows;
    static int cols;
    // maybe make the last line of board dimension score the score *****************------------------
    public static int[][] getLastSave() {
        File textFile = new File("lastSave.txt"); // from the default location
        if (textFile.exists()) {
            System.out.println("File \""+ textFile.getName() +"\" exists.");
        } else {
            System.out.println("Could not find the file.");
            System.exit(0);
        }
        Scanner input = null;
        try{
            input = new Scanner(textFile);
        }
        catch (FileNotFoundException e){

        }
        String line;
        int[] intRow=new int[4];
        int boardDimension = 0;
        if (input.hasNextLine()) {
            line = String.valueOf(input.nextLine());
            String[] row = line.split(" ");
            boardDimension = row.length; // assuming the board is N x N in dimensions
            intRow = new int[boardDimension];
            for (int c = 0; c < boardDimension; c++) {
                intRow[c] = Integer.parseInt(row[c]);
            }
        }

        int[][] grid = new int[boardDimension][boardDimension];

        grid[0] = Arrays.copyOf(intRow,intRow.length);

        //read lines from the text file
        for(int lineNumber = 2; lineNumber <= boardDimension; lineNumber++){
            line = String.valueOf(input.nextLine()); // https://www.javatpoint.com/java-int-to-string#:~:text=We%20can%20convert%20int%20to,method%2C%20string%20concatenation%20operator%20etc.
            String[] row = line.split(" ");
            for (int c = 0; c < row.length; c++) {
                intRow[c] = Integer.parseInt(row[c]);
            }

            grid[lineNumber-1] = Arrays.copyOf(intRow,intRow.length);
        }

        rows = boardDimension;
        cols = boardDimension;

        return grid;
    }
    public static int getLastSaveScore(int boardDimension) {
        int targetLineNumber = boardDimension + 1;
        File textFile = new File("lastSave.txt"); // from the default location
        if (textFile.exists()) {
            System.out.println("File \""+ textFile.getName() +"\" exists.");
        } else {
            System.out.println("Could not find the file.");
            System.exit(0);
        }

        Scanner input = null;
        try{
            input = new Scanner(textFile);
        }
        catch (FileNotFoundException e){

        }

        int lineCount = 1;

        while (lineCount != targetLineNumber) {
            lineCount ++;
            input.nextLine(); // skips through lines until it reaches the last line 0f the text file containing the previous score
        }

        return  Integer.parseInt(input.nextLine()); // the next and final line of the text file contains the previous saves score ex. "21", which will be converted to integer 21 and returned
    }

    public static void newSave(int[][] grid, int score) {
        File file = new File("lastSave.txt");
        PrintWriter out  = null;

        try {
            out = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        for (int row = 0; row < grid.length; row++) {
            System.out.println(joinIntsAsString(grid[row]));
            out.println(joinIntsAsString(grid[row]));
        }
        out.print(score);

        System.out.println("finished");
        out.close();
    }

    public static String joinIntsAsString(int[] nums) {
        String result = "";
        for (int el : nums) {
            result += el + " ";
        }
        return result;
    }
}