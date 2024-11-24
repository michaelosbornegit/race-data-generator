package model.track;

import java.util.List;

/**
 * Defines the functionality of a Track to be used in a Race Generation program.
 *
 * @author Myles Haynes
 * @author Michael Osborne
 */
public abstract class Track {

    protected int trackLength;

    /**
     * Constructs a new track with the given length.
     *
     * @param theTrackLength The length of the track
     */
    public Track(final int theTrackLength) {
        trackLength = theTrackLength;
    }

    /**
     * Gets the track length.
     *
     * @return This track's length
     */
    public int getTrackLength() {
        return trackLength;
    }

    /**
     * Sets the sections of the track to the given TrackSpeeds.
     *
     * @param theSpeeds The TrackSpeeds to set the track to.
     */
    public abstract void setSections(List<TrackSpeed> theSpeeds);

    /**
     * Gets the width.
     *
     * @return This track's width
     */
    public abstract double getWidth();

    /**
     * Gets the height.
     *
     * @return This track's height
     */
    public abstract double getHeight();

    /**
     * Gets the yRatio of the track.
     *
     * @return This track's yRatio (height ratio)
     */
    public abstract int getHeightRatio();

    /**
     * Gets the xRatio of the track.
     *
     * @return This track's xRatio (width ratio)
     */
    public abstract int getWidthRatio();

    /**
     * Gets the track speed of the section of track at the given distance.
     *
     * @param distance The distance around the track to find the track speed at
     * @return The TrackSpeed of the track section at the given distance around
     *         the race track
     */
    public abstract TrackSpeed getTrackSpeed(double distance);

    /**
     * Gets the track speed of the section of track AFTER the section of track
     * at the given distance.
     *
     * @param distance The distance around the track to find the next track
     *                 speed at
     * @return The TrackSpeed of the track section AFTER the track section at
     *         the given distance around the race track
     */
    public abstract TrackSpeed getNextTrackSpeed(double distance);

    /**
     * Gets the distance until the next track section from the section
     * associated with the given distance.
     *
     * @param distance The distance to determine the current track section
     * @return The distance until the next track section
     */
    public abstract double getDistanceUntilNextTrackSection(double distance);

    /**
     * Gets the name of the track.
     *
     * @return This track's name
     */
    public abstract String getTrackName();

}
