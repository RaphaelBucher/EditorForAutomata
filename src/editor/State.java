/* 
 * Editor for Automata
 * Bachelor Thesis
 * Raphael Bucher 2016 / 2017
 * */
package editor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/** A state in an automat. */
public class State extends Shape {
  protected int stateIndex; // indexing it myself, not going with the ArrayLists indexing

  // Coordinates where the state is drawn into the canvas. This is the center of
  // the circle, not the top-left of the rendered area of the state.
  protected int x, y;
  
  /** Used for the Layout. Is not updated automatically due to performance reasons. Call
   * State.updateNeighborAmounts() to compute it. */
  protected int neighborAmount;
  
  /** For graph traversation only. */
  private boolean marked;
  
  // Constants for state-type changing
  public static final int STATE = 0;
  public static final int START_STATE = 1;
  public static final int END_STATE = 2;
  public static final int START_END_STATE = 3;

  public State(int stateIndex, int x, int y) {
    this.stateIndex = stateIndex;
    this.x = x;
    this.y = y;
  }

  public void paint(Graphics2D graphics2D) {
    graphics2D.setColor(Color.BLACK);

    // State selected or part of the word-animation-path?
    if (this.isSelected || this.wordAcceptedPath)
      graphics2D.setColor(Config.SELECTED_STATE_COLOR);
    
    // State highlighted during the word-animation?
    if (Editor.getWordAnimation() != null) {
      if (this.equals(Editor.getWordAnimation().getHighlightedShape()))
        graphics2D.setColor(Editor.getWordAnimation().getHighlightedColor());
    }
    
    graphics2D.drawOval(x - Config.STATE_DIAMETER / 2, y - Config.STATE_DIAMETER / 2, Config.STATE_DIAMETER,
        Config.STATE_DIAMETER);

    // draw the state and its index
    graphics2D.setFont(new Font("Arial", Font.PLAIN, 22));
    graphics2D.drawString("q", x - 14, y + 5);

    graphics2D.setFont(new Font("Arial", Font.PLAIN, 14));
    graphics2D.drawString("" + this.getStateIndex(), x - 2, y + 9);
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
  
  /** Deletes all transitions from the list which come from or go to the state.
   * @return Returns all deleted (removed from automats transitions-ArrayList only)
   * Transitions. */
  public ArrayList<Transition> deleteTransitions(Automat automat) {
    // Gather all elements to be removed from the ArrayList first, no removal of ArrayLists
    // elements while looped through that ArrayList
    ArrayList<Transition> stateTransitions = getTransitions(automat.getTransitions());
    
    // Remove them one-by-one
    for (int i = 0; i < stateTransitions.size(); i++) {
      automat.deleteTransition(stateTransitions.get(i), false);
    }
    
    return stateTransitions;
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
  
  /** Checks whether this state has an Transition to the destination State.
   * @return the Transition or null if it has none. */
  public Transition gotTransitionTo(State destinationState, ArrayList<Transition> transitions) {
    return Transition.isInArrayList(this.stateIndex, destinationState.stateIndex, transitions);
  }
  
  /** Moves the state to the passed coordinates. Doesn't update the states transitions. */
  public void moveTo(int x, int y) {
    this.x = x;
    this.y = y;
  }
  
  /** Moves the state by the passed values deltaX and deltaY. */
  public void moveDelta(int deltaX, int deltaY) {
    this.x += deltaX;
    this.y += deltaY;
  }
  
  /** Checks whether this state has an ArcTransition.
   * @return the ArcTransition or null if it has none. */
  public Transition gotArcTransition(ArrayList<Transition> transitions) {
    return Transition.isInArrayList(this.stateIndex, this.stateIndex, transitions);
  }
  
  /** Returns a deep copy of the State. */
  public State copy() {
    State state = new State(stateIndex, x, y);
    return state;
  }
  
  /** Computes the amount of neighbor-states the state has transitions to.
   * Used for the Layout. Is not updated automatically due to performance reasons. */
  public static void updateNeighborAmounts(Automat automat) {
    for (int i = 0; i < automat.getStates().size(); i++) {
      State state = automat.getStates().get(i);
      ArrayList<Transition> stateLineTransitions =
          Transition.getLineTransitionsByState(state, automat.getTransitions());

      Set<Integer> neighbors = new HashSet<Integer>();
      
      for (int j = 0; j < stateLineTransitions.size(); j++) {
        neighbors.add(stateLineTransitions.get(j).getTransitionStart().getStateIndex());
        neighbors.add(stateLineTransitions.get(j).getTransitionEnd().getStateIndex());
      }
      
      neighbors.remove(state.getStateIndex());
      state.neighborAmount = neighbors.size();
    }
  }
  
  /** Sets the marked flag to false for all states in the passed list.*/
  public static void unmarkStates(ArrayList<State> states) {
    for (int i = 0; i < states.size(); i++) {
      states.get(i).marked = false;
    }
  }
  
  /** Returns the found state, or null otherwise. */
  public static State getStateByStateIndex(int stateIndex, ArrayList<State> states) {
    for (int i = 0; i < states.size(); i++) {
      if (stateIndex == states.get(i).getStateIndex())
        return states.get(i);
    }

    return null;
  }
  
  /** Returns the type of the state, e.g. State.STATE or State.START_STATE. */
  public int getType() {
    return State.STATE;
  }
  
  
  // Setters and Getters
  public int getX() {
    return this.x;
  }
  
  public int getY() {
    return this.y;
  }
  
  public void setX(int x) {
    this.x = x;
  }
  
  public void setY(int y) {
    this.y = y;
  }
  
  public int getNeighborAmount() {
    return this.neighborAmount;
  }
  
  public boolean isMarked() {
    return this.marked;
  }
  
  public void setMarked(boolean marked) {
    this.marked = marked;
  }
}
