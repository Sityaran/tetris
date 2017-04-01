/*
 * TCSS 305 Assignment 6 - Tetris
 */

package utility;

/**
 * Enums for all events sent by GUI classes to ensure consistency and reduce redundancy.
 * @author Tenma Rollins
 * @version 07 Mar 2017
 *
 */
public enum TetrisEvents {
    /** For when the game is paused. */
    PAUSED,
    
    /** For when the game is unpaused. */
    UNPAUSED,
    
    /** For when a game is running. */
    RUNNING,
    
    /** For when a new game is started. */
    NEW_GAME,
    
    /** For when a game that is running is ended. */
    END_GAME,
    
    /** For when the user wants to quit the application. */
    QUIT,
    
    /** For when the grid for the game board is changed. */
    GRID_SIZE_CHANGE,
    
    /** For when a new level is reached. */
    LEVEL_UP,
    
    /** For when sound is turned on. */
    SOUND_ON,
    
    /** For when sound is turned off. */
    SOUND_OFF,
    
    /** For when the color scheme is changed to standard. */
    STANDARD_COLOR_CHANGE,
    
    /** For when the color scheme is changed to dark. */
    DARK_COLOR_CHANGE,
    
    /** For when the color scheme should be inverted. */
    INVERT_COLOR_SCHEME_ON,
    
    /** For when the color scheme should not be inverted. */
    INVERT_COLOR_SCHEME_OFF;
}
