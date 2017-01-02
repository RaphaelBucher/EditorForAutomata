package transformation;

import java.util.ArrayList;

import editor.Automat;
import editor.EndState;
import editor.StartEndState;
import editor.State;
import editor.Transition;

/** Used for the automata transformation algorithms
 * Epsilon-automata -> NEA -> DEA -> minimal DEA. */
public class Transformation {
  
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
