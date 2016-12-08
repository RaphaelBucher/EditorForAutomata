package editor;

import java.awt.Graphics2D;
import java.util.ArrayList;

public class Transition extends Shape {
  private State transitionStart;
  private State transitionEnd;
  // A transition can have one or more symbols
  private ArrayList<Symbol> symbols;
  
  // Painting information. Either a TransitionPaintLine or a TransitionPaintArc
  private TransitionPaint transitionPaint;
  
  /** Only this constructor allowed, default-constructor not permitted. */
  public Transition(State transitionStart, State transitionEnd) {
    this.transitionStart = transitionStart;
    this.transitionEnd = transitionEnd;
    symbols = new ArrayList<Symbol>();
    
    if (transitionStart.getStateIndex() == transitionEnd.getStateIndex())
      transitionPaint = new TransitionPaintArc(this);
    else
      transitionPaint = new TransitionPaintLine(this);
  }
  
  public void paint(Graphics2D graphics2D) {
    // Paint the Transitions, a Line or an Arc depending on its transitionPaints type
    transitionPaint.paint(graphics2D);
    
    // The symbols
    Symbol.paint(graphics2D, symbols, transitionPaint.getSymbolDockingPoint(),
        transitionPaint.getSymbolDirection());
  }
  
  @Override
  public boolean mouseClickHit(int mouseX, int mouseY) {
    return false;
  }

  @Override
  public void displaySelectedShapeTooltip() {
  }
  
  /** Adds a symbol to the transitions symbol-ArrayList. If the symbol is already in the list,
   * this method does nothing.
   * @return false if the symbol was invalid, true otherwise, even if the symbol was in the
   * list already. */
  public boolean addSymbol(char symbol) {
    if (!Symbol.isSymbolValid(symbol))
      return false;
    
    if (!containsSymbol(symbol)) {
      symbols.add(new Symbol(symbol));
    }
    
    return true;
  }
  
  /** Checks whether the passed character is already in the list.
   * @return true if the character is in the list, false otherwise. */
  private boolean containsSymbol(char symbol) {
    for (int i = 0; i < symbols.size(); i++) {
      if (symbols.get(i).getSymbol() == symbol) {
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
  
  /** Checks whether the passed list contains a Transition with the passed indices. */
  public static Transition isInArrayList(int startStateIndex, int endStateIndex,
      ArrayList<Transition> transitions) {
    for (int i = 0; i < transitions.size(); i++) {
      // Same transitionStartIndex and same transitionEndIndex? Direction of the transition matters.
      if (startStateIndex == transitions.get(i).getTransitionStart().getStateIndex() &&
          endStateIndex == transitions.get(i).getTransitionEnd().getStateIndex()) {
        return transitions.get(i);
      }
    }
    
    return null; 
  }
  
  /** Computes the painting information based on the type of transitionPaint. */
  public void computePaintingCoordinates(ArrayList<Transition> transitions) {
    transitionPaint.computePaintingCoordinates(transitions);
  }
  
  /** Checks whether the passed transition-list containes a transition with reverse direction,
   * start- and end-states switched.
   * @return Returns the reverseTransition, or null if the list doesn't contain it. */
  public Transition gotReverseTransition(ArrayList<Transition> transitions) {
    // Build a temporary transition with start- and end-states switched
    Transition reverseTransition = new Transition(this.transitionEnd, this.transitionStart);
    
    // Check if the reversed Transition is in the passed list
    return reverseTransition.isInArrayList(transitions);
  }
  
  /** Returns all Transitions from a passed list that have the passed state as a start-state. */
  public static ArrayList<Transition> getTransitionsByStartState(State startState,
      ArrayList<Transition> transitions) {
    ArrayList<Transition> foundTransitions = new ArrayList<Transition>();
    
    for (int i = 0; i < transitions.size(); i++) {
      if (startState.stateIndex == transitions.get(i).getTransitionStart().stateIndex)
        foundTransitions.add(transitions.get(i));
    }
    
    return foundTransitions;
  }
  
  /** Returns all Transitions from a passed list that have the passed state as a start-state or end-state.
   * Also returns arcTransitions (same start- and end-state). */
  public static ArrayList<Transition> getTransitionsByState(State state,
      ArrayList<Transition> transitions) {
    ArrayList<Transition> foundTransitions = new ArrayList<Transition>();
    
    for (int i = 0; i < transitions.size(); i++) {
      if (state.stateIndex == transitions.get(i).getTransitionStart().stateIndex ||
          state.stateIndex == transitions.get(i).getTransitionEnd().stateIndex)
        foundTransitions.add(transitions.get(i));
    }
    
    return foundTransitions;
  }
  
  /** Returns all LineTransitions from a passed list that have the passed state as a start-state or
   * end-state. Filters out arcTransitions (same start- and end-state). */
  public static ArrayList<Transition> getLineTransitionsByState(State state,
      ArrayList<Transition> transitions) {
    ArrayList<Transition> foundTransitions = new ArrayList<Transition>();
    
    for (int i = 0; i < transitions.size(); i++) {
      if (state.stateIndex == transitions.get(i).getTransitionStart().stateIndex ||
          state.stateIndex == transitions.get(i).getTransitionEnd().stateIndex) {
        if (!transitions.get(i).isArcTransition())
          foundTransitions.add(transitions.get(i));
      }
    }
    
    return foundTransitions;
  }
  
  /** Returns true if the transitions start- and end-states are the same (an ArcTransition). */
  private boolean isArcTransition() {
    return this.transitionStart.stateIndex == this.transitionEnd.stateIndex;
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
  
  public ArrayList<Symbol> getSymbols() {
    return this.symbols;
  }
  
  public TransitionPaint getTransitionPaint() {
    return this.transitionPaint;
  }
}
