package controlFlow;

import java.util.ArrayList;

import editor.Editor;
import editor.Transition;

/** Saves the essential information of a Transition. */
public class TransitionCopy {
  private int startStateIndex, endStateIndex;
  private ArrayList<Character> symbols;
  
  public TransitionCopy(Transition transition) {
    this.startStateIndex = transition.getTransitionStart().getStateIndex();
    this.endStateIndex = transition.getTransitionEnd().getStateIndex();
    
    symbols = copySymbols(transition);
  }
  
  /** Returns all symbols from the Transition as a Character-ArrayList. */
  public static ArrayList<Character> copySymbols(Transition transition) {
    ArrayList<Character> copiedSymbols = new ArrayList<Character>();
    
    for (int i = 0; i < transition.getSymbols().size(); i++) {
      copiedSymbols.add(new Character(transition.getSymbols().get(i).getSymbol()));
    }
    
    return copiedSymbols;
  }
  
  /** Returns the original Transition of the automat or null if the automat has no such transition. */
  protected Transition getOriginalTransition() {
    return Transition.isInArrayList(getStartStateIndex(), getEndStateIndex(),
        Editor.getDrawablePanel().getAutomat().getTransitions());
  }
  
  // Setters and Getters
  public int getStartStateIndex() {
    return startStateIndex;
  }
  
  public int getEndStateIndex() {
    return endStateIndex;
  }
  
  public ArrayList<Character> getSymbols() {
    return symbols;
  }
}
