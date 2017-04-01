/*
 * TCSS 305 Assignment 6 - Tetris
 */

package view;

import java.awt.Color;
import java.awt.Dimension;
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

import javax.swing.JPanel;

import model.MovableTetrisPiece;
import utility.Colors;
import utility.TetrisEvents;

/**
 * Displays the preview for the next piece to be dropped into the game board.
 * 
 * @author Tenma Rollins
 * @version 03 Mar 2017
 *
 */
public class PiecePreview extends JPanel implements Observer {
    /** Generated serial version UID. */
    private static final long serialVersionUID = 6646002635448560996L;
    
    /** 
     * Since the preview will be some JPanel where width = height, this represents how many
     * grid-boxes I want to fit into the preview.
     * (i.e. GRID_SIZE = 5 means a 5x5 grid, which will affect the scale factor for drawing
     * any individual tile/box of a given piece).
     */
    private static final int GRID_SIZE = 5;
    
    /** The width of a 3-wide piece. */
    private static final int THREE_WIDE_PIECE = 3;
    
    /** The width of a 4-wide piece. */
    private static final int FOUR_WIDE_PIECE = 4;
    
    /** The maximum width for any piece (also the max height for any piece). */
    private static final int MAX_PIECE_WIDTH = 4; 

    /** The scale factor of grid units to display units for width. */
    private final double myXScale;
    
    /** The scale factor of grid units to display units for height. */
    private final double myYScale;
    
    /** The current piece to draw. */
    private final List<String> myPiece;

    /** The Color object that will create and manipulate color schemes. */
    private final Colors myColorChanger;
    
    /** Boolean flag to determine whether or not to invert the current color scheme. */
    private boolean myInvertFlag;

    /** The current color scheme. */
    private Map<Character, Color> myColorScheme;
    
    /** The current width of myPiece. */
    private int myPieceWidth;

    /**
     * Sets up the size and look of the preview panel. Also sets the initial scale 
     * for drawing pieces.
     * 
     * @param theSize The fixed size for this preview.
     */
    public PiecePreview(final Dimension theSize) {
        super();
        
        setPreferredSize(theSize);
        setMaximumSize(theSize);
        setBackground(Color.WHITE);
        
        myXScale = theSize.getWidth() / GRID_SIZE;
        myYScale = theSize.getHeight() / GRID_SIZE;
        
        myPiece = new ArrayList<String>();
        
        myColorChanger = new Colors();
        myColorScheme = new TreeMap<Character, Color>();
        
        myInvertFlag = false;
    }

    /**
     *  Draws the piece with correct offset to center.
     */
    @Override
    public void paintComponent(final Graphics theGraphics) {
        super.paintComponent(theGraphics);
        final Graphics2D g2d = (Graphics2D) theGraphics;

        // turn antialiasing on for more visually appealing drawing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);
        
        // make sure a color scheme exists
        if (myColorScheme.isEmpty()) {
            myColorScheme = myColorChanger.getDefaultColorScheme();
        }
        
        drawPiece(g2d);
    }

    /**
     * Handles the update information from the backend logic board. If information about the
     * next game piece is passed to theObject, then it parses theObjet.toString.
     */
    @Override
    public void update(final Observable theObserveable, final Object theObject) {
        // MoveableTetrisPiece => Preview Piece Information
        if (theObject instanceof MovableTetrisPiece) {
            final MovableTetrisPiece currentPiece = (MovableTetrisPiece) theObject; 
            parsePiece(currentPiece.toString());  
            myPieceWidth = currentPiece.getWidth();
        } else if (theObject == TetrisEvents.STANDARD_COLOR_CHANGE) {
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
        
        repaint();
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
     * Handles the actual string parse for the piece given the correctly formatted string.
     * 
     * @param thePiece The piece (as a string) to parse.
     */
    private void parsePiece(final String thePiece) {
        String piece = thePiece;
        myPiece.clear();
        
        while (piece.length() > 0) {
            myPiece.add(piece.substring(0, MAX_PIECE_WIDTH));
            piece = piece.substring(MAX_PIECE_WIDTH + 1);
        }
    }
    
    /**
     * Draws the current piece with the current color scheme centered in the preview panel.
     * 
     * @param theG2D The Graphics2D object to draw with.
     */
    private void drawPiece(final Graphics2D theG2D) {
        // draw the piece in the preview panel
        // to do : better math for centering the piece in the panel
        for (int y = 0; y < myPiece.size(); y++) {
            final char[] pieceTiles = myPiece.get(y).toCharArray();
            for (int x = 0; x < pieceTiles.length; x++) {
                if (pieceTiles[x] != ' ') {
                    // O piece center is already centered
                    double xOffset = (GRID_SIZE - MAX_PIECE_WIDTH) / 2.0;
                    double yOffset = (GRID_SIZE - MAX_PIECE_WIDTH) / 2.0;
                    // centers for any piece only differ by 0.5 in x or y
                    final double centeringOffset = 0.5; 
                    
                    if (myPieceWidth == THREE_WIDE_PIECE) {
                        // to align the center of any 3-wide pieces, you need a .5 x offset
                        xOffset = xOffset + centeringOffset; 
                    } else if (myPieceWidth == FOUR_WIDE_PIECE) {
                        yOffset = yOffset + centeringOffset; 
                        // 4-wide piece is the I piece, which is on its side by default so
                        // lower by 0.5 to center
                    }
                    
                    final Rectangle2D.Double tile = 
                                    new Rectangle2D.Double(myXScale * (x + xOffset),
                                                           myYScale * (y + yOffset),
                                                           myXScale, myYScale);
                    theG2D.setColor(myColorScheme.get(pieceTiles[x]));
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
}
