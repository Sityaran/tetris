/*
 * TCSS 305 Assignment 6 - Tetris
 */

package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import utility.Colors;
import utility.TetrisEvents;

/**
 * Main Display for the Tetris game. Handles drawing the game board in any particular game
 * state. 
 * 
 * @author Tenma Rollins
 * @version 03 Mar 2017
 *
 */
public class GameDisplay extends JPanel implements Observer {
    /** Generated serial version UID. */
    private static final long serialVersionUID = 4684162658019289757L;
    
    /** The tolerance for the window resize, in order to prevent any infinite resizing due to
     *  lack of floating point accuracy. 
     */
    private static final double TOLERANCE = 0.001;
    
    /** 
     * The minimum height the display can be (to line up with the right panel).
     */
    private static final int MIN_HEIGHT = 442;
        
    /** Screen overlay color for when the game is paused. */
    private static final Color PAUSE_COLOR = Color.GRAY;
    
    /** Screen overlay color for when the game is finished. */
    private static final Color GAMEOVER_COLOR = Color.RED;
    
    /** X grid size. Determines how many blocks wide the game board is. */
    private int myXGridSize;
    
    /** Y grid size. Determinse how many blocks tall the game board is. */
    private int myYGridSize;
    
    /** 
     * The scale factor for grid to whatever size the JPanel display is currently.
     * (essentially: 1 grid unit * scale factor = 1 display unit). Changes on resize.
     */
    private double myScaleFactor;
    
    /**
     * The parsed grid for the current game board to draw.
     */
    private final List<String> myGameGrid;
    
    /** The Color object that will create and manipulate color schemes. */
    private final Colors myColorChanger;
    
    /** Boolean flag to determine whether or not to invert the current color scheme. */
    private boolean myInvertFlag;

    /** The current color scheme. */
    private Map<Character, Color> myColorScheme;
    
    /** The current game state (See *_STATE constants for details). */
    private TetrisEvents myGameState;

    /**
     * Sets the initial values for any non-constant fields, as well as sets the minimum size.
     * 
     * @param theXGridSize The width of the grid as an int.
     * @param theYGridSize The height of the grid as an int.
     */
    public GameDisplay(final int theXGridSize, final int theYGridSize) {
        super();
        
        myXGridSize = theXGridSize;
        myYGridSize = theYGridSize;

        setInitialSize();
        setBackground(Color.WHITE);
        
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
        
        myGameGrid = new ArrayList<String>();
        myColorChanger = new Colors();
        myColorScheme = new TreeMap<Character, Color>();
    }

    /**
     * Draws onto the display depending on the current game state.
     * (i.e. if myGameState = RUN_STATE, then it draws the current game board).
     */
    @Override
    public void paintComponent(final Graphics theGraphics) {
        super.paintComponent(theGraphics);
        final Graphics2D g2d = (Graphics2D) theGraphics;
        
        // turn antialiasing on for more visually appealing drawing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);
        // resize the GUI before drawing anything
        resize();
        myScaleFactor = 1.0 * this.getHeight() / myYGridSize; 
        
        // make sure a color scheme exists
        if (myColorScheme.isEmpty()) {
            myColorScheme = myColorChanger.getDefaultColorScheme();
        }
        
        
        
        // determine state and draw appropriate screen
        if (myGameState == TetrisEvents.RUNNING) {
            drawGrid(g2d);
        } else if (myGameState == TetrisEvents.PAUSED) {
            drawScreenOverlay(g2d, "PAUSED", PAUSE_COLOR);
        } else if (myGameState == TetrisEvents.END_GAME) {
            drawScreenOverlay(g2d, "GAME OVER", GAMEOVER_COLOR);
        }
    }

    /**
     * Handles update info from the GUI and the backend logic board.
     */
    @Override
    public void update(final Observable theObserveable, final Object theObject) {
        final String updateInfo = theObject.toString();
        
        if (theObject instanceof String) {
            parseBoard(updateInfo);
        } else if (theObject instanceof Dimension) {
            myXGridSize = ((Dimension) theObject).width;
            myYGridSize = ((Dimension) theObject).height;
            setInitialSize();
        } else {
            checkGameStateChange(theObject);
            checkColorChange(theObject);
        }
        
        repaint();
    }
    
    /** 
     * Helper method for checking for changes in the current game state.
     * 
     * @param theObject The update object to be checked.
     */
    private void checkGameStateChange(final Object theObject) {
        if (theObject instanceof Boolean || theObject == TetrisEvents.END_GAME) {
            myGameState = TetrisEvents.END_GAME;
        } else if (myGameState == TetrisEvents.RUNNING && theObject == TetrisEvents.PAUSED) {
            myGameState = TetrisEvents.PAUSED;
        } else if (myGameState == TetrisEvents.PAUSED && theObject == TetrisEvents.UNPAUSED) {
            myGameState = TetrisEvents.RUNNING;
        } else if (theObject == TetrisEvents.NEW_GAME) {
            myGameState = TetrisEvents.RUNNING;
            myGameGrid.clear();
        }
    }
    
    /** 
     * Helper method for checking for changes to the color scheme.
     * 
     * @param theObject The update object to be checked.
     */
    private void checkColorChange(final Object theObject) {
        if (theObject == TetrisEvents.STANDARD_COLOR_CHANGE) {
            myColorScheme = myColorChanger.getDefaultColorScheme();
            checkIfInvert();
        } else if (theObject == TetrisEvents.DARK_COLOR_CHANGE) {
            myColorScheme = myColorChanger.getDarkColorScheme();
            checkIfInvert();
        } else if (theObject == TetrisEvents.INVERT_COLOR_SCHEME_ON) {
            myInvertFlag = true;
            checkIfInvert();
            setBackground(Color.BLACK);
        } else if (theObject == TetrisEvents.INVERT_COLOR_SCHEME_OFF) {
            checkIfInvert(); // reinverts scheme to get original colors
            myInvertFlag = false;
            setBackground(Color.WHITE);
        }
    } 

    /**
     * Checks if the invert flag is true and, if so, inverts the current color scheme.
     */
    private void checkIfInvert() {
        if (myInvertFlag) {
            myColorScheme = myColorChanger.invert(myColorScheme);
        }
    }

    /**
     * Sets the initial size of the display for a new grid size or a new game. Also updates
     * the display properly so that it is visually correct.
     */
    private void setInitialSize() {
        final Dimension size = new Dimension((int) (MIN_HEIGHT * 1.0 * myXGridSize 
                        / myYGridSize), MIN_HEIGHT);
        setMinimumSize(size);
        setPreferredSize(size);
        revalidate(); // so that the panel resizes properly 
    }
    
    /**
     * Resizes the JFrame to maintain the proper aspect ratio (currently 1:2 for w:h).
     */
    private void resize() {
        final Dimension currentSize = this.getSize();
        
        // get the current width and height
        final int width = currentSize.width;
        final int height = currentSize.height;
        
        // find my current desired ratio
        final double gridRatio = (1.0 * myXGridSize) / myYGridSize;

        // calculate as if width is limiting
        int newWidth = width;
        int newHeight = Math.max((int) ((1.0 / gridRatio) * width), MIN_HEIGHT);
        
        // if height is limiting, recalculate until within tolerance
        if ((gridRatio - (1.0 * width) / height) < TOLERANCE) {
            newHeight = height;
            newWidth = (int) (gridRatio * newHeight);
        }
        
        // set size so width:height matches with desired ratio !
        final Dimension newSize = new Dimension(newWidth, newHeight);
        setPreferredSize(newSize);
        setSize(newSize);
    }

    /**
     * Draws a screen overlay given some text and a color.
     * 
     * @param theG2D The Graphics2D object to use to draw on the JPanel. 
     * @param theText The text to display.
     * @param theColor The color to display as the screen overlay (under the text).
     */
    private void drawScreenOverlay(final Graphics2D theG2D, final String theText, 
                                 final Color theColor) {
        theG2D.setColor(theColor);
        theG2D.fillRect(0, 0, this.getWidth(), this.getHeight());
        // need more robust way of determining pause font size, look into that
        final String displayText = theText;
        final int xOffset = 4;
        final int yOffset = 2;
        theG2D.setColor(Color.WHITE);
        theG2D.setFont(new Font("Consolas", Font.PLAIN, 
                                this.getWidth() / displayText.length()));
        theG2D.drawString(displayText, this.getWidth() / xOffset , this.getHeight() / yOffset);
    }

    /**
     * Draws the game board based on the current contents of myGameGrid.
     * 
     * @param theG2D The Graphics2D object to use to draw on the JPanel. 
     */
    private void drawGrid(final Graphics2D theG2D) {
        for (int y = 0; y < myGameGrid.size(); y++) {
            final char[] gameTiles = myGameGrid.get(y).toCharArray();
            for (int x = 0; x < gameTiles.length; x++) {
                if (gameTiles[x] != ' ') {
                    final Rectangle2D.Double tile = new Rectangle2D.Double(myScaleFactor * x, 
                                                                     myScaleFactor * y,
                                                                     myScaleFactor, 
                                                                     myScaleFactor);
                    theG2D.setColor(myColorScheme.get(gameTiles[x]));
                    theG2D.fill(tile);
                    if (myInvertFlag) {
                        theG2D.setColor(Color.WHITE);
                    } else {
                        theG2D.setColor(Color.BLACK);
                    }
                    theG2D.draw(tile);
                }
            }
        }
    }
    
    /**
     * Parses the string from the game board update so that myGameBoard holds only the strings
     * for the part of the board that can change (and that we can see). Thus, borders are 
     * omitted through this parse.
     * 
     * @param theBoard The game board (as a String) to parse.
     */
    private void parseBoard(final String theBoard) { 
        //System.err.println("BEGIN BOARD PARSE");
        String board = theBoard;
        
        myGameGrid.clear();
        
        //get playable gameboard
        board = board.substring(board.indexOf("-\n") + 2, board.indexOf("|-"));
        //System.out.println(board);
        
        while (board.length() > 0) {
            final int endIndex = board.indexOf("|\n");
            
            // add the current line to the game grid
            final String line = board.substring(1, endIndex);
            myGameGrid.add(line);
            
            // truncate the remaining string
            board = board.substring(endIndex + 2);
        }
    }
}
