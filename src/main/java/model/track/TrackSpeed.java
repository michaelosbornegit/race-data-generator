package model.track;

/**
 * This class enumerates the possible speeds of a track section in the race data
 * generation program.
 *
 * @author Myles Hanes
 * @author Michael Osborne
 * @author Peter Bae
 */
public enum TrackSpeed {
    FAST(1.5, "Fast"),
    MEDIUM(1.0, "Medium"),
    SLOW(0.5, "Slow");

    private double multiplier;
    private String myName;

    /**
     * Constructs a new TrackSpeed.
     *
     * @param theMultiplier The multiplier associated with this speed
     * @param theName       The simple name of this speed
     */
    TrackSpeed(final double theMultiplier, final String theName) {
        multiplier = theMultiplier;
        myName = theName;
    }

    /**
     * Updates the multiplier constant associated with this track speed.
     *
     * @param mult The new multiplier constant
     */
    public void setMultiplier(final double mult) {
        multiplier = mult;
    }

    /**
     * Gets the multiplier constant of this track speed.
     *
     * @return The multiplier constant of this track speed
     */
    public double getMultiplier() {
        return multiplier;
    }

    @Override
    public String toString() {
        return myName;
    }
}
