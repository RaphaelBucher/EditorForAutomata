/* 
 * Editor for Automata
 * Bachelor Thesis
 * Raphael Bucher 2016 / 2017
 * */
package editor;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

/** The main-panel where the Editor is drawn into. */
public class DrawablePanel extends JPanel implements MouseMotionListener {
  private static final long serialVersionUID = 1L;
  private Automat automat;
  private KeyboardAdapter keyboardAdapter;

  public DrawablePanel(ToolBar toolBarCopy) {
    // Initialized with minimal dimensions. This Panel is extended in both
    // directions to the full available space via the Editors GridBagLayout.
    this.setPreferredSize(new Dimension(1, 1));
    this.setBackground(Config.BACKGROUND_COLOR);
    this.setDoubleBuffered(true);
    this.setFocusable(true); // needed for the added KeyListener

    addMouseListener();
    addMouseMotionListener(this);
    
    automat = new Automat();
    
    /* Listens for keyboard events. An instance of this class is added to the DrawablePanel instance. 
     * It can only be added to a component that has called setFocusable(true). If two components called
     * setFocusable(true), e.g. ToolBar instance and DrawablePanel instance, it doesn't work. */
    keyboardAdapter = new KeyboardAdapter();
    this.addKeyListener(keyboardAdapter);
  }

  private void addMouseListener() {
    this.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent evt) {
        handleMousePressed(evt);
      }
      
      public void mouseReleased(MouseEvent evt) {
        if (Editor.getToolBar().getMoveCursorButton().isSelected()) {
          automat.handleMoveToolMouseReleased(evt);
        }
      }
    });
  }
  
  @Override
  public void mouseDragged(MouseEvent evt) {
    if (Editor.getToolBar().getMoveCursorButton().isSelected()) {
      automat.handleMoveToolMouseDragged(evt);
    }
    
  }

  @Override
  public void mouseMoved(MouseEvent evt) {
  }

  private void handleMousePressed(MouseEvent evt) {
    automat.handleMousePressed(evt);
  }
  
  public void paint(Graphics graphics) {
    super.paint(graphics);
    Graphics2D graphics2D = (Graphics2D) graphics;
    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
    automat.paint(graphics2D);

    ErrorMessage.paint(graphics2D);
    Tooltip.paint(graphics2D);
    
    // In case it's running on windows, draw a black line below the menu-bar. Else the Menu and the drawablePanel
    // would be both white.
    if (Platform.isWindows()) {
      graphics2D.drawLine(0, 0, this.getWidth(), 0);
    }
    
    graphics2D.dispose();
  }

  // called once per frame
  public void update(ToolBar toolBar) {
    ErrorMessage.update();
    Tooltip.update();
  }

  // Changes the cursor according to what the user has selected in the toolBar
  public void setCustomCursor(Cursor cursor) {
    this.setCursor(cursor);
  }
  
  /** Perform logic when a button is clicked, e.g. deselect a selected state when the user switches
   * away from the selection tool, abort transition building if the user switches away from the transition
   * button and the transition is not done building etc. */
  public void toolBarButtonClicked(ToggleButton clickedButton) {
    // Set the custom cursor of the selected ToggleButton
    this.setCustomCursor(clickedButton.getCustomCursor());
    
    // Was something else than the selection tool (first button, the arrow) being clicked?
    if (!clickedButton.equals(Editor.getToolBar().getArrowButton())) {
      // Deselect a currently selected state / transition
      this.automat.deselectSelectedShape();
    }
    
    // Was something else than the transition Button being clicked?
    if (!clickedButton.equals(Editor.getToolBar().getTransitionButton())) {
      this.automat.resetConstructingTransition();
    } else {
      this.automat.resetConstructingTransition();
    }
  }
  
  /** Changes the automat. */
  public void changeAutomat(Automat automat) {
    this.automat = automat;
  }
  
  // Getters
  public Automat getAutomat() {
    return this.automat;
  }
}