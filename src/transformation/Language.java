/* 
 * Editor for Automata
 * Bachelor Thesis
 * Raphael Bucher 2016 / 2017
 * */
package transformation;

import java.util.ArrayList;

import editor.Automat;
import editor.Config;
import editor.EndState;
import editor.ErrorMessage;
import editor.StartEndState;
import editor.State;
import editor.Transition;

/** Used for computation around the language of the automata in form of a regular Expression. */
public class Language {
  /** Currently longest list of symbols read. Contains epsilon-transitions as well. */
  private static ArrayList<ReadSymbol> mostReadSymbols;
  private static boolean wordAccepted;
  
  /** Tests whether a word is accepted by the automat or not. Saves the used transitions
   * in the passed ArrayList. In case multiple solutions are possible to accept the word,
   * only one solution is stored. In case the word is not accepted, it stores to longest
   * combination of transitions the word could travel until the road-block. */
  public static boolean wordAccepted(String word, Automat automat, ArrayList<ReadSymbol> usedTransitionsSymbols) {
    State startState = automat.getStateByStateIndex(0);
    if (startState == null) {
      ErrorMessage.setMessage(Config.ErrorMessages.startStateMissing);
      return false;
    }
    
    // Reset the longest list and its counter
    mostReadSymbols = new ArrayList<ReadSymbol>();
    wordAccepted = false;
    
    ArrayList<ReadSymbol> readSymbols = new ArrayList<ReadSymbol>();
    readSymbol(word, startState, automat, readSymbols);
    
    // Copy the transitions of the longest traveled transitions list
    ReadSymbol.copyListInto(mostReadSymbols, usedTransitionsSymbols);
    
    return wordAccepted;
  }
  
  /** Recursive method that reads a symbol from the passed word.
   * @param state The State from which we search transitions to read the next symbol. */
  private static void readSymbol(String wordSubstring, State state, Automat automat,
      ArrayList<ReadSymbol> readSymbols) {
    if (ReadSymbol.getEffectiveLenght(readSymbols) >= ReadSymbol.getEffectiveLenght(mostReadSymbols) &&
        !wordAccepted) {
      mostReadSymbols = ReadSymbol.copyList(readSymbols);
    }
    
    if (wordSubstring.length() == 0) {
      if (state instanceof EndState || state instanceof StartEndState) {
        wordAccepted = true;
        return;
      }
    }
    
    // Get all reachable states. Includes ArcTransitions
    ArrayList<Transition> outgoingTransitions = Transition.getTransitionsByStartState(state,
        automat.getTransitions());
    
    for (int i = 0; i < outgoingTransitions.size(); i++) {
      Transition transition = outgoingTransitions.get(i);
      
      // Iterate over all transition-symbols, only if there are still Symbols to read
      if (wordSubstring.length() > 0) {
        for (int j = 0; j < transition.getSymbols().size(); j++) {
          // Read an actual Symbol? (No Epsilon)
          if (wordSubstring.charAt(0) == transition.getSymbols().get(j).getSymbol()) {
            readSymbols.add(new ReadSymbol(wordSubstring.charAt(0), transition));
            readSymbol(wordSubstring.substring(1), outgoingTransitions.get(i).getTransitionEnd(),
                automat, readSymbols);
            readSymbols.remove(readSymbols.size() - 1);
          }
        }
      }
      
      // Try the Epsilon-Transition in case it is one (and not an ArcTransition, Epsilon-Arc is useless)
      if (transition.isEpsilonTransition() && !transition.isArcTransition()) {
        // Will the current transition close an cycle with only Epsilon-Transitions? If so,
        // don't allow this since this leads to a stackOverflow. (algorithm doesn't terminate)
        if (closesEpsilonCycle(transition, readSymbols))
          continue;
        
        readSymbols.add(new ReadSymbol(new Character('\u03B5'), transition));
        readSymbol(wordSubstring, outgoingTransitions.get(i).getTransitionEnd(),
            automat, readSymbols);
        readSymbols.remove(readSymbols.size() - 1);
      }
    }
  }
  
  
  // ---------- Original version, before the Epsilon-Transition modifications ---------------
  /*
  private static void readSymbol(String wordSubstring, State state, Automat automat,
      ArrayList<ReadSymbol> readSymbols) {
    if (ReadSymbol.getEffectiveLenght(readSymbols) >= ReadSymbol.getEffectiveLenght(mostReadSymbols) &&
        !wordAccepted) {
      mostReadSymbols = ReadSymbol.copyList(readSymbols);
    }
    
    if (wordSubstring.length() == 0) {
      if (state instanceof EndState || state instanceof StartEndState) {
        wordAccepted = true;
        return;
      }
    }
    
    // Get all reachable states. Includes ArcTransitions
    ArrayList<Transition> outgoingTransitions = Transition.getTransitionsByStartState(state,
        automat.getTransitions());
    
    for (int i = 0; i < outgoingTransitions.size(); i++) {
      Transition transition = outgoingTransitions.get(i);
      // Skip Epsilon-ArcTransitions
      if (transition.isArcTransition() && transition.getSymbols().size() <= 0)
        continue;
      
      if (transition.getSymbols().size() >= 1) {
        // No Epsilon-Transition
        // If the wordSubstring is empty already, don't allow to read a symbol
        if (wordSubstring.length() <= 0)
          continue;
        
        // Iterate over all transition-symbols
        for (int j = 0; j < transition.getSymbols().size(); j++) {
          if (wordSubstring.charAt(0) == transition.getSymbols().get(j).getSymbol()) {
            readSymbols.add(new ReadSymbol(wordSubstring.charAt(0), transition));
            readSymbol(wordSubstring.substring(1), outgoingTransitions.get(i).getTransitionEnd(),
                automat, readSymbols);
            readSymbols.remove(readSymbols.size() - 1);
          }
        }
      } else {
        // Epsilon-Transition
        // Will the current transition close an cycle with only Epsilon-Transitions? If so,
        // don't allow this since this leads to a stackOverflow! (algorithm doesn't terminate)
        if (closesEpsilonCycle(transition, readSymbols))
          continue;
        
        readSymbols.add(new ReadSymbol(null, transition));
        readSymbol(wordSubstring, outgoingTransitions.get(i).getTransitionEnd(),
            automat, readSymbols);
        readSymbols.remove(readSymbols.size() - 1);
      }
    }
  }
  */

  
  
  /** @return true if the passed transition would close a circle of only Epsilon-transitions.
   * Without this check, Epsilon-cycles result in a StackOverflow because the algorithm 
   * doesn't terminate. */
  private static boolean closesEpsilonCycle(Transition transition, ArrayList<ReadSymbol> readSymbols) {
    State cycleStart = transition.getTransitionEnd();
    
    for (int i = readSymbols.size() - 1; i >= 0; i--) {
      if (!readSymbols.get(i).getTraveledTransition().isEpsilonTransition()) {
        // Transition is not an Epsilon-Transition. There is no Epsilon-Cylce
        return false;
      } else {
        // Transition is an Epsilon-Transition. Did the history actually read the Epsilon-Symbol?
        if (readSymbols.get(i).getReadSymbol().charValue() != '\u03B5')
          return false;
        
        // Epsilon-Transition and the Epsilon-Symbol was actually read. Continue the search
        if (readSymbols.get(i).getTraveledTransition().getTransitionStart().equals(cycleStart)) {
          // There is an Epsilon-cycle
          return true;
        }
      }
    }
    
    return false;
  }
}
