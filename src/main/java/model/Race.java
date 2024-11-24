package model;

import static java.lang.String.format;
import static java.util.Collections.sort;
import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import model.track.Track;
import model.track.TrackSpeed;
import race_constraints.AccelerationConstraint;
import race_constraints.TrackSectionConstraint;

/**
 * This class is the main model class used when generating a race. This class
 * steps through the expected race distance, evaluates constraints, and updates
 * participants to create a realistic race.
 *
 * @author Myles Haynes
 * @author Peter Bae
 * @author Michael Osborne
 */
public class Race {

    private static final Random myRand = new Random();
    private final Track track;
    private List<Participant> participants;
    private final int numLaps;
    private int time;
    private Random rng;
    private Map<Participant, Integer> lastMessageTime;
    private List<Participant> participantsNotFinished;
    private final int timeSlice;

    /**
     * Constructs a new race.
     *
     * @param theTrack             The track geometry we are using
     * @param theNumLaps           The number of laps of the race
     * @param theTelemetryInterval The interval to expect a telemetry message
     *                             from each participant (roughly)
     * @param theParticipants      The participants participating in the race.
     */
    public Race(final Track theTrack, final int theNumLaps,
            final int theTelemetryInterval,
            final List<Participant> theParticipants) {
        this.track = theTrack;
        numLaps = theNumLaps;
        this.participants = theParticipants;
        time = 0;
        timeSlice = theTelemetryInterval;
        lastMessageTime = new HashMap<>();
        participantsNotFinished = new ArrayList<>();
        participantsNotFinished.addAll(theParticipants);

        // fix for now so lastMessageTime starts populated
        for (int i = 0; i < theParticipants.size(); i++) {
            lastMessageTime.put(theParticipants.get(i), 0);
        }
        rng = new Random();
    }

    /**
     * Steps the race one unit of time forward, returning all messages that
     * occur in that time step.
     *
     * @return The messages that occurred in the time step.
     */
    public List<String> stepRace() {
        List<String> messages = new ArrayList<>();
        // construct the header messages and add them
        if (time == 0) {
            messages.addAll(setUpMessages());
        }

        for (Participant participant : participants) {
            // Evaluate constraints
            double lastDistance = participant.getPosition();
            evaluateConstraints(participant);

            participant.step();

            // Check if we passed a gate
            if (track.getDistanceUntilNextTrackSection(
                    participant.getPosition()) > track
                            .getDistanceUntilNextTrackSection(lastDistance)) {
                // Remove acceleration constraint
                participant.removeConstraint("Acceleration");

                // Set the velocity accordingly
                participant.setVelocity(participant.getNextVelocity());

                // calculate next velocity
                participant.calculateNextVelocity();
            }

            // Add some granularity so telemetry doesn't all come in on the same
            // timestamp for all racers.
            if (myRand.nextInt(timeSlice) + 1 == 1 || time == 0) {
                messages.add(format("$T:%d:%s:%.2f:%d", time,
                        participant.getRacerId(), participant.getPosition(),
                        participant.getLapNum()));
            }
            lastMessageTime.compute(participant, (_r, i) -> i + 1);
        }
        newLeaderBoard().ifPresent(messages::add);
        messages.addAll(crossingMessages());
        time++;
        return messages;
    }

    /**
     * Using the current race state, evaluateConstraints checks to see if any
     * constraints need to be added or removed from the given participant.
     *
     * @param participant The participant to evaluate.
     */
    private void evaluateConstraints(final Participant participant) {
        final double participantDistance = participant.getPosition();
        // Add the appropriate track constraint
        participant.addConstraint("track", new TrackSectionConstraint(
                track.getTrackSpeed(participant.getPosition())));

        // Only add acceleration constraints once (because of the way we're
        // calculating acceleration using the distance)
        if (!participant.hasConstraint("Acceleration")) {
            // Determine if Acceleration/Deceleration is necessary
            // roughly the speed we have to be at the next gate
            // - roughly the speed we're going now
            double speedDifference = track
                    .getNextTrackSpeed(participantDistance).getMultiplier()
                    * participant.getNextVelocity()
                    - track.getTrackSpeed(participantDistance).getMultiplier()
                            * participant.getVelocity();

            if (speedDifference > 0) {
                // Need to speed up
                // Decide where to add acceleration constraint using distance
                // formula
                if (track.getDistanceUntilNextTrackSection(
                        participantDistance) <= calculateDistanceForAcceleration(
                                track.getTrackSpeed(participantDistance)
                                        .getMultiplier()
                                        * participant.getVelocity(),
                                track.getNextTrackSpeed(participantDistance)
                                        .getMultiplier()
                                        * participant.getNextVelocity(),
                                Participant.DEFAULT_ACCELERATION)) {

                    participant.addConstraint("Acceleration",
                            new AccelerationConstraint(
                                    Participant.DEFAULT_ACCELERATION,
                                    track.getTrackSpeed(participantDistance)
                                            .getMultiplier()
                                            * participant.getVelocity()));
                }
            } else if (speedDifference < 0) {
                // Need to slow down
                // Decide where to add acceleration constraint using distance
                // formula
                if (track.getDistanceUntilNextTrackSection(
                        participantDistance) <= calculateDistanceForAcceleration(
                                track.getNextTrackSpeed(participantDistance)
                                        .getMultiplier()
                                        * participant.getNextVelocity(),
                                track.getTrackSpeed(participantDistance)
                                        .getMultiplier()
                                        * participant.getVelocity(),
                                Participant.DEFAULT_DECELERATION)) {

                    participant.addConstraint("Acceleration",
                            new AccelerationConstraint(
                                    -Participant.DEFAULT_DECELERATION,
                                    track.getTrackSpeed(participantDistance)
                                            .getMultiplier()
                                            * participant.getVelocity()));
                }
            } else if (participantDistance < 0) {
                participant.addConstraint("Acceleration",
                        new AccelerationConstraint(
                                Participant.DEFAULT_ACCELERATION,
                                TrackSpeed.SLOW.getMultiplier()
                                        * participant.getVelocity()));
            }
        }

        if (participantDistance < 0) {
            participant.addConstraint("track",
                    new TrackSectionConstraint(TrackSpeed.SLOW));
        }
    }

    /**
     * This method calculates the distance needed to reach a target velocity
     * given a constant acceleration and returns it.
     *
     * @param initialVelocity The initial velocity
     * @param finalVelocity   The target or final velocity
     * @param acceleration    The constant acceleration
     * @return The distance needed to reach finalVelocity from initialVelocity
     *         at an acceleration of acceleration
     */
    private double calculateDistanceForAcceleration(
            final double initialVelocity, final double finalVelocity,
            final double acceleration) {
        // t = (vf - vi) / a
        double t = ((finalVelocity - initialVelocity) / acceleration);

        // s = vi*t + (1/2)*a*t^2
        final double s = initialVelocity * t
                + 0.5 * acceleration * Math.pow(t, 2);
        return s;
    }

    /**
     * Constructs the participant list and the first leaderboard message and
     * returns it.
     *
     * @return The participant list and the first leaderboard Strings
     */
    private List<String> setUpMessages() {
        List<String> returnList = participants.stream().map(r -> "#"
                + r.getRacerId() + ":" + r.getName() + ":" + r.getPosition())
                .collect(Collectors.toList());
        returnList.add(
                "$L:0:" + participants.stream().map(Participant::getRacerId)
                        .map(Object::toString).collect(joining(":")));
        return returnList;

    }

    /**
     * Checks to see if the race is still going to see if the race is over.
     *
     * @return true if the race is still going, false otherwise
     */
    public boolean stillGoing() {
        for (int i = 0; i < participants.size(); i++) {
            if (participants.get(i).getLapNum() < numLaps) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to see if a new leaderboard message is necessary.
     *
     * @return An Optional String, possibly containing the new leaderboard.
     */
    private Optional<String> newLeaderBoard() {
        List<Participant> prevPlaces = new ArrayList<>(participants);
        sort(participants);
        if (prevPlaces.equals(participants)) {
            return Optional.empty();
        } else {
            String leaderBoard = "$L:" + time + ":";
            leaderBoard += participants.stream().map(Participant::getRacerId)
                    .map(Object::toString).collect(joining(":"));
            return Optional.of(leaderBoard);
        }
    }

    /**
     * Checks to see if racers crossed the start/finish line, starting a new
     * lap. Also checks to see if racers are finished with their race, to keep
     * track of who has finished and who hasn't.
     *
     * @return The crossing messages to add (can be empty if no one crossed the
     *         start/finish line)
     */
    private List<String> crossingMessages() {
        List<Participant> participantstoRemove = new ArrayList<>();
        List<String> messages = new ArrayList<>();
        for (Participant p : participantsNotFinished) {
            if (p.getLapNum() == numLaps) {
                int crossTime = time;
                messages.add(format("$C:%d:%s:%d:%b", crossTime, p.getRacerId(),
                        p.getLapNum(), p.getLapNum() == numLaps));
                participantstoRemove.add(p);
            }
        }
        participantsNotFinished.removeAll(participantstoRemove);
        return messages;

    }
}
