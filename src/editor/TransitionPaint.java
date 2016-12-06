package editor;

import java.awt.Graphics2D;
import java.util.ArrayList;

public abstract class TransitionPaint {
  public abstract void paint(Graphics2D graphics2D);
  public abstract void computePaintingCoordinates(ArrayList<Transition> transitions);
  
  // A reference on the Transition which instantiated this TransitionPaintLine-Object
  protected Transition aggregateTransition;
  
  /** Computes the transitions angle of the given Triangles Cathetes. Pass the deltaY-value
   * in the cartesian coordinate system not in Swings. Returns a value between 0 and 2 * Math.PI */
  public static double computeAngle(int deltaX, int deltaY) {
    // The hypotenuse
    double c = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

    // Sufficient if the vector is in quadrants 1 or 2
    double angle = Math.acos(deltaX / c);
    
    // Make result valid for 360° and not only for 180° (quadrants 1 and 2)
    if (deltaY < 0)
      angle = Math.PI * 2 - angle;
    
    return angle;
  }
}
