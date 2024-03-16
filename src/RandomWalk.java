/**
 * @Author      Martin Scheele
 * @Created     Wed Mar 06 2024
 * @File        RandomWalk.java
 * @Description Models a two-dimensional simple random walk
 */

 /*
  * Note: This program structure feels overcomplicated, messy, and unintuitive. 
  *
  * - My implementation of passing the KeyFrame for the RandomWalkCircle TimeLine aniimations 
  * with the RandomWalkPane::move() method from the parent to the child RandomWalkCircle feels 
  * illegal, and the way I instantiate and reset the RandomWalkPane in the start method feels 
  * somewhat unintuitive as well.
  *
  * - With the way RandomWalkCircle is currently designed the animation has to be play when 
  * the animation is initially set, since RandomWalkCircle::startAnimation() is called 
  * when the RandomWalkCircle changes fill color (as a listener to fillProperty). Setting
  * another fillProperty listener does not resolve the issue. This feels clunky, as it is 
  * inelegant to have a method perform multiple actions. Not sure how to resolve this, if I 
  * had more time to debug it I could probably work something out.
  *
  * - The recursion in the RandomWalkPane::move() method feels elegant although it does cause 
  * an infinite loop of StackOverflowExceptions and NullPointerExceptions to be thrown if the 
  * RandomWalkPane window size is decreased causing one of the active RandomWalkCircles to go 
  * off of the visible screen. I'm not sure what this points to as being a flaw in my design, 
  * whether its the move method taking an object reference parameter, the recursion itself, 
  * the animation, etc. 
  *
  * If I had the time I would like to rethink the program structure entirely, but it would 
  * be helpful to have some specific suggestions on how to improve my program design.
  */

  // TODO: look over program for cases where methods can be made to return values
  // mainRW -> primary...
  
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * Models a two-dimensional simple random walk with 
 */
public class RandomWalk extends Application {

    /**
     * Main method
     * @param args command line arguments
     * @throws Exception JavaFX exception(?)
     */
    public static void main(String[] args) throws Exception {
        launch(args);
    }

    /**
     * Start method
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        int defaultWidth = 800;     // default width for RandomWalkPane
        int defaultHeight = 600;    // default height for RandomWalkPane

        // create ComboBoxes for width, height, and duration values
        Integer[] resolutionTitles = { 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000 };
        ObservableList<Integer> resolutions = FXCollections.observableArrayList(resolutionTitles);

        ComboBox<Integer> widthComboBox = new ComboBox<Integer>(resolutions);
        widthComboBox.setValue(defaultWidth);
        widthComboBox.setMinWidth(75);
        widthComboBox.setMaxWidth(75);

        ComboBox<Integer> heightComboBox = new ComboBox<Integer>(resolutions);
        heightComboBox.setValue(defaultHeight);
        heightComboBox.setMaxWidth(75);
        heightComboBox.setMinWidth(75);

        Integer[] durationTitles = { 50, 100, 150, 200, 250, 300, 350, 400, 450, 500 };
        ObservableList<Integer> durations = FXCollections.observableArrayList(durationTitles);
        ComboBox<Integer> durationComboBox = new ComboBox<Integer>(durations);
        durationComboBox.setValue(100);
        durationComboBox.setMinWidth(75);
        durationComboBox.setMaxWidth(75);

        // create Labels for ComboBoxes
        Label heightLabel = new Label("Height ");
        Label widthLabel = new Label("Width ");
        Label durationLabel = new Label("Duration ");

        // create initialize, play, pause, and reset Buttons
        Button initializeButton = new Button("Initialize");
        initializeButton.setMinWidth(75);
        initializeButton.setMaxWidth(75);
        Button playButton = new Button("Play");
        playButton.setMinWidth(75);
        playButton.setMaxWidth(75);
        playButton.setDisable(true);
        Button pauseButton = new Button("Pause");
        pauseButton.setMinWidth(75);
        pauseButton.setMaxWidth(75);
        pauseButton.setDisable(true);
        Button resetButton = new Button("Reset");
        resetButton.setMinWidth(75);
        resetButton.setMaxWidth(75);
        resetButton.setDisable(true);

        // TODO: instead of GridPane, VBox on left
        // create GridPane to hold Labels, ComboBoxes, and Buttons
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20));
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(widthLabel, 0, 0);
        gridPane.add(widthComboBox, 1, 0);
        gridPane.add(heightLabel, 0, 1);
        gridPane.add(heightComboBox, 1, 1);
        gridPane.add(durationLabel, 0, 2);
        gridPane.add(durationComboBox, 1, 2);
        gridPane.add(initializeButton, 2, 0);
        gridPane.add(playButton, 2, 1);
        gridPane.add(pauseButton, 2, 2);
        gridPane.add(resetButton, 3, 0);
        GridPane.setHalignment(widthLabel, HPos.RIGHT);
        GridPane.setHalignment(heightLabel, HPos.RIGHT);
        GridPane.setHalignment(durationLabel, HPos.RIGHT);

        // TODO: try rate slider again?

        // create uninitialized RandomWalkPane 
        // (size must be fixed or recursion in RandomWalkPane::move() function can crash on resizing)
        RandomWalkPane rwPane = new RandomWalkPane();
        rwPane.setSize(defaultWidth, defaultHeight);
        rwPane.setMinSize(defaultWidth, defaultHeight);
        rwPane.setMaxSize(defaultWidth, defaultHeight);
        rwPane.setStyle("-fx-border-color: black; -fx-border-width: 2");

        // create central BorderPane 
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(gridPane);
        borderPane.setCenter(rwPane);
        BorderPane.setAlignment(rwPane, Pos.CENTER);

        // initialize button initializes RandomWalkPane to selected width, height, and animation duration
        initializeButton.setOnAction(e -> {

            int width = widthComboBox.getValue();
            widthComboBox.setDisable(true);
            int height = heightComboBox.getValue();
            heightComboBox.setDisable(true);
            int duration = durationComboBox.getValue();
            durationComboBox.setDisable(true);

            rwPane.setSize(width, height);
            rwPane.setDuration(duration);
            rwPane.setMinSize(width, height);
            rwPane.setMaxSize(width, height);
            rwPane.initialize();

            initializeButton.setDisable(true);
            pauseButton.setDisable(false);
            resetButton.setDisable(false);

        });

        // play button plays RandomWalkPane animations
        playButton.setOnAction(e -> {
            rwPane.play();
            playButton.setDisable(true);
            pauseButton.setDisable(false);
        });

        // pause button pauses RandomWalkPane animations
        pauseButton.setOnAction(e -> {
            rwPane.pause();
            pauseButton.setDisable(true);
            playButton.setDisable(false);
        });

        // reset button resets RandomWalkPane
        resetButton.setOnAction(e -> {
            rwPane.reset();
            resetButton.setDisable(true);
            playButton.setDisable(true);
            pauseButton.setDisable(true);
            widthComboBox.setDisable(false);
            heightComboBox.setDisable(false);
            durationComboBox.setDisable(false);
            initializeButton.setDisable(false);

        });

        // create scene
        Scene scene = new Scene(borderPane, defaultWidth + 200, defaultHeight + 200);
        primaryStage.setTitle("Random Walk");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
