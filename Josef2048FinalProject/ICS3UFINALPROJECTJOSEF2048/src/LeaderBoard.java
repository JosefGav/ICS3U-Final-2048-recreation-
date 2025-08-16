import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

/*
 * Leader board class made by @author Josef Gavronskiy (2023-12-28 -> 2024-01-21)
 * Reads and writes to leader board text files which store top 3 scores.
 * 
 * Recourses:
 * https://stackoverflow.com/questions/6714045/how-to-resize-jlabel-imageicon
 * Mr. Benum's reading and writing files video on classroom.
 * */

public class LeaderBoard {
	// file embedement object
	static FileEmbedment fe = new FileEmbedment();
	
	/**
	 * Uploads new scores to the leader board files. Each game mode (board size) has a unique leader board file.
	 * Postcondition: uploads the score to the leader board (top 3 scores) if possible. May re-arrange existing leader board file
	 * */
    public static void uploadNewScore(int score,int boardDimension) {
        int[] topScores = new int[4]; // has a length of 4 to accommodate for our new score
        
        File file = new File("highScore"+boardDimension+"x"+boardDimension+".txt"); // gets the text file corresponding to the board dimension
        
        PrintWriter out  = null;
        
        // check if the score file does not exist
        if (!file.exists()) {
        	try { // create a new print writer
                out = new PrintWriter(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        	
        	// since the file doesn't exist we create one and populate it with the player's score along with 2 lines of "0"s
        	out.println(score);
        	out.println(0);
        	out.println(0);
        	
        	out.close();
        	
        	return;
        }
        

        //  scanner will scan our text file
        Scanner input = null;
        try{
            input = new Scanner(file);
        }
        catch (FileNotFoundException e) { }

        // gets the integer value of each line of the text file. 
        for (int i = 0; i < 3; i++){
            topScores[i] = Integer.parseInt(input.nextLine());
        }
        input.close(); 
        
        // Adds our new score to our top scores array
        topScores[3] = score;

        Arrays.sort(topScores); // scores will be sorted from least to greatest
        
        // creates a new print writer
        try {
            out = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        // re update the high score file with the 3 rightmost scores from the topScores array
        for (int i = 1; i <= 3; i++) {
            out.println(topScores[4-i]);
        }

        out.close(); 
    }

   
    
    /**
     * Displays a JOptionPane representing the leader board
     * Precondition: a text file must exist for the respective board dimension
     * Postcondition: displays leader board (top 3)
     * */
    public static void show(int score, int boardDimension) {
        // stores the message that will be displayed in the JMessageDialog
        String message = "";

        // gets the text file corresponding to the board dimension
        // note that each board dimension game mode has a separate scores file due to varying difficulties
        File file = new File("highScore"+boardDimension+"x"+boardDimension+".txt"); // ex: highScore3x3.txt

        // stores the score at each line of the text file temporarily
        int tempScore = 0;

        // creates the scanner object to read the file
        Scanner input = null;
        try{
            input = new Scanner(file);
        }
        catch (FileNotFoundException e) { }
        
        
        // goes through each line of the high scores file and adds it to the message
        for (int i = 0; i < 3; i++){
            tempScore = Integer.parseInt(input.nextLine());
            // checks if your score is on the leader board
            if (tempScore==score) {
                // specifies that this is your score
                message += i+1+".   " + tempScore + " <- your score \n";
            } else {
                message += i+1+".   " + tempScore +"\n";
            }
        }


        // displays the leader board
        JOptionPane.showMessageDialog(
                null,
                message,
                "LeaderBoard",
                JOptionPane.INFORMATION_MESSAGE,
                // https://stackoverflow.com/questions/6714045/how-to-resize-jlabel-imageicon
                new ImageIcon((fe.returnImageIcon("trophy.jpg")).getImage().getScaledInstance(100,100, Image.SCALE_SMOOTH))); // resizes image icon
    }
}