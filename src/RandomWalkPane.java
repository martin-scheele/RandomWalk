/**
 * @Author      Martin Scheele
 * @Created     Wed Mar 06 2024
 * @File        RandomWalkPane.java
 * @Description Pane subclass for random walk scene
 */

import java.util.ArrayList;
import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

/**
 * Pane subclass for random walk scene
 */
class RandomWalkPane extends Pane {

    // Data fields

    private ArrayList<RandomWalkCircle> rwCircles; // ArrayList for secondary RandomWalkCircles
    private RandomWalkCircle mainRW = null; // main blue RandomWalkCircle
    private int duration = 0; // duration of RandomWalkCircle animations

    // Constructors

    /**
     * No-arg constructor
     */
    RandomWalkPane() {

    }

    /**
     * Multi-arg constructor
     * 
     * @param width    width of pane
     * @param height   height of pane
     * @param duration duration of RandomWalkCircle animations
     */
    RandomWalkPane(int width, int height, int duration) {
        setHeight(height);
        setWidth(width);
        setDuration(duration);
        initialize();
    }

    // Other methods

    /**
     * Initializes RandomWalk
     */
    public void initialize() {
        // instantiate main blue RandomWalkCircle
        mainRW = new RandomWalkCircle(5, Color.BLUE);
        mainRW.setViewOrder(-1);
        mainRW.setCenter(getWidth() / 2, getHeight() / 2);
        getChildren().add(mainRW);

        rwCircles = new ArrayList<RandomWalkCircle>();

        // TODO: turn into method
        // create 10 green inactive RandomWalkCircles
        Random generator = new Random();
        for (int i = 0; i < 10; i++) {

            double x = (getWidth() / 10) * (generator.nextInt(9 - 1 + 1) + 1);
            double y = (getHeight() / 10) * (generator.nextInt(9 - 1 + 1) + 1);

            RandomWalkCircle rw = new RandomWalkCircle(10, Color.GREEN);
            rw.setViewOrder(-1);
            rw.setCenter(x, y);
            rwCircles.add(rw);
            // active animation when fill color changes (to red)
            rw.fillProperty()
                    .addListener(ov -> rw.startAnimation(new KeyFrame(Duration.millis(duration), e -> move(rw))));
        }
        getChildren().addAll(rwCircles);

        // start main blue RandomWalkCircle animation
        mainRW.startAnimation(new KeyFrame(Duration.millis(duration), e -> move(mainRW)));

    }

    /**
     * Setter for duration
     * 
     * @param duration duration for RandomWalkCircle animations
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * Sets size of RandomWalkPane
     * 
     * @param width  width of pane
     * @param height height of pane
     */
    public void setSize(double width, double height) {
        setWidth(width);
        setHeight(height);
    }

    /**
     * Returns a circle
     * 
     * @param x      x-coordinate pane
     * @param y      y-coordinate in pane
     * @param radius radius of circle
     * @param color  fill color of circle
     * @return circle
     */
    private Circle getCircle(double x, double y, double radius, Color color) {
        Circle circle = new Circle(x, y, radius);
        circle.setFill(color);
        return circle;
    }

    /**
     * Moves the RandomWalkCircle within the pane
     * 
     * @param rwCircle circle to move
     */
    private void move(RandomWalkCircle rwCircle) {
        rwCircle.play();
        // generate random direction and number of steps
        rwCircle.setDirection();
        rwCircle.generateRandomSteps();

        double start_x = rwCircle.getCenterX(); // starting x-coordinate
        double start_y = rwCircle.getCenterY(); // starting y-coordinate
        double x = start_x; // x-coordinate of next location
        double y = start_y; // y-coordinate of next location
        double dx = rwCircle.getDx(); // x-coordinate differential
        double dy = rwCircle.getDy(); // y-coordinate differential
        int steps = rwCircle.getSteps(); // number of steps to be taken
        int stepSize = rwCircle.getStepSize(); // size of steps
        double radius = rwCircle.getRadius(); // radius of RandomWalkCircle
        Color color = (Color) rwCircle.getFill(); // color of RandomWalkCircle

        // add circle marker to previous coordinate location
        getChildren().add(getCircle(x, y, 3, color));

        // increment coordinates of next location
        for (int i = 0; i < steps * stepSize; i++) {

            // TODO: investigate further
            // StackOverflowException?
            // NullPointerException?
            // if the generated step would leave the border,
            // rerun function and return from function afterwards
            if (x < radius || x > getWidth() - radius || y < radius || y > getHeight() - radius) {
                rwCircle.setCenter(start_x, start_y);
                try {
                    move(rwCircle);
                } catch (Throwable e) {
                    pause();
                    System.out.println(e);
                    Platform.exit();
                }
                return;
            }

            // update coordinate of next location
            x += dx;
            y += dy;
        }
        rwCircle.setCenter(x, y);
        rwCircle.incrementTotalSteps();

        // TODO: turn into method?
        // create line from old coordinate location to new coordinate location
        Line line = new Line(start_x, start_y, x, y);
        line.setStroke(color);
        line.setFill(color);
        getChildren().add(line);

        // TODO: turn into method?
        // for each non active secondary RandomWalkCircle check collision with the line
        // that was just placed,
        // if collision is detected change the fill color to red to trigger animation
        // start
        for (RandomWalkCircle rw : rwCircles) {
            // TODO: redo logic, remove isAnimating rwCircles from ArrayList if possible
            // https://stackoverflow.com/a/47638860
            // obtains the intersection of two shapes. if the width of the bounds of the
            // resulting
            // intersection is greater than or equal to zero, the shapes are intersecting
            if (!rw.isAnimating() && Shape.intersect(rw, line).getBoundsInLocal().getWidth() >= 0) {
                rw.setFill(Color.RED);
            }

        }
    }

    /**
     * Plays animations of all active RandomWalkCircles
     */
    public void play() {
        mainRW.play();
        for (RandomWalkCircle rw : rwCircles)
            rw.play();
    }

    /**
     * Pauses animations of all active RandomWalkCircles
     */
    public void pause() {
        mainRW.pause();
        for (RandomWalkCircle rw : rwCircles)
            rw.pause();
    }

    /**
     * Resets the state of the RandomWalk
     * Pauses animations, sets RandomWalkCircles to null, and clears children
     */
    public void reset() {
        // if animations aren't paused, orphaned circles cause exceptions to be thrown
        // (i miss manual memory management)
        pause();
        mainRW = null;
        for (RandomWalkCircle rw : rwCircles) {
            rw.pause();
            rw = null;
        }
        getChildren().clear();
        rwCircles = null;
        duration = 0;

        // System.gc();
    }

}