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

import javax.swing.JFrame;

public class Editor extends JFrame {
  private static final long serialVersionUID = 1L;
  private static Container container;
  private static ToolBar toolBar;
  private static DrawablePanel drawablePanel;

  public Editor() {
    this.setTitle("Editor for Automata");
    // close Window => System.exit
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setMinimumSize(new Dimension(Config.FRAME_PANEL_MIN_WIDTH, Config.FRAME_PANEL_MIN_HEIGHT));

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

    // Toolbar
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
    Editor editor = new Editor();
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

      // Updating
      drawablePanel.update(toolBar);

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
  
  // Getter
  public static DrawablePanel getDrawablePanel() {
    return drawablePanel;
  }
  
  public static ToolBar getToolBar() {
    return toolBar;
  }
}
