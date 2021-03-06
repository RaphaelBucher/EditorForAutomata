/* 
 * Editor for Automata
 * Bachelor Thesis
 * Raphael Bucher 2016 / 2017
 * */
package editor;

import java.awt.Graphics2D;

public class EndState extends State {
  public EndState(int stateIndex, int x, int y) {
    super(stateIndex, x, y);
  }

  // custom paint method which overrides the one from class State
  public void paint(Graphics2D graphics2D) {
    super.paint(graphics2D);

    // draw the second inner oval of an end-state
    graphics2D.drawOval(x - (Config.END_STATE_INNER_DIAMETER / 2), y - (Config.END_STATE_INNER_DIAMETER / 2),
        Config.END_STATE_INNER_DIAMETER, Config.END_STATE_INNER_DIAMETER);
  }
  
  /** Returns a deep copy of the State. */
  public State copy() {
    State state = new EndState(stateIndex, x, y);
    return state;
  }
  
  /** Returns the type of the state, e.g. State.STATE or State.START_STATE. */
  public int getType() {
    return State.END_STATE;
  }
}
