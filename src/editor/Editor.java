/* 
 * Editor for Automata
 * Bachelor Thesis
 * Raphael Bucher 2016 / 2017
 * */
package editor;

import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;

import javax.swing.JFrame;

import controlFlow.ChangedAutomat;
import controlFlow.UserAction;
import transformation.ReadSymbol;

public class Editor extends JFrame {
  private static final long serialVersionUID = 1L;
  private static Editor editor;
  private static Container container;
  private static ToolBar toolBar;
  private static DrawablePanel drawablePanel;
  private static MenuBar menuBar;
  
  /** The new Automat if the Editor needs to change its Automat, e.g. when loading one. */
  private static Automat newAutomat;
  
  private static WordAnimation wordAnimation;

  public Editor() {
    this.setTitle("Editor for Automata");
    // close Window => System.exit
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setMinimumSize(new Dimension(Config.FRAME_PANEL_MIN_WIDTH, Config.FRAME_PANEL_MIN_HEIGHT));

    // MenuBar
    menuBar = new MenuBar();
    this.setJMenuBar(menuBar);
    
    this.pack(); // Let the window expand to the minimal size specified above.
    container = this.getContentPane();
    container.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
    container.setLayout(new GridBagLayout());
    GridBagConstraints gridBagConstraints = new GridBagConstraints();

    // Toolbar
    toolBar = new ToolBar(container.getHeight()); // initialize with full
                                                  // available height
    // Element is placed in the first grid on the first row
    gridBagConstraints.gridx = 0; // Element is placed in the first grid
    gridBagConstraints.gridy = 0; // Element is placed in the first grid
    // When the size of the container is bigger than the preferred size,
    // it doesn't expand horizontally but fully vertically
    gridBagConstraints.weightx = 0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.fill = GridBagConstraints.VERTICAL;
    container.add(toolBar, gridBagConstraints);

    // Drawable Panel
    drawablePanel = new DrawablePanel(toolBar);
    // Element is placed in the second grid on the first row
    gridBagConstraints.gridx = 1; // Element is placed in the first grid
    gridBagConstraints.gridy = 0; // Element is placed in the first grid
    // When the size of the container is bigger than the preferred size,
    // it doesn't expands fully in both directions
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    container.add(drawablePanel, gridBagConstraints);

    // The order of these calls is important for the positioning.
    this.pack();
    this.setLocationRelativeTo(null);
    this.setVisible(true);
    this.setResizable(true);
  }

  public static void main(String[] args) {
    editor = new Editor();
    editor.run();
  }

  // The main loop of the program
  private void run() {
    long startTime;
    long millisPerFrame = 1000 / Config.FPS;
    long timeout;
    while (true) {
      // Frame-rate like in games to not permanently repaint() everything when
      // not needed.
      startTime = System.currentTimeMillis();

      /** Was setting a new Automat to the Editor requested? e.g. when loading one. */
      if (newAutomat != null) {
        drawablePanel.changeAutomat(newAutomat);
        newAutomat = null;
      }
      
      // Updating
      drawablePanel.update(toolBar);
      
      if (wordAnimation != null) {
        if (!wordAnimation.update(Editor.getDrawablePanel().getAutomat()))
          stopWordAcceptedAnimation();
      }

      // Painting
      toolBar.repaint();
      drawablePanel.repaint();

      timeout = millisPerFrame - (System.currentTimeMillis() - startTime);
      sleep(timeout);
    }
  }

  /**
   * @param millis
   *          negative values allowed, method will deal with it (set to 0)
   */
  private void sleep(long millis) {
    if (millis < 0)
      millis = 0;

    try {
      Thread.sleep(millis);
    } catch (InterruptedException interruptedException) {
      interruptedException.printStackTrace();
      System.err.println("Thread.sleep() failed.");
    }
  }
  
  /** Changes the Editors Automat at a controlled stage of the main-loop. 
   * @param automat the new automat to be set to the DrawablePanel.
   * @param undoRedoText in case addActionToControlFlow was passed true, pass a String that
   * will be used for the Menu-Entry in undo-redo. In case the flag was false, pass anything
   * (it's unused) */
  public static void changeAutonat(Automat automat, boolean addActionToControlFlow, String undoRedoText) {
    newAutomat = automat;
    
    if (addActionToControlFlow) {
      UserAction.addAction(new ChangedAutomat(Editor.getDrawablePanel().getAutomat(), automat, undoRedoText));
    }
  }
  
  /** Starts the animation after the word has been tested for acceptance. */
  public static void startWordAcceptedAnimation(boolean wordAccepted,
      ArrayList<ReadSymbol> readTransitionsSymbols) {
    // Deselect selected shapes and reset transition construction process if some is active
    Automat automat = Editor.getDrawablePanel().getAutomat();
    automat.deselectSelectedShape();
    automat.resetConstructingTransition();
    
    if (automat.getStateByStateIndex(0) != null)
      wordAnimation = new WordAnimation(wordAccepted, readTransitionsSymbols);
  }
  
  /** Stops the animation of the computed word. */
  public static void stopWordAcceptedAnimation() {
    if (wordAnimation != null)
      wordAnimation.stopAnimation();
    
    wordAnimation = null;
  }
  
  // Getter
  public static Editor getEditor() {
    return editor;
  }
  
  public static DrawablePanel getDrawablePanel() {
    return drawablePanel;
  }
  
  public static ToolBar getToolBar() {
    return toolBar;
  }
  
  /** getMenuBar() is taken by the framework. */
  public static MenuBar getCustomMenuBar() {
    return menuBar;
  }
  
  public static WordAnimation getWordAnimation() {
    return wordAnimation;
  }
}
