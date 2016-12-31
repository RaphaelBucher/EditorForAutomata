package transformation;

import java.awt.Point;

/** A position on a LayoutCircle. In cartesian coordinate system. */
public class LayoutPosition {
  protected Point position;
  protected boolean taken;
  /** In a full circle (statesAmount == circleSlotPlaces), all elements are active. If the states-list
   * has less elements than the circleSlots, then not all will be active and takable. */
  protected boolean active;
  /** @param angle in Radian */
  public LayoutPosition(double angle, int radius) {
    position = new Point();
    
    double initialScaleX = 1.5d;
    position.x = (int)(Math.cos(angle) * radius * initialScaleX);
    position.y = (int)(Math.sin(angle) * radius);
  }
}
