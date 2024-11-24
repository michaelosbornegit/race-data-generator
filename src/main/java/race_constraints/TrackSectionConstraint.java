package race_constraints;

import model.ParticipantSpeed;
import model.track.TrackSpeed;

/**
 * A TrackSectionConstraint modifies the participants speed based on the section
 * of the track the participant is on.
 *
 * @author Michael Osborne
 * @author Peter Bae
 */
public class TrackSectionConstraint implements ParticipantConstraint {

    private TrackSpeed mySpeed;

    /**
     * Builds a new TrackSectionConstraint with the given TrackSpeed.
     *
     * @param speed The TrackSpeed to use with this constraint.
     */
    public TrackSectionConstraint(final TrackSpeed speed) {
        mySpeed = speed;
    }

    /**
     * Gets the TrackSpeed associated with this TrackSectionConstraint.
     *
     * @return The TrackSpeed associated with this TrackSectionConstraint
     */
    public TrackSpeed getTracketSpeed() {
        return mySpeed;
    }

    /**
     * Applies the TrackSectionConstraint (just multiply the given velocity with
     * this TrackSpeed's multiplier).
     */
    @Override
    public double applyConstraint(final double velocity,
            final ParticipantSpeed theSpeed) {
        return velocity * mySpeed.getMultiplier();
    }
}
