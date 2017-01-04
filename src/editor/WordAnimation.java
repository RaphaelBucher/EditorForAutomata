package editor;

import java.util.ArrayList;

import transformation.ReadSymbol;

public class WordAnimation {
  private ArrayList<ReadSymbol> readTransitionsSymbols;
  
  public WordAnimation(ArrayList<ReadSymbol> readTransitionsSymbols) {
    this.readTransitionsSymbols = readTransitionsSymbols;
    
    startAnimation();
  }

  /** Starts the animation of the computed word. */
  private void startAnimation() {
    Automat automat = Editor.getDrawablePanel().getAutomat();
    // Set the flags for the wordAcceptedPath-flag of the Shapes to false
    setWordAcceptedPath(automat, true);
  }
  
  /** Stops the animation of the computed word. */
  public void stopAnimation() {
    Automat automat = Editor.getDrawablePanel().getAutomat();
    // Set the flags for the wordAcceptedPath-flag of the Shapes to false
    setWordAcceptedPath(automat, false);
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
}
