/* 
 * Bachelor Thesis
 * Raphael Bucher
 * November 2016
 * */
package editor;

import java.awt.Graphics2D;

public class StartEndState extends State {
  public StartEndState(int drawX, int drawY) {
    super(drawX, drawY);
  }

  // custom paint method which overrides the one from class State
  public void paint(Graphics2D graphics2D) {
    super.paint(graphics2D);

    // draw the second inner oval of an end-state
    graphics2D.drawOval(drawX - (Config.END_STATE_INNER_DIAMETER / 2), drawY - (Config.END_STATE_INNER_DIAMETER / 2),
        Config.END_STATE_INNER_DIAMETER, Config.END_STATE_INNER_DIAMETER);

    // the arrow indicating that it's a start state
    graphics2D.drawArc(drawX - Config.STATE_DIAMETER, drawY - Config.STATE_DIAMETER, Config.STATE_DIAMETER,
        Config.STATE_DIAMETER, 225, 45);
  }
}
