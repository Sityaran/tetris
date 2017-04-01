/*
 * TCSS 305
 */

package utility; // changed the package to match my current package setup

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.media.Player;

/**
 * MusicPlayer is used to play MP3 and WAV music files.
 * 
 * @author Anon
 * @version 1.2
 */

public class MusicPlayer {

    /** The player. */
    private Player myPlayer;

    /** The file being played. */
    private File myFile;

    /** The playlist. */
    private List<File> myPlayList;

    /** The current index within the playlist. */
    private int myIndex;

    /** Indicates if the music is paused or not. */
    private boolean myPaused;

    /** !!NEW ADDITION!! Boolean flag to determine whether or not to loop. */
    private boolean myShouldLoopFlag; // added to create case for looping functionality

    /**
     * The newList method creates a new playlist using the passed array of files.
     * 
     * @param theFiles The array of files to add to the playlist.
     */
    public void newList(final File[] theFiles) {
        myPlayList = new ArrayList<File>();

        for (int i = 0; i < theFiles.length; i++) {
            myPlayList.add(theFiles[i]);
        }

        myFile = myPlayList.get(0);
        myIndex = 0;
        myPaused = false;
        getSong();
    }

    /**
     * The changed method stops the current song and adds the passed value to
     * the index. The file at the new index is then played
     * 
     * @param theChange The change to the index
     */
    public void change(final int theChange) {
        if (!isStarted()) {
            final int newIndex = myIndex + theChange;

            if (newIndex >= 0 && newIndex <= myPlayList.size() - 1) {
                myIndex = newIndex;
                myFile = myPlayList.get(myIndex);
                myPlayer.stop();
                getSong();
            }
        }
    }

    /**
     * Used to verify if the player has a playlist loaded.
     * 
     * @return true if the player has a playlist.
     */
    public boolean hasList() {
        return myPlayList != null;
    }

    /**
     * Used to check if a song is started.
     * 
     * @return true if there is a song playing.
     */
    public boolean isStarted() {
        return myPlayer != null && myPlayer.getState() == Player.Started;
    }

    /**
     * Stops the current song if applicable.
     */
    public void stopPlay() {
        if (myPlayer != null) {
            myPlayer.stop();
            myPlayer.close();
        }
    }

    /**
     * Restarts a paused song.
     */
    public void play() {
        if (myPlayer != null) {
            myPlayer.start();
        }
    }

    /**
     * Pauses or unpauses the current song as applicable.
     */
    public void togglePause() {
        if (myPlayer != null) {
            if (myPaused) {
                myPaused = false;
                myPlayer.start();
            } else {
                myPaused = true;
                myPlayer.stop();
            }
        }
    }
    
    /**
     * !!NEW ADDITION!! Sets the myShouldLoopFlag for determining whether to loop the current 
     * song.
     * 
     * @param theFlag true for loop, false otherwise.
     */
    public void setShouldLoop(final boolean theFlag) { 
        // addded to create looping functionality
        myShouldLoopFlag = theFlag;
    }

    // Private Methods

    /**
     * !!EDITED!! added a case to loop the current song being played
     * 
     * Loads the song in the current file.
     */
    private void getSong() {
        try {
            if (myPlayer != null) {
                myPlayer.stop();
                myPlayer.close();
            }
            final MediaLocator mediaLocator = new MediaLocator(myFile.toURI().toURL());
            myPlayer = Manager.createPlayer(mediaLocator);
            myPlayer.addControllerListener(new ControllerListener() {
                public void controllerUpdate(final ControllerEvent theEvent) {
                    if (theEvent instanceof EndOfMediaEvent) {
                        if (myShouldLoopFlag) { // Added to create looping functionality
                            change(0);
                        } else { 
                            change(1);
                        }
                    }
                }
            });
            myPlayer.prefetch();
            myPlayer.realize();
            myPlayer.start();
        } catch (final MalformedURLException e) {
            e.printStackTrace();
        } catch (final NoPlayerException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
