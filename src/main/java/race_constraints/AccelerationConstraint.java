package race_constraints;

import model.ParticipantSpeed;

/**
 * An AccelerationConstraint is a ParticipantConstraint that simulates
 * acceleration. When applied to a Participant the participant will accelerate
 * or decelerate at the given acceleration until the constraint is removed.
 *
 * @author Michael Osborne
 * @author Peter Bae
 */
public class AccelerationConstraint implements ParticipantConstraint {

    private double myAcceleration;
    private double myCompoundingVelocity;

    /**
     * Constructs a new AccelerationConstraint.
     *
     * @param theAcceleration     The acceleration to apply to the velocity
     * @param theStartingVelocity The starting velocity of the participant for
     *                            compounding velocity calculations.
     */
    public AccelerationConstraint(final double theAcceleration,
            final double theStartingVelocity) {
        myAcceleration = theAcceleration;
        myCompoundingVelocity = theStartingVelocity;
    }

    /**
     * Applies the acceleration constraint.
     */
    @Override
    public double applyConstraint(final double velocity,
            final ParticipantSpeed theSpeed) {
        myCompoundingVelocity += myAcceleration;
        double ratio = myCompoundingVelocity / velocity;
        return velocity * ratio;
    }

    /**
     * Returns the acceleration of this constraint.
     *
     * @return The acceleration of this constraint
     */
    public double getAcceleration() {
        return myAcceleration;
    }

}
