package model.track;

import static java.lang.Math.PI;

import java.util.List;

/**
 * OvalTrack is a track that supports shaping into an oval.
 *
 * @author Myles Haynes
 * @author Michael Osborne
 * @author Peter Bae
 */
public class OvalTrack extends Track {

    private int xRatio;
    private int yRatio;
    private double width;
    private double height;

    // Hard coded speeds for the sections of the track traversing the track
    // counterclockwise starting with first turn, second turn, back straight
    // away, etc...
    private TrackSpeed[] trackSpeeds = new TrackSpeed[6];

    private double frontStraightAway;
    private double firstTurn;
    private double secondTurn;
    private double backStraightAway;
    private double thirdTurn;
    private double fourthTurn;

    /**
     * Constructs a new OvalTrack.
     *
     * @param distance  The distance of the OvalTrack
     * @param theXRatio The xRatio of the shape of the OvalTrack
     * @param theYRatio The yRation of the shape of the OvalTrack
     */
    public OvalTrack(final int distance, final int theXRatio,
            final int theYRatio) {
        super(distance);
        if (theYRatio > theXRatio) {
            throw new IllegalArgumentException(
                    "width must be greater than height");
        }
        xRatio = theXRatio;
        yRatio = theYRatio;
        buildTrackModel();
    }

    @Override
    public TrackSpeed getTrackSpeed(final double d) {
        return trackSpeeds[(int) getSpeedIndexAndNextGate(d)[0]];
    }

    @Override
    public TrackSpeed getNextTrackSpeed(final double distance) {
        // Get the speed then add 1 to it to get the next track speed in the
        // array.
        int desiredIndex = (int) getSpeedIndexAndNextGate(distance)[0] + 1;

        // Make sure the array wraps
        desiredIndex = desiredIndex % trackSpeeds.length;

        return trackSpeeds[desiredIndex];
    }

    /**
     * Returns a double array with values: { speedIndex, nextGate }
     * corresponding to the given distance around the track.
     *
     * @param distance The distance around the track
     * @return A double array with values { speedIndex, nextGate }
     */
    private double[] getSpeedIndexAndNextGate(final double distance) {
        final double[] returnVals = new double[2];
        if (distance >= 0 && distance < firstTurn) {
            returnVals[0] = 0;
            returnVals[1] = firstTurn;
        } else if (distance < secondTurn) {
            returnVals[0] = 1;
            returnVals[1] = secondTurn;
        } else if (distance < backStraightAway) {
            returnVals[0] = 2;
            returnVals[1] = backStraightAway;
        } else if (distance < thirdTurn) {
            returnVals[0] = 3;
            returnVals[1] = thirdTurn;
        } else if (distance < fourthTurn) {
            returnVals[0] = 4;
            returnVals[1] = fourthTurn;
        } else if (distance < frontStraightAway) {
            returnVals[0] = 5;
            returnVals[1] = frontStraightAway;
        } else {
            // Should never happen
            returnVals[0] = 0;
            returnVals[1] = firstTurn;
        }

        return returnVals;
    }

    @Override
    public double getDistanceUntilNextTrackSection(final double distance) {
        return getSpeedIndexAndNextGate(distance)[1] - distance;
    }

    /**
     * Determines the distance on each section of the track based on the total
     * track length.
     */
    private void buildTrackModel() {
        width = (xRatio * trackLength)
                / ((yRatio * Math.PI) + (2 * xRatio) - (2 * yRatio));
        height = (yRatio * trackLength)
                / ((yRatio * Math.PI) + (2 * xRatio) - (2 * yRatio));

        double straightDist = getWidth() - getHeight();
        double curveDistance = (getHeight() * .5) * PI;
        firstTurn = (curveDistance / 2);
        secondTurn = curveDistance;
        backStraightAway = curveDistance + straightDist;
        thirdTurn = backStraightAway + (curveDistance / 2);
        fourthTurn = backStraightAway + curveDistance;
        frontStraightAway = backStraightAway * 2;
    }

    @Override
    public String getTrackName() {
        return "OvalTrack";
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public int getHeightRatio() {
        return yRatio;
    }

    @Override
    public int getWidthRatio() {
        return xRatio;
    }

    @Override
    public void setSections(final List<TrackSpeed> theSpeeds) {
        Object[] tempArr = theSpeeds.toArray();
        for (int i = 0; i < tempArr.length; i++) {
            trackSpeeds[i] = (TrackSpeed) tempArr[i];
        }
    }

}
