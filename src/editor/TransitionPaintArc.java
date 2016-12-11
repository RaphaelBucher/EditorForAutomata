/* 
 * Editor for Automata
 * Bachelor Thesis
 * Raphael Bucher 2016 / 2017
 * */
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
  
  /** The angle from the hostState to the Transitions-Arc, between 0 and 2 * Math.PI */
  private double arcAngle;
  
  // parameters for Swings drawArc-method
  private int arcX, arcY; // the upper-left corner of the rectangle the arc is drawn into
  private int arcStartAngle; // in degrees
  
  private Point arrowCenter;
  private Point arrowLineOne;
  private Point arrowLineTwo;
  
  /** @params aggregateTransition The Transition which instantiated this TransitionPaintLine-object. */
  public TransitionPaintArc(Transition aggregateTransition) {
    super();
    
    this.aggregateTransition = aggregateTransition;
    this.hostState = aggregateTransition.getTransitionStart();
    
    arrowCenter = new Point();
    arrowLineOne = new Point();
    arrowLineTwo = new Point();
    
    symbolDockingPoint = new Point();
  }
  
  @Override
  public void paint(Graphics2D graphics2D) {
    if (this.aggregateTransition.isSelected)
      graphics2D.setColor(Config.SELECTED_STATE_COLOR);
    
    // The Arc
    graphics2D.drawArc(arcX, arcY, Config.STATE_DIAMETER, Config.STATE_DIAMETER, arcStartAngle, 270);
    
    // The Arrow
    graphics2D.drawLine(arrowCenter.x, arrowCenter.y, arrowLineOne.x, arrowLineOne.y);
    graphics2D.drawLine(arrowCenter.x, arrowCenter.y, arrowLineTwo.x, arrowLineTwo.y);
    
    graphics2D.setColor(Color.BLACK);
  }
  
  /** Entry-point for the painting information computation. */
  public void computePaintingCoordinates(ArrayList<Transition> transitions) {
    // Determine the middle of the biggest free area on the circle of the state.
    computeTransitionAngle(transitions);
    
    // Compute the parameters for Swings drawArc-function
    computeArc();
    
    // Computes the three Points for the arrow
    computeArrow();
    
    // Compute the symbolDockingPoint
    computeSymbolDockingPoint();
  }
  
  /** Computes the three Points for the arrow */
  private void computeArrow() {
    // States radius
    double radius = Config.STATE_DIAMETER / 2 + 0.6d;
    
    // Angle of the arrow Center point in relation to the hostStates middle
    // A little correction to respect the Arc drawn by Swing
    double arrowAngle = arcAngle - Math.toRadians(45.0d - 0.7d);
    double arrowLength = 11.0d;
    double arrowAngleOffset = Math.toRadians(25.0d);
    // Its an Arc incoming, not a straight line. Rotate the Arrow slightly to the direction the
    // Arc-Line comes in
    double arrowCorrection = Math.toRadians(14.0d);
    
    // Arrow center point
    arrowCenter.x = hostState.x + (int)Math.round(Math.cos(arrowAngle) * radius);
    arrowCenter.y = hostState.y - (int)Math.round(Math.sin(arrowAngle) * radius);
    
    // Arrow point one
    arrowLineOne.x = arrowCenter.x +
        (int)Math.round(Math.cos(arrowAngle + arrowAngleOffset + arrowCorrection) * arrowLength);
    arrowLineOne.y = arrowCenter.y -
        (int)Math.round(Math.sin(arrowAngle + arrowAngleOffset + arrowCorrection) * arrowLength);
    
    // Arrow point two
    arrowLineTwo.x = arrowCenter.x +
        (int)Math.round(Math.cos(arrowAngle - arrowAngleOffset + arrowCorrection) * arrowLength);
    arrowLineTwo.y = arrowCenter.y -
        (int)Math.round(Math.sin(arrowAngle - arrowAngleOffset + arrowCorrection) * arrowLength);
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
  
  /** Computes the Point where the Transitions Symbols are painted. */
  public void computeSymbolDockingPoint() {
    int stateRadius = Config.STATE_DIAMETER / 2;
    double dockingPointVectorLength = Math.sqrt(stateRadius * stateRadius + stateRadius * stateRadius) +
        stateRadius + this.offsetVectorLength;
    
    // In Swings coordinates
    this.symbolDockingPoint.x = hostState.x + (int)Math.round(Math.cos(arcAngle) *
        dockingPointVectorLength);
    this.symbolDockingPoint.y = hostState.y - (int)Math.round(Math.sin(arcAngle) *
        dockingPointVectorLength);
    
    this.symbolDirection();
  }
  
  /** symbolDirection is 1 if the Symbols are displayed into the right direction
   * of their dockingPoint, -1 if they go to the left. Its 0 if they will
   * be displayed centered. */
  private void symbolDirection() {
    // Symbols will be rendered centered for the top and bottom center * 2 degrees, e.g. 80°-100° etc.
    double center = 15.0d;
    
    // most of Quadrants 1 and 4 => Display symbols to the right from the dockingPoint.
    if (arcAngle <= Math.toRadians(90.0d - center) || arcAngle >= Math.toRadians(270.0d + center))
      this.symbolDirection = 1;
    else if (arcAngle >= Math.toRadians(90.0d + center) && arcAngle <= Math.toRadians(270.0d - center))
      this.symbolDirection = -1;
    else
      this.symbolDirection = 0; // Symbols centered
  }
  
  /** Checks if the mouse hit this ArcTransition */
  protected boolean mouseClickHit(Point mousePosition) {
    return Math2D.arcTransitionClicked(mousePosition, new Point(arcX + Config.STATE_DIAMETER / 2,
        arcY + Config.STATE_DIAMETER / 2), arcAngle);
  }
  
  // Setters and Getters
  @Override
  public Point getSymbolDockingPoint() {
    return this.symbolDockingPoint;
  }

  @Override
  public int getSymbolDirection() {
    return this.symbolDirection;
  }
}
