/* 
 * Bachelor Thesis
 * Raphael Bucher
 * November 2016
 * */
package editor;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;

import javax.swing.JToolBar;

public class ToolBar extends JToolBar {
  private static final long serialVersionUID = 1L;
  private ToggleButton stateButton;
  private ToggleButton startStateButton;
  private ToggleButton endStateButton;
  private ToggleButton startEndStateButton;
  private ToggleButton transitionButton;

  public ToolBar(int minimal_height) {
    this.setPreferredSize(new Dimension(Config.TOOLBAR_ICON_WIDTH, minimal_height));
    this.setDoubleBuffered(true);
    this.setFocusable(true);
    this.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));

    // Removes the weird standard-borders with 2 vertical lines on the left
    this.setBorder(null);

    String absolutePath = new File("").getAbsolutePath();

    // Normal state
    stateButton = new ToggleButton(absolutePath + Config.Icon_paths.STATE,
        absolutePath + Config.Windows_cursor_paths.STATE, Config.Cursor_names.STATE_CURSOR, this);
    this.add(stateButton);

    // Start state
    startStateButton = new ToggleButton(absolutePath + Config.Icon_paths.START_STATE,
        absolutePath + Config.Windows_cursor_paths.START_STATE, Config.Cursor_names.START_STATE_CURSOR, this);
    this.add(startStateButton);

    // End state
    endStateButton = new ToggleButton(absolutePath + Config.Icon_paths.END_STATE,
        absolutePath + Config.Windows_cursor_paths.END_STATE, Config.Cursor_names.END_STATE_CURSOR, this);
    this.add(endStateButton);
    
    // Start- and end-state
    startEndStateButton = new ToggleButton(absolutePath + Config.Icon_paths.START_END_STATE,
        absolutePath + Config.Windows_cursor_paths.START_END_STATE, Config.Cursor_names.START_END_STATE_CURSOR, this);
    this.add(startEndStateButton);

    // Transition
    transitionButton = new ToggleButton(absolutePath + Config.Icon_paths.TRANSITION,
        absolutePath + Config.Windows_cursor_paths.TRANSITION, Config.Cursor_names.TRANSITION_CURSOR, this);
    this.add(transitionButton);
  }

  public void paint(Graphics graphics) {
    super.paint(graphics);
    Graphics2D graphics2D = (Graphics2D) graphics;

    // In case it's running on windows, draw a black line below the menu-bar. Else the Menu and the drawablePanel
    // would be both white.
    if (Platform.isWindows()) {
      graphics2D.drawLine(0, 0, this.getWidth(), 0);
    }
    
    graphics2D.dispose();
  }

  // Called by the ToggleButtons itself. Deselects all buttons except the one being clicked. 
  // Leaves this one untouched.
  protected void toggleButtonEventHandler(ToggleButton clickedButton) {
    // Update the cursor of the drawable panel. The API didn't update
    // the selection state yet, so clickedButton.isSelected() getting false
    // means the button has been selected...
    if (!clickedButton.isSelected()) {
      // button was selected - set custum Cursor of the Button
      Editor.getDrawablePanel().setCursor(clickedButton.getCustomCursor());
    } else {
      // button was deselected - set default Cursor
      Editor.getDrawablePanel().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    // Deselect all buttons except the one that was clicked.
    if (!clickedButton.equals(stateButton) && stateButton.isSelected())
      stateButton.doClick();
    if (!clickedButton.equals(startStateButton) && startStateButton.isSelected())
      startStateButton.doClick();
    if (!clickedButton.equals(endStateButton) && endStateButton.isSelected())
      endStateButton.doClick();
    if (!clickedButton.equals(startEndStateButton) && startEndStateButton.isSelected())
      startEndStateButton.doClick();
    if (!clickedButton.equals(transitionButton) && transitionButton.isSelected())
      transitionButton.doClick();
  }
  
  /** Returns the selected ToggleButton. Returns null if none is selected. */
  public ToggleButton getSelectedButton() {
    if (stateButton.isSelected())
      return stateButton;
    if (startStateButton.isSelected())
      return startStateButton;
    if (endStateButton.isSelected())
      return endStateButton;
    if (startEndStateButton.isSelected())
      return startEndStateButton;
    if (transitionButton.isSelected())
      return transitionButton;
    
    return null; // No Button was selected
  }

  // Getters
  public ToggleButton getStateButton() {
    return this.stateButton;
  }

  public ToggleButton getStartStateButton() {
    return this.startStateButton;
  }

  public ToggleButton getEndStateButton() {
    return this.endStateButton;
  }

  public ToggleButton getStartEndStateButton() {
    return this.startEndStateButton;
  }
  
  public ToggleButton getTransitionButton() {
    return this.transitionButton;
  }
}