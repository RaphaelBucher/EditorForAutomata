package transformation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import editor.Automat;
import editor.EndState;
import editor.StartEndState;
import editor.State;
import editor.Transition;

/** Used for the automata transformation algorithms
 * Epsilon-automata -> NEA -> DEA -> minimal DEA. */
public class Transformation {
  private static ArrayList<DEATableRow> toDEATableRows;
  
  /** Creates a DEA from the passed automat which accepts the same language and returns this automat.
   * In case the passed automat is a DEA already, this method returns the passed reference. */
  public static Automat transformToDEA(Automat automat) {
    Automat newDEA = new Automat();
    
    if (Util.isDEA(automat))
      return automat;
    
    // If the Automat is not a NEA but just an Epsilon-Automat, transform the automat into a NEA first.
    // The called method decides itself whether a transformation is needed.
    transformToNEA(automat);
    
    // automat can only be a NEA at this stage
    ArrayList<Character> alphabet = Util.getAlphabet(automat);
    Util.sortCharacters(alphabet);

    toDEATableRows = new ArrayList<DEATableRow>();
    
    // Add a new Row with the Start state
    Set<Integer> startState = new HashSet<Integer>();
    startState.add(0);
    addDEATableRow(startState, alphabet);
    
    // Computes the whole Table for the DEA
    toDEAComputeTable(automat, alphabet);
    
    // Create the States for the new DEA for each Row (State) of the Table and add them to the list
    // of the newly created DEA
    toDEACreateNewStates(automat, newDEA);
    
    // Create the transitions of the new DEA
    toDEACreateTransitions(newDEA);

    return newDEA;
  }
  
  private static void toDEACreateTransitions(Automat newDEA) {
    for (int i = 0; i < toDEATableRows.size(); i++) {
      toDEATableRows.get(i).createTransitions(newDEA, toDEATableRows);
    }
  }
  
  private static void toDEACreateNewStates(Automat originalNEA, Automat newDEA) {
    for (int i = 0; i < toDEATableRows.size(); i++) {
      toDEATableRows.get(i).createNewState(originalNEA, i);
      
      // Add the newly instantiated state to the DEA
      newDEA.addState(toDEATableRows.get(i).getNewState(), false);
    }
  }
  
  private static void toDEAComputeTable(Automat automat, ArrayList<Character> alphabet) {
    int toDEATableRowsIndex = 0;
    
    while (true) {
      if (toDEATableRowsIndex < toDEATableRows.size()) {
        toDEATableRows.get(toDEATableRowsIndex).computeRow(automat, alphabet);
        toDEATableRowsIndex++;
      } else
        break;
    }
  }
  
  /** Checks whether the Set of states is already present in the list. If not, creates a new
   * Row with this set of states. */
  public static void addDEATableRow(Set<Integer> states, ArrayList<Character> alphabet) {
    // Check if the Set of states is already present in the Row-List
    boolean addStates = true;
    for (int i = 0; i < toDEATableRows.size(); i++) {
      if (states.equals(toDEATableRows.get(i).getStates())) {
        addStates = false;
        break;
      }
    }
    if (addStates)
      toDEATableRows.add(new DEATableRow(states, alphabet));
  }

  /** Returns a HashSet of all State-indices that can be reached by reading the symbol
   * from all the passed startingStates. */
  public static Set<Integer> toDEAReachedStates(Set<Integer> startingStates, Character symbol,
      Automat automat) {
    Set<Integer> reachedStates = new HashSet<Integer>();
    
    // Iterate over the passed states
    Iterator<Integer> iterator = startingStates.iterator();
    while (iterator.hasNext()) {
      State state = automat.getStateByStateIndex(iterator.next());
      
      ArrayList<Transition> transitions = Transition.getTransitionsByStartStateAndSymbol(
          state, symbol, automat.getTransitions());
      
      for (int i = 0; i < transitions.size(); i++) {
        reachedStates.add(transitions.get(i).getTransitionEnd().getStateIndex());
      }
    }
    
    return reachedStates;
  }
  
  
  /** Transforms the passed automat into a NEA. In case the passed automat is a NEA already,
   * this method does nothing. */
  public static void transformToNEA(Automat automat) {
    Automat originalAutomat = automat.copy();
    
    if (Util.isNEA(automat))
      return;
    
    // Change states that can reach End-States with a combination of only epsilon-transitions
    // to End-States as well.
    toNEANewEndStates(automat);
    
    // Remove all transitions and let them build by the step 3 of the Algorithm in Abbildung 2.2.6
    automat.deleteAllTransitions();
    toNEARemoveEpsilonTransitions(originalAutomat, automat);
  }
  
  /** Step 3 from Abbildung 2.2.6 in the script.
   * Removes all Epsilon Transitions from the Automat. */
  private static void toNEARemoveEpsilonTransitions(Automat originalAutomat, Automat builtAutomat) {
    for (int i = 0; i < originalAutomat.getStates().size(); i++) {
      State state = originalAutomat.getStates().get(i);
      
      // Gather all incoming transitions
      ArrayList<Transition> incomingTransitions = Transition.getTransitionsByEndState(
          state, originalAutomat.getTransitions());
      
      // filter out Epsilon-Transitions
      incomingTransitions = Transition.removeEpsilonTransitions(incomingTransitions);
      
      // Call the recursive backwards EpsilonTransition searchAlgorithm on all starting-states
      // from the incomingLineTransitions-list
      for (int j = 0; j < incomingTransitions.size(); j++) {
        State.unmarkStates(originalAutomat.getStates());
        ArrayList<State> markedStates = new ArrayList<State>();
        State transitionStart = incomingTransitions.get(j).getTransitionStart();
        markBackwardsEpsilonTransitionStates(originalAutomat, transitionStart, markedStates);
        
        // Build the transitions and add them to the builtAutomat
        for (int k = 0; k < markedStates.size(); k++) {
          // Built the transition on the states of the builtAutomat and add it
          Transition newTransition = new Transition(
              builtAutomat.getStateByStateIndex(markedStates.get(k).getStateIndex()),
              builtAutomat.getStateByStateIndex(state.getStateIndex()));
          newTransition.addSymbols(incomingTransitions.get(j).getSymbols());
          
          // Add the transition
          builtAutomat.addTransition(newTransition, false);
        }
      }
    }
  }
  
  /** Step 2 from Abbildung 2.2.6 in the script.
   * Change states that can reach End-States with a combination of only epsilon-transitions
   * to End-States as well. */
  private static void toNEANewEndStates(Automat automat) {
    ArrayList<State> markedStates = new ArrayList<State>();
    
    // Iterates over all end-states
    for (int i = 0; i < automat.getStates().size(); i++) {
      State state = automat.getStates().get(i);
      if (state instanceof EndState || state instanceof StartEndState) {
        // Get a list of all states that are backwards (counter-direction of the transition, against
        // its arrow) reachable by only Epsilon-Transitions
        State.unmarkStates(automat.getStates());
        markBackwardsEpsilonTransitionStates(automat, state, markedStates);
      }
    }
    
    // Replace all:
    // normal State -> EndState
    // StartState -> StartEndState
    // Leave EndStates and StartEndStates untouched
    for (int i = 0; i < markedStates.size(); i++) {
      if (markedStates.get(i).getType() == State.START_STATE)
        automat.changeStateType(markedStates.get(i), State.START_END_STATE);
      
      if (markedStates.get(i).getType() == State.STATE)
        automat.changeStateType(markedStates.get(i), State.END_STATE);
    }
  }
  
  /** Recursive method that calls itself for all unmarked neighbor-states of that passed state that
   * can be reached backwards by one Epsilon-Transition. Adds them to the passed ArrayList markedStates.
   * Contains the state on which this method is called as well. */
  private static void markBackwardsEpsilonTransitionStates(Automat automat, State state,
      ArrayList<State> markedStates) {
    state.setMarked(true);
    
    if (!markedStates.contains(state))
      markedStates.add(state);
    
    // Get all backwards reachable Neighbor states by one Epsilon-Transition
    ArrayList<Transition> incomingTransitions = Transition.getTransitionsByEndState(state,
        automat.getTransitions());
    
    for (int i = 0; i < incomingTransitions.size(); i++) {
      // Consider only Epsilon-Transitions
      if (incomingTransitions.get(i).getSymbols().size() <= 0) {
      
        State transitionStart = incomingTransitions.get(i).getTransitionStart();
        if (!transitionStart.isMarked()) {
          markBackwardsEpsilonTransitionStates(automat, transitionStart, markedStates);
        }
      }
    }
  }
}