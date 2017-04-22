/* 
 * Editor for Automata
 * Bachelor Thesis
 * Raphael Bucher 2016 / 2017
 * */
package transformation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import editor.Automat;
import editor.EndState;
import editor.StartEndState;
import editor.StartState;
import editor.State;
import editor.Transition;

/** Used for the automata transformation algorithms
 * Epsilon-automata -> NEA -> DEA -> minimal DEA. */
public class Transformation {
  private static ArrayList<DEATableRow> toDEATableRows;
  
  /** Returns a minimal DEA if the passed automat wasn't a minimal DEA already. Returns null
   * if the automat was already a minimal DEA. The passed automat isn't changed, one can
   * pass the Editors current automat, this method copies the automat itself for the 
   * Algorithm. */
  public static Automat transformToMinimalDEA(Automat automat) {
    // control-flag to keep track whether the passed automat was a min. DEA already
    boolean wasMinDEA = true;
    
    // Create a DEA without unreachable states so we can start the minimal-DEA Algorithm
    Automat automatDeepCopy = automat.copy();
    Automat newDEA = Transformation.transformToDEA(automatDeepCopy);
    if (!newDEA.equals(automatDeepCopy))
      wasMinDEA = false; // automat wasn't a DEA
    
    if (Util.deleteUnreachableStates(newDEA, newDEA.getStateByStateIndex(0)))
      wasMinDEA = false; // The automat had anreachable states and they were deleted
    
    Automat minimalDEA = getMinimalDEA(newDEA);
    if (!minimalDEA.equals(newDEA))
      wasMinDEA = false; // Automat had equivalent states to be reduced
    
    if (wasMinDEA)
      return null;
    else
      return minimalDEA;
  }
  
  /** Returns a new minimal DEA in case the passed automat wasn't a minimal DEA already. Returns the
   * same Automat if it was already a minimal DEA (had no equivalent states to be merged by the
   * reduction-Algorithm). */
  private static Automat getMinimalDEA(Automat automat) {
    // passed automat is a DEA without unreachable states
    int statesCount = automat.getStates().size();
    // true means the state-pair (by indices) is unequal (marked), false means their equal
    boolean[][] equivalenceTable = new boolean[statesCount][statesCount];
    
    // The marking before the first iteration of the main loop
    toMinDEABaseMarking(automat, equivalenceTable);
    
    // The iteration marking (2nd step of the Algorithm of Abb. 2.3.1)
    ArrayList<Character> alphabet = Util.getAlphabet(automat);
    while (toMinDEAIterationMarking(automat, equivalenceTable, alphabet));
    
    // Return the same automat if no equivalent states were found (automat was already a minimal DEA)
    if (!hasEquivalentStates(equivalenceTable))
      return automat;
    
    Automat newMinimalDEA = new Automat();
    // Map equivalent states to the same state of the newly created minimal DEA.
    // Instantiates and adds also the new States of the created minimal DEA
    ArrayList<StateMergeMapping> mapping = toMinDEAStateMerging(automat, newMinimalDEA, equivalenceTable);
    
    // Transfer the Transitions from the old Automat to the new minimal DEA, according to the
    // State-mapping
    toMinDEATransferTransitions(automat, newMinimalDEA, mapping);
    
    return newMinimalDEA;
  }
  
  private static void toMinDEATransferTransitions(Automat oldAutomat, 
      Automat newMinimalDEA, ArrayList<StateMergeMapping> mapping) {
    // Iterate over the old Automats transitions
    for (int i = 0; i < oldAutomat.getTransitions().size(); i++) {
      Transition oldTransition = oldAutomat.getTransitions().get(i);
      
      // Get the mapped states for the new Transition
      State starting = getMappedState(oldTransition.getTransitionStart(), mapping);
      State ending = getMappedState(oldTransition.getTransitionEnd(), mapping);
      
      // Create the Transition
      Transition newTransition = new Transition(starting, ending);
      newTransition.addSymbols(oldTransition.getSymbols());
      
      // Add the built Transition to the Automat
      newMinimalDEA.addTransitionNoPaintingUpdate(newTransition);
    }
  }
  
  /** Traverses the passed mapping-list and returns the mapped state of the passed oldAutomatState.
   * Returns null if the state was not in the list. */
  private static State getMappedState(State oldAutomatState, ArrayList<StateMergeMapping> mapping) {
    for (int i = 0; i < mapping.size(); i++) {
      if (oldAutomatState.equals(mapping.get(i).getOldAutomatState()))
        return mapping.get(i).getNewAutomatState();
    }
    
    return null;
  }
  
  /** Creates the mapping of the states from the old to the new minimal DEA. */
  private static ArrayList<StateMergeMapping> toMinDEAStateMerging(Automat oldAutomat,
      Automat newMinimalDEA, boolean[][] equivalenceTable) {
    ArrayList<StateMergeMapping> mapping = new ArrayList<StateMergeMapping>();
    
    // Map the Start-state
    State startState = oldAutomat.getStateByStateIndex(0);
    if (startState.isEndState()) {
      newMinimalDEA.addState(new StartEndState(0, 1, 1), false);
    } else {
      newMinimalDEA.addState(new StartState(0, 1, 1), false);
    }
    mapping.add(new StateMergeMapping(oldAutomat.getStateByStateIndex(0), newMinimalDEA.getStateByStateIndex(0)));
    
    // Iterate over all states except the Start-state
    for (int i = 0; i < oldAutomat.getStates().size(); i++) {
      State state = oldAutomat.getStates().get(i);
      // Skip the start-state
      if (state.getStateIndex() == 0)
        continue;
      
      // Add the state to the mapping
      State newState = getNewEquivalentState(state, oldAutomat, newMinimalDEA, mapping, equivalenceTable);
      mapping.add(new StateMergeMapping(state, newState));
      
      // Add the State to the new minimal DEA
      if (newMinimalDEA.getStateByStateIndex(newState.getStateIndex()) == null)
        newMinimalDEA.addState(newState, false);
    }
    
    return mapping;
  }
  
  private static State getNewEquivalentState(State oldState, Automat oldAutomat, Automat newMinimalDEA,
      ArrayList<StateMergeMapping> mapping, boolean[][] equivalenceTable) {
    // Search for an equivalent state in the mapping-list
    for (int i = 0; i < mapping.size(); i++) {
      // Is the state-pair unmarked? (equivalent)
      if (!isStatePairMarked(oldState, mapping.get(i).getOldAutomatState(), equivalenceTable, oldAutomat)) {
        return mapping.get(i).getNewAutomatState();
      }
    }
    
    // The state isn't equivalent to any other state that is already in the mapping-list
    if (oldState.isEndState())
      return new EndState(newMinimalDEA.findNewStateIndex(), 1, 1);
    else
      return new State(newMinimalDEA.findNewStateIndex(), 1, 1);
  }
  
  private static boolean hasEquivalentStates(boolean[][] equivalenceTable) {
    for (int i = 0; i < equivalenceTable.length - 1; i++) {
      for (int j = i + 1; j < equivalenceTable.length; j++) {
        if (!equivalenceTable[i][j])
          return true;
      }
    }
    
    return false;
  }
  
  /** Performas the marking of MinDEATableElements-Array in the iteration loop of the Algorithm of
   * Abb. 2.3.1. in the Script. */
  private static boolean toMinDEAIterationMarking(Automat automat, boolean[][] equivalenceTable,
      ArrayList<Character> alphabet) {
    // Control-flag that is used for algorithm-termination
    boolean markedSomething = false;
    // The ending-States that were reached by reading the same Symbol
    State reachedFromP;
    State reachedFromQ;
    
    // i: Row
    for (int i = 0; i < equivalenceTable.length - 1; i++) {
      // j: Column
      for (int j = i + 1; j < equivalenceTable.length; j++) {
        // Consider only unmarked State-pairs
        if (!equivalenceTable[i][j]) {
          State p = automat.getStates().get(i);
          State q = automat.getStates().get(j);
          
          // Iterate over the alphabet
          for (int k = 0; k < alphabet.size(); k++) {
            // Get a list of all Transitions (with their Ending-States) that can be reached
            // by reading the passed Symbol once from the passed State. Since the base automat
            // must be a DEA by constraints, only one Transitions will be returned here
            reachedFromP = Transition.getTransitionsByStartStateAndSymbol(p, alphabet.get(k),
                automat.getTransitions()).get(0).getTransitionEnd();
            reachedFromQ = Transition.getTransitionsByStartStateAndSymbol(q, alphabet.get(k),
                automat.getTransitions()).get(0).getTransitionEnd();
            
            if (isStatePairMarked(reachedFromP, reachedFromQ, equivalenceTable, automat)) {
              // Mark the pair (p, q)
              equivalenceTable[i][j] = true;  
              markedSomething = true;
            }
          }
        }
      }
    }
    
    return markedSomething;
  }
  
  private static boolean isStatePairMarked(State p, State q,
      boolean[][] equivalenceTable, Automat automat) {
    int indexP = automat.getStates().indexOf(p);
    int indexQ = automat.getStates().indexOf(q);
    
    // Both orders of the states need to be checked since (p, q) is 
    // the same like (q, p) for the Algorithm
    return equivalenceTable[indexP][indexQ] || equivalenceTable[indexQ][indexP];
  }
  
  /** Performas the marking of MinDEATableElements-Array in the first step of the Algorithm of Abb. 2.3.1.
   * in the Script. */
  private static void toMinDEABaseMarking(Automat automat, boolean[][] equivalenceTable) {
    // i: Row
    for (int i = 0; i < equivalenceTable.length - 1; i++) {
      // j: Column
      for (int j = i + 1; j < equivalenceTable.length; j++) {
        State p = automat.getStates().get(i);
        State q = automat.getStates().get(j);
        
        // Is one state an EndState and the other not? If so, mark them
        if (p.isEndState() && !q.isEndState() || !p.isEndState() && q.isEndState())
          equivalenceTable[i][j] = true;
      }
    }
  }
  
  // Debugging
  public static void printEquiTable(boolean[][] equivalenceTable, Automat automat) {
    for (int i = 0; i < equivalenceTable.length; i++) {
      System.out.println("ArrayListIndex " + i + " has stateIndex " + automat.getStates().get(i).getStateIndex());
    }
    System.out.println("--------------------------------");
    
    for (int i = 0; i < equivalenceTable.length; i++) {
      for (int j = 0; j < equivalenceTable.length; j++) {
        System.out.print("" + automat.getStates().get(i).getStateIndex() +
            " " + automat.getStates().get(j).getStateIndex() + " " + equivalenceTable[i][j] + "   ");
      }
      System.out.println();
    }
    
    System.out.println("--------------------------------");
    System.out.println();
  }
  
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
   * Removes all Epsilon Transitions from the Automat and replaces Epsilon-chains with a 
   * non-Epsilon-symbol in the end with a new transition according to the script. */
  private static void toNEARemoveEpsilonTransitions(Automat originalAutomat, Automat builtAutomat) {
    for (int i = 0; i < originalAutomat.getStates().size(); i++) {
      State state = originalAutomat.getStates().get(i);
      
      // Gather all incoming transitions
      ArrayList<Transition> incomingTransitions = Transition.getTransitionsByEndState(
          state, originalAutomat.getTransitions());
      
      // filter out pure Epsilon-Transitions (Transitions with an Epsilon-Symbol only)
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
          
          // Ignore Epsilon-Symbols. Can't delete since their needed for the rest of the computation
          newTransition.removeSymbol('\u03B5', false);
          
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
      if (incomingTransitions.get(i).isEpsilonTransition()) {
      
        State transitionStart = incomingTransitions.get(i).getTransitionStart();
        if (!transitionStart.isMarked()) {
          markBackwardsEpsilonTransitionStates(automat, transitionStart, markedStates);
        }
      }
    }
  }
}