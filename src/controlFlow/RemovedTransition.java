package controlFlow;

import editor.Automat;
import editor.Editor;
import editor.Transition;

/** Used when the user removes a Transition. */
public class RemovedTransition extends UserAction {
  private TransitionCopy transitionCopy;
  
  /** Will not save a reference to the transition itself, but will copy all essential information out
   * of it. */
  public RemovedTransition(Transition transition) {
    transitionCopy = new TransitionCopy(transition);
  }

  @Override
  protected void undo() {
    Automat automat = Editor.getDrawablePanel().getAutomat();
    Transition restoredTransition = new Transition(
        automat.getStateByStateIndex(transitionCopy.getStartStateIndex()),
        automat.getStateByStateIndex(transitionCopy.getEndStateIndex()));
    
    for (int i = 0; i < transitionCopy.getSymbols().size(); i++) {
      restoredTransition.addSymbol(transitionCopy.getSymbols().get(i), false);
    }
    
    automat.addTransition(restoredTransition, false);
  }

  @Override
  protected void redo() {
    Automat automat = Editor.getDrawablePanel().getAutomat();
    automat.deleteTransition(transitionCopy.getOriginalTransition(), false);
  }
  
  /** String representation for the Undo / Redo MenuItem */
  public String toString() {
    return "Remove Transition";
  }
}
