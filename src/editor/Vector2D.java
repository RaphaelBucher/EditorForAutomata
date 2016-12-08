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
  
  public static double crossProduct(Vector2D v1, Vector2D v2) {
    return v1.x * v2.y - v1.y * v2.x;
  }
  
  /** Performs v1 - v2 */
  public static Vector2D subtract(Vector2D v1, Vector2D v2) {
    return new Vector2D(v1.x - v2.x, v1.y - v2.y);
  }
  
  public static double distance(Vector2D v1, Vector2D v2) {
    return Vector2D.subtract(v1, v2).absolute();
  }
}
