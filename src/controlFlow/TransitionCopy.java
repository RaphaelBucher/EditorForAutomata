package controlFlow;

import java.util.ArrayList;

import editor.Automat;
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
  
  /** Returns a Transition-instance of this object. */
  public Transition toTransition() {
    Automat automat = Editor.getDrawablePanel().getAutomat();
    Transition transition = new Transition(
        automat.getStateByStateIndex(getStartStateIndex()),
        automat.getStateByStateIndex(getEndStateIndex()));
    
    // Append the Symbols
    for (int i = 0; i < symbols.size(); i++) {
      transition.addSymbol(symbols.get(i).charValue(), false);
    }
    
    return transition;
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
