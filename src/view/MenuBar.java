/*
 * TCSS 305 Assignment 6 - Tetris
 */

package view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import utility.MusicPlayer;
import utility.TetrisEvents;

/**
 * Class to create and provide a menubar for the GUI, as well as handle events to and from the
 * menu bar.
 * 
 * @author Tenma Rollins
 * @version 07 Mar 2017
 *
 */
public final class MenuBar extends Observable implements Observer {
    /** The path to the classic Tetris song file. */
    private static final String CLASSIC_TETRIS_SONG = "sound/Tetris.mp3";
    
    /** The path to the remixed Tetris song file. */
    private static final String REMIX_TETRIS_SONG = "sound/TetrisRemix.mp3";
    
    /** The path to the default icon (the same as the main window icon). */
    private static final ImageIcon DEFAULT_ICON = new ImageIcon("images/default.png");
    
    /** The path to the sidebar icon for the scoring information window. */
    private static final ImageIcon SIDEBAR_ICON = new ImageIcon("images/sidebar.jpg");
    
    /** The menubar itself. */
    private final JMenuBar myMenuBar;

    /** The music player (for playing mp3s). */
    private final MusicPlayer myMusicPlayer;
    
    /** The current grid width. */
    private int myXGridSize;
    
    /** The current grid height. */
    private int myYGridSize;
    
    /** The boolean flag to determine whether or not to mute the music. */
    private boolean myMuteFlag;
    
    /** The current sound file to be played. */
    private String mySoundFile;
    
    /** The end game button (only to be enabled when a game is in progress. */
    private JMenuItem myEndGameButton;
    
    /**
     * Creates and sets up the menubar as well as the music player.
     */
    public MenuBar() {
        super();
        
        myMenuBar = new JMenuBar();
        myMusicPlayer = new MusicPlayer();
        myMusicPlayer.setShouldLoop(true); // makes sure any song plays on loop
        
        myMuteFlag = true;
        
        setupFileMenu();
        setupOptionsMenu();
        setupHelpMenu();
    }
    
    /**
     * Provides the menubar so that it can be added to a JFrame or otherwise.
     * @return The menubar as a JMenuBar.
     */
    public JMenuBar getMenu() {
        return myMenuBar;
    }
    
    /**
     * Handles update info from the GUI and the backend logic board.
     */
    @Override
    public void update(final Observable theObservable, final Object theObject) {
        if (theObject == TetrisEvents.NEW_GAME) {
            myEndGameButton.setEnabled(true);
            myMusicPlayer.play();
        } else if (theObject instanceof Boolean || theObject == TetrisEvents.END_GAME) {
            myEndGameButton.setEnabled(false);
            if (myMusicPlayer.isStarted()) {
                myMusicPlayer.stopPlay();
            }
        }
    }
    
    

    /**
     * Sets up the "File" menu.
     */
    private void setupFileMenu() {
        // create menu and components
        final JMenu file = new JMenu("File");
        
        final JMenuItem newGame = new JMenuItem("New Game");
        final JMenuItem endGame = new JMenuItem("End Game");
        final JMenuItem quitGame = new JMenuItem("Quit");
        
        // add actions to each menu item
        newGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent theEvent) {
                setupNewGame();
            }
        });
        endGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent theEvent) {
                setChanged();
                notifyObservers(TetrisEvents.END_GAME);
            }
        });
        quitGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent theEvent) {
                setChanged();
                notifyObservers(TetrisEvents.QUIT);
            }
        });        
        
        // ensure the end game button is not enabled by default
        myEndGameButton = endGame;
        myEndGameButton.setEnabled(false);
        
        // attach everything to the menu and add menu to the menubar
        file.add(newGame);
        file.add(endGame);
        file.addSeparator();
        file.add(quitGame);
        
        myMenuBar.add(file);
    }
    
    /**
     * Sets up the "Options" menu.
     */
    private void setupOptionsMenu() {
        // create menu and components
        final JMenu options = new JMenu("Options");
        
        final JMenu colors = new JMenu("Colors...");
        final JMenu sounds = new JMenu("Sounds...");
        
        // set up sub menus
        setupColorsSubMenu(colors);
        setupSoundsSubMenu(sounds);
        
        // attach everything and add menu to menubar
        options.add(colors);
        options.add(sounds);
        
        myMenuBar.add(options);
    }

    /**
     * Helper method for setting up the "Colors.." submenu.
     * @param theColorMenu The JMenu that corresponds to the "Colors..." submenu.
     */
    private void setupColorsSubMenu(final JMenu theColorMenu) {
        // button group so only one color can be picked at any given time
        final ButtonGroup colorSchemeGroup = new ButtonGroup();
        
        final JCheckBoxMenuItem standard = new JCheckBoxMenuItem("Standard Colors");
        final JCheckBoxMenuItem dark = new JCheckBoxMenuItem("Dark Colors");
        
        standard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent theEvent) {
                setChanged();
                notifyObservers(TetrisEvents.STANDARD_COLOR_CHANGE);
            }
        });
        dark.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent theEvent) {
                setChanged();
                notifyObservers(TetrisEvents.DARK_COLOR_CHANGE);
            }
        });
        
        final JCheckBoxMenuItem invert = new JCheckBoxMenuItem("Invert");

        invert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent theEvent) {
                setChanged();
                if (invert.isSelected()) {
                    notifyObservers(TetrisEvents.INVERT_COLOR_SCHEME_ON);
                } else {
                    notifyObservers(TetrisEvents.INVERT_COLOR_SCHEME_OFF);
                }
            }
        });
        
        // add color scheme buttons to button group
        colorSchemeGroup.add(standard);
        colorSchemeGroup.add(dark);
        colorSchemeGroup.clearSelection();
        standard.setSelected(true);
        
        theColorMenu.add(standard);
        theColorMenu.add(dark);
        theColorMenu.addSeparator();
        theColorMenu.add(invert);
    }    
    
    /**
     * Helper method to set up the "Sounds..." sub menu.
     * 
     * @param theSoundsMenu The JMenu corresponding to the "Sounds..." sub menu.
     */
    private void setupSoundsSubMenu(final JMenu theSoundsMenu) {
        final JMenu selectSong = new JMenu("Select Song...");
        final ButtonGroup songGroup = new ButtonGroup();
        final JCheckBoxMenuItem classicTetris = new JCheckBoxMenuItem("Classic Tetris Theme");
        final JCheckBoxMenuItem remixTetris = new JCheckBoxMenuItem("Tetris Remix Theme");
        final JCheckBoxMenuItem mute = new JCheckBoxMenuItem("Mute Sound");
        
        classicTetris.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent theEvent) {
                if (classicTetris.isSelected()) {
                    mySoundFile = CLASSIC_TETRIS_SONG;
                    playSound();    
                }
            }
        });
        remixTetris.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent theEvent) {
                if (remixTetris.isSelected()) {
                    mySoundFile = REMIX_TETRIS_SONG;
                    playSound();
                }
            }
        });
        
        
        mute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent theEvent) {
                if (myEndGameButton.isEnabled()) {
                    myMusicPlayer.togglePause();
                
                    if (mute.isSelected()) {
                        myMuteFlag = true;
                    } else {
                        myMuteFlag = false;
                    }
                } else {
                    mute.setSelected(false);
                }
            }
        });
        
        songGroup.add(classicTetris);
        songGroup.add(remixTetris);
        songGroup.clearSelection();
        
        selectSong.add(classicTetris);
        selectSong.add(remixTetris);
        
        theSoundsMenu.add(selectSong);
        theSoundsMenu.addSeparator();
        theSoundsMenu.add(mute);
    }

    /**
     * Loads the current sound file into the music player and then plays it if the sound is
     * not muted.
     */
    private void playSound() {
        myMusicPlayer.stopPlay();
        final File[] files = {new File(mySoundFile)};
        myMusicPlayer.newList(files);
        if (!myMuteFlag || !myEndGameButton.isEnabled()) {
            myMusicPlayer.togglePause();
        }
    }
    
    /**
     * Sets up the "Help" menu.
     */
    private void setupHelpMenu() {
        // create menu and components
        final JMenu help = new JMenu("Help");
        
        final JMenuItem scoringInfo = new JMenuItem("Scoring Info");
        final JMenuItem resourceCredit = new JMenuItem("External Resource Credit");
        final JMenuItem about = new JMenuItem("About");

        // add menu item actions
        scoringInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent theEvent) {
                
                final String info = "1 Line Cleared = 40 points\n2 Lines Cleared = 100 points"
                                + "\n3 Lines Cleared = 300 points\n4 Lines Cleared = 1200 "
                                + "points\n \nNo bonus score for placed pieces.";
                
                JOptionPane.showMessageDialog(scoringInfo, info, "Scoring Rules", 
                                              JOptionPane.INFORMATION_MESSAGE, SIDEBAR_ICON);
            }
        });
        
        resourceCredit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent theEvent) {
                showResourceCreditWindow(resourceCredit);
            }
        });
        
        about.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent theEvent) {
                final String info = "Assignment : Tetris"
                                + "\nCourse : TCSS 305 - Winter 2017"
                                + "\nAuthor : Tenma Rollins\n \n"
                                + "Main Icon (seen in the title bar and\n"
                                + "to the left here) were made by me.";
                
                JOptionPane.showMessageDialog(about, info, "About this program", 
                                              JOptionPane.INFORMATION_MESSAGE, DEFAULT_ICON);
            }
        });
        
        // attach everything and add to menu
        help.add(scoringInfo);
        help.add(resourceCredit);
        help.add(about);
        
        myMenuBar.add(help);
    }
  
    /**
     * Helper method for showing the resource credit window.
     * 
     * @param theComponent the parent component to determine where the dialog box should be 
     * displayed.
     */
    private void showResourceCreditWindow(final Component theComponent) {  
        final String info = "Resources Used:\n"
                        + "    [Audio]\n"
                        + "        Classic Theme : "
                        + "https://archive.org/details/TetrisThemeMusic\n"
                        + "        Remix Theme : "
                        + "https://archive.org/details/Tetris_570\n"
                        + "    [Images]\n"
                        + "        Scoring Information Side Icon : "
                        + "https://en.wikipedia.org/wiki/File:NES_Tetris_Box_Front.jpg\n";
        
        JOptionPane.showMessageDialog(theComponent, info, "Credit for External Resources Used",
                                      JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Creates a window to allow the user to chose a board width and height to start a new
     * game with.
     */
    private void setupNewGame() {
        final Box chooser = Box.createVerticalBox();
        final int padding = 10;
        
        final int majorSpacing = 5;
        final int minorSpacing = 1;
        final int gridSizeMinimum = 10;
        final int gridSizeMaximum = 30;
        // create slider for grid width
        final JSlider xSlider = new JSlider(gridSizeMinimum, gridSizeMaximum);
        xSlider.setMajorTickSpacing(majorSpacing);
        xSlider.setMinorTickSpacing(minorSpacing);
        xSlider.setValue(gridSizeMinimum);
        xSlider.setPaintTicks(true);
        xSlider.setPaintLabels(true);
        
        // create slider or grid height
        final JSlider ySlider = new JSlider(gridSizeMinimum, gridSizeMaximum);
        ySlider.setMinorTickSpacing(minorSpacing);
        ySlider.setMajorTickSpacing(majorSpacing);
        ySlider.setValue(gridSizeMinimum + gridSizeMinimum);
        ySlider.setPaintTicks(true);
        ySlider.setPaintLabels(true);
        
        // create informative labels
        final JLabel xSliderLabel = new JLabel("Board Width:");
        final JLabel ySliderLabel = new JLabel("Board Height:");
        
        // create horizontal layouts for LABEL: SLIDER
        final Box slider1 = Box.createHorizontalBox();
        slider1.add(xSliderLabel);
        slider1.add(xSlider);
        final Box slider2 = Box.createHorizontalBox();
        slider2.add(ySliderLabel);
        slider2.add(ySlider);
        
        // add horizontal slider panels in a vertical fashion
        chooser.add(slider1);
        chooser.add(Box.createVerticalStrut(padding));
        chooser.add(slider2);
        chooser.add(Box.createVerticalStrut(padding));
        // warns the user about large width and display limitations
        chooser.add(new JLabel("WARNING: Large width can potentially"));
        chooser.add(new JLabel("create window too large to be displayed"));
        chooser.add(new JLabel("on current screen."));
        
        // ensure that these fields always have some kind of value
        myXGridSize = xSlider.getValue();
        myYGridSize = ySlider.getValue();
        
        // listeners will change the above fields IF AND ONLY IF the slider is moved
        xSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent theEvent) {
                myXGridSize = xSlider.getValue();
            }
        });
        ySlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent theEvent) {
                myYGridSize = ySlider.getValue();
            }
        });
        
        final int result = JOptionPane.showConfirmDialog(null, chooser, "Choose Board Size:", 
                                                   JOptionPane.OK_CANCEL_OPTION); 
        
        // only begin a new game if the user says okay
        if (result == JOptionPane.OK_OPTION) {
            setChanged();
            notifyObservers(new Dimension(myXGridSize, myYGridSize));
            setChanged();
            notifyObservers(TetrisEvents.NEW_GAME);
        }
    }
}
