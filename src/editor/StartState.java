/* 
 * Bachelor Thesis
 * Raphael Bucher
 * November 2016
 * */
package editor;

import java.awt.Graphics2D;

public class StartState extends State {
  public StartState(int drawX, int drawY) {
    super(drawX, drawY);
  }

  // custom paint method which overrides the one from class State
  public void paint(Graphics2D graphics2D) {
    super.paint(graphics2D);

    // the arrow indicating that it's a start state
    graphics2D.drawArc(drawX - Config.STATE_DIAMETER, drawY - Config.STATE_DIAMETER, Config.STATE_DIAMETER,
        Config.STATE_DIAMETER, 225, 45);
  }
}
