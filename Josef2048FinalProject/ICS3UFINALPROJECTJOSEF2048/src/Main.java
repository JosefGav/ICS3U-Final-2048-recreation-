import javax.swing.*;
import java.io.IOException;

/*   Animated recreation of popular puzzle game 2048, with the ability to select game mode.
 *   Uses WASD or Swipe to control the board.
 *   Implements reading and writing to text files for leader board tracking and saving game in progress 
 *   
 *   Made by Josef Gavronskiy (2023-12-28 -> 2024-01-21)
 *   
 *   Recourses: 
 *   https://docs.oracle.com/javase/tutorial/uiswing/components/dialog.html
 *   https://www.javatpoint.com/post/java-system-exit-method
 *   https://stackoverflow.com/questions/1234912/how-to-programmatically-close-a-jframe
 *   */

public class Main  {
    static Board b;

    public static void main(String[] args) throws InterruptedException, IOException {
        FileEmbedment fe = new FileEmbedment();

        // each iteration of the loop represents one game (including game mode selection, post game GUI, etc)
        while (true) {

            // Asks if user wants to continue from their last save
            // The user clicking on yes returns 0, no returns 1, nothing returns -1
            int yesNo = JOptionPane.showConfirmDialog(
                    null,
                    "Would You Like To Continue From Where You Last Left Off?",
                    "RESUME GAME",
                    JOptionPane.YES_NO_OPTION);
            
            // if b!= null, then a board object exists. that means that the user has already played the game at least once
            // in order to keep the user experience clean the window of said board is disposed.
            // dispose is called upon b's parent JFrame disposing of everything within that window
            if (b != null) b.f.dispose();

            boolean continueFromLastSave = false; // program assumes they will not continue from their last save unless stated otherwise

            // yesNo is equal to zero 0 only when the user clicks yes.
            // If the user presses no or presses the "x" button on the option dialog continueFromLastSave will be assumed false.
            if (yesNo == 0) {
                continueFromLastSave = true;
            }


            // checks if the user would like to continue from their last save
            if (continueFromLastSave) {
                // Instantiates a board object, restoring game information from the last save.
                b = new Board(Save.getLastSave(), Save.rows, Save.cols);
                // score isn't restored by default when calling the constructor
                b.score = Save.getLastSaveScore(Save.rows); // Save.columns would also work
                
            } else { // creates a completely new game

                // will be later set to the dimensions of the board
                int boardDimension = 0;

                // https://docs.oracle.com/javase/tutorial/uiswing/components/dialog.html
                // array of all the all the board size options available
                String[] possibilities = {"3x3", "4x4", "5x5", "6x6", "7x7", "8x8","11x11 (CRAZY MODE)"};


                // Input dialog requests a board size from the user displaying the possibilities[] list as a drop down menu.
                String result = (String) JOptionPane.showInputDialog(
                        null,
                        "Choose A Board Size",
                        "CONFIG",
                        JOptionPane.PLAIN_MESSAGE,
                        fe.returnImageIcon("nerdydog.jpg"), 
                        possibilities,
                        "4x4"); // defaults to 4x4
                
                // if the user exits the input dialog result would be null
                // indicating the user no longer wants to play the program resulting in the program shutdown
                if (result == null) {
                    System.exit(0); // https://www.javatpoint.com/post/java-system-exit-method
                }

                // converts result to an integer
                // example: "8x8" -> "8". and then converts it to an integer
                if (result.length()==3) { 
                	boardDimension = Integer.parseInt(result.substring(0, 1));
                // for larger board sizes:
                // example: "20x20" -> "20". and then converts it to an integer
                } else { 
                	boardDimension = Integer.parseInt(result.substring(0, 2)); 
                }

                // instantiate a board object
                b = new Board(boardDimension, boardDimension); // creates the board
            }
            
            // how to play information
            howToPlayDialog();
            
            // stores the direction (W,A,S,D). As well as key pressed (ex:  i)
            char dir = ' ';

            // Game loop 
            while (true) {
                // gets key pressed (W,A,S,D)
                dir = b.getKey();

                // ensures b.getKey isn't skipped over
                Thread.sleep(1);


                // shifts left if the 'a' key has been pressed.
                if (dir == 'a') {
                    boolean moveHasOccurred = b.shiftLeft();

                    // if any movement has occurred within the board then we can add a random tile to the board
                    if (moveHasOccurred) {
                        b.repaint();
                        Thread.sleep(25); // sleep makes the new tile appearing identifiable
                        b.addRandomTile();
                    }
                    b.repaint();
                }
                // shifts right
                else if (dir == 'd') {
                    boolean moveHasOccurred = b.shiftRight();

                    // if any movement has occurred within the board then we can add a random tile to the board
                    if (moveHasOccurred) {
                        b.repaint();
                        Thread.sleep(25); // sleep makes the new tile appearing identifiable
                        b.addRandomTile();
                    }
                    b.repaint();
                }
                // shifts down
                else if (dir == 's') {
                    boolean moveHasOccurred = b.shiftDown();

                    // if any movement has occurred within the board then we can add a random tile to the board
                    if (moveHasOccurred) {
                        b.repaint();
                        Thread.sleep(25); // sleep makes the new tile appearing identifiable
                        b.addRandomTile();
                    }
                    b.repaint();
                }
                // shifts up
                if (dir == 'w') {
                    boolean moveHasOccurred = b.shiftUp();

                    // if any movement has occurred within the board then we can add a random tile to the board
                    if (moveHasOccurred) {
                        b.repaint();
                        Thread.sleep(25); // sleep makes the new tile appearing identifiable
                        b.addRandomTile();
                    }
                    b.repaint();
                }
                
                // displays information relating to game controls
                if (dir == 'i') { // direction stores direction / key pressed
                    howToPlayDialog();
                }
                
                // checks if there is no move available 
                if (!b.nextMoveAvailable()) {
                	// sets the game to over
                	b.gameOver = true;
                	// makes the save button invisible
                	b.save.setVisible(false);
                	
                	break; // breaks out of the game loop
                }
            }
            
            // ========================= POST GAME GUI  =============================
            
            
            // delay and then show upload your score to the leader board
            Thread.sleep(100);
            LeaderBoard.uploadNewScore(b.score, b.getRows());

            // stores the options for the end game screen 
            String[] options = {"LEADERBOARD",
                                "EXIT",
                                "PLAY AGAIN"};
            
            // loops until the user presses exit or "x"
            while (true) {
            	// done:
            	// leader board 0
            	// exit 	  +-1 (1 for exit button, -1 for x button)
            	// play again   2 
            	
            	// shows a Leader Board, Exit, Play Again
	            int done = JOptionPane.showOptionDialog(null,
	                "Congrats, Your Score Is " + b.score,
	                "END SCREEN",
	                JOptionPane.YES_NO_CANCEL_OPTION,
	                JOptionPane.QUESTION_MESSAGE,
	                fe.returnImageIcon("screaming-icon.gif"),
	                options,
	                options[2]);
	           
	            if (done==0) { // shows leader board
	                LeaderBoard.show(b.score, b.getRows()); // (getRows  / getCols is the same)
	            } else if (done == 1 || done == -1) { // exits
	            	System.exit(0);
	            } else { // breaks out of the loop (NEW GAME)
	            	break; 
	            }
            }
        }
    }
    
    /**
     * Testing method prints a 2D array
     * Precondition: array must not be null
     * Postcondition: prints a 2D array. Original array is not modified.
     * */
    public static void print2D ( int[][] arr){
        for (int row = 0; row < arr.length; row++) {
            for (int element : arr[row]) {
                System.out.print(element + ", ");
            }
            System.out.println();
        }
    }
    /**
     * Testing method prints a specified row (index) of a 2D array
     * Precondition: array must not be null, index must be within the bounds of the array length
     * Postcondition: prints a row (index) of an array. Original array is not modified.
     * */
    public static void print2D ( int[][] arr, int index){
        System.out.print("row: " + index + "\n");
        for (int element : arr[index]) {
            System.out.print(element + ", ");
        }
        System.out.println();
    }
    
    /**
     * Displays intstructions on how to play the game.
     * */
    public static void howToPlayDialog() {
    	// how to play information
        JOptionPane.showMessageDialog(
        		null,
        		"Use WASD or swipe your mouse to move tiles on the board."
        		+ "\n\nClicking \"W\", for example, shifts ALL the tiles on the board up."
        		+ "\n\nTo swipe on desktop simply press down, move your mouse at least a tile away and release."
        		+ "\n\nFor touch screen users simply swipe as if you are playing a mobile game."
        		+ "\n\nAdjacent tiles combine their values when merged. "
        		+ "\n\nGet the highest score you can to end up on the leaderboard!\n\n"
        		+ "\n\nPress \"i\" at any point to play this message again." 
        		);
    }
    
}