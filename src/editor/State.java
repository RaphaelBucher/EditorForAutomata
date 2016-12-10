/* 
 * Bachelor Thesis
 * Raphael Bucher
 * November 2016
 * */
package editor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;

/** A state in an automat. */
public class State extends Shape {
  protected int stateIndex; // indexing it myself, not going with the ArrayLists indexing

  // Coordinates where the state is drawn into the canvas. This is the center of
  // the circle, not the top-left of the rendered area of the state.
  protected int x, y;

  public State(int stateIndex, int x, int y) {
    this.stateIndex = stateIndex;
    this.x = x;
    this.y = y;
  }

  public void paint(Graphics2D graphics2D) {
    // At the start of all paint-methods of State and subclasses
    if (this.isSelected)
      graphics2D.setColor(Config.SELECTED_STATE_COLOR);
    
    graphics2D.drawOval(x - Config.STATE_DIAMETER / 2, y - Config.STATE_DIAMETER / 2, Config.STATE_DIAMETER,
        Config.STATE_DIAMETER);

    // draw the state and its index
    graphics2D.setFont(new Font("Arial", Font.PLAIN, 22));
    graphics2D.drawString("q", x - 14, y + 5);

    graphics2D.setFont(new Font("Arial", Font.PLAIN, 14));
    graphics2D.drawString("" + this.getStateIndex(), x - 2, y + 9);
    
    // At the end of all paint-methods of State and subclasses. Restore default color
    graphics2D.setColor(Color.BLACK);
  }

  public int getStateIndex() {
    return this.stateIndex;
  }
  
  @Override
  public boolean mouseClickHit(int mouseX, int mouseY) {
    int deltaX = mouseX - x;
    int deltaY = mouseY - y;
    int stateRadius = Config.STATE_DIAMETER / 2;
    
    // Simple Pythagoras. Square root not computed for performance reasons.
    if (deltaX * deltaX + deltaY * deltaY <= stateRadius * stateRadius)
      return true;
    
    return false;
  }
  
  @Override
  public void displaySelectedShapeTooltip() {
    Tooltip.setMessage(Config.Tooltips.stateSelected, Config.TOOLTIP_DRAWABLE_PANEL_DISPLAY_AMOUNT);
  }
  
  /** Deletes all transitions from the list which come from or go to the state. */
  public void deleteTransitions(ArrayList<Transition> transitions) {
    // Gather all elements to be removed from the ArrayList first, no removal of ArrayLists
    // elements while looped through that ArrayList
    ArrayList<Transition> stateTransitions = getTransitions(transitions);
    
    // Remove them one-by-one
    for (int i = 0; i < stateTransitions.size(); i++) {
      Editor.getDrawablePanel().getAutomat().deleteTransition(stateTransitions.get(i));
    }
  }
  
  /** Returns a list with all transitions that have the state as a start- or end-state. */
  public ArrayList<Transition> getTransitions(ArrayList<Transition> transitions) {
    ArrayList<Transition> stateTransitions = new ArrayList<Transition>();
    
    for (int i = 0; i < transitions.size(); i++) {
      if (this.equals(transitions.get(i).getTransitionStart()) ||
          this.equals(transitions.get(i).getTransitionEnd())) {
        stateTransitions.add(transitions.get(i));
      }
    }
    
    return stateTransitions;
  }
  
  /** Moves the state to the passed coordinates. Doesn't update the states transitions. */
  public void moveTo(int x, int y) {
    this.x = x;
    this.y = y;
  }
  
  /** Checks whether this state has an ArcTransition.
   * @return the ArcTransition or null if it has none. */
  public Transition gotArcTransition(ArrayList<Transition> transitions) {
    return Transition.isInArrayList(this.stateIndex, this.stateIndex, transitions);
  }
  
  // Setters and Getters
  public int getX() {
    return this.x;
  }
  
  public int getY() {
    return this.y;
  }
}
