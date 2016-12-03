package editor;

import java.util.ArrayList;

public class Transition extends Shape {
  private State transitionStart;
  private State transitionEnd;
  // A transition can have one or more symbols
  private ArrayList<Character> symbols;

  public Transition(State transitionStart) {
    this.transitionStart = transitionStart;
    symbols = new ArrayList<Character>();
  }
  
  @Override
  public boolean mouseClickHit(int mouseX, int mouseY) {
    return false;
  }

  @Override
  public void displaySelectedShapeTooltip() {
  }
  
  /** Adds a symbol to the transitions symbol-ArrayList. If the symbol is already in the list,
   * this method does nothing. */
  public void addSymbol(char symbol) {
    if (!containsSymbol(symbol))
      symbols.add(new Character(symbol));
  }
  
  /** Checks whether the passed character is already in the list.
   * @return true if the character is in the list, false otherwise. */
  private boolean containsSymbol(char symbol) {
    for (int i = 0; i < symbols.size(); i++) {
      if (symbols.get(i).compareTo(new Character(symbol)) == 0) {
        // chars are equal
        return true;
      }
    }
    
    return false;
  }
  
  /** Checks whether this Transition is in the passed list of Transitions. Compares
   * only by States stateIndex, not States.equal(...) since the States have a flag isSelected
   * which would lead to wrong results. Returns the transition from the list in case it has
   * the same indices, or null if the transition is not present in the list.  */
  public Transition isInArrayList(ArrayList<Transition> transitions) {
    for (int i = 0; i < transitions.size(); i++) {
      // Same transitionStartIndex and same transitionEndIndex? Directions of the transition matters.
      if (this.getTransitionStart().getStateIndex() == transitions.get(i).getTransitionStart().getStateIndex() &&
          this.getTransitionEnd().getStateIndex() == transitions.get(i).getTransitionEnd().getStateIndex()) {
        return transitions.get(i);
      }
    }
    
    return null; 
  }
  
  // Setters and Getters
  public State getTransitionStart() {
    return this.transitionStart;
  }
  
  public void setTransitionEnd(State transitionEnd) {
    this.transitionEnd = transitionEnd;
  }
  
  public State getTransitionEnd() {
    return this.transitionEnd;
  }
  
  public ArrayList<Character> getSymbols() {
    return this.symbols;
  }
  

}
