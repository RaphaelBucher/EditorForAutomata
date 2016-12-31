package transformation;

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
    
    for (int i = 0; i < layoutPositions.length; i++) {
      layoutPositions[i] = new LayoutPosition((i + offset) * Math.PI * 2 / layoutPositions.length,
          radius * circleNumber);
    }
  }
  
  public void takePositions(LayoutCircle[] layoutCircles) {
    for (int i = 0; i < states.length; i++) {
      states[i].setX(layoutPositions[i].position.x);
      states[i].setY(layoutPositions[i].position.y);
    }
  }
}
