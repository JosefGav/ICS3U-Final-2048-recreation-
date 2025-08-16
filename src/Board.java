import jdk.jshell.execution.Util;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.Random;
import java.util.Arrays;



/**  Board GUI for implementation with various games
 *   Author: Kirill Levin, Troy Vasiga, Chris Ingram
 *   Modified by Mr. Benum to handle Key presses on Jan. 3, 2011
 *   Modified by Josef Gavronskiy to work for ICS3U final culminating project (2048)
 */

// =============================MUST IMPLEMENT KEYLISTENER ====================
public class Board  extends JPanel implements KeyListener
{
    // 2D array stores the board
    public  int[][] boardMap;
    private Random r = new Random();
    private static final int X_DIM = 25;
    private static final int Y_DIM = 25;
    private static final int X_OFFSET = 30;
    private static final int Y_OFFSET = 30;
    private static final double MIN_SCALE = 0.25;
    private static final int GAP = 10;
    private static final int FONT_SIZE = 16;
    // THIS VARIABLE STORES THE UNICODE VALUE OF THE KEY THAT WAS PRESSED =======
    private static char key;

    // Grid colours
    private static final Color GRID_COLOR_A = new Color(0,0,0);
    private static final Color GRID_COLOR_B = new Color(0,0,0);

    // Preset colours for pieces
    private static final Color[] COLOURS =
            {Color.YELLOW, Color.BLUE, Color.CYAN, Color.GREEN,
                    Color.PINK, Color.WHITE, Color.RED, Color.ORANGE };

    // String used to indicate each colour
    private static final String[] COLOUR_NAMES =
            {"yellow", "blue", "cyan", "green", "pink", "white", "red", "orange"};

    // Colour to use if a match is not found
    private static final Color DEFAULT_COLOUR = Color.BLACK;

    private Color[][] grid;
    private Coordinate lastClick;  // How the mouse handling thread communicates
    // to the board where the last click occurred
    private String message = "Score: ";
    public int score = 0;
    private int numLines = 0;
    private int[][] line = new int[4][100];  // maximum number of lines is 100
    private int columns, rows;

    private boolean first = true;
    private int originalWidth;
    private int originalHeight;
    private double scale;

    public JButton save;



    /** A constructor to build a 2D board.
     * Precondition: Rows and cols must be at least 3. The processing requirement of the program increases as rows and cols increase.
     * Use judgement to keep rows and cols within a reasonable range.
     * Rows and columns must be equal for the rest of the game to work.
     */
    public Board (int rows, int cols)
    {
        super( true );

        // create a JFrame
        JFrame f = new JFrame( "512 ULTIMATE" );

        // initialize the board array
        this.boardMap = new int[rows][cols];

        // populate the board array with zeros
        for (int r = 0; r < rows; r++ ) {
            for (int c = 0; c < cols; c ++) {
                this.boardMap[r][c] = 0;
            }
        }

        // adds two 2 tiles to the board
        // the location of these tiles remains constant as making them random could easily go un-noticed by the user, not adding any useful features.
        this.boardMap[0][0] = 2;
        this.boardMap[2][2] = 2;

        // sets rows and columns properties of the instantiated board object
        this.columns = cols;
        this.rows = rows;

        // vvvvv  not Josef's code  vvvvv
        originalWidth = 2*X_OFFSET+X_DIM*cols;
        originalHeight = 2*Y_OFFSET+Y_DIM*rows+GAP+FONT_SIZE;

        this.setPreferredSize( new Dimension( originalWidth, originalHeight ) );

        f.setResizable(true);

        this.grid = new Color[cols][rows];
        this.setFocusable(true);
        // MUST ADD THE KEYLISTENER TO THE BOARD (JPANEL) =========================
        addKeyListener(this);

        // ^^^^^  not Josef's code  ^^^^^

        // not applicable ================
//        this.addMouseListener(
//                new MouseInputAdapter()
//                {
//                    /** A method that is called when the mouse is clicked
//                     */
//                    public void mouseClicked(MouseEvent e)
//                    {
//                        int x = (int)e.getPoint().getX();
//                        int y = (int)e.getPoint().getY();
//
//                        // We need to by synchronized to the parent class so we can wake
//                        // up any threads that might be waiting for us
//                        synchronized(Board.this)
//                        {
//                            int curX = (int)Math.round(X_OFFSET*scale);
//                            int curY = (int)Math.round(Y_OFFSET*scale);
//                            int nextX = (int)Math.round((X_OFFSET+X_DIM*grid.length)*scale);
//                            int nextY = (int)Math.round((Y_OFFSET+Y_DIM*grid[0].length)*scale);
//
//                            // Subtract one from high end so clicks on the black edge
//                            // don't yield a row or column outside of board because of
//                            // the way the coordinate is calculated.
//                            if (x >= curX && y >= curY && x < nextX && y < nextY)
//                            {
//                                lastClick = new Coordinate(y,x);
//                                // Notify any threads that would be waiting for a mouse click
//                                Board.this.notifyAll() ;
//                            } /* if */
//                        } /* synchronized */
//                    } /* mouseClicked */
//                } /* anonymous MouseInputAdapater */
//        );

        // creates a new button for saving and exiting the program
        this.save =new JButton("Save And Exit");
        this.save.setFocusable(false);

        this.save.setBounds(50,100,100,50);

        // make the following into a method maybe to reduce repeated code
        // onclick handler for the submit button
        this.save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Saving in progress");

                // save your progress
                Save.newSave(boardMap,score); // why does this.boardmap not work *********** ask mr benum + if i can use lambda **************
                //this.save.setEnabled(true); < ----- breaks the submit button visually

                // terminate program
                System.exit(0);
            }
        });
        // set dimensions of the button and add it to the JFrame
        this.save.setBounds(50,100,100,50);
        this.add(this.save);


        // vvvvv  not Josef's code  vvvvv
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setContentPane( this );

        f.pack();
        f.setVisible(true);
        // ^^^^^  not Josef's code  ^^^^^
    }

    public Board(int[][] grid, int rows, int cols) {
        super( true );

        // RENAME THE WINDOW HERE =================================================
        JFrame f = new JFrame( "512 ULTIMATE" );

        // sets the board map to the grid value of the argument passed for the grid parameter
        this.boardMap = grid;

        // sets rows and columns
        this.columns = cols;
        this.rows = rows;

        // vvvvv  not Josef's code vvvvv
        originalWidth = 2*X_OFFSET+X_DIM*cols;
        originalHeight = 2*Y_OFFSET+Y_DIM*rows+GAP+FONT_SIZE;

        this.setPreferredSize( new Dimension( originalWidth, originalHeight ) );

        f.setResizable(true);

        this.grid = new Color[cols][rows];
        this.setFocusable(true);
        // MUST ADD THE KEYLISTENER TO THE BOARD (JPANEL) =========================
        addKeyListener(this);
        // ^^^^^  not Josef's code  ^^^^^

//        this.addMouseListener(
//                new MouseInputAdapter()
//                {
//                    /** A method that is called when the mouse is clicked
//                     */
//                    public void mouseClicked(MouseEvent e)
//                    {
//                        int x = (int)e.getPoint().getX();
//                        int y = (int)e.getPoint().getY();
//
//                        // We need to by synchronized to the parent class so we can wake
//                        // up any threads that might be waiting for us
//                        synchronized(Board.this)
//                        {
//                            int curX = (int)Math.round(X_OFFSET*scale);
//                            int curY = (int)Math.round(Y_OFFSET*scale);
//                            int nextX = (int)Math.round((X_OFFSET+X_DIM*grid.length)*scale);
//                            int nextY = (int)Math.round((Y_OFFSET+Y_DIM*grid[0].length)*scale);
//
//                            // Subtract one from high end so clicks on the black edge
//                            // don't yield a row or column outside of board because of
//                            // the way the coordinate is calculated.
//                            if (x >= curX && y >= curY && x < nextX && y < nextY)
//                            {
//                                lastClick = new Coordinate(y,x);
//                                // Notify any threads that would be waiting for a mouse click
//                                Board.this.notifyAll() ;
//                            } /* if */
//                        } /* synchronized */
//                    } /* mouseClicked */
//                } /* anonymous MouseInputAdapater */
//        );


        // creates a new button for saving and exiting the program
        this.save =new JButton("Save And Exit");
        // make the following into a method maybe to reduce repeated code
        // onclick handler for the submit button
        this.save.addActionListener(e -> { // https://www.javatpoint.com/java-jbutton
            System.out.println("Saving in progress");

            // save your progress
            Save.newSave(this.boardMap,this.score); // why does this.boardmap not work *********** ask mr benum + if i can use lambda **************
            //this.save.setEnabled(true); < ----- breaks the submit button visually

            // terminate program
            System.exit(0);
        });
        // set dimensions of the button and add it to the JFrame
        this.save.setBounds(50,100,100,50);
        this.add(this.save);

        // vvvvv  not Josef's code  vvvvv
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setContentPane( this );
        f.pack();
        f.setVisible(true);
        // ^^^^^  not Josef's code  ^^^^^
    }


    // ADD THE KEYLISTENER EVENT METHODS HERE ====================================
    public void keyPressed(KeyEvent e) {

    }

    public void keyReleased(KeyEvent e) {

    }

    /**
     * Key event is called whenever a user types a key
     * */
    public void keyTyped(KeyEvent e) {
        key = e.getKeyChar();
    }

    // ACCESSOR METHOD THAT RETURNS THE KEY CHAR PRESSED ======================
    public char getKey(){
        char k = key;
        resetKey(); // resets the key so that the application doesn't repeatedly call the same method (get key is called on loop in the application classes main method)
        return k;
    }

    private void resetKey() {
        key = ' ';
    }

    private void paintText(Graphics g)
    {
        g.setColor( this.getBackground() );
        g.setFont(new Font(g.getFont().getFontName(), Font.ITALIC+Font.BOLD, (int)(Math.round(FONT_SIZE*scale))));


        // **************************** this stuff is for text under the board. it currently cuts off part of the border it will need to be lowered ***************
        int x = (int)Math.round(X_OFFSET*scale);
        int y = (int)Math.round((Y_OFFSET+Y_DIM*grid[0].length)*scale + GAP  ) + 5*(int)scale ;

//        this.save.setBounds(50,100,20*(int)scale,12*(int)scale);


        g.fillRect(x,y, this.getSize().width, (int)Math.round(GAP+FONT_SIZE*scale) );
        g.setColor( Color.DARK_GRAY );
        g.drawString(message+score, x, y + (int)Math.round(FONT_SIZE*scale));

        g.setColor( Color.WHITE );


        for (int i = 0; i < this.boardMap.length; i++)
        {
            for (int j = 0; j < this.boardMap[i].length; j++)
            {
//                g.drawString(message, x, y + (int)Math.round(FONT_SIZE*scale));

                int curX = (int)Math.round((X_OFFSET+X_DIM*i)*scale);
                int curY = (int)Math.round((Y_OFFSET+Y_DIM*j)*scale);
                int nextX = (int)Math.round((X_OFFSET+X_DIM*(i+1))*scale);
                int nextY = (int)Math.round((Y_OFFSET+Y_DIM*(j+1))*scale);
                int deltaX = nextX-curX;
                int deltaY = nextY-curY;
                // curX -deltax
//                g.fillRect( curX, curY, deltaX, deltaY );
//                Color curColour = this.grid[i][j];
//                if (curColour != null) // Draw pegs if they exist
//                {
//                    g.setColor(curColour);
//                    g.fillRect(curX + 7, curY + 7, deltaX, deltaY);
                g.setFont(new Font("Arial", Font.PLAIN,deltaY/(Utilities.intLen(this.boardMap[j][i]))));

                if (this.boardMap[j][i]  != 0) {
                    g.drawString(this.boardMap[j][i] + "",curX + 6*(int)scale,curY+23*(int)scale);
                }
//                }
//                if (this.boardMap[j][i]  != 0 ) {
//                    g.drawString(this.boardMap[j][i] + "", X_OFFSET*(int)scale + X_DIM * i*(int)scale + 1 * X_DIM / 4*(int)scale, //******************************************************
//                            Y_OFFSET*(int)scale + Y_DIM * j*(int)scale + 1 * Y_DIM / 2*(int)scale);
//                } else {
//                    g.drawString("", X_OFFSET*(int)scale + X_DIM * i *(int)scale+ 1 * X_DIM / 4*(int)scale, //******************************************************
//                            Y_OFFSET*(int)scale + Y_DIM * j*(int)scale + 1 * Y_DIM / 2*(int)scale);
//                }

            }
        }

//        this.save.setBounds((int)scale*20,(int)scale*10,(int)scale*70,(int)scale*35);

    }

    private void paintGrid(Graphics g)
    {
        for (int i = 0; i < this.boardMap.length; i++)
        {
            for (int j = 0; j < this.boardMap[i].length; j++)
            {
//                if ((i%2 == 0 && j%2 != 0) || (i%2 != 0 && j%2 == 0))
//                    g.setColor(Color.PINK);
//                else
//                    g.setColor(Color.LIGHT_GRAY);
                Color c;
                if (this.boardMap[j][i]==0) {
                    c = Color.lightGray;
                } else if (256-(int)(Utilities.log(2,this.boardMap[j][i]))*30>=0) {
                    c = new Color(256-(int)( Utilities.log(2,this.boardMap[j][i]))*30,0,0);
                } else {
                    c = new Color(this.r.nextInt(160)+40,this.r.nextInt(160)+40,this.r.nextInt(160)+40);
                }
                g.setColor(c);
                int curX = (int)Math.round((X_OFFSET+X_DIM*i)*scale);
                int curY = (int)Math.round((Y_OFFSET+Y_DIM*j)*scale);
                int nextX = (int)Math.round((X_OFFSET+X_DIM*(i+1))*scale);
                int nextY = (int)Math.round((Y_OFFSET+Y_DIM*(j+1))*scale);
                int deltaX = nextX-curX;
                int deltaY = nextY-curY;

                g.fillRoundRect( curX, curY, deltaX, deltaY,5*(int)scale,5*(int)scale);

//                g2d.drawRoundRect(curX, curY, deltaX, deltaY ,10,10);
//                g.setColor(Color.lightGray);
//                g.drawRect(curX, curY, deltaX, deltaY);

//                Color curColour = this.grid[i][j];
//                if (curColour != null) // Draw pegs if they exist
//                {
//                    g.setColor(curColour);
//                    g.fillRect(curX + 7, curY + 7, deltaX, deltaY);
//
//                }
//                this.save.setBounds(50,100,100*(int)scale,50*(int)scale);
            }
        }
        // https://docs.oracle.com/javase/tutorial/2d/geometry/strokeandfill.html#:~:text=To%20set%20the%20stroke%20attribute,rendered%20with%20the%20draw%20method.
        //  + casting graphics
        ((Graphics2D) g).setStroke( new BasicStroke( 5*(float)scale) );
        g.setColor(Color.BLUE);
        int curX = (int)Math.round(X_OFFSET*scale)-5*(int)scale;
        int curY = (int)Math.round(Y_OFFSET*scale)-5*(int)scale;
        int nextX = (int)Math.round((X_OFFSET+X_DIM*grid.length)*scale)+5*(int)scale;
        int nextY = (int)Math.round((Y_OFFSET+Y_DIM*grid[0].length)*scale)+5*(int)scale;
        g.drawRoundRect(curX, curY, nextX-curX, nextY-curY,15*(int)scale,15*(int)scale);
    }

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

    /**
     * Convert a String to the corresponding Color defaulting to Black
     * with an invald input
     */
    private Color convertColour( String theColour )
    {
        for( int i=0; i<COLOUR_NAMES.length; i++ )
        {
            if( COLOUR_NAMES[i].equalsIgnoreCase( theColour ) )
                return COLOURS[i];
        }

        return DEFAULT_COLOUR;
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

    public void setScale()
    {
        double width = (0.0+this.getSize().width) / this.originalWidth;
        double height = (0.0+this.getSize().height) / this.originalHeight;
        this.scale = Math.max( Math.min(width,height), MIN_SCALE );
    }

    /** Sets the message to be displayed under the board
     */
    public void displayMessage(String theMessage)
    {
        message = theMessage;
        this.repaint();
    }


    /** This method will save the value of the colour of the peg in a specific
     * spot.  theColour is restricted to
     *   "yellow", "blue", "cyan", "green", "pink", "white", "red", "orange"
     * Otherwise the colour black will be used.
     */
    public void putPeg(String theColour, int row, int col)
    {
        this.grid[col][row] = this.convertColour( theColour );
        this.repaint();
    }

    /** Same as putPeg above but for 1D boards
     */
    public void putPeg(String theColour, int col)
    {
        this.putPeg( theColour, 0, col );
    }

    /** Remove a peg from the gameboard.
     */
    public void removePeg(int row, int col)
    {
        this.grid[col][row] = null;
        repaint();
    }

    /** Same as removePeg above but for 1D boards
     */
    public void removePeg(int col)
    {
        this.grid[col][0] = null;
        repaint();
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

    /** Waits for user to click somewhere and then returns the click.
     */
    public Coordinate getClick()
    {
        Coordinate returnedClick = null;
        synchronized(this) {
            lastClick = null;
            while (lastClick == null)
            {
                try {
                    this.wait();
                } catch(Exception e) {
                    // We'll never call Thread.interrupt(), so just consider
                    // this an error.
                    e.printStackTrace();
                    System.exit(-1) ;
                } /* try */
            }

            int col = (int)Math.floor((lastClick.getCol()-X_OFFSET*scale)/X_DIM/scale);
            int row = (int)Math.floor((lastClick.getRow()-Y_OFFSET*scale)/Y_DIM/scale);

            // Put this into a new object to avoid a possible race.
            returnedClick = new Coordinate(row,col);
        }
        return returnedClick;
    }

    /** Same as getClick above but for 1D boards
     */
    public int getPosition()
    {
        return this.getClick().getCol();
    }

    public int getColumns()
    {
        return this.grid.length;
    }

    public int getRows()
    {
        return this.grid[0].length;
    }


    //************************************** only change beyond this point.
    // put shifting methods here

    /**
     * Precondition: the array must contain the data for the board, the array length and with must be the same as the boards length and width
     * Post: board is updated
     * */
    public void updateGUI() {
        repaint();
    }

    public boolean shiftLeft() throws InterruptedException {
        boolean moveHasOccured = false;
        for (int row = 0; row < this.rows; row++) {
            // makes a copy of the original row to check if row is changed, row change will only be animated if the row is changed
            int[] rowOriginal = Arrays.copyOf(this.boardMap[row],this.boardMap[row].length);
            // pull everything to the right
            for (int col = 0; col < this.columns; col++) {
                if (this.boardMap[row][col] != 0) {
                    int val = this.boardMap[row][col];
                    this.boardMap[row][col] = 0;
                    // go backwards
                    for (int i = col; i >= 0; i--) {
                        if (this.boardMap[row][i] != 0) {
                            if (val == this.boardMap[row][i]) {

                                this.boardMap[row][i] += val;
                                // increases score
                                this.score += val*2;
                                // another check to see if the number to the left is the same
                                // make this a recursive function so it checks as many times as need be *********************
                                if(i>0 && this.boardMap[row][i]==this.boardMap[row][i-1]) {
                                    this.boardMap[row][i-1] += this.boardMap[row][i];

                                    // increases score
                                    this.score += this.boardMap[row][i]*2;

                                    this.boardMap[row][i] = 0;

                                }
                                break;

                            } else {
                                this.boardMap[row][i + 1] = val;
                                break;

                            }
                        }
                        if (i == 0 && this.boardMap[row][i] == 0) {
                            this.boardMap[row][i] = val;
                            break;
                        }
                        //print2D(boardMap,row);

                    }
//                            print2D(boardMap,row);/..
                }
                // check if the row was changed only then u sleep to maike it faster
                // compare arrays and sleep only if the row has been changed
                System.out.println("oogabooga");
                Utilities.printArr(this.boardMap[row]);
                Utilities.printArr(rowOriginal);
                System.out.println("oogabooga");

                // only sleep/ animate if row has been changed
                if (!Utilities.compareIntegerArrays(rowOriginal,this.boardMap[row])) {
                    Thread.sleep(25);
                    moveHasOccured = true;
                }
                this.updateGUI();
            }
        }

        return moveHasOccured;
    }

    public boolean shiftRight() throws InterruptedException {
        boolean moveHasOccured = false;

        for (int row = 0; row < this.rows; row++) {
            // makes a copy of the original row to check if row is changed, row change will only be animated if the row is changed
            int[] rowOriginal = Arrays.copyOf(this.boardMap[row],this.boardMap[row].length);
            // pull everything to the left
            for (int col = this.columns-1; col >= 0; col--) {
                if (this.boardMap[row][col] != 0) {
                    int val = this.boardMap[row][col];
                    this.boardMap[row][col] = 0;
                    // go backwards
                    for (int i = col; i < this.columns; i++) {
                        if (this.boardMap[row][i] != 0) {
                            if (val == this.boardMap[row][i]) {
                                this.boardMap[row][i] += val;

                                // increases score
                                this.score += val*2;

                                if(i<this.columns-1 && boardMap[row][i]==this.boardMap[row][i+1]) {
                                    this.boardMap[row][i+1] += this.boardMap[row][i];
                                    // increase score
                                    this.score += this.boardMap[row][i]*2;
                                    this.boardMap[row][i] = 0;

                                }
                                break;
                            } else {
                                this.boardMap[row][i - 1] = val;
                                break;
                            }
                        }
                        if (i == this.columns-1 && this.boardMap[row][i] == 0) {
                            this.boardMap[row][i] = val;
                            break;
                        }
                    }
//                            print2D(boardMap,row);
                }
                // only sleep/ animate if row has been changed
                if (!Utilities.compareIntegerArrays(rowOriginal,this.boardMap[row])) {
                    Thread.sleep(25);
                    moveHasOccured = true;
                }
                this.updateGUI();
            }

        }
        return moveHasOccured;
    }

    public boolean shiftDown() throws InterruptedException {
        boolean moveHasOccured = false;
        for (int col = 0; col < this.columns; col++) {
            // get the colum. make .getcolumn method
            int[] colOriginal = Arrays.copyOf(Utilities.getColumn(this.boardMap, col),this.boardMap.length);

            // pull everything to the left
            for (int row = this.rows-1; row >= 0; row--) {
                if (this.boardMap[row][col] != 0) {
                    int val = this.boardMap[row][col];
                    this.boardMap[row][col] = 0;
                    // go backwards
                    for (int i = row; i < this.rows; i++) {
                        if (this.boardMap[i][col] != 0) {
                            if (val == this.boardMap[i][col]) {
                                this.boardMap[i][col] += val;
                                //increase score
                                this.score+= val*2;
                                if(i<this.rows-1 && this.boardMap[i][col]==this.boardMap[i+1][col]) {
                                    this.boardMap[i+1][col] += this.boardMap[i][col];
                                    // increase score
                                    this.score += this.boardMap[i][col]*2;

                                    this.boardMap[i][col] = 0;

                                }
                                break;
                            } else {
                                this.boardMap[i-1][col] = val;
                                break;
                            }
                        }
                        if (i == this.rows-1 && this.boardMap[i][col] == 0) {
                            this.boardMap[i][col] = val;
                            break;
                        }
                    }
//                            print2D(boardMap,row);
                }
                if (!Utilities.compareIntegerArrays(colOriginal,Utilities.getColumn(this.boardMap, col))) {
                    Thread.sleep(25);
                    moveHasOccured = true;
                }
                this.updateGUI();
            }

        }
        return moveHasOccured;
    }

    public boolean shiftUp() throws InterruptedException {
        boolean moveHasOccured = false;

        for (int col = 0; col < this.columns; col++) {
            int[] colOriginal = Arrays.copyOf(Utilities.getColumn(this.boardMap, col), this.boardMap.length);

            // pull everything to the right
            for (int row = 0; row < this.rows; row++) {
                if (this.boardMap[row][col] != 0) {
                    int val = this.boardMap[row][col];
                    this.boardMap[row][col] = 0;
                    // go backwards
                    for (int i = row; i >= 0; i--) {
                        if (this.boardMap[i][col] != 0) {
                            if (val == this.boardMap[i][col]) {
                                this.boardMap[i][col] += val;
                                // increase score
                                this.score += val * 2;
                                // another check to see if the number to the left is the same
                                if (i > 0 && this.boardMap[i][col] == this.boardMap[i - 1][col]) {
                                    this.boardMap[i - 1][col] += this.boardMap[i][col];
                                    // increase score
                                    this.score += this.boardMap[i][col] * 2;
                                    this.boardMap[i][col] = 0;

                                }
                                break;

                            } else {
                                this.boardMap[i + 1][col] = val;
                                break;

                            }
                        }
                        if (i == 0 && this.boardMap[i][col] == 0) {
                            this.boardMap[i][col] = val;
                            break;
                        }
                        //print2D(boardMap,row);

                    }
//                            print2D(boardMap,row);/..
                }
                if (!Utilities.compareIntegerArrays(colOriginal, Utilities.getColumn(this.boardMap, col))) {
                    Thread.sleep(25);
                    moveHasOccured = true;
                }
                this.updateGUI();
            }

        }
        return moveHasOccured;
    }

    public void addRandomTile() {
        // generate the tiles value randomly
        int newTile;

        double random = r.nextDouble();

        if (random<0.9) {
            newTile = 2;
        } else {
            newTile = 4;
        }

        int[][] openTiles;
        int numOpenTiles = this.getOpenTiles();

        openTiles = new int[numOpenTiles][2];
        int nthOpenTile = 0;

        if (openTiles.length == 0) return; // this is a loss


        for (int r = 0; r < this.rows; r++) {
            for (int c = 0; c < this.columns; c++) {
                if (this.boardMap[r][c] == 0) {
                    openTiles[nthOpenTile] =  new int[] {r, c};
                    nthOpenTile ++;
                }
            }
        }

        int randomIndex = this.r.nextInt(openTiles.length);


    //******************* I opted for a different approach as this can create an infinite loop when there is no free space ******************
//        // find an open spot
//        boolean spotIsValid = false;
//        int row = 0;
//        int col = 0;
//
//        while (!spotIsValid) {
//            // generate new coordinates within the bounds of height and width
//            row = r.nextInt(HEIGHT);
//            col = r.nextInt(WIDTH);
//
//            if (arr[row][col] == 0) {
//                spotIsValid = true;
//            }
//        }
        // 0 index is rows, 1st index is cols
        this.boardMap[openTiles[randomIndex][0]][openTiles[randomIndex][1]] = newTile;
    }

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

    public boolean nextMoveAvailable () {
        if (this.getOpenTiles() != 0) return true;

        // check if there are any two equal adjacent blocks
        for (int r = 0; r < this.rows; r++) {
            for (int c = 0; c < this.columns; c++) {
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

        return false;
    }
}
