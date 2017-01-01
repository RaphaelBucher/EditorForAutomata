package controlFlow;

import editor.Automat;
import editor.Editor;

public class ChangedAutomat extends UserAction {
  private Automat oldAutomat;
  private Automat newAutomat;
  private String undoRedoText;
  
  public ChangedAutomat(Automat oldAutomat, Automat newAutomat, String undoRedoText) {
    this.oldAutomat = oldAutomat;
    this.newAutomat = newAutomat;
    this.undoRedoText = undoRedoText;
  }
  
  @Override
  protected void undo() {
    Editor.changeAutonat(oldAutomat, false, "");
  }

  @Override
  protected void redo() {
    Editor.changeAutonat(newAutomat, false, "");
  }
  
  /** String representation for the Undo / Redo MenuItem */
  public String toString() {
    return undoRedoText;
  }
}
