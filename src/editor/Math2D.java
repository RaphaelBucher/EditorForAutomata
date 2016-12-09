package editor;

import java.awt.Point;

public class Math2D {
  /** How far the mouseClick can be away from the transitions Line or Arc */
  private static final double clickTolerance = 6.0d;
  
  public static boolean lineTransitionClicked(Point mousePosition, Point transitionStart,
      Point transitionEnd) {
    // Create the needed vectors in Cartesian coordinate system
    Vector2D mouse = new Vector2D(mousePosition.x, - mousePosition.y);
    // Straight line start point
    Vector2D r1 = new Vector2D(transitionStart.x, - transitionStart.y);
    // Straight line direction vector
    Vector2D a = Vector2D.subtract(new Vector2D(transitionEnd.x, - transitionEnd.y), r1);
    
    double distanceToLine = pointDistanceToLine(mouse, r1, a);
    
    double distanceToTransitionMiddle = Vector2D.distance(mouse, 
        new Vector2D(r1.x + a.x / 2.0d, r1.y + a.y / 2.0d));
    double transitionLength = a.absolute();
    
    return distanceToLine < clickTolerance && distanceToTransitionMiddle < transitionLength / 2.0d; 
  }
  
  /** Computes the distance of a point to a straight line. */
  private static double pointDistanceToLine(Vector2D mouse, Vector2D r1, Vector2D a) {
    double numerator = Math.abs(Vector2D.det(a, Vector2D.subtract(mouse, r1)));
    double denominator = a.absolute();
    
    return numerator / denominator;
  }
  
  /** arcAngle is between 0 and 2 * Math.PI, its the angle of the vector from the Arcs
   * hostState to the Arcs Center. Arc radius is Config.STATE_DIAMETER / 2. */
  public static boolean arcTransitionClicked(Point mousePosition, Point arcCenter, double arcAngle) {
    Vector2D mouse = new Vector2D(mousePosition.x, - mousePosition.y);
    Vector2D arc = new Vector2D(arcCenter.x, - arcCenter.y);
    
    double distance = Vector2D.distance(mouse, arc);
    double stateRadius = Config.STATE_DIAMETER / 2;
    
    // The angle of the mouse in relation to the Arcs Center
    double mouseAngle = Vector2D.computeAngle(mouse.x - arc.x, mouse.y - arc.y);
    double reverseArcAngle = arcAngle + Math.PI; // ReverseArcAngle is between PI and 3 * PI
    // Ensure that the angles are periodically as close as possible for easy comparison
    if (reverseArcAngle - mouseAngle > Math.PI)
      mouseAngle += 2 * Math.PI;
    
    boolean ret = distance > stateRadius - clickTolerance && distance < stateRadius + clickTolerance &&
        Math.toDegrees(Math.abs(mouseAngle - reverseArcAngle)) > 45.0d;
    
    return ret;
  }
}
