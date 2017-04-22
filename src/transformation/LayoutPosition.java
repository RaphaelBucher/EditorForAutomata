/* 
 * Editor for Automata
 * Bachelor Thesis
 * Raphael Bucher 2016 / 2017
 * */
package transformation;

import java.awt.Point;

import editor.State;

/** A position on a LayoutCircle. In cartesian coordinate system. */
public class LayoutPosition {
  protected Point position;
  protected State state;
  
  /** @param angle in Radian */
  public LayoutPosition(double angle, int radius, double initialScaleX) {
    position = new Point();
    
    position.x = (int)(Math.cos(angle) * radius * initialScaleX);
    position.y = (int)(Math.sin(angle) * radius);
  }
  
  public void occupy(State state) {
    this.state = state;
  }
}
