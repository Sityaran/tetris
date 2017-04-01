/*
 * TCSS 305 Assignment 6 - Tetris
 */

package view;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import utility.TetrisEvents;

/**
 * Scoreboard code for the Tetris game. Currently a WORK-IN-PROGRESS.
 * 
 * @author Tenma Rollins
 * @version 03 Mar 2017
 *
 */
public class Scoreboard extends JPanel implements Observer {    
    /** Generated serial version UID. */
    private static final long serialVersionUID = 1102657896549936653L;
    
    /** The number of lines you can clear before the next level starts. */
    private static final int LINE_ROLLOVER = 5;
    
    /** Default padding for any components. */
    private static final int PADDING = 10;
    
    /** 
     * The score for # of lines cleared at the same time. 
     * (pulled from a description about Tetris scoring).
     */
    private static final int[] LINE_SCORES = {40, 100, 300, 1200};

    /** The timer that is being used for the game. */
    private final Timer myTimer;
    
    /** The total number of lines cleared so far. */
    private int myLinesCleared;
    
    /** The total score so far. */
    private int myScore;
    
    /** The current level. */
    private int myLevel;

    /** The amount of lines until the next level. */
    private int myNextLevel;
    
    /** The JLabel that displays the score text. */
    private JLabel myScoreLabel;
    
    /** The JLabel that displays the number of lines cleared text. */
    private JLabel myLinesLabel;
    
    /** The JLabel that displays the current level text. */
    private JLabel myLevelLabel;
    
    /** The JLabel that displays the amount of lines until the next level text. */
    private JLabel myNextLevelLabel;

    /** The JLabel that displays the current drop speed (aka the current timer delay). */
    private JLabel myCurrentDropSpeed;

    
    /**
     * Sets the size for the score board and initializes all statistics (score, lines, level)
     * to 0.
     * 
     * @param theSize The fixed size for this score board.
     * @param theTimer A reference to the timer being used for the game.
     */
    public Scoreboard(final Dimension theSize, final Timer theTimer) {
        super();

        setMaximumSize(theSize);
        setBackground(Color.WHITE);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        myTimer = theTimer;
        
        setup();
        
        reset();
    }

    /**
     * Handles update info. If an Integer[] is passed, this means lines have been cleared, so
     * then update the score board appropriately.
     */
    @Override
    public void update(final Observable theObservable, final Object theObject) {
        if (theObject instanceof Integer[]) {
            final Integer[] rows = ((Integer[]) theObject).clone();
            final int rowsCleared = rows.length;
            
            final int previousLevel = myLevel;
            
            myLinesCleared = myLinesCleared + rowsCleared;
            myScore = myScore + LINE_SCORES[rowsCleared - 1] * (myLevel + 1);
            myLevel = myLinesCleared / LINE_ROLLOVER;
            myNextLevel = LINE_ROLLOVER - myLinesCleared % LINE_ROLLOVER;
            
            if (previousLevel != myLevel) {
                firePropertyChange(TetrisEvents.LEVEL_UP.toString(), null, myLevel);
            }
            
            updateText();
        } else if (theObject == TetrisEvents.NEW_GAME) {
            reset();
        }
    } 
    
    /**
     * Sets up the score board as it should be layed out:
     * Score : ....
     * Lines Cleared : ....
     * Level : ....
     * - Next Level in ... lines! -
     * Drop speed: ...
     */
    private void setup() {        
        // initialize each label
        myScoreLabel = new JLabel();
        myScoreLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        
        myLinesLabel = new JLabel();
        myLinesLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        myLevelLabel = new JLabel();
        myLevelLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        
        myNextLevelLabel = new JLabel();
        myNextLevelLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        
        myCurrentDropSpeed = new JLabel();
        myCurrentDropSpeed.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        
        // make sure the labels actually have text to begin with
        updateText();
        
        // add everything to the scoreboard
        add(myScoreLabel);
        add(myLinesLabel);
        add(myLevelLabel);
        add(Box.createVerticalStrut(PADDING));
        add(myNextLevelLabel);
        add(Box.createVerticalStrut(PADDING));
        add(myCurrentDropSpeed);
    }
    
    /**
     * Updates the text for the labels. Should be called only when these values update which
     * happens after certain information is passed in the update() function.
     */
    private void updateText() {
        myScoreLabel.setText("Score: " + myScore);
        myLinesLabel.setText("Lines Cleared: " + myLinesCleared);
        myLevelLabel.setText("Level: " + myLevel);
        myNextLevelLabel.setText("- Next Level in " + myNextLevel + " lines! -");
        myCurrentDropSpeed.setText("Drop speed: " + myTimer.getDelay() + "ms");
        
        repaint();
    }
    
    /**
     * Resets the scoreboard with proper initial values.
     */
    private void reset() {
        myScore = 0;
        myLinesCleared = 0;
        myLevel = 1; // always start on 1st level
        
        updateText();
    }
}
