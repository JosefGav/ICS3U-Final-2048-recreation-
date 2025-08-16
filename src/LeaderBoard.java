import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

public class LeaderBoard {
    public static void uploadNewScore(int score,int boardDimension) {
        int[] topScores = new int[4]; // to accommodate for our new score

        File file = new File("highScore"+boardDimension+"x"+boardDimension+".txt");
        PrintWriter out  = null;
        Scanner input = null;

        // is this necessary?

        try{
            input = new Scanner(file);
        }
        catch (FileNotFoundException e){

        }



        for (int i = 0; i < 3; i++){
            topScores[i] = Integer.parseInt(input.nextLine());
        }

        topScores[3] = score;


        Arrays.sort(topScores); // scores will be sorted from least to greatest

        try {
            out = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        // re update the high score file with the 3 rightmost scores from the topScores array
        Utilities.printArr(topScores);
        for (int i = 1; i <= 3; i++) {

            out.println(topScores[4-i]);
        }

        out.close();
    }

    // show method

    public static void show(int score, int boardDimension) {
        int[] topScores = new int[3];

        String message = "";

        File file = new File("highScore"+boardDimension+"x"+boardDimension+".txt");

        Scanner input = null;

        try{
            input = new Scanner(file);
        }
        catch (FileNotFoundException e){

        }

        for (int i = 0; i < 3; i++){
            topScores[i] = Integer.parseInt(input.nextLine());
        }

        for (int i = 0; i < topScores.length; i++) {
            if (topScores[i]==score) {
                message += i+1+".   " + topScores[i] + " <- your score \n";
            } else {
                message += i+1+".   " + topScores[i] +"\n";
            }
        }


        JOptionPane.showMessageDialog(
                null,
                message,
                "LeaderBoard",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
