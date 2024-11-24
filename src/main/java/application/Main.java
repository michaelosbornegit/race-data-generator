package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.Controller;

/**
 * This class launches the JavaFX application.
 *
 * @author Myles Haynes
 * @author Michael Osborne
 */
public class Main extends Application {

    /**
     * Creates a Controller, adds it to the stage, and shows it.
     *
     * {@inheritDoc}
     */
    @Override
    public void start(final Stage stage) throws Exception {
        stage.setTitle("305 Race Builder");
        Controller c = new Controller();
        Scene scene = new Scene(c);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    /**
     * The main method that kicks the program off.
     *
     * @param args the command line arguments.
     */
    public static void main(final String[] args) {
        launch(args);
    }
}
