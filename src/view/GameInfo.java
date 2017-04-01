/*
 * TCSS 305 Assignment 6 - Tetris
 */

package view;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Displays relevant information about the game. Currently only displays the controls. 
 * 
 * @author Tenma Rollins
 * @version 03 Mar 2017
 *
 */
public class GameInfo extends JPanel {   
    /** Generated serial version UID. */
    private static final long serialVersionUID = -5092590270910709159L;
    
    /**
     * Sets the size and sets up the layout for text that displays the controls. 
     * 
     * @param theSize The fixed size for this panel.
     */
    public GameInfo(final Dimension theSize) {
        super();
        
        setMaximumSize(theSize);
        setBackground(Color.WHITE);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        setup();
    }
    
    /**
     * Sets up the panel so that the JLabels displaying relevant text are aligned vertically.
     * Also spaces the labels based on the string[] "controls".
     */
    private void setup() {
        final String newLine = " "; // Adds one line of blank space
        final String[] controls = {"Rotate : Up Key", 
            "Move Left : Left Key", "Move Right : Right Key", "Move Down : Down Key", newLine, 
            "Drop : Space Key", newLine, "Pause : P"};
        
        for (final String line : controls) {
            final JLabel lineInfo = new JLabel();
            lineInfo.setText(line);
            lineInfo.setAlignmentX(JLabel.CENTER_ALIGNMENT); // centers it
            
            add(lineInfo); // this is in vertical box layout so it will add it below the last
        }
    }
}
