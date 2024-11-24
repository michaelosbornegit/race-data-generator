package view.track;

import java.awt.Shape;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

/**
 *
 * @author Michael Osborne
 */
public class VisibleRaceTrack extends RoundRectangle2D.Double {

    /** The serialization ID. */
    private static final long serialVersionUID = 7813589334818900395L;

    /** The amount of points on the straight track sections. */
    private double myPointsOnStraight;

    /** The amount of points on the turns. */
    private double myPointsOnTurns;

    /** The curve representing the first turn. */
    private CubicCurve2D.Double myFirstTurn;

    /** The curve representing the second turn. */
    private CubicCurve2D.Double mySecondTurn;

    /** The curve representing the third turn. */
    private CubicCurve2D.Double myThirdTurn;

    /** The curve representing the fourth turn. */
    private CubicCurve2D.Double myFourthTurn;

    /** The line representing the top straight. */
    private Line2D.Double myTopStraight;

    /** The line representing the bottom straight. */
    private Line2D.Double myBottomStraight;

    /** The length of the track (number of subdivisions). */
    private final int myLength;

    /**
     * Creates a new RaceTrackRoundRect at the given (x, y) coordinate with the
     * given width and height and the given length. The length is how many
     * sections the track is divided into or how many points are on the track
     * for use with the getPointAtDistance method.
     *
     * @param theX      The X coordinate.
     * @param theY      The Y coordinate.
     * @param theWidth  The width.
     * @param theHeight The height.
     * @param theLength The length of the race track.
     */
    public VisibleRaceTrack(final int theX, final int theY, final int theWidth,
            final int theHeight, final int theLength) {
        // Using height as the Arc width and the Arc height makes a nice
        // conventional
        // race track shape as long as height > width.
        super(theX, theY, theWidth, theHeight, theHeight, theHeight);

        myLength = theLength;

        if (theWidth < theHeight) {
            throw new IllegalArgumentException(
                    "Racetracks width must be longer than height");
        }

        calculateCurvesAndStraights();
        calculateDivisions();
    }

    /**
     * Returns the track length.
     *
     * @return The length of the track.
     */
    public int getTrackLength() {
        return myLength;
    }

    /**
     * Calculates how many 'points' should fall on each section of track based
     * on the size of the track and myLength.
     */
    private void calculateDivisions() {
        final double allPoints = (getHeight() * Math.PI)
                + (getWidth() - getHeight()) * 2;
        myPointsOnTurns = (((getHeight() * Math.PI) / 4) / allPoints)
                * myLength;
        myPointsOnStraight = ((getWidth() - getHeight()) / allPoints)
                * myLength;
    }

    /**
     * Splits the track into four Curve2D turns and two Line2D straight sections
     * to use to traverse the track.
     */
    private void calculateCurvesAndStraights() {
        // A PathIterator to use to parameterize the curves.
        PathIterator pathIterator = this.getPathIterator(null);

        // Get starting point for the first path iterator segment
        double[] startingCoords = new double[6];
        pathIterator.currentSegment(startingCoords);
        Point2D.Double lastStartingPoint = new Point2D.Double(startingCoords[0],
                startingCoords[1]);
        for (int i = 0; i < startingCoords.length; i++) {
            System.out.print(startingCoords[i] + " ");
        }

        // Calculate and store the curves
        // Extra nexts are to skip over lines we don't need in the rounded
        // rectangle.
        pathIterator.next();
        pathIterator.next();
        mySecondTurn = (CubicCurve2D.Double) parseSegment(pathIterator,
                lastStartingPoint);
        pathIterator.next();
        myBottomStraight = (Line2D.Double) parseSegment(pathIterator,
                lastStartingPoint);
        pathIterator.next();
        myThirdTurn = (CubicCurve2D.Double) parseSegment(pathIterator,
                lastStartingPoint);
        pathIterator.next();
        pathIterator.next();
        myFourthTurn = (CubicCurve2D.Double) parseSegment(pathIterator,
                lastStartingPoint);
        pathIterator.next();
        myTopStraight = (Line2D.Double) parseSegment(pathIterator,
                lastStartingPoint);
        pathIterator.next();
        myFirstTurn = (CubicCurve2D.Double) parseSegment(pathIterator,
                lastStartingPoint);
    }

    /**
     * Parses the current segment the given PathIterator. Also mutates
     * theStartingPoint to be the ending point of the parsed segment. Currently
     * only parses PathIterator.SEG_CUBICTO and PathIterator.SEG_LINETO
     *
     * @param thePathIterator The path iterator currently iterating over the
     *                        shape
     * @return The Shape representing the current segment.
     */
    public Shape parseSegment(final PathIterator thePathIterator,
            final Point2D.Double theStartingPoint) {
        Shape returnShape = null;

        final int numberOfCoords = 6;
        double[] coordArray = new double[numberOfCoords];
        int segmentType = thePathIterator.currentSegment(coordArray);
        while (segmentType == 0) {
            theStartingPoint.setLocation(coordArray[0], coordArray[1]);
            thePathIterator.next();
            segmentType = thePathIterator.currentSegment(coordArray);
        }
        if (segmentType == PathIterator.SEG_LINETO) {
            returnShape = new Line2D.Double(theStartingPoint.getX(),
                    theStartingPoint.getY(), coordArray[0], coordArray[1]);

            theStartingPoint.setLocation(coordArray[0], coordArray[1]);
        } else if (segmentType == PathIterator.SEG_CUBICTO) {
            final int ctrlpt1Index = 0;
            final int ctrlpt2Index = 2;
            final int x2Index = 4;

            returnShape = new CubicCurve2D.Double(theStartingPoint.getX(),
                    theStartingPoint.getY(), coordArray[ctrlpt1Index],
                    coordArray[ctrlpt1Index + 1], coordArray[ctrlpt2Index],
                    coordArray[ctrlpt2Index + 1], coordArray[x2Index],
                    coordArray[x2Index + 1]);

            theStartingPoint.setLocation(coordArray[x2Index],
                    coordArray[x2Index + 1]);
        }

        return returnShape;
    }

    /**
     * Returns a Point2D.Double at the given distance on the race track. Note
     * that the 0 distance is at the upper left of the race track, before the
     * first left turn.
     *
     * @param theDistance The distance around the raceTrack to find
     * @return The Point2D.Double at the given distance around the race track.
     */
    public Point2D.Double getPointAtDistance(final double theDistance) {
        double correctedDistance = theDistance;
        while (correctedDistance < 0) {
            correctedDistance += myLength;
        }
        while (correctedDistance > myLength) {
            correctedDistance -= myLength;
        }
        System.out.println(correctedDistance);

        final double firstTurnDistance = myPointsOnTurns;
        final double secondTurnDistance = myPointsOnTurns * 2;
        final double firstStraightDistance = secondTurnDistance
                + myPointsOnStraight;
        final double thirdTurnDistance = myPointsOnTurns * 3
                + myPointsOnStraight;
        final double fourthTurnDistance = myPointsOnTurns * 4
                + myPointsOnStraight;
        final double secondStraightDistance = fourthTurnDistance
                + myPointsOnStraight;

        Shape specificShape = new Line2D.Double(0, 0, 0, 0);
        double t = 0.0;

        if (correctedDistance < firstTurnDistance) {
            // Lands on first turn
            t = correctedDistance / firstTurnDistance;
            specificShape = myFirstTurn;
        } else if (correctedDistance < secondTurnDistance) {
            // Lands on second turn
            t = (correctedDistance - firstTurnDistance) / myPointsOnTurns;
            specificShape = mySecondTurn;
        } else if (correctedDistance < firstStraightDistance) {
            // Lands on bottom straight
            t = (correctedDistance - secondTurnDistance) / myPointsOnStraight;
            specificShape = myBottomStraight;
        } else if (correctedDistance < thirdTurnDistance) {
            // Lands on third turn
            t = (correctedDistance - firstStraightDistance) / myPointsOnTurns;
            specificShape = myThirdTurn;
        } else if (correctedDistance < fourthTurnDistance) {
            // Lands on fourth turn
            t = (correctedDistance - thirdTurnDistance) / myPointsOnTurns;
            specificShape = myFourthTurn;
        } else if (correctedDistance < secondStraightDistance) {
            // Lands on top straight
            t = (correctedDistance - fourthTurnDistance) / myPointsOnStraight;
            specificShape = myTopStraight;
        }

        return parametricSolver(specificShape, t);
    }

    /**
     * Solves the given Shape (as long as its a Line2D or a CubicCurve2D) for
     * the point at the given parameter.
     *
     * @param theShape  The Shape to solve, must be a Line2D or a CubicCurve2D.
     * @param theTValue The parameter (between 0 and 1 inclusive)
     * @return The Point2D.Double on theShape at theTValue.
     */
    private Point2D.Double parametricSolver(final Shape theShape,
            final double theTValue) {
        final Point2D.Double returnPoint = new Point2D.Double(0, 0);

        if (theShape instanceof Line2D) {
            final Line2D.Double line = (Line2D.Double) theShape;

            // The parametric expression for a line.
            returnPoint.x = line.getX1()
                    + theTValue * (line.getX2() - line.getX1());
            returnPoint.y = line.getY1()
                    + theTValue * (line.getY2() - line.getY1());
        } else {
            final CubicCurve2D.Double curve = (CubicCurve2D.Double) theShape;

            final int cubed = 3;
            final int squared = 2;

            // The parametric expression for a cubic Bezier curve.
            returnPoint.x = Math.pow(1 - theTValue, cubed) * curve.x1
                    + cubed * theTValue * Math.pow(1 - theTValue, squared)
                            * curve.ctrlx1
                    + cubed * Math.pow(theTValue, squared) * (1 - theTValue)
                            * curve.ctrlx2
                    + Math.pow(theTValue, cubed) * curve.x2;
            returnPoint.y = Math.pow(1 - theTValue, cubed) * curve.y1
                    + cubed * theTValue * Math.pow(1 - theTValue, squared)
                            * curve.ctrly1
                    + cubed * Math.pow(theTValue, squared) * (1 - theTValue)
                            * curve.ctrly2
                    + Math.pow(theTValue, cubed) * curve.y2;
        }
        return returnPoint;
    }
}
