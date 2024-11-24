/**
* The classes in this package contain utilities to help with various tasks in
* the view.
*
* @author Myles Haynes
* @author Peter Bae
* @author Michael Osborne
*/
package view.util;

import javafx.scene.control.Tooltip;
import javafx.util.Duration;

/**
 * This class holds the text for the apps informational tooltips as well as
 * methods to aid with tooltip creation.
 *
 * @author Michael Osborne
 */
public final class ToolTips {

    /**
     * Information about the Telemetry Interval Slider.
     */
    public static final String TELEMETRY_INTERVAL_SLIDER =
            "Used to change the amount of time it takes for a "
            + "telemetry message to be generated for each racer\n"
            + "Greatly effects race file size and lag\n"
            + "Lower number = smoother but more lag when viewing\n"
            + "Higher number = possibly less smooth but less lag";

    /**
     * Information about the Num Laps field.
     */
    public static final String NUM_LAPS = "The number of laps to race for\n"
            + "NOTE: The race only ends when the last participant finishes "
            + "their last lap!";

    /**
     * Information about the Num Racers field.
     */
    public static final String NUM_RACERS =
            "The number of participants in the race\n"
            + "NOTE: Max value is 30 racers";

    /**
     * Information about the Race Name field.
     */
    public static final String RACE_NAME =
            "The name of the race, does not effect anything in the race other "
            + "than the name in the file";

    /**
     * Information about the Track Length field.
     */
    public static final String TRACK_LENGTH =
            "The distance of one lap of the race in arbitrary units\n"
            + "This number is kind of like centimeters or inches in the real "
            + "world because racers travel their speed around this distance "
            + "every millisecond\n"
            + "Smaller number = racers appear to move a lot faster on the "
            + "screen\n"
            + "Larger number = racers move slower and race is longer";

    /**
     * Information about the X Ratio field.
     */
    public static final String X_RATIO =
            "The x Ratio of the track (how stretched it is horizontally)\n"
            + "Changing this is the easiest way to change the shape of the "
            + "track\n"
            + "Larger number (for fixed yRatio) = track is stretched more left "
            + "and right with smaller turns\n"
            + "Smaller number = more rounded track with larger turns\n"
            + "NOTE: xRatio should be greater than or equal to yRatio";

    /**
     * Information about the Y Ratio field.
     */
    public static final String Y_RATIO =
            "The y Ratio of the track (how big vertically the track is)\n"
            + "The best way to change the track shape is to modify the xRatio, "
            + "but modifying the yRatio can result in fun new track shapes\n"
            + "Larger number (for fixed xRatio) = taller track\n"
            + "NOTE: xRatio should be greater than or equal to yRatio";

    /**
     * Information about the Track Section fields.
     */
    public static final String TRACK_SECTION =
            "The speed of this section of track\n"
            + "This determines the speed of participants on certain sections "
            + "of the track relative to other sections\n"
            + "NOTE: These are in the order that participants traverse them,\n"
            + "they start right before the first turn, then travel to the "
            + "second turn, then the bottom straight etc.";

    /**
     * Information about the Track Speed fields.
     */
    public static final String TRACK_SPEED =
            "The multiplier for this track section speed\n"
            + "These correspond with the sections of track, and is different "
            + "than participant speed classes\n"
            + "The process is: A participant rolls a speed within the given "
            + "range of base speed +/- variability, then\n"
            + "that speed is multiplied by this constant based on the section "
            + "of track they are on\n"
            + "This is how participants all go faster on the straights and "
            + "slow down on the corners with the default values\n"
            + "(but you can make some really strange races)\n"
            + "NOTE: Extreme differences between these multipliers will cause "
            + "participants to instantaneously speed up or slow down,\n"
            + "this is because the acceleration algorithm assumes there is "
            + "enough space to accelerate up to speed or decelerate (and high "
            + "differences\n"
            + "in multipliers can make it so that is impossible so it doesn't "
            + "even try to accelerate/decelerate)";

    /**
     * The duration the tooltip stays on screen, in seconds.
     */
    private static final double TOOLTIP_DURATION = 300.0;

    /**
     * The duration before the tooltip shows, in seconds.
     */
    private static final double TOOLTIP_DELAY = 0.5;

    /**
     * Utility classes should not be able to be instantiated.
     */
    private ToolTips() {

    }

    /**
     * Creates and configures a tooltip.
     *
     * @param toolTipText The text for the tooltip.
     * @return The configured tooltip.
     */
    public static Tooltip createTooltip(final String toolTipText) {
        Tooltip tip = new Tooltip(toolTipText);

        // Make the tooltip show up instantly and stay on screen for a
        // long time.
        tip.setShowDelay(Duration.seconds(TOOLTIP_DELAY));
        tip.setShowDuration(Duration.seconds(TOOLTIP_DURATION));

        return tip;
    }
}
