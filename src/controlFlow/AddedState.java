/* 
 * Editor for Automata
 * Bachelor Thesis
 * Raphael Bucher 2016 / 2017
 * */
package controlFlow;

import editor.Automat;
import editor.Editor;
import editor.EndState;
import editor.StartEndState;
import editor.StartState;
import editor.State;

public class AddedState extends UserAction {
  private int stateIndex;
  private int x, y;
  private String type;
  
  /** Will not save a reference to the state itself, but will copy all essential information out
   * of it. */
  public AddedState(State state) {
    this.stateIndex = state.getStateIndex();
    this.x = state.getX();
    this.y = state.getY();
    this.type = state.getClass().getSimpleName();
  }

  @Override
  protected void undo() {
    // automats deleteState will not delete any Transitions in this case since there could
    // have been none when this new State was added
    Automat automat = Editor.getDrawablePanel().getAutomat();
    automat.deleteState(automat.getStateByStateIndex(stateIndex), false);
  }

  @Override
  protected void redo() {
    State state;
    if (type.equals(State.class.getSimpleName())) {
      state = new State(stateIndex, x, y);
    } else if (type.equals(StartState.class.getSimpleName())) {
      state = new StartState(stateIndex, x, y);
    } else if (type.equals(EndState.class.getSimpleName())) {
      state = new EndState(stateIndex, x, y);
    } else {
      state = new StartEndState(stateIndex, x, y);
    }
    
    // Call addState(), but don't let this action get registered as a new UserAction
    Editor.getDrawablePanel().getAutomat().addState(state, false);
  }
  
  /** String representation for the Undo / Redo MenuItem */
  public String toString() {
    return "Add State";
  }
}
