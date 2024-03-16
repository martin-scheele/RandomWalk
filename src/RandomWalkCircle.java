/**
 * @Author      Martin Scheele
 * @Created     Wed Mar 06 2024
 * @File        RandomWalkPane.java
 * @Description Models an individual RandomWalkCircle
 */

import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Models an individual RandomWalkCircle
 */
public class RandomWalkCircle extends Circle {

    // Data fields

    private int dx; // x-coordinate differential
    private int dy; // y-coordinate differential
    private int steps; // number of steps to take
    private final int stepSize = 10; // size of steps
    private int totalSteps = 0; // total steps taken
    private final int maxSteps = 2000; // maximum number of steps
    private Random generator = new Random(); // random number generator
    private Timeline animation; // random walk animation
    private boolean isAnimating; // state of animation (start, not play/pause)

    // Constructors

    /**
     * Multi-arg constructor
     * 
     * @param radius radius of circle
     * @param color  fill color of circle
     */
    RandomWalkCircle(double radius, Color color) {
        super.setRadius(radius);
        super.setFill(color);
        super.setStroke(Color.BLACK);
        isAnimating = false;
        // fillProperty().addListener(ov -> play());
    }

    // Getters and setters

    /**
     * Sets the center coordinates of the RandomWalkCircle
     * 
     * @param x x-coordinate in parent
     * @param y y-coordinate in parent
     */
    public void setCenter(double x, double y) {
        super.setCenterX(x);
        super.setCenterY(y);
    }

    /**
     * Getter for dx
     * 
     * @return change in x for next step
     */
    public double getDx() {
        return dx;
    }

    /**
     * Getter for dy
     * 
     * @return change in y for next step
     */
    public double getDy() {
        return dy;
    }

    /**
     * Generates a random step direction from among the cardinal and ordinal
     * directions,
     * and updates dx and dy accordingly
     */
    public void setDirection() {

        int direction = generator.nextInt(8 - 1 + 1) + 1;

        switch (direction) {
            // N
            case 1: dx = 0; dy = -1; break;
            // NE
            case 2: dx = 1; dy = -1; break;
            // E
            case 3: dx = 1; dy = 0; break;
            // SE
            case 4: dx = 1; dy = 1; break;
            // S
            case 5: dx = 0; dy = 1; break;
            // SW
            case 6: dx = -1; dy = 1; break;
            // W
            case 7:  dx = -1; dy = 0; break;
            // NW
            case 8: dx = -1; dy = -1; break;
            default: break;
        }

    }

    /**
     * Getter for stepSize
     * 
     * @return size of step
     */
    public int getStepSize() {
        return stepSize;
    }

    /**
     * Getter for steps
     * 
     * @return number of steps to take
     */
    public int getSteps() {
        return steps;
    }

    /**
     * Generates random number of steps to take
     */
    public void generateRandomSteps() {
        steps = generator.nextInt(9 - 1 + 1) + 1;
    }

    /**
     * Getter for totalSteps
     * 
     * @return total number of steps taken
     */
    public int getTotalSteps() {
        return totalSteps;
    }

    /**
     * Increments number of steps taken and stops animation once maxSteps has been
     * reached
     */
    public void incrementTotalSteps() {
        totalSteps += steps;
        if (totalSteps >= maxSteps) {
            animation.pause();
            animation = null;
        }
    }

    // TODO: use PathTransition instead of TimeLine
    /**
     * Starts the random walk animation
     * 
     * @param keyframe
     */
    public void startAnimation(KeyFrame keyframe) {
        animation = new Timeline(keyframe);
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();
        isAnimating = true;
    }

    /**
     * Getter for isAnimating
     * 
     * @return state of animation (start, not play/pause)
     */
    public boolean isAnimating() {
        return isAnimating;
    }

    // Other methods

    /**
     * Plays the random walk aniimation
     */
    public void play() {
        if (animation != null) {
            animation.play();
        }
        // if (isAnimating) {
        // animation.play();
        // }
    }

    /**
     * Pauses the random walk animation
     */
    public void pause() {
        if (animation != null) {
            animation.pause();
        }
        // if (isAnimating) {
        // animation.pause();
        // }
    }

}