package view;

import java.util.function.BiConsumer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import model.ParticipantSpeed;
import view.util.ToolTips;
/**
 * This class displays participant speed information, and calls the given
 * consumer when it's fields are updated.
 *
 * @author Michael Osborne
 */
public class SpeedConfigDisplay extends GridPane {

    private BiConsumer<Double, Double> myBiConsumer;
    private TextField velocityField;
    private TextField rangeField;
    private Text speedText;
    private Text rangeText;

    /**
     * Constructs a new SpeedConfigDisplay.
     *
     * @param participantSpeed The associated participantSpeed
     * @param theConsumer The consumer that is called when fields are changed
     * in SpeedConfigDisplay.
     */
    public SpeedConfigDisplay(final ParticipantSpeed participantSpeed,
            final BiConsumer<Double, Double> theConsumer) {
        super();
        myBiConsumer = theConsumer;

        // Instantiation
        velocityField = new TextField(
                Double.toString(participantSpeed.getVelocity()));
        rangeField = new TextField(
                Double.toString(participantSpeed.getRange()));
        speedText = new Text(participantSpeed.toString());
        Text bracketText = new Text(
                participantSpeed.toString() + " Participant Settings");

        // Formatting
        velocityField.setPrefColumnCount(4);
        rangeField.setPrefColumnCount(4);
        setHalignment(bracketText, HPos.CENTER);
        bracketText.setFont(new Font(15));
        setPadding(new Insets(5));
        setHgap(3);
        velocityField.setTooltip(ToolTips.createTooltip(
                "The base speed of the participants in this speed bracket class"
                + " in track length units per millisecond"));
        rangeField.setTooltip(ToolTips.createTooltip(
                "The variation allowed in the velocity, participants randomly "
                + "select a speed for each track section within\n"
                        + "Speed - Variability and Speed + Variability\n"
                        + "This basically controls how volatile a participant "
                        + "is, higher variability means the participant can "
                        + "get really lucky and win by a\n"
                        + "mile or get very unlucky and lose to everyone or "
                        + "everything could even out and they finish about "
                        + "where they should given their base speed\n"
                        + "NOTE: Speed must be greater than variability "
                        + "(no moving backwards!)"));

        // Add Listeners
        FieldChangeListener fcl = new FieldChangeListener();
        velocityField.textProperty().addListener(fcl);
        rangeField.textProperty().addListener(fcl);
        velocityField.setPrefColumnCount(5);
        rangeField.setPrefColumnCount(5);

        // Add to UI
        add(bracketText, 0, 0, 4, 1);
        add(new Text("Speed:"), 0, 1);
        add(velocityField, 1, 1);
        add(new Text("Variability(+/-):"), 2, 1);
        add(rangeField, 3, 1);
    }

    /**
     * A ChangeListener to listen for and validate changes in the fields.
     */
    class FieldChangeListener implements ChangeListener<String> {

        @Override
        public void changed(final ObservableValue<? extends String> obsVal,
                final String oldString, final String newString) {
            // We don't care about the new and old values, we know we want to
            // try and send
            // the updated velocity and range regardless
            if (validDouble(velocityField.textProperty().get())
                    && validDouble(rangeField.textProperty().get())) {
                myBiConsumer.accept(
                        Double.parseDouble(velocityField.textProperty().get()),
                        Double.parseDouble(rangeField.textProperty().get()));
            }
        }

        /**
         * Checks a double for validity.
         *
         * @param theString The String to validate.
         * @return The parsed double, if able to be validated.
         */
        private boolean validDouble(final String theString) {
            try {
                Double.parseDouble(theString);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

    }
}
