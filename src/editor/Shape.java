/* 
 * Editor for Automata
 * Bachelor Thesis
 * Raphael Bucher 2016 / 2017
 * */
package editor;

/** A shape of an object of the automat, e.g. a state, a transition, a symbol of a transition.  */
public abstract class Shape {
  /** The flag for selecting shapes, e.g. for shape-deletion or transition-constructing. */
  protected boolean isSelected;
  
  /** The flag for the word accepted animation to highlight the path of states and transition-symbols
   * the word traveled when it was tested for acceptance. */
  protected boolean wordAcceptedPath;

  /** When the default arrow-cursor is active (user has no ToggleButton selected in the ToolBar at the left
   * side) and the user does a mouse-click, did the click hit the Shape? */
  abstract public boolean mouseClickHit(int mouseX, int mouseY);
  
  /** Display the appropriate Tooltip when the user selects a shape. */
  abstract public void displaySelectedShapeTooltip(); 
  
  // Getters and Setters
  public boolean isSelected() {
    return isSelected;
  }

  public void setSelected(boolean isSelected) {
    this.isSelected = isSelected;
  }
  
  public boolean isWordAcceptedPath() {
    return wordAcceptedPath;
  }

  public void setWordAcceptedPath(boolean wordAcceptedPath) {
    this.wordAcceptedPath = wordAcceptedPath;
  }
}
