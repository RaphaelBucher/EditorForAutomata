package editor;

/** A shape of an object of the automat, e.g. a state, a transition, a symbol of a transition.  */
public abstract class Shape {
  protected boolean isSelected;

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
}
