package race_constraints;

import model.ParticipantSpeed;

/**
 * A ParticipantConstraint changes the speed of a participant. A
 * ParticipantConstraint has one method, which is called with a participants
 * current speed and the participants speed bracket, and then determines a new
 * speed the participant should be travelling and returns it.
 *
 * @author Michael Osborne
 */
public interface ParticipantConstraint {

    /**
     * Applies this ParticipantConstraint's constraint on the given speed and
     * returns the new speed.
     *
     * @param speed      The current speed of the Participant (or the most
     *                   recent speed in the constraint application process).
     * @param theBracket The speed bracket of the Participant, useful for
     *                   constraints to know what speed the participant should
     *                   be travelling if no constraints were applied.
     * @return The new speed of the participant after applying the constraint.
     */
    double applyConstraint(double speed, ParticipantSpeed theBracket);
}
