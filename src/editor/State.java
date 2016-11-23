/* 
 * Bachelor Thesis
 * Raphael Bucher
 * November 2016
 * */
package editor;

import java.awt.Graphics2D;

/** A state in an automat. */
public class State {
  // Coordinates where the state is drawn into the canvas. This is the center of
  // the cirble, not
  // the top-left of the rendered area of the state.
  protected int drawX, drawY;

  public State(int drawX, int drawY) {
    this.drawX = drawX;
    this.drawY = drawY;
  }

  public void paint(Graphics2D graphics2D) {
    graphics2D.drawOval(drawX - Config.STATE_DIAMETER / 2, drawY - Config.STATE_DIAMETER / 2, Config.STATE_DIAMETER,
        Config.STATE_DIAMETER);
  }

}
