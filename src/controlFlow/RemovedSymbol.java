/* 
 * Editor for Automata
 * Bachelor Thesis
 * Raphael Bucher 2016 / 2017
 * */
package controlFlow;

import editor.Transition;

/** The transitions first symbols are saved as AddedTransition-actions, not as instances of this class. */
public class RemovedSymbol extends UserAction {
  private char symbol;
  private TransitionCopy hostTransitionCopy;
  
  public RemovedSymbol(char symbol, Transition hostTransition) {
    this.symbol = symbol;
    this.hostTransitionCopy = new TransitionCopy(hostTransition);
  }

  @Override
  protected void undo() {
    Transition hostTransition = hostTransitionCopy.getOriginalTransition();
    hostTransition.addSymbol(symbol, false);
  }

  @Override
  protected void redo() {
    Transition hostTransition = hostTransitionCopy.getOriginalTransition();
    hostTransition.removeSymbol(symbol, false);
  }
  
  /** String representation for the Undo / Redo MenuItem */
  public String toString() {
    return "Remove Symbol";
  }
}
