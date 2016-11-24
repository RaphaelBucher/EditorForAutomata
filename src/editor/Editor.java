/* 
 * Bachelor Thesis
 * Raphael Bucher
 * November 2016
 * */
package editor;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.MouseInfo;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Editor extends JFrame {
  private static final long serialVersionUID = 1L;
  private static JPanel container;
  private static ToolBar toolBar;
  private static DrawablePanel drawablePanel;
  private Timer resizeDelay;

  public Editor(DrawablePanel editor) {
    this.setTitle("Editor for Automata");
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // close Window => System.exit
    
    this.setLayout(new BorderLayout());

    container = new JPanel();
    toolBar = new ToolBar();
    drawablePanel = new DrawablePanel();

    container.setLayout(new FlowLayout(FlowLayout.LEADING, Config.BORDER_THICKNESS,
        Config.BORDER_THICKNESS));
    container.add(toolBar);
    container.add(drawablePanel);

    this.add(container);

    this.pack();
    
    // The order of these calls is important for the positioning through
    // setLocationRelativeTo()
    this.setLocationRelativeTo(null);
    this.setVisible(true);
    this.setResizable(true);
    
    // Called when the Frame is being resized
    this.addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        resizeDrawableArea();
      }
    });
    
    // Frame cannot be resized below this Dimension
    this.setMinimumSize(new Dimension(toolBar.getWidth() + Config.BORDER_THICKNESS * 3 + 
        Config.DRAWABLE_PANEL_MINIMUM_X, Config.MINIMAL_FRAME_HEIGHT));
  }
  
  // Called when the Frame is being resized
  private void resizeDrawableArea() {
    int takenWidth = toolBar.getWidth() + Config.BORDER_THICKNESS * 3;
    int availableWidth = this.getWidth() - takenWidth;
    int availableHeight = this.getContentPane().getHeight() - Config.BORDER_THICKNESS * 2;
    
    drawablePanel.setPreferredSize(new Dimension(availableWidth, availableHeight));
    toolBar.setPreferredSize(new Dimension(Config.TOOLBAR_X, availableHeight));
    
    container.remove(toolBar);
    container.add(toolBar);
    
    container.remove(drawablePanel);
    container.add(drawablePanel);
    
    this.pack();
  }

  public static void main(String[] args) {
    Editor editor = new Editor(drawablePanel);

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
      // Doesn't eat too much unneeded system resources
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
}
