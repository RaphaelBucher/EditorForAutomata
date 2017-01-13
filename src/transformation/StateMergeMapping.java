package transformation;

import editor.State;

public class StateMergeMapping {
  private State oldAutomatState;
  private State newAutomatState;
  
  public StateMergeMapping(State oldAutomatState, State newAutomatState) {
    this.oldAutomatState = oldAutomatState;
    this.newAutomatState = newAutomatState;
  }
  
  // Getters and Setters
  public State getOldAutomatState() {
    return oldAutomatState;
  }
  public void setOldAutomatState(State oldAutomatState) {
    this.oldAutomatState = oldAutomatState;
  }
  public State getNewAutomatState() {
    return newAutomatState;
  }
  public void setNewAutomatState(State newAutomatState) {
    this.newAutomatState = newAutomatState;
  }
}
