/* 
 * Editor for Automata
 * Bachelor Thesis
 * Raphael Bucher 2016 / 2017
 * */
package transformation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import editor.Automat;
import editor.EndState;
import editor.StartEndState;
import editor.StartState;
import editor.State;
import editor.Transition;

public class DEATableRow {
  /** The first column of the table, left to the first Symbol. */
  private Set<Integer> states;
  private ArrayList<DEATableElement> tableElements;
  private State newState;
  
  public DEATableRow(Set<Integer> states, ArrayList<Character> alphabet) {
    this.states = states;
    
    // Initialize all elements of the row (the columns)
    tableElements = new ArrayList<DEATableElement>();
    for (int i = 0; i < alphabet.size(); i++) {
      tableElements.add(new DEATableElement(alphabet.get(i)));
    }
  }
  
  /** Computes the reached Set of states for all tableElements (columns) of this Row-Object. */
  public void computeRow(Automat automat, ArrayList<Character> alphabet) {
    for (int i = 0; i < tableElements.size(); i++) {
      Set<Integer> reachedStates = Transformation.toDEAReachedStates(states,
          tableElements.get(i).getSymbol(), automat);
      
      tableElements.get(i).setReachedStatesBySymbol(reachedStates);
      
      Transformation.addDEATableRow(reachedStates, alphabet);
    }
  }
  
  public void createNewState(Automat automat, int rowIndex) {
    // Is it the first row with the Start state?
    if (rowIndex == 0) {
      if (automat.getStateByStateIndex(0).isEndState())
        newState = new StartEndState(0, 1, 1);
      else
        newState = new StartState(0, 1, 1);
      
      return;
    }
    
    // Create a new EndState if the states-list contains at least one End-State
    if (containsEndState(states, automat))
      newState = new EndState(rowIndex, 1, 1);
    else
      newState = new State(rowIndex, 1, 1);
  }
  
  private boolean containsEndState(Set<Integer> stateIndices, Automat automat) {
    Iterator<Integer> iterator = stateIndices.iterator();
    while (iterator.hasNext()) {
      if (automat.getStateByStateIndex(iterator.next()).isEndState())
        return true;
    }
    
    return false;
  }
  
  public void createTransitions(Automat newDEA, ArrayList<DEATableRow> tableRows) {
    for (int i = 0; i < tableElements.size(); i++) {
      // The transitions starting-state is this objects newState
      State transitionEnd = getStateByStateIndices(tableElements.get(i).getReachedStatesBySymbol(), tableRows);
      
      Transition transition = new Transition(this.newState, transitionEnd);
      
      // Add the Symbol
      transition.addSymbol(tableElements.get(i).getSymbol(), false);
      
      newDEA.addTransitionNoPaintingUpdate(transition);
    }
  }
  
  private static State getStateByStateIndices(Set<Integer> stateIndices, ArrayList<DEATableRow> tableRows) {
    for (int i = 0; i < tableRows.size(); i++) {
      if (stateIndices.equals(tableRows.get(i).getStates()))
        return tableRows.get(i).getNewState();
    }
    
    return null;
  }
  
  // Setters and Getters
  public Set<Integer> getStates() {
    return this.states;
  }
  
  public ArrayList<DEATableElement> getTableElements() {
    return this.tableElements;
  }
  
  public State getNewState() {
    return this.newState;
  }
}
