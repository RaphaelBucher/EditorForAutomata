/* 
 * Editor for Automata
 * Bachelor Thesis
 * Raphael Bucher 2016 / 2017
 * */
package transformation;

import java.awt.Dimension;
import java.awt.Point;

import editor.Automat;
import editor.Config;
import editor.Editor;
import editor.Math2D;
import editor.State;

public class LayoutCircle {
  private static final int radius = 160;
  
  protected State[] states;
  private LayoutPosition[] layoutPositions;
  private int circleNumber;
  
  /** @param circleNumber Pass 0 for the middlePoint, 1 for the first circle, etc. */
  public LayoutCircle(State[] states, int circleNumber) {
    this.states = states;
    this.circleNumber = circleNumber;
    
    int layoutPositionCount = 1;
    if (circleNumber >= 1)
      layoutPositionCount = circleNumber * Layout.circleElements;
    
    layoutPositions = new LayoutPosition[layoutPositionCount];
    initPositions();
  }
  
  public void translateAndScale(int translateX, int translateY, float scaleX, float scaleY) {
    for (int i = 0; i < states.length; i++) {
      // Scale
      states[i].moveTo(Math.round(states[i].getX() * scaleX), Math.round(states[i].getY() * scaleY));
      
      // Translate
      states[i].moveDelta(translateX, translateY);
    }
  }
  
  private void initPositions() {
    double offset = (circleNumber % 2) / 4.0d;
    Dimension drawableArea = Editor.getDrawablePanel().getSize();
    double initialScaleX = (double)(drawableArea.width - Config.STATE_DIAMETER - 100) /
        (drawableArea.height - Config.STATE_DIAMETER - 100);
    
    for (int i = 0; i < layoutPositions.length; i++) {
      layoutPositions[i] = new LayoutPosition((i + offset) * Math.PI * 2 / layoutPositions.length,
          radius * circleNumber, initialScaleX);
    }
  }
  
  public void takePositions(LayoutCircle[] layoutCircles, Automat automat) {
    for (int i = 0; i < states.length; i++) {
      int bestPosition = -1; // no position taken yet
      int bestTransitionDistance = -1;
      
      for (int j = 0; j < layoutPositions.length; j++) {
        if (layoutPositions[j].state == null) {
          int currentTransitionDistance = totalTransitionDistance(layoutCircles, j, states[i], automat);
          if (bestPosition == -1 || currentTransitionDistance < bestTransitionDistance) {
            bestPosition = j;
            bestTransitionDistance = currentTransitionDistance;
          }
        }
      }
      
      states[i].setX(layoutPositions[bestPosition].position.x);
      states[i].setY(layoutPositions[bestPosition].position.y);
      layoutPositions[bestPosition].occupy(states[i]);
    }
  }
  
  private int totalTransitionDistance(LayoutCircle[] layoutCircles, int layoutPositionsIndex, State state,
      Automat automat) {
    int distance = 0;
    
    for (int i = 0; i <= circleNumber; i++) {
      for (int j = 0; j < layoutCircles[i].layoutPositions.length; j++) {
        if (layoutCircles[i].layoutPositions[j].state != null) {
          State comparedState = layoutCircles[i].layoutPositions[j].state;
          
          // do the states have a transition between themselves?
          if (state.gotTransitionTo(comparedState, automat.getTransitions()) != null ||
              comparedState.gotTransitionTo(state, automat.getTransitions()) != null) {
            // The states coordinates are not set yet, use the layoutPositions instead
            Point statePosi = layoutCircles[circleNumber].layoutPositions[layoutPositionsIndex].position;
            Point comparedStatePosi = layoutCircles[i].layoutPositions[j].position;
            
            distance += Math2D.distance(statePosi.x - comparedStatePosi.x, statePosi.y - comparedStatePosi.y);
          }
        }
      }
    }
    
    return distance;
  }
}
