package editor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

import transformation.ReadSymbol;

public class WordAnimation {
  private ArrayList<ReadSymbol> readTransitionsSymbols;
  private long animationStarted;
  
  /** The currently highlighted shape. A State if a state is currently highlighted,
   * a Symbol if a Symbol and its Non-Epsilon-Transition is highlighted,
   * or a Transition if an Epsilon-Transition is highlighted. */
  private Shape highlightedShape;
  private Color highlightedColor;
  private Color highlightedSymbolBackground;
  private Point animatedBall;
  
  private static final long STATE_HIGHLIGHT_DURATION = 1300;
  private static final long TRANSITION_HIGHLIGHT_DURATION = 2100;
  
  public WordAnimation(boolean wordAccepted, ArrayList<ReadSymbol> readTransitionsSymbols) {
    this.readTransitionsSymbols = readTransitionsSymbols;
    if (wordAccepted) {
      this.highlightedColor = Config.HIGHLIGHTED_COLOR_WORD_ACCEPTED;
      highlightedSymbolBackground = Config.HIGHLIGHTED_COLOR_SYMBOL_BACKGROUND_ACCEPTED;
    } else {
      this.highlightedColor = Config.HIGHLIGHTED_COLOR_WORD_DENIED;
      highlightedSymbolBackground = Config.HIGHLIGHTED_COLOR_SYMBOL_BACKGROUND_DENIED;
    }
    
    startAnimation();
  }

  /** Starts the animation of the computed word. */
  private void startAnimation() {
    Automat automat = Editor.getDrawablePanel().getAutomat();
    // Set the flags for the wordAcceptedPath-flag of the Shapes to false
    setWordAcceptedPath(automat, true);
    
    animationStarted = System.currentTimeMillis();
  }
  
  /** Stops the animation of the computed word. */
  public void stopAnimation() {
    Automat automat = Editor.getDrawablePanel().getAutomat();
    // Set the flags for the wordAcceptedPath-flag of the Shapes to false
    setWordAcceptedPath(automat, false);
  }
  
  /** @return true if the animation is still active and performed an update, false if the
   * animation is over. */
  public boolean update(Automat automat) {
    long passedMillis = getPassedMillis();
    long transitionPassedMillis = 0; // For the ball-animation
    
    // the animation-duration previously taken by Shapes in the readTransitionsSymbols
    long durationTaken = STATE_HIGHLIGHT_DURATION;
    
    for (int i = 0; i < readTransitionsSymbols.size(); i++) {
      durationTaken += TRANSITION_HIGHLIGHT_DURATION;
      ReadSymbol readSymbol = readTransitionsSymbols.get(i);
      
      if (passedMillis < durationTaken) {
        transitionPassedMillis = passedMillis - (durationTaken - TRANSITION_HIGHLIGHT_DURATION);
        animateBall(readTransitionsSymbols.get(i).getTraveledTransition(), transitionPassedMillis);
        
        // The transition is highlighted. Differ whether its an Epsilon-Transition or not
        if (readSymbol.getReadSymbol() == null)
          // Epsilon-Transition
          highlightedShape = readSymbol.getTraveledTransition();
        else
          highlightedShape = Symbol.getSymbol(readSymbol.getTraveledTransition().getSymbols(),
              readSymbol.getReadSymbol().charValue());
        
        break;
      }
      
      durationTaken += STATE_HIGHLIGHT_DURATION;
      if (passedMillis < durationTaken) {
        // The ending-state of the transition is highlighted
        highlightedShape = readSymbol.getTraveledTransition().getTransitionEnd();
        break;
      }
    }
    
    // Is the start-state the highlighted shape?
    if (passedMillis < STATE_HIGHLIGHT_DURATION) {
      highlightedShape = automat.getStateByStateIndex(0);
    }
    
    // Is the animation over? If so, it will be stopped
    return !(passedMillis >= durationTaken);
  }
  
  private void animateBall(Transition hostTransition, long passedMillis) {
    animatedBall = hostTransition.getTransitionPaint().wordAnimationBall(
        passedMillis, TRANSITION_HIGHLIGHT_DURATION);
  }
  
  private long getPassedMillis() {
    return System.currentTimeMillis() - animationStarted;
  }
  
  /** Sets the wordAcceptedPath-flag to the whole readTransitionsSymbols-list. */
  private void setWordAcceptedPath(Automat automat, boolean wordAcceptedPath) {
    // Set the flags for the wordAcceptedPath-flag of the Shapes
    if (automat.getStateByStateIndex(0) != null)
      automat.getStateByStateIndex(0).setWordAcceptedPath(wordAcceptedPath);
    
    for (int i = 0; i < readTransitionsSymbols.size(); i++) {
      Transition transition = readTransitionsSymbols.get(i).getTraveledTransition();
      transition.getTransitionEnd().setWordAcceptedPath(wordAcceptedPath);
      transition.setWordAcceptedPath(wordAcceptedPath);
      
      Character readSymbol = readTransitionsSymbols.get(i).getReadSymbol();
      if (readSymbol != null) {
        // No Epsilon-Transition
        Symbol symbol = Symbol.getSymbol(transition.getSymbols(), readSymbol.charValue());
        symbol.setWordAcceptedPath(wordAcceptedPath);
      }
    }
  }
  
  public void paintAnimatedBall(Graphics2D graphics2D) {
    graphics2D.setColor(highlightedColor);
    if (highlightedShape instanceof Transition || highlightedShape instanceof Symbol)
      graphics2D.fillOval(animatedBall.x - 5, animatedBall.y - 5, 11, 11);
    
    graphics2D.setColor(Color.BLACK);
  }
  
  // Setters and Getters
  public Shape getHighlightedShape() {
    return this.highlightedShape;
  }
  
  public Color getHighlightedColor() {
    return this.highlightedColor;
  }
  
  public Color getHighlightedSymbolBackground() {
    return this.highlightedSymbolBackground;
  }
}
