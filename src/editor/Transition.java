package editor;

import java.util.ArrayList;

public class Transition extends Shape {
  private State transitionStart;
  private State transitionEnd;
  // A transition can have one or more symbols
  private ArrayList<Character> symbols;

  public Transition(State transitionStart) {
    this.transitionStart = transitionStart;
    symbols = new ArrayList<Character>();
  }
  
  @Override
  public boolean mouseClickHit(int mouseX, int mouseY) {
    return false;
  }

  @Override
  public void displaySelectedShapeTooltip() {
  }
  
  /** Adds a symbol the the transitions symbol-ArrayList */
  public void addSymbol(char symbol) {
    symbols.add(new Character(symbol));
  }
  
  // Setters and Getters
  public State getTransitionStart() {
    return this.transitionStart;
  }
  
  public void setTransitionEnd(State transitionEnd) {
    this.transitionEnd = transitionEnd;
  }
  
  public State getTransitionEnd() {
    return this.transitionEnd;
  }
  
  public ArrayList<Character> getSymbols() {
    return this.symbols;
  }
}
