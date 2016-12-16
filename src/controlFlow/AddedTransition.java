package controlFlow;

import editor.Automat;
import editor.Editor;
import editor.Transition;

/** Used when the user added a new Transition. By design constraints, a newly added
 * Transition can have exactly one symbol. If more symbols are added later, the an
 * instance of the class AddedSymbol will be used. */
public class AddedTransition extends UserAction {
  private TransitionCopy transitionCopy;
  
  /** Will not save a reference to the transition itself, but will copy all essential information out
   * of it. */
  public AddedTransition(Transition transition) {
    transitionCopy = new TransitionCopy(transition);
  }

  @Override
  protected void undo() {
    Automat automat = Editor.getDrawablePanel().getAutomat();
    automat.deleteTransition(transitionCopy.getOriginalTransition(), false);
  }

  @Override
  protected void redo() {
    Automat automat = Editor.getDrawablePanel().getAutomat();
    Transition restoredTransition = new Transition(
        automat.getStateByStateIndex(transitionCopy.getStartStateIndex()),
        automat.getStateByStateIndex(transitionCopy.getEndStateIndex()));
    
    restoredTransition.addSymbol(transitionCopy.getSymbols().get(0), false);
    
    automat.addTransition(restoredTransition, false);
  }
  
  /** String representation for the Undo / Redo MenuItem */
  public String toString() {
    return "Add Transition";
  }
}
