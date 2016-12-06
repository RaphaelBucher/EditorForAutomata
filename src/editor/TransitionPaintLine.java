package editor;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

/** Painting information of a normal transition of a different start- and end-state,
 * resulting in a straight line with a little arrow up front. */
public class TransitionPaintLine extends TransitionPaint {
  // --- Painting information ---
  private boolean isPainted;
  
  // Direction of the transition. Angle between 0 and 2 * Math.PI
  private double directionAngle;
  
  // start- and end-point coordinates
  private int transitionStartX;
  private int transitionStartY;
  private int transitionEndX;
  private int transitionEndY;
  
  /** Transitions length based on the start- and end-point coordinates. */
  private int length;
  
  // Arrow
  private Point arrowPointOne;
  private Point arrowPointTwo;
  
  /** @params aggregateTransition The Transition which instantiated this TransitionPaintLine-object. */
  public TransitionPaintLine(Transition aggregateTransition) {
    super();
    
    this.aggregateTransition = aggregateTransition;
    
    arrowPointOne = new Point();
    arrowPointTwo = new Point();
  }

  @Override
  public void paint(Graphics2D graphics2D) {
    // Does the transition have the minimal length to be painted?
    if (isPainted) {
      // Transitions line
      graphics2D.drawLine(transitionStartX, transitionStartY, transitionEndX, transitionEndY);
      
      // Transitions arrow
      graphics2D.drawLine(transitionEndX, transitionEndY, arrowPointOne.x, arrowPointOne.y);
      graphics2D.drawLine(transitionEndX, transitionEndY, arrowPointTwo.x, arrowPointTwo.y);
    }
  }
  
  /** Computes the (x, y)-coordinates of the Transitions start- and end-point. Distinguishes if
   * there's a Transition with the reverse direction already. Also updates the coordinates
   * of it's reverse-Transition if it has one. */
  public void computePaintingCoordinates(ArrayList<Transition> transitions) {
    computePaintingCoordinatesLine(transitions);
  }
  
  /** The computational method for a transition with a different start- and end-point, resulting
   * in a straight line with an arrow. Computes the (x, y)-coordinates of the Transitions
   * start- and end-point. Distinguishes if there's a Transition with the reverse direction
   * already. Also updates the coordinates of it's reverse-Transition if it has one. */
  private void computePaintingCoordinatesLine(ArrayList<Transition> transitions) {
    // computed in Radian
    this.directionAngle = computeAngle();
    
    double startAngle = directionAngle;
    // Ending angle = startAngle + 180°
    double endAngle = startAngle + Math.PI; 
    
    // In case there's a reverse transition, shift the transitions painted line
    // a bit away from the direct states center-to-center line by an offsetAngle
    Transition reverseTransition = aggregateTransition.gotReverseTransition(transitions);
    if (reverseTransition != null) {
      startAngle += Config.TRANSITION_PAINT_ANGLE_OFFSET;
      endAngle -= Config.TRANSITION_PAINT_ANGLE_OFFSET;
      
      // Update the reverse transition to shift it away by the offsetAngle
      ((TransitionPaintLine) reverseTransition.getTransitionPaint()).computePaintingCoordinatesLine(
          startAngle + Math.PI, endAngle + Math.PI);
    }
    
    // Compute the coordinates of the transition
    computePaintingCoordinatesLine(startAngle, endAngle);
  }
  
  /** Computes the (x, y)-coordinates of the Transitions start- and end-point. */
  public void computePaintingCoordinatesLine(double startAngle, double endAngle) {
    // Radius of a state. Take a tiny bit more to ensure the lines don't reach into the 
    // states circle.
    double stateRadius = (double)Config.STATE_DIAMETER / 2 + 0.5d;
    
    // --- starting state ---
    transitionStartX = (int)(Math.round( aggregateTransition.getTransitionStart().getX()
        + Math.cos(startAngle) * stateRadius ));
    // Compute it in Swings coordinate system ("going down" => y increase), not in the cartesian one
    transitionStartY = (int)(Math.round( aggregateTransition.getTransitionStart().getY()
        - Math.sin(startAngle) * stateRadius ));
    
    // --- ending state ---
    transitionEndX = (int)(Math.round( aggregateTransition.getTransitionEnd().getX()
        + Math.cos(endAngle) * stateRadius ));
    transitionEndY = (int)(Math.round( aggregateTransition.getTransitionEnd().getY()
        - Math.sin(endAngle) * stateRadius ));
    
    // The arrow
    computeArrowPaintingCoordinates();
    
    // The length of the transition and based on this the flag is the transitions will be painted
    computeLength();
  }
  
  /** Computes the arrow painting information. */
  private void computeArrowPaintingCoordinates() {
    double arrowLength = 12.0d;
    double arrowAngleOffset = 15 / 180.0d * Math.PI;
    double reverseAngle = this.directionAngle + Math.PI;
    
    // First arrow Point. Y-Axis reversed to the Cartesian coordinate system.
    this.arrowPointOne.x = (int)(Math.round(transitionEndX + 
        Math.cos(reverseAngle + arrowAngleOffset) * arrowLength));
    this.arrowPointOne.y = (int)(Math.round(transitionEndY - 
        Math.sin(reverseAngle + arrowAngleOffset) * arrowLength));
    
    // Second arrow Point. Y-Axis reversed to the Cartesian coordinate system.
    this.arrowPointTwo.x = (int)(Math.round(transitionEndX + 
        Math.cos(reverseAngle - arrowAngleOffset) * arrowLength));
    this.arrowPointTwo.y = (int)(Math.round(transitionEndY - 
        Math.sin(reverseAngle - arrowAngleOffset) * arrowLength));
  }
  
  /** Computes the transitions angle between the start- and the end-state in radian. It 
   * computes the angle of the end-state in relation to the start-state. Returns a value 
   * between 0 and 2 * Math.PI */
  private double computeAngle() {
    int deltaX = aggregateTransition.getTransitionEnd().getX()
        - aggregateTransition.getTransitionStart().getX();
    // Make deltaY in the cartesian coordinate system, not like in Swings painting.
    int deltaY = aggregateTransition.getTransitionStart().getY()
        - aggregateTransition.getTransitionEnd().getY();
    
    return TransitionPaint.computeAngle(deltaX, deltaY);
  }
  
  /** Computed the transitions exact length based on the start- and end-point coordinates.
   * If the length surpasses the minimal length, it will be painted. */
  private void computeLength() {
    int deltaX = transitionEndX - transitionStartX;
    int deltaY = transitionEndY - transitionStartY;
    
    this.length = (int)(Math.sqrt(deltaX * deltaX + deltaY * deltaY));
    
    this.isPainted = this.length >= Config.TRANSITION_MIN_LENGTH;
  }

  // Setters and Getters
  public int getTransitionStartX() {
    return transitionStartX;
  }

  public int getTransitionStartY() {
    return transitionStartY;
  }

  public int getTransitionEndX() {
    return transitionEndX;
  }

  public int getTransitionEndY() {
    return transitionEndY;
  }
}