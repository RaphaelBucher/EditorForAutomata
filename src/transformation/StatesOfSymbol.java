package transformation;

import java.util.HashSet;
import java.util.Set;

/** Helper-class for the Transfer-function of the Automaton-Info. Stores all reached state-indices
 * of a read Symbol from a state. */
public class StatesOfSymbol {
  private char symbol;
  private Set<Integer> reachedStateIndices;
  
  public StatesOfSymbol(char symbol, int stateIndex) {
    this.symbol = symbol;
    reachedStateIndices = new HashSet<Integer>();
    reachedStateIndices.add(stateIndex);
  }
  
  public void addStateIndex(int stateIndex) {
    reachedStateIndices.add(stateIndex);
  }
  
  public static StatesOfSymbol getObjectBySymbol(Set<StatesOfSymbol> objects, char symbol) {
    for (StatesOfSymbol statesOfSymbol : objects) {
      if (statesOfSymbol.symbol == symbol)
        return statesOfSymbol;
    }
    
    return null;
  }
  
  public String toString(boolean isDEA) {
    String statesAsSet = "";
    if (!isDEA)
      statesAsSet += "{";
    
    for (Integer index : reachedStateIndices) {
      statesAsSet += Util.subscript("q" + index) + ", ";
    }
    statesAsSet = statesAsSet.substring(0,  statesAsSet.length() - 2);
    if (!isDEA)
      statesAsSet += "}";
    
    return statesAsSet;
  }
  
  // Setters and Getters
  public char getSymbol() {
    return this.symbol;
  }
}
