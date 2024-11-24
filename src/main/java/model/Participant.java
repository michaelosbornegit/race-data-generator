package model;

import static java.lang.Math.ceil;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import race_constraints.ParticipantConstraint;

/**
 * This class represents a participant in a race.
 *
 * @author Myles Haynes
 * @author Peter Bae
 * @author Michael Osborne
 */
public class Participant implements Comparable<Participant> {
    public static final double DEFAULT_ACCELERATION = 0.0015;
    public static final double DEFAULT_DECELERATION = 0.002;

    private static Random rng = new Random();
    private int myID;
    private String myName;
    private double myPosition;
    private ParticipantSpeed mySpeedBracket;
    private double myVelocity;
    private double myNextVelocity;
    private int myLapNum;
    private int myTrackLength;
    private Map<String, ParticipantConstraint> myConstraints;

    /**
     * Constructs a new Participant.
     *
     * @param id            The participant id
     * @param name          The participant name
     * @param startDistance The starting position of the participant
     * @param trackLength   The length of the track the participant is racing on
     * @param speed         The ParticipantSpeed representing this participants
     *                      speed
     */
    public Participant(final int id, final String name,
            final double startDistance, final int trackLength,
            final ParticipantSpeed speed) {
        myID = id;
        myName = name;
        myPosition = startDistance;
        mySpeedBracket = speed;
        myVelocity = speed.getNewVelocity();
        myNextVelocity = speed.getNewVelocity();
        myTrackLength = trackLength;
        myConstraints = new HashMap<>();
    }

    /**
     * Steps the participant, applying all constraints and moving them forward
     * on the imaginary racing surface.
     *
     * @return The new position of the participant after updates.
     */
    public double step() {
        double velocity = myVelocity;
        for (String condition : myConstraints.keySet()) {
            velocity = myConstraints.get(condition).applyConstraint(velocity,
                    mySpeedBracket);
        }

        myPosition += velocity;

        if (myPosition >= myTrackLength) {
            myLapNum++;
            myPosition -= myTrackLength;
        }

        return myPosition;
    }

    /**
     * Gets this participant's ParticipantSpeed.
     *
     * @return This participant's ParticipantSpeed
     */
    public ParticipantSpeed getParticipantSpeed() {
        return mySpeedBracket;
    }

    /**
     * Adds a ParticipantConstraint to the participant.
     *
     * @param key The key associated with the given ParticipantConstraint
     * @param rc  The ParticipantConstraint to add to the participant
     */
    public void addConstraint(final String key,
            final ParticipantConstraint rc) {
        myConstraints.put(key, rc);
    }

    /**
     * Removes the constraint associated with the given key from the
     * participant.
     *
     * @param key The key of the ParticipantConstraint to remove
     */
    public void removeConstraint(final String key) {
        myConstraints.remove(key);
    }

    /**
     * Returns true if this participant has a constraint associated with the
     * given key.
     *
     * @param key The key of the constraint to check.
     * @return True if the participant has the constraint, false otherwise
     */
    public boolean hasConstraint(final String key) {
        return myConstraints.containsKey(key);
    }

    /**
     * Gets this participant's position.
     *
     * @return This participant's position
     */
    public double getPosition() {
        return myPosition;
    }

    /**
     * Gets this participant's lap number.
     *
     * @return This participant's lap number
     */
    public int getLapNum() {
        return myLapNum;
    }

    /**
     * Updates the velocity of this participant.
     *
     * @param theVelocity The new velocity
     */
    public void setVelocity(final double theVelocity) {
        this.myVelocity = theVelocity;
    }

    /**
     * Calculates the next velocity for this participant.
     */
    public void calculateNextVelocity() {
        // Whenever a participant crosses a gate we need to randomly select the
        // next
        // speed it is going to go.
        myNextVelocity = mySpeedBracket.getNewVelocity();
    }

    /**
     * Gets this participant's next velocity (constant, previously calculated
     * when the participant got to his/her current section of track).
     *
     * @return This participant's ParticipantSpeed
     */
    public double getNextVelocity() {
        return myNextVelocity;
    }

    /**
     * Returns the Racer ID of this participant.
     *
     * @return The Racer ID
     */
    public String getRacerId() {
        return Integer.toString(myID);
    }

    @Override
    public String toString() {
        return myID + " : " + myPosition;
    }

    @Override
    public int compareTo(final Participant o) {
        if (myLapNum != o.myLapNum) {
            return o.myLapNum - myLapNum;
        } else if (myPosition != o.myPosition) {
            return (int) ceil(o.myPosition - myPosition);
        } else {
            return 0;
        }
    }

    /**
     * Gets this participants name.
     *
     * @return This participants name
     */
    public String getName() {
        return myName;
    }

    /**
     * Gets the current velocity of this participant.
     *
     * @return The current velocity
     */
    public double getVelocity() {
        return myVelocity;
    }
}
