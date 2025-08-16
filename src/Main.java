import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;
import java.util.Scanner;

public class Main extends WindowAdapter {
    static Board b;

    public static void main(String[] args) throws InterruptedException {
        Scanner input = new Scanner(System.in);

        // Asks if user wants to continue from their last save
        // The user clicking on yes returns 0, no returns 1, nothing returns -1
        int yesNo = JOptionPane.showConfirmDialog(
                null,
                "Would You Like To Continue From Where You Last Left Off?",
                "RESUME GAME",
                JOptionPane.YES_NO_OPTION);

        boolean continueFromLastSave = false; // program assumes they will not continue from their last save unless stated otherwise

        // yesNo is equal to zero 0 only when the user clicks yes.
        // If the user presses no or presses the "x" button on the option dialog continueFromLastSave will be assumed false.
        if (yesNo == 0) {
            continueFromLastSave = true;
        }


        // checks if the user would like to continue from their last save
        if (continueFromLastSave) {
            // Instantiates a board object, restoring game information from the last save.
            b = new Board(Save.getLastSave(),Save.rows,Save.cols);
            // score isn't restored by default when calling the constructor
            b.score = Save.getLastSaveScore(Save.rows); // rows and cols are the same which one is passed as param is arbitrary
        } else  {
            // creates a completely new game

            // will be later set to the dimensions of the board
            int boardDimension = 0;

            // https://docs.oracle.com/javase/tutorial/uiswing/components/dialog.html
            // array of all the different board options available
            // the board could  accommodate an infinite variation of board sizes. (l and w shouldn't differ)
            // the array stores all the reasonable board sizes. Try adding "20x20", and uncomment the code at lines 66-67 :).
            String[] possibilities = {"3x3", "4x4", "5x5", "6x6", "7x7", "8x8"};

            // Input dialog requests a board size from the user displaying the possibilities[] list as a dropdown menu.
            String result = (String) JOptionPane.showInputDialog(
                    null,
                    "Choose A Board Size",
                    "CONFIG",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    possibilities,
                    "4x4");

            // if the user exits the input dialog result would be null
            // indicating the user no longer wants to play the program resulting in the program shutdown
            if (result==null) {
                System.exit(0); // https://www.javatpoint.com/post/java-system-exit-method
            }

            /*if (result.length()==3)*/ boardDimension = Integer.parseInt(result.substring(0, 1));
//            if (result.length()==5) boardDimension = Integer.parseInt(result.substring(0, 2));

            // instantiate a board object
            b = new Board(boardDimension, boardDimension); // creates the board
        }

        // stores the direction (wasd)
        char dir = ' ';


        while (true) {
            // gets key pressed (wasd)
            dir = b.getKey();

            // ensures b.getKey isn't skipped over
            Thread.sleep(1);


            // shifts left if the 'a' key has been pressed.
            if (dir == 'a') {
                boolean moveHasOccurred =  b.shiftLeft();

                // if any movement has occurred within the board then we can add a random tile to the board
                // is this bad code reuse ???????????????????????????????????????????????????????????????
                if (moveHasOccurred) {
                    b.updateGUI();
                    Thread.sleep(25); // sleep makes the new tile appearing identifiable
                    b.addRandomTile();
//                } else if (b.getOpenTiles() == 0) { // if move has not occurred and there are no open tiles
//
                }
                b.updateGUI();
            }
            // shifts right
            else if (dir == 'd') {
                boolean moveHasOccurred =  b.shiftRight();

                // if any movement has occurred within the board then we can add a random tile to the board
                // is this bad code reuse ???????????????????????????????????????????????????????????????
                if (moveHasOccurred) {
                    b.updateGUI();
                    Thread.sleep(25); // sleep makes the new tile appearing identifiable
                    b.addRandomTile();
                }
                b.updateGUI();
            }
            // shifts down
            else if (dir == 's') {
                boolean moveHasOccurred =  b.shiftDown();

                // if any movement has occurred within the board then we can add a random tile to the board
                // is this bad code reuse ???????????????????????????????????????????????????????????????
                if (moveHasOccurred) {
                    b.updateGUI();
                    Thread.sleep(25); // sleep makes the new tile appearing identifiable
                    b.addRandomTile();
                }
                b.updateGUI();
            }
            // shifts up
            if (dir == 'w') {
                boolean moveHasOccurred =  b.shiftUp();

                // if any movement has occurred within the board then we can add a random tile to the board
                // is this bad code reuse ???????????????????????????????????????????????????????????????
                if (moveHasOccurred) {
                    b.updateGUI();
                    Thread.sleep(25); // sleep makes the new tile appearing identifiable
                    b.addRandomTile();
                }
                b.updateGUI();
            }

            if (!b.nextMoveAvailable()) break; // ----------------------- is this good mr benum ask him????????????????????????????????????
        }
        Thread.sleep(100);

        LeaderBoard.uploadNewScore(b.score,b.getRows());

        int done = JOptionPane.showConfirmDialog(
                null,
                "Congrats, Your Score Is " + b.score + "! Would You Like To Try Again? Click No To View Leaderboard!",
                "END SCREEN",
                JOptionPane.YES_NO_OPTION);

        LeaderBoard.show(b.score,b.getRows());
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

}