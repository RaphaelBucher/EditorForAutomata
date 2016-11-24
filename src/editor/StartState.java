/* 
 * Bachelor Thesis
 * Raphael Bucher
 * November 2016
 * */
package editor;

import java.awt.Graphics2D;

public class StartState extends State {
  public StartState(int stateIndex, int x, int y) {
    super(stateIndex, x, y);
  }

  // custom paint method which overrides the one from class State
  public void paint(Graphics2D graphics2D) {
    super.paint(graphics2D);

    // the arrow indicating that it's a start state
    graphics2D.drawArc(x - Config.STATE_DIAMETER, y - Config.STATE_DIAMETER, Config.STATE_DIAMETER,
        Config.STATE_DIAMETER, 225, 45);
    
    // the arrow
    graphics2D.drawLine(x - Config.STATE_DIAMETER / 2, y, x - Config.STATE_DIAMETER / 2 - 8, y - 7);
    graphics2D.drawLine(x - Config.STATE_DIAMETER / 2, y, x - Config.STATE_DIAMETER / 2 - 8, y + 5);
  }
}
