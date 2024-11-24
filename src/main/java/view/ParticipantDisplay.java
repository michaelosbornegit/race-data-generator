package view;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import model.ParticipantSpeed;

/**
 * This class is a GridPane that displays a single participant's information.
 *
 * @author Michael Osborne
 */
public class ParticipantDisplay extends GridPane {

    private static final Random myRandom = new Random();

    private static final List<String> USED_NAMES = new ArrayList<>();

    private static final List<Integer> USED_IDS = new ArrayList<>();

    /** The lower bound for random Participant IDs (inclusive). */
    private static final int DEFAULT_ID_LOWER = 1;

    /** The upper bound for random Participant IDs (inclusive). */
    private static final int DEFAULT_ID_UPPER = 99;

    /** The default names to choose from when generating racers. */
    private static final String[] DEFAULT_NAMES = { "Rebecka", "Lexie",
            "Betsey", "Elane", "Miss", "Daniele", "Angele", "Aja", "Floretta",
            "Patrice", "Jamison", "Myles", "Sally", "Viola", "Delicia", "Dwain",
            "Alfredia", "Mina", "Charlena", "Catheryn", "Bud", "Suellen",
            "Herbert", "Adelaida", "Carmine", "Sylvie", "Fawn", "Nathanial",
            "Gertrudis", "Marcellus", "Catrina", "Emelia", "Aliza", "Julieann",
            "Ronni", "Michael", "Bridgett", "Shira", "Evia", "Lorine",
            "Blanche", "Carita", "Perry", "Isela", "Yelena", "Florrie",
            "Angelica", "Kiyoko", "Kelli", "Stacie" };

    private TextField myRacerIDField;
    private TextField myNameTextField;
    private ComboBox<ParticipantSpeed> mySpeedComboBox;

    /**
     * Constructs a new ParticipantDisplay.
     */
    public ParticipantDisplay() {
        // TODO implement checks to make sure there aren't duplicates in default
        // names and id's (check somewhere else to see if the user entered
        // duplicate names or ID's)

        // Generate new unique name and ID
        String name = DEFAULT_NAMES[myRandom.nextInt(DEFAULT_NAMES.length)];
        int id = myRandom.nextInt(DEFAULT_ID_UPPER - 1) + DEFAULT_ID_LOWER;

        while (USED_NAMES.contains(name)) {
            name = DEFAULT_NAMES[myRandom.nextInt(DEFAULT_NAMES.length)];
        }
        while (USED_IDS.contains(id)) {
            id = myRandom.nextInt(DEFAULT_ID_UPPER - 1) + DEFAULT_ID_LOWER;
        }

        USED_NAMES.add(name);
        USED_IDS.add(id);

        // Instantiation
        myNameTextField = new TextField(name);
        myRacerIDField = new TextField(Integer.toString(id));
        ObservableList<ParticipantSpeed> options = FXCollections
                .observableArrayList();

        options.addAll(ParticipantSpeed.values());
        mySpeedComboBox = new ComboBox<ParticipantSpeed>(options);
        mySpeedComboBox.getSelectionModel()
                .select(myRandom.nextInt(options.size()));

        // Formatting
        myRacerIDField.setPrefColumnCount(2);
        myNameTextField.setPrefColumnCount(10);
        setMargin(myNameTextField, new Insets(0, 5, 0, 5));
        setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);

        // Add to the GridPane
        add(new Text("ID:"), 0, 0);
        add(myRacerIDField, 1, 0);
        add(new Text("Name:"), 2, 0);
        add(myNameTextField, 3, 0);
        add(mySpeedComboBox, 4, 0);
    }

    /**
     * Clears the name and ID lists.
     */
    public static void clearNamesAndIDS() {
        USED_NAMES.clear();
        USED_IDS.clear();
    }

    /**
     * Returns the participants name.
     *
     * @return The participants name.
     */
    public String getName() {
        return myNameTextField.textProperty().get();
    }

    /**
     * Returns the participants ID.
     *
     * @return The participants ID.
     */
    public int getID() {
        return Integer.parseInt(myRacerIDField.getText());
    }

    /**
     * Returns the ParticipantSpeed associated with the participant.
     *
     * @return The ParticipantSpeed associated with the participant.
     */
    public ParticipantSpeed getSpeed() {
        return mySpeedComboBox.getSelectionModel().getSelectedItem();
    }

}
