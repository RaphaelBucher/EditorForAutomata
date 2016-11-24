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
public class State {
  protected int stateIndex; // indexing it myself, not goind with the ArrayLists indexing
  
  // Coordinates where the state is drawn into the canvas. This is the center of
  // the cirble, not
  // the top-left of the rendered area of the state.
  protected int x, y;

  public State(int stateIndex, int x, int y) {
    this.stateIndex = stateIndex;
    this.x = x;
    this.y = y;
  }

  public void paint(Graphics2D graphics2D) {
    graphics2D.drawOval(x - Config.STATE_DIAMETER / 2, y - Config.STATE_DIAMETER / 2, Config.STATE_DIAMETER,
        Config.STATE_DIAMETER);
    
    // draw the state and its index
    graphics2D.setFont(new Font("Arial", Font.PLAIN, Config.CURSOR_XY / 2));
    graphics2D.drawString("q", x - (int)(Config.CURSOR_XY / 3.5), y + Config.CURSOR_XY / 10);
    
    graphics2D.setFont(new Font("Arial", Font.PLAIN, (int)(Config.CURSOR_XY / 3.2)));
    graphics2D.drawString("" + this.getStateIndex(), x, y + Config.CURSOR_XY / 5);
  }
  
  public int getStateIndex() {
    return this.stateIndex;
  }
}
