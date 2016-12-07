package editor;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/** Painting information of a transition with the same start- and end-state,
 * resulting in an arc back to its state with a little arrow in the end. */
public class TransitionPaintArc extends TransitionPaint {
  // The state on which the ArcTransition is
  private State hostState;
  
  // The angle from the hostState to the Transitions-Arc, between 0 and 2 * Math.PI
  private double arcAngle;
  
  // parameters for Swings drawArc-method
  private int arcX, arcY; // the upper-left corner of the rectangle the arc is drawn into
  private int arcStartAngle; // in degrees
  
  /** @params aggregateTransition The Transition which instantiated this TransitionPaintLine-object. */
  public TransitionPaintArc(Transition aggregateTransition) {
    super();
    
    this.aggregateTransition = aggregateTransition;
    this.hostState = aggregateTransition.getTransitionStart();
  }
  
  @Override
  public void paint(Graphics2D graphics2D) {
    graphics2D.drawArc(arcX, arcY, Config.STATE_DIAMETER, Config.STATE_DIAMETER, arcStartAngle, 270);
  }
  
  /** Entry-point for the painting information computation. */
  public void computePaintingCoordinates(ArrayList<Transition> transitions) {
    // Determine the middle of the biggest free area on the circle of the state.
    computeTransitionAngle(transitions);
    
    // Compute the parameters for Swings drawArc-function
    computeArc();
  }
  
  /** Computes the parameters for Swings drawArc-method. */
  private void computeArc() {
    int radius = Config.STATE_DIAMETER / 2;
    
    // distance from the hostStates middle to the Arcs middle. Make it slightly bigger
    // with 0.5 so that the Arc never enters the states circle, but still has
    // no white space between them too.
    double distance = Math.sqrt(radius * radius + radius * radius) + 0.5d;
    
    // compute the upper left corner of the arcs bounding rectangle
    this.arcX = hostState.x + (int)Math.round(Math.cos(arcAngle) * distance) - radius;
    // Cartesian to Swings coordinate system
    this.arcY = hostState.y - (int)Math.round(Math.sin(arcAngle) * distance) - radius;
    
    // Arcs startAngle in degrees
    this.arcStartAngle = (int)Math.round(Math.toDegrees(arcAngle)) - 135;
  }
  
  /** Determine the middle of the biggest free area on the circle of the state */
  private void computeTransitionAngle(ArrayList<Transition> transitions) {
    // Get all LineTransitions that involve the ArcTransitions state
    ArrayList<Transition> lineTransitions = Transition.getLineTransitionsByState(
        hostState, transitions);

    // Get a list of all Transition-PaintingPoints on the states circle
    ArrayList<Point> circlePoints = getStateCirclePoints(lineTransitions);
    
    // Compute all the circlePoints angles in relation to the states center
    // and sorts them.
    ArrayList<Double> circlePointsAngles = circlePointAngles(circlePoints);
    
    // find the best spot for the Arc to be painted.
    this.arcAngle = middleOfBiggestGap(circlePointsAngles);
  }
  
  /** Takes a list of angles between 0 and 2 * Math.PI and returns the middle of
   * the biggest gap of them. If the State has no LineTransitions meaning
   * the arc has the full area at his disposal, this method returns the angle
   * of the hostState in relation to the DrawablePanels middle. */
  private double middleOfBiggestGap(ArrayList<Double> circlePointsAngles) {
    // Special case if the hostState has no LineTransitions
    if (circlePointsAngles.size() <= 0)
      return hostStateAngleToMiddle();
    
    double middle;
    double biggestGap = 0.0d;
    double currentGap = 0.0d;
    int biggestGapIndex = 0; // refering to the first of the two indices compared
    
    for (int i = 0; i < circlePointsAngles.size() - 1; i++) {
      currentGap = circlePointsAngles.get(i + 1) - circlePointsAngles.get(i);
      if (currentGap >= biggestGap) {
        biggestGap = currentGap;
        biggestGapIndex = i;
      }
    }
    
    // Compare the last entry with the first one
    currentGap = Math.PI * 2 + circlePointsAngles.get(0) -
        circlePointsAngles.get(circlePointsAngles.size() - 1);
    if (currentGap >= biggestGap) {
      biggestGap = currentGap;
      biggestGapIndex = circlePointsAngles.size() - 1; // Index to the last element
    }
    
    // Compute the middle of the biggest Gap
    middle = circlePointsAngles.get(biggestGapIndex) + biggestGap / 2.0d;
    
    // Ensure result is between 0 and 2 * Math.PI. For sin-cos, bigger values would not
    // be a problem of course since they are periodical functions. This restriction is
    // for internal purpose.
    if (middle >= 2 * Math.PI)
      middle -= 2 * Math.PI;
    
    return middle;
  }
  
  /** Computes the angle of the hostState in relation to the middle of the DrawablePanel. 
   * This is used for the Arc-computation if the hostState has no LineTransitions. */
  private double hostStateAngleToMiddle() {
    int drawablePanelMidX = Editor.getDrawablePanel().getWidth() / 2;
    int drawablePanelMidY = Editor.getDrawablePanel().getHeight() / 2;
    
    return TransitionPaint.computeAngle(hostState.x - drawablePanelMidX, drawablePanelMidY - hostState.y);
  }
  
  /** Compute all the circlePoints angles in relation to the hostStates center.  */
  private ArrayList<Double> circlePointAngles(ArrayList<Point> circlePoints) {
    ArrayList<Double> angles = new ArrayList<Double>();
    
    int deltaX, deltaY;
    for (int i = 0; i < circlePoints.size(); i++) {
      deltaX = circlePoints.get(i).x - hostState.x;
      
      // Cartesian coordinate system not Swings
      deltaY = hostState.y - circlePoints.get(i).y;
      angles.add(new Double(TransitionPaint.computeAngle(deltaX, deltaY)));
    }
    
    // Sort the angles-Collection with a Comparator
    Collections.sort(angles, new Comparator<Double>() {
      @Override
      public int compare(Double angle1, Double angle2)
      {
        return angle1.compareTo(angle2);
      }
    });
    
    return angles;
  }
  
  /** Get a list of all LineTransition-PaintingPoints on the states circle. This is used to
   * determine these points angles. */
  private ArrayList<Point> getStateCirclePoints(ArrayList<Transition> lineTransitions) {
    ArrayList<Point> circlePoints = new ArrayList<Point>();
    
    TransitionPaintLine transitionPaintLine;
    for (int i = 0; i < lineTransitions.size(); i++) {
      // Get the painting information of the iterated Transition
      transitionPaintLine = (TransitionPaintLine) lineTransitions.get(i).getTransitionPaint();
      if (hostState.stateIndex == lineTransitions.get(i).getTransitionStart().stateIndex) {
        // Add the transitions startingStates Point to the list
        circlePoints.add(new Point(transitionPaintLine.getTransitionStartX(),
            transitionPaintLine.getTransitionStartY()));
      } else {
        // Add the transitions endingStates Point to the list
        circlePoints.add(new Point(transitionPaintLine.getTransitionEndX(),
            transitionPaintLine.getTransitionEndY()));
      }
    }
    
    return circlePoints;
  }
}
