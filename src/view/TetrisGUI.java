/*
 * TCSS 305 Assignment 6 - Tetris
 */

package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.Timer;

import model.Board;
import utility.TetrisEvents;

/**
 * GUI code for the visual appearance and layout of all components of the Tetris game, as well
 * as the connection of listeners and observers to the respective components.
 * 
 * @author Tenma Rollins
 * @version 23 Feb 2017
 *
 */
public final class TetrisGUI extends Observable implements Observer, PropertyChangeListener {  
    /** The initial delay before the game begins. */
    private static final int TIMER_GAMESTART_DELAY = 2000;
    
    /** The initial delay of the timer for the first level of Tetris. */
    private static final int TIMER_INITIAL_DELAY = 1000;
    
    /** Default padding amount (in pixels) for any components. */
    private static final int COMPONENT_PADDING = 10;

    /** Default window icon to use. */
    private static final ImageIcon DEFAULT_ICON = new ImageIcon("images/default.png");
    
    /** Default size for any fixed-size components. */
    private static final Dimension STATS_COMPONENT_SIZE = new Dimension(150, 150);
    
    /** The main JFrame to display the game. */
    private final JFrame myFrame;
    
    /** The timer to control animation speed. */
    private final Timer myTimer;
    
    /** KeyListener for connection keystrokes to actions. */
    private final KeyboardListener myKeyListener;
    
    /** The information panel for the controls of the game. */
    private final GameInfo myInfo;
    
    /** The menubar for the JFrame. */
    private final MenuBar myMenuBar;

    /** The graphical display of the board. */
    private GameDisplay myDisplay;
    
    /** The graphical preview of the next piece to fall. */
    private PiecePreview myPreview;
    
    /** The scoreboard panel to keep track of score, level, etc. */
    private Scoreboard myScoreboard;
    
    /** The backend logic board to provide all necessary update info for the game. */
    private Board myLogicBoard;
    
    /** The current game state (false for paused/gameover, true for playing). */
    private TetrisEvents myGameState;
    
    /** The current board width. */
    private int myXGridSize;
    
    /** The current board height. */
    private int myYGridSize;

    
    /** 
     * Creates the references to the main frame and window icon to use. 
     */
    public TetrisGUI() {
        super();
        myFrame = new JFrame("TCSS 305 - Tetris");
        
        myTimer = new Timer(TIMER_INITIAL_DELAY, new TimerListener());
        
        myKeyListener = new KeyboardListener();
        
        myInfo = new GameInfo(STATS_COMPONENT_SIZE);  
        myMenuBar = new MenuBar();
    }
    
    /**
     * Sets up and begins running the GUI.
     */
    public void start() {        
        myGameState = TetrisEvents.END_GAME;
        
        final int defaultWidth = 10;
        final int defaultHeight = 20;
        myDisplay = new GameDisplay(defaultWidth, defaultHeight);
        myPreview = new PiecePreview(STATS_COMPONENT_SIZE);
        myScoreboard = new Scoreboard(STATS_COMPONENT_SIZE, myTimer);
        
        // #########################
        // SETUP VISUAL COMPONENTS #
        // #########################
        myPreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        myScoreboard.setBorder(BorderFactory.createTitledBorder("Scoreboard"));
        myInfo.setBorder(BorderFactory.createTitledBorder("Controls"));
        
        // create the stats panel (all supplementary info to the game board)
        final Box statsPanel = Box.createVerticalBox();
        statsPanel.add(Box.createVerticalStrut(COMPONENT_PADDING));
        statsPanel.add(myPreview);
        statsPanel.add(Box.createVerticalStrut(COMPONENT_PADDING));
        statsPanel.add(myScoreboard);
        statsPanel.add(Box.createVerticalStrut(COMPONENT_PADDING));
        statsPanel.add(myInfo);
        statsPanel.add(Box.createVerticalGlue());
        
        // create the rightPanel so side padding can be added
        final Box rightPanel = Box.createHorizontalBox();
        rightPanel.add(statsPanel);
        rightPanel.add(Box.createHorizontalStrut(COMPONENT_PADDING));
              
        // create the main panel to be added to the jframe
        final Box mainPanel = Box.createHorizontalBox();
        mainPanel.add(Box.createHorizontalGlue());
        mainPanel.add(myDisplay);
        mainPanel.add(Box.createHorizontalStrut(COMPONENT_PADDING));
        // for reference: createEmptyBorder(top,left,bottom,right)
        mainPanel.setBorder(BorderFactory.createEmptyBorder(COMPONENT_PADDING, 
                                                            COMPONENT_PADDING,
                                                            COMPONENT_PADDING, 
                                                            COMPONENT_PADDING));
        
        // add everything to frame
        myFrame.add(mainPanel, BorderLayout.CENTER);  
        myFrame.add(rightPanel, BorderLayout.EAST);
        
        myFrame.setJMenuBar(myMenuBar.getMenu());

        // ###################
        // CONNECT LISTENERS #
        // ###################
        addObserver(this);
        addObserver(myMenuBar);
        addObserver(myDisplay);
        
        myFrame.addKeyListener(myKeyListener);
        
//        myKeyListener.addObserver(this);
//        myKeyListener.addObserver(myDisplay);
        
        myScoreboard.addPropertyChangeListener(this);
        
        myMenuBar.addObserver(this);
        myMenuBar.addObserver(myScoreboard);
        myMenuBar.addObserver(myDisplay);
        myMenuBar.addObserver(myPreview);
        myMenuBar.addObserver(myMenuBar);
        
        // to deal with focus and focus lost events        
        final FocusListener focusListener = new FocusListener() {
            @Override
            public void focusGained(final FocusEvent theEvent) {
                setChanged();
                notifyObservers(TetrisEvents.UNPAUSED);
            }
            @Override
            public void focusLost(final FocusEvent theEvent) {
                setChanged();
                notifyObservers(TetrisEvents.PAUSED);
            }
        };
        myFrame.addFocusListener(focusListener);
        
        // ###############################
        // FORMAT AND DISPLAY MAIN FRAME #
        // ###############################
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.setIconImage(DEFAULT_ICON.getImage());
        myFrame.setBackground(Color.WHITE);
        
        myFrame.pack();
        myFrame.setMinimumSize(myFrame.getSize());
        myFrame.setLocationRelativeTo(null);
        myFrame.setVisible(true);
    }

    /**
     * Gets update information from the myKeyListener to determine game state.
     */
    @Override
    public void update(final Observable theObservable, final Object theObject) {
        if (theObject instanceof Dimension) {
            myXGridSize = ((Dimension) theObject).width;
            myYGridSize = ((Dimension) theObject).height;
        } else {
            checkGameStateChange(theObject);
        }
    }

    @Override
    public void propertyChange(final PropertyChangeEvent theEvent) {
        final String propertyType = theEvent.getPropertyName();
        if (TetrisEvents.LEVEL_UP.toString().equals(propertyType)) {
            final int currentLevel = (int) theEvent.getNewValue();
            
            // create some new delay that is smaller but not too absurd
            final int newDelay = (int) Math.max(100.0,
                                            TIMER_INITIAL_DELAY * Math.pow(0.9, currentLevel));
            myTimer.setDelay(newDelay);
        } else if (TetrisEvents.GRID_SIZE_CHANGE.toString().equals(propertyType)) {
            final int[] gridSize = (int[]) theEvent.getNewValue();
            
            myXGridSize = gridSize[0];
            myYGridSize = gridSize[1];
            
            // after every grid size change we start a new game
            setChanged();
            notifyObservers(TetrisEvents.NEW_GAME);
        }
    }
    
    /** 
     * Helper method for checking for changes in the current game state.
     * 
     * @param theObject The update object to be checked.
     */
    private void checkGameStateChange(final Object theObject) {
        if (myGameState != TetrisEvents.END_GAME && theObject == TetrisEvents.PAUSED) {
            myTimer.stop();
            myGameState = TetrisEvents.PAUSED;
        } else if (myGameState != TetrisEvents.END_GAME 
                        && theObject == TetrisEvents.UNPAUSED) {
            myTimer.start();
            myGameState = TetrisEvents.RUNNING;
        } else if (theObject == TetrisEvents.NEW_GAME) {
            startNewGame();
        } else if (theObject instanceof Boolean || theObject == TetrisEvents.END_GAME) {
            myGameState = TetrisEvents.END_GAME;
        } else if (theObject == TetrisEvents.QUIT) {
            myFrame.dispatchEvent(new WindowEvent(myFrame, WindowEvent.WINDOW_CLOSING));
        }
    }
    
    /**
     * Starts a new game by removing observers from the current board reference, creating a
     * new board with the current width and height, resetting the timer, and resetting the
     * main window frame.
     */
    private void startNewGame() {
        if (myLogicBoard != null) {
            myLogicBoard.deleteObservers(); // to prevent listening in on forgotten instances
        }
        
        myLogicBoard = new Board(myXGridSize, myYGridSize);
        reconnectLogicBoardObservers();

        // so that the frame can be set smaller than it previously was if needed
        myFrame.setMinimumSize(new Dimension(0, 0)); 
        myFrame.pack();
        myFrame.setMinimumSize(myFrame.getSize());
        // relocate the frame so it is centered on the screen
        myFrame.setLocationRelativeTo(null);
        
        myTimer.setInitialDelay(TIMER_GAMESTART_DELAY);
        myTimer.setDelay(TIMER_INITIAL_DELAY);
        myTimer.start();
        myLogicBoard.newGame();
        myGameState = TetrisEvents.RUNNING;
    }

    /**
     * Helper method for reconnecting observers to new instances of the logic board.
     */
    private void reconnectLogicBoardObservers() {
        myLogicBoard.addObserver(this);
        myLogicBoard.addObserver(myDisplay);
        myLogicBoard.addObserver(myPreview);
        myLogicBoard.addObserver(myScoreboard);
        myLogicBoard.addObserver(myMenuBar);
    }
    
    /**
     * Handles all relevant key strokes and their corresponding actions. 
     * 
     * @author Tenma Rollins
     * @version 03 Mar 2017
     *
     */
    private class KeyboardListener extends KeyAdapter {  
        // note to self, consider implementing keybinding to reduce if/if-else statement usage
        /**
         * Connects keystrokes to actions. Also disables any actions corresponding to movement
         * on the Tetris board when myGameState is false.
         * 
         * Current default keymapping: 
         * W, w, up-arrow       => ROTATE
         * A, a, left-arrow     => LEFT
         * D, d, right-arrow    => RIGHT
         * S, s, down-arrow     => DOWN
         * Space-bar            => DROP
         * P, p                 => PAUSE
         * 
         * 
         */
        @Override
        public void keyPressed(final KeyEvent theEvent) {
            final int key = theEvent.getKeyCode();
            
            // only check controls if the game state is true
            if (myGameState == TetrisEvents.RUNNING) {
                checkControls(key);
            }
            
            if (key == KeyEvent.VK_P && myGameState != TetrisEvents.END_GAME) {
                if (myTimer.isRunning()) {
                    setChanged();
                    notifyObservers(TetrisEvents.PAUSED);
                } else {
                    setChanged();
                    notifyObservers(TetrisEvents.UNPAUSED);
                }
            }
        }
        
        /**
         * Helper method to check if the given key corresponds to any particular control key.
         * 
         * @param theKey The given key to check against.
         */
        private void checkControls(final int theKey) {
            if (checkMultiKey(theKey, KeyEvent.VK_W, KeyEvent.VK_UP)) {
                myLogicBoard.rotate();
            } else if (checkMultiKey(theKey, KeyEvent.VK_A, KeyEvent.VK_LEFT)) {
                myLogicBoard.left();
            } else if (checkMultiKey(theKey, KeyEvent.VK_D, KeyEvent.VK_RIGHT)) {
                myLogicBoard.right();
            } else if (checkMultiKey(theKey, KeyEvent.VK_S, KeyEvent.VK_DOWN)) {
                myLogicBoard.down();
            } else if (theKey == KeyEvent.VK_SPACE) {
                myLogicBoard.drop();
            }
        }

        /**
         * Checks multiple keys against the given key to reduce logic statement complexity.
         * 
         * @param theKey The given key to be checked against.
         * @param key1 The first key to be checked.
         * @param key2 The second key to be checked.
         * @return True if theKey matches key1 OR key2, otherwise false.
         */
        private boolean checkMultiKey(final int theKey, final int key1, final int key2) {
            return theKey == key1 || theKey == key2;
        }
    }
    
    /**
     * Listener for timer update events. 
     * 
     * @author Tenma Rollins
     * @version 03 Mar 2017
     *
     */
    private class TimerListener implements ActionListener {
        /**
         *  Preforms the relevant action on a timer update (which is to drop the piece down
         *  by 1).
         */
        @Override
        public void actionPerformed(final ActionEvent theEvent) {
            final Object source = theEvent.getSource();
            
            if (source.equals(myTimer)) {
                myLogicBoard.down(); // move the piece down every time the timer updates
            }
        }
        
    }

}
