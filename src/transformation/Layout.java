package transformation;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import editor.Automat;
import editor.Config;
import editor.Editor;
import editor.State;

/** A helper class to layout automata. */
public class Layout {
  /** The first circle starts with 5 elements, the next ones with 10, 15, 20 etc. */
  protected static final int circleElements = 5;
  
  protected static LayoutCircle[] layoutCircles;
  
  /** Performas a layout-algorithm on the passed automat. It doesn't matter whether the
   * states coordinates are uninitialized (0, 0) or not. Will call automats updatePainting-method
   * which updates all the painting informations of all the automats transitions. 
   * The Editors automat should not be passed directly, but a copy of it since the method 
   * performs sorting on the automats states. */
  public static void layoutAutomat(Automat automat) {
    if (automat.getStates().size() <= 0)
      return;
    
    // Sort the automats states by neighbor-amounts
    sortStatesByNeighborAmount(automat);

    initLayoutCircles(automat);
    
    // Let the states take a position in the according LayoutCircle
    takePositions();
    
    translateAndScale();
    
    // Update the automats painting info in the end
    automat.updatePainting();
  }
  
  private static void takePositions() {
    // TODO to be changed
    for (int i = 0; i < layoutCircles.length; i++) {
      layoutCircles[i].takePositions(layoutCircles);
    }
  }
  
  private static void translateAndScale() {
    int translateX, translateY;
    float scaleX = 1.0f, scaleY = 1.0f;
    Dimension drawableArea = Editor.getDrawablePanel().getSize();
    Point min = new Point(0, 0);
    Point max = new Point(0, 0);
    
    // middle is (0, 0) still, states are not translated yet
    statesMinMaxXY(min, max);
    
    // Scaling
    int availableSpaceX = drawableArea.width - Config.STATE_DIAMETER - 100; // for Arc-Transitions
    int availableSpaceY = drawableArea.height - Config.STATE_DIAMETER - 100; // for Arc-Transitions
    if (max.x - min.x > availableSpaceX) {
      scaleX = (float)availableSpaceX / (max.x - min.x);
    }
    if (max.y - min.y > availableSpaceY) {
      scaleY = (float)availableSpaceY / (max.y - min.y);
    }
    
    for (int i = 0; i < layoutCircles.length; i++) {
      layoutCircles[i].translateAndScale(0, 0, scaleX, scaleY);
    }
    
    // compute new min-max for translating
    statesMinMaxXY(min, max);
    
    // Translating (scale down first, this has an effect on the translating!)
    translateX = drawableArea.width / 2 - (min.x + (max.x - min.x) / 2);
    translateY = drawableArea.height / 2 - (min.y + (max.y - min.y) / 2);
    
    for (int i = 0; i < layoutCircles.length; i++) {
      layoutCircles[i].translateAndScale(translateX, translateY, 1.0f, 1.0f);
    }
  }
  
  /** Iterates over all states and gets the min & max of the state-positions. Saves the result
   * in the passed references. */
  private static void statesMinMaxXY(Point min, Point max) {
    min.x = 0;
    min.y = 0;
    max.x = 0;
    max.y = 0;
    
    for (int i = 0; i < layoutCircles.length; i++) {
      for (int j = 0; j < layoutCircles[i].states.length; j++) {
        // min
        if (layoutCircles[i].states[j].getX() < min.x)
          min.x = layoutCircles[i].states[j].getX();
        if (layoutCircles[i].states[j].getY() < min.y)
          min.y = layoutCircles[i].states[j].getY();
        
        // max
        if (layoutCircles[i].states[j].getX() > max.x)
          max.x = layoutCircles[i].states[j].getX();
        if (layoutCircles[i].states[j].getY() > max.y)
          max.y = layoutCircles[i].states[j].getY();
      }
    }
  }
  
  private static void initLayoutCircles(Automat automat) {
    // compute the number of layoutCircles needed
    int circleCount = 1;
    int currentIndex = 1;
    
    while (currentIndex < automat.getStates().size()) {
      currentIndex += circleCount * circleElements;
      circleCount++;
    }
    
    layoutCircles = new LayoutCircle[circleCount];
    
    // Init the circles
    State[] states = new State[automat.getStates().size()];
    states = automat.getStates().toArray(states);
    layoutCircles[0] = new LayoutCircle(Arrays.copyOfRange(states, 0, 1), 0);
    
    int startIndex = 1;
    for (int i = 1; i < layoutCircles.length; i++) {
      int range = i * circleElements;
      if (startIndex + range > automat.getStates().size())
        range = automat.getStates().size() - startIndex;
      
      layoutCircles[i] = new LayoutCircle(Arrays.copyOfRange(states, startIndex, startIndex + range), i);
      startIndex += range;
    }
  }
  
  /** Sorts the states of the passed automat according to their neighborAmounts (The amount of 
   * neighbor-states the state has lineTransitions to). In ascending order (biggest first). */
  private static void sortStatesByNeighborAmount(Automat automat) {
    State.updateNeighborAmounts(automat);
    
    // Sort the list
    Collections.sort(automat.getStates(), new Comparator<State>() {
      @Override
      public int compare(State state1, State state2)
      {
        // state1 and state2 exchanged (ascending order)
        return new Integer(state2.getNeighborAmount()).compareTo(state1.getNeighborAmount());
      }
    });
  }
}
