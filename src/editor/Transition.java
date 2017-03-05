/* 
 * Editor for Automata
 * Bachelor Thesis
 * Raphael Bucher 2016 / 2017
 * */
package editor;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

import controlFlow.AddedSymbol;
import controlFlow.RemovedSymbol;
import controlFlow.UserAction;

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
    
    // In case of a LineTransition that is not painted (minimal distance not surpassed),
    // don't paint the Transitions Symbols
    if (transitionPaint instanceof TransitionPaintLine) {
      if ( !((TransitionPaintLine) transitionPaint).isPainted() ) {
        return;
      }
    }
    
    // The symbols
    Symbol.paint(graphics2D, symbols, transitionPaint.getSymbolDockingPoint(),
        transitionPaint.getSymbolDirection());
  }
  
  @Override
  public boolean mouseClickHit(int mouseX, int mouseY) {
    return transitionPaint.mouseClickHit(new Point(mouseX, mouseY));
  }

  @Override
  public void displaySelectedShapeTooltip() {
    Tooltip.setMessage(Config.Tooltips.transitionSelected, 0);
  }
  
  /** Adds a symbol to the transitions symbol-ArrayList. If the symbol is already in the list,
   * this method does nothing.
   * @return false if the symbol was invalid, true otherwise, even if the symbol was in the
   * list already. */
  public boolean addSymbol(char symbol, boolean addActionToControlFlow) {
    if (!Symbol.isSymbolValid(symbol))
      return false;
    
    if (!containsSymbol(symbol)) {
      symbols.add(new Symbol(this, symbol));
      
      if (addActionToControlFlow)
        UserAction.addAction(new AddedSymbol(symbol, this));
    }
    
    return true;
  }
  
  /** Adds a list of symbols to the transitions symbol-ArrayList. If any symbol is already in the list,
   * it is not added. */
  public void addSymbols(ArrayList<Symbol> symbols) {
    for (int i = 0; i < symbols.size(); i++) {
      addSymbol(symbols.get(i).getSymbol(), false);
    }
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
  
  /** Removes a symbol from the Transitions Symbol-list. */
  public void removeSymbol(char symbol, boolean addActionToControlFlow) {
    // Need to check on my own since the Symbols setSelected flag could cause trouble
    // when just removing by Object-comparison. Comparison is done by char only, and their
    // unique in the list by adding-constraints.
    for (int i = 0; i < symbols.size(); i++) {
      if (symbol == symbols.get(i).getSymbol()) {
        symbols.remove(i);
        
        if (addActionToControlFlow)
          UserAction.addAction(new RemovedSymbol(symbol, this));
        
        return;
      }
    }
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
   * start- and end-states switched. Returns null for ArcTransitions.
   * @return Returns the reverseTransition, or null if the list doesn't contain it. */
  public Transition gotReverseTransition(ArrayList<Transition> transitions) {
    if (this.isArcTransition())
      return null;
    
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
  
  /** Returns all Transitions from a passed list that have the passed state as a starting-state
   * and contain the passed symbol in their symbol-list. Can contain the passed startState itself. */
  public static ArrayList<Transition> getTransitionsByStartStateAndSymbol(State startState, Character symbol,
      ArrayList<Transition> transitions) {
    ArrayList<Transition> foundTransitions = new ArrayList<Transition>();
    
    for (int i = 0; i < transitions.size(); i++) {
      if (startState.stateIndex == transitions.get(i).getTransitionStart().stateIndex) {
        if (transitions.get(i).containsSymbol(symbol.charValue()))
          foundTransitions.add(transitions.get(i));
      }
    }
    
    return foundTransitions;
  }
  
  /** Returns all Transitions from a passed list that have the passed state as an end-state. */
  public static ArrayList<Transition> getTransitionsByEndState(State endState,
      ArrayList<Transition> transitions) {
    ArrayList<Transition> foundTransitions = new ArrayList<Transition>();
    
    for (int i = 0; i < transitions.size(); i++) {
      if (endState.stateIndex == transitions.get(i).getTransitionEnd().stateIndex)
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
  
  /** Returns all LineTransitions from a passed list that have the passed state as an end-state.
   * Filters out arcTransitions (same start- and end-state). */
  public static ArrayList<Transition> getLineTransitionsByEndState(State state,
      ArrayList<Transition> transitions) {
    ArrayList<Transition> foundTransitions = new ArrayList<Transition>();
    
    for (int i = 0; i < transitions.size(); i++) {
      if (state.stateIndex == transitions.get(i).getTransitionEnd().stateIndex) {
        if (!transitions.get(i).isArcTransition())
          foundTransitions.add(transitions.get(i));
      }
    }
    
    return foundTransitions;
  }
  
  /** Removes all 'pure' Epsilon-Transitions (Transitions which only an Epsilon-symbol) from a
   * Transition-ArrayList and returns the new list. Transitions which have an Epsilon-Symbol and 
   * other Symbols will not be removed. */
  public static ArrayList<Transition> removeEpsilonTransitions(ArrayList<Transition> transitions) {
    ArrayList<Transition> resultList = new ArrayList<Transition>();
    
    for (int i = 0; i < transitions.size(); i++) {
      // The Transition is a Transition with an Epsilon-Symbol only
      if (transitions.get(i).isEpsilonTransition() && transitions.get(i).getSymbols().size() <= 1)
        continue;
      
      // Else the Transition will be added
      resultList.add(transitions.get(i));
    }
    
    return resultList;
  }
  
  /** Gets all Arc-Transitions from a Transition-ArrayList and return this list. */
  public static ArrayList<Transition> getArcTransitions(ArrayList<Transition> transitions) {
    ArrayList<Transition> resultList = new ArrayList<Transition>();
    
    for (int i = 0; i < transitions.size(); i++) {
      if (transitions.get(i).isArcTransition()) {
        resultList.add(transitions.get(i));
      }
    }
    
    return resultList;
  }
  
  /** Returns true if the transitions start- and end-states are the same (an ArcTransition). */
  public boolean isArcTransition() {
    return this.transitionStart.stateIndex == this.transitionEnd.stateIndex;
  }
  
  /** Returns a deep copy of the Transition. Copies the transitions symbols as well. 
   * @param The automat on which the transitions states should refer to. */
  public Transition copy(Automat automat) {
    Transition transition = new Transition(automat.getStateByStateIndex(transitionStart.stateIndex),
        automat.getStateByStateIndex(transitionEnd.stateIndex));
    
    // Copy the symbols
    for (int i = 0; i < symbols.size(); i++) {
      transition.symbols.add(symbols.get(i).copy(transition));
    }
    
    return transition;
  }
  
  /** Replaces all occurences of the oldState in the list (Starting or Ending-State) with the newState. */
  public static void replaceState(State oldState, State newState, ArrayList<Transition> transitions) {
    for (int i = 0; i < transitions.size(); i++) {
      Transition transition = transitions.get(i);
      
      if (transition.getTransitionStart().equals(oldState)) {
        transition.transitionStart = newState;
      }
      
      if (transition.getTransitionEnd().equals(oldState)) {
        transition.transitionEnd = newState;
      }
    }
  }
  
  /** Returns true if the Transition contains the empty word ε, \u03B5 in Unicode as the Java char value. */
  public boolean isEpsilonTransition() {
    for (int i = 0; i < symbols.size(); i++) {
      if (symbols.get(i).getSymbol() == '\u03B5')
        return true;
    }
    
    return false;
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
