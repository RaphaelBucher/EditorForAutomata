/* 
 * Editor for Automata
 * Bachelor Thesis
 * Raphael Bucher 2016 / 2017
 * */
package controlFlow;

import java.awt.Point;

import editor.Automat;
import editor.Editor;

public class StateMoved extends UserAction {
  private int stateIndex;
  private Point oldPosition;
  private Point newPosition;
  
  public StateMoved(int stateIndex, Point oldPosition, Point newPosition) {
    this.stateIndex = stateIndex;
    this.oldPosition = oldPosition;
    this.newPosition = newPosition;
  }

  @Override
  protected void undo() {
    Automat automat = Editor.getDrawablePanel().getAutomat();
    automat.getStateByStateIndex(stateIndex).moveTo(oldPosition.x, oldPosition.y);
    
    // Reset the states transitions
    automat.updateStateTransitions(automat.getStateByStateIndex(stateIndex));
  }

  @Override
  protected void redo() {
    Automat automat = Editor.getDrawablePanel().getAutomat();
    automat.getStateByStateIndex(stateIndex).moveTo(newPosition.x, newPosition.y);
    
    // Reset the states transitions
    automat.updateStateTransitions(automat.getStateByStateIndex(stateIndex));
  }
  
  /** String representation for the Undo / Redo MenuItem */
  public String toString() {
    return "State Moving";
  }
}
