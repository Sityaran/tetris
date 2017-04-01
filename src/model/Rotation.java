/*
 * TCSS 305 - Project Tetris
 */

package model;

import java.util.Random;

/**
 * Enumeration of Rotation types.
 * 
 * @author TCSS 305 Instructors
 * @version Winter 2017
 */
public enum Rotation {

    /**
     * The original starting rotation.
     */
    START,

    /**
     * Quarter rotation or 90 degrees.
     */
    QUARTER,

    /**
     * Half rotation or 180 degrees.
     */
    HALF,

    /**
     * Three quarters rotation or 270 degrees.
     */
    THREEQUARTER;

    /**
     * A Random object used for generating random rotations.
     */
    private static final Random RANDOM = new Random();

    /**
     * Create a new Rotation from this one rotated clockwise.
     * 
     * @return new Rotation object that is rotated 90 degrees clockwise.
     */
    public Rotation clockwise() {
        return values()[(this.ordinal() + 1) % values().length];
    }

    /**
     * Creates a new Rotation with a random angle.
     * 
     * @return new random Rotation.
     */
    public static Rotation random() {
        return values()[RANDOM.nextInt(values().length)];
    }

}