package editor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class Transition extends Shape {
  private State transitionStart;
  private State transitionEnd;
  // A transition can have one or more symbols
  private ArrayList<Character> symbols;
  
  // Painting information
  private int transitionStartX;
  private int transitionStartY;
  private int transitionEndX;
  private int transitionEndY;

  public Transition(State transitionStart) {
    this.transitionStart = transitionStart;
    symbols = new ArrayList<Character>();
  }
  
  public Transition(State transitionStart, State transitionEnd) {
    this(transitionStart);
    this.transitionEnd = transitionEnd;
  }
  
  public void paint(Graphics2D graphics2D) {
    // Transitions line
    graphics2D.drawLine(transitionStartX, transitionStartY, transitionEndX, transitionEndY);
    
    // Transitions arrow
  }
  
  @Override
  public boolean mouseClickHit(int mouseX, int mouseY) {
    return false;
  }

  @Override
  public void displaySelectedShapeTooltip() {
  }
  
  /** Adds a symbol to the transitions symbol-ArrayList. If the symbol is already in the list,
   * this method does nothing. */
  public void addSymbol(char symbol) {
    if (!containsSymbol(symbol))
      symbols.add(new Character(symbol));
  }
  
  /** Checks whether the passed character is already in the list.
   * @return true if the character is in the list, false otherwise. */
  private boolean containsSymbol(char symbol) {
    for (int i = 0; i < symbols.size(); i++) {
      if (symbols.get(i).compareTo(new Character(symbol)) == 0) {
        // chars are equal
        return true;
      }
    }
    
    return false;
  }
  
  /** Checks whether this Transition is in the passed list of Transitions. Compares
   * only by States stateIndex, not States.equal(...) since the States have a flag isSelected
   * which would lead to wrong results. Returns the transition from the list in case it has
   * the same indices, or null if the transition is not present in the list.  */
  public Transition isInArrayList(ArrayList<Transition> transitions) {
    for (int i = 0; i < transitions.size(); i++) {
      // Same transitionStartIndex and same transitionEndIndex? Directions of the transition matters.
      if (this.getTransitionStart().getStateIndex() == transitions.get(i).getTransitionStart().getStateIndex() &&
          this.getTransitionEnd().getStateIndex() == transitions.get(i).getTransitionEnd().getStateIndex()) {
        return transitions.get(i);
      }
    }
    
    return null; 
  }
  
  /** Computes the (x, y)-coordinates of the Transitions start- and end-point. Distinguishes if
   * there's a Transition with the reverse direction already. */
  public void computePaintingCoordinates(ArrayList<Transition> transitions) {
    // computed in Radian
    double startAngle = computeAngle();
    // Ending angle = startAngle + 180°
    double endAngle = startAngle + Math.PI; 
    
    // In case there's a reverse transition, shift the transitions painted line
    // a bit away from the direct states center-to-center line by an offsetAngle
    Transition reverseTransition = gotReverseTransition(transitions);
    if (reverseTransition != null) {
      startAngle += Config.TRANSITION_PAINT_ANGLE_OFFSET;
      endAngle -= Config.TRANSITION_PAINT_ANGLE_OFFSET;
      
      // Update the reverse transition to shift it away by the offsetAngle
      reverseTransition.computePaintingCoordinates(startAngle + Math.PI, endAngle + Math.PI);
    }
    
    // Compute the coordinates of the transition
    computePaintingCoordinates(startAngle, endAngle);
  }
  
  /** Computes the (x, y)-coordinates of the Transitions start- and end-point. */
  private void computePaintingCoordinates(double startAngle, double endAngle) {
    // Radius of a state
    double stateRadius = (double)Config.STATE_DIAMETER / 2;
    
    // --- starting state ---
    transitionStartX = (int)(Math.round( transitionStart.getX() + Math.cos(startAngle) * stateRadius ));
    // Compute it in Swings coordinate system ("going down" => y increase), not in the cartesian one
    transitionStartY = (int)(Math.round( transitionStart.getY() - Math.sin(startAngle) * stateRadius ));
    
    // --- ending state ---
    transitionEndX = (int)(Math.round( transitionEnd.getX() + Math.cos(endAngle) * stateRadius ));
    transitionEndY = (int)(Math.round( transitionEnd.getY() - Math.sin(endAngle) * stateRadius ));
  }
  
  /** Computes the transitions angle between the start- and the end-state in radian. It 
   * coputes the angle of the end-state in relation to the start-state. Returns a value 
   * between 0 and 2 * Math.PI */
  private double computeAngle() {
    int deltaX = transitionEnd.getX() - transitionStart.getX();
    // Make deltaY in the cartesian coordinate system, not like in Swings painting.
    int deltaY = transitionStart.getY() - transitionEnd.getY();
    // The hypotenuse
    double c = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

    // Sufficient if the end-state is in the quadrants 1 or 2 in relation to the start-state
    double angle = Math.acos(deltaX / c);
    
    // Make result valid for 360° and not only for 180° (quadrants 1 and 2)
    if (deltaY < 0)
      angle = Math.PI * 2 - angle;
    
    return angle;
  }
  
  /** Checks whether the passed transition-list containes a transition with reverse direction,
   * start- and end-states switched.
   * @return Returns the reverseTransition, or null if the list doesn't contain it. */
  private Transition gotReverseTransition(ArrayList<Transition> transitions) {
    // Build a temporary transition with start- and end-states switched
    Transition reverseTransition = new Transition(this.transitionEnd, this.transitionStart);
    
    // Check if the reversed Transition is in the passed list
    return reverseTransition.isInArrayList(transitions);
  }
  
  // Setters and Getters
  public State getTransitionStart() {
    return this.transitionStart;
  }
  
  public void setTransitionEnd(State transitionEnd) {
    this.transitionEnd = transitionEnd;
  }
  
  public State getTransitionEnd() {
    return this.transitionEnd;
  }
  
  public ArrayList<Character> getSymbols() {
    return this.symbols;
  }
  
  public int getTransitionStartX() {
    return this.transitionStartX;
  }
  
  public int getTransitionStartY() {
    return this.transitionStartY;
  }
  
  public int getTransitionEndX() {
    return this.transitionEndX;
  }
  
  public int getTransitionEndY() {
    return this.transitionEndY;
  }
}
