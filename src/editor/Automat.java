/* 
 * Bachelor Thesis
 * Raphael Bucher
 * November 2016
 * */
package editor;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Automat {
  ArrayList<State> states;

  public Automat() {
    this.states = new ArrayList<State>();
  }

  /** Iterates over all added states and draws them. */
  public void paint(Graphics2D graphics2D) {
    for (int i = 0; i < states.size(); i++) {
      states.get(i).paint(graphics2D);
    }
  }

  public void handleMouseClicked(MouseEvent evt, String cursorName) {
    if (cursorName.equals(new Cursor(Cursor.DEFAULT_CURSOR).getName())) {
      // Default arrow-cursor

    } else if (cursorName.equals(Config.Cursor_names.STATE_CURSOR)) {
      // Add state Cursor
      addState(new State(findNewStateIndex(), evt.getX(), evt.getY()));
    } else if (cursorName.equals(Config.Cursor_names.START_STATE_CURSOR)) {
      // Add start state cursor
      addState(new StartState(findNewStateIndex(), evt.getX(), evt.getY()));
    } else if (cursorName.equals(Config.Cursor_names.END_STATE_CURSOR)) {
      // Add end state cursor
      addState(new EndState(findNewStateIndex(), evt.getX(), evt.getY()));
    } else if (cursorName.equals(Config.Cursor_names.START_END_STATE_CURSOR)) {
      // Add start-end state cursor
      addState(new StartEndState(findNewStateIndex(), evt.getX(), evt.getY()));
    } else if ((cursorName.equals(Config.Cursor_names.TRANSITION_CURSOR))) {
      // Transition cursor
    }
  }

  private void addState(State state) {
    if (addingStateAllowed(state))
      states.add(state);
  }

  /**
   * Checks whether it's allowed to add the passed state-object to the Automat's
   * ArrayList states. Reasons to prohibit are e.g. visual collision with
   * another state, state is partly outside the drawable JPanel etc.
   */
  private boolean addingStateAllowed(State state) {
    // check if state is visually allowed to be added??? Write default checking
    // method in class State and override it then in the subclasses when
    // start-state is bigger on the left side for example???

    // checking for class-dependent rules
    if (state instanceof StartState || state instanceof StartEndState) {
      if (!addingStartStateAllowed()) {
        return false;
      }
    }

    // if all checks passed, return true
    return true;
  }

  private boolean addingStartStateAllowed() {
    if (hasStartState()) {
      ErrorMessage.setMessage("Has already a start state");
      return false;
    }

    return true;
  }

  /** Returns true if the Automat already has a start-state, false instead. */
  private boolean hasStartState() {
    for (int i = 0; i < states.size(); i++) {
      if (states.get(i) instanceof StartState || states.get(i) instanceof StartEndState)
        return true;
    }

    return false;
  }

  private int findNewStateIndex() {
    int i = 0;
    while (getStateByStateIndex(i) != null) {
      i++;
    }

    return i;
  }

  /** Returns the found state, or null otherwise. */
  private State getStateByStateIndex(int stateIndex) {
    for (int i = 0; i < states.size(); i++) {
      if (stateIndex == states.get(i).getStateIndex())
        return states.get(i);
    }

    return null;
  }

  // Currently only used for testing
  public void createExampleAutomat() {
    // Discard all current states.
    this.states = new ArrayList<State>();

    // add some states
    states.add(new State(findNewStateIndex(), 100, 100));
    // states.add(new StartState(200, 200));
    states.add(new EndState(findNewStateIndex(), 300, 300));
    states.add(new StartEndState(findNewStateIndex(), 400, 400));

    // states.remove(1);

    states.add(new State(findNewStateIndex(), 600, 600));
    states.add(new State(findNewStateIndex(), 600, 600));
    // states.remove(2);
  }
}
