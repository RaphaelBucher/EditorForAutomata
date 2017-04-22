/* 
 * Editor for Automata
 * Bachelor Thesis
 * Raphael Bucher 2016 / 2017
 * */
package transformation;

import java.util.Set;

public class DEATableElement {
  /** The State-Concatenation that is also drawn on the paper if on does the Algorithm himself. 
   * It's the Set of States that are reachable when reading the Symbol of this Object. */
  private Set<Integer> reachedStatesBySymbol;
  
  /** The read symbol, in the first row of the table. */
  private Character symbol;
  
  public DEATableElement(Character symbol) {
    this.symbol = symbol;
  }
  
  // Setters and Getters
  public Character getSymbol() {
    return this.symbol;
  }
  
  public void setReachedStatesBySymbol(Set<Integer> reachedStatesBySymbol) {
    this.reachedStatesBySymbol = reachedStatesBySymbol;
  }
  
  public Set<Integer> getReachedStatesBySymbol() {
    return this.reachedStatesBySymbol;
  }
}
