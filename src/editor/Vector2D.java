/* 
 * Editor for Automata
 * Bachelor Thesis
 * Raphael Bucher 2016 / 2017
 * */
package editor;

/** Computes in Cartesian coordinate system. */
public class Vector2D {
  protected double x;
  protected double y;
  
  public Vector2D(double x, double y) {
    this.x = x;
    this.y = y;
  }
  
  public double absolute() {
    return Math.sqrt(x * x + y * y);
  }
  
  /** Returns the determinant from the Matrix (v1, v2)*/
  public static double det(Vector2D v1, Vector2D v2) {
    return v1.x * v2.y - v1.y * v2.x;
  }
  
  /** Performs v1 - v2 */
  public static Vector2D subtract(Vector2D v1, Vector2D v2) {
    return new Vector2D(v1.x - v2.x, v1.y - v2.y);
  }
  
  public static double distance(Vector2D v1, Vector2D v2) {
    return Vector2D.subtract(v1, v2).absolute();
  }
  
  /** Computes the transitions angle of the given Triangles Cathetes. Pass the deltaY-value
   * in the Cartesian coordinate system not in Swings. Returns a value between 0 and 2 * Math.PI */
  public static double computeAngle(double deltaX, double deltaY) {
    // The Hypotenuse
    double c = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

    // Sufficient if the vector is in quadrants 1 or 2
    double angle = Math.acos(deltaX / c);
    
    // Make result valid for 360° and not only for 180° (quadrants 1 and 2)
    if (deltaY < 0)
      angle = Math.PI * 2 - angle;
    
    return angle;
  }
  
  /** Scales the vector by the passed factor. 1.0d keeps the vector unchanged. */
  public void scale(double factor) {
    x *= factor;
    y *= factor;
  }
}
