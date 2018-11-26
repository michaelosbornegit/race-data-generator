package view;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import model.Participant;
import model.ParticipantSpeed;
import model.Race;
import model.track.Track;
import model.track.TrackSpeed;
import view.track.OvalController;
import view.util.DoubleListener;
import view.util.IntListener;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Myles Haynes
 */
public class Controller {
	private static final int DEFAULT_LAP_TIME = 60;
	private static final int DEFAULT_RACERS = 10;
	private static final int DEFAULT_NUM_LAPS = 1;

	private static Random rand = new Random();

	@FXML
	private TextField raceName;

	@FXML
	private Slider telemetryIntervalSlider;

	@FXML
	private Slider trackSpeedSlider;

	@FXML
	private Text fileDisplay;

	@FXML
	private Button outputFileButton;

	@FXML
	private TextField numLapsField;

	@FXML
	private Button submitRace;

	@FXML
	private BorderPane pane;

//	@FXML
//	private TextField lapTimeField;

	@FXML
	private TextField numRacersField;

	@FXML
	private GridPane trackSectionPane;

	@FXML
	private List<TextField> speedFields;

	@FXML
	private List<TextField> rangeFields;

	private List<ParticipantDisplay> participantDisplays;
	
	private ScrollPane myParticipantScrollPane;

	private GridPane myParticipantPane;

	public ProgressBar progressBar;

	private File outputFile;

	private TrackController controller;

	private List<String> linesToWrite;

	private int numLaps;
	private int numRacers;
	private int lapTime;
	private int numSpeedBrackets;
	private List<ParticipantSpeed> speedBracketList;
	private Map<ParticipantSpeed, Text> estimateTimes;

	/**
	 * This is called when Controller.fxml is loaded by javafx, its essentially a
	 * constructor.
	 */
	@FXML
	public void initialize() {
		linesToWrite = new ArrayList<>();
		participantDisplays = new ArrayList<>();
		estimateTimes = new HashMap<>();
		setUpTrackView();

		numLaps = DEFAULT_NUM_LAPS;
		// lapTime = DEFAULT_LAP_TIME;
		numSpeedBrackets = (int) trackSpeedSlider.getValue();
		setSpeedBracketList();
		setupTrackSpeedView();
		numRacers = Integer.parseInt(numRacersField.textProperty().getValue());
		
		setUpParticipantPane();

		outputFile = new File(numLaps + "lap-" + numRacers + "racer-" + lapTime + "timeRace.rce");
		try {
			fileDisplay.setText(outputFile.getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
		}

		outputFileButton.setOnAction(event -> chooseFile());
		submitRace.setOnAction(event -> onSubmit());

		final IntListener racerNumListener = new IntListener((i) -> {
			numRacers = i;
			updateParticipantPane();
		});
		numRacersField.textProperty().addListener(racerNumListener);
		// Force the new IntListener to update
		racerNumListener.changed(numRacersField.textProperty(), "", Integer.toString(numRacers));

		numLapsField.textProperty().addListener(new IntListener((i) -> numLaps = i));
		// lapTimeField.textProperty().addListener(new IntListener((i) -> lapTime = i));
		trackSpeedSlider.valueProperty().addListener((i, o, n) -> {
			numSpeedBrackets = i.getValue().intValue();
			resetSpeedBrackets();
			setSpeedBracketList();
			setupTrackSpeedView();
			updateSpeedBracketComboBox();
		});

	}

	private void setUpParticipantPane() {
		// Instantiation
		myParticipantScrollPane = new ScrollPane();
		myParticipantPane = new GridPane();

		// Formatting
		myParticipantScrollPane.setPrefHeight(pane.getHeight());

		// Adding
		myParticipantScrollPane.setContent(myParticipantPane);
		pane.setRight(myParticipantScrollPane);		
	}

	private void updateSpeedBracketComboBox() {
		int num = 0;
		List<Node> boxes = new ArrayList<>();
		for (Node n : myParticipantPane.getChildren()) {
			if (n instanceof ComboBox) {
				boxes.add(n);
				num++;
			}
		}

		myParticipantPane.getChildren().removeAll(boxes);

		for (int i = 0; i < num; i++) {
			ComboBox<String> cb = new ComboBox<>(getOptions());
			cb.getSelectionModel().select(cb.getItems().size() / 2);
			myParticipantPane.add(cb, 4, i);
		}
	}

	private void setSpeedBracketList() {
		speedBracketList = new ArrayList<>();
//		switch (numSpeedBrackets) {
//		case 3:
//			speedBracketList.add(ParticipantSpeed.FAST);
//			speedBracketList.add(ParticipantSpeed.MEDIUM);
//			speedBracketList.add(ParticipantSpeed.SLOW);
//			break;
//		case 5:
//			speedBracketList.add(ParticipantSpeed.FASTER);
//			speedBracketList.add(ParticipantSpeed.FAST);
//			speedBracketList.add(ParticipantSpeed.MEDIUM);
//			speedBracketList.add(ParticipantSpeed.SLOW);
//			speedBracketList.add(ParticipantSpeed.SLOWER);
//			break;
//		case 7:
//			speedBracketList.add(ParticipantSpeed.FASTEST);
//			speedBracketList.add(ParticipantSpeed.FASTER);
//			speedBracketList.add(ParticipantSpeed.FAST);
//			speedBracketList.add(ParticipantSpeed.MEDIUM);
//			speedBracketList.add(ParticipantSpeed.SLOW);
//			speedBracketList.add(ParticipantSpeed.SLOWER);
//			speedBracketList.add(ParticipantSpeed.SLOWEST);
//		}
		speedBracketList.add(ParticipantSpeed.FAST);
		speedBracketList.add(ParticipantSpeed.MEDIUM);
		speedBracketList.add(ParticipantSpeed.SLOW);
	}

	private void updateParticipantPane() {
		participantDisplays.clear();
		myParticipantPane.getChildren().clear();
		

		for (int i = 0; i < numRacers; i++) {
			ParticipantDisplay newPartDisp = new ParticipantDisplay();
			participantDisplays.add(newPartDisp);
			myParticipantPane.add(newPartDisp, 0, i);
			
			
//			TextField name = new TextField(buildRacerName());
//			name.setPrefWidth(115);
//			TextField id = new TextField(iter.next().toString());
//			id.setPrefWidth(35);
//			participantNameFields.add(name);
//			participantIdFields.add(id);
//			participantPane.add(new Text("Name:"), 0, i);
//			participantPane.add(participantNameFields.get(i), 1, i);
//			participantPane.add(new Text("ID:"), 2, i);
//			participantPane.add(participantIdFields.get(i), 3, i);
//			ComboBox<String> cb = new ComboBox<>(getOptions());
//			cb.getSelectionModel().select(cb.getItems().size() / 2);
//			participantPane.add(cb, 4, i);
		}
		myParticipantPane.setPrefWidth(new ParticipantDisplay().getPrefWidth());
	}

	private ObservableList<String> getOptions() {
		return FXCollections.observableArrayList(
				speedBracketList.stream().map((name) -> name.toString()).collect(Collectors.toList()));
	}

	private void setupTrackSpeedView() {
		speedFields = new ArrayList<>();
		rangeFields = new ArrayList<>();
		estimateTimes = new HashMap<>();
		trackSectionPane.getChildren().clear();

		for (ParticipantSpeed ps : speedBracketList) {
			addTrackSpeedAndRange(ps);
		}
	}

	private void addTrackSpeedAndRange(ParticipantSpeed bracket) {
		int size = trackSectionPane.getChildren().size() / 6;

		trackSectionPane.add(new Text(bracket + " Speed"), 0, size);

		TextField speedField = new TextField(Double.toString(bracket.getVelocity()));
		speedField.setPrefWidth(50);
		speedField.textProperty().addListener(new DoubleListener((i) -> updateSpeedBracket(bracket, i)));
		speedFields.add(speedField);
		trackSectionPane.add(speedField, 1, size);

		trackSectionPane.add(new Text(bracket + " Range (+/-)"), 2, size);

		TextField rangeField = new TextField(Double.toString(bracket.getRange()));
		rangeField.setPrefWidth(50);
		rangeField.textProperty().addListener(new DoubleListener((i) -> updateRangeBracket(bracket, i)));
		rangeFields.add(rangeField);
		trackSectionPane.add(rangeField, 3, size);

		trackSectionPane.add(new Text(bracket + " Est. Finish"), 4, size);

		Text estimate = new Text(String.format("%.2f", controller.getLength() / bracket.getVelocity() / 1000));
		estimateTimes.put(bracket, estimate);
		trackSectionPane.add(estimate, 5, size);
	}

	private void updateSpeedBracket(ParticipantSpeed speedBracket, double value) {
		speedBracket.setVelocity(value);
		estimateTimes.get(speedBracket)
				.setText(String.format("%.2f", controller.getLength() / speedBracket.getVelocity()));
		System.out.println(speedBracket + " Speed " + value);
	}

	private void resetSpeedBrackets() {
//		ParticipantSpeed.FASTEST.setVelocity(100);
//		ParticipantSpeed.FASTER.setVelocity(100);
		ParticipantSpeed.FAST.setVelocity(110);
		ParticipantSpeed.MEDIUM.setVelocity(100);
		ParticipantSpeed.SLOW.setVelocity(90);
//		ParticipantSpeed.SLOWER.setVelocity(100);
//		ParticipantSpeed.SLOWEST.setVelocity(100);
	}

	private void updateRangeBracket(ParticipantSpeed speedBracket, double value) {
		speedBracket.setRange(value);
		System.out.println(speedBracket + " Range " + value);
	}

	private void setUpTrackView() {
		try {
			FXMLLoader loader = new FXMLLoader(OvalController.class.getResource("OvalController.fxml"));
			pane.setCenter(loader.load());
			controller = loader.getController();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void chooseFile() {
		FileChooser chooser = new FileChooser();
		outputFile = chooser.showSaveDialog(null);
		try {
			fileDisplay.setText(outputFile.getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void onSubmit() {
		progressBar.setVisible(true);
		SimTask task = new SimTask();
		progressBar.progressProperty().bind(task.progressProperty());
		task.setOnSucceeded(wse -> progressBar.setVisible(false));
		new Thread(task).start();
	}

	// This is a fix for the randomness causing the last racer to
	// actually finish after the specified time, this method is a
	// hack to change the time to the last racer
	private void adjustForLastRacer() {

		// Start at the end of the array and look back until we find when
		// the last racer crossed the finish line.
		int i = linesToWrite.size() - 1;
		while (!linesToWrite.get(i).contains("$C")) {
			i--;
		}

		// Extract the millisecond from the string
		int raceActuallyOver = (int) Double.parseDouble(linesToWrite.get(i).split(":")[1]);

		// Because this is technically an index, add one to get the length
		raceActuallyOver++;

		// Modify the time to be this new value
		linesToWrite.set(5, "#TIME:" + raceActuallyOver);

//		System.out.println(raceActuallyOver);
	}

	private class SimTask extends Task<Void> {
		@Override
		protected Void call() throws Exception {
			try {
				System.out.println("Begin");
				int telemetryInterval = (int) telemetryIntervalSlider.getValue();
				Track track = controller.getTrack();
				List<Participant> participants = new ArrayList<>();
//				List<Integer> ids = participantIdFields.stream()
//						.map((id) -> Integer.parseInt(id.textProperty().getValue())).collect(Collectors.toList());
//				List<String> names = participantNameFields.stream().map((name) -> name.textProperty().getValue())
//						.collect(Collectors.toList());
				
				
				double start = 0;
				// System.out.println(speedBracket);
				// System.out.println(rangeBracket);
				for (ParticipantDisplay pd : participantDisplays) {
					participants.add(new Participant(pd.getID(), pd.getName(), start, track.getTrackLength(), pd.getSpeed()));
					// System.out.println(id);
				}
				Race race = new Race(track, numLaps, telemetryInterval, participants);
				System.out.println("race");

				// Convert seconds to millis
				int expectedTime = track.getTrackLength() / 10;
				System.out.println( track.getTrackLength() + " " + numLaps + " " + expectedTime + " EXPECTED");

				int currentTime = 0;
				linesToWrite.add("#RACE:" + raceName.getText());
				linesToWrite.add("#TRACK:" + track.getTrackName());

				// TODO These are hard coded values, make things in the UI to
				// change this.
				linesToWrite.add("#WIDTH:" + track.getWidthRatio());
				linesToWrite.add("#HEIGHT:" + track.getHeightRatio());

				linesToWrite.add("#DISTANCE:" + track.getTrackLength());
				linesToWrite.add("#TIME:" + expectedTime);
				linesToWrite.add("#PARTICIPANTS:" + numRacers);

				System.out.println("race2");
				while (race.stillGoing()) {
//					System.out.println("going");
					race.stepRace().forEach(linesToWrite::add);
					currentTime++;
					updateProgress(currentTime, expectedTime);

				}

				adjustForLastRacer();

				System.out.println("Right before writing file");
				try (PrintWriter pw = new PrintWriter(outputFile)) {
					System.out.println("writing file");
					for (int i = 0; i < linesToWrite.size(); i++) {
						pw.println(linesToWrite.get(i));
					}
					pw.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

}
