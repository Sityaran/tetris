/*
 * TCSS 305 Assignment 6 - Tetris
 */

package view;

import com.sun.media.codec.audio.mp3.JavaDecoder;

import javax.media.Codec;
import javax.media.PlugInManager;

/**
 * Driver for the TetrisGUI class.
 * 
 * @author Tenma Rollins
 * @version 23 Feb 2017
 *
 */
public final class TetrisMain {

    /**
     * Private constructor to prevent instantiation.
     */
    private TetrisMain() { }
    
    
    /**
     * Creates and begins an instance of the GUI for this tetris game. 
     * 
     * @param theArgs Default arguments for main method.
     */
    public static void main(final String[] theArgs) {
        // since I am using the media player class, this code is necessary
        final Codec c = new JavaDecoder();
        PlugInManager.addPlugIn("com.sun.media.codec.audio.mp3.JavaDecoder",
                                c.getSupportedInputFormats(),
                                c.getSupportedOutputFormats(null),
                                PlugInManager.CODEC);

        // start the gui
        final TetrisGUI gui = new TetrisGUI();
        gui.start();
    }

}
