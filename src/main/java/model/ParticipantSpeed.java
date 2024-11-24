package model;

/**
 * ParticipantSpeed is an enumeration of speeds of participants.
 *
 * @author Michael Osborne
 * @author Peter Bae
 */
public enum ParticipantSpeed {
    FAST("Fast", 10.75, 1),
    MEDIUM("Medium", 10, 1),
    SLOW("Slow", 9.25, 1);

    private double myVelocity;
    private double myRange;
    private String myName;

    /**
     * Constructs a new ParticipantSpeed.
     *
     * @param theName     The name of the speed bracket.
     * @param theVelocity The base velocity for this speed bracket.
     * @param theRange    The variability range of the velocity
     */
    ParticipantSpeed(final String theName, final double theVelocity,
            final double theRange) {
        myVelocity = theVelocity;
        myRange = theRange;
        myName = theName;
    }

    /**
     * Updates the base velocity of this ParticipantSpeed.
     *
     * @param velocity The new velocity
     */
    public void setVelocity(final double velocity) {
        myVelocity = velocity;
    }

    /**
     * Updates the range or variability of this ParticipantSpeed.
     *
     * @param range The new range
     */
    public void setRange(final double range) {
        myRange = range;
    }

    /**
     * Gets the base velocity.
     *
     * @return The base velocity
     */
    public double getVelocity() {
        return myVelocity;
    }

    /**
     * Gets the range or variability.
     *
     * @return The range
     */
    public double getRange() {
        return myRange;
    }

    /**
     * Calculates a new velocity based on the base velocity and the range.
     *
     * @return The new random velocity
     */
    public double getNewVelocity() {
        // get some random value between myVelocity - myrange and my velocity +
        // myrange
        return Math.random() * myRange * 2 + (myVelocity - myRange);
    }

    /**
     * Returns the pretty name of the speed.
     */
    @Override
    public String toString() {
        return myName;
    }
}
