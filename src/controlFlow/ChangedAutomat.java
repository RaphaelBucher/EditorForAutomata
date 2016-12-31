package controlFlow;

import editor.Automat;
import editor.Editor;

public class ChangedAutomat extends UserAction {
  private Automat oldAutomat;
  private Automat newAutomat;
  
  public ChangedAutomat(Automat oldAutomat, Automat newAutomat) {
    this.oldAutomat = oldAutomat;
    this.newAutomat = newAutomat;
  }
  
  @Override
  protected void undo() {
    Editor.changeAutonat(oldAutomat, false);
  }

  @Override
  protected void redo() {
    Editor.changeAutonat(newAutomat, false);
  }
  
  /** String representation for the Undo / Redo MenuItem */
  public String toString() {
    return "Layout";
  }
}
