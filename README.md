# RandomWalk
---
GUI random walk simulator made with JavaFX

Unit project for CSC-239 - JAVA Programming, Spring 2024

Retroactive upload

## Notes
---
This program structure feels overcomplicated, messy, and unintuitive. 

- My implementation of passing the KeyFrame for the RandomWalkCircle TimeLine aniimations 
with the RandomWalkPane::move() method from the parent to the child RandomWalkCircle feels 
illegal, and the way I instantiate and reset the RandomWalkPane in the start method feels 
somewhat unintuitive as well.

- With the way RandomWalkCircle is currently designed the animation has to be play when 
the animation is initially set, since RandomWalkCircle::startAnimation() is called 
when the RandomWalkCircle changes fill color (as a listener to fillProperty). Setting
another fillProperty listener does not resolve the issue. This feels clunky, as it is 
inelegant to have a method perform multiple actions. Not sure how to resolve this, if I 
had more time to debug it I could probably work something out.

- The recursion in the RandomWalkPane::move() method feels somewhat elegant although it does cause 
an infinite loop of StackOverflowExceptions and NullPointerExceptions to be thrown if the 
RandomWalkPane window size is decreased causing one of the active RandomWalkCircles to go 
off of the visible screen. I'm not sure what this points to as being a flaw in my design, 
whether its the move method taking an object reference parameter, the recursion itself, 
the animation, etc.
