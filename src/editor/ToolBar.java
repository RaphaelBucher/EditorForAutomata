/* 
 * Editor for Automata
 * Bachelor Thesis
 * Raphael Bucher 2016 / 2017
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
  private ToggleButton arrowButton;
  private ToggleButton moveCursorButton;
  private ToggleButton stateButton;
  private ToggleButton startStateButton;
  private ToggleButton endStateButton;
  private ToggleButton startEndStateButton;
  private ToggleButton transitionButton;

  public ToolBar(int minimal_height) {
    this.setPreferredSize(new Dimension(Config.TOOLBAR_ICON_WIDTH, minimal_height));
    this.setDoubleBuffered(true);
    //this.setFocusable(true);
    this.setFloatable(false);
    
    this.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));

    // Removes the weird standard-borders with 2 vertical lines on the left
    this.setBorder(null);

    String absolutePath = new File("").getAbsolutePath();

    // Selection Button (Default cursors normal arrow). Uses the default cursor.
    arrowButton = new ToggleButton(absolutePath + Config.IconPaths.ARROW, new Cursor(Cursor.DEFAULT_CURSOR),
        true, this, Config.ToolbarTooltips.selectTool);
    this.add(arrowButton);
    
    // Tool to move States
    moveCursorButton = new ToggleButton(absolutePath + Config.IconPaths.MOVE_CURSOR,
        new Cursor(Cursor.CROSSHAIR_CURSOR), false, this, Config.ToolbarTooltips.moveTool);
    this.add(moveCursorButton);
    
    // Normal state
    stateButton = new ToggleButton(absolutePath + Config.IconPaths.STATE,
        absolutePath + Config.WindowsCursorPaths.STATE, Config.CursorNames.STATE_CURSOR, false, this, "");
    this.add(stateButton);

    // Start state.
    startStateButton = new ToggleButton(absolutePath + Config.IconPaths.START_STATE,
        absolutePath + Config.WindowsCursorPaths.START_STATE, Config.CursorNames.START_STATE_CURSOR,
        false, this, "");
    this.add(startStateButton);

    // End state
    endStateButton = new ToggleButton(absolutePath + Config.IconPaths.END_STATE,
        absolutePath + Config.WindowsCursorPaths.END_STATE, Config.CursorNames.END_STATE_CURSOR,
        false, this, "");
    this.add(endStateButton);
    
    // Start- and end-state
    startEndStateButton = new ToggleButton(absolutePath + Config.IconPaths.START_END_STATE,
        absolutePath + Config.WindowsCursorPaths.START_END_STATE, Config.CursorNames.START_END_STATE_CURSOR,
        false, this, "");
    this.add(startEndStateButton);

    // Transition
    transitionButton = new ToggleButton(absolutePath + Config.IconPaths.TRANSITION,
        new Cursor(Cursor.DEFAULT_CURSOR), false, this, Config.ToolbarTooltips.transition);
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
  
  /** Called by the ToggleButtons itself. Deselects all buttons and reselects the passed
   * (= clicked Button). There's always exactly one button of the ToolBar selected. */
  protected void toggleButtonEventHandler(ToggleButton clickedButton) {
    // Switch the cursor and let the DrawablePanel perform the nescessary logic
    // when the user selects a new ToolBar-Button
    Editor.getDrawablePanel().toolBarButtonClicked(clickedButton);
    
    // Deselect all buttons
    this.deselectAllButtons();
    
    // When a click on a Button occur, this method is called first by the event-handler
    // and secondly the button performs it's doClick()-method itself which switches the
    // selected-flag. Thats why we don't have to manually reselect the clickedButton here.
  }
  
  /** Deselects all Button the ToolBar has. Sets only the selected boolean flag and doesn't
   * perform the action event via doClick(). */
  private void deselectAllButtons() {
    arrowButton.setSelected(false);
    moveCursorButton.setSelected(false);
    stateButton.setSelected(false);
    startStateButton.setSelected(false);
    endStateButton.setSelected(false);
    startEndStateButton.setSelected(false);
    transitionButton.setSelected(false);
  }
  
  /** Returns the currently selected ToggleButton. Null if none is selected. This only happens
   * if the user presses a ToggleButton and keeps the mouseButton pressed. Once the mouseButton
   * is released again, this method returns the selected state again. */
  public ToggleButton getSelectedButton() {
    if (arrowButton.isSelected())
      return arrowButton;
    else if (moveCursorButton.isSelected())
      return moveCursorButton;
    else if (stateButton.isSelected())
      return stateButton;
    else if (startStateButton.isSelected())
      return startStateButton;
    else if (endStateButton.isSelected())
      return endStateButton;
    else if (startEndStateButton.isSelected())
      return startEndStateButton;
    else if (transitionButton.isSelected())
      return transitionButton;
    
    return null;
  }

  // Getters
  public ToggleButton getArrowButton() {
    return this.arrowButton;
  }
  
  public ToggleButton getMoveCursorButton() {
    return this.moveCursorButton;
  }
  
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