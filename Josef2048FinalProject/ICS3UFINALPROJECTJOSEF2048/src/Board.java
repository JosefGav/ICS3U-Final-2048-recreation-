import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.Random;
import java.util.Arrays;



/*   Board.java
 *   Heavily modified version of the board class provided by Mr. Benum
 * 
 *   Animated recreation of popular puzzle game 2048, with the ability to select game mode
 *   Utilizes WASD and SWIPED events for movement. 
 *   Implements reading and writing to text files for leader board tracking and saving game in progress
 *   
 *   Made by Josef Gavronskiy (2023-12-28 -> 2024-01-21)
 *   
 *   Adapted from: 
 *   Board class made by Authors: Kirill Levin, Troy Vasiga, Chris Ingram
 *   Modified by Mr. Benum to handle Key presses on Jan. 3, 2011
 *   
 *   Recourses used:
 *   https://docs.oracle.com/javase/tutorial/2d/geometry/strokeandfill.html#:~:text=To%20set%20the%20stroke%20attribute,rendered%20with%20the%20draw%20method.
 *   https://www.javatpoint.com/java-jbutton
 *   Google classroom and Mr. Benum's tutorials
 */

// =============================MUST IMPLEMENT KEYLISTENER ====================
public class Board  extends JPanel implements KeyListener
{
    // 2D array stores the board
    public  int[][] boardMap;
    
    // random object is used to generate new tiles
    private Random r = new Random();
    
    // pre-existing code
    private static final int X_DIM = 25;
    private static final int Y_DIM = 25;
    private static final int X_OFFSET = 30;
    private static final int Y_OFFSET = 30;
    private static final double MIN_SCALE = 0.25;
    private static final int GAP = 10;
    private static final int FONT_SIZE = 16;
    
    
    // THIS VARIABLE STORES THE UNICODE VALUE OF THE KEY THAT WAS PRESSED =======
    private static char key;
    
    private int[] mouseCoordinateInitial; // stores mouse coordinates {x,y}


    
    // displayed to the left of the score
    private final String SCORE_PREFIX = "Score: ";
    
    // displayed to the right of the score
    private String scoreSuffix = "";
    
    // stores the players score
    public int score = 0;
    
    // pre- existing code
    private int numLines = 0;
    private int[][] line = new int[4][100];  // maximum number of lines is 100
    private int columns, rows;

    private int originalWidth;
    private int originalHeight;
    private double scale;
    
    // save and exit JButton
    public JButton save;
    
    // stores the swipe sensitivity 
    private final int SWIPE_SENSITIVITY = 15;
    
    public boolean gameOver = false; // game over set to false by default
    
    // parent JFrame of the board object
    public JFrame f;
    

    /** A constructor to build a 2D board.
     * Precondition: Rows and columns must be at least 3. The processing requirement of the program increases as rows and columns increase.
     * Use judgment to keep rows and columns within a reasonable range.
     * Rows and columns must be equal for the rest of the game to work.
     * Postcondition: creates an instantiation of the board class of rows x cols dimension
     */
    public Board (int rows, int cols)
    {
        super( true );

        // create a JFrame
        f = new JFrame( "2048 HOLIDAY EDITION" );

        // initialize the board array
        this.boardMap = new int[rows][cols];

        // populate the board array with zeros
        for (int r = 0; r < rows; r++ ) {
            for (int c = 0; c < cols; c ++) {
                this.boardMap[r][c] = 0;
            }
        }

        // adds two 2 tiles to the board
        // the location of these tiles remains constant as making them random would not be a useful feature.
        this.boardMap[0][0] = 2;
        this.boardMap[2][2] = 2;

        // sets rows and columns properties of the instantiated board object
        this.columns = cols;
        this.rows = rows;

        // pre-existing code block
        originalWidth = 2*X_OFFSET+X_DIM*cols;
        originalHeight = 2*Y_OFFSET+Y_DIM*rows+GAP+FONT_SIZE;
        this.setPreferredSize( new Dimension( originalWidth, originalHeight ) );
        f.setResizable(true);
        this.setFocusable(true);
        addKeyListener(this);

        // modification of Mr. benum's mouse click listener to detect drag and release
        this.addMouseListener(returnSwipeInputAdaptar()); 
        
        // creates the save button
        this.save = this.createSaveButton();
        this.add(this.save);


        // pre existing code block
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setContentPane( this );
        f.pack();
        f.setVisible(true);
    }

    
    /** A constructor to build a 2D board from a given array
     * Precondition: rows and columns must be equal to array length
     * Postcondition: creates a board object of rows x cols dimension
     */
    public Board(int[][] grid, int rows, int cols) {
        super( true );

        // RENAME THE WINDOW HERE =================================================
        f = new JFrame( "2048 HOLIDAY EDITION" );

        // sets the board map to the provided grid array
        this.boardMap = grid;

        // sets rows and columns
        this.columns = cols;
        this.rows = rows;

        // pre existing code block
        originalWidth = 2*X_OFFSET+X_DIM*cols;
        originalHeight = 2*Y_OFFSET+Y_DIM*rows+GAP+FONT_SIZE;
        this.setPreferredSize( new Dimension( originalWidth, originalHeight ) );
        f.setResizable(true);
        this.setFocusable(true);
        // MUST ADD THE KEYLISTENER TO THE BOARD (JPANEL) =========================
        addKeyListener(this);
       

        // modification of Mr. Benum's mouse click listener to detect drag and release
        this.addMouseListener(returnSwipeInputAdaptar()); /* anonymous MouseInputAdapter */

        // creates save and exit button
        this.save = this.createSaveButton();
        this.add(this.save);

        // pre-existing code block
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setContentPane( this );
        f.pack();
        f.setVisible(true);
    }


    /**
     * Key event is called whenever a user types a key
     * Post-condition: key is set to whatever key was typed
     * */
    public void keyTyped(KeyEvent e) {
        key = e.getKeyChar();
    }

    /**
     * Accessor method returns the last key that has been pressed
     * Postcondition: the last key pressed is returned. key is set to -> ' '
     * */
    public char getKey(){
        char k = key;
        resetKey(); // resets the key so that one key press only results in one shift of the board
        return k;   // otherwise, the shift method would be called on loop until you press another key.
    }
    
    /**
     * Resets the key variable to an empty char
     * Postcondition: key set to -> ' ' 
     * */
    private void resetKey() {
        key = ' ';
    }
    
    // must implement key listener
    public void keyPressed(KeyEvent arg0) { }
	public void keyReleased(KeyEvent arg0) {}

    // Modified pre-existing method
    private void paintText(Graphics g)
    {
    	// pre-existing code block
        g.setColor( this.getBackground() );
        g.setFont(new Font(g.getFont().getFontName(), Font.ITALIC+Font.BOLD, (int)(Math.round(FONT_SIZE*scale))));

        // coordinates of the score message
        int x = (int)Math.round(X_OFFSET*scale);
        int y = (int)Math.round((Y_OFFSET+Y_DIM*this.columns)*scale + GAP) + (int)(scale*5);

        g.setColor( Color.DARK_GRAY );
        
        // displays score message 
        g.drawString(SCORE_PREFIX+score+scoreSuffix, x, y + (int)Math.round(FONT_SIZE*scale));
       
        g.setColor( Color.WHITE );

        // displays each tile 
        for (int i = 0; i < this.boardMap.length; i++)
        {
            for (int j = 0; j < this.boardMap[i].length; j++)
            {
            	// pre-existing code block 
                int curX = (int)Math.round((X_OFFSET+X_DIM*i)*scale);
                int curY = (int)Math.round((Y_OFFSET+Y_DIM*j)*scale);
                int nextX = (int)Math.round((X_OFFSET+X_DIM*(i+1))*scale);
                int nextY = (int)Math.round((Y_OFFSET+Y_DIM*(j+1))*scale);
                int deltaX = nextX-curX;
                int deltaY = nextY-curY;

                // changes font size to fit # of characters
                g.setFont(new Font("Arial", Font.PLAIN,deltaY/(Utilities.intLen(this.boardMap[j][i]))));
                
                // displays the value of the tile (given that tile != 0)
                if (this.boardMap[j][i]  != 0) {
                    g.drawString(this.boardMap[j][i] + "",curX + (int)(5.85*scale),curY+(int)(scale*22));
                }

            }
        }
    }
    
    // Modified pre-existing method
    private void paintGrid(Graphics g)
    {
        super.paintComponent(g);// fixes weird JButton teleportation glitch
        
        // displays each tile
        for (int i = 0; i < this.boardMap.length; i++)
        {
            for (int j = 0; j < this.boardMap[i].length; j++)
            {
                Color c;
                
                if (this.boardMap[j][i]==0) { // empty tiles are grey
                    c = Color.lightGray; 
                } else if (256-(int)(Utilities.log(2,this.boardMap[j][i]))*30>=0) { // uses logarithmic equation used to calculate the color of the tile
                																	// checks if tile color is within an acceptable range (<256)
                    c = new Color(256-(int)( Utilities.log(2,this.boardMap[j][i]))*30,0,0);
                } else { // high value tiles (>=512), show up rainbow
                    c = new Color(this.r.nextInt(160)+40,this.r.nextInt(160)+40,this.r.nextInt(160)+40);
                }
                g.setColor(c);
                
            	// pre-existing code block 
                int curX = (int)Math.round((X_OFFSET+X_DIM*i)*scale);
                int curY = (int)Math.round((Y_OFFSET+Y_DIM*j)*scale);
                int nextX = (int)Math.round((X_OFFSET+X_DIM*(i+1))*scale);
                int nextY = (int)Math.round((Y_OFFSET+Y_DIM*(j+1))*scale);
                int deltaX = nextX-curX;
                int deltaY = nextY-curY;
                
                // displays a tile (not including text). 
                // arguments have been tuned to scale properly (uses predefined scale variable)
                g.fillRoundRect( curX+1*(int)scale, curY+(int)scale, deltaX-(int)(2*scale), deltaY-(int)(2*scale),(int)(5*scale),(int)(5*scale));

            }
        }

        
        // https://docs.oracle.com/javase/tutorial/2d/geometry/strokeandfill.html#:~:text=To%20set%20the%20stroke%20attribute,rendered%20with%20the%20draw%20method.
        //  + casting graphics
        
        // sets the border of the next shape that will be drawn
        ((Graphics2D) g).setStroke( new BasicStroke( 5*(float)scale) );
        
        // sets color to blue 
        g.setColor(Color.BLUE);
        
        // already existing calculations done by the board class
        int curX = (int)Math.round(X_OFFSET*scale)-(int)(5*scale);
        int curY = (int)Math.round(Y_OFFSET*scale)-(int)(5*scale);
        int nextX = (int)Math.round((X_OFFSET+X_DIM*this.rows)*scale)+(int)(5*scale);
        int nextY = (int)Math.round((Y_OFFSET+Y_DIM*this.columns)*scale)+(int)(5*scale);
        
        // repositions the save button to sit on top of the grid (in case the window has been rescaled)
        this.save.setBounds((2*curX+nextX-curX)/2-(nextX-curX)/2,0,(nextX-curX),curY-(int)(5*scale/2));
        
        // draws the grid border
        g.drawRoundRect(curX, curY, nextX-curX, nextY-curY,(int)(15*scale),(int)(15*scale));
    }

    // Pre-existing method
    private void drawLine(Graphics g)
    {
        for (int i =0; i < numLines; i++ )
        {
            ((Graphics2D) g).setStroke( new BasicStroke( 5.0f*(float)scale) );
            g.drawLine( (int)Math.round((X_OFFSET+X_DIM/2.0+line[0][i]*X_DIM)*scale),
                    (int)Math.round((Y_OFFSET+Y_DIM/2.0+line[1][i]*Y_DIM)*scale),
                    (int)Math.round((X_OFFSET+X_DIM/2.0+line[2][i]*X_DIM)*scale),
                    (int)Math.round((Y_OFFSET+Y_DIM/2.0+line[3][i]*Y_DIM)*scale) );
        }
    }




    /** The method that draws everything
     */
    public void paintComponent( Graphics g )
    {
        this.setScale();
        this.paintGrid(g);
        this.drawLine(g);
        this.paintText(g);
    }

    
    // Pre-existing method
    public void setScale()
    {
        double width = (0.0+this.getSize().width) / this.originalWidth;
        double height = (0.0+this.getSize().height) / this.originalHeight;
        this.scale = Math.max( Math.min(width,height), MIN_SCALE );
    }

    /** Sets the message to be displayed under the board
     *  Postcondition: suffix of the score message is changed and the board is repainted.
     */
    public void displayMessage(String newSuffix)
    {
    	this.scoreSuffix = newSuffix;
        this.repaint();
    }

    /** Draws a line on the board using the given co-ordinates as endpoints
     */
    public void drawLine(int row1, int col1, int row2, int col2)
    {
        this.line[0][numLines]=col1;
        this.line[1][numLines]=row1;
        this.line[2][numLines]=col2;
        this.line[3][numLines]=row2;
        this.numLines++;
        repaint();
    }

    /** Removes one line from a board given the co-ordinates as endpoints
     * If there is no such line, nothing happens
     * If multiple lines, all copies are removed
     */
    public void removeLine(int row1, int col1, int row2, int col2)
    {
        int curLine = 0;
        while (curLine < this.numLines)
        {
            // Check for either endpoint being specified first in our line table
            if ( (line[0][curLine] == col1 && line[1][curLine] == row1 &&
                    line[2][curLine] == col2 && line[3][curLine] == row2)   ||
                    (line[2][curLine] == col1 && line[3][curLine] == row1 &&
                            line[0][curLine] == col2 && line[1][curLine] == row2) )
            {
                // found a matching line: overwrite with the last one
                numLines--;
                line[0][curLine] = line[0][numLines];
                line[1][curLine] = line[1][numLines];
                line[2][curLine] = line[2][numLines];
                line[3][curLine] = line[3][numLines];
                curLine--; // perhaps the one we copied is also a match
            }
            curLine++;

        }
        repaint();
    }

    public int getColumns()
    {
        return this.columns;
    }

    public int getRows()
    {
        return this.rows;
    }


    /**
     * Shifts the entire board left, returns whether a move has occurred
     * Post-condition: Entire board shifted left. Equal adjacent tiles combine. Returns whether a move has occurred.
     * */
    public boolean shiftLeft() throws InterruptedException {
        // stores whether a move has occurred
        // set to false as no move has occurred yet.
        boolean moveHasOccured = false;

        // loops through the board array
        for (int row = 0; row < this.rows; row++) {
            // makes a copy of the original row to check if row is changed, this iteration will only be animated if the row is changed
            int[] rowOriginal = Arrays.copyOf(this.boardMap[row], this.boardMap[row].length);

            // Shifts everything in the row left
            // Begins at the first column and works its way towards the last column
            for (int col = 0; col < this.columns; col++) {
                // checks if the current tile is not equal to zero and must be shifted
                // (zeros are empty tiles and don't need to be shifted)
                if (this.boardMap[row][col] != 0) {
                    // stores the value of the current tile
                    int val = this.boardMap[row][col];
                    // sets the index of the current tile to zero
                    this.boardMap[row][col] = 0;

                    // shifts the selected tile left
                    for (int i = col; i >= 0; i--) {
                        // checks if the current index is not equal to zero
                        // (zeros are empty can be passed through by other tiles)
                        if (this.boardMap[row][i] != 0) {
                            // checks if tiles are the same value
                            if (val == this.boardMap[row][i]) {
                                // combines the 2 tiles
                                this.boardMap[row][i] += val;

                                // increases score
                                this.score += val * 2;
                                
                                this.displayMessage("+" +  val * 2); // shows what we are adding to the right of the score temporarily (suffix)

                                // new feature checks if the tile adjacent to the newly merged tile is equal in value
                                // 16 8 8 0
                                // can combine to 32 0 0 0 in one turn!
                                // i > 0 to prevent an ArrayIndexOutOfBoundsException
                                if (i > 0 && boardMap[row][i] == this.boardMap[row][i - 1]) {
                                    this.boardMap[row][i - 1] += this.boardMap[row][i];
                                    // increase score
                                    this.score += this.boardMap[row][i] * 2;
                                    
                                    this.displayMessage("+" +  this.boardMap[row][i] * 2);   // shows what we are adding to the right of the score temporarily (suffix)

                                    // after two tiles combine one must be set to zero (so it disappears)
                                    // 16 16 -> 0 32 not 16 32
                                    this.boardMap[row][i] = 0;
                                }
                                // if this point is reached the selected tile has been thoroughly shifted
                                // the loop can be exited
                                break;
                            } else { // if it reaches a tile that cannot be combined place selected tile beside said tile
                                this.boardMap[row][i + 1] = val;
                                break;
                            }
                        }
                        // checks if you have reached the original column
                        if (i == 0 && this.boardMap[row][i] == 0) {
                            this.boardMap[row][i] = val;
                            break;
                        }
                    }
                }

                // checks if row has been changed by comparing the original row with the new row.
                // compareIntegerArray method returns True if the 2 arrays are the same
                // if the 2 rows are different that indicates a move has occurred
                // only sleep if the row has been changed, to animate the row change.
                if (!Utilities.compareIntegerArrays(rowOriginal, this.boardMap[row])) {
                    Thread.sleep(25);
                    moveHasOccured = true;
                }

                // refreshes the board after the new changes
                this.repaint();
                
                
            }
        }
        // resets the suffix of the score message
        this.displayMessage("");
        
        return moveHasOccured; // returns T/F
    }

    /**
     * Shifts the entire board right, returns whether a move has occurred
     * Post-condition: Entire board shifted right. Equal adjacent tiles combine. Returns whether a move has occurred.
     * */
    public boolean shiftRight() throws InterruptedException {
        // stores whether a move has occurred
        // set to false as no move has occurred yet.
        boolean moveHasOccured = false;
        
        // loops through the board array
        for (int row = 0; row < this.rows; row++) {
            // makes a copy of the original row to check if row is changed, this iteration will only be animated if the row is changed.
            int[] rowOriginal = Arrays.copyOf(this.boardMap[row],this.boardMap[row].length);

            // Shifts everything in the row right
            // Begins at the last column and works its away from said column
            for (int col = this.columns-1; col >= 0; col--) {
            	 // checks if the current tile is not equal to zero and must be shifted
                // (zeros are empty tiles and don't need to be shifted)
                if (this.boardMap[row][col] != 0) {
                	// stores the value of the current tile
                    int val = this.boardMap[row][col];
                    // sets the index of the current tile to zero
                    this.boardMap[row][col] = 0;
                    
                    // shifts the selected tile right
                    for (int i = col; i < this.columns; i++) {
                        // checks if the current index is not equal to zero
                    	// (zeros are empty can be passed through by other tiles) 
                        if (this.boardMap[row][i] != 0) {
                        	// checks if tiles are the same value
                            if (val == this.boardMap[row][i]) {
                            	// combines the 2 tiles
                                this.boardMap[row][i] += val;

                                // increases score
                                this.score += val*2;
                                
                                // shows what we are adding to the right of the score temporarily (suffix)
                                this.displayMessage("+" +  val * 2);


                                
                                // new feature checks if the tile adjacent to the newly merged tile is equal in value
                                // 16 8 8 0
                                // can combine to 32 0 0 0 in one turn!
                                // i > columns -1 to prevent an ArrayIndexOutOfBoundsException
                                if(i<this.columns-1 && boardMap[row][i]==this.boardMap[row][i+1]) {
                                    this.boardMap[row][i+1] += this.boardMap[row][i];
                                    // increase score
                                    this.score += this.boardMap[row][i]*2;
                                    
                                    // shows what we are adding to the right of the score temporarily (suffix)
                                    this.displayMessage("+" +  this.boardMap[row][i] * 2);

                                    
                                    // after two tiles combine one must be set to zero (so it disappears)
                                    // 16 16 -> 0 32 not 16 32
                                    this.boardMap[row][i] = 0;
                                }
                                // if this point is reached the selected tile has been thoroughly shifted
                                // the loop can be exited
                                break;
                            } else { // if it reaches a tile that cannot be combined place selected tile beside said tile
                                this.boardMap[row][i - 1] = val;
                                break;
                            }
                        }
                        // checks if you have reached the original column	
                        if (i == this.columns-1 && this.boardMap[row][i] == 0) {
                            this.boardMap[row][i] = val;
                            break;
                        }
                    }
//                            print2D(boardMap,row);
                }


                // checks if row has been changed by comparing the original row with the new row.
                // compareIntegerArray method returns True if the 2 arrays are the same
                // if the 2 rows are different that indicates a move has occurred
                // only sleep if the row has been changed, to animate the row change.
                if (!Utilities.compareIntegerArrays(rowOriginal,this.boardMap[row])) {
                    Thread.sleep(25);
                    moveHasOccured = true;
                }
                // refreshes the board after the new changes

                this.repaint();
            }

        }
        // resets the suffix of the score message
        this.displayMessage("");
        
        return moveHasOccured; // returns T/F
    }

    /**
     * Shifts the entire board down, returns whether a move has occurred
     * Post-condition: Entire board shifted down. Equal adjacent tiles combine. Returns whether a move has occurred.
     * */
    public boolean shiftDown() throws InterruptedException {
        // stores whether a move has occurred
        // set to false as no move has occurred yet.
        boolean moveHasOccured = false;

        // loop through the board array
        for (int col = 0; col < this.columns; col++) {
            // creates a copy of the current column. This iteration will only be animated if the column is changed
            // Arrays.copyOf copies the original column by VALUE, NOT REFERENCE
            int[] colOriginal = Arrays.copyOf(Utilities.getColumn(this.boardMap, col),this.boardMap.length);

            // Shifts everything in the column down
            // Begins at the last row and works its way up away from said row
            for (int row = this.rows-1; row >= 0; row--) {
                // checks if the current tile is not equal to zero and must be shifted
                // (zeros are empty tiles and don't need to be shifted)
                if (this.boardMap[row][col] != 0) {
                    // stores the value of the current tile
                    int val = this.boardMap[row][col];
                    // sets the index of the current tile to zero
                    this.boardMap[row][col] = 0;

                    // shifts the selected tile down
                    for (int i = row; i < this.rows; i++) {
                        // checks if the current index is not equal to zero
                    	// (zeros are empty can be passed through by other tiles) 

                        if (this.boardMap[i][col] != 0) {
                            // checks if 2 tiles are the same value
                            if (val == this.boardMap[i][col]) {
                                // combines the values of 2 tiles
                                this.boardMap[i][col] += val;
                                //increase score
                                this.score+= val*2;
                                
                                // shows what we are adding to the right of the score temporarily (suffix)
                                this.displayMessage("+"+val*2);

                                // new feature checks if the tile adjacent to the newly merged tile is equal in value
                                // 16 8 8 0
                                // can combine to 32 0 0 0 in one turn!
                                // i > rows-1 to prevent an ArrayIndexOutOfBoundsException
                                if(i<this.rows-1 && this.boardMap[i][col]==this.boardMap[i+1][col]) {
                                    this.boardMap[i+1][col] += this.boardMap[i][col];
                                    // increase score
                                    this.score += this.boardMap[i][col]*2;
                                    
                                    // shows what we are adding to the right of the score temporarily (suffix)
                                    this.displayMessage("+"+this.boardMap[i][col]*2);

                                    // after two tiles combine one must be set to zero (so it disappears)
                                    // 16 16 -> 0 32 not 16 32
                                    this.boardMap[i][col] = 0;

                                }
                                // if this point is reached the selected tile has been thoroughly shifted
                                // the loop can be exited
                                break;
                            } else {
                                // if it reaches a tile that cannot be combined place the selected tile a row above
                                this.boardMap[i-1][col] = val;
                                // since the selected tile has been shifted the loop can be exited
                                break;
                            }
                        }
                        // checks if you have reached the original row
                        if (i == this.rows-1 && this.boardMap[i][col] == 0) {
                            this.boardMap[i][col] = val;
                            break;
                        }
                    }


                // checks if row has been changed by comparing the original col with the new col.
                // compareIntegerArray method returns True if the 2 arrays are the same
                // if the 2 col are different that indicates a move has occurred
                // only sleep if the col has been changed, to animate the col change.
                }
                if (!Utilities.compareIntegerArrays(colOriginal,Utilities.getColumn(this.boardMap, col))) {
                    Thread.sleep(25);
                    moveHasOccured = true;
                }
                // refreshes the board after the new changes

                this.repaint();
            }

        }
        // resets the suffix of the score message
        this.displayMessage("");
        
        return moveHasOccured; // returns T/F
    }
    /**
     * Shifts the entire board up, returns whether a move has occurred
     * Post-condition: Entire board shifted up. Equal adjacent tiles combine. Returns whether a move has occurred.
     * */
    public boolean shiftUp() throws InterruptedException {
        // stores whether a move has occurred
        // set to false as no move has occurred yet.
        boolean moveHasOccured = false;

        // loop through the board array
        for (int col = 0; col < this.columns; col++) {
            // creates a copy of the current column. This iteration will only be animated if the column is changed
            // Arrays.copyOf copies the original column by VALUE, NOT REFERENCE
            int[] colOriginal = Arrays.copyOf(Utilities.getColumn(this.boardMap, col), this.boardMap.length);
            
            // Shifts everything in the column up
            // Begins at the first row and works its way down away from said row
            for (int row = 0; row < this.rows; row++) {
                // checks if the current tile is not equal to zero and must be shifted
                // (zeros are empty tiles and don't need to be shifted)
                if (this.boardMap[row][col] != 0) {
                    // stores the value of the current tile
                    int val = this.boardMap[row][col];
                    // sets the index of the current tile to zero
                    this.boardMap[row][col] = 0;

                    // shifts the selected tile up
                    for (int i = row; i >= 0; i--) {
                        // checks if the current index is not equal to zero
                    	// (zeros are empty can be passed through by other tiles) 

                        if (this.boardMap[i][col] != 0) {
                            // checks if 2 tiles are the same value
                            if (val == this.boardMap[i][col]) {
                                // combines the values of the 2 tiles
                                this.boardMap[i][col] += val;

                                // increase score
                                this.score += val * 2;
                                
                                // shows what we are adding to the right of the score temporarily (suffix)
                                this.displayMessage("+"+val*2);

                                // new feature checks if the tile adjacent to the newly merged tile is equal in value
                                // 16 8 8 0
                                // can combine to 32 0 0 0 in one turn!
                                // i > 0, as index[i-1] to prevent an ArrayIndexOutOfBoundsException which would occur when i == 0
                                if (i > 0 && this.boardMap[i][col] == this.boardMap[i - 1][col]) {
                                    //combines the two tiles
                                    this.boardMap[i - 1][col] += this.boardMap[i][col];
                                    // increase score
                                    this.score += this.boardMap[i][col] * 2;
                                    
                                    // shows what we are adding to the right of the score temporarily (suffix)
                                    this.displayMessage("+"+this.boardMap[i][col] * 2);

                                    // after two tiles combine one must be set to zero (so it disapeers)
                                    // 16 16 -> 0 32 not 16 32
                                    this.boardMap[i][col] = 0;

                                }
                                // if this point is reached the selected tile has been thoroughly shifted
                                // the loop can be exited
                                break;
                            } else {
                                // if it reaches a tile that cannot be combined place the selected tile a row below
                                this.boardMap[i + 1][col] = val;
                                // since the selected tile has been shifted the loop can be exited
                                break;

                            }
                        }

                        // checks if you have reached the original row
                        if (i == 0 && this.boardMap[i][col] == 0) {
                            this.boardMap[i][col] = val;
                            break;
                        }
                    }
                }


                // checks if row has been changed by comparing the original col with the new col.
                // compareIntegerArray method returns True if the 2 arrays are the same
                // if the 2 col are different that indicates a move has occurred
                // only sleep if the col has been changed, to animate the col change.
                if (!Utilities.compareIntegerArrays(colOriginal, Utilities.getColumn(this.boardMap, col))) {
                    Thread.sleep(25);
                    moveHasOccured = true;
                }

                // refreshes the board after the new changes
                this.repaint();
            }
        }
        
        // resets the suffix of the score message
        this.displayMessage("");
        
        return moveHasOccured; // returns T/F
    }

    /**
     * Adds a random tile to the board
     * Pre-condition: must be called after a board SHIFT
     * Post-condition: adds a new tile to the board (either a 2 or 4)
     * */
    public void addRandomTile() {
        // generates the tile's value randomly
        int newTile;
        double random = r.nextDouble();
        
        // (90% -> 2, 10% -> 4)
        if (random<0.9) {
            newTile = 2;
        } else {
            newTile = 4;
        }
        
        
        
        // gets the number of open tiles (AKA "zero tiles")
        int numOpenTiles = this.getOpenTiles();
        
        // creates a 2d array that will store all the open tiles in the form:
        // {
        //  {r,c},{r,c}...
        // }
        int[][] openTiles = new int[numOpenTiles][2];
        
        // counter
        int nthOpenTile = 0;

        
        if (openTiles.length == 0) return; // no open tiles = loss
        
        // adds all "zero tiles" to the openTiles array
        for (int r = 0; r < this.rows; r++) {
            for (int c = 0; c < this.columns; c++) {
                if (this.boardMap[r][c] == 0) {
                    openTiles[nthOpenTile] =  new int[] {r, c};
                    nthOpenTile ++;
                }
            }
        }
        
        // picks a random open tile
        int randomIndex = this.r.nextInt(openTiles.length);

        // sets the random open tile to our newTile value
        this.boardMap[openTiles[randomIndex][0]][openTiles[randomIndex][1]] = newTile;
    }

    /**
     * Gets the number of empty tiles on the board from this instance of the board class
     * Post-condition: returns n, representing the number of empty tiles. n >= 0.
     * */
    public int getOpenTiles() {
        int numOpenTiles = 0;

        for (int r = 0; r < this.rows; r++) {
            for (int c = 0; c < this.columns; c++) {
                if (this.boardMap[r][c] == 0) {
                    numOpenTiles++;
                }
            }
        }

        return numOpenTiles;
    }

    /**
     * Checks if a next move is possible in this instance of the Board class(left, right, up, down)
     * Post-condition: returns true if there is a move available and false if there isn't
     * */
    public boolean nextMoveAvailable () {
        // if there is an open space on the board map that means that there is a next move available
        if (this.getOpenTiles() != 0) return true;

        // check if there are any two equal adjacent tiles that can combine.
        // if there are tiles that can combine that means a next move is available
        for (int r = 0; r < this.rows; r++) {
            for (int c = 0; c < this.columns; c++) {
                // stores the value of the tile at the current index
                int thisBlockVal = this.boardMap[r][c];

                // checks if column to the right is the same
                try {
                    if (thisBlockVal == this.boardMap[r][c+1]) {
                        return true;
                    }
                } catch (ArrayIndexOutOfBoundsException e)  {}

                // checks if column to the left is the same
                try {
                    if (thisBlockVal == this.boardMap[r][c-1]) {
                        return true;
                    }
                } catch (ArrayIndexOutOfBoundsException e)  {}

                // checks if row below is the same
                try {
                    if (thisBlockVal == this.boardMap[r+1][c]) {
                        return true;
                    }
                } catch (ArrayIndexOutOfBoundsException e)  {}

                // checks if row above is the same
                try {
                    if (thisBlockVal == this.boardMap[r-1][c+1]) {

                    }
                } catch (ArrayIndexOutOfBoundsException e)  {}

            }
        }

        // if this point is reached that means there is no next move available
        return false;
    }
    
    /**
     * Creates a MouseInputAdapter that will keep track of swipes (to prevent code reuse)
     * Modified version Mr. Benum's mouse click listener to detect drag and release
     * If a large swipe is detected the adapter will change the key variable to (w,a,s,d) based on the direction of the swipe
     * */
    public MouseInputAdapter returnSwipeInputAdaptar() {
    	return new MouseInputAdapter() {
    		// method comments follow same format as the original board class
    		/**
    		 * Method is called when the mouse is pressed
    		 * */
            public void mousePressed(MouseEvent e) {
                int x = (int) e.getPoint().getX();
                int y = (int) e.getPoint().getY();

                // We need to be synchronized to the parent class so we can wake
                // up any threads that might be waiting for us
                synchronized (Board.this) {
                	
                    mouseCoordinateInitial = new int[]{x, y}; // coordinates of the mouse at the beginning of our swipe
                    
                } /* synchronized */
            } /* mousePressed */

            /**
    		 * Method is called when the mouse is released
    		 * */
            public void mouseReleased(MouseEvent e) {
                int x = (int) e.getPoint().getX();
                int y = (int) e.getPoint().getY();

                // We need to be synchronized to the parent class so we can wake
                // up any threads that might be waiting for us
                synchronized (Board.this) {
                	
                    int[] mouseCoordinateFinal = {x, y}; // coordinates of the mouse at the end of our swipe
                    
                    detectMouseDirection(mouseCoordinateInitial, mouseCoordinateFinal); // detect mouse direction method updates the key field
                    																	// based on its equivalent swipe. right swipe -> key = 'd'
                } /* synchronized */
            } /* mouseReleased */
            
            /**
    		 * Method  detects which direction the mouse was swiped. 
    		 * Precondition: Coordinates must be provided in the form {x,y}
    		 * Postcondition: Changes the key variable to (w,a,s,d) based on the direction of the swipe.
    		 * */
            private void detectMouseDirection(int[] initial, int[] current) {
                int changeInX = current[0] - initial[0];
                int changeInY = current[1] - initial[1];

                // checks if the swipe was more horizontal or vertical
                if (Math.abs(changeInX) > Math.abs(changeInY)) {
                	// checks Left/Right swipes. makes sure the swipe was significant
                    if (changeInX > SWIPE_SENSITIVITY*(int)scale) {
                        // no key has been actually pressed. as getKey() is how the Main class will access direction we must update board.key
                        key = 'd';
                    } else if ( changeInX < -SWIPE_SENSITIVITY*(int)scale*rows/4)  {
                        // no key has been actually pressed. as getKey() is how the Main class will access direction we must update board.key
                        key = 'a';
                    }
                } else {
                	// checks Up/Down swipes. makes sure the swipe was significant
                    if (changeInY > SWIPE_SENSITIVITY*(int)scale*rows/4) {
                        // no key has been actually pressed. as getKey() is how the Main class will access direction we must update board.key
                        key = 's';
                    } else if ( changeInY < -SWIPE_SENSITIVITY*(int)scale*rows/4) {
                        // no key has been actually pressed. as getKey() is how the Main class will access direction we must update board.key
                        key = 'w';
                    }
                }
            }
    	};
    }
    
    /**
     * Returns a new button for saving and exiting the program mid-game.
     * Postcondition: returns a new save and exit button with default bounds (50,100,100,50)
     * */
    public JButton createSaveButton() {
    	// creates a new button for saving and exiting the program 
        JButton save =new JButton("Save And Exit"); // https://www.javatpoint.com/java-jbutton
        
        save.setFocusable(false);

        // on click handler for the submit button
        save.addActionListener(new ActionListener() { // https://www.javatpoint.com/java-jbutton
            @Override
            public void actionPerformed(ActionEvent e) {

                // save your progress. You should not be able to save your *progress* once the game is over
                if (gameOver != true) Save.newSave(boardMap,score); 

                // terminate program
                System.exit(0);
            }
        });
        
        // set dimensions of the button and add it to the JFrame
        save.setBounds(50,100,100,50);
        
        return save;
    }
}