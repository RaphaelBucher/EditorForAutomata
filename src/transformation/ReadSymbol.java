package transformation;

import java.util.ArrayList;

import editor.EndState;
import editor.StartEndState;
import editor.State;
import editor.Transition;

public class ReadSymbol {
  private Transition traveledTransition;
  
  /** The symbol that was read by traveling the transition. */
  private Character readSymbol;
  
  public ReadSymbol(Character readSymbol, Transition traveledTransition) {
    this.readSymbol = readSymbol;
    this.traveledTransition = traveledTransition;
  }
  
  /** Returns amount of symbols read by filtering out all traveled Epsilon-Transitions. */
  public static int getEffectiveLenght(ArrayList<ReadSymbol> readSymbols) {
    int readSymbolCount = 0;
    
    for (int i = 0; i < readSymbols.size(); i++) {
      // Epsilon-Symbols don't count, rest does
      if (readSymbols.get(i).readSymbol.charValue() != '\u03B5')
        readSymbolCount++;
    }
    
    return readSymbolCount;
  }
  
  /** Returns true if the last transitions ending-state is an EndState or a StartEndState, false otherwise.
   * Returns false for empty lists. */
  public static boolean stopsWithEndState(ArrayList<ReadSymbol> readSymbols) {
    if (readSymbols.size() <= 0)
      return false;
    
    State lastState = readSymbols.get(readSymbols.size() - 1).traveledTransition.getTransitionEnd();
    return lastState instanceof EndState || lastState instanceof StartEndState;
  }
  
  /** Copies the passed list and returns the copy. Returns a new ArrayList with the same
   * references (elements) on ReadSymbol-objects. */
  public static ArrayList<ReadSymbol> copyList(ArrayList<ReadSymbol> readSymbols) {
    ArrayList<ReadSymbol> copy = new ArrayList<ReadSymbol>();
    
    for (int i = 0; i < readSymbols.size(); i++) {
      copy.add(readSymbols.get(i));
    }
    
    return copy;
  }
  
  /** Copies the readSymbols lists transitions into the copyInto-list. */
  public static void copyListInto(ArrayList<ReadSymbol> readSymbols, ArrayList<ReadSymbol> copyInto) {
    for (int i = 0; i < readSymbols.size(); i++) {
      copyInto.add(readSymbols.get(i));
    }
  }
  
  // Getters and Setters
  public Transition getTraveledTransition() {
    return this.traveledTransition;
  }
  
  public Character getReadSymbol() {
    return this.readSymbol;
  }
}
