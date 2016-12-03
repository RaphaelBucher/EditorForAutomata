/* 
 * Bachelor Thesis
 * Raphael Bucher
 * November 2016
 * */
package editor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/** A state in an automat. */
public class State extends Shape {
  protected int stateIndex; // indexing it myself, not going with the ArrayLists indexing

  // Coordinates where the state is drawn into the canvas. This is the center of
  // the circle, not the top-left of the rendered area of the state.
  protected int x, y;

  public State(int stateIndex, int x, int y) {
    this.stateIndex = stateIndex;
    this.x = x;
    this.y = y;
  }

  public void paint(Graphics2D graphics2D) {
    // At the start of all paint-methods of State and subclasses
    if (this.isSelected)
      graphics2D.setColor(Config.SELECTED_STATE_COLOR);
    
    graphics2D.drawOval(x - Config.STATE_DIAMETER / 2, y - Config.STATE_DIAMETER / 2, Config.STATE_DIAMETER,
        Config.STATE_DIAMETER);

    // draw the state and its index
    graphics2D.setFont(new Font("Arial", Font.PLAIN, 22));
    graphics2D.drawString("q", x - 14, y + 5);

    graphics2D.setFont(new Font("Arial", Font.PLAIN, 14));
    graphics2D.drawString("" + this.getStateIndex(), x - 2, y + 9);
    
    // At the end of all paint-methods of State and subclasses. Restore default color
    graphics2D.setColor(Color.BLACK);
  }

  public int getStateIndex() {
    return this.stateIndex;
  }
  
  @Override
  public boolean mouseClickHit(int mouseX, int mouseY) {
    int deltaX = mouseX - x;
    int deltaY = mouseY - y;
    int stateRadius = Config.STATE_DIAMETER / 2;
    
    // Simple Pythagoras. Square root not computed for performance reasons.
    if (deltaX * deltaX + deltaY * deltaY <= stateRadius * stateRadius)
      return true;
    
    return false;
  }
  
  @Override
  public void displaySelectedShapeTooltip() {
    Tooltip.setMessage(Config.Tooltips.stateSelected);
  }
  
  // Setters and Getters
  public int getX() {
    return this.x;
  }
  
  public int getY() {
    return this.y;
  }
}
