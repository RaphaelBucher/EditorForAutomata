package editor;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

import transformation.ReadSymbol;

public class WordAnimation {
  private ArrayList<ReadSymbol> readTransitionsSymbols;
  private long animationStarted;
  private boolean wordAccepted;
  private String wordNotAcceptedSubstring;
  
  /** The currently highlighted shape. A State if a state is currently highlighted,
   * a Symbol if a Symbol and its Non-Epsilon-Transition is highlighted,
   * or a Transition if an Epsilon-Transition is highlighted. */
  private Shape highlightedShape;
  private Color highlightedColor;
  private Color highlightedSymbolBackground;
  private Point animatedBall;
  
  private String readSymbols = "";
  private String readSymbol = "";
  private String toReadSymbols = "";
  
  private static final long STATE_HIGHLIGHT_DURATION = 1300;
  private static final long TRANSITION_HIGHLIGHT_DURATION = 2100;
  
  public WordAnimation(String originalWord, boolean wordAccepted, ArrayList<ReadSymbol> readTransitionsSymbols) {
    this.readTransitionsSymbols = readTransitionsSymbols;
    this.wordAccepted = wordAccepted;
    this.wordNotAcceptedSubstring = originalWord.substring(getWord().length());
    
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
    
    // Is the start-state the highlighted shape?
    if (passedMillis < STATE_HIGHLIGHT_DURATION) {
      highlightedShape = automat.getStateByStateIndex(0);
      wordSubstrings(-1);
      return true;
    }
    
    // the animation-duration previously taken by Shapes in the readTransitionsSymbols
    long durationTaken = STATE_HIGHLIGHT_DURATION;
    
    for (int i = 0; i < readTransitionsSymbols.size(); i++) {
      durationTaken += TRANSITION_HIGHLIGHT_DURATION;
      ReadSymbol readSymbol = readTransitionsSymbols.get(i);
      
      if (passedMillis < durationTaken) {
        transitionPassedMillis = passedMillis - (durationTaken - TRANSITION_HIGHLIGHT_DURATION);
        animateBall(readTransitionsSymbols.get(i).getTraveledTransition(), transitionPassedMillis);
        
        // The transition is highlighted. 
        highlightedShape = Symbol.getSymbol(readSymbol.getTraveledTransition().getSymbols(), 
            readSymbol.getReadSymbol().charValue());
        
        wordSubstrings(i);
        break;
      }
      
      durationTaken += STATE_HIGHLIGHT_DURATION;
      if (passedMillis < durationTaken) {
        // The ending-state of the transition is highlighted
        highlightedShape = readSymbol.getTraveledTransition().getTransitionEnd();
        wordSubstrings(i);
        break;
      }
    }
    
    // Is the animation over? If so, it will be stopped
    return !(passedMillis >= durationTaken);
  }
  
  /** Computes the word substrings, depending on which symbol the animation currently is.
   * @param readTransitionsSymbolsIndex the index on the current element, -1 if the start-state
   * is highlighted. */
  private void wordSubstrings(int readTransitionsSymbolsIndex) {
    String word = getWord();
    
    readSymbols = "";
    readSymbol = "";
    toReadSymbols = word;
    
    int highlightedSymbolIndex = readTransitionsSymbolsIndex;
    int toReadSymbolsIndex = readTransitionsSymbolsIndex + 1;
    
    // Is the Start-State highlighted?
    if (readTransitionsSymbolsIndex == -1)
      return;
    
   if (highlightedShape instanceof State) {
      // An End-State of a traversed Transition is highlighted
      highlightedSymbolIndex++;
    }
    
    readSymbols = getWord(0, highlightedSymbolIndex);
    readSymbol = getWord(highlightedSymbolIndex, toReadSymbolsIndex);
    toReadSymbols = getWord(toReadSymbolsIndex, readTransitionsSymbols.size());
  }
  
  private String getWord(int beginIndex, int endIndex) {
    String word = "";
    
    for (int i = beginIndex; i < endIndex; i++) {
      if (readTransitionsSymbols.get(i).getReadSymbol().charValue() != '\u03B5')
        word += readTransitionsSymbols.get(i).getReadSymbol();
    }
    
    return word;
  }
  
  private String getWord() {
    String word = "";
    
    for (int i = 0; i < readTransitionsSymbols.size(); i++) {
      if (readTransitionsSymbols.get(i).getReadSymbol().charValue() != '\u03B5')
        word += readTransitionsSymbols.get(i).getReadSymbol();
    }
    
    return word;
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
        
      Symbol symbol = Symbol.getSymbol(transition.getSymbols(), readSymbol.charValue());
      symbol.setWordAcceptedPath(wordAcceptedPath);
    }
  }
  
  public void paint(Graphics2D graphics2D) {
    // The animated ball
    graphics2D.setColor(highlightedColor);
    if (highlightedShape instanceof Transition) {
      Transition transition = ((Transition)highlightedShape);
      
      if (transition.isArcTransition())
        graphics2D.fillOval(animatedBall.x - 5, animatedBall.y - 5, 11, 11);
      else {
        if( ((TransitionPaintLine)transition.getTransitionPaint()).isPainted() )
          graphics2D.fillOval(animatedBall.x - 5, animatedBall.y - 5, 11, 11);
      }
    }
    
    if (highlightedShape instanceof Symbol) {
      Transition transition = ((Symbol)highlightedShape).getHostTransition();
      
      if (transition.isArcTransition())
        graphics2D.fillOval(animatedBall.x - 5, animatedBall.y - 5, 11, 11);
      else {
        if( ((TransitionPaintLine)transition.getTransitionPaint()).isPainted() )
          graphics2D.fillOval(animatedBall.x - 5, animatedBall.y - 5, 11, 11);
      }
    }
    
    graphics2D.setColor(Color.BLACK);
    
    // Paint the word in the top-left corner
    paintWord(graphics2D);
  }
  
  private void paintWord(Graphics2D graphics2D) {
    // Font metric calculations
    Font messageFont = new Font("Arial", Font.PLAIN, 20);
    graphics2D.setFont(messageFont);
    FontMetrics fontMetrics = graphics2D.getFontMetrics();
    
    String prefix = "Word " + (wordAccepted ? "accepted: " : "denied: ");
    String wholeText = prefix + readSymbols + readSymbol + toReadSymbols + wordNotAcceptedSubstring;
    Color wordAcceptedColor =
        wordAccepted ? Config.HIGHLIGHTED_COLOR_WORD_ACCEPTED : Config.HIGHLIGHTED_COLOR_WORD_DENIED;
    
    int messageWidth = fontMetrics.stringWidth(wholeText);
    int messageHeight = fontMetrics.getAscent();
    int whiteSpace = 4; // 8 pixels of white space around the message to free eventually occupied space
    Point fontPosition = new Point(20, 33); // the font-startingPosition. y is the baseline, not the top
    
    // The white round filled rectangle to "clear" up the area
    graphics2D.setColor(new Color(255, 255, 255));
    graphics2D.fillRoundRect(fontPosition.x - whiteSpace, fontPosition.y - messageHeight - whiteSpace + 2,
        messageWidth + 2 * whiteSpace, messageHeight + 2 * whiteSpace, 8, 8);

    // Draw the texts
    graphics2D.setColor(wordAcceptedColor);
    int currentFontX = fontPosition.x;
    graphics2D.drawString(prefix, currentFontX, fontPosition.y);
    currentFontX += fontMetrics.stringWidth(prefix);
    
    // readSymbols
    graphics2D.setColor(Config.SELECTED_STATE_COLOR);
    graphics2D.drawString(readSymbols, currentFontX, fontPosition.y);
    currentFontX += fontMetrics.stringWidth(readSymbols);

    // readSymbol
    graphics2D.setColor(wordAcceptedColor);
    graphics2D.drawString(readSymbol, currentFontX, fontPosition.y);
    currentFontX += fontMetrics.stringWidth(readSymbol);
    
    // toReadSymbols and wordNotAcceptedSubstring
    graphics2D.setColor(Config.SELECTED_STATE_COLOR);
    graphics2D.drawString(toReadSymbols + wordNotAcceptedSubstring, currentFontX, fontPosition.y);

    graphics2D.setColor(Color.BLACK); // restore the default color for further rendering
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
