/*
 * TCSS 305 Assignment 6 - Tetris
 */

package utility;

import java.awt.Color;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class for creating and manipulating color schemes to be used to draw Tetris pieces.
 * 
 * @author Tenma Rollins
 * @version 10 Mar 2017
 *
 */
public class Colors {
    /** The default color scheme. */
    private final Map<Character, Color> myColorScheme;
    
    /**
     * Initializes the default color scheme using standard tetris colors.
     */
    public Colors() {
        myColorScheme = new TreeMap<Character, Color>();
        
        myColorScheme.put('I', Color.CYAN);
        myColorScheme.put('J', Color.BLUE);
        myColorScheme.put('L', Color.ORANGE);
        myColorScheme.put('O', Color.YELLOW);
        myColorScheme.put('S', Color.GREEN);
        myColorScheme.put('T', Color.MAGENTA);
        myColorScheme.put('Z', Color.RED);
    }
    
    /**
     * Provides the default color scheme (standard tetris colors).
     * 
     * @return The color scheme as a Map<Character, Color>, each character corresponding to
     * the Tetris piece in the shape of that character.
     */
    public Map<Character, Color> getDefaultColorScheme() {
        return myColorScheme;
    }
    
    /**
     * Provides a color scheme which is one .darker() call darker than the default 
     * color scheme.
     * 
     * @return The darker color scheme as a Map<Character, Color>, each character corresponding
     * to the Tetris piece in the shape of that character. 
     */
    public Map<Character, Color> getDarkColorScheme() {
        final Map<Character, Color> darkColors = new TreeMap<Character, Color>();
        for (final char key : myColorScheme.keySet()) {
            final Color currentColor = myColorScheme.get(key);
            darkColors.put(key, currentColor.darker());
        }
        
        return darkColors;
    }

    /**
     * Inverts a given color scheme so that their RGB values reflect the original values
     * subtracted from 255 (for each value).
     * 
     * @param theColorScheme The original color scheme.
     * @return The inverted color scheme as a Map<Character, Color>, each character 
     * corresponding to the Tetris piece in the shape of that character.
     */
    public Map<Character, Color> invert(final Map<Character, Color> theColorScheme) {
        final Map<Character, Color> invertedScheme = new TreeMap<Character, Color>();
        for (final char key : theColorScheme.keySet()) {
            final Color currentColor = theColorScheme.get(key);
            final int redComp = 255 - currentColor.getRed();
            final int greenComp = 255 - currentColor.getGreen();
            final int blueComp = 255 - currentColor.getBlue();
            invertedScheme.put(key, new Color(redComp, greenComp, blueComp));
        }
        
        return invertedScheme;
    }
     
}
