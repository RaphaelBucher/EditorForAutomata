/* 
 * Editor for Automata
 * Bachelor Thesis
 * Raphael Bucher 2016 / 2017
 * */
package controlFlow;

import java.util.ArrayList;

import editor.Automat;
import editor.Editor;
import editor.EndState;
import editor.StartEndState;
import editor.StartState;
import editor.State;
import editor.Transition;

public class RemovedState extends UserAction {
  private int stateIndex;
  private int x, y;
  private String type;
  
  // All Transitions that have been deleted when the state was deleted
  private ArrayList<TransitionCopy> deletedTransitionsCopies;
  
  /** Will not save a reference to the state itself, but will copy all essential information out
   * of it. */
  public RemovedState(State state, ArrayList<Transition> deletedTransitions) {
    this.stateIndex = state.getStateIndex();
    this.x = state.getX();
    this.y = state.getY();
    this.type = state.getClass().getSimpleName();
    
    deletedTransitionsCopies = new ArrayList<TransitionCopy>();
    for (int i = 0; i < deletedTransitions.size(); i++) {
      deletedTransitionsCopies.add(new TransitionCopy(deletedTransitions.get(i)));
    }
  }

  /** Restore the state again. Needs also to restore all transitions that were deleted
   * at the first place. */
  @Override
  protected void undo() {
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
    Automat automat = Editor.getDrawablePanel().getAutomat();
    automat.addState(state, false);
    
    // Restore all originally deleted transitions
    for (int i = 0; i < deletedTransitionsCopies.size(); i++) {
      TransitionCopy deletedTransition = deletedTransitionsCopies.get(i);
      Transition restoredTransition = new Transition(
          automat.getStateByStateIndex(deletedTransition.getStartStateIndex()),
          automat.getStateByStateIndex(deletedTransition.getEndStateIndex()));
      
      // Restore the transitions symbols
      for (int j = 0; j < deletedTransition.getSymbols().size(); j++) {
        restoredTransition.addSymbol(deletedTransition.getSymbols().get(j), false);
      }
      
      automat.addTransition(restoredTransition, false);
    }
  }

  /** Delete the state again. */
  @Override
  protected void redo() {
    Automat automat = Editor.getDrawablePanel().getAutomat();
    automat.deleteState(automat.getStateByStateIndex(stateIndex), false);
  }
  
  /** String representation for the Undo / Redo MenuItem */
  public String toString() {
    return "Remove State";
  }
}
