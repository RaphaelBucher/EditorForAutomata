package editor;

import java.awt.Color;
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
  
  /** @params aggregateTransition The Transition which instantiated this TransitionPaintLine-object. */
  public TransitionPaintArc(Transition aggregateTransition) {
    super();
    
    this.aggregateTransition = aggregateTransition;
    this.hostState = aggregateTransition.getTransitionStart();
  }
  
  
  @Override
  public void paint(Graphics2D graphics2D) {
    graphics2D.setColor(Color.GREEN);
    
    graphics2D.drawLine(hostState.x, hostState.y, (int)(hostState.x + Math.cos(arcAngle) * 40.0d),
        (int)(hostState.y - Math.sin(arcAngle) * 40.0d));
    
    graphics2D.setColor(Color.BLACK);
  }
  
  /** ... */
  public void computePaintingCoordinates(ArrayList<Transition> transitions) {
    // Determine the middle of the biggest free area on the circle of the state.
    computeTransitionAngle(transitions);
    
    
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
    // TODO: todotodotodo
    return 0.0d; // todo
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
