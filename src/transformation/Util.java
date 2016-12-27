package transformation;

import java.util.ArrayList;
import java.util.Collections;
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
    String info = "\n";
    String indentation = "   ";
    
    info += indentation + "yolo";
    
    return info;
  }
}
