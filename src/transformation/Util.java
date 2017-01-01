package transformation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import editor.Automat;
import editor.State;
import editor.Symbol;
import editor.Transition;

public class Util {
  /** Every automat is an Epsilon-Automat (NEAs and DEAs are just Epsilon-Automata without Epsilon-Transitions).
   * A NEA is an automat without Epsilon-Transitions. */
  public static boolean isNEA(Automat automat) {
    for (int i = 0; i < automat.getTransitions().size(); i++) {
      if (automat.getTransitions().get(i).getSymbols().size() <= 0)
        return false;
    }
    
    return true;
  }
  
  /** Computes whether the passed automat is a DEA. */
  public static boolean isDEA(Automat automat) {
    // Make its a NEA (no Epsilon-Transitions)
    if (!isNEA(automat))
      return false;
    
    // Grab the automats alphabet
    ArrayList<Character> alphabet = getAlphabet(automat);
    
    // Iterate over all states
    for (int i = 0; i < automat.getStates().size(); i++) {
      State state = automat.getStates().get(i);
      
      // Grab all symbols of all outgoing Transitions from this state, duplicates enabled by default
      ArrayList<Character> outgoingSymbols = getStateOutgoingSymbolsDuplicates(automat, state);
     
      for (int j = 0; j < alphabet.size(); j++) {
        if (Collections.frequency(outgoingSymbols, alphabet.get(j)) != 1)
          return false;
      }
    }
    
    return true;
  }
  
  /** Iterates over all the automats transitions that have the passed state as a startState and
   * puts the transitions symbols into the returned ArrayList. Can contain duplicates.  */
  public static ArrayList<Character> getStateOutgoingSymbolsDuplicates(Automat automat, State state) {
    ArrayList<Character> symbols = new ArrayList<Character>();
    
    // Grab all outgoing transitions from the current state
    ArrayList<Transition> outgoingTransitions =
        Transition.getTransitionsByStartState(state, automat.getTransitions());
    
    // Put all symbols of all outgoing Transitions into an ArrayList, duplications enabled by default
    for (int i = 0; i < outgoingTransitions.size(); i++) {
      for (int j = 0; j < outgoingTransitions.get(i).getSymbols().size(); j++) {
        symbols.add(outgoingTransitions.get(i).getSymbols().get(j).getSymbol());
      }
    }
    
    return symbols;
  }
  
  /** Iterates over all the automats transitions that have the passed state as a startState and
   * puts the transitions symbols into the returned ArrayList. Doesn't contain duplicates  */
  public static ArrayList<Character> getStateOutgoingSymbolsNoDuplicates(Automat automat, State state) {
    ArrayList<Character> symbols = getStateOutgoingSymbolsDuplicates(automat, state);
    
    // Remove duplicates
    Set<Character> symbolsNoDuplicates = new HashSet<Character>();
    symbolsNoDuplicates.addAll(symbols);
    symbols.clear();
    symbols.addAll(symbolsNoDuplicates);
    
    return symbols;
  }
  
  
  /** @return All symbols that the automat contains. */
  public static ArrayList<Character> getAlphabet(Automat automat) {
    ArrayList<Character> symbols = new ArrayList<Character>();
    
    for (int i = 0; i < automat.getTransitions().size(); i++) {
      ArrayList<Symbol> transitionSymbols = automat.getTransitions().get(i).getSymbols();
      
      for (int j = 0; j < transitionSymbols.size(); j++) {
        if (!symbols.contains(transitionSymbols.get(j).getSymbol()))
          symbols.add(new Character(transitionSymbols.get(j).getSymbol()));
      }
    }
    
    return symbols;
  }
  
  /** A String representation of the automat, containing information about its type (NEA, DEA...) and
   * the formal definition of the automat (The sets of the states, input alphabet...) */
  public static String automatInfo(Automat automat) {
    String info = "";
    
    // Append the type information
    info = automatInfoAddTypes(automat, info);
    
    // Automat Definition
    info = automatInfoAddDefinition(automat, info);
    
    return info;
  }
  
  /** Appends the infos about the automat-type to the info-String. */
  private static String automatInfoAddTypes(Automat automat, String info) {
    info += "------ Types ------\n";
    
    // All EAs are Epsilon-automata
    info += "\u03B5-Automat: yes\n";
    
    // NEA
    info += "Nichtdeterministischer endlicher Automat (NEA): ";
    info += isNEA(automat) ? "yes" : "no";
    info += "\n";
    
    // DEA
    info += "Deterministischer endlicher Automat (DEA): ";
    info += isDEA(automat) ? "yes" : "no";
    info += "\n";
    
    // minimal DEA
    info += "Minimaler Deterministischer endlicher Automat: todo todo todo";
    // TODO wahrscheinlich den minimal DEA Algorithmus anwerfen und gucken ob sich was verändert
    // hat (weniger Zustände)
    info += "\n";
    info += "\n";
    
    return info;
  }
  
  /** Appends the Definition of the automat to the info-String. */
  private static String automatInfoAddDefinition(Automat automat, String info) {
    info += "------ Definition ------\n";
    
    info += "E = (Q, \u03A3, \u03B4, q\u2080, F) with\n";
    
    // Append the String representation of the set of states
    info = automatInfoApendStates(automat, info);
    
    // Append the String representation of the input alphabet
    info = automatInfoApendAlphabet(automat, info);
    
    // The Set of Endstates
    info += "F = {" + toOrderedString(automat.getEndStates()) + "}\n";
    
    // The transfer-function
    info = appendTransferFunction(automat, info);
    
    return info;
  }
  
  /** Appends the String representation of the automats transfer function to the passed String. */
  private static String appendTransferFunction(Automat automat, String info) {
    // Work on a copy of the automats original states-ArrayList
    ArrayList<State> sortedStates = copyStates(automat.getStates());
    sortStates(sortedStates);

    for (int i = 0; i < sortedStates.size(); i++) {
      ArrayList<Transition> outgoingTransitions = Transition.getTransitionsByStartState(
          sortedStates.get(i), automat.getTransitions());
      
      for (int j = 0; j < outgoingTransitions.size(); j++) {
        ArrayList<Symbol> symbols = outgoingTransitions.get(j).getSymbols();
        
        if (symbols.size() <= 0) {
          // Epsilon-Transition
          info += "\u03B4(" + subscript("q" + sortedStates.get(i).getStateIndex()) + ", \u03B5) = " +
              subscript("q" + outgoingTransitions.get(j).getTransitionEnd().getStateIndex()) + "    ";
        } else {
          for (int k = 0; k < symbols.size(); k++) {
            info += "\u03B4(" + subscript("q" + sortedStates.get(i).getStateIndex()) + ", " +
                symbols.get(k).getSymbol() + ") = " +
                subscript("q" + outgoingTransitions.get(j).getTransitionEnd().getStateIndex()) + "    ";
          }
        }
      }
      
      if (outgoingTransitions.size() >= 1)
        info += "\n";
    }
    
    return info;
  }
  
  /** @return a copy of the passed ArrayList. The states itself are not copied, they use the
   * same reference. */
  private static ArrayList<State> copyStates(ArrayList<State> states) {
    ArrayList<State> statesCopy = new ArrayList<State>();
    
    for (int i = 0; i < states.size(); i++) {
      statesCopy.add(states.get(i));
    }
    
    return statesCopy;
  }
  
  /** Appends the set of states of the automat to the info-String. */
  private static String automatInfoApendStates(Automat automat, String info) {
    String statesString = toOrderedString(automat.getStates());
    info += "Q = {" + statesString + "}\n";
    
    return info;
  }
  
  /** Returns an ordered String representation of the passed states, e.g. "q0, q1, q3" */
  private static String toOrderedString(ArrayList<State> states) {
    String statesString = "";
    ArrayList<Integer> stateIndices = new ArrayList<Integer>();
    for (int i = 0; i < states.size(); i++) {
      stateIndices.add(states.get(i).getStateIndex());
    }
    
    // Sort the list
    Collections.sort(stateIndices, new Comparator<Integer>() {
      @Override
      public int compare(Integer int1, Integer int2)
      {
        return int1.compareTo(int2);
      }
    });
    
    for (int i = 0; i < stateIndices.size(); i++) {
      String state = "q" + stateIndices.get(i);
      statesString += subscript(state);
      
      if (i < stateIndices.size() - 1)
        statesString += ", ";
    }
    
    return statesString;
  }
  
  /** Replaces all numbers in the passed string with their subscript version. */
  private static String subscript(String str) {
    str = str.replaceAll("0", "\u2080");
    str = str.replaceAll("1", "\u2081");
    str = str.replaceAll("2", "\u2082");
    str = str.replaceAll("3", "\u2083");
    str = str.replaceAll("4", "\u2084");
    str = str.replaceAll("5", "\u2085");
    str = str.replaceAll("6", "\u2086");
    str = str.replaceAll("7", "\u2087");
    str = str.replaceAll("8", "\u2088");
    str = str.replaceAll("9", "\u2089");
    return str;
  }
  
  /** Appends the of the automat to the info-String. */
  private static String automatInfoApendAlphabet(Automat automat, String info) {
    info += "\u03A3 = {";
    
    ArrayList<Character> alphabetList = getAlphabet(automat);
    Collections.sort(alphabetList, new Comparator<Character>() {
      @Override
      public int compare(Character char1, Character char2)
      {
        return char1.compareTo(char2);
      }
    });
    
    // Add the Epsilon-sign in case there's an Epsilon-Transition
    if (!isNEA(automat)) {
      info += "\u03B5";
      
      info += alphabetList.size() >= 1 ? ", " : "";
    }
    
    String alphabet = alphabetList.toString();
    alphabet = alphabet.substring(1, alphabet.length() - 1);
    info += alphabet;
    
    info += "}\n";
    
    return info;
  }
  
  /** Sorts the passed states-list according to their stateIndices. */
  private static void sortStates(ArrayList<State> states) {
    // Sort the list
    Collections.sort(states, new Comparator<State>() {
      @Override
      public int compare(State state1, State state2)
      {
        return new Integer(state1.getStateIndex()).compareTo(state2.getStateIndex());
      }
    });
  }
  
  
  /** Returns an ArrayList of all States that are reachable by moving along 
   * transitions (only in their direction!). Includes the passed starting state as well.
   * Uses recursion. The startingState can be any state of the automat, not just the real
   * StartState of the automat. */
  public static ArrayList<State> getReachableStates(Automat automat, State startingState) {
    ArrayList<State> states = new ArrayList<State>();
    State.unmarkStates(automat.getStates());
    
    // If the startingState is null, no state can be reached and all states of the automat will be removed
    // This can occur if the user removes unreachable states of an automat without a starting state.
    if (startingState != null)
      markReachableNeighborStates(automat, startingState);
    
    // Add all marked (reachable) states to the list
    for (int i = 0; i < automat.getStates().size(); i++) {
      State state = automat.getStates().get(i);
      if (state.isMarked())
        states.add(state);
    }
    
    return states;
  }
  
  /** Returns an ArrayList of all States that are unreachable by moving along 
   * transitions (only in their direction!). Uses recursion. The startingState can
   * be any state of the automat, not just the real StartState of the automat. */
  public static ArrayList<State> getUnreachableStates(Automat automat, State startingState) {
    ArrayList<State> reachableStates = getReachableStates(automat, startingState);
    
    ArrayList<State> unreachableStates = setComplement(reachableStates, automat.getStates());
    
    return unreachableStates;
  }
  
  /** Returns all states that are in the superSet but not in the complementFrom-set.
   * Compares them be stateIndices only, not be their references. */
  public static ArrayList<State> setComplement(ArrayList<State> complementFrom, ArrayList<State> superSet) {
    ArrayList<State> complement = new ArrayList<State>();
    
    for (int i = 0; i < superSet.size(); i++) {
      if (State.getStateByStateIndex(superSet.get(i).getStateIndex(), complementFrom) == null) {
        complement.add(superSet.get(i));
      }
    }
    
    return complement;
  }
  
  /** Deletes all States that are unreachable by moving along 
   * transitions (only in their direction!). Uses recursion. The startingState can
   * be any state of the automat, not just the real StartState of the automat. */
  public static void deleteUnreachableStates(Automat automat, State startingState) {
    ArrayList<State> unreachableStates = getUnreachableStates(automat, startingState);
    
    automat.deleteStates(unreachableStates, false);
  }
  
  /** Recursive method that calls itself for all unmarked neighbor-states of that passed state that
   * can be reached by one transition. */
  private static void markReachableNeighborStates(Automat automat, State state) {
    state.setMarked(true);
    
    // Get all reachable Neighbor states
    ArrayList<Transition> outgoingTransitions = Transition.getTransitionsByStartState(state,
        automat.getTransitions());
    
    for (int i = 0; i < outgoingTransitions.size(); i++) {
      if (!outgoingTransitions.get(i).getTransitionEnd().isMarked()) {
        markReachableNeighborStates(automat, outgoingTransitions.get(i).getTransitionEnd());
      }
    }
  }
}


