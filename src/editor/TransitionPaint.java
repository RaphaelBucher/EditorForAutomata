/* 
 * Editor for Automata
 * Bachelor Thesis
 * Raphael Bucher 2016 / 2017
 * */
package editor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

public abstract class TransitionPaint {
  public abstract void paint(Graphics2D graphics2D);
  public abstract void computePaintingCoordinates(ArrayList<Transition> transitions);
  public abstract void computeSymbolDockingPoint();
  public abstract Point getSymbolDockingPoint();
  public abstract int getSymbolDirection();
  public abstract Point wordAnimationBall(long passedMillis, long totalMillis);
  protected abstract boolean mouseClickHit(Point mousePosition);
  
  // A reference on the Transition which instantiated this TransitionPaintLine-Object
  protected Transition aggregateTransition;
  
  // Docking point for the Transitions symbols
  protected Point symbolDockingPoint;
  /** 1 if the symbols go from docking point to right, -1 if the symbols go from docking pint to left. */
  protected int symbolDirection;
  /** The offsetVectors length from the LineTransitions middle to the symbolDockingPoint, or
   * from the middle of the ArcCircle to the dockingPoint in cases its an ArcTransition. */
  protected final double offsetVectorLength = 11.0d;
  
  /** Computes the transitions angle of the given Triangles Cathetes. Pass the deltaY-value
   * in the Cartesian coordinate system not in Swings. Returns a value between 0 and 2 * Math.PI */
  public static double computeAngle(int deltaX, int deltaY) {
    // The Hypotenuse
    double c = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

    // Sufficient if the vector is in quadrants 1 or 2
    double angle = Math.acos(deltaX / c);
    
    // Make result valid for 360° and not only for 180° (quadrants 1 and 2)
    if (deltaY < 0)
      angle = Math.PI * 2 - angle;
    
    return angle;
  }
  
  /** Returns the painting color for the transition. */
  protected Color getPaintingColor() {
    Color color = new Color(0, 0, 0);
    
    if (this.aggregateTransition.isSelected || this.aggregateTransition.wordAcceptedPath)
      color = Config.SELECTED_STATE_COLOR;
    
    // Transition highlighted during the word-animation?
    WordAnimation wordAnimation = Editor.getWordAnimation();
    if (wordAnimation != null) {
      // Is this Epsilon-Transition highlighted
      if (this.aggregateTransition.equals(wordAnimation.getHighlightedShape()))
        color = wordAnimation.getHighlightedColor();
      
      // Is thie Non-Epsilon-Transition highlighted?
      if (wordAnimation.getHighlightedShape() instanceof Symbol) {
        if (aggregateTransition.equals( ((Symbol) wordAnimation.getHighlightedShape()).getHostTransition()) )
          color = wordAnimation.getHighlightedColor();
      }
    }
    
    return color;
  }
}
